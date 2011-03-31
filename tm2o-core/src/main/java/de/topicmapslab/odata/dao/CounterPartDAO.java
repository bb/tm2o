package de.topicmapslab.odata.dao;

import java.util.ArrayList;
import java.util.List;

import de.topicmapslab.odata.exception.TopicMapsODataException;
import de.topicmapslab.tmql4j.components.processor.results.model.IResult;
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet;

/**
 * DAO containing information about a counter player
 * 
 * @author Sven Krosse
 */
public class CounterPartDAO implements IDAO {

	private String counterPlayerId;

	/**
	 * constructor
	 */
	public CounterPartDAO() {
		// VOID
	}

	/**
	 * constructor
	 * 
	 * @param counterPlayerId
	 *            the ID of counter player
	 */
	public CounterPartDAO(final String counterPlayerId) {
		this.counterPlayerId = counterPlayerId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IResult result) throws TopicMapsODataException {
		if (result == null || result.size() < 1) {
			throw new TopicMapsODataException("The number of arguments is invalid. DAO expects 1 arguments.");
		}
		this.counterPlayerId = result.get(0);
	}

	/**
	 * Returns the id of counter player
	 * 
	 * @return the ID of counter player
	 */
	public String getCounterPlayerId() {
		return counterPlayerId;
	}

	/**
	 * Method extracts the counter player DAOs form the given result set
	 * 
	 * @param resultSet
	 *            the result set
	 * @return the list of DAOs
	 * @throws TopicMapsODataException
	 *             throws if conversation failed
	 */
	public static List<CounterPartDAO> getDAOs(IResultSet<?> resultSet) throws TopicMapsODataException {
		List<CounterPartDAO> daos = new ArrayList<CounterPartDAO>();
		for (IResult r : resultSet) {
			CounterPartDAO dao = new CounterPartDAO();
			dao.load(r);
			daos.add(dao);
		}
		return daos;
	}

}
