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
			"Campus Center", "Gordon Libraray", "Higgins House", "Project Center", 
			"Stratton Hall"};
	String rooms[] = {"Select room #", "111", "222", "333", "444"};
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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
					
		JComboBox<String> comboBox = new JComboBox(buildings);
		comboBox.setBounds(122, 30, 148, 20);
		frame.getContentPane().add(comboBox);
		
		JComboBox<String> comboBox_1 = new JComboBox(buildings);
		comboBox_1.setBounds(122, 80, 148, 20);
		frame.getContentPane().add(comboBox_1);
		
		JComboBox comboBox_2 = new JComboBox(rooms);
		comboBox_2.setBounds(296, 30, 148, 20);
		frame.getContentPane().add(comboBox_2);
		
		JComboBox comboBox_3 = new JComboBox(rooms);
		comboBox_3.setBounds(296, 80, 148, 20);
		frame.getContentPane().add(comboBox_3);
		
		textField = new JTextField();
		textField.setBounds(-13, 176, 463, 30);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Directions");
		btnNewButton.setBackground(new Color(0, 255, 127));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				point1 = (String) comboBox.getSelectedItem();
				point2 = (String) comboBox_1.getSelectedItem();
				if (point1 == "Select a Point" || point2 == "Select Building")
					textField.setText("Error: Select Two Points");
				else
					textField.setText("Point " + point1 + " to Point " + point2);
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
}


