package main_package;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

public class GradientButton extends JButton{
	private Color baseColor;

    public GradientButton(String text, Color baseColor)
    {
    	super(text);
        this.baseColor = baseColor;

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

        // Draws a round rectangle with gradient color specified previously
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);

        // Draws a black border around the rectangle 
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

        super.paintComponent(g);
    }
}