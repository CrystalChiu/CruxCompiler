// Generated from crux/pt/Crux.g4 by ANTLR 4.7.2
package crux.pt;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CruxLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		VOID=1, BOOL=2, INT=3, AND=4, OR=5, NOT=6, IF=7, ELSE=8, FOR=9, BREAK=10, 
		TRUE=11, FALSE=12, RETURN=13, CONTINUE=14, LOOP=15, OPEN_PAREN=16, CLOSE_PAREN=17, 
		OPEN_BRACE=18, CLOSE_BRACE=19, OPEN_BRACKET=20, CLOSE_BRACKET=21, ADD=22, 
		SUB=23, MUL=24, DIV=25, GREATER_EQUAL=26, LESSER_EQUAL=27, NOT_EQUAL=28, 
		EQUAL=29, GREATER_THAN=30, LESS_THAN=31, ASSIGN=32, COMMA=33, SEMICOLON=34, 
		INTEGER=35, IDENTIFIER=36, WhiteSpaces=37, Comment=38, ERROR=39, EOF_TOKEN=40;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"VOID", "BOOL", "INT", "AND", "OR", "NOT", "IF", "ELSE", "FOR", "BREAK", 
			"TRUE", "FALSE", "RETURN", "CONTINUE", "LOOP", "OPEN_PAREN", "CLOSE_PAREN", 
			"OPEN_BRACE", "CLOSE_BRACE", "OPEN_BRACKET", "CLOSE_BRACKET", "ADD", 
			"SUB", "MUL", "DIV", "GREATER_EQUAL", "LESSER_EQUAL", "NOT_EQUAL", "EQUAL", 
			"GREATER_THAN", "LESS_THAN", "ASSIGN", "COMMA", "SEMICOLON", "INTEGER", 
			"IDENTIFIER", "WhiteSpaces", "Comment", "ERROR", "EOF_TOKEN"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'void'", "'bool'", "'int'", "'&&'", "'||'", "'!'", "'if'", "'else'", 
			"'for'", "'break'", "'true'", "'false'", "'return'", "'continue'", "'loop'", 
			"'('", "')'", "'{'", "'}'", "'['", "']'", "'+'", "'-'", "'*'", "'/'", 
			"'>='", "'<='", "'!='", "'=='", "'>'", "'<'", "'='", "','", "';'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "VOID", "BOOL", "INT", "AND", "OR", "NOT", "IF", "ELSE", "FOR", 
			"BREAK", "TRUE", "FALSE", "RETURN", "CONTINUE", "LOOP", "OPEN_PAREN", 
			"CLOSE_PAREN", "OPEN_BRACE", "CLOSE_BRACE", "OPEN_BRACKET", "CLOSE_BRACKET", 
			"ADD", "SUB", "MUL", "DIV", "GREATER_EQUAL", "LESSER_EQUAL", "NOT_EQUAL", 
			"EQUAL", "GREATER_THAN", "LESS_THAN", "ASSIGN", "COMMA", "SEMICOLON", 
			"INTEGER", "IDENTIFIER", "WhiteSpaces", "Comment", "ERROR", "EOF_TOKEN"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public CruxLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Crux.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2*\u00ec\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\3\2\3\2\3\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\7"+
		"\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3"+
		"\20\3\20\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3"+
		"\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\33\3"+
		"\34\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\""+
		"\3\"\3#\3#\3$\3$\3$\7$\u00c9\n$\f$\16$\u00cc\13$\5$\u00ce\n$\3%\3%\7%"+
		"\u00d2\n%\f%\16%\u00d5\13%\3&\6&\u00d8\n&\r&\16&\u00d9\3&\3&\3\'\3\'\3"+
		"\'\3\'\7\'\u00e2\n\'\f\'\16\'\u00e5\13\'\3\'\3\'\3(\3(\3)\3)\2\2*\3\3"+
		"\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!"+
		"A\"C#E$G%I&K\'M(O)Q*\3\2\b\3\2\63;\3\2\62;\4\2C\\c|\6\2\62;C\\aac|\5\2"+
		"\13\f\17\17\"\"\4\2\f\f\17\17\2\u00f0\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3"+
		"\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65"+
		"\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3"+
		"\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2"+
		"\2\2O\3\2\2\2\2Q\3\2\2\2\3S\3\2\2\2\5X\3\2\2\2\7]\3\2\2\2\ta\3\2\2\2\13"+
		"d\3\2\2\2\rg\3\2\2\2\17i\3\2\2\2\21l\3\2\2\2\23q\3\2\2\2\25u\3\2\2\2\27"+
		"{\3\2\2\2\31\u0080\3\2\2\2\33\u0086\3\2\2\2\35\u008d\3\2\2\2\37\u0096"+
		"\3\2\2\2!\u009b\3\2\2\2#\u009d\3\2\2\2%\u009f\3\2\2\2\'\u00a1\3\2\2\2"+
		")\u00a3\3\2\2\2+\u00a5\3\2\2\2-\u00a7\3\2\2\2/\u00a9\3\2\2\2\61\u00ab"+
		"\3\2\2\2\63\u00ad\3\2\2\2\65\u00af\3\2\2\2\67\u00b2\3\2\2\29\u00b5\3\2"+
		"\2\2;\u00b8\3\2\2\2=\u00bb\3\2\2\2?\u00bd\3\2\2\2A\u00bf\3\2\2\2C\u00c1"+
		"\3\2\2\2E\u00c3\3\2\2\2G\u00cd\3\2\2\2I\u00cf\3\2\2\2K\u00d7\3\2\2\2M"+
		"\u00dd\3\2\2\2O\u00e8\3\2\2\2Q\u00ea\3\2\2\2ST\7x\2\2TU\7q\2\2UV\7k\2"+
		"\2VW\7f\2\2W\4\3\2\2\2XY\7d\2\2YZ\7q\2\2Z[\7q\2\2[\\\7n\2\2\\\6\3\2\2"+
		"\2]^\7k\2\2^_\7p\2\2_`\7v\2\2`\b\3\2\2\2ab\7(\2\2bc\7(\2\2c\n\3\2\2\2"+
		"de\7~\2\2ef\7~\2\2f\f\3\2\2\2gh\7#\2\2h\16\3\2\2\2ij\7k\2\2jk\7h\2\2k"+
		"\20\3\2\2\2lm\7g\2\2mn\7n\2\2no\7u\2\2op\7g\2\2p\22\3\2\2\2qr\7h\2\2r"+
		"s\7q\2\2st\7t\2\2t\24\3\2\2\2uv\7d\2\2vw\7t\2\2wx\7g\2\2xy\7c\2\2yz\7"+
		"m\2\2z\26\3\2\2\2{|\7v\2\2|}\7t\2\2}~\7w\2\2~\177\7g\2\2\177\30\3\2\2"+
		"\2\u0080\u0081\7h\2\2\u0081\u0082\7c\2\2\u0082\u0083\7n\2\2\u0083\u0084"+
		"\7u\2\2\u0084\u0085\7g\2\2\u0085\32\3\2\2\2\u0086\u0087\7t\2\2\u0087\u0088"+
		"\7g\2\2\u0088\u0089\7v\2\2\u0089\u008a\7w\2\2\u008a\u008b\7t\2\2\u008b"+
		"\u008c\7p\2\2\u008c\34\3\2\2\2\u008d\u008e\7e\2\2\u008e\u008f\7q\2\2\u008f"+
		"\u0090\7p\2\2\u0090\u0091\7v\2\2\u0091\u0092\7k\2\2\u0092\u0093\7p\2\2"+
		"\u0093\u0094\7w\2\2\u0094\u0095\7g\2\2\u0095\36\3\2\2\2\u0096\u0097\7"+
		"n\2\2\u0097\u0098\7q\2\2\u0098\u0099\7q\2\2\u0099\u009a\7r\2\2\u009a "+
		"\3\2\2\2\u009b\u009c\7*\2\2\u009c\"\3\2\2\2\u009d\u009e\7+\2\2\u009e$"+
		"\3\2\2\2\u009f\u00a0\7}\2\2\u00a0&\3\2\2\2\u00a1\u00a2\7\177\2\2\u00a2"+
		"(\3\2\2\2\u00a3\u00a4\7]\2\2\u00a4*\3\2\2\2\u00a5\u00a6\7_\2\2\u00a6,"+
		"\3\2\2\2\u00a7\u00a8\7-\2\2\u00a8.\3\2\2\2\u00a9\u00aa\7/\2\2\u00aa\60"+
		"\3\2\2\2\u00ab\u00ac\7,\2\2\u00ac\62\3\2\2\2\u00ad\u00ae\7\61\2\2\u00ae"+
		"\64\3\2\2\2\u00af\u00b0\7@\2\2\u00b0\u00b1\7?\2\2\u00b1\66\3\2\2\2\u00b2"+
		"\u00b3\7>\2\2\u00b3\u00b4\7?\2\2\u00b48\3\2\2\2\u00b5\u00b6\7#\2\2\u00b6"+
		"\u00b7\7?\2\2\u00b7:\3\2\2\2\u00b8\u00b9\7?\2\2\u00b9\u00ba\7?\2\2\u00ba"+
		"<\3\2\2\2\u00bb\u00bc\7@\2\2\u00bc>\3\2\2\2\u00bd\u00be\7>\2\2\u00be@"+
		"\3\2\2\2\u00bf\u00c0\7?\2\2\u00c0B\3\2\2\2\u00c1\u00c2\7.\2\2\u00c2D\3"+
		"\2\2\2\u00c3\u00c4\7=\2\2\u00c4F\3\2\2\2\u00c5\u00ce\7\62\2\2\u00c6\u00ca"+
		"\t\2\2\2\u00c7\u00c9\t\3\2\2\u00c8\u00c7\3\2\2\2\u00c9\u00cc\3\2\2\2\u00ca"+
		"\u00c8\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00ce\3\2\2\2\u00cc\u00ca\3\2"+
		"\2\2\u00cd\u00c5\3\2\2\2\u00cd\u00c6\3\2\2\2\u00ceH\3\2\2\2\u00cf\u00d3"+
		"\t\4\2\2\u00d0\u00d2\t\5\2\2\u00d1\u00d0\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3"+
		"\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4J\3\2\2\2\u00d5\u00d3\3\2\2\2"+
		"\u00d6\u00d8\t\6\2\2\u00d7\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00d7"+
		"\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00dc\b&\2\2\u00dc"+
		"L\3\2\2\2\u00dd\u00de\7\61\2\2\u00de\u00df\7\61\2\2\u00df\u00e3\3\2\2"+
		"\2\u00e0\u00e2\n\7\2\2\u00e1\u00e0\3\2\2\2\u00e2\u00e5\3\2\2\2\u00e3\u00e1"+
		"\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e6\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e6"+
		"\u00e7\b\'\2\2\u00e7N\3\2\2\2\u00e8\u00e9\13\2\2\2\u00e9P\3\2\2\2\u00ea"+
		"\u00eb\7\2\2\3\u00ebR\3\2\2\2\b\2\u00ca\u00cd\u00d3\u00d9\u00e3\3\b\2"+
		"\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}