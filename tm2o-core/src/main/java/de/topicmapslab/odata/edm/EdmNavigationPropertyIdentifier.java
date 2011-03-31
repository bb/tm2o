package de.topicmapslab.odata.edm;

import de.topicmapslab.odata.dao.AssociationTypeDAO;

/**
 * Identifier of an EDM navigation property
 * 
 * @author Sven Krosse
 */
public class EdmNavigationPropertyIdentifier extends EdmIdentifier implements IEdmNavigationPropertyIdentifier {

	private String associationTypeId;
	private String counterPlayerTypeId;
	private AssociationTypeDAO dao;

	/**
	 * constructor
	 * 
	 * @param dao
	 *            the DAO containing the information
	 * @param name
	 *            the name of the navigation property
	 * @param associationTypeId
	 *            the id of association type
	 * @param counterPlayerTypeId
	 *            the id of counter player type
	 * @param themeId
	 *            the id of the theme or <code>null</code>
	 */
	public EdmNavigationPropertyIdentifier(final AssociationTypeDAO dao, final String name, final String associationTypeId,
			final String counterPlayerTypeId, final String themeId) {
		super(name, themeId);
		this.associationTypeId = associationTypeId;
		this.dao = dao;
		this.counterPlayerTypeId = counterPlayerTypeId;
	}

	/**
	 * Returns the id of the counter player type
	 * 
	 * @return the id
	 */
	public String getCounterPlayerTypeId() {
		return counterPlayerTypeId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AssociationTypeDAO getDAO() {
		return dao;
	}

	/**
	 * Returns the id of the association type
	 * 
	 * @return the id
	 */
	public String getAssociationTypeId() {
		return associationTypeId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof EdmNavigationPropertyIdentifier) {
			EdmNavigationPropertyIdentifier other = (EdmNavigationPropertyIdentifier) obj;
			boolean r = (getThemeId() == null) ? other.getThemeId() == null : getThemeId().equalsIgnoreCase(other.getThemeId());
			r &= dao.equals(((EdmNavigationPropertyIdentifier) obj).dao);
			return r;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash |= dao.hashCode();
		return hash;
	}

}
