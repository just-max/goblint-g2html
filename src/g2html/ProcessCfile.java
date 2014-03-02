package g2html;

import java.io.*;
import java.util.logging.Logger;

public class ProcessCfile extends Thread {
	private final static Logger LOGGER = Logger.getLogger(ProcessCfile.class.getName());

	private static String pre =
					"<html>\n<head>\n  <link href='http://fonts.googleapis.com/css?family=Alegreya+Sans:400,500' rel='stylesheet' type='text/css' /> \n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n  <link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\"></link>\n</head>\n<body>\n  <div class=\"source-block\">";
	private static String post =
					"</div></body></html>";

	private static String ln1 = "<div id=\"line";
	private static String ln2 = "\" class=\"source-line\"><span class=\"source-line-nr\">";
	private static String ln3 = "</span><span class=\"source-line-warn\"></span><span class=\"source-line-data\">";
	private static String ln4 = "</span></div>";

	private File to;
	private File from;

	ProcessCfile(File from, File to) {
		super();
		this.from = from;
		this.to = to;
	}

	public static void encodeHTMLAppend(String s, FileWriter out) throws IOException {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>') {
				out.append("&#" + (int) c + ";");
			} else {
				out.append(c);
			}
		}
	}

	@Override
	public void run() {
		super.run();
		if (from!=null && from.exists()) {
			LOGGER.info("processing '"+from.getAbsolutePath()+"' to '"+to.getAbsolutePath()+"'");
			try {
				FileWriter fw = new FileWriter(to);
				fw.append(pre);
				int lineNr = 1;
				try (BufferedReader br = new BufferedReader(new FileReader(from))) {
					for (String line; (line = br.readLine()) != null; ) {
						fw.append(ln1);
						fw.append("" + lineNr);
						fw.append(ln2);
						fw.append("" + lineNr);
						fw.append(ln3);
						encodeHTMLAppend(line, fw);
						fw.append(ln4);
						lineNr++;
					}
				}
				fw.append(post);
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
