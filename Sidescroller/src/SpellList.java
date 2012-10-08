
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
		
		if (spell.equals("FireBall"))
		{
			s = new Spell("FireBall", 60, pos, new int[]{0,0,37,30},
					new boolean[]{true, false, false, false, false}, new int[]{17, 0}, "Data/Resources/Spells/fireball.png", 0, true, 2000, 700, Entity.FIRE, 50, exclude);	
		}
		else if (spell.equals("RockSpike"))
		{
			s = new Spell("RockSpike", 100, pos, new int[]{0,15,50,30},
					new boolean[]{false, true, false, false, false}, new int[]{10, 0}, "Data/Resources/Spells/rockspike.png", 3, true, 2000, 1300, Entity.EARTH, 50, exclude);
		}
		else if (spell.equals("FlameWall"))
		{
			s = new Spell("FlameWall", 100, pos, new int[]{0,0,50,100},
					new boolean[]{false, true, false, false, false}, new int[]{4, 0}, "Data/Resources/Spells/flamewall.png", 3, true, 2000, 1300, Entity.FIRE, 50, exclude);
		}
		else if (spell.equals("WindBlade"))
		{
			s = new Spell("WindBlade", 100, pos, new int[]{0,15,50,30},
					new boolean[]{true, false, false, false, false}, new int[]{15, 0}, "Data/Resources/Spells/windblade.png", 0, true, 2000, 700, Entity.AIR, 50, exclude);
		}
		else if (spell.equals("IceSpike"))
		{
			s = new Spell("IceSpike", 100, pos, new int[]{0,15,50,30},
					new boolean[]{true, false, false, false, false}, new int[]{16, 0}, "Data/Resources/Spells/icespike.png", 3, true, 2000, 700, Entity.WATER, 50, exclude);
		}
		else if (spell.equals("RockShot"))
		{
			s = new Spell("RockShot", 100, pos, new int[]{0,15,50,30},
					new boolean[]{true, false, false, false, false}, new int[]{17, 0}, "Data/Resources/Spells/rockshot.png", 3, true, 2000, 700, Entity.EARTH, 50, exclude);
		}
		else if (spell.equals("RockWall"))
		{
			s = new Spell("RockWall", 100, pos, new int[]{0,0,50,100},
					new boolean[]{false, false, false, false, false}, new int[]{0, 0}, "Data/Resources/Spells/rockwall.png", 3, false, 2000, 2300, Entity.EARTH, 0, exclude);
		}
		else if (spell.equals("Scorch"))
		{
			s = new Spell("Scorch", 110, pos, new int[]{0,0,50,50},
					new boolean[]{false, false, false, true, false}, new int[]{0, 0}, "Data/Resources/Spells/scorch.png", 3, true, 2000, 1300, Entity.FIRE, 1, exclude);
		}
		else if (spell.equals("WildFire"))
		{
			s = new Spell("WildFire", 110, pos, new int[]{0,0,50,50},
					new boolean[]{false, false, false, false, true}, new int[]{15, 0}, "Data/Resources/Spells/scorch.png", 3, true, 2000, 300, Entity.FIRE, 1, exclude);
		}
		
		if (pos[2] == 0)
			s.velocity[0] = -s.velocity[0];
		
		return s;
	}
	
	public static Spell getStrike(String type, int amount, int[] pos, String exclude, int[] collision)
	{
		Spell s = null;
		
		s = new Spell(type+" Strike", 60, pos, collision,
				new boolean[]{false, false, true, false, false}, new int[]{17, 0}, null, 0, true, 1300, 700, type, amount, exclude);
		
		s.setVisible(false);
		
		return s;
	}
}
