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
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
	//private MapInsertion frame = new MapInsertion();
	private MapInserterGUIButtonPanel buttonPanel;
	private static ArrayList<Point> alignmentPoints;
	private static int cornerNum;
	private int pointSize = 5;
	private boolean remove =false;
	private boolean imageSet = false;
	private static BufferedImage CampusMap = null;
	private BufferedImage AddingMap = null;
	private static double windowScale = 0;
	private static Point point1 = null;
	private static double Rotation = 0;
	private static double point3x;
	private static double point3y;
	private double scaleSize = 1;
	private int drawnposx;
	private int drawnposy;
	private boolean drawnfirst = false;
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
	private boolean scrolled = false;
	
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
		
		pointFrame.addMouseMotionListener(new MouseMotionListener(){
			public void mouseDragged(MouseEvent g){
				//System.out.println("dragged");
				drawnfirst = true;
				Dragged = true;
				mousex = g.getX();
				mousey = g.getY();
				frame.repaint();
			}

			public void mouseMoved(MouseEvent arg0) {

			}
		});
		
		pointFrame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				originx = e.getX();
				originy = e.getY();
				//System.out.println(originx+", "+originy);
			}
			public void mouseReleased(MouseEvent e) {
				if (!Dragged){
					if (newClick == false) {
						if(buttonPanel.isSecondMapSelect())
						{
							lastMousex = e.getX();
							lastMousey = e.getY();
							newClick = true;
						}
					}
					doRepaint();
				} else{
					//System.out.println("dragged = true");
					Dragged = false;
					drawnfirst = true;
				}
			}
		});

		frame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){
			 
            @Override
            public void ancestorMoved(HierarchyEvent e) {
                           
            }
            @Override
            public void ancestorResized(HierarchyEvent e) {
                frame.repaint();
            }           
        });
		
		

		frame.addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e) {
				scrolled = true;
				String message;
				int notches = e.getWheelRotation();
				if (notches < 0) {
					message = "Mouse wheel moved UP " + -notches + " notch(es)\n";
				} else {
					message = "Mouse wheel moved DOWN " + notches + " notch(es)\n";
				}
				double oldWidth = CampusMap.getWidth() * scaleSize;
				double oldHeight = CampusMap.getHeight() * scaleSize;
				if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL && !(buttonPanel.getSelectedMap().contentEquals("Select Map"))) {
					drawnfirst = true;
					scroldirection = e.getWheelRotation();
					if (e.getWheelRotation() > 0) {
						if (scaleSize <= 2) {
							// System.out.println("scale before plus: " +
							// scaleSize);
							scaleSize += (e.getWheelRotation() * .01);
							// System.out.println("scale plus: " + scaleSize);
							atMinZoom = false;
						} else {
							atMaxZoom = true;
						}
					} else {
						if (scaleSize >= 0.1) {
							// System.out.println("scale before minus: " +
							// scaleSize);
							scaleSize += (e.getWheelRotation() * .01);
							// System.out.println("scale minus: " + scaleSize);
							atMaxZoom = false;
						} else {
							atMinZoom = true;
						}
					}
					double newWidth = CampusMap.getWidth() * scaleSize;
					double newHeight = CampusMap.getHeight() * scaleSize;
					difWidth = (oldWidth - newWidth);
					difHeight = (oldHeight - newHeight);
				} else { // scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL

				}
				frame.repaint();
				// System.out.println(message);
			}
		});

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
		/*System.out.println("Told to send info");
		System.out.println("Point 1x: " + point1.getLocX());
		System.out.println("Point 1y: " + point1.getLocY());
		System.out.println("Point 3x: " + point3x);
		System.out.println("Point 3y: " + point3y);
		System.out.println("Rotation: " + Rotation);
		System.out.println("Window Scale is: " + windowScale);*/
		double point1LocalX = ((double)point1.getLocX() * windowScale) / (double)CampusMap.getWidth();
		double point1LocalY = ((double)point1.getLocY() * windowScale) / (double)CampusMap.getHeight();
		double point3LocalX = (double)point3x / (double)CampusMap.getWidth();
		double point3LocalY = (double)point3y / (double)CampusMap.getHeight();
		MapUpdaterGUI.setInfo(point1LocalX, point1LocalY, point3LocalX, point3LocalY, Rotation);
		System.out.println("Sending info");
		clearAlignmentPoints();
		resetCornerNum();
	}

	//sets the alignment point on the image with the correct number.
	private void addPoints() {
		// TODO Auto-generated method stub
		Integer numberCorner = getCornerNum();
		double LocalX = (lastMousex-drawnposx)/newImageWidth;
		double LocalY = (lastMousey-drawnposy)/newImageHeight;
		Point cornerPoint = new Point(numberCorner.toString(), "AlignmentPoint",LocalX,LocalY);

		if(alignmentPoints.size() == 0 && !buttonPanel.getSelectedMap().contentEquals("Select Map"))
		{//if there isn't anything in the array, the foreach won't run.
			alignmentPoints.add(getCornerNum(), cornerPoint);
			cornerNum = alignmentPoints.size();
			//System.out.println("add Alignment #"+cornerNum +" X: "+cornerPoint.getLocX()+" Y: "+cornerPoint.getLocY());

		}else if(!buttonPanel.getSelectedMap().contentEquals("Select Map")){
			for (Point currentPoint : alignmentPoints) {
				int point1x = (int)((currentPoint.getLocX()*newImageWidth)+drawnposx);
				int point1y = (int)((currentPoint.getLocY()*newImageHeight)+drawnposy);
				if ((lastMousex > point1x - (pointSize + 5)
						&& lastMousex < point1x + (pointSize + 5))
						&& (lastMousey > point1y - (pointSize + 5)
								&& lastMousey < point1y + (pointSize + 5)))
				{
					//System.out.println("remove");
					cornerNum = alignmentPoints.indexOf(currentPoint);
					remove =true;
					break;
				}
			}

			if(remove == false && alignmentPoints.size()<numSelectionPoints && !buttonPanel.getSelectedMap().contentEquals("Select Map"))
			{
				//System.out.println("Adding a point.");
				alignmentPoints.add(getCornerNum(), cornerPoint);
				cornerNum = alignmentPoints.size();
				//System.out.println("add Alignment #"+cornerNum +" X: "+cornerPoint.getLocX()+" Y: "+cornerPoint.getLocY());

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
	public static void setFrameSize(int width, int height)
	{
		//System.out.println("setSize");
		frame.setSize(width, height);
		frame.validate();
	}

	class PaintFrame extends JPanel {

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponents(g);
			
			
			g.setFont(new Font("arial", Font.BOLD, 16));

			if (newClick == true) {
				addPoints();
			}
			
			
			CampusMap = MapInserterGUIButtonPanel.getCampusMap();

			if (!(CampusMap == null)) {
				if(!buttonPanel.getSelectedMap().contentEquals("Select Map")){
					// Scale the image to the appropriate screen size
					if (drawnfirst == false){
						windowScale = ((double)CampusMap.getWidth() / (double)frame.getContentPane().getWidth());
						scaleSize = 1/((double)CampusMap.getWidth() / (double)frame.getWidth());
						//System.out.println("setting: "+scaleSize);
						//System.out.println("Image Original Width " + CampusMap.getWidth());
						int WidthSize = (int)((double) CampusMap.getHeight() / windowScale);
						if (WidthSize > (double)frame.getHeight()){
							windowScale = (double)CampusMap.getHeight() / (double)frame.getContentPane().getHeight();
							scaleSize = 1/((double)CampusMap.getHeight() / (double)frame.getHeight());
							//System.out.println("setting: "+scaleSize);
						}
						newImageWidth = (int)((double)CampusMap.getWidth() / windowScale);
						newImageHeight = (int)((double)CampusMap.getHeight() / windowScale);
						int centerx = (frame.getWidth()/2);
						int centery = (frame.getHeight()/2);
						drawnposx = centerx -(int)(newImageWidth/2);
						drawnposy = centery -(int)(newImageHeight/2);
						g.drawImage(CampusMap, drawnposx, drawnposy, (int)newImageWidth, (int)newImageHeight, null);
						//System.out.println(newImageWidth+", "+newImageHeight);
					} else{
						double deltax = 0;
						double deltay = 0;
						newImageHeight = (int)CampusMap.getHeight()*scaleSize;
						newImageWidth = (int)CampusMap.getWidth()*scaleSize;
							if(Dragged){
								deltax = -(originx - mousex);
								deltay = -(originy - mousey);
								originx = mousex;
								originy = mousey;
								difWidth = 0;
								difHeight = 0;
							} else if(scrolled){
								deltax = difWidth;
								deltay = difWidth;
								scrolled = false;
							}
							drawnposx += deltax;
							drawnposy += deltay;
							System.out.println(drawnposx+", "+drawnposy);
							g.drawImage(CampusMap, drawnposx, drawnposy, (int)newImageWidth, (int)newImageHeight, null);
					}
				}
			}

			AddingMap = MapInserterGUIButtonPanel.getAddingMap();

			
			if (!(AddingMap == null) && (!(alignmentPoints == null)) && (alignmentPoints.size() > 1)) {
				if (imageSet){
					point1 = alignmentPoints.get(0);
					Point point2 = alignmentPoints.get(1);
					int point1x = (int)((point1.getLocX()*newImageWidth)+drawnposx);
					int point1y = (int)((point1.getLocY()*newImageHeight)+drawnposy);
					int point2x = (int)((point2.getLocX()*newImageWidth)+drawnposx);
					int point2y = (int)((point2.getLocY()*newImageHeight)+drawnposy);
					int ImageHeight = Math.abs((int)Math.sqrt(Math.pow((point1x-point2x),2)+Math.pow((point1y-point2y),2)));
					double WidthScale2 = (double)AddingMap.getWidth()/(double)AddingMap.getHeight();
					int ImageWidth = (int) (ImageHeight*WidthScale2);
					
					System.out.println("Original Width " + AddingMap.getWidth());
					System.out.println("Image Width " + ImageWidth);
					System.out.println("Window scale is: " + windowScale);
					//System.out.println("Image Scale Height " + HeightScale);
					//System.out.println("Image Scale Width " + WidthScale);
					Rotation = -Math.atan2(point1y-point2y, point1x-point2x)-Math.toRadians(90);
					point3y = point2y + (-(double)(Math.cos(Math.toRadians(90)-Rotation))*(double)ImageWidth);
					point3x = point2x + ((double)(Math.sin(Math.toRadians(90)-Rotation))*(double)ImageWidth);
					point3y = point3y * windowScale;
					point3x = point3x * windowScale;

					double HeightScale = Math.abs((double)ImageHeight/(double)AddingMap.getHeight());
					double WidthScale = Math.abs((double)ImageWidth/(double)AddingMap.getWidth());
					
					AffineTransform tx = AffineTransform.getTranslateInstance((point1x), (point1y));
					System.out.println("rotation: "+Rotation);
					tx.rotate(-Rotation);
					tx.scale(WidthScale, HeightScale);
					
					Graphics2D g2d = (Graphics2D) g;
					
					g2d.drawImage(AddingMap, tx, null);
					g.fillOval( (int) point3x, (int) point3y, 5, 5);
				}
				
			}
			if (!(alignmentPoints == null)){
				for (Point currentPoint : alignmentPoints) {
					int point1x = (int)((currentPoint.getLocX()*newImageWidth)+drawnposx);
					int point1y = (int)((currentPoint.getLocY()*newImageHeight)+drawnposy);
					int drawX = (int) point1x;
					int drawY = (int) point1y;
					// draws the points onto the map
					Integer drawNum = Integer.parseInt(currentPoint.getId())+1;
					g.drawString(drawNum.toString(), drawX-pointSize, drawY+pointSize);
					//g.fillOval(drawX - (pointSize / 2), drawY - (pointSize / 2), pointSize, pointSize);
					//System.out.println(alignmentPoints.size());
				}
			}
			
		}
	}
}