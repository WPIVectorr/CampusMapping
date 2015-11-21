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

	private BufferedImage img = null;

	//drop down menu of room numbers based off of the building selected on campus

	String rooms[] = {"Select room #", "Please choose building first"};

	private ArrayList<Map> maps = new ArrayList<Map>();
	private ArrayList<Point> route;
	private String[] textDir;
	private int textPos;
	private int currentStep;
	private Point start;
	private Point end;
	private boolean showRoute;
	private JTextField textField;
	private JPanel buttonPanel;
	private DrawRoute drawPanel = new DrawRoute();
	private int windowScale = 2;
	private int windowSizeX = 932;
	private int windowSizeY = 778;

	private int buildStartIndex;
	private int buildDestIndex;

	public GUI() throws IOException, AlreadyExistsException, SQLException{
		super("GUI");
		setSize(932, 778);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//  img = ImageIO.read(new File("temp.jpg"));
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		MappingDatabase.initDatabase();

		md.initDatabase();
		//md.testMaps();
		maps = md.getMaps();
		System.out.println("-------------------------------------------");
		System.out.println("maps size:"+maps.size());
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		//System.out.println("testMaps: " + md.getMaps().size());
		//maps = md.getMaps();
		System.out.println("------------------edges check-------------------");
		maps.get(0).getPointList().get(0).print();
		//maps.get(index)
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/*
		// Stub for testing 
<<<<<<< HEAD
=======

>>>>>>> refs/remotes/origin/master
>>>>>>> refs/remotes/origin/master
		// Stub for testing 
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
<<<<<<< HEAD

=======
		/*System.out.println("```````````````````````````````````````````````````````````");
	System.out.println(maps.equals(maps1));
	System.out.println("maps.get(0)/(1) " + maps.get(0).getName() + " " + maps.get(1).getName());
	System.out.println("maps1.get(0)/(1) " + maps1.get(0).getName() + " " + maps1.get(1).getName());
System.out.println("mapslistsize: " + maps.get(1).getPointList().size());
System.out.println("maps1listSize " + maps1.get(1).getPointList().size());/*
>>>>>>> refs/remotes/origin/master
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
				////////////////////////////////////////////////////////////////////////////////

				String mapTitle = maps.get(buildDestIndex-1).getName();
				//String mapTitle = "AtwaterKent1";
				File dest = new File("src/VectorMaps");
				String destInput = dest.getAbsolutePath();
				//assuming all maps saved in vectorMaps are in jpg
				destInput = destInput + "/" + mapTitle + ".jpg";

				File destFile = new File(destInput);
				try{
					img = ImageIO.read(destFile);
				}
				catch(IOException a){
					System.out.println("Could not find file:"+destInput);
					a.printStackTrace();
				}

				System.out.println("DIe die: " +maps.get(0).getPointList().size());
				System.out.println("points: ");
				for(int count = 0; count < maps.get(0).getPointList().size(); count++){
					System.out.println(maps.get(0).getPointList().get(count).getName());
				}
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
						System.out.println("startBuildsSize: " + maps.get(buildDestIndex-1).getPointList().size());
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
				// reset text position index
				textPos = 0;


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
					// Array is stored end to start, so step 1 is actually the last step
					// Also arrays start at 0, so need to subtract 1
					currentStep = route.size() - 1;
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
						textDir = gentextdir.genTextDir(route);
						for(int i = 0; i < textDir.length; i++){
							//System.out.println(directions[i]);
						}
						textField.setText(textDir[0]);

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

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		buttonPanel.add(horizontalStrut_1);

		JButton btnPrevious = new JButton("Previous");
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textPos == 0 || textDir == null){

				}
				else{
					textPos--;
					textField.setText(textDir[textPos]);
				}
				// Routes are stored end to beginning, so increment to "step backward"
				if(currentStep != route.size() - 1){
					currentStep++;
				}
				repaint();
			}
		});
		buttonPanel.add(btnPrevious);



		//creates a centered text field that will write back the users info they typed in
		textField = new JTextField();
		buttonPanel.add(textField);
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setToolTipText("");
		textField.setBounds(6, 174, 438, 30);
		textField.setColumns(1);

		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(textDir == null || textPos == textDir.length - 1){

				}
				else {
					textPos++;
					textField.setText(textDir[textPos]);
				}
				// Routes are stored end to beginning, so decrement to "step forward"
				if(currentStep != 0){
					currentStep--;
				}
				repaint();
			}
		});
		buttonPanel.add(btnNext);

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
			Graphics2D g2 = (Graphics2D) g;
			double wScale;
			if(img != null){
				if (img.getHeight() >= img.getWidth()) {
					wScale = (double) img.getHeight() / (double) windowSizeY;
					windowScale = img.getHeight() / windowSizeY;
				} else {
					wScale = (double) img.getHeight() / (double) windowSizeY;
					windowScale = img.getWidth() / windowSizeX;
				}
				if (wScale > windowScale)
					windowScale += 1;

				g.drawImage(img, 0, 0, img.getWidth() / windowScale, img.getHeight() / windowScale, null);


				if (showRoute && route != null){
					// Draw green lines for all points up to currentStep point
					g.setColor(Color.GREEN);
					g2.setStroke(new BasicStroke(3));
					for (int i = currentStep + 1; i < route.size(); i++){
						g2.drawLine(route.get(i).getX(), route.get(i).getY(), route.get(i-1).getX(), route.get(i-1).getY());


					}
					// Special case for currentStep = 0, don't draw line or array out of bounds error
					// Otherwise, draw yellow line from currentStep point to previous point
					if (currentStep != 0){
						g2.setStroke(new BasicStroke(6));
						g.setColor(Color.YELLOW);
						g2.drawLine(route.get(currentStep).getX(), route.get(currentStep).getY(), route.get(currentStep-1).getX(), route.get(currentStep-1).getY());
						g2.setStroke(new BasicStroke(3));
					}
					// Draw red lines for rest of points
					g2.setColor(Color.RED);
					for (int i = 0; i < currentStep - 1; i++){
						g2.drawLine(route.get(i).getX(), route.get(i).getY(), route.get(i+1).getX(), route.get(i+1).getY());
					}
				}
			}
		}
	}
}