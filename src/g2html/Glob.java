package g2html;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.logging.Logger;

public class Glob {
	private final static Logger LOGGER = Logger.getLogger(Glob.class.getName());

	static public void parseGlobNode(XMLStreamReader parser, XMLStreamWriter globals) throws XMLStreamException {
		XMLStreamTagCopier.copyTag(parser,globals);
	}
}
