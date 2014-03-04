package g2html;

import java.io.File;

public class Config {
	public static String xmlFileName       = "";
	public static String resultDir         = "result";
	public static String cfgSubdir         = "cfgs";
	public static String nodesSubdir       = "nodes";
	public static String warningsSubdir    = "warn";
	public static String sourceFilesSubdir = "files";
	
	public static String[] preparedFiles = {"style.css", "node.xsl", "warn.xsl", "frame.html",
										"nothing.html", "script.js", "svg-pan-zoom.js", "jquery-2.1.0.min.js",
										"globals.xsl", "report.xsl", "AlegreyaSans400.woff", "AlegreyaSans500.woff",
										"iframeResizer.contentWindow.min.js", "jquery.iframeResizer.min.js"};
	
	// Parse arguments
	public static void load(String[] args)
	{
		int currentArg = 0;
		int argMode = 0;
		while (currentArg < args.length) {
			if (argMode == 0) {
				if (xmlFileName.length() > 0) {
					System.out.println("Cannot process more than one xml file!");
					System.exit(2);
				}
				else xmlFileName = args[currentArg];
			}
			currentArg++;
		}
		
		// Check xml file argument
		if (xmlFileName.length() == 0) {
			System.out.println("g2html <xml file> [options]");
			System.exit(1);
		}
	}

	public static File getFunDotFile(String file, String fun) {
		File f = new File(new File(new File(new File("."),"cfgs"),file),fun+".dot");
		return f;
	}
}
