package g2html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;

public class GoblintServer extends NanoHTTPD {
	public GoblintServer(int port) {
		super(port);
	}
	static Pattern xmlPattern = Pattern.compile("^.*\\.xml$");
	static Pattern htmlPattern = Pattern.compile("^.*\\.html$");
	static Pattern jsPattern = Pattern.compile("^.*\\.js$");
	static Pattern cssPattern = Pattern.compile("^.*\\.css$");
	static Pattern xslPattern = Pattern.compile("^.*\\.xsl$");
	static Pattern svgPattern = Pattern.compile("^.*\\.svg$");
	static Pattern woffPattern = Pattern.compile("^.*\\.woff$");

	private static String guessMime(String fname) {
		if (xmlPattern.matcher(fname).find()) {
			return "application/xml";
		} else if (htmlPattern.matcher(fname).find()) {
			return MIME_HTML;
		} else if (jsPattern.matcher(fname).find()) {
			return "application/javascript";
		} else if (cssPattern.matcher(fname).find()) {
			return "text/css";
		} else if (xslPattern.matcher(fname).find()) {
			return "text/xsl";
		}	else if (svgPattern.matcher(fname).find()) {
			return "image/svg+xml";
		} else if (woffPattern.matcher(fname).find()) {
			return "application/font-woff";
		} else {
				return MIME_PLAINTEXT;
		}
	}

	static Pattern instPattern = Pattern.compile("[^/]*/([0-9]+)/([^\\?]*)(\\?.*)?");

	static Pattern cfgGenPattern = Pattern.compile("^cfgs/([^/]+)/(.*)\\.svg$");
	static Pattern sourceGenPattern = Pattern.compile("^files/(.*)\\.xml$");

	ExecutorService executorService = Executors.newFixedThreadPool(2);

	private int next = 0;
	private Map<Integer,GoblintInstance> instances = new TreeMap<>();

	@Override public Response serve(IHTTPSession session) {
		Map<String, String> files = new HashMap<>();
		Method method = session.getMethod();
		if (Method.PUT.equals(method) || Method.POST.equals(method)) {
			try {
				session.parseBody(files);
			} catch (IOException ioe) {
				return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
			} catch (ResponseException re) {
				return new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
			}
		}

		Map<String,String> parms = session.getParms();

//		String postBody = session.getQueryParameterString();
//		System.out.printf("Post Body: %s\n",postBody);

		System.out.printf("Params: ");
		for(Map.Entry<String, String> e : parms.entrySet()) {
			System.out.printf("%s:=%s ",e.getKey(),e.getValue());
		}
		System.out.println();


//		System.out.printf("Header: ");
//		for(Map.Entry<String, String> e : session.getHeaders()  .entrySet()) {
//			System.out.printf("%s:=%s ",e.getKey(),e.getValue());
//		}
//		System.out.println();

		if (session.getUri().equals("/")) {
			StringBuilder ms = new StringBuilder();
			ms.append("<html><body>");
			ms.append("<form action=\"");
				ms.append(next);
					ms.append("/index.xml\" method=\"post\">\n");
			ms.append("  Parameters: <input type=\"text\" name=\"param\" /><br>\n");
			ms.append("  <input type=\"submit\" value=\"Submit\" />\n");
			ms.append("</form>");
			ms.append("</body></html>");
			next ++;
			return new Response(ms.toString());
		}

		Matcher m = instPattern.matcher(session.getUri());
		if (m.find()) {
			int instNr = Integer.parseInt(m.group(1));
			String page = m.group(2);
//			System.out.printf("URI = %s\n",session.getUri());
//			System.out.printf("instNr = %s\npage = %s\n",m.group(1), m.group(2));
			if (!instances.containsKey(instNr)) {
				instances.put(instNr, new GoblintInstance(instNr, parms.get("param")));
			}
			GoblintInstance inst = instances.get(instNr);


			if (parms.containsKey("goblint")) {
				System.out.printf("Got special command: %s\n",parms.get("goblint"));
				switch (parms.get("goblint")) {
					case "exit":
						inst.goblintExit();
						break;
					case "next":
						inst.goblintNext();
						break;
					case "continue":
						inst.goblintContinue();
						break;
					case "stop":
						inst.goblintStop();
						break;
					default:
				}
			}

			InputStream stream = Main.class.getResourceAsStream("/resources/"+page);
			if (stream != null) {
				return new Response(Response.Status.OK,guessMime(page),stream);
			}

			File file = new File(Integer.toString(instNr),page);

			if (!file.exists()) {
				Matcher cfgMatch = cfgGenPattern.matcher(page);
				Matcher srcMatch = sourceGenPattern.matcher(page);
				if (cfgMatch.find()) {
					String fromFileName = instNr+"/cfgs/"+cfgMatch.group(1)+"/"+cfgMatch.group(2)+".dot";
					File fromFile = new File(fromFileName);
					System.out.printf("Generating:\n\tfrom:%s\n\tto:%s\n",fromFileName,page);
					executorService.execute(new ProcessDotFileToSvg(fromFile,file));
				} else if (srcMatch.find()) {
					String fromFileName = srcMatch.group(1).replaceAll("%2F","/") ;
					File fromFile = new File(fromFileName);
					new File(file.getParent()).mkdirs();
					System.out.printf("Generating:\n\tfrom:%s\n\tto:%s\n",fromFileName,page);
					executorService.execute(new ProcessCfile(fromFile,file,new FileStats()));
				} else {
					return new Response(Response.Status.NOT_FOUND,MIME_PLAINTEXT, "404");
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignored) {}
			}

			try {
				stream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (stream != null) {
				return new Response(Response.Status.OK,guessMime(page),stream);
			}

		}
		return new Response(Response.Status.INTERNAL_ERROR,MIME_PLAINTEXT,"Error!");
	}

	private Response waitMsg() {
		return new Response("wait...");
	}

	private Response error() {
		return new Response(Response.Status.INTERNAL_ERROR,MIME_PLAINTEXT, "Error!");
	}

}
