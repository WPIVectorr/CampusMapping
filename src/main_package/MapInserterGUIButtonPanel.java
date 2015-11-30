package main_package;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import main_package.MapUpdaterGUI.UpdateMap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Panel;
import java.awt.RenderingHints;
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


public class MapInserterGUIButtonPanel extends JFrame{

	private InserterButtonPanel ButtonPanel = new InserterButtonPanel();
	private JComboBox mapDropDown;
	private BufferedImage img = null;

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
		
		setSize(400, 600);
		setLocation((int) (inserterLocation.x+windowSize.getWidth()),inserterLocation.y);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(ButtonPanel);
		getContentPane().setVisible(true);
		getContentPane().setName("Vectorr");
		getContentPane().setLayout(null);
		
		JButton DeleteMap = new JButton("Delete Map");
		DeleteMap.setBounds(10, 109, 126, 23);
		getContentPane().add(DeleteMap);
		
		
		
		JComboBox mapDropDown = new JComboBox();
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

				}
				repaint();
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
				mapDropDown.addItem(imgList[f].getName());
			}
		}
		
		
		JComboBox secondMap = new JComboBox();
		secondMap.setBounds(168, 55, 211, 26);
		getContentPane().add(secondMap);
		secondMap.addItem("Select Map");
		
		JLabel lblSelectSecondMap = new JLabel("Select Map to Add:");
		lblSelectSecondMap.setBounds(10, 58, 178, 20);
		getContentPane().add(lblSelectSecondMap);
		
		secondMap.addActionListener(new ActionListener() {//Open the dropdown menu
			public void actionPerformed(ActionEvent a) {
				String name = secondMap.getSelectedItem().toString();//When you select an item, grab the name of the map selected
				System.out.println("Selected item:"+name);

				File destinationFile = new File("src/VectorMaps/" + name);
				destinationFile = new File(destinationFile.getAbsolutePath());

				
				if (!(name.equals("Select Map"))) {
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

				}
				repaint();
			}
		});

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





	class InserterButtonPanel extends JPanel {




		@Override
		public void paintComponent(Graphics g) {

		}
	}
}
