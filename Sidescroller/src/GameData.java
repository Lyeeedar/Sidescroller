import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Class containing all the data used by the game
 * @author Lyeeedar
 *
 */
public class GameData {

	public static HashMap<String, BufferedImage> gameImages = new HashMap<String, BufferedImage>();
	
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
	public boolean[][] collisionMap;

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
	
	public ArrayList<SystemMessage> systemMessages = new ArrayList<SystemMessage>();
	
	public boolean saving = false;
	public boolean loading = false;
	
	public File saveFile;
	
	public OggClip BGM;
	public static float gain = 0.75f;

	public GameData()
	{
		clearGame();
		
		try {
			BGM = new OggClip("Data/Resources/Sounds/test.ogg");
			BGM.loop();
			BGM.setGain(gain);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clearGame()
	{
		Entity p = new Entity("Player", 800000000, 0, 0, new int[]{0, 0, 0}, 1, null, new int[]{0, 0, 0, 0}, new boolean[]{false, false, false, false}, null);
		gameEntities.put("Player", p);
		
		for (int i = 0; i < 5; i++)
		{
			BufferedImage im = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			background[i] = im;
		}
		
		this.createCollisionMap();
	}

	/**
	 * Test method
	 */
	public void test()
	{	
		Character.resetAll();
		boolean a = true;
		if (a)
		{
			//Dialogue dia = new Dialogue(new String[]{"test part 1", "test part 2"}, 0);

			Entity e = new Entity("Player", 80, 7, 8, new int[]{20, 20, 0}, 8, "Data/Resources/Spritesheets/male.png", new int[]{46, 18, 27, 65}, new boolean[]{true, true, false, false}, null);

			gameEntities.put("Player", e);
			e.setFaction("Player");

			for (int i = 0; i<2; i++)
			{
				Entity ef = new Entity("NPC"+i, 60, 7, 8, new int[]{300+(i*50), 90, 0}, 3, "Data/Resources/Spritesheets/female.png", new int[]{46, 18, 27, 65}, new boolean[]{false, true, true, false}, null);

				ef.setFaction("Enemy");
				gameEntities.put("NPC"+i, ef);
				ef.expAmount = 100;
				ef.getDropList().put("Chest", 100);
				ef.getDropList().put("Speed Sigil - Fire", 100);
				ef.getDropList().put("Speed Sigil - Life", 100);
			}
			//		gameEntities.add(efd);	
			//		gameEntities.add(eff);	
			//		gameEntities.add(efg);

			background[0] = GameData.getImage("Data/Resources/Levels/level1/back0.png");

			background[1] = GameData.getImage("Data/Resources/Levels/level1/back1.png");

			background[2] = GameData.getImage("Data/Resources/Levels/level1/back2.png");

			background[3] = GameData.getImage("Data/Resources/Levels/level1/back3.png");

			background[4] = GameData.getImage("Data/Resources/Levels/level1/back4.png");
			
			createCollisionMap();
			
			//this.loadGame(new File("Data/Saves/test.sav"));
			
			this.saveGame(new File("Data/Saves/autosave.sav"));
		}
		else
		{
			this.loadLevel("test");
		}

	}
	
	public static BufferedImage getImage(String image)
	{
		if (gameImages.containsKey(image))
			return gameImages.get(image);
		else
		{
			BufferedImage im = null;
			try{
		    	InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(image);
		    	
		    	if (in == null)
		    	{
		    		in = new FileInputStream("src/"+image);
		    	}
		    	
				im = ImageIO.read(in);
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

	/**
	 * Method to calculate and store the collision map for the entity. Created from the collision layer (background[3]). Collisionable pixels are the non-transparent ones
	 */
	public void createCollisionMap()
	{
		levelSize[0] = background[0].getWidth();
		levelSize[1] = background[0].getHeight();

		collisionMap = new boolean[levelSize[0]][levelSize[1]];

		for (int x = 0; x < levelSize[0]; x++)
		{
			for (int y = 0; y < levelSize[1]; y++)
			{
				// Extract pixel colour
				int colour = background[3].getRGB(x, y);

				// Extract alpha value
				int alpha = (colour>>24) & 0xff;

				// If alpha is not 0 then store true in the collision map
				collisionMap[x][y] = (alpha != 0);
			}
		}
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

	public Iterator<Entity> getEntityIterator()
	{
		Collection<Entity> c = gameEntities.values();
		Iterator<Entity> itr = c.iterator();
		return itr;
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

	/**
	 * Returns {@link GameData#collisionMap}
	 * @return the collisionMap
	 */
	public boolean[][] getCollisionMap() {
		return collisionMap;
	}

	/**
	 * Sets {@link GameData#collisionMap}
	 * @param collisionMap the collisionMap to set
	 */
	public void setCollisionMap(boolean[][] collisionMap) {
		this.collisionMap = collisionMap;
	}

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
