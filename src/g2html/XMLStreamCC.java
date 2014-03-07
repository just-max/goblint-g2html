package g2html;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.StreamReaderDelegate;

// Useful for copying portions of xml files to other files
public class XMLStreamCC extends StreamReaderDelegate {
	private final XMLStreamReader reader;
	private final XMLStreamWriter writer;

	// creates a XMLStreamReader that copies all read elements into a writer
	XMLStreamCC(XMLStreamReader reader, XMLStreamWriter writer)
					throws XMLStreamException
	{
		super(reader);
		this.reader = reader;
		this.writer = writer;
		copyTag();
	}

	public static void copyAttributes(XMLStreamReader reader, XMLStreamWriter writer)
					throws XMLStreamException {
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

	public void copyTag() throws XMLStreamException {
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
				writer.writeEndDocument();
				break;
			case XMLStreamConstants.END_ELEMENT:
				writer.writeEndElement();
				break;
			case XMLStreamConstants.NAMESPACE:
				writer.writeNamespace(reader.getPrefix(), reader.getNamespaceURI());
				break;
			case XMLStreamConstants.START_ELEMENT:
				writer.writeStartElement(reader.getLocalName());
				copyAttributes(reader,writer);
				break;
			case XMLStreamConstants.START_DOCUMENT:
				writer.writeStartDocument();
				break;
			default:
		}
	}

	@Override
	public int next() throws XMLStreamException {
		int n = super.next();
		copyTag();
		return n;
	}
}
