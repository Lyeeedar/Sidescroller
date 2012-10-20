import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Lyeeedar
 *
 */
public class Scene {
	
	int zoomAmount = 0;
	float zoomStepX = 0.4f;
	float zoomStepY = 0.3f;

	ArrayList<SceneActor> actors = new ArrayList<SceneActor>();
	
	BufferedImage background;
	
	int[] screenPosition;
	int[] resolution = MainCanvas.resolution;
	
	/**
	 * 4 modes: <p>
	 * 0 = Inactive <p>
	 * 1 = Zoom in <p>
	 * 2 = normal <p>
	 * 3 = Zoom out
	 */
	int mode = 0;
	
	String parent;
	
	ArrayList<SceneAction> sceneActions = new ArrayList<SceneAction>();
	
	int sceneStage = 0;

	public Scene(String parent)
	{
		this.parent = parent;
		Entity e = Main.gamedata.getGameEntities().get(parent);
		actors.add(new SceneActor(e.spriteFile, new int[]{0, 0, e.pos[2]}, false, e.animateStage, e.animStages, e.animateStrip, e.totalAnimateStrip, (int)e.animateTime));
		
		screenPosition = new int[]{e.pos[0]-(resolution[0]/2), e.pos[1]-(resolution[1]/2)};
	}

	public void start()
	{	
		background = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = background.createGraphics();

		// Distant background, moves at half the speed of the rest
		g2d.drawImage(Main.gamedata.getBackground()[0],
				0, 0, resolution[0], resolution[1],
				screenPosition[0]/3, screenPosition[1], (screenPosition[0]/3)+(int)(resolution[0]), (screenPosition[1])+(int)(resolution[1]),
				null);

		// Far background
		g2d.drawImage(Main.gamedata.getBackground()[1],
				0, 0, resolution[0], resolution[1],
				screenPosition[0]/2, screenPosition[1], (screenPosition[0]/2)+resolution[0], screenPosition[1]+resolution[1],
				null);

		// Close Background layer
		g2d.drawImage(Main.gamedata.getBackground()[2],
				0, 0, resolution[0], resolution[1],
				screenPosition[0], screenPosition[1], screenPosition[0]+resolution[0], screenPosition[1]+resolution[1],
				null);

		// Collision layer
		g2d.drawImage(Main.gamedata.getBackground()[3],
				0, 0, resolution[0], resolution[1],
				screenPosition[0], screenPosition[1], screenPosition[0]+resolution[0], screenPosition[1]+resolution[1],
				null);
		
		mode = 1;
		
		Main.setState(5);
	}
	
	public void updateTime(long time)
	{
		if (mode == 1)
		{
			zoomAmount += time;
			
			if (zoomAmount > 1000)
			{
				zoomAmount = 1000;
				mode = 2;
			}
		}
		else if (mode == 3)
		{
			zoomAmount -= time;
			
			if (zoomAmount < 0)
			{
				zoomAmount = 0;
				end();
			}
		}
		
		for (SceneActor sa : actors)
		{
			sa.updateTime(time);
		}
	}

	public void end()
	{
		Main.setState(1);
		Main.gamedata.currentScene = null;
		sceneStage = 0;
		mode = 0;
		
	}

	public void draw(Graphics2D g2d)
	{
		BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		g.drawImage(background, 0, 0, null);
		for (SceneActor sa : actors)
		{
			g.drawImage(sa.getSprite(), 400+sa.pos[0], 300+sa.pos[1], null);
		}
		
		if ((mode == 1) || (mode == 3))
		{
			g.drawImage(Main.gamedata.getBackground()[4],
					0, 0, resolution[0], resolution[1],
					screenPosition[0], screenPosition[1], screenPosition[0]+resolution[0], screenPosition[1]+resolution[1],
					null);
			zoom(g2d, image);
		}
	}
	
	public void zoom(Graphics2D g2d, BufferedImage image)
	{
		g2d.drawImage(image, (int)(-zoomStepX*zoomAmount), (int)(-zoomStepY*zoomAmount), 800+(int)(zoomStepX*zoomAmount*2), 600+(int)(zoomStepY*zoomAmount*2), null);
	}

}

class SceneActor
{
	BufferedImage spriteSheet;
	int[] pos;
	boolean animate;
	int stage;
	int totalStage;
	int strip;
	int totalStrip;
	String dialogue;
	BufferedImage sprite;
	int totalAnimateTime;
	int animateTime;
	
	public SceneActor(String image, int[] pos, boolean animate, int stage, int totalStage, int strip, int totalStrip, int animateTime)
	{
		spriteSheet = GameData.getImage("Spritesheet", image);
		this.pos = pos;
		this.animate = animate;
		this.stage = stage;
		this.totalStage = totalStage;
		this.strip = strip;
		this.totalStrip = totalStrip;
		this.totalAnimateTime = animateTime;
		this.animateTime = animateTime;
		
		sprite = new BufferedImage(spriteSheet.getWidth()/totalStage, spriteSheet.getHeight()/totalStrip, BufferedImage.TYPE_INT_ARGB);
	}
	
	public void updateTime(long time)
	{
		if (animate)
			animate(time);
	}
	
	public void animate(long time)
	{
		animateTime -= time;
		
		if (animateTime < 0)
		{
			animateTime = totalAnimateTime;
			
			stage++;
			
			if (stage > totalStage)
				stage = 1;
		}
	}
	
	public BufferedImage getSprite()
	{
		Graphics2D g2d = sprite.createGraphics();
		
		if (pos[2] == 1)
			g2d.drawImage(spriteSheet, 0, 0, sprite.getWidth(), sprite.getHeight(), sprite.getWidth()*(stage-1), sprite.getHeight()*(strip-1), sprite.getWidth()*stage, sprite.getHeight()*strip, null);
		else
			g2d.drawImage(spriteSheet, sprite.getWidth(), 0, 0, sprite.getHeight(), sprite.getWidth()*(stage-1), sprite.getHeight()*(strip-1), sprite.getWidth()*stage, sprite.getHeight()*strip, null);
		
		return sprite;
	}
}

class SceneAction
{
	
}
