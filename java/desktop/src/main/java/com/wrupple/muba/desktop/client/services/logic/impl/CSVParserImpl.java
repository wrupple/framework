package com.wrupple.muba.desktop.client.services.logic.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.StringJSOadapter;
import com.wrupple.muba.desktop.client.services.logic.CSVParser;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;

public class CSVParserImpl implements CSVParser {

	@Inject
	public CSVParserImpl() {
		super();
	}

	@Override
	public ImportData parse(String csv) {
		JsArray<JsArrayString> parsed = regexParseCsv(csv, ",");
		ImportData result = new ImportData(parsed);
		return result;
	}

	/**
	 * from http://www.bennadel.com/blog/1504-Ask-Ben-Parsing-CSV-Strings-With-
	 * Javascript-Exec-Regular-Expression-Command.htm
	 * 
	 * @return
	 */
	protected native JsArray<JsArrayString> regexParseCsv(String strData, String strDelimiter)/*-{
		// Check to see if the delimiter is defined. If not,
		// then default to comma.
		if (strDelimiter == null) {
			strDelimiter = ",";
		}

		// Create a regular expression to parse the CSV values.
		var objPattern = new RegExp((
		// Delimiters.
		"(\\" + strDelimiter + "|\\r?\\n|\\r|^)" +

		// Quoted fields.
		"(?:\"([^\"]*(?:\"\"[^\"]*)*)\"|" +

		// Standard fields.
		"([^\"\\" + strDelimiter + "\\r\\n]*))"), "gi");

		// Create an array to hold our data. Give the array
		// a default empty first row.
		var arrData = [ [] ];

		// Create an array to hold our individual pattern
		// matching groups.
		var arrMatches = null;
		var doubleQuoteRegex = new RegExp("\"\"","g");
		var strMatchedDelimiter = null;
		var strMatchedValue =null;
		// Keep looping over the regular expression matches
		// until we can no longer find a match.
		while (arrMatches = objPattern.exec(strData)) {

			// Get the delimiter that was found.
			strMatchedDelimiter = arrMatches[1];

			// Check to see if the given delimiter has a length
			// (is not the start of string) and if it matches
			// field delimiter. If id does not, then we know
			// that this delimiter is a row delimiter.
			if (strMatchedDelimiter.length
					&& (strMatchedDelimiter != strDelimiter)) {

				// Since we have reached a new row of data,
				// add an empty row to our data array.
				arrData.push([]);

			}

			// Now that we have our delimiter out of the way,
			// let's check to see which kind of value we
			// captured (quoted or unquoted).
			if (arrMatches[2]) {

				// We found a quoted value. When we capture
				// this value, unescape any double quotes.
				strMatchedValue = arrMatches[2].replace(doubleQuoteRegex, "\"");

			} else {

				// We found a non-quoted value.
				strMatchedValue = arrMatches[3];

			}
			strMatchedValue.trim();
			if(strMatchedValue!=null && strMatchedValue.length==0){
				strMatchedValue=null;
			}
			
			// Now that we have our value string, let's add
			// it to the data array.
			arrData[arrData.length - 1].push(strMatchedValue);
		}

		// Return the parsed data.
		return (arrData);

	}-*/;

	public List<JavaScriptObject> parseJSOfromCSV(String[] lines, String[] fields) {
		JsArray<JavaScriptObject> array = csv2json(lines, fields);
		List<JavaScriptObject> regreso = new ArrayList<JavaScriptObject>(array.length());
		GWTUtils.copyIntoList(array, regreso);
		return regreso;
	}

	private JsArray<JavaScriptObject> csv2json(String[] lines, String[] fields) {

		JsArray<JavaScriptObject> array = JavaScriptObject.createArray().cast();
		String line;

		JSONObject temp;
		for (int i = 1; i < lines.length; i++) {
			line = lines[i];
			temp = new JSONObject();

			parseLine(line, temp, fields);

			array.set(i - 1, temp.getJavaScriptObject());
		}

		return array;
	}

	public void parseLine(String line, JSONObject store, String[] fields) {
		StringBuffer curVal = new StringBuffer();
		boolean inquotes = false;
		char ch;
		int fieldIndex = 0;
		for (int i = 0; i < line.length(); i++) {
			ch = line.charAt(i);
			if (inquotes) {
				if (ch == '\"') {
					inquotes = false;
				} else {
					curVal.append(ch);
				}
			} else {
				if (ch == '\"') {
					inquotes = true;
					if (curVal.length() > 0) {
						// if this is the second quote in a value, add a quote
						// this is for the double quote in the middle of a value
						curVal.append('\"');
					}
				} else if (ch == ',') {
					store.put(fields[fieldIndex], getValueOf(curVal.toString()));
					fieldIndex++;
					curVal = new StringBuffer();
				} else {
					curVal.append(ch);
				}
			}
		}
		store.put(fields[fieldIndex], getValueOf(curVal.toString()));
	}

	public String[] parseCSVLines(String csv) {
		String[] regreso = csv.split("\"\n");
		for (int i = 0; i < regreso.length; i++) {
			regreso[i] = regreso[i] + '\"';
		}
		return regreso;
	}

	private JSONValue getValueOf(String string) {
		string = string.trim();
		if (string.contains("\"")) {
			// quoted string value
			string = string.substring(1, string.length() - 1);
		}
		return StringJSOadapter.performJSONValueTransformation(string);
	}

}
