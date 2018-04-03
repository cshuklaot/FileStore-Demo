package com.emc.schema;

import java.util.ArrayList;
import java.util.List;

public class InputDataModel {

	public String name;
	public String type;
	public int maxLength;
	public int minlength;
	public boolean isReapeating;
	public int minvalue;
	public int maxvalue;
	public List<InputDataModel> models = new ArrayList<>();

}
