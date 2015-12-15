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
import javax.swing.text.JTextComponent;

import database.AlreadyExistsException;
import database.ServerDB;
import javax.swing.border.TitledBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

public class GUI{
	private boolean DEBUG = false;
	private ServerDB md = ServerDB.getInstance();

	private BufferedImage img = null;
	private BufferedImage tempImg = null;

	// Array of strings to initally populate dropdown menus with
	private String rooms[] = {"Select room #", "Please choose building first"};

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
	private JLabel directionsText;
	private JPanel mainMenu;
	private JPanel navMenu;
	private JPanel prefMenu;
	private JPanel menus;
	private CardLayout menuLayout;
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
	private Color starColor = new Color(255, 51, 255);
	private ArrayList<Point> pointArray;
	private ArrayList<Edge> edgeArray;
	private JFrame frame = new JFrame("Directions with Magnitude");
	private JComboBox<String> startMapsDropDown = new JComboBox();
	private JComboBox<Point> destBuilds = new JComboBox();
	private JComboBox<Point> startBuilds = new JComboBox();
	private JComboBox destMapsDropDown = new JComboBox();
	private int pointSize = 16;
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
	private String mapTitle = "Select Map";
	private static SplashPage loadingAnimation;
	private JTextField txtFieldEmail;
	private JLabel txtTimeToDestination;
	private boolean resetPath = false;
	private GradientButton btnSwapStartAndDest;
	private GradientButton directionsButton;
	private JPanel panelDirections;
	private double mousezoomx;
	private double mousezoomy;
	private double minZoomSize;
	private JTextArea txtpnFullTextDir;
	protected JTextField txtSearchStart;
	protected JTextField txtSearchDest;
	private GradientButton btnFullTextDirections;
	private double startStarX;
	private double startStarY;
	private double destStarX;
	private double destStarY;
	


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


		mainMenu = new JPanel();
		mainMenu.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainMenu.setBackground(backgroundColor);

		GridBagLayout gbl_mainMenu = new GridBagLayout();
		gbl_mainMenu.columnWidths = new int[]{80, 90, 131, 44, 150, 90, 131, 44, 80};
		gbl_mainMenu.rowHeights = new int[]{10, 18, 27, 0, 0, 0, 0, 0, 0};
		gbl_mainMenu.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_mainMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		mainMenu.setLayout(gbl_mainMenu);

		navMenu = new JPanel();
		navMenu.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		navMenu.setBackground(backgroundColor);

		GridBagLayout gbl_navMenu = new GridBagLayout();
		gbl_navMenu.columnWidths = new int[]{30, 215, 290, 215, 30, 0};
		gbl_navMenu.rowHeights = new int[]{15, 19, 0, 0, 31, 30, 7, 0};
		gbl_navMenu.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_navMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		navMenu.setLayout(gbl_navMenu);

		JPanel aboutMenu = new JPanel();
		aboutMenu.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		aboutMenu.setBackground(backgroundColor);

		GridBagLayout gbl_aboutMenu = new GridBagLayout();
		gbl_aboutMenu.columnWidths = new int[]{30, 280, 50, 280, 0};
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
				frame.repaint();
			}
		});

		Hashtable<Integer, JLabel> speeds = new Hashtable<Integer, JLabel>();
		speeds.put(0, new JLabel("Medium"));
		speeds.put(-1, new JLabel("Slow"));
		speeds.put(1, new JLabel("Fast"));

		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(0, new JLabel("Neutral"));
		labels.put(-1, new JLabel("Avoid"));
		labels.put(1, new JLabel("Priority"));

		destMapsDropDown.addItem("Select Map");
		startMapsDropDown.addItem("Select Map");

		JSlider sliderOutside = new JSlider(JSlider.HORIZONTAL, -1, 1, 0);
		sliderOutside.setPaintLabels(true);
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.gridwidth = 2;
		gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider.insets = new Insets(0, 0, 5, 5);
		gbc_slider.gridx = 1;
		gbc_slider.gridy = 4;
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
		gbc_slider_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider_1.insets = new Insets(0, 0, 5, 5);
		gbc_slider_1.gridx = 3;
		gbc_slider_1.gridy = 4;
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
		gbc_walkingSpeed.gridx = 4;
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
		sliderWalkingSpeed.setLabelTable(speeds);
		sliderWalkingSpeed.setPaintTicks(true);

		GradientButton btnSavePreferences = new GradientButton("Back", buttonColor);
		GridBagConstraints gbc_btnSavePreferences = new GridBagConstraints();
		gbc_btnSavePreferences.gridwidth = 8;
		gbc_btnSavePreferences.insets = new Insets(0, 0, 5, 0);
		gbc_btnSavePreferences.gridx = 0;
		gbc_btnSavePreferences.gridy = 5;
		btnSavePreferences.setFont(new Font("Serif", Font.BOLD, 15));
		prefMenu.add(btnSavePreferences, gbc_btnSavePreferences);
		// Return to previous view
		btnSavePreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

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


		frame.getContentPane().add(menus, BorderLayout.NORTH);


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
				System.out.println("toAdd: " + toAdd);
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
		gbc_btnOptionsMain.anchor = GridBagConstraints.SOUTH;
		gbc_btnOptionsMain.insets = new Insets(0, 0, 5, 5);
		gbc_btnOptionsMain.gridx = 6;
		gbc_btnOptionsMain.gridy = 1;
		mainMenu.add(btnOptionsMain, gbc_btnOptionsMain);
		btnOptionsMain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Note which menu to return to
				returnMenu = "Main Menu";
				// Show preferences menu
				menuLayout.show(menus, "Pref Menu");
			}
		});

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut.gridx = 8;
		gbc_horizontalStrut.gridy = 1;
		mainMenu.add(horizontalStrut, gbc_horizontalStrut);

		drawPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				originx = e.getX();
				originy = e.getY();
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
				//System.out.println("dragged");
				Dragged = true;
				mousex = g.getX();
				mousey = g.getY();
				frame.repaint();
			}

			public void mouseMoved(MouseEvent j) {
				//System.out.println((j.getX()-(drawnposx+newImageWidth/2))+", "+(j.getY()-(drawnposy+newImageHeight/2)));
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
					System.out.println(minZoomSize);
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

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_1.gridx = 0;
		gbc_horizontalStrut_1.gridy = 2;
		mainMenu.add(horizontalStrut_1, gbc_horizontalStrut_1);

		JLabel lblStart = new JLabel("Start");
		GridBagConstraints gbc_lblStart = new GridBagConstraints();
		gbc_lblStart.gridwidth = 2;
		gbc_lblStart.insets = new Insets(0, 0, 5, 5);
		gbc_lblStart.gridx = 1;
		gbc_lblStart.gridy = 2;
		mainMenu.add(lblStart, gbc_lblStart);

		GradientButton btnClearStart = new GradientButton("X", Color.RED);
		btnClearStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
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
		GridBagConstraints gbc_btnClearStart = new GridBagConstraints();
		gbc_btnClearStart.insets = new Insets(0, 0, 5, 5);
		gbc_btnClearStart.gridx = 3;
		gbc_btnClearStart.gridy = 2;
		mainMenu.add(btnClearStart, gbc_btnClearStart);

		JLabel lblDestination_1 = new JLabel("Destination");
		GridBagConstraints gbc_lblDestination_1 = new GridBagConstraints();
		gbc_lblDestination_1.gridwidth = 2;
		gbc_lblDestination_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestination_1.gridx = 5;
		gbc_lblDestination_1.gridy = 2;
		mainMenu.add(lblDestination_1, gbc_lblDestination_1);

		GradientButton btnClearDest = new GradientButton("X", Color.RED);
		GridBagConstraints gbc_btnClearDest = new GridBagConstraints();
		gbc_btnClearDest.insets = new Insets(0, 0, 5, 5);
		gbc_btnClearDest.gridx = 7;
		gbc_btnClearDest.gridy = 2;
		mainMenu.add(btnClearDest, gbc_btnClearDest);
		btnClearDest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
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


		txtSearchStart = new JTextField();
		txtSearchStart.setText("Search");
		GridBagConstraints gbc_txtSearchStart = new GridBagConstraints();
		gbc_txtSearchStart.gridwidth = 3;
		gbc_txtSearchStart.insets = new Insets(0, 0, 5, 5);
		gbc_txtSearchStart.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSearchStart.gridx = 1;
		gbc_txtSearchStart.gridy = 3;
		mainMenu.add(txtSearchStart, gbc_txtSearchStart);
		txtSearchStart.setColumns(10);

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
		destMapsDropDown.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				drawnfirst = false;
				if (destMapsDropDown.getSelectedItem().equals("Select Map")){
					destBuilds.removeAllItems();
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
					frame.repaint();
				}
				else{

					destBuilds.setEnabled(true);
					if (startBuilds.isEnabled()){
						btnSwapStartAndDest.setEnabled(true);
						directionsButton.setEnabled(true);
					}

					buildDestIndex = destMapsDropDown.getSelectedIndex();



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
						System.out.println("pointArraysize: " + pointArray.size());
						boolean check = true;
						//System.out.println("building size: " + buildings.length);
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
		gbc_destMaps.gridwidth = 2;
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
				if (startBuilds.getItemCount() != 0){
					showStartPoint = true;
					showDestPoint = false;
					startStarX = ((Point)(startBuilds.getSelectedItem())).getLocX();
					startStarY = ((Point)(startBuilds.getSelectedItem())).getLocY();
					frame.repaint();
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
		gbc_destBuilds.gridwidth = 2;
		gbc_destBuilds.fill = GridBagConstraints.BOTH;
		gbc_destBuilds.insets = new Insets(0, 0, 5, 5);
		gbc_destBuilds.gridx = 6;
		gbc_destBuilds.gridy = 5;
		destBuilds.setEnabled(false);
		mainMenu.add(destBuilds, gbc_destBuilds);
		destBuilds.setBounds(122, 30, 148, 20);
		destBuilds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (destBuilds.getItemCount() != 0){
					showDestPoint = true;
					showStartPoint = false;
					destStarX = ((Point)(destBuilds.getSelectedItem())).getLocX();
					destStarY = ((Point)(destBuilds.getSelectedItem())).getLocY();
					frame.repaint();
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
			}
		});

		drawPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				originx = e.getX();
				originy = e.getY();
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
					minZoomSize = 1 / ((double) img.getWidth() / (double) drawPanel.getWidth());
					int WidthSize = (int) ((double) img.getHeight() * minZoomSize);
					if (WidthSize > (double) drawPanel.getHeight()) {
						minZoomSize = 1 / ((double) img.getHeight() / (double) drawPanel.getHeight());
					}
					System.out.println(minZoomSize);
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
				menuLayout.show(menus, "Main Menu");
				showRoute = false;
				panelDirections.setVisible(false);
				btnFullTextDirections.setText("Send Email");
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
		directionsText = new JLabel(" ");
		directionsText.setHorizontalAlignment(JTextField.CENTER);
		directionsText.setToolTipText("");
		directionsText.setBounds(6, 174, 438, 30);

		//directionsText.setColumns(1);
		directionsText.setFont(new Font("Serif", Font.BOLD, 20));
		GridBagConstraints gbc_directionsText = new GridBagConstraints();
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
						while((multiMapFinalDir.get(mapPos).size() != 0) && (multiMapFinalDir.get(mapPos).size() == 0)){
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
						int m = mapPos - 1;
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
							directionsText.setText("Enter " + dirMaps.get(m).getMapName());
						}
					} else {

						textPos = multiMapFinalDir.get(mapPos).size();
						//directionsText.setText(textDir.get(mapPos).get(textPos));

						directionsText.setText("Enter " + dirMaps.get(mapPos + 1).getMapName());
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


		// Add panel for drawing
		frame.getContentPane().add(drawPanel);

		panelDirections = new JPanel();
		panelDirections.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		frame.getContentPane().add(panelDirections, BorderLayout.WEST);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 110, 110, 0, 0};
		gbl_panel.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0};
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
		
				GradientButton btnEmailDirections = new GradientButton("E-Mail Directions", buttonColor);
				GridBagConstraints gbc_btnEmailDirections = new GridBagConstraints();
				gbc_btnEmailDirections.insets = new Insets(0, 0, 5, 5);
				gbc_btnEmailDirections.gridwidth = 2;
				gbc_btnEmailDirections.anchor = GridBagConstraints.NORTH;
				gbc_btnEmailDirections.gridx = 1;
				gbc_btnEmailDirections.gridy = 11;
				panelDirections.add(btnEmailDirections, gbc_btnEmailDirections);
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

		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_4 = new GridBagConstraints();
		gbc_horizontalStrut_4.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_4.gridx = 0;
		gbc_horizontalStrut_4.gridy = 12;
		panelDirections.add(horizontalStrut_4, gbc_horizontalStrut_4);

		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_5 = new GridBagConstraints();
		gbc_horizontalStrut_5.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_5.gridx = 3;
		gbc_horizontalStrut_5.gridy = 12;
		panelDirections.add(horizontalStrut_5, gbc_horizontalStrut_5);

		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 1;
		gbc_verticalStrut.gridy = 14;
		panelDirections.add(verticalStrut, gbc_verticalStrut);

		// Make frame visible after initializing everything
		frame.setVisible(true);
		loadingAnimation.hideSplash(0);
	}

	public JPanel createPrefMenu(){
		prefMenu = new JPanel();
		prefMenu.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		prefMenu.setBackground(backgroundColor);
		GridBagLayout gbl_prefMenu = new GridBagLayout();
		gbl_prefMenu.columnWidths = new int[]{40, 100, 100, 200, 200, 0, 136, 40, 0};
		gbl_prefMenu.rowHeights = new int[]{0, 0, 10, 0, 32, 12, 11, 0};
		gbl_prefMenu.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_prefMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		prefMenu.setLayout(gbl_prefMenu);

		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 3;
		gbc_verticalStrut.gridy = 0;
		prefMenu.add(verticalStrut, gbc_verticalStrut);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 1;
		prefMenu.add(horizontalStrut, gbc_horizontalStrut);

		GradientButton btnHelp = new GradientButton("Help", buttonColor);
		GridBagConstraints gbc_btnHelp = new GridBagConstraints();
		gbc_btnHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnHelp.gridx = 1;
		gbc_btnHelp.gridy = 1;
		prefMenu.add(btnHelp, gbc_btnHelp);

		GradientButton btnAbout = new GradientButton("About", buttonColor);
		GridBagConstraints gbc_btnAbout = new GridBagConstraints();
		gbc_btnAbout.insets = new Insets(0, 0, 5, 5);
		gbc_btnAbout.gridx = 2;
		gbc_btnAbout.gridy = 1;
		prefMenu.add(btnAbout, gbc_btnAbout);
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuLayout.show(menus, "About Menu");
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

		JLabel lblVisualPreferences = new JLabel("Theme\r\n");
		GridBagConstraints gbc_lblVisualPreferences = new GridBagConstraints();
		gbc_lblVisualPreferences.insets = new Insets(0, 0, 5, 5);
		gbc_lblVisualPreferences.gridx = 6;
		gbc_lblVisualPreferences.gridy = 1;
		prefMenu.add(lblVisualPreferences, gbc_lblVisualPreferences);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_1.gridx = 7;
		gbc_horizontalStrut_1.gridy = 1;
		prefMenu.add(horizontalStrut_1, gbc_horizontalStrut_1);

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_2 = new GridBagConstraints();
		gbc_horizontalStrut_2.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_2.gridx = 5;
		gbc_horizontalStrut_2.gridy = 2;
		prefMenu.add(horizontalStrut_2, gbc_horizontalStrut_2);


		JRadioButton rdbtnStandard = new JRadioButton("Standard");
		GridBagConstraints gbc_rdbtnStandard = new GridBagConstraints();
		gbc_rdbtnStandard.fill = GridBagConstraints.HORIZONTAL;
		gbc_rdbtnStandard.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnStandard.gridx = 6;
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
				starColor = new Color(255, 51, 255);
			}
		});

		JRadioButton rdbtnColorBlindMode = new JRadioButton("Color Blind Mode");
		GridBagConstraints gbc_rdbtnColorBlindMode = new GridBagConstraints();
		gbc_rdbtnColorBlindMode.fill = GridBagConstraints.HORIZONTAL;
		gbc_rdbtnColorBlindMode.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnColorBlindMode.gridx = 6;
		gbc_rdbtnColorBlindMode.gridy = 3;

		// Add action listener to swap color palette, needs to be set after buttons are initialized
		rdbtnColorBlindMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Switch to color blind friendly colors
				previousColor = new Color(182, 109, 255);
				nextColor = new Color(0, 146, 146);
				pointColor = new Color(255, 255, 255);
				starColor = new Color(146, 0, 0);
			}
		});

		ButtonGroup visualPreferences = new ButtonGroup();

		JLabel lblOutside = new JLabel("Outside");
		GridBagConstraints gbc_lblOutside = new GridBagConstraints();
		gbc_lblOutside.gridwidth = 2;
		gbc_lblOutside.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutside.gridx = 1;
		gbc_lblOutside.gridy = 3;
		prefMenu.add(lblOutside, gbc_lblOutside);

		JLabel lblStairs = new JLabel("Stairs");
		GridBagConstraints gbc_lblStairs = new GridBagConstraints();
		gbc_lblStairs.insets = new Insets(0, 0, 5, 5);
		gbc_lblStairs.gridx = 3;
		gbc_lblStairs.gridy = 3;
		prefMenu.add(lblStairs, gbc_lblStairs);

		JLabel lblWalkingSpeed = new JLabel("Walking Speed");
		GridBagConstraints gbc_lblWalkingSpeed = new GridBagConstraints();
		gbc_lblWalkingSpeed.insets = new Insets(0, 0, 5, 5);
		gbc_lblWalkingSpeed.gridx = 4;
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
		loadingAnimation = new SplashPage("GuiSplashThread");
		loadingAnimation.run();
		try {
			Thread.sleep(50);
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

			if (showStartPoint){
				Shape startStar = createStar(5, (int)((startStarX * newImageWidth) + drawnposx) , (int)((startStarY * newImageHeight) + drawnposy), 7, 12);
				g.setColor(starColor);
				g2.fill(startStar);
				g.setColor(Color.BLACK);
				g2.draw(startStar);
			}
			if (showDestPoint){
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
	
}
