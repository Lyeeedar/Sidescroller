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
 * @author Lyeeedar
 *
 */
public class Level implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8036541149682971280L;
	
	String name;
	HashMap<String, Entity> gameEntities = new HashMap<String, Entity>();
	
	public Level()
	{
		
	}
	
	public void create(String name, HashMap<String, Entity> gameEntities)
	{
		this.name = name;
		this.gameEntities = gameEntities;
	}
	
//	public BufferedImage[] getBackground()
//	{
//		BufferedImage[] back = new BufferedImage[4];
//		
//		for (int i = 0; i < 4; i++)
//		{
//			back[i] = background[i].getImage();
//		}
//		
//		return back;
//	}
	
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

	public static Level load(File file)
	{
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

/**
 * A wrapper for BufferedImage to allow it to be serialized
 * @author Lyeeedar
 *
 */
class ImageWrapper implements Serializable{

	private static final long serialVersionUID = -383914860206735079L;

	int width; int height; int[] pixels;

	public ImageWrapper(BufferedImage bi) {
		width = bi.getWidth();
		height = bi.getHeight();
		pixels=bi.getRGB(0,0,width,height,pixels,0,width);
	}

	public BufferedImage getImage() {
		BufferedImage bi = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
		bi.setRGB(0,0,width,height,pixels,0,width);
		return bi;
	}

}