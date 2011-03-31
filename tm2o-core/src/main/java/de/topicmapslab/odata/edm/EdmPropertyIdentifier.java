package de.topicmapslab.odata.edm;

/**
 * Bean class storing the information of EDM property
 * 
 * @author Sven Krosse
 */
public class EdmPropertyIdentifier extends EdmIdentifier {

	private String typeId;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            the property name
	 * @param typeId
	 *            the ID of characteristics type
	 * @param themeId
	 *            the ID of theme
	 */
	public EdmPropertyIdentifier(final String name, String typeId, String themeId) {
		super(name, themeId);
		this.typeId = typeId;
	}

	/**
	 * Returns the ID of type
	 * 
	 * @return the type
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof EdmPropertyIdentifier) {
			EdmPropertyIdentifier other = (EdmPropertyIdentifier) obj;
			boolean r = other.typeId == typeId;
			r &= super.equals(obj);
			return r;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash |= typeId.hashCode();
		return hash;
	}
}
