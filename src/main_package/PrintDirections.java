package main_package;

import java.awt.GraphicsEnvironment;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import junit.framework.Test;

import javax.mail.*;
import javax.mail.internet.*;
import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.activation.*;



public class PrintDirections {
	public PrintDirections(ArrayList<ArrayList<String>> directions,ArrayList<Directions> points, String emailFilename) throws AddressException
	{
		try {
			emailDirections(directions, points, new InternetAddress(emailFilename));
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			if(emailFilename.contains("save"))
			{
				printDirectionstoFile(directions, emailFilename);
			}else
				throw new AddressException();
			
		}
		
		
	}
	
	public PrintDirections(ArrayList<ArrayList<String>> directions) throws FileNotFoundException
	{
		
		
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
		PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
		PrintService service = ServiceUI.printDialog(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(), 200, 200,
		                      printService, defaultService, flavor, pras);
		if (service != null) {
		    DocPrintJob job = service.createPrintJob();
		    FileInputStream fis = new FileInputStream(printDirectionstoFile(directions, "Test.html"));
		    DocAttributeSet das = new HashDocAttributeSet();
		    Doc document = new SimpleDoc(fis, flavor, das);
		    try {
				job.print(document, pras);
			} catch (PrintException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

/*
		  InputStream is = null;    
		  try
		  { 
		     PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
		     DocPrintJob printerJob = defaultPrintService.createPrintJob();
		 
		     //File pdfFile = new File(arg);
		     is = new BufferedInputStream(new ByteArrayInputStream(generatePrintout(directions).getBytes("UTF-8")));
		     Doc simpleDoc =  new SimpleDoc(is, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
		     printerJob.print(simpleDoc, null);
		     PrinterJob pj = PrinterJob.getPrinterJob(); 
		     if (pj.printDialog()) {
		         try {
		        	 pj.print();
		        	 System.out.println("Printed");
		         
		         }
		         catch (PrinterException exc) {
		             System.out.println(exc);
		          }
		      }   

		 
		  }
		  catch (Exception e) 
		  {
		     e.printStackTrace(System.out);
		  }
		
		
*/

/*		FileWriter writer;
		try {
			writer = new FileWriter(fileName);
			String printout = generatePrintout(directions);
			writer.write(printout);

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	private File printDirectionstoFile(ArrayList<ArrayList<String>> directions,String fileName)
	{
		File outputFilename = null;

		if(fileName == null)
		{
			JFrame chooseFile = new JFrame();

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Specify Filename");

			int userSelection = fileChooser.showSaveDialog(chooseFile);


			if (userSelection == JFileChooser.APPROVE_OPTION) {
				outputFilename = fileChooser.getSelectedFile();
				fileName = outputFilename.toString();
			}

		}else{
			outputFilename = new File(fileName);
		}
		
		
			FileWriter writer;
			try {
				writer = new FileWriter(fileName);
				String printout = generatePrintout(directions);
				writer.write(printout);
				writer.close();
				System.out.println("Printed "+outputFilename.getName());
				return outputFilename;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		
	}





	private void emailDirections(ArrayList<ArrayList<String>> directions,ArrayList<Directions> points, InternetAddress to) throws AddressException
	{

		boolean canSend =true;
		InternetAddress toEmailAddress = null;
		try {
			toEmailAddress = to;
			toEmailAddress.validate();
		} catch (AddressException e1) {
			// TODO Auto-generated catch block
			canSend=false;
			throw new AddressException("Bad Address");	
		}

		// Sender's email ID needs to be mentioned
		String from = "Vectorr@wpi.edu";

		// Assuming you are sending email from localhost
		String host = "smtp.wpi.edu";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			InternetAddress fromAddress = new InternetAddress(from);
			fromAddress.setPersonal("Vectorr Solutions");
			// Set From: header field of the header.
			message.setFrom(fromAddress);
			
			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, toEmailAddress);

			// Set Subject: header field with the first and last location.
			message.setSubject("Directions with Magnitude From: "+points.get(0).getOrigin().getName()+" To: "
					+points.get(points.size()-1).getDestination().getName());

			// Send the actual HTML message, as big as you like

			message.setContent(generatePrintout(directions), "text/html" );

			// Send message
			if(canSend)
			{
				Transport.send(message);
				System.out.println("Sent message successfully....");
			}
		}catch (MessagingException | UnsupportedEncodingException mex) {
			mex.printStackTrace();
		}
	}

	public PrintDirections(ArrayList<ArrayList<String>> directions, String to,String from,String subject)
	{

		boolean canSend =true;
		InternetAddress toEmailAddress = null;
		try {
			toEmailAddress = new InternetAddress(to);
			toEmailAddress.validate();
		} catch (AddressException e1) {
			// TODO Auto-generated catch block
			System.out.println("bad address");	
			canSend=false;
		}

		// Sender's email ID needs to be mentioned
		String fromEmail = from;

		// Assuming you are sending email from localhost
		String host = "smtp.wpi.edu";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			InternetAddress fromAddress = new InternetAddress(from);
			fromAddress.setPersonal("Vectorr Solutions");
			// Set From: header field of the header.
			message.setFrom(fromAddress);

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, toEmailAddress);

			// Set Subject: header field
			message.setSubject(subject);

			// Send the actual HTML message, as big as you like

			message.setContent(generatePrintout(directions), "text/html" );

			// Send message
			if(canSend)
			{
				Transport.send(message);
				System.out.println("Sent message successfully....");
			}
		}catch (MessagingException | UnsupportedEncodingException mex) {
			mex.printStackTrace();
		}
	}


/*	private String generatePrintout(ArrayList<String> directions) {
		// TODO Auto-generated method stub
		String printout = "<h4>";
		for(String step:directions)
		{
			printout += step+'\n'+"<br>";
			printout += System.getProperty("line.separator");
		}
		printout += "<h4>";
		return printout;
	}*/
	private String generatePrintout(ArrayList<ArrayList<String>> directions) {
		// TODO Auto-generated method stub
		String printout = "<h4>";
		for(ArrayList<String> map:directions)
		{
			
			for(String step:map)
			{
				printout += step+'\n'+"<br>";
				printout += System.getProperty("line.separator");
		
			}
			printout += '\n'+"<br>" + System.getProperty("line.separator");
			printout += "<h4>";
		}

		return printout;
	}

	public static void main(String[] args)
	{
		ArrayList<ArrayList<String>> testDirections = new ArrayList<ArrayList<String>>();
		ArrayList<String> building1 = new ArrayList<String>();
		ArrayList<String> building2 = new ArrayList<String>();
		ArrayList<String> building3 = new ArrayList<String>();
		ArrayList<String> building4 = new ArrayList<String>();
		testDirections.add(building1);
		testDirections.add(building2);
		testDirections.add(building3);
		testDirections.add(building4);
		
		building1.add("Build1-room1");
		building1.add("Build1-room2");
		building1.add("Build1-room3");
		building1.add("Build1-room4");
		building1.add("Build1-room5");
		building1.add("Build1-room6");
		building1.add("Build1-room7");

		building2.add("Build2-room1");
		building2.add("Build2-room2");
		building2.add("Build2-room3");
		building2.add("Build2-room4");
		building2.add("Build2-room5");
		building2.add("Build2-room6");
		building2.add("Build2-room7");
		
		building3.add("Build3-room1");
		building3.add("Build3-room2");
		building3.add("Build3-room3");
		building3.add("Build3-room4");
		building3.add("Build3-room5");
		building3.add("Build3-room6");
		building3.add("Build3-room7");
		
		building4.add("Build4-room1");
		building4.add("Build4-room2");
		building4.add("Build4-room3");
		building4.add("Build4-room4");
		building4.add("Build4-room5");
		building4.add("Build4-room6");
		building4.add("Build4-room7");
		
		
		
		try {
			new PrintDirections(testDirections);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}