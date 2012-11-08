package br.ufabc.bassani.jpct.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class Texto2D
{
    public String text, fontName;
    public int fontSize, fontStyle;
    public int borderWidth = 10;
    public float alpha;
    public Color fgColor, bgColor;
    
    public Font myFont;
    
    private Color defBGColor = Color.DARK_GRAY;
    private Color defFGColor = Color.YELLOW;
    
    public Texto2D( String txt )
    {
        text = txt;
        fontName = "Dialog";
        fontSize = 16;
        borderWidth = 5;
        alpha = 0.7f;
        
        fgColor = defFGColor;
        bgColor = defBGColor;
        
        createFont();
    }
    public Texto2D( String txt, String fName, int fSize, int fStyle, int bW, float a )
    {
        text = txt;
        fontName = fName;
        fontSize = fSize;
        fontStyle = fStyle;
        
        borderWidth = bW;
        
        alpha = a;
        
        fgColor = Color.YELLOW;
        bgColor = Color.DARK_GRAY;
        
        createFont();
    }    
    
    public void render( Graphics2D g, int x, int y )
    {
        Graphics2D g2d = ( Graphics2D )g.create();
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                              RenderingHints.VALUE_ANTIALIAS_ON );
        g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                              RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        Composite oldComposite = g2d.getComposite();
        g2d.setComposite( AlphaComposite.SrcOver.derive( alpha ) );
        g2d.setColor( bgColor );
        g2d.setFont( myFont );
        TextLayout tl =
            new TextLayout( text, myFont, g2d.getFontRenderContext() );
        Rectangle2D bounds = tl.getBounds();
        int tw = (int)bounds.getWidth();
        int th = (int)bounds.getHeight();
        g2d.fillRoundRect( x, y, tw + (borderWidth * 2), th + (borderWidth * 2), 10, 10 );
        g2d.setComposite( oldComposite );
        g2d.setColor( fgColor );
        tl.draw( g2d, x + borderWidth, y + borderWidth +  th - tl.getDescent() );
        g2d.dispose();
    }
    
    public void setFont( String fName, int fSize, int fStyle )
    {
        fontName = fName;
        fontSize = fSize;
        fontStyle = fStyle;
        createFont();
    }
    
    public void setFont( String fName, int fSize, int fStyle, Color c )
    {
        fgColor = c;
        
        fontName = fName;
        fontSize = fSize;
        fontStyle = fStyle;
        createFont();
    }
    
    public void setText( String t )
    {
        text = t;
    }
    
    public void setColors( Color fgC, Color bgC )
    {
        fgColor = fgC;
        bgColor = bgC;
    }
    
    public void setBorder( int w, Color c, float a )
    {
        borderWidth = w;
        bgColor = c;
        alpha = a;
    }
    
    public void setBorderWidth( int w )
    {
        borderWidth = w;
    }
    
    
    private void createFont()
    {
        myFont = new Font( fontName, fontStyle, fontSize );
    }

}
