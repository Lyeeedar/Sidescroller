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
		
		if (element.equals(Entity.FIRE))
		{
			s = Character.fireSpells.get(stage).spells.get(index);
		}
		else if (element.equals(Entity.AIR))
		{
			s = Character.airSpells.get(stage).spells.get(index);
		}
		else if (element.equals(Entity.EARTH))
		{
			s = Character.earthSpells.get(stage).spells.get(index);
		}
		else if (element.equals(Entity.WATER))
		{
			s = Character.waterSpells.get(stage).spells.get(index);
		}
		else if (element.equals(Entity.DEATH))
		{
			s = Character.deathSpells.get(stage).spells.get(index);
		}
		else if (element.equals(Entity.LIFE))
		{
			s = Character.lifeSpells.get(stage).spells.get(index);
		}
		
		if (s.unlocked < 3)
		{
			s.unlocked = 2;
		}
	}

}
