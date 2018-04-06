package com.ot.schema;

import java.util.ArrayList;
import java.util.List;

public class ReturnSchemaModel {

	public ReturnSchemaModel() {
		super();
	}

	public List<InputDataModel> simpleModels = new ArrayList<>();
	public List<InputDataModel> schemaModel = new ArrayList<>();

	public ReturnSchemaModel(List<InputDataModel> simpleModels, List<InputDataModel> schemaModel) {
		super();
		this.simpleModels = simpleModels;
		this.schemaModel = schemaModel;
	}

}
