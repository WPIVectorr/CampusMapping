package main_package;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import main_package.MapUpdaterGUI.UpdateMap;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class MapInserterGUI extends JFrame{
	private static boolean newClick = false;
	private static int lastMousex = 0;
	private static int lastMousey = 0;
	private MapInsertion drawPanel = new MapInsertion();
	private ArrayList<Point> alignmentPoints;
	private int cornerNum;
	
	public MapInserterGUI() {
		// TODO Auto-generated constructor stub
		super("MapInserterGUI");
		setSize(932, 778);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(drawPanel);
		
		
		cornerNum = 0;
		alignmentPoints = new ArrayList<Point>();
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MapInserterGUI insertMap = new MapInserterGUI();
		insertMap.setVisible(true);
	}
	
	/**
	 * @return the alignmentPoints
	 */
	public ArrayList<Point> getAlignmentPoints() {
		return alignmentPoints;
	}

	public int getCornerNum()
	{
		return cornerNum;
	}
	

	class MapInsertion extends JPanel {
		
		
		private void addAlignment() {
			// TODO Auto-generated method stub
			Point cornerPoint = new Point("align-corner", "AlignmentPoint",lastMousex,lastMousey);
			System.out.println("add Alignment #"+cornerNum );
			
			alignmentPoints.add(getCornerNum(), cornerPoint);
			repaint();
			
			cornerNum++;
			newClick =false;
		}	
		
		

			@Override
			public void paintComponent(Graphics g) {
		
				super.paintComponent(g);
			


			
			
			
				//selecting points on the map
				addMouseListener(new MouseAdapter() {
					public void mouseReleased(MouseEvent e) {
						
						if(newClick == false)
						{
							lastMousex = e.getX();
							lastMousey = e.getY();
							newClick = true;
						}	
							
						repaint();
					}
				});
				
				
				if(newClick == true)
				{
					addAlignment();
				}
				
				
				
		}
	}
}
