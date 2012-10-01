
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
			int[] velocity, String spriteFile, int weight, boolean passable, int spellCDTime, int aliveTime,
			String damageType, int damageAmount, String exclude) {
		
		super(name, animateTime, 2, 8, pos, spriteFile, collision, behaviour, null);


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
		if (behavior[1])
			behaviorGroundHugger();
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

	}

	public void behaviorGroundHugger()
	{

		// Work out positions that the entity is trying to move to
		int[] cpos = {this.getPos()[0]+velocity[0], this.getPos()[1]};
		
		if (!alive)
		{
			//velocity[0] /= 2;
			cpos[0] = pos[0]+(velocity[0]/4);
		}
		
		String s = checkCollision(cpos);

		// If there is no collision at this point then all is well and move the entity to this point
		if (s == null)
		{
			while (s == null)
			{
				cpos[1] += 2;
				s = checkCollision(cpos);
			}
			cpos[1] -= 3;
		}
		else
		{
			cpos = new int[]{this.getPos()[0], pos[1]};
			int val = Math.abs(velocity[0]);
			int x = val;
			int y = 0;
			boolean found = false;

			for (int i = 1; i <= val-1; i++)
			{
				x = val-i;
				y = i;

				if (velocity[0] < 0)
				{
					cpos[0] = this.getPos()[0] - x;
				}
				else
				{
					cpos[0] = this.getPos()[0] + x;
				}
				cpos[1] = pos[1] - y;

				s = checkCollision(cpos);
				
				if (s == null)
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				cpos[0] = pos[0];
				cpos[1] = pos[1];
			}
		}
		
		if (cpos[0] == pos[0])
		{
			this.setAlive(false);
		}
		
		this.changePosition(cpos[0], cpos[1], pos[2]);
		
		if ((s != null) && (!s.equals(this.getName())))
		{
			if (alive)
			{
				Main.gamedata.getGameEntities().get(s).damage(this.damageAmount, this.damageType);
				this.setAlive(false);
			}
			
			if (!alive)
			{
				this.changePosition(cpos[0], cpos[1], pos[2]);
			}
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