
package main_package;


import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.mysql.fabric.Server;

import database.AlreadyExistsException;
import database.DoesNotExistException;
import database.InsertFailureException;
import database.NoMapException;
import database.ServerDB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class InterMapEdgeGUI extends JFrame {

	private interButtonPanel buttonPanel = new interButtonPanel();;
	private MapPanel mapFrame= new MapPanel();
	private JComboBox<String> mapDropDown;
	private JComboBox<String> destDropDown;
	private static BufferedImage campusMap = null;
	private static BufferedImage img = null;
	private int windowSizeX = 0;
	private int windowSizeY = 0;
	private double windowScale = 0;
	private static int lastMousex,lastMousey;
	private static int pointSize = 7;
	private ArrayList<Point> pointArray = new ArrayList<Point>();
	private ArrayList<Map> maps = new ArrayList<Map>();
	private ArrayList<Edge> edgeArray = new ArrayList<Edge>();
	private Point srcPoint;
	private JButton btnConfirmSelection;

	private Point connectPoint;
	String name;
	File destinationFile;
	File logo;

	private Map connectMap = null;
	private Map srcMap = null;
	private ServerDB md = ServerDB.getInstance();
	
	private double imagewidth;
	private double imageheight;



	private static JFrame frame = new JFrame("Add Edges Between Maps");


	public InterMapEdgeGUI(ArrayList<Map> mapList, Point passedPoint, int drawwidth, int drawheight) {

		imagewidth = drawwidth;
		imageheight = drawheight;
		if(mapList != null)
			maps = mapList;

		if(passedPoint != null)
		{
			srcPoint = passedPoint;
			for(Map loopMap:maps)
			{
				if(loopMap.getMapId()==srcPoint.getMapId())
					srcMap = loopMap;
			}
		}


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
		setSize(932, 778);
		frame.setResizable(false);
		setLocation(screenWidth / 4, screenHeight / 4);
		setVisible(true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
		if(maps ==null || maps.size()==0)
			mapDropDown.setEnabled(false);

		JLabel lblSelectDestPoint = new JLabel("Select Destination Point");
		GridBagConstraints gbc_lblSelectDestPoint = new GridBagConstraints();
		gbc_lblSelectDestPoint.anchor = GridBagConstraints.EAST;
		gbc_lblSelectDestPoint.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectDestPoint.gridx = 4;
		gbc_lblSelectDestPoint.gridy = 0;
		buttonPanel.add(lblSelectDestPoint, gbc_lblSelectDestPoint);

		destDropDown = new JComboBox<String>();
		GridBagConstraints gbc_destDropDown = new GridBagConstraints();
		gbc_destDropDown.insets = new Insets(0, 0, 5, 5);
		gbc_destDropDown.fill = GridBagConstraints.HORIZONTAL;
		gbc_destDropDown.gridx = 5;
		gbc_destDropDown.gridy = 0;
		buttonPanel.add(destDropDown, gbc_destDropDown);


		btnConfirmSelection = new JButton("Confirm Selection");
		GridBagConstraints gbc_btnConfirmSelection = new GridBagConstraints();
		gbc_btnConfirmSelection.insets = new Insets(0, 0, 5, 5);
		gbc_btnConfirmSelection.gridx = 4;
		gbc_btnConfirmSelection.gridy = 1;
		buttonPanel.add(btnConfirmSelection, gbc_btnConfirmSelection);
		btnConfirmSelection.setEnabled(false);
		
		buttonPanel.repaint();
		mapFrame.repaint();

		mapFrame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				btnConfirmSelection.setText("Confirm Selection");
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
				if(maps != null)
				{
					for(Map currMap: maps){
						System.out.println("printing from database: " + currMap.getMapName());
						if(currMap.getMapName().compareTo(temp) == 0){
							mapDropDown.addItem(temp);

						}
					}
				}
			}
		}	
		btnConfirmSelection.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				if(connectPoint != null) {
					if (!((edgeArray.contains(new Edge(srcPoint, connectPoint)) || (!(edgeArray.contains(new Edge(connectPoint, srcPoint))))))){
						try {
							ServerDB.insertEdge(new Edge(srcPoint, connectPoint));
						} catch (InsertFailureException | AlreadyExistsException | SQLException | DoesNotExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	
						System.out.println("edge Created ");
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					} else{
						btnConfirmSelection.setText("Edge Already Exists");
					}
				} else {
					btnConfirmSelection.setText("Pease Select Point");
				}
				
			}
		});

		mapDropDown.addActionListener(new ActionListener() {//Open the dropdown menu
			public void actionPerformed(ActionEvent a) {
				pointArray.clear();
				connectPoint = null;
				name = mapDropDown.getSelectedItem().toString();//When you select an item, grab the name of the map selected
				System.out.println("Selected item:"+name);

				destinationFile = new File("src/VectorMaps/" + name+".jpg");
				destinationFile = new File(destinationFile.getAbsolutePath());


				if(maps == null)
				{
					ArrayList<Map> maps = md.getMapsFromLocal(); //Grab all the maps from the database
					System.out.println("Got "+maps.size() + " from Database");//Print out the size of the maps from the database
				}else{
					System.out.println("Got "+maps.size() + " maps from Updater");
				}
				pointArray.clear();
				destDropDown.removeAllItems();
				if (!(name.equals("Select Map"))) {//If the name is not the default: "Select map", go further

					
					for(int i = 0; i < maps.size(); i++){//Iterate through the maps until we find the item we are looking for
						//System.out.println("Trying to find name:"+name);
						if(name.equals(maps.get(i).getMapName()))//Once we find the map:
						{
							connectMap = maps.get(i);//Grab the current map at this position.
							pointArray = connectMap.getPointList();//Populate the point array with all the points found.
							for(int count = 0; count < pointArray.size(); count ++){
								destDropDown.addItem(pointArray.get(count).getName());
							}
							System.out.println("Map list size:"+maps.size());

							System.out.println("Found map with number of points: "+connectMap.getPointList().size());

							i = maps.size();
						}
						
					}
					System.out.println("getting edges");
					if(pointArray != null){
						for(int k=0; k < pointArray.size(); k++){
							for(int j=0; j < pointArray.get(k).getNumEdges(); j++){
								edgeArray.add(pointArray.get(k).getEdges().get(j));
							}
						}
					}
					System.out.println("got edges");
					System.out.println("current Map: " +connectMap.getMapName());

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
						System.out.println("loadLogo");
					}
					catch(IOException g){
						System.out.println("Invalid logo");
						g.printStackTrace();
					}
					pointArray.clear();

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
			System.out.println("loadLogo");
			mapFrame.repaint();
		}
		catch(IOException g){
			System.out.println("Invalid logo");
			g.printStackTrace();
		}
		mapFrame.repaint();
		buttonPanel.repaint();
	}

	private void selectPoint()
	{
		if(pointArray.size()!=0)
		{
			for(Point loopPoint:pointArray)
			{
				if(checkInPoint(loopPoint))
					connectPoint = loopPoint;
			}
		}
		if(connectPoint != null)
		{
			mapFrame.repaint();
			btnConfirmSelection.setEnabled(true);
			System.out.println("srcPoint id: "+srcPoint.getId());
			System.out.println("Connection Point id: "+connectPoint.getId());
		}

	}

	public static BufferedImage getcampusMap() {
		return campusMap;
	}


	public void setcampusMap(BufferedImage campusMap) {
		campusMap = campusMap;
	}

	public String getSelectedMap()
	{
		String selectedMap = mapDropDown.getSelectedItem().toString();	

		return selectedMap;
	}

	public static BufferedImage getimg() {
		return img;
	}


	public static void setimg(BufferedImage mapToAdd) {
		img = mapToAdd;
	}
	private static void doRepaint()
	{
		//buttonPanel.repaint();
		//mapFrame.repaint();
	}


	class interButtonPanel extends JPanel {

		/*		public interButtonPanel()
		{
		}*/
		@Override
		public void paintComponent(Graphics l) {
			super.paintComponents(l);
			if(maps == null || maps.size() == 0)
			{
				if(mapDropDown != null && destDropDown != null)
				{
					mapDropDown.setEnabled(false);
					destDropDown.setEnabled(false);
				}
			}else{
				if(mapDropDown != null)
					mapDropDown.setEnabled(true);
			}
			if(pointArray != null && pointArray.size()!=0)
			{
				/*for(Point currPoint: pointArray)
				{
					destDropDown.addItem(currPoint.getName());
				}*/
				if(destDropDown != null)
					destDropDown.setEnabled(true);
			}else if(pointArray.size() == 0)
			{
				destDropDown.setEnabled(false);
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
			// File("/User/ibanatoski/git/campusMapping/src/VectorMaps/"));

			if (!(img == null)) {

				// Scale the image to the appropriate screen size
				//System.out.println("painting Image");
				System.out.println("Map Frame Width: "+ mapFrame.getWidth());
				System.out.println("Map Frame Height: "+ mapFrame.getHeight());
				windowScale = ((double)img.getWidth() / (double)imagewidth);
				int WidthSize = (int)((double) img.getHeight() / windowScale);
				if (WidthSize > (double)imageheight){
					windowScale =  ((double)img.getHeight() / (double)imageheight);
				}
				System.out.println("SCALE!: "+ windowScale);
				//System.out.println((int)((double)img.getWidth() / windowScale)+", "+(int)((double)img.getHeight() / windowScale));
				g.drawImage(img, 0, 0, (int)((double)img.getWidth() / windowScale), (int)((double)img.getHeight() / windowScale), null);
			}


			// draws all the points onto the map.
			// cleans the array of deleted points.
			if (pointArray.size() > 0) {
				for (Point loopPoint: pointArray) {
					int drawX = (int) loopPoint.getLocX();
					int drawY = (int) loopPoint.getLocY();
					// draws the points onto the map.
					//System.out.println("printoval");
					g.fillOval(drawX - (pointSize / 2), drawY - (pointSize / 2), pointSize, pointSize);

					if(connectPoint != null)
					{
						g.setColor(Color.ORANGE);
						g.fillOval(connectPoint.getLocX()-(pointSize+2 / 2),connectPoint.getLocY()-(pointSize+2 / 2), pointSize+2, pointSize+2);
						g.setColor(Color.BLACK);						
					}
				}

				//mapFrame.repaint();
			}

		}

	}


	private boolean checkInPoint(Point selectPoint)
	{

		if ((lastMousex > selectPoint.getLocX() - (pointSize + 5)
				&& lastMousex < selectPoint.getLocX() + (pointSize + 5))
				&& (lastMousey > selectPoint.getLocY() - (pointSize + 5)
						&& lastMousey < selectPoint.getLocY() + (pointSize + 5))) {
			System.out.println("in Point");
			return true;
		}else{
			return false;
		}
	}


}