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

	/**
	 * The internal name of the Entity
	 */
	protected String name;

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
	
	protected double health = 100;
	
	protected HashMap<String, Double> defense = new HashMap<String, Double>();
	
	protected long spellCD = 0;
	
	protected ArrayList<String> spells = new ArrayList<String>();


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
			this.dialogue = new Dialogue(dia);
		}

		this.spriteFile = spritefile;

		processSpritesheet();

		collisionShape = collision;
		
		spells.add("Fireball");
		
		defense.put(Entity.DAMAGE_PHYSICAL, (double) 0);
	}

	/**
	 * Method to evaluate the AI for the Entity. <p>
	 * Looks through the {@link Entity#behavior} for 'true's and then evaluates the AI packages in order from 0++
	 */
	public void AI()
	{
		if (!this.isAlive())
			return;
		
		if (behavior[0])
			behavior0();

		if (behavior[1])
			behavior1();

//		if (behavior[2])
//			behavior2();
	}

	/**
	 * Method that takes player input (from {@link MainFrame}) to control entity. <p>
	 * Moves the Entity depending on the state of the keys 'up', 'left' and 'right' <p>
	 * Performs actions on state of other keys. <p>
	 */
	protected void behavior0()
	{
		if (MainFrame.left)
		{
			this.getVelocity()[0] = -3;
			this.getPos()[2] = 0;
			this.setAnimate(true);
		}
		else if (MainFrame.right)
		{
			this.getVelocity()[0] = 3;
			this.getPos()[2] = 1;
			this.setAnimate(true);
		}
		else
			this.setAnimate(false);

		if ((MainFrame.up) && (this.isGrounded()))
		{
			this.getVelocity()[1] -= 15;
		}

		if (MainFrame.enter)
		{
			MainFrame.enter = false;
			Rectangle r = null;
			if (this.getPos()[2] == 0)
			{
				r = new Rectangle(pos[0]-size[0]/2, pos[1], size[0], size[1]);
			}
			else
			{
				r = new Rectangle(pos[0]+size[0]/2, pos[1], size[0], size[1]);
			}

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
		
		if (MainFrame.key1)
		{
			
			if (spellCD > 0)
				return;
			
			int dir = 0;
			
			if (pos[2] == 0)
				dir = -7;
			else
				dir = 7;
			
			Spell s = SpellList.getSpell("Fireball");
			s.setPos(new int[]{pos[0], pos[1], pos[2]});
			s.setVelocity(new int[]{dir, 0});
			s.setExclude(this.getName());
			
			spellCD = s.spellCDTime;
			
			Main.gamedata.getGameEntities().put("Fire"+System.currentTimeMillis(), s);
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

		this.setGrounded(true);

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

		int[] cpos = {this.getPos()[0]+velocity[0], this.getPos()[1]+velocity[1]};

		if (checkCollision(cpos) == null)
		{
			this.changePosition(cpos[0], cpos[1], this.getPos()[2]);
			this.setGrounded(false);

			updateJumpAnim(grounded);

			return;
		}

		// Extract and store current position
		int[] pos = {this.pos[0], this.pos[1]};

		// Modify new position by the velocity of the entity in the X direction
		pos[0] += velocity[0];

		// Check if moving in the X direction would cause a collision, if so the reset the new X position to the old one
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
			}
			// Else check if the velocity is positive. If it is then the Entity is falling
			// and therefore the ground is somewhere between the entity position and the 
			// entity position + Yvelocity. So find out where and put it on the ground.
			else if (velocity[1] > 0)
			{
				velocity[1] = 0;
				pos[1] = this.pos[1];
				this.setGrounded(true);

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

	protected void updateJumpAnim(boolean grounded)
	{
		this.setAnimateStrip(1);
		if (grounded == this.isGrounded())
		{
			this.animChangeCtr++;
			if ((!grounded) && (this.animChangeCtr >= 5))
			{
				this.setAnimateStrip(2);
			}
		}
		else
		{
			this.animChangeCtr = 0;
		}
	}

	protected void applyFriction()
	{
		velocity[0] = 0;
	}

	/**
	 * Method to check if this entity will collide with another entity or a surface if moved to the position given.
	 * @return
	 */
	public String checkCollision(int[] pos)
	{
		if ((pos[0]+collisionShape[0] < 0) || (pos[0]+collisionShape[0]+collisionShape[2] > GameData.levelSize[0])
				|| (pos[1]+collisionShape[1] < 0) || (pos[1]+collisionShape[1]+collisionShape[3] > GameData.levelSize[1]))
		{
			return this.getName();
		}

		int x = pos[0]+collisionShape[0];
		int y = pos[1]+collisionShape[1];

		Rectangle r = new Rectangle(x, y, collisionShape[2], collisionShape[3]);

		for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
		{
			Entity e = entry.getValue();
			if ((e.getName().equals(this.getName())) || (e.isPassable()))
				continue;

			Rectangle rn = new Rectangle(e.getPos()[0]+e.getCollisionShape()[0], e.getPos()[1]+e.getCollisionShape()[1],
					e.getCollisionShape()[2], e.getCollisionShape()[3]);

			if (r.intersects(rn))
			{
				return e.getName();
			}
		}

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
		
		if (animate)
		{
			this.remainingAnimateTime -= time;
			if (this.remainingAnimateTime <= 0)
			{
				this.remainingAnimateTime = this.animateTime;
				this.animateStage += 1;
				if (this.animateStage > Entity.animStages)
				{
					this.animateStage = 1;
				}
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

	public void processSpritesheet()
	{
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

	public void damage(double amount, String type)
	{
		if (type.equals(Entity.DAMAGE_PHYSICAL))
		{
			amount -= amount/defense.get(Entity.DAMAGE_PHYSICAL);
			health -= amount;
		}
		
		if (health <= 0)
		{
			this.setAlive(false);
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
		if (!animate)
			animateStage=1;
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

}
