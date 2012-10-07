/**
 * 
 */

/**
 * @author Lyeeedar
 *
 */
public class Scroll extends Upgrade {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3071188198002988324L;

	int index;
	int stage;
	String element;
	/**
	 * @param name
	 * @param description
	 * @param spriteFile
	 * @param pos
	 * @param number
	 * @param type
	 */
	public Scroll(String name, String description, String spriteFile,
			int[] pos, int index, int stage, String element) {
		super(name, description, spriteFile, pos);

		this.index = index;
		this.stage = stage;
		this.element = element;
		
	}
	
	@Override
	void upgrade()
	{
		SpellsStageEntry s = null;
		
		if (element.equals(Entity.DAMAGE_FIRE))
		{
			s = Character.fireSpells.get(stage).spells.get(index);
		}
		else if (element.equals(Entity.DAMAGE_AIR))
		{
			s = Character.airSpells.get(stage).spells.get(index);
		}
		else if (element.equals(Entity.DAMAGE_EARTH))
		{
			s = Character.earthSpells.get(stage).spells.get(index);
		}
		else if (element.equals(Entity.DAMAGE_WATER))
		{
			s = Character.waterSpells.get(stage).spells.get(index);
		}
		else if (element.equals(Entity.DAMAGE_DEATH))
		{
			s = Character.deathSpells.get(stage).spells.get(index);
		}
		else if (element.equals(Entity.DAMAGE_LIFE))
		{
			s = Character.lifeSpells.get(stage).spells.get(index);
		}
		
		if (s.unlocked < 3)
		{
			s.unlocked = 2;
		}
		
		number--;
	}

}
