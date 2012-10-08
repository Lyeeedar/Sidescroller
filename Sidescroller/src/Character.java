import java.awt.Color;
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
	
	public static void addEXP(int amount)
	{
		for (int i = 0; i < 5; i++)
		{
			socketedSpells[i].addEXP(amount);
		}
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
		String element = Entity.LIFE;
		
		ArrayList<SpellsStage> spells = new ArrayList<SpellsStage>();
		
		for (int i = 0 ; i < 6; i++)
		{
			spells.add(new SpellsStage());
		}
		
		spells.get(0).spells.add(new SpellsStageEntry("SmallHeal", null, 2, new int[]{500, 100},
				new String[]{"Data/Resources/GUI/Life/spellIconSmallHeal.png","Data/Resources/GUI/Life/spellIconLifeMaster.png"},
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
				new String[]{"Data/Resources/GUI/Death/spellIconImpI.png","Data/Resources/GUI/Death/spellIconDeathMaster.png"},
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
				new String[]{"Data/Resources/GUI/Water/spellIconIceSpike.png","Data/Resources/GUI/Water/spellIconWaterMaster.png"},
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
				new String[]{"Data/Resources/GUI/Earth/spellIconRockSpike.png","Data/Resources/GUI/Earth/spellIconEarthMaster.png"},
				"Send a series of rock spikes flying out in front of you.", 500, element));
		
		spells.get(1).spells.add(new SpellsStageEntry("RockWall", null, 0, new int[]{400, 200},
				new String[]{"Data/Resources/GUI/Earth/spellIconRockWall.png","Data/Resources/GUI/Earth/spellIconEarthMaster.png"},
				"Raise a wall of rock from the ground to stop foes.", 580, element));
		
		spells.get(1).spells.add(new SpellsStageEntry("Stone", new String[]{"RockSpike"}, 0, new int[]{600, 200},
				new String[]{"Data/Resources/GUI/Earth/spellIconStone.png","Data/Resources/GUI/Earth/spellIconEarthMaster.png"},
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
				new String[]{"Data/Resources/GUI/Air/spellIconWindBlade.png","Data/Resources/GUI/Air/spellIconAirMaster.png"},
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
				new String[]{"Data/Resources/GUI/Fire/spellIconFireBall.png","Data/Resources/GUI/Fire/spellIconFireMaster.png"},
				"A ball of burning fire. Will singe a target somewhat fierce.", 500, element));
		
		fireSpells.get(1).spells.add(new SpellsStageEntry("Scorch", null, 2, new int[]{400, 200},
				new String[]{"Data/Resources/GUI/Fire/spellIconScorch.png","Data/Resources/GUI/Fire/spellIconFireMaster.png"},
				"Sets fire to a spot, scorching all who enter it.", 700, element));
		
		fireSpells.get(1).spells.add(new SpellsStageEntry("FlameWall", new String[]{"FireBall"}, 0, new int[]{600, 200},
				new String[]{"Data/Resources/GUI/Fire/spellIconFlameWall.png","Data/Resources/GUI/Fire/spellIconFireMaster.png"},
				"Creates a wall of fire, burning any who pass through it.", 580, element));
		
		fireSpells.get(2).spells.add(new SpellsStageEntry("WildFire", new String[]{"Scorch"}, 0, new int[]{400, 300},
				new String[]{"Data/Resources/GUI/Fire/spellIconWildFire.png","Data/Resources/GUI/Fire/spellIconFireMaster.png"},
				"Sets fire to a line, scorching all who enter it.", 900, element));

	
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
	public int maxEXP;
	public int currentEXP = 0;
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