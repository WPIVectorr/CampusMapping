package main_package;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;

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
	JComboBox mapsDropdown = new JComboBox();
	JComboBox<Point> destBuilds = new JComboBox();
	JComboBox<Point> startBuilds = new JComboBox();
	JComboBox DestMaps = new JComboBox();
	Map startMap;
	public void createAndShowGUI() throws IOException, AlreadyExistsException, SQLException{

		frame.setSize(932, 778);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.getContentPane().setBackground(backgroundColor);

		maps = md.getMapsFromLocal();
		allPoints = new ArrayList<Point>();
		for(int i = 0; i < maps.size(); i++){
			for(int j = 0; j < maps.get(i).getPointList().size(); j++){
				allPoints.add(maps.get(i).getPointList().get(j));
			}
		}
		//System.out.println("------------------edges check-------------------");



		//maps.get(0).getPointList().get(0).print();

		mainMenu = new JPanel();
		mainMenu.setBackground(backgroundColor);

		navMenu = new JPanel();
		navMenu.setBackground(backgroundColor);

		GridBagLayout gbl_navMenu = new GridBagLayout();
		gbl_navMenu.columnWidths = new int[]{0, 298, 298, 298, 0, 0};
		gbl_navMenu.rowHeights = new int[]{0, 0, 56, 56, 0};
		gbl_navMenu.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_navMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		navMenu.setLayout(gbl_navMenu);

		menus = new JPanel(new CardLayout());
		menuLayout = (CardLayout) menus.getLayout();
		menus.add(mainMenu, "Main Menu");
		menus.add(navMenu, "Nav Menu");
		menus.add(createPrefMenu(), "Pref Menu");


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
		gbl_mainMenu.columnWidths = new int[]{0, 160, 209, 166, 298, 0, 0};
		gbl_mainMenu.rowHeights = new int[]{27, 27, 27, 0, 0, 0};
		gbl_mainMenu.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_mainMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		mainMenu.setLayout(gbl_mainMenu);
		mapsDropdown.addItem("Select Map");
		DestMaps.addItem("Select Map");
		for(int i = 0; i < maps.size(); i++){	
			mapsDropdown.addItem(maps.get(i).getMapName());
			DestMaps.addItem(maps.get(i).getMapName());
		}

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 1;
		mainMenu.add(horizontalStrut, gbc_horizontalStrut);


		JLabel lblMaps = new JLabel("Starting Map:");
		lblMaps.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblMaps = new GridBagConstraints();
		gbc_lblMaps.fill = GridBagConstraints.BOTH;
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
				buildStartIndex = mapsDropdown.getSelectedIndex();


				String mapTitle = maps.get(buildStartIndex-1).getMapName();
				//String mapTitle = "AtwaterKent1";

				File start = new File("src/VectorMaps");
				String startInput = start.getAbsolutePath();
				//assuming all maps saved in vectorMaps are in jpg
				startInput = startInput + "/" + mapTitle + ".jpg";

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

					//System.out.println("building size: " + buildings.length);
					for (int i = 0; i < maps.get(buildStartIndex-1).getPointList().size(); i++){
						if(!maps.get(buildStartIndex-1).getPointList().get(i).getName().equals("Hallway")){
							startBuilds.addItem(maps.get(buildStartIndex-1).getPointList().get(i));
							if(DEBUG)
								System.out.println("startBuildsSize: " + maps.get(buildStartIndex-1).getPointList().size());
						}
						//System.out.println("buildings[i] " + buildings[i]);

						// destRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectDest)));
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
		}
				);
		//adds the correct points for the building specified
		DestMaps.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				buildDestIndex = DestMaps.getSelectedIndex();


				String mapTitle = maps.get(buildDestIndex-1).getMapName();
				//String mapTitle = "AtwaterKent1";

				File dest = new File("src/VectorMaps");
				String destInput = dest.getAbsolutePath();
				//assuming all maps saved in vectorMaps are in jpg
				destInput = destInput + "/" + mapTitle + ".jpg";

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

					//System.out.println("building size: " + buildings.length);
					/*for (int i = 0; i < maps.get(buildDestIndex-1).getPointList().size(); i++){
						if(!maps.get(buildDestIndex-1).getPointList().get(i).getName().equals("Hallway")){
							startBuilds.addItem(maps.get(buildDestIndex-1).getPointList().get(i));
							System.out.println("startBuildsSize: " + maps.get(buildDestIndex-1).getPointList().size());
						}
						//System.out.println("buildings[i] " + buildings[i]);
						// destRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectDest)));
					}*/

					for (int i = 0; i < maps.get(buildDestIndex-1).getPointList().size(); i++){
						if(!maps.get(buildDestIndex-1).getPointList().get(i).getName().equals("Hallway")){
							destBuilds.addItem(maps.get(buildDestIndex-1).getPointList().get(i));
						}
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



		//creates drop down box with building names
		GridBagConstraints gbc_startBuilds = new GridBagConstraints();
		gbc_startBuilds.fill = GridBagConstraints.BOTH;
		gbc_startBuilds.insets = new Insets(0, 0, 5, 5);
		gbc_startBuilds.gridx = 4;
		gbc_startBuilds.gridy = 1;
		mainMenu.add(startBuilds, gbc_startBuilds);
		startBuilds.setBounds(122, 30, 148, 20);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_1.gridx = 5;
		gbc_horizontalStrut_1.gridy = 1;
		mainMenu.add(horizontalStrut_1, gbc_horizontalStrut_1);

		JLabel lblDestinationMap = new JLabel("Destination Map:");
		GridBagConstraints gbc_lblDestinationMap = new GridBagConstraints();
		gbc_lblDestinationMap.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestinationMap.gridx = 1;
		gbc_lblDestinationMap.gridy = 2;
		mainMenu.add(lblDestinationMap, gbc_lblDestinationMap);


		GridBagConstraints gbc_destMaps = new GridBagConstraints();
		gbc_destMaps.insets = new Insets(0, 0, 5, 5);
		gbc_destMaps.fill = GridBagConstraints.HORIZONTAL;
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
		mainMenu.add(destBuilds, gbc_destBuilds);
		destBuilds.setBounds(122, 30, 148, 20);

		//buttonPanel.add(destBuilds);
		destBuilds.setBounds(122, 80, 148, 20);
		lblDestination.setLabelFor(destBuilds);

		GradientButton btnSwapStartAndDest = new GradientButton("Swap Start and Destination", buttonColor);
		GridBagConstraints gbc_btnSwapStartAndDest = new GridBagConstraints();
		gbc_btnSwapStartAndDest.insets = new Insets(0, 0, 5, 5);
		gbc_btnSwapStartAndDest.gridx = 2;
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

		GradientButton btnSetPreferencesMain = new GradientButton("Set Preferences", buttonColor);
		GridBagConstraints gbc_btnSetPreferencesMain = new GridBagConstraints();
		gbc_btnSetPreferencesMain.insets = new Insets(0, 0, 5, 5);
		gbc_btnSetPreferencesMain.gridx = 4;
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

		// Button that generates a route and switches to nav display
		GradientButton directionsButton = new GradientButton("Directions", new Color(0, 255, 127));
		GridBagConstraints gbc_directionsButton = new GridBagConstraints();
		gbc_directionsButton.fill = GridBagConstraints.BOTH;
		gbc_directionsButton.insets = new Insets(0, 0, 0, 5);
		gbc_directionsButton.gridx = 3;
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

					route = astar.PathFind(start, end);
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

						File destinationFile = new File("src/VectorMaps/" + dirMaps.get(mapPos).getMapName() + ".jpg");
						destinationFile = new File(destinationFile.getAbsolutePath());
						try {
							//System.out.println("The absolute path is: " + destinationFile.getAbsolutePath());
							//System.out.println("Map name " + currentMap.getMapName());
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

		// Initalize this button first so it can be used in return button
		GradientButton btnFullTextDirections = new GradientButton("Show Full Text Directions", buttonColor);

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

		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 2;
		gbc_verticalStrut.gridy = 0;
		navMenu.add(verticalStrut, gbc_verticalStrut);

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_2 = new GridBagConstraints();
		gbc_horizontalStrut_2.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_2.gridx = 0;
		gbc_horizontalStrut_2.gridy = 1;
		navMenu.add(horizontalStrut_2, gbc_horizontalStrut_2);
		GridBagConstraints gbc_btnReturn = new GridBagConstraints();
		gbc_btnReturn.insets = new Insets(0, 0, 5, 5);
		gbc_btnReturn.gridx = 1;
		gbc_btnReturn.gridy = 1;
		navMenu.add(btnReturn, gbc_btnReturn);

		GridBagConstraints gbc_btnFullTextDirections = new GridBagConstraints();
		gbc_btnFullTextDirections.insets = new Insets(0, 0, 5, 5);
		gbc_btnFullTextDirections.gridx = 2;
		gbc_btnFullTextDirections.gridy = 1;
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

		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_3 = new GridBagConstraints();
		gbc_horizontalStrut_3.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_3.gridx = 4;
		gbc_horizontalStrut_3.gridy = 1;
		navMenu.add(horizontalStrut_3, gbc_horizontalStrut_3);



		//creates a centered text field that will write back the users info they typed in
		directionsText = new JTextField();
		directionsText.setHorizontalAlignment(JTextField.CENTER);
		directionsText.setToolTipText("");
		directionsText.setBounds(6, 174, 438, 30);
		directionsText.setColumns(1);
		GridBagConstraints gbc_directionsText = new GridBagConstraints();
		gbc_directionsText.gridwidth = 3;
		gbc_directionsText.fill = GridBagConstraints.HORIZONTAL;
		gbc_directionsText.insets = new Insets(0, 0, 5, 5);
		gbc_directionsText.gridx = 1;
		gbc_directionsText.gridy = 2;
		navMenu.add(directionsText, gbc_directionsText);


		// Button to get previous step in directions
		//sets the previous button color to green
		btnPrevious = new GradientButton("Previous", previousColor);
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
					File destinationFile = new File("src/VectorMaps/" + dirMaps.get(mapPos).getMapName() + ".jpg");
					destinationFile = new File(destinationFile.getAbsolutePath());
					try {
						//System.out.println("The absolute path is: " + destinationFile.getAbsolutePath());
						//System.out.println("Map name " + currentMap.getMapName());
						img = ImageIO.read(destinationFile);
					} catch (IOException g) {
						System.out.println("Invalid Map Selection");
						g.printStackTrace();
					}
					frame.repaint();
				} else {
					textPos--;
					directionsText.setText(textDir.get(mapPos).get(textPos));
				}
				frame.repaint();
			}
		});
		GridBagConstraints gbc_btnPrevious = new GridBagConstraints();
		gbc_btnPrevious.anchor = GridBagConstraints.NORTH;
		gbc_btnPrevious.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPrevious.insets = new Insets(0, 0, 0, 5);
		gbc_btnPrevious.gridx = 1;
		gbc_btnPrevious.gridy = 3;
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
						textPos++;
						if (textPos != multiMapFinalDir.get(mapPos).size()){
							directionsText.setText(textDir.get(mapPos).get(textPos));
						}
						else if (mapPos != multiMapFinalDir.size() - 1){
							directionsText.setText("Enter " + dirMaps.get(mapPos + 1).getMapName());
						}
						else {
							textPos = multiMapFinalDir.get(mapPos).size();
							//mapPos = multiMapFinalDir.size() - 1;
							directionsText.setText("You have arrived at your destination");
						}
					}	


					else if (textPos == multiMapFinalDir.get(mapPos).size() && mapPos != multiMapFinalDir.size() - 1) {
						textPos = 0; // For route coloring 
						mapPos++;
						//change map
						File destinationFile = new File("src/VectorMaps/" + dirMaps.get(mapPos).getMapName() + ".jpg");
						directionsText.setText(textDir.get(mapPos).get(textPos));


						destinationFile = new File(destinationFile.getAbsolutePath());
						try {
							//System.out.println("The absolute path is: " + destinationFile.getAbsolutePath());
							//System.out.println("Map name " + currentMap.getMapName());
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
		GridBagConstraints gbc_btnNext = new GridBagConstraints();
		gbc_btnNext.insets = new Insets(0, 0, 0, 5);
		gbc_btnNext.anchor = GridBagConstraints.NORTH;
		gbc_btnNext.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNext.gridx = 3;
		gbc_btnNext.gridy = 3;
		navMenu.add(btnNext, gbc_btnNext);

		// Add panel for drawing
		frame.getContentPane().add(drawPanel);

		// Make frame visible after initializing everything
		frame.setVisible(true);
	}

	public JPanel createPrefMenu(){
		prefMenu = new JPanel();
		prefMenu.setBackground(backgroundColor);
		GridBagLayout gbl_prefMenu = new GridBagLayout();
		gbl_prefMenu.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_prefMenu.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_prefMenu.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_prefMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		prefMenu.setLayout(gbl_prefMenu);


		JLabel lblPathfinding = new JLabel("Pathfinding Preferences");
		GridBagConstraints gbc_lblPathfinding = new GridBagConstraints();
		gbc_lblPathfinding.gridwidth = 3;
		gbc_lblPathfinding.insets = new Insets(0, 0, 5, 5);
		gbc_lblPathfinding.gridx = 2;
		gbc_lblPathfinding.gridy = 1;
		prefMenu.add(lblPathfinding, gbc_lblPathfinding);

		JLabel lblVisualPreferences = new JLabel("Visual Preferneces");
		GridBagConstraints gbc_lblVisualPreferences = new GridBagConstraints();
		gbc_lblVisualPreferences.insets = new Insets(0, 0, 5, 0);
		gbc_lblVisualPreferences.gridx = 11;
		gbc_lblVisualPreferences.gridy = 1;
		prefMenu.add(lblVisualPreferences, gbc_lblVisualPreferences);

		JLabel lblOutside = new JLabel("Outside");
		GridBagConstraints gbc_lblOutside = new GridBagConstraints();
		gbc_lblOutside.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutside.gridx = 2;
		gbc_lblOutside.gridy = 2;
		prefMenu.add(lblOutside, gbc_lblOutside);

		JLabel lblStairs = new JLabel("Stairs");
		GridBagConstraints gbc_lblStairs = new GridBagConstraints();
		gbc_lblStairs.insets = new Insets(0, 0, 5, 5);
		gbc_lblStairs.gridx = 4;
		gbc_lblStairs.gridy = 2;
		prefMenu.add(lblStairs, gbc_lblStairs);

		JRadioButton rdbtnStandard = new JRadioButton("Standard");
		GridBagConstraints gbc_rdbtnStandard = new GridBagConstraints();
		gbc_rdbtnStandard.anchor = GridBagConstraints.WEST;
		gbc_rdbtnStandard.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnStandard.gridx = 11;
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

		JSlider sliderOutside = new JSlider();
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.insets = new Insets(0, 0, 5, 5);
		gbc_slider.gridx = 2;
		gbc_slider.gridy = 3;
		prefMenu.add(sliderOutside, gbc_slider);

		JSlider sliderStairs = new JSlider();
		GridBagConstraints gbc_slider_1 = new GridBagConstraints();
		gbc_slider_1.insets = new Insets(0, 0, 5, 5);
		gbc_slider_1.gridx = 4;
		gbc_slider_1.gridy = 3;
		prefMenu.add(sliderStairs, gbc_slider_1);

		JRadioButton rdbtnColorBlindMode = new JRadioButton("Color Blind Mode");
		GridBagConstraints gbc_rdbtnColorBlindMode = new GridBagConstraints();
		gbc_rdbtnColorBlindMode.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnColorBlindMode.anchor = GridBagConstraints.WEST;
		gbc_rdbtnColorBlindMode.gridx = 11;
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
		prefMenu.add(rdbtnColorBlindMode, gbc_rdbtnColorBlindMode);
		prefMenu.add(rdbtnStandard, gbc_rdbtnStandard);
		visualPreferences.add(rdbtnStandard);
		visualPreferences.add(rdbtnColorBlindMode);

		GradientButton btnSavePreferences = new GradientButton("Save Preferences", buttonColor);
		GridBagConstraints gbc_btnSavePreferences = new GridBagConstraints();
		gbc_btnSavePreferences.insets = new Insets(0, 0, 0, 5);
		gbc_btnSavePreferences.gridx = 7;
		gbc_btnSavePreferences.gridy = 4;
		prefMenu.add(btnSavePreferences, gbc_btnSavePreferences);
		// Return to previous view
		btnSavePreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set button colors based on preferences selected
				btnPrevious.setColor(previousColor);
				btnNext.setColor(nextColor);
				// Return to view that preferences menu was accessed from
				menuLayout.show(menus, returnMenu);
				frame.repaint();
			}
		});

		return prefMenu;
	}

	public static void main(String[] args) throws IOException, AlreadyExistsException, SQLException{

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


				windowScale = ((double)img.getWidth() / (double)drawPanel.getWidth());
				//System.out.println("Image Original Width " + img.getWidth());
				int WidthSize = (int)((double) img.getHeight() / windowScale);
				if (WidthSize > (double)drawPanel.getHeight()){
					windowScale = (double)img.getHeight() / (double)drawPanel.getHeight();
				}
				g.drawImage(img, 0, 0, (int)((double)img.getWidth() / windowScale), (int)((double)img.getHeight() / windowScale), null);
			}


			if (showRoute && route != null){

				// Draw multi colored lines depending on current step in directions and color settings (color blind mode or not)
				// Draw lines for all points up to current point, use previousColor (same color as "Previous" button)
				g.setColor(new Color(previousColor.getRed(), previousColor.getGreen(), previousColor.getBlue(), 50));
				g2.setStroke(new BasicStroke(3));
				for (int i = 0; i < textPos; i++){
					g2.drawLine(multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX(), multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY(), multiMapFinalDir.get(mapPos).get(i).getDestination().getLocX(), multiMapFinalDir.get(mapPos).get(i).getDestination().getLocY());
				}
				// Draw a thicker line for the current step in the directions, use currentColor
				if(DEBUG)
					System.out.println("mapPos: " + mapPos);
				if (textPos !=  multiMapFinalDir.get(mapPos).size()){ //|| (mapPos == multiMapFinalDir.size()-1 && multiMapFinalDir.get(mapPos).size()-1 == textPos)){
					g2.setStroke(new BasicStroke(6));
					g.setColor(currentColor);
					g2.drawLine(multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocX(), multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocY(), multiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocX(), multiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocY());
					g2.setStroke(new BasicStroke(3));
				}

				// Draw lines for all points until the end, use nextColor (same color as "Next" button)
				g.setColor(nextColor);
				for (int i = textPos + 1; i < multiMapFinalDir.get(mapPos).size(); i++){
					g2.drawLine(multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX(), multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY(), multiMapFinalDir.get(mapPos).get(i).getDestination().getLocX(), multiMapFinalDir.get(mapPos).get(i).getDestination().getLocY());
				}

				// Draws ovals with black borders at each of the points along the path, needs to use an offset
				for (int i = 0; i < multiMapFinalDir.get(mapPos).size(); i++){
					if (i != textPos){
						g.setColor(pointColor);
						g.fillOval(multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX() - 6, multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY() -6, 12, 12);
						g.setColor(Color.BLACK);
						g.drawOval(multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX() - 6, multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY() -6, 12, 12);
					}
					else {
						// Prints a star indicating where the user currently is
						Shape star = createStar(5, multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocX(), multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocY(), 7, 12);
						g.setColor(pointColor);
						g2.fill(star);
						g.setColor(Color.BLACK);
						g2.draw(star);
					}
				}
				// Draws final oval or star in path
				if (textPos != multiMapFinalDir.get(mapPos).size()){
					g.setColor(pointColor);
					g.fillOval(multiMapFinalDir.get(mapPos).get(multiMapFinalDir.get(mapPos).size()-1).getDestination().getLocX() - 6, multiMapFinalDir.get(mapPos).get(multiMapFinalDir.get(mapPos).size()-1).getDestination().getLocY() -6, 12, 12);
					g.setColor(Color.BLACK);
					g.drawOval(multiMapFinalDir.get(mapPos).get(multiMapFinalDir.get(mapPos).size()-1).getDestination().getLocX() - 6, multiMapFinalDir.get(mapPos).get(multiMapFinalDir.get(mapPos).size()-1).getDestination().getLocY() -6, 12, 12);	
				}	
				else {
					Shape star = createStar(5, multiMapFinalDir.get(mapPos).get(textPos - 1).getDestination().getLocX(), multiMapFinalDir.get(mapPos).get(textPos - 1).getDestination().getLocY(), 7, 12);
					g.setColor(pointColor);
					g2.fill(star);
					g.setColor(Color.BLACK);
					g2.draw(star);
				}
			}
		}
	}
}