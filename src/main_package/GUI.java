package main_package;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import database.AlreadyExistsException;
import database.DoesNotExistException;
import database.PopulateErrorException;
import database.ServerDB;

import javax.swing.border.TitledBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.StringUtils;

public class GUI implements Runnable{

	private static Thread guiThreadObject;
	private String threadName;

	private boolean DEBUG = false;
	private ServerDB md = ServerDB.getInstance();

	private BufferedImage img = null;
	private BufferedImage tempImg = null;

	private ArrayList<Map> maps = new ArrayList<Map>();
	private ArrayList<Point> route;
	private ArrayList<ArrayList<String>> textDir;
	private int mapPos;
	private int textPos;
	private Point start;
	private Point end;
	private boolean showRoute;
	private boolean showStartPoint = false;
	private boolean showDestPoint = false;
	private JTextPane directionsText;
	private boolean isShowStart = false;
	private boolean isShowDest = false;
	private JPanel mainMenu;
	private JPanel navMenu;
	private JPanel prefMenu;
	private JPanel menus;
	private JPanel panelHelp;
	private CardLayout menuLayout;
	private JPanel panels;
	private CardLayout panelLayout;
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
	private Color backgroundColor = new Color(135,206,250);
	private Color buttonColor = new Color(153, 204, 255);
	private Color starColor = new Color(255, 51, 255);
	private ArrayList<Point> pointArray;
	private ArrayList<Edge> edgeArray;
	private JFrame frame = new JFrame("Directions with Magnitude");
	private JComboBox<String> startMapsDropDown = new JComboBox();
	private JComboBox<Point> destBuilds = new JComboBox();
	private JComboBox<Point> startBuilds = new JComboBox();
	private JComboBox destMapsDropDown = new JComboBox();
	private int pointSize = 10;
	private int originalpointSize = 25;
	private double scaleSize = 1;
	private double drawnposx;
	private double drawnposy;
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
	private int lastMouseX;
	private int lastMouseY;
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
	private Map startMap;
	private Map destMap;
	private String mapTitle = "Select Map";
	//private String startPointName = "Select Start Location";
	//private String destPointName = "Select Destination Location";
	private static SplashPage loadingAnimation;
	private JTextField txtFieldEmail;
	private JLabel txtTimeToDestination;
	private boolean resetPath = false;
	private GradientButton btnSwapStartAndDest;
	private GradientButton directionsButton;
	private ArrayList<Point> roomPointsToDraw = new ArrayList<Point>();
	private JPanel panelDirections;
	private double mousezoomx;
	private double mousezoomy;
	private double minZoomSize;
	private JTextArea txtpnFullTextDir;
	private JTextArea textAreaHelp;
	private JTextField txtSearchStart;
	private JTextField txtSearchDest;
	private GradientButton btnFullTextDirections;
	private boolean newClick = false;
	private Point startPoint = new Point();
	private Point destPoint = new Point();
	private int startPointIndex;
	private int destPointIndex;
	private Map currentMap;
	private ArrayList<Point> tempStartRoom = new ArrayList<Point>();
	private ArrayList<Point> tempDestRoom = new ArrayList<Point>();
	private Point startPointName = new Point();
	private Point destPointName = new Point();
	private String selectedPointID;
	private Point selectedPoint;
	private boolean startIsSelected = false;
	private boolean destIsSelected = false;
	private JLabel lblCurrentMap;



	private double startStarX;
	private double startStarY;
	private double destStarX;
	private double destStarY;
	// Indices for resetting preferences if cancel
	private int stairsIndex = 0;
	private int outsideIndex = 0;
	private int walkSpeedIndex = 0;
	private int themeIndex = 0;



	private static String searchStartPointName;
	private static String searchDestPointName;
	private static Point searchStartPoint;
	private static Point searchDestPoint;
	private Map searchStartMap;
	private Map searchDestMap;
	private static SearchLocation googleStart = new SearchLocation();
	private static SearchLocation googleDest = new SearchLocation();

	private Highlighter hilit;
	private Highlighter.HighlightPainter painter;

	/**
	 * @wbp.parser.entryPoint
	 */
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

		frame.getContentPane().setBackground(Color.WHITE);

		maps = md.getMapsFromLocal();
		allPoints = new ArrayList<Point>();
		System.out.println("All Points: " );
		for(int i = 0; i < maps.size(); i++){
			for(int j = 0; j < maps.get(i).getPointList().size(); j++){
				allPoints.add(maps.get(i).getPointList().get(j));

			}
		}
		//System.out.println("------------------edges check-------------------");
		googleStart.prepData(allPoints);
		googleDest.prepData(allPoints);


		mainMenu = new JPanel();
		mainMenu.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainMenu.setBackground(backgroundColor);

		//create a dummy point for the titles of the dropdowns for selecting points
		startPointName.setName("Select Start Location");
		destPointName.setName("Select Destination Location");

		startPoint = startPointName;
		destPoint = destPointName;

		//destMapsDropDown.setEnabled(startIsSelected);

		GridBagLayout gbl_mainMenu = new GridBagLayout();
		gbl_mainMenu.columnWidths = new int[]{80, 90, 113, 62, 150, 90, 83, 30, 62, 80};
		gbl_mainMenu.rowHeights = new int[]{10, 18, 27, 0, 0, 0, 0, 0, 0};
		gbl_mainMenu.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_mainMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		mainMenu.setLayout(gbl_mainMenu);

		navMenu = new JPanel();
		navMenu.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		navMenu.setBackground(backgroundColor);

		GridBagLayout gbl_navMenu = new GridBagLayout();
		gbl_navMenu.columnWidths = new int[]{30, 215, 290, 215, 30};
		gbl_navMenu.rowHeights = new int[]{15, 19, 0, 45, 20, 30, 40, 0};
		gbl_navMenu.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_navMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		navMenu.setLayout(gbl_navMenu);

		JPanel aboutMenu = new JPanel();
		aboutMenu.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		aboutMenu.setBackground(backgroundColor);

		GridBagLayout gbl_aboutMenu = new GridBagLayout();
		gbl_aboutMenu.columnWidths = new int[]{50, 280, 50, 280, 50};
		gbl_aboutMenu.rowHeights = new int[]{13, 19, 0, 20, 20, 20, 20, 30, 0};
		gbl_aboutMenu.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_aboutMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		aboutMenu.setLayout(gbl_aboutMenu);

		menus = new JPanel(new CardLayout());
		menuLayout = (CardLayout) menus.getLayout();
		menus.add(mainMenu, "Main Menu");
		menus.add(navMenu, "Nav Menu");
		menus.add(createPrefMenu(), "Pref Menu");
		menus.add(aboutMenu, "About Menu");

		panelHelp = new JPanel();
		GridBagLayout gbl_panelHelp = new GridBagLayout();
		gbl_panelHelp.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panelHelp.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panelHelp.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panelHelp.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		panelHelp.setLayout(gbl_panelHelp);
		panelHelp.setVisible(false);
		panelHelp.setBackground(backgroundColor);

		panels = new JPanel(new CardLayout());
		panelLayout = (CardLayout) panels.getLayout();
		panels.add((JPanel) drawPanel, "Draw Panel");
		panels.add(panelHelp, "Help Panel");

		JLabel lblTitle = new JLabel("Vectorr Solutions - Team Five         ");
		lblTitle.setFont(new Font("Sitka Text", Font.PLAIN, 22));
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.gridwidth = 3;
		gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblTitle.gridx = 1;
		gbc_lblTitle.gridy = 0;
		aboutMenu.add(lblTitle, gbc_lblTitle);

		JLabel lblNewLabel = new JLabel("Worcester Polytechnic Institute   -   CS3733 B15   -   Prof. Wilson Wong   -   Team Coach: Nick McMahon                        ");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 3;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 1;
		aboutMenu.add(lblNewLabel, gbc_lblNewLabel);

		JLabel lblTeamMembers = new JLabel("Team Members");
		GridBagConstraints gbc_lblTeamMembers = new GridBagConstraints();
		gbc_lblTeamMembers.insets = new Insets(0, 0, 5, 5);
		gbc_lblTeamMembers.gridx = 2;
		gbc_lblTeamMembers.gridy = 2;
		aboutMenu.add(lblTeamMembers, gbc_lblTeamMembers);

		JLabel lblIan = new JLabel("Ian Banatoski - Lead Graphics Engineer");
		GridBagConstraints gbc_lblIan = new GridBagConstraints();
		gbc_lblIan.insets = new Insets(0, 0, 5, 5);
		gbc_lblIan.gridx = 1;
		gbc_lblIan.gridy = 3;
		aboutMenu.add(lblIan, gbc_lblIan);

		JLabel lblCassidy = new JLabel("Cassidy Litch - Lead Test Eng. (It. 1 & 2), Proj. Manager (It. 3 & 4)");
		GridBagConstraints gbc_lblCassidy = new GridBagConstraints();
		gbc_lblCassidy.insets = new Insets(0, 0, 5, 5);
		gbc_lblCassidy.gridx = 3;
		gbc_lblCassidy.gridy = 3;
		aboutMenu.add(lblCassidy, gbc_lblCassidy);

		JLabel lblBrett = new JLabel("Brett Cohen - Lead UI Engineer");
		GridBagConstraints gbc_lblBrett = new GridBagConstraints();
		gbc_lblBrett.insets = new Insets(0, 0, 5, 5);
		gbc_lblBrett.gridx = 1;
		gbc_lblBrett.gridy = 4;
		aboutMenu.add(lblBrett, gbc_lblBrett);

		JLabel lblPaul = new JLabel("Paul Raynes - Lead Soft. Eng. (It. 1 & 2), Product Owner (It. 3 & 4)");
		GridBagConstraints gbc_lblPaul = new GridBagConstraints();
		gbc_lblPaul.insets = new Insets(0, 0, 5, 5);
		gbc_lblPaul.gridx = 3;
		gbc_lblPaul.gridy = 4;
		aboutMenu.add(lblPaul, gbc_lblPaul);

		JLabel lblJosh = new JLabel("Josh Graff - Product Owner (Iterations 1 & 2)");
		GridBagConstraints gbc_lblJosh = new GridBagConstraints();
		gbc_lblJosh.insets = new Insets(0, 0, 5, 5);
		gbc_lblJosh.gridx = 1;
		gbc_lblJosh.gridy = 5;
		aboutMenu.add(lblJosh, gbc_lblJosh);

		JLabel lblBrian = new JLabel("Brian Rubenstein - Project Manager (Iterations 1 & 2)");
		GridBagConstraints gbc_lblBrian = new GridBagConstraints();
		gbc_lblBrian.insets = new Insets(0, 0, 5, 5);
		gbc_lblBrian.gridx = 3;
		gbc_lblBrian.gridy = 5;
		aboutMenu.add(lblBrian, gbc_lblBrian);

		JLabel lblAlexi = new JLabel("Alexi Kessler - Lead Software Engineer (Iterations 3 & 4)");
		GridBagConstraints gbc_lblAlexi = new GridBagConstraints();
		gbc_lblAlexi.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlexi.gridx = 1;
		gbc_lblAlexi.gridy = 6;
		aboutMenu.add(lblAlexi, gbc_lblAlexi);

		JLabel lblSteven = new JLabel("Steven Ruotolo - Lead Test Engineer (Iterations 3 & 4)");
		GridBagConstraints gbc_lblSteven = new GridBagConstraints();
		gbc_lblSteven.insets = new Insets(0, 0, 5, 5);
		gbc_lblSteven.gridx = 3;
		gbc_lblSteven.gridy = 6;
		aboutMenu.add(lblSteven, gbc_lblSteven);

		GradientButton btnReturnToOptions = new GradientButton("Return to Options", buttonColor);
		btnReturnToOptions.setText("Close");
		GridBagConstraints gbc_btnReturnToOptions = new GridBagConstraints();
		gbc_btnReturnToOptions.insets = new Insets(0, 0, 0, 5);
		gbc_btnReturnToOptions.gridx = 2;
		gbc_btnReturnToOptions.gridy = 7;
		aboutMenu.add(btnReturnToOptions, gbc_btnReturnToOptions);
		btnReturnToOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuLayout.show(menus, "Pref Menu");
				img = tempImg;
				roomPointsToDraw = getRoomPoints(currentMap.getPointList());
				if(isShowStart){
					startIsSelected = true;
					isShowStart = false;
				}
				if(isShowDest){
					destIsSelected = true;
					isShowDest = false;
				}
				frame.repaint();
			}
		});


		destMapsDropDown.addItem("Select Map");
		startMapsDropDown.addItem("Select Map");

		frame.getContentPane().add(menus, BorderLayout.NORTH);
		frame.getContentPane().add(panels, BorderLayout.CENTER);

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
				String toAdd = "";
				boolean prevIsUnderscore = true;
				for(int j = 0; j < maps.get(i).getMapName().length(); j++){
					char tempChar;
					if(prevIsUnderscore){
						tempChar = maps.get(i).getMapName().charAt(j);
						//converts to upper case
						tempChar = Character.toUpperCase(tempChar);
						prevIsUnderscore = false;
					}
					else if (maps.get(i).getMapName().charAt(j) == ('_')){
						prevIsUnderscore = true;
						tempChar = ' ';
					}
					else{
						tempChar = maps.get(i).getMapName().charAt(j);
						prevIsUnderscore = false;
					}
					toAdd += tempChar;
					//mapsDropdown.addItem(maps.get(i).getMapName());
					//DestMaps.addItem(maps.get(i).getMapName());
				}
				//System.out.println("toAdd: " + toAdd);
				temp.add(toAdd);
				//temp.add(maps.get(i).getMapName());

			}

		}
		Collections.sort(temp);
		Collections.sort(maps);
		for(int count = 0; count < temp.size(); count++){
			startMapsDropDown.addItem(temp.get(count));
			destMapsDropDown.addItem(temp.get(count));
		}

		GradientButton btnOptionsMain = new GradientButton("Set Preferences", buttonColor);
		btnOptionsMain.setText("Options\r\n");
		GridBagConstraints gbc_btnOptionsMain = new GridBagConstraints();
		gbc_btnOptionsMain.gridwidth = 2;
		gbc_btnOptionsMain.anchor = GridBagConstraints.SOUTHEAST;
		gbc_btnOptionsMain.insets = new Insets(0, 0, 5, 5);
		gbc_btnOptionsMain.gridx = 7;
		gbc_btnOptionsMain.gridy = 1;
		mainMenu.add(btnOptionsMain, gbc_btnOptionsMain);
		btnOptionsMain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showStartPoint = false;
				showDestPoint = false;
				// Note which menu to return to
				returnMenu = "Main Menu";
				// Show preferences menu
				menuLayout.show(menus, "Pref Menu");
				frame.repaint();
			}
		});

		drawPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				originx = e.getX();
				originy = e.getY();
			}

			public void mouseReleased(MouseEvent a) {
				System.out.println("New Mouse Release");
				System.out.println("Map Title: " + mapTitle);
				if(!(mapTitle.equals("Select Map"))){
					System.out.println("Dragged: " + Dragged);
					if (!Dragged) {
						// System.out.println("not dragged");
						newClick = false;
						lastMouseX = a.getX();
						lastMouseY = a.getY();
						System.out.println("lastMouseX: " + lastMouseX);
						System.out.println("lastMouseY: " + lastMouseY);
						System.out.println("startMapsDropDown selectedItem: " + startMapsDropDown.getSelectedItem().toString());
						if (!(startMapsDropDown.getSelectedItem().equals("Select Map")) || !(destMapsDropDown.getSelectedItem().equals("Select Map")))
							newClick = true;


						//frame.repaint();
					} else {
						// System.out.println("dragged = true");
						Dragged = false;
						drawnfirst = true;
					}


				}
				else{
					if(!(roomPointsToDraw == null)){
						roomPointsToDraw.clear();
						System.out.println("\tHey I'm clearing the roomPointsToDraw");
					}
				}
				frame.repaint();

			}

		});

		frame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){

			@Override
			public void ancestorMoved(HierarchyEvent e) {

			}
			@Override
			public void ancestorResized(HierarchyEvent e) {
				drawnfirst = false;
				frame.repaint();
			}           
		});

		drawPanel.addMouseMotionListener(new MouseMotionListener(){
			public void mouseDragged(MouseEvent g){
				if(!mapTitle.equals("Select Map")){
					//System.out.println("dragged");
					drawnfirst = true;
					Dragged = true;
					mousex = g.getX();
					mousey = g.getY();
					frame.repaint();
				}
			}

			public void mouseMoved(MouseEvent j) {
				mousezoomx = j.getX();
				mousezoomy = j.getY();
			}
		});

		frame.addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(!mapTitle.equals("Select Map")){
					scrolled = true;
					if(!(img == null)){
						minZoomSize = 1 / ((double) img.getWidth() / (double) drawPanel.getWidth());
						int WidthSize = (int) ((double) img.getHeight() * minZoomSize);
						if (WidthSize > (double) drawPanel.getHeight()) {
							minZoomSize = 1 / ((double) img.getHeight() / (double) drawPanel.getHeight());
						}
					}
					//System.out.println(minZoomSize);
					double oldWidth = (img.getWidth() * scaleSize);
					double oldHeight = (img.getHeight() * scaleSize);
					if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL && (!(mapTitle.equals("Select Map")))) {
						drawnfirst = true;
						if (e.getWheelRotation() > 0) {
							if (scaleSize <= 2) {
								scroldirection = 1;
								scaleSize += (scroldirection * .02);
								atMinZoom = false;
							} else {
								atMaxZoom = true;
							}
						} else {
							if (scaleSize >= (minZoomSize)) {
								scroldirection = -1;
								// System.out.println("scale before minus: " +
								// scaleSize);
								scaleSize += (scroldirection * .02);
								// System.out.println("scale minus: " + scaleSize);
								atMaxZoom = false;
							} else {
								atMinZoom = true;
							}
						}
						if(atMaxZoom == false && atMinZoom == false){
							double ogx = ((mousezoomx-drawnposx)/oldWidth);
							double ogy = ((mousezoomy-drawnposy)/oldHeight);
							//System.out.println("old map size: "+oldWidth+", "+oldHeight);
							//System.out.println("drawn pos x and y: "+drawnposx+", "+drawnposy);
							//System.out.println("percentage x and y: "+ogx+", "+ogy);
							double newWidth = (img.getWidth() * scaleSize);
							double newHeight = (img.getHeight() * scaleSize);
							difWidth = ((ogx*(oldWidth-newWidth)));//((ogx*((oldWidth-newWidth)*2)));
							difHeight = ((ogy*(oldHeight-newHeight)));//((ogy*((oldHeight-newHeight)*2)));
							//System.out.println("difference pos: "+movex+", "+movey);
							//System.out.println("move pos: "+difWidth+", "+difHeight);
							//System.out.println("new map size: "+newWidth+", "+newHeight);
							//System.out.println("imagesize in scroll: "+newWidth+", "+newHeight);
							//System.out.println("size diff: "+(oldHeight-newHeight));
							drawnposx += difWidth;
							drawnposy += difHeight;
						}else{
							difHeight = 0;
						}
						drawPanel.repaint();
					} else { // scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL

					}
				}
			}
		});

		JLabel lblStart = new JLabel("Start");
		lblStart.setFont(new Font("Tahoma", Font.PLAIN, 20));
		GridBagConstraints gbc_lblStart = new GridBagConstraints();
		gbc_lblStart.gridwidth = 3;
		gbc_lblStart.insets = new Insets(0, 0, 5, 5);
		gbc_lblStart.gridx = 1;
		gbc_lblStart.gridy = 2;
		mainMenu.add(lblStart, gbc_lblStart);


		JLabel lblDestination_1 = new JLabel("Destination");
		lblDestination_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		GridBagConstraints gbc_lblDestination_1 = new GridBagConstraints();
		gbc_lblDestination_1.gridwidth = 4;
		gbc_lblDestination_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestination_1.gridx = 5;
		gbc_lblDestination_1.gridy = 2;
		mainMenu.add(lblDestination_1, gbc_lblDestination_1);


		GradientButton btnClearStart = new GradientButton("X", new Color(240,128,128));
		btnClearStart.setText("Clear");
		btnClearStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showStartPoint = false;
				txtSearchStart.setText("");
				startMapsDropDown.setSelectedIndex(0);
				try{
					tempImg = img;
					img = ImageIO.read(new File("src/VectorLogo/VectorrLogo.png"));
				}
				catch(IOException g){
					System.out.println("Invalid logo1");
					g.printStackTrace();
				}
				frame.repaint();
			}
		});

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 3;
		mainMenu.add(horizontalStrut, gbc_horizontalStrut);
		GridBagConstraints gbc_btnClearStart = new GridBagConstraints();
		gbc_btnClearStart.insets = new Insets(0, 0, 5, 5);
		gbc_btnClearStart.gridx = 3;
		gbc_btnClearStart.gridy = 3;
		mainMenu.add(btnClearStart, gbc_btnClearStart);

		txtSearchStart = new JTextField();

		txtSearchStart.setText("Search");
		GridBagConstraints gbc_txtSearchStart = new GridBagConstraints();
		gbc_txtSearchStart.gridwidth = 2;
		gbc_txtSearchStart.insets = new Insets(0, 0, 5, 5);
		gbc_txtSearchStart.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSearchStart.gridx = 1;
		gbc_txtSearchStart.gridy = 3;
		mainMenu.add(txtSearchStart, gbc_txtSearchStart);
		txtSearchStart.setColumns(10);


		hilit = new DefaultHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);

		txtSearchStart.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent startSearchTypeEvent) {

				txtSearchStart.setHighlighter(hilit);
				// TODO Auto-generated method stub
				if(startSearchTypeEvent.getKeyCode() != KeyEvent.VK_ENTER )
				{
					String searchString;
					try{

						if(txtSearchStart.getCaretPosition()>0)
						{

							searchString = txtSearchStart.getText().substring(0, txtSearchStart.getCaretPosition());
							System.out.println("Caret Position: "+txtSearchStart.getCaretPosition()+" SearchString: "+searchString);
							searchStartPointName = googleStart.searchFor(searchString);
							if(searchStartPointName != "" )
							{
								//String fullResult = searchString.concat(searchStartPointName).substring(searchString.length()-1);

								txtSearchStart.setText(searchStartPointName);				
								//txtSearchStart.setSelectedTetColor(Color.RED);
								txtSearchStart.setCaretPosition(searchString.length());
								System.out.println("Search Term: "+searchString+" Result: "+searchStartPointName);
							}else{
								System.out.println("no autocomplete found");
							}
							hilit.addHighlight(searchString.length(), searchStartPointName.length(), painter);

						}else{
							txtSearchStart.setText("");
							searchString = "";
						}

						//txtSearchStart.moveCaretPosition(searchString.length());
						//txtSearchStart.select(searchString.length(), searchStartPointName.length());


					}catch(java.lang.IllegalArgumentException searchExcept1){

					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}else if(searchStartPointName != "")
				{
					searchStartPoint = googleStart.getPointFromName(searchStartPointName);
					System.out.println("Enter Pressed, Point: "+searchStartPoint.getName());

					for(int i=0;i<maps.size();i++)
					{
						if(searchStartPoint.getMapId() == maps.get(i).getMapId())
						{
							System.out.println("MapSearchedName: "+maps.get(i).getMapName());
							startMapsDropDown.setSelectedIndex(i+1);
						}
					}

					for(int i=0; i<startBuilds.getItemCount();i++)
					{
						if(searchStartPoint.equals(startBuilds.getItemAt(i)))
							startBuilds.setSelectedIndex(i);
					}
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});


		txtSearchStart.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e){
				// Empty textbox for input upon click if placeholder text
				if (txtSearchStart.getText().equals("Search"))
					txtSearchStart.setText("");

			}

			public void focusLost(FocusEvent e) {
				// If textboxes are empty and somewhere else is clicked, bring back placeholder text
				if (txtSearchStart.getText().equals(""))
					txtSearchStart.setText("Search");
			}
		});

		txtSearchDest = new JTextField();
		txtSearchDest.setText("Search");
		GridBagConstraints gbc_txtSearchDest = new GridBagConstraints();
		gbc_txtSearchDest.gridwidth = 3;
		gbc_txtSearchDest.insets = new Insets(0, 0, 5, 5);
		gbc_txtSearchDest.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSearchDest.gridx = 5;
		gbc_txtSearchDest.gridy = 3;
		mainMenu.add(txtSearchDest, gbc_txtSearchDest);
		txtSearchDest.setColumns(10);

		txtSearchDest.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent destSearchTypeEvent) {
				txtSearchDest.setHighlighter(hilit);

				// TODO Auto-generated method stub
				if(destSearchTypeEvent.getKeyCode() != KeyEvent.VK_ENTER )
				{
					System.out.println(destSearchTypeEvent.getKeyCode());
					try
					{
						String searchString= "";


						if(txtSearchDest.getCaretPosition()>1)
						{
							searchString = txtSearchDest.getText().substring(0, txtSearchDest.getCaretPosition());
							//System.out.println("Caret Position: "+txtSearchDest.getCaretPosition()+" SearchString: "+searchString);
							searchDestPointName = googleDest.searchFor(searchString);
							if(searchDestPointName != "")
							{
								//String searchDestPointName = searchDestPoint.get(0).getName();
								//String fullResult = searchString.concat(searchStartPointName).substring(searchString.length()-1);

								txtSearchDest.setCaretPosition(searchString.length());
								txtSearchDest.setText(searchDestPointName);
								txtSearchDest.setCaretPosition(searchString.length());
								System.out.println("Search Term: "+searchString+" Result: "+searchDestPointName);
							}else{
								System.out.println("no autocomplete found");
							}
							hilit.addHighlight(searchString.length(), searchDestPointName.length(), painter);
						}else if(txtSearchDest.getCaretPosition() == 0){
							txtSearchDest.setText("");
							searchString ="";
						}


					}catch(java.lang.IllegalArgumentException searchExcept1){

					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}

				}else if(searchDestPointName != "")
				{
					searchDestPoint = googleDest.getPointFromName(searchDestPointName);
					System.out.println("Enter Pressed, Point: "+searchDestPoint.getName());

					for(int i=0;i<maps.size();i++)
					{
						if(searchDestPoint.getMapId() == maps.get(i).getMapId())
						{
							System.out.println("MapSearchedName: "+maps.get(i).getMapName());
							destMapsDropDown.setSelectedIndex(i+1);
						}
					}

					for(int i=0; i<destBuilds.getItemCount();i++)
					{
						if(searchDestPoint.equals(destBuilds.getItemAt(i)))
							destBuilds.setSelectedIndex(i);
					}
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});


		txtSearchDest.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e){				
				// Empty textbox for input upon click if placeholder text
				if (txtSearchDest.getText().equals("Search"))
					txtSearchDest.setText("");
			}

			public void focusLost(FocusEvent e) {
				// If textboxes are empty and somewhere else is clicked, bring back placeholder text
				if (txtSearchDest.getText().equals(""))
					txtSearchDest.setText("Search");
			}
		});

		GradientButton btnClearDest = new GradientButton("X", new Color(240,128,128));
		btnClearDest.setText("Clear");
		GridBagConstraints gbc_btnClearDest = new GridBagConstraints();
		gbc_btnClearDest.anchor = GridBagConstraints.EAST;
		gbc_btnClearDest.insets = new Insets(0, 0, 5, 5);
		gbc_btnClearDest.gridx = 8;
		gbc_btnClearDest.gridy = 3;
		mainMenu.add(btnClearDest, gbc_btnClearDest);
		btnClearDest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showDestPoint = false;
				txtSearchDest.setText("");
				destMapsDropDown.setSelectedIndex(0);
				try{
					tempImg = img;
					img = ImageIO.read(new File("src/VectorLogo/VectorrLogo.png"));
				}
				catch(IOException g){
					System.out.println("Invalid logo1");
					g.printStackTrace();
				}
				frame.repaint();
			}
		});

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_1.gridx = 9;
		gbc_horizontalStrut_1.gridy = 3;
		mainMenu.add(horizontalStrut_1, gbc_horizontalStrut_1);


		JLabel lblMaps = new JLabel("Starting Map:");
		lblMaps.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblMaps = new GridBagConstraints();
		gbc_lblMaps.anchor = GridBagConstraints.WEST;
		gbc_lblMaps.fill = GridBagConstraints.VERTICAL;
		gbc_lblMaps.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaps.gridx = 1;
		gbc_lblMaps.gridy = 4;
		mainMenu.add(lblMaps, gbc_lblMaps);

		//creates a dropdown menu with map names
		GridBagConstraints gbc_mapsDropdown = new GridBagConstraints();
		gbc_mapsDropdown.gridwidth = 2;
		gbc_mapsDropdown.fill = GridBagConstraints.BOTH;
		gbc_mapsDropdown.insets = new Insets(0, 0, 5, 5);
		gbc_mapsDropdown.gridx = 2;
		gbc_mapsDropdown.gridy = 4;
		mainMenu.add(startMapsDropDown, gbc_mapsDropdown);

		//adds the correct points for the building specified
		startMapsDropDown.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				drawnfirst = false;
				if (startMapsDropDown.getSelectedItem().equals("Select Map")){
					startBuilds.removeAllItems();
					startBuilds.setEnabled(false);
					if(!(roomPointsToDraw.isEmpty())){
						roomPointsToDraw.clear();
					}
					btnSwapStartAndDest.setEnabled(false);
					directionsButton.setEnabled(false);
					showStartPoint = false;
					try{
						tempImg = img;
						img = ImageIO.read(new File("src/VectorLogo/VectorrLogo.png"));
					}
					catch(IOException g){
						System.out.println("Invalid logo1");
						g.printStackTrace();
					}
					if(currentMap != null){
						currentMap = null;
					}
					if(startMap != null){
						startMap = null;
					}
					startPoint = startPointName;
					startIsSelected = false;
					directionsButton.setEnabled(false);
					frame.repaint();
				}
				else{
					startBuilds.setEnabled(true);
					if (destBuilds.isEnabled()){
						btnSwapStartAndDest.setEnabled(true);
						directionsButton.setEnabled(true);
					}
					buildStartIndex = startMapsDropDown.getSelectedIndex();


					mapTitle = maps.get(buildStartIndex-1).getMapName();
					System.out.println("MapTitle: " + mapTitle);
					currentMap = maps.get(buildStartIndex-1); //old
					startMap = maps.get(buildStartIndex-1); //new	

					if(!(startMap.getMapId() == startPoint.getMapId())){
						startPoint = startPointName;
						startIsSelected = false;
						directionsButton.setEnabled(false);
					}

					//String mapTitle = "AtwaterKent1";
					if(startPoint != null && destPoint != null && startPoint.getMapId() != destPoint.getMapId() || destPoint == null || destPoint.getMapId() == 0){
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
					}

					startBuilds.removeAllItems();
					//-----------------------------------------------------------------------------------------------------------------------------

					if(!(roomPointsToDraw == null)){
						roomPointsToDraw.clear();
					}

					if(buildStartIndex!=0){
						edgeArray = new ArrayList<Edge>();

						pointArray = maps.get(buildStartIndex - 1).getPointList();
						//roomPointsToDraw = maps.get(buildStartIndex - 1).getPointList();
						String pointName = "";

						for(int i = 0; i < pointArray.size(); i++){
							for(int j = 0; j < pointArray.get(i).getEdges().size(); j++){
								edgeArray.add(pointArray.get(i).getEdges().get(j));
							}
							pointName = pointArray.get(i).getName();
							pointName = pointName.trim();
							pointName = pointName.toLowerCase();
							if(!(pointArray == null)){
								if(!((pointName.equals("hallway")) || (pointName.equals("stairs bottom")) || (pointName.equals("stairs top")) || (pointName.equals("stairs")) || (pointName.equals("elevator") || (pointName.equals("room")) || (pointName.equals("path"))))){
									roomPointsToDraw.add(pointArray.get(i));
								}
							}
						}
						boolean check = true;
						System.out.println("number of points: " + maps.get(buildStartIndex-1).getPointList().size());
						tempStartRoom.clear();
						for (int i = 0; i < maps.get(buildStartIndex-1).getPointList().size(); i++){
							check = true;
							if(!maps.get(buildStartIndex-1).getPointList().get(i).getName().equalsIgnoreCase("Hallway") &&
									!maps.get(buildStartIndex-1).getPointList().get(i).getName().contains("Stair") &&
									!maps.get(buildStartIndex-1).getPointList().get(i).getName().equalsIgnoreCase("Path") &&
									!maps.get(buildStartIndex-1).getPointList().get(i).getName().contains("stair") &&
									!maps.get(buildStartIndex-1).getPointList().get(i).getName().equalsIgnoreCase("room") &&
									!maps.get(buildStartIndex-1).getPointList().get(i).getName().contains("Elevator") &&
									!maps.get(buildStartIndex-1).getPointList().get(i).getName().contains("elevator")) {
								if(i > 0){
									//System.out.println("i>0");
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
									if(!(roomPointsToDraw.contains(pointArray.get(i)))){
										roomPointsToDraw.add(pointArray.get(i));
									}
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
						startBuilds.addItem(startPointName);
						for(int count = 0; count < tempStartRoom.size(); count++){
							startBuilds.addItem(tempStartRoom.get(count));
						}		
					}


					if(startIsSelected){
						startBuilds.setSelectedItem(startPoint);
					}
				}
			}
		});


		//adds the correct points for the building specified
		destMapsDropDown.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				drawnfirst = false;
				if (destMapsDropDown.getSelectedItem().equals("Select Map")){
					destBuilds.removeAllItems();
					if(!(roomPointsToDraw.isEmpty())){
						roomPointsToDraw.clear();
					}
					destBuilds.setEnabled(false);
					btnSwapStartAndDest.setEnabled(false);
					directionsButton.setEnabled(false);
					showDestPoint = false;
					try{
						tempImg = img;
						img = ImageIO.read(new File("src/VectorLogo/VectorrLogo.png"));
					}
					catch(IOException g){
						System.out.println("Invalid logo1");
						g.printStackTrace();
					}
					if(currentMap != null){
						currentMap = null;
					}
					if(destMap != null){
						destMap = null;
					}
					destPoint = destPointName;
					destIsSelected = false;
					directionsButton.setEnabled(false);
					frame.repaint();
				}
				else{
					destBuilds.setEnabled(true);
					if (startBuilds.isEnabled()){
						btnSwapStartAndDest.setEnabled(true);
						directionsButton.setEnabled(true);
					}

					buildDestIndex = destMapsDropDown.getSelectedIndex();


					mapTitle = maps.get(buildDestIndex-1).getMapName();

					currentMap = maps.get(buildDestIndex-1);
					destMap = maps.get(buildDestIndex-1); //new

					if(!(destMap.getMapId() == destPoint.getMapId())){
						destPoint = destPointName;
						destIsSelected = false;
						directionsButton.setEnabled(false);
					}


					if(startPoint != null && destPoint != null && startPoint.getMapId() != destPoint.getMapId() || startPoint == null || startPoint.getMapId() == 0){
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
					}

					//startBuilds.removeAllItems();
					destBuilds.removeAllItems();

					if(!(roomPointsToDraw == null)){
						roomPointsToDraw.clear();
					}

					if(buildDestIndex!=0){
						edgeArray = new ArrayList<Edge>();

						pointArray = maps.get(buildDestIndex - 1).getPointList();
						String pointName = "";

						for(int i = 0; i < pointArray.size(); i++){
							for(int j = 0; j < pointArray.get(i).getEdges().size(); j++){
								edgeArray.add(pointArray.get(i).getEdges().get(j));
							}
							pointName = pointArray.get(i).getName();
							pointName = pointName.trim();
							pointName = pointName.toLowerCase();
							if(!(pointArray == null)){
								if(!((pointName.equals("hallway")) || (pointName.equals("stairs bottom")) || (pointName.equals("stairs top")) || (pointName.equals("stairs")) || (pointName.equals("elevator") || (pointName.equals("room")) || (pointName.equals("path"))))){
									roomPointsToDraw.add(pointArray.get(i));
								}
							}
						}

						System.out.println("pointArraysize: " + pointArray.size());
						boolean check = true;
						//System.out.println("building size: " + buildings.length);

						tempDestRoom.clear();
						for (int i = 0; i < pointArray.size(); i++){
							check = true;
							if(!pointArray.get(i).getName().equalsIgnoreCase("Hallway") &&
									!pointArray.get(i).getName().contains("Stair") &&
									!pointArray.get(i).getName().equalsIgnoreCase("Path") &&
									!pointArray.get(i).getName().contains("stair") &&
									!pointArray.get(i).getName().equalsIgnoreCase("room") &&
									!pointArray.get(i).getName().contains("Elevator") &&
									!pointArray.get(i).getName().contains("elevator")){

								if(i > 0){
									System.out.println("i>0");
									for(int count = i-1;count >= 0 ; count--){
										if(pointArray.get(i).getName().compareTo(pointArray.get(count).getName()) == 0){
											System.out.println("Found Duplicate");
											check = false;
											count = -1;
										}
									}
								}

								if(check){

									tempDestRoom.add(pointArray.get(i));
									if(!(roomPointsToDraw.contains(pointArray.get(i)))){
										roomPointsToDraw.add(pointArray.get(i));
									}
									System.out.println("testDestRoom last added: " + maps.get(buildDestIndex - 1).getPointList().get(i));
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
						destBuilds.addItem(destPointName);
						for(int count = 0; count < tempDestRoom.size(); count++){
							destBuilds.addItem(tempDestRoom.get(count));
						}
					}

					if(destIsSelected){
						destBuilds.setSelectedItem(destPoint);
					}
				}
			}
		});

		btnSwapStartAndDest = new GradientButton("Swap Start and Destination", buttonColor);
		btnSwapStartAndDest.setText("Swap");
		btnSwapStartAndDest.setEnabled(false);
		GridBagConstraints gbc_btnSwapStartAndDest = new GridBagConstraints();
		gbc_btnSwapStartAndDest.insets = new Insets(0, 0, 5, 5);
		gbc_btnSwapStartAndDest.gridx = 4;
		gbc_btnSwapStartAndDest.gridy = 4;
		mainMenu.add(btnSwapStartAndDest, gbc_btnSwapStartAndDest);
		btnSwapStartAndDest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (startBuilds.getItemCount() != 0 && destBuilds.getItemCount() != 0){
					int startMapIndex = startMapsDropDown.getSelectedIndex();
					int startPointIndex = startBuilds.getSelectedIndex();

					startMapsDropDown.setSelectedIndex(destMapsDropDown.getSelectedIndex());
					startBuilds.setSelectedIndex(destBuilds.getSelectedIndex());


					destMapsDropDown.setSelectedIndex(startMapIndex);
					destBuilds.setSelectedIndex(startPointIndex);
				}
			}
		});

		JLabel lblDestinationMap = new JLabel("Destination Map:");
		GridBagConstraints gbc_lblDestinationMap = new GridBagConstraints();
		gbc_lblDestinationMap.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblDestinationMap.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestinationMap.gridx = 5;
		gbc_lblDestinationMap.gridy = 4;
		mainMenu.add(lblDestinationMap, gbc_lblDestinationMap);


		GridBagConstraints gbc_destMaps = new GridBagConstraints();
		gbc_destMaps.gridwidth = 3;
		gbc_destMaps.fill = GridBagConstraints.HORIZONTAL;
		gbc_destMaps.insets = new Insets(0, 0, 5, 5);
		gbc_destMaps.gridx = 6;
		gbc_destMaps.gridy = 4;
		mainMenu.add(destMapsDropDown, gbc_destMaps);


		//adds the starting location label to the line with starting location options
		JLabel lblStartingLocation = new JLabel("Starting Room:");
		GridBagConstraints gbc_lblStartingLocation = new GridBagConstraints();
		gbc_lblStartingLocation.fill = GridBagConstraints.BOTH;
		gbc_lblStartingLocation.insets = new Insets(0, 0, 5, 5);
		gbc_lblStartingLocation.gridx = 1;
		gbc_lblStartingLocation.gridy = 5;
		mainMenu.add(lblStartingLocation, gbc_lblStartingLocation);
		lblStartingLocation.setBounds(6, 31, 119, 16);



		//creates drop down box with building names
		GridBagConstraints gbc_startBuilds = new GridBagConstraints();
		gbc_startBuilds.gridwidth = 2;
		gbc_startBuilds.fill = GridBagConstraints.BOTH;
		gbc_startBuilds.insets = new Insets(0, 0, 5, 5);
		gbc_startBuilds.gridx = 2;
		gbc_startBuilds.gridy = 5;
		startBuilds.setEnabled(false);
		mainMenu.add(startBuilds, gbc_startBuilds);
		startBuilds.setBounds(122, 30, 148, 20);
		startBuilds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!(startBuilds.getSelectedIndex() == 0)){
					if (startBuilds.getItemCount() > 1){
						showStartPoint = false;
						showDestPoint = false;
						startStarX = ((Point)(startBuilds.getSelectedItem())).getLocX();
						startStarY = ((Point)(startBuilds.getSelectedItem())).getLocY();
						startMap = currentMap;
						startPoint = (Point) startBuilds.getSelectedItem();
						if(destPoint != destPointName){
							directionsButton.setEnabled(true);
						}

						//If the startPoint and the destPoint are the same then force the startPoint to be
						// "Select
						if(startPoint != startPointName && !(destPoint == startPoint)){
							startIsSelected = true;
							for(int i = 0; i < maps.size(); i++){
								if(startPoint.getMapId() == maps.get(i).getMapId()){
									currentMap = maps.get(i);
									i = maps.size();
								}
							}
							mapTitle = currentMap.getMapName();




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
							roomPointsToDraw = getRoomPoints(currentMap.getPoints());
							drawnfirst = false;
						}
						else{
							startIsSelected = false;
							startPoint = startPointName;
							directionsButton.setEnabled(false);
						}
						if(startPoint == startPointName){
							directionsButton.setEnabled(false);
						}

						System.out.println("Selected Point Name From the DropDown: " + startPoint.getName());
						frame.repaint();
					}
				} else {
					startIsSelected = false;
					startPoint = startPointName;
					directionsButton.setEnabled(false);
				}
			}
		});

		//adds the destination label to the line with destination location options
		JLabel lblDestination = new JLabel("Destination Room:");
		GridBagConstraints gbc_lblDestination = new GridBagConstraints();
		gbc_lblDestination.fill = GridBagConstraints.BOTH;
		gbc_lblDestination.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestination.gridx = 5;
		gbc_lblDestination.gridy = 5;
		mainMenu.add(lblDestination, gbc_lblDestination);
		lblDestination.setBounds(6, 68, 85, 44);
		lblDestination.setLabelFor(destBuilds);

		//adds destBuilds to the dropdown for destination
		GridBagConstraints gbc_destBuilds = new GridBagConstraints();
		gbc_destBuilds.gridwidth = 3;
		gbc_destBuilds.fill = GridBagConstraints.BOTH;
		gbc_destBuilds.insets = new Insets(0, 0, 5, 5);
		gbc_destBuilds.gridx = 6;
		gbc_destBuilds.gridy = 5;
		destBuilds.setEnabled(false);
		mainMenu.add(destBuilds, gbc_destBuilds);
		destBuilds.setBounds(122, 30, 148, 20);
		destBuilds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!(destBuilds.getSelectedIndex() == 0)){
					if (destBuilds.getItemCount() > 1){
						showDestPoint = false;
						showStartPoint = false;
						destStarX = ((Point)(destBuilds.getSelectedItem())).getLocX();
						destStarY = ((Point)(destBuilds.getSelectedItem())).getLocY();
						startMap = currentMap;
						destPoint = (Point) destBuilds.getSelectedItem();
						if(startPoint != startPointName){
							directionsButton.setEnabled(true);
						}
						if(destPoint != destPointName && !(destPoint == startPoint)){
							destIsSelected = true;
							for(int i = 0; i < maps.size(); i++){
								if(destPoint.getMapId() == maps.get(i).getMapId()){
									currentMap = maps.get(i);
									i = maps.size();
								}
							}
							mapTitle = currentMap.getMapName();




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
							roomPointsToDraw = getRoomPoints(currentMap.getPoints());
							drawnfirst = false;
						}
						else{
							destIsSelected = false;
							destPoint = destPointName;
							directionsButton.setEnabled(false);
						}
						System.out.println("Selected Point ID From the DropDown: " + destPoint.getName());
						if(startPoint != startPointName){
							directionsButton.setEnabled(true);
						}
						frame.repaint();
					}
				}
				else{
					directionsButton.setEnabled(false);
				}
			}
		});

		//buttonPanel.add(destBuilds);
		destBuilds.setBounds(122, 80, 148, 20);

		// Button that generates a route and switches to nav display
		directionsButton = new GradientButton("Directions", new Color(0, 255, 127));
		directionsButton.setText("          Directions          ");
		directionsButton.setEnabled(false);
		GridBagConstraints gbc_directionsButton = new GridBagConstraints();
		gbc_directionsButton.insets = new Insets(0, 0, 5, 5);
		gbc_directionsButton.fill = GridBagConstraints.VERTICAL;
		gbc_directionsButton.gridx = 4;
		gbc_directionsButton.gridy = 6;
		mainMenu.add(directionsButton, gbc_directionsButton);
		directionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(startIsSelected && destIsSelected){
					drawnfirst = false;
					// reset text position and map position indexes
					textPos = 0;
					mapPos = 0;
					showStartPoint = false;
					showDestPoint = false;


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

						AStar.reset();

						route = AStar.PathFind(start, end, outside, stairs, allPoints);
						//System.out.println("route variable: " + (route == null));

						if(route != null){
							/*System.out.println("route: ");
																														for(int i = route.size() - 1; i >= 0; i--){
																															System.out.println(route.get(i));
																														}*/

						}
						showRoute = true;
						if (route == null){
							directionsText.setText("No Valid Route.");
						}
						else{
							btnNext.setEnabled(true);
							btnPrevious.setEnabled(false);
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
										System.out.println("Found a valid map!");
										for(int s = 0; s < maps.size(); s++){
											if(multiMapFinalDir.get(r).get(0).getOrigin().getMapId() == maps.get(s).getMapId()){
												dirMaps.add(maps.get(s));
											}
										}
									} else {
										dirMaps.add(new Map());
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
							String secEstString = Integer.toString(secEst);
							if(secEstString.length() == 1){
								secEstString = "0" + secEstString;
							} else if (secEstString.length() == 0){
								secEstString = "00";
							}
							txtTimeToDestination.setText("Estimated Time to Destination: " + minEst + ":" + secEstString);
							int m = mapPos;
							while(multiMapFinalDir.get(m).size() == 0){
								m++;
							}
							File destinationFile = new File("src/VectorMaps/" + dirMaps.get(m).getMapName() + ".png");
							destinationFile = new File(destinationFile.getAbsolutePath());
							try {
								img = ImageIO.read(destinationFile);
							} catch (IOException g) {
								System.out.println("Invalid Map Selection");
								g.printStackTrace();
							}

							String toAdd = "";
							boolean prevIsUnderscore = true;
							for(int j = 0; j < dirMaps.get(m).getMapName().length(); j++){
								char tempChar;
								if(prevIsUnderscore){
									tempChar = dirMaps.get(m).getMapName().charAt(j);
									//converts to upper case
									tempChar = Character.toUpperCase(tempChar);
									prevIsUnderscore = false;
								}
								else if (dirMaps.get(m).getMapName().charAt(j) == ('_')){
									prevIsUnderscore = true;
									tempChar = ' ';
								}
								else{
									tempChar = dirMaps.get(m).getMapName().charAt(j);
									prevIsUnderscore = false;
								}
								toAdd += tempChar;
								//mapsDropdown.addItem(maps.get(i).getMapName());
								//DestMaps.addItem(maps.get(i).getMapName());
							}
							lblCurrentMap.setText("Current Map: " + toAdd);

							frame.repaint();
							for(int r = 0; r < multiMapFinalDir.size(); r++){
								if(multiMapFinalDir.get(r).size() == 0){
									mapPos++;
								}
								else {
									r = multiMapFinalDir.size();
								}
							}

							String fullText = " Full List of Directions:\n";
							directionsText.setText(textDir.get(mapPos).get(0));

							int tempPos = 0;
							for(int i = 0; i < textDir.size(); i++){
								for(int j = 0; j < textDir.get(i).size(); j++){
									tempPos++;
									fullText += " " + tempPos + ". " + textDir.get(i).get(j) + "\n\n";
								}
							}

							txtpnFullTextDir.setText(fullText);
							// Reset text box to top
							txtpnFullTextDir.setCaretPosition(0);
						}

						frame.repaint();
						menuLayout.show(menus, "Nav Menu");
					}

					//if the points are identical, asks the user to input different points
					else{
						directionsText.setText("Pick two different points");
						frame.repaint();
					}
				} else {
					directionsButton.setEnabled(false);
				}
			}
		});

		frame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){

			@Override
			public void ancestorMoved(HierarchyEvent e) {

			}
			@Override
			public void ancestorResized(HierarchyEvent e) {
				drawnfirst = false;
				frame.repaint();
			}           
		});



		frame.addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(!mapTitle.equals("Select Map")){
					minZoomSize = 1 / ((double) img.getWidth() / (double) drawPanel.getWidth());
					int WidthSize = (int) ((double) img.getHeight() * minZoomSize);
					if (WidthSize > (double) drawPanel.getHeight()) {
						minZoomSize = 1 / ((double) img.getHeight() / (double) drawPanel.getHeight());
					}
					//System.out.println(minZoomSize);
					double oldWidth = (img.getWidth() * scaleSize);
					double oldHeight = (img.getHeight() * scaleSize);
					if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL && !mapTitle.equals("SelectMap")){
						drawnfirst = true;
						if (e.getWheelRotation() > 0) {
							if (scaleSize <= 2) {
								scroldirection = 1;
								scaleSize += (scroldirection * .02);
								atMinZoom = false;
							} else {
								atMaxZoom = true;
							}
						} else {
							if (scaleSize >= (minZoomSize)) {
								scroldirection = -1;
								// System.out.println("scale before minus: " +
								// scaleSize);
								scaleSize += (scroldirection * .02);
								// System.out.println("scale minus: " + scaleSize);
								atMaxZoom = false;
							} else {
								atMinZoom = true;
							}
						}
						if(atMaxZoom == false && atMinZoom == false){
							double ogx = ((mousezoomx-drawnposx)/oldWidth);
							double ogy = ((mousezoomy-drawnposy)/oldHeight);
							double newWidth = (img.getWidth() * scaleSize);
							double newHeight = (img.getHeight() * scaleSize);
							difWidth = ((ogx*(oldWidth-newWidth)));
							difHeight = ((ogy*(oldHeight-newHeight)));
							drawnposx += difWidth;
							drawnposy += difHeight;
						}else{
							difHeight = 0;
						}
						frame.repaint();
					} else { // scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL

					}
				}

				// System.out.println(message);
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
		Border textBorder = BorderFactory.createLineBorder(Color.BLACK, 2);

		// Button to return to main menu
		GradientButton btnReturn = new GradientButton("Select New Route", buttonColor);
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Return to main menu, don't show route anymore
				currentMap = null;
				roomPointsToDraw.clear();
				menuLayout.show(menus, "Main Menu");
				showRoute = false;
				panelDirections.setVisible(false);

				btnFullTextDirections.setText("Show Full Text Directions");
				route = null;
				showRoute = false;
				if(startPoint.getMapId() != 0){
					for(int i = 0; i < maps.size(); i++){
						if(maps.get(i).getMapId() == startPoint.getMapId()){
							currentMap = maps.get(i);
							i = maps.size();
						}
					}
					File destinationFile = new File("src/VectorMaps/" + currentMap.getMapName() + ".png");
					destinationFile = new File(destinationFile.getAbsolutePath());
					drawnfirst = false;
					try {
						img = ImageIO.read(destinationFile);
					} catch (IOException g) {
						System.out.println("Invalid Map Selection");
						g.printStackTrace();
					}
					ArrayList<Point> tempPointList = currentMap.getPointList();
					pointArray = tempPointList;
					boolean check = true;
					for(int n = 0; n < tempPointList.size(); n++){
						check = true;
						if(!tempPointList.get(n).getName().equalsIgnoreCase("Hallway") &&
								!tempPointList.get(n).getName().contains("Stair") &&
								!tempPointList.get(n).getName().equalsIgnoreCase("Path") &&
								!tempPointList.get(n).getName().contains("stair") &&
								!tempPointList.get(n).getName().equalsIgnoreCase("room") &&
								!tempPointList.get(n).getName().contains("Elevator") &&
								!tempPointList.get(n).getName().contains("elevator")) {
							if(n > 0){
								//System.out.println("i>0");
								for(int count = n-1;count >= 0 ; count--){
									if(maps.get(buildStartIndex - 1).getPointList().get(n).getName().compareTo(maps.get(buildStartIndex-1).getPointList().get(count).getName()) == 0){
										System.out.println("here");
										check = false;
										count = -1;
									}
								}
							}
							if(check){
								tempStartRoom.add(maps.get(buildStartIndex - 1).getPointList().get(n));
								if(!(roomPointsToDraw.contains(tempPointList.get(n)))){
									roomPointsToDraw.add(tempPointList.get(n));
								}
								//mapsDropdown.addItem(maps.get(i).getMapName());
								//DestMaps.addItem(maps.get(i).getMapName());
							}

						}
					}
				} else {
					directionsButton.setEnabled(false);
					startBuilds.removeAllItems();
					startBuilds.setEnabled(false);
					startMapsDropDown.setSelectedIndex(0);
					if(!(roomPointsToDraw.isEmpty())){
						roomPointsToDraw.clear();
					}
					btnSwapStartAndDest.setEnabled(false);
					directionsButton.setEnabled(false);
					showStartPoint = false;
					try{
						tempImg = img;
						img = ImageIO.read(new File("src/VectorLogo/VectorrLogo.png"));
					}
					catch(IOException g){
						System.out.println("Invalid logo1");
						g.printStackTrace();
					}
					if(currentMap != null){
						currentMap = null;
					}
					if(startMap != null){
						startMap = null;
					}
					startPoint = startPointName;
					startIsSelected = false;
					directionsButton.setEnabled(false);
				}
				frame.repaint();

			}
		});
		GridBagConstraints gbc_btnReturn = new GridBagConstraints();
		gbc_btnReturn.insets = new Insets(0, 0, 5, 5);
		gbc_btnReturn.gridx = 1;
		gbc_btnReturn.gridy = 1;
		navMenu.add(btnReturn, gbc_btnReturn);

		//creates the drop down box with rooms for start (initially waits for the building to have 
		//the specific buildings room numbers)

		//	         buttonPanel.add(startRooms);
		//startRooms.setBounds(296, 30, 148, 20);


		// Initalize this button first so it can be used in return button
		btnFullTextDirections = new GradientButton("Show Full Text Directions", buttonColor);

		GridBagConstraints gbc_btnFullTextDirections = new GridBagConstraints();
		gbc_btnFullTextDirections.insets = new Insets(0, 0, 5, 5);
		gbc_btnFullTextDirections.gridx = 2;
		gbc_btnFullTextDirections.gridy = 1;
		navMenu.add(btnFullTextDirections, gbc_btnFullTextDirections);
		btnFullTextDirections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(panelDirections.isVisible()){
					panelDirections.setVisible(false);
					btnFullTextDirections.setText("Show Full Text Directions");
				}
				else{
					panelDirections.setVisible(true);
					btnFullTextDirections.setText("Hide Full Text Directions");
				}
			}
		});

		GradientButton btnOptionsNav = new GradientButton("Set Preferences", buttonColor);
		btnOptionsNav.setText("Options");
		GridBagConstraints gbc_btnOptionsNav = new GridBagConstraints();
		gbc_btnOptionsNav.insets = new Insets(0, 0, 5, 5);
		gbc_btnOptionsNav.gridx = 3;
		gbc_btnOptionsNav.gridy = 1;
		navMenu.add(btnOptionsNav, gbc_btnOptionsNav);
		btnOptionsNav.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save which menu to return to
				returnMenu = "Nav Menu";
				// Change view to preferences menu, don't show route anymore
				menuLayout.show(menus, "Pref Menu");
				showRoute = false;
				panelDirections.setVisible(false);
				btnFullTextDirections.setText("Show Full Text Directions");

				frame.repaint();
			}
		});

		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_3 = new GridBagConstraints();
		gbc_horizontalStrut_3.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_3.gridx = 0;
		gbc_horizontalStrut_3.gridy = 2;
		navMenu.add(horizontalStrut_3, gbc_horizontalStrut_3);



		//creates a centered text field that will write back the users info they typed in
		directionsText = new JTextPane();
		directionsText.setEditable(false);
		directionsText.setToolTipText("");
		directionsText.setBounds(6, 174, 438, 30);

		UIDefaults defaults = new UIDefaults();
		defaults.put("TextPane[Enabled].backgroundPainter", backgroundColor);
		directionsText.putClientProperty("Nimbus.Overrides", defaults);
		directionsText.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
		directionsText.setBackground(backgroundColor);

		directionsText.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		directionsText.setMinimumSize(new Dimension(720, 65));
		directionsText.setPreferredSize(new Dimension(720, 65));

		StyledDocument doc = directionsText.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		StyleConstants.setBackground(center, backgroundColor);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);



		//directionsText.setColumns(1);
		directionsText.setFont(new Font("Serif", Font.BOLD, 20));
		GridBagConstraints gbc_directionsText = new GridBagConstraints();
		gbc_directionsText.gridheight = 2;
		gbc_directionsText.gridwidth = 5;
		gbc_directionsText.insets = new Insets(0, 0, 5, 0);
		gbc_directionsText.gridx = 0;
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
					int marker = mapPos;
					mapPos--;
					if(multiMapFinalDir.get(mapPos).size() == 0){
						while((mapPos != 0) && (multiMapFinalDir.get(mapPos).size() == 0)){
							mapPos--;
						}
						if(mapPos == 0){
							while(multiMapFinalDir.get(mapPos).size() == 0){
								mapPos++;
							}
						}
						if(mapPos != marker){
							textPos = multiMapFinalDir.get(mapPos).size();
						} else { 
							textPos = 0;
						}
						int m = mapPos;
						while((m != 0) && multiMapFinalDir.get(m).size() == 0){
							m--;
						}
						if(m == 0){
							while(multiMapFinalDir.get(m).size() == 0){
								m++;
							}
						}
						//directionsText.setText(textDir.get(mapPos).get(textPos));
						if(mapPos != marker){
							String toAdd = "";
							boolean prevIsUnderscore = true;
							for(int j = 0; j < dirMaps.get(m).getMapName().length(); j++){
								char tempChar;
								if(prevIsUnderscore){
									tempChar = dirMaps.get(m).getMapName().charAt(j);
									//converts to upper case
									tempChar = Character.toUpperCase(tempChar);
									prevIsUnderscore = false;
								}
								else if (dirMaps.get(m).getMapName().charAt(j) == ('_')){
									prevIsUnderscore = true;
									tempChar = ' ';
								}
								else{
									tempChar = dirMaps.get(m).getMapName().charAt(j);
									prevIsUnderscore = false;
								}
								toAdd += tempChar;
								//mapsDropdown.addItem(maps.get(i).getMapName());
								//DestMaps.addItem(maps.get(i).getMapName());
							}

							directionsText.setText("Enter " + toAdd);
						}
					} else {

						textPos = multiMapFinalDir.get(mapPos).size();
						//directionsText.setText(textDir.get(mapPos).get(textPos));
						String toAdd = "";
						boolean prevIsUnderscore = true;
						for(int j = 0; j < dirMaps.get(mapPos + 1).getMapName().length(); j++){
							char tempChar;
							if(prevIsUnderscore){
								tempChar = dirMaps.get(mapPos + 1).getMapName().charAt(j);
								//converts to upper case
								tempChar = Character.toUpperCase(tempChar);
								prevIsUnderscore = false;
							}
							else if (dirMaps.get(mapPos + 1).getMapName().charAt(j) == ('_')){
								prevIsUnderscore = true;
								tempChar = ' ';
							}
							else{
								tempChar = dirMaps.get(mapPos + 1).getMapName().charAt(j);
								prevIsUnderscore = false;
							}
							toAdd += tempChar;
							//mapsDropdown.addItem(maps.get(i).getMapName());
							//DestMaps.addItem(maps.get(i).getMapName());
						}
						directionsText.setText("Enter " + toAdd);
					}
					File destinationFile = new File("src/VectorMaps/" + dirMaps.get(mapPos).getMapName() + ".png");
					destinationFile = new File(destinationFile.getAbsolutePath());
					drawnfirst = false;
					try {
						img = ImageIO.read(destinationFile);
					} catch (IOException g) {
						System.out.println("Invalid Map Selection");
						g.printStackTrace();
					}

					String toAdd = "";
					boolean prevIsUnderscore = true;
					for(int j = 0; j < dirMaps.get(mapPos).getMapName().length(); j++){
						char tempChar;
						if(prevIsUnderscore){
							tempChar = dirMaps.get(mapPos).getMapName().charAt(j);
							//converts to upper case
							tempChar = Character.toUpperCase(tempChar);
							prevIsUnderscore = false;
						}
						else if (dirMaps.get(mapPos).getMapName().charAt(j) == ('_')){
							prevIsUnderscore = true;
							tempChar = ' ';
						}
						else{
							tempChar = dirMaps.get(mapPos).getMapName().charAt(j);
							prevIsUnderscore = false;
						}
						toAdd += tempChar;
						//mapsDropdown.addItem(maps.get(i).getMapName());
						//DestMaps.addItem(maps.get(i).getMapName());
					}
					lblCurrentMap.setText("Current Map: " + toAdd);

					frame.repaint();
				} else {
					if (textPos != 0){
						timeEst += (Math.floor(multiMapFinalDir.get(mapPos).get(textPos-1).getDistance() / walkSpeed));
					}
					int minEst = (int) Math.floor(timeEst / 60);
					int secEst = timeEst % 60;
					String secEstString = Integer.toString(secEst);
					if(secEstString.length() == 1){
						secEstString = "0" + secEstString;
					} else if (secEstString.length() == 0){
						secEstString = "00";
					}
					txtTimeToDestination.setText("Estimated Time to Destination: " + minEst + ":" + secEstString);
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
		gbc_btnPrevious.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPrevious.insets = new Insets(0, 0, 5, 5);
		gbc_btnPrevious.gridx = 1;
		gbc_btnPrevious.gridy = 5;
		navMenu.add(btnPrevious, gbc_btnPrevious);


		lblCurrentMap = new JLabel("Current Map:");
		lblCurrentMap.setFont(new Font("Serif", Font.PLAIN, 18));
		GridBagConstraints gbc_lblCurrentMap = new GridBagConstraints();
		gbc_lblCurrentMap.gridwidth = 3;
		gbc_lblCurrentMap.insets = new Insets(0, 0, 0, 5);
		gbc_lblCurrentMap.gridx = 1;
		gbc_lblCurrentMap.gridy = 6;
		navMenu.add(lblCurrentMap, gbc_lblCurrentMap);

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
						String secEstString = Integer.toString(secEst);
						if(secEstString.length() == 1){
							secEstString = "0" + secEstString;
						} else if (secEstString.length() == 0){
							secEstString = "00";
						}
						txtTimeToDestination.setText("Estimated Time to Destination: " + minEst + ":" + secEstString);

						textPos++;
						if (!btnPrevious.isEnabled()){
							btnPrevious.setEnabled(true);
						}

						if (textPos != multiMapFinalDir.get(mapPos).size()){
							directionsText.setText(textDir.get(mapPos).get(textPos));
						}
						else if (mapPos != multiMapFinalDir.size() - 1){
							int m = mapPos+1;
							while(dirMaps.get(m).getMapName() == null){
								m++;
							}

							String toAdd = "";
							boolean prevIsUnderscore = true;
							for(int j = 0; j < dirMaps.get(m).getMapName().length(); j++){
								char tempChar;
								if(prevIsUnderscore){
									tempChar = dirMaps.get(m).getMapName().charAt(j);
									//converts to upper case
									tempChar = Character.toUpperCase(tempChar);
									prevIsUnderscore = false;
								}
								else if (dirMaps.get(m).getMapName().charAt(j) == ('_')){
									prevIsUnderscore = true;
									tempChar = ' ';
								}
								else{
									tempChar = dirMaps.get(m).getMapName().charAt(j);
									prevIsUnderscore = false;
								}
								toAdd += tempChar;
								//mapsDropdown.addItem(maps.get(i).getMapName());
								//DestMaps.addItem(maps.get(i).getMapName());
							}

							directionsText.setText("Enter " + toAdd);
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
						boolean mapChange = true;
						while(mapPos < multiMapFinalDir.size() && multiMapFinalDir.get(mapPos).size() == 0 ){
							System.out.println("Incrementing value.");
							mapPos++;
						}
						System.out.println("mapPos to: " + mapPos);
						System.out.println("Size of arrayList super is: " + multiMapFinalDir.size());
						if(mapPos >= multiMapFinalDir.size()){

							mapPos = multiMapFinalDir.size() - 1;
							System.out.println("Reducing mapPos to: " + mapPos);
							if(multiMapFinalDir.get(mapPos).size() == 0){
								mapChange = false;
							}
						}
						//change map
						if(mapChange){
							File destinationFile = new File("src/VectorMaps/" + dirMaps.get(mapPos).getMapName() + ".png");
							directionsText.setText(textDir.get(mapPos).get(textPos));
							drawnfirst = false;


							destinationFile = new File(destinationFile.getAbsolutePath());
							try {
								img = ImageIO.read(destinationFile);
							} catch (IOException g) {
								System.out.println("Invalid Map Selection");
								g.printStackTrace();
							}

							String toAdd = "";
							boolean prevIsUnderscore = true;
							for(int j = 0; j < dirMaps.get(mapPos).getMapName().length(); j++){
								char tempChar;
								if(prevIsUnderscore){
									tempChar = dirMaps.get(mapPos).getMapName().charAt(j);
									//converts to upper case
									tempChar = Character.toUpperCase(tempChar);
									prevIsUnderscore = false;
								}
								else if (dirMaps.get(mapPos).getMapName().charAt(j) == ('_')){
									prevIsUnderscore = true;
									tempChar = ' ';
								}
								else{
									tempChar = dirMaps.get(mapPos).getMapName().charAt(j);
									prevIsUnderscore = false;
								}
								toAdd += tempChar;
								//mapsDropdown.addItem(maps.get(i).getMapName());
								//DestMaps.addItem(maps.get(i).getMapName());
							}
							lblCurrentMap.setText("Current Map: " + toAdd);
						}
						mapChange = true;
						frame.repaint();
					}
					frame.repaint();
				}
			}});

		txtTimeToDestination = new JLabel();
		txtTimeToDestination.setText("Estimated Time to Destination: ");
		GridBagConstraints gbc_txtTimeToDestination = new GridBagConstraints();
		gbc_txtTimeToDestination.insets = new Insets(0, 0, 5, 5);
		gbc_txtTimeToDestination.gridx = 2;
		gbc_txtTimeToDestination.gridy = 5;
		navMenu.add(txtTimeToDestination, gbc_txtTimeToDestination);

		//txtTimeToDestination.setColumns(10);
		txtTimeToDestination.setFont(new Font("Serif", Font.PLAIN, 18));

		GridBagConstraints gbc_btnNext = new GridBagConstraints();
		gbc_btnNext.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNext.insets = new Insets(0, 0, 5, 5);
		gbc_btnNext.gridx = 3;
		gbc_btnNext.gridy = 5;
		navMenu.add(btnNext, gbc_btnNext);


		panelDirections = new JPanel();
		panelDirections.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		frame.getContentPane().add(panelDirections, BorderLayout.WEST);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 110, 110, 0, 0};
		gbl_panel.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, -11, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelDirections.setLayout(gbl_panel);
		panelDirections.setVisible(false);

		txtpnFullTextDir = new JTextArea();

		JScrollPane scrollPane = new JScrollPane(txtpnFullTextDir);
		scrollPane.setMinimumSize(new Dimension(220, 300));
		scrollPane.setPreferredSize(new Dimension(220, 300));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.gridheight = 9;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		panelDirections.add(scrollPane, gbc_scrollPane);


		scrollPane.setViewportView(txtpnFullTextDir);
		scrollPane.setPreferredSize(new Dimension(220, 500));

		// Text box for full list of directions, initially invisible, appears when directions button pressed
		txtpnFullTextDir.setText(" Full List of Directions:");
		txtpnFullTextDir.setEditable(false);

		txtFieldEmail = new JTextField();

		txtFieldEmail.setText("Enter E-Mail Here");
		GridBagConstraints gbc_txtFieldEmail = new GridBagConstraints();
		gbc_txtFieldEmail.insets = new Insets(0, 0, 5, 5);
		gbc_txtFieldEmail.gridwidth = 2;
		gbc_txtFieldEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFieldEmail.gridx = 1;
		gbc_txtFieldEmail.gridy = 10;
		panelDirections.add(txtFieldEmail, gbc_txtFieldEmail);
		txtFieldEmail.setColumns(10);

		txtFieldEmail.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e){
				// Empty textbox for input upon click if placeholder text
				if (txtFieldEmail.getText().equals("Enter E-Mail Here"))
					txtFieldEmail.setText("");
			}

			public void focusLost(FocusEvent e) {
				// If textboxes are empty and somewhere else is clicked, bring back placeholder text
				if (txtFieldEmail.getText().equals(""))
					txtFieldEmail.setText("Enter E-Mail Here");
			}
		});


		JLabel labelEmail = new JLabel("New label");
		GridBagConstraints gbc_labelEmail = new GridBagConstraints();
		gbc_labelEmail.gridwidth = 2;
		gbc_labelEmail.insets = new Insets(0, 0, 5, 5);
		gbc_labelEmail.gridx = 1;
		gbc_labelEmail.gridy = 12;
		panelDirections.add(labelEmail, gbc_labelEmail);
		labelEmail.setVisible(false);

		GradientButton btnEmailDirections = new GradientButton("E-Mail Directions", buttonColor);
		GridBagConstraints gbc_btnEmailDirections = new GridBagConstraints();
		gbc_btnEmailDirections.insets = new Insets(0, 0, 5, 5);
		gbc_btnEmailDirections.gridwidth = 2;
		gbc_btnEmailDirections.anchor = GridBagConstraints.NORTH;
		gbc_btnEmailDirections.gridx = 1;
		gbc_btnEmailDirections.gridy = 11;
		panelDirections.add(btnEmailDirections, gbc_btnEmailDirections);
		btnEmailDirections.setEnabled(false);
		btnEmailDirections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					new PrintDirections(textDir,finalDir,txtFieldEmail.getText());
					txtFieldEmail.setText("");
					labelEmail.setVisible(true);
					labelEmail.setForeground(Color.BLACK);
					labelEmail.setText("Email Successfully Sent!");
				} catch (AddressException e1) {
					// TODO Auto-generated catch block
					labelEmail.setVisible(true);
					labelEmail.setForeground(Color.RED);
					labelEmail.setText("Invalid Address");
				}
			}
		});

		txtFieldEmail.getDocument().addDocumentListener(new DocumentListener()
		{

			public void changedUpdate(DocumentEvent arg0) 
			{

			}
			public void insertUpdate(DocumentEvent arg0) 
			{
				String format = StringUtils.substringAfter(txtFieldEmail.getText(), "@");
				if (format.contains(".") && format.charAt(0) != '.'
						&& format.charAt(format.length()-1) != '.' && !format.contains("@")){
					labelEmail.setVisible(false);
					txtFieldEmail.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					btnEmailDirections.setEnabled(true);
				}
				else if(txtFieldEmail.getText().equals("") || txtFieldEmail.getText().equals("Enter E-Mail Here")){
					labelEmail.setVisible(false);
					txtFieldEmail.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					btnEmailDirections.setEnabled(false);
				}
				else{
					txtFieldEmail.setBorder(BorderFactory.createLineBorder(Color.RED));
					labelEmail.setForeground(Color.RED);
					labelEmail.setText("Invalid Format");
					labelEmail.setVisible(true);
					btnEmailDirections.setEnabled(false);
				}


			}

			public void removeUpdate(DocumentEvent arg0) 
			{
				String format = StringUtils.substringAfter(txtFieldEmail.getText(), "@");
				if (format.contains(".") && format.charAt(0) != '.'
						&& format.charAt(format.length()-1) != '.' && !format.contains("@")){
					labelEmail.setVisible(false);
					txtFieldEmail.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					btnEmailDirections.setEnabled(true);
				}
				else if(txtFieldEmail.getText().equals("") || txtFieldEmail.getText().equals("Enter E-Mail Here")){
					labelEmail.setVisible(false);
					txtFieldEmail.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					btnEmailDirections.setEnabled(false);
				}
				else{
					txtFieldEmail.setBorder(BorderFactory.createLineBorder(Color.RED));
					labelEmail.setForeground(Color.RED);
					labelEmail.setText("Invalid Format");
					labelEmail.setVisible(true);
					btnEmailDirections.setEnabled(false);
				}
			}
		});

		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_4 = new GridBagConstraints();
		gbc_horizontalStrut_4.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_4.gridx = 0;
		gbc_horizontalStrut_4.gridy = 13;
		panelDirections.add(horizontalStrut_4, gbc_horizontalStrut_4);

		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_5 = new GridBagConstraints();
		gbc_horizontalStrut_5.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_5.gridx = 3;
		gbc_horizontalStrut_5.gridy = 13;
		panelDirections.add(horizontalStrut_5, gbc_horizontalStrut_5);

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_2 = new GridBagConstraints();
		gbc_horizontalStrut_2.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_2.gridx = 0;
		gbc_horizontalStrut_2.gridy = 0;
		panelHelp.add(horizontalStrut_2, gbc_horizontalStrut_2);

		JScrollPane scrollPaneHelp = new JScrollPane();
		scrollPaneHelp.setViewportBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_scrollPaneHelp = new GridBagConstraints();
		gbc_scrollPaneHelp.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneHelp.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneHelp.gridx = 1;
		gbc_scrollPaneHelp.gridy = 0;
		panelHelp.add(scrollPaneHelp, gbc_scrollPaneHelp);

		textAreaHelp = new JTextArea();
		textAreaHelp.setEditable(false);
		scrollPaneHelp.setViewportView(textAreaHelp);

		Component horizontalStrut_6 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_6 = new GridBagConstraints();
		gbc_horizontalStrut_6.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_6.gridx = 2;
		gbc_horizontalStrut_6.gridy = 0;
		panelHelp.add(horizontalStrut_6, gbc_horizontalStrut_6);

		GradientButton btnCloseHelp = new GradientButton("Close Help", buttonColor);
		GridBagConstraints gbc_btnCloseHelp = new GridBagConstraints();
		gbc_btnCloseHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnCloseHelp.gridx = 1;
		gbc_btnCloseHelp.gridy = 1;
		panelHelp.add(btnCloseHelp, gbc_btnCloseHelp);

		Component verticalStrut_1 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_1 = new GridBagConstraints();
		gbc_verticalStrut_1.insets = new Insets(0, 0, 0, 5);
		gbc_verticalStrut_1.gridx = 1;
		gbc_verticalStrut_1.gridy = 2;
		panelHelp.add(verticalStrut_1, gbc_verticalStrut_1);
		btnCloseHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				panelHelp.setVisible(false);
				menus.setVisible(true);
				panelLayout.show(panels, "Draw Panel");
				roomPointsToDraw = getRoomPoints(currentMap.getPoints());
			}
		});

		// Make frame visible after initializing everything
		frame.setVisible(true);
	}

	public JPanel createPrefMenu(){

		Hashtable<Integer, JLabel> speeds = new Hashtable<Integer, JLabel>();
		speeds.put(0, new JLabel("Medium"));
		speeds.put(-1, new JLabel("Slow"));
		speeds.put(1, new JLabel("Fast"));

		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(0, new JLabel("Neutral"));
		labels.put(-1, new JLabel("Avoid"));
		labels.put(1, new JLabel("Prefer"));

		prefMenu = new JPanel();
		prefMenu.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		prefMenu.setBackground(backgroundColor);
		GridBagLayout gbl_prefMenu = new GridBagLayout();
		gbl_prefMenu.columnWidths = new int[]{25, 75, 30, 145, 110, 30, 110, 250, 25};
		gbl_prefMenu.rowHeights = new int[]{0, 0, 10, 0, 32, 12, 11, 0};
		gbl_prefMenu.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_prefMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		prefMenu.setLayout(gbl_prefMenu);

		GradientButton btnHelp = new GradientButton("Help", buttonColor);
		GridBagConstraints gbc_btnHelp = new GridBagConstraints();
		gbc_btnHelp.anchor = GridBagConstraints.EAST;
		gbc_btnHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnHelp.gridx = 1;
		gbc_btnHelp.gridy = 0;
		prefMenu.add(btnHelp, gbc_btnHelp);
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{

				panelLayout.show(panels, "Help Panel");

				menus.setVisible(false);
				try{
					FileReader reader = new FileReader("src/VectorLogo/helpMain.txt");
					BufferedReader br = new BufferedReader(reader);
					textAreaHelp.read( br, null );
					br.close();
					textAreaHelp.requestFocus();
				}
				catch(Exception e2) { System.out.println(e2); 
				}
			}
		});

		GradientButton btnAbout = new GradientButton("About", buttonColor);
		GridBagConstraints gbc_btnAbout = new GridBagConstraints();
		gbc_btnAbout.anchor = GridBagConstraints.WEST;
		gbc_btnAbout.insets = new Insets(0, 0, 5, 5);
		gbc_btnAbout.gridx = 3;
		gbc_btnAbout.gridy = 0;
		prefMenu.add(btnAbout, gbc_btnAbout);
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuLayout.show(menus, "About Menu");
				roomPointsToDraw.clear();
				try{
					tempImg = img;
					img = ImageIO.read(new File("src/VectorLogo/VectorrLogo.png"));
				}
				catch(IOException g){
					System.out.println("Invalid logo1");
					g.printStackTrace();
				}
				if(startIsSelected){
					startIsSelected = false;
					isShowStart = true;
				}
				if(destIsSelected){
					destIsSelected = false; 
					isShowDest = true;
				}
				frame.repaint();
			}
		});

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 1;
		prefMenu.add(horizontalStrut, gbc_horizontalStrut);

		JLabel lblOutside = new JLabel("Outside");
		GridBagConstraints gbc_lblOutside = new GridBagConstraints();
		gbc_lblOutside.gridwidth = 3;
		gbc_lblOutside.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutside.gridx = 1;
		gbc_lblOutside.gridy = 2;
		prefMenu.add(lblOutside, gbc_lblOutside);

		JLabel lblStairs = new JLabel("Stairs");
		GridBagConstraints gbc_lblStairs = new GridBagConstraints();
		gbc_lblStairs.gridwidth = 3;
		gbc_lblStairs.insets = new Insets(0, 0, 5, 5);
		gbc_lblStairs.gridx = 4;
		gbc_lblStairs.gridy = 2;
		prefMenu.add(lblStairs, gbc_lblStairs);

		JLabel lblWalkingSpeed = new JLabel("Walking Speed");
		GridBagConstraints gbc_lblWalkingSpeed = new GridBagConstraints();
		gbc_lblWalkingSpeed.insets = new Insets(0, 0, 5, 5);
		gbc_lblWalkingSpeed.gridx = 7;
		gbc_lblWalkingSpeed.gridy = 2;
		prefMenu.add(lblWalkingSpeed, gbc_lblWalkingSpeed);

		JSlider sliderOutside = new JSlider(JSlider.HORIZONTAL, -1, 1, 0);
		sliderOutside.setPaintLabels(true);
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.gridwidth = 3;
		gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider.insets = new Insets(0, 0, 5, 5);
		gbc_slider.gridx = 1;
		gbc_slider.gridy = 3;
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

		sliderOutside.setLabelTable(labels);
		sliderOutside.setPaintTicks(true);

		JSlider sliderStairs = new JSlider(JSlider.HORIZONTAL, -1, 1, 0);
		sliderStairs.setPaintLabels(true);
		GridBagConstraints gbc_slider_1 = new GridBagConstraints();
		gbc_slider_1.gridwidth = 3;
		gbc_slider_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider_1.insets = new Insets(0, 0, 5, 5);
		gbc_slider_1.gridx = 4;
		gbc_slider_1.gridy = 3;
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
		sliderStairs.setMajorTickSpacing(1);

		JSlider sliderWalkingSpeed = new JSlider(-1, 1, 0);
		sliderWalkingSpeed.setPaintLabels(true);
		GridBagConstraints gbc_walkingSpeed = new GridBagConstraints();
		gbc_walkingSpeed.fill = GridBagConstraints.HORIZONTAL;
		gbc_walkingSpeed.insets = new Insets(0, 0, 5, 5);
		gbc_walkingSpeed.gridx = 7;
		gbc_walkingSpeed.gridy = 3;
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
		sliderWalkingSpeed.setLabelTable(speeds);
		sliderWalkingSpeed.setPaintTicks(true);

		String[] themes = {"Standard", "Color Blind Mode"};

		JLabel lblVisualPreferences = new JLabel("Visual Theme:\r\n");
		GridBagConstraints gbc_lblVisualPreferences = new GridBagConstraints();
		gbc_lblVisualPreferences.anchor = GridBagConstraints.EAST;
		gbc_lblVisualPreferences.insets = new Insets(0, 0, 5, 5);
		gbc_lblVisualPreferences.gridx = 4;
		gbc_lblVisualPreferences.gridy = 4;
		prefMenu.add(lblVisualPreferences, gbc_lblVisualPreferences);

		JComboBox<String> dropdownTheme = new JComboBox(themes);
		GridBagConstraints gbc_dropdownTheme = new GridBagConstraints();
		gbc_dropdownTheme.gridwidth = 2;
		gbc_dropdownTheme.insets = new Insets(0, 0, 5, 5);
		gbc_dropdownTheme.fill = GridBagConstraints.HORIZONTAL;
		gbc_dropdownTheme.gridx = 5;
		gbc_dropdownTheme.gridy = 4;
		prefMenu.add(dropdownTheme, gbc_dropdownTheme);

		GradientButton btnCancel = new GradientButton("Cancel", buttonColor);
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.anchor = GridBagConstraints.EAST;
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 4;
		gbc_btnCancel.gridy = 6;
		prefMenu.add(btnCancel, gbc_btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sliderStairs.setValue(stairsIndex);
				sliderOutside.setValue(outsideIndex);
				sliderWalkingSpeed.setValue(walkSpeedIndex);
				dropdownTheme.setSelectedIndex(themeIndex);

				resetPath = false;
				// Show the route again if returning to nav view
				if (returnMenu.equals("Nav Menu")){
					showRoute = true;
				}
				// Return to view that preferences menu was accessed from
				menuLayout.show(menus, returnMenu);
				frame.repaint();
			}
		});

		GradientButton btnSavePreferences = new GradientButton("Save Preferences", buttonColor);
		btnSavePreferences.setText(" Save ");
		GridBagConstraints gbc_btnSavePreferences = new GridBagConstraints();
		gbc_btnSavePreferences.anchor = GridBagConstraints.WEST;
		gbc_btnSavePreferences.insets = new Insets(0, 0, 0, 5);
		gbc_btnSavePreferences.gridx = 6;
		gbc_btnSavePreferences.gridy = 6;
		prefMenu.add(btnSavePreferences, gbc_btnSavePreferences);
		// Return to previous view
		btnSavePreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set indices
				stairsIndex = sliderStairs.getValue();
				outsideIndex = sliderOutside.getValue();
				walkSpeedIndex = sliderWalkingSpeed.getValue();
				themeIndex = dropdownTheme.getSelectedIndex();

				// Set color according to selection
				if (((String)dropdownTheme.getSelectedItem()).equals("Standard")){
					// Switch to standard colors
					previousColor = new Color(255, 75, 75);
					nextColor = new Color(51, 255, 51);
					pointColor = Color.ORANGE;
					starColor = new Color(255, 51, 255);
				}
				else if (((String)dropdownTheme.getSelectedItem()).equals("Color Blind Mode")){
					// Switch to color blind friendly colors
					previousColor = new Color(182, 109, 255);
					nextColor = new Color(0, 146, 146);
					pointColor = new Color(255, 255, 255);
					starColor = new Color(146, 0, 0);
				}

				// If the position in the route should be reset, does that
				if (resetPath){
					textPos = 0;
					mapPos = 0;
					resetPath = false;

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


					double estimatedDirDist = 0;
					for(int g = 0; g < multiMapFinalDir.size(); g++){
						for(int p = 0; p < multiMapFinalDir.get(g).size(); p++){
							estimatedDirDist+=multiMapFinalDir.get(g).get(p).getDistance();
						}
					}

					timeEst = (int) (estimatedDirDist / walkSpeed);
					int minEst = (int) Math.floor(timeEst / 60);
					int secEst = timeEst % 60;
					String secEstString = Integer.toString(secEst);
					if(secEstString.length() == 1){
						secEstString = "0" + secEstString;
					} else if (secEstString.length() == 0){
						secEstString = "00";
					}
					txtTimeToDestination.setText("Estimated Time to Destination: " + minEst + ":" + secEstString);
				}
				// Set button colors based on preferences selected
				btnPrevious.setColor(previousColor);
				btnNext.setColor(nextColor);
				// Show the route again if returning to nav view
				if (returnMenu.equals("Nav Menu")){
					showRoute = true;
				}
				// Return to view that preferences menu was accessed from
				menuLayout.show(menus, returnMenu);
				frame.repaint();
			}
		});
		return prefMenu;
	}

	public static void main(String[] args) throws IOException, AlreadyExistsException, SQLException{

		GUI gui = new GUI();

		if(guiThreadObject == null)
		{
			guiThreadObject= new Thread (gui, "GUI Thread");
			guiThreadObject.setPriority(4);
			guiThreadObject.start();


		}


		//added by JPG starts and plays the animation


		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());

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

		//loadingAnimation.hideSplash(0);


	}				


	public double getMaxXPoint(ArrayList<Point> pointList){
		int length = pointList.size();
		double maxX = pointList.get(0).getGlobX();
		for(int i = 0; i < length-1; i++){
			if(pointList.get(i).getGlobX() < pointList.get(i+1).getGlobX())
				maxX = pointList.get(i+1).getGlobX();
		}
		return maxX;
	}

	public double getMaxYPoint(ArrayList<Point> pointList){
		int length = pointList.size();
		double maxY = pointList.get(0).getGlobX();
		for(int i = 0; i < length-1; i++){
			if(pointList.get(i).getGlobY() < pointList.get(i+1).getGlobY())
				maxY = pointList.get(i+1).getGlobY();
		}
		return maxY;
	}

	public ArrayList<Point> getRoomPoints(ArrayList<Point> pointList){
		int length = pointList.size();
		ArrayList<Point> roomPoints = new ArrayList<Point>();
		String name = "";
		for(int i = 0; i < length; i++){
			name = pointList.get(i).getName();
			name = name.trim();
			name = name.toLowerCase();
			if(!(name.equals("stairs top")) && !(name.equals("stairs bottom")) && !(name.equals("room")) && !(name.equals("hallway")) && !(name.equals("elevator")))
				roomPoints.add(pointList.get(i));
		}
		return roomPoints;
	}

	class DrawRoute extends JPanel {

		public Shape createStar(int arms, int centerx, int centery, double rOuter, double rInner) {
			double angle = Math.PI / arms;

			GeneralPath path = new GeneralPath();

			for (int i = 0; i < 2 * arms; i++) {
				double r = (i & 1) == 0 ? rOuter : rInner;
				Point2D.Double p = new Point2D.Double(centerx + Math.cos(i * angle) * r,
						centery + Math.sin(i * angle) * r);
				if (i == 0)
					path.moveTo(p.getX(), p.getY());
				else
					path.lineTo(p.getX(), p.getY());
			}
			path.closePath();
			return path;
		}

		@Override
		public void paintComponent(Graphics g) {



			Graphics2D g2 = (Graphics2D) g;
			if (!(img == null)) {


				// Scale the image to the appropriate screen size

				if (drawnfirst == false) {
					windowScale = ((double) img.getWidth() / (double) drawPanel.getWidth());
					scaleSize = 1 / ((double) img.getWidth() / (double) drawPanel.getWidth());
					int WidthSize = (int) ((double) img.getHeight() / windowScale);
					if (WidthSize > (double) drawPanel.getHeight()) {
						windowScale = (double) img.getHeight() / (double) drawPanel.getHeight();
						scaleSize = 1 / ((double) img.getHeight() / (double) drawPanel.getHeight());
						// System.out.println("setting: "+scaleSize);
					}
					newImageWidth = (int) ((double) img.getWidth() / windowScale);
					newImageHeight = (int) ((double) img.getHeight() / windowScale);
					int centerx = (drawPanel.getWidth() / 2);
					int centery = (drawPanel.getHeight() / 2);
					drawnposx = centerx - (int) (newImageWidth / 2);
					drawnposy = centery - (int) (newImageHeight / 2);
					g.drawImage(img, (int)drawnposx, (int)drawnposy, (int) newImageWidth, (int) newImageHeight, null);
					// System.out.println(newImageWidth+", "+newImageHeight);
				} else {
					double deltax = 0;
					double deltay = 0;
					newImageHeight = (int) (img.getHeight() * scaleSize);
					newImageWidth = (int) (img.getWidth() * scaleSize);
					if (!mapTitle.equals("Select Map")){
						if (Dragged) {
							deltax = -(originx - mousex);
							deltay = -(originy - mousey);
							originx = mousex;
							originy = mousey;
							difWidth = 0;
							difHeight = 0;
						} else if (scrolled) {
							deltax = 0;
							deltay = 0;
							scrolled = false;
						}
						drawnposx += deltax;
						drawnposy += deltay;
						g.drawImage(img, (int)drawnposx, (int)drawnposy, (int) newImageWidth, (int) newImageHeight, null);
					}
				}
			}




			if (!showRoute && route == null) {
				//Draws the points in each room so that the user can select each point as a source or destination
				//System.out.println("\t\t\t\t\t\troomPointsToDraw size: " + roomPointsToDraw.size());
				if(roomPointsToDraw != null && !(roomPointsToDraw.isEmpty()) ){
					//System.out.println("\t\t\t12312312321roomPointsToDraw size: " + roomPointsToDraw.size());
					for (int i = 0; i < roomPointsToDraw.size(); i++){
						int point1x = (int)((roomPointsToDraw.get(i).getLocX()*newImageWidth)+drawnposx);
						int point1y = (int)((roomPointsToDraw.get(i).getLocY()*newImageHeight)+drawnposy);			
						g.setColor(pointColor);
						g.fillOval((int)(point1x - (pointSize/2)), (int)(point1y - (pointSize/2)), pointSize, pointSize);
						g.setColor(Color.BLACK);
						g.drawOval((int)(point1x - (pointSize/2)), (int)(point1y - (pointSize/2)), pointSize, pointSize);
					}
				}
				System.out.println("Found Something?");
				if(currentMap != null){
					if(currentMap.getMapName().equalsIgnoreCase("campus") && newClick == true){
						double campusX = (lastMouseX-drawnposx)/newImageWidth;
						double campusY = (lastMouseY-drawnposy)/newImageHeight;
						System.out.println("=================IN CAMPUS=================");
						System.out.println("Campus x is: " + campusX);
						System.out.println("Campus y is: " + campusY);
						for(int m = 0; m < maps.size(); m++){
							double x1 = maps.get(m).getxTopLeft();
							double y1 = maps.get(m).getyTopLeft();
							double x2 = x1 + maps.get(m).getWidth() * Math.cos(maps.get(m).getRotationAngle());
							double y2 = y1 + maps.get(m).getWidth() * Math.sin(maps.get(m).getRotationAngle());
							double x3 = maps.get(m).getxBotRight();
							double y3 = maps.get(m).getyBotRight();
							double x4 = x1 - maps.get(m).getHeight() * Math.cos(maps.get(m).getRotationAngle());
							double y4 = y1 - maps.get(m).getHeight() * Math.sin(maps.get(m).getRotationAngle());
							double centerX = (x1 + x3) / 2;
							double centerY = (y1 + y3) / 2;
							g.fillOval((int)((centerX / newImageWidth) - drawnposx - pointSize), (int)((centerY / newImageHeight) - drawnposy - pointSize), pointSize, pointSize);
							if(campusX > maps.get(m).getxTopLeft() + ((campusY - maps.get(m).getyTopLeft()) * Math.sin(maps.get(m).getRotationAngle()))&&
									campusX < maps.get(m).getxBotRight() + ((maps.get(m).getyBotRight() - campusY) * Math.sin(maps.get(m).getRotationAngle())) && 
									campusY > maps.get(m).getyTopLeft() - ((campusX - maps.get(m).getxTopLeft()) * Math.cos(maps.get(m).getRotationAngle())) && 
									campusY < maps.get(m).getyBotRight() - ((maps.get(m).getxBotRight()) * Math.cos(maps.get(m).getRotationAngle()))){
								//g.fillOval(campusX, campusY, 20, 20);
								System.out.println("Found that the click is contained by: " + maps.get(m).getMapName());
							}
						}
					}
				}
				for(int i = 0; i < roomPointsToDraw.size(); i++){
					//System.out.println("Room point " + i + " name is: " + roomPointsToDraw.get(i).getName());
					double posx = ((roomPointsToDraw.get(i).getLocX()*newImageWidth)+drawnposx);
					double posy = ((roomPointsToDraw.get(i).getLocY()*newImageHeight)+drawnposy);

					DEBUG = false;
					if(DEBUG){
						System.out.println(i + " - Point Name: " + roomPointsToDraw.get(i).getName());
						System.out.println("posx: " + posx);
						System.out.println("posy: " + posy);
						System.out.println("lastMouseX: " + lastMouseX);
						System.out.println("lastMouseY: " + lastMouseY);
						System.out.println("startIsSelected: " + startIsSelected);
						System.out.println("startPoint ID: " + startPoint.getName());
						if(startMap != null)
							System.out.println("Current Start Map Name: " + startMap.getMapId());
						System.out.println("------------------------------------");
						System.out.println("destPoint ID: " + destPoint.getName());
						if(selectedPointID != null)
							System.out.println("Selected Point ID: " + selectedPointID);
						System.out.println("destIsSelected: " + destIsSelected);
						if(destMap != null)
							System.out.println("Current Dest Map Name: " + destMap.getMapId());
						System.out.println("------------------------------------");
					}

					DEBUG = false;
					if ((lastMouseX > posx - (pointSize + (1*scaleSize))
							&& lastMouseX < posx + (pointSize + (1*scaleSize)))
							&& (lastMouseY > posy - (pointSize + (1*scaleSize))
									&& lastMouseY < posy + (pointSize + (1*scaleSize)))) {
						//If there is a new click action and a start point has not been selected then the start point is set
						if(DEBUG){
							System.out.println("Point Name: " + roomPointsToDraw.get(i).getName());
						}
						selectedPoint = roomPointsToDraw.get(i);
						selectedPointID = selectedPoint.getId();


						//System.out.println("newClick: " + newClick);
						//System.out.println("selectedPointID is null: " + (selectedPointID == null));
						if( !(selectedPointID == null) ){
							//System.out.println("selectedPointID: " + selectedPoint.getId());
						}

						if(currentMap == startMap && startMap != null){
							//System.out.println("Marker 1---------");
							//select the starting point
							if(!(startIsSelected)){
								//System.out.println("Marker 2---------");
								if (newClick == true && startPoint.getName().equals("Select Start Location")) {
									//if we select a new point to edit
									startPoint = selectedPoint;
									startPointIndex = i;
									//System.out.println("Set the startPoint to first point clicked");

									//set the drop down for points
									//startBuilds.setSelectedIndex(tempStartRoom.indexOf(roomPointsToDraw.get(i)));
									startBuilds.setSelectedItem(startPoint);

									//System.out.println("startPoint Name: " + startPoint.getName());
									//System.out.println("starBuilds selectedIndex: " + startBuilds.getSelectedIndex());
									//System.out.println("startPointIndex: " + startPointIndex);



									newClick = false;
									startIsSelected = true;
									selectedPoint = null;
									if(destPoint != destPointName){
										directionsButton.setEnabled(true);
									}
								}
							}

							//unselect the starting point
							if(startIsSelected){
								if(newClick == true && selectedPointID.equals(startPoint.getId()) && startIsSelected){
									startPoint = startPointName;
									startPointIndex = 0;
									startBuilds.setSelectedItem(startPointName);
									//System.out.println("Unselected the startPoint");
									newClick = false;
									startIsSelected = false;
									selectedPoint = null;
									directionsButton.setEnabled(false);
								}
							}
						}//end of currentMap = startMap

						if(currentMap == destMap){
							//select destination point
							if(!(destIsSelected)){
								if(newClick == true && destPoint.getName().equals("Select Destination Location") ){
									//System.out.println("Set the Selected Destination Point");
									destPoint = selectedPoint;
									destPointIndex = i;
									destBuilds.setSelectedItem(destPoint);
									//System.out.println("Unselected the destPoint");
									newClick = false;
									destIsSelected = true;
									selectedPoint = null;
									if(startPoint != startPointName){
										directionsButton.setEnabled(true);
									}
								}
							}

							//unselect the destination point
							if(destIsSelected){
								if(newClick == true && destPoint.getId().equals(destPointName.getId()) && destIsSelected){
									destPoint = destPointName;
									destPointIndex = 0;
									destBuilds.setSelectedItem(destPointName);
									//System.out.println("Unselected the destPoint");
									newClick = false;
									destIsSelected = false;
									selectedPoint = null;
									directionsButton.setEnabled(false);
									directionsButton.setEnabled(false);
								}
							}
						}//end of currentMap == destMap
					}
					mainMenu.repaint();
				}





				if(startMap != null && currentMap != null && startPoint != startPointName && startPoint.getMapId() == currentMap.getMapId()){
					System.out.println("In startMap");
					//System.out.println("startPointID: " + startPoint.getId());
					//System.out.println("startPoint name: " + startPoint.getName());
					//System.out.println("currentMapID: " + currentMap.getMapId());
					//System.out.println("startMapID: " + startMap.getMapId());

					if(startPoint.getId() != null && !(startPoint.getName().equals(startPointName.getName())) && startIsSelected){
						int point1x = (int)((startPoint.getLocX()*newImageWidth)+drawnposx);
						int point1y = (int)((startPoint.getLocY()*newImageHeight)+drawnposy);			
						g.setColor(Color.GREEN);
						g.fillOval((int)(point1x - (pointSize/2)), (int)(point1y - (pointSize/2)), pointSize, pointSize);
						g.setColor(Color.BLACK);
						g.drawOval((int)(point1x - (pointSize/2)), (int)(point1y - (pointSize/2)), pointSize, pointSize);
					}
				}


				if(destMap != null && currentMap != null && destPoint != destPointName && destPoint.getMapId() == currentMap.getMapId()){
					System.out.println("In destMap");

					if(destPoint.getId() != null && !(destPoint.getName().equals(destPointName.getName())) && destIsSelected){
						int point1x = (int)((destPoint.getLocX()*newImageWidth)+drawnposx);
						int point1y = (int)((destPoint.getLocY()*newImageHeight)+drawnposy);			
						g.setColor(Color.RED);
						g.fillOval((int)(point1x - (pointSize/2)), (int)(point1y - (pointSize/2)), pointSize, pointSize);
						g.setColor(Color.BLACK);
						g.drawOval((int)(point1x - (pointSize/2)), (int)(point1y - (pointSize/2)), pointSize, pointSize);
					}
				}
			}


			if(destMap != null && currentMap != null && currentMap.getMapId() == destMap.getMapId() && showDestPoint){
				if(destPoint.getId() != null && !(destPoint.getName().equals(destPointName.getName())) && currentMap.getMapId() == destPoint.getMapId()){
					int point1x = (int)((destPoint.getLocX()*newImageWidth)+drawnposx);
					int point1y = (int)((destPoint.getLocY()*newImageHeight)+drawnposy);			
					g.setColor(Color.RED);
					g.fillOval((int)(point1x - (pointSize/2)), (int)(point1y - (pointSize/2)), pointSize, pointSize);
					g.setColor(Color.BLACK);
					g.drawOval((int)(point1x - (pointSize/2)), (int)(point1y - (pointSize/2)), pointSize, pointSize);
				}
			}


			if (showStartPoint && !(startPoint.getName().equals("Select Start Location"))){
				Shape startStar = createStar(5, (int)((startStarX * newImageWidth) + drawnposx) , (int)((startStarY * newImageHeight) + drawnposy), 7, 12);
				g.setColor(starColor);
				g2.fill(startStar);
				g.setColor(Color.BLACK);
				g2.draw(startStar);
			}
			if (showDestPoint && !(destPoint.getName().equals("Select Destination Location"))){
				Shape destStar = createStar(5, (int)((destStarX * newImageWidth) + drawnposx), (int)((destStarY * newImageHeight) + drawnposy), 7, 12);
				g.setColor(starColor);
				g2.fill(destStar);
				g.setColor(Color.BLACK);
				g2.draw(destStar);
			}

			if (showRoute && route != null) {
				// Draw multi colored lines depending on current step in
				// directions and color settings (color blind mode or not)
				// Draw lines for all points up to current point, use
				// previousColor (same color as "Previous" button)
				g.setColor(new Color(previousColor.getRed(), previousColor.getGreen(), previousColor.getBlue(), 150));
				g2.setStroke(new BasicStroke(8));
				for (int i = 0; i < textPos; i++) {
					int point1x = (int) ((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX() * newImageWidth)
							+ drawnposx);
					int point1y = (int) ((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY() * newImageHeight)
							+ drawnposy);
					int point2x = (int) ((multiMapFinalDir.get(mapPos).get(i).getDestination().getLocX()
							* newImageWidth) + drawnposx);
					int point2y = (int) ((multiMapFinalDir.get(mapPos).get(i).getDestination().getLocY()
							* newImageHeight) + drawnposy);
					g2.drawLine(point1x, point1y, point2x, point2y);
				}
				// Draw a thicker line for the current step in the directions,
				// use currentColor
				if (DEBUG)
					System.out.println("mapPos: " + mapPos);
				if (textPos != multiMapFinalDir.get(mapPos).size()) { // ||
					// (mapPos
					// ==
					// multiMapFinalDir.size()-1
					// &&
					// multiMapFinalDir.get(mapPos).size()-1
					// ==
					// textPos)){
					g2.setStroke(new BasicStroke(13));
					g.setColor(currentColor);
					int point1x = (int) ((multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocX()
							* newImageWidth) + drawnposx);
					int point1y = (int) ((multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocY()
							* newImageHeight) + drawnposy);
					int point2x = (int) ((multiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocX()
							* newImageWidth) + drawnposx);
					int point2y = (int) ((multiMapFinalDir.get(mapPos).get(textPos).getDestination().getLocY()
							* newImageHeight) + drawnposy);
					g2.drawLine(point1x, point1y, point2x, point2y);
				}


				g2.setStroke(new BasicStroke(8));
				g.setColor(nextColor);
				for (int i = textPos + 1; i < multiMapFinalDir.get(mapPos).size(); i++) {
					int point1x1 = (int) ((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX() * newImageWidth)
							+ drawnposx);
					int point1y1 = (int) ((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY() * newImageHeight)
							+ drawnposy);
					int point2x1 = (int) ((multiMapFinalDir.get(mapPos).get(i).getDestination().getLocX()
							* newImageWidth) + drawnposx);
					int point2y1 = (int) ((multiMapFinalDir.get(mapPos).get(i).getDestination().getLocY()
							* newImageHeight) + drawnposy);
					g2.drawLine(point1x1, point1y1, point2x1, point2y1);
				}

				// Draws ovals with black borders at each of the points along
				// the path, needs to use an offset
				g2.setStroke(new BasicStroke(2));
				for (int i = 0; i < multiMapFinalDir.get(mapPos).size(); i++) {

					int point1x = (int) ((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocX() * newImageWidth)
							+ drawnposx);
					int point1y = (int) ((multiMapFinalDir.get(mapPos).get(i).getOrigin().getLocY() * newImageHeight)
							+ drawnposy);
					if (i != textPos) {
						g.setColor(pointColor);
						g.fillOval((int) (point1x - (pointSize / 2)), (int) (point1y - (pointSize / 2)), pointSize,
								pointSize);
						g.setColor(Color.BLACK);
						g.drawOval((int) (point1x - (pointSize / 2)), (int) (point1y - (pointSize / 2)), pointSize,
								pointSize);
					} 
				}


				// Draws final oval or star in path
				int pointx = (int) ((multiMapFinalDir.get(mapPos).get(multiMapFinalDir.get(mapPos).size() - 1)
						.getDestination().getLocX() * newImageWidth) + drawnposx);
				int pointy = (int) ((multiMapFinalDir.get(mapPos).get(multiMapFinalDir.get(mapPos).size() - 1)
						.getDestination().getLocY() * newImageHeight) + drawnposy);
				if (textPos != multiMapFinalDir.get(mapPos).size()) {
					g.setColor(pointColor);
					g.fillOval(pointx - (pointSize / 2), pointy - (pointSize / 2), pointSize, pointSize);
					g.setColor(Color.BLACK);
					g.drawOval(pointx - (pointSize / 2), pointy - (pointSize / 2), pointSize, pointSize);
				} else {
					int point1x1 = (int) ((multiMapFinalDir.get(mapPos).get(textPos - 1).getDestination().getLocX()
							* newImageWidth) + drawnposx);
					int point1y1 = (int) ((multiMapFinalDir.get(mapPos).get(textPos - 1).getDestination().getLocY()
							* newImageHeight) + drawnposy);
					Shape star = createStar(5, point1x1, point1y1, 7, 12);
					g.setColor(starColor);
					g2.fill(star);
					g.setColor(Color.BLACK);
					g2.draw(star);
				}

				// Draw star after so its drawn over points
				if (textPos != multiMapFinalDir.get(mapPos).size()){
					int point1x1 = (int) ((multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocX()
							* newImageWidth) + drawnposx);
					int point1y1 = (int) ((multiMapFinalDir.get(mapPos).get(textPos).getOrigin().getLocY()
							* newImageHeight) + drawnposy);
					// Prints a star indicating where the user currently is
					Shape star = createStar(5, point1x1, point1y1, 7, 12);
					g.setColor(starColor);
					g2.fill(star);
					g.setColor(Color.BLACK);
					g2.draw(star);
				}
			}
		}


	}


	//runs the startup and the object for the GUI class in its' own thread.
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//added by JPG starts and plays the animation
		loadingAnimation = new SplashPage("GuiSplashThread");
		loadingAnimation.showSplash();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}


		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

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

		try {
			this.createAndShowGUI();
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


		loadingAnimation.hideSplash(0);
	}
}
