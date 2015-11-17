package main_package;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javafx.scene.shape.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Box;

public class MapUpdaterGUI extends JFrame {

	


	private int lastMousex, lastMousey;
	private int pointSize = 5;
	private boolean newClick = false;
	private boolean editingPoint = false;
	private ArrayList<Point> pointArray = new ArrayList<Point>();
	private Point currentPoint;
	private Point editPoint;

	private ArrayList<Edge> edgeArray = new ArrayList<Edge>();
	private Edge currentEdge;
	
	BufferedImage img = null;
	private JPanel contentPane;
	private static JButton btnSaveMap;
	private static JButton btnSavePoint;
	private static JRadioButton rdbtnAddPoints;
	private static JRadioButton rdbtnEditPoints;
	private static JRadioButton rdbtnRemovePoints;
	
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
	private JTextField roomNumber;
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
 		  
        /*Initialization of the radio buttons*/
		  ButtonGroup modeSelector = new ButtonGroup(); 
 		  rdbtnAddPoints = new JRadioButton("Add Points",true);
 		  buttonPanel.add(rdbtnAddPoints);
 		  modeSelector.add(rdbtnAddPoints);
 		  
 		  
 		  
 		  
 		  
 		  mapName = new JTextField();
 		  buttonPanel.add(mapName);
 		  mapName.setColumns(10);
 		  


 		  //creates a centered text field that will write back the users info they typed in
 		  roomNumber = new JTextField();
 		  buttonPanel.add(roomNumber);
 		  roomNumber.setHorizontalAlignment(JTextField.CENTER);
 		  roomNumber.setText("Select a Point to Edit");
 		  roomNumber.setToolTipText("");
 		  roomNumber.setBounds(6, 174, 438, 30);
 		  roomNumber.setColumns(1);
 		  
 		  
 		  rdbtnRemovePoints = new JRadioButton("Remove Points");
 		  buttonPanel.add(rdbtnRemovePoints);
 		  modeSelector.add(rdbtnRemovePoints);
 		  
 		  Component horizontalStrut = Box.createHorizontalStrut(20);
 		  buttonPanel.add(horizontalStrut);
 		  
 		  Component horizontalStrut_1 = Box.createHorizontalStrut(20);
 		  buttonPanel.add(horizontalStrut_1);
 		  
 		  rdbtnEditPoints = new JRadioButton("Edit Points");
 		  buttonPanel.add(rdbtnEditPoints); 
 		  modeSelector.add(rdbtnEditPoints); 

 		  
 		  JLabel lblLastPoint = new JLabel("Select a Point to Edit");
 		  buttonPanel.add(lblLastPoint);
 		  
 		  /*JButton*/ 
		 btnSavePoint = new JButton("No Point Selected");
		 buttonPanel.add(btnSavePoint);
		 btnSaveMap = new JButton("Save Map"); //defined above to change text in point selector
         buttonPanel.add(btnSaveMap);
         getContentPane().add(drawPanel);

         btnSavePoint.addActionListener(new ActionListener(){
             
             @Override
             public void actionPerformed(ActionEvent e) {
             	System.out.println("SavePoint");
             	editPoint.setName(roomNumber.getText());
             	roomNumber.setText("Select a Point to Edit");
             	editingPoint = false;
             }
         });
         


    }
    /*Returns the currently selected radbutton in the form of an 
    int. 1 for addPoint, 2 for editPoint, 3 for removePoint*/
    int getRadButton()
    {
    	int activeButton = 0;
    	if(rdbtnAddPoints.isSelected())
    		activeButton = 1;
    	if(rdbtnEditPoints.isSelected())
    		activeButton = 2;
    	if(rdbtnRemovePoints.isSelected())
    		activeButton = 3;
    	return activeButton;
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
    

    class DrawRoute extends JPanel{
		ArrayList<Point> markForDelete = new ArrayList<Point>();
    	//Point editPoint;
    	//Driver values used for testing:
    	int pointID = 0;
    	int numEdges = 0;
    	int edgeWeight = 1;
    		
    	

    	
        @Override
        public void paintComponent(Graphics g) {
       
            super.paintComponent(g);
            g.drawImage(img, 0, 0, null);
           

		            addMouseListener(new MouseAdapter() {
		                
		                public void mouseReleased(MouseEvent e) {
		                	newClick = false;
		                    lastMousex =  e.getX();
		                    lastMousey =  e.getY();
		                    newClick = true;
		                }

		            });


		            
		            //add point to the point array (has to take place outside of below loop)
		            if(newClick == true)
		            {
		            	System.out.println(newClick);
		                if(getRadButton()==1) //if addpoint
		               		{
		                		Integer arraySize = pointArray.size();
					            Point point = new Point(arraySize, "Point " + arraySize.toString(), lastMousex, lastMousey, numEdges);
				                pointArray.add(point); 
		               		}
		            }
		            
		            
            //draws all the points onto the map.
		            //cleans the array of deleted points.
		    if(pointArray.size()>0)
		    {
	            for(int i=0;i<pointArray.size();i++)
	            {			    
	
	            	currentPoint = pointArray.get(i);	
	            	int drawX = (int) currentPoint.getX();
	            	int drawY = (int) currentPoint.getY();
	            	//draws the points onto the map.
	            	g.fillOval(drawX -(pointSize/2), drawY -(pointSize/2), pointSize, pointSize);
	            	
	            	
	            
	            	
	        			for (int j = 0; j < edgeArray.size(); j++) {
	            			g.drawLine(edgeArray.get(j).getPoint1().getX(),edgeArray.get(j).getPoint1().getY(),
	            					edgeArray.get(j).getPoint2().getX(),edgeArray.get(j).getPoint2().getY());
	            			
						}
	        			

	            	//add edges to list
	            	for(int j=0;j<currentPoint.getNumEdges();j++)
	            	{
	            		edgeArray.add(currentPoint.getEdges().get(j));
	            	}	
	                //newClick has some interesting storage things going on. wtf is with Java
		            if(newClick == true)
		            {
	
		                MapUpdaterGUI.btnSaveMap.setText("Save Map, X:" + lastMousex + ", Y:" + lastMousey);
		                
			                switch (getRadButton()) 
			                {
				    			case 2://edit points				    				
				    				if(		   (lastMousex>currentPoint.getX()-(pointSize+5) 
											&& 	lastMousex<currentPoint.getX()+(pointSize+5))
											&& (lastMousey>currentPoint.getY()-(pointSize+5)
											&& 	lastMousey<currentPoint.getY()+(pointSize+5)))
										{
											if(newClick ==true && editingPoint == false)
											{
												editPoint = currentPoint;
												roomNumber.setText(editPoint.getName());
												btnSavePoint.setText("Save");
												editingPoint = true;
												newClick =false;
											}else if(newClick == true && editingPoint == true)
											{
												currentEdge = new Edge(editPoint,currentPoint,edgeWeight);
												if(currentPoint.getNumberEdges()>0)//this has to be caught in an exception later
												{
													for (int j = 0; j < currentPoint.getNumberEdges(); j++) {
														System.out.println("add edge: "+
																currentPoint.getEdges().get(j).getPoint1().getName() 
																+ ", " +
																currentPoint.getEdges().get(j).getPoint2().getName());	
													}
												}
												newClick = false;
											}
										}
				    				break;
				    			case 3://remove points
									if(		(	lastMousex>currentPoint.getX()-(pointSize+5) 
											&& 	lastMousex<currentPoint.getX()+(pointSize+5))
											&& (lastMousey>currentPoint.getY()-(pointSize+5)
											&& 	lastMousey<currentPoint.getY()+(pointSize+5)))
										{
										if(newClick ==true)
											markForDelete.add(currentPoint);
										
										newClick =false;
										}
				    	            break;
				    			default:
				    				break;
			    			}
		            }
	            }	            
            }
		    for (int i = 0; i < markForDelete.size(); i++) {	    	
            	//remove edges to list
            	for(int j=0;j<markForDelete.get(i).getNumEdges();j++)
            	{
            		edgeArray.clear();
            	}	
            	
            	markForDelete.get(i).deleteEdges();
				pointArray.remove(markForDelete.get(i));
				markForDelete.remove(i);
			}
		    
            newClick = false;
            repaint();
        }



    }


}
