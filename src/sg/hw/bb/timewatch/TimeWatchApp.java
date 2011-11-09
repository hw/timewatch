package sg.hw.bb.timewatch;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class TimeWatchApp extends UiApplication implements RealtimeClockListener
{
	class TimeSignalTask extends TimerTask
	{
		TimeWatchApp app;
		public TimeSignalTask(TimeWatchApp app)
		{
			super();
			this.app = app;
		}
		
		public void run()
		{
			app.refreshScreen();
		}
	};
	
	private final static boolean 	DEBUG_MODE = false;	
	private final static long 		PERSISTENT_STORE_KEY = 0x667a7384ab9ab9f1L;
	private final static short[]	CHIME = new short[] { 1200, 100, 1700, 100, 0, 50 };
	private final static String 	CHIME_HASHTABLE_KEY = "IsChimeEnabled";
	private final static String[]	CHIME_MENU_TEXT = new String[] { "Enable Chime", "Disable Chime" };

	
	private TimeWatchScreen mainScreen;
	private Timer refreshTimer;
	private Boolean isChimeEnabled;
	
	
	private MenuItem markMenuItem = new MenuItem("Mark Time", 1000, 100)
	{
		public void run()
		{
			DateFormat df = (SimpleDateFormat)(DateFormat.getInstance(DateFormat.TIME_LONG));
			setText("Marked " + df.formatLocal(System.currentTimeMillis()));
			mainScreen.markTime();
		}
	};
		
	private MenuItem toggleMinuteBeepMenuItem = new MenuItem(CHIME_MENU_TEXT[1], 1001, 101)
	{
		public void run()
		{
			isChimeEnabled = (isChimeEnabled.booleanValue() ? Boolean.FALSE : Boolean.TRUE);
			setText(CHIME_MENU_TEXT[isChimeEnabled.booleanValue()? 1 : 0]);
			saveSettings();
		}
	};
	

	private MenuItem aboutMenuItem = new MenuItem("About ...", 1011, 102)
	{
		public void run()
		{
			Dialog dlg = new Dialog(Dialog.D_OK, "Time Watch \u00a9 2011\n  Tan Hock Woo", 0, Bitmap.getBitmapResource("icon.png"), Screen.NO_SYSTEM_MENU_ITEMS);
			dlg.doModal();
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
        TimeWatchApp theApp = new TimeWatchApp();       
        theApp.enterEventDispatcher();
    }
        
    /**
     * Creates a new TimeSignalApp object
     */
    public TimeWatchApp()
    {        
    	// Load settings and setup menus
    	loadSettings();
    	toggleMinuteBeepMenuItem.setText(CHIME_MENU_TEXT[isChimeEnabled.booleanValue() ? 1 : 0]);    	
    	
    	// Push a screen onto the UI stack for rendering.
    	mainScreen = new TimeWatchScreen();
    	mainScreen.addMenuItem(markMenuItem);
    	mainScreen.addMenuItem(toggleMinuteBeepMenuItem);
    	mainScreen.addMenuItem(MenuItem.separator(1010));
    	mainScreen.addMenuItem(aboutMenuItem);
    	pushScreen(mainScreen);
        
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimeSignalTask(this), 200, 200);
        addRealtimeClockListener(this);
    }
    private void loadSettings()
    {
    	isChimeEnabled = Boolean.TRUE;
    	if (!DEBUG_MODE)
    	{
	    	PersistentObject persistentObject = PersistentStore.getPersistentObject(PERSISTENT_STORE_KEY);
	    	Hashtable settings = (Hashtable)persistentObject.getContents();
	    	if (settings == null || !settings.containsKey(CHIME_HASHTABLE_KEY)) return; 
	    	isChimeEnabled = (Boolean)settings.get(CHIME_HASHTABLE_KEY);
    	}
    }
    
    private void saveSettings()
    {
       	if (!DEBUG_MODE)
    	{  	
	    	PersistentObject persistentObject = PersistentStore.getPersistentObject(PERSISTENT_STORE_KEY);
	    	Hashtable settings = (Hashtable)persistentObject.getContents();
	
	    	settings.put(CHIME_HASHTABLE_KEY, isChimeEnabled);
	    	persistentObject.setContents(settings);
	    	persistentObject.commit();    	
    	}
    }
    
	public void clockUpdated() 
    {
		if (!Application.getApplication().isForeground()) return;
		if (!DEBUG_MODE && isChimeEnabled.booleanValue())
		{
			Alert.startAudio(CHIME, Alert.getVolume());
		}
	}    
	
	public void refreshScreen() {
		mainScreen.refreshScreen();
	}
}
