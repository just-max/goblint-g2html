package g2html;

import java.io.*;
import javax.xml.stream.*;

public class XmlResult {
	
	// Parse the complete xml result file
	static public void parse()
	{
		try {
			// Open xml file
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(new FileInputStream(Config.xmlFileName));
			
			// Walk through the xml file
			while ( parser.hasNext() )	{				
				switch ( parser.getEventType() )
				{
				    case XMLStreamConstants.START_ELEMENT:
				    	String elementName = parser.getLocalName();				    	

				    	if (elementName.equals("loc")) Analysis.parseLocNode(parser);
				    	if (elementName.equals("glob")) Globals.parseGlobNode(parser);	
				    	if (elementName.equals("warning")) Warnings.parseWarningNode(parser);
				    	break;
				    	
				    default:
				    	break;
				}
				
				// Next event
				parser.next();			
			}
		}
		catch (XMLStreamException xmle) {
			System.out.println("XML Error: " + xmle.toString());
		}
		catch (FileNotFoundException fnfe) {
			System.out.println("File Error: " + fnfe.toString());
		};
	}
}

