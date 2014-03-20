package g2html;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Structure {
	// read the structure from the xml and store it in the databases
	public static void parseFileNode(XMLStreamReader parser, ResultStats resultStats)
					throws XMLStreamException {

		// get basic information
		String name = parser.getAttributeValue("", "path").replaceAll(File.separator,"%2F");;
		String fun = "";

		// parse the file and
		while(parser.hasNext()){

			// store functions for the file, put function name in fun
			if (parser.getEventType() == XMLStreamConstants.START_ELEMENT &&
							parser.getLocalName().equals("function")){
				fun = parser.getAttributeValue("","name");
				resultStats.getStats(name).addFunction(fun);
			}

			// store nodes for each function
			if (parser.getEventType() == XMLStreamConstants.START_ELEMENT &&
							parser.getLocalName().equals("node")){
				String node = parser.getAttributeValue("","name");
				resultStats.getStats(name).addNodeToFun(node,fun);
			}

			// finish if the file node closes
			if (parser.getEventType() == XMLStreamConstants.END_ELEMENT &&
							parser.getLocalName().equals("file")){
				break;
			}

			parser.next();
		}
	}
}
