package de.topicmapslab.odata.config;

/**
 * Enumeration of configuration properties for the OData mapper
 * 
 * @author Sven Krosse
 */
public enum EContentProviderConfiguration {

	/**
	 * associations are only split by player types. Association type and role type will be ignored. <br />
	 * <br />
	 * The created navigation property looks like: TopyType2: TopicType1->TopicType2
	 */
	FLAT_ASSOCIATION,
	/**
	 * associations are only split by player types, association type and role types. <br />
	 * <br />
	 * The created navigation property looks like: AssociationType-TopicType2: RoleType1->RoleType2
	 */
	STRONG_ASSOCIATION,

}
