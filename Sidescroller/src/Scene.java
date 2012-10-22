import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Lyeeedar
 *
 */
public class Scene implements Serializable{

	private static final long serialVersionUID = 7976569180276076531L;
	final static int updateSpeed = 20;
	transient int updateTimer = updateSpeed;

	transient int zoomAmount = 0;
	final float zoomStepX = 0.4f;
	final float zoomStepY = 0.3f;
	
	/**
	 * Wait status: <p>
	 * 0 = no wait <p>
	 * 1 = wait on enter pressed then increase stage <p>
	 * 2 = wait time specified then increase stage <p>
	 * 3 = increase stage <p>
	 */
	transient int wait = 0;
	transient int waitDuration = 0;

	transient ArrayList<SceneActor> actors = new ArrayList<SceneActor>();

	transient BufferedImage background;

	transient int[] screenPosition;
	transient int[] resolution = MainCanvas.resolution;

	/**
	 * 4 modes: <p>
	 * 0 = Inactive <p>
	 * 1 = Zoom in <p>
	 * 2 = normal <p>
	 * 3 = Zoom out
	 */
	transient int mode = 0;

	Color backgroundColor = new Color(0, 0, 0);
	
	String parent;

	ArrayList<SceneAction> sceneActions = new ArrayList<SceneAction>();

	transient int sceneStage = 0;

	public Scene(String parent)
	{
		this.parent = parent;
		
	}

	public void start()
	{
		actors = new ArrayList<SceneActor>();
		Entity e = Main.gamedata.getGameEntities().get(parent);
		resolution = MainCanvas.resolution;
		
		actors.add(new SceneActor(e.spriteFile, new int[]{0, 0, e.pos[2]}, new int[]{e.collisionShape[0]+(e.collisionShape[2]/2), e.collisionShape[1]}, false, e.animateStage, e.animStages, e.animateStrip, e.totalAnimateStrip, (int)e.animateTime));
		screenPosition = new int[]{e.pos[0]-(resolution[0]/2), e.pos[1]-(resolution[1]/2)};
		
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
		sceneStage = 0;

		g2d.dispose();

		Main.setState(5);
	}
	
	public void evaluateActions(long time)
	{
		updateTimer -= time;
		
		if (updateTimer > 0)
			return;
		
		updateTimer = updateSpeed;
		
		if (wait == 0)
		{
			sceneActions.get(sceneStage).action();
		}
		else if (wait == 1)
		{
			if (MainCanvas.enter)
			{
				MainCanvas.enter = false;
				
				sceneStage++;
				wait = 0;
			}
		}
		else if (wait == 2)
		{
			waitDuration -= time;
			if (waitDuration < 0)
			{
				sceneStage++;
				wait = 0;
			}
		}
		else if (wait == 3)
		{
			sceneStage++;
		}
		
		if (sceneStage >= sceneActions.size())
		{
			mode = 3;
		}
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
		else
		{
			evaluateActions(time);
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
		
		g.setColor(backgroundColor);
		g.fillRect(0, 0, 800, 600);

		g.drawImage(background, 0, 0, null);
		for (SceneActor sa : actors)
		{
			if (!sa.visible)
				continue;

			if (sa.pos[2] == 1)
				g.drawImage(sa.spriteSheet, 400+sa.pos[0], 300+sa.pos[1], 400+sa.pos[0]+sa.spriteSize[0], 300+sa.pos[1]+sa.spriteSize[1], sa.spriteSize[0]*(sa.stage-1), sa.spriteSize[1]*(sa.strip-1), sa.spriteSize[0]*sa.stage, sa.spriteSize[1]*sa.strip, null);
			else
				g.drawImage(sa.spriteSheet, 400+sa.pos[0]+sa.spriteSize[0], 300+sa.pos[1], 400+sa.pos[0], 300+sa.pos[1]+sa.spriteSize[1], sa.spriteSize[0]*(sa.stage-1), sa.spriteSize[1]*(sa.strip-1), sa.spriteSize[0]*sa.stage, sa.spriteSize[1]*sa.strip, null);
		}

		if ((mode == 1) || (mode == 3))
		{
			g.drawImage(Main.gamedata.getBackground()[4],
					0, 0, resolution[0], resolution[1],
					screenPosition[0], screenPosition[1], screenPosition[0]+resolution[0], screenPosition[1]+resolution[1],
					null);

			g.dispose();

			zoom(g2d, image);
		}
		else if (mode == 2)
		{
			g.dispose();

			normalDraw(g2d, image);
		}
	}

	public void normalDraw(Graphics2D g2d, BufferedImage image)
	{
		g2d.drawImage(image, (int)(-zoomStepX*zoomAmount), (int)(-zoomStepY*zoomAmount), 800+(int)(zoomStepX*zoomAmount*2), 600+(int)(zoomStepY*zoomAmount*2), null);

		drawSpeech(g2d);

	}

	public void zoom(Graphics2D g2d, BufferedImage image)
	{
		g2d.drawImage(image, (int)(-zoomStepX*zoomAmount), (int)(-zoomStepY*zoomAmount), 800+(int)(zoomStepX*zoomAmount*2), 600+(int)(zoomStepY*zoomAmount*2), null);
	}

	public void drawSpeech(Graphics2D g2d)
	{
		for (SceneActor sa : actors)
		{
			if ((sa.dialogue == null) || (sa.dialogue.equals("")))
				continue;

			String text = sa.dialogue;

			// Create the colours used in the speech bubbles
			Color dark = new Color(0, 0, 0);
			Color pale = new Color(202, 255, 255);

			// Calculate the width and height of the dialogue bubble depending on how much text needs to be drawn
			int width = 20+text.length()*6;

			if (width > 230)
				width = 230;

			String[] textLines = MainCanvas.wrapText(text, 34);
			int height = textLines.length*25;

			int x = 400+sa.pos[0]+sa.headPos[0]+35;
			int y = 300+sa.pos[1]+sa.headPos[1]-height-20;

			int[] xp = {x+20, x+35, x-10+(sa.spriteSize[0]/4)};
			int[] yp = {y+height, y+height, y+height+30};

			g2d.setColor(pale);
			g2d.fillRoundRect(x, y, width, height, 30, 30);
			g2d.setColor(dark);
			g2d.drawRoundRect(x, y, width, height, 30, 30);
			g2d.setColor(pale);
			g2d.fillPolygon(xp, yp, 3);
			g2d.setColor(dark);
			g2d.drawLine(xp[0], yp[0], xp[2], yp[2]);
			g2d.drawLine(xp[1], yp[1], xp[2], yp[2]);

			g2d.setColor(dark);
			for (int i = 0; i < textLines.length; i++)
			{
				g2d.drawString(textLines[i], x+15, y+((i+1)*20));
			}
		}
	}

}

class SceneActor
{
	BufferedImage spriteSheet;
	int[] spriteSize;
	int[] headPos;
	int[] pos;
	boolean animate;
	int stage;
	int totalStage;
	int strip;
	int totalStrip;
	String dialogue = "";
	int totalAnimateTime;
	int animateTime;
	boolean visible = true;

	public SceneActor(String image, int[] pos, int[] headPos, boolean animate, int stage, int totalStage, int strip, int totalStrip, int animateTime)
	{
		this.headPos = headPos;
		this.pos = pos;
		this.animate = animate;
		this.stage = stage;
		this.totalStage = totalStage;
		this.strip = strip;
		this.totalStrip = totalStrip;
		this.totalAnimateTime = animateTime;
		this.animateTime = animateTime;

		if ((image == null) || (image.equals("")))
		{
			
		}
		else
			spriteSheet = GameData.getImage("Spritesheet", image);
		
		if (spriteSheet != null)
			spriteSize = new int[]{spriteSheet.getWidth()/totalStage, spriteSheet.getHeight()/totalStrip};
		else 
			visible = false;
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
}

class SceneAction implements Serializable
{
	private static final long serialVersionUID = 6931684291766473278L;
	String type;
	ArrayList<String> arg = new ArrayList<String>();
	Scene parent;
	
	public SceneAction(String type, Scene parent)
	{
		this.type = type;
		this.parent = parent;
	}
	
	public void action()
	{
		if (type.equals("Speech"))
		{
			speech();
		}
		else if (type.equals("AddNewActor"))
		{
			addNewActor();
		}
		else if (type.equals("ChangeSpritesheet"))
		{
			changeSpritesheet();
		}
		else if (type.equals("ChangeAnimate"))
		{
			changeAnimate();
		}
		else if (type.equals("Pause"))
		{
			pause();
		}
		else if (type.equals("EntityToActor"))
		{
			entityToActor();
		}
	}
	
	public void speech()
	{
		parent.actors.get(Integer.parseInt(arg.get(0))).dialogue = arg.get(1);
		parent.wait = 1;
	}
	
	public void entityToActor()
	{
		Entity e = Main.gamedata.getGameEntities().get(arg.get(0));
		
		if (e == null)
			System.err.println("Entity " + arg.get(0) + " not found!");
		
		int[] pos = {Integer.parseInt(arg.get(1)), Integer.parseInt(arg.get(2)), Integer.parseInt(arg.get(3))};
		
		SceneActor sa = new SceneActor(e.spriteFile, pos, new int[]{e.collisionShape[0]+(e.collisionShape[2]/2), e.collisionShape[1]}, false, e.animateStage, e.animStages, e.animateStrip, e.totalAnimateStrip, (int)e.animateTime);
		
		parent.actors.add(sa);
		
		parent.wait = 3;
	}
	
	public void addNewActor()
	{
		String image = arg.get(0);
		int[] pos = {Integer.parseInt(arg.get(1)), Integer.parseInt(arg.get(2)), Integer.parseInt(arg.get(3))};
		int[] headPos = {Integer.parseInt(arg.get(4)), Integer.parseInt(arg.get(5))};
		boolean animate = arg.get(6).equals("true");
		int stage = Integer.parseInt(arg.get(7));
		int totalStage = Integer.parseInt(arg.get(8));
		int strip = Integer.parseInt(arg.get(9));
		int totalStrip = Integer.parseInt(arg.get(10));
		int animTime = Integer.parseInt(arg.get(11));
		
		SceneActor sa = new SceneActor(image, pos, headPos, animate, stage, totalStage, strip, totalStrip, animTime);
		
		parent.actors.add(sa);
		
		parent.wait = 3;
		
	}
	
	public void changeSpritesheet()
	{
		SceneActor sa = parent.actors.get(Integer.parseInt(arg.get(0)));
		
		sa.spriteSheet = GameData.getImage("Spritesheet", arg.get(1));
		
		sa.stage = Integer.parseInt(arg.get(2));
		sa.totalStage = Integer.parseInt(arg.get(3));
		sa.strip = Integer.parseInt(arg.get(4));
		sa.totalStrip = Integer.parseInt(arg.get(5));
		
		parent.wait = 3;
	}
	
	public void changeAnimate()
	{
		SceneActor sa = parent.actors.get(Integer.parseInt(arg.get(0)));
		
		sa.animate = arg.get(1).equals("true");
		sa.visible = arg.get(2).equals("true");
		sa.animateTime = Integer.parseInt(arg.get(3));
		
		parent.wait = 3;
	}
	
	public void pause()
	{
		parent.waitDuration = Integer.parseInt(arg.get(0));
		parent.wait = 2;
	}
}
