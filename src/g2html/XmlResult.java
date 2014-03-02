package g2html;

import javax.xml.stream.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class XmlResult {
	private final static Logger LOGGER = Logger.getLogger(XmlResult.class.getName());

	// Parse the complete xml result file
	static public ResultStats parse(Result res) throws IOException {
		ResultStats resultStats = new ResultStats();
		try {
			// Open xml file
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(new FileInputStream(Config.xmlFileName));

			XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter  globalStream = outFactory.createXMLStreamWriter(new FileOutputStream(res.getGlobalFile()));
			globalStream.writeStartDocument();
			globalStream.writeProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../globals.xsl\"");
			globalStream.writeStartElement("globs");

			// Walk through the xml file
			while (parser.hasNext()) {
				switch (parser.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						String elementName = parser.getLocalName();

						if (elementName.equals("file"))    Structure.parseFileNode(parser, resultStats);
						if (elementName.equals("loc"))     Loc.parseLocNode(parser,res,resultStats);
						if (elementName.equals("glob"))    Glob.parseGlobNode(parser, globalStream);
						if (elementName.equals("warning")) Warning.parseWarningNode(parser, res);
						break;

					default:
						break;
				}

				// Next event
				parser.next();
			}
			globalStream.writeEndElement();
			globalStream.writeEndDocument();
			globalStream.close();

		} catch (XMLStreamException xmle) {
			System.out.println("XML Error: " + xmle.toString());
			System.exit(255);
		} catch (IOException fnfe) {
			System.out.println("File Error: " + fnfe.toString());
			System.exit(255);
		}
		return resultStats;
	}
}

