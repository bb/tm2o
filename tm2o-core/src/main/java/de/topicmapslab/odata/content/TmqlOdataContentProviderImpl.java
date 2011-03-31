package de.topicmapslab.odata.content;

import java.util.ArrayList;
import java.util.List;

import de.topicmapslab.odata.dao.AssociationTypeDAO;
import de.topicmapslab.odata.dao.CounterPartDAO;
import de.topicmapslab.odata.dao.NameTypeDAO;
import de.topicmapslab.odata.dao.OccurrenceTypeDAO;
import de.topicmapslab.odata.dao.TopicTypeDAO;
import de.topicmapslab.odata.exception.TopicMapsODataException;
import de.topicmapslab.odata.tmql.TmqlHelper;
import de.topicmapslab.odata.tmql.TmqlQueryBuilder;

/**
 * Abstract content provider using TMQL helpers to manage TMQL requests
 * 
 * @author Sven Krosse
 * @param <T>
 *            the type of TMQL helper
 */
public abstract class TmqlOdataContentProviderImpl<T extends TmqlHelper> extends OdataContentProviderImpl {

	/**
	 * constructor
	 * 
	 * @param topicMapId
	 *            the topic map id
	 */
	public TmqlOdataContentProviderImpl(final String topicMapId) {
		super(topicMapId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object[] getEntityData(String typeId, String entityId) {
		Object[][] values = getTmqlHelper().getProperties(typeId, entityId);
		if (values.length == 0) {
			throw new TopicMapsODataException("Missing values for entity with id '" + entityId + "'!");
		}
		return values[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object[][] getEntityData(String typeId, TmqlQueryBuilder query) {
		return getTmqlHelper().runQuery(query.toInstancesQuery(), typeId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<TopicTypeDAO> getTopicTypesByTmql() {
		return getTmqlHelper().getTopicTypes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<NameTypeDAO> getNameTypesByTmql(String typeId) {
		return getTmqlHelper().getNameTypes(typeId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<OccurrenceTypeDAO> getOccurrenceTypesByTmql(String typeId) {
		return getTmqlHelper().getOccurrenceTypes(typeId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<AssociationTypeDAO> getAssociationSignaturesByTmql() {
		return getTmqlHelper().getAssociationSignatures();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<AssociationTypeDAO> getAssociationSignaturesOfEntityByTmql(String entityId) {
		return getTmqlHelper().getAssociationSignaturesOfEntityByTmql(entityId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerQueryBuilder(String typeId, TmqlQueryBuilder builder) {
		super.registerQueryBuilder(typeId, builder);
		/*
		 * register prepared statement
		 */
		getTmqlHelper().registerInstancePreparedStatement(typeId, builder.toInstanceQuery());
		getTmqlHelper().registerTypePreparedStatement(typeId, builder.toInstancesQuery());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<CounterPartDAO> getCounterPlayers(String entityId, String associationTypeId, String counterTypeId, String themeId) {
		return getTmqlHelper().getCounterPlayers(entityId, associationTypeId, counterTypeId, themeId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<CounterPartDAO> getCounterPlayers(String query) {
		List<CounterPartDAO> daos = new ArrayList<CounterPartDAO>();
		for (Object[] values : getTmqlHelper().runQuery(query)) {
			daos.add(new CounterPartDAO(values[0].toString()));
		}
		return daos;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract T getTmqlHelper();

}
