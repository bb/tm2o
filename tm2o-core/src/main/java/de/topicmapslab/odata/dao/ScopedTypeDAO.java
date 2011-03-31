package de.topicmapslab.odata.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DAO containing all information about a scoped construct
 * 
 * @author Sven Krosse
 */
public abstract class ScopedTypeDAO extends TopicTypeDAO {

	private List<TopicTypeDAO> themeDAOs;
	private boolean nonScopedInstance;

	/**
	 * constructor
	 */
	public ScopedTypeDAO() {
		// VOID
	}

	/**
	 * constructor
	 * 
	 * @param typeId
	 *            the topic type ID
	 * @param typeLabel
	 *            the topic type label
	 * @param themeDAOs
	 *            the theme DAOs
	 * @param nonScopedInstance
	 *            flag indicates if there is at least one instance of this DAO without a scope
	 */
	public ScopedTypeDAO(String typeId, String typeLabel, List<TopicTypeDAO> themeDAOs, boolean nonScopedInstance) {
		super(typeId, typeLabel);
		this.themeDAOs = themeDAOs;
		this.nonScopedInstance = nonScopedInstance;
	}

	/**
	 * Internal method to extract theme DAO from given content
	 * 
	 * @param themes
	 *            the themes
	 * @param themeIds
	 *            the theme IDs
	 */
	void setThemes(Object themes, Object themeIds) {
		if (themes instanceof List<?>) {
			List<?> themeList = (List<?>) themes;
			List<?> themeIdList = (List<?>) themeIds;
			this.themeDAOs = new ArrayList<TopicTypeDAO>();
			for (int i = 0; i < themeList.size(); i++) {
				this.themeDAOs.add(new TopicTypeDAO(themeIdList.get(i).toString(), themeList.get(i).toString()));
			}
		}
	}

	/**
	 * Returns the theme DAOs
	 * 
	 * @return the DAOs
	 */
	public List<TopicTypeDAO> getThemeDAOs() {
		if (themeDAOs == null) {
			return Collections.emptyList();
		}
		return themeDAOs;
	}

	/**
	 * Check if the DAO has themes, which results in multiple properties
	 * 
	 * @return <code>true</code> if themes are present, <code>false</code> otherwise
	 */
	public boolean isMultiple() {
		return themeDAOs != null && themeDAOs.size() > 1;
	}

	/**
	 * Checks if there is an instance of this DAO, which has no scope
	 * 
	 * @return <code>true</code> if at least one instance has no scope, <code>false</code> otherwise
	 */
	public boolean hasNonScopedInstance() {
		return nonScopedInstance;
	}

	/**
	 * Setter of internal flag, indicates if there is at least one instance without the scope
	 * 
	 * @param nonScopedInstance
	 *            <code>true</code> if there is at least one instance without the scope otherwise <code>false</code>
	 */
	void hasNonScopedInstance(boolean nonScopedInstance) {
		this.nonScopedInstance = nonScopedInstance;
	}

	/**
	 * Setter of internal flag, indicates if there is at least one instance without the scope
	 * 
	 * @param nonScopedInstance
	 *            the result part containing the information about the minimum number of themes
	 */
	void hasNonScopedInstance(Object nonScopedInstance) {
		if (nonScopedInstance instanceof List<?>) {
			final String first = ((List<?>) nonScopedInstance).get(0).toString();
			this.nonScopedInstance = "0".equalsIgnoreCase(first);
		}
	}
}