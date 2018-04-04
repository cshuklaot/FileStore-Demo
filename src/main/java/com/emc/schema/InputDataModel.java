package com.emc.schema;

import java.util.ArrayList;
import java.util.List;

public class InputDataModel {

	public String name;
	public String type;
	public int maxLength;
	public int minLength;
	public boolean isReapeating;

	public InputDataModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InputDataModel(String name, String type, int maxLength, int minLength, boolean isReapeating, int minvalue,
			int maxvalue, List<InputDataModel> models) {
		super();
		this.name = name;
		this.type = type;
		this.maxLength = maxLength;
		this.minLength = minLength;
		this.isReapeating = isReapeating;
		this.minvalue = minvalue;
		this.maxvalue = maxvalue;
		this.models = models;
	}

	public int minvalue;
	public int maxvalue;
	public List<InputDataModel> models = new ArrayList<>();

}
