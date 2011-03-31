package de.topicmapslab.odata.content.remote;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonToken;

import de.topicmapslab.tmql4j.components.processor.results.jtmqr.reader.JTMQRReader;
import de.topicmapslab.tmql4j.components.processor.results.jtmqr.reader.result.SimpleJtmqrResultSet;
import de.topicmapslab.tmql4j.components.processor.results.model.IResult;

/**
 * Extension of {@link JTMQRReader} to handle the MaJorToM server JSON response.
 * 
 * @author Sven Krosse
 */
public class ExtendedJTMQRReader extends JTMQRReader {

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
	 * Constructor
	 * 
	 * @param in
	 *            the input stream
	 * @throws JsonParseException
	 *             thrown if JSON document is invalid
	 * @throws IOException
	 *             thrown if an I/O error occur
	 */
	public ExtendedJTMQRReader(InputStream in) throws JsonParseException, IOException {
		super(in);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IResult handleField(SimpleJtmqrResultSet resultSet, IResult result, String text) throws JsonParseException, IOException {
		/*
		 * JSON node of server request state
		 */
		if (text.equalsIgnoreCase("code")) {
			/*
			 * get code
			 */
			getParser().nextToken();
			lastState = Integer.parseInt(getParser().getText());
			return result;
		}
		/*
		 * JSON node of server message
		 */
		else if (text.equalsIgnoreCase("msg")) {
			/*
			 * get message content
			 */
			getParser().nextToken();
			lastMessage = getParser().getText();
			return result;
		}
		/*
		 * JSON node of request meta information
		 */
		else if (text.equalsIgnoreCase("meta")) {
			/*
			 * iterate until end of meta data
			 */
			while (!getParser().getCurrentToken().equals(JsonToken.END_OBJECT) && getParser().nextToken() != null) {
				// VOID
			}
			return result;
		}
		/*
		 * the data node of JSON response
		 */
		else if (text.equalsIgnoreCase("data")) {
			return result;
		}
		/*
		 * normal JTMQR parts will be redirected to base JTMQR reader
		 */
		else {
			return super.handleField(resultSet, result, text);
		}
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
