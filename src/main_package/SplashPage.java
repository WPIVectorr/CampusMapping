package main_package;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class SplashPage extends JWindow implements Runnable {


	private static SplashPage testSplash;
	private Thread t;
	private String threadName;
	private ImageIcon loadingIcon;


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testSplash = new SplashPage("Splash Thread");
		testSplash.showStaticSplash();


		testSplash.hideSplash(5000);

	}


	public SplashPage(String t) {
		threadName = t;

		/*
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
        });*/
		//showSplash();
	}

	public SplashPage()
	{
		
		
	}
	public void showSplash() {
		if(t == null)
		{
			t= new Thread (this, threadName);
			t.setPriority(4);
			t.start();

		}



		// Display it


	}
	/*    public class ResourceLoader extends SwingWorker<Object, Object> {

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


    }*/
	public void hideSplash(long delay)
	{
		System.out.println("Make Splash Invisible delay: "+delay);
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setVisible(false);    	
		dispose();
	}

	public void showStaticSplash()
	{
		// TODO Auto-generated method stub
		JPanel content = (JPanel) getContentPane();
		//content.setBackground(Color.blue);

		// Set the window's bounds, centering the window
		int width = 850;
		int height = 625;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - width) / 2;
		int y = (screen.height - height) / 2;
		setBounds(x, y, width, height);
		loadingIcon = new ImageIcon(getClass().getResource("/VectorLogo/VectorrLogo-transparent.png"));
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

		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
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


	}
}