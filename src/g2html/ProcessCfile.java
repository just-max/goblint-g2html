package g2html;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.Set;

public class ProcessCfile implements Runnable {
	private File to;
	private FileStats stats;
	private File from;

	// allocate a new transformation task
	ProcessCfile(File from, File to, FileStats stats) {
		super();
		this.from = from;
		this.to = to;
		this.stats = stats;
	}

	// write the set as an JSON string array
	static String setToJsonArray(Set<String> s){
		if (s==null) return "[]";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		boolean first = true;
		for (String e : s){
			if (!first)
				stringBuilder.append(", ");
			first = false;
			stringBuilder.append("\"");
			stringBuilder.append(e);
			stringBuilder.append("\"");
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	
	@Override
	public void run() {
		Log.printf("Starting:%s.\n", from.getPath());

		// do nothing if the input file cannot be found
		if (from!=null && from.exists()) {
			try {
				// open the output stream
				XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
				XMLStreamWriter sw = outFactory.createXMLStreamWriter(new FileOutputStream(to));
				// write the preamble
				sw.writeStartDocument();
  			sw.writeCharacters("\n");
 	  		sw.writeProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../file.xsl\"");
				sw.writeCharacters("\n");
				sw.writeStartElement("file");
				sw.writeCharacters("\n");

				// for each line write a ln tag with line number, nodes, warnings, and reachability
				int lineNr = 1;
				try (BufferedReader br = new BufferedReader(new FileReader(from))) {
					for (String line; (line = br.readLine()) != null; ) {
						sw.writeStartElement("ln");
						sw.writeAttribute("nr" , Integer.toString(lineNr));
						sw.writeAttribute("ns" , setToJsonArray(stats.getLineData(lineNr)));
						sw.writeAttribute("wrn", setToJsonArray(stats.getWarnData(lineNr)));
						sw.writeAttribute("ded", Boolean.toString(stats.isDead(lineNr)));
						sw.writeCharacters(line);
						sw.writeEndElement();
						sw.writeCharacters("\n");
						lineNr++;
					}
				}

				// close the stream
				sw.writeEndElement();
				sw.writeEndDocument();
				sw.close();

			} catch (IOException e) {
				e.printStackTrace();

			} catch (XMLStreamException e) {
				e.printStackTrace();
  		}

		}
		Log.printf("Finished:%s.\n", from.getPath());
	}

}
