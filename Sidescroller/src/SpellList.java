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
		else if (spell.equals("EarthSpike"))
		{
			return new File("Data/Resources/Spells/rockspike.png");
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
	public static Spell getSpell(String spell, int[] pos, String exclude)
	{
		Spell s = null;
		
		if (spell.equals("Fireball"))
		{
			s = new Spell("Fireball", 60, pos, new int[]{0,0,37,30},
					new boolean[]{true, false}, new int[]{17, 0}, getImage("Fireball"), 0, true, 200, 700, Entity.DAMAGE_FIRE, 10, exclude);
			
		}
		else if (spell.equals("EarthSpike"))
		{
			s = new Spell("EarthSpike", 100, pos, new int[]{0,15,50,30},
					new boolean[]{false, true}, new int[]{10, 0}, getImage("EarthSpike"), 3, true, 200, 1300, Entity.DAMAGE_EARTH, 10, exclude);
		}
		
		if (pos[2] == 0)
			s.velocity[0] = -s.velocity[0];
		
		return s;
	}
}
