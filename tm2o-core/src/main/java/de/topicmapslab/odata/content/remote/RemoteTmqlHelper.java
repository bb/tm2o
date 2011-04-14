package de.topicmapslab.odata.content.remote;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import de.topicmapslab.odata.exception.TopicMapsODataException;
import de.topicmapslab.odata.tmql.TmqlHelper;
import de.topicmapslab.tmql4j.components.processor.prepared.IPreparedStatement;
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet;

/**
 * TMQL helper instance using HTTP communication to execute queries on remote server
 * 
 * @author Sven Krosse
 */
public class RemoteTmqlHelper extends TmqlHelper {

	/**
	 * encoding
	 */
	private static final String UTF_8 = "utf-8";
	/**
	 * message of I/O error
	 */
	private static final String IOERROR = "An I/O error occur during communication with the server.";
	/**
	 * the topic map ID of remote server
	 */
	private final String topicMapId;
	/**
	 * the URL of remote server
	 */
	private final String uri;
	/**
	 * the API Key of remote server
	 */
	private final String apiKey;

	/**
	 * URL pattern for POST request
	 */
	private final String PATTERN = "{0}/tm/tmql/{1}";

	/**
	 * constructor
	 * 
	 * @param server
	 *            the server URL
	 * @param apiKey
	 *            the API key
	 * @param topicMapId
	 *            the topic map id
	 */
	public RemoteTmqlHelper(final String server, final String apiKey, final String topicMapId) {
		this.topicMapId = topicMapId;
		this.apiKey = apiKey;
		this.uri = MessageFormat.format(PATTERN, server, topicMapId);
	}

	/**
	 * Returns the topic map id
	 * 
	 * @return the topic map id
	 */
	public String getTopicMapId() {
		return topicMapId;
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
			int i = 0;
			for (Object o : arguments) {
				stmt.set(i++, o);
			}
			return remoteExecute(stmt.getNonParameterizedQueryString());
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
			IPreparedStatement stmt = getRuntime().preparedStatement(query);
			IResultSet<?> set = runPreparedStatement(stmt, arguments);
			return toArray(set);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Internal method to send a request to the server and proceed the response results.
	 * 
	 * @param query
	 *            the query to send
	 * @return the result of querying as result set
	 */
	private IResultSet<?> remoteExecute(final String query) {
		try {
			/*
			 * build POST message
			 */
			final String msg = "apikey=" + apiKey + "&query=" + query;
			/*
			 * open streams
			 */
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			/*
			 * send POST message
			 */
			OutputStream os = connection.getOutputStream();
			os.write(msg.getBytes(UTF_8));
			os.flush();
			BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
			StringBuilder builder = new StringBuilder();
			String line = r.readLine();
			while (line != null) {
				builder.append(line + "\r\n");
				line = r.readLine();
			}
			r.close();
			System.out.println(builder.toString());
			/*
			 * proceed response as extended JTMQR
			 */
			MaJorToMJSONReader reader = new MaJorToMJSONReader(new ByteArrayInputStream(builder.toString().getBytes("utf-8")));
			reader.read();
			IResultSet<?> set = reader.getResultSet();
			/*
			 * check incoming state
			 */
			if (reader.getLastState() != MaJorToMJSONReader.STATE_OK) {
				throw new TopicMapsODataException(reader.getLastMessage());
			}
			return set;
		} catch (IOException e) {
			throw new TopicMapsODataException(IOERROR, e);
		}
	}

}
