package com.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.java.RecordsBean;

/**
 * This class gets two different file format as input and reads data from it and
 * does validation
 * 
 * @author Saranya
 *
 */

public class ValidateCSVandXML extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private BufferedReader reader;
	private static String tableDetails = null;
	private static final String FAILURE_REASON_DUPLICATE = "Duplicate Transaction Reference";
	private static final String FAILURE_END_BALANCE_VALIDATION = "Failure End Balance Validation";

	/**
	 * This function gets the filename from jsp form and checks whether file
	 * extension is xml or csv and reads the file accordingly
	 */

	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		RecordsBean record = null;
		final String fileName = request.getParameter("fileName");
		Map<Integer, Integer> transRefMap = new HashMap<Integer, Integer>();
		boolean isValidFile = false;
		
		tableDetails = "<table><th>TRANSACTION_REFERENCE</th><th>DESCRIPTION</th><th>FAILURE_REASON</th>";
		
		// Checks whether the given file extension is XML  
		
		if (fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase("xml")) {
			// Get Document Builder
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			isValidFile = true;
			try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException parserConfigurationException) {
				parserConfigurationException.printStackTrace();
			}

			Document document = null;
			try {
				document = builder.parse(request.getParameter("fileName"));
			} catch (SAXException saxException) {
				saxException.printStackTrace();
			}
			document.getDocumentElement().normalize();
			NodeList nList = document.getElementsByTagName("record");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node node = nList.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;

					//Converting start balance, end balance and mutation to double as they are currency type
					final double startBalance = Double
							.parseDouble(eElement.getElementsByTagName("startBalance").item(0).getTextContent());
					final double endBalance = Double
							.parseDouble(eElement.getElementsByTagName("endBalance").item(0).getTextContent());
					final double mutation = Double
							.parseDouble(eElement.getElementsByTagName("mutation").item(0).getTextContent());
					final String accountNumber = eElement.getElementsByTagName("accountNumber").item(0).getTextContent();
					final String description = eElement.getElementsByTagName("description").item(0).getTextContent();
					final int transactionReference = Integer.parseInt(eElement.getAttribute("reference"));
					
					// Create new RecordBean Object and setting the values of all fields from XML file	
					record = setAttributesForRecord(transactionReference, accountNumber, description, startBalance,
							mutation, endBalance);
					isTransRefUnique(transRefMap, record);
				}
			}
			// Checks whether the give file extension is CSV
			
		} else if (fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase("csv")) {
			try {
				isValidFile = true;
				reader = new BufferedReader(new FileReader(request.getParameter("fileName")));

				// read file line by line
				String line = null;
				reader.readLine();
				while ((line = reader.readLine()) != null) {
					
					//Creating the object for RecordBean and setting the values of all fields from CSV file
					final String[] lineItems = line.split(",");
					record = setAttributesForRecord(Integer.parseInt(lineItems[0]),lineItems[1],lineItems[2],
							Double.parseDouble(lineItems[3]),Double.parseDouble(lineItems[4]),
							Double.parseDouble(lineItems[5]));
					isTransRefUnique(transRefMap, record);
				}
			} catch (NumberFormatException numberFormatException) {
				// TODO Auto-generated catch block
				numberFormatException.printStackTrace();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		else {
			out.println("Please upload a valid file");
		}
		if (isValidFile == true) {
		tableDetails += "</table>";
		out.println(tableDetails);
	}
	}
	
	/**
	 * 
	 * this function sets all the fields in the given file to RecordsBean class
	 * 
	 * @param transReference
	 * @param accountNumber
	 * @param description
	 * @param startBalance
	 * @param mutation
	 * @param endBalance
	 * @return
	 */
	private RecordsBean setAttributesForRecord(final int transReference, final String accountNumber, final String description,
			final double startBalance, final double mutation, final double endBalance) {
		RecordsBean record = new RecordsBean();
		record.setTransReference(transReference);
		record.setAccno(accountNumber);
		record.setDescription(description);
		record.setStartBalance(startBalance);
		record.setMutation(mutation);
		record.setEndBalance(endBalance);
		return record;
	}
	
	/**
	 * This function validates the records for unique transaction reference for each
	 * record and displays the results accordingly
	 * 
	 * @param record
	 */

	private void printTransRefRecord(final RecordsBean record) {
		tableDetails += "<tr><td>" + record.getTransReference() + "</td><td>" + record.getDescription() + "</td><td>"
			 + FAILURE_REASON_DUPLICATE + "</td></tr>";
	}

	/**
	 * 
	 * This function validates the record for end balance amount which is the
	 * summation of start balance and mutation. The record which does not satisfies
	 * will be failed transaction and are displayed as output
	 * 
	 * @param record
	 */
	private void validateEndBalance(final RecordsBean record) {
		
		//Adding two startBalance and mutation values and comparing it with endBalance and if it is not equal (i.e., 1)
		//then it will be failed transaction
		
		final double endBalCalc = record.getStartBalance() + record.getMutation();
		if (Double.compare(endBalCalc, record.getEndBalance()) == 1) {
			tableDetails += "<tr><td>" + record.getTransReference() + "</td><td>" + record.getDescription() + "</td><td>"
					+ FAILURE_END_BALANCE_VALIDATION + "</td></tr>";
		}
	}
	
	/**
	 * 
	 * This function validates the transaction reference for unique constraint and in turn calls the 
	 * validation method to print the corresponding records
	 * 
	 * @param transRefMap
	 * @param record
	 */
	
	private void isTransRefUnique(final Map<Integer, Integer> transRefMap, final RecordsBean record) {
		
		//Set the transRefMap to 1 if the reference value comes once and incrementing it every time if the same value comes again
		// while iterating
		
		if (!transRefMap.containsKey(record.getTransReference())) {
			transRefMap.put(record.getTransReference(), 1);
		} else {
			transRefMap.put(record.getTransReference(), transRefMap.get(record.getTransReference()) + 1);
		}
		if (transRefMap.get(record.getTransReference()) > 1) {
			printTransRefRecord(record);
		}
		validateEndBalance(record);
	}
}
