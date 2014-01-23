package g2html;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.xml.stream.*;

public class Analysis {
	
	static void writeHtmlFile(HtmlResultFile resultFile) {
		try {
			BufferedWriter bwHtml = resultFile.bwHtml;
			bwHtml.write("<div id=\"leftwidget_analysis\">\r\n");
			
			// Default text
			bwHtml.write("<div id=\"analysis_0\" >\r\n");
			bwHtml.write("No analysis selected!\r\n");
			bwHtml.write("</div>\r\n");
			
			// Write analysis content into the html result file
			if (Config.analysisExtraFile == false) {
				for (AnalysisBlock analysisBlock : resultFile.analysisBlocks) {
					bwHtml.write(analysisBlock.analysisContent);
				}
			}
			else {
				bwHtml.write("<iframe id=\"analysisiframe\" style=\"display: none;\" class=\"iframebox\">\r\n");
				bwHtml.write("</iframe>\r\n");
			}
			
			// Close tag			
			bwHtml.write("</div>\r\n");		
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
	}
	
	// Parse a <value> node
	static public void parseValueNode(HtmlResultFile resultFile, int line, XMLStreamReader parser, String key) {
		try {
			boolean isKey = false;
			boolean isData = false;
			String nextKeyname = "";
			
			parser.next();			
			while ( parser.hasNext() ) {				
				switch ( parser.getEventType() )
				{
				case XMLStreamConstants.START_ELEMENT:
					String nodeName = parser.getLocalName();
					
					if (nodeName.equals("map")) {
						if (key != null) {
							resultFile.addAnalysisText(line, "<div><span class=\"toggle\">"+key+"</span><div class=\"entrydircontent\">\r\n");
						}
					}
					
					if (nodeName.equals("data")) {	
						isData = true;
					}
					
					if (nodeName.equals("key")) {	
						isKey = true;
					}
					
					if (nodeName.equals("value")) {	
						parseValueNode(resultFile, line, parser,nextKeyname);
						nextKeyname = null;
					}
					
					break;
					
				case XMLStreamConstants.CHARACTERS:					
					if (!parser.isWhiteSpace()) {						
						String text = parser.getText();
						text = text.replaceAll("\r", " ");
						text = text.replaceAll("\n", " ");
						text = text.trim();		
						
						if (isKey == true) {
							nextKeyname = text;
							isKey = false;
						}
						
						if (isData == true) {
							resultFile.addAnalysisText(line, key + " = " + text + "<br/>");
							nextKeyname = "";
							isData = false;
						}
					}
					break;
					
				case XMLStreamConstants.END_ELEMENT:
					nodeName = parser.getLocalName();
					
					if (nodeName.equals("map")) {
						if (key != null) resultFile.addAnalysisText(line, "</div></div>\r\n");
					}

					if (nodeName.equals("value")) return;					
					break;
				}
				
				parser.next();
			}
					
		}
		catch (XMLStreamException xmle) {
			System.out.println("XML Error: " + xmle.toString());
		}
	}
	
	
	static public void parseLocNode(XMLStreamReader parser) {
		try {
			String filename = parser.getAttributeValue(null, "file");
			
			// Get the html result file
			HtmlResultFile resultFile = Result.getHtmlResultFile(filename);
			
			// Check line
			int line = Integer.parseInt(parser.getAttributeValue(null, "line"));
			AnalysisBlock analysisBlock = resultFile.getAnalysisBlock(line);
			analysisBlock.setLineAnalyzed(line);
			
			// Write the beginning of the analysis box
			resultFile.addAnalysisText(line, "<div id=\"analysis_" + line + "\" class=\"analysisbox\">\r\n");
			
			// Parse child nodes (until </loc>)
			parser.next();			
			while ( parser.hasNext() ) {				
				switch ( parser.getEventType() )
				{
				case XMLStreamConstants.START_ELEMENT:
					String nodeName = parser.getLocalName();

					// Context, path, analysis and value node
					if (nodeName.equals("context") || nodeName.equals("path")) {
						resultFile.addAnalysisText(line, "<div><span class=\"toggle\">"+nodeName+"</span><div class=\"entrydircontent\">\r\n");
					}
					if (nodeName.equals("analysis")) {
						resultFile.addAnalysisText(line, "<div><span class=\"toggle\">Analysis: "+parser.getAttributeValue(null, "name")+"</span><div class=\"entrydircontent\">\r\n");
					}					
					if (nodeName.equals("value")) {
						parseValueNode(resultFile, line, parser,null);
					}					
					break;
					
				case XMLStreamConstants.CHARACTERS:
					break;
					
			    case XMLStreamConstants.END_ELEMENT:	
			    	nodeName = parser.getLocalName();
			    	
			    	// Return when </loc> is reached
			    	if (nodeName.equals("loc")) {	
			    		resultFile.addAnalysisText(line, "</div>");
			    		return;
			    	}
			    	
			    	// Context and path node
			    	if (nodeName.equals("context") || nodeName.equals("path") || nodeName.equals("analysis")) {
						resultFile.addAnalysisText(line, "</div></div>\r\n");
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
		try {
			bwCss.write(".toggle {\r\n");
			bwCss.write("  cursor: pointer;\r\n");
			bwCss.write("  font-weight: bold;\r\n");
			bwCss.write("}\r\n\r\n");
			
			bwCss.write(".entrydircontent {\r\n");
			bwCss.write("  margin-left: 15px;\r\n");
			bwCss.write("}\r\n\r\n");	
			
			bwCss.write(".iframebox {\r\n");
			bwCss.write("  overflow: scroll;\r\n");
			bwCss.write("  border: 0px;\r\n");
			bwCss.write("  width: 100%;\r\n");
			bwCss.write("  height: 90%;\r\n");
			bwCss.write("}\r\n\r\n");	
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}		
	}
	
	static void writeJsFile(BufferedWriter bwJs) {
		try {
			bwJs.write("function jsHideAnalysisBoxes() {\r\n");
			bwJs.write("  var els = document.getElementsByClassName('analysisbox');\r\n");
			bwJs.write("  for (var i=0; i < els.length; i++) {");
			bwJs.write("    els[i].style.display = 'none';");
			bwJs.write("  }");
			bwJs.write("}\r\n\r\n");
			
			bwJs.write("var selectedLine = 0;\r\n\r\n");
			
			if (Config.analysisExtraFile == true) {
				bwJs.write("var loadedPage = -1;\r\n\r\n");
				
				bwJs.write("function jsShow_analysis(line) {\r\n");
				bwJs.write("  var requestedPage = ( line - (line % "+Config.analysisCountPerFile+") ) / "+Config.analysisCountPerFile+";\r\n\r\n");
				bwJs.write("  var iframe = document.getElementById('analysisiframe');");
				bwJs.write("  var d = document.getElementById('analysis_0');\r\n");
				bwJs.write("  if (d != null) d.style.display = 'none';\r\n");
				bwJs.write("  if (loadedPage != requestedPage) {");
				bwJs.write("    iframe.src = shortFilename+'_files/analysis'+requestedPage+'.html';");
				bwJs.write("    iframe.onload = function() {");
				bwJs.write("      iframe.style.display = '';");
				bwJs.write("      iframe.contentWindow.postMessage(selectedLine+':'+line,'*');");
				bwJs.write("      loadedPage = requestedPage;");
				bwJs.write("      selectedLine = line;");
				bwJs.write("    }");
				bwJs.write("  }");
				bwJs.write("  else {");
				bwJs.write("    iframe.contentWindow.postMessage(selectedLine+':'+line,'*');");
				bwJs.write("    selectedLine = line;");
				bwJs.write("  }");				
				bwJs.write("}\r\n\r\n");
				
				bwJs.write("function OnAnalysisFileLoaded() {\r\n");
				bwJs.write("  isAnalysisFrame = true;");
				bwJs.write("  handleResponse = function(e) {");				
				bwJs.write("    var lines = e.data.split(':');");
				bwJs.write("    var el = document.getElementById('analysis_' + lines[0]);");
				bwJs.write("    if (el != null) el.style.display = 'none';");
				bwJs.write("    el = document.getElementById('analysis_' + lines[1]);");
				bwJs.write("    if (el != null) el.style.display = '';");
				bwJs.write("  }\r\n");
				bwJs.write("  window.addEventListener('message', handleResponse, false);");
				bwJs.write("}\r\n\r\n");
			}
			else {
				bwJs.write("function jsShow_analysis(line) {\r\n");
				bwJs.write("  jsHighlightLine(line, selectedLine);\r\n");
				bwJs.write("  var d = document.getElementById('analysis_'+line);\r\n");
				bwJs.write("  if ((d != null) && (line != selectedLine)) {");
				bwJs.write("    d.style.display = '';\r\n");
				bwJs.write("    d = document.getElementById('analysis_'+selectedLine);\r\n");
				bwJs.write("    if (d != null) { ");
				bwJs.write("      d.style.display = 'none';");
				bwJs.write("    }");
				bwJs.write("    selectedLine = line;\r\n");			
				bwJs.write("  }");
				bwJs.write("}\r\n\r\n");
			}
			
			bwJs.write("function jsHighlightLine(newline, oldline) {");
			bwJs.write("  document.getElementById('codeline'+newline).style.background = '#FFFFD8';\r\n");
			bwJs.write("  if (oldline > 0) document.getElementById('codeline'+oldline).style.background = ((selectedLine % 2) == 0)?'#ECECEC':'#F8F8F8';\r\n");
			bwJs.write("}\r\n\r\n");
			
			bwJs.write("function init_toggle(e) {");
			bwJs.write("  e.firstChild.nodeValue = '+'+e.firstChild.nodeValue;");
			bwJs.write("  e.onmousedown = function ch(t) { ");
			bwJs.write("    e.firstChild.nodeValue = '-' + e.firstChild.nodeValue.substr(1, e.firstChild.nodeValue.length) ;");
			bwJs.write("  };");
			bwJs.write("  e.onmouseup = function ch(t) {changeContentVisibility(e);};");
			bwJs.write("}");
			
			bwJs.write("function changeContentVisibility(e) {");
			bwJs.write("  if (e.parentNode.children[1].style.display == 'none') {");
			bwJs.write("    e.parentNode.children[1].style.display = '';");
			bwJs.write("    e.firstChild.nodeValue = '+' + e.firstChild.nodeValue.substr(1, e.firstChild.nodeValue.length) ;");
			bwJs.write("  }");
			bwJs.write("  else {");
			bwJs.write("    e.parentNode.children[1].style.display = 'none';");
			bwJs.write("    e.firstChild.nodeValue = '-' + e.firstChild.nodeValue.substr(1, e.firstChild.nodeValue.length);");
			bwJs.write("  }");
			bwJs.write("}");
			
			bwJs.write("function init_toggle_all() {");
			bwJs.write("  var els = document.getElementsByClassName('toggle');");
			bwJs.write("  for (var i=0; i < els.length; i++) {");
			bwJs.write("    init_toggle(els[i]);");
			bwJs.write("  }");
			bwJs.write("  jsHideAnalysisBoxes();");
			bwJs.write("}");

			bwJs.write("window.addEventListener('load', init_toggle_all);");
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
	}
}
