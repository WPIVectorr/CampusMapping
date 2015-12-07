package main_package;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javax.mail.*;
import javax.mail.internet.*;

import javax.activation.*;



public class PrintDirections {

	public PrintDirections(ArrayList<String> directions)
	{
		File outputFilename = null;
		String fileName = null;
		JFrame chooseFile = new JFrame();

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify File to Print");

		int userSelection = fileChooser.showSaveDialog(chooseFile);


		if (userSelection == JFileChooser.APPROVE_OPTION) {
			outputFilename = fileChooser.getSelectedFile();
			fileName = outputFilename.toString();
		}




		FileWriter writer;
		try {
			writer = new FileWriter(fileName);
			String printout = generatePrintout(directions);
			writer.write(printout);

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*	//email
	public PrintDirections(ArrayList<String> directions, String toEmailAddress)
	{ 

	    Properties props = System.getProperties();
	    props.put("mail.smtp.starttls.enable", true); // added this line
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.user", "JPGIndustry");
	    props.put("mail.smtp.password", "jntdpnxrrafnlklj");
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.from", "vectorr@wpi.edu");
	    props.put("mail.smtp.auth", true);



	    Session session = Session.getInstance(props,null);
	    MimeMessage message = new MimeMessage(session);

	    System.out.println("Port: "+session.getProperty("mail.smtp.port"));

	    // Create the email addresses involved
	    try {
	        InternetAddress from = new InternetAddress("Vectorr@WPI.edu");
	        try {
	        	from.setAddress("Vectorr@wpi.edu");
				from.setPersonal("Vectorr Solutions");

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        message.setFrom(from);
	        message.setReplyTo(InternetAddress.parse("Vectorr@wpi.edu"));
	        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress));

	        // Create a multi-part to combine the parts
	        Multipart multipart = new MimeMultipart("alternative");

			message.setSubject("Vectorr, directions with magnitude from: "+directions.get(0)+" to: "
					+directions.get(directions.size()-1));

	        // Create your text message part
	        BodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setText(generatePrintout(directions));

	        // Add the text part to the multipart
	        multipart.addBodyPart(messageBodyPart);

	        // Create the html part
	        messageBodyPart = new MimeBodyPart();
	        String htmlMessage = generatePrintout(directions);
	        messageBodyPart.setContent(htmlMessage, "text/html");


	        // Add html part to multi part
	        multipart.addBodyPart(messageBodyPart);

	        // Associate multi-part with message
	        message.setContent(multipart);

	        // Send message
	        Transport transport = session.getTransport("smtp");
	        transport.connect("smtp.gmail.com", "JPGIndustry", "jntdpnxrrafnlklj");
	        transport.sendMessage(message, message.getAllRecipients());
	        System.out.println("Sendmail Success");

	    } catch (AddressException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (MessagingException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}*/



	public PrintDirections(ArrayList<String> directions, String to)
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

			// Set Subject: header field
			message.setSubject("Directions with Magnitude from: "+directions.get(0)+" to: "
					+directions.get(directions.size()-1));

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

	public PrintDirections(ArrayList<String> directions, String to,String from,String subject)
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


	private String generatePrintout(ArrayList<String> directions) {
		// TODO Auto-generated method stub
		String printout = "<h4>";
		for(String step:directions)
		{
			printout += step+'\n'+"<br>";
			printout += System.getProperty("line.separator");
		}
		printout += "<h4>";
		return printout;
	}





}
