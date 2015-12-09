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

public class SwingTest extends JFrame{

	private JFrame frame;
	String labels[] = {"Select a Point", "A", "B", "C"};
	String point1;
	String point2;
	private JTextField textField;

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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
					
		JComboBox<String> comboBox = new JComboBox(labels);
		comboBox.setBounds(107, 33, 201, 20);
		frame.getContentPane().add(comboBox);
		
		JComboBox<String> comboBox_1 = new JComboBox(labels);
		comboBox_1.setBounds(107, 83, 201, 20);
		frame.getContentPane().add(comboBox_1);
		
		textField = new JTextField();
		textField.setBounds(107, 173, 201, 30);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Directions");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				point1 = (String) comboBox.getSelectedItem();
				point2 = (String) comboBox_1.getSelectedItem();
				if (point1 == "Select a Point" || point2 == "Select a Point")
					textField.setText("Error: Select Two Points");
				else
					textField.setText("Point " + point1 + " to Point " + point2);
			}
		});
		
		btnNewButton.setBounds(165, 132, 94, 30);
		frame.getContentPane().add(btnNewButton);
		
	}
}