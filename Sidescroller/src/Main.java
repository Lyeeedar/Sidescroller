import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

/**
 * Class containing the main game data
 * @author Lyeeedar
 *
 */
public class Main {

	public static boolean fullscreen = true;

	/**
	 *  Game state. <p>
	 * 0 = Close game <p>
	 * 1 = Normal loop <p>
	 * 2 = Toggle Fullscreen <p>
	 * 3 = Menu
	 * 4 = Paused
	 * 
	 */
	private static int state = 3;
	/**
	 * Holds all the data used by the Game
	 */
	public static final GameData gamedata = new GameData();
	/**
	 * The frame to draw the game onto
	 */
	public static MainCanvas maincanvas;

	/**
	 * Instance of Random for generating random numbers
	 */
	public static final Random ran = new Random();
	
	/**
	 * The current game window
	 */
	static JFrame window;

	public static GraphicsConfiguration gc = null;
	public static GraphicsDevice device = null;

	public static void main(String[] args) {

		// If system in linux then sound effects will be buggy so set them to muted by default
		if(System.getProperty("os.name").startsWith("Linux"))
		{
			SoundEffect.volume = SoundEffect.Volume.MUTE;
		}

		System.out.println(System.getProperty("os.name"));

		// Turn off direct3D to improve game stability
		System.setProperty("sun.java2d.d3d","False");

		// Reset all game variables
		Character.resetAll();
		// Reload sound effects
		SoundEffect.init();
		// Create the game
		Main game = new Main();

		try{

			// Get the current Graphics environment
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			// Get the current screen device for the current graphics environment
			device = env.getDefaultScreenDevice();
			// Get the graphics configuration for the current screen device
			gc = device.getDefaultConfiguration();

			// Create the Main Frame
			maincanvas = new MainCanvas(gc);

			// Create the frame
			Main.toggleFullscreen();

			// Run the game loop
			game.loop(gc);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally{
			// Remove the program from fullscreen mode
			device.setFullScreenWindow(null);

		}

		System.exit(0);
	}

	/**
	 * Method used to toggle fullscreen mode. It also disposes of the old frame to prevent old windows hanging around the screen.
	 */
	public static void toggleFullscreen()
	{
		// If there is currently a window open, then close it
		if (window != null)
		{
			window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			window.dispose();
		}
		
		window = new JFrame("Sidescroller");
		window.setIgnoreRepaint(true);
		window.add(maincanvas);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// If not fullscreen then set the new frame to fullscreen
		if (!fullscreen)
		{
			// Turn off decorations for the frame
			window.setUndecorated(true);
			// Set the Main Frame to fullscreen exclusive mode
			device.setFullScreenWindow(window);

			// Set the screen display mode to automatically resize the screen to fullscreen
			// Try 2 different versions of the display mode to catch most bugs
			try {
				if( device.isDisplayChangeSupported() ) {
					device.setDisplayMode( 
							new DisplayMode( MainCanvas.resolution[0], MainCanvas.resolution[1], device.getDisplayMode().getBitDepth(), device.getDisplayMode().getRefreshRate() )
							);
				}
				fullscreen = true;
			}
			catch (Exception e)
			{
				if( device.isDisplayChangeSupported() ) {
					device.setDisplayMode( 
							new DisplayMode( MainCanvas.resolution[0], MainCanvas.resolution[1], device.getDisplayMode().getBitDepth(), 60 )
							);
				}
				fullscreen = true;
			}
		}
		// If fullscreen then remove the frame from fullscreen and restore decorations
		else
		{
			device.setFullScreenWindow(null);
			window.setUndecorated(false);
			fullscreen = false;
		}

		// Set the window size and then prevent any further resizing
		if (fullscreen)
			window.pack();
		else
			window.setSize(802, 627);
		window.setResizable(false);
		
		
		// Get the size of the screen
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    // Determine the new location of the window
	    int w = window.getSize().width;
	    int h = window.getSize().height;
	    int x = (dim.width-w)/2;
	    int y = (dim.height-h)/2;
	    
	    // Move the window
	    window.setLocation(x, y);
		
	    // Set the keyboard focus to the canvas
		maincanvas.requestFocusInWindow();
		window.setVisible(true);
		
		// Recreate the buffer strategy for the frame
		maincanvas.createStrategy();
	}

	/**
	 * The last state the game loop was in
	 */
	public static int oldState = 3;
	/**
	 * The main loop of the game. <p>
	 * Is stateful depending on the value of {@link Main#state}
	 */
	public void loop(GraphicsConfiguration gc)
	{
		long lastTime = System.currentTimeMillis();
		long elapsedTime;
		long totalTime = 0;
		int frames = 0;
		int framerate = 0;
		int updateTimer = 0;

		while(true)
		{
			// Make sure the focus is on the canvas
			maincanvas.requestFocusInWindow();
			
			// Update fps every 0.5 seconds
			if (totalTime < 500)
			{
				totalTime += System.currentTimeMillis() - lastTime;
				frames++;
			}
			else
			{
				totalTime = 0;
				framerate = frames*2;
				frames = 0;
			}

			// ------------------- State 0 Start ------------------- //
			// This state is used to quit the game
			if (state == 0)
			{
				break;
			}
			// ------------------- State 0 End   ------------------- //
			
			// ------------------- State 1 Start ------------------- //
			// This state is used to run the normal game. AI updates, animation updates and etc
			else if (state == 1)
			{

				// Store current time
				lastTime = System.currentTimeMillis();

				// Paint game graphics
				Main.maincanvas.paintGame(framerate, gc);

				// Work out the time that painting the game graphics took
				elapsedTime = System.currentTimeMillis() - lastTime;

				// Evaluate the Entity AI, telling it how much time has passed since the last update
				Main.gamedata.evaluateAI(elapsedTime);

				// Work out time taken to draw graphics and evaluate AI
				elapsedTime = System.currentTimeMillis() - lastTime;

				ArrayList<SystemMessage> newMessages = new ArrayList<SystemMessage>();

				// Time out system messages in the message box
				int i = 0;
				for (SystemMessage sys : Main.gamedata.systemMessages)
				{
					if (i < 10)
						sys.aliveTime -= elapsedTime;

					if (sys.aliveTime > 0)
						newMessages.add(sys);

					i++;
				}

				Main.gamedata.systemMessages = newMessages;

				// Work out time taken to draw graphics and evaluate AI
				elapsedTime = System.currentTimeMillis() - lastTime;

				Character.updateTime(elapsedTime);

				Character.timePlayed += elapsedTime;
				Character.genderSwapCD -= elapsedTime;

				ArrayList<Entity> update = new ArrayList<Entity>();

				// Update animation for entities
				for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
				{
					Entity e = entry.getValue();
					update.add(e);
				}

				for (Entity e : update)
				{
					e.updateTime(elapsedTime);
				}
			}
			// ------------------- State 1 End   ------------------- //
			
			// ------------------- State 2 Start ------------------- //
			// This state is used to toggle the fullscreen mode
			else if (state == 2)
			{
				toggleFullscreen();
				state = oldState;
			}
			// ------------------- State 2 End   ------------------- //
			
			// ------------------- State 3 Start ------------------- //
			// This state is used to draw and update the menus
			else if (state == 3)
			{
				// Store current time
				lastTime = System.currentTimeMillis();

				// Paint menu graphics
				Main.maincanvas.paintMenu(framerate, gc);

				// Work out time taken to draw graphics and evaluate AI
				elapsedTime = System.currentTimeMillis() - lastTime;

				// Evaluate menu AI
				Main.gamedata.evaluateMenu(elapsedTime);

			}
			// ------------------- State 3 End   ------------------- //
			
			// ------------------- State 4 Start ------------------- //
			// This state is used to draw the pause screen
			else if (state == 4)
			{
				Main.maincanvas.paintPaused(gc);
			}
			// ------------------- State 4 End   ------------------- //
		}
	}


	/**
	 * @return the state
	 */
	public static int getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public static void setState(int state) {
		Main.state = state;
	}

}
