import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import javax.imageio.ImageIO;

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
	
	public Level()
	{
		
	}
	
	/**
	 * Method to store the name and game entities in the level object.
	 * @param name
	 * @param gameEntities
	 */
	public void create(String name, HashMap<String, Entity> gameEntities)
	{
		this.name = name;
		this.gameEntities = gameEntities;
	}
	
	/**
	 * Method that loads and returns the background images for this level name
	 * @return
	 */
	public BufferedImage[] getBackground()
	{
		BufferedImage[] back = new BufferedImage[5];
		
		for (int i = 0; i < 5; i++)
		{
			File file = new File("Data/Resources/Levels/"+name+"/back"+i+".png");
			
			try {
				back[i] = ImageIO.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return back;
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
			File folders = new File("Data/Resources");
			folders.mkdirs();
			FileOutputStream fileOut = new FileOutputStream("Data/" + level.name + ".data");
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
	public static Level load(File file)
	{
		if (!file.exists())
			return null;
		
		Level level = null;
		try{
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fin);
			level = (Level) in.readObject();
			in.close();
			fin.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return level;
	}

}