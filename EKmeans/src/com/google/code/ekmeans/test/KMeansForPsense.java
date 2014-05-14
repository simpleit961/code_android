package com.google.code.ekmeans.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class KMeansForPsense {

	public static void main(String args[]) {
		ReadJsonFile();
	}

	public static void ReadJsonFile() {

		JSONParser parser = new JSONParser();

		//JSONArray a;
		JSONObject a; 
		
			try {
				a = (JSONObject) parser.parse(new FileReader("data.json"));
				JSONArray reports =  (JSONArray) a.get("reports");
				
				System.out.println("Size of reports+"+ reports.size());
				
				int i = 0;
				
				for(i = 0; i < reports.size(); i++) {
					JSONObject report =  (JSONObject) reports.get(i);
					JSONObject place = (JSONObject) report.get("place");
					
					String lattitude = (String) place.get("latitude");
					String longitude = (String) place.get("longitude");
					
					System.out.println(lattitude+":"+longitude);
				}
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	

	}
}
