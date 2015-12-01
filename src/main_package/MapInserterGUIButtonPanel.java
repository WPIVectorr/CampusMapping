package main_package;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import main_package.MapInserterGUI.PaintFrame;
//import main_package.MapUpdaterGUI.UpdateMap;

import java.awt.BorderLayout;
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
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;


public class MapInserterGUIButtonPanel extends JFrame {

	private InserterButtonPanel ButtonPanel = new InserterButtonPanel();
	private JComboBox mapDropDown;
	private JComboBox secondMap;
	private static BufferedImage CampusMap = null;
	private static BufferedImage AddingMap = null;
	private int windowSizeX = 0;
	private int windowSizeY = 0;
	private int windowScale = 0;

	public MapInserterGUIButtonPanel(java.awt.Point inserterLocation, Dimension windowSize) {
		super("MapInserterGUIButtonPanel");
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
		setSize(screenWidth / 2, screenHeight / 2);
		setLocation(screenWidth / 4, screenHeight / 4);
		
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(ButtonPanel);
		getContentPane().setVisible(true);
		getContentPane().setName("Vectorr");
		getContentPane().setLayout(null);
		repaint();
		//JButton DeleteMap = new JButton("Delete Map");
		//DeleteMap.setBounds(10, 109, 126, 23);
		//getContentPane().add(DeleteMap);
		
		
		mapDropDown = new JComboBox();
		mapDropDown.setBounds(168, 13, 211, 26);
		getContentPane().add(mapDropDown);
		mapDropDown.addItem("Select Map");
		
		JLabel lblSelectCampusMap = new JLabel("Select Campus Map:");
		lblSelectCampusMap.setBounds(10, 16, 178, 20);
		getContentPane().add(lblSelectCampusMap);

		
		mapDropDown.addActionListener(new ActionListener() {//Open the dropdown menu
			public void actionPerformed(ActionEvent a) {
				String name = mapDropDown.getSelectedItem().toString();//When you select an item, grab the name of the map selected
				System.out.println("Selected item:"+name);

				File destinationFile = new File("src/VectorMaps/" + name);
				destinationFile = new File(destinationFile.getAbsolutePath());

				
				if (!(name.equals("Select Map"))) {
					try {
						setCampusMap(ImageIO.read(destinationFile));
					} catch (IOException g) {
						System.out.println("Invalid Map Selection");
						g.printStackTrace();
					}
				} else {
					File logo = new File("src/VectorLogo/VectorrLogo.png");
					File logoFinal = new File(logo.getAbsolutePath());
					//System.out.println("logoFinal: " + logoFinal);
					try{
						setCampusMap(ImageIO.read(logoFinal));
					}
					catch(IOException g){
						System.out.println("Invalid logo");
						g.printStackTrace();
					}

				}
				MapInserterGUI.doRepaint();
			}
		});

		
		JButton Savemap = new JButton();
		Savemap.setText("Save Map");
		Savemap.setBounds(210, 98, 150, 26);
		getContentPane().add(Savemap);
		Savemap.addActionListener(new ActionListener() {//Saves the map in the specific location
			public void actionPerformed(ActionEvent e) {
				MapInserterGUI.DisposeFrame();
				dispose();
				System.out.println("telling to send");
				MapInserterGUI.GiveMapUpdaterInfo();
			}
		});
		
		JButton DeletePoints = new JButton();
		DeletePoints.setText("Delete Points");
		DeletePoints.setBounds(30, 98, 150, 26);
		getContentPane().add(DeletePoints);
		DeletePoints.addActionListener(new ActionListener() {//Saves the map in the specific location
			public void actionPerformed(ActionEvent e) {
				MapInserterGUI.clearAlignmentPoints();
				MapInserterGUI.resetCornerNum();
				MapInserterGUI.doRepaint();
			}
		});
		
		JComboBox secondMap = new JComboBox();
		secondMap.setBounds(168, 55, 211, 26);
		getContentPane().add(secondMap);
		secondMap.addItem("Select Map");
		
		JLabel lblSelectSecondMap = new JLabel("Select Map to Add:");
		lblSelectSecondMap.setBounds(10, 58, 178, 20);
		getContentPane().add(lblSelectSecondMap);
		
		JPanel panel = new InserterButtonPanel();
		panel.setBounds(0, 178, 394, 394);
		getContentPane().add(panel);
		
		windowSizeX = panel.getWidth();
		windowSizeY = panel.getHeight();
		
		secondMap.addActionListener(new ActionListener() {//Open the dropdown menu
			public void actionPerformed(ActionEvent a) {
				String name = secondMap.getSelectedItem().toString();//When you select an item, grab the name of the map selected
				System.out.println("Selected item:"+name);

				File destinationFile = new File("src/VectorMaps/" + name);
				destinationFile = new File(destinationFile.getAbsolutePath());

				
				if (!(name.equals("Select Map"))) {
					try {
						setAddingMap(ImageIO.read(destinationFile));
					} catch (IOException g) {
						System.out.println("Invalid Map Selection");
						g.printStackTrace();
					}
				} else {
					File logo = new File("src/VectorLogo/VectorrLogo.png");
					File logoFinal = new File(logo.getAbsolutePath());
					//System.out.println("logoFinal: " + logoFinal);
					try{
						setAddingMap(ImageIO.read(logoFinal));
					}
					catch(IOException g){
						System.out.println("Invalid logo");
						g.printStackTrace();
					}
				}
				panel.repaint();
				System.out.println("painting");
			}
		});
		
		// When the Updater opens the software the list will be populated with
				// the files in
				// the VectorMapps resource folder
				File vectorMapDir = new File("src/VectorMaps");
				vectorMapDir = new File(vectorMapDir.getAbsolutePath());
				//System.out.println("Vectormap abs path: " + vectorMapDir.getAbsolutePath());

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
						
						secondMap.addItem(imgList[f].getName());
						mapDropDown.addItem(imgList[f].getName());
					}
				}
				

/*		// When the Updater opens the software the list will be populated with
		// the files in
		// the VectorMapps resource folder
		File vectorMapDir = new File("src/VectorMaps");
		vectorMapDir = new File(vectorMapDir.getAbsolutePath());
		//System.out.println("Vectormap abs path: " + vectorMapDir.getAbsolutePath());

		// Truncates the extensions off of the map name so only the name is
		// displayed in the
		// drop-down menu for selecting a map
		File[] imgList = vectorMapDir.listFiles();
		for (int f = 0; f < imgList.length; f++) {
			
			 * tempMapName = imgList[f].getName(); nameLength =
			 * tempMapName.length();
			 * mapDropDown.addItem(tempMapName.substring(0, nameLength - 4));
			 
			// includes extension
			if(!(imgList[f].getName().equals(".DS_Store"))){
				secondMap.addItem(imgList[f].getName());
			}
		}*/

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


	class InserterButtonPanel extends JPanel {
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponents(g);
			
			if (!(AddingMap == null)){
				double wScale;

				if (AddingMap.getHeight() >= AddingMap.getWidth()) {
					wScale = (double) AddingMap.getHeight() / (double) windowSizeY;
				}

				else {
					wScale = (double) AddingMap.getWidth() / (double) windowSizeX;
				}
				int imagelocationx = (windowSizeX/2)-((int)(AddingMap.getWidth()/wScale)/2);
				int imagelocationy = (windowSizeY/2)-((int)(AddingMap.getHeight()/wScale)/2);
				int lowerleft = imagelocationy+(int)(AddingMap.getHeight()/wScale)-5;
				g.drawImage(AddingMap, imagelocationx, imagelocationy, (int)(AddingMap.getWidth()/wScale), (int)(AddingMap.getHeight()/wScale), null);
				g.fillOval(imagelocationx, imagelocationy, 5, 5);
				g.fillOval(imagelocationx, lowerleft, 5, 5);
				g.drawString("Point 1", imagelocationx, imagelocationy+20);
				g.drawString("Point 2", imagelocationx, lowerleft);
			}
		}
	}
}
