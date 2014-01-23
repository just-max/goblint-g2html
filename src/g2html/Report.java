package g2html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Report {
	
	static File fileHtml;
	static FileWriter fwHtml;
	static BufferedWriter bwHtml;
	
	// Write html report file
	static void write()
	{
		try {
			// Open or create report file
			fileHtml = new File("report.html");			
			//new File(fileHtml.getParent()).mkdirs();
			if (!fileHtml.exists()) fileHtml.createNewFile();
			fwHtml = new FileWriter(fileHtml.getAbsoluteFile());
			bwHtml = new BufferedWriter(fwHtml);
			
			// Write beginning of the html file
			bwHtml.write(
					"<html>"
					+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"resultfiles\\style.css\">"
					+ "</head>"
					+ "<body>"
					+ "  <h1>Goblint Analysis Report</h1>");
			
			// List files
			bwHtml.write("<h2>Files</h2><ul>");
			for (HtmlResultFile resultFile : Result.resultFiles) {
				bwHtml.write("<li><a href=\"" + resultFile.htmlFilename + "\">" + resultFile.sourceFilename + "</a></li>");
			}			
			bwHtml.write("</ul>\r\n");
			
			// List warnings
			bwHtml.write("<h2>Warnings</h2><ul>");
			for (int i = 0; i < Warnings.warnings.size(); i++) {
				bwHtml.write("<li><b>" + Warnings.warnings.get(i).name + "</b></li><ul>");
				for (int g = 0; g < Warnings.warnings.get(i).lines.size(); g++ ) {
					bwHtml.write("<li>" + Warnings.warnings.get(i).lines.get(g).filename + " @ line " + Warnings.warnings.get(i).lines.get(g).line + " : " + Warnings.warnings.get(i).lines.get(g).text + "</li>");
				}
				bwHtml.write("</ul>");
			}			
			bwHtml.write("</ul>\r\n");
			
			// Finish html file
			bwHtml.write("</body></html>\r\n");
			
			// Close file
			bwHtml.close();
			fwHtml.close();			
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
	}
}
