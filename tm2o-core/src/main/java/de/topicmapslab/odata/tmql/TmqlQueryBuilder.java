package de.topicmapslab.odata.tmql;

import java.util.Map;

import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.odata.edm.EdmPropertyIdentifier;
import de.topicmapslab.odata.edm.IEdmPropertyIdentifier;
import de.topicmapslab.odata.util.ODataUtils;
import de.topicmapslab.tmql4j.grammar.lexical.Wildcard;
import de.topicmapslab.tmql4j.hibernate.builder.FlwrQueryBuilder;
import de.topicmapslab.tmql4j.hibernate.criterion.ICriterion;
import de.topicmapslab.tmql4j.hibernate.exception.InvalidModelException;
import de.topicmapslab.tmql4j.hibernate.path.Navigation;
import de.topicmapslab.tmql4j.hibernate.path.Step;
import de.topicmapslab.tmql4j.hibernate.path.filter.IndexFilter;
import de.topicmapslab.tmql4j.path.grammar.lexical.AxisAtomify;
import de.topicmapslab.tmql4j.path.grammar.lexical.AxisCharacteristics;
import de.topicmapslab.tmql4j.path.grammar.lexical.AxisId;
import de.topicmapslab.tmql4j.path.grammar.lexical.AxisInstances;

/**
 * Utility class to build TMQL queries
 * 
 * @author Sven Krosse
 */
public class TmqlQueryBuilder {

	/**
	 * The variable prefix for characteristic types
	 */
	private static final String VARIABLE = "$ct";

	/**
	 * The topic variable
	 */
	public static final String TOPIC = "$t";

	/**
	 * Internal FLWR query builder for one instance
	 */
	private FlwrQueryBuilder qbInstance;
	/**
	 * Internal FLWR query builder for all instances of the type
	 */
	private FlwrQueryBuilder qbInstances;
	/**
	 * current number of arguments
	 */
	private int numberOfArgs = 0;
	// /**
	// * Map of variables of a selected characteristic type by its id
	// */
	// private Map<String, String> variablesForType = HashUtil.getHashMap();
	/**
	 * Map of variable of a selected characteristic type by its identifier
	 */
	private Map<IEdmPropertyIdentifier, String> variables = HashUtil.getHashMap();

	/**
	 * constructor
	 */
	public TmqlQueryBuilder() {
		qbInstance = new FlwrQueryBuilder();
		/*
		 * create for clause of topic
		 */
		Navigation nav = new Navigation(Wildcard.TOKEN);
		nav.addStep(new Step(AxisId.class, false));
		qbInstance.for_(TOPIC, nav);
		/*
		 * create return clause
		 */
		nav = new Navigation(TOPIC);
		nav.addStep(new Step(AxisId.class, true));
		qbInstance.return_(nav);

		qbInstances = new FlwrQueryBuilder();
		/*
		 * create for clause of topic
		 */
		nav = new Navigation(Wildcard.TOKEN);
		nav.addStep(new Step(AxisId.class, false));
		nav.addStep(new Step(AxisInstances.class, true));
		qbInstances.for_(TOPIC, nav);
		/*
		 * create return clause
		 */
		nav = new Navigation(TOPIC);
		nav.addStep(new Step(AxisId.class, true));
		qbInstances.return_(nav);
	}

	/**
	 * Adding a new return and for part to TMQL query
	 * 
	 * @param identifier
	 *            the identifier of name or occurrence
	 */
	public void fetch(final EdmPropertyIdentifier identifier) {
		/*
		 * add variable for type if not set before
		 */
		String var = null;
		boolean newForClause = false;
		/*
		 * type not already known as for clause component
		 */
		if (!variables.containsKey(identifier)) {
			var = VARIABLE + numberOfArgs;
			// variablesForType.put(identifier.getTypeId(), var);
			variables.put(identifier, var);
			newForClause = true;
		}
		/*
		 * type already known
		 */
		else {
			var = variables.get(identifier);
		}
		fetch(qbInstance, identifier, newForClause);
		fetch(qbInstances, identifier, newForClause);
		/*
		 * increment only if necessary
		 */
		if (newForClause) {
			numberOfArgs++;
		}
	}

	/**
	 * Internal method to add a new return and for part to TMQL query
	 * 
	 * @param qb
	 *            the query builder to add
	 * @param identifier
	 *            identifier of name or occurrence
	 */
	private void fetch(FlwrQueryBuilder qb, final EdmPropertyIdentifier identifier, boolean newForClause) {
		/*
		 * create for clause
		 */
		final String variable = variables.get(identifier);
		if (newForClause) {
			Navigation nav = new Navigation(identifier.getTypeId());
			nav.addStep(new Step(AxisId.class, false));
			qb.for_(variable, nav);
		}
		/*
		 * create return clause
		 */
		Navigation nav = new Navigation(TOPIC);
		Step step = new Step(AxisCharacteristics.class, true, variable);
		if (identifier.getThemeId() != null) {
			step.setFilter(ODataUtils.getScopeFilter(identifier.getThemeId()));
		} else {
			step.setFilter(ODataUtils.getEmptyScopeFilter());
		}
		nav.addStep(step);
		step = new Step(AxisAtomify.class, true);
		step.setFilter(new IndexFilter(0));
		nav.addStep(step);
		qb.return_(nav);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toInstancesQuery() throws InvalidModelException {
		return qbInstances.toQueryString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toInstanceQuery() throws InvalidModelException {
		return qbInstance.toQueryString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TmqlQueryBuilder clone() throws CloneNotSupportedException {
		TmqlQueryBuilder builder = new TmqlQueryBuilder();
		builder.numberOfArgs = numberOfArgs;
		builder.qbInstance = qbInstance.clone();
		builder.qbInstances = qbInstances.clone();
		builder.variables = variables;
		return builder;
	}

	/**
	 * Adding a new order by part
	 * 
	 * @param typeId
	 *            the typeId of property to select
	 * @param ascending
	 *            the ordering order
	 */
	public void orderBy(final IEdmPropertyIdentifier identifier, boolean ascending) {
		String variable = variables.get(identifier);
		if (variable == null) {
			throw new RuntimeException("No variable set for given type identifier '" + identifier.getPropertyName() + "'.");
		}
		Navigation nav = new Navigation(TOPIC);
		nav.addStep(AxisCharacteristics.class, true, variable);
		nav.addStep(AxisAtomify.class, true);
		qbInstances.orderBy(nav, ascending);
		qbInstance.orderBy(nav, ascending);
	}

	/**
	 * Adds an limit clause to embedded query
	 * 
	 * @param limit
	 *            the limit
	 */
	public void limit(long limit) {
		qbInstance.limit(limit);
		qbInstances.limit(limit);
	}

	/**
	 * Adds an offset clause to embedded query
	 * 
	 * @param offset
	 *            the offset
	 */
	public void offset(long offset) {
		qbInstance.offset(offset);
		qbInstances.offset(offset);
	}

	/**
	 * Returns the variable name for the given type identifier
	 * 
	 * @param identifier
	 *            the identifier
	 * @return the variable
	 */
	public String getVariable(IEdmPropertyIdentifier identifier) {
		return variables.get(identifier);
	}

	/**
	 * Adding a criterion as part of the where clause
	 * 
	 * @param criterion
	 *            the criterion
	 */
	public void where(ICriterion criterion) {
		qbInstance.conjunction(criterion);
		qbInstances.conjunction(criterion);
	}

}
