package g2html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;

public class Codelines {
	
	// Writes the codelines to a html result file
	static void writeHtmlFile(HtmlResultFile resultFile) {		
		try {
			BufferedWriter bwHtml = resultFile.bwHtml;
			
			bwHtml.write("<div id=\"mainwidget_codelines\">\r\n");
			bwHtml.write("<!-- Codelines -->\r\n");
			
			// Open source file
			BufferedReader br = new BufferedReader(new FileReader(resultFile.sourceFilename));
			boolean even = true;
			int line = 1;
			String codeLine = "";
			while (br.ready()) {
				codeLine = br.readLine();
				if (codeLine == null) codeLine = "";
				
				// Replace < and > tags with html codes
				codeLine = codeLine.replace("<","&lt;");
				codeLine = codeLine.replace(">","&gt;");
				
				// Keyword highlighting *ToDo*
				codeLine = codeLine.replaceAll("int","<span class=\"cpp_datatype\">int</span>");
				codeLine = codeLine.replaceAll("void","<span class=\"cpp_datatype\">void</span>");
				codeLine = codeLine.replaceAll("return","<span class=\"cpp_keyword\">return</span>");				
				
				// Preprocessor highlighting
				if (codeLine.indexOf("#") >= 0) codeLine = "<span class=\"cpp_preprocessor\">"+codeLine+"</b>";
				
				// Comment highlighting
				int pos = codeLine.indexOf("//");
				if (pos >= 0) {
					String lineBuffer = codeLine.substring(0);					
					codeLine = lineBuffer.substring(0,pos);					
					codeLine = codeLine + "<span class=\"cpp_comment\">" + lineBuffer.substring(pos,lineBuffer.length()) + "</span>";
				}
				
				// Write line to html file
				String linelink = "jsShow_analysis("+line+")";
				String prelinetext = "";
				if (resultFile.getAnalysisBlock(line).isLineAnalyzed(line) == false) prelinetext = line+":";
				else prelinetext = "<a href=\"javascript:" + linelink + ";\">" + line + "</a>:";
				bwHtml.write("<div id=\"codeline" + line + "\" class=\"" + ((line % 2 == 0)?"cl_even":"cl_odd") + "\"><pre>" + prelinetext + " " + codeLine + "</pre></div>\r\n");
				even = !even;
				line++;
			}
			br.close();
			
			bwHtml.write("</div>\r\n");
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}		
	}
	
	static void writeCssFile(BufferedWriter bwCss) {
		try {	
			bwCss.write("#mainwidget_codelines pre { \r\n");
			bwCss.write("    padding: 0px;\r\n");
			bwCss.write("    margin: 0px;\r\n");
			bwCss.write("}\r\n");
			
			bwCss.write("#mainwidget_codelines a { \r\n");
			bwCss.write("    color: #000000;\r\n");
			bwCss.write("    font-weight: bold;\r\n");
			bwCss.write("    text-decoration: none;\r\n");
			bwCss.write("}\r\n");
			
			bwCss.write("#mainwidget_codelines a:hover { \r\n");
			bwCss.write("    text-decoration: underline;\r\n");
			bwCss.write("}\r\n");
			
			bwCss.write(".cpp_keyword { \r\n");
			bwCss.write("    color: blue;\r\n");
			bwCss.write("}\r\n");
			
			bwCss.write(".cpp_datatype { \r\n");
			bwCss.write("    color: blue;\r\n");
			bwCss.write("}\r\n");
			
			bwCss.write(".cpp_preprocessor { \r\n");
			bwCss.write("    color: purple;\r\n");
			bwCss.write("}\r\n");
			
			bwCss.write(".cpp_comment { \r\n");
			bwCss.write("    color: green;\r\n");
			bwCss.write("}\r\n");
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
	}
	
	static void writeJsFile(BufferedWriter bwJs) {
	}

}
