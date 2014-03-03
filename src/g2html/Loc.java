package g2html;

import javax.xml.stream.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Logger;

public class Loc {
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
		xmlOutStream.writeCharacters("\n");
		xmlOutStream.writeProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../node.xsl\"");
		xmlOutStream.writeCharacters("\n");
		XMLStreamCC readcc = new XMLStreamCC(parser,xmlOutStream);

		boolean pathFound = false;
		boolean notDead = false;
		while(readcc.hasNext()){
			if (readcc.getEventType()==XMLStreamConstants.END_ELEMENT &&
							readcc.getLocalName()=="loc"){
				break;
			} else if (readcc.getEventType()==XMLStreamConstants.START_ELEMENT &&
							readcc.getLocalName()=="path"){
				pathFound = true;
			} else if (readcc.getEventType()==XMLStreamConstants.START_ELEMENT &&
							readcc.getLocalName()=="analysis" && pathFound){
				notDead = true;
			}
				readcc.next();
		}

		if (!notDead)
			resultStats.getStats(shortFile).addDead(Integer.parseInt(line));

		xmlOutStream.close();
	}
}
