package main_package;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import database.AlreadyExistsException;
import database.ServerDB;

public class GUI{
	
	ServerDB md = ServerDB.getInstance();

	private BufferedImage img = null;

	// Array of strings to initally populate dropdown menus with
	String rooms[] = {"Select room #", "Please choose building first"};

	private ArrayList<Map> maps = new ArrayList<Map>();
	private ArrayList<Point> route;
	private String[] textDir;
	private int textPos;
	private int currentStep;
	private Point start;
	private Point end;
	private boolean showRoute;
	private JTextField directionsText;
	private JPanel mainMenu;
	private JPanel navMenu;
	private JPanel menus;
	private DrawRoute drawPanel = new DrawRoute();
	private int windowScale = 2;
	private int windowSizeX = 932;
	private int windowSizeY = 778;

	private int buildStartIndex;
	private int buildDestIndex;

	private JFrame frame = new JFrame("Directions with Magnitude");


	public void createAndShowGUI() throws IOException, AlreadyExistsException, SQLException{
		
		frame.setSize(932, 778);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		maps = md.getMapsFromLocal();


		//System.out.println("------------------edges check-------------------");


		maps.get(0).getPointList().get(0).print();

		mainMenu = new JPanel();
		mainMenu.setLayout(new GridLayout(4, 0, 10, 10));
		mainMenu.setBackground(new Color(255, 235, 205));
		
		navMenu = new JPanel();
		navMenu.setLayout(new GridLayout(2, 3, 10, 10));
		navMenu.setBackground(new Color(255, 235, 205));
		
		menus = new JPanel(new CardLayout());
		menus.add(mainMenu, "Main Menu");
		menus.add(navMenu, "Nav Menu");
		CardLayout menuLayout = (CardLayout) menus.getLayout();
		
		Container contentPane = frame.getContentPane();
		contentPane.add(menus, BorderLayout.NORTH);
		
		/*adds the room numbers based off of building name
        startBuilds.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
            	startRooms.removeAllItems();
            	String buildSelectStart = (String)startBuilds.getSelectedItem();
                buildStartIndex = startBuilds.getSelectedIndex();
                int startRoomSize = maps.get(buildStartIndex).getPointList().size();
                String[] startRoomArray = new String[startRoomSize];
                startRoomArray[0] = "Select a room";
            }
        });*/

		//creates the drop down box with rooms for start (initially waits for the building to have 
		//the specific buildings room numbers)

		//	         buttonPanel.add(startRooms);
		//startRooms.setBounds(296, 30, 148, 20);

		JComboBox<Point> startBuilds = new JComboBox();
		JComboBox<Point> destBuilds = new JComboBox();
		JComboBox mapsDropdown = new JComboBox();


		JLabel lblMaps = new JLabel("Select Map:");
		lblMaps.setHorizontalAlignment(SwingConstants.CENTER);
		mainMenu.add(lblMaps);

		//adds the starting location label to the line with starting location options
		JLabel lblStartingLocation = new JLabel("Starting Location:");
		mainMenu.add(lblStartingLocation);
		lblStartingLocation.setBounds(6, 31, 119, 16);
		mapsDropdown.addItem("Select Map");
		for(int i = 0; i < maps.size(); i++){	
			mapsDropdown.addItem(maps.get(i).getMapName());
		}

		//creates drop down box with building names
		mainMenu.add(startBuilds);
		startBuilds.setBounds(122, 30, 148, 20);



		//creates a dropdown menu with map names
		mainMenu.add(mapsDropdown);

		//adds the destination label to the line with destination location options
		JLabel lblDestination = new JLabel("Destination:");
		mainMenu.add(lblDestination);
		lblDestination.setBounds(6, 68, 85, 44);
		lblDestination.setLabelFor(destBuilds);
		//adds destBuilds to the dropdown for destination
		mainMenu.add(destBuilds);
		destBuilds.setBounds(122, 30, 148, 20);
		/*for (int i=0; i < buildings.length; i++){
			destBuilds.addItem(buildings[i]);
		}*/
		//buttonPanel.add(destBuilds);
		destBuilds.setBounds(122, 80, 148, 20);


		//adds the correct points for the building specified
		mapsDropdown.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				buildDestIndex = mapsDropdown.getSelectedIndex();

				String mapTitle = maps.get(buildDestIndex-1).getMapName();
				//String mapTitle = "AtwaterKent1";
				
				File dest = new File("src/VectorMaps");
				String destInput = dest.getAbsolutePath();
				//assuming all maps saved in vectorMaps are in jpg
				destInput = destInput + "/" + mapTitle + ".jpg";

				File destFile = new File(destInput);
				try{
					img = ImageIO.read(destFile);
					frame.repaint();
				}
				catch(IOException a){
					System.out.println("Could not find file:"+destInput);
					a.printStackTrace();
				}

				System.out.println("DIe die: " +maps.get(0).getPointList().size());
				System.out.println("points: ");
				for(int count = 0; count < maps.get(0).getPointList().size(); count++){
					System.out.println(maps.get(0).getPointList().get(count).getName());
				}
				
				startBuilds.removeAllItems();
				destBuilds.removeAllItems();
				if(buildDestIndex!=0){
					//System.out.println("building size: " + buildings.length);
					for (int i = 0; i < maps.get(buildDestIndex-1).getPointList().size(); i++){
						startBuilds.addItem(maps.get(buildDestIndex-1).getPointList().get(i));
						System.out.println("startBuildsSize: " + maps.get(buildDestIndex-1).getPointList().size());
						//System.out.println("buildings[i] " + buildings[i]);

						// destRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectDest)));
					}
					for (int i = 0; i < maps.get(buildDestIndex-1).getPointList().size(); i++){
						destBuilds.addItem(maps.get(buildDestIndex-1).getPointList().get(i));
						//System.out.println("buildings[i] " + buildings[i]);

						// destRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectDest)));
					}
				}
				//startBuilds.removeAllItems();
				//for (int i=0; i < buildings.length; i++){
				//System.out.println("buildings[i] match: " + buildings[i]);
				//startBuilds.addItem(buildings[i]);
				//}
				//destBuilds.removeAllItems();
				//for (int i=0; i < buildings.length; i++){
				///destBuilds.addItem(buildings[i]);
				//}
			}
		});

		// Empty componenet for formatting
		Component horizontalStrut = Box.createHorizontalStrut(20);
		mainMenu.add(horizontalStrut);

		// Button that generates a route and switches to nav display
		JButton directionsButton = new JButton("Directions");
		mainMenu.add(directionsButton);
		directionsButton.setBackground(new Color(0, 255, 127));
		directionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// reset text position index
				textPos = 0;


				//gets the start and end building and room numbers the user chose

				start = (Point) startBuilds.getSelectedItem();
				end = (Point) destBuilds.getSelectedItem();
				if(!start.equals(end)){



					//System.out.println("--------------------astar--------------------------------");
					//start.print();
					//end.print();
					AStar astar = new AStar();
					astar.reset();
					
					route = astar.PathFind(start, end);
					// Array is stored end to start, so step 1 is actually the last step
					// Also arrays start at 0, so need to subtract 1
					currentStep = route.size() - 1;
					//System.out.println("route variable: " + (route == null));
			
					if(route != null){
						/*System.out.println("route: ");
						for(int i = route.size() - 1; i >= 0; i--){
							System.out.println(route.get(i));
						}*/

					}
					showRoute = true;
					if (route == null){
						directionsText.setText(start.getName() + "->" + end.getName());
					}
					else{
						//System.out.println(route.size());
						GenTextDir gentextdir = new GenTextDir();
						ArrayList<Directions> tempDir = gentextdir.genTextDir(route);
						ArrayList<Directions> finalDir = null;
						try {
							finalDir = gentextdir.generateDirections(tempDir);
						} catch (MalformedDirectionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							textDir = gentextdir.genDirStrings(finalDir);
						} catch (MalformedDirectionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for(int i = 0; i < textDir.length; i++){
							//System.out.println(directions[i]);
						}
						directionsText.setText(textDir[0]);

					}

					frame.repaint();
					menuLayout.show(menus, "Nav Menu");
				}
				//if the points are identical, asks the user to input different points
				else{
					directionsText.setText("Pick two different points");
					frame.repaint();
				}
			}
		});


		directionsButton.setBounds(187, 132, 94, 30);

		// For formatting
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		mainMenu.add(horizontalStrut_1);
		
		// Button to get previous step in directions
		JButton btnPrevious = new JButton("Previous");
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textPos == 0 || textDir == null){

				}
				else{
					textPos--;
					directionsText.setText(textDir[textPos]);
				}
				// Routes are stored end to beginning, so increment to "step backward"
				if(currentStep != route.size() - 1){
					currentStep++;
				}
				frame.repaint();
			}
		});
		navMenu.add(btnPrevious);



		//creates a centered text field that will write back the users info they typed in
		directionsText = new JTextField();
		directionsText.setHorizontalAlignment(JTextField.CENTER);
		directionsText.setToolTipText("");
		directionsText.setBounds(6, 174, 438, 30);
		directionsText.setColumns(1);
		navMenu.add(directionsText);

		// Button to get next step in directions
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(textDir == null || textPos == textDir.length - 1){

				}
				else {
					textPos++;
					directionsText.setText(textDir[textPos]);
				}
				// Routes are stored end to beginning, so decrement to "step forward"
				if(currentStep != 0){
					currentStep--;
				}
				frame.repaint();
			}
		});
		navMenu.add(btnNext);


		Component horizontalStrut2 = Box.createHorizontalStrut(20);
		navMenu.add(horizontalStrut2);
		
		// Button to return to main menu
		JButton btnReturn = new JButton("Select New Route");
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Return to main menu, don't show route anymore
				menuLayout.show(menus, "Main Menu");
				showRoute = false;
				frame.repaint();
			}
		});
		navMenu.add(btnReturn);

		Component horizontalStrut3 = Box.createHorizontalStrut(20);
		navMenu.add(horizontalStrut3);
		
		// Add panel for drawing
		frame.getContentPane().add(drawPanel);
		// Make frame visible after initializing everything
		frame.setVisible(true);
	}



	public static void main(String[] args) throws IOException, AlreadyExistsException, SQLException{
		//GUI myTest = new GUI();
		//myTest.setVisible(true);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				GUI gui = new GUI();
				try {
					gui.createAndShowGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AlreadyExistsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}



	class DrawRoute extends JPanel {

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			double wScale;
			if(img != null){
				if (img.getHeight() >= img.getWidth()) {
					wScale = (double) img.getHeight() / (double) windowSizeY;
					windowScale = img.getHeight() / windowSizeY;
				} else {
					wScale = (double) img.getHeight() / (double) windowSizeY;
					windowScale = img.getWidth() / windowSizeX;
				}
				if (wScale > windowScale)
					windowScale += 1;

				g.drawImage(img, 0, 0, img.getWidth() / windowScale, img.getHeight() / windowScale, null);


				if (showRoute && route != null){
					// Draw green lines for all points up to currentStep point
					g.setColor(Color.GREEN);
					g2.setStroke(new BasicStroke(3));
					for (int i = currentStep + 1; i < route.size(); i++){
						g2.drawLine(route.get(i).getLocX(), route.get(i).getLocY(), route.get(i-1).getLocX(), route.get(i-1).getLocY());


					}
					// Special case for currentStep = 0, don't draw line or array out of bounds error
					// Otherwise, draw yellow line from currentStep point to previous point
					if (currentStep != 0){
						g2.setStroke(new BasicStroke(6));
						g.setColor(Color.YELLOW);
						g2.drawLine(route.get(currentStep).getLocX(), route.get(currentStep).getLocY(), route.get(currentStep-1).getLocX(), route.get(currentStep-1).getLocY());
						g2.setStroke(new BasicStroke(3));
					}
					// Draw red lines for rest of points
					g2.setColor(Color.RED);
					for (int i = 0; i < currentStep - 1; i++){
						g2.drawLine(route.get(i).getLocX(), route.get(i).getLocY(), route.get(i+1).getLocX(), route.get(i+1).getLocY());
					}
				}
			}
		}
	}
}
