package de.topicmapslab.odata.dao;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.edm.EdmType;

import de.topicmapslab.odata.edm.EdmPropertyIdentifier;

/**
 * DAO containing all information about characteristics.
 * 
 * @author Sven Krosse
 */
public abstract class CharacteristicsTypeDAO extends ScopedTypeDAO {

	/**
	 * dynamic flag
	 */
	private boolean isDynamic;
	/**
	 * the datatype
	 */
	private EdmType datatype = EdmType.STRING;

	/**
	 * constructor
	 */
	public CharacteristicsTypeDAO() {
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
	 *            the EDM data type
	 * @param isDynamic
	 *            flag if this characteristics DAO is dynamic
	 * @param themeDAOs
	 *            the theme DAOs
	 * @param nonScopedInstance
	 *            flag indicates if there is at least one instance of this DAO without a scope
	 */
	public CharacteristicsTypeDAO(String typeId, String typeLabel, EdmType datatype, boolean isDynamic, List<TopicTypeDAO> themeDAOs,
			boolean nonScopedInstance) {
		super(typeId, typeLabel, themeDAOs, nonScopedInstance);
		this.datatype = datatype;
		this.isDynamic = isDynamic;
	}

	/**
	 * Modify the dynamic state. It is only dynamic if at least one instance of an entity type does not have this
	 * property.
	 * 
	 * @param isDynamic
	 *            <code>true</code> if it is dynamic.
	 */
	void setDynamic(boolean isDynamic) {
		this.isDynamic = isDynamic;
	}

	/**
	 * Checks if the DAO is dynamic. It is only dynamic if at least one instance of an entity type does not have this
	 * property.
	 * 
	 * @return <code>true</code> if it is dynamic, <code>false</code> otherwise.
	 */
	public boolean isDynamic() {
		return isDynamic;
	}

	/**
	 * Returns the data type of this occurrences as EDM type
	 * 
	 * @return the EDM type
	 */
	public EdmType getDatatype() {
		return datatype;
	}

	/**
	 * Modify the internal datatype variable
	 * 
	 * @param datatype
	 *            the new datatype
	 */
	void setDatatype(EdmType datatype) {
		this.datatype = datatype;
	}

	/**
	 * Returns the list of corresponding EDM property identifiers created by contained members
	 * 
	 * @return the list of EDM properties
	 */
	public List<EdmPropertyIdentifier> getEdmPropertyIdentifiers() {
		List<EdmPropertyIdentifier> list = new ArrayList<EdmPropertyIdentifier>();
		/*
		 * there are scopes
		 */
		if (isMultiple()) {
			for (TopicTypeDAO themeDAO : getThemeDAOs()) {
				final String name = getTypeLabel() + themeDAO.getTypeLabel();
				list.add(new EdmPropertyIdentifier(name, getTypeId(), themeDAO.getTypeId()));
			}
			/*
			 * has non scoped instance
			 */
			if (hasNonScopedInstance()) {
				list.add(new EdmPropertyIdentifier(getTypeLabel(), getTypeId(), null));
			}
		}
		/*
		 * there are no scopes
		 */
		else {
			list.add(new EdmPropertyIdentifier(getTypeLabel(), getTypeId(), null));
		}
		return list;
	}

}