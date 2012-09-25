import java.awt.image.BufferedImage;
import java.io.File;


class Spell extends Entity
{

	/**
	 * Time that spell casting is locked for
	 */
	long spellCDTime;
	
	int explode = 0;
	
	int aliveTime;
	
	String damageType;
	
	int damageAmount;
	
	String exclude;
	
	public Spell(String name, int animateTime,	int[] pos, int[] collision, boolean[] behaviour,
			int[] velocity, BufferedImage spriteImage, int weight, boolean passable, int spellCDTime, int aliveTime,
			String damageType, int damageAmount, String exclude) {
		super(name, animateTime, 2, pos, null, collision, behaviour, null);


		this.velocity = velocity;
		this.spriteSheet = spriteImage;
		this.weight = weight;
		this.passable = passable;
		this.spellCDTime = spellCDTime;
		this.aliveTime = aliveTime;
		this.damageType = damageType;
		this.damageAmount = damageAmount;
		this.exclude = exclude;
		
		if (spriteSheet != null)
		{
			int width = spriteSheet.getWidth() / Entity.animStages;
			int height = spriteSheet.getHeight() / this.getTotalAnimateStrip();

			this.size[0] = width;
			this.size[1] = height;
		}
	}
	
	@Override
	public void animate(long time)
	{
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
	
	@Override
	public void AI()
	{
		if (behavior[0])
			behaviorProjectile();
	}
	
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
	
	public void behaviorProjectile()
	{
		int[] npos = {pos[0]+velocity[0], pos[1]+velocity[1]};
		
		if (!alive)
		{
			npos[0] = pos[0]+(velocity[0]/5);
			npos[1] = pos[1]+(velocity[1]/5);
		}
		
		String s = this.checkCollision(npos);
		
		
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