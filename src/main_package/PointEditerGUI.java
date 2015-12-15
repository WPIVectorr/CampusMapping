package main_package;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import database.AlreadyExistsException;
import database.DoesNotExistException;
import database.PopulateErrorException;
import database.ServerDB;


public class PointEditerGUI extends JFrame {

	private interButtonPanel buttonPanel = new interButtonPanel();
	private MapPanel mapFrame= new MapPanel();
	private JComboBox<String> mapDropDown;
	private JTextField destDropDown;
	private static BufferedImage img = null;
	private int windowSizeX = 0;
	private int windowSizeY = 0;
	private double windowScale = 0;
	private static int lastMousex,lastMousey;
	private static int pointSize = 7;
	private ArrayList<Point> pointArray = new ArrayList<Point>();
	private static ArrayList<Map> maps = new ArrayList<Map>();
	private ArrayList<Edge> edgeArray = new ArrayList<Edge>();
	private Point srcPoint;
	private JButton btnConfirmSelection;

	private Point currentPoint;
	String name = "Select Map";
	File destinationFile;
	File logo;

	private Map connectMap = null;
	private Map srcMap = null;
	private ServerDB md = ServerDB.getInstance();
	
	private double imagewidth;
	private double imageheight;
	private double scaleSize = 1;
	private int drawnposx;
	private int drawnposy;
	private boolean drawnfirst = false;
	private int screenHeight;
	private int screenWidth;
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
	private ArrayList<Map> emptyMaps;
	private Map currentMap;
	private int mousezoomx;
	private int mousezoomy;
	private double minZoomSize;
	
//=============================set grid size==============================================
	
	private double gridSize = 0.005;
	

//=============================set grid size==============================================

	private static JFrame frame = new JFrame("Add Edges Between Maps");


	public PointEditerGUI() {
		buttonPanel = new interButtonPanel();
		mapFrame= new MapPanel();
		img = null;
		windowSizeX = 0;
		windowSizeY = 0;
		windowScale = 0;
		pointSize = 7;
		pointArray = new ArrayList<Point>();
		maps = new ArrayList<Map>();
		edgeArray = new ArrayList<Edge>();
		name = "Select Map";
		connectMap = null;
		srcMap = null;
		md = ServerDB.getInstance();
		scaleSize = 1;
		drawnfirst = false;
		atMaxZoom = false;
		atMinZoom = false;
		Dragged = false;
		scrolled = false;
		//maps = ServerDB.getMapsFromLocal();
		emptyMaps = ServerDB.getEmptyMapsFromServer();
		


		try {
			// Set to cross-platform Java Look and Feel (also called "Metal")
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub


		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		int framex = 932;
		int framey = 778;
		setSize(framex, framey);
		frame.setResizable(false);
		int locationx = screenWidth / 2;
		int locationy = screenHeight / 2;
		setLocation(locationx-(framex/2), locationy-(framey/2));
		setVisible(true);
		mapFrame.setBackground(Color.WHITE);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));


		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		GridBagLayout gbl_buttonPanel = new GridBagLayout();
		gbl_buttonPanel.columnWidths = new int[]{16, 99, 204, 53, 85, 277, 0, 0, 0};
		gbl_buttonPanel.rowHeights = new int[]{26, 0, 0, 0};
		gbl_buttonPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_buttonPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		buttonPanel.setLayout(gbl_buttonPanel);

		

		mapDropDown = new JComboBox<String>();
		GridBagConstraints gbc_mapDropDown = new GridBagConstraints();
		gbc_mapDropDown.insets = new Insets(0, 0, 5, 5);
		gbc_mapDropDown.fill = GridBagConstraints.HORIZONTAL;
		gbc_mapDropDown.gridx = 0;
		gbc_mapDropDown.gridy = 0;
		buttonPanel.add(mapDropDown, gbc_mapDropDown);

		btnConfirmSelection = new JButton("Save Point Locations");
		GridBagConstraints gbc_btnConfirmSelection = new GridBagConstraints();
		gbc_btnConfirmSelection.insets = new Insets(0, 0, 5, 5);
		gbc_btnConfirmSelection.gridx = 0;
		gbc_btnConfirmSelection.gridy = 2;
		buttonPanel.add(btnConfirmSelection, gbc_btnConfirmSelection);
		btnConfirmSelection.setEnabled(false);
		
		JLabel lblchangex = new JLabel("Change Point X");
		GridBagConstraints gbc_lblchangex = new GridBagConstraints();
		gbc_lblchangex.gridwidth = 1;
		gbc_lblchangex.fill = GridBagConstraints.VERTICAL;
		gbc_lblchangex.insets = new Insets(0, 0, 5, 5);
		gbc_lblchangex.gridx = 1;
		gbc_lblchangex.gridy = 0;
		buttonPanel.add(lblchangex, gbc_lblchangex);
		
		JButton changexneg = new JButton("-X");
		GridBagConstraints gbc_changexneg = new GridBagConstraints();
		gbc_changexneg.insets = new Insets(0, 0, 5, 5);
		gbc_changexneg.gridx = 2;
		gbc_changexneg.gridy = 0;
		buttonPanel.add(changexneg, gbc_changexneg);
		btnConfirmSelection.setEnabled(true);
		
		JButton changexpos = new JButton("+X");
		GridBagConstraints gbc_changexpos = new GridBagConstraints();
		gbc_changexpos.insets = new Insets(0, 0, 5, 5);
		gbc_changexpos.gridx = 3;
		gbc_changexpos.gridy = 0;
		buttonPanel.add(changexpos, gbc_changexpos);
		btnConfirmSelection.setEnabled(true);
		
		JLabel lblchangey = new JLabel("Change Point Y");
		GridBagConstraints gbc_lblchangey = new GridBagConstraints();
		gbc_lblchangey.gridwidth = 1;
		gbc_lblchangey.fill = GridBagConstraints.VERTICAL;
		gbc_lblchangey.insets = new Insets(0, 0, 5, 5);
		gbc_lblchangey.gridx = 1;
		gbc_lblchangey.gridy = 2;
		buttonPanel.add(lblchangey, gbc_lblchangey);
		
		JButton changeyneg = new JButton("-Y");
		GridBagConstraints gbc_changeyneg = new GridBagConstraints();
		gbc_changeyneg.insets = new Insets(0, 0, 5, 5);
		gbc_changeyneg.gridx = 2;
		gbc_changeyneg.gridy = 2;
		buttonPanel.add(changeyneg, gbc_changeyneg);
		btnConfirmSelection.setEnabled(true);
		
		JButton changeypos = new JButton("+Y");
		GridBagConstraints gbc_changeypos = new GridBagConstraints();
		gbc_changeypos.insets = new Insets(0, 0, 5, 5);
		gbc_changeypos.gridx = 3;
		gbc_changeypos.gridy = 2;
		buttonPanel.add(changeypos, gbc_changeypos);
		btnConfirmSelection.setEnabled(true);
		
		buttonPanel.setBackground(Color.WHITE);
		
		buttonPanel.repaint();
		mapFrame.repaint();

		getContentPane().add(mapFrame, BorderLayout.CENTER);

		buttonPanel.setBounds(0, 178, 394, 394);

		windowSizeX = buttonPanel.getWidth();
		windowSizeY = buttonPanel.getHeight();

		mapDropDown.addItem("Select Map");
		File vectorMapDir = new File("src/VectorMaps");
		vectorMapDir = new File(vectorMapDir.getAbsolutePath());
		File[] imgList = vectorMapDir.listFiles();
		for (File imageFile: imgList) {
			/*
			 * tempMapName = imgList[f].getName(); nameLength =
			 * tempMapName.length();
			 * mapDropDown.addItem(tempMapName.substring(0, nameLength - 4));
			 */
			// includes extension
			if(!(imageFile.getName().equals(".DS_Store")) || imageFile.getName().equals(srcMap.getMapName())){
				//System.out.println("Dropdown:" );
				String temp = imageFile.getName().substring(0, imageFile.getName().length() -4);
				//System.out.println(temp);

				for(int count = 0; count < emptyMaps.size(); count++){
					if(emptyMaps.get(count).getMapName().compareTo(temp) == 0){
						mapDropDown.addItem(temp);
					}
				}
			}
		}	
		btnConfirmSelection.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				for(int i = 0; i < pointArray.size(); i++){
					try {
							ServerDB.updatePoint(pointArray.get(i));
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (DoesNotExistException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		changexneg.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				editPoint(-gridSize,0);
			}
		});
		
		changexpos.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				editPoint(gridSize,0);
			}
		});
		
		changeyneg.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				editPoint(0,-gridSize);
			}
		});
		
		changeypos.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				editPoint(0,gridSize);
			}
		});
		
		mapFrame.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (!Dragged){
					//System.out.println("not dragged");
					btnConfirmSelection.setText("Confirm Selection");
					//System.out.println("Clicked");
					lastMousex = e.getX();
					lastMousey = e.getY();
					for(int i = 0; i < pointArray.size(); i++){
						double pointposx = (pointArray.get(i).getLocX()*newImageWidth)+drawnposx;
						double pointposy = (pointArray.get(i).getLocY()*newImageHeight)+drawnposy;
						if(lastMousex < (pointposx+(pointSize)) && lastMousex > (pointposx-(pointSize))){
							if(lastMousey < (pointposy+(pointSize)) && lastMousey > (pointposy-(pointSize))){
								currentPoint = pointArray.get(i);
								i = pointArray.size();
							}
						}
					}
					mapFrame.repaint();
					frame.repaint();
				} else{
					//System.out.println("dragged = true");
					Dragged = false;
					
				}
			}
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
                frame.repaint();
            }           
        });
		
		mapFrame.addMouseMotionListener(new MouseMotionListener(){

			public void mouseDragged(MouseEvent g){
				//System.out.println("dragged");
				drawnfirst = true;
				Dragged = true;
				mousex = g.getX();
				mousey = g.getY();
				mapFrame.repaint();
			}

			public void mouseMoved(MouseEvent j) {
				mousezoomx = j.getX();
				mousezoomy = j.getY();
			}
		});
		
		frame.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				System.out.println("key pressed");
				if (e.getKeyCode() == KeyEvent.VK_LEFT){
					editPoint(-gridSize,0);
					System.out.println("left");
	            }
	            else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
	            	editPoint(gridSize,0);
	            	System.out.println("right");
	            }
	            else if (e.getKeyCode() == KeyEvent.VK_UP){
	            	editPoint(0,-gridSize);
	            	System.out.println("up");
	            }
	            else if (e.getKeyCode() == KeyEvent.VK_DOWN){
	            	editPoint(0,gridSize);
	            	System.out.println("down");
	            }
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
	      });

		mapFrame.addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(!name.equals("Select Map")){
					scrolled = true;
					if(!(img == null)){
						minZoomSize = 1 / ((double) img.getWidth() / (double) mapFrame.getWidth());
						int WidthSize = (int) ((double) img.getHeight() * minZoomSize);
						if (WidthSize > (double) mapFrame.getHeight()) {
							minZoomSize = 1 / ((double) img.getHeight() / (double) mapFrame.getHeight());
						}
					}
					//System.out.println(minZoomSize);
					double oldWidth = (img.getWidth() * scaleSize);
					double oldHeight = (img.getHeight() * scaleSize);
					double ogx = ((mousezoomx-drawnposx)/oldWidth);
					double ogy = ((mousezoomy-drawnposy)/oldHeight);
					if(ogx<1 && ogx>0 && ogy<1 && ogy>0){
						if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL && (!(name.equals("Select Map")))) {
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
							
						}
					} else { // scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL

					}
					mapFrame.repaint();
					frame.repaint();
				}
			}
		});

		mapDropDown.addActionListener(new ActionListener() {//Open the dropdown menu
			

			public void actionPerformed(ActionEvent a) {
				pointArray.clear();
				edgeArray.clear();
				name = mapDropDown.getSelectedItem().toString();//When you select an item, grab the name of the map selected
				//System.out.println("Selected item:"+name);

				destinationFile = new File("src/VectorMaps/" + name+".png");
				destinationFile = new File(destinationFile.getAbsolutePath());
				ArrayList<Point> tempArrayList = null;
				//System.out.println("Got "+maps.size() + " maps from Updater");
				if (!(name.equals("Select Map"))) {//If the name is not the default: "Select map", go further

					
					for(int i = 0; i < emptyMaps.size(); i++){//Iterate through the mapList until we find the item we are looking for
						if(name.equals(emptyMaps.get(i).getMapName()))//Once we find the map:
						{
							try {
								currentMap = ServerDB.getMapFromServer(emptyMaps.get(i).getMapId());
								//System.out.println("Map ID is: " + currentMap.getMapId());
							} catch (DoesNotExistException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}//Grab the current map at this position.
							try {
								tempArrayList = ServerDB.getPointsFromServer(currentMap);
							} catch (PopulateErrorException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}//Populate the point array with all the points found.

							
							edgeArray.clear();
							Point newpoint;
							for(int j = 0; j < tempArrayList.size(); j++){
								Point point = tempArrayList.get(j);
								double newx = (((int)Math.floor((tempArrayList.get(j).getLocX()*(1/gridSize))+.5))*gridSize);
								double newy = (((int)Math.floor((tempArrayList.get(j).getLocY()*(1/gridSize))+.5))*gridSize);
								double ourRotation = currentMap.getRotationAngle();
								ourRotation = 2 * Math.PI - ourRotation;

								File destinationFile2 = new File("src/VectorMaps/" + "campus" + ".png");
								destinationFile2 = new File(destinationFile2.getAbsolutePath());
								BufferedImage campusImage = null;
								try {
									campusImage = ImageIO.read(destinationFile2);
								} catch (IOException e) {
									System.out.println("Invalid Map Selection");
									e.printStackTrace();
								}
								double tempPreRotateX ;
								double tempPreRotateY;
								//System.out.println("At step 1 x is: " + tempPreRotateX + " y is: " + tempPreRotateY);
								tempPreRotateX = newx;
								tempPreRotateY = newy;
								//System.out.println("At step 2 x is: " + tempPreRotateX + " y is: " + tempPreRotateY);
								tempPreRotateX = tempPreRotateX - 0.5;
								tempPreRotateY = tempPreRotateY - 0.5;
								//System.out.println("At step 3 x is: " + tempPreRotateX + " y is: " + tempPreRotateY);
								tempPreRotateX = tempPreRotateX * currentMap.getWidth();
								tempPreRotateY = tempPreRotateY * currentMap.getHeight();
								//System.out.println("At step 4 x is: " + tempPreRotateX + " y is: " + tempPreRotateY);
								double rotateX = Math.cos(ourRotation) * tempPreRotateX - Math.sin(ourRotation) * tempPreRotateY;
								double rotateY = Math.sin(ourRotation) * tempPreRotateX + Math.cos(ourRotation) * tempPreRotateY;
								//System.out.println("At step 5 x is: " + rotateX + " y is: " + rotateY);
								rotateX = rotateX * campusImage.getWidth();
								rotateY = rotateY * campusImage.getHeight();
								//System.out.println("At step 6 x is: " + rotateX + " y is: " + rotateY);
								int finalGlobX = (int) Math.round(rotateX + (campusImage.getWidth() * (currentMap.getxTopLeft() + currentMap.getxBotRight()) / 2));
								int finalGlobY = (int) Math.round(rotateY + (campusImage.getHeight() * (currentMap.getyTopLeft() + currentMap.getyBotRight()) / 2));
								newpoint = new Point(point.getId(), point.getMapId(), point.getName(), point.getIndex(), newx, newy, finalGlobX, 
										finalGlobY, point.getNumEdges(), point.isStairs(), point.isOutside());
								newpoint.setEdges(point.getEdges());
								pointArray.add(newpoint);
								ArrayList<Edge> tmpEdges = newpoint.getEdges();
								for(int k = 0; k < tmpEdges.size(); k++){
									if (tmpEdges.get(k).getPoint1().getMapId()==tmpEdges.get(k).getPoint2().getMapId()){
										if(tmpEdges.get(k).getPoint1().getId().equals(newpoint.getId())){
											tmpEdges.get(k).setPoint1(newpoint);
										}
										if(tmpEdges.get(k).getPoint2().getId().equals(newpoint.getId())){
											tmpEdges.get(k).setPoint2(newpoint);
										}
										edgeArray.add(tmpEdges.get(k));
									}
								}
							}
							i = emptyMaps.size();
						}
					}
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
						//System.out.println("loadLogo");
					}
					catch(IOException g){
						//System.out.println("Invalid logo");
						g.printStackTrace();
					}

				}
				mapFrame.repaint();
			}
		});


		//add logo at program init
		File logo = new File("src/VectorLogo/VectorrLogo.png");
		File logoFinal = new File(logo.getAbsolutePath());
		//System.out.println("logoFinal: " + logoFinal);
		try{
			img = ImageIO.read(logoFinal);
			//System.out.println("loadLogo");
			mapFrame.repaint();
		}
		catch(IOException g){
			//System.out.println("Invalid logo");
			g.printStackTrace();
		}
		buttonPanel.repaint();
	}

	class interButtonPanel extends JPanel {

		@Override
		public void paintComponent(Graphics l) {
			super.paintComponents(l);
			

		}
	}

	public static void main(String[] args) throws IOException, AlreadyExistsException, SQLException{
		
		new PointEditerGUI();

	}

	public void editPoint(double changex, double changey){
		Point pointtoadd = null;
		for(int i = 0; i < pointArray.size(); i++){
			if(currentPoint.getId().equals(pointArray.get(i).getId())){
				Point point = pointArray.get(i);
				System.out.println("old local pos x: "+point.getLocX()+", old local pos y: "+point.getLocY());
				System.out.println("new local pos x: "+(point.getLocX()+changex)+", new local pos y: "+(point.getLocY()+changey));
				Integer nameNumber = currentMap.getPointIDIndex()+1;
				double ourRotation = currentMap.getRotationAngle();
				ourRotation = 2 * Math.PI - ourRotation;

				File destinationFile2 = new File("src/VectorMaps/" + "campus" + ".png");
				destinationFile2 = new File(destinationFile2.getAbsolutePath());
				BufferedImage campusImage = null;
				try {
					campusImage = ImageIO.read(destinationFile2);
				} catch (IOException e) {
					System.out.println("Invalid Map Selection");
					e.printStackTrace();
				}
				double tempPreRotateX ;
				double tempPreRotateY;
				//System.out.println("At step 1 x is: " + tempPreRotateX + " y is: " + tempPreRotateY);
				tempPreRotateX = (point.getLocX()+changex);
				tempPreRotateY = (point.getLocY()+changey);
				//System.out.println("At step 2 x is: " + tempPreRotateX + " y is: " + tempPreRotateY);
				tempPreRotateX = tempPreRotateX - 0.5;
				tempPreRotateY = tempPreRotateY - 0.5;
				//System.out.println("At step 3 x is: " + tempPreRotateX + " y is: " + tempPreRotateY);
				tempPreRotateX = tempPreRotateX * currentMap.getWidth();
				tempPreRotateY = tempPreRotateY * currentMap.getHeight();
				//System.out.println("At step 4 x is: " + tempPreRotateX + " y is: " + tempPreRotateY);
				double rotateX = Math.cos(ourRotation) * tempPreRotateX - Math.sin(ourRotation) * tempPreRotateY;
				double rotateY = Math.sin(ourRotation) * tempPreRotateX + Math.cos(ourRotation) * tempPreRotateY;
				//System.out.println("At step 5 x is: " + rotateX + " y is: " + rotateY);
				rotateX = rotateX * campusImage.getWidth();
				rotateY = rotateY * campusImage.getHeight();
				//System.out.println("At step 6 x is: " + rotateX + " y is: " + rotateY);
				int finalGlobX = (int) Math.round(rotateX + (campusImage.getWidth() * (currentMap.getxTopLeft() + currentMap.getxBotRight()) / 2));
				int finalGlobY = (int) Math.round(rotateY + (campusImage.getHeight() * (currentMap.getyTopLeft() + currentMap.getyBotRight()) / 2));
				System.out.println("old global pos x: "+point.getGlobX()+", old global pos y: "+point.getGlobY());
				System.out.println("new global pos x: "+finalGlobX+", new global pos y: "+finalGlobY);
				pointtoadd = new Point(point.getId(), point.getMapId(), point.getName(), point.getIndex(), (point.getLocX()+changex), (point.getLocY()+changey), finalGlobX, 
								finalGlobY, point.getNumEdges(), point.isStairs(), point.isOutside());
						pointtoadd.setEdges(point.getEdges());
				pointArray.remove(i);
				pointArray.add(pointtoadd);
				currentPoint = pointtoadd;
				i = pointArray.size();
			}
		}
		for(int l = 0; l < edgeArray.size(); l++){
			if(edgeArray.get(l).getPoint1().getId().equals(pointtoadd.getId())){
				edgeArray.get(l).setPoint1(pointtoadd);
			}
			if(edgeArray.get(l).getPoint2().getId().equals(pointtoadd.getId())){
				edgeArray.get(l).setPoint2(pointtoadd);
			}
		}
		mapFrame.repaint();
		frame.repaint();
	}



	class MapPanel extends JPanel {

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			// -------------------------------
			// if(img == null)
			// img = ImageIO.read(new
			// File("/User/ibanatoski/git/campusMapping/src/VectorMaps/"));
			if (!(img == null)) {

				
				// Scale the image to the appropriate screen size

				if (drawnfirst == false) {
					windowScale = ((double) img.getWidth() / (double) mapFrame.getWidth());
					scaleSize = 1 / ((double) img.getWidth() / (double) mapFrame.getWidth());
					int WidthSize = (int) ((double) img.getHeight() / windowScale);
					if (WidthSize > (double) mapFrame.getHeight()) {
						windowScale = (double) img.getHeight() / (double) mapFrame.getHeight();
						scaleSize = 1 / ((double) img.getHeight() / (double) mapFrame.getHeight());
						// System.out.println("setting: "+scaleSize);
					}
					newImageWidth = (int) ((double) img.getWidth() / windowScale);
					newImageHeight = (int) ((double) img.getHeight() / windowScale);
					int centerx = (mapFrame.getWidth() / 2);
					int centery = (mapFrame.getHeight() / 2);
					drawnposx = centerx - (int) (newImageWidth / 2);
					drawnposy = centery - (int) (newImageHeight / 2);
					g.drawImage(img, (int)drawnposx, (int)drawnposy, (int) newImageWidth, (int) newImageHeight, null);
					// System.out.println(newImageWidth+", "+newImageHeight);
				} else {
					double deltax = 0;
					double deltay = 0;
					newImageHeight = (int) (img.getHeight() * scaleSize);
					newImageWidth = (int) (img.getWidth() * scaleSize);
					if (!name.equals("Select Map")){
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
			edgeArray.clear();
			for(int l = 0; l < pointArray.size(); l++){
				ArrayList<Edge> tmpEdges = pointArray.get(l).getEdges();
				for(int k = 0; k < tmpEdges.size(); k++){
					if (tmpEdges.get(k).getPoint1().getMapId()==tmpEdges.get(k).getPoint2().getMapId()){
						edgeArray.add(tmpEdges.get(k));
					}
				}
			}
			
			g.setColor(Color.GREEN);
			g2d.setStroke(new BasicStroke(4));
			for (int i = 0; i < edgeArray.size(); i++) {
				int point1x = (int) ((edgeArray.get(i).getPoint1().getLocX() * newImageWidth)
						+ drawnposx);
				int point1y = (int) ((edgeArray.get(i).getPoint1().getLocY() * newImageHeight)
						+ drawnposy);
				int point2x = (int) ((edgeArray.get(i).getPoint2().getLocX() * newImageWidth)
						+ drawnposx);
				int point2y = (int) ((edgeArray.get(i).getPoint2().getLocY() * newImageHeight)
						+ drawnposy);
				g2d.drawLine(point1x, point1y, point2x, point2y);
			}
			g.setColor(Color.BLACK);
			
			for(int i = 0; i < pointArray.size(); i++){
				int pointx = (int)((pointArray.get(i).getLocX()*newImageWidth)+drawnposx);
				int pointy = (int)((pointArray.get(i).getLocY()*newImageHeight)+drawnposy);
				g.setColor(Color.YELLOW);
				g.fillOval(pointx-(pointSize/2), pointy-(pointSize/2), pointSize, pointSize);
				g.setColor(Color.BLACK);
			}
			
			if(!(currentPoint == null)){
				int pointx = (int)((currentPoint.getLocX()*newImageWidth)+drawnposx);
				int pointy = (int)((currentPoint.getLocY()*newImageHeight)+drawnposy);
				g.setColor(Color.RED);
				g.fillOval(pointx-(pointSize/2), pointy-(pointSize/2), pointSize, pointSize);
				g.setColor(Color.BLACK);
			}
		}
	}
}