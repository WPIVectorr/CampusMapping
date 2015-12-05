package main_package;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SplashPage extends JWindow {

    public static void main(String[] args) {
    	try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        new SplashPage();
    }

	private ImageIcon loadingIcon;

    public SplashPage() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                showSplash();
                
            }
        });
    }

    public void showSplash() {

        JPanel content = (JPanel) getContentPane();
        //content.setBackground(Color.blue);

        // Set the window's bounds, centering the window
        int width = 850;
        int height = 625;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);
        loadingIcon = new ImageIcon("src/VectorLogo/animation.gif");
        // Build the splash screen
        JLabel label = new JLabel(loadingIcon);
        label.setBounds(0, 0, 850, 625);
        JLabel copyrt = new JLabel("Vectorr Solutions", JLabel.CENTER);
        getContentPane().setLayout(null);
        
        JButton btnCancelOpen = new JButton("Cancel Operation");
        btnCancelOpen.setBounds(717, 591, 123, 23);
        btnCancelOpen.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		hideSplash();
        	}
        });
        
        JButton btnX = new JButton("X");
        btnX.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		hideSplash();
        	}
        });
        btnX.setBounds(801, 11, 39, 34);
        getContentPane().add(btnX);
        getContentPane().add(btnCancelOpen);

        content.add(label);

        label.setLayout(new GridBagLayout());
        Font font = copyrt.getFont();
        copyrt.setFont(font.deriveFont(Font.BOLD, 24f));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        //label.add(copyrt, gbc);





        // Display it
        setVisible(true);
        toFront();

    }
    public void hideSplash()
    {
    	setVisible(false);    	
    	dispose();
    }
}