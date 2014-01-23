package g2html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Result {
	
	// Html result files
	static List<HtmlResultFile> resultFiles = new ArrayList<HtmlResultFile>();
	
	// Get or create a result entry
	static HtmlResultFile getHtmlResultFile(String filename)
	{
		// Find entry
		for (int i = 0; i < resultFiles.size(); i++) {
			HtmlResultFile resultFile = resultFiles.get(i);
			if (resultFile.sourceFilename.equals(filename) == true) {
				return resultFile;
			}
		}
		
		// Create new entry
		HtmlResultFile resultFile = new HtmlResultFile(filename);
		resultFiles.add(resultFile);
		return resultFile;
	}
	
	// Write and complete html result files
	static void writeHtmlFiles()
	{
		for (int i = 0; i < resultFiles.size(); i++) {
			HtmlResultFile resultFile = resultFiles.get(i);
			resultFile.write();
		}
	}
	
	// Write css file
	static void writeCssFile()
	{
		try {
			// Create and open css file
			File fileCss = new File("resultfiles\\style.css");
			new File(fileCss.getParent()).mkdirs();
			if (!fileCss.exists()) fileCss.createNewFile();
			FileWriter fwCss = new FileWriter(fileCss.getAbsoluteFile());
			BufferedWriter bwCss = new BufferedWriter(fwCss);
			
			// Write beginning of css file
			bwCss.write("body {\r\n");
			bwCss.write("  padding: 0px;\r\n");
			bwCss.write("  margin: 0px;\r\n");
			bwCss.write("}\r\n\r\n");
			bwCss.write(".tabbar {\r\n");
			bwCss.write("  background-color: #D8D8D8;\r\n");
			bwCss.write("}\r\n\r\n");
			bwCss.write(".tabbar ul {\r\n");
			bwCss.write("  padding: 3px;\r\n");
			bwCss.write("  margin: 0px;\r\n");
			bwCss.write("}\r\n\r\n");
			bwCss.write(".tabbar ul li {\r\n");
			bwCss.write("  list-style-type: none;\r\n");
			bwCss.write("  display: inline;\r\n");
			bwCss.write("  padding-top: 1px;\r\n");
			bwCss.write("  margin: 1px;\r\n");
			bwCss.write("  background-color: #F8F8F8;\r\n");
			bwCss.write("  border: 1px solid #B0B0B0;\r\n");
			bwCss.write("  font-weight: bold;\r\n");
			
			bwCss.write("}\r\n\r\n");
			bwCss.write(".tabbar li a {\r\n");
			bwCss.write("  text-decoration: none;\r\n");
			bwCss.write("}\r\n\r\n");
			bwCss.write(".invisible {\r\n");
			bwCss.write("  display: none;\r\n");
			bwCss.write("}\r\n\r\n");
			bwCss.write(".cl_even {\r\n");
			bwCss.write("  background-color: #ECECEC;\r\n");
			bwCss.write("}\r\n\r\n");
			bwCss.write(".cl_odd {\r\n");
			bwCss.write("  background-color: #F8F8F8;\r\n");
			bwCss.write("}\r\n\r\n");
			
			bwCss.write("#leftwidget { \r\n");
			bwCss.write("    border: 1px solid #909090;\r\n");
			bwCss.write("    background-color: #F8F8F8;\r\n");
			bwCss.write("    position: fixed;\r\n");
			bwCss.write("}\r\n");
			
			bwCss.write("#mainwidget { \r\n");
			bwCss.write("    border: 1px solid #909090;\r\n");
			bwCss.write("}\r\n");

			
			Analysis.writeCssFile(bwCss);
			Globals.writeCssFile(bwCss);
			Codelines.writeCssFile(bwCss);
			Warnings.writeCssFile(bwCss);
			
			// Close files
			bwCss.close();
			fwCss.close();
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
	}
	
	// Write javascript file
	static void writeJsFile()
	{
		try {
			// Create and open javascript file
			File fileJs = new File("resultfiles\\script.js");
			new File(fileJs.getParent()).mkdirs();
			if (!fileJs.exists()) fileJs.createNewFile();
			FileWriter fwJs = new FileWriter(fileJs.getAbsoluteFile());
			BufferedWriter bwJs = new BufferedWriter(fwJs);
			
			// ============= Basic javascript code =============
			// Set resize function
			bwJs.write("window.addEventListener('resize', jsResizeWindow);\r\n\r\n");
			bwJs.write("window.addEventListener('load', jsResizeWindow);\r\n\r\n");
			
			bwJs.write("var isAnalysisFrame = false;");
			bwJs.write("var shortFilename = '';");

			//  Resize window function
			bwJs.write("function jsResizeWindow() {\r\n");
			bwJs.write("  var leftWidget = document.getElementById('leftwidget');\r\n");
			bwJs.write("  var mainWidget = document.getElementById('mainwidget');\r\n");
			bwJs.write("  if (leftWidget == null) return;\r\n");
			//bwJs.write("  leftWidget.style.position = 'absolute';\r\n");
			bwJs.write("  leftWidget.style.left = '10px';\r\n");
			bwJs.write("  leftWidget.style.top = '35px';\r\n");
			bwJs.write("  leftWidget.style.width = '200px';\r\n");
			//bwJs.write("  leftWidget.style.height = document.body.clientHeight - 240;\r\n");
			bwJs.write("  mainWidget.style.position = 'absolute';\r\n");
			bwJs.write("  mainWidget.style.left = '220px';\r\n");
			bwJs.write("  mainWidget.style.top = '35px';\r\n");
			bwJs.write("  mainWidget.style.width = document.body.clientWidth - 240;\r\n");			
			bwJs.write("}\r\n\r\n");
			
			bwJs.write("function jsShowTab_leftwidget(tabIndex) {\r\n");
			bwJs.write("  if (document.getElementById('leftwidget_analysis') == null) return;\r\n");
			bwJs.write("  document.getElementById('leftwidget_analysis').style.display = (tabIndex==0)?'':'none';\r\n");
			bwJs.write("  document.getElementById('leftwidget_declarations').style.display = (tabIndex==1)?'':'none';\r\n");
			bwJs.write("  document.getElementById('leftwidget_globals').style.display = (tabIndex==2)?'':'none';\r\n");
			bwJs.write("}\r\n\r\n");
			
			bwJs.write("function jsShowTab_mainwidget(tabIndex) {\r\n");
			bwJs.write("  if (document.getElementById('mainwidget_codelines') == null) return;\r\n");
			bwJs.write("  document.getElementById('mainwidget_codelines').style.display = (tabIndex==0)?'':'none';\r\n");
			bwJs.write("  document.getElementById('mainwidget_warnings').style.display = (tabIndex==1)?'':'none';\r\n");
			bwJs.write("}\r\n\r\n");	
			
			bwJs.write("function jsInitWidgets() {\r\n");
			bwJs.write("  jsShowTab_leftwidget(0);\r\n");
			bwJs.write("  jsShowTab_mainwidget(0);\r\n");
			bwJs.write("}\r\n\r\n");
			
			bwJs.write("function jsInitFile(filename) {\r\n");
			bwJs.write("  shortFilename = filename;\r\n");
			bwJs.write("}\r\n\r\n");	
			
			bwJs.write("window.addEventListener('load', jsInitWidgets);");
			
			Analysis.writeJsFile(bwJs);
			Globals.writeJsFile(bwJs);
			Codelines.writeJsFile(bwJs);
			Warnings.writeJsFile(bwJs);
			
			// Close files
			bwJs.close();
			fwJs.close();
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
	}
}
