package main_package;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.*;
import javax.swing.*;

/*
 * This applet allows the user to move a texture painted rectangle around the applet
 * window.  The rectangle flickers and draws slowly because this applet does not use 
 * double buffering.
*/

public class MapUpdater extends JApplet {
    static protected JLabel label;
    DPanel d;

    public void init(){
        getContentPane().setLayout(new BorderLayout());

        d = new DPanel();
        d.setBackground(Color.white);
        getContentPane().add(d);

        label = new JLabel("Click to add point");
        getContentPane().add("South", label);
    }

    public static void main(String s[]) {
        JFrame f = new JFrame("MapUpdater");
        
        f.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent e) {System.exit(0);}});
        
        JApplet applet = new MapUpdater();
        f.getContentPane().add("Center", applet);
        applet.init();
        f.pack();
        f.setSize(new Dimension(550,250));
        f.setVisible(true);
    }

}


class DPanel extends JPanel implements MouseListener, MouseMotionListener{
        //Rectangle rect = new Rectangle(0, 0, 100, 50);
        BufferedImage bi;
        Graphics2D big;
        
        // Holds the coordinates of the user's last mousePressed event.
        int last_x, last_y;
        boolean firstTime = true;
        //TexturePaint fillPolka, strokePolka;
        Rectangle area;                
        // True if the user pressed, dragged or released the mouse outside of
        // the rectangle; false otherwise.
        boolean pressOut = false;   


    public DPanel(){
               setBackground(Color.white);
                addMouseMotionListener(this);
                addMouseListener(this);

/*                // Creates the fill texture paint pattern.
                bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
                big = bi.createGraphics();
                big.setColor(Color.pink);
                big.fillRect(0, 0, 7, 7);
                big.setColor(Color.cyan);
                big.fillOval(0, 0, 3, 3);
                Rectangle r = new Rectangle(0,0,5,5);
                fillPolka = new TexturePaint(bi, r);
                big.dispose();*/
        
/*                //Creates the stroke texture paint pattern.
                bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
                big = bi.createGraphics();
                big.setColor(Color.cyan);
                big.fillRect(0, 0, 7, 7);
                big.setColor(Color.pink);
                big.fillOval(0, 0, 3, 3);
                r = new Rectangle(0,0,5,5);
                strokePolka = new TexturePaint(bi, r);
                big.dispose();*/
    }

    // Handles the event of the user pressing down the mouse button.
    public void mousePressed(MouseEvent e){

          /*  last_x = rect.x - e.getX();
            last_y = rect.y - e.getY();*/

        last_x =  e.getX();
        last_y =  e.getY();
       // Point2D.Double point = new Point2D.Double(last_x, last_y);
        MapUpdater.label.setText("last click = " + last_x + ", " + last_y);
        
        placePoint(last_x, last_y);
        
        
        
    }

    /*
     * @param x x coord of point to be placed
     * @param y y coord of point to be placed.
     * @return void
     */
    private void placePoint(int x, int y) {
		// TODO Auto-generated method stub
		
/*    	if(firstTime)
    	{
    		for(int i=0;i<db.points.length;i++)
    		{
    			//draw all the points on open.
    		}
    		firstTime =false;
    	}*/
    	
    	
    	Point2D.Double point = new Point2D.Double(x,y);
    	System.out.println(point.getX());
		
		//storePoint(last_x, last_y);
		
		
    	
	}

	/*
     * @param x x coord of point placed on map
     * @param y y coord of point placed on map
     * @return db index for point
     */
    private int storePoint(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}

	// Handles the event of a user dragging the mouse while holding
    // down the mouse button.
    public void mouseDragged(MouseEvent e){

    }

    // Handles the event of a user releasing the mouse button.
    public void mouseReleased(MouseEvent e){

    }
       
     // This method is required by MouseListener.
     public void mouseMoved(MouseEvent e){}

     // These methods are required by MouseMotionListener.
     public void mouseClicked(MouseEvent e){}
     public void mouseExited(MouseEvent e){}
     public void mouseEntered(MouseEvent e){}
                         


}