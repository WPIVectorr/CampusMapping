package main_package;


import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import database.DoesNotExistException;
import database.ServerDB;
import main_package.MapInserterGUI.PaintFrame;
//import main_package.MapUpdaterGUI.UpdateMap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class InterMapEdgeGUI extends JFrame {

	private interButtonPanel buttonPanel = new interButtonPanel();;
	private MapPanel mapFrame= new MapPanel();
	private JComboBox<String> mapDropDown;
	private JComboBox<String> comboDestPoint;
	private static BufferedImage CampusMap = null;
	private static BufferedImage AddingMap = null;
	private int windowSizeX = 0;
	private int windowSizeY = 0;
	private int windowScale = 0;
	private BufferedImage img = null;
	private static int lastMousex,lastMousey;
	private static int pointSize = 5;
	private static boolean mapSelected;
	private ArrayList<Point> pointArray = new ArrayList<Point>();
	private ArrayList<Map> maps = new ArrayList<Map>();

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
	private static JFrame frame = new JFrame("Add Edges Between Maps");

	
	public InterMapEdgeGUI(Map destMap, Point srcPoint) {
		

		
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
		
		setSize(800, 700);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setSize(screenWidth / 2, screenHeight / 2);
		setLocation(screenWidth / 4, screenHeight / 4);
		setVisible(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		
		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		GridBagLayout gbl_buttonPanel = new GridBagLayout();
		gbl_buttonPanel.columnWidths = new int[]{16, 99, 204, 53, 85, 277, 0, 0, 0};
		gbl_buttonPanel.rowHeights = new int[]{26, 0, 0, 0};
		gbl_buttonPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_buttonPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		buttonPanel.setLayout(gbl_buttonPanel);
		
		JLabel lblSelectDestMap = new JLabel("Select Destination Map:");
		GridBagConstraints gbc_lblSelectDestMap = new GridBagConstraints();
		gbc_lblSelectDestMap.anchor = GridBagConstraints.EAST;
		gbc_lblSelectDestMap.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectDestMap.gridx = 1;
		gbc_lblSelectDestMap.gridy = 0;
		buttonPanel.add(lblSelectDestMap, gbc_lblSelectDestMap);
		
		mapDropDown = new JComboBox<String>();
		GridBagConstraints gbc_mapDropDown = new GridBagConstraints();
		gbc_mapDropDown.insets = new Insets(0, 0, 5, 5);
		gbc_mapDropDown.fill = GridBagConstraints.HORIZONTAL;
		gbc_mapDropDown.gridx = 2;
		gbc_mapDropDown.gridy = 0;
		buttonPanel.add(mapDropDown, gbc_mapDropDown);
		
		
		JLabel lblSelectDestPoint = new JLabel("Select Destination Point");
		GridBagConstraints gbc_lblSelectDestPoint = new GridBagConstraints();
		gbc_lblSelectDestPoint.anchor = GridBagConstraints.EAST;
		gbc_lblSelectDestPoint.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectDestPoint.gridx = 4;
		gbc_lblSelectDestPoint.gridy = 0;
		buttonPanel.add(lblSelectDestPoint, gbc_lblSelectDestPoint);
		
		comboDestPoint = new JComboBox<String>();
		GridBagConstraints gbc_comboDestPoint = new GridBagConstraints();
		gbc_comboDestPoint.insets = new Insets(0, 0, 5, 5);
		gbc_comboDestPoint.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboDestPoint.gridx = 5;
		gbc_comboDestPoint.gridy = 0;
		buttonPanel.add(comboDestPoint, gbc_comboDestPoint);
		
		JButton btnConfirmSelection = new JButton("Confirm Selection");
		GridBagConstraints gbc_btnConfirmSelection = new GridBagConstraints();
		gbc_btnConfirmSelection.insets = new Insets(0, 0, 5, 5);
		gbc_btnConfirmSelection.gridx = 4;
		gbc_btnConfirmSelection.gridy = 1;
		buttonPanel.add(btnConfirmSelection, gbc_btnConfirmSelection);
		

		mapFrame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("Clicked");
				lastMousex = e.getX();
				lastMousey = e.getY();
				selectPoint();
			}
		});
		getContentPane().add(mapFrame, BorderLayout.CENTER);
		
		buttonPanel.setBounds(0, 178, 394, 394);
		
		windowSizeX = buttonPanel.getWidth();
		windowSizeY = buttonPanel.getHeight();

		maps = ServerDB.getMapsFromLocal();
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
			if(!(imageFile.getName().equals(".DS_Store"))){
				System.out.println("Dropdown:" );
				String temp = imageFile.getName().substring(0, imageFile.getName().length() -4);
				System.out.println(temp);

				//checks to make sure the names populating the drop down are in both the vector maps package and 
				//the database
				for(Map currMap: maps){
					System.out.println("printing from database: " + currMap.getMapName());
					if(currMap.getMapName().compareTo(temp) == 0){
						mapDropDown.addItem(temp);
						
					}
				}

			}
		}	
		
		
		frame.repaint();
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
								mapSelected = true;
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
								System.out.println("loadLogo");
							}
							catch(IOException g){
								System.out.println("Invalid logo");
								g.printStackTrace();
							}
							pointArray.clear();
							edgeArray.clear();
						}
						doRepaint();
					}
				});


	}

	private void selectPoint()
	{
		if(pointArray.size()!=0)
		{
			for(Point loopPoint:pointArray)
			{
				if(checkInPoint(loopPoint))
					currentPoint = loopPoint;
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InterMapEdgeGUI addInterMapEdge = new InterMapEdgeGUI(null, null);
		addInterMapEdge.setVisible(true);
	}
	public static BufferedImage getCampusMap() {
		return CampusMap;
	}


	public void setCampusMap(BufferedImage campusMap) {
		CampusMap = campusMap;
	}

	public String getSelectedMap()
	{
		String selectedMap = mapDropDown.getSelectedItem().toString();	
		
		return selectedMap;
	}

	public static BufferedImage getAddingMap() {
		return AddingMap;
	}


	public static void setAddingMap(BufferedImage addingMap) {
		AddingMap = addingMap;
	}
	private static void doRepaint()
	{
		//buttonPanel.repaint();
		//`mapPanel.repaint();
	}


	class interButtonPanel extends JPanel {
		
		public interButtonPanel()
		{

			
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponents(g);
			
			if (!(AddingMap == null)){
				double wScale;

				if (AddingMap.getHeight() >= AddingMap.getWidth()) {
					wScale = (double) AddingMap.getHeight() / (double) windowSizeY;
				}

				else {
					wScale = (double) AddingMap.getHeight() / (double) windowSizeY;
				}
				int imagelocationx = (windowSizeX/2)-((int)(AddingMap.getWidth()/wScale)/2);
				int imagelocationy = (windowSizeY/2)-((int)(AddingMap.getHeight()/wScale)/2);
				g.drawImage(AddingMap, imagelocationx, imagelocationy, (int)(AddingMap.getWidth()/wScale), (int)(AddingMap.getHeight()/wScale), null);
			}
		}
	}
	
	
	
	
	
	
	
	class MapPanel extends JPanel {

		ArrayList<Point> paintArray = new ArrayList<Point>(); // arraylist of
		// points
		// already
		// painted
		// Point editPoint;
		// Driver values used for testing:

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






			

			// draws all the points onto the map.
			// cleans the array of deleted points.
			if (pointArray.size() > 0) {
				for (Point currentPoint: pointArray) {


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

		}

	}
	
	
	private boolean checkInPoint(Point currentPoint)
	{
		
		if ((lastMousex > currentPoint.getLocX() - (pointSize + 5)
				&& lastMousex < currentPoint.getLocX() + (pointSize + 5))
				&& (lastMousey > currentPoint.getLocY() - (pointSize + 5)
						&& lastMousey < currentPoint.getLocY() + (pointSize + 5))) {
				System.out.println("in Point");
				return true;
			}else{
				return false;
			}
	}



	private Map updatedestMap(Map map)
	{
		int mapId = map.getMapId();
		ArrayList<Map> mapList = ServerDB.getMapsFromLocal();
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
