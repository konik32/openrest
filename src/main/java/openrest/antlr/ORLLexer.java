// Generated from ORL.g4 by ANTLR 4.2.2
package openrest.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ORLLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, OR=2, AND=3, LPAREN=4, RPAREN=5, FUNCTION_NAME=6, PROPERTY=7, 
		WHITESPACE=8, TEXT=9, NUMBER=10, FLOAT=11, INT=12;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"','", "';or;'", "';and;'", "'('", "')'", "FUNCTION_NAME", "PROPERTY", 
		"WHITESPACE", "TEXT", "NUMBER", "FLOAT", "INT"
	};
	public static final String[] ruleNames = {
		"T__0", "OR", "AND", "LPAREN", "RPAREN", "FUNCTION_NAME", "PROPERTY", 
		"WHITESPACE", "TEXT", "NUMBER", "FLOAT", "INT"
	};


		StringBuilder buf = new StringBuilder();


	public ORLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ORL.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 8: TEXT_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void TEXT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: buf.append('\''); break;

		case 1: buf.append('~'); break;

		case 2: buf.append((char)_input.LA(-1)); break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\16\u00bf\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\5\7\u0092\n\7\3\b\3\b\7\b\u0096\n\b\f\b\16\b\u0099\13\b\3\t\6\t"+
		"\u009c\n\t\r\t\16\t\u009d\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\7\n\u00ac\n\n\f\n\16\n\u00af\13\n\3\n\3\n\3\13\3\13\5\13\u00b5\n"+
		"\13\3\f\3\f\3\f\3\f\3\r\6\r\u00bc\n\r\r\r\16\r\u00bd\2\2\16\3\3\5\4\7"+
		"\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\3\2\6\5\2C\\aac|\b\2&"+
		"&\60\60\62;C\\aac|\4\2\13\13\"\"\4\2))\u0080\u0080\u00d7\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2"+
		"\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\3"+
		"\33\3\2\2\2\5\35\3\2\2\2\7\"\3\2\2\2\t(\3\2\2\2\13*\3\2\2\2\r\u0091\3"+
		"\2\2\2\17\u0093\3\2\2\2\21\u009b\3\2\2\2\23\u00a1\3\2\2\2\25\u00b4\3\2"+
		"\2\2\27\u00b6\3\2\2\2\31\u00bb\3\2\2\2\33\34\7.\2\2\34\4\3\2\2\2\35\36"+
		"\7=\2\2\36\37\7q\2\2\37 \7t\2\2 !\7=\2\2!\6\3\2\2\2\"#\7=\2\2#$\7c\2\2"+
		"$%\7p\2\2%&\7f\2\2&\'\7=\2\2\'\b\3\2\2\2()\7*\2\2)\n\3\2\2\2*+\7+\2\2"+
		"+\f\3\2\2\2,-\7d\2\2-.\7g\2\2./\7v\2\2/\60\7y\2\2\60\61\7g\2\2\61\62\7"+
		"g\2\2\62\u0092\7p\2\2\63\64\7k\2\2\64\65\7u\2\2\65\66\7P\2\2\66\67\7q"+
		"\2\2\678\7v\2\289\7P\2\29:\7w\2\2:;\7n\2\2;\u0092\7n\2\2<=\7k\2\2=>\7"+
		"u\2\2>?\7P\2\2?@\7w\2\2@A\7n\2\2A\u0092\7n\2\2BC\7n\2\2C\u0092\7v\2\2"+
		"DE\7i\2\2E\u0092\7v\2\2FG\7i\2\2G\u0092\7g\2\2HI\7n\2\2I\u0092\7g\2\2"+
		"JK\7d\2\2KL\7g\2\2LM\7h\2\2MN\7q\2\2NO\7t\2\2O\u0092\7g\2\2PQ\7c\2\2Q"+
		"R\7h\2\2RS\7v\2\2ST\7g\2\2T\u0092\7t\2\2UV\7p\2\2VW\7q\2\2WX\7v\2\2XY"+
		"\7N\2\2YZ\7k\2\2Z[\7m\2\2[\u0092\7g\2\2\\]\7n\2\2]^\7k\2\2^_\7m\2\2_\u0092"+
		"\7g\2\2`a\7u\2\2ab\7v\2\2bc\7c\2\2cd\7v\2\2de\7k\2\2ef\7p\2\2fg\7i\2\2"+
		"gh\7Y\2\2hi\7k\2\2ij\7v\2\2j\u0092\7j\2\2kl\7g\2\2lm\7p\2\2mn\7f\2\2n"+
		"o\7k\2\2op\7p\2\2pq\7i\2\2qr\7Y\2\2rs\7k\2\2st\7v\2\2t\u0092\7j\2\2uv"+
		"\7e\2\2vw\7q\2\2wx\7p\2\2xy\7v\2\2yz\7c\2\2z{\7k\2\2{|\7p\2\2|}\7k\2\2"+
		"}~\7p\2\2~\u0092\7i\2\2\177\u0080\7p\2\2\u0080\u0081\7q\2\2\u0081\u0082"+
		"\7v\2\2\u0082\u0083\7K\2\2\u0083\u0092\7p\2\2\u0084\u0085\7k\2\2\u0085"+
		"\u0092\7p\2\2\u0086\u0087\7v\2\2\u0087\u0088\7t\2\2\u0088\u0089\7w\2\2"+
		"\u0089\u0092\7g\2\2\u008a\u008b\7h\2\2\u008b\u008c\7c\2\2\u008c\u008d"+
		"\7n\2\2\u008d\u008e\7u\2\2\u008e\u0092\7g\2\2\u008f\u0090\7g\2\2\u0090"+
		"\u0092\7s\2\2\u0091,\3\2\2\2\u0091\63\3\2\2\2\u0091<\3\2\2\2\u0091B\3"+
		"\2\2\2\u0091D\3\2\2\2\u0091F\3\2\2\2\u0091H\3\2\2\2\u0091J\3\2\2\2\u0091"+
		"P\3\2\2\2\u0091U\3\2\2\2\u0091\\\3\2\2\2\u0091`\3\2\2\2\u0091k\3\2\2\2"+
		"\u0091u\3\2\2\2\u0091\177\3\2\2\2\u0091\u0084\3\2\2\2\u0091\u0086\3\2"+
		"\2\2\u0091\u008a\3\2\2\2\u0091\u008f\3\2\2\2\u0092\16\3\2\2\2\u0093\u0097"+
		"\t\2\2\2\u0094\u0096\t\3\2\2\u0095\u0094\3\2\2\2\u0096\u0099\3\2\2\2\u0097"+
		"\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\20\3\2\2\2\u0099\u0097\3\2\2"+
		"\2\u009a\u009c\t\4\2\2\u009b\u009a\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009b"+
		"\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a0\b\t\2\2\u00a0"+
		"\22\3\2\2\2\u00a1\u00ad\7)\2\2\u00a2\u00a3\7\u0080\2\2\u00a3\u00a4\7)"+
		"\2\2\u00a4\u00ac\b\n\3\2\u00a5\u00a6\7\u0080\2\2\u00a6\u00a7\7\u0080\2"+
		"\2\u00a7\u00a8\3\2\2\2\u00a8\u00ac\b\n\4\2\u00a9\u00aa\n\5\2\2\u00aa\u00ac"+
		"\b\n\5\2\u00ab\u00a2\3\2\2\2\u00ab\u00a5\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ac"+
		"\u00af\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00b0\3\2"+
		"\2\2\u00af\u00ad\3\2\2\2\u00b0\u00b1\7)\2\2\u00b1\24\3\2\2\2\u00b2\u00b5"+
		"\5\31\r\2\u00b3\u00b5\5\27\f\2\u00b4\u00b2\3\2\2\2\u00b4\u00b3\3\2\2\2"+
		"\u00b5\26\3\2\2\2\u00b6\u00b7\5\31\r\2\u00b7\u00b8\7\60\2\2\u00b8\u00b9"+
		"\5\31\r\2\u00b9\30\3\2\2\2\u00ba\u00bc\4\62;\2\u00bb\u00ba\3\2\2\2\u00bc"+
		"\u00bd\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00be\3\2\2\2\u00be\32\3\2\2"+
		"\2\n\2\u0091\u0097\u009d\u00ab\u00ad\u00b4\u00bd\6\b\2\2\3\n\2\3\n\3\3"+
		"\n\4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}