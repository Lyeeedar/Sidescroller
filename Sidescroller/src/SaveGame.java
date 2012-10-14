import java.awt.GraphicsConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lyeeedar
 *
 */
public class SaveGame implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 566737083548919063L;
	String currentLevel = "";
	HashMap<String, HashMap<String, Entity>> gameEntities = new HashMap<String, HashMap<String, Entity>>();
	Entity player = null;
	ArrayList<HashMap<String, Item>> inventory = new ArrayList<HashMap<String, Item>>();
	SpellsStageEntry[] socketedSpells = new SpellsStageEntry[5];
	ArrayList<ArrayList<SpellsStage>> spellTrees = new ArrayList<ArrayList<SpellsStage>>();
	long timePlayed = 0;
	int gender = 0;

	public static boolean save(GameData gamedata, File file)
	{
		SaveGame save = null;
		
		File dir = new File("Data/Saves");
		dir.mkdirs();

		if (file.exists())
		{
			try{
				FileInputStream fin = new FileInputStream(file);
				ObjectInputStream in = new ObjectInputStream(fin);
				save = (SaveGame) in.readObject();
				in.close();
				fin.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			save = new SaveGame();
			save.gameEntities = new HashMap<String, HashMap<String, Entity>>();
		}

		HashMap<String, Entity> saveMap = new HashMap<String, Entity>();

		for (Map.Entry<String, Entity> entry : gamedata.getGameEntities().entrySet()){
			Entity e = entry.getValue();

			if (e instanceof Spell)
			{
				continue;
			}
			else
			{
				entry.getValue().setTalking(false);
				saveMap.put(entry.getKey(), entry.getValue());
			}
		}

		if (save.gameEntities.containsKey(gamedata.getLevelName()))
		{
			save.gameEntities.remove(gamedata.getLevelName());
		}

		save.gameEntities.put(gamedata.getLevelName(), saveMap);
		
		save.currentLevel = gamedata.getLevelName();
		
		save.player = saveMap.get("Player");
		
		save.inventory = Character.inventory;
		
		save.socketedSpells = Character.socketedSpells;
		
		save.spellTrees.add(Character.fireSpells);
		save.spellTrees.add(Character.airSpells);
		save.spellTrees.add(Character.earthSpells);
		save.spellTrees.add(Character.waterSpells);
		save.spellTrees.add(Character.deathSpells);
		save.spellTrees.add(Character.lifeSpells);
		
		save.timePlayed = Character.timePlayed;
		save.gender = Character.gender;
		
		try
		{
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(save);
			out.close();
			fileOut.close();
		}
		catch(IOException i)
		{
			i.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static boolean loadGame(File file, GameData gamedata)
	{
		
		GraphicsConfiguration gc = Main.gc;
		
		SaveGame save = null;
		
		File dir = new File("Data/Saves");
		dir.mkdirs();
		
		if (file == null)
			return false;
		
		Main.gamedata.loadStage = 1;
		Main.gamedata.loadText = "Loading old file";
		Main.maincanvas.paintLoad(gc);
		if (file.exists())
		{
			try {
				FileInputStream fin = new FileInputStream(file);
				ObjectInputStream in = new ObjectInputStream(fin);
				save = (SaveGame) in.readObject();
				in.close();
				fin.close();
			}
			catch (Exception ioe)
			{
				ioe.printStackTrace();
				return false;
			}
		}
		else
		{
			return false;
		}
		
		Main.gamedata.loadStage = 2;
		Main.gamedata.loadText = "Loading level";
		Main.maincanvas.paintLoad(gc);
		Level level = Level.load(new File("Data/"+save.currentLevel+".data"));
		if (level == null)
			return false;
		
		HashMap<String, Entity> gameEntities = level.gameEntities;
		
		Main.gamedata.loadStage = 3;
		Main.gamedata.loadText = "Overwriting game entities";
		Main.maincanvas.paintLoad(gc);
		if (save.gameEntities.containsKey(level.name))
		{
			gameEntities = save.gameEntities.get(level.name);
		}
		gamedata.levelName = level.name;
		
//		Entity oldPlayer = level.gameEntities.get("Player");
//		level.gameEntities.remove("Player");
//		
//		save.player.setPos(oldPlayer.getPos());
//		
//		gameEntities.put("Player", save.player);
		
		Main.gamedata.loadStage = 4;
		Main.gamedata.loadText = "Processing spritesheets";
		Main.maincanvas.paintLoad(gc);
		for (Map.Entry<String, Entity> entry : gameEntities.entrySet())
		{
			Entity ent = entry.getValue();
			ent.processSpritesheet();
		}
		
		Main.gamedata.loadStage = 5;
		Main.gamedata.loadText = "Loading background";
		Main.maincanvas.paintLoad(gc);
		gamedata.setGameEntities(gameEntities);
		gamedata.setBackground(level.getBackground());
		
		Main.gamedata.loadStage = 6;
		Main.gamedata.loadText = "Generating Collision Map";
		Main.maincanvas.paintLoad(gc);
		gamedata.createCollisionMap();
		
		Main.gamedata.loadStage = 7;
		Main.gamedata.loadText = "Setting references";
		Main.maincanvas.paintLoad(gc);
		Character.inventory = save.inventory;
		Character.socketedSpells = save.socketedSpells;
		
		Character.fireSpells = save.spellTrees.get(0);
		Character.airSpells = save.spellTrees.get(1);
		Character.earthSpells = save.spellTrees.get(2);
		Character.waterSpells = save.spellTrees.get(3);
		Character.deathSpells = save.spellTrees.get(4);
		Character.lifeSpells = save.spellTrees.get(5);
		
		Character.timePlayed = save.timePlayed;
		Character.gender = save.gender;
		
		Main.gamedata.loadStage = 8;
		Main.gamedata.loadText = "Reloading character images";
		Main.maincanvas.paintLoad(gc);
		Character.reloadAllImages();
		
		gamedata.systemMessages.clear();
		
		gamedata.changeSong(level.getBGM());
		
		return true;
	}
	
	public static boolean loadLevel(String levelName, GameData gamedata)
	{
		GraphicsConfiguration gc = Main.gc;
		SaveGame save = null;
		
		File dir = new File("Data/Saves");
		dir.mkdirs();
		
		File file = new File("Data/Saves/autosave.sav");
		
		Main.gamedata.loadStage = 1;
		Main.gamedata.loadText = "Loading old file";
		Main.maincanvas.paintLoad(gc);
		if (file.exists())
		{
			try {
				FileInputStream fin = new FileInputStream(file);
				ObjectInputStream in = new ObjectInputStream(fin);
				save = (SaveGame) in.readObject();
				in.close();
				fin.close();
			}
			catch (Exception ioe)
			{
				ioe.printStackTrace();
				return false;
			}
		}
		else
		{
			save = new SaveGame();
		}
		
		Main.gamedata.loadStage = 2;
		Main.gamedata.loadText = "Loading new level";
		Main.maincanvas.paintLoad(gc);
		Level level = Level.load(new File("Data/"+levelName+".data"));
		
		HashMap<String, Entity> gameEntities = level.gameEntities;
		
		Main.gamedata.loadStage = 3;
		Main.gamedata.loadText = "Overwriting game entities";
		Main.maincanvas.paintLoad(gc);
		if (save.gameEntities.containsKey(level.name))
		{
			gameEntities = save.gameEntities.get(level.name);
		}
		gamedata.levelName = level.name;
		
		Main.gamedata.loadStage = 4;
		Main.gamedata.loadText = "Updating player stats";
		Main.maincanvas.paintLoad(gc);
		if (save.player != null)
		{
			Entity oldPlayer = level.gameEntities.get("Player");
			level.gameEntities.remove("Player");

			if (oldPlayer != null)
				save.player.setPos(oldPlayer.getPos());

			gameEntities.put("Player", save.player);
		}
		
		Main.gamedata.loadStage = 5;
		Main.gamedata.loadText = "Processing entity images";
		Main.maincanvas.paintLoad(gc);
		for (Map.Entry<String, Entity> entry : gameEntities.entrySet())
		{
			Entity ent = entry.getValue();
			
			ent.processSpritesheet();
		}
		
		Main.gamedata.loadStage = 6;
		Main.gamedata.loadText = "Loading background";
		Main.maincanvas.paintLoad(gc);
		gamedata.setGameEntities(gameEntities);
		gamedata.setBackground(level.getBackground());
		
		Main.gamedata.loadStage = 7;
		Main.gamedata.loadText = "Calculating collision map";
		Main.maincanvas.paintLoad(gc);
		gamedata.createCollisionMap();
		
		gamedata.changeSong(level.getBGM());
		
		return true;
	}

}
