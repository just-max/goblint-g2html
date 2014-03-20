package g2html;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static g2html.CTokens.Kind;

public class ProcessCfile implements Runnable {
	private File to;
	private FileStats stats;
	private File from;

	// allocate a new transformation task
	ProcessCfile(File from, File to, FileStats stats) {
		super();
		this.from = from;
		this.to = to;
		this.stats = stats;
	}

	// write the set as an JSON string array
	static String setToJsonArray(Set<String> s){
		if (s==null) return "[]";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		boolean first = true;
		for (String e : s){
			if (!first)
				stringBuilder.append(", ");
			first = false;
			stringBuilder.append("\"");
			stringBuilder.append(e);
			stringBuilder.append("\"");
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	private static Set<Kind> type_kw = new TreeSet<>(Arrays.asList(new Kind[]{Kind.CHAR, Kind.CONST, Kind.DOUBLE, Kind.AUTO, Kind.ENUM,
        Kind.EXTERN, Kind.FLOAT, Kind.INT, Kind.LONG, Kind.REGISTER, Kind.SHORT, Kind.SIGNED, Kind.STATIC, Kind.STRUCT, 
        Kind.TYPEDEF, Kind.UNION, Kind.UNSIGNED, Kind.VOID, Kind.VOLATILE}));

	private static Set<Kind> stmt_kw = new TreeSet<>(Arrays.asList(new Kind[]{Kind.ASM, Kind.BREAK, Kind.CASE, Kind.CONTINUE,
        Kind.DEFAULT, Kind.DO, Kind.ELSE, Kind.FOR, Kind.GOTO, Kind.IF, Kind.RETURN, Kind.SIZEOF, Kind.SWITCH, 
        Kind.WHILE}));

	private static Set<Kind> operatr = new TreeSet<>(Arrays.asList(new Kind[]{	Kind.DOT, Kind.EQ, Kind.GT, Kind.LT, Kind.NOT, Kind.COMP,
				Kind.QUESTION, Kind.COLON, Kind.EQEQ, Kind.LTEQ, Kind.GTEQ, Kind.NOTEQ, Kind.ANDAND, Kind.OROR, Kind.PLUSPLUS, 
        Kind.MINUSMINUS, Kind.PLUS, Kind.MINUS, Kind.MULT, Kind.DIV, Kind.AND, Kind.OR, Kind.XOR, Kind.MOD, 
        Kind.LSHIFT, Kind.RSHIFT, Kind.URSHIFT, Kind.PLUSEQ, Kind.MINUSEQ, Kind.MULTEQ, Kind.DIVEQ,
				Kind.ANDEQ, Kind.OREQ, Kind.XOREQ, Kind.MODEQ, Kind.LSHIFTEQ, Kind.RSHIFTEQ, Kind.URSHIFTEQ}));

	private static Set<Kind> separtr = new TreeSet<>(Arrays.asList(new Kind[]{Kind.LPAREN, Kind.RPAREN, Kind.LBRACE, Kind.RBRACE,
          Kind.LBRACK, Kind.RBRACK, Kind.SEMICOLON, Kind.COMMA}));

	private static Set<Kind> comment = new TreeSet<>(Arrays.asList(new Kind[]{Kind.COMMENT, Kind.COMMENT_CONT}));

	private static Set<Kind> strings = new TreeSet<>(Arrays.asList(new Kind[]{Kind.STRING, Kind.STRING_CONT, Kind.CHARLIT}));


	private static void kind_wrap_start(Kind k, XMLStreamWriter sw) throws XMLStreamException {
		if (k==Kind.TEXT) {

		} else if (k==Kind.NUMBER){
			sw.writeStartElement("sht");
			sw.writeAttribute("type" , "nr");
		} else if (k==Kind.PPROC) {
			sw.writeStartElement("sht");
			sw.writeAttribute("type" , "pp");
		} else if (type_kw.contains(k)) {
			sw.writeStartElement("sht");
			sw.writeAttribute("type" , "tk");
		} else if (stmt_kw.contains(k)) {
			sw.writeStartElement("sht");
			sw.writeAttribute("type" , "sk");
		} else if (operatr.contains(k)) {
			sw.writeStartElement("sht");
			sw.writeAttribute("type" , "op");
		} else if (separtr.contains(k)) {
			sw.writeStartElement("sht");
			sw.writeAttribute("type" , "sp");
		} else if (comment.contains(k)) {
			sw.writeStartElement("sht");
			sw.writeAttribute("type" , "cm");
		} else if (strings.contains(k)){
			sw.writeStartElement("sht");
			sw.writeAttribute("type" , "st");
		}
	}

	private static void kind_wrap_end(Kind k, XMLStreamWriter sw) throws XMLStreamException {
		if (k==Kind.TEXT) {

		} else if (k==Kind.NUMBER){
			sw.writeEndElement();
		} else if (k==Kind.PPROC) {
			sw.writeEndElement();
		} else if (type_kw.contains(k)) {
			sw.writeEndElement();
		} else if (stmt_kw.contains(k)) {
			sw.writeEndElement();
		} else if (operatr.contains(k)) {
			sw.writeEndElement();
		} else if (separtr.contains(k)) {
			sw.writeEndElement();
		} else if (comment.contains(k)) {
			sw.writeEndElement();
		} else if (strings.contains(k)){
			sw.writeEndElement();
		}
	}

	@Override
	public void run() {
		Log.printf("Starting:%s.\n", from.getPath());

		// do nothing if the input file cannot be found
		if (from!=null && from.exists()) {
			try {
				// open the output stream
				XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
				XMLStreamWriter sw = outFactory.createXMLStreamWriter(new FileOutputStream(to));
				// write the preamble
				sw.writeStartDocument();
				sw.writeCharacters("\n");
				sw.writeProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../file.xsl\"");
				sw.writeCharacters("\n");
				sw.writeStartElement("file");
				sw.writeCharacters("\n");


				// for each line write a ln tag with line number, nodes, warnings, and reachability
				int lineNr = 1;
				try (BufferedReader br = new BufferedReader(new FileReader(from))) {
					CTokens scanner = null;
					scanner = new CTokens(br);
					do {
						sw.writeStartElement("ln");
						sw.writeAttribute("nr" , Integer.toString(lineNr));
						sw.writeAttribute("ns" , setToJsonArray(stats.getLineData(lineNr)));
						sw.writeAttribute("wrn", setToJsonArray(stats.getWarnData(lineNr)));
						sw.writeAttribute("ded", Boolean.toString(stats.isDead(lineNr) && (!stats.isLive(lineNr))));

						CTokens.Symbol s;
						do {
							s = scanner.next_token();
							if (s.k == Kind.EOL || s.k == Kind.EOF)
								break;
							kind_wrap_start(s.k,sw);
							sw.writeCharacters(s.s);
							kind_wrap_end(s.k, sw);
							if (s.k == Kind.STRING_CONT || s.k == Kind.COMMENT_CONT)
								break;
						} while (true);

						sw.writeEndElement();
						sw.writeCharacters("\n");
						lineNr++;

						if (s.k== Kind.EOF)
							break;
					} while (true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}

				// close the stream
				sw.writeEndElement();
				sw.writeEndDocument();
				sw.close();

			} catch (IOException e) {
				e.printStackTrace();

			} catch (XMLStreamException e) {
				e.printStackTrace();
			}

		}
		Log.printf("Finished:%s.\n", from.getPath());
	}

}
