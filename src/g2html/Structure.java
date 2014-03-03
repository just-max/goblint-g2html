package g2html;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class Structure {
	public static void parseFileNode(XMLStreamReader parser, ResultStats resultStats) throws XMLStreamException {
		String name = parser.getAttributeValue("","name");
		String fun = "";
		while(parser.hasNext()){
			if (parser.getEventType() == XMLStreamConstants.START_ELEMENT &&
							parser.getLocalName().equals("function")){
				fun = parser.getAttributeValue("","name");
				resultStats.getStats(name).addFunction(fun);
			}

			if (parser.getEventType() == XMLStreamConstants.START_ELEMENT &&
							parser.getLocalName().equals("node")){
				String node = parser.getAttributeValue("","name");
				resultStats.getStats(name).addNodeToFun(node,fun);
			}

			if (parser.getEventType() == XMLStreamConstants.END_ELEMENT &&
							parser.getLocalName().equals("file")){
				break;
			}

			parser.next();
		}
	}
}
