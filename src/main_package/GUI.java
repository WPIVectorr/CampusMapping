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
	
	int[][] coordTest1 = {{30,30}, {100, 300}, {800, 300}, {100, 200}};
	int[][] coordTest2 = {{500,500}, {300, 100}, {800, 500}, {100, 200}};
	int[][] coordTest3 = {{400, 200}, {50, 30}, {8, 300}, {10, 20}};
	
	String point1;
	String point2;
	String point3;
	String point4;
	private boolean showRoute;
	private boolean showRoute2;
	private boolean showRoute3;
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
 		  
 		  JButton directionsButton = new JButton("Directions");
 		  buttonPanel.add(directionsButton);
 		  directionsButton.setBackground(new Color(0, 255, 127));
 		  directionsButton.addActionListener(new ActionListener() {
 		  	public void actionPerformed(ActionEvent arg0) {
 		  		//gets the start and end building and room numbers the user chose
 		  		point1 = (String) startBuilds.getSelectedItem();
 		  		point2 = (String) destBuilds.getSelectedItem();
 		  		point3 = (String) startRooms.getSelectedItem();
 		  		point4 = (String) destRooms.getSelectedItem();
 		  		
 		  		if (point1.equals("Atwater Kent")){
 		  			showRoute = true;
 		  			showRoute2 = false;
 		  			showRoute3 = false;
 		  		}
 		  		else if (point1.equals("Boynton Hall")){
 		  			showRoute = false;
 		  			showRoute2 = true;
 		  			showRoute3 = false;
 		  		}
 		  		else if (point1.equals("Campus Center")){
 		  			showRoute = false;
 		  			showRoute2 = false;
 		  			showRoute3 = true;
 		  		}
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
 		  
 		  directionsButton.setBounds(187, 132, 94, 30);
 		  
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
           
            if (showRoute){
                g.drawString("Origin", coordTest1[0][0], coordTest1[0][1]);
	            // For each point with (x, y) draw a line from the previous point to the point
	            for (int i = 1; i < coordTest1.length; i++){
	            	g.drawLine(coordTest1[i - 1][0], coordTest1[i - 1][1], coordTest1[i][0], coordTest1[i][1]);
	                // Adds strings denoting point number for demonstration
	            	g.drawString("Point " + i, coordTest1[i][0], coordTest1[i][1]);
	            }
            }
            
            if (showRoute2){
                g.drawString("Origin", coordTest2[0][0], coordTest2[0][1]);
	            // For each point with (x, y) draw a line from the previous point to the point
	            for (int i = 1; i < coordTest2.length; i++){
	            	g.drawLine(coordTest2[i - 1][0], coordTest2[i - 1][1], coordTest2[i][0], coordTest2[i][1]);
	                // Adds strings denoting point number for demonstration
	            	g.drawString("Point " + i, coordTest2[i][0], coordTest2[i][1]);
	            }
            }
            
            if (showRoute3){
                g.drawString("Origin", coordTest3[0][0], coordTest3[0][1]);
	            // For each point with (x, y) draw a line from the previous point to the point
	            for (int i = 1; i < coordTest3.length; i++){
	            	g.drawLine(coordTest3[i - 1][0], coordTest3[i - 1][1], coordTest3[i][0], coordTest3[i][1]);
	                // Adds strings denoting point number for demonstration
	            	g.drawString("Point " + i, coordTest3[i][0], coordTest3[i][1]);
	            }
            }
        }
    }
}