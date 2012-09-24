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
	public void AI()
	{
		if (behavior[0])
			behaviorProjectile();
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
			npos[0] = pos[0]+(velocity[0]/2);
			npos[1] = pos[1]+(velocity[1]/2);
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
			Main.gamedata.getGameEntities().get(s).damage(this.damageAmount, this.damageType);
			this.setAlive(false);
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
	
}