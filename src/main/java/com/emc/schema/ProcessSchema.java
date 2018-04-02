package com.emc.schema;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * @Author: Shameera
 *
 *          This is an example for how to iterate XmlSchema
 */
public class ProcessSchema {

	XmlSchema xmlSchema;

	public ProcessSchema(XmlSchema xmlSchema) {
		this.xmlSchema = xmlSchema;
	}

	public void processXmlSchema() {
		Map<QName, XmlSchemaElement> elements = this.xmlSchema.getElements();
		Iterator<XmlSchemaElement> xmlSchemaElementIterator = elements.values().iterator();
		while (xmlSchemaElementIterator.hasNext()) {
			XmlSchemaElement element = xmlSchemaElementIterator.next();
			XmlSchemaType schemaType = element.getSchemaType();
			if (schemaType instanceof XmlSchemaComplexType) {
				processComplexType(element);
			} else if (schemaType instanceof XmlSchemaSimpleType) {
				processSimpleType(element);
			}
		}
	}

	public void processComplexType(XmlSchemaElement xmlSchemaElement) {
		String name = xmlSchemaElement.getName();
		QName qName = xmlSchemaElement.getSchemaTypeName();
		String localPart = "";
		if (qName != null) {
			localPart = qName.getLocalPart();
		}
		if (isArray(xmlSchemaElement)) {
			System.out.println(name + " ---> compelx " + localPart + " type ARRAY");
		} else {
			System.out.println(name + " ---> compelx " + localPart + " type ARRAY");
		}
		XmlSchemaSequence schemaSequence;
		XmlSchemaParticle particle = ((XmlSchemaComplexType) xmlSchemaElement.getSchemaType()).getParticle();
		if (particle instanceof XmlSchemaSequence) {
			schemaSequence = (XmlSchemaSequence) particle;
			List<XmlSchemaSequenceMember> schemaObjectCollection = schemaSequence.getItems();
			for (XmlSchemaSequenceMember element : schemaObjectCollection) {
				XmlSchemaElement innerElement = ((XmlSchemaElement) element);
				XmlSchemaType innerEleType = innerElement.getSchemaType();
				if (innerEleType instanceof XmlSchemaComplexType) {
					processComplexType(innerElement);
				} else if (innerEleType instanceof XmlSchemaSimpleType) {
					processSimpleType(innerElement);
				}

			}
		}
	}

	public void processSimpleType(XmlSchemaElement element) {
		QName qName = element.getSchemaTypeName();
		if (isArray(element)) {
			System.out.println(element.getName() + "  ----> simple " + qName.getLocalPart() + " type ARRAY");
		} else {
			System.out.println(element.getName() + "  ----> simple " + qName.getLocalPart() + " type");
		}
	}

	private boolean isArray(XmlSchemaElement element) {
		return !(element.getMaxOccurs() == 1);
	}
}