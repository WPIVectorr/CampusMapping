package main_package;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class MapUpdaterGUI extends JFrame implements MouseListener{

	
	private static JLabel lblLastPoint;


	private int lastMousex, lastMousey;
	private int pointSize = 5;
	private ArrayList<Point> pointArray = new ArrayList<Point>();
	
	
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
	private JTextField startingLocation;
	private JTextField txtStartingLocation;
	private JTextField txtDestination;
    private JPanel buttonPanel;
    private DrawRoute drawPanel = new DrawRoute();
    private JTextField mapName;

    public MapUpdaterGUI() throws IOException  {
    	super("MapUpdaterGUI");
        setSize(932, 778);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
       //  img = ImageIO.read(new File("temp.jpg"));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 0, 10, 10));
        buttonPanel.setBackground(new Color(255, 235, 205));
        
        //adds the destination label to the line with destination location options
        JLabel lblDestination = new JLabel("Map Name:");
        buttonPanel.add(lblDestination);
        lblDestination.setBounds(6, 68, 85, 44);

        Container contentPane = this.getContentPane();
        contentPane.add(buttonPanel, BorderLayout.NORTH);
 		         
          //adds the starting location label to the line with starting location options
        JLabel lblStartingLocation = new JLabel("Room Number");
        buttonPanel.add(lblStartingLocation);
        lblStartingLocation.setBounds(6, 31, 119, 16);
 		  
 		  JRadioButton rdbtnAddPoints = new JRadioButton("Add Points");
 		  buttonPanel.add(rdbtnAddPoints);
 		  
 		  mapName = new JTextField();
 		  buttonPanel.add(mapName);
 		  mapName.setColumns(10);
 		  


 		  //creates a centered text field that will write back the users info they typed in
 		  startingLocation = new JTextField();
 		  buttonPanel.add(startingLocation);
 		  startingLocation.setHorizontalAlignment(JTextField.CENTER);
 		  startingLocation.setToolTipText("");
 		  startingLocation.setBounds(6, 174, 438, 30);
 		  startingLocation.setColumns(1);
 		  
 		  JRadioButton rdbtnEditPoints = new JRadioButton("Edit Points");
 		  buttonPanel.add(rdbtnEditPoints);
 		  
 		  Component horizontalStrut = Box.createHorizontalStrut(20);
 		  buttonPanel.add(horizontalStrut);
 		  
 		  Component horizontalStrut_1 = Box.createHorizontalStrut(20);
 		  buttonPanel.add(horizontalStrut_1);
 		  
 		  JRadioButton rdbtnRemovePoints = new JRadioButton("Remove Points");
 		  buttonPanel.add(rdbtnRemovePoints);
 		  
 		  lblLastPoint = new JLabel("No Point Selected");
 		  buttonPanel.add(lblLastPoint);
 		  
 		  JButton btnSaveMap = new JButton("Save Map");
 		  buttonPanel.add(btnSaveMap);
 		  
 		  JButton btnChangeMode = new JButton("Change Mode");
 		  buttonPanel.add(btnChangeMode);
          
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
    	MapUpdaterGUI myTest = new MapUpdaterGUI();
        myTest.setVisible(true);
    }

    class DrawRoute extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, null);
           
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                lastMousex =  e.getX();
                lastMousey =  e.getY();
                Point point = new Point(pointID, pointName, lastMousex, lastMousey, numEdges);
                MapUpdaterGUI.lblLastPoint.setText("Last Click X:" + lastMousex + ", Y:" + lastMousey);

                pointArray.add(point);
                //placePoint(lastMousex, lastMousey);
                repaint();
                }
            });
            
            
            for(int i=0;i<pointArray.size();i++)
            {
            	int drawX = (int) pointArray.get(i).getX();
            	int drawY = (int) pointArray.get(i).getY();
            	//draws the points onto the map.
            	g.drawOval(drawX -(pointSize/2), drawY -(pointSize/2), pointSize, pointSize);
            }
            g.drawOval(lastMousex-2, lastMousey-2, 5, 5);
            

            
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {


	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
