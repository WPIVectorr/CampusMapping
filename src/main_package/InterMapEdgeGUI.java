
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
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
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

	private Point connectPoint;
	String name;
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



	private static JFrame frame = new JFrame("Add Edges Between Maps");


	public InterMapEdgeGUI(ArrayList<Map> mapList, Point passedPoint, int drawwidth, int drawheight) {

		imagewidth = drawwidth;
		imageheight = drawheight;
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
		int framex = 932;
		int framey = 778;
		setSize(framex, framey);
		frame.setResizable(false);
		int locationx = screenWidth / 2;
		int locationy = screenHeight / 2;
		setLocation(locationx-(framex/2), locationy-(framey/2));
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
		if(maps == null || maps.size()==0)
			mapDropDown.setEnabled(false);

		JLabel lblSelectDestPoint = new JLabel("Select Destination Point");
		GridBagConstraints gbc_lblSelectDestPoint = new GridBagConstraints();
		gbc_lblSelectDestPoint.anchor = GridBagConstraints.EAST;
		gbc_lblSelectDestPoint.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectDestPoint.gridx = 4;
		gbc_lblSelectDestPoint.gridy = 0;
		buttonPanel.add(lblSelectDestPoint, gbc_lblSelectDestPoint);

		destDropDown = new JTextField();
		GridBagConstraints gbc_destDropDown = new GridBagConstraints();
		gbc_destDropDown.insets = new Insets(0, 0, 5, 5);
		gbc_destDropDown.fill = GridBagConstraints.HORIZONTAL;
		gbc_destDropDown.gridx = 5;
		gbc_destDropDown.gridy = 0;
		buttonPanel.add(destDropDown, gbc_destDropDown);
		destDropDown.setEditable(false);


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
				//System.out.println("Clicked");
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
			if(!(imageFile.getName().equals(".DS_Store")) || imageFile.getName().equals(srcMap.getMapName())){
				//System.out.println("Dropdown:" );
				String temp = imageFile.getName().substring(0, imageFile.getName().length() -4);
				//System.out.println(temp);

				for(int count = 0; count < maps.size(); count++){
					if(maps.get(count).getMapName().compareTo(temp) == 0){
						mapDropDown.addItem(temp);
						System.out.println("Number of edges: " + edgeArray.size());
					}
				}
			}
		}	
		btnConfirmSelection.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				boolean alreadyExists = false;
				if(connectPoint != null) {
					String edgeIDPossibility1 = (srcPoint.getId()+"-"+connectPoint.getId());
					String edgeIDPossibility2 = (connectPoint.getId()+"-"+srcPoint.getId());
					for (int j = 0; j < edgeArray.size(); j++)
					{
						Edge currEdge =  edgeArray.get(j);
						String edgeId = currEdge.getId();
						if (edgeId.contentEquals(edgeIDPossibility1) || edgeId.contentEquals(edgeIDPossibility2))
						{
							alreadyExists = true;
						}
					}
					
					if (!alreadyExists){
						try {
							ServerDB.insertEdge(new Edge(srcPoint, connectPoint));
						} catch (InsertFailureException | AlreadyExistsException | SQLException | DoesNotExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//System.out.println("edge Created ");
						frame.dispose();
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					} else{
						btnConfirmSelection.setText("Edge Already Exists");
						buttonPanel.repaint();
						buttonPanel.validate();
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
				//System.out.println("Selected item:"+name);

				destinationFile = new File("src/VectorMaps/" + name+".jpg");
				destinationFile = new File(destinationFile.getAbsolutePath());
				
				//System.out.println("Got "+maps.size() + " maps from Updater");
				destDropDown.setText("Select Point");
				if (!(name.equals("Select Map"))) {//If the name is not the default: "Select map", go further

					
					for(Map currMap:maps){//Iterate through the maps until we find the item we are looking for
						//System.out.println("Trying to find name:"+name);
						if(name.equals(currMap.getMapName()))//Once we find the map:
						{
							connectMap = currMap;//Grab the current map at this position.
							for(Point pointBreak:connectMap.getPointList())
							{
								pointArray.add(pointBreak);//Populate the point array with all the points found.
							}
						}
						
					}
					//System.out.println("getting edges");
					if(pointArray != null){
						for(int k=0; k < pointArray.size(); k++){
							for(int j=0; j < pointArray.get(k).getNumEdges(); j++){
								edgeArray.add(pointArray.get(k).getEdges().get(j));
							}
						}
					}
					//System.out.println("got edges");
					//System.out.println("current Map: " +connectMap.getMapName());

					/*				File destinationFile = new File("src/VectorMaps/" + name);
															destinationFile = new File(destinationFile.getAbsolutePath());
															if (!(name.equals("Select Map"))) {*/
					try {
						img = ImageIO.read(destinationFile);
					} catch (IOException g) {
						//System.out.println("Invalid Map Selection");
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
			//System.out.println("srcPoint id: "+srcPoint.getId());
			//System.out.println("Connection Point id: "+connectPoint.getId());
		}
		destDropDown.setText(connectPoint.getName());
	}

	class interButtonPanel extends JPanel {

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
				if(destDropDown != null)
					destDropDown.setEnabled(true);
			}else if(pointArray.size() == 0)
			{
				destDropDown.setEnabled(false);
			}

		}
	}







	class MapPanel extends JPanel {

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2D = (Graphics2D) g;
			// -------------------------------
			// if(img == null)
			// img = ImageIO.read(new
			// File("/User/ibanatoski/git/campusMapping/src/VectorMaps/"));

			if (!(img == null)) {

				// Scale the image to the appropriate screen size

				if (drawnfirst == false){
					windowScale = ((double)img.getWidth() / (double)mapFrame.getWidth());
					scaleSize = 1/((double)img.getWidth() / (double)mapFrame.getWidth());
					//System.out.println("setting: "+scaleSize);
					//System.out.println("Image Original Width " + img.getWidth());
					int WidthSize = (int)((double) img.getHeight() / windowScale);
					if (WidthSize > (double)mapFrame.getHeight()){
						windowScale = (double)img.getHeight() / (double)mapFrame.getHeight();
						scaleSize = 1/((double)img.getHeight() / (double)mapFrame.getHeight());
						//System.out.println("setting: "+scaleSize);
					}
					int imagesizex = (int)((double)img.getWidth() / windowScale);
					int imagesizey = (int)((double)img.getHeight() / windowScale);
					int centerx = (mapFrame.getWidth()/2);
					int centery = (mapFrame.getHeight()/2);
					int drawx = centerx -(imagesizex/2);
					int drawy = centery -(imagesizey/2);
					g.drawImage(img, drawx, drawy, imagesizex, imagesizey, null);
					drawnposx = drawx;
					drawnposy = drawy;
					
				} else{
					AffineTransform originalTransform = g2D.getTransform();
					double deltax = 0;
					double deltay = 0;
					g2D.scale(scaleSize, scaleSize);
					if(Dragged){
						deltax = -(originx - mousex)/scaleSize;
						deltay = -(originy - mousey)/scaleSize;
						//System.out.println("change in x: " + deltax);
						//System.out.println("change in y: " + deltay);
						originx = mousex;
						originy = mousey;
						g2D.translate(deltax, deltay);
						imageX =0;
						imageY = 0;
						difWidth = 0;
						difHeight = 0;
					} else{
						//System.out.println("I did it");
						g2D.translate(0, 0);
						deltax = -difWidth;
						deltay = -difHeight;
					}
					//System.out.println("Before Draw Scale: " + scaleSize);
					g2D.drawImage(img,  drawnposx, drawnposy, null);
					g.fillOval(drawnposx, drawnposy, 20, 20);
					//System.out.println("scale: " + scaleSize);
					//System.out.println("drawn position x: " + drawnposx);
					//System.out.println("drawn position y: " + drawnposy);
					drawnposx += deltax;
					drawnposy += deltay;
					g2D.setTransform(originalTransform);
					newImageHeight = img.getHeight()*scaleSize;
					newImageWidth = img.getWidth()*scaleSize;
				}
				drawnfirst = true;
				
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
						int pointx = (int)((connectPoint.getLocX()*newImageWidth)+drawnposx);
						int pointy = (int)((connectPoint.getLocY()*newImageHeight)+drawnposy);
						g.fillOval(pointx-((pointSize / 2) + 2),pointy-((pointSize / 2) + 2), pointSize+2, pointSize+2);
						g.setColor(Color.BLACK);
					}
				}
			}

		}

	}


	private boolean checkInPoint(Point selectPoint)
	{

		if ((lastMousex > selectPoint.getLocX() - (pointSize + 5)
				&& lastMousex < selectPoint.getLocX() + (pointSize + 5))
				&& (lastMousey > selectPoint.getLocY() - (pointSize + 5)
						&& lastMousey < selectPoint.getLocY() + (pointSize + 5))) {
			//System.out.println("in Point");
			return true;
		}else{
			return false;
		}
	}


}