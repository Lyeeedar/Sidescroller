import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lyeeedar
 *
 */
public class SaveGame {

	String currentLevel = null;
	HashMap<String, HashMap<String, Entity>> gameEntities = new HashMap<String, HashMap<String, Entity>>();
	Entity player = null;

	public static boolean save(GameData gamedata)
	{
		SaveGame save = null;
		
		File dir = new File("Data/Saves");
		dir.mkdirs();

		File file = new File("Data/Saves/"+gamedata.getGameName()+".sav");

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
			file = new File("Data/Saves/"+gamedata.gameName+".sav");
		
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
		HashMap<String, Entity> gameEntities = level.gameEntities;
		
		if (save.gameEntities.containsKey(level.name))
		{
			gameEntities = save.gameEntities.get(level.name);
		}
		gamedata.levelName = level.name;
		
		Entity oldPlayer = level.gameEntities.get("Player");
		level.gameEntities.remove("Player");
		
		save.player.setPos(oldPlayer.getPos());
		
		gameEntities.put("Player", save.player);
		
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
	
	public static boolean loadLevel(String levelName, GameData gamedata)
	{
		SaveGame save = null;
		
		File dir = new File("Data/Saves");
		dir.mkdirs();
		
		File file = new File("Data/Saves/"+gamedata.gameName+".sav");
		
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
