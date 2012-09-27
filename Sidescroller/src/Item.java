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
	public Item(String name, int[] pos, File spritefile, int[] collision, int number) {
		super(name, 1, 1, pos, spritefile, collision, null, null);
		this.number = number;
		passable = true;
		animStages = 1;
		
		this.processSpritesheet();
	}
	
	/**
	 * This behavior makes the entity initiate dialogue when being stepped onto by the entity 
	 */
	public void pickUp()
	{
		String s = this.collideEntities(pos);
		if (s == null)
		{
			return;
		}
		else if (s.equals("Player"))
		{
			Main.gamedata.systemMessages.add(new SystemMessage("Picked up a "+this.getName()+"!"));
			alive = false;
		}
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
		
		pickUp();
	}
	
	@Override
	public void updateTime(long time)
	{
		
	}


}
