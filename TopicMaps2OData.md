# Topic Maps 2 OData #

To understand the workflow of the TM2O service, this wiki page provides an overview about the transformation rules.

## Entity Types ##

The first class objects in topic maps are topics. Each topic represents one specific subject of interest. Similar to EDM where entities are instances of an entity type, topics can have one or more types. This analogy is used to define a mapping between topics and entities as core concepts.
In EDM the only attribute of entity types is a unique and human readable name as an identifier. Hence, if a topic type should become an entity type, it is necessary to generate such a name automatically.

## Entity Properties ##

In the TMDM, topics may have names and a set of occurrences, like date of birth or coordinates of a place. This is similar to the entity properties of the EDM and can be mapped in a generic way. The EDM key of a property can be understood as the kind or type of information the value represents. This key is represented as a human readable string literal. It is similar to the concept of name types or occurrence types in the TMDM and can be used similar to topic types .

The TMDM contains the concept of scopes as a set of themes, which represents the validity of a statement, for example the language a name is written in. Because of that, characteristics of the same type but in different scopes may not map to the same EDM property type. Each of these scoping topics should be used to generate more specific property types. The TM2O service uses any themes to generate more specific identifiers for the properties. For example a name which is only valid in english, will be identified by the key _NameEnglish_.

In the TMDM, occurrences have datatypes, which can be interpreted as the property type in EDM. Usually, the datatype system in topic maps is based on XML scheme definition (XSD) datatypes, which in most cases overlap with the EDM type system. Hence, it is straightforward to map XSD datatypes to the EDM type system. In case no corresponding type in the EDM system is available, the datatype Edm.String is used. A name or an occurrence of a specific type may exist zero, one or many times. Because of that, they should be marked as dynamic in the context of OData.

## Navigation Properties ##

Alongside topics, the associations are the second top level concept of the TMDM. They represent relationships between topic instances or topic types. Associations are typed and contain a set of roles, which are also typed and played by one topic instance of the topic map. In contrast to the EDM, associations are not compulsorily binary. But to enable a generic mapping of all associations, the non-binary associations of the topic map are split to a set of binary ones. In general, we developed two possible mapping rules of associations: a flat and a more complex one.

### Flat assoiations ###

The flat associations only use the type of the players and ignore the role types and the association types. In this case the EDM role names are equal to the entity type name as described above. The name of the association is a combination of both role names to represent the relation between both types in a human readable way. The pattern of this name looks like _Name of Left Entity Type-Name of Right Entity Type_.

### Strong associations ###

The strong associations regard any information of the TMDM association, the association type, the player types and the role types. If we use the information of the role types, the EDM role names can be set to the label of the role type, which is also a topic type. The association name is more complicated than the easy mapping of the type, because the EDM does not support the same relationship between different entity types in contrast to the TMDM. Because of that, the names of associations have to combine the label of the association type and the label of the counter-player’s type as in the following pattern: _Name of Association Type-Name of Right Entity Type_.