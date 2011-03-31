package de.topicmapslab.odata.dao;

import de.topicmapslab.odata.exception.TopicMapsODataException;
import de.topicmapslab.tmql4j.components.processor.results.model.IResult;

/**
 * Interface definition of a DAO
 * 
 * @author Sven Krosse
 */
public interface IDAO {

	/**
	 * Transforms the given result set to a DAO object
	 * 
	 * @param result
	 *            the result
	 */
	public void load(IResult result) throws TopicMapsODataException;

}
