package main_package;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GUI extends JFrame{

	BufferedImage img = null;
	private JPanel contentPane;
	//drop down menu of buildings on campus
	String buildings[] = {"Select Building", "Atwater Kent", "Boynton Hall", 
			"Campus Center", "Gordon Library", "Higgins House", "Project Center", 
			"Stratton Hall"};
	
	//drop down menu of room numbers based off of the building selected on campus
	String rooms[] = {"Select room #", "Please choose building first"};
	String[] akRooms = {"Select room #", "10", "20", "30", "40"};
	String[] bhRooms = {"Select room #", "11", "21", "31", "41"};
	String[] ccRooms = {"Select room #", "12", "22", "32", "42"};
	String[] glRooms = {"Select room #", "13", "23", "33", "43"};
	String[] hhRooms = {"Select room #", "14", "24", "34", "44"};
	String[] pcRooms = {"Select room #", "15", "25", "35", "45"};
	String[] shRooms = {"Select room #", "16", "26", "36", "46"};
	
	String point1;
	String point2;
	String point3;
	String point4;
	private JTextField textField;
	private JTextField txtStartingLocation;
	private JTextField txtDestination;
	
    private JPanel buttonPanel;
    private DrawRoute drawPanel = new DrawRoute();

    public GUI() throws IOException{
    	super("GUI");
        setSize(932, 778);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
       //  img = ImageIO.read(new File("temp.jpg"));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 0, 10, 10));
        buttonPanel.setBackground(new Color(255, 235, 205));
                  
          //adds the starting location label to the line with starting location options
        JLabel lblStartingLocation = new JLabel("Starting Location:");
        buttonPanel.add(lblStartingLocation);
        lblStartingLocation.setBounds(6, 31, 119, 16);

        Container contentPane = this.getContentPane();
        contentPane.add(buttonPanel, BorderLayout.NORTH);
 		         
        //creates drop down box with building names
        JComboBox<String> startBuilds = new JComboBox(buildings);
        buttonPanel.add(startBuilds);
        startBuilds.setBounds(122, 30, 148, 20);
        JComboBox startRooms = new JComboBox(rooms);
        //adds the room numbers based off of building name
        startBuilds.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
            	
            	startRooms.removeAllItems();
            	String buildSelectStart = (String)startBuilds.getSelectedItem();
            	startRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectStart)));
            }
        });
 		         
         
 		         
       //creates the drop down box with rooms for start (initially waits for the building to have 
 		         //the specific buildings room numbers)

 		         buttonPanel.add(startRooms);
 		         startRooms.setBounds(296, 30, 148, 20);
 		         
 		         //adds the destination label to the line with destination location options
 		         JLabel lblDestination = new JLabel("Destination:");
 		         buttonPanel.add(lblDestination);
 		         lblDestination.setBounds(6, 68, 85, 44);
        //creates a drop down box with destination building names
        JComboBox<String> destBuilds = new JComboBox(buildings);
        buttonPanel.add(destBuilds);
        destBuilds.setBounds(122, 80, 148, 20);
 		
 		JComboBox destRooms = new JComboBox(rooms);
        //adds the correct room numbers for the building specified
        destBuilds.addActionListener (new ActionListener () {
        public void actionPerformed(ActionEvent e) {
         		    	
        destRooms.removeAllItems();
        String buildSelectDest = (String)destBuilds.getSelectedItem();
        destRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectDest)));
        }
        });
        lblDestination.setLabelFor(destBuilds);

 		
 		//creates the drop down of room numbers for destination (initially waits for the building to have 
 		//the specific buildings room numbers)
 		buttonPanel.add(destRooms);
 		destRooms.setBounds(296, 80, 148, 20);
 		


 		//creates a centered text field that will write back the users info they typed in
 		textField = new JTextField();
 		buttonPanel.add(textField);
 		textField.setHorizontalAlignment(JTextField.CENTER);
 		textField.setToolTipText("");
 		textField.setBounds(6, 174, 438, 30);
 		textField.setColumns(1);
 		  
 		  JButton btnNewButton = new JButton("Directions");
 		  buttonPanel.add(btnNewButton);
 		  btnNewButton.setBackground(new Color(0, 255, 127));
 		  btnNewButton.addActionListener(new ActionListener() {
 		  	public void actionPerformed(ActionEvent arg0) {
 		  		//gets the start and end building and room numbers the user chose
 		  		point1 = (String) startBuilds.getSelectedItem();
 		  		point2 = (String) destBuilds.getSelectedItem();
 		  		point3 = (String) startRooms.getSelectedItem();
 		  		point4 = (String) destRooms.getSelectedItem();
 		  		
 		  		//prints an error if no or both buildings weren't selected
 		  		if (point1.equals("Select Building") || point2.equals("Select Building"))
 		  			textField.setText("Error: Select Two Points");
 		  		//prints an error if the exact same room number and building were put as start and destinaiton
 		  		else if(point1.equals(point2) && point3.equals(point4)){
 		  			textField.setText("Error: Select Two Different Points");
 		  		}
 		  		//if no room numbers are selected prints an error
 		  		else if (point3.equals("Select room #") || point4.equals("Select room #"))
 		  			textField.setText("Error: please select room numbers");
 		  		//prints the starting building and room number and end building and room number
 		  		else
 		  			textField.setText(point1 + " room " + point3 + " to  " + point2 + " room " + point4);
 		  		repaint();
 		  	}
 		  });
 		  
 		  btnNewButton.setBounds(187, 132, 94, 30);
 		  
 		  Component horizontalStrut = Box.createHorizontalStrut(20);
 		  buttonPanel.add(horizontalStrut);
          
         getContentPane().add(drawPanel);

    }
     
 	//assigns the selected building name to have the string of room 
 	//numbers based off of the building selected
 	public String[] generateRoomNums(String select){
 		switch (select){
 		case "Atwater Kent":
 			return akRooms;
 		case "Boynton Hall":
 			return bhRooms;
 		case "Campus Center":
 			return ccRooms;
 		case "Gordon Library":
 			return glRooms;
 		case "Higgins House":
 			return hhRooms;
 		case "Project Center":
 			return pcRooms;
 		case "Stratton Hall":
 			return shRooms;
 		}
 		return rooms;
 	}


    public static void main(String[] args) throws IOException{
    	GUI myTest = new GUI();
        myTest.setVisible(true);
    }

    class DrawRoute extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, null);
           
            if (point1 == "Boynton Hall"){
            	g.drawLine(0, 0, 50, 100);
            }
            else if (point2 == "Atwater Kent"){
            	g.drawLine(100, 200, 300, 400);
            }
        }
    }
}
