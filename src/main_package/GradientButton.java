package main_package;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.UIManager.LookAndFeelInfo;

public class GradientButton extends JButton{
	private Color baseColor;

    public GradientButton(String text, Color baseColor)
    {
    	super(text);
        this.baseColor = baseColor;
        this.putClientProperty( "JButton.buttonType", "textured" );
        this.putClientProperty("JComponent.background", baseColor);
        //Border emptyBorder = BorderFactory.createSoftBevelBorder(BevelBorder.LOWERED, baseColor, baseColor);
        this.setColor(baseColor);
        setContentAreaFilled(false);
    }
    
    public void setColor(Color newColor){
    	baseColor = newColor;
    	repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {


        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Sets gradient paint
        GradientPaint gp = new GradientPaint(0, 0, baseColor.brighter(), 0, getHeight(), baseColor.darker());

        g2d.setPaint(gp);
        //if(System.getProperty("os.name").toLowerCase().startsWith("mac"))
        //{
		    // Draws a round rectangle with gradient color specified previously
		    g2d.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, 5, 5);
		
		    // Draws a black border around the rectangle 
		    g2d.setColor(Color.BLACK);
		    //g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
        //}
        super.paintComponent(g);
    }
}