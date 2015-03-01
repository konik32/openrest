package openrest.antlr;

import java.util.List;
import java.util.Stack;

import org.hibernate.criterion.LogicalExpression;

import openrest.antlr.ORLParser.FunctionContext;
import openrest.antlr.ORLParser.FunctionNameContext;
import openrest.antlr.ORLParser.LogicalExpressionAndContext;
import openrest.antlr.ORLParser.LogicalExpressionContext;
import openrest.antlr.ORLParser.LogicalExpressionOrContext;
import openrest.antlr.ORLParser.ParameterContext;
import openrest.antlr.ORLParser.ParameterExpressionValueContext;
import openrest.antlr.ORLParser.ParameterNumberValueContext;
import openrest.antlr.ORLParser.ParameterTextValueContext;
import openrest.antlr.ORLParser.PropertyNameContext;
import openrest.httpquery.parser.RequestParsingException;
import openrest.httpquery.parser.TempPart;
import openrest.httpquery.parser.TempPart.Type;

public class ORLParserListener extends ORLBaseListener {

	private Stack<TempPart> tempPartsStack = new Stack<TempPart>();

	private boolean innerFilter = false;

	public ORLParserListener(boolean innerFilter) {
		this.innerFilter = innerFilter;
	}

	public ORLParserListener() {
	}

	public TempPart getRoot() {
		return tempPartsStack.peek();
	}

	@Override
	public void enterFunction(FunctionContext ctx) {
		TempPart tempPart = new TempPart();
		tempPart.setType(Type.LEAF);
		tempPartsStack.push(tempPart);
	}

	@Override
	public void exitFunctionName(FunctionNameContext ctx) {
		tempPartsStack.peek().setFunctionName(ctx.getText());
	}

	@Override
	public void exitPropertyName(PropertyNameContext ctx) {
		tempPartsStack.peek().setPropertyName(ctx.getText());
	}

	@Override
	public void exitParameterNumberValue(ParameterNumberValueContext ctx) {
		tempPartsStack.peek().addParameter(ctx.getText());
	}

	@Override
	public void exitParameterTextValue(ParameterTextValueContext ctx) {
		tempPartsStack.peek().addParameter(ctx.getText().substring(1, ctx.getText().length() - 1));
	}

	@Override
	public void exitParameterExpressionValue(ParameterExpressionValueContext ctx) {
		if(innerFilter)
			tempPartsStack.peek().addParameter(ctx.getText());
		else
			throw new RequestParsingException("Expressions allowed only in static filters");
	}

	@Override
	public void exitLogicalExpressionOr(LogicalExpressionOrContext ctx) {
		TempPart part = new TempPart(Type.OR, 2);
		part.addPart(tempPartsStack.pop());
		part.addPart(tempPartsStack.pop());
		tempPartsStack.push(part);
	}

	@Override
	public void exitLogicalExpressionAnd(LogicalExpressionAndContext ctx) {
		TempPart part = new TempPart(Type.AND, 2);
		part.addPart(tempPartsStack.pop());
		part.addPart(tempPartsStack.pop());
		tempPartsStack.push(part);
	}
}
