package g2html;

import javax.xml.stream.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Warning {
	private static int counter = 1;

	// parse a wwarning node
	static public void parseWarningNode(XMLStreamReader parser, Result res, ResultStats resultStats)
					throws XMLStreamException, FileNotFoundException {

		// create a new id for the new warning
		String id = "warn"+counter;
		counter++;

		// open the output stream
		File xmlOut = res.getWarningFile(id);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlOutStream = factory.createXMLStreamWriter(new FileOutputStream(xmlOut));

		// write the preamble
		xmlOutStream.writeStartDocument();
		xmlOutStream.writeCharacters("\n");
		xmlOutStream.writeProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../warn.xsl\"");
		xmlOutStream.writeCharacters("\n");

		// copy form the input
		XMLStreamCC readcc = new XMLStreamCC(parser,xmlOutStream);
		while(readcc.hasNext()){

			// until the warning tag closes
			if (readcc.getEventType()== XMLStreamConstants.END_ELEMENT &&
							readcc.getLocalName()=="warning"){
				break;
			}

			// for each text element, store the id of the warning with the text location
			if (readcc.getEventType()== XMLStreamConstants.START_ELEMENT &&
							readcc.getLocalName()=="text"){
				String path = readcc.getAttributeValue("","file");
				String line = readcc.getAttributeValue("","line");
				String shortFile = path.replaceAll(Pattern.quote(File.separator),"%2F");;

				resultStats.getStats(shortFile).addWarning(id,Integer.valueOf(line));
			}
			readcc.next();
		}

		// close the document
		xmlOutStream.writeEndDocument();
		xmlOutStream.close();
	}
}
