package com.ot.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.emc.schema.ReturnSchemaModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/upload")
public class UploadController {
	private static final String UPLOADED_FOLDER = "";

	@RequestMapping("/store")
	public String uploadFileMulti(@RequestParam("formInfo") String formInfo,
			@RequestParam("InputModel") String inputmodel, @RequestParam("files") MultipartFile uploadfile)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ReturnSchemaModel model = mapper.readValue(inputmodel, ReturnSchemaModel.class);
		Map<String, Object> formMap = new ObjectMapper().readValue(formInfo, HashMap.class);

		formMap.keySet().iterator().forEachRemaining(key -> {
			System.out.println("key" + key);
			System.out.println(formMap.get(key));
		});

		String uploadedFileName = uploadfile.getOriginalFilename();
		System.out.println("UploadController.uploadFileMulti()   " + formInfo);
		if (StringUtils.isEmpty(uploadedFileName)) {
			return "please select a file!";
		}
		try {
			saveUploadedFile(uploadfile);
		} catch (IOException e) {
			throw new RuntimeException();
		}

		return "Successfully uploaded - " + uploadedFileName;

	}

	private void saveUploadedFile(MultipartFile file) throws IOException {
		byte[] bytes = file.getBytes();
		Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
		Files.write(path, bytes);
		System.out.println(path.toFile().getAbsolutePath());

	}
}
