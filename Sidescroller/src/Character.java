import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class holds a lot of the character details, such as what spells are learned and equipped, and what sigils are socketed
 * @author Lyeeedar
 *
 */
public class Character {
	
	/**
	 * The total time played on this game
	 */
	public static long timePlayed;

	/**
	 * The entire inventory, it is stored as 3 HashMap's in the order: <p>
	 * 0 = Sigils (Armour upgrades) <p>
	 * 1 = Upgrades (Usuable items) <p>
	 * 2 = Misc <p>
	 */
	public static ArrayList<HashMap<String, Item>> inventory = new ArrayList<HashMap<String, Item>>();
	
	/**
	 * The spells socketted in the 5 slots
	 */
	public static SpellsStageEntry[] socketedSpells = new SpellsStageEntry[5];
	
	/**
	 * The current gender: <p>
	 *  0 = female <p>
	 *  1 = male <p>
	 */
	public static int gender = 1;
	/**
	 * The cooldown before it is possible to genderswap again
	 */
	public static long genderSwapCD = 0;
	/**
	 * The current stage of the genderswap animation
	 */
	public static int genderSwapAnimStage = 0;
	/**
	 * The time before the genderswap animation is updates
	 */
	public static int genderSwapAnimCD = 0;
	/**
	 * Whether the genderswap is animating
	 */
	public static boolean genderSwapAnimating = false;
	/**
	 * The sprite used for the genderswap animation
	 */
	public static BufferedImage genderSwapSprite = GameData.getImage("Spritesheet", "transform.png");
	
	/**
	 * The cooldown for each of the socketed spells
	 */
	public static long[] spellCooldown = new long[5];
	
	/**
	 * The currently equipped sigils.
	 */
	public static Sigil[] equippedSigils = new Sigil[6];
	
	/**
	 * Method to equip a sigil. Only 1 sigil can be equipped per element
	 * @param sigil
	 */
	public static void equipSigil(Sigil sigil)
	{
		int index = 0;
		
		if (sigil.element.equals(Entity.FIRE))
		{
			index = 0;
		}
		else if (sigil.element.equals(Entity.AIR))
		{
			index = 1;
		}
		else if (sigil.element.equals(Entity.EARTH))
		{
			index = 2;
		}
		else if (sigil.element.equals(Entity.WATER))
		{
			index = 3;
		}
		else if (sigil.element.equals(Entity.DEATH))
		{
			index = 4;
		}
		else if (sigil.element.equals(Entity.LIFE))
		{
			index = 5;
		}
		
		if (equippedSigils[index] != null)
			equippedSigils[index].unequip();
		equippedSigils[index] = sigil;
		equippedSigils[index].equip();
	}
	
	/**
	 * Method used to unequip a sigil
	 * @param sigil
	 */
	public static void unequipSigil(Sigil sigil)
	{
		int index = 0;
		
		if (sigil.element.equals(Entity.FIRE))
		{
			index = 0;
		}
		else if (sigil.element.equals(Entity.AIR))
		{
			index = 1;
		}
		else if (sigil.element.equals(Entity.EARTH))
		{
			index = 2;
		}
		else if (sigil.element.equals(Entity.WATER))
		{
			index = 3;
		}
		else if (sigil.element.equals(Entity.DEATH))
		{
			index = 4;
		}
		else if (sigil.element.equals(Entity.LIFE))
		{
			index = 5;
		}
		
		equippedSigils[index].unequip();
		equippedSigils[index] = null;
	}
	
	/**
	 * Method to add exp to each of the equipped spells
	 * @param amount
	 */
	public static void addEXP(int amount)
	{
		for (int i = 0; i < 5; i++)
		{
			socketedSpells[i].addEXP(amount);
		}
	}
	
	/**
	 * Method to update time. In this case timePlayed, genderswap, spells
	 * @param elapsedTime
	 */
	public static void updateTime(long elapsedTime)
	{
		timePlayed += elapsedTime;
		genderSwapCD -= elapsedTime;
		
		for (int i = 0; i < 5; i++)
		{
			spellCooldown[i] -= elapsedTime;
		}
		
		if (genderSwapAnimating)
		{
			genderSwapAnimCD -= elapsedTime;
			
			if (genderSwapAnimCD < 0)
			{
				genderSwapAnimCD = 50;
				genderSwapAnimStage += 1;
				
				if (genderSwapAnimStage == 3)
				{
					swapGender();
				}
				
				if (genderSwapAnimStage > 7)
				{
					genderSwapAnimStage = 0;
					genderSwapAnimating = false;
				}
			}
		}
	}
	
	/**
	 * Method to start the genderswap process
	 */
	public static void beginGenderSwap()
	{
		if (!Main.gamedata.transformAllowed)
		{
			Main.gamedata.systemMessages.add(new SystemMessage("Transform not allowed in this area!", Color.PINK, 10000));
			return;
		}
		
		if (genderSwapCD > 0)
			return;

		genderSwapCD = 5000;
		
		genderSwapAnimating = true;
	}
	
	/**
	 * Method that actually swaps the sprite depending on the characters gender
	 */
	public static void swapGender()
	{
		
		if (gender == 0)
		{
			gender = 1;
			Main.gamedata.getGameEntities().get("Player").setSpriteSheet(GameData.getImage("Spritesheet", "male.png"));
			Main.gamedata.getGameEntities().get("Player").setSpriteFile("male.png");
		}
		else
		{
			gender = 0;
			Main.gamedata.getGameEntities().get("Player").setSpriteSheet(GameData.getImage("Spritesheet", "female.png"));
			Main.gamedata.getGameEntities().get("Player").setSpriteFile("female.png");
		}
		
	}
	
	/**
	 * Method that adds an item to the inventory. Will stack the item if it already exists in the inventory
	 * @param item
	 */
	public static void addItem(Item item)
	{
		HashMap<String, Item> inventoryMap = inventory.get(item.type);
		if (inventoryMap.containsKey(item.getName()))
		{
			inventoryMap.get(item.getName()).number += item.number;
		}
		else
		{
			inventoryMap.put(item.getName(), item);
		}
	}
	
	public static ArrayList<SpellsStage> fireSpells = new ArrayList<SpellsStage>();
	public static ArrayList<SpellsStage> airSpells = new ArrayList<SpellsStage>();
	public static ArrayList<SpellsStage> earthSpells = new ArrayList<SpellsStage>();
	public static ArrayList<SpellsStage> waterSpells = new ArrayList<SpellsStage>();
	public static ArrayList<SpellsStage> deathSpells = new ArrayList<SpellsStage>();
	public static ArrayList<SpellsStage> lifeSpells = new ArrayList<SpellsStage>();
	
	/**
	 * Reset all the variables in this class. Empties inventory, resets sockted spells, resets spell unlocks and reloads all images
	 */
	public static void resetAll()
	{
		inventory.clear();
		for (int i = 0; i < 3; i++)
		{
			inventory.add(new HashMap<String, Item>());
		}
		
		resetSpells();
		
		for (int i = 0; i < 5; i++)
		{
			socketedSpells[i] = fireSpells.get(0).spells.get(0);
		}
		
		reloadAllImages();
	}
	
	public static void resetSpells()
	{
		populateFireSpells();
		populateAirSpells();
		populateEarthSpells();
		populateWaterSpells();
		populateDeathSpells();
		populateLifeSpells();
	}
	
	public static void populateLifeSpells()
	{
		String element = Entity.LIFE;
		
		ArrayList<SpellsStage> spells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			spells.add(new SpellsStage());
		}
		
		spells.get(0).spells.add(new SpellsStageEntry("SmallHeal", null, 2, new int[]{500, 100},
				new String[]{"Life/spellIconSmallHeal.png","Life/spellIconLifeMaster.png"},
				"Heal a little of your health.", 500, element));
		
		lifeSpells = spells;
	}
	
	public static void populateDeathSpells()
	{
		String element = Entity.DEATH;
		
		ArrayList<SpellsStage> spells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			spells.add(new SpellsStage());
		}
		
		spells.get(0).spells.add(new SpellsStageEntry("ImpI", null, 2, new int[]{500, 100},
				new String[]{"Death/spellIconImpI.png","Death/spellIconDeathMaster.png"},
				"Summon an imp to attack your foe.", 500, element));
		
		deathSpells = spells;
	}
	
	public static void populateWaterSpells()
	{
		String element = Entity.WATER;
		
		ArrayList<SpellsStage> spells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			spells.add(new SpellsStage());
		}
		
		spells.get(0).spells.add(new SpellsStageEntry("IceSpike", null, 2, new int[]{500, 100},
				new String[]{"Water/spellIconIceSpike.png","Water/spellIconWaterMaster.png"},
				"Send a spike of ice at your foe.", 500, element));
		
		waterSpells = spells;
	}
	
	public static void populateEarthSpells()
	{
		String element = Entity.EARTH;
		
		ArrayList<SpellsStage> spells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			spells.add(new SpellsStage());
		}
		
		spells.get(0).spells.add(new SpellsStageEntry("RockSpike", null, 2, new int[]{500, 100},
				new String[]{"Earth/spellIconRockSpike.png","Earth/spellIconEarthMaster.png"},
				"Send a series of rock spikes flying out in front of you.", 500, element));
		
		spells.get(1).spells.add(new SpellsStageEntry("RockWall", null, 0, new int[]{400, 200},
				new String[]{"Earth/spellIconRockWall.png","Earth/spellIconEarthMaster.png"},
				"Raise a wall of rock from the ground to stop foes.", 580, element));
		
		spells.get(1).spells.add(new SpellsStageEntry("Stone", new String[]{"RockSpike"}, 0, new int[]{600, 200},
				new String[]{"Earth/spellIconStone.png","Earth/spellIconEarthMaster.png"},
				"Send a stone flying at an opponent.", 580, element));
		
		earthSpells = spells;
	}
	
	public static void populateAirSpells()
	{
		String element = Entity.AIR;
		
		airSpells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			airSpells.add(new SpellsStage());
		}
		
		airSpells.get(0).spells.add(new SpellsStageEntry("WindBlade", null, 2, new int[]{500, 100},
				new String[]{"Air/spellIconWindBlade.png","Air/spellIconAirMaster.png"},
				"Slice a foe with a blade of air.", 500, element));
	}
	
	public static void populateFireSpells()
	{
		String element = Entity.FIRE;
		
		fireSpells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			fireSpells.add(new SpellsStage());
		}
		
		fireSpells.get(0).spells.add(new SpellsStageEntry("FireBall", null, 2, new int[]{500, 100},
				new String[]{"Fire/spellIconFireBall.png","Fire/spellIconFireMaster.png"},
				"A ball of burning fire. Will singe a target somewhat fierce.", 500, element));
		
		fireSpells.get(1).spells.add(new SpellsStageEntry("Scorch", null, 0, new int[]{400, 200},
				new String[]{"Fire/spellIconScorch.png","Fire/spellIconFireMaster.png"},
				"Sets fire to a spot, scorching all who enter it.", 700, element));
		
		fireSpells.get(1).spells.add(new SpellsStageEntry("FlameWall", new String[]{"FireBall"}, 0, new int[]{600, 200},
				new String[]{"Fire/spellIconFlameWall.png","Fire/spellIconFireMaster.png"},
				"Creates a wall of fire, burning any who pass through it.", 580, element));
		
		fireSpells.get(2).spells.add(new SpellsStageEntry("WildFire", new String[]{"Scorch"}, 0, new int[]{400, 300},
				new String[]{"Fire/spellIconWildFire.png","Fire/spellIconFireMaster.png"},
				"Sets fire to a line, scorching all who enter it.", 900, element));

	
		unlockSpells(fireSpells);
	}
	
	/**
	 * Method that checks a spell tree and unlocks all the spells depending on their requirements. <p>
	 * will set a spell to unlock 1 aslong as all the prerequisites have been at least learnt. <p>
	 * Will unlock a spell if all its prerequisistes have been mastered
	 * @param spells
	 */
	public static void unlockSpells(ArrayList<SpellsStage> spells)
	{
		for (int i = 0; i < spells.size(); i++)
		{
			SpellsStage s = spells.get(i);
			
			for (SpellsStageEntry sse : s.spells)
			{
				if (sse.unlocked == 3)
				{
					continue;
				}
				else if (sse.parents.size() == 0)
				{
					continue;
				}
				
				int unlocked = 2;
				for (String p : sse.parents)
				{
					if (spells.get(i-1).getSpellStageEntry(p).isUnlocked() < 3)
					{
						if (spells.get(i-1).getSpellStageEntry(p).isUnlocked() == 2)
						{
							unlocked = 1;
						}
						else
						{
							unlocked = 0;
							break;
						}
					}
				}
				sse.unlocked = unlocked;
			}
		}
	}
	
	/**
	 * Return the spell for the given element (static String in Entity)
	 * @param name
	 * @return
	 */
	public static ArrayList<SpellsStage> getSpell(String name)
	{
		if (name.equals(Entity.FIRE))
		{
			return fireSpells;
		}
		else if (name.equals(Entity.AIR))
		{
			return airSpells;
		}
		else if (name.equals(Entity.EARTH))
		{
			return earthSpells;
		}
		else if (name.equals(Entity.WATER))
		{
			return waterSpells;
		}
		else if (name.equals(Entity.DEATH))
		{
			return deathSpells;
		}
		else if (name.equals(Entity.LIFE))
		{
			return lifeSpells;
		}
		
		return null;
	}
	
	public static void reloadAllImages()
	{
		for (SpellsStage ss : fireSpells)
		{
			ss.loadImages();
		}
		for (SpellsStage ss : airSpells)
		{
			ss.loadImages();
		}
		for (SpellsStage ss : earthSpells)
		{
			ss.loadImages();
		}
		for (SpellsStage ss : waterSpells)
		{
			ss.loadImages();
		}
		for (SpellsStage ss : deathSpells)
		{
			ss.loadImages();
		}
		for (SpellsStage ss : lifeSpells)
		{
			ss.loadImages();
		}
	}
		
}
// End of Class

/**
 * Class used to model a stage in the spell tree
 * @author Lyeeedar
 *
 */
class SpellsStage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2385401053100828396L;
	/**
	 * The list of spells in the spell stage
	 */
	public ArrayList<SpellsStageEntry> spells = new ArrayList<SpellsStageEntry>();
	
	/**
	 * Get the spell with the given name from the spells array
	 * @param name
	 * @return
	 */
	public SpellsStageEntry getSpellStageEntry(String name)
	{
		for (SpellsStageEntry s : spells)
		{
			if (s.name.equals(name))
			{
				return s;
			}
		}
		
		return null;
	}
	
	/**
	 * Load all the spell images in this stage
	 */
	public void loadImages()
	{
		for (SpellsStageEntry sse : spells)
		{
			sse.loadImage();
		}
	}
}

/**
 * Class to model a spell in the spell tree
 * @author Lyeeedar
 *
 */
class SpellsStageEntry implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1455799641945222577L;
	/**
	 * Spell name
	 */
	public String name;
	/**
	 * The parents of the spell to be used for unlocking
	 */
	public ArrayList<String> parents = new ArrayList<String>();
	/**
	 * The unlocked stage of the spell. <p>
	 * 0 = Unknown <p>
	 * 1 = Known <p>
	 * 2 = Learnt <p>
	 * 3 = Mastered
	 */
	public int unlocked;
	/**
	 * The position in the spell tree
	 */
	public int[] pos;
	/**
	 * The names of the images used by this spell
	 */
	public String[] imageFiles;
	/**
	 * The images used by this spell
	 */
	public transient BufferedImage[] images;
	/**
	 * The description of this spell
	 */
	public String description;
	/**
	 * The exp needed to Master this spell
	 */
	public int maxEXP;
	/**
	 * The current exp gained by this spell
	 */
	public int currentEXP = 0;
	/**
	 * The element of this spell
	 */
	public String element;
	
	public SpellsStageEntry(String name, String[] parents, int unlocked, int[] pos, String[] imageFiles, String description, int maxEXP, String element)
	{
		this.element = element;
		this.maxEXP = maxEXP;
		this.imageFiles = imageFiles;
		this.description = description;
		this.name = name;
		this.pos = pos;
		this.unlocked = unlocked;
		if (parents != null)
		{
			for (int i = 0; i < parents.length; i++)
			{
				this.parents.add(parents[i]);
			}
		}
	}
	
	/**
	 * Load the images used by this spell
	 */
	public void loadImage()
	{
		images = new BufferedImage[2];
		for (int i = 0; i < 2; i++)
			images[i] = GameData.getImage("GUI", imageFiles[i]);
	}
	
	public int isUnlocked()
	{
		return unlocked;
	}
	
	/**
	 * Add exp to this spell and unlock the spell if the maxExp is reached
	 * @param exp
	 */
	public void addEXP(int exp)
	{
		currentEXP += exp;
		
		if ((currentEXP >= maxEXP) && (unlocked < 3))
		{
			unlocked = 3;
			Main.gamedata.systemMessages.add(new SystemMessage("Mastered "+name, Color.ORANGE, 10000));
			Main.gamedata.getGameEntities().get("Player").infoText.add(new SystemMessage("Spell Mastered", Color.ORANGE, 3000));
			Character.unlockSpells(Character.getSpell(element));
		}
	}
}