package com.expleoautomation.sample;
import java.util.ArrayList;
import java.util.List;
import com.expleoautomation.commons.serializationBase;

public class CsvFile extends serializationBase {


		public List<Row> rows = new ArrayList<>();
		public class Row {
			public String field1 = "ABC";
			public String field2;
			public String field3;
			public String field4; 
			public String field5 = "123";
			
		}
		
		
		public CsvFile() {	}
		public CsvFile(String field3, String field4) {	
			add("0000", field3, field4);	
			add("0001", field3, field4);	
			add("0002", field3, field4);	
		}


		// add row
		public void add(String field2, String field3, String field4) {
			Row row = new Row();
			row.field2 = field2; 
			row.field3 = field3;
			row.field4 = field4;
			rows.add(row);
		}



}
