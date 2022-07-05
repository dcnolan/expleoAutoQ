package com.expleoautomation.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.Assert;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class serializationBase extends reflectionBase {

	public String toString() {

		StringBuilder value = new StringBuilder();

		// get list of rows objects
		String listPropertyName = getListProperty();
		List<Object> rows = null;
		try {
			rows = (List<Object>)this.getClass().getField(listPropertyName).get(this);
		} catch (Exception e) {
			log.error("Failed to get value for property '" + listPropertyName + "'" + System.lineSeparator() + e.getMessage());
		}
		

		// loop rows
		for (int index = 0; index < rows.size(); index++) {
			Object row = rows.get(index);
			
			// loop fields on row
			for (Field field : row.getClass().getFields()) {
				String name = field.getName();

				// get property object
				Object obj = null;
				try {
					obj = field.get(row);
				} catch (Exception e) {
					log.error("Failed to get value for property '" + name + "'" + System.lineSeparator() + e.getMessage());
				}
				if (obj != null) {
					// append value to string builder
					value.append(obj.toString());
				}
				value.append(";");
			}
			value.append(System.lineSeparator());
		}

		// return string
		log.info("toString(): Serialize class to string ... " + System.lineSeparator() + value.toString());
		return value.toString();
	}

	public long toFile(String filePath) {
		// open file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filePath, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			Assert.fail("toFile(): FAILED to create file '" + filePath + "'");
		}

		// write file
		writer.print(toString());
		writer.close();

		// return file size
		log.info("toFile(): Write file " + filePath);
		return (new File(filePath).length());
	}

	public String toTempFile(String extension) {
		File f = null;
		try {
			f = File.createTempFile(this.getClassName() + ".", "." + extension);
		} catch (IOException e) {
			Assert.fail("toTempFile(): FAILED to create file TEMP file");
		}
		String filePath = f.getAbsolutePath();

		// write file
		toFile(filePath);

		// mark file for deletion
		new File(filePath).deleteOnExit();

		// return file name
		log.info("toTempFile(): File " + filePath + " will be deleted when JVM quits");
		return (filePath);
	}

}
