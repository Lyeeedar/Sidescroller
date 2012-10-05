import java.awt.Color;
import java.awt.Rectangle;

/**
 * 
 */

/**
 * @author Lyeeedar
 *
 */
public class EXPOrb extends Item {

	private static final long serialVersionUID = 2238802434120595613L;

	/**
	 * @param name
	 * @param spriteFile
	 * @param pos
	 * @param number
	 * @param type
	 */
	public EXPOrb(int[] pos, int number) {
		super("EXP", "", "Data/Resources/Items/exp.png", pos, number, 3);
	}
	
	@Override
	public void AI()
	{
		if (!alive)
			return;
		
		move();
	}
	
	@Override
	protected void move()
	{
		Entity p = Main.gamedata.getGameEntities().get("Player");
		
		pos[0] += velocity[0];
		pos[1] += velocity[1];
		
		if (velocity[0] < 0)
		{
			if (velocity[0] < -10)
			{
				velocity[0] = -10;
			}
		}
		
		if (velocity[0] > 0)
		{
			if (velocity[0] > 10)
			{
				velocity[0] = 10;
			}
		}
		
		if (velocity[1] < 0)
		{
			if (velocity[1] < -10)
			{
				velocity[1] = -10;
			}
		}
		
		if (velocity[1] > 0)
		{
			if (velocity[1] > 10)
			{
				velocity[1] = 10;
			}
		}
		
		if (pos[0] < p.pos[0]+p.getCollisionShape()[0])
		{
			velocity[0] += 1;
		}
		else
		{
			velocity[0] -= 1;
		}
		
		if (pos[1] < p.pos[1]+p.getCollisionShape()[1])
		{
			velocity[1] += 1;
		}
		else
		{
			velocity[1] -= 1;
		}
		
		Rectangle rp = new Rectangle(p.getPos()[0]+p.getCollisionShape()[0], p.getPos()[1]+p.getCollisionShape()[1],
				p.getCollisionShape()[2], p.getCollisionShape()[3]);
		
		Rectangle re = new Rectangle(getPos()[0]+getCollisionShape()[0], getPos()[1]+getCollisionShape()[1],
				getCollisionShape()[2], getCollisionShape()[3]);
		
		if (re.intersects(rp))
		{
			Character.addEXP(number);
			
			Main.gamedata.getGameEntities().get("Player").getInfoText().add(new SystemMessage("+"+number+" EXP", Color.GREEN, 3000));
			
			this.setAlive(false);
		}
	}

}
