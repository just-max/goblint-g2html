package g2html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

//Class for each analysis (with its globals as content)
class GlobalAnalysisBlock {
	String analysisName;
	boolean completed;
	
	// Used when globals are saved directly into html result files
	String stringContent;
	
	GlobalAnalysisBlock(String analysisName)
	{			
		this.analysisName = analysisName;
		this.completed = false;
		
		if (Config.globalsExtraFile) {
		  // *ToDo*
		}
		else stringContent = "<div><span class=\"toggle\">"+analysisName+"</span><div class=\"entrydircontent\">\r\n";
	}
	
	void add(String globalText)
	{
		if (Config.globalsExtraFile) {
		  // *ToDo*
		}
		else stringContent += globalText;
	}
	
	void writeHtmlFile(HtmlResultFile resultFile)
	{
		// Complete the analysis globals block with adding </div> tags
		if (completed == false) {
			if (Config.globalsExtraFile) {
			  // *ToDo*
			}
			else stringContent = stringContent += "</div></div>";
			completed = true;
		}
		
		// Write globals
		if (Config.globalsExtraFile) {
			// *ToDo*
		}
		else {
			try {
				resultFile.bwHtml.write(stringContent);
			}
			catch (IOException e) {			
				System.out.println(e.toString());
			}	
		}
		
	}		
}

public class Globals {

	// List with global analysis blocks
	static List<GlobalAnalysisBlock> globals = new ArrayList<GlobalAnalysisBlock>();
	
	static GlobalAnalysisBlock getGlobalAnalysisBlock(String analysisName)
	{
		// Find entry
		for (int i = 0; i < globals.size(); i++) {
			GlobalAnalysisBlock globalBlock = globals.get(i);
			if (globalBlock.analysisName.equals(analysisName) == true) {
				return globalBlock;
			}
		}
		
		// Create new entry
		GlobalAnalysisBlock globalBlock = new GlobalAnalysisBlock(analysisName);
		globals.add(globalBlock);
		return globalBlock;		
	}
	
	static void writeHtmlFile(HtmlResultFile resultFile) {
		try {
			resultFile.bwHtml.write("<div id=\"leftwidget_globals\">\r\n");
			for (int i = 0; i < globals.size(); i++) {
				globals.get(i).writeHtmlFile(resultFile);
			}
			resultFile.bwHtml.write("</div>\r\n");
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}		
	}
	
	static void writeCssFile(BufferedWriter bwCss) {
	}
	
	static void writeJsFile(BufferedWriter bwJs) {
	}

	static public void parseGlobNode(XMLStreamReader parser) {
		try	{
			boolean isData = false;
			boolean isKey = false;
			String keyname = "";
			String analysisName = "";
			
			parser.next();			
			while ( parser.hasNext() ) {				
				switch ( parser.getEventType() )
				{
				case XMLStreamConstants.START_ELEMENT:
					String nodeName = parser.getLocalName();
					
					if (nodeName.equals("key")) {
						isKey = true;
					}
					if (nodeName.equals("data")) {
						isData = true;
					}
					if (nodeName.equals("analysis")) {
						analysisName = parser.getAttributeValue(null, "name");
					}					
					break;
					
				case XMLStreamConstants.CHARACTERS:
					if (!parser.isWhiteSpace()) {						
						String text = parser.getText();
						text = text.replaceAll("\r", " ");
						text = text.replaceAll("\n", " ");
						text = text.trim();		
						
						if (isKey == true) {
							keyname = text;
							isKey = false;
						}						
						if (isData == true) {
							GlobalAnalysisBlock global = getGlobalAnalysisBlock(analysisName);
							global.add(keyname + " = " + text + "<br/>");
							isData = false;
						}
					}
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					nodeName = parser.getLocalName();
					if (nodeName.equals("glob")) return;					
					break;
				}
				
				parser.next();
			}
					
		}
		catch (XMLStreamException xmle) {
			System.out.println("XML Error: " + xmle.toString());
		}
	}
}
