package de.topicmapslab.odata.util;

import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.BOOLEAN;
import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.DATE;
import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.DATETIME;
import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.DECIMAL;
import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.DOUBLE;
import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.FLOAT;
import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.INT;
import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.INTEGER;
import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.LONG;
import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.TIME;

import org.odata4j.edm.EdmType;
import org.tmapi.core.Locator;

public class EdmUtils {

	/**
	 * utility method to transform a given XSD locator to a {@link EdmType}
	 * 
	 * @param locator
	 *            the locator
	 * @return the {@link EdmType}
	 */
	public static EdmType xsdToEdm(Locator locator) {
		final String reference = locator.getReference();
		return xsdToEdm(reference);
	}

	/**
	 * utility method to transform a given XSD locator to a {@link EdmType}
	 * 
	 * @param reference
	 *            the reference
	 * @return the {@link EdmType}
	 */
	public static EdmType xsdToEdm(String reference) {
		if (BOOLEAN.equalsIgnoreCase(reference)) {
			return EdmType.BOOLEAN;
		} else if (INT.equalsIgnoreCase(reference)) {
			return EdmType.INT32;
		} else if (INTEGER.equalsIgnoreCase(reference)) {
			return EdmType.INT32;
		} else if (LONG.equalsIgnoreCase(reference)) {
			return EdmType.INT64;
		} else if (DATE.equalsIgnoreCase(reference)) {
			return EdmType.DATETIME;
		} else if (DATETIME.equalsIgnoreCase(reference)) {
			return EdmType.DATETIME;
		} else if (TIME.equalsIgnoreCase(reference)) {
			return EdmType.TIME;
		} else if (DECIMAL.equalsIgnoreCase(reference)) {
			return EdmType.DECIMAL;
		} else if (DOUBLE.equalsIgnoreCase(reference)) {
			return EdmType.DOUBLE;
		} else if (FLOAT.equalsIgnoreCase(reference)) {
			return EdmType.SINGLE;
		}
		return EdmType.STRING;
	}

}
