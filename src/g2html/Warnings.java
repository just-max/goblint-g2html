package g2html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

class WarningLine {
	String filename;
	int line;
	String text;
	
	WarningLine(String filename, int line, String text) {
		this.filename = filename;
		this.line = line;
		this.text = text;
	}
}

class WarningGroup {
	String name;
	public ArrayList<WarningLine> lines = new ArrayList<WarningLine>();
	
	WarningGroup(String name) {
		this.name = name;
	}
	
	void addLine(WarningLine line) {
		lines.add(line);
	}
}

public class Warnings {
	
	public static ArrayList<WarningGroup> warnings = new ArrayList<WarningGroup>();
	
	static void writeHtmlFile(HtmlResultFile resultFile) {
		try {
			resultFile.bwHtml.write("<div id=\"mainwidget_warnings\">\r\n");
			
			// *ToDo* : Filter the filename
			for (int i = 0; i < Warnings.warnings.size(); i++) {
				resultFile.bwHtml.write("<li><b>" + Warnings.warnings.get(i).name + "</b></li><ul>");
				for (int g = 0; g < Warnings.warnings.get(i).lines.size(); g++ ) {
					resultFile.bwHtml.write("<li>" + Warnings.warnings.get(i).lines.get(g).filename + " @ line " + Warnings.warnings.get(i).lines.get(g).line + " : " + Warnings.warnings.get(i).lines.get(g).text + "</li>");
				}
				resultFile.bwHtml.write("</ul>");
			}

			resultFile.bwHtml.write("</div>\r\n");
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}		
	}
	
	static public void parseWarningNode(XMLStreamReader parser) {
		try {
			WarningGroup group = null;
			String filename = "";
			int line = -1;			
			
			// Parse child nodes (until </warning>)
			parser.next();
			while ( parser.hasNext() ) {				
				switch ( parser.getEventType() )
				{
				case XMLStreamConstants.START_ELEMENT:
					String nodeName = parser.getLocalName();

					//  group and text node
					if (nodeName.equals("group")) {
						String groupName = parser.getAttributeValue(null, "name");
						group = new WarningGroup(groupName); 
						warnings.add(group);
					}
					if (nodeName.equals("text")) {
						filename = parser.getAttributeValue(null, "file");
						line = Integer.parseInt(parser.getAttributeValue(null, "line"));
					}					
					break;

			    case XMLStreamConstants.END_ELEMENT:	
			    	nodeName = parser.getLocalName();
			    	
			    	// Return when </warning> is reached
			    	if (nodeName.equals("warning")) {	
			    		return;
			    	}
			    	else if (nodeName.equals("group")) {
			    		group = null;
					}
			    	else if (nodeName.equals("text")) {
			    		filename = "";
			    		line = -1;
					}			    	
			    	break;
			    	
			    case XMLStreamConstants.CHARACTERS:
			    	if (!parser.isWhiteSpace()) {						
						String text = parser.getText();
						if (group != null) group.addLine(new WarningLine(filename, line, text));
			    	}
					break;

			    default:
			    	break;
				}
				
				// Next event
				parser.next();	
			}		

		}
		catch (XMLStreamException xmle) {
			System.out.println("XML Error: " + xmle.toString());
		}
	}
	
	static void writeCssFile(BufferedWriter bwCss) {	
	}
	
	static void writeJsFile(BufferedWriter bwJs) {
	}
}
