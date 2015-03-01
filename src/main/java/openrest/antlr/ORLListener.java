// Generated from ORL.g4 by ANTLR 4.2.2
package openrest.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ORLParser}.
 */
public interface ORLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ORLParser#logicalExpressionAtom}.
	 * @param ctx the parse tree
	 */
	void enterLogicalExpressionAtom(@NotNull ORLParser.LogicalExpressionAtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link ORLParser#logicalExpressionAtom}.
	 * @param ctx the parse tree
	 */
	void exitLogicalExpressionAtom(@NotNull ORLParser.LogicalExpressionAtomContext ctx);

	/**
	 * Enter a parse tree produced by {@link ORLParser#logicalExpressionOr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalExpressionOr(@NotNull ORLParser.LogicalExpressionOrContext ctx);
	/**
	 * Exit a parse tree produced by {@link ORLParser#logicalExpressionOr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalExpressionOr(@NotNull ORLParser.LogicalExpressionOrContext ctx);

	/**
	 * Enter a parse tree produced by {@link ORLParser#parameterTextValue}.
	 * @param ctx the parse tree
	 */
	void enterParameterTextValue(@NotNull ORLParser.ParameterTextValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ORLParser#parameterTextValue}.
	 * @param ctx the parse tree
	 */
	void exitParameterTextValue(@NotNull ORLParser.ParameterTextValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link ORLParser#functionName}.
	 * @param ctx the parse tree
	 */
	void enterFunctionName(@NotNull ORLParser.FunctionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ORLParser#functionName}.
	 * @param ctx the parse tree
	 */
	void exitFunctionName(@NotNull ORLParser.FunctionNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link ORLParser#propertyName}.
	 * @param ctx the parse tree
	 */
	void enterPropertyName(@NotNull ORLParser.PropertyNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ORLParser#propertyName}.
	 * @param ctx the parse tree
	 */
	void exitPropertyName(@NotNull ORLParser.PropertyNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link ORLParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(@NotNull ORLParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ORLParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(@NotNull ORLParser.FunctionContext ctx);

	/**
	 * Enter a parse tree produced by {@link ORLParser#parameterNumberValue}.
	 * @param ctx the parse tree
	 */
	void enterParameterNumberValue(@NotNull ORLParser.ParameterNumberValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ORLParser#parameterNumberValue}.
	 * @param ctx the parse tree
	 */
	void exitParameterNumberValue(@NotNull ORLParser.ParameterNumberValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link ORLParser#logicalExpressionAnd}.
	 * @param ctx the parse tree
	 */
	void enterLogicalExpressionAnd(@NotNull ORLParser.LogicalExpressionAndContext ctx);
	/**
	 * Exit a parse tree produced by {@link ORLParser#logicalExpressionAnd}.
	 * @param ctx the parse tree
	 */
	void exitLogicalExpressionAnd(@NotNull ORLParser.LogicalExpressionAndContext ctx);

	/**
	 * Enter a parse tree produced by {@link ORLParser#parameterExpressionValue}.
	 * @param ctx the parse tree
	 */
	void enterParameterExpressionValue(@NotNull ORLParser.ParameterExpressionValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ORLParser#parameterExpressionValue}.
	 * @param ctx the parse tree
	 */
	void exitParameterExpressionValue(@NotNull ORLParser.ParameterExpressionValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link ORLParser#logicalExpressionNested}.
	 * @param ctx the parse tree
	 */
	void enterLogicalExpressionNested(@NotNull ORLParser.LogicalExpressionNestedContext ctx);
	/**
	 * Exit a parse tree produced by {@link ORLParser#logicalExpressionNested}.
	 * @param ctx the parse tree
	 */
	void exitLogicalExpressionNested(@NotNull ORLParser.LogicalExpressionNestedContext ctx);
}