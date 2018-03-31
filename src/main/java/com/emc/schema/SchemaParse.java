package com.emc.schema;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ot.storefile.Greeting;

@RestController
public class SchemaParse {

	@RequestMapping("/parse/schema")
	 public List<InputDataModel> uploadFileMulti(
	            @RequestParam("schema") MultipartFile multipart) {
			if(validRequest(multipart))
			{
				InputStream is=multipart.getInputStream();
				XmlSchemaCollection schemaCol = new XmlSchemaCollection();
				XmlSchema schema = schemaCol.read(new StreamSource(is));
				schema.
			}
		
	}
}
