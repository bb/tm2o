package de.topicmapslab.odata.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.odata4j.edm.EdmType;

import de.topicmapslab.odata.exception.TopicMapsODataException;
import de.topicmapslab.odata.util.EdmUtils;
import de.topicmapslab.tmql4j.components.processor.results.model.IResult;
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet;

/**
 * DAO containing all information about an occurrence signature
 * 
 * @author Sven Krosse
 */
public class OccurrenceTypeDAO extends CharacteristicsTypeDAO {
	/**
	 * constructor
	 */
	public OccurrenceTypeDAO() {
		// VOID
	}

	/**
	 * constructor
	 * 
	 * @param typeId
	 *            the topic type ID
	 * @param typeLabel
	 *            the topic type label
	 * @param datatype
	 *            the data type
	 * @param isDynamic
	 *            the dynamic flag
	 * @param maxOcc
	 *            maximum occurrence
	 * @param themeDAOs
	 *            the theme DAOs
	 * @param nonScopedInstance
	 *            flag indicates if there is at least one instance of this DAO without a scope
	 */
	public OccurrenceTypeDAO(String typeId, String typeLabel, EdmType datatype, boolean isDynamic, List<TopicTypeDAO> themeDAOs,
			boolean nonScopedInstance) {
		super(typeId, typeLabel, datatype, isDynamic, themeDAOs, nonScopedInstance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IResult result) throws TopicMapsODataException {
		super.load(result);
		if (result == null || result.size() < 8) {
			throw new TopicMapsODataException("The number of arguments is invalid. DAO expects 8 arguments but was " + result.size() + ".");
		}
		setDatatype(EdmUtils.xsdToEdm(result.get(2).toString()));
		BigInteger minOcc = result.get(3);
		setDynamic(minOcc.longValue() == 0);
		setThemes(result.get(5), result.get(6));
		hasNonScopedInstance(result.get(7));
	}

	/**
	 * Method extracts the occurrence type DAOs form the given result set
	 * 
	 * @param resultSet
	 *            the result set
	 * @return the list of DAOs
	 * @throws TopicMapsODataException
	 *             throws if conversation failed
	 */
	public static List<OccurrenceTypeDAO> getOccurrenceTypeDAOs(IResultSet<?> resultSet) throws TopicMapsODataException {
		System.out.println(resultSet);
		List<OccurrenceTypeDAO> daos = new ArrayList<OccurrenceTypeDAO>();
		for (IResult r : resultSet) {
			OccurrenceTypeDAO dao = new OccurrenceTypeDAO();
			dao.load(r);
			daos.add(dao);
		}
		return daos;
	}
}