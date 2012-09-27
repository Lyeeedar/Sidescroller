import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
	public String levelName;
	
	/**
	 * The name for this instance of the game, effects the name of the save file.
	 */
	public String gameName = "test";
	
	public ArrayList<SystemMessage> systemMessages = new ArrayList<SystemMessage>();
	
	public boolean saving = false;
	public boolean loading = false;

	public GameData()
	{
		test();
	}

	/**
	 * Test method
	 */
	public void test()
	{	
		boolean a = true;
		if (a)
		{
			//Dialogue dia = new Dialogue(new String[]{"test part 1", "test part 2"}, 0);

			Entity e = new Entity("Player", 100, 4, new int[]{20, 20, 0}, new File("Data/Resources/Spritesheets/HumanFemale.png"), new int[]{15, 0, 20, 57}, new boolean[]{true, true, false, false}, null);

			gameEntities.put("Player", e);

			Entity ef = new Entity("NPC", 60, 4, new int[]{90, 90, 0}, new File("Data/Resources/Spritesheets/HumanFemale.png"), new int[]{15, 0, 20, 57}, new boolean[]{false, true, true, false}, null);

			gameEntities.put("NPC", ef);	
			//		gameEntities.add(efd);	
			//		gameEntities.add(eff);	
			//		gameEntities.add(efg);

			BufferedImage im = null;
			try{
				im = ImageIO.read(new File("Test2/back0.png"));
			}
			catch (IOException ex)
			{

			}

			background[0] = im;

			try{
				im = ImageIO.read(new File("Test2/back1.png"));
			}
			catch (IOException ex)
			{

			}

			background[1] = im;

			try{
				im = ImageIO.read(new File("Test2/back2.png"));
			}
			catch (IOException ex)
			{

			}

			background[2] = im;

			try{
				im = ImageIO.read(new File("Test2/back3.png"));
			}
			catch (IOException ex)
			{

			}

			background[3] = im;

			try{
				im = ImageIO.read(new File("Test2/back4.png"));
			}
			catch (IOException ex)
			{

			}

			background[4] = im;
			
			createCollisionMap();
		}
		else
		{
			this.loadLevel("Test5");
		}

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

			MainFrame.menu.evaluateButtons();
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
				if ((e.getPos()[0] < MainFrame.screenPosition[0]-MainFrame.resolution[0]) || (e.getPos()[0] > (MainFrame.screenPosition[0]+(2*MainFrame.resolution[0])))
						|| (e.getPos()[1]+e.getSize()[1] < MainFrame.screenPosition[1]-MainFrame.resolution[1]) || (e.getPos()[1] > (MainFrame.screenPosition[1]+(2*MainFrame.resolution[1]))))
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
	public boolean saveGame()
	{
		// Set the screen to show the 'saving' message
		this.saving = true;
		Main.setState(2);

		SaveGame.save(this);

		this.saving = false;
		Main.setState(1);
		return true;
	}


	public boolean loadGame(File file)
	{
		this.loading = true;
		Main.setState(2);
		
		SaveGame.loadGame(file, this);
		
		this.loading = false;
		Main.setState(1);
		return true;
	}
	
	public boolean loadLevel(String levelName)
	{
		this.loading = true;
		Main.setState(2);
		
		SaveGame.loadLevel(levelName, this);
		
		this.loading = false;
		Main.setState(1);
		return true;
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

	/**
	 * Returns {@link GameData#gameName}
	 * @return the gameName
	 */
	public String getGameName() {
		return gameName;
	}

	/**
	 * Sets {@link GameData#gameName}
	 * @param gameName the gameName to set
	 */
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

}
