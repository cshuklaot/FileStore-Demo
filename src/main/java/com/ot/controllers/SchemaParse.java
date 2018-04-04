package com.ot.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.emc.schema.ProcessSchema;
import com.emc.schema.ReturnSchemaModel;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/parse")
public class SchemaParse {

	@RequestMapping("/schema")
	public ReturnSchemaModel schemaParse(@RequestParam("schema") MultipartFile multipart) throws IOException {
		System.out.println("SchemaParse.schemaParse()");
		if (validRequest(multipart)) {
			InputStream is = multipart.getInputStream();
			XmlSchemaCollection schemaCol = new XmlSchemaCollection();
			XmlSchema schema = schemaCol.read(new StreamSource(is));
			return new ProcessSchema(schema).processXmlSchema();

		}
		throw new RuntimeException("invalid input");
	}

	private boolean validRequest(MultipartFile multipart) {
		return true;
	}

	public static void main(String[] args) throws FileNotFoundException, JsonProcessingException {
		FileInputStream stream = new FileInputStream("pdi-schema.xsd");
		XmlSchemaCollection schemaCol = new XmlSchemaCollection();

		XmlSchema schema = schemaCol.read(new StreamSource(stream));
		new ProcessSchema(schema).processXmlSchema();
	}
}
