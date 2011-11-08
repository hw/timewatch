package sg.hw.bb.simpleclock;

import java.util.Calendar;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class SimpleClockScreen extends MainScreen
{
	final double HOUR_MARKER_LEN = 0.1;
	final double MINUTE_MARKER_LEN = 0.05;
	final double DIGIT_OFFSET = 0.2;
	
	int[]   handsColor    = new int[] { Color.AZURE, Color.ANTIQUEWHITE, Color.AZURE };
	int[]   handsTipColor = new int[] { Color.RED, Color.AZURE, Color.RED };
	float[] handsLength   = new float[] { 0.6f, 0.8f, 1.0f }; 
	
	private Bitmap cacheBitmap = null;
	private int cachedWidth = 0;
	private int cachedHeight = 0;
	
    /**
     * Creates a new TimeSignalFrame object
     */
    public SimpleClockScreen()
    {        
    	// Set the displayed title of the screen       
        setTitle("Simple Clock");
 //       addMenuItem(toggleMinuteSignalMenuItem);
    }
    
    protected void paint(Graphics graphics)
    {
    	int 	width = Display.getWidth();
    	int 	height = Display.getHeight();
    	int 	midX = width/2;
    	int 	midY = (height-25)/2;
    	int 	radius = (midX < midY) ? midX-1 : midY-1 ;
    	Bitmap bitmap;
    	
    	if (cacheBitmap == null || width != cachedWidth || height != cachedHeight)
    	{
    		cacheBitmap = new Bitmap(width, height);
    		cachedWidth = width;
    		cachedHeight = height;
    	}    	
    	bitmap = cacheBitmap;
    	
    	Graphics g = Graphics.create(bitmap);
    	g.setBackgroundColor(0x000000);
    	g.clear();
    	
    	g.setColor(0xFFFFFF);
    	g.drawEllipse(midX, midY, midX+radius, midY, midX, midY+radius, 0, 360);
    	g.drawEllipse(midX, midY, midX+4, midY, midX, midY+4, 0, 360);

    	for (int n = 0; n < 60; ++n)
    	{
    		double angle = ((double)n/60.0f)*2.0f*Math.PI;
    		double cAngle = Math.cos(angle);
    		double sAngle = Math.sin(angle);
    		int x1 = (int)(midX + radius*sAngle);
    		int y1 = (int)(midY - radius*cAngle);
    		int x2 = (int)(midX + (1-MINUTE_MARKER_LEN)*radius*sAngle);
    		int y2 = (int)(midY - (1-MINUTE_MARKER_LEN)*radius*cAngle);    		
    		g.drawLine(x1, y1, x2, y2);
    	}
    	
    	for (int n = 0; n < 12; ++n)
    	{
    		double angle = ((double)n/12.0f)*2.0f*Math.PI;
    		double cAngle = Math.cos(angle);
    		double sAngle = Math.sin(angle);
    		int x1 = (int)(midX + radius*sAngle);
    		int y1 = (int)(midY - radius*cAngle);
    		int x2 = (int)(midX + (1-HOUR_MARKER_LEN)*radius*sAngle);
    		int y2 = (int)(midY - (1-HOUR_MARKER_LEN)*radius*cAngle);
    		int x3 = (int)(midX + (1-DIGIT_OFFSET)*radius*sAngle);
    		int y3 = (int)(midY - (1-DIGIT_OFFSET)*radius*cAngle);
    		
    		g.drawLine(x1, y1, x2, y2);
            g.drawText(Integer.toString(n), x3, y3, Graphics.VCENTER | Graphics.HCENTER);
    	}

    	Calendar cal = Calendar.getInstance();   	    	
    	float hour   = (float)cal.get(Calendar.HOUR);
    	float minute = (float)cal.get(Calendar.MINUTE);
    	float second = (float)cal.get(Calendar.SECOND);
    	float milsec = (float)cal.get(Calendar.MILLISECOND);
    	
    	SimpleDateFormat df = new SimpleDateFormat("E d");
    	String dayOfMonthText = df.formatLocal(System.currentTimeMillis()).toUpperCase();
    	int domWidth = g.getFont().getBounds(dayOfMonthText);
    	int domHeight = g.getFont().getHeight();
    	int domX = (int)(midX + (1-DIGIT_OFFSET-0.1)*radius);
    	g.drawRoundRect(domX - domWidth - 10, midY - domHeight/2 - 2, domWidth + 10, domHeight+4, 3, 3); 	
    	g.drawText(dayOfMonthText, domX - domWidth - 5, midY, Graphics.VCENTER | Graphics.HCENTER);
    	
    	int[]   handsValue = new int[] { 
    			(int)(hour*60 + minute), 
    			(int)(minute*12 + second/60.0f*12), 
    			(int)(second*12 + milsec/1000.f*12) 
    		};
    	    	
    	g.setDrawingStyle(Graphics.DRAWSTYLE_ANTIALIASED, false);
    	for (int n = 0; n < handsValue.length; ++n)
    	{
    		double angle = ((double)handsValue[n]/720)*2.0f*Math.PI;
    		double cAngle = Math.cos(angle);
    		double sAngle = Math.sin(angle);
    		int x1 = (int)(midX + handsLength[n]*radius*sAngle);
    		int y1 = (int)(midY - handsLength[n]*radius*cAngle);
    		int x2 = (int)(midX + 0.8*handsLength[n]*radius*sAngle);
    		int y2 = (int)(midY - 0.8*handsLength[n]*radius*cAngle);
    		
    		int dX = (int)(2.0*cAngle);
    		int dY = (int)(2.0*sAngle);
    	
    		g.setColor(handsColor[n]);
    		g.drawLine(midX, midY, x2, y2);
    		g.setColor(handsTipColor[n]);
    		g.drawLine(x2, y2, x1, y1);
    		
    		if (n != handsValue.length - 1) // thinner second hand
    		{
    			g.setColor(handsColor[n]);
    			g.drawLine(midX-dX, midY+dY, x2-dX, y2+dY);
    			g.drawLine(midX+dX, midY-dY, x2+dX, y2-dY);
    			g.setColor(handsTipColor[n]);
	    		g.drawLine(x2-dX, y2+dY, x1-dX, y1+dY);
	    		g.drawLine(x2+dX, y2-dY, x1+dX, y1-dY);
    		}
    	}
    	
    	super.paint(graphics);    	
    	graphics.drawBitmap(0, 25, bitmap.getWidth(), bitmap.getHeight()-25, bitmap, 0, 0);
    }
        
    public void refreshScreen()
    {   
    	this.invalidate();
    }
}
