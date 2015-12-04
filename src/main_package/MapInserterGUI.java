package main_package;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//import main_package.MapUpdaterGUI.UpdateMap;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

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
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Math;


public class MapInserterGUI extends JFrame{
	private static boolean newClick = false;
	private static int lastMousex = 0;
	private static int lastMousey = 0;
	private static int numSelectionPoints = 2;
	//private MapInsertion drawPanel = new MapInsertion();
	private MapInserterGUIButtonPanel buttonPanel;
	private static ArrayList<Point> alignmentPoints;
	private static int cornerNum;
	private int pointSize = 5;
	private boolean remove =false;
	private boolean imageSet = false;
	private BufferedImage CampusMap = null;
	private BufferedImage AddingMap = null;
	private int windowScale = 0;
	private static Point point1 = null;
	private static double Rotation = 0;
	private static double point3x;
	private static double point3y;
	
	Toolkit tk = Toolkit.getDefaultToolkit();
	Dimension screenSize = tk.getScreenSize();
	int screenHeight = screenSize.height;
	int screenWidth = screenSize.width;
	private int windowSizeX = 0;
	private int windowSizeY = 0;
	private boolean alignedProperly = false;
	private static JFrame frame = new JFrame("Add to Campus Map");

	public MapInserterGUI() {
		//super("Add to Campus Map");
		
		createAndShowGUI();	
		cornerNum = 0;
		alignmentPoints = new ArrayList<Point>();
		doRepaint();
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
		//frame.setSize(932, 778);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		double framex = screenWidth/1.5;
		double framey = screenHeight/1.5;
		frame.setSize((int)framex, (int)framey);
		double xlocation = (screenWidth / 2)-(framex/2);
		double ylocation = (screenHeight / 2)-(framey/2);
		frame.setLocation((int)xlocation, (int)ylocation);
		frame.setVisible(true);
		pointFrame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.getContentPane().add(frame);
		//getContentPane().setLayout(null);
		windowSizeX = frame.getContentPane().getWidth();
		windowSizeY = frame.getContentPane().getHeight();
		//System.out.println(frame.getSize());
		buttonPanel = new MapInserterGUIButtonPanel(frame.getLocation(),frame.getSize());
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
	
	public static void clearAlignmentPoints() {
		alignmentPoints.clear();
	}
	
	public static int resetCornerNum(){
		return cornerNum = 0;
	}

	public int getCornerNum()
	{
		return cornerNum;
	}

	public static void doRepaint(){
		frame.repaint();
	}
	
	public static void DisposeFrame(){
		frame.dispose();
	}
	
	public static void GiveMapUpdaterInfo() {
		System.out.println("Told to send info");
		System.out.println("Point 1x: " + point1.getLocX());
		System.out.println("Point 1y: " + point1.getLocY());
		System.out.println("Point 3x: " + point3x);
		System.out.println("Point 3y: " + point3y);
		System.out.println("Rotation: " + Rotation);
		MapUpdaterGUI.setInfo((double) point1.getLocX(), (double) point1.getLocY(), point3x, point3y, Rotation);
		System.out.println("Sending info");
		clearAlignmentPoints();
		resetCornerNum();
	}

	//sets the alignment point on the image with the correct number.
	private void addPoints() {
		// TODO Auto-generated method stub
		Integer numberCorner = getCornerNum();
		
		Point cornerPoint = new Point(numberCorner.toString(), "AlignmentPoint",lastMousex,lastMousey);

		if(alignmentPoints.size() == 0 && !buttonPanel.getSelectedMap().contentEquals("Select Map"))
		{//if there isn't anything in the array, the foreach won't run.
			alignmentPoints.add(getCornerNum(), cornerPoint);
			cornerNum = alignmentPoints.size();
			System.out.println("add Alignment #"+cornerNum +" X: "+cornerPoint.getLocX()+" Y: "+cornerPoint.getLocY());

		}else if(!buttonPanel.getSelectedMap().contentEquals("Select Map")){
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

			if(remove == false && alignmentPoints.size()<numSelectionPoints && !buttonPanel.getSelectedMap().contentEquals("Select Map"))
			{
				System.out.println("Adding a point.");
				alignmentPoints.add(getCornerNum(), cornerPoint);
				cornerNum = alignmentPoints.size();
				System.out.println("add Alignment #"+cornerNum +" X: "+cornerPoint.getLocX()+" Y: "+cornerPoint.getLocY());

				doRepaint();
			}else if(remove == true)
			{
				alignmentPoints.remove(cornerNum);
				remove = false;
				imageSet = false;
			}
		}

		doRepaint();
		newClick =false;
		if(alignmentPoints.size()==numSelectionPoints && !imageSet)
		{
			imageSet = true;
			doRepaint();
			//setImage();
		}
	}	

	private void setImage(){
		
		int answer = JOptionPane.showConfirmDialog(null, "Is the image aligned properly?",
	            "Image Alignment", JOptionPane.YES_NO_OPTION);
		
		switch (answer) {
		case 0:
			imageSet = true;
			doRepaint();
			//yes break and close image inserter
			break;
		case 1:
			//no go back to inserter
			alignmentPoints.clear();
			cornerNum = 0;
			doRepaint();
			break;
		default:
			break;
		}
	}
	
	class PaintFrame extends JPanel {

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponents(g);
			
			
			g.setFont(new Font("arial", Font.BOLD, 16));
			
			// selecting points on the map
			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {

					if (newClick == false) {
						lastMousex = e.getX();
						lastMousey = e.getY();
						newClick = true;
					}
					doRepaint();
				}
			});

			if (newClick == true) {
				addPoints();
			}
			
			
			CampusMap = MapInserterGUIButtonPanel.getCampusMap();
			if (!(CampusMap == null)) {
				// Scale the image to the appropriate screen size
				double wScale;

				if (CampusMap.getHeight() >= CampusMap.getWidth()) {
					wScale = (double) CampusMap.getHeight() / (double) windowSizeY;
					windowScale = CampusMap.getHeight() / windowSizeY;
				}

				else {
					wScale = (double) CampusMap.getHeight() / (double) windowSizeY;
					windowScale = CampusMap.getWidth() / windowSizeX;
				}
				int imagelocationx = (windowSizeX/2)-((int)(CampusMap.getWidth()/wScale)/2);
				int imagelocationy = (windowSizeY/2)-((int)(CampusMap.getHeight()/wScale)/2);
				// sets the correct dimensions for logo
				g.drawImage(CampusMap, imagelocationx, imagelocationy, (int)(CampusMap.getWidth()/wScale), (int)(CampusMap.getHeight()/wScale), null);
			}

			AddingMap = MapInserterGUIButtonPanel.getAddingMap();
			if (!(AddingMap == null) && (!(alignmentPoints == null)) && (alignmentPoints.size() > 1)) {
				if (imageSet){
					point1 = alignmentPoints.get(0);
					Point point2 = alignmentPoints.get(1);
					int ImageHeight = Math.abs((int)Math.sqrt(Math.pow((point1.getLocX()-point2.getLocX()),2)+Math.pow((point1.getLocY()-point2.getLocY()),2)));
					double WidthScale2 = (double)AddingMap.getWidth()/(double)AddingMap.getHeight();
					int ImageWidth = (int) (ImageHeight*WidthScale2);
					
					System.out.println("Original Width " + AddingMap.getWidth());
					System.out.println("Image Width " + ImageWidth);
					//System.out.println("Image Scale Height " + HeightScale);
					//System.out.println("Image Scale Width " + WidthScale);
					Rotation = -Math.atan2(point1.getLocY()-point2.getLocY(), point1.getLocX()-point2.getLocX())-Math.toRadians(90);
					point3y = point2.getLocY() + (-(double)(Math.cos(Math.toRadians(90)-Rotation))*(double)ImageWidth);
					point3x = point2.getLocX() + ((double)(Math.sin(Math.toRadians(90)-Rotation)*(double)ImageWidth));
					
					double HeightScale = Math.abs((double)ImageHeight/(double)AddingMap.getHeight());
					double WidthScale = Math.abs((double)ImageWidth/(double)AddingMap.getWidth());
					
					AffineTransform tx = AffineTransform.getTranslateInstance((point1.getLocX()), (point1.getLocY()));
					tx.rotate(-Rotation);
					tx.scale(WidthScale, HeightScale);
					
					Graphics2D g2d = (Graphics2D) g;
					
					g2d.drawImage(AddingMap, tx, null);
					g.fillOval( (int) point3x, (int) point3y, 5, 5);
				}
				
			}
			if (!(alignmentPoints == null)){
				for (Point currentPoint : alignmentPoints) {
					int drawX = (int) currentPoint.getLocX();
					int drawY = (int) currentPoint.getLocY();
					// draws the points onto the map
					Integer drawNum = Integer.parseInt(currentPoint.getId())+1;
					g.drawString(drawNum.toString(), drawX-pointSize, drawY+pointSize);
					//g.fillOval(drawX - (pointSize / 2), drawY - (pointSize / 2), pointSize, pointSize);
					System.out.println(alignmentPoints.size());
				}
			}
			
		}
	}
}
