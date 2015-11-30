package main_package;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import main_package.MapUpdaterGUI.UpdateMap;

import java.awt.BorderLayout;
import java.awt.Container;
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


public class MapInserterGUI extends JFrame{
	private static boolean newClick = false;
	private static int lastMousex = 0;
	private static int lastMousey = 0;
	//private MapInsertion drawPanel = new MapInsertion();
	private MapInserterGUIButtonPanel buttonPanel;
	private ArrayList<Point> alignmentPoints;
	private int cornerNum;
	private int pointSize = 5;
	private boolean remove =false;
	private boolean imageSet = false;
	
	private JFrame frame= new JFrame("Add to Campus Map");;

	public MapInserterGUI() {
		//super("Add to Campus Map");
		
		createAndShowGUI();	
	
		cornerNum = 0;
		alignmentPoints = new ArrayList<Point>();
		frame.repaint();
	}

	private void createAndShowGUI()
	{
		
		//frame.add(pointFrame);
		PaintFrame pointFrame = new PaintFrame();
		//Container contentPane = frame.getContentPane();
		frame.getContentPane().add(pointFrame);
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(MapInserterGUI.class.getResource("/VectorLogo/VectorrLogo.png")));
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
		frame.setSize(932, 778);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		frame.setSize(screenWidth / 2, screenHeight / 2);
		frame.setLocation(screenWidth / 4, screenHeight / 4);
		frame.setVisible(true);
		pointFrame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.getContentPane().add(frame);
		//getContentPane().setLayout(null);

		MapInserterGUIButtonPanel buttonPanel = new MapInserterGUIButtonPanel(frame.getLocation(),frame.getSize());
		buttonPanel.setVisible(true);

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MapInserterGUI insertMap = new MapInserterGUI();
		//insertMap.setVisible(true);
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

	

	//sets the alignment point on the image with the correct number.
	private void addPoints() {
		// TODO Auto-generated method stub
		Integer numberCorner = getCornerNum();
		
		Point cornerPoint = new Point(numberCorner.toString(), "AlignmentPoint",lastMousex,lastMousey);
		System.out.println("add Alignment #"+cornerNum +" X: "+cornerPoint.getLocX()+" Y: "+cornerPoint.getLocY());

		if(alignmentPoints.size() == 0)
		{//if there isn't anything in the array, the foreach won't run.
			alignmentPoints.add(getCornerNum(), cornerPoint);
			cornerNum = alignmentPoints.size();
		}else {
			for (Point currentPoint : alignmentPoints) {
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
				
			if(remove == false && alignmentPoints.size()<=4)
			{
				System.out.println("Adding a point.");
				alignmentPoints.add(getCornerNum(), cornerPoint);
				cornerNum = alignmentPoints.size();
				frame.repaint();

				if(alignmentPoints.size()==4 && !imageSet)
					setImage();
			}else if(remove == true)
			{
				alignmentPoints.remove(cornerNum);
				remove = false;
				imageSet = false;
			}
		}
		frame.repaint();
		newClick =false;
	}	

	private void setImage(){
		
		int answer = JOptionPane.showConfirmDialog(null, "Is the image aligned properly?",
	            "Image Alignment", JOptionPane.YES_NO_OPTION);
		
		switch (answer) {
		case 0:
			imageSet = true;
			//yes break and close image inserter
			break;
		case 1:
			//no go back to inserter
			break;
		default:
			break;
		}
	}
	
	class PaintFrame extends JPanel {

		@Override
		public void paintComponent(Graphics g) {
	
			super.paintComponents(g);
			
			//selecting points on the map
			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
	
					if(newClick == false)
					{
						lastMousex = e.getX();
						lastMousey = e.getY();
						newClick = true;
					}	
					frame.repaint();
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
					g.drawString(currentPoint.getId(),drawX,drawY);
				}
			}			
		}
	}
}
