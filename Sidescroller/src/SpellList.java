import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Class that holds all the spells in the game. Also stores their spritesheets so they do not need to be reloaded everytime a spell is created. 
 * @author Lyeeedar
 *
 */
public class SpellList {

	/**
	 * A HashMap of all the Spell names mapped to their images.
	 */
	public static final HashMap<String, BufferedImage> spellImages = new HashMap<String, BufferedImage>();
	
	/**
	 * Method to get the File for the spell
	 * @param spell
	 * @return
	 */
	private static File getIm(String spell)
	{
		if (spell.equals("Fireball"))
		{
			return new File("Data/Resources/Spells/fireball.png");
		}
		
		return null;
	}
	
	/**
	 * Method to get the Image for the spell given. If the Image is not yet loaded into {@link SpellList#spellImages} then load it into the HashMap for future use.
	 * @param spell
	 * @return
	 */
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
	
	/**
	 * Method to get a spell object for the spell name specified. Initialises it with the values given.
	 * @param spell
	 * @return
	 */
	public static Spell getSpell(String spell, int[] pos, int[] velocity, String exclude)
	{
		if (spell.equals("Fireball"))
		{
			Spell s = new Spell("Fireball", 60, pos, new int[]{0,0,37,30},
					new boolean[]{true}, velocity, getImage("Fireball"), 0, true, 200, 700, Entity.DAMAGE_PHYSICAL, 10, exclude);
			s.launch(velocity);
			
			return s;
		}
		
		return null;
	}
}
