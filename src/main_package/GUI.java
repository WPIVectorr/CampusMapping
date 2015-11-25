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
	private ArrayList<Directions> finalDir = null;
	private int buildStartIndex;
	private int buildDestIndex;
	private Color previousColor = Color.RED;
	private Color currentColor = Color.YELLOW;
	private Color nextColor = Color.GREEN;

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
		navMenu.setBackground(new Color(255, 235, 205));

		GridBagLayout gbl_navMenu = new GridBagLayout();
		gbl_navMenu.columnWidths = new int[]{298, 298, 298, 0};
		gbl_navMenu.rowHeights = new int[]{0, 56, 56, 0};
		gbl_navMenu.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_navMenu.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		navMenu.setLayout(gbl_navMenu);


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
						//ArrayList<Directions> finalDir = null;
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
		GridBagConstraints gbc_btnReturn = new GridBagConstraints();
		gbc_btnReturn.insets = new Insets(0, 0, 5, 5);
		gbc_btnReturn.gridx = 1;
		gbc_btnReturn.gridy = 0;
		navMenu.add(btnReturn, gbc_btnReturn);

		JCheckBox chckbxColorBlindMode = new JCheckBox("Color Blind Mode");
		GridBagConstraints gbc_chckbxColorBlindMode = new GridBagConstraints();
		gbc_chckbxColorBlindMode.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxColorBlindMode.gridx = 2;
		gbc_chckbxColorBlindMode.gridy = 0;
		chckbxColorBlindMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If checkbox is selected, switch to color blind friendly colors
				// Otherwise if it is unselected, switch to default colors
				if (chckbxColorBlindMode.isSelected()){
					previousColor = new Color(219, 209, 0);
					currentColor = new Color(182, 109, 255);
					nextColor = new Color(0, 146, 146);
				}
				else{
					previousColor = Color.RED;
					currentColor = Color.YELLOW;
					nextColor = Color.GREEN;
				}
				frame.repaint();
			}
		});
		navMenu.add(chckbxColorBlindMode, gbc_chckbxColorBlindMode);



		//creates a centered text field that will write back the users info they typed in
		directionsText = new JTextField();
		directionsText.setHorizontalAlignment(JTextField.CENTER);
		directionsText.setToolTipText("");
		directionsText.setBounds(6, 174, 438, 30);
		directionsText.setColumns(1);
		GridBagConstraints gbc_directionsText = new GridBagConstraints();
		gbc_directionsText.gridwidth = 3;
		gbc_directionsText.fill = GridBagConstraints.BOTH;
		gbc_directionsText.insets = new Insets(0, 0, 5, 0);
		gbc_directionsText.gridx = 0;
		gbc_directionsText.gridy = 1;
		navMenu.add(directionsText, gbc_directionsText);

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
				frame.repaint();
			}
		});
		GridBagConstraints gbc_btnPrevious = new GridBagConstraints();
		gbc_btnPrevious.anchor = GridBagConstraints.NORTH;
		gbc_btnPrevious.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPrevious.insets = new Insets(0, 0, 0, 5);
		gbc_btnPrevious.gridx = 0;
		gbc_btnPrevious.gridy = 2;
		navMenu.add(btnPrevious, gbc_btnPrevious);

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
				frame.repaint();
			}
		});
		GridBagConstraints gbc_btnNext = new GridBagConstraints();
		gbc_btnNext.anchor = GridBagConstraints.NORTH;
		gbc_btnNext.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNext.gridx = 2;
		gbc_btnNext.gridy = 2;
		navMenu.add(btnNext, gbc_btnNext);

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
					// Draw green lines for all points up to current point
					g.setColor(nextColor);
					g2.setStroke(new BasicStroke(3));
					for (int i = 0; i < textPos; i++){
						g2.drawLine(finalDir.get(i).getOrigin().getLocX(), finalDir.get(i).getOrigin().getLocY(), finalDir.get(i).getDestination().getLocX(), finalDir.get(i).getDestination().getLocY());
					}
					// Draw a thicker yellow line for the current step in the directions
					g2.setStroke(new BasicStroke(6));
					g.setColor(currentColor);
					g2.drawLine(finalDir.get(textPos).getOrigin().getLocX(), finalDir.get(textPos).getOrigin().getLocY(), finalDir.get(textPos).getDestination().getLocX(), finalDir.get(textPos).getDestination().getLocY());
					g2.setStroke(new BasicStroke(3));

					// Draw red lines for all points until the end
					g.setColor(previousColor);
					for (int i = textPos + 1; i < finalDir.size(); i++){
						g2.drawLine(finalDir.get(i).getOrigin().getLocX(), finalDir.get(i).getOrigin().getLocY(), finalDir.get(i).getDestination().getLocX(), finalDir.get(i).getDestination().getLocY());
					}
				}
			}
		}
	}
}
