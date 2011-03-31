package de.topicmapslab.odata.content.memory;

import org.tmapi.core.TopicMap;

import de.topicmapslab.odata.tmql.TmqlHelper;
import de.topicmapslab.tmql4j.components.processor.prepared.IPreparedStatement;
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet;

/**
 * TMQL helper instance using memory calls to execute queries on remote server
 * 
 * @author Sven Krosse
 */
public class MemoryTmqlHelper extends TmqlHelper {

	/**
	 * the topic map
	 */
	private final TopicMap topicMap;

	/**
	 * constructor
	 * 
	 * @param topicMap
	 *            the topic map
	 */
	public MemoryTmqlHelper(TopicMap topicMap) {
		this.topicMap = topicMap;
	}

	/**
	 * Returns the topic map
	 * 
	 * @return the topic map
	 */
	public TopicMap getTopicMap() {
		return topicMap;
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
	@Override
	protected IResultSet<?> runPreparedStatement(final IPreparedStatement stmt, Object... arguments) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			stmt.setTopicMap(topicMap);
			stmt.run(arguments);
			IResultSet<?> rs = stmt.getResults();
			stmt.setTopicMap(null);
			return rs;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Utility method to execute a query
	 * 
	 * @param query
	 *            the query
	 * @param arguments
	 *            the arguments
	 * @return the results as string array
	 */
	@Override
	public Object[][] runQuery(final String query, Object... arguments) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			return toArray(getRuntime().run(topicMap, query, arguments).getResults());
		} finally {
			lock.unlock();
		}
	}
}
