package g2html;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.Set;

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

				isMultilineCommentActive = false;
				isMultilineStringActive = false;

				// for each line write a ln tag with line number, nodes, warnings, and reachability
				int lineNr = 1;
				try (BufferedReader br = new BufferedReader(new FileReader(from))) {
					for (String line; (line = br.readLine()) != null; ) {
						sw.writeStartElement("ln");
						sw.writeAttribute("nr" , Integer.toString(lineNr));
						sw.writeAttribute("ns" , setToJsonArray(stats.getLineData(lineNr)));
						sw.writeAttribute("wrn", setToJsonArray(stats.getWarnData(lineNr)));
						sw.writeAttribute("ded", Boolean.toString(stats.isDead(lineNr)));

						// Disabled syntax highlighting
						if (Config.conf.getSyntaxHighlightingStatus() == "off") {
							sw.writeCharacters(line);
						}
						// Enabled syntax highlighting
						else {
							writeSyntaxHighlightedLinePart(sw,line);
						}

						sw.writeEndElement();
						sw.writeCharacters("\n");
						lineNr++;
					}
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


	// Syntax highlighting code

	private boolean isMultilineCommentActive = false;
	private boolean isMultilineStringActive = false;
	static private String[] keywordList = {"const", "auto", "default", "extern", "typedef", "union", "class", 
					       "register", "struct", "volatile", "enum", "static", "sizeof",
                                               "signed", "unsigned", "int", "long", "float", "double", "void", "char", "short",
					       "return", "continue", "break", "if", "else", "do", "while", "switch", "for", "case", "goto"};

	int uint_min(int a, int b) {
		if (a < 0) return b;
		if (b < 0) return a;
		if (a < b) return a;
		else return b;
	}

	void writeSyntaxHighlightedLinePart(XMLStreamWriter sw, String line)
	{
		try {
			// if active multiline comment, check if it ends
			if (isMultilineCommentActive == true) {
				int endCmtPos = line.indexOf("*/");
				if (endCmtPos == -1) {  // Complete line is comment
					sw.writeStartElement("sht");
					sw.writeAttribute("type", "cmt");
					sw.writeCharacters(line);
					sw.writeEndElement();
					return;
				}
				else {
					sw.writeStartElement("sht");
					sw.writeAttribute("type", "cmt");
					sw.writeCharacters(line.substring(0, endCmtPos+2));
					sw.writeEndElement();
					isMultilineCommentActive = false;
					writeSyntaxHighlightedLinePart(sw, line.substring(endCmtPos+2));
					return;
				}
			}

			// if active multiline string, check if it ends
			if (isMultilineStringActive == true) {
				int endStrPos = line.indexOf("\"");
				if (endStrPos == -1) {
					sw.writeStartElement("sht");
					sw.writeAttribute("type", "str");
					sw.writeCharacters(line);
					sw.writeEndElement();
					if (line.charAt(line.length()-1) != '\\') {
						isMultilineStringActive = false;
					}
				}
				else {	// String is finished
					sw.writeStartElement("sht");
					sw.writeAttribute("type", "str");
					sw.writeCharacters(line.substring(0, endStrPos+1));
					sw.writeEndElement();
					isMultilineStringActive = false;
					writeSyntaxHighlightedLinePart(sw, line.substring(endStrPos+1));
				}
				return;
			}

			// Get first important character
			int firstCommentPos = line.indexOf("//");
			int firstMultilineCommentPos = line.indexOf("/*");
			int firstStringPos = line.indexOf("\"");
			int firstCharPos = line.indexOf("'");
			int firstSharpPos = line.indexOf("#");
			int firstPos = uint_min(firstCommentPos, uint_min(firstMultilineCommentPos, uint_min(firstStringPos, uint_min(firstCharPos, firstSharpPos))));
			int endPos = line.length()-1;

			// Write characters without any syntax highlighting for strings, comments, ...
			if (firstPos == -1) {
				// Find the first keyword in the line
				for (int i = 0; i < keywordList.length; i++) {
					int keywordPos = line.indexOf(keywordList[i]);
					if (keywordPos == -1) continue;
					int keywordLen = keywordList[i].length();

					// No letters or digits before and after the keyword
					if (keywordPos > 0) if (Character.isLetterOrDigit(line.charAt(keywordPos-1)) == true) continue;
					if (line.length() > keywordPos+keywordLen) if (Character.isLetterOrDigit(line.charAt(keywordPos+keywordLen)) == true) continue;

					// Highlight keyword and recursive call to the before and after part
					if (keywordPos > 0) writeSyntaxHighlightedLinePart(sw, line.substring(0, keywordPos));
					sw.writeStartElement("sht");
					sw.writeAttribute("type", "key");
					sw.writeCharacters(line.substring(keywordPos, keywordPos+keywordLen));
					sw.writeEndElement();					
					if (line.length() > keywordPos+keywordLen-1) writeSyntaxHighlightedLinePart(sw, line.substring(keywordPos+keywordLen));

					return;
				}

				sw.writeCharacters(line);
				return;
			}

			// Write characters before syntax characters
			if (firstPos > 0) writeSyntaxHighlightedLinePart(sw, line.substring(0, firstPos));

			// Preprocessor starts
			if (firstPos == firstSharpPos) {
				sw.writeStartElement("sht");
				sw.writeAttribute("type", "pp");
				sw.writeCharacters(line.substring(firstPos));	// All characters following the "#"
				sw.writeEndElement();
				return;
			}

			// Comment starts
			if (firstPos == firstCommentPos) {
				sw.writeStartElement("sht");
				sw.writeAttribute("type", "cmt");
				sw.writeCharacters(line.substring(firstPos));	// All characters following the "//"
				sw.writeEndElement();
				return;
			}

			// Character starts
			if (firstPos == firstCharPos) {
				endPos = line.indexOf("'", firstPos+1);
				if (endPos == -1) {
					sw.writeCharacters(line.substring(firstPos));
					return;
				}
				sw.writeStartElement("sht");
				sw.writeAttribute("type", "chr");
				sw.writeCharacters(line.substring(firstPos, endPos+1));
				sw.writeEndElement();
			}

			// String starts
			if (firstPos == firstStringPos) {
				endPos = line.indexOf("\"", firstPos+1);
				if (endPos == -1) {
					if (line.charAt(line.length()-1) == '\\') {
						sw.writeStartElement("sht");
						sw.writeAttribute("type", "str");
						sw.writeCharacters(line.substring(firstPos));
						sw.writeEndElement();
						isMultilineStringActive = true;
					}
					else sw.writeCharacters(line.substring(firstPos));
					return;
				}
				sw.writeStartElement("sht");
				sw.writeAttribute("type", "str");
				sw.writeCharacters(line.substring(firstPos, endPos+1));
				sw.writeEndElement();
			}

			// Multiline comment
			if (firstPos == firstMultilineCommentPos) {
				endPos = line.indexOf("*/", firstPos+1);
				if (endPos > firstPos) { // Comment ends in this line
					sw.writeStartElement("sht");
					sw.writeAttribute("type", "cmt");
					sw.writeCharacters(line.substring(firstPos, endPos+2));
					sw.writeEndElement();
					endPos++;
				}
				else {	// Comment does not end in this line
					sw.writeStartElement("sht");
					sw.writeAttribute("type", "cmt");
					sw.writeCharacters(line.substring(firstPos));
					sw.writeEndElement();
					isMultilineCommentActive = true;
					return;
				}
			}

			// Parse the string part after the highlighted part
			if (endPos < line.length()) writeSyntaxHighlightedLinePart(sw, line.substring(endPos+1));

		} catch (XMLStreamException e) {
			e.printStackTrace();
  		}
	}

	

}
