package de.topicmapslab.odata.content.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import de.topicmapslab.tmql4j.components.processor.results.jtmqr.reader.JTMQRReader;
import de.topicmapslab.tmql4j.components.processor.results.model.IResultSet;

/**
 * JSON reader for MaJorToM server
 * 
 * @author Sven Krosse
 */
public class MaJorToMJSONReader {

	/**
	 * State returns by MaJorToM server if request was valid
	 */
	public static final int STATE_OK = 0;
	/**
	 * State returns by MaJorToM server if request was invalid
	 */
	public static final int STATE_ERROR = 1;
	/**
	 * State set before first request
	 */
	public static final int STATE_INVALID = -1;

	/**
	 * the last message received from server
	 */
	private String lastMessage;
	/**
	 * the last state received from server
	 */
	private int lastState = STATE_INVALID;
	/**
	 * the result set
	 */
	private IResultSet<?> resultSet;

	/**
	 * the input stream used to read the incoming JSON
	 */
	private InputStream stream;

	/**
	 * constructor
	 * 
	 * @param stream
	 *            the stream
	 */
	public MaJorToMJSONReader(InputStream stream) {
		this.stream = stream;
	}

	/**
	 * Reads the result of MaJorToM server
	 * 
	 * @throws JsonParseException
	 *             thrown if JSON cannot be parsed
	 * @throws JsonMappingException
	 *             thrown if JSON cannot be created
	 * @throws IOException
	 *             thrown if any I/O error occur
	 */
	public void read() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper m = new ObjectMapper();
		JsonNode rootNode = m.readValue(stream, JsonNode.class);
		JsonNode code = rootNode.path("code");
		try {
			lastState = Integer.parseInt(code.getValueAsText());
		} catch (NumberFormatException e) {
			lastState = -1;
		}
		JsonNode msg = rootNode.path("msg");
		lastMessage = msg.getTextValue();
		if (lastState == 0) {
			JsonNode data = rootNode.path("data");
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			JsonFactory factory = new JsonFactory();
			JsonGenerator gen = factory.createJsonGenerator(os, JsonEncoding.UTF8);
			gen.setCodec(m);
			gen.writeTree(data);
			resultSet = new JTMQRReader(new ByteArrayInputStream(os.toByteArray())).readResultSet();

		}
	}

	/**
	 * Returns the result set
	 * 
	 * @return the result set
	 */
	public IResultSet<?> getResultSet() {
		return resultSet;
	}

	/**
	 * Returns the last message received from server. If the message is called before the first request, the return
	 * value is <code>null</code>.
	 * 
	 * @return the message or <code>null</code>
	 */
	public String getLastMessage() {
		return lastMessage;
	}

	/**
	 * Returns the last state receiver from server. If the message is called before the first request, the return value
	 * is {@link #STATE_INVALID}.
	 * 
	 * @return {@link #STATE_OK}({@value #STATE_OK}), {@link #STATE_ERROR}({@value #STATE_ERROR}) or
	 *         {@link #STATE_INVALID}({@value #STATE_INVALID})
	 */
	public int getLastState() {
		return lastState;
	}

}
