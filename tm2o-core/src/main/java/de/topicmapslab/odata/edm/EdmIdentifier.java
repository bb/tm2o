package de.topicmapslab.odata.edm;

/**
 * Abstract base implementation of an {@link IEdmNavigationPropertyIdentifier}
 * 
 * @author Sven Krosse
 */
public abstract class EdmIdentifier implements IEdmPropertyIdentifier {

	private String themeId;
	private String name;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            the property name
	 * @param themeId
	 *            the ID of theme
	 */
	public EdmIdentifier(String name, String themeId) {
		this.name = name;
		this.themeId = themeId;
	}

	/**
	 * Returns the theme ID
	 * 
	 * @return the theme ID
	 */
	public String getThemeId() {
		return themeId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPropertyName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof EdmIdentifier) {
			EdmIdentifier other = (EdmIdentifier) obj;
			boolean r = (themeId == null) ? other.themeId == null : other.themeId == themeId;
			r &= name.equalsIgnoreCase(other.name);
			return r;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = themeId == null ? 0 : themeId.hashCode();
		hash |= name.hashCode();
		return hash;
	}
}
