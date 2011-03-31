package de.topicmapslab.odata.dao;

import java.util.ArrayList;
import java.util.List;

import de.topicmapslab.odata.edm.EdmNavigationPropertyIdentifier;
import de.topicmapslab.odata.exception.TopicMapsODataException;
import de.topicmapslab.tmql4j.components.processor.results.model.IResult;
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet;

/**
 * DAO containing all information about an association signature
 * 
 * @author Sven Krosse
 */
public class AssociationTypeDAO extends ScopedTypeDAO {

	private TopicTypeDAO leftPlayerTypeDAO;
	private TopicTypeDAO leftRoleTypeDAO;
	private TopicTypeDAO rightRoleTypeDAO;
	private TopicTypeDAO rightPlayerTypeDAO;

	/**
	 * constructor
	 */
	public AssociationTypeDAO() {
		// VOID
	}

	/**
	 * constructor
	 * 
	 * @param typeId
	 *            the ID of association type
	 * @param typeLabel
	 *            the label of type
	 * @param themeDAOs
	 *            the themes
	 * @param nonScopedInstance
	 *            flag indicates if there is at least one instance of this DAO without a scope
	 * @param leftPlayerTypeDAO
	 *            the DAO of left hand player type
	 * @param leftRoleTypeDAO
	 *            the DAO of left hand role type
	 * @param rightRoleTypeDAO
	 *            the DAO of right role type
	 * @param rightPlayerTypeDAO
	 *            the DAO of right player type
	 */
	public AssociationTypeDAO(String typeId, String typeLabel, List<TopicTypeDAO> themeDAOs, boolean nonScopedInstance,
			TopicTypeDAO leftPlayerTypeDAO, TopicTypeDAO leftRoleTypeDAO, TopicTypeDAO rightRoleTypeDAO, TopicTypeDAO rightPlayerTypeDAO) {
		super(typeId, typeLabel, themeDAOs, nonScopedInstance);
		this.leftPlayerTypeDAO = leftPlayerTypeDAO;
		this.leftRoleTypeDAO = leftRoleTypeDAO;
		this.rightRoleTypeDAO = rightRoleTypeDAO;
		this.rightPlayerTypeDAO = rightPlayerTypeDAO;
	}

	/**
	 * the DAO of left hand player type
	 */
	public TopicTypeDAO getLeftPlayerTypeDAO() {
		return leftPlayerTypeDAO;
	}

	/**
	 * the DAO of association type
	 */
	public TopicTypeDAO getLeftRoleTypeDAO() {
		return leftRoleTypeDAO;
	}

	/**
	 * the DAO of right player type
	 */
	public TopicTypeDAO getRightPlayerTypeDAO() {
		return rightPlayerTypeDAO;
	}

	/**
	 * the DAO of right role type
	 */
	public TopicTypeDAO getRightRoleTypeDAO() {
		return rightRoleTypeDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IResult result) throws TopicMapsODataException {
		if (result == null || result.size() < 10) {
			throw new TopicMapsODataException("The number of arguments is invalid. DAO expects 7 arguments.");
		}

		Object leftPlayerTypeDAOId = result.get(0);
		Object leftPlayerTypeDAOLabel = result.get(1);
		leftPlayerTypeDAO = new TopicTypeDAO(leftPlayerTypeDAOId.toString(), leftPlayerTypeDAOLabel.toString());

		Object leftRoleTypeDAOId = result.get(2);
		Object leftRoleTypeDAOLabel = result.get(3);
		leftRoleTypeDAO = new TopicTypeDAO(leftRoleTypeDAOId.toString(), leftRoleTypeDAOLabel.toString());

		setTypeId(result.get(4).toString());
		setTypeLabel(result.get(5).toString());

		Object rightRoleTypeDAOId = result.get(6);
		Object rightRoleTypeDAOLabel = result.get(7);
		rightRoleTypeDAO = new TopicTypeDAO(rightRoleTypeDAOId.toString(), rightRoleTypeDAOLabel.toString());

		Object rightPlayerTypeDAOId = result.get(8);
		Object rightPlayerTypeDAOLabel = result.get(9);
		if (rightPlayerTypeDAOId != null) {
			rightPlayerTypeDAO = new TopicTypeDAO(rightPlayerTypeDAOId.toString(), rightPlayerTypeDAOLabel.toString());
		}
		setThemes(result.get(10), result.get(11));
		hasNonScopedInstance(result.get(12));
	}

	/**
	 * Returns the list of EDM identifiers for flat relationships representing an entry of this DAO
	 * 
	 * @return a list of relationship identifiers
	 */
	public List<EdmNavigationPropertyIdentifier> getFlatRelationshipIdentifier() {
		if (getRightPlayerTypeDAO() == null) {
			throw new RuntimeException("Counter part is null!");
		}
		List<EdmNavigationPropertyIdentifier> list = new ArrayList<EdmNavigationPropertyIdentifier>();
		/*
		 * has themes?
		 */
		if (isMultiple()) {
			for (TopicTypeDAO themeDAO : getThemeDAOs()) {
				/*
				 * calculate name of property as combination of player type labels and theme label
				 */
				final String name = getLeftPlayerTypeDAO().getTypeLabel() + getRightPlayerTypeDAO().getTypeLabel() + themeDAO.getTypeLabel();
				list.add(new EdmNavigationPropertyIdentifier(this, name, null, getRightPlayerTypeDAO().getTypeId(), themeDAO.getTypeId()));
			}
			/*
			 * add non scoped part
			 */
			if (hasNonScopedInstance()) {
				final String name = getLeftPlayerTypeDAO().getTypeLabel() + getRightPlayerTypeDAO().getTypeLabel();
				list.add(new EdmNavigationPropertyIdentifier(this, name, null, getRightPlayerTypeDAO().getTypeId(), null));
			}
		}
		/*
		 * there are no themes
		 */
		else {
			/*
			 * calculate name of property as combination of player type labels
			 */
			final String name = getLeftPlayerTypeDAO().getTypeLabel() + getRightPlayerTypeDAO().getTypeLabel();
			list.add(new EdmNavigationPropertyIdentifier(this, name, null, getRightPlayerTypeDAO().getTypeId(), null));
		}
		return list;
	}

	/**
	 * Returns the list of EDM identifiers for strong relationships representing an entry of this DAO
	 * 
	 * @return a list of relationship identifiers
	 */
	public List<EdmNavigationPropertyIdentifier> getStrongRelationshipIdentifier() {
		if (getRightPlayerTypeDAO() == null || getRightRoleTypeDAO() == null) {
			throw new RuntimeException("Counter part is null!");
		}
		List<EdmNavigationPropertyIdentifier> list = new ArrayList<EdmNavigationPropertyIdentifier>();
		/*
		 * has themes?
		 */
		if (isMultiple()) {
			for (TopicTypeDAO themeDAO : getThemeDAOs()) {
				/*
				 * calculate name of property as combination of association and counter player type labels and theme
				 * label
				 */
				final String name = getTypeLabel() + getRightPlayerTypeDAO().getTypeLabel() + themeDAO.getTypeLabel();
				list.add(new EdmNavigationPropertyIdentifier(this, name, getTypeId(), getRightPlayerTypeDAO().getTypeId(), themeDAO.getTypeId()));
			}
			/*
			 * add non scoped part
			 */
			if (hasNonScopedInstance()) {
				final String name = getTypeLabel() + getRightPlayerTypeDAO().getTypeLabel();
				list.add(new EdmNavigationPropertyIdentifier(this, name, getTypeId(), getRightPlayerTypeDAO().getTypeId(), null));
			}
		}
		/*
		 * there are no themes
		 */
		else {
			/*
			 * calculate name of property as combination of association and counter player type labels
			 */
			final String name = getTypeLabel() + getRightPlayerTypeDAO().getTypeLabel();
			list.add(new EdmNavigationPropertyIdentifier(this, name, getTypeId(), getRightPlayerTypeDAO().getTypeId(), null));
		}
		return list;
	}

	/**
	 * Method extracts the association type DAOs form the given result set
	 * 
	 * @param resultSet
	 *            the result set
	 * @return the list of DAOs
	 * @throws TopicMapsODataException
	 *             throws if conversation failed
	 */
	public static List<AssociationTypeDAO> getAssociationDAOs(IResultSet<?> resultSet) throws TopicMapsODataException {
		List<AssociationTypeDAO> daos = new ArrayList<AssociationTypeDAO>();
		for (IResult r : resultSet) {
			AssociationTypeDAO dao = new AssociationTypeDAO();
			dao.load(r);
			daos.add(dao);
		}
		return daos;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AssociationTypeDAO) {
			AssociationTypeDAO other = (AssociationTypeDAO) obj;
			/*
			 * same themes and same type
			 */
			if (!super.equals(other)) {
				return false;
			}
			/*
			 * left role types are equal
			 */
			if (getLeftRoleTypeDAO().equals(other.getLeftRoleTypeDAO())) {
				/*
				 * left player type, right role type and right role player type should be equal
				 */
				boolean r = getLeftPlayerTypeDAO().equals(other.getLeftPlayerTypeDAO());
				r &= getRightRoleTypeDAO().equals(other.getRightRoleTypeDAO());
				r &= getRightPlayerTypeDAO() == null ? other.getRightPlayerTypeDAO() == null : getRightPlayerTypeDAO().equals(
						other.getRightPlayerTypeDAO());
				return r;
			}
			/*
			 * left role type equals others right role type
			 */
			else if (getLeftRoleTypeDAO().equals(other.getRightRoleTypeDAO())) {
				/*
				 * left player type = right player type, right role type = left role type and right role player type =
				 * left role player type
				 */
				boolean r = getLeftPlayerTypeDAO().equals(other.getRightPlayerTypeDAO());
				r &= getRightRoleTypeDAO().equals(other.getLeftRoleTypeDAO());
				r &= getRightPlayerTypeDAO() == null ? other.getLeftPlayerTypeDAO() == null : getRightPlayerTypeDAO().equals(
						other.getLeftPlayerTypeDAO());
				return r;
			}
		}
		return false;
	}

	/**
	 * Return the topic type DAO of the counter player type
	 * 
	 * @param typeLabel
	 *            the label of the current topic type
	 * @return the counter type DAO
	 */
	public TopicTypeDAO getCounterPlayerDAO(final String typeLabel) {
		/*
		 * incoming player type is left player
		 */
		if (getLeftPlayerTypeDAO().getTypeLabel().equalsIgnoreCase(typeLabel)) {
			return getRightPlayerTypeDAO();
		}
		/*
		 * incoming player type is right player
		 */
		else {
			return getLeftPlayerTypeDAO();
		}
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash |= getLeftPlayerTypeDAO().hashCode();
		hash |= getLeftRoleTypeDAO().hashCode();
		hash |= getRightRoleTypeDAO().hashCode();
		hash |= getRightPlayerTypeDAO() == null ? 0 : getRightPlayerTypeDAO().hashCode();
		return hash;
	}

}
