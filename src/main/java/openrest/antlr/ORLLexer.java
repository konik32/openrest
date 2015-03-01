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
		PROPERTY=8, WHITESPACE=9, TEXT=10, NUMBER=11, EXPRESSION=12, FLOAT=13, 
		INT=14;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"','", "';or;'", "';and;'", "'('", "')'", "FUNCTION_NAME", "'IgnoreCase'", 
		"PROPERTY", "WHITESPACE", "TEXT", "NUMBER", "EXPRESSION", "FLOAT", "INT"
	};
	public static final String[] ruleNames = {
		"T__0", "OR", "AND", "LPAREN", "RPAREN", "FUNCTION_NAME", "IGNORE_CASE", 
		"PROPERTY", "WHITESPACE", "TEXT", "NUMBER", "EXPRESSION", "FLOAT", "INT"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\20\u00f9\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\3\3\3\3\3\3\3\3"+
		"\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7c\n\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7"+
		"k\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7{\n\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u0089\n\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u0097\n\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00ad"+
		"\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u00b6\n\7\5\7\u00b8\n\7\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\7\t\u00c7\n\t\f\t\16\t\u00ca"+
		"\13\t\3\n\6\n\u00cd\n\n\r\n\16\n\u00ce\3\n\3\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\7\13\u00dd\n\13\f\13\16\13\u00e0\13\13\3\13"+
		"\3\13\3\f\3\f\5\f\u00e6\n\f\3\r\3\r\7\r\u00ea\n\r\f\r\16\r\u00ed\13\r"+
		"\3\r\3\r\3\16\3\16\3\16\3\16\3\17\6\17\u00f6\n\17\r\17\16\17\u00f7\2\2"+
		"\20\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35"+
		"\20\3\2\7\5\2C\\aac|\b\2&&\60\60\62;C\\aac|\4\2\13\13\"\"\4\2))\u0080"+
		"\u0080\3\2%%\u011a\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\3\37\3\2\2\2\5"+
		"!\3\2\2\2\7&\3\2\2\2\t,\3\2\2\2\13.\3\2\2\2\r\u00b7\3\2\2\2\17\u00b9\3"+
		"\2\2\2\21\u00c4\3\2\2\2\23\u00cc\3\2\2\2\25\u00d2\3\2\2\2\27\u00e5\3\2"+
		"\2\2\31\u00e7\3\2\2\2\33\u00f0\3\2\2\2\35\u00f5\3\2\2\2\37 \7.\2\2 \4"+
		"\3\2\2\2!\"\7=\2\2\"#\7q\2\2#$\7t\2\2$%\7=\2\2%\6\3\2\2\2&\'\7=\2\2\'"+
		"(\7c\2\2()\7p\2\2)*\7f\2\2*+\7=\2\2+\b\3\2\2\2,-\7*\2\2-\n\3\2\2\2./\7"+
		"+\2\2/\f\3\2\2\2\60\61\7d\2\2\61\62\7g\2\2\62\63\7v\2\2\63\64\7y\2\2\64"+
		"\65\7g\2\2\65\66\7g\2\2\66\u00b8\7p\2\2\678\7k\2\289\7u\2\29:\7P\2\2:"+
		";\7q\2\2;<\7v\2\2<=\7P\2\2=>\7w\2\2>?\7n\2\2?\u00b8\7n\2\2@A\7k\2\2AB"+
		"\7u\2\2BC\7P\2\2CD\7w\2\2DE\7n\2\2E\u00b8\7n\2\2FG\7n\2\2G\u00b8\7v\2"+
		"\2HI\7i\2\2I\u00b8\7v\2\2JK\7i\2\2K\u00b8\7g\2\2LM\7n\2\2M\u00b8\7g\2"+
		"\2NO\7d\2\2OP\7g\2\2PQ\7h\2\2QR\7q\2\2RS\7t\2\2S\u00b8\7g\2\2TU\7c\2\2"+
		"UV\7h\2\2VW\7v\2\2WX\7g\2\2X\u00b8\7t\2\2YZ\7p\2\2Z[\7q\2\2[\\\7v\2\2"+
		"\\]\7N\2\2]^\7k\2\2^_\7m\2\2_`\7g\2\2`b\3\2\2\2ac\5\17\b\2ba\3\2\2\2b"+
		"c\3\2\2\2c\u00b8\3\2\2\2de\7n\2\2ef\7k\2\2fg\7m\2\2gh\7g\2\2hj\3\2\2\2"+
		"ik\5\17\b\2ji\3\2\2\2jk\3\2\2\2k\u00b8\3\2\2\2lm\7u\2\2mn\7v\2\2no\7c"+
		"\2\2op\7t\2\2pq\7v\2\2qr\7k\2\2rs\7p\2\2st\7i\2\2tu\7Y\2\2uv\7k\2\2vw"+
		"\7v\2\2wx\7j\2\2xz\3\2\2\2y{\5\17\b\2zy\3\2\2\2z{\3\2\2\2{\u00b8\3\2\2"+
		"\2|}\7g\2\2}~\7p\2\2~\177\7f\2\2\177\u0080\7k\2\2\u0080\u0081\7p\2\2\u0081"+
		"\u0082\7i\2\2\u0082\u0083\7Y\2\2\u0083\u0084\7k\2\2\u0084\u0085\7v\2\2"+
		"\u0085\u0086\7j\2\2\u0086\u0088\3\2\2\2\u0087\u0089\5\17\b\2\u0088\u0087"+
		"\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u00b8\3\2\2\2\u008a\u008b\7e\2\2\u008b"+
		"\u008c\7q\2\2\u008c\u008d\7p\2\2\u008d\u008e\7v\2\2\u008e\u008f\7c\2\2"+
		"\u008f\u0090\7k\2\2\u0090\u0091\7p\2\2\u0091\u0092\7k\2\2\u0092\u0093"+
		"\7p\2\2\u0093\u0094\7i\2\2\u0094\u0096\3\2\2\2\u0095\u0097\5\17\b\2\u0096"+
		"\u0095\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u00b8\3\2\2\2\u0098\u0099\7p"+
		"\2\2\u0099\u009a\7q\2\2\u009a\u009b\7v\2\2\u009b\u009c\7K\2\2\u009c\u00b8"+
		"\7p\2\2\u009d\u009e\7k\2\2\u009e\u00b8\7p\2\2\u009f\u00a0\7v\2\2\u00a0"+
		"\u00a1\7t\2\2\u00a1\u00a2\7w\2\2\u00a2\u00b8\7g\2\2\u00a3\u00a4\7h\2\2"+
		"\u00a4\u00a5\7c\2\2\u00a5\u00a6\7n\2\2\u00a6\u00a7\7u\2\2\u00a7\u00b8"+
		"\7g\2\2\u00a8\u00a9\7g\2\2\u00a9\u00aa\7s\2\2\u00aa\u00ac\3\2\2\2\u00ab"+
		"\u00ad\5\17\b\2\u00ac\u00ab\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00b8\3"+
		"\2\2\2\u00ae\u00af\7p\2\2\u00af\u00b0\7q\2\2\u00b0\u00b1\7v\2\2\u00b1"+
		"\u00b2\7G\2\2\u00b2\u00b3\7s\2\2\u00b3\u00b5\3\2\2\2\u00b4\u00b6\5\17"+
		"\b\2\u00b5\u00b4\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b8\3\2\2\2\u00b7"+
		"\60\3\2\2\2\u00b7\67\3\2\2\2\u00b7@\3\2\2\2\u00b7F\3\2\2\2\u00b7H\3\2"+
		"\2\2\u00b7J\3\2\2\2\u00b7L\3\2\2\2\u00b7N\3\2\2\2\u00b7T\3\2\2\2\u00b7"+
		"Y\3\2\2\2\u00b7d\3\2\2\2\u00b7l\3\2\2\2\u00b7|\3\2\2\2\u00b7\u008a\3\2"+
		"\2\2\u00b7\u0098\3\2\2\2\u00b7\u009d\3\2\2\2\u00b7\u009f\3\2\2\2\u00b7"+
		"\u00a3\3\2\2\2\u00b7\u00a8\3\2\2\2\u00b7\u00ae\3\2\2\2\u00b8\16\3\2\2"+
		"\2\u00b9\u00ba\7K\2\2\u00ba\u00bb\7i\2\2\u00bb\u00bc\7p\2\2\u00bc\u00bd"+
		"\7q\2\2\u00bd\u00be\7t\2\2\u00be\u00bf\7g\2\2\u00bf\u00c0\7E\2\2\u00c0"+
		"\u00c1\7c\2\2\u00c1\u00c2\7u\2\2\u00c2\u00c3\7g\2\2\u00c3\20\3\2\2\2\u00c4"+
		"\u00c8\t\2\2\2\u00c5\u00c7\t\3\2\2\u00c6\u00c5\3\2\2\2\u00c7\u00ca\3\2"+
		"\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\22\3\2\2\2\u00ca\u00c8"+
		"\3\2\2\2\u00cb\u00cd\t\4\2\2\u00cc\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce"+
		"\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d1\b\n"+
		"\2\2\u00d1\24\3\2\2\2\u00d2\u00de\7)\2\2\u00d3\u00d4\7\u0080\2\2\u00d4"+
		"\u00d5\7)\2\2\u00d5\u00dd\b\13\3\2\u00d6\u00d7\7\u0080\2\2\u00d7\u00d8"+
		"\7\u0080\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00dd\b\13\4\2\u00da\u00db\n\5"+
		"\2\2\u00db\u00dd\b\13\5\2\u00dc\u00d3\3\2\2\2\u00dc\u00d6\3\2\2\2\u00dc"+
		"\u00da\3\2\2\2\u00dd\u00e0\3\2\2\2\u00de\u00dc\3\2\2\2\u00de\u00df\3\2"+
		"\2\2\u00df\u00e1\3\2\2\2\u00e0\u00de\3\2\2\2\u00e1\u00e2\7)\2\2\u00e2"+
		"\26\3\2\2\2\u00e3\u00e6\5\35\17\2\u00e4\u00e6\5\33\16\2\u00e5\u00e3\3"+
		"\2\2\2\u00e5\u00e4\3\2\2\2\u00e6\30\3\2\2\2\u00e7\u00eb\7%\2\2\u00e8\u00ea"+
		"\n\6\2\2\u00e9\u00e8\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb"+
		"\u00ec\3\2\2\2\u00ec\u00ee\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00ef\7%"+
		"\2\2\u00ef\32\3\2\2\2\u00f0\u00f1\5\35\17\2\u00f1\u00f2\7\60\2\2\u00f2"+
		"\u00f3\5\35\17\2\u00f3\34\3\2\2\2\u00f4\u00f6\4\62;\2\u00f5\u00f4\3\2"+
		"\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8"+
		"\36\3\2\2\2\22\2bjz\u0088\u0096\u00ac\u00b5\u00b7\u00c8\u00ce\u00dc\u00de"+
		"\u00e5\u00eb\u00f7\6\b\2\2\3\13\2\3\13\3\3\13\4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}