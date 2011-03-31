package de.topicmapslab.odata.dao;

import java.util.ArrayList;
import java.util.List;

import de.topicmapslab.odata.exception.TopicMapsODataException;
import de.topicmapslab.tmql4j.components.processor.results.model.IResult;
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet;

/**
 * a simple data object containing all information of the
 * 
 * @author Sven Krosse
 */
public class TopicTypeDAO implements IDAO {

	private String typeId;
	private String typeLabel;

	/**
	 * constructor
	 * 
	 * @param typeId
	 *            the topic type ID
	 * @param typeLabel
	 *            the topic type label
	 */
	public TopicTypeDAO(String typeId, String typeLabel) {
		this.typeId = typeId;
		this.typeLabel = cleanPropertyKey(typeLabel);
	}

	/**
	 * constructor
	 */
	public TopicTypeDAO() {
		// VOID
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IResult result) throws TopicMapsODataException {
		if (result == null || result.size() < 2) {
			throw new TopicMapsODataException("The number of arguments is invalid. DAO expects 2 arguments");
		}
		this.typeId = result.get(0);
		this.typeLabel = cleanPropertyKey(result.get(1));
	}

	/**
	 * Returns the internal ID
	 * 
	 * @return the id
	 */
	public String getTypeId() {
		return typeId;
	}

	void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	/**
	 * Returns the internal label
	 * 
	 * @return the label
	 */
	public String getTypeLabel() {
		return typeLabel;
	}

	void setTypeLabel(String typeLabel) {
		this.typeLabel = typeLabel;
	}

	/**
	 * Method extracts the topic type DAOs form the given result set
	 * 
	 * @param resultSet
	 *            the result set
	 * @return the list of DAOs
	 * @throws TopicMapsODataException
	 *             throws if conversation failed
	 */
	public static List<TopicTypeDAO> getDAOs(IResultSet<?> resultSet) throws TopicMapsODataException {
		List<TopicTypeDAO> daos = new ArrayList<TopicTypeDAO>();
		for (IResult r : resultSet) {
			TopicTypeDAO dao = new TopicTypeDAO();
			dao.load(r);
			daos.add(dao);
		}
		return daos;
	}

	/**
	 * utility method to clean to key from white spaces and anything else
	 * 
	 * @param key
	 *            the key
	 * @return the cleaned key
	 */
	public static String cleanPropertyKey(Object key) {
		if (key == null) {
			return null;
		}
		String key_ = key.toString();
		/*
		 * contains slash
		 */
		if (key_.contains("/")) {
			key_ = key_.substring(key_.lastIndexOf("/") + 1);
		}
		/*
		 * contains colon
		 */
		if (key_.contains(":")) {
			key_ = key_.substring(key_.lastIndexOf(":") + 1);
		}
		/*
		 * contains hash
		 */
		if (key_.contains("#")) {
			key_ = key_.substring(key_.lastIndexOf("#") + 1);
		}
		/*
		 * clean white space
		 */
		StringBuffer buffer = new StringBuffer();
		boolean nextUp = true;
		for (int i = 0; i < key_.length(); i++) {
			char c = key_.charAt(i);
			/*
			 * is white space
			 */
			if (c == ' ' || c == '-') {
				nextUp = true;
			}
			/*
			 * character must be upper case
			 */
			else if (nextUp) {
				nextUp = false;
				String up = c + "";
				buffer.append(up.toUpperCase());
			}
			/*
			 * normal add character
			 */
			else {
				buffer.append(c);
			}
		}

		return buffer.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof TopicTypeDAO) {
			if (typeId == null) {
				return ((TopicTypeDAO) obj).typeId == null;
			}
			return ((TopicTypeDAO) obj).typeId.equalsIgnoreCase(typeId);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return typeId == null ? 0 : typeId.hashCode();
	}

}
