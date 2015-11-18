package main_package;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import database.AlreadyExistsException;
import database.MappingDatabase;

public class GUI extends JFrame{
	MappingDatabase md = MappingDatabase.getInstance();

	BufferedImage img = null;

	//drop down menu of room numbers based off of the building selected on campus
	String rooms[] = {"Select room #", "Please choose building first"};

	private ArrayList<Map> maps = new ArrayList<Map>();
	private ArrayList<Point> route;
	private Point start;
	private Point end;
	private boolean showRoute;
	private JTextField textField;
	private JTextField txtStartingLocation;
	private JTextField txtDestination;
	private JPanel buttonPanel;
	private DrawRoute drawPanel = new DrawRoute();

	private int buildStartIndex;
	private int buildDestIndex;

	public GUI() throws IOException, AlreadyExistsException, SQLException{
		super("GUI");
		setSize(932, 778);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//  img = ImageIO.read(new File("temp.jpg"));
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		md.initDatabase();
		md.testMaps();
		//System.out.println("testMaps: " + md.getMaps().size());
		maps = md.getMaps();
		System.out.println("------------------edges check-------------------");
		maps.get(0).getPointList().get(0).print();
		//maps.get(index)
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		/*// Stub for testing 
		ArrayList<Map> maps = new ArrayList<Map>();
		Point testPoint1 = new Point (1, "One", 50, 100);
		Point testPoint2 = new Point (2, "Two", 600, 500);
		Point testPoint3 = new Point (3, "Three", 500, 700);
		Point testPoint4 = new Point (4, "Four", 200, 200);
		Edge e1 = new Edge(testPoint1, testPoint2, 1);
		Edge e2 = new Edge(testPoint2, testPoint3, 1);
		Edge e3 = new Edge(testPoint1, testPoint4, 1);
		//testPoint1.addEdge(e1);
		//testPoint1.addEdge(e3);
		//testPoint2.addEdge(e1);
		//testPoint2.addEdge(e2);
		//testPoint3.addEdge(e2);
		//testPoint4.addEdge(e3);
		ArrayList<Point> testArrayList = new ArrayList<Point>();
		testArrayList.add(testPoint1);
		testArrayList.add(testPoint2);
		testArrayList.add(testPoint3);
		testArrayList.add(testPoint4);

		Point testPoint7 = new Point (7, "seven", 100, 500);
		Point testPoint5 = new Point (5, "Five", 500, 600);
		Point testPoint6 = new Point (6, "Six", 700, 500);
		Edge e7 = new Edge(testPoint7, testPoint6, 1);
		//testPoint7.addEdge(e7);
		ArrayList<Point> testArrayList2 = new ArrayList<Point>();
		testArrayList2.add(testPoint7);
		testArrayList2.add(testPoint5);
		testArrayList2.add(testPoint6);
		Map testMap = new Map(testArrayList, 1, "Campus");
		Map testMap2 = new Map(testArrayList2, 2, "AK");
		maps.add(testMap);
		maps.add(testMap2);
		/*System.out.println("```````````````````````````````````````````````````````````");
	System.out.println(maps.equals(maps1));
	System.out.println("maps.get(0)/(1) " + maps.get(0).getName() + " " + maps.get(1).getName());
	System.out.println("maps1.get(0)/(1) " + maps1.get(0).getName() + " " + maps1.get(1).getName());
System.out.println("mapslistsize: " + maps.get(1).getPointList().size());
System.out.println("maps1listSize " + maps1.get(1).getPointList().size());/*
		// Fill building drop down menus with names of points
		//int pointListSize = maps.get(0).getPointList().size();

		//buildings[0] = "Select a building";
		//for (int i = 0; i < pointListSize; i++){
		//buildings[i] = maps.get(0).getPointList().get(i);
		//}

		//creates string of rooms for dropdown menu
		String[] rooms = new String[maps.size()];
		rooms[0] = "Select Map";
		int count = -1;
		for(int i = 1; i < maps.size(); i++){
			count++;
			rooms[i] = maps.get(count).getName();
		}

		/*maps = MappingDatabase.getInstance().getMaps();
    	ArrayList<Map> maps = new ArrayList<Map>();
    	ArrayList<String> buildingsTest = new ArrayList<String>(); 
    	Point testPoint1 = new Point (1, "One", 50, 100);
    	Point testPoint2 = new Point (2, "Two", 50, 100);
    	Point testPoint3 = new Point (3, "Three", 50, 100);
    	ArrayList<Point> testArrayList = new ArrayList<Point>();
    	testArrayList.add(testPoint1);
    	testArrayList.add(testPoint2);
    	testArrayList.add(testPoint3);
    	Map testMap = new Map(testArrayList, 1, "Test");
    	maps.add(testMap);
    	String[] buildings = new String[maps.size()];
    	for (int i = 0; i < maps.size(); i++){
    		buildings[i] = maps.get(i).getName();
    	}*/

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4, 0, 10, 10));
		buttonPanel.setBackground(new Color(255, 235, 205));

		Container contentPane = this.getContentPane();
		contentPane.add(buttonPanel, BorderLayout.NORTH);
		// for (int i=0; i < buildings.length; i++){
		//	startBuilds.addItem(buildings[i]);
		//}




		/*adds the room numbers based off of building name
        startBuilds.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
            	startRooms.removeAllItems();
            	String buildSelectStart = (String)startBuilds.getSelectedItem();
                buildStartIndex = startBuilds.getSelectedIndex();
                int startRoomSize = maps.get(buildStartIndex).getPointList().size();
                String[] startRoomArray = new String[startRoomSize];
                startRoomArray[0] = "Select a room";
            }
        });*/

		//creates the drop down box with rooms for start (initially waits for the building to have 
		//the specific buildings room numbers)

		//	         buttonPanel.add(startRooms);
		//startRooms.setBounds(296, 30, 148, 20);

		JComboBox<Point> startBuilds = new JComboBox();
		JComboBox<Point> destBuilds = new JComboBox();
		JComboBox mapsDropdown = new JComboBox();


		JLabel lblMaps = new JLabel("Select Map:");
		lblMaps.setHorizontalAlignment(SwingConstants.CENTER);
		buttonPanel.add(lblMaps);

		//adds the starting location label to the line with starting location options
		JLabel lblStartingLocation = new JLabel("Starting Location:");
		buttonPanel.add(lblStartingLocation);
		lblStartingLocation.setBounds(6, 31, 119, 16);
		mapsDropdown.addItem("Select Map");
		for(int i = 0; i < maps.size(); i++){	
			mapsDropdown.addItem(maps.get(i).getName());
		}

		//creates drop down box with building names
		buttonPanel.add(startBuilds);
		startBuilds.setBounds(122, 30, 148, 20);



		//creates a dropdown menu with map names
		buttonPanel.add(mapsDropdown);

		//adds the destination label to the line with destination location options
		JLabel lblDestination = new JLabel("Destination:");
		buttonPanel.add(lblDestination);
		lblDestination.setBounds(6, 68, 85, 44);
		lblDestination.setLabelFor(destBuilds);
		//adds destBuilds to the dropdown for destination
		buttonPanel.add(destBuilds);
		destBuilds.setBounds(122, 30, 148, 20);
		/*for (int i=0; i < buildings.length; i++){
			destBuilds.addItem(buildings[i]);
		}*/
		//buttonPanel.add(destBuilds);
		destBuilds.setBounds(122, 80, 148, 20);


		//adds the correct points for the building specified
		mapsDropdown.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("inside action listener");
				buildDestIndex = mapsDropdown.getSelectedIndex();
				//if(buildDestIndex == -1)
				//buildDestIndex = 0;
				//System.out.println("buildDest: " + buildDestIndex);
				//int pointListSize = maps.get(buildDestIndex).getPointList().size();
				//Point[] buildings = new Point[pointListSize];
				startBuilds.removeAllItems();
				destBuilds.removeAllItems();
				if(buildDestIndex!=0){
					//System.out.println("building size: " + buildings.length);
					for (int i = 0; i < maps.get(buildDestIndex-1).getPointList().size(); i++){
						startBuilds.addItem(maps.get(buildDestIndex-1).getPointList().get(i));
						//System.out.println("buildings[i] " + buildings[i]);

						// destRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectDest)));
					}
					for (int i = 0; i < maps.get(buildDestIndex-1).getPointList().size(); i++){
						destBuilds.addItem(maps.get(buildDestIndex-1).getPointList().get(i));
						//System.out.println("buildings[i] " + buildings[i]);

						// destRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectDest)));
					}
				}
				//startBuilds.removeAllItems();
				//for (int i=0; i < buildings.length; i++){
				//System.out.println("buildings[i] match: " + buildings[i]);
				//startBuilds.addItem(buildings[i]);
				//}
				//destBuilds.removeAllItems();
				//for (int i=0; i < buildings.length; i++){
				///destBuilds.addItem(buildings[i]);
				//}
			}
		});
		/*
		JLabel lblMaps = new JLabel("Select Map:");
		lblMaps.setHorizontalAlignment(SwingConstants.CENTER);
		buttonPanel.add(lblMaps);
		//adds the starting location label to the line with starting location options
		JLabel lblStartingLocation = new JLabel("Starting Location:");
		buttonPanel.add(lblStartingLocation);
		lblStartingLocation.setBounds(6, 31, 119, 16);
		mapsDropdown.addItem("Select Map");
		for(int i = 0; i < maps.size(); i++){	
			mapsDropdown.addItem(maps.get(i).getName());
		}*/
		/*for (int i=0; i < buildings.length; i++){
			System.out.println("buildings[i] match: " + buildings[i]);
			startBuilds.addItem(buildings[i]);
		}*/



		//JComboBox destRooms = new JComboBox(rooms);


		//creates the drop down of room numbers for destination (initially waits for the building to have 
		//the specific buildings room numbers)
		//	buttonPanel.add(destRooms);
		//destRooms.setBounds(296, 80, 148, 20);


		Component horizontalStrut = Box.createHorizontalStrut(20);
		buttonPanel.add(horizontalStrut);

		JButton directionsButton = new JButton("Directions");
		buttonPanel.add(directionsButton);
		directionsButton.setBackground(new Color(0, 255, 127));
		directionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//gets the start and end building and room numbers the user chose

				start = (Point) startBuilds.getSelectedItem();
				end = (Point) destBuilds.getSelectedItem();
				if(!start.equals(end)){



					System.out.println("--------------------astar--------------------------------");
					start.print();
					end.print();
					AStar astar = new AStar();
					astar.reset();
					/*for (int i=0; i<route.size();i++){
 		  			route.set(i, null);
 		  		}*/
					route = astar.PathFind(start, end);
					System.out.println("route variable: " + (route == null));
					System.out.println("Hey");
					if(route != null){
						System.out.println("route: ");
						for(int i = route.size() - 1; i >= 0; i--){
							System.out.println(route.get(i));
						}
					}
					showRoute = true;
					if (route == null){
						textField.setText(start.getName() + "->" + end.getName());
					}
					else{
						//System.out.println(route.size());
						GenTextDir gentextdir = new GenTextDir();
						String[] directions; // = new String[route.size() + 1];
						directions = gentextdir.genTextDir(route);
						for(int i = 0; i < directions.length; i++){
							//System.out.println(directions[i]);
						}
						textField.setText(directions[0]);

					}

					repaint();
				}
				//if the points are identical, asks the user to input different points
				else{
					textField.setText("Pick two different points");
					repaint();
				}
			}
		});


		directionsButton.setBounds(187, 132, 94, 30);



		//creates a centered text field that will write back the users info they typed in
		textField = new JTextField();
		buttonPanel.add(textField);
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setToolTipText("");
		textField.setBounds(6, 174, 438, 30);
		textField.setColumns(1);

		getContentPane().add(drawPanel);

	}

	public static void main(String[] args) throws IOException, AlreadyExistsException, SQLException{
		GUI myTest = new GUI();
		myTest.setVisible(true);
	}

	class DrawRoute extends JPanel {

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, null);

			if (showRoute && route != null){           
				for (int i = 1; i < route.size(); i++){
					//System.out.println(route.get(i));
					g.drawLine(route.get(i-1).getX(), route.get(i-1).getY(), route.get(i).getX(), route.get(i).getY());
				}
			}
		}
	}
}
