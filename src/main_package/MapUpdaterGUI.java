package main_package;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javafx.scene.shape.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Box;
import javax.swing.UIManager.LookAndFeelInfo;

import database.AlreadyExistsException;
import database.DoesNotExistException;
import database.InsertFailureException;
import database.ServerDB;
import database.NoMapException;

public class MapUpdaterGUI{

	private int lastMousex, lastMousey;
	private int pointSize = 5;
	private boolean newClick = false;
	private boolean editingPoint = false;
	private boolean addingMap = false;
	private ArrayList<Point> pointArray = new ArrayList<Point>();
	private ArrayList<Point> markForDelete = new ArrayList<Point>();


	private Point currentPoint;
	private Point editPoint;
	private int editPointIndex;
	String name;
	File destinationFile;
	File logo;

	private Map currentMap = null;
	private ServerDB md = ServerDB.getInstance();

	private ArrayList<Edge> edgeArray = new ArrayList<Edge>();
	private Edge currentEdge;

	// ---------------------------------
	private int windowScale = 2;
	private int windowSizeX = 932;
	private int windowSizeY = 778;

	private BufferedImage img = null;
	// ---------------------------------

	private static JButton btnSaveMap;
	private static JButton btnSavePoint;
	private static JRadioButton rdbtnAddPoints;
	private static JRadioButton rdbtnEditPoints;
	private static JRadioButton rdbtnRemovePoints;

	//---------------------------------
	private static boolean DEBUG = true;

	String point1;
	String point2;
	String point3;
	String point4;
	private JTextField roomNumber;
	private DrawPanel drawPanel = new DrawPanel();
	private JTextField mapName;
	private JTextField txtImageDirectoryPath;
	private JComboBox mapDropDown;
	private File mapToAdd;
	private Boolean pathMode = false;

	private Color buttonColor = new Color(153, 204, 255);

	private JFrame frame = new JFrame("Map Updater");
	private JLabel lblMapImageDirectory;
	private JLabel lblMapName;
	private Component verticalStrut;

	public void createAndShowGUI() throws IOException, AlreadyExistsException, SQLException {

		frame.setSize(932, 778);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		//frame.setSize(screenWidth / 2, screenHeight / 2);
		frame.setLocation(screenWidth / 4, screenHeight / 4);
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setMinimumSize(new Dimension(800, 600));
		frame.getContentPane().setBackground(new Color(255, 235, 205));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		frame.getContentPane().add(buttonPanel, BorderLayout.NORTH);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Maps", createMapsPanel());
		tabs.addTab("Points", createPointsPanel());

		buttonPanel.add(tabs, BorderLayout.NORTH);

		frame.getContentPane().add(drawPanel);

		// Show the frame after everything has been initalized
		frame.setVisible(true);
	}


	/*
	 * Returns the currently selected radbutton in the form of an int. 1 for
	 * addPoint, 2 for editPoint, 3 for removePoint
	 */
	public int getRadButton() {
		int activeButton = 0;
		if (rdbtnAddPoints.isSelected())
			activeButton = 1;
		if (rdbtnEditPoints.isSelected())
			activeButton = 2;
		if (rdbtnRemovePoints.isSelected())
			activeButton = 3;
		return activeButton;
	}

	public static void main(String[] args) throws IOException, AlreadyExistsException, SQLException {

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
				MapUpdaterGUI mapUpdater = new MapUpdaterGUI();
				try {
					mapUpdater.createAndShowGUI();
				} catch (IOException | AlreadyExistsException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}


	public JComponent createMapsPanel(){
		JPanel mapsPanel = new JPanel();
		mapsPanel.setBackground(new Color(255, 235, 205));
		GridBagLayout gbl_mapsPanel = new GridBagLayout();
		gbl_mapsPanel.rowHeights = new int[] {0, 30, 30};
		gbl_mapsPanel.columnWidths = new int[] {280, 280, 280};
		gbl_mapsPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_mapsPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
		mapsPanel.setLayout(gbl_mapsPanel);

		// When the Updater opens the software the list will be populated with
		// the files in
		// the VectorMapps resource folder
		File vectorMapDir = new File("src/VectorMaps");
		vectorMapDir = new File(vectorMapDir.getAbsolutePath());
		//System.out.println("Vectormap abs path: " + vectorMapDir.getAbsolutePath());

		mapDropDown = new JComboBox();

		// Truncates the extensions off of the map name so only the name is
		// displayed in the
		// drop-down menu for selecting a map
		File[] imgList = vectorMapDir.listFiles();
		for (int f = 0; f < imgList.length; f++) {
			/*
			 * tempMapName = imgList[f].getName(); nameLength =
			 * tempMapName.length();
			 * mapDropDown.addItem(tempMapName.substring(0, nameLength - 4));
			 */
			// includes extension
			if(!(imgList[f].getName().equals(".DS_Store"))){
				//checks to make sure the names populating the drop down are in both the vector maps package and 
				//the database
				for(int count = 0; count < md.getMapsFromLocal().size(); count++){
					if(md.getMapsFromLocal().get(count).getMapName().compareTo(imgList[f].getName()) == 0){
						mapDropDown.addItem(imgList[f].getName());
					}
				}

			}
		}
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

		lblMapImageDirectory = new JLabel("Map Image Directory Path");
		GridBagConstraints gbc_lblMapImageDirectory = new GridBagConstraints();
		gbc_lblMapImageDirectory.insets = new Insets(0, 0, 5, 5);
		gbc_lblMapImageDirectory.gridx = 0;
		gbc_lblMapImageDirectory.gridy = 0;
		mapsPanel.add(lblMapImageDirectory, gbc_lblMapImageDirectory);

		lblMapName = new JLabel("Map Name");
		GridBagConstraints gbc_lblMapName = new GridBagConstraints();
		gbc_lblMapName.insets = new Insets(0, 0, 5, 5);
		gbc_lblMapName.gridx = 1;
		gbc_lblMapName.gridy = 0;
		mapsPanel.add(lblMapName, gbc_lblMapName);

		txtImageDirectoryPath = new JTextField();
		txtImageDirectoryPath.setText("Map Image Directory Path");
		GridBagConstraints gbc_txtImageDirectoryPath = new GridBagConstraints();
		gbc_txtImageDirectoryPath.fill = GridBagConstraints.BOTH;
		gbc_txtImageDirectoryPath.insets = new Insets(0, 0, 5, 5);
		gbc_txtImageDirectoryPath.gridx = 0;
		gbc_txtImageDirectoryPath.gridy = 1;
		mapsPanel.add(txtImageDirectoryPath, gbc_txtImageDirectoryPath);
		txtImageDirectoryPath.setColumns(10);

		mapDropDown.addItem("Select Map");

		mapDropDown.addActionListener(new ActionListener() {//Open the dropdown menu
			public void actionPerformed(ActionEvent a) {
				name = mapDropDown.getSelectedItem().toString();//When you select an item, grab the name of the map selected
				System.out.println("Selected item:"+name);

				destinationFile = new File("src/VectorMaps/" + name);
				destinationFile = new File(destinationFile.getAbsolutePath());


				if (!(name.equals("Select Map"))) {//If the name is not the default: "Select map", go further
					pointArray.clear();
					edgeArray.clear();
					ArrayList<Map> mapList = md.getMapsFromLocal(); //Grab all the maps from the database
					System.out.println("MapList size is "+mapList.size());//Print out the size of the maps from the database
					for(int i = 0; i < mapList.size(); i++){//Iterate through the mapList until we find the item we are looking for
						System.out.println("Trying to find name:"+name);
						if(name.equals(mapList.get(i).getMapName()+".jpg"))//Once we find the map:
						{
							currentMap = mapList.get(i);//Grab the current map at this position.
							pointArray = currentMap.getPointList();//Populate the point array with all the points found.
							System.out.println("Map list size:"+mapList.size());

							for(int j = 0; j < pointArray.size(); j++){
								ArrayList<Edge> tmpEdges = pointArray.get(j).getEdges();
								for(int k = 0; k < tmpEdges.size(); k++){
									System.out.println(tmpEdges.get(k).getId());
									edgeArray.add(tmpEdges.get(k));
								}
							}


							System.out.println("Found map with number of points: "+currentMap.getPointList().size());
							i = mapList.size();
						}
					}


					/*				File destinationFile = new File("src/VectorMaps/" + name);
													destinationFile = new File(destinationFile.getAbsolutePath());
													if (!(name.equals("Select Map"))) {*/
					try {
						img = ImageIO.read(destinationFile);
					} catch (IOException g) {
						System.out.println("Invalid Map Selection");
						g.printStackTrace();
					}
				} else {
					File logo = new File("src/VectorLogo/VectorrLogo.png");
					File logoFinal = new File(logo.getAbsolutePath());
					//System.out.println("logoFinal: " + logoFinal);
					try{
						img = ImageIO.read(logoFinal);
					}
					catch(IOException g){
						System.out.println("Invalid logo");
						g.printStackTrace();
					}
					pointArray.clear();
					edgeArray.clear();
				}
				frame.repaint();
			}
		});

		
		// List that stores the name of every Map in the database

		// mapList.add("Select Map");


		mapName = new JTextField();
		mapName.setText("Map Name");
		GridBagConstraints gbc_mapName = new GridBagConstraints();
		gbc_mapName.fill = GridBagConstraints.BOTH;
		gbc_mapName.insets = new Insets(0, 0, 5, 5);
		gbc_mapName.gridx = 1;
		gbc_mapName.gridy = 1;
		mapsPanel.add(mapName, gbc_mapName);
		mapName.setColumns(10);

		GradientButton findMapFile = new GradientButton("Add Map From File", buttonColor);
		GridBagConstraints gbc_findMapFile = new GridBagConstraints();
		gbc_findMapFile.fill = GridBagConstraints.BOTH;
		gbc_findMapFile.insets = new Insets(0, 0, 0, 5);
		gbc_findMapFile.gridx = 0;
		gbc_findMapFile.gridy = 2;
		mapsPanel.add(findMapFile, gbc_findMapFile);
		findMapFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				JFrame chooseFile = new JFrame();

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Specify Map to Add");

				int userSelection = fileChooser.showSaveDialog(chooseFile);

				if (userSelection == JFileChooser.APPROVE_OPTION) {
					mapToAdd = fileChooser.getSelectedFile();
					txtImageDirectoryPath.setText(mapToAdd.toString());
				}
			}
		});

		GradientButton btnAddMap = new GradientButton("Add Map", buttonColor);
		GridBagConstraints gbc_btnAddMap = new GridBagConstraints();
		gbc_btnAddMap.fill = GridBagConstraints.BOTH;
		gbc_btnAddMap.insets = new Insets(0, 0, 0, 5);
		gbc_btnAddMap.gridx = 1;
		gbc_btnAddMap.gridy = 2;
		mapsPanel.add(btnAddMap, gbc_btnAddMap);



		btnAddMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addingMap = true;
				String maptitle = mapName.getText();

				maptitle = maptitle.trim();
				String mapNameNoExt;
				String srcInput = "";
				int l = 0;

				// check directory to see if it exists
				if(!(mapToAdd == null))
					srcInput = mapToAdd.toString();
				File srcFile = mapToAdd;
				if ((maptitle == null || maptitle.equals("")) || mapToAdd == null) {
					addingMap = false;
					System.out.println("Error: Map is invalid");
				} else {
					for (int k = 0; k < mapDropDown.getItemCount(); k++) {
						l = mapDropDown.getItemAt(k).toString().length();
						mapNameNoExt = mapDropDown.getItemAt(k).toString().substring(0, l - 4);
						System.out.println(mapNameNoExt + "           " + (l - 4));
						if (maptitle.equals(mapNameNoExt)) {
							addingMap = false;
							System.out.println("Error: Map invalid");
						}
					}
				}

				if (addingMap) {
					// /Users/ibanatoski/Downloads/AtwaterKent2.jpg
					System.out.println("SavingMap");
					File dest = new File("src/VectorMaps");
					// File destAbs = dest.getAbsoluteFile();

					String destInput = dest.getAbsolutePath();
					// System.out.println("Destination Input: " + destInput);
					// System.out.println("Source Input: " + srcInput);
					destInput = destInput + "/" + maptitle + srcInput.substring(srcInput.length() - 4);
					System.out.println(destInput);
					File destFile = new File(destInput);

					// Add the name of the map to the Map Selction Dropdown menu
					mapDropDown.addItem(maptitle + srcInput.substring(srcInput.length() - 4));

					// Finds the highest mapID in the database and stores it in
					// highestID
					int highestID;
					if(md.getMapsFromLocal().isEmpty()){
						highestID = 0;
						System.out.print("Database contains no maps so highest ID is 1");


					}
					else{
						//determines the highest mapID from the Maps stored in the database
						ArrayList<Map> mdMapList = md.getMapsFromLocal();
						highestID = mdMapList.get(0).getMapId();
						for (int h = 0; h < mdMapList.size(); h++) {
							if (highestID < mdMapList.get(h).getMapId()) {
								highestID = mdMapList.get(h).getMapId();
							}
						}
					}

					// Create the Map object to be stored in the database
					Map m = new Map(highestID + 1, maptitle);

					highestID++;

					try {
						md.insertMap(m);
					} catch (AlreadyExistsException e1) {
						System.out.print("Look at me im  an error 1");
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						System.out.print("Look at me im  an error 2");
						e1.printStackTrace();
					}

					try {
						copyFileUsingStream(srcFile, destFile);
						img = ImageIO.read(destFile);
					} catch (IOException a) {
						System.out.println("invalid copy");
						a.printStackTrace();
					}
					mapDropDown.setSelectedIndex(mapDropDown.getItemCount()-1);
					System.out.println(mapDropDown.getItemCount());
					addingMap = false;
				} else {

				}

			}
		});

		// Map Drop Down List
		mapDropDown.setToolTipText("Select Map");
		GridBagConstraints gbc_mapDropDown = new GridBagConstraints();
		gbc_mapDropDown.fill = GridBagConstraints.BOTH;
		gbc_mapDropDown.gridx = 2;
		gbc_mapDropDown.gridy = 2;
		mapsPanel.add(mapDropDown, gbc_mapDropDown);


		return mapsPanel;
	}


	public JComponent createPointsPanel(){

		JPanel pointsPanel = new JPanel();
		pointsPanel.setBackground(new Color(255, 235, 205));
		GridBagLayout gbl_pointsPanel = new GridBagLayout();
		gbl_pointsPanel.columnWidths = new int[]{140, 91, 297, 297, 0, 0};
		gbl_pointsPanel.rowHeights = new int[]{0, 23, 23, 0, 0, 8, 0};
		gbl_pointsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_pointsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pointsPanel.setLayout(gbl_pointsPanel);

		/* Initialization of the radio buttons */
		ButtonGroup modeSelector = new ButtonGroup();

		verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 2;
		gbc_verticalStrut.gridy = 0;
		pointsPanel.add(verticalStrut, gbc_verticalStrut);
		rdbtnAddPoints = new JRadioButton("Add Points", true);
		GridBagConstraints gbc_rdbtnAddPoints = new GridBagConstraints();
		gbc_rdbtnAddPoints.fill = GridBagConstraints.BOTH;
		gbc_rdbtnAddPoints.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnAddPoints.gridx = 1;
		gbc_rdbtnAddPoints.gridy = 1;
		pointsPanel.add(rdbtnAddPoints, gbc_rdbtnAddPoints);
		modeSelector.add(rdbtnAddPoints);

		// adds the starting location label to the line with starting location
		// options
		JLabel lblStartingLocation = new JLabel("Point Name");
		GridBagConstraints gbc_lblStartingLocation = new GridBagConstraints();
		gbc_lblStartingLocation.fill = GridBagConstraints.VERTICAL;
		gbc_lblStartingLocation.insets = new Insets(0, 0, 5, 5);
		gbc_lblStartingLocation.gridx = 3;
		gbc_lblStartingLocation.gridy = 1;
		pointsPanel.add(lblStartingLocation, gbc_lblStartingLocation);
		lblStartingLocation.setBounds(6, 31, 119, 16);

		rdbtnEditPoints = new JRadioButton("Edit Points");
		rdbtnEditPoints.setPreferredSize(new Dimension(125, 23));
		rdbtnEditPoints.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_rdbtnEditPoints = new GridBagConstraints();
		gbc_rdbtnEditPoints.fill = GridBagConstraints.BOTH;
		gbc_rdbtnEditPoints.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnEditPoints.gridx = 1;
		gbc_rdbtnEditPoints.gridy = 2;
		pointsPanel.add(rdbtnEditPoints, gbc_rdbtnEditPoints);
		modeSelector.add(rdbtnEditPoints);

		JCheckBox chckbxPathMode = new JCheckBox("Path Mode");
		GridBagConstraints gbc_chckbxPathMode= new GridBagConstraints();
		gbc_chckbxPathMode.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxPathMode.gridx = 2;
		gbc_chckbxPathMode.gridy = 2;
		pointsPanel.add(chckbxPathMode, gbc_chckbxPathMode);
		chckbxPathMode.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (chckbxPathMode.isSelected()){
					pathMode = true;
				}
				else{
					pathMode = false;
				}
			}
		});


		// creates a centered text field that will write back the users info
		// they typed in
		roomNumber = new JTextField();
		GridBagConstraints gbc_roomNumber = new GridBagConstraints();
		gbc_roomNumber.fill = GridBagConstraints.BOTH;
		gbc_roomNumber.insets = new Insets(0, 0, 5, 5);
		gbc_roomNumber.gridx = 3;
		gbc_roomNumber.gridy = 2;
		pointsPanel.add(roomNumber, gbc_roomNumber);
		roomNumber.setHorizontalAlignment(JTextField.CENTER);
		roomNumber.setText("Select a Point to Edit");
		roomNumber.setToolTipText("");
		roomNumber.setBounds(6, 174, 438, 30);
		roomNumber.setColumns(1);

		rdbtnRemovePoints = new JRadioButton("Remove Points");
		GridBagConstraints gbc_rdbtnRemovePoints = new GridBagConstraints();
		gbc_rdbtnRemovePoints.fill = GridBagConstraints.BOTH;
		gbc_rdbtnRemovePoints.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnRemovePoints.gridx = 1;
		gbc_rdbtnRemovePoints.gridy = 3;
		pointsPanel.add(rdbtnRemovePoints, gbc_rdbtnRemovePoints);
		modeSelector.add(rdbtnRemovePoints);

		btnSavePoint = new GradientButton("No Point Selected", buttonColor);


		btnSavePoint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (editingPoint) {
					System.out.println("Updating changed points");
					editPoint.setName(roomNumber.getText());
					roomNumber.setText("Select a Point to Edit");
					editingPoint = false;
				} else {

				}
			}
		});
		GridBagConstraints gbc_btnSavePoint = new GridBagConstraints();
		gbc_btnSavePoint.fill = GridBagConstraints.BOTH;
		gbc_btnSavePoint.insets = new Insets(0, 0, 5, 5);
		gbc_btnSavePoint.gridx = 3;
		gbc_btnSavePoint.gridy = 3;
		pointsPanel.add(btnSavePoint, gbc_btnSavePoint);

		btnSaveMap = new GradientButton("Save Map", buttonColor); // defined above to change text in
		// point selector
		GridBagConstraints gbc_btnSaveMap = new GridBagConstraints();
		gbc_btnSaveMap.insets = new Insets(0, 0, 5, 5);
		gbc_btnSaveMap.fill = GridBagConstraints.BOTH;
		gbc_btnSaveMap.gridx = 2;
		gbc_btnSaveMap.gridy = 4;
		pointsPanel.add(btnSaveMap, gbc_btnSaveMap);

		btnSaveMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < pointArray.size(); i++) {
					Point storePoint = pointArray.get(i);

					System.out.println("Storing point in:"+currentMap.getMapName());
					try {
						ServerDB.insertPoint(currentMap, pointArray.get(i));
						System.out.println("AddPointSuccess");
					} catch (AlreadyExistsException f){
						System.out.println(f.getMessage());
					} catch (NoMapException e1) {
						e1.printStackTrace();
					} catch (InsertFailureException e1) {
						e1.printStackTrace();
					} catch (SQLException e1) {
						e1.printStackTrace();
					} 
					markForDelete.add(storePoint);
				}
				System.out.println("Edge array size is: " + edgeArray.size());
				for (int i = 0; i < edgeArray.size(); i++) {
					Edge storeEdge = edgeArray.get(i);
					storeEdge.setId(storeEdge.getPoint1().getId() + "-" + storeEdge.getPoint2().getId());
					System.out.println("Storing Edge: " + storeEdge.getId());
					System.out.println("Storing Edge point 1: " + storeEdge.getPoint1().getId());
					System.out.println("Storing Edge point 2: " + storeEdge.getPoint2().getId());
					try {
						ServerDB.insertEdge(storeEdge);
					} catch (InsertFailureException | AlreadyExistsException | SQLException
							| DoesNotExistException g) {
						// TODO Auto-generated catch block
						System.out.println(g.getMessage());
					}
				}
			}
		});


		return pointsPanel;


	}



	class DrawPanel extends JPanel {

		ArrayList<Point> paintArray = new ArrayList<Point>(); // arraylist of
		// points
		// already
		// painted
		// Point editPoint;
		// Driver values used for testing:
		int pointID = 0;
		int numEdges = 0;
		int edgeWeight = 1;



		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			// -------------------------------
			// if(img == null)
			// img = ImageIO.read(new
			// File("/User/ibanatoski/git/CampusMapping/src/VectorMaps/"));
			if (!(img == null)) {

				// Scale the image to the appropriate screen size
				double wScale;

				if (img.getHeight() >= img.getWidth()) {
					wScale = (double) img.getHeight() / (double) windowSizeY;
					windowScale = img.getHeight() / windowSizeY;
				} 

				else {
					wScale = (double) img.getHeight() / (double) windowSizeY;
					windowScale = img.getWidth() / windowSizeX;
				}
				if (wScale > windowScale)
					windowScale += 1;

				//sets the correct dimensions for logo
				if(img.getHeight() < windowSizeY && img.getWidth() < windowSizeX){
					g.drawImage(img,  0,  0,  windowSizeX, img.getHeight(), null);
				}
				//sets the correct dimensions for maps
				else{
					// draw image/map
					g.drawImage(img, 0, 0, img.getWidth() / windowScale, img.getHeight() / windowScale, null);
				}
			} else {
				//System.out.println("Reaching here---------------------------------");
			}


			//selecting points on the map
			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					newClick = false;
					lastMousex = e.getX();
					lastMousey = e.getY();
					newClick = true;
					repaint();
				}
			});

			// add point to the point array (has to take place outside of below
			// loop)
			if (newClick == true) {
				//System.out.println(newClick);
				if (getRadButton() == 1) // if addpoint
				{
					Integer nameNumber = pointArray.size()+1;
					double ourRotation = currentMap.getRotationAngle();
					//ourRotation = 2 * Math.PI - ourRotation;


					double centerCurrentMapX = (currentMap.getxTopLeft() + currentMap.getxBotRight()) / 2;
					double centerCurrentMapY = (currentMap.getyTopLeft() + currentMap.getyBotRight()) / 2;
					double tempPreRotateX = lastMousex;
					double tempPreRotateY = lastMousey;

					tempPreRotateX = tempPreRotateX - (img.getWidth() / 2);
					tempPreRotateY = tempPreRotateY - (img.getHeight() / 2);
					tempPreRotateX = (tempPreRotateX/img.getWidth()) * currentMap.getWidth();
					tempPreRotateY = (tempPreRotateY/img.getHeight()) * currentMap.getHeight();
					double rotateX = Math.cos(ourRotation) * tempPreRotateX - Math.sin(ourRotation) * tempPreRotateY;
					double rotateY = Math.sin(ourRotation) * tempPreRotateX + Math.cos(ourRotation) * tempPreRotateY;

					int finalGlobX = (int) Math.round(rotateX + centerCurrentMapX);
					int finalGlobY = (int) Math.round(rotateY + centerCurrentMapY);

					Point point = new Point(currentMap.getNewPointID(), currentMap.getMapId(),
							"Point " + nameNumber.toString(), currentMap.getPointIDIndex(),
							lastMousex, lastMousey, finalGlobX, finalGlobY, numEdges);

					boolean shouldAdd = true;
					for(int k = 0; k < pointArray.size(); k++){
						if(point.getId() == pointArray.get(k).getId()){
							shouldAdd = false;
						}
					}
					if(shouldAdd){
						pointArray.add(point);
					}
					//System.out.println("add point to map: "+currentMap.getMapId()+" Point Array size: "+pointArray.size());
					repaint();
				}
			}

			// draws all the points onto the map.
			// cleans the array of deleted points.
			if (pointArray.size() > 0) {
				for (int i = 0; i < pointArray.size(); i++) {

					currentPoint = pointArray.get(i);
					//System.out.println("numEdges: "+currentPoint.getNumEdges());

					// add edges to list
					/*for (int j = 0; j < currentPoint.getNumEdges(); j++) {
						Edge tmpEdge = currentPoint.getEdges().get(j);
						System.out.println("Adding edge: " + currentPoint.getEdges().get(j).getId());
						boolean shouldAdd = true;
						for(int k = 0; k < edgeArray.size(); k++){
							if(tmpEdge.getId() == edgeArray.get(k).getId()){
								shouldAdd = false;
							}
						}
						if(shouldAdd){
							edgeArray.add(currentPoint.getEdges().get(j));
						}
					}*/
					// newClick has some interesting storage things going on.
					if (newClick == true) {
						//TODO CHANGE THIS LATER, GOOD FOR TESTING CLICKS
						MapUpdaterGUI.btnSaveMap.setText("Save Map, X:" + lastMousex + ", Y:" + lastMousey);

						switch (getRadButton()) {
						case 2:// edit points
							if ((lastMousex > currentPoint.getLocX() - (pointSize + 5)
									&& lastMousex < currentPoint.getLocX() + (pointSize + 5))
									&& (lastMousey > currentPoint.getLocY() - (pointSize + 5)
											&& lastMousey < currentPoint.getLocY() + (pointSize + 5))) {
								if (newClick == true && editingPoint == false) {
									editPoint = currentPoint;
									editPointIndex = i;
									roomNumber.setText(editPoint.getName());
									btnSavePoint.setText("Unselect Current Point");
									editingPoint = true;
									newClick = false;
								} else if (newClick == true && editingPoint == true) {
									if(editPoint.getId().contentEquals(currentPoint.getId())){

									} else {
										currentEdge = new Edge(editPoint, currentPoint, edgeWeight);
										pointArray.set(editPointIndex, editPoint);
										pointArray.set(i, currentPoint);
										System.out.println("Edge sizes- editPoint:"+pointArray.get(editPointIndex).getEdges().size()+
												" currentPoint:"+pointArray.get(i).getEdges().size());
										System.out.println("Current Edge is: " + currentEdge.getId());
										edgeArray.add(currentEdge);
										if (currentPoint.getNumEdges() > 0)//this has to be caught in an exception later
										{
											for (int j = 0; j < currentPoint.getNumEdges(); j++) {
												System.out.println("Adding clicked edge between: "
														+ currentPoint.getEdges().get(j).getPoint1().getName() + ", "
														+ currentPoint.getEdges().get(j).getPoint2().getName());
											}
										}
									}
									newClick = false;
									if(pathMode){
										Point tempEditPoint = pointArray.get(editPointIndex);
										tempEditPoint.setName(roomNumber.getText());
										pointArray.set(editPointIndex, tempEditPoint);
										editPoint = currentPoint;
										editPointIndex = i;
										roomNumber.setText(editPoint.getName());
									}

								}
								repaint();
							}
							break;
						case 3:// remove points
							System.out.println("Remove point called.");
							System.out.println("Size of edgeArray:"+edgeArray.size());
							for (int j = 0; j < pointArray.size(); j++)
							{
								System.out.println("Number of edges in point "
										+pointArray.get(j).getId()+": "+pointArray.get(j).getEdges().size());
							}
							if ((lastMousex > currentPoint.getLocX() - (pointSize + 5)
									&& lastMousex < currentPoint.getLocX() + (pointSize + 5))
									&& (lastMousey > currentPoint.getLocY() - (pointSize + 5)
											&& lastMousey < currentPoint.getLocY() + (pointSize + 5))) {
								if (newClick == true){
									/* The error lies in the fact that edges, when created, are not being added to the points in the pointArray,
									 * as a result, the code breaks when trying to remove said edge. The code below is the first half of a quick
									 * fix, but we are going to try to address the underlying issue first.
									int j = 0;
									for (j = 0; j<edgeArray.size(); j++)
									{
										Edge currEdge = edgeArray.get(j);
										if (currEdge.getPoint1().getId().contentEquals(currentPoint.getId()))
										{
											System.out.println("--Adding edge:"+currEdge.getId()+" to currPoint--");
											currentPoint.addEdge(currEdge);
										}
										else if (currEdge.getPoint2().getId().contentEquals(currentPoint.getId()))
										{
											System.out.println("--Adding edge:"+currEdge.getId()+" to currPoint--");
											currentPoint.addEdge(currEdge);
										}
									}*/
									try{
										System.out.println("Number of edges in point to be removed:"+currentPoint.getEdges().size());
										ServerDB.removePoint(currentPoint);
									} catch (DoesNotExistException e1){
										System.out.println("Reached Here");
										e1.printStackTrace();
									}

									//edgeArray.remove(markForDelete.get(j).getEdges().get(kj));
									for(int kj = 0; kj < currentPoint.getEdges().size(); kj++){
										//edgeArray.remove(markForDelete.get(j).getEdges().get(kj));
										while(edgeArray.contains(currentPoint.getEdges().get(kj))){
											edgeArray.remove(currentPoint.getEdges().get(kj));
										}
										Edge edgeRemoving = currentPoint.getEdges().get(kj);

										if(currentPoint.getId().contentEquals(edgeRemoving.getPoint1().getId()))
										{
											for(int f = 0; f < pointArray.size(); f++){
												if(pointArray.get(f).getId().contentEquals(edgeRemoving.getPoint2().getId())){
													for(int o = 0; o < pointArray.get(f).getEdges().size(); o++){
														if(pointArray.get(f).getEdges().get(o).getId().contentEquals(edgeRemoving.getId())){
															Point tmpPoint = pointArray.get(f);
															tmpPoint.getEdges().remove(edgeRemoving);
															tmpPoint.setNumEdges(tmpPoint.getNumEdges() - 1);
															pointArray.set(f, tmpPoint);
														}
													}
												}
											}
										}
										else
										{
											for(int f = 0; f < pointArray.size(); f++){
												if(pointArray.get(f).getId().contentEquals(edgeRemoving.getPoint1().getId())){
													for(int o = 0; o < pointArray.get(f).getEdges().size(); o++){
														if(pointArray.get(f).getEdges().get(o).getId().contentEquals(edgeRemoving.getId())){
															Point tmpPoint = pointArray.get(f);
															tmpPoint.getEdges().remove(edgeRemoving);
															tmpPoint.setNumEdges(tmpPoint.getNumEdges() - 1);
															pointArray.set(f, tmpPoint);
														}
													}
												}
											}
										}
									}

									currentPoint.deleteEdges();
									pointArray.remove(currentPoint);
									pointArray.remove(currentPoint);
								}
								newClick = false;
								repaint();
							}
							break;
						default:
							break;
						}
					}

					for (int j = 0; j < markForDelete.size(); j++) {
						// remove edges to list


						for(int kj = 0; kj < markForDelete.get(j).getEdges().size(); kj++){
							//edgeArray.remove(markForDelete.get(j).getEdges().get(kj));
							while(edgeArray.contains(markForDelete.get(j).getEdges().get(kj))){
								edgeArray.remove(markForDelete.get(j).getEdges().get(kj));
							}
						}
						/*try {
							ServerDB.removePoint(markForDelete.get(j));
						} catch (DoesNotExistException e1) {
							System.out.println("Attempted to delete point that doesn;t exist in the database. Not an error");
						}*/
						markForDelete.get(j).deleteEdges();
						pointArray.remove(markForDelete.get(j));
						pointArray.remove(markForDelete.get(j));
						markForDelete.remove(j);
					}

					int drawX = (int) currentPoint.getLocX();
					int drawY = (int) currentPoint.getLocY();
					// draws the points onto the map.
					g.fillOval(drawX - (pointSize / 2), drawY - (pointSize / 2), pointSize, pointSize);

					//draw lines between points
				}
				for (int j = 0; j < edgeArray.size(); j++) {
					g.drawLine(edgeArray.get(j).getPoint1().getLocX(), edgeArray.get(j).getPoint1().getLocY(),
							edgeArray.get(j).getPoint2().getLocX(), edgeArray.get(j).getPoint2().getLocY());

				}

			}

			/*			for (int i = 0; i < markForDelete.size(); i++) {
				// remove edges to list
				edgeArray.clear();
				markForDelete.get(i).deleteEdges();
				pointArray.remove(markForDelete.get(i));
				markForDelete.remove(i);
			}*/

			newClick = false;
		}

	}




	/*
	 * Takes an input file directory path and a target directory path and copies
	 * that File to the target location
	 */
	private void copyFileUsingStream(File source, File dest) throws IOException {
		System.out.println(source.getPath());
		FileInputStream is = null;
		FileOutputStream os = null;
		is = new FileInputStream(source);
		System.out.println(source.getPath());
		os = new FileOutputStream(dest);
		System.out.println(dest.getPath());
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}
		is.close();
		os.close();
	}

	private Map updateCurrentMap(Map map)
	{
		int mapId = map.getMapId();
		ArrayList<Map> mapList = md.getMapsFromLocal();
		boolean foundMap = false;
		int j = 0;
		for (j = 0; j<mapList.size(); j++)
		{
			if (mapId == mapList.get(j).getMapId())
			{
				foundMap = true;
				return mapList.get(j);
			}
		}
		if (foundMap == false)
		{
			System.out.println("Failed to find and update map");
		}
		return null;
	}

}