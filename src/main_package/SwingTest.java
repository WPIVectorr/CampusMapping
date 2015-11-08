package main_package;
import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.SpringLayout;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import java.awt.FlowLayout;
import javax.swing.JSplitPane;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.JLabel;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import javax.swing.JScrollBar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import java.awt.Color;

public class SwingTest extends JFrame{

	private JFrame frame;
	String buildings[] = {"Select Building", "Atwater Kent", "Boynton Hall", 
			"Campus Center", "Gordon Library", "Higgins House", "Project Center", 
			"Stratton Hall"};
	String rooms[] = {"Select room #", "111", "222", "333", "444"};
	String[] akRooms = {"Select room #", "10", "20", "30", "40"};
	String[] bhRooms = {"Select room #", "11", "21", "31", "41"};
	String[] ccRooms = {"Select room #", "12", "22", "32", "42"};
	String[] glRooms = {"Select room #", "13", "23", "33", "43"};
	String[] hhRooms = {"Select room #", "14", "24", "34", "44"};
	String[] pcRooms = {"Select room #", "15", "25", "35", "45"};
	String[] shRooms = {"Select room #", "16", "26", "36", "46"};
	
	String point1;
	String point2;
	private JTextField textField;
	private JTextField txtStartingLocation;
	private JTextField txtDestination;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingTest window = new SwingTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SwingTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(255, 235, 205));
		frame.setBounds(100, 100, 470, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		
		JComboBox startRooms = new JComboBox(rooms);
		startRooms.setBounds(296, 30, 148, 20);
		frame.getContentPane().add(startRooms);
		
		
		JComboBox<String> startBuilds = new JComboBox(buildings);
		startBuilds.setBounds(122, 30, 148, 20);
		frame.getContentPane().add(startBuilds);
		startBuilds.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	
		    	startRooms.removeAllItems();
		    	String buildSelectStart = (String)startBuilds.getSelectedItem();
		    	startRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectStart)));
		    }
		});
		
		JComboBox destRooms = new JComboBox(rooms);
		destRooms.setBounds(296, 80, 148, 20);
		frame.getContentPane().add(destRooms);
		
		JComboBox<String> destBuilds = new JComboBox(buildings);
		destBuilds.setBounds(122, 80, 148, 20);
		frame.getContentPane().add(destBuilds);
		destBuilds.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	
		    	destRooms.removeAllItems();
		    	String buildSelectDest = (String)destBuilds.getSelectedItem();
		    	destRooms.setModel(new DefaultComboBoxModel(generateRoomNums(buildSelectDest)));
		    }
		});
		


		
		textField = new JTextField();
	    textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setToolTipText("");
		textField.setBounds(6, 174, 438, 30);
		frame.getContentPane().add(textField);
		textField.setColumns(1);
		
		JButton btnNewButton = new JButton("Directions");
		btnNewButton.setBackground(new Color(0, 255, 127));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				point1 = (String) startBuilds.getSelectedItem();
				point2 = (String) destBuilds.getSelectedItem();
				if (point1 == "Select a Point" || point2 == "Select Building")
					textField.setText("Error: Select Two Points");
				else
					textField.setText(point1 + " to  " + point2);
			}
		});
		
		btnNewButton.setBounds(187, 132, 94, 30);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblStartingLocation = new JLabel("Starting Location:");
		lblStartingLocation.setBounds(6, 31, 119, 16);
		frame.getContentPane().add(lblStartingLocation);
		
		JLabel lblDestination = new JLabel("Destination:");
		lblDestination.setBounds(6, 81, 119, 16);
		frame.getContentPane().add(lblDestination);
				
		
		
		}
	
	public String[] generateRoomNums(String select){
		switch (select){
		case "Atwater Kent":
			return akRooms;
		case "Boynton Hall":
			return bhRooms;
		case "Campus Center":
			return ccRooms;
		case "Gordon Library":
			return glRooms;
		case "Higgins House":
			return hhRooms;
		case "Project Center":
			return pcRooms;
		case "Stratton Hall":
			return shRooms;
		}
		return rooms;
	}
}


