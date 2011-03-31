package de.topicmapslab.odata.tmql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.topicmapslab.odata.dao.AssociationTypeDAO;
import de.topicmapslab.odata.dao.CounterPartDAO;
import de.topicmapslab.odata.dao.NameTypeDAO;
import de.topicmapslab.odata.dao.OccurrenceTypeDAO;
import de.topicmapslab.odata.dao.TopicTypeDAO;
import de.topicmapslab.tmql4j.components.processor.prepared.IPreparedStatement;
import de.topicmapslab.tmql4j.components.processor.results.model.IResult;
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet;
import de.topicmapslab.tmql4j.components.processor.runtime.ITMQLRuntime;
import de.topicmapslab.tmql4j.components.processor.runtime.TMQLRuntimeFactory;
import de.topicmapslab.tmql4j.path.components.processor.runtime.TmqlRuntime2007;

/**
 * Abstract TMQL helper
 * 
 * @author Sven Krosse
 */
public abstract class TmqlHelper {

	/**
	 * the locker
	 */
	protected static final Lock lock = new ReentrantLock(true);
	/**
	 * the TMQL runtime
	 */
	private final ITMQLRuntime runtime = TMQLRuntimeFactory.newFactory().newRuntime(TmqlRuntime2007.TMQL_2007);

	/**
	 * the prepared statement for {@link ITmqlQueries#TOPICTYPES}
	 */
	private IPreparedStatement stmtTopicTypes;
	/**
	 * the prepared statement for {@link ITmqlQueries#NAMETYPES}
	 */
	private IPreparedStatement stmtNameTypes;
	/**
	 * the prepared statement for {@link ITmqlQueries#OCCURRENCETYPES}
	 */
	private IPreparedStatement stmtOccurrenceTypes;
	/**
	 * the prepared statement for {@link ITmqlQueries#ASSOCIATIONS}
	 */
	private IPreparedStatement stmtAssociations;
	/**
	 * the prepared statement for {@link ITmqlQueries#ASSOCIATION_SIGNATURES}
	 */
	private IPreparedStatement stmtAssociationSignatures;
	/**
	 * the prepared statement for {@link ITmqlQueries#COUNTER_PLAYERS}
	 */
	private IPreparedStatement stmtCounterPlayers;
	/**
	 * the prepared statement for {@link ITmqlQueries#COUNTER_PLAYERS_WITH_ASSOCIATIONTYPE}
	 */
	private IPreparedStatement stmtCounterPlayersWithType;
	/**
	 * the prepared statement for {@link ITmqlQueries#COUNTER_PLAYERS_WITH_SCOPE}
	 */
	private IPreparedStatement stmtCounterPlayersWithScope;
	/**
	 * the prepared statement for {@link ITmqlQueries#COUNTER_PLAYERS_WITH_ASSOCIATIONTYPE_AND_SCOPE}
	 */
	private IPreparedStatement stmtCounterPlayersWithTypeAndScope;
	/**
	 * a map of all stored prepared statements to access information of a entity
	 */
	private Map<String, IPreparedStatement> instanceStatements;
	/**
	 * a map of all stored prepared statements to access information of a whole entity type
	 */
	private Map<String, IPreparedStatement> typeStatements;

	/**
	 * Get all topic types.
	 * 
	 * @return the results as list of DAO
	 */
	public List<TopicTypeDAO> getTopicTypes() {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			/*
			 * lazy load
			 */
			if (stmtTopicTypes == null) {
				stmtTopicTypes = runtime.preparedStatement(ITmqlQueries.TOPICTYPES);
			}
			/*
			 * execute statement
			 */
			IResultSet<?> set = runPreparedStatement(stmtTopicTypes);
			return TopicTypeDAO.getDAOs(set);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Get all names types.
	 * 
	 * @param typeId
	 *            the typeId
	 * @return the results as list of DAO
	 */
	public List<NameTypeDAO> getNameTypes(final String typeId) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			/*
			 * lazy load
			 */
			if (stmtNameTypes == null) {
				stmtNameTypes = runtime.preparedStatement(ITmqlQueries.NAMETYPES);
			}
			/*
			 * execute statement
			 */
			IResultSet<?> set = runPreparedStatement(stmtNameTypes, typeId);
			return NameTypeDAO.getNameTypeDAOs(set);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Get all occurrence types.
	 * 
	 * @param typeId
	 *            the typeId
	 * @return the results as list of DAO
	 */
	public List<OccurrenceTypeDAO> getOccurrenceTypes(final String typeId) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			/*
			 * lazy load
			 */
			if (stmtOccurrenceTypes == null) {
				stmtOccurrenceTypes = runtime.preparedStatement(ITmqlQueries.OCCURRENCETYPES);
			}
			/*
			 * execute
			 */
			IResultSet<?> set = runPreparedStatement(stmtOccurrenceTypes, typeId);
			return OccurrenceTypeDAO.getOccurrenceTypeDAOs(set);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Get all association signatures of the given entity
	 * 
	 * @param entityId
	 *            the entity
	 * @return a list of association signatures represented by their DAO.
	 */
	public List<AssociationTypeDAO> getAssociationSignaturesOfEntityByTmql(final String entityId) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			/*
			 * lazy load
			 */
			if (stmtAssociations == null) {
				stmtAssociations = runtime.preparedStatement(ITmqlQueries.ASSOCIATIONS);
			}
			/*
			 * execute
			 */
			IResultSet<?> set = runPreparedStatement(stmtAssociations, entityId);
			return AssociationTypeDAO.getAssociationDAOs(set);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Get all association signatures.
	 * 
	 * @return the results as list of DAO
	 */
	public List<AssociationTypeDAO> getAssociationSignatures() {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			/*
			 * lazy load
			 */
			if (stmtAssociationSignatures == null) {
				stmtAssociationSignatures = runtime.preparedStatement(ITmqlQueries.ASSOCIATION_SIGNATURES);
			}
			/*
			 * execute
			 */
			IResultSet<?> set = runPreparedStatement(stmtAssociationSignatures);
			return AssociationTypeDAO.getAssociationDAOs(set);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Utility method to convert the results to string array
	 * 
	 * @param resultSet
	 *            the results set
	 * @return the string array
	 */
	protected synchronized Object[][] toArray(IResultSet<?> resultSet) {
		Object results[][] = new Object[resultSet.size()][];
		int i = 0;
		for (IResult r : resultSet) {
			Object[] data = new Object[r.size()];
			int j = 0;
			for (Object o : r) {
				data[j++] = o;
			}
			results[i++] = data;
		}
		return results;
	}

	/**
	 * Register a new TMQL query as prepared statement for the given type id. The query is designed to get all
	 * properties of an instance.
	 * 
	 * @param typeId
	 *            the id of type
	 * @param query
	 *            the query
	 */
	public void registerInstancePreparedStatement(final String typeId, final String query) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			if (instanceStatements == null) {
				instanceStatements = new HashMap<String, IPreparedStatement>();
			}
			instanceStatements.put(typeId, runtime.preparedStatement(query));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Register a new TMQL query as prepared statement for the given type id. The query is designed to get all
	 * properties of all instances
	 * 
	 * @param typeId
	 *            the id of type
	 * @param query
	 *            the query
	 */
	public void registerTypePreparedStatement(final String typeId, final String query) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			if (typeStatements == null) {
				typeStatements = new HashMap<String, IPreparedStatement>();
			}
			typeStatements.put(typeId, runtime.preparedStatement(query));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns all properties of all instances of the given type id
	 * 
	 * @param typeId
	 *            the type id
	 * @return an array containing all property values
	 */
	public Object[][] getProperties(final String typeId) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			if (typeStatements == null || !typeStatements.containsKey(typeId)) {
				throw new RuntimeException("Missing prepared statement for given type id!");
			}
			IPreparedStatement stmt = typeStatements.get(typeId);
			IResultSet<?> set = runPreparedStatement(stmt, typeId);
			return toArray(set);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns all properties of the topic identified by the given id
	 * 
	 * @param typeId
	 *            the type id
	 * @param entityId
	 *            the entity id
	 * @return an array containing all property values
	 */
	public Object[][] getProperties(final String typeId, final String entityId) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			if (instanceStatements == null || !instanceStatements.containsKey(typeId)) {
				throw new RuntimeException("Missing prepared statement for given type id!");
			}
			IPreparedStatement stmt = instanceStatements.get(typeId);
			IResultSet<?> set = runPreparedStatement(stmt, entityId);
			return toArray(set);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Utility method to execute a prepared statement
	 * 
	 * @param stmt
	 *            the statement
	 * @param arguments
	 *            the arguments
	 * @return the results set
	 */
	protected abstract IResultSet<?> runPreparedStatement(final IPreparedStatement stmt, Object... arguments);

	/**
	 * Utility method to execute a query
	 * 
	 * @param query
	 *            the query
	 * @param arguments
	 *            the arguments
	 * @return the results as string array
	 */
	public abstract Object[][] runQuery(final String query, Object... arguments);

	/**
	 * Fetch all counter players of the given entity. If the association type is given, it is used to filter out counter
	 * players of associations with this type. If the theme id is given, it is used to filter out all associations with
	 * the given theme in scope. If the theme is missed, only associations without any scope are recognized. If the
	 * counter player type is given, only topics with this type are recognized as counter player.
	 * 
	 * @param entityId
	 *            the id of entity
	 * @param associationTypeId
	 *            the id of association type
	 * @param counterTypeId
	 *            the id of counter player type
	 * @param themeId
	 *            the id of theme
	 * @return a list of counter player DAOs
	 */
	public List<CounterPartDAO> getCounterPlayers(String entityId, String associationTypeId, String counterTypeId, final String themeId) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			Object[] arguments;
			IPreparedStatement stmt;

			if (associationTypeId == null) {
				/*
				 * without theme
				 */
				if (themeId == null) {
					/*
					 * lazy load
					 */
					if (stmtCounterPlayers == null) {
						stmtCounterPlayers = runtime.preparedStatement(ITmqlQueries.COUNTER_PLAYERS);
					}
					arguments = new Object[] { entityId, counterTypeId };
					stmt = stmtCounterPlayers;
				}
				/*
				 * with theme
				 */
				else {
					/*
					 * lazy load
					 */
					if (stmtCounterPlayersWithScope == null) {
						stmtCounterPlayersWithScope = runtime.preparedStatement(ITmqlQueries.COUNTER_PLAYERS_WITH_SCOPE);
					}
					arguments = new Object[] { entityId, counterTypeId, themeId };
					stmt = stmtCounterPlayersWithScope;
				}
			} else {
				/*
				 * without theme
				 */
				if (themeId == null) {
					/*
					 * lazy load
					 */
					if (stmtCounterPlayersWithType == null) {
						stmtCounterPlayersWithType = runtime.preparedStatement(ITmqlQueries.COUNTER_PLAYERS_WITH_ASSOCIATIONTYPE);
					}
					arguments = new Object[] { entityId, associationTypeId, counterTypeId };
					stmt = stmtCounterPlayersWithType;
				}
				/*
				 * with theme
				 */
				else {
					/*
					 * lazy load
					 */
					if (stmtCounterPlayersWithTypeAndScope == null) {
						stmtCounterPlayersWithTypeAndScope = runtime.preparedStatement(ITmqlQueries.COUNTER_PLAYERS_WITH_ASSOCIATIONTYPE_AND_SCOPE);
					}
					arguments = new Object[] { entityId, associationTypeId, counterTypeId, themeId };
					stmt = stmtCounterPlayersWithTypeAndScope;
				}
			}
			IResultSet<?> set = runPreparedStatement(stmt, arguments);
			return CounterPartDAO.getDAOs(set);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns the internal runtime
	 * 
	 * @return the runtime
	 */
	protected ITMQLRuntime getRuntime() {
		return runtime;
	}
}
