package de.topicmapslab.odata;

import java.util.List;
import java.util.Properties;

import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.InlineCount;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;

import de.topicmapslab.odata.content.IOdataContentProvider;

public class TopicMapODataProducer implements ODataProducer {

	private final TopicMapMetadata metadata;

	/**
	 * constructor
	 * 
	 * @param contentProvider
	 *            the content provider
	 * @param namespace
	 *            the namespace
	 */
	public TopicMapODataProducer(final Class<? extends IOdataContentProvider> contentProviderClazz, final Properties properties,
			final String namespace) {
		this.metadata = new TopicMapMetadata(contentProviderClazz, properties, namespace);
	}

	public TopicMapMetadata getService() {
		return metadata;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		// VOID
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityResponse createEntity(String entitySetName, OEntity entity) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteEntity(String entitySetName, Object entityKey) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	/**
	 * {@inheritDoc}
	 */
	public EntitiesResponse getEntities(final String topicMapId, String entitySetName, QueryInfo queryInfo) {
		/*
		 * get entity set
		 */
		final EdmEntitySet entitySet = metadata.getService(topicMapId).getEdmEntitySet(entitySetName);
		if (entitySet == null) {
			throw new RuntimeException("Entity set for name '" + entitySetName + "' not found!");
		}
		/*
		 * generate entities
		 */
		final List<OEntity> entities = metadata.getEntities(topicMapId, entitySetName, queryInfo);
		final Integer inlineCount = queryInfo.inlineCount == InlineCount.ALLPAGES ? entities.size() : null;
		return new EntitiesResponse() {

			@Override
			public List<OEntity> getEntities() {
				return entities;
			}

			@Override
			public EdmEntitySet getEntitySet() {
				return entitySet;
			}

			@Override
			public Integer getInlineCount() {
				return inlineCount;
			}

			@Override
			public String getSkipToken() {
				return null;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityResponse getEntity(final String topicMapId, String entitySetName, Object entityKey) {
		/*
		 * get entity set
		 */
		final EdmEntitySet entitySet = metadata.getService(topicMapId).getEdmEntitySet(entitySetName);
		if (entitySet == null) {
			throw new RuntimeException("Entity set for name '" + entitySetName + "' not found!");
		}
		/*
		 * get entity
		 */
		final OEntity entity = metadata.getEntity(topicMapId, entitySetName, entityKey.toString());
		return new EntityResponse() {
			@Override
			public EdmEntitySet getEntitySet() {
				return entitySet;
			}

			@Override
			public OEntity getEntity() {
				return entity;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public EdmDataServices getMetadata(final String topicMapId) {
		return metadata.getService(topicMapId);
	}

	/**
	 * {@inheritDoc}
	 */
	public void mergeEntity(String entitySetName, Object entityKey, OEntity entity) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateEntity(String entitySetName, Object entityKey, OEntity entity) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	@Override
	public EntityResponse createEntity(String entitySetName, Object entityKey, String navProp, OEntity entity) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	@Override
	public EntitiesResponse getNavProperty(final String topicMapId, String entitySetName, Object entityKey, String navProp, QueryInfo queryInfo) {
		/*
		 * get entity set
		 */
		final EdmEntitySet entitySet = metadata.getService(topicMapId).getEdmEntitySet(entitySetName);
		if (entitySet == null) {
			throw new RuntimeException("Entity set for name '" + entitySetName + "' not found!");
		}
		/*
		 * generate entities
		 */
		final List<OEntity> entities = metadata.getEntities(topicMapId, entitySetName, entityKey.toString(), navProp, queryInfo);
		final Integer inlineCount = queryInfo.inlineCount == InlineCount.ALLPAGES ? entities.size() : null;
		return new EntitiesResponse() {

			@Override
			public List<OEntity> getEntities() {
				return entities;
			}

			@Override
			public EdmEntitySet getEntitySet() {
				return entitySet;
			}

			@Override
			public Integer getInlineCount() {
				return inlineCount;
			}

			@Override
			public String getSkipToken() {
				return null;
			}
		};
	}

}
