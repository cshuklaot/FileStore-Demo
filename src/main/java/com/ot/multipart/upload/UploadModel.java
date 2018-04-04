package com.ot.multipart.upload;

import org.springframework.web.multipart.MultipartFile;

import net.minidev.json.JSONObject;

public class UploadModel {

	private JSONObject formInfo;

	public UploadModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	private MultipartFile[] files;

	public MultipartFile[] getFiles() {
		return files;
	}

	public void setFiles(MultipartFile[] files) {
		this.files = files;
	}

	public JSONObject getFormInfo() {
		return formInfo;
	}

	public void setFormInfo(JSONObject formInfo) {
		this.formInfo = formInfo;
	}

}
