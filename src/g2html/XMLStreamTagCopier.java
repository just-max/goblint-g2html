package g2html;

import javax.xml.stream.*;
import java.util.logging.Logger;

public class XMLStreamTagCopier {
	private final static Logger LOGGER = Logger.getLogger(XMLStreamTagCopier.class.getName());

	public static void copyAttributes(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException { 
		for(int i=0; i<reader.getAttributeCount(); i++){
			String namespace = reader.getAttributeNamespace(i);
			if (namespace==null)
				writer.writeAttribute(
						reader.getAttributeLocalName(i), 
						reader.getAttributeValue(i)
						);
			else
				writer.writeAttribute(
						reader.getAttributePrefix(i), 
						namespace, 
						reader.getAttributeLocalName(i), 
						reader.getAttributeValue(i)
						);
		}		
	}
	
	public static void copyTag(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
		String name = reader.getLocalName();
		int counter = 0;
		do {
			switch(reader.getEventType()){
			case XMLStreamConstants.ATTRIBUTE:
				copyAttributes(reader,writer);
				break;
			case XMLStreamConstants.CDATA:
				writer.writeCData(reader.getText());
				break;
			case XMLStreamConstants.CHARACTERS:
				writer.writeCharacters(reader.getText());
				break;
			case XMLStreamConstants.COMMENT:
				writer.writeComment(reader.getText());
				break;
			case XMLStreamConstants.END_DOCUMENT:
				reader.close();
				writer.close();
				break;
			case XMLStreamConstants.END_ELEMENT:
				if (reader.getLocalName().equals(name))
					counter--;
				writer.writeEndElement();
				break;
			case XMLStreamConstants.NAMESPACE:
				writer.writeNamespace(reader.getPrefix(), reader.getNamespaceURI());
				break;
			case XMLStreamConstants.START_ELEMENT: 
				if (reader.getLocalName().equals(name))
					counter++;
				writer.writeStartElement(reader.getLocalName());
				copyAttributes(reader,writer);
				break;
			case XMLStreamConstants.START_DOCUMENT:
				writer.writeStartDocument();
				break;
			default:	
			}
	    	reader.next();
		} while (counter!=0 && reader.hasNext());
	}
}
