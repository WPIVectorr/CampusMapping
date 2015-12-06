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
import java.awt.Insets;

public class SplashPage extends JWindow {
private static SplashPage testSplash;
    public static void main(String[] args) {
    	try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        testSplash = new SplashPage();
        testSplash.showSplash();
        

        testSplash.hideSplash(5000);
        testSplash.setVisible(false);

    }

	private ImageIcon loadingIcon;

    public SplashPage() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	new ResourceLoader().execute();
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                
            }
        });
    	//showSplash();
    }

    public void showSplash() {


        

        // Display it


    }
    public class ResourceLoader extends SwingWorker<Object, Object> {

        @Override
        protected Object doInBackground() throws Exception {
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
            GridBagLayout gridBagLayout = new GridBagLayout();
            gridBagLayout.columnWidths = new int[]{850, 0};
            gridBagLayout.rowHeights = new int[]{45, 546, 34, 0};
            gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
            gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
            getContentPane().setLayout(gridBagLayout);
            
            JLabel label = new JLabel(loadingIcon);
            
                    GridBagConstraints gbc_label = new GridBagConstraints();
                    gbc_label.fill = GridBagConstraints.BOTH;
                    gbc_label.gridheight = 3;
                    gbc_label.gridx = 0;
                    gbc_label.gridy = 0;
                    content.add(label, gbc_label);
                    
                         label.setLayout(new GridBagLayout());
                         
             
             setVisible(true);
             System.out.println("makesplashvisible");
             toFront();

			return null;
  
        }
        
        
      @Override
        protected void done() {
            //hideSplash();
        }


    }
    public void hideSplash(long delay)
    {
    	System.out.println("makeSplashInvisible delay: "+delay);
    	try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	setVisible(false);    	
    	dispose();
    }
}