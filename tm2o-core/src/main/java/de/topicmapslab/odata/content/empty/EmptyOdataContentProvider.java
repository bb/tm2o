package de.topicmapslab.odata.content.empty;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.producer.QueryInfo;

import de.topicmapslab.odata.TopicMapMetadata;
import de.topicmapslab.odata.config.EContentProviderConfiguration;
import de.topicmapslab.odata.content.IOdataContentProvider;
import de.topicmapslab.odata.edm.IEdmPropertyIdentifier;
import de.topicmapslab.odata.exception.TopicMapsODataException;

public class EmptyOdataContentProvider implements IOdataContentProvider {

	private String namespace;

	@Override
	public List<EdmEntitySet> getEntitySets() {
		return Collections.emptyList();
	}

	@Override
	public List<EdmAssociation> getAssociations() {
		return Collections.emptyList();
	}

	@Override
	public void setService(TopicMapMetadata service) {
		// VOID
	}

	@Override
	public void initialize(String namespace, Properties properties) throws TopicMapsODataException {
		this.namespace = namespace;
	}

	@Override
	public List<EdmEntityType> getEntityTypes() {
		return Collections.emptyList();
	}

	@Override
	public OEntity getEntity(EdmEntitySet entitySet, String entityType, String id) throws TopicMapsODataException {
		List<OProperty<?>> props = Collections.emptyList();
		List<OLink> olinks = Collections.emptyList();
		return OEntities.create(entitySet, props, olinks, id);
	}

	@Override
	public List<OEntity> getEntities(EdmEntitySet entitySet, QueryInfo queryInfo) {
		return Collections.emptyList();
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getEntityId(String entityName) throws TopicMapsODataException {
		return null;
	}

	@Override
	public IEdmPropertyIdentifier getPropertyType(String property) throws TopicMapsODataException {
		return null;
	}

	@Override
	public List<OEntity> getEntities(EdmEntitySet entitySet, String entityId, String navigationProperty, QueryInfo info) {
		return Collections.emptyList();
	}

	@Override
	public void configAssociationHandling(EContentProviderConfiguration cfg) {
		// VOID
	}

}
