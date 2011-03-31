package de.topicmapslab.odata.tmql;

/**
 * Interface containing all pre-defined queries
 * 
 * @author Sven Krosse
 */
public interface ITmqlQueries {

	/**
	 * <b>description:</b> Getting all topic types.<br />
	 * <b>parameters:</b> -<br/>
	 * <b>result:</b><br />
	 * <ul>
	 * <li>id of topic type</li>
	 * <li>best label of the topic type</li>
	 * </ul>
	 * <br />
	 */
	public static final String TOPICTYPES = "FOR $tt IN fn:get-topic-types ( ) RETURN $tt >> id , fn:best-label( $tt )";

	/**
	 * <b>description:</b> Getting all name types of a specific topic type.<br />
	 * <b>parameters:</b> the id of topic type <br />
	 * <b>result:</b> <br />
	 * <ul>
	 * <li>id of name type</li>
	 * <li>best label of the name type</li>
	 * <li>minimum number of occurrence</li>
	 * <li>maximum number of occurrence</li>
	 * <li>a list of theme best label</li>
	 * <li>a list of theme IDs</li>
	 * <li>smallest number of themes</li>
	 * </ul>
	 * <br />
	 * 
	 */
	public static final String NAMETYPES = "FOR $t IN ? << id"
			+ " GROUP BY $0, $1, $2, $3 "
			+ " UNIQUE "
			+ " RETURN {"
			+ " FOR $nt IN fn:get-name-types ( $t ) "
			+ " RETURN $nt >> id , fn:best-label( $nt ), "
			+ " fn:min ( $t >> instances , fn:count ( . >> characteristics $nt )),"
			+ " fn:max ( $t >> instances , fn:count ( . >> characteristics $nt )), "
			+ " fn:best-label ( fn:uniq ( $t >> instances >> characteristics $nt >> scope  )), fn:uniq ( $t >> instances >> characteristics $nt >> scope >> id ),"
			+ " fn:min ( $t >> instances >> characteristics $nt  , fn:count ( . >> scope))}";

	/**
	 * <b>description:</b> Getting all occurrence types of a specific topic type.<br />
	 * <b>parameters:</b> the id of topic type<br />
	 * <b>result:</b><br />
	 * <ul>
	 * <li>id of occurrence type</li>
	 * <li>best label of the occurrence type</li>
	 * <li>the data types</li>
	 * <li>minimum number of occurrence</li>
	 * <li>maximum number of occurrence</li>
	 * <li>a list of theme best label</li>
	 * <li>a list of theme IDs</li>
	 * <li>smallest number of themes</li>
	 * </ul>
	 * <br />
	 */
	public static final String OCCURRENCETYPES = "FOR $t IN ? << id"
			+ " GROUP BY $0, $1, $2, $3, $4 "
			+ " UNIQUE "
			+ " RETURN {"
			+ " FOR $ot IN fn:get-occurrence-types ( $t )"
			+ " RETURN $ot >> id , fn:best-label( $ot ), "
			+ " fn:uniq ( fn:has-datatype ( $ot >> typed )), "
			+ " fn:min ( $t >> instances , fn:count ( . >> characteristics $ot )),"
			+ " fn:max ( $t >> instances , fn:count ( . >> characteristics $ot )) , "
			+ " fn:best-label ( fn:uniq ( $t >> instances >> characteristics $ot >> scope  )), fn:uniq ( $t >> instances >> characteristics $ot >> scope >> id ), "
			+ " fn:min ( $t >> instances >> characteristics $ot  , fn:count ( . >> scope))}";

	/**
	 * <b>description:</b> Getting all association signatures.<br />
	 * <b>parameters:</b> -<br />
	 * <b>result:</b><br />
	 * <ul>
	 * <li>the id of left player type</li>
	 * <li>the best label of left player type</li>
	 * <li>the id of left role type</li>
	 * <li>the best label of left role type</li>
	 * <li>the id of association type</li>
	 * <li>the best label of association type</li>
	 * <li>the id of right role type</li>
	 * <li>the best label of right role type</li>
	 * <li>the id of right player type</li>
	 * <li>the best label of right player type</li>
	 * <li>a list of best labels of all themes</li>
	 * <li>a list of IDs of all themes</li>
	 * <li>the smallest number of themes</li>
	 * </ul>
	 * <br />
	 */
	public static final String ASSOCIATION_SIGNATURES = "For $tt IN fn:get-topic-types ( )"
			+ " UNIQUE"
			+ " GROUP BY $0, $1, $2, $3, $4, $5, $6, $7, $8, $9 "
			+ " RETURN {"
			+ " FOR $o IN $tt >> instances << players"
			+ " RETURN {"
			+ " FOR $rt IN fn:uniq ( $o >> types ) MINUS { http://psi.topicmaps.org/iso13250/model/instance UNION http://psi.topicmaps.org/iso13250/model/type } "
			+ " RETURN {"
			+ " FOR $a IN $o << roles [ fn:count ( . >> roles ) == 2 OR fn:count( . >> roletypes ) == 2 ] "
			+ " RETURN {"
			+ " FOR $or IN $a >> roles [ fn:count( $a >> roles ) == 2 OR . >> types != $rt ]"
			+ " RETURN { FOR $oor IN $or MINUS $o "
			+ " RETURN $tt >> id , fn:best-label( $tt ) , $rt >> id , fn:best-label( $rt) , "
			+ " $a >> types >> id , fn:best-label( $a >> types ),"
			+ " $or >> types >> id , fn:best-label($or >> types), "
			+ " $or >> players >> types [0] >> id , fn:best-label ($or >> players >> types [0]) , fn:best-label( $a >> types >> typed >> scope ) , $a >> types >> typed >> scope >> id, fn:min ( $a >> types >> typed , fn:count ( . >> scope ))"
			+ " } } } } }";
	/**
	 * <b>description:</b> Getting all association signatures of a specific topic type.<br />
	 * <b>parameters:</b> the id of topic type<br />
	 * <b>result:</b> <br />
	 * <ul>
	 * <li>the id of left player type</li>
	 * <li>the best label of left player type</li>
	 * <li>the id of left role type</li>
	 * <li>the best label of left role type</li>
	 * <li>the id of association type</li>
	 * <li>the best label of association type</li>
	 * <li>the id of right role type</li>
	 * <li>the best label of right role type</li>
	 * <li>the id of right player type</li>
	 * <li>the best label of right player type</li>
	 * <li>a list of best labels of all themes</li>
	 * <li>a list of IDs of all themes</li>
	 * <li>the smallest number of themes</li>
	 * </ul>
	 * <br />
	 */
	public static final String ASSOCIATIONS = "FOR $t IN ? << id"
			+ " GROUP BY $0,$1,$2"
			+ " RETURN {"
			+ " FOR $r IN $t << players [ . >> types MINUS { http://psi.topicmaps.org/iso13250/model/instance UNION http://psi.topicmaps.org/iso13250/model/type } ]"
			+ " RETURN {"
			+ " FOR $a IN $r << roles [ fn:count( . >> roles ) == 2 OR fn:count( . >> roles >> types ) > 1 ]"
			+ " RETURN {"
			+ " FOR $or IN $a >> roles [ fn:count( . << roles >> roles ) == 2 OR . >> types MINUS $r >> types ]"
			+ " RETURN {"
			+ " FOR $oor IN $or MINUS $r"
			+ " RETURN $t >> id ,"
			+ " fn:best-label( $r >> types ) , $r >> types >> id , fn:best-label( $r >> types ), "
			+ " $a >> types >> id, fn:best-label( $a >> types ) ,"
			+ "$or >> types >> id , fn:best-label( $or >> types ), "
			+ " $or >> players >> id, "
			+ " fn:best-label ( $or >> players ), fn:best-label( $a >> types >> typed >> scope ) , $a >> types >> typed >> scope >> id, fn:min ( $a >> types >> typed , fn:count ( . >> scope ))"
			+ " } } } }";

	/**
	 * <b>description:</b> Getting all counter players of a topic with recognition of the association type and counter
	 * player type.<br />
	 * <b>parameters:</b> the id of topic type, the id of association type, the id of counter player type<br />
	 * <b>result:</b> the id of the counter player<br />
	 */
	public static final String COUNTER_PLAYERS_WITH_ASSOCIATIONTYPE = "FOR $t IN ? << id " + " FOR $at IN ? << id" + " FOR $ot IN ? << id"
			+ " RETURN fn:uniq ( $t << players << roles $at [ fn:count ( . >> scope ) == 0 ] >> roles >> players $ot >> id MINUS $t >> id )";

	/**
	 * <b>description:</b> Getting all counter players of a topic with recognition of the association type, a theme and
	 * counter player type.<br />
	 * <b>parameters:</b> the id of topic type, the id of association type, the id of counter player type, the theme id<br />
	 * <b>result:</b> the id of the counter player<br />
	 */
	public static final String COUNTER_PLAYERS_WITH_ASSOCIATIONTYPE_AND_SCOPE = "FOR $t IN ? << id " + " FOR $at IN ? << id"
			+ " FOR $ot IN ? << id FOR $theme IN ? << id "
			+ " RETURN fn:uniq (  $t << players << roles $at [ . >> scope == $theme ] >> roles >> players $ot >> id MINUS $t >> id )";

	/**
	 * <b>description:</b> Getting all counter players of a topic with recognition of a theme and counter player type.<br />
	 * <b>parameters:</b> the id of topic type, the id of counter player type, the theme id<br />
	 * <b>result:</b> the id of the counter player<br />
	 */
	public static final String COUNTER_PLAYERS_WITH_SCOPE = "FOR $t IN ? << id " + " FOR $ot IN ? << id FOR $theme IN ? << id "
			+ " RETURN fn:uniq ( $t << players << roles [ . >> scope == $theme ] >> roles >> players $ot >> id MINUS $t >> id )";

	/**
	 * <b>description:</b> Getting all counter players of a topic with recognition of the counter player type.<br />
	 * <b>parameters:</b> the id of topic type, the id of counter player type<br />
	 * <b>result:</b> the id of the counter player<br />
	 */
	public static final String COUNTER_PLAYERS = "FOR $t IN ? << id " + " FOR $ot IN ? << id"
			+ " RETURN fn:uniq ( $t << players << roles [ fn:count ( . >> scope ) == 0 ] >> roles >> players $ot >> id MINUS $t >> id )";
}
