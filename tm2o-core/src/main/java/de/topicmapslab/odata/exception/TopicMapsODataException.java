package de.topicmapslab.odata.exception;

/**
 * base exception thrown by the library
 * 
 * @author Sven Krosse
 * 
 */
public class TopicMapsODataException extends RuntimeException {

	private static final long serialVersionUID = -7780777507871926259L;

	/**
	 * constructor
	 */
	public TopicMapsODataException() {
	}

	/**
	 * constructor
	 * 
	 * @param message
	 *            the message
	 */
	public TopicMapsODataException(String message) {
		super(message);
	}

	/**
	 * constructor
	 * 
	 * @param cause
	 *            the cause
	 */
	public TopicMapsODataException(Throwable cause) {
		super(cause);
	}

	/**
	 * constructor
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public TopicMapsODataException(String message, Throwable cause) {
		super(message, cause);
	}

}
