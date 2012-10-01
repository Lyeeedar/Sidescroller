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
		SaveGame save = null;
		
		File dir = new File("Data/Saves");
		dir.mkdirs();
		
		if (file == null)
			return false;
		
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
		
		Level level = Level.load(new File("Data/"+save.currentLevel+".data"));
		if (level == null)
			return false;
		
		HashMap<String, Entity> gameEntities = level.gameEntities;
		
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
		
		for (Map.Entry<String, Entity> entry : gameEntities.entrySet())
		{
			Entity ent = entry.getValue();
			ent.processSpritesheet();
		}
		
		gamedata.setGameEntities(gameEntities);
		gamedata.setBackground(level.getBackground());
		gamedata.createCollisionMap();
		
		Character.inventory = save.inventory;
		Character.socketedSpells = save.socketedSpells;
		
		Character.fireSpells = save.spellTrees.get(0);
		Character.airSpells = save.spellTrees.get(1);
		Character.earthSpells = save.spellTrees.get(2);
		Character.waterSpells = save.spellTrees.get(3);
		Character.deathSpells = save.spellTrees.get(4);
		Character.lifeSpells = save.spellTrees.get(5);
		
		Character.timePlayed = save.timePlayed;
		
		Character.reloadAllImages();
		
		gamedata.systemMessages.clear();
		
		return true;
	}
	
	public static boolean loadLevel(String levelName, GameData gamedata)
	{
		SaveGame save = null;
		
		File dir = new File("Data/Saves");
		dir.mkdirs();
		
		File file = new File("Data/Saves/autosave.sav");
		
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
		
		
		Level level = Level.load(new File("Data/"+levelName+".data"));
		HashMap<String, Entity> gameEntities = level.gameEntities;
		
		if (save.gameEntities.containsKey(level.name))
		{
			gameEntities = save.gameEntities.get(level.name);
		}
		gamedata.levelName = level.name;
		
		if (save.player != null)
		{
			Entity oldPlayer = level.gameEntities.get("Player");
			level.gameEntities.remove("Player");

			save.player.setPos(oldPlayer.getPos());

			gameEntities.put("Player", save.player);
		}
		
		for (Map.Entry<String, Entity> entry : gameEntities.entrySet())
		{
			Entity ent = entry.getValue();
			
			ent.processSpritesheet();
		}
		
		gamedata.setGameEntities(gameEntities);
		gamedata.setBackground(level.getBackground());
		gamedata.createCollisionMap();
		
		return true;
	}

}
