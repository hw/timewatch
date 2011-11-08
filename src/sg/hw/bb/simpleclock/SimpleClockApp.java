package sg.hw.bb.simpleclock;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class SimpleClockApp extends UiApplication implements RealtimeClockListener
{
	class TimeSignalTask extends TimerTask
	{
		SimpleClockApp app;
		public TimeSignalTask(SimpleClockApp app)
		{
			super();
			this.app = app;
		}
		
		public void run()
		{
			app.refreshScreen();
		}
	}
	
	private final short[] BEEP = new short[] { 1440, 250, 0, 50 };
	private SimpleClockScreen mainScreen;
	private Timer refreshTimer;
	
	private final static long persistentStoreKey = 0x8656bdebbc4bfe7L;
	private PersistentObject persistentObject = PersistentStore.getPersistentObject(persistentStoreKey);
	private Hashtable settings;
	
	private final String[] minuteMenuText = new String[] { "Turn Beep On", "Turn Beep Off" };
	
	private MenuItem toggleMinuteBeepMenuItem = new MenuItem(minuteMenuText[1], 100, 100)
	{
		public void run()
		{
			Boolean shouldBeep = Boolean.TRUE;
			if (settings.containsKey("MinuteBeep"))
			{
				shouldBeep = (Boolean)settings.get("MinuteBeep");
			}
			
			if (shouldBeep == Boolean.TRUE)
			{
				shouldBeep = Boolean.FALSE;
				setText(minuteMenuText[0]);
			}
			else
			{
				shouldBeep = Boolean.TRUE;
				this.setText(minuteMenuText[1]);
			}
			
			settings.put("MinuteBeep", shouldBeep);
			persistentObject.setContents(settings);
			persistentObject.commit();
		}
	};
	
	private MenuItem aboutMenuItem = new MenuItem("About ...", 101, 101)
	{
		public void run()
		{
			Dialog.alert("SimpleClock (c) 2011  Tan Hock Woo");
		}
	};
	
    /**
     * Entry point for application
     * @param args Command line arguments (not used)
     */ 
    public static void main(String[] args)
    {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        SimpleClockApp theApp = new SimpleClockApp();       
        theApp.enterEventDispatcher();
    }
        
    /**
     * Creates a new TimeSignalApp object
     */
    public SimpleClockApp()
    {        
    	
    	settings = (Hashtable)persistentObject.getContents();
    	if (settings == null)
    	{
    		settings = new Hashtable();
    		settings.put("MinuteBeep", new Boolean(true));
    		persistentObject.setContents(settings);
    		persistentObject.commit();
    	};
    	
		Boolean shouldBeep = Boolean.TRUE;
		if (settings.containsKey("MinuteBeep"))
		{
			shouldBeep = (Boolean)settings.get("MinuteBeep");
		}
		toggleMinuteBeepMenuItem.setText(minuteMenuText[shouldBeep == Boolean.TRUE ? 1 : 0]);
    	
        // Push a screen onto the UI stack for rendering.
    	mainScreen = new SimpleClockScreen();
    	mainScreen.addMenuItem(toggleMinuteBeepMenuItem);
    	mainScreen.addMenuItem(aboutMenuItem);
        pushScreen(mainScreen);
        
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimeSignalTask(this), 200, 200);
        addRealtimeClockListener(this);
    }
    
	public void clockUpdated() {
		if (!Application.getApplication().isForeground()) return;
		Boolean shouldBeep = Boolean.TRUE;
		if (settings.containsKey("MinuteBeep"))
		{
			shouldBeep = (Boolean)settings.get("MinuteBeep");
		}
		
		if (shouldBeep == Boolean.TRUE)
		{
			Alert.startAudio(BEEP, 100);
		}
	}    
	
	public void refreshScreen() {
		mainScreen.refreshScreen();
	}
}
