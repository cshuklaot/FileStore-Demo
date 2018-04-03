package com.emc.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeContent;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * @Author: Chandresh
 *
 *          class to walk through schema file and generate JSON object.
 */
public class ProcessSchema {

	XmlSchema xmlSchema;

	public ProcessSchema(XmlSchema xmlSchema) {
		this.xmlSchema = xmlSchema;
	}

	public List<InputDataModel> processXmlSchema() {
		List<InputDataModel> inputModel = new ArrayList<>();
		Map<QName, XmlSchemaElement> elements = this.xmlSchema.getElements();
		Iterator<XmlSchemaElement> xmlSchemaElementIterator = elements.values().iterator();
		while (xmlSchemaElementIterator.hasNext()) {
			XmlSchemaElement element = xmlSchemaElementIterator.next();
			XmlSchemaType schemaType = element.getSchemaType();
			InputDataModel model = null;
			if (schemaType instanceof XmlSchemaComplexType) {
				model = processComplexType(element);

			} else if (schemaType instanceof XmlSchemaSimpleType) {
				model = processSimpleType(element);
			}
			inputModel.add(model);
		}
		System.out.println();
		return inputModel;
	}

	public InputDataModel processComplexType(XmlSchemaElement element) {
		InputDataModel model = new InputDataModel();
		model.name = element.getName();

		QName qName = element.getSchemaTypeName();
		model.type = qName != null ? qName.getLocalPart() : null;
		XmlSchemaSequence schemaSequence;
		XmlSchemaParticle particle = ((XmlSchemaComplexType) element.getSchemaType()).getParticle();
		if (particle instanceof XmlSchemaSequence) {
			schemaSequence = (XmlSchemaSequence) particle;
			model.isReapeating = isArray(element) || schemaSequence.getMaxOccurs() > 1;
			List<XmlSchemaSequenceMember> schemaObjectCollection = schemaSequence.getItems();
			for (XmlSchemaSequenceMember sq : schemaObjectCollection) {
				XmlSchemaElement innerElement = ((XmlSchemaElement) sq);
				XmlSchemaType innerEleType = innerElement.getSchemaType();
				if (innerEleType instanceof XmlSchemaComplexType) {
					model.models.add(processComplexType(innerElement));
				} else if (innerEleType instanceof XmlSchemaSimpleType) {
					model.models.add(processSimpleType(innerElement));
				}

			}
		}
		return model;
	}

	public InputDataModel processSimpleType(XmlSchemaElement element) {
		InputDataModel model = new InputDataModel();
		model.name = element.getName();
		model.isReapeating = isArray(element);
		QName qName = element.getSchemaTypeName();

		XmlSchemaSimpleType elementType = (XmlSchemaSimpleType) element.getSchemaType();
		XmlSchemaSimpleTypeContent content = elementType.getContent();
		if (content instanceof XmlSchemaSimpleTypeRestriction && qName == null) {
			XmlSchemaSimpleTypeRestriction resC = (XmlSchemaSimpleTypeRestriction) content;
			List<XmlSchemaFacet> e = resC.getFacets();
			for (XmlSchemaFacet facet : e) {
				if (facet instanceof XmlSchemaMinInclusiveFacet) {
					model.minvalue = Integer.parseInt((String) facet.getValue());
				} else if (facet instanceof XmlSchemaMaxLengthFacet) {
					model.maxLength = Integer.parseInt((String) facet.getValue());
				} else if (facet instanceof XmlSchemaMaxInclusiveFacet) {
					model.maxvalue = Integer.parseInt((String) facet.getValue());
				}
			}
			model.type = resC.getBaseTypeName().getLocalPart();
		} else {
			model.type = qName.getLocalPart();
		}

		return model;

	}

	private boolean isArray(XmlSchemaElement element) {
		return !(element.getMaxOccurs() == 1);
	}
}