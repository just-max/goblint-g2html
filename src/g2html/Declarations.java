package g2html;

import java.io.IOException;

public class Declarations {
	
	static void writeHtmlFile(HtmlResultFile resultFile) {
		try {
			resultFile.bwHtml.write("<div id=\"leftwidget_declarations\">\r\n");
			resultFile.bwHtml.write("Declarations! /* ToDo */\r\n");
			resultFile.bwHtml.write("</div>\r\n");
		}
		catch (IOException e) {			
			System.out.println(e.toString());
		}
	}
}
