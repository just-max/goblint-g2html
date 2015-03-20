package g2html;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

final public class XmlResult {
	// Parse the complete xml result file
	static public ResultStats parse(Result res) throws IOException {
		ResultStats resultStats = new ResultStats();
		try {
			FileWriter lostAndFound = new FileWriter(new File(new File(Config.conf.getResultDir(), Config.nodesSubdir),"lost.txt"));

			// Open xml file
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(new BufferedInputStream(new FileInputStream(Config.conf.getFile())));

		  // prepare the globals file with the globs root node
			XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter  globalStream = outFactory.createXMLStreamWriter(new BufferedOutputStream(new FileOutputStream(res.getGlobalFile())));

			// preamble for the globals file
			globalStream.writeStartDocument();
			globalStream.writeCharacters("\n");
			globalStream.writeProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../globals.xsl\"");
			globalStream.writeCharacters("\n");
			globalStream.writeStartElement("globs");

			// Walk through the xml file
			while (parser.hasNext()) {
				switch (parser.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						String elementName = parser.getLocalName();

						// use the specialized parser when possible
						if (elementName.equals("file"))    Structure.parseFileNode(parser, resultStats);
						if (elementName.equals("call"))    Loc.parseLocNode(parser,res,resultStats,lostAndFound);
						if (elementName.equals("glob"))    Glob.parseGlobNode(parser, globalStream);
						if (elementName.equals("warning")) Warning.parseWarningNode(parser, res, resultStats);
						break;

					default:
						break;
				}

				// Next event
				parser.next();
			}

			// close the globals document
			globalStream.writeEndElement();
			globalStream.writeEndDocument();
			globalStream.close();
			lostAndFound.close();

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

