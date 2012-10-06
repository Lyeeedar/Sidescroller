import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author Lyeeedar
 *
 */
public class Character {
	
	public static long timePlayed;

	public static ArrayList<HashMap<String, Item>> inventory = new ArrayList<HashMap<String, Item>>();
	public static SpellsStageEntry[] socketedSpells = new SpellsStageEntry[5];
	
	/**
	 *  0 = female
	 *  1 = male
	 */
	public static int gender = 1;
	public static long genderSwapCD = 0;
	
	public static int genderSwapAnimStage = 0;
	public static int genderSwapAnimCD = 0;
	public static boolean genderSwapAnimating = false;
	public static BufferedImage genderSwapSprite = GameData.getImage("Data/Resources/Spritesheets/transform.png");
	
	public static long[] spellCooldown = new long[5];
	
	public static Sigil[] equippedSigils = new Sigil[6];
	
	public static void equipSigil(Sigil sigil)
	{
		int index = 0;
		
		if (sigil.element.equals(Entity.DAMAGE_FIRE))
		{
			index = 0;
		}
		else if (sigil.element.equals(Entity.DAMAGE_AIR))
		{
			index = 1;
		}
		else if (sigil.element.equals(Entity.DAMAGE_EARTH))
		{
			index = 2;
		}
		else if (sigil.element.equals(Entity.DAMAGE_WATER))
		{
			index = 3;
		}
		else if (sigil.element.equals(Entity.DAMAGE_DEATH))
		{
			index = 4;
		}
		else if (sigil.element.equals(Entity.DAMAGE_LIFE))
		{
			index = 5;
		}
		
		if (equippedSigils[index] != null)
			equippedSigils[index].unequip();
		equippedSigils[index] = sigil;
		equippedSigils[index].equip();
	}
	
	public static void unequipSigil(Sigil sigil)
	{
		int index = 0;
		
		if (sigil.element.equals(Entity.DAMAGE_FIRE))
		{
			index = 0;
		}
		else if (sigil.element.equals(Entity.DAMAGE_AIR))
		{
			index = 1;
		}
		else if (sigil.element.equals(Entity.DAMAGE_EARTH))
		{
			index = 2;
		}
		else if (sigil.element.equals(Entity.DAMAGE_WATER))
		{
			index = 3;
		}
		else if (sigil.element.equals(Entity.DAMAGE_DEATH))
		{
			index = 4;
		}
		else if (sigil.element.equals(Entity.DAMAGE_LIFE))
		{
			index = 5;
		}
		
		equippedSigils[index].unequip();
		equippedSigils[index] = null;
	}
	
	public static void addEXP(int amount)
	{
		
	}
	
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
	
	public static void beginGenderSwap()
	{
		if (genderSwapCD > 0)
			return;

		genderSwapCD = 5000;
		
		genderSwapAnimating = true;
	}
	
	public static void swapGender()
	{
		
		if (gender == 0)
		{
			gender = 1;
			Main.gamedata.getGameEntities().get("Player").setSpriteSheet(GameData.getImage("Data/Resources/Spritesheets/male.png"));
			Main.gamedata.getGameEntities().get("Player").setSpriteFile("Data/Resources/Spritesheets/male.png");
		}
		else
		{
			gender = 0;
			Main.gamedata.getGameEntities().get("Player").setSpriteSheet(GameData.getImage("Data/Resources/Spritesheets/female.png"));
			Main.gamedata.getGameEntities().get("Player").setSpriteFile("Data/Resources/Spritesheets/female.png");
		}
		
	}
	
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
		String[] icons = {"Data/Resources/GUI/spellIconLife.png","Data/Resources/GUI/spellIconLifeSelected.png"};
		
		ArrayList<SpellsStage> spells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			spells.add(new SpellsStage());
		}
		
		spells.get(0).spells.add(new SpellsStageEntry("Weak Heal", null, 3, new int[]{500, 100},
				icons,
				"Heal a little of your health."));
		
		lifeSpells = spells;
	}
	
	public static void populateDeathSpells()
	{
		String[] icons = {"Data/Resources/GUI/spellIconDeath.png","Data/Resources/GUI/spellIconDeathSelected.png"};
		
		ArrayList<SpellsStage> spells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			spells.add(new SpellsStage());
		}
		
		spells.get(0).spells.add(new SpellsStageEntry("Summon imp", null, 3, new int[]{500, 100},
				icons,
				"Summon an imp to attack your foe."));
		
		deathSpells = spells;
	}
	
	public static void populateWaterSpells()
	{
		String[] icons = {"Data/Resources/GUI/spellIconWater.png","Data/Resources/GUI/spellIconWaterSelected.png"};
		
		ArrayList<SpellsStage> spells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			spells.add(new SpellsStage());
		}
		
		spells.get(0).spells.add(new SpellsStageEntry("IceSpike", null, 3, new int[]{500, 100},
				icons,
				"Send a spike of ice at your foe."));
		
		waterSpells = spells;
	}
	
	public static void populateEarthSpells()
	{
		String[] icons = {"Data/Resources/GUI/spellIconEarth.png","Data/Resources/GUI/spellIconEarthSelected.png"};
		
		ArrayList<SpellsStage> spells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			spells.add(new SpellsStage());
		}
		
		spells.get(0).spells.add(new SpellsStageEntry("RockSpike", null, 3, new int[]{500, 100},
				icons,
				"Send a series of rock spikes flying out in front of you."));
		
		spells.get(1).spells.add(new SpellsStageEntry("RockWall", null, 3, new int[]{400, 200},
				icons,
				"Raise a wall of rock from the ground to stop foes."));
		
		spells.get(1).spells.add(new SpellsStageEntry("RockShot", null, 3, new int[]{600, 200},
				icons,
				"Send a large rock flying at an opponent."));
		
		earthSpells = spells;
	}
	
	public static void populateAirSpells()
	{
		String[] airIcons = {"Data/Resources/GUI/spellIconAir.png","Data/Resources/GUI/spellIconAirSelected.png"};
		
		airSpells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			airSpells.add(new SpellsStage());
		}
		
		airSpells.get(0).spells.add(new SpellsStageEntry("WindBlade", null, 3, new int[]{500, 100},
				airIcons,
				"Slice a foe with a blade of air."));
	}
	
	public static void populateFireSpells()
	{
		String[] fireIcons = {"Data/Resources/GUI/spellIconFire.png","Data/Resources/GUI/spellIconFireSelected.png"};
		
		fireSpells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			fireSpells.add(new SpellsStage());
		}
		
		fireSpells.get(0).spells.add(new SpellsStageEntry("FireBall", null, 3, new int[]{500, 100},
				fireIcons,
				"A ball of burning fire. Will singe a target somewhat fierce."));
		
		fireSpells.get(1).spells.add(new SpellsStageEntry("Scorch", null, 3, new int[]{400, 200},
				fireIcons,
				"Sets fire to a spot, scorching all who enter it."));
		
		fireSpells.get(1).spells.add(new SpellsStageEntry("FlameWall", null, 3, new int[]{600, 200},
				fireIcons,
				"Creates a wall of fire, burning any who pass through it."));
		
		fireSpells.get(2).spells.add(new SpellsStageEntry("WildFire", new String[]{"Scorch"}, 3, new int[]{400, 300},
				fireIcons,
				"Sets fire to a line, scorching all who enter it."));

	
		unlockSpells(fireSpells);
	}
	
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
				else if (sse.parents == null)
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
	
	
	public static ArrayList<SpellsStage> getSpell(String name)
	{
		if (name.equals("Fire"))
		{
			return fireSpells;
		}
		else if (name.equals("Air"))
		{
			return airSpells;
		}
		else if (name.equals("Earth"))
		{
			return earthSpells;
		}
		else if (name.equals("Water"))
		{
			return waterSpells;
		}
		else if (name.equals("Death"))
		{
			return deathSpells;
		}
		else if (name.equals("Life"))
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


class SpellsStage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2385401053100828396L;
	public ArrayList<SpellsStageEntry> spells = new ArrayList<SpellsStageEntry>();
	
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
	
	public void loadImages()
	{
		for (SpellsStageEntry sse : spells)
		{
			sse.loadImage();
		}
	}
}

class SpellsStageEntry implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1455799641945222577L;
	public String name;
	public ArrayList<String> parents = new ArrayList<String>();
	public int unlocked;
	public int[] pos;
	public String[] imageFiles;
	public transient BufferedImage[] images;
	public String description;
	
	public SpellsStageEntry(String name, String[] parents, int unlocked, int[] pos, String[] imageFiles, String description)
	{
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
	
	public void loadImage()
	{
		images = new BufferedImage[2];
		for (int i = 0; i < 2; i++)
			images[i] = GameData.getImage(imageFiles[i]);
	}
	
	public int isUnlocked()
	{
		return unlocked;
	}
}