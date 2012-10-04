import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to model a game entity. Entity behavior determined by AI packages, defined in {@link Entity#behavior}
 * @author Lyeeedar
 *
 */
public class Entity implements Serializable{

	/**
	 * 
	 */
	protected static final long serialVersionUID = -8723388619027762099L;

	public static final String DAMAGE_PHYSICAL = "DPHYS";
	public static final String DAMAGE_FIRE = "DFIRE";
	public static final String DAMAGE_AIR = "DAIR";
	public static final String DAMAGE_EARTH = "DEARTH";
	public static final String DAMAGE_WATER = "DWATER";
	public static final String DAMAGE_DEATH = "DDEATH";
	public static final String DAMAGE_LIFE = "DLIFE";

	/**
	 * The internal name of the Entity
	 */
	protected String name = "";

	protected String faction = "";

	/**
	 *  Total animation stages
	 */
	public int animStages = 8;

	/**
	 *  Total time between animation updates
	 */
	protected long animateTime;
	/**
	 *  Remaining time until the next animation update
	 */
	protected long remainingAnimateTime;
	/**
	 *  Stage of the animation (Horizontal, out of animStages). 1 indexed
	 */
	protected int animateStage;
	/**
	 *  Current animation strip (Vertical). 1 indexed
	 */
	protected int animateStrip;
	/**
	 *  Total number of animation strips
	 */
	protected int totalAnimateStrip;
	/**
	 *  Position. X, Y and then direction <p>
	 *  0 = left <p>
	 *  1 = right <p>
	 */
	protected int[] pos = new int[3];

	/**
	 * Velocity. X, Y
	 */
	protected int[] velocity = new int[2];

	/**
	 * Weight of the Entity, changes the effect gravity has on it. <p>
	 * 1 = normal effect <p>
	 * 0 = no effect <p>
	 * 2 = double effect <p>
	 * etc.
	 */
	protected int weight = 1;

	/**
	 * Whether the entity is on the ground or in the air
	 */
	protected boolean grounded = false; 

	/**
	 * Number used by various methods to determine when to change the animation
	 */
	protected int animChangeCtr;

	/**
	 *  Sprite sheet for this Entity. Value of null = invisible
	 */
	protected transient BufferedImage spriteSheet;
	/**
	 *  Whether the Entity is visible or not
	 */
	protected boolean visible = true;

	/**
	 * Whether that entity is collidable or not
	 */
	protected boolean passable = false;

	/**
	 * The size of the entity. Width, Height
	 */
	protected int[] size = new int[2];

	/**
	 *  Array to determine entity behavior: <p>
	 *  0 = Takes player input for actions - {@link Entity#behavior0} <p>
	 *  1 = Effected by Physics - {@link Entity#behavior1} <p>
	 *  2 = Simple enemy AI - {@link Entity#behavior2()} <p>
	 *  3 = Activate dialogue when player steps into it <p>
	 */
	protected boolean[] behavior;

	/**
	 * The Entities dialogue. See {@link Dialogue} for more details.
	 */
	protected Dialogue dialogue;

	/**
	 * Whether the entity is talking or not. (Displays text above head)
	 */
	protected boolean talking;

	/**
	 * Internal timer for how long it has been since this Entity has been talked to.
	 */
	protected long talkingTimer = 0;

	/**
	 * The shape to use for collision detection.
	 */
	protected int[] collisionShape;

	/**
	 * File for the spritesheet.
	 */
	protected String spriteFile;

	/**
	 * Whether the entity is alive or not
	 */
	protected boolean alive = true;

	protected double maxHealth = 100;

	/**
	 * The Entities current health
	 */
	protected double health = 100;

	/**
	 * The elemental defense for the entity. Elements are: <br>
	 * {@link Entity#DAMAGE_PHYSICAL} <br>
	 * {@link Entity#DAMAGE_FIRE} <br>
	 * {@link Entity#DAMAGE_AIR} <br>
	 * {@link Entity#DAMAGE_EARTH} <br>
	 * {@link Entity#DAMAGE_WATER} <br>
	 * {@link Entity#DAMAGE_DEATH} <br>
	 * {@link Entity#DAMAGE_LIFE} <br>
	 */
	protected HashMap<String, Double> defense = new HashMap<String, Double>();

	/**
	 * Cooldown until this entity can cast a spell again
	 */
	protected long spellCD = 0;

	/**
	 * All the spells that this entity knows
	 */
	protected ArrayList<String> spells = new ArrayList<String>();

	/**
	 * Whether this entity has been damaged
	 */
	protected int damaged = 0;

	protected int newAnimStrip = 1;
	protected int newAnimStage = 0;

	protected boolean isAnimating = false;

	protected Spell spellToCast = null;
	protected int castSpellAt = 0;
	protected int[] castSpellOffset;
	protected int castSpellIndex;

	protected HashMap<String, Integer> dropList = new HashMap<String, Integer>();

	protected int speed;
	
	protected int expAmount = 0;

	protected ArrayList<SystemMessage> infoText = new ArrayList<SystemMessage>();
	/**
	 * @param animateTime
	 * @param totalAnimateStrip
	 * @param pos - int[3], X, Y, dir
	 * @param spriteSheet
	 * @param collision - int[4], X, Y, width, height
	 * @param behaviour
	 * @param dialogue
	 */
	public Entity(String name, long animateTime, int totalAnimateStrip, int totalAnimateStages, int[] pos, int speed, String spritefile, int[] collision, boolean[] behaviour, Dialogue dialogue)
	{
		this.name = name;
		this.animateTime = animateTime;
		this.remainingAnimateTime = animateTime;
		this.totalAnimateStrip = totalAnimateStrip;
		this.animateStage = 1;
		this.animateStrip = 1;
		this.pos[0] = pos[0];
		this.pos[1] = pos[1];
		this.pos[2] = pos[2];
		this.behavior = behaviour;
		this.animStages = totalAnimateStages;
		this.speed = speed;

		if (dialogue != null)
			this.dialogue = dialogue;
		else
		{
			ArrayList<ArrayList<String>> dia = new ArrayList<ArrayList<String>>();
			this.dialogue = new Dialogue(dia, 0);
		}

		this.spriteFile = spritefile;

		processSpritesheet();

		collisionShape = collision;

		spells.add("Fireball");

		defense.put(Entity.DAMAGE_PHYSICAL, (double) 0);
		defense.put(Entity.DAMAGE_FIRE, (double) 0);
		defense.put(Entity.DAMAGE_AIR, (double) 0);
		defense.put(Entity.DAMAGE_EARTH, (double) 0);
		defense.put(Entity.DAMAGE_WATER, (double) 0);
		defense.put(Entity.DAMAGE_DEATH, (double) 0);
		defense.put(Entity.DAMAGE_LIFE, (double) 0);

	}

	/**
	 * Method to evaluate the AI for the Entity. <p>
	 * Looks through the {@link Entity#behavior} for 'true's and then evaluates the AI packages in order from 0++
	 */
	public void AI()
	{
		if (behavior[1])
			behavior1();

		if (!this.isAlive())
			return;

		if (behavior[0])
			behavior0();

		if (behavior[2])
			behavior2();

		if ((behavior.length > 3) && (behavior[3]))
			behavior3();
	}

	/**
	 * Method that takes player input (from {@link MainFrame}) to control entity. <p>
	 * Moves the Entity depending on the state of the keys 'up', 'left' and 'right' <p>
	 * Performs actions on state of other keys. <p>
	 */
	protected void behavior0()
	{
		// Move left and right
		if (MainFrame.left)
		{
			this.getVelocity()[0] = -speed;
			this.getPos()[2] = 0;

			if ((animateStage != 1) && (animateStrip == 2))
				newAnimStrip = 1;
		}
		else if (MainFrame.right)
		{
			this.getVelocity()[0] = speed;
			this.getPos()[2] = 1;

			if ((animateStage != 1) && (animateStrip == 2))
				newAnimStrip = 1;
		}
		else
		{
			if (animateStrip == 1)
			{
				newAnimStrip = 2;
				newAnimStage = 2;

			}
		}

		// Jump
		if ((MainFrame.up) && (this.isGrounded()))
		{
			this.getVelocity()[1] -= 25;
		}

		// Activate infront of Entity
		if (MainFrame.enter)
		{
			// Make sure that you can super speedily iterate through all the dialogue
			MainFrame.enter = false;
			Rectangle r = null;

			// Create a rectangle representing the area to be activated
			if (this.getPos()[2] == 0)
			{
				r = new Rectangle(pos[0]-size[0]/2, pos[1], size[0], size[1]);
			}
			else
			{
				r = new Rectangle(pos[0]+size[0]/2, pos[1], size[0], size[1]);
			}

			// See if any Entities lie within this rectangle
			for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
			{
				Entity e = entry.getValue();

				Rectangle rn = new Rectangle(e.getPos()[0]+e.getCollisionShape()[0], e.getPos()[1]+e.getCollisionShape()[1],
						e.getCollisionShape()[2], e.getCollisionShape()[3]);

				if ((!e.equals(this)) && (r.intersects(rn)))
				{
					e.activate();
				}
			}

		}
		
		if (MainFrame.space)
		{
			Character.beginGenderSwap();
			
			MainFrame.tab = false;
		}

		// Cast the spell bound to Key1 (the number 1 key on the keyboard)
		if (MainFrame.key1)
		{
			// If still in cooldown then don't allow spell casting 
			if ((Character.spellCooldown[0] > 0) || (isAnimating))
				return;

			newAnimStrip = 3;

			int[] pos = {0, 0, this.getPos()[2]};

			if (this.getPos()[2] == 0){
				pos[0] = this.getCollisionShape()[0]-10;
			}
			else
			{
				pos[0] =this.getCollisionShape()[0]+this.getCollisionShape()[2]+10;
			}

			pos[1] = this.getCollisionShape()[1]+(this.getCollisionShape()[3]/2)-50;

			Spell s = SpellList.getSpell(Character.socketedSpells[0].name, pos, this.getName());
			s.setFaction(this.getFaction());

			castSpellOffset = pos;

			castSpellIndex = 0;

			spellToCast = s;
			castSpellAt = 5;
		}
		else if (MainFrame.key2)
		{
			// If still in cooldown then don't allow spell casting 
			if ((Character.spellCooldown[1] > 0) || (isAnimating))
				return;

			newAnimStrip = 4;

			int[] pos = {0, 0, this.getPos()[2]};

			if (this.getPos()[2] == 0){
				pos[0] = this.getCollisionShape()[0]-10;
			}
			else
			{
				pos[0] =this.getCollisionShape()[0]+this.getCollisionShape()[2]+10;
			}

			pos[1] = this.getCollisionShape()[1]+(this.getCollisionShape()[3]/2)-45;

			Spell s = SpellList.getSpell(Character.socketedSpells[1].name, pos, this.getName());
			s.setFaction(this.getFaction());

			castSpellOffset = pos;

			castSpellIndex = 1;

			spellToCast = s;
			castSpellAt = 5;
		}
		else if (MainFrame.key3)
		{
			// If still in cooldown then don't allow spell casting 
			if ((Character.spellCooldown[2] > 0) || (isAnimating))
				return;

			newAnimStrip = 5;

			int[] pos = {0, 0, this.getPos()[2]};

			if (this.getPos()[2] == 0){
				pos[0] = this.getCollisionShape()[0]-10;
			}
			else
			{
				pos[0] =this.getCollisionShape()[0]+this.getCollisionShape()[2]+10;
			}

			pos[1] = this.getCollisionShape()[1]+(this.getCollisionShape()[3]/2)-45;

			Spell s = SpellList.getSpell(Character.socketedSpells[2].name, pos, this.getName());
			s.setFaction(this.getFaction());

			castSpellOffset = pos;

			castSpellIndex = 2;

			spellToCast = s;
			castSpellAt = 5;
		}
		else if (MainFrame.key4)
		{
			// If still in cooldown then don't allow spell casting 
			if ((Character.spellCooldown[3] > 0) || (isAnimating))
				return;

			newAnimStrip = 6;

			int[] pos = {0, 0, this.getPos()[2]};

			if (this.getPos()[2] == 0){
				pos[0] = this.getCollisionShape()[0]-10;
			}
			else
			{
				pos[0] =this.getCollisionShape()[0]+this.getCollisionShape()[2]+10;
			}

			pos[1] = this.getCollisionShape()[1]+(this.getCollisionShape()[3]/2)-15;

			Spell s = SpellList.getSpell(Character.socketedSpells[3].name, pos, this.getName());
			s.setFaction(this.getFaction());

			castSpellOffset = pos;

			castSpellIndex = 3;

			spellToCast = s;
			castSpellAt = 5;
		}
		else if (MainFrame.key5)
		{
			// If still in cooldown then don't allow spell casting 
			if ((Character.spellCooldown[4] > 0) || (isAnimating))
				return;

			newAnimStrip = 7;

			int[] pos = {0, 0, this.getPos()[2]};

			if (this.getPos()[2] == 0){
				pos[0] = this.getCollisionShape()[0]-10;
			}
			else
			{
				pos[0] =this.getCollisionShape()[0]+this.getCollisionShape()[2]+10;
			}

			pos[1] = this.getCollisionShape()[1]+(this.getCollisionShape()[3]/2)-15;

			Spell s = SpellList.getSpell(Character.socketedSpells[4].name, pos, this.getName());
			s.setFaction(this.getFaction());

			castSpellOffset = pos;

			castSpellIndex = 4;

			spellToCast = s;
			castSpellAt = 5;
		}

	}

	/**
	 * Method to simulate physics. Will move the Entity depending on the values in
	 *  {@link Entity#velocity} and {@link Main#gravity}. <p>
	 *  It will also check for collision using {@link Entity#checkCollision(int[])}.
	 */
	public void behavior1()
	{
		boolean grounded = this.isGrounded();

		// Scale X velocity so it can never exceed 30
		if ((velocity[0] < 0) && (velocity[0] < -30))
		{
			velocity[0] = -30;
		}
		else if ((velocity[0] > 0) && (velocity[0] > 30))
		{
			velocity[0] = 30;
		}


		// Modify the Y velocity with gravity
		velocity[1] += (weight * GameData.gravity);


		// Scale Y velocity so it never exceeds 40
		if (velocity[1] > 40)
		{
			velocity[1] = 40;
		}

		// Work out positions that the entity is trying to move to
		int[] cpos = {this.getPos()[0]+velocity[0], this.getPos()[1]+velocity[1]};

		// If there is no collision at this point then all is well and move the entity to this point
		if (checkCollision(cpos) == null)
		{
			this.changePosition(cpos[0], cpos[1], this.getPos()[2]);
			this.setGrounded(false);

			updateJumpAnim(grounded);

			return;
		}

		// Extract and store current position for individual axis checking
		int[] pos = {this.pos[0], this.pos[1]};

		// Modify new position by the velocity of the entity in the X direction
		pos[0] += velocity[0];

		// Check if moving in the X direction would cause a collision, if so then see if the entity can slide up the slope to get to a valid position. 
		// If no position is found then just reset X to the old X. If a position is found then the entity will be on the ground, so the entity can be moved
		// and no more checks need to be done.
		if (checkCollision(pos) != null)
		{
			int[] npos = {this.getPos()[0], pos[1]};
			int val = Math.abs(velocity[0]);
			int x = val;
			int y = 0;

			for (int i = 1; i <= val-2; i++)
			{
				x = val-i;
				y = i;

				if (velocity[0] < 0)
				{
					npos[0] = this.getPos()[0] - x;
				}
				else
				{
					npos[0] = this.getPos()[0] + x;
				}
				npos[1] = pos[1] - y;

				if (checkCollision(npos) == null)
				{
					velocity[1] = 0;
					applyFriction();
					this.changePosition(npos[0], npos[1], this.getPos()[2]);
					this.setGrounded(true);
					updateJumpAnim(grounded);
					return;
				}
			}

			pos[0] = this.getPos()[0];
		}

		applyFriction();

		// Move the Entity to the new position
		this.changePosition(pos[0], pos[1], this.pos[2]);	

		// Modify the new Y position depending on velocity
		pos[1] += velocity[1];

		// Check if there is a collision on the Y axis.
		if (checkCollision(pos) != null)
		{
			// If velocity equals weight*gravity then it has just hit a surface, so assume its on the ground
			if (velocity[1] == (this.weight * GameData.gravity))
			{
				velocity[1] = 0;
				pos[1] = this.pos[1];
				this.setGrounded(true);
			}
			// Else check if the velocity is positive. If it is then the Entity is falling
			// and therefore the ground is somewhere between the entity position and the 
			// entity position + Yvelocity. So find out where and put it on the ground.
			else if (velocity[1] > 0)
			{
				velocity[1] = 0;
				pos[1] = this.pos[1];

				for (int i = 0; i < 40; i++)
				{
					if (checkCollision(pos) != null)
					{
						break;
					}
					pos[1]++;
				}
			}
			else
			{
				velocity[1] = 0;
				pos[1] = this.pos[1];
				this.setGrounded(false);
			}
		}

		// Move the Entity to the new position
		this.changePosition(pos[0], pos[1], this.pos[2]);

		updateJumpAnim(grounded);

		if (velocity[1] < 0)
		{
			this.setGrounded(false);
		}
	}

	boolean alerted = false;
	int patrolDistance = 300;
	int[] lastTargetPos;
	/**
	 * Very simple enemy AI. Just moves towards player and fires off its spells randomly.
	 */
	public void behavior2()
	{
		if (alerted)
		{
			if (lastTargetPos[0] < pos[0])
			{
				velocity[0] = -(speed+2);
				pos[2] = 0;
			}
			else
			{
				velocity[0] = speed+2;
				pos[2] = 1;
			}
			newAnimStrip = 1;
			
			if ((Main.ran.nextInt(340) == 1) && (grounded))
			{
				velocity[1] -= 15;
			}
			
			if ((Math.abs(pos[0] - lastTargetPos[0]) < 50) &&
					(Math.abs(pos[1] - lastTargetPos[1]) < 50))
			{
				if (Math.abs(pos[0] - lastTargetPos[0]) < 50)
				{
					alerted = false;
				}
				if ((this.spellCD > 0) || (isAnimating))
					return;

				newAnimStrip = 3;

				int[] pos = {0, 0, this.getPos()[2]};

				Spell s = SpellList.getStrike(Entity.DAMAGE_PHYSICAL, 5, pos, name, collisionShape);
				s.setFaction(this.getFaction());

				castSpellOffset = pos;

				this.spellCD = s.spellCDTime;

				spellToCast = s;
				castSpellAt = 5;
				
			}
		}
		else
		{
			if (pos[2] == 0)
			{
				velocity[0] = -(speed);
			}
			else
			{
				velocity[0] = (speed);
			}

			newAnimStrip = 1;

			patrolDistance -= 3;

			if (patrolDistance <= 0)
			{
				patrolDistance = (Main.ran.nextInt(3)+3)*100;
				if (pos[2] == 0)
				{
					pos[2] = 1;
				}
				else
				{
					pos[2] = 0;
				}
			}

			
		}
		
		String s = enemyRayCast();

		if ((s != null) && (s != this.getName()))
		{
			Entity e = Main.gamedata.getGameEntities().get(s);
			if (e != null)
			{
				alerted = true;
				lastTargetPos = e.getPos();
			}
		}
		else
		{
			alerted = false;
		}

	}

	/**
	 * This behavior makes the entity initiate dialogue when being stepped onto by the entity 
	 */
	public void behavior3()
	{
		Entity p = Main.gamedata.getGameEntities().get("Player");
		
		Rectangle rp = new Rectangle(p.getPos()[0]+p.getCollisionShape()[0], p.getPos()[1]+p.getCollisionShape()[1],
				p.getCollisionShape()[2], p.getCollisionShape()[3]);
		
		Rectangle re = new Rectangle(getPos()[0]+getCollisionShape()[0], getPos()[1]+getCollisionShape()[1],
				getCollisionShape()[2], getCollisionShape()[3]);
		
		if (re.intersects(rp))
		{
			this.activate();
		}
	}

	/**
	 * Update the entity jump animation to reflect if the entity is in the air or on the ground. The entity needs to be in the air for 5 AI updates for the animation to change.
	 * @param grounded
	 */
	protected void updateJumpAnim(boolean grounded)
	{		
		if (newAnimStrip > 2)
		{
			return;
		}
		else if (this.isGrounded())
		{
			if ((newAnimStrip != 1) && (animateStrip == 2) && (animateStage == 1))
			{
				newAnimStrip = 2;
				newAnimStage = 2;
			}
			animChangeCtr = 0;
		}
		else
		{
			animChangeCtr++;

			if (animChangeCtr > 2)
			{
				newAnimStrip = 2;
				newAnimStage = 1;
				animChangeCtr = 0;
			}
		}
	}

	protected void applyFriction()
	{
		velocity[0] = 0;
	}
	
	public String enemyRayCast()
	{
		int totDist = 250*250;
		
		ArrayList<Entity> rayEntities = new ArrayList<Entity>();
		
		for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
		{
			Entity e = entry.getValue();
			
			if ((!e.getFaction().equals("")) && (!e.getFaction().equals(faction)) && (!e.getName().equals(name)) && (!e.isPassable()))
			{
				if (pos[2] == 0)
				{
					if (e.getPos()[0] < pos[0])
					{
						if (((e.getPos()[0]-pos[0])*(e.getPos()[0]-pos[0]))+((e.getPos()[1]-pos[1])*(e.getPos()[1]-pos[1])) < totDist)
						{
							rayEntities.add(e);
						}
					}
				}
				else
				{
					if (e.getPos()[0] > pos[0])
					{
						if (((e.getPos()[0]-pos[0])*(e.getPos()[0]-pos[0]))+((e.getPos()[1]-pos[1])*(e.getPos()[1]-pos[1])) < totDist)
						{
							rayEntities.add(e);
						}
					}
				}
			}
		}
		
		String s = null;
		
		int minDist = totDist;
		
		for (Entity e : rayEntities)
		{
			if (rayCast(e.getPos()))
			{
				if (((e.getPos()[0]-pos[0])*(e.getPos()[0]-pos[0]))+((e.getPos()[1]-pos[1])*(e.getPos()[1]-pos[1])) < minDist)
				{
					s = e.getName();
					minDist = ((e.getPos()[0]-pos[0])*(e.getPos()[0]-pos[0]))+((e.getPos()[1]-pos[1])*(e.getPos()[1]-pos[1]));
				}
			}
		}
		
		return s;
	}

	public boolean rayCast(int[] targetpos)
	{
		ArrayList<int[]> pointList = BresenhamsLineAlgorithm(pos[0]+collisionShape[0], pos[1]+collisionShape[1], targetpos[0], targetpos[1]);
		
		for (int[] point : pointList)
		{
			if ((point[0] < 0) || (point[0] > Main.gamedata.collisionMap.length)
					|| (point[1] < 0) || (point[1] > Main.gamedata.collisionMap[0].length))
			{
				return false;
			}
			
			if (Main.gamedata.collisionMap[point[0]][point[1]])
			{
				return false;
			}

		}


		return true;
	}

	public ArrayList<int[]> BresenhamsLineAlgorithm(int x0,int y0,int x1, int y1) {

		ArrayList<int[]> points = new ArrayList<int[]>();

		int dx = Math.abs(x1-x0);
		int dy = Math.abs(y1-y0);

		int sx, sy, e2;

		if (x0 < x1) sx = 1; else sx = -1;
		if (y0 < y1) sy = 1; else sy = -1;

		int err = dx-dy;

		while(true){
			points.add(new int[]{x0, y0});
			if ((x0 == x1) && (y0 == y1)) break;
			e2 = 2*err;
			if (e2 > -dy)
			{
				err = err - dy;
				x0 = x0 + sx;
			}
			if (e2 <  dx) 
			{
				err = err + dx;
				y0 = y0 + sy;
			}
		}

		return points;
	}

	/**
	 * Method to check if this entity will collide with another entity or a surface if moved to the position given. <p>
	 * Returns null for no collision, this Entities name if theres is a collision with the level or the name of the Entity collided with.
	 * @return
	 */
	public String checkCollision(int[] pos)
	{
		int x = pos[0]+collisionShape[0];
		int y = pos[1]+collisionShape[1];

		// If the entity is trying to move outside the bounds of the level then return to say that its colliding with the level.
		if ((x < 0) || (x+collisionShape[2] > GameData.levelSize[0])
				|| (y < 0) || (y+collisionShape[3] > GameData.levelSize[1]))
		{
			return this.getName();
		}

		String s = this.collideEntities(pos);
		if (s != null)
			return s;

		// Check base
		for (int nx = x; nx < x+collisionShape[2]; nx++)
		{
			for (int ny = y+collisionShape[3]-1; ny >= y+collisionShape[3]-4; ny--)
			{
				if (nx >= Main.gamedata.getCollisionMap().length)
					return this.getName();
				else if (Main.gamedata.getCollisionMap()[nx][ny])
					return this.getName();
			}
		}
		
		// Check top
		for (int nx = x; nx < x+collisionShape[2]; nx++)
		{
			for (int ny = y; ny < y+3; ny++)
			{
				if (nx >= Main.gamedata.getCollisionMap().length)
					return this.getName();
				else if (Main.gamedata.getCollisionMap()[nx][ny])
					return this.getName();
			}
		}
		
		//Check side
		if (this.pos[2] == 0)
		{
			for (int nx = x; nx < x+3; nx++)
			{
				for (int ny = y; ny < y+collisionShape[3]; ny++)
				{
					if (nx >= Main.gamedata.getCollisionMap().length)
						return this.getName();
					else if (Main.gamedata.getCollisionMap()[nx][ny])
						return this.getName();
				}
			}
		}
		else
		{
			for (int nx = x+collisionShape[2]; nx > x+collisionShape[2]-4; nx--)
			{
				for (int ny = y; ny < y+collisionShape[3]; ny++)
				{
					if (nx >= Main.gamedata.getCollisionMap().length)
						return this.getName();
					else if (Main.gamedata.getCollisionMap()[nx][ny])
						return this.getName();
				}
			}
		}
		
//		// Check the collision box for this entity to see if any of the level is inside it (any non-transparent pixels)
//		for (int nx = x; nx < x+collisionShape[2]; nx++)
//		{
//			for (int ny = y+collisionShape[3]-1; ny >= y; ny--)
//			{
//				if (nx >= Main.gamedata.getCollisionMap().length)
//					return this.getName();
//				else if (Main.gamedata.getCollisionMap()[nx][ny])
//					return this.getName();
//			}
//		}

		return null;
	}

	public String collideEntities(int[] pos)
	{
		int x = pos[0]+collisionShape[0];
		int y = pos[1]+collisionShape[1];

		// Create a rectangle simulating the collision box of the entity
		Rectangle r = new Rectangle(x, y, collisionShape[2], collisionShape[3]);

		// Iterate through all the game Entities and check if there is a collision is an entity that isnt passable
		for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
		{
			Entity e = entry.getValue();
			if ((e.getName().equals(this.getName())) || (e.isPassable()) || ((e.getFaction() != null) && (e.getFaction().equals(this.getFaction()))))
				continue;

			if(e.getFaction().equals(""))
			{
				continue;
			}
			
			// Create a collision box for the entity that is being checked
			Rectangle rn = new Rectangle(e.getPos()[0]+e.getCollisionShape()[0], e.getPos()[1]+e.getCollisionShape()[1],
					e.getCollisionShape()[2], e.getCollisionShape()[3]);

			// If the two rectangles intersect (overlap) then return the entity name that the collision happened with
			if (r.intersects(rn))
			{
				return e.getName();
			}
		}

		return null;
	}


	/**
	 * Method that allows this entity to be talked to
	 */
	public void activate()
	{
		if (!this.isAlive())
			return;

		if (dialogue == null)
			return;
		if (!this.isTalking())
			this.setTalking(true);
		else if (this.getTalkingTimer() < Dialogue.dialogueFade)
		{
			this.setTalkingTimer(0);
			this.getDialogue().incrStage();
		}
		else if (this.getTalkingTimer() < Dialogue.dialogueFade+Dialogue.fadeDuration)
			this.setTalkingTimer(0);


	}

	/**
	 * Update all values based on game time
	 * @param time
	 */
	public void updateTime(long time)
	{
		this.spellCD -= time;
		animate(time);
		
		ArrayList<SystemMessage> newInfoText = new ArrayList<SystemMessage>();
		for (SystemMessage sysM : infoText)
		{
			sysM.aliveTime -= time;
			if (sysM.aliveTime > 0)
			{
				newInfoText.add(sysM);
			}
		}
		infoText = newInfoText;
	}

	/**
	 * Method to update the animation stage for the entity
	 * @param time
	 */
	public void animate(long time)
	{
		if (!this.isAlive())
		{
			this.setAnimateStrip(2);
			this.setAnimateStage(3);
			this.setPassable(true);
			return;
		}

		this.remainingAnimateTime -= time;
		if (this.remainingAnimateTime <= 0)
		{
			// --------------------------------------------------------------------------------------------------------			
			this.remainingAnimateTime = this.animateTime;

			// IsAnimating
			if (isAnimating)
			{
				this.animateStage++;

				if ((animateStrip > 1) && (animateStage == castSpellAt))
				{
					if (spellToCast != null)
					{
						spellToCast.pos[0] = this.getPos()[0]+castSpellOffset[0];
						spellToCast.pos[1] = this.getPos()[1]+castSpellOffset[1];

						if (spellToCast.pos[2] == 0)
						{
							spellToCast.pos[0] -= spellToCast.collisionShape[2];
						}

						Main.gamedata.getGameEntities().put(spellToCast.getName()+System.currentTimeMillis(), spellToCast);
						
						if (name.equals("Player"))
							Character.spellCooldown[castSpellIndex] = spellToCast.spellCDTime;
						
						spellToCast = null;
					}
				}

				if (this.animateStage > animStages)
				{
					if (animateStrip > 2)
					{
						isAnimating = false;
						newAnimStrip = 1;
					}

					this.animateStage = 1;
				}
				//animChangeCtr = 0;
			}
			// IsAnimating End

			// newAnimStrip != animateStrip
			else if (newAnimStrip != animateStrip)
			{
				if (newAnimStrip == 1)
				{
					animateStrip = 1;
				}
				else if (newAnimStrip == 2)
				{
					animateStrip = 2;
					animateStage = newAnimStage;

					newAnimStage = 0;
				}
				else if (newAnimStrip > 2)
				{

					animateStage = 1;
					animateStrip = newAnimStrip;
					isAnimating = true;
				}
			}
			// newAnimStrip != animateStrip End

			else if ((newAnimStage > 0) && (newAnimStage != animateStage))
			{
				animateStage = newAnimStage;
				newAnimStage = 0;
			}

			// animateStrip == 1
			else if (animateStrip == 1)
			{	
				this.animateStage++;

				if (this.animateStage > animStages)
				{					
					this.animateStage = 1;
				}
			}
			// animateStrip == 1 End
			// -------------------------------------------------------------------------------------------------------------------------
		}
	}

	/**
	 * Method to change the Entity position. {@link Entity#pos}
	 * @param X
	 * @param Y
	 * @param dir
	 */
	public void changePosition(int X, int Y, int dir)
	{
		pos[0] = X;
		pos[1] = Y;
		pos[2] = dir;
	}

	/**
	 * Method that loads the spritesheet determined by {@link Entity#spriteFile}. Also works out the width and height of a frame for the entity.
	 */
	public void processSpritesheet()
	{

		if (!visible)
			return;

		if (spriteFile != null)
			this.spriteSheet = GameData.getImage(spriteFile);

		// If spritesheet exists (and the Entity is therefore visible) then work out
		// width and height of a frame
		if (spriteSheet != null)
		{
			int width = spriteSheet.getWidth() / animStages;
			int height = spriteSheet.getHeight() / this.getTotalAnimateStrip();

			this.size[0] = width;
			this.size[1] = height;
		}
	}

	/**
	 * method to damage the entity. If the elemental defense for the damage type is not 0 then do this -> amount -= amount/defense
	 * @param amount
	 * @param type
	 */
	public void damage(double amount, String type)
	{		
		double eleDefense = defense.get(type);

		if (eleDefense != 0)
			amount -= amount/defense.get(Entity.DAMAGE_PHYSICAL);

		health -= amount;

		infoText.add(new SystemMessage("-"+amount, Color.RED, 3000));
		
		if (health <= 0)
		{
			death();
			this.setAlive(false);
		}

		this.setDamaged(15);
	}

	public static final String[] deathMessages = {" died!", " bit the dust!", " kicked the bucket!", " became a statistic!"};
	public void death()
	{
		Main.gamedata.systemMessages.add(new SystemMessage(this.getName()+deathMessages[Main.ran.nextInt(deathMessages.length)], Color.GREEN, 10000));

		for (Map.Entry<String, Integer> entry : dropList.entrySet())
		{
			if (Main.ran.nextInt(100) < entry.getValue())
			{
				Main.gamedata.getGameEntities().put(entry.getKey()+System.currentTimeMillis(), ItemList.getItem(entry.getKey(), new int[]{pos[0], pos[1], pos[2]}, 1));
			}
			
		}
		
		int orbExp = this.expAmount/5;
		
		if (orbExp > 0)
		{
			for (int i = 0; i < 5; i++)
			{
				Main.gamedata.getGameEntities().put("EXP"+System.currentTimeMillis()+i, new EXPOrb(new int[]{pos[0], pos[1], pos[2]}, orbExp));
			}
		}
	}















	/**
	 * Returns {@link Entity#animateTime}
	 * @return the animateTime
	 */
	public long getAnimateTime() {
		return animateTime;
	}

	/**
	 * Sets {@link Entity#animateTime}
	 * @param animateTime the animateTime to set
	 */
	public void setAnimateTime(long animateTime) {
		this.animateTime = animateTime;
	}

	/**
	 * Returns {@link Entity#remainingAnimateTime}
	 * @return the remainingAnimateTime
	 */
	public long getRemainingAnimateTime() {
		return remainingAnimateTime;
	}

	/**
	 * Sets {@link Entity#remainingAnimateTime}
	 * @param remainingAnimateTime the remainingAnimateTime to set
	 */
	public void setRemainingAnimateTime(long remainingAnimateTime) {
		this.remainingAnimateTime = remainingAnimateTime;
	}

	/**
	 * Returns {@link Entity#animateStage}
	 * @return the animateStage
	 */
	public int getAnimateStage() {
		return animateStage;
	}

	/**
	 * Sets {@link Entity#animateStage}
	 * @param animateStage the animateStage to set
	 */
	public void setAnimateStage(int animateStage) {
		this.animateStage = animateStage;
	}

	/**
	 * Returns {@link Entity#animateStrip}
	 * @return the animateStrip
	 */
	public int getAnimateStrip() {
		return animateStrip;
	}

	/**
	 * Sets {@link Entity#animateStrip}
	 * @param animateStrip the animateStrip to set
	 */
	public void setAnimateStrip(int animateStrip) {
		this.animateStrip = animateStrip;
	}

	/**
	 * Returns {@link Entity#totalAnimateStrip}
	 * @return the totalAnimateStrip
	 */
	public int getTotalAnimateStrip() {
		return totalAnimateStrip;
	}

	/**
	 * Sets {@link Entity#totalAnimateStrip}
	 * @param totalAnimateStrip the totalAnimateStrip to set
	 */
	public void setTotalAnimateStrip(int totalAnimateStrip) {
		this.totalAnimateStrip = totalAnimateStrip;
	}

	/**
	 * Returns {@link Entity#pos}
	 * @return the pos
	 */
	public int[] getPos() {
		return pos;
	}

	/**
	 * Sets {@link Entity#pos}
	 * @param pos the pos to set
	 */
	public void setPos(int[] pos) {
		this.pos = pos;
	}

	/**
	 * Returns {@link Entity#velocity}
	 * @return the velocity
	 */
	public int[] getVelocity() {
		return velocity;
	}

	/**
	 * Sets {@link Entity#velocity}
	 * @param velocity the velocity to set
	 */
	public void setVelocity(int[] velocity) {
		this.velocity = velocity;
	}

	/**
	 * Returns {@link Entity#weight}
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Sets {@link Entity#weight}
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * Returns {@link Entity#grounded}
	 * @return the grounded
	 */
	public boolean isGrounded() {
		return grounded;
	}

	/**
	 * Sets {@link Entity#grounded}
	 * @param grounded the grounded to set
	 */
	public void setGrounded(boolean grounded) {
		this.grounded = grounded;
	}

	/**
	 * Returns {@link Entity#spriteSheet}
	 * @return the spriteSheet
	 */
	public BufferedImage getSpriteSheet() {
		return spriteSheet;
	}

	/**
	 * Sets {@link Entity#spriteSheet}
	 * @param spriteSheet the spriteSheet to set
	 */
	public void setSpriteSheet(BufferedImage spriteSheet) {
		this.spriteSheet = spriteSheet;
	}

	/**
	 * Returns {@link Entity#visible}
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets {@link Entity#visible}
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Returns {@link Entity#passable}
	 * @return the passable
	 */
	public boolean isPassable() {
		return passable;
	}

	/**
	 * Sets {@link Entity#passable}
	 * @param passable the passable to set
	 */
	public void setPassable(boolean passable) {
		this.passable = passable;
	}

	/**
	 * Returns {@link Entity#size}
	 * @return the size
	 */
	public int[] getSize() {
		return size;
	}

	/**
	 * Sets {@link Entity#size}
	 * @param size the size to set
	 */
	public void setSize(int[] size) {
		this.size = size;
	}

	/**
	 * Returns {@link Entity#behavior}
	 * @return the behavior
	 */
	public boolean[] getBehavior() {
		return behavior;
	}

	/**
	 * Sets {@link Entity#behavior}
	 * @param behavior the behavior to set
	 */
	public void setBehavior(boolean[] behavior) {
		this.behavior = behavior;
	}

	/**
	 * Returns {@link Entity#animStages}
	 * @return the animstages
	 */
	public int getAnimstages() {
		return animStages;
	}

	/**
	 * Returns {@link Entity#dialogue}
	 * @return the dialogue
	 */
	public Dialogue getDialogue() {
		return dialogue;
	}

	/**
	 * Sets {@link Entity#dialogue}
	 * @param dialogue the dialogue to set
	 */
	public void setDialogue(Dialogue dialogue) {
		this.dialogue = dialogue;
	}

	/**
	 * Returns {@link Entity#talking}
	 * @return the talking
	 */
	public boolean isTalking() {
		return talking;
	}

	/**
	 * Sets {@link Entity#talking}
	 * @param talking the talking to set
	 */
	public void setTalking(boolean talking) {
		this.talking = talking;
	}

	/**
	 * Returns {@link Entity#collisionShape}
	 * @return the collisionShape
	 */
	public int[] getCollisionShape() {
		return collisionShape;
	}

	/**
	 * Sets {@link Entity#collisionShape}
	 * @param collisionShape the collisionShape to set
	 */
	public void setCollisionShape(int[] collisionShape) {
		this.collisionShape = collisionShape;
	}

	/**
	 * Returns {@link Entity#animChangeCtr}
	 * @return the animChangeCtr
	 */
	public int getAnimChangeCtr() {
		return animChangeCtr;
	}

	/**
	 * Sets {@link Entity#animChangeCtr}
	 * @param animChangeCtr the animChangeCtr to set
	 */
	public void setAnimChangeCtr(int animChangeCtr) {
		this.animChangeCtr = animChangeCtr;
	}

	/**
	 * Returns {@link Entity#talkingTimer}
	 * @return the talkingTimer
	 */
	public long getTalkingTimer() {
		return talkingTimer;
	}

	/**
	 * Sets {@link Entity#talkingTimer}
	 * @param talkingTimer the talkingTimer to set
	 */
	public void setTalkingTimer(long talkingTimer) {
		this.talkingTimer = talkingTimer;
	}

	/**
	 * Returns {@link Entity#name}
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@link Entity#name}
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns {@link Entity#spriteFile}
	 * @return the spriteFile
	 */
	public String getSpriteFile() {
		return spriteFile;
	}

	/**
	 * Sets {@link Entity#spriteFile}
	 * @param spriteFile the spriteFile to set
	 */
	public void setSpriteFile(String spriteFile) {
		this.spriteFile = spriteFile;
	}

	/**
	 * Returns {@link Entity#alive}
	 * @return the alive
	 */
	public boolean isAlive() {

		return alive;
	}

	/**
	 * Sets {@link Entity#alive}
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {

		this.alive = alive;
	}

	/**
	 * Returns {@link Entity#health}
	 * @return the health
	 */
	public double getHealth() {
		return health;
	}

	/**
	 * Sets {@link Entity#health}
	 * @param health the health to set
	 */
	public void setHealth(double health) {
		this.health = health;
	}

	/**
	 * Returns {@link Entity#defense}
	 * @return the defense
	 */
	public HashMap<String, Double> getDefense() {
		return defense;
	}

	/**
	 * Sets {@link Entity#defense}
	 * @param defense the defense to set
	 */
	public void setDefense(HashMap<String, Double> defense) {
		this.defense = defense;
	}

	/**
	 * Returns {@link Entity#spellCD}
	 * @return the spellCD
	 */
	public long getSpellCD() {
		return spellCD;
	}

	/**
	 * Sets {@link Entity#spellCD}
	 * @param spellCD the spellCD to set
	 */
	public void setSpellCD(long spellCD) {
		this.spellCD = spellCD;
	}

	/**
	 * Returns {@link Entity#spells}
	 * @return the spells
	 */
	public ArrayList<String> getSpells() {
		return spells;
	}

	/**
	 * Sets {@link Entity#spells}
	 * @param spells the spells to set
	 */
	public void setSpells(ArrayList<String> spells) {
		this.spells = spells;
	}

	/**
	 * Returns {@link Entity#damaged}
	 * @return the damaged
	 */
	public int getDamaged() {
		return damaged;
	}

	/**
	 * Sets {@link Entity#damaged}
	 * @param damaged the damaged to set
	 */
	public void setDamaged(int damaged) {
		this.damaged = damaged;
	}

	/**
	 * Returns {@link Entity#faction}
	 * @return the faction
	 */
	public String getFaction() {
		return faction;
	}

	/**
	 * Sets {@link Entity#faction}
	 * @param faction the faction to set
	 */
	public void setFaction(String faction) {
		this.faction = faction;
	}

	/**
	 * Returns {@link Entity#newAnimStrip}
	 * @return the newAnimStrip
	 */
	public int getNewAnimStrip() {
		return newAnimStrip;
	}

	/**
	 * Sets {@link Entity#newAnimStrip}
	 * @param newAnimStrip the newAnimStrip to set
	 */
	public void setNewAnimStrip(int newAnimStrip) {
		this.newAnimStrip = newAnimStrip;
	}

	/**
	 * Returns {@link Entity#isAnimating}
	 * @return the isAnimating
	 */
	public boolean isAnimating() {
		return isAnimating;
	}

	/**
	 * Sets {@link Entity#isAnimating}
	 * @param isAnimating the isAnimating to set
	 */
	public void setAnimating(boolean isAnimating) {
		this.isAnimating = isAnimating;
	}

	/**
	 * Returns {@link Entity#spellToCast}
	 * @return the spellToCast
	 */
	public Spell getSpellToCast() {
		return spellToCast;
	}

	/**
	 * Sets {@link Entity#spellToCast}
	 * @param spellToCast the spellToCast to set
	 */
	public void setSpellToCast(Spell spellToCast) {
		this.spellToCast = spellToCast;
	}

	/**
	 * Returns {@link Entity#castSpellAt}
	 * @return the castSpellAt
	 */
	public int getCastSpellAt() {
		return castSpellAt;
	}

	/**
	 * Sets {@link Entity#castSpellAt}
	 * @param castSpellAt the castSpellAt to set
	 */
	public void setCastSpellAt(int castSpellAt) {
		this.castSpellAt = castSpellAt;
	}

	/**
	 * Returns {@link Entity#maxHealth}
	 * @return the maxHealth
	 */
	public double getMaxHealth() {
		return maxHealth;
	}

	/**
	 * Sets {@link Entity#maxHealth}
	 * @param maxHealth the maxHealth to set
	 */
	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}

	/**
	 * Returns {@link Entity#animStages}
	 * @return the animStages
	 */
	public int getAnimStages() {
		return animStages;
	}

	/**
	 * Sets {@link Entity#animStages}
	 * @param animStages the animStages to set
	 */
	public void setAnimStages(int animStages) {
		this.animStages = animStages;
	}

	/**
	 * Returns {@link Entity#newAnimStage}
	 * @return the newAnimStage
	 */
	public int getNewAnimStage() {
		return newAnimStage;
	}

	/**
	 * Sets {@link Entity#newAnimStage}
	 * @param newAnimStage the newAnimStage to set
	 */
	public void setNewAnimStage(int newAnimStage) {
		this.newAnimStage = newAnimStage;
	}

	/**
	 * Returns {@link Entity#castSpellOffset}
	 * @return the castSpellOffset
	 */
	public int[] getCastSpellOffset() {
		return castSpellOffset;
	}

	/**
	 * Sets {@link Entity#castSpellOffset}
	 * @param castSpellOffset the castSpellOffset to set
	 */
	public void setCastSpellOffset(int[] castSpellOffset) {
		this.castSpellOffset = castSpellOffset;
	}

	/**
	 * Returns {@link Entity#castSpellIndex}
	 * @return the castSpellIndex
	 */
	public int getCastSpellIndex() {
		return castSpellIndex;
	}

	/**
	 * Sets {@link Entity#castSpellIndex}
	 * @param castSpellIndex the castSpellIndex to set
	 */
	public void setCastSpellIndex(int castSpellIndex) {
		this.castSpellIndex = castSpellIndex;
	}

	/**
	 * Returns {@link Entity#dropList}
	 * @return the dropList
	 */
	public HashMap<String, Integer> getDropList() {
		return dropList;
	}

	/**
	 * Sets {@link Entity#dropList}
	 * @param dropList the dropList to set
	 */
	public void setDropList(HashMap<String, Integer> dropList) {
		this.dropList = dropList;
	}

	/**
	 * Returns {@link Entity#speed}
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Sets {@link Entity#speed}
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * Returns {@link Entity#infoText}
	 * @return the infoText
	 */
	public ArrayList<SystemMessage> getInfoText() {
		return infoText;
	}

	/**
	 * Sets {@link Entity#infoText}
	 * @param infoText the infoText to set
	 */
	public void setInfoText(ArrayList<SystemMessage> infoText) {
		this.infoText = infoText;
	}

	/**
	 * Returns {@link Entity#alerted}
	 * @return the alerted
	 */
	public boolean isAlerted() {
		return alerted;
	}

	/**
	 * Sets {@link Entity#alerted}
	 * @param alerted the alerted to set
	 */
	public void setAlerted(boolean alerted) {
		this.alerted = alerted;
	}

	/**
	 * Returns {@link Entity#patrolDistance}
	 * @return the patrolDistance
	 */
	public int getPatrolDistance() {
		return patrolDistance;
	}

	/**
	 * Sets {@link Entity#patrolDistance}
	 * @param patrolDistance the patrolDistance to set
	 */
	public void setPatrolDistance(int patrolDistance) {
		this.patrolDistance = patrolDistance;
	}

	/**
	 * Returns {@link Entity#lastTargetPos}
	 * @return the lastTargetPos
	 */
	public int[] getLastTargetPos() {
		return lastTargetPos;
	}

	/**
	 * Sets {@link Entity#lastTargetPos}
	 * @param lastTargetPos the lastTargetPos to set
	 */
	public void setLastTargetPos(int[] lastTargetPos) {
		this.lastTargetPos = lastTargetPos;
	}

}
