import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Class that holds all the data for a level. Designed to be serialized
 * @author Lyeeedar
 *
 */
public class Level implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8036541149682971280L;
	
	/**
	 * The level name
	 */
	String name;
	
	/**
	 * The entities in the level
	 */
	HashMap<String, Entity> gameEntities = new HashMap<String, Entity>();
	
	String BGM;
	
	boolean transformAllowed;
	
	public Level()
	{
		
	}
	
	/**
	 * Method to store the name and game entities in the level object.
	 * @param name
	 * @param gameEntities
	 */
	public void create(String name, HashMap<String, Entity> gameEntities, String BGM, boolean transformAllowed)
	{
		this.BGM = BGM;
		this.name = name;
		this.gameEntities = gameEntities;
		this.transformAllowed = transformAllowed;
	}
	
	public OggClip getBGM()
	{
		if (BGM.equals(""))
			return null;
		
		OggClip bgm = null;
		try {
			bgm = new OggClip(BGM);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bgm;
	}
	
	/**
	 * Method that serializes the level objected given into the Data folder, and saves the background images into the Data/Resources/Levels/'name' folder
	 * @param level
	 * @return
	 */
	public static boolean save(Level level)
	{
		try
		{
			File folders = new File("src/Data/Resources");
			folders.mkdirs();
			FileOutputStream fileOut = new FileOutputStream("src/Data/" + level.name + ".data");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(level);
			out.close();
			fileOut.close();
		}catch(IOException i)
		{
			i.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Method that deserializes a level object from the file given
	 * @param file
	 * @return
	 */
	public static Level load(String file)
	{	
		file = "Data/"+file;
		Level level = null;
		try{
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
			
			if (in == null)
			{
				try{
					// Try to load it from the local file system
					in = new FileInputStream(file);
				}
				catch (FileNotFoundException fne)
				{
					// Try to load it from the src folder (only useful if run from eclipse)
					in = new FileInputStream("src/"+file);
				}
			}
			
			ObjectInputStream oin = new ObjectInputStream(in);
			level = (Level) oin.readObject();
			oin.close();
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return level;
	}

}