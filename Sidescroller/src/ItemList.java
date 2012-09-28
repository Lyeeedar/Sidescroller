import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * @author Lyeeedar
 *
 */
public class ItemList {


	public static final HashMap<String, BufferedImage> itemImages = new HashMap<String, BufferedImage>();
	
	private static File getIm(String item)
	{
		if (item.equals("Chest"))
		{
			return new File("Data/Resources/Items/chest.png");
		}
		
		return null;
	}
	
	public static BufferedImage getImage(String item)
	{
		if (itemImages.containsKey(item))
			return itemImages.get(item);
		else
		{
			BufferedImage im = null;
			try{
				im = ImageIO.read(getIm(item));
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			itemImages.put(item, im);
			return im;
		}
	}
	

	public static Item getItem(String item, int[] pos, int number)
	{
		Item i = null;
		
		if (item.equals("Chest"))
		{
			i = new Item(item, pos, 1);			
		}
		
		return i;
	}
}
