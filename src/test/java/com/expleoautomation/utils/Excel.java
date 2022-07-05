package com.expleoautomation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.util.DateUtil;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;



import lombok.extern.log4j.Log4j2;


@Log4j2
public class Excel {
	
	XSSFWorkbook workbook = null;
	XSSFSheet worksheet = null;
	
	

	
	public void close() {
		worksheet = null;
		workbook= null;	
	}
		
	public void openWorksheet(String xlsWorksheetName) {
		worksheet = workbook.getSheet(xlsWorksheetName);
		if (worksheet == null) {
			Assert.fail("openWorksheet(): FAILED to open worksheet '" + xlsWorksheetName + "'");			
		} else {
			log.info("openWorksheet(): '" + xlsWorksheetName + "'");
		}
	}
	
	public Map<String, String> getDataMap(String rangeJsonProperties, String reference) {
		
		 for (Row row : worksheet) {
			    for (Cell cell : row) {
		    		
			    	// logging
			    	//log.debug(getCellAddress(cell) + ": '" + getValue(cell) + "'");
			    	
			    	if (getValue(cell).equals(reference)) {
			
			    		// get json property names
			   		 	CellReference[] jsonPropertyNames = new AreaReference(rangeJsonProperties).getAllReferencedCells();
			   		 	// get json property values
			   		 	String rangeJsonValues = rangeJsonProperties.replace(columnAlpha(jsonPropertyNames[0].getCol()), columnAlpha(cell.getColumnIndex()));
			   		 	CellReference[] jsonPropertyValues = new AreaReference(rangeJsonValues).getAllReferencedCells();
			                
			   		 	//log.debug("jsonPropertyNames: " + rangeJsonProperties);
			   		 	//log.debug("jsonPropertyValues: " + rangeJsonValues);
			   		 	
			   		 	// build map
			   		 	Map<String, String> properties = new HashMap<String, String>();
			   		 	for(int index=0; index<jsonPropertyNames.length; index++) {
			   		 		
			   		 		// add property & value to map
			   		 		String name = getValue(jsonPropertyNames[index]);
			   		 		if (name != "") {
				   		 		String value = cleanData(getValue(jsonPropertyValues[index]));
				   		 		log.debug("   getDataMap(): " + name + ": " + value);
				   		 		properties.put(name, value);
			   		 		}

			   		 	}
			   		 	
			   		 	// return
			   		 	return properties;
			    	}
  		        }
		 }

		 Assert.fail("getDataMap(): Failed to find reference value '" + reference + "'"); 
		 
         return null;
	}
	
	
	public void openWorkbook(String xlsFilePath) {
		
        FileInputStream file = null;
		try {
			file = new FileInputStream(new File(xlsFilePath));
        	workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			Assert.fail("Failed to open Excel workbook '" + xlsFilePath + "'");
		}
        log.info("Opened Excel workbook '" + xlsFilePath + "'");
	}
	
	
	
	private String getValue(CellReference cellRef) {
		Row row = worksheet.getRow(cellRef.getRow());
		return getValue(row.getCell(cellRef.getCol()));
	}

	private String getValue(Cell cell) {
		
		String value = "";
		if (cell != null) {
			// get cell value (simple)
			value = cell.toString();
		
			// check for date formatting (because formatCellValue() can switch day/month)
		    String regEx = "^([0-9]{2})-([a-zA-Z]{3})-([0-9]{4})$";  // 01-Jan-2020
		    Pattern pattern = Pattern.compile(regEx); 
		    Matcher matcher = pattern.matcher(value); 
		    if (matcher.find() ) { 
		    	value = DateUtils.reFormat(value, "dd-MMM-yyyy", "dd/MM/yyyy");
	        } else {
				DataFormatter formatter = new DataFormatter();
				value = formatter.formatCellValue(cell).replace("\u00A0","").trim();
		    }
		}
	    return value;
	}
	
	private String cleanData(String value) {
		
		// split code - description eg: 0003 - Company
		if (value.contains("-")) {
			String[] splits = value.split("[-]");
			value = splits[0];
		}
		
		// remove "blank"
		value = value.replace("Blank", "");
		value = value.replace("blank", "");
		value = value.replace("BLANK", "");
		value = value.replace("TBC", "");
		value = value.replace("tbc", "");
		value = value.replace("MFGI default", "");
		value = value.replace("Default to dummy", "");
		value = value.replace("System generated", "");
		value = value.replace("System Generated", "");
		value = value.replace("Auto populated ", "");
		value = value.replace("n/a", "");	
		value = value.replace("N/A", "");
		value = value.replace("na", "");	
		value = value.replace("NA", "");
		value = value.replace("Unticked", "");
		
		
		
		// get country code
		if (CountryCodeMap.alpha2Code.containsKey(value)) {
			value = CountryCodeMap.alpha2Code.get(value);
		}

		/* convert percentage
		if (value.endsWith("%")) {
			value = value.replace("%", "");
			value = String.valueOf(Float.valueOf(value)/100);
		} */
		
		// fix date formatting
	    String regEx = "^([0-9]{2})/([0-9]{2})/([0-9]{2})$"; 
	    Pattern pattern = Pattern.compile(regEx); 
	    Matcher matcher = pattern.matcher(value); 
	    if (matcher.find() ) { 
	    	value = DateUtils.reFormat(value, "MM/dd/yy", "dd/MM/yyyy");
        }
		
		return value.trim();
	}
		
	private String columnAlpha(int index) {
		return CellReference.convertNumToColString(index);
	}
	
	

	
	//@Test
	public  void example() 
    {
        try
        {
            FileInputStream file = new FileInputStream(new File("C:\\Users\\DN137\\Documents\\Agueda\\RAD.JAD _Nutrimenta REGISTERS DATA Mapping.xlsx"));
 
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            //XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFSheet sheet = workbook.getSheet("HT.Diamon Diversified Fund");
            System.out.println(sheet.getSheetName());
            System.out.println("");
            
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            
             
            while (rowIterator.hasNext()) 
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                 
                while (cellIterator.hasNext()) 
                {
                    Cell cell = cellIterator.next();
                    
                    //Check the cell type and format accordingly
                    switch (cell.getCellType()) 
                    {
                        case Cell.CELL_TYPE_NUMERIC:
                        	System.out.print("(" + CellReference.convertNumToColString(cell.getColumnIndex()+1) + String.valueOf(cell.getRowIndex()+1) + ") "); 
                            System.out.println(cell.getNumericCellValue());
                            break;
                        case Cell.CELL_TYPE_STRING:
                        	System.out.print("(" + CellReference.convertNumToColString(cell.getColumnIndex()+1) + String.valueOf(cell.getRowIndex()+1) + ") "); 
                            System.out.println(cell.getStringCellValue());
                            break;
                    }
                }
                System.out.println("");
            }
            file.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	

}
