import java.util.HashMap;

/**
 * 
 */

/**
 * @author Lyeeedar
 *
 */
public class Sigil extends Item {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6973097036136114663L;
	String element;
	boolean equipped = false;
	
	/**
	 * 0 = health
	 * 1 = speed
	 * 2 = phys def
	 * 3 = fire def
	 * 4 = air def
	 * 5 = earth def
	 * 6 = water def
	 * 7 = death def
	 * 8 = life def
	 */
	int[] values;

	/**
	 * @param name
	 * @param description
	 * @param spriteFile
	 * @param pos
	 * @param number
	 * @param type
	 */
	public Sigil(String name, String description, String element, String spriteFile, int[] pos, int[] values) {
		super(name, description, spriteFile, pos, 1, 0);

		this.element = element;
		this.values = values;
		
	}
	
	public void equip()
	{
		Entity p = Main.gamedata.getGameEntities().get("Player");
		
		p.maxHealth += values[0];
		p.health += values[0];
		
		p.speed += values[1];
		
		HashMap<String, Double> newDefense = new HashMap<String, Double>();
		
		Double oldval = p.defense.get(Entity.PHYSICAL);
		oldval += values[2];
		newDefense.put(Entity.PHYSICAL, oldval);
		
		oldval = p.defense.get(Entity.FIRE);
		oldval += values[3];
		newDefense.put(Entity.FIRE, oldval);
		
		oldval = p.defense.get(Entity.AIR);
		oldval += values[4];
		newDefense.put(Entity.AIR, oldval);
		
		oldval = p.defense.get(Entity.EARTH);
		oldval += values[5];
		newDefense.put(Entity.EARTH, oldval);
		
		oldval = p.defense.get(Entity.WATER);
		oldval += values[6];
		newDefense.put(Entity.WATER, oldval);
		
		oldval = p.defense.get(Entity.DEATH);
		oldval += values[7];
		newDefense.put(Entity.DEATH, oldval);
		
		oldval = p.defense.get(Entity.LIFE);
		oldval += values[8];
		newDefense.put(Entity.LIFE, oldval);
		
		p.setDefense(newDefense);
		
		equipped = true;
	}
	
	public void unequip()
	{
		Entity p = Main.gamedata.getGameEntities().get("Player");
		
		p.maxHealth -= values[0];
		p.health -= values[0];
		
		p.speed -= values[1];
		
		HashMap<String, Double> newDefense = new HashMap<String, Double>();
		
		Double oldval = p.defense.get(Entity.PHYSICAL);
		oldval -= values[2];
		newDefense.put(Entity.PHYSICAL, oldval);
		
		oldval = p.defense.get(Entity.FIRE);
		oldval -= values[3];
		newDefense.put(Entity.FIRE, oldval);
		
		oldval = p.defense.get(Entity.AIR);
		oldval -= values[4];
		newDefense.put(Entity.AIR, oldval);
		
		oldval = p.defense.get(Entity.EARTH);
		oldval -= values[5];
		newDefense.put(Entity.EARTH, oldval);
		
		oldval = p.defense.get(Entity.WATER);
		oldval -= values[6];
		newDefense.put(Entity.WATER, oldval);
		
		oldval = p.defense.get(Entity.DEATH);
		oldval -= values[7];
		newDefense.put(Entity.DEATH, oldval);
		
		oldval = p.defense.get(Entity.LIFE);
		oldval -= values[8];
		newDefense.put(Entity.LIFE, oldval);
		
		p.setDefense(newDefense);
		
		equipped = false;
	}

}
