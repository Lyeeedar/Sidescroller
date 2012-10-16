import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;


/**
 * This class simulates a spell. It is a subclass of entity but overrides many of the methods contained within it.
 * @author Lyeeedar
 *
 */
class Spell extends Entity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2847831540861108398L;

	/**
	 * Time that spell casting is locked for the entity that casts this spell.
	 */
	long spellCDTime;

	/**
	 * The current stage of the explode animation
	 */
	int explode = 0;

	/**
	 * The length of time this spell will exist for before exploding
	 */
	int aliveTime;

	/**
	 *  the type of damage this spell inflicts
	 */
	String damageType;

	/**
	 * The amount of damage this spell inflicts
	 */
	int damageAmount;

	/**
	 * The entity to exclude from collision (normally the entity that casts the spell)
	 */
	String exclude;

	public Spell(String name, int animateTime,	int[] pos, int[] collision, boolean[] behaviour,
			int[] velocity, String spriteFile, int weight, boolean passable, boolean visible, int spellCDTime, int aliveTime,
			String damageType, int damageAmount, String exclude) {
		
		super(name, animateTime, 2, 8, pos, 0, spriteFile, collision, behaviour, null);

		this.visible = visible;
		this.velocity = velocity;
		this.weight = weight;
		this.passable = passable;
		this.spellCDTime = spellCDTime;
		this.aliveTime = aliveTime;
		this.damageType = damageType;
		this.damageAmount = damageAmount;
		this.exclude = exclude;

		if (this.pos[2] == 0)
		{
			this.pos[0] -= size[0];
		}
	}
	
	@Override
	public void processSpritesheet()
	{

		if (!visible)
			return;

		if (spriteFile != null)
			this.spriteSheet = GameData.getImage("Spell", spriteFile);

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

	@Override
	public void activate()
	{

	}

	@Override
	public void animate(long time)
	{
		this.remainingAnimateTime -= time;
		if (this.remainingAnimateTime <= 0)
		{
			this.remainingAnimateTime = this.animateTime;

			if (!alive)
				animateStage = explode;
			else
				this.animateStage += 1;

			if (this.animateStage > animStages)
			{
				this.animateStage = 1;
			}
		}
	}

	@Override
	public void AI()
	{	
		if (behavior[0])
			behaviorProjectile();
		
		if (behavior.length < 2)
			return;
		
		if (behavior[1])
			behaviorGroundHugger();
		
		if (behavior.length < 3)
			return;
		
		if (behavior[2])
			behaviorStrike();
		
		if (behavior.length < 4)
			return;
		
		if (behavior[3])
			behaviorAreaEffect();
		
		if (behavior.length < 5)
			return;
		
		if (behavior[4])
			behaviorSpreadAreaEffect();
		
		if (behavior.length < 6)
			return;
		
		if (behavior[5])
			behaviorHeal();
	}

	/**
	 * Method that sets the entity velocity depending on what direction its facing
	 * @param velocity
	 */
	public void launch(int[] velocity)
	{
		if (pos[2] == 0)
		{
			this.velocity[0] = -velocity[0];
		}
		else
		{
			this.velocity[0] = velocity[0];
		}
		this.velocity[1] = velocity[1];
	}

	int dps = 0;
	
	@Override
	public void updateTime(long time)
	{		
		super.updateTime(time);
		
		aliveTime -= time;

		if ((!alive) && (this.remainingAnimateTime == this.animateTime))
		{
			explode++;
		}

		if ((aliveTime < 0) && (alive))
		{
			alive = false;
			this.animateStrip = 2;
			this.animateStage = 0;
		}

		if (!alive)
		{
			this.animateStrip = 2;
		}
		
		dps -= time;

	}

	public void behaviorGroundHugger()
	{
		int npos = pos[0];
		if (!alive)
		{
			npos += velocity[0]/4;
		}
		
		npos += velocity[0];
		
		int[] cpos = {npos, this.pos[1]-(size[1]/2), this.pos[2]};

		if (checkCollision(cpos) != null)
		{
			alive = false;
		}
		else
		{
			cpos = findGround(cpos, this.collisionShape[2]);
			
			cpos[1] -= collisionShape[1]+collisionShape[3];
		
			pos = cpos;
		}
		
		if (!alive)
			return;
		
		String s = super.collideEntities(pos);
		
		if ((s != null) && (!s.equals(this.name)))
		{
			Main.gamedata.getGameEntities().get(s).damage(damageAmount, damageType);
			this.setAlive(false);
		}
	}

	/**
	 * Method that makes the Spell act like a projectile. Moves it directly in the direction of {@link Entity#velocity} and then explodes when it hits something, damaging it.
	 */
	public void behaviorProjectile()
	{
		int[] npos = {pos[0]+velocity[0], pos[1]+velocity[1]};

		if (!alive)
		{
			//velocity[0] /= 2;
			//velocity[1] /= 2;
			npos[0] = pos[0]+(velocity[0]/4);
			npos[1] = pos[1]+(velocity[1]/4);
		}

		String s = checkCollision(npos);


		if (s == null)
		{
			this.changePosition(npos[0], npos[1], pos[2]);
		}
		else if (s.equals(this.getName()))
		{
			this.setAlive(false);
		}
		else if (s != null)
		{
			if (alive)
			{
				Main.gamedata.getGameEntities().get(s).damage(this.damageAmount, this.damageType);
				this.setAlive(false);
			}
			
			if (!alive)
			{
				this.changePosition(npos[0], npos[1], pos[2]);
			}
		}
	}
	
	public void behaviorStrike()
	{
		if (!alive)
		{
			return;
		}
		
		if (pos[2] == 0)
		{
			pos[0] -= collisionShape[2]/2;
		}
		else
		{
			pos[0] += 2*collisionShape[2]/2;
		}
		
		String s = super.collideEntities(pos);
		
		if ((s != null) && (!s.equals(this.getName())))
		{
			Main.gamedata.getGameEntities().get(s).damage(damageAmount, damageType);
		}
		
		this.setAlive(false);
	}
	
	public void behaviorAreaEffect()
	{
		if (!alive)
			return;
		
		if (dps > 0)
			return;
		
		if (dps < 0)
			dps = 400;
		
		String sc = checkCollision(pos);
		if ((sc != null) && (sc.equals(this.getName())))
		{
			alive = false;
			return;
		}
		
		ArrayList<Entity> entityCheck = new ArrayList<Entity>();
		for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
		{
			Entity e = entry.getValue();

			if (e instanceof Spell)
			{
				continue;
			}
			else if (e instanceof Item)
			{
				continue;
			}
			
			if (faction.equals(e.faction))
			{
				continue;
			}
			
			entityCheck.add(e);
		}
		
		Rectangle rt = new Rectangle(pos[0]+collisionShape[0], pos[1]+collisionShape[1], collisionShape[2], collisionShape[3]);
		
		for (Entity e : entityCheck)
		{
			Rectangle re = new Rectangle(e.pos[0]+e.collisionShape[0], e.pos[1]+e.collisionShape[1], e.collisionShape[2], e.collisionShape[3]);
			
			if (rt.intersects(re))
			{
				e.damage(damageAmount, damageType);
			}
		}
	}
	
	public void behaviorSpreadAreaEffect()
	{
		if (!alive)
			return;
		
		this.setVisible(false);
		pos[0] += velocity[0];
		
//		String sc = checkCollision(pos);
//		if ((sc != null) && (sc.equals(this.getName())))
//		{
//			alive = false;
//			return;
//		}
		
		int[] npos = {this.pos[0], this.pos[1]+collisionShape[1]+collisionShape[3], this.pos[2]};
		
		npos = findGround(pos, this.collisionShape[2]);
		
		npos[1] -= collisionShape[1]+collisionShape[3];
		
		Spell s = new Spell(name+" Effect", (int)this.animateTime, npos, collisionShape,
				new boolean[]{false, false, false, true, false}, new int[]{0, 0}, this.spriteFile, 3, true, true, 0, 1000, this.damageType, this.damageAmount, this.exclude);
		s.setFaction(faction);
		
		Main.gamedata.getGameEntities().put(s.name+System.currentTimeMillis(), s);
		
	}
	
	public void behaviorHeal()
	{
		if (!alive)
			return;
		
		if (dps > 0)
			return;
		
		if (dps < 0)
			dps = 400;
		
		Entity e = Main.gamedata.getGameEntities().get(exclude);
		
		e.heal(damageAmount);
		e.infoText.add(new SystemMessage("+"+damageAmount+"     HP", Color.GREEN, 3000));
	}
	
	public int[] findGround(int[] npos, int width)
	{
		if (npos[0] < 0)
			npos[0] = 0;
		
		if (npos[0] > Main.gamedata.collisionMap.length-1-width)
			npos[0] = Main.gamedata.collisionMap.length-1-width;
		
		boolean ground = Main.gamedata.collisionMap[npos[0]][npos[1]];
		
		while (!ground)
		{
			npos[1]++;
			
			ground = Main.gamedata.collisionMap[npos[0]][npos[1]];
			
			if (!ground)
			{
				ground = Main.gamedata.collisionMap[npos[0]+width][npos[1]];
			}
		}
		
		return npos;
	}
	
	@Override
	public void changePosition(int X, int Y, int dir)
	{	
		pos[0] = X;
		pos[1] = Y;
		pos[2] = dir;

	}
	
	
	@Override
	public String checkCollision(int[] pos)
	{
		String s = super.checkCollision(pos);

		if (s == null)
		{
			return null;
		}
		else if (s.equals(exclude))
		{
			return null;
		}
		else
		{
			return s;
		}
	}

	/**
	 * Returns {@link Spell#spellCDTime}
	 * @return the spellCDTime
	 */
	public long getSpellCDTime() {
		return spellCDTime;
	}

	/**
	 * Sets {@link Spell#spellCDTime}
	 * @param spellCDTime the spellCDTime to set
	 */
	public void setSpellCDTime(long spellCDTime) {
		this.spellCDTime = spellCDTime;
	}

	/**
	 * Returns {@link Spell#explode}
	 * @return the explode
	 */
	public int getExplode() {
		return explode;
	}

	/**
	 * Sets {@link Spell#explode}
	 * @param explode the explode to set
	 */
	public void setExplode(int explode) {
		this.explode = explode;
	}

	/**
	 * Returns {@link Spell#aliveTime}
	 * @return the aliveTime
	 */
	public int getAliveTime() {
		return aliveTime;
	}

	/**
	 * Sets {@link Spell#aliveTime}
	 * @param aliveTime the aliveTime to set
	 */
	public void setAliveTime(int aliveTime) {
		this.aliveTime = aliveTime;
	}

	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}
	
	/**
	 * Returns {@link Spell#damageType}
	 * @return the damageType
	 */
	public String getDamageType() {
		return damageType;
	}

	/**
	 * Sets {@link Spell#damageType}
	 * @param damageType the damageType to set
	 */
	public void setDamageType(String damageType) {
		this.damageType = damageType;
	}

	/**
	 * Returns {@link Spell#damageAmount}
	 * @return the damageAmount
	 */
	public int getDamageAmount() {
		return damageAmount;
	}

	/**
	 * Sets {@link Spell#damageAmount}
	 * @param damageAmount the damageAmount to set
	 */
	public void setDamageAmount(int damageAmount) {
		this.damageAmount = damageAmount;
	}

	/**
	 * Returns {@link Spell#exclude}
	 * @return the exclude
	 */
	public String getExclude() {
		return exclude;
	}

	/**
	 * Sets {@link Spell#exclude}
	 * @param exclude the exclude to set
	 */
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

}