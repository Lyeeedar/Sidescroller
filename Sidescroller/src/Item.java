import java.awt.Color;
import java.awt.Rectangle;

/**
 * @author Lyeeedar
 *
 */
public class Item extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8997364590836092463L;
	int number;
	/**
	 * 0 = armour
	 * 1 = upgrades
	 * 2 = other
	 */
	int type = 0;
	
	/**
	 * @param name
	 * @param animateTime
	 * @param totalAnimateStrip
	 * @param pos
	 * @param spritefile
	 * @param collision
	 * @param behaviour
	 * @param dialogue
	 */
	public Item(String name, String spriteFile, int[] pos, int number, int type) {
		super(name, 1, 1, 1, pos, 0, spriteFile, new int[]{0, 0, 10, 10}, null, null);
		
		this.number = number;
		this.passable = true;
		this.animStages = 1;
		this.type = type;
		
		launch();
	}
	
	public void launch()
	{
		velocity[1] = -Main.ran.nextInt(25);
		
		if (Main.ran.nextInt(2) == 0)
			velocity[0] = Main.ran.nextInt(10);
		else
			velocity[0] = -Main.ran.nextInt(10);
	}
	

	protected void move()
	{	
		velocity[1] += 2;
		
		int[] npos = {pos[0]+velocity[0], pos[1]+velocity[1], pos[2]};
		
		String s = checkCollision(npos);
		
		if (s == null)
		{
			changePosition(npos[0], npos[1], npos[2]);
		}
		else
		{
			velocity[1] = 0;
			velocity[0] = 0;
		}
	}
	
	public void pickUp()
	{
		Entity e = Main.gamedata.getGameEntities().get("Player");
		
		Rectangle tr = new Rectangle(pos[0]+collisionShape[0], pos[1]+collisionShape[1], collisionShape[2], collisionShape[3]);
		Rectangle pr = new Rectangle(e.pos[0]+e.collisionShape[0], e.pos[1]+e.collisionShape[1], e.collisionShape[2], e.collisionShape[3]);
		if (tr.intersects(pr))
		{
			Character.addItem(this);
			Main.gamedata.systemMessages.add(new SystemMessage("Picked up a "+this.getName()+"!", Color.YELLOW, 10000));
			alive = false;
		}
	}
	
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
	
	@Override
	public void activate()
	{
		
	}
	
	@Override
	public void AI()
	{
		if (!alive)
			return;
		
		move();
		pickUp();
	}
	
	@Override
	public void updateTime(long time)
	{
		
	}


}
