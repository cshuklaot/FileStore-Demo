package com.ot.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.emc.schema.ReturnSchemaModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ot.controllers.utils.CSVUtils;

@RestController
@RequestMapping("/upload")
public class UploadController {
	private static final File UPLOADED_FOLDER = new File("C:\\InfoArchiveUploadedStuff");
	private File file;

	@RequestMapping("/store")
	public ResponseEntity<InputStreamResource> uploadFileMulti(@RequestParam("formInfo") String formInfo,
			@RequestParam("InputModel") String inputmodel, @RequestParam("files") MultipartFile uploadfile)
			throws JsonParseException, JsonMappingException, IOException {

		try {
			saveUploadedFile(uploadfile);
		} catch (IOException e) {
			throw new RuntimeException();
		}

		// System.out.println("UploadController.uploadFileMulti()");
		File csvFile = extractCSV(formInfo, inputmodel);
		String uploadedFileName = uploadfile.getOriginalFilename();
		if (StringUtils.isEmpty(uploadedFileName)) {
			return ResponseEntity.badRequest().body(null);
		}

		FileInputStream stream = new FileInputStream(csvFile);
		return ResponseEntity.ok().contentLength(stream.available())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.header("Content-Disposition", "attachment; filename=" + csvFile.getName())
				.body(new InputStreamResource(stream));

	}

	@RequestMapping("/test")
	public ResponseEntity<InputStreamResource> storetest()
			throws JsonParseException, JsonMappingException, IOException {
		File csvFile = new File("pdi-schema.xsd");

		FileInputStream stream = new FileInputStream(csvFile);
		return ResponseEntity.ok().contentLength(stream.available())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(new InputStreamResource(stream));

	}

	private File extractCSV(String formInfo, String inputmodel)
			throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		ReturnSchemaModel model = mapper.readValue(inputmodel, ReturnSchemaModel.class);
		Map<String, Object> formMap = new ObjectMapper().readValue(formInfo, HashMap.class);
		// System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formMap));
		Map<String, String> object = (Map<String, String>) formMap.get("holding");
		String holding = object.get("name");
		object = (Map<String, String>) formMap.get("application");
		String application = object.get("name");
		object = (Map<String, String>) formMap.get("tanent");
		String tanent = object.get("name");
		formMap.remove("holding");
		formMap.remove("application");
		formMap.remove("tanent");
		Date cal = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = formatter.format(cal);
		String fileName = formattedDate + "_" + application + "_" + holding + ".csv";

		File csvFile = new File(UPLOADED_FOLDER, fileName);
		synchronized (csvFile) {
			boolean b = !csvFile.exists();
			FileWriter writer = new FileWriter(csvFile, true);
			if (b) {
				createCSVFile(model, formMap, writer);
			}
			writeDataToFile(model, formMap, tanent, application, holding, writer);
			writer.flush();
			writer.close();
		}
		return csvFile;
	}

	private void createCSVFile(ReturnSchemaModel model, Map<String, Object> formMap, Writer writer) throws IOException {

		List<String> values = new ArrayList<>();
		values.add("Tanent");
		values.add("Application");
		values.add("Holding");
		model.simpleModels.forEach(smodel -> {
			values.add(smodel.name);
		});
		values.add("File Name");
		values.add("MimeType");
		values.add("CreatedBy");
		values.add("CreatedOnDate");

		CSVUtils.writeLine(writer, values);

	}

	private void writeDataToFile(ReturnSchemaModel model, Map<String, Object> formMap, String tanent,
			String application, String holding, Writer writer) throws IOException {
		List<String> values = new ArrayList<>();
		values.add(tanent);
		values.add(application);
		values.add(holding);
		model.simpleModels.forEach(smodel -> {
			String value = (String) formMap.get(smodel.name);

			if (smodel.type.equalsIgnoreCase("datetime-local") && !StringUtils.isEmpty(value)) {
				System.out.println(value);
				Date fromXmltoDate;
				try {
					fromXmltoDate = fromXmltoDate(value);
				} catch (ParseException e) {
					throw new RuntimeException();
				}
				System.out.println(value);

				value = fromDateToXmlDateTime(fromXmltoDate);
				System.out.println(value);

			}
			values.add(value);
		});
		values.add(this.file.getName());
		String mimetype = Files.probeContentType(file.toPath());
		values.add(mimetype);
		values.add("");
		values.add(fromDateToXmlDateTime(new Date()));

		CSVUtils.writeLine(writer, values);
	}

	private void saveUploadedFile(MultipartFile file) throws IOException {
		byte[] bytes = file.getBytes();
		Path path = Paths.get(UPLOADED_FOLDER.getAbsolutePath(), file.getOriginalFilename());
		Files.write(path, bytes);
		this.file = path.toFile();
	}

	private Date fromXmltoDate(String xmlDate) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
		return (Date) formatter.parse(xmlDate);
	}

	private String fromDateToXmlDateTime(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return DatatypeConverter.printDateTime(c);
	}
}
