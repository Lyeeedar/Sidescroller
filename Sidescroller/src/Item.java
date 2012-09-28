import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

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
	 * @param name
	 * @param animateTime
	 * @param totalAnimateStrip
	 * @param pos
	 * @param spritefile
	 * @param collision
	 * @param behaviour
	 * @param dialogue
	 */
	public Item(String name, int[] pos, int number) {
		super(name, 1, 1, pos, null, null, null, null);
		
		this.number = number;
		this.passable = true;
		this.animStages = 1;
		
		launch();
	}
	
	@Override
	public void processSpritesheet()
	{
		spriteSheet = ItemList.getImage(this.getName());
		
		if (spriteSheet != null)
		{
			int width = spriteSheet.getWidth();
			int height = spriteSheet.getHeight();

			this.size[0] = width;
			this.size[1] = height;
			
			this.collisionShape = new int[]{0, 0, width, height};
		}
	}
	
	public void launch()
	{
		velocity[1] = -Main.ran.nextInt(25);
		
		if (Main.ran.nextInt(2) == 0)
			velocity[0] = Main.ran.nextInt(10);
		else
			velocity[0] = -Main.ran.nextInt(10);
	}
	

	public void move()
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
			Main.gamedata.systemMessages.add(new SystemMessage("Picked up a "+this.getName()+"!", Color.YELLOW));
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