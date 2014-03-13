package g2html;

import com.lexicalscope.jewel.cli.*;

import java.io.File;
import java.util.List;

public class Config {
	// default subdirectory names in the result
	public static String cfgSubdir         = "cfgs";
	public static String nodesSubdir       = "nodes";
	public static String warningsSubdir    = "warn";
	public static String sourceFilesSubdir = "files";

	// JewelCli definitions for the configuration
	@CommandLineInterface(application = "java -jar g2html.jar")
	public interface G2HtmlArgs {
		@Option (shortName = "d",longName = "dot-path", defaultValue = "dot", exactly = 1,description = "Path to the dot binary.")
		String getDotPath();

		@Option (shortName = "c",longName = "cfg-dir", defaultValue = "cfgs", exactly = 1, description = "Path to 'exp.cfgdot' output directory.")
		String getCfgDir();

		@Option (shortName = "o",longName = "result-dir", defaultValue = "result", exactly = 1, description = "Name of the result directory.")
		String getResultDir();

		@Option (shortName = "I",longName = "include", defaultValue = {"."}, description = "Add to the include path.")
		List<String> getIncludes();

		@Option (shortName = "h",longName = "help", helpRequest = true, description = "Prints this help message.")
		boolean needHelp();

		@Option (shortName = "v", description = "Verbose output.")
		boolean verbose();

		@Option (shortName = "sh", defaultValue = "on", exactly = 1, description = "Syntax highlighting on/off.")
		String getSyntaxHighlightingStatus();

		@Unparsed(description = "input xml file", defaultToNull = false,exactly = 1,name = "<xml file>")
		String getFile();
	}

	// cuurent configuration loaded by JewelCli
	static G2HtmlArgs conf ;

	// files to be copied from the jar file
	public static String[] preparedFiles = {"style.css", "node.xsl", "warn.xsl", "frame.html",
					"nothing.html", "script.js", "svg-pan-zoom.js", "jquery-2.1.0.min.js",
					"globals.xsl", "report.xsl", "AlegreyaSans400.woff", "AlegreyaSans500.woff",
					"iframeResizer.contentWindow.min.js", "jquery.iframeResizer.min.js", "file.xsl"};

	// Parse arguments
	public static void load(String[] args) {
		try {
			Cli<G2HtmlArgs> cli = CliFactory.createCli(G2HtmlArgs.class);
			conf =  cli.parseArguments(args);
			if (!ProcessDotFileToSvg.testDotPath(conf.getDotPath()))
				System.out.printf("Warning: dot not found on '%s'.\n", conf.getDotPath());
			if (!new File(conf.getCfgDir()).exists())
				System.out.printf("Warning: cfg directory '%s' does not exist.\n", conf.getCfgDir());
			if (!new File(conf.getFile()).exists()){
				System.out.printf("Error: input data file '%s' not found.\n", conf.getFile());
				System.exit(255);
			}
		} catch (ArgumentValidationException e) {
			System.out.println(e.getMessage());
			System.exit(255);
		}
	}

	// returns the input dot file for a given C file, and function
	public static File getFunDotFile(String file, String fun) {
		File f = new File(new File(new File(conf.getCfgDir()), file), fun + ".dot");
		return f;
	}
}
