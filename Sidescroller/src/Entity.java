import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

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
	protected String name;
	
	protected String faction;

	/**
	 *  Total animation stages
	 */
	public static final int animStages = 8;

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
	 *  Whether the Entity should be animated or not
	 */
	protected boolean animate = true;

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
	protected File spriteFile;
	
	/**
	 * Whether the entity is alive or not
	 */
	protected boolean alive = true;
	
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
	protected boolean damaged = false;
	
	protected int newAnimStrip = 1;
	
	protected boolean isAnimating = false;
	
	protected Spell spellToCast = null;
	protected int castSpellAt = 0;


	/**
	 * @param animateTime
	 * @param totalAnimateStrip
	 * @param pos - int[3], X, Y, dir
	 * @param spriteSheet
	 * @param collision - int[4], X, Y, width, height
	 * @param behaviour
	 * @param dialogue
	 */
	public Entity(String name, long animateTime, int totalAnimateStrip, int[] pos, File spritefile, int[] collision, boolean[] behaviour, Dialogue dialogue)
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
		
		if (this.isDamaged())
			this.setDamaged(false);
		
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
			this.getVelocity()[0] = -6;
			this.getPos()[2] = 0;
			
			this.setAnimate(true);
		}
		else if (MainFrame.right)
		{
			this.getVelocity()[0] = 6;
			this.getPos()[2] = 1;
			
			this.setAnimate(true);
		}
		else
		{
			this.setAnimate(false);
			
			if (animateStrip == 1)
				animateStage = 1;
		}

		// Jump
		if ((MainFrame.up) && (this.isGrounded()))
		{
			this.getVelocity()[1] -= 35;
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
		
		// Cast the spell bound to Key1 (the number 1 key on the keyboard)
		if (MainFrame.key1)
		{
			// If still in cooldown then don't allow spell casting 
			if ((spellCD > 0) || (isAnimating))
				return;
			
			newAnimStrip = 4;
			
			int[] pos = {0, 0, this.getPos()[2]};
			
			if (this.getPos()[2] == 0){
				pos[0] = this.getPos()[0]+this.getCollisionShape()[0]-10;
			}
			else
			{
				pos[0] = this.getPos()[0]+this.getCollisionShape()[0]+this.getCollisionShape()[2]+10;
			}
			
			pos[1] = this.getPos()[1]+this.getCollisionShape()[1]+(this.getCollisionShape()[3]/2)-45;
			
			Spell s = SpellList.getSpell("Fireball", pos, new int[]{17,0}, this.getName());
			s.setFaction(this.getFaction());
			
			spellCD = s.spellCDTime;
			
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

			for (int i = 1; i <= val-1; i++)
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
	
	/**
	 * Very simple enemy AI. Just moves towards player and fires off its spells randomly.
	 */
	public void behavior2()
	{
		
		
		Entity player = Main.gamedata.getGameEntities().get("Player");
		
		if (Main.ran.nextInt(3)  !=  1)
		{
			this.setAnimate(false);
		}
		else if (player.getPos()[0] < this.getPos()[0])
		{
			velocity[0] -= 2;
			pos[2] = 0;
			this.setAnimate(true);
		}
		else
		{
			velocity[0] += 2;
			pos[2] = 1;
			this.setAnimate(true);
		}
		
		if (Math.abs(player.getPos()[0]-pos[0]) < 10)
		{
			if (spellCD > 0)
				return;
			
			String spell = spells.get(Main.ran.nextInt(spells.size()));
			
			newAnimStrip = 4;
			this.setAnimate(true);
			
			int[] pos = {0, 0, this.getPos()[2]};
			
			if (this.getPos()[2] == 0){
				pos[0] = this.getPos()[0]+this.getCollisionShape()[0]-10;
			}
			else
			{
				pos[0] = this.getPos()[0]+this.getCollisionShape()[0]+this.getCollisionShape()[2]+10;
			}
			
			pos[1] = this.getPos()[1]+this.getCollisionShape()[1]+(this.getCollisionShape()[3]/2)-45;
			
			Spell s = SpellList.getSpell(spell, pos, new int[]{17,0}, this.getName());
			s.setFaction(this.getFaction());
			
			spellCD = s.spellCDTime*15;
			
			spellToCast = s;
			castSpellAt = 5;
		}
		
	}
	
	/**
	 * This behavior makes the entity initiate dialogue when being stepped onto by the entity 
	 */
	public void behavior3()
	{
		String s = this.collideEntities(pos);
		if (s == null)
		{
			return;
		}
		else if (s.equals("Player"))
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
		else if (!this.isGrounded())
		{
			newAnimStrip = 2;
		}
		else
		{
			newAnimStrip = 1;
		}
	}

	protected void applyFriction()
	{
		velocity[0] = 0;
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
		
		// Check the collision box for this entity to see if any of the level is inside it (any non-transparent pixels)
		for (int nx = x; nx < x+collisionShape[2]; nx++)
		{
			for (int ny = y+collisionShape[3]-1; ny >= y; ny--)
			{
				if (nx >= Main.gamedata.getCollisionMap().length)
					return this.getName();
				else if (Main.gamedata.getCollisionMap()[nx][ny])
					return this.getName();
			}
		}

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
	}

	/**
	 * Method to update the animation stage for the entity
	 * @param time
	 */
	public void animate(long time)
	{
		if (!this.isAlive())
		{
			this.setAnimateStrip(3);
			this.setAnimateStage(1);
			this.setAnimate(false);
			this.setPassable(true);
			return;
		}
		
		this.remainingAnimateTime -= time;
		if (this.remainingAnimateTime <= 0)
		{
			this.remainingAnimateTime = this.animateTime;
			this.remainingAnimateTime = 80;
			
			if (isAnimating)
			{
				this.animateStage++;
				
				if ((animateStrip > 3) && (animateStage == castSpellAt))
				{
					if (spellToCast != null)
					{
						Main.gamedata.getGameEntities().put(spellToCast.getName()+System.currentTimeMillis(), spellToCast);
						spellToCast = null;
					}
				}
				
				if (this.animateStage > Entity.animStages)
				{
					if (animateStrip > 3)
					{
						isAnimating = false;
						newAnimStrip = 1;
					}
					
					this.animateStage = 1;
				}
				animChangeCtr = 0;
			}
			else if (newAnimStrip != animateStrip)
			{
				if (newAnimStrip == 1)
				{

					animateStrip = 1;
				}
				else if (newAnimStrip == 2)
				{

					animChangeCtr++;
					if (animChangeCtr > 3)
					{

						animateStrip = 2;
						animateStage = 1;
					}
				}
				else if (newAnimStrip > 3)
				{

					animateStage = 1;
					animateStrip = newAnimStrip;
					isAnimating = true;
				}
			}
			else if (animateStrip == 1)
			{	
				if (animate)
				{

					this.animateStage++;
					
					if (this.animateStage > Entity.animStages)
					{					
						this.animateStage = 1;
					}
					animChangeCtr = 0;
				}
			}
			else if (animateStrip != 2)
			{
				animateStrip = 1;
			}
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
		
		BufferedImage im = null;
		if (spriteFile != null)
		{
			try{
				im = ImageIO.read(spriteFile);
			}
			catch (IOException e)
			{
				System.err.println("Invalid file: " + spriteFile.getAbsolutePath());
			}
		}

		this.spriteSheet = im;

		// If spritesheet exists (and the Entity is therefore visible) then work out
		// width and height of a frame
		if (spriteSheet != null)
		{
			int width = spriteSheet.getWidth() / Entity.animStages;
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
		
		if (health <= 0)
		{
			this.setAlive(false);
		}
		
		this.setDamaged(true);
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
	 * Returns {@link Entity#animate}
	 * @return the animate
	 */
	public boolean isAnimate() {
		return animate;
	}

	/**
	 * Sets {@link Entity#animate}
	 * @param animate the animate to set
	 */
	public void setAnimate(boolean animate) {
		this.animate = animate;
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
	public static int getAnimstages() {
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
	public File getSpriteFile() {
		return spriteFile;
	}

	/**
	 * Sets {@link Entity#spriteFile}
	 * @param spriteFile the spriteFile to set
	 */
	public void setSpriteFile(File spriteFile) {
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
	public boolean isDamaged() {
		return damaged;
	}

	/**
	 * Sets {@link Entity#damaged}
	 * @param damaged the damaged to set
	 */
	public void setDamaged(boolean damaged) {
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

}
