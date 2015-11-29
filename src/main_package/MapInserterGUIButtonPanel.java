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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JButton;


public class MapInserterGUIButtonPanel extends JFrame{

	private InserterButtonPanel ButtonPanel = new InserterButtonPanel();


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
		
		setSize(200, 400);
		setLocation((int) (inserterLocation.x+windowSize.getWidth()),inserterLocation.y);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(ButtonPanel);
		getContentPane().setVisible(true);
		getContentPane().setName("Vectorr");
		getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(10, 11, 89, 23);
		getContentPane().add(btnNewButton);


	}





	class InserterButtonPanel extends JPanel {




		@Override
		public void paintComponent(Graphics g) {

		}
	}
}
