package g2html;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Logger;

public class Loc {
	private final static Logger LOGGER = Logger.getLogger(Loc.class.getName());

	static public void parseLocNode(XMLStreamReader parser, Result res, ResultStats resultStats) throws FileNotFoundException, XMLStreamException {
		String file = parser.getAttributeValue("", "file");
		String line = parser.getAttributeValue("", "line");
		String id   = parser.getAttributeValue("", "id");

		String shortFile = (new File(file)).getName();

		FileStats fileStats = resultStats.getStats(shortFile);
		fileStats.addLineData(id,Integer.valueOf(line));
		fileStats.setCFile(file);

		File xmlOut = res.getNodeFile(id);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlOutStream = factory.createXMLStreamWriter(new FileOutputStream(xmlOut));
		xmlOutStream.writeStartDocument();
		xmlOutStream.writeProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../node.xsl\"");
		XMLStreamTagCopier.copyTag(parser,xmlOutStream);
		xmlOutStream.close();
	}
}
