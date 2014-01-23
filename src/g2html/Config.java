package g2html;

public class Config {
	public static String xmlFileName = "";	
	
	public static boolean analysisExtraFile = true;
	public static int analysisCountPerFile = 100;
	
	public static boolean globalsExtraFile = false;
	
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
}
