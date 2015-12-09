package main_package;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;
import javax.mail.internet.AddressException;
import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import database.AlreadyExistsException;
import database.ServerDB;

public class GUI{
	boolean DEBUG = false;
	ServerDB md = ServerDB.getInstance();

	private BufferedImage img = null;

	// Array of strings to initally populate dropdown menus with
	String rooms[] = {"Select room #", "Please choose building first"};

	private ArrayList<Map> maps = new ArrayList<Map>();
	private ArrayList<Point> route;
	private ArrayList<ArrayList<String>> textDir;
	private int mapPos;
	private int textPos;
	private Point start;
	private Point end;
	private boolean showRoute;
	private JTextField directionsText;
	private JPanel mainMenu;
	private JPanel navMenu;
	private JPanel prefMenu;
	private JPanel menus;
	CardLayout menuLayout;
	private String returnMenu;
	private GradientButton btnNext;
	private GradientButton btnPrevious;
	private DrawRoute drawPanel = new DrawRoute();
	private double windowScale = 2;
	private int windowSizeX = 932;
	private int windowSizeY = 778;
	private ArrayList<Directions> finalDir = null;
	private ArrayList<ArrayList<Directions>> multiMapFinalDir = null;
	private ArrayList<Map> dirMaps = null;
	private ArrayList<Point> allPoints = null;
	private int buildStartIndex;
	private int buildDestIndex;
	private Color previousColor = new Color(255, 75, 75);
	private Color currentColor = new Color(219, 209, 0);
	private Color nextColor = new Color(51, 255, 51);
	private Color pointColor = Color.ORANGE;
	private Color backgroundColor = new Color(255, 235, 205);
	private Color buttonColor = new Color(153, 204, 255);
	private ArrayList<Point> pointArray;
	private ArrayList<Edge> edgeArray;
	private JFrame frame = new JFrame("Directions with Magnitude");
	JComboBox<String> mapsDropdown = new JComboBox();
	JComboBox<Point> destBuilds = new JComboBox();
	JComboBox<Point> startBuilds = new JComboBox();
	JComboBox DestMaps = new JComboBox();
	private int pointSize = 16;
	private int originalpointSize = 25;
	private double scaleSize = 1;
	private int drawnposx;
	private int drawnposy;
	private boolean drawnfirst = false;
	private int screenHeight;
	private int screenWidth;
	private double imageX;
	private double imageY;
	private int scroldirection;
	private boolean atMaxZoom = false;
	private boolean atMinZoom = false;
	private boolean Dragged = false;
	private int mousex;
	private int mousey;
	private int originx;
	private int originy;
	private double difWidth;
	private double difHeight;
	private double newImageHeight;
	private double newImageWidth;
	private boolean scrolled = false;
	private double upperx;
	private double uppery;
	private int timeEst = 0;
	private int outside;
	private int stairs;
	private double walkSpeed = 4.5;
	Map startMap;
	private String mapTitle = "Select Map";
	private static SplashPage loadingAnimation;
	private JTextField txtFieldEmail;
	private JTextField txtTimeToDestination;
	private boolean resetPath = false;
	private GradientButton btnSwapStartAndDest;
	private GradientButton directionsButton;

	public void createAndShowGUI() throws IOException, AlreadyExistsException, SQLException{

		//frame.setSize(932, 778);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(800, 600));

		//added by JPG scales the GUI to the screensize.
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		double frameHeight = screenSize.height*4/5;
		double frameWidth = frameHeight*(1.15);
		frame.setSize((int)frameWidth, (int)frameHeight);
		double xlocation = (screenSize.width / 2)-(frame.getWidth()/2);
		double ylocation = (screenSize.height / 2)-(frame.getHeight()/2);
		frame.setLocation((int)xlocation, (int)ylocation);

		frame.getContentPane().setBackground(backgroundColor);

		maps = md.getMapsFromLocal();
		allPoints = new ArrayList<Point>();
		for(int i = 0; i < maps.size(); i++){
			for(int j = 0; j < maps.get(i).getPointList().size(); j++){
				allPoints.add(maps.get(i).getPointList().get(j));
			}
		}
		//System.out.println("------------------edges check-------------------");


		mainMenu = new JPanel();
		mainMenu.setBackground(backgroundColor);

		navMenu = new JPanel();
		navMenu.setBackground(backgroundColor);

		GridBagLayout gbl_navMenu = new GridBagLayout();
		gbl_navMenu.columnWidths = new int[]{0, 298, 298, 298, 0, 0};
		gbl_navMenu.rowHeights = new int[]{15, 19, 0, 0, 31, 30, 7, 0};
		gbl_navMenu.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_navMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		navMenu.setLayout(gbl_navMenu);

		menus = new JPanel(new CardLayout());
		menuLayout = (CardLayout) menus.getLayout();
		menus.add(mainMenu, "Main Menu");
		menus.add(navMenu, "Nav Menu");
		menus.add(createPrefMenu(), "Pref Menu");

		GradientButton btnSavePreferences = new GradientButton("Save Preferences", buttonColor);
		GridBagConstraints gbc_btnSavePreferences = new GridBagConstraints();
		gbc_btnSavePreferences.gridwidth = 7;
		gbc_btnSavePreferences.insets = new Insets(0, 0, 5, 0);
		gbc_btnSavePreferences.gridx = 0;
		gbc_btnSavePreferences.gridy = 5;
		prefMenu.add(btnSavePreferences, gbc_btnSavePreferences);
		// Return to previous view
		btnSavePreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If the position in the route should be reset, does that
				if (resetPath){
					textPos = 0;
					mapPos = 0;
					resetPath = false;

					double estimatedDirDist = 0;
					for(int g = 0; g < multiMapFinalDir.size(); g++){
						for(int p = 0; p < multiMapFinalDir.get(g).size(); p++){
							estimatedDirDist+=multiMapFinalDir.get(g).get(p).getDistance();
						}
					}

					timeEst = (int) (estimatedDirDist / walkSpeed);
					int minEst = (int) Math.floor(timeEst / 60);
					int secEst = timeEst % 60;
					txtTimeToDestination.setText("Estimated Time to Destination: " + minEst + ":" + secEst);
				}
				// Set button colors based on preferences selected
				btnPrevious.setColor(previousColor);
				btnNext.setColor(nextColor);
				// Return to view that preferences menu was accessed from
				menuLayout.show(menus, returnMenu);
				frame.repaint();
			}
		});


		frame.getContentPane().add(menus, BorderLayout.NORTH);

		JTextPane txtpnFullTextDir = new JTextPane();


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
		GridBagLayout gbl_mainMenu = new GridBagLayout();
		gbl_mainMenu.columnWidths = new int[]{15, 24, 210, 132, 220, 15, 0};
		gbl_mainMenu.rowHeights = new int[]{27, 27, 27, 0, 0, 0};
		gbl_mainMenu.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_mainMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		mainMenu.setLayout(gbl_mainMenu);
		mapsDropdown.addItem("Select Map");
		DestMaps.addItem("Select Map");
		boolean check = true;
		ArrayList<String> temp = new ArrayList<String> ();
		for(int i = 0; i < maps.size(); i++){	
			if(i > 0){
				for(int count = i-1;count >= 0 ; count--){
					if(maps.get(i).getMapName().compareTo(maps.get(count).getMapName()) == 0){
						check = false;
						count = -1;
					}
				}
			}
			if(check){
				temp.add(maps.get(i).getMapName());
				//mapsDropdown.addItem(maps.get(i).getMapName());
				//DestMaps.addItem(maps.get(i).getMapName());
			}

		}

		Collections.sort(temp);
		Collections.sort(maps);
		for(int count = 0; count < temp.size(); count++){
			mapsDropdown.addItem(temp.get(count));
			DestMaps.addItem(temp.get(count));
		}
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_1.gridx = 0;
		gbc_horizontalStrut_1.gridy = 1;
		mainMenu.add(horizontalStrut_1, gbc_horizontalStrut_1);


		JLabel lblMaps = new JLabel("Starting Map:");
		lblMaps.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblMaps = new GridBagConstraints();
		gbc_lblMaps.anchor = GridBagConstraints.EAST;
		gbc_lblMaps.fill = GridBagConstraints.VERTICAL;
		gbc_lblMaps.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaps.gridx = 1;
		gbc_lblMaps.gridy = 1;
		mainMenu.add(lblMaps, gbc_lblMaps);



		//creates a dropdown menu with map names
		GridBagConstraints gbc_mapsDropdown = new GridBagConstraints();
		gbc_mapsDropdown.fill = GridBagConstraints.BOTH;
		gbc_mapsDropdown.insets = new Insets(0, 0, 5, 5);
		gbc_mapsDropdown.gridx = 2;
		gbc_mapsDropdown.gridy = 1;
		mainMenu.add(mapsDropdown, gbc_mapsDropdown);

		//adds the correct points for the building specified
		mapsDropdown.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {

				if (mapsDropdown.getSelectedItem().equals("Select Map")){
					startBuilds.removeAllItems();
					startBuilds.setEnabled(false);
					btnSwapStartAndDest.setEnabled(false);
					directionsButton.setEnabled(false);
				}
				else{
					startBuilds.setEnabled(true);
					if (destBuilds.isEnabled()){
						btnSwapStartAndDest.setEnabled(true);
						directionsButton.setEnabled(true);
					}
					buildStartIndex = mapsDropdown.getSelectedIndex();


					mapTitle = maps.get(buildStartIndex-1).getMapName();
					//String mapTitle = "AtwaterKent1";

					File start = new File("src/VectorMaps");
					String startInput = start.getAbsolutePath();
					//assuming all maps saved in vectorMaps are in jpg
					startInput = startInput + "/" + mapTitle + ".png";

					File destFile = new File(startInput);
					try{
						img = ImageIO.read(destFile);
						frame.repaint();
					}
					catch(IOException a){
						System.out.println("Could not find file:"+startInput);
						a.printStackTrace();
					}

					startBuilds.removeAllItems();
					//destBuilds.removeAllItems();
					if(buildStartIndex!=0){
						edgeArray = new ArrayList<Edge>();

						pointArray = maps.get(buildStartIndex - 1).getPointList();

						for(int i = 0; i < pointArray.size(); i++){
							for(int j = 0; j < pointArray.get(i).getEdges().size(); j++){
								edgeArray.add(pointArray.get(i).getEdges().get(j));
							}
						}
						ArrayList<Point> tempStartRoom = new ArrayList<Point>();
						boolean check = true;
						System.out.println("number of points: " + maps.get(buildStartIndex-1).getPointList().size());
						for (int i = 0; i < maps.get(buildStartIndex-1).getPointList().size(); i++){
							if(!maps.get(buildStartIndex-1).getPointList().get(i).getName().equalsIgnoreCase("Hallway") &&
									!maps.get(buildStartIndex-1).getPointList().get(i).getName().equalsIgnoreCase("Stairs") &&
									!maps.get(buildStartIndex-1).getPointList().get(i).getName().equalsIgnoreCase("Path")){
								if(i > 0){
									System.out.println("i>0");
									for(int count = i-1;count >= 0 ; count--){
										if(maps.get(buildStartIndex - 1).getPointList().get(i).getName().compareTo(maps.get(buildStartIndex-1).getPointList().get(count).getName()) == 0){
											System.out.println("here");
											check = false;
											count = -1;
										}
									}
								}
								if(check){
									tempStartRoom.add(maps.get(buildStartIndex - 1).getPointList().get(i));
									//mapsDropdown.addItem(maps.get(i).getMapName());
									//DestMaps.addItem(maps.get(i).getMapName());
								}

							}

							if(DEBUG)
								System.out.println("startBuildsSize: " + maps.get(buildStartIndex-1).getPointList().size());
						}
						Collections.sort(tempStartRoom);
						System.out.println("tempStartRoom size: " + tempStartRoom.size());
						//tempStartRoom = sort(tempStartRoom);
						for(int count = 0; count < tempStartRoom.size(); count++){
							startBuilds.addItem(tempStartRoom.get(count));

						}
						//System.out.println("buildings[i] " + buildings[i]);

						// destRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectDest)));
						//}

						/*for (int i = 0; i < maps.get(buildDestIndex-1).getPointList().size(); i++){
						if(!maps.get(buildDestIndex-1).getPointList().get(i).getName().equals("Hallway")){
							destBuilds.addItem(maps.get(buildDestIndex-1).getPointList().get(i));
						}*/
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
		}
				);
		//adds the correct points for the building specified
		DestMaps.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (DestMaps.getSelectedItem().equals("Select Map")){
					destBuilds.removeAllItems();
					destBuilds.setEnabled(false);
					btnSwapStartAndDest.setEnabled(false);
					directionsButton.setEnabled(false);
				}
				else{

					destBuilds.setEnabled(true);
					if (startBuilds.isEnabled()){
						btnSwapStartAndDest.setEnabled(true);
						directionsButton.setEnabled(true);
					}

					buildDestIndex = DestMaps.getSelectedIndex();



					String mapTitle = maps.get(buildDestIndex-1).getMapName();
					//String mapTitle = "AtwaterKent1";

					File dest = new File("src/VectorMaps");
					String destInput = dest.getAbsolutePath();
					//assuming all maps saved in vectorMaps are in jpg
					destInput = destInput + "/" + mapTitle + ".png";

					File destFile = new File(destInput);
					try{
						img = ImageIO.read(destFile);
						frame.repaint();
					}
					catch(IOException a){
						System.out.println("Could not find file:"+destInput);
						a.printStackTrace();
					}

					//startBuilds.removeAllItems();
					destBuilds.removeAllItems();
					if(buildDestIndex!=0){
						edgeArray = new ArrayList<Edge>();

						pointArray = maps.get(buildDestIndex - 1).getPointList();

						for(int i = 0; i < pointArray.size(); i++){
							for(int j = 0; j < pointArray.get(i).getEdges().size(); j++){
								edgeArray.add(pointArray.get(i).getEdges().get(j));
							}
						}
						ArrayList<Point> tempDestRoom = new ArrayList<Point>();
						boolean check = true;
						//System.out.println("building size: " + buildings.length);
						for (int i = 0; i < maps.get(buildDestIndex-1).getPointList().size(); i++){
							if(!maps.get(buildStartIndex-1).getPointList().get(i).getName().equalsIgnoreCase("Hallway") &&
									!maps.get(buildStartIndex-1).getPointList().get(i).getName().equalsIgnoreCase("Stairs") &&
									!maps.get(buildStartIndex-1).getPointList().get(i).getName().equalsIgnoreCase("Path")){

								if(i > 0){
									System.out.println("i>0");
									for(int count = i-1;count >= 0 ; count--){
										if(maps.get(buildDestIndex - 1).getPointList().get(i).getName().compareTo(maps.get(buildStartIndex-1).getPointList().get(count).getName()) == 0){
											System.out.println("here");
											check = false;
											count = -1;
										}
									}
								}
								if(check){
									tempDestRoom.add(maps.get(buildDestIndex - 1).getPointList().get(i));
									//mapsDropdown.addItem(maps.get(i).getMapName());
									//DestMaps.addItem(maps.get(i).getMapName());
								}

							}

							if(DEBUG)
								System.out.println("startBuildsSize: " + maps.get(buildStartIndex-1).getPointList().size());
						}
						Collections.sort(tempDestRoom);
						System.out.println("tempDestRoom size: " + tempDestRoom.size());
						//tempStartRoom = sort(tempStartRoom);
						for(int count = 0; count < tempDestRoom.size(); count++){
							destBuilds.addItem(tempDestRoom.get(count));

						}
					}

					/*for (int i = 0; i < maps.get(buildDestIndex-1).getPointList().size(); i++){
						if(!maps.get(buildDestIndex-1).getPointList().get(i).getName().equals("Hallway")){
							destBuilds.addItem(maps.get(buildDestIndex-1).getPointList().get(i));
						}*/
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

		});


		//adds the starting location label to the line with starting location options
		JLabel lblStartingLocation = new JLabel("Starting Room:");
		GridBagConstraints gbc_lblStartingLocation = new GridBagConstraints();
		gbc_lblStartingLocation.anchor = GridBagConstraints.EAST;
		gbc_lblStartingLocation.fill = GridBagConstraints.VERTICAL;
		gbc_lblStartingLocation.insets = new Insets(0, 0, 5, 5);
		gbc_lblStartingLocation.gridx = 3;
		gbc_lblStartingLocation.gridy = 1;
		mainMenu.add(lblStartingLocation, gbc_lblStartingLocation);
		lblStartingLocation.setBounds(6, 31, 119, 16);

		//creates the drop down box with rooms for start (initially waits for the building to have 
		//the specific buildings room numbers)

		//	         buttonPanel.add(startRooms);
		//startRooms.setBounds(296, 30, 148, 20);


		// Initalize this button first so it can be used in return button
		GradientButton btnFullTextDirections = new GradientButton("Show Full Text Directions", buttonColor);



		//creates drop down box with building names
		GridBagConstraints gbc_startBuilds = new GridBagConstraints();
		gbc_startBuilds.fill = GridBagConstraints.BOTH;
		gbc_startBuilds.insets = new Insets(0, 0, 5, 5);
		gbc_startBuilds.gridx = 4;
		gbc_startBuilds.gridy = 1;
		startBuilds.setEnabled(false);
		mainMenu.add(startBuilds, gbc_startBuilds);
		startBuilds.setBounds(122, 30, 148, 20);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut.gridx = 5;
		gbc_horizontalStrut.gridy = 1;
		mainMenu.add(horizontalStrut, gbc_horizontalStrut);

		JLabel lblDestinationMap = new JLabel("Destination Map:");
		GridBagConstraints gbc_lblDestinationMap = new GridBagConstraints();
		gbc_lblDestinationMap.anchor = GridBagConstraints.EAST;
		gbc_lblDestinationMap.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestinationMap.gridx = 1;
		gbc_lblDestinationMap.gridy = 2;
		mainMenu.add(lblDestinationMap, gbc_lblDestinationMap);


		GridBagConstraints gbc_destMaps = new GridBagConstraints();
		gbc_destMaps.fill = GridBagConstraints.HORIZONTAL;
		gbc_destMaps.insets = new Insets(0, 0, 5, 5);
		gbc_destMaps.gridx = 2;
		gbc_destMaps.gridy = 2;
		mainMenu.add(DestMaps, gbc_destMaps);

		//adds the destination label to the line with destination location options
		JLabel lblDestination = new JLabel("Destination Room:");
		GridBagConstraints gbc_lblDestination = new GridBagConstraints();
		gbc_lblDestination.anchor = GridBagConstraints.EAST;
		gbc_lblDestination.fill = GridBagConstraints.VERTICAL;
		gbc_lblDestination.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestination.gridx = 3;
		gbc_lblDestination.gridy = 2;
		mainMenu.add(lblDestination, gbc_lblDestination);
		lblDestination.setBounds(6, 68, 85, 44);
		//adds destBuilds to the dropdown for destination
		GridBagConstraints gbc_destBuilds = new GridBagConstraints();
		gbc_destBuilds.fill = GridBagConstraints.BOTH;
		gbc_destBuilds.insets = new Insets(0, 0, 5, 5);
		gbc_destBuilds.gridx = 4;
		gbc_destBuilds.gridy = 2;
		destBuilds.setEnabled(false);
		mainMenu.add(destBuilds, gbc_destBuilds);
		destBuilds.setBounds(122, 30, 148, 20);

		//buttonPanel.add(destBuilds);
		destBuilds.setBounds(122, 80, 148, 20);
		lblDestination.setLabelFor(destBuilds);

		GradientButton btnSetPreferencesMain = new GradientButton("Set Preferences", buttonColor);
		GridBagConstraints gbc_btnSetPreferencesMain = new GridBagConstraints();
		gbc_btnSetPreferencesMain.insets = new Insets(0, 0, 5, 5);
		gbc_btnSetPreferencesMain.gridx = 2;
		gbc_btnSetPreferencesMain.gridy = 3;
		mainMenu.add(btnSetPreferencesMain, gbc_btnSetPreferencesMain);
		btnSetPreferencesMain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Note which menu to return to
				returnMenu = "Main Menu";
				// Show preferences menu
				menuLayout.show(menus, "Pref Menu");
			}
		});

		btnSwapStartAndDest = new GradientButton("Swap Start and Destination", buttonColor);
		btnSwapStartAndDest.setEnabled(false);
		GridBagConstraints gbc_btnSwapStartAndDest = new GridBagConstraints();
		gbc_btnSwapStartAndDest.insets = new Insets(0, 0, 5, 5);
		gbc_btnSwapStartAndDest.gridx = 4;
		gbc_btnSwapStartAndDest.gridy = 3;
		mainMenu.add(btnSwapStartAndDest, gbc_btnSwapStartAndDest);
		btnSwapStartAndDest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (startBuilds.getItemCount() != 0 && destBuilds.getItemCount() != 0){
					int startMapIndex = mapsDropdown.getSelectedIndex();
					int startPointIndex = startBuilds.getSelectedIndex();

					mapsDropdown.setSelectedIndex(DestMaps.getSelectedIndex());
					startBuilds.setSelectedIndex(destBuilds.getSelectedIndex());

					DestMaps.setSelectedIndex(startMapIndex);
					destBuilds.setSelectedIndex(startPointIndex);
				}
			}
		});

		// Button that generates a route and switches to nav display
		directionsButton = new GradientButton("Directions", new Color(0, 255, 127));
		directionsButton.setText("          Directions          ");
		directionsButton.setEnabled(false);
		GridBagConstraints gbc_directionsButton = new GridBagConstraints();
		gbc_directionsButton.gridwidth = 5;
		gbc_directionsButton.fill = GridBagConstraints.VERTICAL;
		gbc_directionsButton.gridx = 1;
		gbc_directionsButton.gridy = 4;
		mainMenu.add(directionsButton, gbc_directionsButton);
		directionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// reset text position and map position indexes
				textPos = 0;
				mapPos = 0;


				//gets the start and end building and room numbers the user chose

				for(int i = 0; i < allPoints.size(); i++){
					if(allPoints.get(i).getName().equals(startBuilds.getSelectedItem().toString())){
						start = allPoints.get(i);
						i = allPoints.size();
					}
				}
				for(int i = 0; i < allPoints.size(); i++){
					if(allPoints.get(i).getName().equals(destBuilds.getSelectedItem().toString())){
						end = allPoints.get(i);
						i = allPoints.size();
					}
				}

				if(!start.getId().equals(end.getId())){

					//System.out.println("--------------------astar--------------------------------");
					//start.print();
					//end.print();
					AStar astar = new AStar();
					astar.reset();

					route = astar.PathFind(start, end, outside, stairs);
					//System.out.println("route variable: " + (route == null));

					if(route != null){
						/*System.out.println("route: ");
																				for(int i = route.size() - 1; i >= 0; i--){
																					System.out.println(route.get(i));
																				}*/

					}
					showRoute = true;
					if (route == null){
						directionsText.setText(start.getName() + "->" + end.getName());
					}
					else{
						//System.out.println(route.size());
						GenTextDir gentextdir = new GenTextDir();
						ArrayList<Directions> tempDir = gentextdir.genTextDir(route, 2.8);
						//ArrayList<Directions> finalDir = null;
						try {
							finalDir = gentextdir.generateDirections(tempDir);
						} catch (MalformedDirectionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dirMaps = new ArrayList<Map>();
						if(multiMapFinalDir != null){
							multiMapFinalDir.clear();
						}
						multiMapFinalDir = gentextdir.genMultiMapDirections(finalDir);
						if(!(multiMapFinalDir.get(0).isEmpty() && multiMapFinalDir.size() == 1)){

							for(int r = 0; r < multiMapFinalDir.size(); r++){
								if(multiMapFinalDir.get(r).size() != 0){
									for(int s = 0; s < maps.size(); s++){
										if(multiMapFinalDir.get(r).get(0).getOrigin().getMapId() == maps.get(s).getMapId()){
											dirMaps.add(maps.get(s));
										}
									}
								} else if (r != 0 && multiMapFinalDir.get(r - 1).size() != 0){
									for(int s = 0; s < maps.size(); s++){
										if(multiMapFinalDir.get(r-1).get(0).getOrigin().getMapId() == maps.get(s).getMapId()){
											dirMaps.add(maps.get(s));
										}
									}
								} else if(r == 0){
									for(int s = 0; s < maps.size(); s++){
										if(multiMapFinalDir.get(r+1).get(0).getOrigin().getMapId() == maps.get(s).getMapId()){
											dirMaps.add(maps.get(s));
										}
									}
								}

							}

							try {
								textDir = gentextdir.genDirStrings(multiMapFinalDir);
							} catch (MalformedDirectionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							multiMapFinalDir.add(gentextdir.genTextDir(route, 2.8));
							try {
								textDir = gentextdir.genDirStrings(multiMapFinalDir);
							} catch (MalformedDirectionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						double estimatedDirDist = 0;
						for(int g = 0; g < multiMapFinalDir.size(); g++){
							for(int p = 0; p < multiMapFinalDir.get(g).size(); p++){
								estimatedDirDist+=multiMapFinalDir.get(g).get(p).getDistance();
							}
						}

						timeEst = (int) (estimatedDirDist / walkSpeed);
						int minEst = (int) Math.floor(timeEst / 60);
						int secEst = timeEst % 60;
						txtTimeToDestination.setText("Estimated Time to Destination: " + minEst + ":" + secEst);

						File destinationFile = new File("src/VectorMaps/" + dirMaps.get(mapPos).getMapName() + ".png");

						destinationFile = new File(destinationFile.getAbsolutePath());
						try {
							img = ImageIO.read(destinationFile);
						} catch (IOException g) {
							System.out.println("Invalid Map Selection");
							g.printStackTrace();
						}
						frame.repaint();
						for(int r = 0; r < multiMapFinalDir.size(); r++){
							if(multiMapFinalDir.get(r).size() == 0){
								mapPos++;
							} else {
								r = multiMapFinalDir.size();
							}
						}

						String fullText = " Full List of Directions:\n";
						directionsText.setText(textDir.get(mapPos).get(0));

						int tempPos = 0;
						for(int i = 0; i < textDir.size(); i++){
							tempPos++;
							for(int j = 0; j < textDir.get(i).size(); j++){
								tempPos++;
								fullText += " " + (tempPos + 1) + ". " + textDir.get(i).get(j) + "\n\n";
							}
						}



						txtpnFullTextDir.setText(fullText);						
					}

					frame.repaint();
					menuLayout.show(menus, "Nav Menu");
				}
				//if the points are identical, asks the user to input different points
				else{
					directionsText.setText("Pick two different points");
					frame.repaint();
				}
			}
		});


		directionsButton.setBounds(187, 132, 94, 30);
		/*for (int i=0; i < buildings.length; i++){
			destBuilds.addItem(buildings[i]);
		}*/

		//adds the logo to the front screen of the window
		File logo = new File("src/VectorLogo/VectorrLogo.png");
		logo = new File(logo.getAbsolutePath());
		//System.out.println("logoFinal: " + logo);
		try{
			img = ImageIO.read(logo);
		}
		catch(IOException g){
			System.out.println("Invalid logo1");
			g.printStackTrace();
		}

		// Text box for full list of directions, initially invisible, appears when directions button pressed
		txtpnFullTextDir.setText("Full List of Directions:");
		frame.getContentPane().add(txtpnFullTextDir, BorderLayout.WEST);
		txtpnFullTextDir.setVisible(false);
		txtpnFullTextDir.setEditable(false);
		Border textBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
		txtpnFullTextDir.setBorder(textBorder);

		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 2;
		gbc_verticalStrut.gridy = 0;
		navMenu.add(verticalStrut, gbc_verticalStrut);

		// Button to return to main menu
		GradientButton btnReturn = new GradientButton("Select New Route", buttonColor);
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Return to main menu, don't show route anymore
				menuLayout.show(menus, "Main Menu");
				showRoute = false;
				txtpnFullTextDir.setVisible(false);
				btnFullTextDirections.setText("Show Full Text Directions");
				frame.repaint();
			}
		});
		GridBagConstraints gbc_btnReturn = new GridBagConstraints();
		gbc_btnReturn.insets = new Insets(0, 0, 5, 5);
		gbc_btnReturn.gridx = 1;
		gbc_btnReturn.gridy = 1;
		navMenu.add(btnReturn, gbc_btnReturn);

		GradientButton btnSetPreferencesNav = new GradientButton("Set Preferences", buttonColor);
		GridBagConstraints gbc_btnSetPreferencesNav = new GridBagConstraints();
		gbc_btnSetPreferencesNav.insets = new Insets(0, 0, 5, 5);
		gbc_btnSetPreferencesNav.gridx = 3;
		gbc_btnSetPreferencesNav.gridy = 1;
		navMenu.add(btnSetPreferencesNav, gbc_btnSetPreferencesNav);
		btnSetPreferencesNav.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save which menu to return to
				returnMenu = "Nav Menu";
				// Change view to preferences menu, don't show route anymore
				menuLayout.show(menus, "Pref Menu");
				txtpnFullTextDir.setVisible(false);
				btnFullTextDirections.setText("Show Full Text Directions");
				frame.repaint();
			}
		});

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_2 = new GridBagConstraints();
		gbc_horizontalStrut_2.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_2.gridx = 0;
		gbc_horizontalStrut_2.gridy = 2;
		navMenu.add(horizontalStrut_2, gbc_horizontalStrut_2);

		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_3 = new GridBagConstraints();
		gbc_horizontalStrut_3.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_3.gridx = 4;
		gbc_horizontalStrut_3.gridy = 2;
		navMenu.add(horizontalStrut_3, gbc_horizontalStrut_3);



		//creates a centered text field that will write back the users info they typed in
		directionsText = new JTextField();
		directionsText.setEditable(false);
		directionsText.setHorizontalAlignment(JTextField.CENTER);
		directionsText.setToolTipText("");
		directionsText.setBounds(6, 174, 438, 30);
		directionsText.setColumns(1);
		GridBagConstraints gbc_directionsText = new GridBagConstraints();
		gbc_directionsText.gridwidth = 3;
		gbc_directionsText.fill = GridBagConstraints.HORIZONTAL;
		gbc_directionsText.insets = new Insets(0, 0, 5, 5);
		gbc_directionsText.gridx = 1;
		gbc_directionsText.gridy = 3;
		navMenu.add(directionsText, gbc_directionsText);


		// Button to get previous step in directions
		//sets the previous button color to green
		btnPrevious = new GradientButton("Previous", previousColor);
		btnPrevious.setEnabled(false);
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if((textPos == 0 && mapPos == 0) || textDir == null){

				}
				else if (textPos == 0){
					mapPos--;
					if(multiMapFinalDir.get(mapPos).size() == 0){
						mapPos++;
					} else {

						textPos = multiMapFinalDir.get(mapPos).size();
						//directionsText.setText(textDir.get(mapPos).get(textPos));
						directionsText.setText("Enter " + dirMaps.get(mapPos + 1).getMapName());
					}
					File destinationFile = new File("src/VectorMaps/" + dirMaps.get(mapPos).getMapName() + ".png");
					destinationFile = new File(destinationFile.getAbsolutePath());
					try {
						img = ImageIO.read(destinationFile);
					} catch (IOException g) {
						System.out.println("Invalid Map Selection");
						g.printStackTrace();
					}
					frame.repaint();
				} else {
					if (textPos != 0){
						timeEst += (Math.floor(multiMapFinalDir.get(mapPos).get(textPos-1).getDistance() / walkSpeed));
					}
					int minEst = (int) Math.floor(timeEst / 60);
					int secEst = timeEst % 60;
					txtTimeToDestination.setText("Estimated Time to Destination: " + minEst + ":" + secEst);
					textPos--;
					directionsText.setText(textDir.get(mapPos).get(textPos));
					if (!btnNext.isEnabled()){
						btnNext.setEnabled(true);
					}
					if (textPos == 0 && mapPos == 0){
						btnPrevious.setEnabled(false);
					}
				}
				frame.repaint();
			}
		});
		GridBagConstraints gbc_btnPrevious = new GridBagConstraints();
		gbc_btnPrevious.anchor = GridBagConstraints.NORTH;
		gbc_btnPrevious.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPrevious.insets = new Insets(0, 0, 5, 5);
		gbc_btnPrevious.gridx = 1;
		gbc_btnPrevious.gridy = 4;
		navMenu.add(btnPrevious, gbc_btnPrevious);

		// Button to get next step in directions
		//sets the next button color to red
		btnNext = new GradientButton("Next", nextColor);
		btnNext.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (textDir == null){

				}
				else {
					// Checks if incrementing textPos will set the array out of bounds
					// If it will, the user is at the end, display a message accordingly


					if(textPos < multiMapFinalDir.get(mapPos).size()){ 
						timeEst -= Math.floor((multiMapFinalDir.get(mapPos).get(textPos).getDistance()/walkSpeed));
						int minEst = (int) Math.floor(timeEst / 60);
						int secEst = timeEst % 60;
						txtTimeToDestination.setText("Estimated Time to Destination: " + minEst + ":" + secEst);

						textPos++;
						if (!btnPrevious.isEnabled()){
							btnPrevious.setEnabled(true);
						}

						if (textPos != multiMapFinalDir.get(mapPos).size()){
							directionsText.setText(textDir.get(mapPos).get(textPos));
						}
						else if (mapPos != multiMapFinalDir.size() - 1){
							directionsText.setText("Enter " + dirMaps.get(mapPos + 1).getMapName());
						}
						else {
							textPos = multiMapFinalDir.get(mapPos).size();
							//mapPos = multiMapFinalDir.size() - 1;
							txtTimeToDestination.setText("");
							directionsText.setText("You have arrived at your destination");
							btnNext.setEnabled(false);
						}
					}	


					else if (textPos == multiMapFinalDir.get(mapPos).size() && mapPos != multiMapFinalDir.size() - 1) {
						textPos = 0; // For route coloring 
						mapPos++;
						//change map
						File destinationFile = new File("src/VectorMaps/" + dirMaps.get(mapPos).getMapName() + ".png");
						directionsText.setText(textDir.get(mapPos).get(textPos));


						destinationFile = new File(destinationFile.getAbsolutePath());
						try {
							img = ImageIO.read(destinationFile);
						} catch (IOException g) {
							System.out.println("Invalid Map Selection");
							g.printStackTrace();
						}
						frame.repaint();
					}
					frame.repaint();
				}
			}});

		txtTimeToDestination = new JTextField();
		txtTimeToDestination.setEditable(false);
		txtTimeToDestination.setText("Estimated Time to Destination: ");
		GridBagConstraints gbc_txtTimeToDestination = new GridBagConstraints();
		gbc_txtTimeToDestination.insets = new Insets(0, 0, 5, 5);
		gbc_txtTimeToDestination.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTimeToDestination.gridx = 2;
		gbc_txtTimeToDestination.gridy = 4;
		navMenu.add(txtTimeToDestination, gbc_txtTimeToDestination);
		txtTimeToDestination.setColumns(10);

		GridBagConstraints gbc_btnNext = new GridBagConstraints();
		gbc_btnNext.insets = new Insets(0, 0, 5, 5);
		gbc_btnNext.anchor = GridBagConstraints.NORTH;
		gbc_btnNext.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNext.gridx = 3;
		gbc_btnNext.gridy = 4;
		navMenu.add(btnNext, gbc_btnNext);

		txtFieldEmail = new JTextField();
		GridBagConstraints gbc_txtFieldEmail = new GridBagConstraints();
		gbc_txtFieldEmail.insets = new Insets(0, 0, 5, 5);
		gbc_txtFieldEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFieldEmail.gridx = 1;
		gbc_txtFieldEmail.gridy = 5;
		navMenu.add(txtFieldEmail, gbc_txtFieldEmail);
		txtFieldEmail.setColumns(10);

		GradientButton btnEmailDirections = new GradientButton("E-Mail Directions", buttonColor);
		GridBagConstraints gbc_btnEmailDirections = new GridBagConstraints();
		gbc_btnEmailDirections.insets = new Insets(0, 0, 5, 5);
		gbc_btnEmailDirections.gridx = 2;
		gbc_btnEmailDirections.gridy = 5;
		navMenu.add(btnEmailDirections, gbc_btnEmailDirections);

		GridBagConstraints gbc_btnFullTextDirections = new GridBagConstraints();
		gbc_btnFullTextDirections.insets = new Insets(0, 0, 5, 5);
		gbc_btnFullTextDirections.gridx = 3;
		gbc_btnFullTextDirections.gridy = 5;
		navMenu.add(btnFullTextDirections, gbc_btnFullTextDirections);
		btnFullTextDirections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(txtpnFullTextDir.isVisible()){
					txtpnFullTextDir.setVisible(false);
					btnFullTextDirections.setText("Show Full Text Directions");
				}
				else{
					txtpnFullTextDir.setVisible(true);
					btnFullTextDirections.setText("Hide Full Text Directions");
				}
			}
		});
		btnEmailDirections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					new PrintDirections(textDir,finalDir,txtFieldEmail.getText());
					txtFieldEmail.setText("");
				} catch (AddressException e1) {
					// TODO Auto-generated catch block
					btnEmailDirections.setText("Invalid Address");
				}
			}
		});

		// Add panel for drawing
		frame.getContentPane().add(drawPanel);

		// Make frame visible after initializing everything
		frame.setVisible(true);
		loadingAnimation.hideSplash(0);
	}

	public JPanel createPrefMenu(){
		prefMenu = new JPanel();
		prefMenu.setBackground(backgroundColor);
		GridBagLayout gbl_prefMenu = new GridBagLayout();
		gbl_prefMenu.columnWidths = new int[]{58, 0, 0, 56, 99, 147, 38, 0};
		gbl_prefMenu.rowHeights = new int[]{0, 0, 0, 0, 32, 12, 11, 0};
		gbl_prefMenu.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_prefMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		prefMenu.setLayout(gbl_prefMenu);

		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 5;
		gbc_verticalStrut.gridy = 0;
		prefMenu.add(verticalStrut, gbc_verticalStrut);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 1;
		prefMenu.add(horizontalStrut, gbc_horizontalStrut);

		JLabel lblOutside = new JLabel("Outside");
		GridBagConstraints gbc_lblOutside = new GridBagConstraints();
		gbc_lblOutside.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutside.gridx = 1;
		gbc_lblOutside.gridy = 1;
		prefMenu.add(lblOutside, gbc_lblOutside);

		JLabel lblStairs = new JLabel("Stairs");
		GridBagConstraints gbc_lblStairs = new GridBagConstraints();
		gbc_lblStairs.insets = new Insets(0, 0, 5, 5);
		gbc_lblStairs.gridx = 3;
		gbc_lblStairs.gridy = 1;
		prefMenu.add(lblStairs, gbc_lblStairs);

		JLabel lblVisualPreferences = new JLabel("Theme\r\n");
		GridBagConstraints gbc_lblVisualPreferences = new GridBagConstraints();
		gbc_lblVisualPreferences.insets = new Insets(0, 0, 5, 5);
		gbc_lblVisualPreferences.gridx = 5;
		gbc_lblVisualPreferences.gridy = 1;
		prefMenu.add(lblVisualPreferences, gbc_lblVisualPreferences);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_1.gridx = 6;
		gbc_horizontalStrut_1.gridy = 1;
		prefMenu.add(horizontalStrut_1, gbc_horizontalStrut_1);

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_2 = new GridBagConstraints();
		gbc_horizontalStrut_2.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_2.gridx = 4;
		gbc_horizontalStrut_2.gridy = 2;
		prefMenu.add(horizontalStrut_2, gbc_horizontalStrut_2);

		JRadioButton rdbtnStandard = new JRadioButton("Standard");
		GridBagConstraints gbc_rdbtnStandard = new GridBagConstraints();
		gbc_rdbtnStandard.anchor = GridBagConstraints.WEST;
		gbc_rdbtnStandard.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnStandard.gridx = 5;
		gbc_rdbtnStandard.gridy = 2;
		prefMenu.add(rdbtnStandard, gbc_rdbtnStandard);
		rdbtnStandard.setSelected(true);

		// Add action listener to swap color palette, needs to be set after buttons are initialized
		rdbtnStandard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Switch to standard colors
				previousColor = new Color(255, 75, 75);
				nextColor = new Color(51, 255, 51);
				pointColor = Color.ORANGE;
			}
		});

		JSlider sliderOutside = new JSlider(JSlider.HORIZONTAL, -1, 1, 0);
		sliderOutside.setPaintLabels(true);
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.insets = new Insets(0, 0, 5, 5);
		gbc_slider.gridx = 1;
		gbc_slider.gridy = 2;
		prefMenu.add(sliderOutside, gbc_slider);
		sliderOutside.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent event) {
				int value = sliderOutside.getValue();
				if (value == 0) {
					outside = 0;
					//System.out.println("0");
				} else if (value > 0 ) {
					outside = 1;
					//System.out.println("value > 0 " + value);
				} else{
					outside = -1;
					//System.out.println("value < 0" + value);
				} 
			}
		});

		sliderOutside.setMajorTickSpacing(1);
		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(0, new JLabel("Neutral"));
		labels.put(-1, new JLabel("Avoid"));
		labels.put(1, new JLabel("Priority"));

		sliderOutside.setLabelTable(labels);
		sliderOutside.setPaintTicks(true);

		JSlider sliderStairs = new JSlider(JSlider.HORIZONTAL, -1, 1, 0);
		sliderStairs.setPaintLabels(true);
		GridBagConstraints gbc_slider_1 = new GridBagConstraints();
		gbc_slider_1.insets = new Insets(0, 0, 5, 5);
		gbc_slider_1.gridx = 3;
		gbc_slider_1.gridy = 2;
		prefMenu.add(sliderStairs, gbc_slider_1);
		sliderStairs.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent event) {
				int value = sliderStairs.getValue();
				if (value == 0) {
					stairs = 0;
					//System.out.println("0");
				} else if (value > 0 ) {
					stairs = 1;
					//System.out.println("value > 0 " + value);
				} else{
					stairs = -1;
					//System.out.println("value < 0" + value);
				} 
			}
		});

		sliderStairs.setMajorTickSpacing(1);
		sliderStairs.setLabelTable(labels);
		sliderStairs.setPaintTicks(true);

		JSlider sliderWalkingSpeed = new JSlider(-1, 1, 0);
		sliderWalkingSpeed.setPaintLabels(true);
		GridBagConstraints gbc_walkingSpeed = new GridBagConstraints();
		gbc_walkingSpeed.insets = new Insets(0, 0, 5, 5);
		gbc_walkingSpeed.gridx = 1;
		gbc_walkingSpeed.gridy = 4;
		prefMenu.add(sliderWalkingSpeed, gbc_walkingSpeed);
		sliderWalkingSpeed.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent event) {
				int value = sliderWalkingSpeed.getValue();
				if (value > 0) {
					walkSpeed = 6;
					resetPath = true;
					//System.out.println("0");
				} else if (value == 0 ) {
					walkSpeed = 4.5;
					resetPath = true;
					//System.out.println("value > 0 " + value);
				} else{
					walkSpeed = 3;
					resetPath = true;
					//System.out.println("value < 0" + value);
				} 
			}
		});

		sliderWalkingSpeed.setMajorTickSpacing(1);
		Hashtable<Integer, JLabel> speeds = new Hashtable<Integer, JLabel>();
		speeds.put(0, new JLabel("Medium"));
		speeds.put(-1, new JLabel("Slow"));
		speeds.put(1, new JLabel("Fast"));

		sliderWalkingSpeed.setLabelTable(speeds);
		sliderWalkingSpeed.setPaintTicks(true);
		sliderStairs.setMajorTickSpacing(1);

		JRadioButton rdbtnColorBlindMode = new JRadioButton("Color Blind Mode");
		GridBagConstraints gbc_rdbtnColorBlindMode = new GridBagConstraints();
		gbc_rdbtnColorBlindMode.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnColorBlindMode.anchor = GridBagConstraints.WEST;
		gbc_rdbtnColorBlindMode.gridx = 5;
		gbc_rdbtnColorBlindMode.gridy = 3;

		// Add action listener to swap color palette, needs to be set after buttons are initialized
		rdbtnColorBlindMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Switch to color blind friendly colors
				previousColor = new Color(182, 109, 255);
				nextColor = new Color(0, 146, 146);
				pointColor = new Color(146, 0, 0);
			}
		});

		ButtonGroup visualPreferences = new ButtonGroup();

		JLabel lblWalkingSpeed = new JLabel("Walking Speed");
		GridBagConstraints gbc_lblWalkingSpeed = new GridBagConstraints();
		gbc_lblWalkingSpeed.insets = new Insets(0, 0, 5, 5);
		gbc_lblWalkingSpeed.gridx = 1;
		gbc_lblWalkingSpeed.gridy = 3;
		prefMenu.add(lblWalkingSpeed, gbc_lblWalkingSpeed);
		prefMenu.add(rdbtnColorBlindMode, gbc_rdbtnColorBlindMode);
		prefMenu.add(rdbtnStandard, gbc_rdbtnStandard);
		visualPreferences.add(rdbtnStandard);
		visualPreferences.add(rdbtnColorBlindMode);

		return prefMenu;
	}

	public static void main(String[] args) throws IOException, AlreadyExistsException, SQLException{

		//added by JPG starts and plays the animation
		loadingAnimation = new SplashPage();
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}


		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, use lookAndFeel of current system
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException | UnsupportedLookAndFeelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				GUI gui = new GUI();
				try {
					gui.createAndShowGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AlreadyExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}



	class DrawRoute extends JPanel {

		public Shape createStar(int arms, int centerx, int centery, double rOuter, double rInner)
		{
			double angle = Math.PI / arms;

			GeneralPath path = new GeneralPath();

			for (int i = 0; i < 2 * arms; i++)
			{
				double r = (i & 1) == 0 ? rOuter : rInner;
				Point2D.Double p = new Point2D.Double(centerx + Math.cos(i * angle) * r, centery + Math.sin(i * angle) * r);
				if (i == 0) path.moveTo(p.getX(), p.getY());
				else path.lineTo(p.getX(), p.getY());
			}
			path.closePath();
			return path;
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			if (!(img == null)) {

				// Scale the image to the appropriate screen size

				if (drawnfirst == false){
					windowScale = ((double)img.getWidth() / (double)drawPanel.getWidth());
					scaleSize = 1/((double)img.getWidth() / (double)drawPanel.getWidth());
					//System.out.println("setting: "+scaleSize);
					//System.out.println("Image Original Width " + img.getWidth());
					int WidthSize = (int)((double) img.getHeight() / windowScale);
					if (WidthSize > (double)drawPanel.getHeight()){
						windowScale = (double)img.getHeight() / (double)drawPanel.getHeight();
						scaleSize = 1/((double)img.getHeight() / (double)drawPanel.getHeight());
						//System.out.println("setting: "+scaleSize);
					}
					newImageWidth = (int)((double)img.getWidth() / windowScale);
					newImageHeight = (int)((double)img.getHeight() / windowScale);
					int centerx = (drawPanel.getWidth()/2);
					int centery = (drawPanel.getHeight()/2);
					drawnposx = centerx -(int)(newImageWidth/2);
					drawnposy = centery -(int)(newImageHeight/2);
					g.drawImage(img, drawnposx, drawnposy, (int)newImageWidth, (int)newImageHeight, null);
					System.out.println("image width og: "+img.getWidth()+"*"+scaleSize+"="+newImageWidth);
					System.out.println("image height og: "+img.getHeight()+"*"+scaleSize+"="+newImageHeight);

				} else{
					double deltax = 0;
					double deltay = 0;
					newImageHeight = (int)img.getHeight()*scaleSize;
					newImageWidth = (int)img.getWidth()*scaleSize;
					if(!(mapTitle.equals("Select Map"))){
						if(Dragged){
							System.out.println("dragged");
							deltax = -(originx - mousex);
							deltay = -(originy - mousey);
							originx = mousex;
							originy = mousey;
							difWidth = 0;
							difHeight = 0;
						} else if(scrolled){
							System.out.println("I did it");
							deltax = difWidth;
							deltay = difWidth;
							scrolled = false;
						}
					}
					drawnposx += deltax;
					drawnposy += deltay;
					g.drawImage(img, drawnposx, drawnposy, (int)newImageWidth, (int)newImageHeight, null);
				}

				if (showRoute && route != null){


					// Draw multi colored lines depending on current step in directions and color settings (color blind mode or not)
					// Draw lines for all points up to current point, use previousColor (same color as "Previous" button)
					g.setColor(new Color(previousColor.getRed(), previousColor.getGreen(), previousColor.getBlue(), 150));
					g2.setStroke(new BasicStroke(3));
					for (int i = 0; i < textPos; i++){
						int point1x = (int)((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX()*newImageWidth)+drawnposx);
						int point1y = (int)((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY()*newImageHeight)+drawnposy);
						int point2x = (int)((multiMapFinalDir.get(mapPos).get(i).getDestination().getLocX()*newImageWidth)+drawnposx);
						int point2y = (int)((multiMapFinalDir.get(mapPos).get(i).getDestination().getLocY()*newImageHeight)+drawnposy);
						g2.drawLine(point1x, point1y, point2x, point2y);
					}
					// Draw a thicker line for the current step in the directions, use currentColor
					if(DEBUG)
						System.out.println("mapPos: " + mapPos);
					if (textPos != multiMapFinalDir.get(mapPos).size()){ //|| (mapPos == multiMapFinalDir.size()-1 && multiMapFinalDir.get(mapPos).size()-1 == textPos)){
						g2.setStroke(new BasicStroke(6));
						g.setColor(currentColor);
						int point1x = (int)((multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocX()*newImageWidth)+drawnposx);
						int point1y = (int)((multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocY()*newImageHeight)+drawnposy);
						int point2x = (int)((multiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocX()*newImageWidth)+drawnposx);
						int point2y = (int)((multiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocY()*newImageHeight)+drawnposy);
						g2.drawLine(point1x, point1y, point2x, point2y);
					}


					g2.setStroke(new BasicStroke(3));
					g.setColor(nextColor);
					for (int i = textPos + 1; i < multiMapFinalDir.get(mapPos).size(); i++){
						int point1x1 = (int)((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX()*newImageWidth)+drawnposx);
						int point1y1 = (int)((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY()*newImageHeight)+drawnposy);
						int point2x1 = (int)((multiMapFinalDir.get(mapPos).get(i).getDestination().getLocX()*newImageWidth)+drawnposx);
						int point2y1 = (int)((multiMapFinalDir.get(mapPos).get(i).getDestination().getLocY()*newImageHeight)+drawnposy);
						g2.drawLine(point1x1, point1y1, point2x1, point2y1);
					}

					// Draws ovals with black borders at each of the points along the path, needs to use an offset
					for (int i = 0; i < multiMapFinalDir.get(mapPos).size(); i++){

						int point1x = (int)((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX()*newImageWidth)+drawnposx);
						int point1y = (int)((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY()*newImageHeight)+drawnposy);
						if (i != textPos){
							g.setColor(pointColor);
							g.fillOval((int)(point1x - (pointSize/2)), (int)(point1y - (pointSize/2)), pointSize, pointSize);
							g.setColor(Color.BLACK);
							g.drawOval((int)(point1x - (pointSize/2)), (int)(point1y - (pointSize/2)), pointSize, pointSize);
						}
						else {
							int point1x1 = (int)((multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocX()*newImageWidth)+drawnposx);
							int point1y1 = (int)((multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocY()*newImageHeight)+drawnposy);
							// Prints a star indicating where the user currently is
							Shape star = createStar(5, point1x1, point1y1, 7, 12);
							g.setColor(pointColor);
							g2.fill(star);
							g.setColor(Color.BLACK);
							g2.draw(star);
						}
					}

					// Draws final oval or star in path
					int pointx = (int)((multiMapFinalDir.get(mapPos).get(multiMapFinalDir.get(mapPos).size()-1).getDestination().getLocX()*newImageWidth)+drawnposx);
					int pointy = (int)((multiMapFinalDir.get(mapPos).get(multiMapFinalDir.get(mapPos).size()-1).getDestination().getLocY()*newImageHeight)+drawnposy);
					if (textPos != multiMapFinalDir.get(mapPos).size()){
						g.setColor(pointColor);
						g.fillOval(pointx - (pointSize/2), pointy - (pointSize/2), pointSize, pointSize);
						g.setColor(Color.BLACK);
						g.drawOval(pointx - (pointSize/2), pointy - (pointSize/2), pointSize, pointSize);	
					}	
					else {
						int point1x1 = (int)((multiMapFinalDir.get(mapPos).get(textPos - 1).getDestination().getLocX()*newImageWidth)+drawnposx);
						int point1y1 = (int)((multiMapFinalDir.get(mapPos).get(textPos - 1).getDestination().getLocY()*newImageHeight)+drawnposy);
						Shape star = createStar(5, point1x1, point1y1, 7, 12);
						g.setColor(pointColor);
						g2.fill(star);
						g.setColor(Color.BLACK);
						g2.draw(star);
					}
				}
			}

		}
	}
}

