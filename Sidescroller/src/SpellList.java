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
		if (spellImages.containsKey(spell))
			return spellImages.get(spell);
		else
		{
			BufferedImage im = null;
			try{
				im = ImageIO.read(getIm(spell));
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			spellImages.put(spell, im);
			return im;
		}
	}
	
	public static Spell getSpell(String spell)
	{
		if (spell.equals("Fireball"))
		{
			Spell s = new Spell("Fireball", 60, new int[]{0, 0, 0}, new int[]{0,0,37,30},
					new boolean[]{true}, new int[]{0, 0}, getImage("Fireball"), 0, true, 200, 700, Entity.DAMAGE_PHYSICAL, 10, "");
			
			return s;
		}
		
		return null;
	}
}
