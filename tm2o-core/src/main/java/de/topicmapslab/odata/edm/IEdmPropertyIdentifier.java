package de.topicmapslab.odata.edm;

/**
 * Base implementation of an identifier of an EDM property
 * 
 * @author Sven Krosse
 */
public interface IEdmPropertyIdentifier {

	/**
	 * Returns the theme ID
	 * 
	 * @return the theme ID
	 */
	public String getThemeId();

	/**
	 * Returns the property name of OData
	 * 
	 * @return the name
	 */
	public String getPropertyName();

}
