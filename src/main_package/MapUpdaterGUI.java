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
import java.util.concurrent.locks.Lock;

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
import database.PopulateErrorException;

public class MapUpdaterGUI{

	private int lastMousex, lastMousey;
	private int pointSize = 7;
	private boolean newClick = false;
	private boolean editingPoint = false;
	private static boolean addingMap = false;
	private ArrayList<Point> pointArray = new ArrayList<Point>();
	private ArrayList<Point> oldPoints = new ArrayList<Point>();
	private ArrayList<Point> newPoints = new ArrayList<Point>();
	private ArrayList<Point> updatedPoints = new ArrayList<Point>();
	private ArrayList<Point> markForDelete = new ArrayList<Point>();


	private Point currentPoint;
	private Point editPoint;
	private int editPointIndex;
	String name;
	File destinationFile;
	File logo;
	int prevRadButtonVal = 0;

	private Map currentMap = null;
	private static ServerDB md = ServerDB.getInstance();

	private ArrayList<Edge> edgeArray = new ArrayList<Edge>();
	private ArrayList<Edge> newEdges = new ArrayList<Edge>();
	private ArrayList<Edge> drawnEdges = new ArrayList<Edge>();
	private Edge currentEdge;

	// ---------------------------------
	private double windowScale = 2;
	private int windowSizeX = 932;
	private int windowSizeY = 778;

	private static BufferedImage img = null;
	// ---------------------------------

	private static JButton btnSaveMap;
	private static JButton btnSavePoint;
	private static JRadioButton rdbtnAddPoints;
	private static JRadioButton rdbtnEditPoints;
	private static JRadioButton rdbtnRemovePoints;

	//---------------------------------
	private final static boolean DEBUG = false;

	String point1;
	String point2;
	String point3;
	String point4;
	private JTextField roomNumber;
	private DrawPanel drawPanel = new DrawPanel();
	private JTextField mapName;
	private JTextField txtImageDirectoryPath;
	private static JComboBox<String> mapDropDown;
	private File mapToAdd;
	private JCheckBox chckbxPathMode;
	private Boolean pathMode = false;
	private static String maptitle = "";
	private static String srcInput = "";
	private static File srcFile = null;

	private Color buttonColor = new Color(153, 204, 255);

	private JFrame frame = new JFrame("Map Updater");
	private JLabel lblMapImageDirectory;
	private JLabel lblMapName;
	private Component verticalStrut;
	private Icon loadingIcon;
	private JLabel mapsLoadingLabel;
	private JLabel pointsLoadingLabel;
	private JTabbedPane tabs = new JTabbedPane();
	private ArrayList<Map> maps = new ArrayList<Map>();
	private JButton btnConnectToOther;
	private InterMapEdgeGUI connectMapGUI;

	public void createAndShowGUI() throws IOException, AlreadyExistsException, SQLException {

		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		frame.setVisible(true);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MapUpdaterGUI.class.getResource("/VectorLogo/Logo Icon.png")));
		double framex = 932;
		double framey = 778;
		frame.setSize((int)framex, (int)framey);
		frame.setResizable(false);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		double xlocation = (screenWidth / 2)-(framex/2);
		double ylocation = (screenHeight / 2)-(framey/2);
		frame.setLocation((int)xlocation, (int)ylocation);


		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		maps = md.getMapsFromLocal();

		frame.setMinimumSize(new Dimension(800, 600));
		frame.getContentPane().setBackground(new Color(255, 235, 205));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		frame.getContentPane().add(buttonPanel, BorderLayout.NORTH);

		loadingIcon = new ImageIcon("src/VectorLogo/faster reverse.gif");

		tabs.addTab("Maps", createMapsPanel());
		tabs.addTab("Points", createPointsPanel());

		buttonPanel.add(tabs, BorderLayout.NORTH);

		frame.getContentPane().add(drawPanel);

		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
	}


	/*
	 * Returns the currently selected radbutton in the form of an int. 1 for
	 * addPoint, 2 for editPoint, 3 for removePoint
	 */
	public int getRadButton() {
		int activeButton = 0;
		if (rdbtnAddPoints.isSelected()){
			activeButton = 1;
			if(prevRadButtonVal != 1){
				btnSaveMap.doClick();
				prevRadButtonVal = 1;
			}
		}
		if (rdbtnEditPoints.isSelected()){
			activeButton = 2;
			if(prevRadButtonVal != 2){
				btnSaveMap.doClick();
				prevRadButtonVal = 2;
			}
		}
		if (rdbtnRemovePoints.isSelected()){

			activeButton = 3;
			if(prevRadButtonVal != 3){
				btnSaveMap.doClick();
				prevRadButtonVal = 3;
			}
		}
		//if(activeButton != 0){
		//btnSaveMap.doClick();
		//System.out.println("saves map here" );
		//}
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
				if(DEBUG)
					System.out.println("Calling run");
				MapUpdaterGUI mapUpdater = new MapUpdaterGUI();
				try {
					if(DEBUG)
						System.out.println("Calling create and show GUI");
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
		gbl_mapsPanel.rowHeights = new int[] {0, 0, 30, 30};
		gbl_mapsPanel.columnWidths = new int[] {280, 280, 280};
		gbl_mapsPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_mapsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		mapsPanel.setLayout(gbl_mapsPanel);

		mapsLoadingLabel = new JLabel(loadingIcon);
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.fill = GridBagConstraints.VERTICAL;
		gbc_label.gridheight = 3;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 2;
		gbc_label.gridy = 0;
		mapsLoadingLabel.setVisible(false);

		// When the Updater opens the software the list will be populated with
		// the files in
		// the VectorMapps resource folder
		File vectorMapDir = new File("src/VectorMaps");
		vectorMapDir = new File(vectorMapDir.getAbsolutePath());
		//System.out.println("Vectormap abs path: " + vectorMapDir.getAbsolutePath());

		mapDropDown = new JComboBox();
		mapDropDown.addItem("Select Map");
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
				if(DEBUG)
					System.out.println("Dropdown:" );
				String temp = imgList[f].getName().substring(0, imgList[f].getName().length() -4);
				if(DEBUG)
					System.out.println(temp);

				//checks to make sure the names populating the drop down are in both the vector maps package and 
				//the database
				for(int count = 0; count < maps.size(); count++){
					if(DEBUG)
						System.out.println("printing from database: " + maps.get(count).getMapName());
					if(maps.get(count).getMapName().compareTo(temp) == 0){
						mapDropDown.addItem(temp);

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
		gbc_lblMapImageDirectory.gridy = 1;
		mapsPanel.add(lblMapImageDirectory, gbc_lblMapImageDirectory);

		lblMapName = new JLabel("Map Name");
		GridBagConstraints gbc_lblMapName = new GridBagConstraints();
		gbc_lblMapName.insets = new Insets(0, 0, 5, 5);
		gbc_lblMapName.gridx = 1;
		gbc_lblMapName.gridy = 1;
		mapsPanel.add(lblMapName, gbc_lblMapName);

		txtImageDirectoryPath = new JTextField();
		txtImageDirectoryPath.setText("Map Image Directory Path");
		GridBagConstraints gbc_txtImageDirectoryPath = new GridBagConstraints();
		gbc_txtImageDirectoryPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtImageDirectoryPath.insets = new Insets(0, 0, 5, 5);
		gbc_txtImageDirectoryPath.gridx = 0;
		gbc_txtImageDirectoryPath.gridy = 2;
		mapsPanel.add(txtImageDirectoryPath, gbc_txtImageDirectoryPath);
		txtImageDirectoryPath.setColumns(10);

		mapDropDown.addActionListener(new ActionListener() {//Open the dropdown menu
			public void actionPerformed(ActionEvent a) {
				mapsLoadingLabel.setVisible(true);

				btnSaveMap.setEnabled(true);		
				rdbtnAddPoints.setEnabled(true);
				rdbtnEditPoints.setEnabled(true);
				rdbtnRemovePoints.setEnabled(true);

				name = mapDropDown.getSelectedItem().toString();//When you select an item, grab the name of the map selected
				if(DEBUG)
					System.out.println("Selected item:"+name);


				destinationFile = new File("src/VectorMaps/" + name + ".jpg");


				destinationFile = new File(destinationFile.getAbsolutePath());
				if(DEBUG)
					System.out.println("New selected item:"+name);


				if (!(name.equals("Select Map"))) {//If the name is not the default: "Select map", go further
					pointArray.clear();
					oldPoints.clear();
					edgeArray.clear();
					newPoints.clear();
					updatedPoints.clear();
					newEdges.clear();
					//ArrayList<Map> mapList = md.getMapsFromLocal(); //Grab all the maps from the database
					if(DEBUG)
						System.out.println("MapList size is "+maps.size());//Print out the size of the maps from the database
					for(int i = 0; i < maps.size(); i++){//Iterate through the mapList until we find the item we are looking for
						if(DEBUG)
							System.out.println("Trying to find name:"+ name + ".jpg");
						if(name.equals(maps.get(i).getMapName()))//Once we find the map:
						{
							currentMap = maps.get(i);//Grab the current map at this position.
							try {
								pointArray = ServerDB.getPointsFromServer(currentMap);
							} catch (PopulateErrorException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}//Populate the point array with all the points found.
							oldPoints = pointArray;
							if(DEBUG)
								System.out.println("Map list size:"+maps.size());

							for(int j = 0; j < pointArray.size(); j++){
								ArrayList<Edge> tmpEdges = pointArray.get(j).getEdges();
								for(int k = 0; k < tmpEdges.size(); k++){
									if (tmpEdges.get(k).getPoint1().getMapId()==tmpEdges.get(k).getPoint2().getMapId()){
										if(DEBUG){
											System.out.println(tmpEdges.get(k).getId());
										}
										edgeArray.add(tmpEdges.get(k));
									}
								}
							}

							if(DEBUG)
								System.out.println("Found map with number of points: "+currentMap.getPointList().size());
							i = maps.size();
						}
					}


					/*				File destinationFile = new File("src/VectorMaps/" + name);
													destinationFile = new File(destinationFile.getAbsolutePath());
													if (!(name.equals("Select Map"))) {*/
					try {
						if(DEBUG)
							System.out.println("The absolute path is: " + destinationFile.getAbsolutePath());
						//System.out.println("Map name " + currentMap.getMapName());

						img = ImageIO.read(destinationFile);
					} catch (IOException g) {
						System.out.println("Invalid Map Selection");
						g.printStackTrace();
					}
				} else {

					chckbxPathMode.setEnabled(false);
					roomNumber.setEnabled(true);
					btnSavePoint.setEnabled(false);
					btnSaveMap.setEnabled(false);				
					rdbtnAddPoints.setEnabled(false);
					rdbtnEditPoints.setEnabled(false);
					rdbtnRemovePoints.setEnabled(false);
					btnConnectToOther.setEnabled(false);

					File logo = new File("src/VectorLogo/VectorrLogo.png");
					File logoFinal = new File(logo.getAbsolutePath());
					//System.out.println("logoFinal: " + logoFinal);
					try{
						img = ImageIO.read(logoFinal);
					}
					catch(IOException g){
						if(DEBUG)
							System.out.println("Invalid logo");
						g.printStackTrace();
					}
					pointArray.clear();
					edgeArray.clear();
					newPoints.clear();
					updatedPoints.clear();
					newEdges.clear();
				}
				frame.repaint();
				mapsLoadingLabel.setVisible(false);
			}
		});


		// List that stores the name of every Map in the database

		// mapList.add("Select Map");


		mapName = new JTextField();
		mapName.setText("Map Name");
		GridBagConstraints gbc_mapName = new GridBagConstraints();
		gbc_mapName.fill = GridBagConstraints.HORIZONTAL;
		gbc_mapName.insets = new Insets(0, 0, 5, 5);
		gbc_mapName.gridx = 1;
		gbc_mapName.gridy = 2;
		mapsPanel.add(mapName, gbc_mapName);
		mapName.setColumns(10);

		mapsPanel.add(mapsLoadingLabel, gbc_label);

		GradientButton findMapFile = new GradientButton("Add Map From File", buttonColor);
		GridBagConstraints gbc_findMapFile = new GridBagConstraints();
		gbc_findMapFile.fill = GridBagConstraints.BOTH;
		gbc_findMapFile.insets = new Insets(0, 0, 0, 5);
		gbc_findMapFile.gridx = 0;
		gbc_findMapFile.gridy = 3;
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
		gbc_btnAddMap.gridy = 3;
		mapsPanel.add(btnAddMap, gbc_btnAddMap);



		btnAddMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mapToAdd == null){
					mapToAdd = new File(txtImageDirectoryPath.getText());
				}
				mapsLoadingLabel.setVisible(true);
				addingMap = true;

				maptitle = mapName.getText();

				btnSaveMap.setEnabled(true);
				rdbtnAddPoints.setEnabled(true);
				rdbtnEditPoints.setEnabled(true);
				rdbtnRemovePoints.setEnabled(true);

				//maptitle = mapName.getText();
				if(DEBUG)
					System.out.println("Map title is: "+maptitle);
				maptitle = maptitle.trim();
				if(DEBUG)
					System.out.println("Map title after trim is: "+maptitle);
				String mapNameNoExt;
				int l = 0;
				Boolean MapNameExists = false;
				if(DEBUG)
					System.out.println("map to add: " + mapToAdd.toString());
				// check directory to see if it exists
				if (mapToAdd.exists() && mapToAdd.isFile()){
					srcInput = mapToAdd.toString();
					srcFile = mapToAdd;
					for (int y = 0; y < mapDropDown.getItemCount(); y++){
						if (mapDropDown.getItemAt(y).toString().equals(maptitle)){
							mapName.setText("Map Name Already Exists");
							MapNameExists = true;
							y = mapDropDown.getItemCount();
						}
					}
				}else {
					txtImageDirectoryPath.setText("Error Map Directory is Invalid");
					addingMap = false;
				}
				if ((maptitle == null || maptitle.equals("Map Name") || maptitle.equals("")) || MapNameExists) {
					if(MapNameExists){
						addingMap = false;
					}else{
						mapName.setText("Map Name Invalid");
						addingMap = false;
					}
				} else {


					if (MapNameExists == false){
						for (int k = 0; k < mapDropDown.getItemCount(); k++) {
							l = mapDropDown.getItemAt(k).toString().length();
							mapNameNoExt = mapDropDown.getItemAt(k).toString().substring(0, l - 4);
							addingMap = true;
							if(DEBUG)
								System.out.println("Map name now: " + maptitle);
						}
					}
				}
				if (addingMap){
					// /Users/ibanatoski/Downloads/AtwaterKent2.jpg
					if(DEBUG)
						System.out.println("SavingMap");
					File dest = new File("src/VectorMaps");
					// File destAbs = dest.getAbsoluteFile();

					String destInput = dest.getAbsolutePath();
					// System.out.println("Destination Input: " + destInput);
					// System.out.println("Source Input: " + srcInput);
					
					if(DEBUG)
						System.out.println("Mapname after addingMap: "+maptitle);
					
					destInput = destInput + "/" + maptitle + srcInput.substring(srcInput.length() - 4);
					if(DEBUG)
						System.out.println("dest input: "+destInput);
					File destFile = new File(destInput);
					try {
						copyFileUsingStream(srcFile, destFile);
						img = ImageIO.read(destFile);
					} catch (IOException a) {
						if(DEBUG)
							System.out.println("invalid copy");
						a.printStackTrace();
					}
					if(maps == null || maps.size() == 0){
						setInfo(0, 0, 1, 1, 0);
					} else {
						new MapInserterGUI();
					}
				}
				if(DEBUG)
					System.out.println("maptitle: " + maptitle);
				mapDropDown.addItem(maptitle);
				mapsLoadingLabel.setVisible(false);
			}
		});

		// Map Drop Down List
		mapDropDown.setToolTipText("Select Map");
		GridBagConstraints gbc_mapDropDown = new GridBagConstraints();
		gbc_mapDropDown.fill = GridBagConstraints.BOTH;
		gbc_mapDropDown.gridx = 2;
		gbc_mapDropDown.gridy = 3;
		mapsPanel.add(mapDropDown, gbc_mapDropDown);


		return mapsPanel;
	}



	public JComponent createPointsPanel(){

		JPanel pointsPanel = new JPanel();
		pointsPanel.setBackground(new Color(255, 235, 205));
		GridBagLayout gbl_pointsPanel = new GridBagLayout();
		gbl_pointsPanel.columnWidths = new int[]{26, 84, 136, 160, 205, 203, 146, 0};
		gbl_pointsPanel.rowHeights = new int[]{0, 23, 23, 0, 0, 8, 0};
		gbl_pointsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_pointsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pointsPanel.setLayout(gbl_pointsPanel);

		/* Initialization of the radio buttons */
		ButtonGroup modeSelector = new ButtonGroup();

		verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 3;
		gbc_verticalStrut.gridy = 0;
		pointsPanel.add(verticalStrut, gbc_verticalStrut);
		rdbtnAddPoints = new JRadioButton("Add Points", true);
		rdbtnAddPoints.setEnabled(false);
		GridBagConstraints gbc_rdbtnAddPoints = new GridBagConstraints();
		gbc_rdbtnAddPoints.fill = GridBagConstraints.BOTH;
		gbc_rdbtnAddPoints.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnAddPoints.gridx = 2;
		gbc_rdbtnAddPoints.gridy = 1;
		pointsPanel.add(rdbtnAddPoints, gbc_rdbtnAddPoints);
		modeSelector.add(rdbtnAddPoints);

		// adds the starting location label to the line with starting location
		// options
		JLabel lblStartingLocation = new JLabel("Point Name");
		GridBagConstraints gbc_lblStartingLocation = new GridBagConstraints();
		gbc_lblStartingLocation.gridwidth = 2;
		gbc_lblStartingLocation.fill = GridBagConstraints.VERTICAL;
		gbc_lblStartingLocation.insets = new Insets(0, 0, 5, 5);
		gbc_lblStartingLocation.gridx = 4;
		gbc_lblStartingLocation.gridy = 1;
		pointsPanel.add(lblStartingLocation, gbc_lblStartingLocation);
		lblStartingLocation.setBounds(6, 31, 119, 16);

		rdbtnEditPoints = new JRadioButton("Edit Points");
		rdbtnEditPoints.setEnabled(false);
		rdbtnEditPoints.setPreferredSize(new Dimension(125, 23));
		rdbtnEditPoints.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_rdbtnEditPoints = new GridBagConstraints();
		gbc_rdbtnEditPoints.fill = GridBagConstraints.BOTH;
		gbc_rdbtnEditPoints.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnEditPoints.gridx = 2;
		gbc_rdbtnEditPoints.gridy = 2;
		pointsPanel.add(rdbtnEditPoints, gbc_rdbtnEditPoints);
		modeSelector.add(rdbtnEditPoints);

		chckbxPathMode = new JCheckBox("Path Mode");
		chckbxPathMode.setEnabled(false);
		GridBagConstraints gbc_chckbxPathMode= new GridBagConstraints();
		gbc_chckbxPathMode.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxPathMode.gridx = 3;
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
		roomNumber.setEnabled(true);
		GridBagConstraints gbc_roomNumber = new GridBagConstraints();
		gbc_roomNumber.gridwidth = 2;
		gbc_roomNumber.fill = GridBagConstraints.BOTH;
		gbc_roomNumber.insets = new Insets(0, 0, 5, 5);
		gbc_roomNumber.gridx = 4;
		gbc_roomNumber.gridy = 2;
		pointsPanel.add(roomNumber, gbc_roomNumber);
		roomNumber.setHorizontalAlignment(JTextField.CENTER);
		roomNumber.setText("Hallway");
		roomNumber.setToolTipText("");
		roomNumber.setBounds(6, 174, 438, 30);
		roomNumber.setColumns(1);

		pointsLoadingLabel = new JLabel(loadingIcon);
		pointsLoadingLabel.setOpaque(false);
		GridBagConstraints gbc_lblPointsloadinglabel = new GridBagConstraints();
		gbc_lblPointsloadinglabel.anchor = GridBagConstraints.EAST;
		gbc_lblPointsloadinglabel.gridheight = 3;
		gbc_lblPointsloadinglabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblPointsloadinglabel.gridx = 6;
		gbc_lblPointsloadinglabel.gridy = 1;
		pointsPanel.add(pointsLoadingLabel, gbc_lblPointsloadinglabel);

		rdbtnRemovePoints = new JRadioButton("Remove Points");
		rdbtnRemovePoints.setEnabled(false);
		GridBagConstraints gbc_rdbtnRemovePoints = new GridBagConstraints();
		gbc_rdbtnRemovePoints.fill = GridBagConstraints.BOTH;
		gbc_rdbtnRemovePoints.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnRemovePoints.gridx = 2;
		gbc_rdbtnRemovePoints.gridy = 3;
		pointsPanel.add(rdbtnRemovePoints, gbc_rdbtnRemovePoints);
		modeSelector.add(rdbtnRemovePoints);

		btnSavePoint = new GradientButton("No Point Selected", buttonColor);
		btnSavePoint.setEnabled(false);


		btnSavePoint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (editingPoint) {
					if(DEBUG)
						System.out.println("Updating changed points");
					editPoint.setName(roomNumber.getText());
					if(updatedPoints.contains(editPoint)){
						for(int r = 0; r < updatedPoints.size(); r++){
							if(editPoint.getId().contentEquals(updatedPoints.get(r).getId())){
								updatedPoints.set(r, currentPoint);
								r = updatedPoints.size();
							}
						}
					} else {
						updatedPoints.add(editPoint);
					}
					editingPoint = false;
				} else {

				}
				if (rdbtnEditPoints.isSelected())
					roomNumber.setText("Select Point to Edit");
			}
		});
		GridBagConstraints gbc_btnSavePoint = new GridBagConstraints();
		gbc_btnSavePoint.fill = GridBagConstraints.BOTH;
		gbc_btnSavePoint.insets = new Insets(0, 0, 5, 5);
		gbc_btnSavePoint.gridx = 4;
		gbc_btnSavePoint.gridy = 3;
		pointsPanel.add(btnSavePoint, gbc_btnSavePoint);

		btnConnectToOther = new GradientButton("Connect to Other Map", buttonColor);
		btnConnectToOther.setEnabled(false);
		GridBagConstraints gbc_btnConnectToOther = new GridBagConstraints();
		gbc_btnConnectToOther.fill = GridBagConstraints.BOTH;
		gbc_btnConnectToOther.insets = new Insets(0, 0, 5, 5);
		gbc_btnConnectToOther.gridx = 5;
		gbc_btnConnectToOther.gridy = 3;
		pointsPanel.add(btnConnectToOther, gbc_btnConnectToOther);

		btnConnectToOther.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(editPoint != null && maps != null)
					connectMapGUI = new InterMapEdgeGUI(maps, editPoint, drawPanel.getWidth(), drawPanel.getHeight());

			}
		});


		btnSaveMap = new GradientButton("Save Map", buttonColor); // defined above to change text in
		btnSaveMap.setEnabled(false);
		// point selector
		GridBagConstraints gbc_btnSaveMap = new GridBagConstraints();
		gbc_btnSaveMap.insets = new Insets(0, 0, 5, 5);
		gbc_btnSaveMap.fill = GridBagConstraints.BOTH;
		gbc_btnSaveMap.gridx = 3;
		gbc_btnSaveMap.gridy = 4;
		pointsPanel.add(btnSaveMap, gbc_btnSaveMap);

		btnSaveMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnSaveMap.setText("Saving");
				pointsLoadingLabel.setVisible(true);
				btnSaveMap.setText("Saving");
				for (int i = 0; i < newPoints.size(); i++){
					try {
						ServerDB.insertPoint(currentMap, newPoints.get(i));
						if(DEBUG)
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
				}

				for (int j = 0; j < updatedPoints.size(); j++){
					try {
						if(!newPoints.contains(updatedPoints.get(j))){
							ServerDB.updatePoint(updatedPoints.get(j));
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (DoesNotExistException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}


				if(DEBUG)
					System.out.println("Edge array size is: " + edgeArray.size());
				for (int i = 0; i < newEdges.size(); i++) {
					Edge storeEdge = newEdges.get(i);
					storeEdge.setId(storeEdge.getPoint1().getId() + "-" + storeEdge.getPoint2().getId());
					if(DEBUG){
						System.out.println("Storing Edge: " + storeEdge.getId());
						System.out.println("Storing Edge point 1: " + storeEdge.getPoint1().getId());
						System.out.println("Storing Edge point 2: " + storeEdge.getPoint2().getId());
					}
					try {
						ServerDB.insertEdge(storeEdge);
					} catch (InsertFailureException | AlreadyExistsException | SQLException
							| DoesNotExistException g) {
						// TODO Auto-generated catch block
						System.out.println(g.getMessage());
					}
				}

				newPoints.clear();
				updatedPoints.clear();
				newEdges.clear();
				edgeArray.clear();
				try {
					pointArray = ServerDB.getPointsFromServer(currentMap);
				} catch (PopulateErrorException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for(int j = 0; j < pointArray.size(); j++){
					ArrayList<Edge> tmpEdges = pointArray.get(j).getEdges();
					for(int k = 0; k < tmpEdges.size(); k++){
						if (tmpEdges.get(k).getPoint1().getMapId()==tmpEdges.get(k).getPoint2().getMapId()){
							if(DEBUG){
								System.out.println(tmpEdges.get(k).getId());
							}
							edgeArray.add(tmpEdges.get(k));
						}
					}
				}
				editingPoint = false;
				frame.repaint();
				if (rdbtnEditPoints.isSelected())
					roomNumber.setText("Select Point to Edit");
				pointsLoadingLabel.setVisible(false);
				btnSaveMap.setText("Saved Map");
			}
		});
		rdbtnAddPoints.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				chckbxPathMode.setEnabled(false);
				btnSavePoint.setEnabled(false);
				roomNumber.setEnabled(true);
				roomNumber.setText("Hallway");
				lblStartingLocation.setText("Select Point Name");
				btnConnectToOther.setEnabled(false);
				editPoint = null;
				frame.repaint();
			}
		});
		rdbtnEditPoints.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				chckbxPathMode.setEnabled(true);
				btnSavePoint.setEnabled(true);
				roomNumber.setEnabled(true);
				roomNumber.setText("Select a Point to Edit");
				btnConnectToOther.setEnabled(true);
				frame.repaint();

			}
		});
		rdbtnRemovePoints.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				chckbxPathMode.setEnabled(false);
				btnSavePoint.setEnabled(false);
				roomNumber.setEnabled(false);
				btnConnectToOther.setEnabled(false);
				editPoint = null;
				frame.repaint();
			}
		});
		pointsLoadingLabel.setVisible(false);

		return pointsPanel;

	}

	public static void setInfo(double x, double y, double x2, double y2, double angle){
		if(DEBUG)
			System.out.println("setting info");
		//frame.setVisible(true);
		if (addingMap) {

			// Add the name of the map to the Map Selction Dropdown menu

			// Finds the highest mapID in the database and stores it in
			// highestID
			int highestID;
			if(md.getMapsFromLocal().isEmpty()){
				highestID = 0;
				if(DEBUG)
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
			Map m = new Map(highestID + 1, maptitle, x, y, x2, y2, angle, 0);
			System.out.println("m top left x: " + x);
			System.out.println("m top left y: " + y);
			System.out.println("m bot right x: " + x2);
			System.out.println("m bot right y: " + y2);
			highestID++;
			if(DEBUG)
				System.out.println("rotation angle = " + m.getRotationAngle());
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
			mapDropDown.setSelectedIndex(mapDropDown.getItemCount()-1);
			if(DEBUG)
				System.out.println(mapDropDown.getItemCount());
			addingMap = false;
		} else {

		}
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
			
			Graphics2D g2D = (Graphics2D) g;

			// -------------------------------
			// if(img == null)
			// img = ImageIO.read(new
			// File("/User/ibanatoski/git/CampusMapping/src/VectorMaps/"));
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



			//selecting points on the map
			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					newClick = false;
					lastMousex = e.getX();
					lastMousey = e.getY();
					if(tabs.getSelectedIndex() == 1)
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
					Integer nameNumber = currentMap.getPointIDIndex()+1;
					double ourRotation = currentMap.getRotationAngle();
					ourRotation = 2 * Math.PI - ourRotation;

					destinationFile = new File("src/VectorMaps/" + "Campus" + ".jpg");
					destinationFile = new File(destinationFile.getAbsolutePath());
					BufferedImage campusImage = null;
					try {
						if(DEBUG)
							System.out.println("The absolute path is: " + destinationFile.getAbsolutePath());
						//System.out.println("Map name " + currentMap.getMapName());

						campusImage = ImageIO.read(destinationFile);
					} catch (IOException e) {
						System.out.println("Invalid Map Selection");
						e.printStackTrace();
					}
					//double centerCurrentMapX = (Math.floor(((currentMap.getxTopLeft() + currentMap.getxBotRight()) / 2) * img.getWidth())) / (campusImage.getWidth());
					//double centerCurrentMapY = (Math.floor(((currentMap.getyTopLeft() + currentMap.getyBotRight()) / 2) * img.getHeight())) / (campusImage.getHeight());
					double tempPreRotateX = lastMousex;
					double tempPreRotateY = lastMousey;
					
					tempPreRotateX = tempPreRotateX/(img.getWidth()/windowScale);
					tempPreRotateY = tempPreRotateY/(img.getHeight()/windowScale);
					tempPreRotateX = tempPreRotateX - 0.5;
					tempPreRotateY = tempPreRotateY - 0.5;
					tempPreRotateX = tempPreRotateX * currentMap.getWidth();
					tempPreRotateY = tempPreRotateY * currentMap.getHeight();
					double rotateX = Math.cos(ourRotation) * tempPreRotateX - Math.sin(ourRotation) * tempPreRotateY;
					double rotateY = Math.sin(ourRotation) * tempPreRotateX + Math.cos(ourRotation) * tempPreRotateY;
					rotateX = rotateX * campusImage.getWidth();
					rotateY = rotateY * campusImage.getHeight();
					int finalGlobX = (int) Math.round(rotateX + (campusImage.getWidth() * (currentMap.getxTopLeft() + currentMap.getxBotRight()) / 2));
					int finalGlobY = (int) Math.round(rotateY + (campusImage.getHeight() * (currentMap.getyTopLeft() + currentMap.getyBotRight()) / 2));
					
					
					if(DEBUG)
						System.out.println("newest map id: "+currentMap.getNewPointID());
					Point point = new Point(currentMap.getNewPointID(), currentMap.getMapId(),
							roomNumber.getText(), currentMap.getPointIDIndex(),
							lastMousex, lastMousey, finalGlobX, finalGlobY, numEdges);

					boolean shouldAdd = true;
					for(int k = 0; k < pointArray.size(); k++){
						//System.out.println(pointArray.get(k).getId());
						if(point.getId().equals(pointArray.get(k).getId())){
							//System.out.println(pointArray.get(k).getId());
							//System.out.println("should add = false");
							shouldAdd = false;
						}
					}
					if(shouldAdd){
						pointArray.add(point);
						newPoints.add(point);
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

					if (newClick == true && tabs.getSelectedIndex() == 1) {
						//if(newClick == true){
						//TODO CHANGE THIS LATER, GOOD FOR TESTING CLICKS
						MapUpdaterGUI.btnSaveMap.setText("Save Map, X:" + lastMousex + ", Y:" + lastMousey);

						switch (getRadButton()) {
						case 2:// edit points
							if ((lastMousex > currentPoint.getLocX() - (pointSize + 1)
									&& lastMousex < currentPoint.getLocX() + (pointSize + 1))
									&& (lastMousey > currentPoint.getLocY() - (pointSize + 1)
											&& lastMousey < currentPoint.getLocY() + (pointSize + 1))) {
								if (newClick == true && editingPoint == false) {
									editPoint = currentPoint;
									editPointIndex = i;
									roomNumber.setText(editPoint.getName());
									btnSavePoint.setText("Unselect Current Point");
									editingPoint = true;
									newClick = false;/*
								g.setColor(Color.ORANGE);
								g.fillOval(currentPoint.getLocX() - 4, currentPoint.getLocY() - 4, 8, 8);*/
								} else if (newClick == true && editingPoint == true) {
									if(editPoint.getId().contentEquals(currentPoint.getId())){

									} else {
										currentEdge = new Edge(editPoint, currentPoint);
										pointArray.set(editPointIndex, editPoint);
										pointArray.set(i, currentPoint);
										if(!(updatedPoints.contains(editPoint))){
											updatedPoints.add(editPoint);
										} else {
											for(int r = 0; r < updatedPoints.size(); r++){
												if(editPoint.getId().contentEquals(updatedPoints.get(r).getId())){
													updatedPoints.set(r, editPoint);
													r = updatedPoints.size();
												}
											}
										}
										if(!(updatedPoints.contains(currentPoint))){
											updatedPoints.add(currentPoint);
										} else {
											for(int r = 0; r < updatedPoints.size(); r++){
												if(currentPoint.getId().contentEquals(updatedPoints.get(r).getId())){
													updatedPoints.set(r, currentPoint);
													r = updatedPoints.size();
												}
											}
										}
										if(DEBUG){
											System.out.println("Edge sizes- editPoint:"+pointArray.get(editPointIndex).getEdges().size()+
													" currentPoint:"+pointArray.get(i).getEdges().size());
											System.out.println("Current Edge is: " + currentEdge.getId());
										}
										edgeArray.add(currentEdge);
										if(newEdges.contains(currentEdge)){
											for(int r = 0; r < newEdges.size(); r++){
												if(currentEdge.getId().contentEquals(newEdges.get(r).getId())){
													newEdges.set(r, currentEdge);
													r = newEdges.size();
												}
											}
										} else {
											newEdges.add(currentEdge);
										}

										if (currentPoint.getNumEdges() > 0)//this has to be caught in an exception later
										{
											for (int j = 0; j < currentPoint.getNumEdges(); j++) {
												if(DEBUG){
												System.out.println("Adding clicked edge between: "
														+ currentPoint.getEdges().get(j).getPoint1().getName() + ", "
														+ currentPoint.getEdges().get(j).getPoint2().getName());
												}
											}
										}
									}

									newClick = false;
									if(pathMode){
										Point tempEditPoint = pointArray.get(editPointIndex);
										tempEditPoint.setName(roomNumber.getText());
										pointArray.set(editPointIndex, tempEditPoint);
										if(updatedPoints.contains(tempEditPoint)){
											for(int r = 0; r < updatedPoints.size(); r++){
												if(tempEditPoint.getId().contentEquals(updatedPoints.get(r).getId())){
													updatedPoints.set(r, currentPoint);
													r = updatedPoints.size();
												}
											}
										} else {
											updatedPoints.add(tempEditPoint);
										}
										editPoint = currentPoint;
										editPointIndex = i;
										roomNumber.setText(editPoint.getName());
									}

								}
								repaint();
							}
							break;
						case 3:// remove points
							if(DEBUG){
								System.out.println("Remove point called.");
								System.out.println("Size of edgeArray:"+edgeArray.size());
							}
							for (int j = 0; j < pointArray.size(); j++)
							{
								if(DEBUG){
								System.out.println("Number of edges in point "
										+pointArray.get(j).getId()+": "+pointArray.get(j).getEdges().size());
								}
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
									if(!newPoints.contains(currentPoint)){
										int z = 0;
										while(z < currentPoint.getEdges().size()){
											if(newEdges.contains(currentPoint.getEdges().get(z))){
												newEdges.remove(currentPoint.getEdges().get(z));
												while(edgeArray.contains(currentPoint.getEdges().get(z))){
													edgeArray.remove(currentPoint.getEdges().get(z));
												}
												currentPoint.getEdges().remove(z);
												currentPoint.setNumEdges(currentPoint.getNumEdges() - 1);
											} else {
												z++;
											}
										}

										try{
											if(DEBUG)
												System.out.println("Number of edges in point to be removed:"+currentPoint.getEdges().size());
											ServerDB.removePoint(currentPoint);
										} catch (DoesNotExistException e1){
											System.out.println("Reached Here");
											e1.printStackTrace();
										}
									} else {
										newPoints.remove(currentPoint);
									}

									//edgeArray.remove(markForDelete.get(j).getEdges().get(kj));
									for(int kj = 0; kj < currentPoint.getEdges().size(); kj++){
										//edgeArray.remove(markForDelete.get(j).getEdges().get(kj));

										while(edgeArray.contains(currentPoint.getEdges().get(kj))){
											edgeArray.remove(currentPoint.getEdges().get(kj));
											if(newEdges.contains(currentPoint.getEdges().get(kj))){
												newEdges.remove(currentPoint.getEdges().get(kj));
											}
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
															o = pointArray.get(f).getEdges().size();
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
															o = pointArray.get(f).getEdges().size();
														}
													}
												}
											}
										}
									}
									for(int kj = 0; kj < currentPoint.getEdges().size(); kj++){
										//edgeArray.remove(markForDelete.get(j).getEdges().get(kj));
										while(edgeArray.contains(currentPoint.getEdges().get(kj))){
											edgeArray.remove(currentPoint.getEdges().get(kj));
										}
									}
									if(newPoints.contains(currentPoint)){
										newPoints.remove(currentPoint);
									}
									if(updatedPoints.contains(currentPoint)){
										updatedPoints.remove(currentPoint);
									}
									currentPoint.deleteEdges();
									while(pointArray.contains(currentPoint)){
										pointArray.remove(currentPoint);
									}

								}
								newClick = false;
								frame.repaint();
							}
							break;
						default:
							break;
						}
					}//end of case switch

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

					

					

					if(editPoint != null)
					{
						//g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 5.0f));
						g.setColor(Color.RED);
						//g2D.setColor(Color.RED);
						g.fillOval(editPoint.getLocX() - ((pointSize / 2) + 2), editPoint.getLocY() - ((pointSize / 2)+2), pointSize + 4, pointSize + 4);
						g.setColor(Color.BLACK);
						g.drawOval(editPoint.getLocX() - ((pointSize / 2) + 2), editPoint.getLocY() - ((pointSize / 2)+2), pointSize + 4, pointSize + 4);

					}
					//draw lines between points
				}
				
				

			}//end of traversing pointArray
			
			
			//draws all edges in the edge array
			for (int j = 0; j < edgeArray.size(); j++) {
				
				g.setColor(Color.ORANGE);
				
				//if(!(drawnEdges.contains(edgeArray.get(j)))){
					g.drawLine(edgeArray.get(j).getPoint1().getLocX(), edgeArray.get(j).getPoint1().getLocY(),
						edgeArray.get(j).getPoint2().getLocX(), edgeArray.get(j).getPoint2().getLocY());
					drawnEdges.add(edgeArray.get(j));
					g.setColor(Color.BLACK);
				//}
			}
			
			for( int w = 0; w < pointArray.size(); w++){
				int drawX = (int) pointArray.get(w).getLocX();
				int drawY = (int) pointArray.get(w).getLocY();
				
				System.out.println("For point: " + pointArray.get(w).getId() + " loc X val is: " + pointArray.get(w).getLocX() + " glob X val is: " + pointArray.get(w).getGlobX());
				System.out.println("For point: " + pointArray.get(w).getId() + " loc Y val is: " + pointArray.get(w).getLocY() + " glob Y val is: " + pointArray.get(w).getGlobY());
				// draws the points onto the map.
				g.setColor(Color.BLACK);
				
				g.drawOval(drawX - (pointSize / 2), drawY - (pointSize / 2), pointSize, pointSize);
				//g2D.drawPolygon();
				g.setColor(Color.getHSBColor(0.12f, 0.74f, 0.7f));
				g.fillOval(drawX - (pointSize / 2), drawY - (pointSize / 2), pointSize, pointSize);
				g.setColor(Color.BLACK);
			}
			System.out.println("Window scale is: " + windowScale);
			drawnEdges.clear();
			newClick = false;
		}

	}




	/*
	 * Takes an input file directory path and a target directory path and copies
	 * that File to the target location
	 */
	private static void copyFileUsingStream(File source, File dest) throws IOException {
		if(DEBUG)
			System.out.println(source.getPath());
		FileInputStream is = null;
		FileOutputStream os = null;
		is = new FileInputStream(source);
		if(DEBUG)
			System.out.println(source.getPath());
		os = new FileOutputStream(dest);
		if(DEBUG)
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
		//ArrayList<Map> mapList = md.getMapsFromLocal();
		boolean foundMap = false;
		int j = 0;
		for (j = 0; j<maps.size(); j++)
		{
			if (mapId == maps.get(j).getMapId())
			{
				foundMap = true;
				return maps.get(j);
			}
		}
		if (foundMap == false)
		{
			if(DEBUG)
				System.out.println("Failed to find and update map");
		}
		return null;
	}



}