package de.topicmapslab.odata.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.odata4j.edm.EdmType;

import de.topicmapslab.odata.exception.TopicMapsODataException;
import de.topicmapslab.tmql4j.components.processor.results.model.IResult;
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet;

/**
 * DAO containing all information about a name signature
 * 
 * @author Sven Krosse
 */
public class NameTypeDAO extends CharacteristicsTypeDAO {

	/**
	 * constructor
	 */
	public NameTypeDAO() {
		// VOID
	}

	/**
	 * constructor
	 * 
	 * @param typeId
	 *            the topic type ID
	 * @param typeLabel
	 *            the topic type label
	 * @param isDynamic
	 *            dynamic property
	 * @param themeDAOs
	 *            the theme DAOs
	 * @param nonScopedInstance
	 *            flag indicates if there is at least one instance of this DAO without a scope
	 */
	public NameTypeDAO(String typeId, String typeLabel, boolean isDynamic, List<TopicTypeDAO> themeDAOs, boolean nonScopedInstance) {
		super(typeId, typeLabel, EdmType.STRING, isDynamic, themeDAOs, nonScopedInstance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IResult result) throws TopicMapsODataException {
		super.load(result);
		if (result == null || result.size() < 7) {
			throw new TopicMapsODataException("The number of arguments is invalid. DAO expects 7 arguments but was " + result.size() + ".");
		}
		BigInteger minOcc = result.get(2);
		setDynamic(minOcc.longValue() == 0);
		setThemes(result.get(4), result.get(5));
		hasNonScopedInstance(result.get(6));
	}

	/**
	 * Method extracts the name type DAOs form the given result set
	 * 
	 * @param resultSet
	 *            the result set
	 * @return the list of DAOs
	 * @throws TopicMapsODataException
	 *             throws if conversation failed
	 */
	public static List<NameTypeDAO> getNameTypeDAOs(IResultSet<?> resultSet) throws TopicMapsODataException {
		List<NameTypeDAO> daos = new ArrayList<NameTypeDAO>();
		for (IResult r : resultSet) {
			NameTypeDAO dao = new NameTypeDAO();
			dao.load(r);
			daos.add(dao);
		}
		return daos;
	}

}