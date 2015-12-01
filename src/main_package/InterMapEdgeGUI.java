package main_package;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import main_package.MapInserterGUI.PaintFrame;
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
import javax.swing.JToolBar;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class InterMapEdgeGUI extends JFrame {

	private InserterButtonPanel ButtonPanel = new InserterButtonPanel();
	JComboBox mapDropDown;
	private static BufferedImage CampusMap = null;
	private static BufferedImage AddingMap = null;
	private int windowSizeX = 0;
	private int windowSizeY = 0;
	private int windowScale = 0;

	public InterMapEdgeGUI(java.awt.Point inserterLocation, Dimension windowSize) {
		super("Connect Two Maps");
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
		setLocation((int) (inserterLocation.x+windowSize.getWidth()),inserterLocation.y);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel SelectionPanel = new JPanel();
		getContentPane().add(SelectionPanel, BorderLayout.NORTH);
		GridBagLayout gbl_SelectionPanel = new GridBagLayout();
		gbl_SelectionPanel.columnWidths = new int[]{16, 99, 204, 53, 85, 0, 0, 0};
		gbl_SelectionPanel.rowHeights = new int[]{26, 0, 0, 0};
		gbl_SelectionPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_SelectionPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		SelectionPanel.setLayout(gbl_SelectionPanel);
		
		JLabel lblSelectDestMap = new JLabel("Select Destination Map:");
		GridBagConstraints gbc_lblSelectDestMap = new GridBagConstraints();
		gbc_lblSelectDestMap.anchor = GridBagConstraints.EAST;
		gbc_lblSelectDestMap.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectDestMap.gridx = 1;
		gbc_lblSelectDestMap.gridy = 0;
		SelectionPanel.add(lblSelectDestMap, gbc_lblSelectDestMap);
		
		mapDropDown = new JComboBox();
		GridBagConstraints gbc_mapDropDown = new GridBagConstraints();
		gbc_mapDropDown.insets = new Insets(0, 0, 5, 5);
		gbc_mapDropDown.fill = GridBagConstraints.HORIZONTAL;
		gbc_mapDropDown.gridx = 2;
		gbc_mapDropDown.gridy = 0;
		SelectionPanel.add(mapDropDown, gbc_mapDropDown);
		
		JLabel lblSelectDestPoint = new JLabel("Select Destination Point");
		GridBagConstraints gbc_lblSelectDestPoint = new GridBagConstraints();
		gbc_lblSelectDestPoint.anchor = GridBagConstraints.EAST;
		gbc_lblSelectDestPoint.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectDestPoint.gridx = 4;
		gbc_lblSelectDestPoint.gridy = 0;
		SelectionPanel.add(lblSelectDestPoint, gbc_lblSelectDestPoint);
		
		JComboBox comboDestPoint = new JComboBox();
		GridBagConstraints gbc_comboDestPoint = new GridBagConstraints();
		gbc_comboDestPoint.insets = new Insets(0, 0, 5, 5);
		gbc_comboDestPoint.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboDestPoint.gridx = 5;
		gbc_comboDestPoint.gridy = 0;
		SelectionPanel.add(comboDestPoint, gbc_comboDestPoint);
		
		JButton btnConfirmSelection = new JButton("Confirm Selection");
		GridBagConstraints gbc_btnConfirmSelection = new GridBagConstraints();
		gbc_btnConfirmSelection.insets = new Insets(0, 0, 5, 5);
		gbc_btnConfirmSelection.gridx = 4;
		gbc_btnConfirmSelection.gridy = 1;
		SelectionPanel.add(btnConfirmSelection, gbc_btnConfirmSelection);
		
		JPanel MapPanel = new JPanel();
		getContentPane().add(MapPanel, BorderLayout.CENTER);
		
		JPanel panel = new InserterButtonPanel();
		panel.setBounds(0, 178, 394, 394);
		
		windowSizeX = panel.getWidth();
		windowSizeY = panel.getHeight();
		
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
						
						//secondMap.addItem(imgList[f].getName());
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
					wScale = (double) AddingMap.getHeight() / (double) windowSizeY;
				}
				int imagelocationx = (windowSizeX/2)-((int)(AddingMap.getWidth()/wScale)/2);
				int imagelocationy = (windowSizeY/2)-((int)(AddingMap.getHeight()/wScale)/2);
				g.drawImage(AddingMap, imagelocationx, imagelocationy, (int)(AddingMap.getWidth()/wScale), (int)(AddingMap.getHeight()/wScale), null);
			}
		}
	}
}
