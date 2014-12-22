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
		T__0=1, OR=2, AND=3, LPAREN=4, RPAREN=5, FUNCTION_NAME=6, IGNORE_CASE=7, 
		PROPERTY=8, WHITESPACE=9, TEXT=10, NUMBER=11, FLOAT=12, INT=13;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"','", "';or;'", "';and;'", "'('", "')'", "FUNCTION_NAME", "'IgnoreCase'", 
		"PROPERTY", "WHITESPACE", "TEXT", "NUMBER", "FLOAT", "INT"
	};
	public static final String[] ruleNames = {
		"T__0", "OR", "AND", "LPAREN", "RPAREN", "FUNCTION_NAME", "IGNORE_CASE", 
		"PROPERTY", "WHITESPACE", "TEXT", "NUMBER", "FLOAT", "INT"
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
		case 9: TEXT_action((RuleContext)_localctx, actionIndex); break;
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\17\u00ee\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\5\7a\n\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7i\n\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7y\n\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u0087\n\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u0095\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00ab\n\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00b4\n\7\5\7\u00b6\n\7\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\7\t\u00c5\n\t\f\t\16\t\u00c8\13\t"+
		"\3\n\6\n\u00cb\n\n\r\n\16\n\u00cc\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\7\13\u00db\n\13\f\13\16\13\u00de\13\13\3\13\3\13"+
		"\3\f\3\f\5\f\u00e4\n\f\3\r\3\r\3\r\3\r\3\16\6\16\u00eb\n\16\r\16\16\16"+
		"\u00ec\2\2\17\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16"+
		"\33\17\3\2\6\5\2C\\aac|\b\2&&\60\60\62;C\\aac|\4\2\13\13\"\"\4\2))\u0080"+
		"\u0080\u010e\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2"+
		"\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2"+
		"\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\3\35\3\2\2\2\5\37\3\2\2\2\7$\3\2"+
		"\2\2\t*\3\2\2\2\13,\3\2\2\2\r\u00b5\3\2\2\2\17\u00b7\3\2\2\2\21\u00c2"+
		"\3\2\2\2\23\u00ca\3\2\2\2\25\u00d0\3\2\2\2\27\u00e3\3\2\2\2\31\u00e5\3"+
		"\2\2\2\33\u00ea\3\2\2\2\35\36\7.\2\2\36\4\3\2\2\2\37 \7=\2\2 !\7q\2\2"+
		"!\"\7t\2\2\"#\7=\2\2#\6\3\2\2\2$%\7=\2\2%&\7c\2\2&\'\7p\2\2\'(\7f\2\2"+
		"()\7=\2\2)\b\3\2\2\2*+\7*\2\2+\n\3\2\2\2,-\7+\2\2-\f\3\2\2\2./\7d\2\2"+
		"/\60\7g\2\2\60\61\7v\2\2\61\62\7y\2\2\62\63\7g\2\2\63\64\7g\2\2\64\u00b6"+
		"\7p\2\2\65\66\7k\2\2\66\67\7u\2\2\678\7P\2\289\7q\2\29:\7v\2\2:;\7P\2"+
		"\2;<\7w\2\2<=\7n\2\2=\u00b6\7n\2\2>?\7k\2\2?@\7u\2\2@A\7P\2\2AB\7w\2\2"+
		"BC\7n\2\2C\u00b6\7n\2\2DE\7n\2\2E\u00b6\7v\2\2FG\7i\2\2G\u00b6\7v\2\2"+
		"HI\7i\2\2I\u00b6\7g\2\2JK\7n\2\2K\u00b6\7g\2\2LM\7d\2\2MN\7g\2\2NO\7h"+
		"\2\2OP\7q\2\2PQ\7t\2\2Q\u00b6\7g\2\2RS\7c\2\2ST\7h\2\2TU\7v\2\2UV\7g\2"+
		"\2V\u00b6\7t\2\2WX\7p\2\2XY\7q\2\2YZ\7v\2\2Z[\7N\2\2[\\\7k\2\2\\]\7m\2"+
		"\2]^\7g\2\2^`\3\2\2\2_a\5\17\b\2`_\3\2\2\2`a\3\2\2\2a\u00b6\3\2\2\2bc"+
		"\7n\2\2cd\7k\2\2de\7m\2\2ef\7g\2\2fh\3\2\2\2gi\5\17\b\2hg\3\2\2\2hi\3"+
		"\2\2\2i\u00b6\3\2\2\2jk\7u\2\2kl\7v\2\2lm\7c\2\2mn\7t\2\2no\7v\2\2op\7"+
		"k\2\2pq\7p\2\2qr\7i\2\2rs\7Y\2\2st\7k\2\2tu\7v\2\2uv\7j\2\2vx\3\2\2\2"+
		"wy\5\17\b\2xw\3\2\2\2xy\3\2\2\2y\u00b6\3\2\2\2z{\7g\2\2{|\7p\2\2|}\7f"+
		"\2\2}~\7k\2\2~\177\7p\2\2\177\u0080\7i\2\2\u0080\u0081\7Y\2\2\u0081\u0082"+
		"\7k\2\2\u0082\u0083\7v\2\2\u0083\u0084\7j\2\2\u0084\u0086\3\2\2\2\u0085"+
		"\u0087\5\17\b\2\u0086\u0085\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u00b6\3"+
		"\2\2\2\u0088\u0089\7e\2\2\u0089\u008a\7q\2\2\u008a\u008b\7p\2\2\u008b"+
		"\u008c\7v\2\2\u008c\u008d\7c\2\2\u008d\u008e\7k\2\2\u008e\u008f\7p\2\2"+
		"\u008f\u0090\7k\2\2\u0090\u0091\7p\2\2\u0091\u0092\7i\2\2\u0092\u0094"+
		"\3\2\2\2\u0093\u0095\5\17\b\2\u0094\u0093\3\2\2\2\u0094\u0095\3\2\2\2"+
		"\u0095\u00b6\3\2\2\2\u0096\u0097\7p\2\2\u0097\u0098\7q\2\2\u0098\u0099"+
		"\7v\2\2\u0099\u009a\7K\2\2\u009a\u00b6\7p\2\2\u009b\u009c\7k\2\2\u009c"+
		"\u00b6\7p\2\2\u009d\u009e\7v\2\2\u009e\u009f\7t\2\2\u009f\u00a0\7w\2\2"+
		"\u00a0\u00b6\7g\2\2\u00a1\u00a2\7h\2\2\u00a2\u00a3\7c\2\2\u00a3\u00a4"+
		"\7n\2\2\u00a4\u00a5\7u\2\2\u00a5\u00b6\7g\2\2\u00a6\u00a7\7g\2\2\u00a7"+
		"\u00a8\7s\2\2\u00a8\u00aa\3\2\2\2\u00a9\u00ab\5\17\b\2\u00aa\u00a9\3\2"+
		"\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00b6\3\2\2\2\u00ac\u00ad\7p\2\2\u00ad"+
		"\u00ae\7q\2\2\u00ae\u00af\7v\2\2\u00af\u00b0\7G\2\2\u00b0\u00b1\7s\2\2"+
		"\u00b1\u00b3\3\2\2\2\u00b2\u00b4\5\17\b\2\u00b3\u00b2\3\2\2\2\u00b3\u00b4"+
		"\3\2\2\2\u00b4\u00b6\3\2\2\2\u00b5.\3\2\2\2\u00b5\65\3\2\2\2\u00b5>\3"+
		"\2\2\2\u00b5D\3\2\2\2\u00b5F\3\2\2\2\u00b5H\3\2\2\2\u00b5J\3\2\2\2\u00b5"+
		"L\3\2\2\2\u00b5R\3\2\2\2\u00b5W\3\2\2\2\u00b5b\3\2\2\2\u00b5j\3\2\2\2"+
		"\u00b5z\3\2\2\2\u00b5\u0088\3\2\2\2\u00b5\u0096\3\2\2\2\u00b5\u009b\3"+
		"\2\2\2\u00b5\u009d\3\2\2\2\u00b5\u00a1\3\2\2\2\u00b5\u00a6\3\2\2\2\u00b5"+
		"\u00ac\3\2\2\2\u00b6\16\3\2\2\2\u00b7\u00b8\7K\2\2\u00b8\u00b9\7i\2\2"+
		"\u00b9\u00ba\7p\2\2\u00ba\u00bb\7q\2\2\u00bb\u00bc\7t\2\2\u00bc\u00bd"+
		"\7g\2\2\u00bd\u00be\7E\2\2\u00be\u00bf\7c\2\2\u00bf\u00c0\7u\2\2\u00c0"+
		"\u00c1\7g\2\2\u00c1\20\3\2\2\2\u00c2\u00c6\t\2\2\2\u00c3\u00c5\t\3\2\2"+
		"\u00c4\u00c3\3\2\2\2\u00c5\u00c8\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c6\u00c7"+
		"\3\2\2\2\u00c7\22\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c9\u00cb\t\4\2\2\u00ca"+
		"\u00c9\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc\u00cd\3\2"+
		"\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00cf\b\n\2\2\u00cf\24\3\2\2\2\u00d0\u00dc"+
		"\7)\2\2\u00d1\u00d2\7\u0080\2\2\u00d2\u00d3\7)\2\2\u00d3\u00db\b\13\3"+
		"\2\u00d4\u00d5\7\u0080\2\2\u00d5\u00d6\7\u0080\2\2\u00d6\u00d7\3\2\2\2"+
		"\u00d7\u00db\b\13\4\2\u00d8\u00d9\n\5\2\2\u00d9\u00db\b\13\5\2\u00da\u00d1"+
		"\3\2\2\2\u00da\u00d4\3\2\2\2\u00da\u00d8\3\2\2\2\u00db\u00de\3\2\2\2\u00dc"+
		"\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00df\3\2\2\2\u00de\u00dc\3\2"+
		"\2\2\u00df\u00e0\7)\2\2\u00e0\26\3\2\2\2\u00e1\u00e4\5\33\16\2\u00e2\u00e4"+
		"\5\31\r\2\u00e3\u00e1\3\2\2\2\u00e3\u00e2\3\2\2\2\u00e4\30\3\2\2\2\u00e5"+
		"\u00e6\5\33\16\2\u00e6\u00e7\7\60\2\2\u00e7\u00e8\5\33\16\2\u00e8\32\3"+
		"\2\2\2\u00e9\u00eb\4\62;\2\u00ea\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec"+
		"\u00ea\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\34\3\2\2\2\21\2`hx\u0086\u0094"+
		"\u00aa\u00b3\u00b5\u00c6\u00cc\u00da\u00dc\u00e3\u00ec\6\b\2\2\3\13\2"+
		"\3\13\3\3\13\4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}