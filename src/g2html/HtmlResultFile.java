package g2html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// Manage separated analysis files
class AnalysisBlock {
	File fileAnalysis;
	FileWriter fwAnalysis;
	BufferedWriter bwAnalysis;
	
	String analysisContent = "";
	
	int firstLine;
	int lines;
	
	boolean lineAnalyzed[];
	
	AnalysisBlock(HtmlResultFile resultFile, int firstLine, int lines) {
		this.firstLine = firstLine;
		this.lines = lines;
		this.analysisContent = "";
		this.lineAnalyzed = new boolean[Config.analysisCountPerFile];

		// Create and open analysis html file
		try {
			fileAnalysis = new File("resultfiles"+File.separator+(new File(resultFile.sourceFilename).getName())+"_files"+File.separator+"analysis"+(firstLine/Config.analysisCountPerFile)+".html");
			new File(fileAnalysis.getParent()).mkdirs();
			if (!fileAnalysis.exists()) fileAnalysis.createNewFile();
			fwAnalysis = new FileWriter(fileAnalysis.getAbsoluteFile());
			bwAnalysis = new BufferedWriter(fwAnalysis);
			
			bwAnalysis.write("<html>\r\n");
			bwAnalysis.write("  <head>\r\n");
			bwAnalysis.write("    <link rel=\"stylesheet\" href=\"../style.css\" type=\"text/css\"></link>\r\n");
			bwAnalysis.write("    <script type=\"text/javascript\" src=\"../script.js\"></script>\r\n");
			bwAnalysis.write("  </head>\r\n");
			bwAnalysis.write("  <body id=\"analysisfilebody\" onload=\"OnAnalysisFileLoaded();\">\r\n");
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
	}
	
	public boolean isLineAnalyzed(int line)
	{
		if (Config.analysisExtraFile == true) {
			return lineAnalyzed[line % Config.analysisCountPerFile];
		}
		
		else {
			if (lineAnalyzed.length <= line) {
				int start = lineAnalyzed.length;
				lineAnalyzed = Arrays.copyOf(lineAnalyzed, line + 100);
				for (int i = start; i < lineAnalyzed.length; i++) lineAnalyzed[i] = false;
			}
			return lineAnalyzed[line];
		}
	}
	
	public void setLineAnalyzed(int line)
	{
		if (Config.analysisExtraFile == true) {
			lineAnalyzed[line % Config.analysisCountPerFile] = true;
		}
		
		else {
			if (lineAnalyzed.length <= line) {
				int start = lineAnalyzed.length;
				lineAnalyzed = Arrays.copyOf(lineAnalyzed, line + 100);
				for (int i = start; i < lineAnalyzed.length; i++) lineAnalyzed[i] = false;
			}
			lineAnalyzed[line] = true;
		}
	}
	
	void addText(String analysisText)
	{
		if (Config.analysisExtraFile == true) {
			try {
				bwAnalysis.write(analysisText);
			}
			catch (IOException e) {			
				System.out.println(e.toString());
			}
		}
		else {
			analysisContent += analysisText;
		}
	}
	
	void finish() {
		try {
			bwAnalysis.write("  </body>");
			bwAnalysis.write("</html>");
			bwAnalysis.close();
			fwAnalysis.close();
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
	}
}

public class HtmlResultFile {	
	String sourceFilename;
	String htmlFilename;
	
	File fileHtml;
	FileWriter fwHtml;
	BufferedWriter bwHtml;
	
	ArrayList<AnalysisBlock> analysisBlocks = new ArrayList<AnalysisBlock>();
	
	ArrayList<String> warnings = new ArrayList<String>();

	HtmlResultFile(String sourceFilename)
	{
		this.sourceFilename = sourceFilename;
		this.htmlFilename = "resultfiles" + File.separator + (new File(sourceFilename).getName())+".html";

		// Create and open html file
		try {
			System.out.println(htmlFilename);
			fileHtml = new File(htmlFilename);
			if (fileHtml == null)
				System.out.println("WHY?");
			else
				System.out.println(fileHtml.getParent());
			new File(fileHtml.getParent()).mkdirs();
			if (!fileHtml.exists()) fileHtml.createNewFile();
			fwHtml = new FileWriter(fileHtml.getAbsoluteFile());
			bwHtml = new BufferedWriter(fwHtml);
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
		
		// Create analysis block
		AnalysisBlock analysisBlock;
		if (Config.analysisExtraFile) analysisBlock = new AnalysisBlock(this,0,Config.analysisCountPerFile);
		else analysisBlock = new AnalysisBlock(this,0,0);
		analysisBlocks.add(analysisBlock);
	}
	
	AnalysisBlock getAnalysisBlock(int line)
	{
		if (Config.analysisExtraFile) {
			int blockIndex = line / Config.analysisCountPerFile;
			while (blockIndex >= analysisBlocks.size()) {
				AnalysisBlock block = new AnalysisBlock(this,Config.analysisCountPerFile*analysisBlocks.size(), Config.analysisCountPerFile);
				analysisBlocks.add(block);
			}
			return analysisBlocks.get(blockIndex);
		}
		else return analysisBlocks.get(0);
	}
	
	public void addAnalysisText(int line, String analysisText)
	{
		getAnalysisBlock(line).addText(analysisText);
	}
	
	void write()
	{
		try {
			// Write beginning of html file
			bwHtml.write("<html>\r\n<head>\r\n");
			bwHtml.write("  <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">\r\n");
			bwHtml.write("  <script type=\"text/javascript\" src=\"script.js\"></script>\r\n");
			bwHtml.write("</head>\r\n");
			bwHtml.write("<body onload=\"jsInitFile('"+(new File(sourceFilename).getName())+"');\">\r\n");
			
			bwHtml.write("<div style=\"z-index:1; position: fixed; width: 100%; border-bottom: 1px solid #808080;\">\r\n");
			bwHtml.write("<div style=\"border-bottom: 1px solid #000000; color: #5060C0; background-color: #F0F0F0; font-size: 16px; padding: 2px; font-weight: bold;\">\r\n");
			bwHtml.write("Filename: "+sourceFilename+"\r\n");
			bwHtml.write("</div>\r\n");
			bwHtml.write("</div>\r\n");
			
			// ============= Left widget code =============
			bwHtml.write("<div id=\"leftwidget\">\r\n");
			
			bwHtml.write("<div id=\"leftwidget_tabbar\" class=\"tabbar\"><ul>\r\n");	
			bwHtml.write("<li><a href=\"javascript:jsShowTab_leftwidget(0);\">Analysis</a></li>\r\n");
			bwHtml.write("<li><a href=\"javascript:jsShowTab_leftwidget(1);\">Decl</a></li>\r\n");
			bwHtml.write("<li><a href=\"javascript:jsShowTab_leftwidget(2);\">Globals</a></li>\r\n");
			bwHtml.write("</ul></div>\r\n");
			
			Analysis.writeHtmlFile(this);
			Declarations.writeHtmlFile(this);
			Globals.writeHtmlFile(this);
			
			bwHtml.write("</div>\r\n");
			
			// ============= Main widget code =============
			bwHtml.write("<div id=\"mainwidget\">\r\n");
			bwHtml.write("<div id=\"leftwidget_tabbar\" class=\"tabbar\"><ul>\r\n");	
			bwHtml.write("<li><a href=\"javascript:jsShowTab_mainwidget(0);\">Code</a></li>\r\n");
			bwHtml.write("<li><a href=\"javascript:jsShowTab_mainwidget(1);\">Warnings</a></li>\r\n");
			bwHtml.write("</ul></div>\r\n");
			
			Codelines.writeHtmlFile(this);
			Warnings.writeHtmlFile(this);
			
			bwHtml.write("</div>\r\n");
			
			// Write ending of html file
			bwHtml.write("</html>\r\n");	
			
			// Close files
			bwHtml.close();
			fwHtml.close();	
			
			// Close extra analysis files
			if (Config.analysisExtraFile == true) {
				for (AnalysisBlock block : analysisBlocks) {
					block.finish();
				}
			}
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
	}
}
