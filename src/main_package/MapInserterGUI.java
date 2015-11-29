package main_package;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import main_package.MapUpdaterGUI.UpdateMap;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Panel;
import java.awt.RenderingHints;
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
	private int pointSize = 5;
	private boolean remove =false;

	public MapInserterGUI() {
		// TODO Auto-generated constructor stub
		super("MapInserterGUI");
		setSize(932, 778);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(drawPanel);
		//getContentPane().setLayout(null);


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

		//sets the alignment point on the image with the correct number.
		private void addPoints() {
			// TODO Auto-generated method stub
			Point cornerPoint = new Point("align-corner", "AlignmentPoint",lastMousex,lastMousey);
			System.out.println("add Alignment #"+cornerNum +" X: "+cornerPoint.getLocX()+" Y: "+cornerPoint.getLocY());

			if(alignmentPoints.size() == 0)
			{//if there isn't anything in the array, the foreach won't run.
				alignmentPoints.add(getCornerNum(), cornerPoint);
				cornerNum = alignmentPoints.size();
			}else{
				for (Point currentPoint : alignmentPoints) {
					System.out.println("looping");
					if ((lastMousex > currentPoint.getLocX() - (pointSize + 5)
							&& lastMousex < currentPoint.getLocX() + (pointSize + 5))
							&& (lastMousey > currentPoint.getLocY() - (pointSize + 5)
									&& lastMousey < currentPoint.getLocY() + (pointSize + 5)))
					{
						System.out.println("remove");
						cornerNum = alignmentPoints.indexOf(currentPoint);
						remove =true;
						break;
					}
				}
					
				if(remove == false)
				{
					System.out.println("Adding a point.");
					alignmentPoints.add(getCornerNum(), cornerPoint);
					cornerNum = alignmentPoints.size();
				}else if(remove == true)
				{
					alignmentPoints.remove(cornerNum);
					remove = false;
				}
			}
			repaint();
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
				addPoints();

				for(Point currentPoint: alignmentPoints) {
					System.out.println("paint");
					int drawX = (int) currentPoint.getLocX();
					int drawY = (int) currentPoint.getLocY();
					System.out.println(drawY);
					g.drawString("An",drawX,drawY);

				}
				repaint();
			}		



		}
	}
}
