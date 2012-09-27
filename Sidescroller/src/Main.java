import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Class containing the main game data
 * @author Lyeeedar
 *
 */
public class Main {
	
	public static boolean fullscreen = false;
	
	/**
	 *  Game state. <p>
	 * 0 = Close game <p>
	 * 1 = Normal loop <p>
	 * 2 = Load/Save
	 * 3 = Menu
	 * 
	 */
	private static int state = 1;
	/**
	 * Holds all the data used by the Game
	 */
	public static final GameData gamedata = new GameData();
	/**
	 * The frame to draw the game onto
	 */
	public static MainFrame mainframe;
	
	public static final Random ran = new Random();
	
	public static void main(String[] args) {
		
		// Create the game
		Main game = new Main();
		GraphicsDevice device = null;

        try{
        	
        	// Get the current Graphics environment
        	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        	// Get the current screen device for the current graphics environment
            device = env.getDefaultScreenDevice();
            // Get the graphics configuration for the current screen device
            GraphicsConfiguration gc = device.getDefaultConfiguration();
            
            // Create the Main Frame
            mainframe = new MainFrame(gc);
        	
            if (fullscreen)
            {
            	// Set the Main Frame to fullscreen exclusive mode
            	device.setFullScreenWindow(mainframe);
            }
    		
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
		
		while(true)
		{
			
			if (state == 0)
			{
				break;
			}
			else if (state == 1)
			{
				
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
				
				// Store current time
				lastTime = System.currentTimeMillis();
				
				// Paint game graphics
				Main.mainframe.paintGame(framerate, gc);
				
				// Work out the time that painting the game graphics took
				elapsedTime = System.currentTimeMillis() - lastTime;
				
				// Evaluate the Entity AI, telling it how much time has passed since the last update
				Main.gamedata.evaluateAI(elapsedTime);
				
				// Work out time taken to draw graphics and evaluate AI
				elapsedTime = System.currentTimeMillis() - lastTime;
				
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
			else if (state == 2)
			{
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
				
				// Store current time
				lastTime = System.currentTimeMillis();
				
				// Paint game graphics
				Main.mainframe.paintGame(framerate, gc);
				
				// Work out time taken to draw graphics and evaluate AI
				elapsedTime = System.currentTimeMillis() - lastTime;
			}
			else if (state == 3)
			{
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
				
				// Store current time
				lastTime = System.currentTimeMillis();
				
				// Paint game graphics
				Main.mainframe.paintMenu(gc);
				
				// Work out time taken to draw graphics and evaluate AI
				elapsedTime = System.currentTimeMillis() - lastTime;
				
				Main.gamedata.evaluateMenu(elapsedTime);

			}
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
