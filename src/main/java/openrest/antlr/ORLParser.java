// Generated from ORL.g4 by ANTLR 4.2.2
package openrest.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ORLParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, OR=2, AND=3, LPAREN=4, RPAREN=5, FUNCTION_NAME=6, IGNORE_CASE=7, 
		PROPERTY=8, WHITESPACE=9, TEXT=10, NUMBER=11, FLOAT=12, INT=13;
	public static final String[] tokenNames = {
		"<INVALID>", "','", "';or;'", "';and;'", "'('", "')'", "FUNCTION_NAME", 
		"'IgnoreCase'", "PROPERTY", "WHITESPACE", "TEXT", "NUMBER", "FLOAT", "INT"
	};
	public static final int
		RULE_functionName = 0, RULE_function = 1, RULE_propertyName = 2, RULE_parameter = 3, 
		RULE_logicalExpression = 4;
	public static final String[] ruleNames = {
		"functionName", "function", "propertyName", "parameter", "logicalExpression"
	};

	@Override
	public String getGrammarFileName() { return "ORL.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


		StringBuilder buf = new StringBuilder();

	public ORLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FunctionNameContext extends ParserRuleContext {
		public TerminalNode FUNCTION_NAME() { return getToken(ORLParser.FUNCTION_NAME, 0); }
		public FunctionNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).enterFunctionName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).exitFunctionName(this);
		}
	}

	public final FunctionNameContext functionName() throws RecognitionException {
		FunctionNameContext _localctx = new FunctionNameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_functionName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10); match(FUNCTION_NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionContext extends ParserRuleContext {
		public FunctionNameContext functionName() {
			return getRuleContext(FunctionNameContext.class,0);
		}
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public TerminalNode LPAREN() { return getToken(ORLParser.LPAREN, 0); }
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(ORLParser.RPAREN, 0); }
		public PropertyNameContext propertyName() {
			return getRuleContext(PropertyNameContext.class,0);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).exitFunction(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12); functionName();
			setState(13); match(LPAREN);
			setState(14); propertyName();
			setState(19);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==1) {
				{
				{
				setState(15); match(1);
				setState(16); parameter();
				}
				}
				setState(21);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(22); match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropertyNameContext extends ParserRuleContext {
		public TerminalNode PROPERTY() { return getToken(ORLParser.PROPERTY, 0); }
		public PropertyNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propertyName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).enterPropertyName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).exitPropertyName(this);
		}
	}

	public final PropertyNameContext propertyName() throws RecognitionException {
		PropertyNameContext _localctx = new PropertyNameContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_propertyName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24); match(PROPERTY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterContext extends ParserRuleContext {
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
	 
		public ParameterContext() { }
		public void copyFrom(ParameterContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ParameterTextValueContext extends ParameterContext {
		public TerminalNode TEXT() { return getToken(ORLParser.TEXT, 0); }
		public ParameterTextValueContext(ParameterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).enterParameterTextValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).exitParameterTextValue(this);
		}
	}
	public static class ParameterNumberValueContext extends ParameterContext {
		public TerminalNode NUMBER() { return getToken(ORLParser.NUMBER, 0); }
		public ParameterNumberValueContext(ParameterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).enterParameterNumberValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).exitParameterNumberValue(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_parameter);
		try {
			setState(28);
			switch (_input.LA(1)) {
			case NUMBER:
				_localctx = new ParameterNumberValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(26); match(NUMBER);
				}
				break;
			case TEXT:
				_localctx = new ParameterTextValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(27); match(TEXT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LogicalExpressionContext extends ParserRuleContext {
		public LogicalExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logicalExpression; }
	 
		public LogicalExpressionContext() { }
		public void copyFrom(LogicalExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LogicalExpressionAtomContext extends LogicalExpressionContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public LogicalExpressionAtomContext(LogicalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).enterLogicalExpressionAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).exitLogicalExpressionAtom(this);
		}
	}
	public static class LogicalExpressionOrContext extends LogicalExpressionContext {
		public LogicalExpressionContext logicalExpression(int i) {
			return getRuleContext(LogicalExpressionContext.class,i);
		}
		public TerminalNode OR() { return getToken(ORLParser.OR, 0); }
		public List<LogicalExpressionContext> logicalExpression() {
			return getRuleContexts(LogicalExpressionContext.class);
		}
		public LogicalExpressionOrContext(LogicalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).enterLogicalExpressionOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).exitLogicalExpressionOr(this);
		}
	}
	public static class LogicalExpressionAndContext extends LogicalExpressionContext {
		public LogicalExpressionContext logicalExpression(int i) {
			return getRuleContext(LogicalExpressionContext.class,i);
		}
		public TerminalNode AND() { return getToken(ORLParser.AND, 0); }
		public List<LogicalExpressionContext> logicalExpression() {
			return getRuleContexts(LogicalExpressionContext.class);
		}
		public LogicalExpressionAndContext(LogicalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).enterLogicalExpressionAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).exitLogicalExpressionAnd(this);
		}
	}
	public static class LogicalExpressionNestedContext extends LogicalExpressionContext {
		public TerminalNode LPAREN() { return getToken(ORLParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(ORLParser.RPAREN, 0); }
		public LogicalExpressionContext logicalExpression() {
			return getRuleContext(LogicalExpressionContext.class,0);
		}
		public LogicalExpressionNestedContext(LogicalExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).enterLogicalExpressionNested(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ORLListener ) ((ORLListener)listener).exitLogicalExpressionNested(this);
		}
	}

	public final LogicalExpressionContext logicalExpression() throws RecognitionException {
		return logicalExpression(0);
	}

	private LogicalExpressionContext logicalExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		LogicalExpressionContext _localctx = new LogicalExpressionContext(_ctx, _parentState);
		LogicalExpressionContext _prevctx = _localctx;
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_logicalExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				_localctx = new LogicalExpressionNestedContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(31); match(LPAREN);
				setState(32); logicalExpression(0);
				setState(33); match(RPAREN);
				}
				break;
			case FUNCTION_NAME:
				{
				_localctx = new LogicalExpressionAtomContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(35); function();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(46);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(44);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new LogicalExpressionAndContext(new LogicalExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_logicalExpression);
						setState(38);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(39); match(AND);
						setState(40); logicalExpression(5);
						}
						break;

					case 2:
						{
						_localctx = new LogicalExpressionOrContext(new LogicalExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_logicalExpression);
						setState(41);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(42); match(OR);
						setState(43); logicalExpression(4);
						}
						break;
					}
					} 
				}
				setState(48);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 4: return logicalExpression_sempred((LogicalExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean logicalExpression_sempred(LogicalExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return precpred(_ctx, 4);

		case 1: return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\17\64\4\2\t\2\4\3"+
		"\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\3\3\3\3\3\3\3\3\3\7\3\24\n\3\f"+
		"\3\16\3\27\13\3\3\3\3\3\3\4\3\4\3\5\3\5\5\5\37\n\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\5\6\'\n\6\3\6\3\6\3\6\3\6\3\6\3\6\7\6/\n\6\f\6\16\6\62\13\6\3\6\2"+
		"\3\n\7\2\4\6\b\n\2\2\63\2\f\3\2\2\2\4\16\3\2\2\2\6\32\3\2\2\2\b\36\3\2"+
		"\2\2\n&\3\2\2\2\f\r\7\b\2\2\r\3\3\2\2\2\16\17\5\2\2\2\17\20\7\6\2\2\20"+
		"\25\5\6\4\2\21\22\7\3\2\2\22\24\5\b\5\2\23\21\3\2\2\2\24\27\3\2\2\2\25"+
		"\23\3\2\2\2\25\26\3\2\2\2\26\30\3\2\2\2\27\25\3\2\2\2\30\31\7\7\2\2\31"+
		"\5\3\2\2\2\32\33\7\n\2\2\33\7\3\2\2\2\34\37\7\r\2\2\35\37\7\f\2\2\36\34"+
		"\3\2\2\2\36\35\3\2\2\2\37\t\3\2\2\2 !\b\6\1\2!\"\7\6\2\2\"#\5\n\6\2#$"+
		"\7\7\2\2$\'\3\2\2\2%\'\5\4\3\2& \3\2\2\2&%\3\2\2\2\'\60\3\2\2\2()\f\6"+
		"\2\2)*\7\5\2\2*/\5\n\6\7+,\f\5\2\2,-\7\4\2\2-/\5\n\6\6.(\3\2\2\2.+\3\2"+
		"\2\2/\62\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\13\3\2\2\2\62\60\3\2\2\2"+
		"\7\25\36&.\60";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}