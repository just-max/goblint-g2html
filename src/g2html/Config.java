package g2html;

import java.io.File;
import java.util.List;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.CommandLineInterface;
import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.Unparsed;

public class Config {
	// default subdirectory names in the result
	public static String cfgSubdir         = "cfgs";
	public static String nodesSubdir       = "nodes";
	public static String warningsSubdir    = "warn";
	public static String sourceFilesSubdir = "files";

	// JewelCli definitions for the configuration
	@CommandLineInterface(application = "java -jar g2html.jar")
	public interface G2HtmlArgs {

		@Option (shortName = "n",longName = "num-threads", defaultValue = "2", exactly = 1,description = "Number of worker threads.")
		Integer getNumThreads();

		@Option (shortName = "d",longName = "dot-path", defaultValue = "dot", exactly = 1,description = "Path to the dot binary.")
		String getDotPath();

		@Option (longName = "dot-alternative-path", defaultValue = "sfdp", exactly = 1,description = "Path to an alternative dot binary.")
		String getAlternativeDotPath();

		@Option (longName = "dot-timeout", defaultValue = "2000", exactly = 1,description = "Timeout for graph processing (0 means no timeout).")
		Integer getDotTimeout();

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

		@Unparsed(description = "input xml file", defaultToNull = false,exactly = 1,name = "<xml file>")
		String getFile();
	}

	// cuurent configuration loaded by JewelCli
	static G2HtmlArgs conf ;

	// files to be copied from the jar file
	public static String[] preparedFiles = {"style.css", "node.xsl", "warn.xsl", "frame.html",
					"nothing.html", "script.js", "svg-pan-zoom.js", "jquery-2.1.0.min.js",
					"globals.xsl", "report.xsl", "AlegreyaSans400.woff", "AlegreyaSans500.woff",
					"iframeResizer.contentWindow.min.js", "jquery.iframeResizer.min.js", "file.xsl",
					"UbuntuMonoRegular.woff", "UbuntuMonoBold.woff"};

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
	public static File getFunDotFile(String path, String fun) {
		path = path.replaceAll("/", "%2F");
		File f = new File(new File(new File(conf.getCfgDir()), path), fun + ".dot");
		return f;
	}
}
