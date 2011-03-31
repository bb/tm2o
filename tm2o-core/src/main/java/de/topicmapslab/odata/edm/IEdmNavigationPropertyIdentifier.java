package de.topicmapslab.odata.edm;

import de.topicmapslab.odata.dao.AssociationTypeDAO;

/**
 * Interface definition of an identifier of an EDM navigation property
 * 
 * @author Sven Krosse
 */
public interface IEdmNavigationPropertyIdentifier {

	/**
	 * Returns the corresponding DAO.
	 * 
	 * @return the DAO
	 */
	public AssociationTypeDAO getDAO();

}
