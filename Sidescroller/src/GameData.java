import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Class containing all the data used by the game
 * @author Lyeeedar
 *
 */
public class GameData {
	
	public static String gameSessionID;

	/**
	 * The images used by the game. Stored with thier filename so each image is only ever loaded once
	 */
	public static HashMap<String, BufferedImage> gameImages = new HashMap<String, BufferedImage>();
	
	public static CircularArrayRing<TempLevelData> storedLevels = new CircularArrayRing<TempLevelData>(4);
	
	/**
	 *  The rate at which the game runs (evaluates AI)
	 */
	private long gameSpeed = 40;
	/**
	 * The remaining time before another Game Logic update
	 */
	private long gameSpeedRemainder = gameSpeed;

	/**
	 * The current framerate
	 */
	private long framerate;

	/**
	 * Size of the level. Width, Height
	 */
	public static int[] levelSize = new int[2];

	/**
	 *  All the entities currently in the game. <p>
	 *  Index 0 is always the player
	 */
	private HashMap<String, Entity> gameEntities = new HashMap<String, Entity>();

	/**
	 *  Array to hold the background images. <p>
	 *  0 = Distant <p>
	 *  1 = Near background <p>
	 *  2 = Collision background
	 *  3 = Foreground
	 */
	public BufferedImage[] background = new BufferedImage[5];

	/**
	 * A map of which pixels are collidable with
	 */
	private int[][] collisionMap;
	
	public int collisionX = 1;
	public int collisionY = 1;

	/**
	 * Strength of gravity in the game
	 */
	public static final int gravity = 2;

	/**
	 * Strength of friction in the game
	 */
	public static final int friction = 3;

	/**
	 * The name of the level
	 */
	public String levelName = "";
	
	/**
	 * Messages to be displayed in the chatbox
	 */
	public ArrayList<SystemMessage> systemMessages = new ArrayList<SystemMessage>();
	
	/**
	 * The current savefile
	 */
	public File saveFile;
	
	/**
	 * The current Background music
	 */
	public OggClip BGM;
	/**
	 * The volume of the BGM
	 */
	public static float gain = 0.75f;
	
	public boolean transformAllowed = true;

	public GameData()
	{
		clearGame();
	}
	
	/**
	 * Method to clear all the game data kept in GameData
	 */
	public void clearGame()
	{
		gameEntities.clear();
		
		Entity e = new Entity("Player", 80, 7, 8, new int[]{20, 20, 0}, 8, null, new int[]{46, 18, 27, 69}, new boolean[]{true, true, false, false}, null);
		gameEntities.put("Player", e);
		
		for (int i = 0; i < 5; i++)
		{
			BufferedImage im = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			background[i] = im;
		}
		
		this.createCollisionMap(1, 1);
	}
	
	/**
	 * Method to change the current BGM. If theres an old song playing then stop it, and then start the new one.
	 * @param bgm
	 */
	public void changeSong(OggClip bgm)
	{
		if (bgm == null)
		{

		}
		else if (BGM == null)
		{
			BGM = bgm;
			BGM.loop();

		}
		else if (bgm.getName().equals(BGM.getName()))
		{

		}
		else
		{
			BGM.stop();
			BGM = bgm;
			BGM.loop();
		}
		
		BGM.setGain(gain);
	}

	/**
	 * Test method
	 */
	public void newGame()
	{	
		Character.resetAll();
		
		gameSessionID = Long.toHexString(Double.doubleToLongBits(Math.random()))+Long.toHexString(Double.doubleToLongBits(Math.random()))+Long.toHexString(Double.doubleToLongBits(Math.random()));
		
		this.loadLevel("Tutorial");
	}
	
	/**
	 * Method to load an image. Will check if the image has already been loaded, and returns a reference if it has.
	 * If it hasn't been loaded then load the image and store it for future use
	 * @param image
	 * @return
	 */
	public static BufferedImage getImage(String type, String image)
	{
		
		if (type.equals("Spritesheet"))
		{
			image = "Data/Resources/Spritesheets/"+image;
		}
		else if (type.equals("Spell"))
		{
			image = "Data/Resources/Spells/"+image;
		}
		else if (type.equals("Item"))
		{
			image = "Data/Resources/Items/"+image;
		}
		else if (type.equals("GUI"))
		{
			image = "Data/Resources/GUI/"+image;
		}
		else
		{
			System.err.println("Error loading image file: Invalid type "+type + "     File to be loaded: "+image);
			return null;
		}
		
		if (gameImages.containsKey(image))
			return gameImages.get(image);
		else
		{
			BufferedImage im = null;
			try{
				// Try to load the image from the .jar file
		    	InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(image);
		    	
		    	if (in == null)
		    	{
		    		try{
		    			// Try to load it from the local file system
		    			in = new FileInputStream(image);
		    		}
		    		catch (FileNotFoundException fne)
		    		{
		    			// Try to load it from the src folder (only useful if run from eclipse)
		    			in = new FileInputStream("src/"+image);
		    		}
		    	}
		    	
				im = ImageIO.read(in);
				
				if (im != null)
					im = toCompatibleImage(im);
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			gameImages.put(image, im);
			return im;
		}
	}
	
	public void loadLevelImages(String name)
	{
		for (TempLevelData tld : storedLevels)
		{
			if (tld == null)
				continue;
			
			if (name.equals(tld.name))
			{
				background = tld.background;
				collisionMap = tld.collisionMap;
				this.levelName = name;
				
				//return;
			}
		}
		
		BufferedImage[] back = new BufferedImage[5];
		
		for (int i = 0; i < 5; i++)
		{
			File file = new File("Data/Resources/Levels/"+name+"/back"+i+".png");
			
			try {
				back[i] = toCompatibleImage(ImageIO.read(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		background = back;
		
		if (Main.preloadCollisionMap)
			fillCollisionMap();
		else
			createCollisionMap();
		
		storedLevels.add(new TempLevelData(back, name, collisionMap));
	}
	
	/**
	 * Method to convert the image passed into an image optimised for the current display mode
	 * @param image
	 * @return
	 */
	private static BufferedImage toCompatibleImage(BufferedImage image)
	{
		// obtain the current system graphical settings
		GraphicsConfiguration gfx_config = GraphicsEnvironment.
			getLocalGraphicsEnvironment().getDefaultScreenDevice().
			getDefaultConfiguration();

		/*
		 * if image is already compatible and optimized for current system 
		 * settings, simply return it
		 */
		if (image.getColorModel().equals(gfx_config.getColorModel()))
			return image;

		// image is not optimized, so create a new image that is
		BufferedImage new_image = gfx_config.createCompatibleImage(
				image.getWidth(), image.getHeight(), image.getTransparency());

		// get the graphics context of the new image to draw the old image on
		Graphics2D g2d = (Graphics2D) new_image.getGraphics();

		// actually draw the image and dispose of context no longer needed
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		// return the new optimized image
		return new_image; 
	}

	public void evaluateMenu(long time)
	{
		// Minus the time since last method call from the game speed timer
		this.gameSpeedRemainder -= time;

		// If the time since the last update exceeds the game speed then do an update
		if (this.gameSpeedRemainder <= 0)
		{
			// Reset game speed timer
			this.gameSpeedRemainder = this.gameSpeed;

			MainCanvas.menu.evaluateButtons();
		}
	}

	/**
	 * Method to update and evaluate the AI for every entity in the game
	 * @param time
	 */
	public void evaluateAI(long time)
	{
		// Minus the time since last method call from the game speed timer
		this.gameSpeedRemainder -= time;

		// If the time since the last update exceeds the game speed then do an update
		if (this.gameSpeedRemainder <= 0)
		{
			// Reset game speed timer
			this.gameSpeedRemainder = this.gameSpeed;

			// Create an array to hold all the entities to be deleted at the end of the loop
			ArrayList<String> delete = new ArrayList<String>();

			// Create an array to hold only the entities that need their AI evaluated
			ArrayList<Map.Entry<String, Entity>> evaluate = new ArrayList<Map.Entry<String, Entity>>();

			// Iterate over all the game entities
			for (Map.Entry<String, Entity> entry : gameEntities.entrySet())
			{
				Entity e = entry.getValue();
				// If the entity is over double the resolution away from the screen (and player)
				// then do not update its AI. Stops excessive AI processing.
				if ((e.getPos()[0] < MainCanvas.screenPosition[0]-MainCanvas.resolution[0]) || (e.getPos()[0] > (MainCanvas.screenPosition[0]+(2*MainCanvas.resolution[0])))
						|| (e.getPos()[1]+e.getSize()[1] < MainCanvas.screenPosition[1]-MainCanvas.resolution[1]) || (e.getPos()[1] > (MainCanvas.screenPosition[1]+(2*MainCanvas.resolution[1]))))
					continue;

				evaluate.add(entry);
			}

			for (Map.Entry<String, Entity> entry : evaluate){
				Entity e = entry.getValue();
				// Evaluate the Entity AI
				e.AI();

				// If the entity is talking then check that the time the entity has stood idle with the text up doesnt exceed the maximum length, else turn the dialogue off
				if (e.isTalking())
				{
					if (e.getTalkingTimer() > Dialogue.dialogueFade+Dialogue.fadeDuration)
					{
						e.setTalking(false);
						e.setTalkingTimer(0);
					}
					else
					{
						e.setTalkingTimer(e.getTalkingTimer()+time);
					}
				}
				else
				{
					e.setTalkingTimer(0);
				}

				// If the Entity is a spell and has exploded and played its entire explode animation then add it to the delete array
				if ((e instanceof Spell) && (!e.isAlive()) && (((Spell)e).explode >= 8))
				{
					delete.add(entry.getKey());
				}
				else if ((e instanceof Item) && (!e.isAlive()))
				{
					delete.add(entry.getKey());
				}
			}

			// Delete all the entities
			for (String s : delete)
			{
				this.getGameEntities().remove(s);
			}
		}
	}
	
	/**
	 * This method is used to save the game (state of all the entities in the current level)
	 * @return
	 */
	public void saveGame(File file)
	{
		SaveGame.save(this, file);
		System.gc();
	}
	
	volatile int loadStage = 0;
	volatile String loadText = "";

	public void loadGame(final File file)
	{	
		loadStage = 0;
		loadText = "";
		final int state = Main.getState();
		final GameData gd = this;
		
		SaveGame.loadGame(file, gd);
		Main.setState(state);
		System.gc();	
	}
	
	public void loadLevel(final String levelName)
	{
		loadStage = 0;
		loadText = "";
		final int state = Main.getState();
		final GameData gd = this;
		
		SaveGame.loadLevel(levelName, gd);
		Main.setState(state);
		System.gc();	
		
	}
	

	/**
	 * Method to calculate and store the collision map for the entity. Created from the collision layer (background[3]). Collisionable pixels are the non-transparent ones
	 */
	public void fillCollisionMap()
	{
		levelSize[0] = background[3].getWidth();
		levelSize[1] = background[3].getHeight();

		createCollisionMap(levelSize[0], levelSize[1]);
		
		boolean collide = false;

		for (int x = 0; x < levelSize[0]; x++)
		{
			for (int y = 0; y < levelSize[1]; y++)
			{
				// Extract pixel colour
				int colour = background[3].getRGB(x, y);

				// Extract alpha value
				int alpha = (colour>>24) & 0xff;

				collide = (alpha != 0);
				
				if (collide)
				{
					// If alpha is not 0 then store true in the collision map
					collisionMap[x][y] = 2;
				}
				else
				{
					// If alpha is not 0 then store true in the collision map
					collisionMap[x][y] = 1;
				}
			}
		}
	}
	
	public boolean checkCollision(int x, int y)
	{
		boolean collide = false;
		
		if (collisionMap[x][y] == 1)
		{
			collide = false;
		}
		else if (collisionMap[x][y] == 2)
		{
			collide = true;
		}
		else
		{
			// Extract pixel colour
			int colour = background[3].getRGB(x, y);

			// Extract alpha value
			int alpha = (colour>>24) & 0xff;

			collide = (alpha != 0);
			
			if (collide)
			{
				// If alpha is not 0 then store true in the collision map
				collisionMap[x][y] = 2;
			}
			else
			{
				// If alpha is not 0 then store true in the collision map
				collisionMap[x][y] = 1;
			}
		}
		
		
		return collide;
	}

	public void createCollisionMap(int x, int y)
	{	
		collisionMap = new int[x][y];
		
		collisionX = x;
		collisionY = y;
	}
	
	public void createCollisionMap()
	{
		levelSize[0] = background[3].getWidth();
		levelSize[1] = background[3].getHeight();
		
		collisionMap = new int[levelSize[0]][levelSize[1]];
		
		collisionX = levelSize[0];
		collisionY = levelSize[1];
	}





	/**
	 *  Returns {@link GameData#gameEntities}
	 * @return the gameEntities
	 */
	public HashMap<String, Entity> getGameEntities() {
		return gameEntities;
	}

	/**
	 * Sets {@link GameData#gameEntities}
	 * @param gameEntities to set
	 */
	public void setGameEntities(HashMap<String, Entity> gameEntities) {
		this.gameEntities = gameEntities;
	}

	/**
	 * Returns {@link GameData#gameSpeed}
	 * @return the gameSpeed
	 */
	public long getGameSpeed() {
		return gameSpeed;
	}

	/**
	 * Sets {@link GameData#gameSpeed}
	 * @param gameSpeed the gameSpeed to set
	 */
	public void setGameSpeed(long gameSpeed) {
		this.gameSpeed = gameSpeed;
	}

	/**
	 * Returns {@link GameData#framerate}
	 * @return the framerate
	 */
	public long getFramerate() {
		return framerate;
	}

	/**
	 * Sets {@link GameData#framerate}
	 * @param framerate the framerate to set
	 */
	public void setFramerate(long framerate) {
		this.framerate = framerate;
	}

	/**
	 * Returns {@link GameData#gameSpeedRemainder}
	 * @return the gameSpeedRemainder
	 */
	public long getGameSpeedRemainder() {
		return gameSpeedRemainder;
	}

	/**
	 * Sets {@link GameData#gameSpeedRemainder}
	 * @param gameSpeedRemainder the gameSpeedRemainder to set
	 */
	public void setGameSpeedRemainder(long gameSpeedRemainder) {
		this.gameSpeedRemainder = gameSpeedRemainder;
	}

	/**
	 * Returns {@link GameData#background}
	 * @return the background
	 */
	public BufferedImage[] getBackground() {
		return background;
	}

	/**
	 * Sets {@link GameData#background}
	 * @param background the background to set
	 */
	public void setBackground(BufferedImage[] background) {
		this.background = background;
	}
//
//	/**
//	 * Returns {@link GameData#collisionMap}
//	 * @return the collisionMap
//	 */
//	public boolean[][] getCollisionMap() {
//		return collisionMap;
//	}
//
//	/**
//	 * Sets {@link GameData#collisionMap}
//	 * @param collisionMap the collisionMap to set
//	 */
//	public void setCollisionMap(boolean[][] collisionMap) {
//		this.collisionMap = collisionMap;
//	}

	/**
	 * Returns {@link GameData#levelName}
	 * @return the levelName
	 */
	public String getLevelName() {
		return levelName;
	}

	/**
	 * Sets {@link GameData#levelName}
	 * @param levelName the levelName to set
	 */
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	/**
	 * Returns {@link GameData#levelSize}
	 * @return the levelSize
	 */
	public static int[] getLevelSize() {
		return levelSize;
	}

	/**
	 * Sets {@link GameData#levelSize}
	 * @param levelSize the levelSize to set
	 */
	public static void setLevelSize(int[] levelSize) {
		GameData.levelSize = levelSize;
	}

}
