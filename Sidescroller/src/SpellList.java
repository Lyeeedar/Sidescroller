import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * @author Lyeeedar
 *
 */
public class SpellList {

	public static final HashMap<String, BufferedImage> spellImages = new HashMap<String, BufferedImage>();
	
	public static void populateSpellImages(HashMap<String, Entity> entities)
	{
		ArrayList<String> spells = new ArrayList<String>();
		
		for (Map.Entry<String, Entity> entry : entities.entrySet())
		{
			Entity e = entry.getValue();
			
			for (String s : e.getSpells())
			{
				if (!spells.contains(s))
					spells.add(s);
			}
		}
		
		for (String s : spells)
		{
			File file = getIm(s);
			
			if (file == null)
			{
				System.err.println("Invalid spell "+s);
				continue;
			}
			
			BufferedImage im = null;
			
			try{
				im = ImageIO.read(file);
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
			
			spellImages.put(s, im);
		}
	}
	
	private static File getIm(String spell)
	{
		if (spell.equals("Fireball"))
		{
			return new File("Data/Resources/Spells/fireball.png");
		}
		
		return null;
	}
	
	public static BufferedImage getImage(String spell)
	{
		return spellImages.get(spell);
	}
}
