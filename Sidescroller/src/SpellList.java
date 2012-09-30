
/**
 * Class that holds all the spells in the game.
 * @author Lyeeedar
 *
 */
public class SpellList {
	
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
					new boolean[]{true, false}, new int[]{17, 0}, "Data/Resources/Spells/fireball.png", 0, true, 2000, 700, Entity.DAMAGE_FIRE, 90, exclude);
			
		}
		else if (spell.equals("EarthSpike"))
		{
			s = new Spell("EarthSpike", 100, pos, new int[]{0,15,50,30},
					new boolean[]{false, true}, new int[]{10, 0}, "Data/Resources/Spells/rockspike.png", 3, true, 200, 1300, Entity.DAMAGE_EARTH, 10, exclude);
		}
		
		if (pos[2] == 0)
			s.velocity[0] = -s.velocity[0];
		
		return s;
	}
}
