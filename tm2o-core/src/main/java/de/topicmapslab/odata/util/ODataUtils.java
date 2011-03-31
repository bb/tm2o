package de.topicmapslab.odata.util;

import org.odata4j.expression.AddExpression;
import org.odata4j.expression.AndExpression;
import org.odata4j.expression.BinaryCommonExpression;
import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.expression.CommonExpression;
import org.odata4j.expression.DivExpression;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.expression.EqExpression;
import org.odata4j.expression.Expression;
import org.odata4j.expression.GeExpression;
import org.odata4j.expression.GtExpression;
import org.odata4j.expression.LeExpression;
import org.odata4j.expression.LiteralExpression;
import org.odata4j.expression.LtExpression;
import org.odata4j.expression.MethodCallExpression;
import org.odata4j.expression.ModExpression;
import org.odata4j.expression.MulExpression;
import org.odata4j.expression.NeExpression;
import org.odata4j.expression.NotExpression;
import org.odata4j.expression.OrExpression;
import org.odata4j.expression.SubExpression;

import de.topicmapslab.odata.content.IOdataContentProvider;
import de.topicmapslab.odata.edm.EdmPropertyIdentifier;
import de.topicmapslab.odata.edm.IEdmPropertyIdentifier;
import de.topicmapslab.odata.exception.TopicMapsODataException;
import de.topicmapslab.odata.tmql.TmqlQueryBuilder;
import de.topicmapslab.tmql4j.hibernate.IQueryPart;
import de.topicmapslab.tmql4j.hibernate.core.NonInterpreted;
import de.topicmapslab.tmql4j.hibernate.criterion.Addition;
import de.topicmapslab.tmql4j.hibernate.criterion.Conjunction;
import de.topicmapslab.tmql4j.hibernate.criterion.Criterion;
import de.topicmapslab.tmql4j.hibernate.criterion.Disjunction;
import de.topicmapslab.tmql4j.hibernate.criterion.Division;
import de.topicmapslab.tmql4j.hibernate.criterion.Equals;
import de.topicmapslab.tmql4j.hibernate.criterion.Exists;
import de.topicmapslab.tmql4j.hibernate.criterion.GreaterEquals;
import de.topicmapslab.tmql4j.hibernate.criterion.GreaterThan;
import de.topicmapslab.tmql4j.hibernate.criterion.ICriteria;
import de.topicmapslab.tmql4j.hibernate.criterion.ICriterion;
import de.topicmapslab.tmql4j.hibernate.criterion.LowerEquals;
import de.topicmapslab.tmql4j.hibernate.criterion.LowerThan;
import de.topicmapslab.tmql4j.hibernate.criterion.Modulo;
import de.topicmapslab.tmql4j.hibernate.criterion.Multiplication;
import de.topicmapslab.tmql4j.hibernate.criterion.Negation;
import de.topicmapslab.tmql4j.hibernate.criterion.Subtraction;
import de.topicmapslab.tmql4j.hibernate.criterion.UnEquals;
import de.topicmapslab.tmql4j.hibernate.path.Navigation;
import de.topicmapslab.tmql4j.hibernate.path.Step;
import de.topicmapslab.tmql4j.hibernate.path.filter.Filter;
import de.topicmapslab.tmql4j.hibernate.path.function.CountFunction;
import de.topicmapslab.tmql4j.path.grammar.lexical.AxisAtomify;
import de.topicmapslab.tmql4j.path.grammar.lexical.AxisCharacteristics;
import de.topicmapslab.tmql4j.path.grammar.lexical.AxisId;
import de.topicmapslab.tmql4j.path.grammar.lexical.AxisScope;
import de.topicmapslab.tmql4j.path.grammar.lexical.Dot;
import de.topicmapslab.tmql4j.util.LiteralUtils;

public class ODataUtils {

	/**
	 * Transforms the given OData boolean expression to a TMQL-Hibernate {@link ICriterion}.
	 * 
	 * @param contentProvider
	 *            the calling content provider
	 * @param queryBuilder
	 *            the query builder
	 * @param booleanExpression
	 *            the boolean expression
	 * @return the {@link ICriterion}
	 * @throws TopicMapsODataException
	 *             thrown if transformation is not possible
	 */
	public static final ICriterion transformToTmql(IOdataContentProvider contentProvider, TmqlQueryBuilder queryBuilder,
			BoolCommonExpression booleanExpression) {
		if (booleanExpression instanceof BinaryCommonExpression) {
			return transformToTmql(contentProvider, queryBuilder, (BinaryCommonExpression) booleanExpression);
		} else if (booleanExpression instanceof AndExpression) {
			AndExpression and = (AndExpression) booleanExpression;
			Conjunction conjunction = new Conjunction();
			conjunction.add(transformToTmql(contentProvider, queryBuilder, and.getLHS()));
			conjunction.add(transformToTmql(contentProvider, queryBuilder, and.getRHS()));
			return conjunction;
		} else if (booleanExpression instanceof OrExpression) {
			OrExpression or = (OrExpression) booleanExpression;
			Disjunction disjunction = new Disjunction();
			disjunction.add(transformToTmql(contentProvider, queryBuilder, or.getLHS()));
			disjunction.add(transformToTmql(contentProvider, queryBuilder, or.getRHS()));
			return disjunction;
		} else if (booleanExpression instanceof NotExpression) {
			NotExpression not = (NotExpression) booleanExpression;
			Negation negation = new Negation();
			IQueryPart part = transformToTmql(contentProvider, queryBuilder, not.getExpression());
			negation.add(part instanceof ICriterion ? (ICriterion) part : new Exists(part));
			return negation;
		}
		throw new UnsupportedOperationException("Current content provider does not support given expression type '"
				+ booleanExpression.getClass().getSimpleName() + "'!");
	}

	public static final IQueryPart transformToTmql(IOdataContentProvider contentProvider, TmqlQueryBuilder queryBuilder, CommonExpression expression) {
		/*
		 * is simple member access
		 */
		if (expression instanceof EntitySimpleProperty) {
			final IEdmPropertyIdentifier identifier = contentProvider.getPropertyType((((EntitySimpleProperty) expression).getPropertyName()));
			if (!(identifier instanceof EdmPropertyIdentifier)) {
				throw new RuntimeException("Invalid identifier type of property");
			}
			final String variableName = queryBuilder.getVariable(identifier);
			Navigation navigation = new Navigation(variableName);
			Step step = new Step(AxisAtomify.class, true);
			/*
			 * add theme notification
			 */
			if (identifier.getThemeId() != null) {
				step.setFilter(ODataUtils.getScopeFilter(identifier.getThemeId()));
			}
			navigation.addStep(step);
			return navigation;
		}
		/*
		 * is method access
		 */
		if (expression instanceof MethodCallExpression) {
			// MethodCallExpression me = (MethodCallExpression) expression;
			throw new UnsupportedOperationException("Current content provider does not support given expression type '"
					+ expression.getClass().getSimpleName() + "'!");
		}
		/*
		 * is literal expression
		 */
		if (expression instanceof LiteralExpression) {
			return new NonInterpreted(getLiteral((LiteralExpression) expression));
		}
		/*
		 * is boolean expression
		 */
		if (expression instanceof BoolCommonExpression) {
			return transformToTmql(contentProvider, queryBuilder, (BoolCommonExpression) expression);
		}
		throw new UnsupportedOperationException("Current content provider does not support given expression type '"
				+ expression.getClass().getSimpleName() + "'!");
	}

	/**
	 * Transforms the given binary expression to its corresponding TMQL numerical or comparison criteria
	 * 
	 * @param contentProvider
	 *            the content provider
	 * @param queryBuilder
	 *            the query builder
	 * @param expression
	 *            the expression
	 * @return the criteria
	 */
	public static final ICriterion transformToTmql(IOdataContentProvider contentProvider, TmqlQueryBuilder queryBuilder,
			BinaryCommonExpression expression) {
		ICriteria criteria;
		/*
		 * left hand side only supports entity name
		 */
		if (!(expression.getLHS() instanceof EntitySimpleProperty)) {
			throw new UnsupportedOperationException("Current content provider only supports simple property expressions");
		}
		EntitySimpleProperty lhs = (EntitySimpleProperty) expression.getLHS();
		/*
		 * transform left hand
		 */
		final IEdmPropertyIdentifier identifier = contentProvider.getPropertyType(lhs.getPropertyName());
		final String variableName = queryBuilder.getVariable(identifier);
		Navigation navigation = new Navigation(TmqlQueryBuilder.TOPIC);
		Step c = new Step(AxisCharacteristics.class, true, variableName);
		if (identifier.getThemeId() != null) {
			c.setFilter(ODataUtils.getScopeFilter(identifier.getThemeId()));
		}
		navigation.addStep(c);

		Step step = new Step(AxisAtomify.class, true);
		/*
		 * add theme notification
		 */
		if (identifier.getThemeId() != null) {
			step.setFilter(ODataUtils.getScopeFilter(identifier.getThemeId()));
		}
		navigation.addStep(step);
		/*
		 * right hand side only supports literals
		 */
		if (!(expression.getRHS() instanceof LiteralExpression)) {
			throw new UnsupportedOperationException("Current content provider only supports simple property expressions");
		}
		LiteralExpression rhs = (LiteralExpression) expression.getRHS();
		/*
		 * create criteria
		 */
		if (expression instanceof EqExpression) {
			criteria = new Equals();
		} else if (expression instanceof NeExpression) {
			criteria = new UnEquals();
		} else if (expression instanceof GtExpression) {
			criteria = new GreaterThan();
		} else if (expression instanceof GeExpression) {
			criteria = new GreaterEquals();
		} else if (expression instanceof LtExpression) {
			criteria = new LowerThan();
		} else if (expression instanceof LeExpression) {
			criteria = new LowerEquals();
		} else if (expression instanceof AddExpression) {
			criteria = new Addition();
		} else if (expression instanceof SubExpression) {
			criteria = new Subtraction();
		} else if (expression instanceof DivExpression) {
			criteria = new Division();
		} else if (expression instanceof MulExpression) {
			criteria = new Multiplication();
		} else if (expression instanceof ModExpression) {
			criteria = new Modulo();
		}
		/*
		 * unknown type
		 */
		else {
			throw new TopicMapsODataException("Currently the given binary expression '" + expression.getClass().getSimpleName() + "' is not support!");
		}
		/*
		 * add left and right hand
		 */
		criteria.add(new Exists(navigation));
		criteria.add(new Criterion(getLiteral(rhs)));
		return criteria;
	}

	/**
	 * Utility method transform the given value to a TMQL literal
	 * 
	 * @param expression
	 *            the expression
	 * @return the literal
	 */
	public static String getLiteral(LiteralExpression expression) {
		Object value = Expression.literalValue(expression);
		if (value instanceof String) {
			return "\"\"\"" + LiteralUtils.asString(value) + "\"\"\"";
		}
		return value.toString();
	}

	/**
	 * Utility method to create a scope filter
	 * 
	 * @param themeId
	 *            the theme Id
	 * @return the scope filter
	 */
	public static Filter getScopeFilter(final String themeId) {
		/*
		 * create . >> scope
		 */
		Navigation left = new Navigation(Dot.TOKEN);
		left.addStep(new Step(AxisScope.class, true));
		left.addStep(AxisId.class, true);
		/*
		 * create "id" << id
		 */
		Navigation right = new Navigation("\"" + themeId + "\"");
		// right.addStep(new Step(AxisId.class, false));
		/*
		 * create equality as filter
		 */
		Equals eq = new Equals();
		eq.add(new Exists(left));
		eq.add(new Exists(right));
		return new Filter(eq);
	}

	/**
	 * Utility method to create an empty scope filter
	 * 
	 * @return the scope filter
	 */
	public static Filter getEmptyScopeFilter() {
		/*
		 * create . >> scope
		 */
		Navigation left = new Navigation(Dot.TOKEN);
		left.addStep(new Step(AxisScope.class, true));
		CountFunction fct = new CountFunction(left);
		Equals eq = new Equals();
		eq.add(new Exists(fct));
		eq.add(new Exists(new NonInterpreted("0")));
		return new Filter(eq);
	}
}
