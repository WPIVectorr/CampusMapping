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

import database.AlreadyExistsException;
import database.DoesNotExistException;
import database.InsertFailureException;
import database.MappingDatabase;
import database.NoMapException;

public class MapUpdaterGUI extends JFrame {

	private int lastMousex, lastMousey;
	private int pointSize = 5;
	private boolean newClick = false;
	private boolean editingPoint = false;
	private boolean addingMap = false;
	private ArrayList<Point> pointArray = new ArrayList<Point>();
	private ArrayList<Point> markForDelete = new ArrayList<Point>();
	private Point currentPoint;
	private Point editPoint;
	
	private Map currentMap;
	private MappingDatabase md = MappingDatabase.getInstance();

	private ArrayList<Edge> edgeArray = new ArrayList<Edge>();
	private Edge currentEdge;

	// ---------------------------------
	private int windowScale = 2;
	private int windowSizeX = 932;
	private int windowSizeY = 778;
	BufferedImage img;
	// ---------------------------------

	private JPanel contentPane;
	private static JButton btnSaveMap;
	private static JButton btnSavePoint;
	private static JRadioButton rdbtnAddPoints;
	private static JRadioButton rdbtnEditPoints;
	private static JRadioButton rdbtnRemovePoints;

	// drop down menu of buildings on campus
	String buildings[] = { "Select Building", "Atwater Kent", "Boynton Hall", "Campus Center", "Gordon Library",
			"Higgins House", "Project Center", "Stratton Hall" };

	// drop down menu of room numbers based off of the building selected on
	// campus
	String rooms[] = { "Select room #", "Please choose building first" };
	String[] akRooms = { "Select room #", "10", "20", "30", "40" };
	String[] bhRooms = { "Select room #", "11", "21", "31", "41" };
	String[] ccRooms = { "Select room #", "12", "22", "32", "42" };
	String[] glRooms = { "Select room #", "13", "23", "33", "43" };
	String[] hhRooms = { "Select room #", "14", "24", "34", "44" };
	String[] pcRooms = { "Select room #", "15", "25", "35", "45" };
	String[] shRooms = { "Select room #", "16", "26", "36", "46" };

	int[][] coordTest1 = { { 30, 30 }, { 100, 300 }, { 800, 300 }, { 100, 200 } };
	int[][] coordTest2 = { { 500, 500 }, { 300, 100 }, { 800, 500 }, { 100, 200 } };
	int[][] coordTest3 = { { 400, 200 }, { 50, 30 }, { 8, 300 }, { 10, 20 } };

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
	private JButton btnNewButton;
	private JTextField txtImageDirectoryPath;
	private JComboBox mapDropDown;
	private JButton btnNewButton_1;
	private File mapToAdd;
	private JSplitPane splitPane;

	public MapUpdaterGUI() throws IOException {
		super("MapUpdaterGUI");
		setSize(932, 778);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4, 0, 10, 10));
		buttonPanel.setBackground(new Color(255, 235, 205));

		// Map Drop Down List
		mapDropDown = new JComboBox();
		mapDropDown.setToolTipText("Select Map");
		buttonPanel.add(mapDropDown);
		mapDropDown.addItem("Select Map");

		// When the Updater opens the software the list will be poulated with
		// teh files in
		// the VectorMapps resource folder
		File vectorMapDir = new File("src/VectorMaps");
		vectorMapDir = new File(vectorMapDir.getAbsolutePath());

		// Truncates the extensions off of the map name so only the name is
		// displayed in the
		// dropdown menu for selecting a map
		File[] imgList = vectorMapDir.listFiles();
		String tempMapName;
		int nameLength = 0;
		for (int f = 0; f < imgList.length; f++) {
			/*
			 * tempMapName = imgList[f].getName(); nameLength =
			 * tempMapName.length();
			 * mapDropDown.addItem(tempMapName.substring(0, nameLength - 4));
			 */
			// includes extension
			if(!(imgList[f].getName().equals(".DS_Store"))){
					mapDropDown.addItem(imgList[f].getName());
			}
		}

		mapDropDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				String name = mapDropDown.getSelectedItem().toString();

				
				File destinationFile = new File("src/VectorMaps/" + name);
				destinationFile = new File(destinationFile.getAbsolutePath());
				if (!(name.equals("Select Map"))) {

				
					ArrayList<Map> mapList = md.getMaps();
					//System.out.println("mapsize: "+mapList.size());
					//System.out.println("map Name:"+mapList.get(0).getName());
					for(int i = 0; i < mapList.size(); i++){
						System.out.println("Trying to find name:"+name);
						if(name.equals(mapList.get(i).getName()+".jpg"))
						{
							currentMap = mapList.get(i);
							pointArray = currentMap.getPointList();
							System.out.println("Found map with number of points: "+currentMap.getPointList().size());
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
					img = null;
				}

				repaint();
			}
		});
		// List that stores the name of every Map in the database

		// mapList.add("Select Map");

		Container contentPane = this.getContentPane();
		contentPane.add(buttonPanel, BorderLayout.NORTH);

		// adds the starting location label to the line with starting location
		// options
		JLabel lblStartingLocation = new JLabel("Room Number");
		buttonPanel.add(lblStartingLocation);
		lblStartingLocation.setBounds(6, 31, 119, 16);

		/* Initialization of the radio buttons */
		ButtonGroup modeSelector = new ButtonGroup();
		rdbtnAddPoints = new JRadioButton("Add Points", true);
		buttonPanel.add(rdbtnAddPoints);
		modeSelector.add(rdbtnAddPoints);

		mapName = new JTextField();
		mapName.setText("Map Name");
		buttonPanel.add(mapName);
		mapName.setColumns(10);

		// creates a centered text field that will write back the users info
		// they typed in
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

		/* JButton Add Map */

		getContentPane().add(drawPanel);
		txtImageDirectoryPath = new JTextField();
		txtImageDirectoryPath.setText("Map Image Directory Path");
		buttonPanel.add(txtImageDirectoryPath);
		txtImageDirectoryPath.setColumns(10);
		
				JLabel lblLastPoint = new JLabel("Select a Point to Edit");
				buttonPanel.add(lblLastPoint);

		rdbtnEditPoints = new JRadioButton("Edit Points");
		buttonPanel.add(rdbtnEditPoints);
		modeSelector.add(rdbtnEditPoints);

		/* JButton */
		
		splitPane = new JSplitPane();
		buttonPanel.add(splitPane);
		
		JButton findMapFile = new JButton("Add Map From File");
		splitPane.setLeftComponent(findMapFile);
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
		
				JButton btnAddMap = new JButton("Add Map");
				splitPane.setRightComponent(btnAddMap);
				
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
									if(md.getMaps().isEmpty()){
										highestID = 0;
										System.out.print("Database contains no maps so highest ID is 1");
										
										
									}
									else{
										//determines the highest mapID from the Maps stored in the database
										ArrayList<Map> mdMapList = md.getMaps();
										highestID = mdMapList.get(0).getId();
										for (int h = 0; h < mdMapList.size(); h++) {
											if (highestID < mdMapList.get(h).getId()) {
												highestID = mdMapList.get(h).getId();
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
		
		
		btnSavePoint = new JButton("No Point Selected");
		buttonPanel.add(btnSavePoint);
		btnSaveMap = new JButton("Save Map"); // defined above to change text in
												// point selector
		buttonPanel.add(btnSaveMap);
		getContentPane().add(drawPanel);

		btnSavePoint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (editingPoint) {
					System.out.println("SavePoint");
					editPoint.setName(roomNumber.getText());
					roomNumber.setText("Select a Point to Edit");
					editingPoint = false;
				} else {

				}
			}
		});
		
		btnSaveMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < pointArray.size(); i++) {
					Point storePoint = pointArray.get(i);
					storePoint.setID(i+currentMap.getId()*500);//CHANGE THIS to reflect ID stuff (AND DELETE THIS COMMENT
					System.out.println(currentMap.getName());
						try {
							md.insertPoint(currentMap, storePoint);
							System.out.println("AddPointSuccess");
						} catch (AlreadyExistsException | NoMapException | InsertFailureException | SQLException f) {
							// TODO Auto-generated catch block
							System.out.println(f.getMessage());
							//f.printStackTrace();
						}

					markForDelete.add(storePoint);
				}
				for (int i = 0; i < edgeArray.size(); i++) {
					Edge storeEdge = edgeArray.get(i);

						try {
							md.insertEdge(storeEdge);
						} catch (InsertFailureException | AlreadyExistsException | SQLException
								| DoesNotExistException g) {
							// TODO Auto-generated catch block
							System.out.println(g.getMessage());
						}
				}
			}
		});

	}

	/*
	 * Returns the currently selected radbutton in the form of an int. 1 for
	 * addPoint, 2 for editPoint, 3 for removePoint
	 */
	int getRadButton() {
		int activeButton = 0;
		if (rdbtnAddPoints.isSelected())
			activeButton = 1;
		if (rdbtnEditPoints.isSelected())
			activeButton = 2;
		if (rdbtnRemovePoints.isSelected())
			activeButton = 3;
		return activeButton;
	}

	// assigns the selected building name to have the string of room
	// numbers based off of the building selected
	public String[] generateRoomNums(String select) {
		switch (select) {
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

	public static void main(String[] args) throws IOException {
		MapUpdaterGUI myTest = new MapUpdaterGUI();
		myTest.setVisible(true);

	}

	class DrawRoute extends JPanel {
		
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
				} else {
					wScale = (double) img.getHeight() / (double) windowSizeY;
					windowScale = img.getWidth() / windowSizeX;
				}
				if (wScale > windowScale)
					windowScale += 1;

				// draw image/map
				g.drawImage(img, 0, 0, img.getWidth() / windowScale, img.getHeight() / windowScale, null);
			} else {
				System.out.println("Image is null");
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
					Integer arraySize = pointArray.size();
					Point point = new Point(arraySize, "Point " + arraySize.toString(), lastMousex, lastMousey,
							numEdges);
					pointArray.add(point);
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
					for (int j = 0; j < currentPoint.getNumEdges(); j++) {
						edgeArray.add(currentPoint.getEdges().get(j));
					}
					// newClick has some interesting storage things going on.
					// wtf is with Java
					if (newClick == true) {

						MapUpdaterGUI.btnSaveMap.setText("Save Map, X:" + lastMousex + ", Y:" + lastMousey);

						switch (getRadButton()) {
						case 2:// edit points
							if ((lastMousex > currentPoint.getX() - (pointSize + 5)
									&& lastMousex < currentPoint.getX() + (pointSize + 5))
									&& (lastMousey > currentPoint.getY() - (pointSize + 5)
											&& lastMousey < currentPoint.getY() + (pointSize + 5))) {
								if (newClick == true && editingPoint == false) {
									editPoint = currentPoint;
									roomNumber.setText(editPoint.getName());
									btnSavePoint.setText("Save Point Changes");
									editingPoint = true;
									newClick = false;
								} else if (newClick == true && editingPoint == true) {
									currentEdge = new Edge(editPoint, currentPoint, edgeWeight);
									if (currentPoint.getNumberEdges() > 0)// this has to be caught in an exception later
									{
										for (int j = 0; j < currentPoint.getNumberEdges(); j++) {
											System.out.println("add edge: "
													+ currentPoint.getEdges().get(j).getPoint1().getName() + ", "
													+ currentPoint.getEdges().get(j).getPoint2().getName());
										}
									}
									newClick = false;
								}
								repaint();
							}
							break;
						case 3:// remove points
							if ((lastMousex > currentPoint.getX() - (pointSize + 5)
									&& lastMousex < currentPoint.getX() + (pointSize + 5))
									&& (lastMousey > currentPoint.getY() - (pointSize + 5)
											&& lastMousey < currentPoint.getY() + (pointSize + 5))) {
								if (newClick == true)
									markForDelete.add(currentPoint);

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
						edgeArray.clear();
						markForDelete.get(j).deleteEdges();
						pointArray.remove(markForDelete.get(j));
						markForDelete.remove(j);
					}
					
					int drawX = (int) currentPoint.getX();
					int drawY = (int) currentPoint.getY();
					// draws the points onto the map.
					g.fillOval(drawX - (pointSize / 2), drawY - (pointSize / 2), pointSize, pointSize);

					//draw lines between points
					for (int j = 0; j < edgeArray.size(); j++) {
						g.drawLine(edgeArray.get(j).getPoint1().getX(), edgeArray.get(j).getPoint1().getY(),
								edgeArray.get(j).getPoint2().getX(), edgeArray.get(j).getPoint2().getY());

					}
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
}