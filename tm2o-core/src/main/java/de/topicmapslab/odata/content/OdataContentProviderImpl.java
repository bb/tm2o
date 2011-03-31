package de.topicmapslab.odata.content;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.core4j.Enumerable;
import org.joda.time.LocalTime;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmAssociationEnd;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmMultiplicity;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmType;
import org.odata4j.expression.CommonExpression;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.expression.OrderByExpression;
import org.odata4j.producer.QueryInfo;

import de.topicmapslab.majortom.model.namespace.Namespaces;
import de.topicmapslab.odata.TopicMapMetadata;
import de.topicmapslab.odata.config.EContentProviderConfiguration;
import de.topicmapslab.odata.dao.AssociationTypeDAO;
import de.topicmapslab.odata.dao.CharacteristicsTypeDAO;
import de.topicmapslab.odata.dao.CounterPartDAO;
import de.topicmapslab.odata.dao.NameTypeDAO;
import de.topicmapslab.odata.dao.OccurrenceTypeDAO;
import de.topicmapslab.odata.dao.TopicTypeDAO;
import de.topicmapslab.odata.edm.EdmNavigationPropertyIdentifier;
import de.topicmapslab.odata.edm.EdmPropertyIdentifier;
import de.topicmapslab.odata.edm.IEdmNavigationPropertyIdentifier;
import de.topicmapslab.odata.edm.IEdmPropertyIdentifier;
import de.topicmapslab.odata.exception.TopicMapsODataException;
import de.topicmapslab.odata.tmql.TmqlHelper;
import de.topicmapslab.odata.tmql.TmqlQueryBuilder;
import de.topicmapslab.odata.util.ODataUtils;
import de.topicmapslab.tmql4j.hibernate.criterion.ICriterion;
import de.topicmapslab.tmql4j.util.LiteralUtils;

/**
 * Abstract implementation of {@link IOdataContentProvider}
 * 
 * @author Sven Krosse
 */
public abstract class OdataContentProviderImpl implements IOdataContentProvider {

	/**
	 * list of all EDM entity sets
	 */
	private List<EdmEntitySet> edmEntitySets = new ArrayList<EdmEntitySet>();
	/**
	 * list of all EDM entity types
	 */
	private List<EdmEntityType> edmEntityTypes = new ArrayList<EdmEntityType>();
	/**
	 * list of all EDM associations
	 */
	private List<EdmAssociation> edmAssociations = new ArrayList<EdmAssociation>();
	/**
	 * Mapping of EDM entity type id and its definition
	 */
	private Map<String, EdmEntityType> edmEntityTypesById = new HashMap<String, EdmEntityType>();
	/**
	 * Mapping of the entity type name and its id
	 */
	private Map<String, String> edmEntityTypeIds = new HashMap<String, String>();
	/**
	 * Mapping of EDM entity property id and its identifier for the topic map source
	 */
	private Map<String, IEdmPropertyIdentifier> edmPropertyTypeIds = new HashMap<String, IEdmPropertyIdentifier>();
	/**
	 * Map of the TMQL queries builder containing the queries to access entities of a specific EDM entity type
	 */
	private Map<String, TmqlQueryBuilder> queryBuilders;
	/**
	 * the namespace
	 */
	private String namespace;
	/**
	 * the meta data service for the OData service
	 */
	private TopicMapMetadata service;
	/**
	 * the association mode
	 */
	private EContentProviderConfiguration associationMode = EContentProviderConfiguration.STRONG_ASSOCIATION;
	/**
	 * the topic map Id
	 */
	private final String topicMapId;

	/**
	 * constructor
	 * 
	 * @param topicMapId
	 *            the topic map id
	 */
	public OdataContentProviderImpl(final String topicMapId) {
		this.topicMapId = topicMapId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public void setService(TopicMapMetadata service) {
		this.service = service;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(String namespace, Properties properties) throws TopicMapsODataException {
		/*
		 * get association mode property
		 */
		Object oAssociationMode = properties.get("association-mode");
		if (oAssociationMode != null) {
			try {
				associationMode = EContentProviderConfiguration.valueOf(oAssociationMode.toString());
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
		this.namespace = namespace;
		List<TopicTypeDAO> topicTypes = getTopicTypesByTmql();
		/*
		 * convert topic types to entity type
		 */
		for (TopicTypeDAO topicType : topicTypes) {
			final String typeId = topicType.getTypeId();
			final String typeLabel = topicType.getTypeLabel();
			List<EdmProperty> edmProperties = new ArrayList<EdmProperty>();
			/*
			 * initialize query builder
			 */
			TmqlQueryBuilder queryBuilder = new TmqlQueryBuilder();
			/*
			 * convert names to property
			 */
			List<NameTypeDAO> nameTypes = getNameTypesByTmql(typeId);
			for (NameTypeDAO nameType : nameTypes) {
				handleCharacteristicsDAO(edmProperties, queryBuilder, nameType);
			}
			/*
			 * convert occurrences to property
			 */
			List<OccurrenceTypeDAO> occurrenceTypes = getOccurrenceTypesByTmql(typeId);
			for (OccurrenceTypeDAO occurrenceType : occurrenceTypes) {
				handleCharacteristicsDAO(edmProperties, queryBuilder, occurrenceType);
			}
			/*
			 * create entity type
			 */
			EdmEntityType entityType = new EdmEntityType(getNamespace(), null, typeLabel, null, Enumerable.create(ID_PROPNAME).toList(),
					edmProperties, new ArrayList<EdmNavigationProperty>());
			EdmEntitySet entitySet = new EdmEntitySet(typeLabel, entityType);
			edmEntitySets.add(entitySet);
			this.edmEntityTypeIds.put(typeLabel, typeId);
			this.edmEntityTypes.add(entityType);
			this.edmEntityTypesById.put(typeId, entityType);
			getTmqlHelper().registerInstancePreparedStatement(typeId, queryBuilder.toInstanceQuery());
			getTmqlHelper().registerTypePreparedStatement(typeId, queryBuilder.toInstancesQuery());
			registerQueryBuilder(typeId, queryBuilder);
		}

		/*
		 * convert associations to navigation entry
		 */
		List<AssociationTypeDAO> associationSignatures = getAssociationSignaturesByTmql();
		if (getAssociationMode() == EContentProviderConfiguration.STRONG_ASSOCIATION) {
			handleStrongAssociation(associationSignatures);
		} else {
			handleFlatAssociation(associationSignatures);
		}
	}

	/**
	 * Internal method to handle a characteristics DAO.
	 * 
	 * @param edmProperties
	 *            the properties
	 * @param queryBuilder
	 *            the query builder
	 * @param dao
	 *            the DAO to handle
	 */
	private void handleCharacteristicsDAO(List<EdmProperty> edmProperties, TmqlQueryBuilder queryBuilder, CharacteristicsTypeDAO dao) {
		/*
		 * iterate over all identifiers
		 */
		for (EdmPropertyIdentifier identifier : dao.getEdmPropertyIdentifiers()) {
			/*
			 * create property
			 */
			EdmProperty property = new EdmProperty(identifier.getPropertyName(), dao.getDatatype(), true);
			edmProperties.add(property);
			/*
			 * add to TMQL query builder
			 */
			queryBuilder.fetch(identifier);
			/*
			 * store for later reuse
			 */
			edmPropertyTypeIds.put(identifier.getPropertyName(), identifier);
		}
	}

	/**
	 * Method handle association signatures as flat associations
	 * 
	 * @param associationSignatures
	 *            the association signatures
	 */
	protected void handleFlatAssociation(List<AssociationTypeDAO> associationSignatures) {
		Set<EdmNavigationPropertyIdentifier> alreadyKnown = new HashSet<EdmNavigationPropertyIdentifier>();
		/*
		 * iterate over all DAO
		 */
		for (AssociationTypeDAO dao : associationSignatures) {
			/*
			 * ignore type-instance and supertype-subtype
			 */
			if (dao.getTypeLabel() == null || dao.getTypeLabel().equalsIgnoreCase(Namespaces.TMDM.SUPERTYPE_SUBTYPE)
					|| dao.getTypeLabel().equalsIgnoreCase(Namespaces.TMDM.TYPE_INSTANCE)) {
				continue;
			}
			/*
			 * ignore topics without type
			 */
			if (dao.getRightPlayerTypeDAO() == null) {
				continue;
			}
			/*
			 * get entity types
			 */
			EdmEntityType fromRoleET = this.edmEntityTypesById.get(dao.getLeftPlayerTypeDAO().getTypeId());
			EdmEntityType toRoleET = this.edmEntityTypesById.get(dao.getRightPlayerTypeDAO().getTypeId());
			/*
			 * ignore missed entity types
			 */
			if (fromRoleET == null || toRoleET == null) {
				continue;
			}
			/*
			 * iterate over all identifiers
			 */
			for (EdmNavigationPropertyIdentifier identifier : dao.getFlatRelationshipIdentifier()) {
				/*
				 * is already bound
				 */
				if (alreadyKnown.contains(identifier)) {
					System.out.println(identifier.getPropertyName() + " already known and will be ignored!");
					continue;
				}
				alreadyKnown.add(identifier);
				/*
				 * create roles
				 */
				EdmAssociationEnd fromRole = new EdmAssociationEnd(dao.getLeftPlayerTypeDAO().getTypeLabel(), fromRoleET, EdmMultiplicity.MANY);
				EdmAssociationEnd toRole = new EdmAssociationEnd(dao.getRightPlayerTypeDAO().getTypeLabel(), toRoleET, EdmMultiplicity.MANY);
				/*
				 * create relationship
				 */
				EdmAssociation relationship = new EdmAssociation(namespace, null, identifier.getPropertyName(), fromRole, toRole);
				edmAssociations.add(relationship);
				edmPropertyTypeIds.put(identifier.getPropertyName(), identifier);
				/*
				 * create navigation properties
				 */
				EdmNavigationProperty navigationPropertyFrom = new EdmNavigationProperty(identifier.getPropertyName(), relationship, fromRole, toRole);
				fromRoleET.navigationProperties.add(navigationPropertyFrom);
				EdmNavigationProperty navigationPropertyTo = new EdmNavigationProperty(identifier.getPropertyName(), relationship, toRole, fromRole);
				toRoleET.navigationProperties.add(navigationPropertyTo);
			}
		}
	}

	/**
	 * Method handle association signatures as strong associations
	 * 
	 * @param associationSignatures
	 *            the association signatures
	 */
	protected void handleStrongAssociation(List<AssociationTypeDAO> associationSignatures) {
		Set<EdmNavigationPropertyIdentifier> alreadyKnown = new HashSet<EdmNavigationPropertyIdentifier>();
		/*
		 * iterate over all signatures
		 */
		for (AssociationTypeDAO associationSignature : associationSignatures) {
			/*
			 * ignore type-instance and supertype-subtype
			 */
			if (associationSignature.getTypeLabel() == null
					|| associationSignature.getTypeLabel().equalsIgnoreCase(Namespaces.TMDM.SUPERTYPE_SUBTYPE)
					|| associationSignature.getTypeLabel().equalsIgnoreCase(Namespaces.TMDM.TYPE_INSTANCE)) {
				continue;
			}
			/*
			 * ignore topics without type
			 */
			if (associationSignature.getRightPlayerTypeDAO() == null) {
				continue;
			}
			/*
			 * get information of association signature
			 */
			final String fromPlayerTypeId = associationSignature.getLeftPlayerTypeDAO().getTypeId();
			final String fromPlayerTypeLabel = associationSignature.getLeftPlayerTypeDAO().getTypeLabel();
			final String fromRoleId = associationSignature.getLeftRoleTypeDAO().getTypeId();
			final String fromRoleLabel = associationSignature.getLeftRoleTypeDAO().getTypeLabel();
			final String toRoleId = associationSignature.getRightRoleTypeDAO().getTypeId();
			final String toRoleLabel = associationSignature.getRightRoleTypeDAO().getTypeLabel();
			final String toPlayerTypeId = associationSignature.getRightPlayerTypeDAO().getTypeId();
			final String toPlayerTypeLabel = associationSignature.getRightPlayerTypeDAO().getTypeLabel();
			/*
			 * get EDM entity types
			 */
			EdmEntityType fromRoleET = this.edmEntityTypesById.get(fromPlayerTypeId);
			EdmEntityType toRoleET = this.edmEntityTypesById.get(toPlayerTypeId);
			/*
			 * ignore missed entity types
			 */
			if (fromRoleET == null || toRoleET == null) {
				continue;
			}
			/*
			 * iterate over all identifier
			 */
			for (EdmNavigationPropertyIdentifier identifier : associationSignature.getStrongRelationshipIdentifier()) {
				/*
				 * check if already known
				 */
				if (alreadyKnown.contains(identifier)) {
					continue;
				}
				alreadyKnown.add(identifier);
				/*
				 * create 'fromRole'
				 */
				EdmAssociationEnd fromRole = new EdmAssociationEnd(fromRoleLabel, fromRoleET, EdmMultiplicity.MANY);
				this.edmEntityTypeIds.put(fromRoleLabel, fromRoleId);
				this.edmEntityTypeIds.put(fromPlayerTypeLabel, fromPlayerTypeId);
				/*
				 * create 'toRole'
				 */
				EdmAssociationEnd toRole = new EdmAssociationEnd(toRoleLabel, toRoleET, EdmMultiplicity.MANY);
				this.edmEntityTypeIds.put(toRoleLabel, toRoleId);
				this.edmEntityTypeIds.put(toPlayerTypeLabel, toPlayerTypeId);

				/*
				 * create relationship
				 */
				EdmAssociation relationship = new EdmAssociation(namespace, null, identifier.getPropertyName(), fromRole, toRole);
				this.edmPropertyTypeIds.put(identifier.getPropertyName(), identifier);
				/*
				 * create navigation properties
				 */
				EdmNavigationProperty fromNavigationProperty = new EdmNavigationProperty(identifier.getPropertyName(), relationship, fromRole, toRole);
				edmAssociations.add(relationship);
				fromRoleET.navigationProperties.add(fromNavigationProperty);
				EdmNavigationProperty toNavigationProperty = new EdmNavigationProperty(identifier.getPropertyName(), relationship, toRole, fromRole);
				edmAssociations.add(relationship);
				toRoleET.navigationProperties.add(toNavigationProperty);
			}
		}
	}

	/**
	 * Returns the EDM entity type by id of an entity
	 * 
	 * @param id
	 *            the entity type id
	 * @return the EDM entity type and never <code>null</code>
	 * @throws TopicMapsODataException
	 *             thrown if id is missed
	 */
	public EdmEntityType getEntityTypeById(final String id) throws TopicMapsODataException {
		/*
		 * get type id
		 */
		final EdmEntityType type = edmEntityTypesById.get(id);
		if (type == null) {
			throw new TopicMapsODataException("Missing entity type for id '" + id + "'!");
		}
		return type;
	}

	/**
	 * Returns the entity id of an entity
	 * 
	 * @param entityName
	 *            the entity name
	 * @return the id and never <code>null</code>
	 * @throws TopicMapsODataException
	 *             thrown if id is missed
	 */
	public String getEntityId(final String entityName) throws TopicMapsODataException {
		/*
		 * get type id
		 */
		final String typeId = edmEntityTypeIds.get(entityName);
		if (typeId == null) {
			throw new TopicMapsODataException("Missing type id for entity type '" + entityName + "'!");
		}
		return typeId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EdmEntityType> getEntityTypes() {
		return edmEntityTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EdmEntitySet> getEntitySets() {
		return edmEntitySets;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EdmAssociation> getAssociations() {
		return edmAssociations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OEntity getEntity(EdmEntitySet entitySet, String entityType, String id) throws TopicMapsODataException {
		/*
		 * get property values
		 */
		Object[] values = getEntityData(getEntityId(entityType), id);
		if (values.length == 0) {
			throw new TopicMapsODataException("The entity with key '" + id + "' not found!");
		}
		/*
		 * translate values to EDM properties
		 */
		List<EdmProperty> edmProperties = entitySet.type.properties;
		return createEntity(entitySet, edmProperties, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OEntity> getEntities(EdmEntitySet entitySet, QueryInfo queryInfo) {
		final String typeId = getEntityId(entitySet.type.name);
		/*
		 * get the query builder
		 */
		TmqlQueryBuilder queryBuilder = queryBuilders.get(typeId);
		if (queryBuilder == null) {
			throw new TopicMapsODataException("Missing querybuilder for entity set '" + entitySet.type.name + "'!");
		}
		try {
			queryBuilder = queryBuilder.clone();
		} catch (CloneNotSupportedException e) {
			throw new TopicMapsODataException(e);
		}
		/*
		 * create filters and oder query parts
		 */
		proceedQueryInfo(queryBuilder, queryInfo);
		/*
		 * create results
		 */
		List<OEntity> entities = new ArrayList<OEntity>();
		Object[][] values = getEntityData(typeId, queryBuilder);
		for (Object value[] : values) {
			OEntity entity = createEntity(entitySet, entitySet.type.properties, value);
			entities.add(entity);
		}
		return entities;
	}

	/**
	 * Method called to proceed the query info given by the request. The method can be override to change behavior.
	 * 
	 * @param queryBuilder
	 *            the query builder
	 * @param queryInfo
	 *            the query info
	 * @throws TopicMapsODataException
	 *             thrown if anything fails
	 */
	protected void proceedQueryInfo(TmqlQueryBuilder queryBuilder, QueryInfo queryInfo) throws TopicMapsODataException {
		/*
		 * check offset value defined by $skip
		 */
		if (queryInfo.skip != null && queryInfo.skip > 0L) {
			queryBuilder.offset(queryInfo.skip - 1);
		}
		/*
		 * check limit value defined by $top
		 */
		if (queryInfo.top != null && queryInfo.top > 0L) {
			queryBuilder.limit(queryInfo.top);
		}
		/*
		 * check order by
		 */
		if (queryInfo.orderBy != null) {
			for (OrderByExpression orderBy : queryInfo.orderBy) {
				CommonExpression expression = orderBy.getExpression();
				if (!(expression instanceof EntitySimpleProperty)) {
					throw new TopicMapsODataException("Current implementation only supports simple property expressions");
				}
				final IEdmPropertyIdentifier identifier = getPropertyType(((EntitySimpleProperty) expression).getPropertyName());
				queryBuilder.orderBy(identifier, orderBy.isAscending());
			}
		}
		/*
		 * check filter
		 */
		if (queryInfo.filter != null) {
			ICriterion criterion = ODataUtils.transformToTmql(this, queryBuilder, queryInfo.filter);
			queryBuilder.where(criterion);
		}
	}

	/**
	 * Internal method called to get data of a entity by its id and type. The order of results should be similar to
	 * their definition.
	 * 
	 * 
	 * @param typeId
	 *            the id of type
	 * @param entityId
	 *            the id of entity
	 * 
	 * @return the string array
	 */
	protected abstract Object[] getEntityData(final String typeId, final String entityId);

	/**
	 * Internal method called to get data of all entities by their type. The order of results should be similar to their
	 * definition.
	 * 
	 * @param typeId
	 *            the id of type
	 * @param query
	 *            the query builder
	 * 
	 * @return the string array
	 */
	protected abstract Object[][] getEntityData(final String typeId, final TmqlQueryBuilder query);

	/**
	 * Internal method called to get data of the topic types by TMQL.
	 * 
	 * @return the list of DAO
	 */
	protected abstract List<TopicTypeDAO> getTopicTypesByTmql();

	/**
	 * Internal method called to get data of the name type by TMQL in context of the given topic type.
	 * 
	 * @param typeId
	 *            the id of type
	 * @return the list of DAO
	 */
	protected abstract List<NameTypeDAO> getNameTypesByTmql(final String typeId);

	/**
	 * Internal method called to get data of the occurrence type by TMQL in context of the given topic type.
	 * 
	 * @param typeId
	 *            the id of type
	 * @return the list of DAO
	 */
	protected abstract List<OccurrenceTypeDAO> getOccurrenceTypesByTmql(final String typeId);

	/**
	 * Internal method called to get all association signatures of an entity
	 * 
	 * @param entityId
	 *            the entity
	 * @return the association signatures
	 */
	protected abstract List<AssociationTypeDAO> getAssociationSignaturesOfEntityByTmql(final String entityId);

	/**
	 * Internal method called to get all counter players of the given entity. If the association type is given, only
	 * counter players of an association with this type are returned. If counter player type is given, only counter
	 * players of this type are returned. If the theme is given, only associations with a scope containing this theme
	 * are returned. If the theme is <code>null</code>, only association with the unconstrained scope are returned.
	 * 
	 * @param entityId
	 *            the entity id
	 * @param associationTypeId
	 *            the association type id
	 * @param counterTypeId
	 *            the counter player type id
	 * @param themeId
	 *            the theme id
	 * @return a list of counter players
	 */
	protected abstract List<CounterPartDAO> getCounterPlayers(final String entityId, final String associationTypeId, final String counterTypeId,
			final String themeId);

	/**
	 * Returns all counter players returned as result of the given query.
	 * 
	 * @param query
	 *            the query
	 * @return the counter players
	 */
	protected abstract List<CounterPartDAO> getCounterPlayers(final String query);

	/**
	 * Internal method called to get data of the association signatures by TMQL in context of the given topic type.
	 * 
	 * @return the list of DAO
	 */
	protected abstract List<AssociationTypeDAO> getAssociationSignaturesByTmql();

	/**
	 * method called to store the builder for a type id
	 * 
	 * @param typeId
	 *            the type id
	 * @param builder
	 *            the builder
	 */
	protected void registerQueryBuilder(final String typeId, final TmqlQueryBuilder builder) {
		if (queryBuilders == null) {
			queryBuilders = new HashMap<String, TmqlQueryBuilder>();
		}
		queryBuilders.put(typeId, builder);
	}

	/**
	 * Internal method to create the entity from given string values
	 * 
	 * @param entitySet
	 *            the entity set
	 * @param edmProperties
	 *            the EDM properties
	 * @param values
	 *            the values
	 * @return the created {@link OEntity}
	 */
	protected OEntity createEntity(EdmEntitySet entitySet, List<EdmProperty> edmProperties, Object[] values) {
		final List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		final String entityId = values[0].toString();
		properties.add(OProperties.int64(ID_PROPNAME, Long.parseLong(entityId)));
		/*
		 * extract properties
		 */
		for (int i = 0; i < edmProperties.size() && i + 1 < values.length; i++) {
			EdmProperty edmProperty = edmProperties.get(i);
			Object value_ = values[i + 1];
			if (value_ == null) {
				continue;
			}
			String value = value_.toString();
			try {
				/*
				 * set property by its data type
				 */
				if (edmProperty.type == EdmType.STRING) {
					properties.add(OProperties.string(edmProperty.name, value));
				} else if (edmProperty.type == EdmType.INT32) {
					properties.add(OProperties.int32(edmProperty.name, Integer.valueOf(value)));
				} else if (edmProperty.type == EdmType.INT64) {
					properties.add(OProperties.int64(edmProperty.name, Long.valueOf(value)));
				} else if (edmProperty.type == EdmType.DATETIME) {
					properties.add(OProperties.datetime(edmProperty.name, LiteralUtils.asDateTime(value).getTime()));
				} else if (edmProperty.type == EdmType.TIME) {
					properties.add(OProperties.time(edmProperty.name, LocalTime.fromCalendarFields(LiteralUtils.asTime(value))));
				} else if (edmProperty.type == EdmType.DECIMAL) {
					properties.add(OProperties.decimal(edmProperty.name, new BigDecimal(value)));
				} else if (edmProperty.type == EdmType.DOUBLE) {
					properties.add(OProperties.decimal(edmProperty.name, Double.valueOf(value)));
				} else if (edmProperty.type == EdmType.BOOLEAN) {
					properties.add(OProperties.boolean_(edmProperty.name, Boolean.valueOf(value)));
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
		/*
		 * check OLink
		 */
		List<OLink> olinks = createOLinks(getEntityId(entitySet.type.name), entityId);
		/*
		 * create entity
		 */
		return OEntities.create(entitySet, properties, olinks, entityId);
	}

	/**
	 * Method generate OLinks for the given entity type and entity Id
	 */
	protected List<OLink> createOLinks(final String entityTypeId, final String entityId) {
		EdmEntityType type = getEntityTypeById(entityTypeId);
		List<OLink> olinks = new ArrayList<OLink>();
		for (EdmNavigationProperty property : type.navigationProperties) {
			final String name = property.relationship.name;
			IEdmPropertyIdentifier identifier = getPropertyType(name);
			if (!(identifier instanceof IEdmNavigationPropertyIdentifier)) {
				throw new RuntimeException("Missed or invalid property identifier for '" + name + "'");
			}
			IEdmNavigationPropertyIdentifier navProperty = (IEdmNavigationPropertyIdentifier) identifier;
			OLink olink = OLinks.link(identifier.getPropertyName(), navProperty.getDAO().getRightPlayerTypeDAO().getTypeLabel(), navProperty.getDAO()
					.getRightPlayerTypeDAO().getTypeLabel());
			olinks.add(olink);
		}
		return olinks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OEntity> getEntities(EdmEntitySet counterPlayerEdmEntitySet, String entityId, String navigationProperty, QueryInfo queryInfo) {
		/*
		 * extract IDs
		 */
		IEdmPropertyIdentifier identifier = getPropertyType(navigationProperty);
		if (!(identifier instanceof IEdmNavigationPropertyIdentifier)) {
			throw new RuntimeException("Missed or invalid property identifier for '" + navigationProperty + "'");
		}
		EdmNavigationPropertyIdentifier navProperty = (EdmNavigationPropertyIdentifier) identifier;
		/*
		 * extract entities
		 */
		List<OEntity> entities = new ArrayList<OEntity>();
		/*
		 * get counter parts
		 */
		List<CounterPartDAO> counterParts;
		TopicTypeDAO counterDAO = navProperty.getDAO().getCounterPlayerDAO(counterPlayerEdmEntitySet.type.name);
		EdmEntitySet edmEntitySet = getService().getService(getTopicMapId()).getEdmEntitySet(counterDAO.getTypeLabel());
		List<OEntity> allCounterParts = getEntities(edmEntitySet, queryInfo);
		/*
		 * extract filter matches as ID of matching entities
		 */
		List<String> filterMatches = new ArrayList<String>();
		for (OEntity e : allCounterParts) {
			filterMatches.add(e.getId().toString());
		}
		/*
		 * no instance found matching the condition
		 */
		if (filterMatches.isEmpty()) {
			return entities;
		}
		/*
		 * get navigation property and the counter players
		 */
		counterParts = getCounterPlayers(entityId, navProperty.getAssociationTypeId(), counterDAO.getTypeId(), navProperty.getThemeId());
		/*
		 * transform
		 */
		for (CounterPartDAO dao : counterParts) {
			/*
			 * is filter match?
			 */
			if (filterMatches.contains(dao.getCounterPlayerId())) {
				entities.add(getEntity(edmEntitySet, counterDAO.getTypeLabel(), dao.getCounterPlayerId()));
			}
		}
		return entities;
	}

	/**
	 * Access the internal topic map service
	 * 
	 * @return the service
	 */
	protected TopicMapMetadata getService() {
		return service;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configAssociationHandling(EContentProviderConfiguration cfg) {
		if (cfg != EContentProviderConfiguration.FLAT_ASSOCIATION && cfg != EContentProviderConfiguration.STRONG_ASSOCIATION) {
			throw new RuntimeException("Only FLAT_ASSOCIATION or STRONG_ASSOCIATION are allowed as association mode.");
		}
		associationMode = cfg;
	}

	/**
	 * Returns the association mode
	 * 
	 * @return the association mode
	 */
	EContentProviderConfiguration getAssociationMode() {
		return associationMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IEdmPropertyIdentifier getPropertyType(String property) throws TopicMapsODataException {
		if (edmPropertyTypeIds.containsKey(property)) {
			return edmPropertyTypeIds.get(property);
		}
		throw new TopicMapsODataException("Missing type for proeprty '" + property + "'!");
	}

	/**
	 * Returns the topic map id
	 * 
	 * @return the topic map id
	 */
	protected String getTopicMapId() {
		return topicMapId;
	}

	/**
	 * Returns the internal reference of the TMQL helper
	 * 
	 * @return TMQL helper
	 */
	protected abstract TmqlHelper getTmqlHelper();
}
