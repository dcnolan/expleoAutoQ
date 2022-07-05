package com.expleoautomation.commons;

import java.lang.reflect.Field;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class reflectionBase {

	// GET property by name from the SUB-CLASS using reflection
	protected String getResource() {
		return getPropertyByName("resource");
	}

	protected String getApiName() {
		String resource = getResource();
		return resource.substring(resource.lastIndexOf('/') + 1);
	}

	protected String getClassName() {
		String name = this.getClass().getName();
		return name.substring(name.lastIndexOf('.') + 1);
	}

	protected String getPropertyByName(String propertyName) {
		return getPropertyByName(propertyName, this);
	}

	protected String getPropertyByName(String propertyName, Object obj) {
		propertyName = propertyName.trim();
		try {
			return obj.getClass().getField(propertyName).get(this).toString();
		} catch (Exception e) {
			log.error("getPropertyByName(): '" + propertyName + "' property is missing from class '" + getClassName()
					+ "'");
			return null;
		}
	}

	// SET property by name from the SUB-CLASS using reflection
	protected void setPropertyByName(String propertyName, Object value) {
		setPropertyByName(propertyName, value, this);
	}

	protected void setPropertyByName(String propertyName, Object value, Object obj) {
		propertyName = propertyName.trim();
		try {
			Field field = obj.getClass().getField(propertyName);
			field.set(obj, value);

		} catch (Exception e) {
			log.warn("setPropertyByName(): '" + propertyName + "' property is missing from class '" + getClassName() + "'");
		}
	}

	// find the name of property that is a list (may not exist)
	protected String getListProperty() {
		String propertyName = null;
		for (Field field : this.getClass().getFields()) {
			if (field.getType().toString().equals("interface java.util.List")) {
				propertyName = field.getName();
				break;
			}
		}
		return propertyName;
	}

}
