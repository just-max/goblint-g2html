package g2html;

import javax.xml.stream.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Logger;

public class Warning {
	private static int counter = 1;

	static public void parseWarningNode(XMLStreamReader parser, Result res, ResultStats resultStats) throws XMLStreamException, FileNotFoundException {
		String id = "warn"+counter;
		counter++;

		File xmlOut = res.getWarningFile(id);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlOutStream = factory.createXMLStreamWriter(new FileOutputStream(xmlOut));
		xmlOutStream.writeStartDocument();
		xmlOutStream.writeCharacters("\n");
		xmlOutStream.writeProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../warn.xsl\"");
		xmlOutStream.writeCharacters("\n");
		XMLStreamCC readcc = new XMLStreamCC(parser,xmlOutStream);

		while(readcc.hasNext()){
			if (readcc.getEventType()== XMLStreamConstants.END_ELEMENT &&
							readcc.getLocalName()=="warning"){
				break;
			}

			if (readcc.getEventType()== XMLStreamConstants.START_ELEMENT &&
							readcc.getLocalName()=="text"){
				String file = readcc.getAttributeValue("","file");
				String line = readcc.getAttributeValue("","line");
				String shortFile = new File(file).getName();
				resultStats.getStats(shortFile).addWarning(id,Integer.valueOf(line));
			}
			readcc.next();
		}
		xmlOutStream.writeEndDocument();
		xmlOutStream.close();
	}
}
