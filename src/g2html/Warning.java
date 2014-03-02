package g2html;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Logger;

public class Warning {
	private final static Logger LOGGER = Logger.getLogger(Warning.class.getName());

	private static int counter = 1;

	static public void parseWarningNode(XMLStreamReader parser, Result res) throws XMLStreamException, FileNotFoundException {
		String id = "warn"+counter;
		counter++;

		File xmlOut = res.getWarningFile(id);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlOutStream = factory.createXMLStreamWriter(new FileOutputStream(xmlOut));
		XMLStreamTagCopier.copyTag(parser,xmlOutStream);
		xmlOutStream.close();
	}
}
