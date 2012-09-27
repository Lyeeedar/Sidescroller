import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;

/**
 * Main frame of the Game. All game graphics gets drawn directly to this. Uses fullscreen exclusive mode.<p>
 * Also keeps track of the state of all keys, and therefore implements KeyListener <p>
 * @author Lyeeedar
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements KeyListener{

	/**
	 * Frame resolution. X, Y
	 */
	public static int[] resolution = new int[2];
	public static BufferStrategy bufferStrategy;
	public static int[] screenPosition = new int[2];
	public static Menu menu = new Menu();
	
	public MainFrame(GraphicsConfiguration gc)
	{
		// Initialise the Frame with the given graphics configuration
		super(gc);

		this.setIgnoreRepaint(true);

		// Set the frame to be undecorated and ignore paint calls from the OS
		if (Main.fullscreen)
			this.setUndecorated(true);

		// Initialise the buffer strategy
		this.createBufferStrategy(1);

		// Store the buffer Strategy
		bufferStrategy = this.getBufferStrategy();

		// If the frame is not running as fullscreen then create a window for it
		if (!Main.fullscreen)
		{
			this.setSize(900, 600);
			this.setVisible(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		// Add a key listener to the frame to record key presses
		this.addKeyListener(this);

		// Set the game resolution
		MainFrame.resolution = new int[]{800, 600};
		
	}
	
	/**
	 * Draws all the in-game graphics
	 */
	public void paintMenu(GraphicsConfiguration gc)
	{
		Graphics2D g2d = null;

		try {

			// Create a BufferedImage compatible with the current environment
			BufferedImage im = gc.createCompatibleImage(resolution[0], resolution[1]);

			// Get its Graphics object
			g2d = (Graphics2D) im.getGraphics();
			
			// Enable AA
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

			// Draw the background to the back buffer
			drawBackground(g2d);

			// Draw all the game Entities to the back buffer
			drawEntities(g2d);

			// Draw the foreground
			drawForeground(g2d);
			
			g2d.setColor(new Color(0, 0, 0, 180));
			
			g2d.fillRect(0, 0, resolution[0], resolution[1]);
			
			menu.drawMenus(g2d);

			// Get the graphics object for the current setting of fullscreen
			if (Main.fullscreen)
			{
				// Get a graphics object for the current backbuffer
				g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
			}
			else
			{
				g2d = (Graphics2D) this.getGraphics();
			}
			
			// Enable AA
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

			// Draw the buffered Image onto the back buffer
			g2d.drawImage(im, 0, 0, this.getWidth(), this.getHeight(), null);


		} finally {
			// Dispose of the graphics object
			if (Main.fullscreen)
				g2d.dispose();
		}
		// Show the back buffer (Page Flipping)
		if (Main.fullscreen)
			bufferStrategy.show();
	}

	/**
	 * Draws all the in-game graphics
	 */
	public void paintGame(long totalTime, GraphicsConfiguration gc)
	{
		Graphics2D g2d = null;

		try {

			// Calculate the screen position
			this.calculateScreen();

			// Create a BufferedImage compatible with the current environment
			BufferedImage im = gc.createCompatibleImage(resolution[0], resolution[1]);

			// Get its Graphics object
			g2d = (Graphics2D) im.getGraphics();
			
			// Enable AA
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

			// Draw the background to the back buffer
			drawBackground(g2d);

			// Draw all the game Entities to the back buffer
			drawEntities(g2d);

			// Draw the foreground
			drawForeground(g2d);
			
			// Draw speech bubbles
			drawSpeech(g2d);
			
			if (Main.getState() == 2)
			{
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.fillRect((resolution[0]/2)-100, (resolution[1]/2)-30, 200, 60);
				
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawRect((resolution[0]/2)-100, (resolution[1]/2)-30, 200, 60);
				
				if (Main.gamedata.loading)
				{
					g2d.drawString("Loading - Please Wait", (resolution[0]/2)-70, (resolution[1]/2)+5);
				}
				else if (Main.gamedata.saving)
				{
					g2d.drawString("Saving - Please Wait", (resolution[0]/2)-70, (resolution[1]/2)+5);
				}
			}

			// Setup fonts and colour for fps
			g2d.setFont(g2d.getFont().deriveFont((float) 20));
			g2d.setColor(Color.YELLOW);

			// Draw fps
			g2d.drawString(Long.toString(totalTime), 50, 50);

			// Get the graphics object for the current setting of fullscreen
			if (Main.fullscreen)
			{
				// Get a graphics object for the current backbuffer
				g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
			}
			else
			{
				g2d = (Graphics2D) this.getGraphics();
			}
			
			// Enable AA
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

			// Draw the buffered Image onto the back buffer
			g2d.drawImage(im, 0, 0, this.getWidth(), this.getHeight(), null);


		} finally {
			// Dispose of the graphics object
			if (Main.fullscreen)
				g2d.dispose();
		}
		// Show the back buffer (Page Flipping)
		if (Main.fullscreen)
			bufferStrategy.show();
	}

	public void drawSpeech(Graphics2D g2d)
	{
		// Loop over all entities
		for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
		{
			Entity e = entry.getValue();
			if (e.isTalking())
			{
				// Create the colours used in the speech bubbles
				Color dark = null;
				Color pale = null;
				if (e.getTalkingTimer() < Dialogue.dialogueFade)
				{
					dark = new Color(0, 0, 0);
					pale = new Color(202, 255, 255);
				}
				else
				{
					int alpha = (int) (255-(Dialogue.fadeStep*(e.getTalkingTimer()-Dialogue.dialogueFade)));

					if (alpha < 0)
						alpha = 0;

					dark = new Color(0, 0, 0, alpha);
					pale = new Color(202, 255, 255, alpha);
				}
				
				// Calculate the width and height of the dialogue bubble depending on how much text needs to be drawn
				int width = 20+e.getDialogue().getText().length()*6;
				
				if (width > 200)
					width = 200;
				
				String[] text = wrapText(e.getDialogue().getText(), 34);
				int height = text.length*25;
				
				// Is the dialogue is of the type 'Speech' then do a speech bubble.
				if (e.getDialogue().getType() == 0)
				{

					int x = e.getPos()[0]-MainFrame.screenPosition[0]+15;
					int y = e.getPos()[1]-MainFrame.screenPosition[1]-height-20;

					int[] xp = {x+20, x+35, x-10+(e.getSize()[0]/2)};
					int[] yp = {y+height, y+height, y+height+15};

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
					for (int i = 0; i < text.length; i++)
					{
						g2d.drawString(text[i], x+15, y+((i+1)*20));
					}
				
				}
				// If dialogue is of the type 'Examine' do a thought bubble
				else if (e.getDialogue().getType() == 1)
				{
					int x = e.getPos()[0]-MainFrame.screenPosition[0]-(width/2)+(e.getSize()[0]/2);
					int y = e.getPos()[1]-MainFrame.screenPosition[1]-height-50;

					g2d.setColor(pale);
					g2d.fillRoundRect(x+(width/2)-5, y+height+35, 10, 10, 10, 10);
					g2d.setColor(dark);
					g2d.drawRoundRect(x+(width/2)-5, y+height+35, 10, 10, 10, 10);
					
					g2d.setColor(pale);
					g2d.fillRoundRect(x+(width/2)-10, y+height+10, 20, 20, 20, 20);
					g2d.setColor(dark);
					g2d.drawRoundRect(x+(width/2)-10, y+height+10, 20, 20, 20, 20);
					
					g2d.setColor(pale);
					g2d.fillRoundRect(x, y, width, height, 30, 30);
					g2d.setColor(dark);
					g2d.drawRoundRect(x, y, width, height, 30, 30);
					g2d.setColor(pale);

					g2d.setColor(dark);
					for (int i = 0; i < text.length; i++)
					{
						g2d.drawString(text[i], x+15, y+((i+1)*20));
					}
				}
				
			}
		}
	}

	/**
	 * Method that draws all the game entites kept in GameData
	 * @param g2d
	 */
	private void drawEntities(Graphics2D g2d)
	{
		// Loop over all entities
		for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
		{
			Entity e = entry.getValue();
			// If entity is not on the screen then dont draw it
			if ((e.getPos()[0]+e.getSize()[0] < screenPosition[0]) || (e.getPos()[0] > (screenPosition[0]+resolution[0]))
					|| (e.getPos()[1]+e.getSize()[1] < screenPosition[1]) || (e.getPos()[1] > (screenPosition[1]+resolution[1])))
				continue;

			// If entity has a graphics sheet and is visible then draw it
			if ((e.getSpriteSheet() != null) && (e.isVisible()))
			{
				BufferedImage i = e.getSpriteSheet();
				
				// If the entity has been damaged then tint the image with red
				//if (e.isDamaged())
					//i = tintImage(i, e.getSize()[0]*(e.getAnimateStage()-1), e.getSize()[1]*(e.getAnimateStrip()-1), e.getSize()[0]*e.getAnimateStage(), e.getSize()[1]*e.getAnimateStrip());

				if (e.getPos()[2] == 1)
				{
					// Draw only a single frame from the spritesheet onto the Graphics object
					g2d.drawImage(i, 
							e.getPos()[0]-MainFrame.screenPosition[0], e.getPos()[1]-MainFrame.screenPosition[1], e.getPos()[0]-MainFrame.screenPosition[0] + e.getSize()[0], e.getPos()[1]-MainFrame.screenPosition[1] + e.getSize()[1], 
							e.getSize()[0]*(e.getAnimateStage()-1), e.getSize()[1]*(e.getAnimateStrip()-1), e.getSize()[0]*e.getAnimateStage(), e.getSize()[1]*e.getAnimateStrip(),
							null);
				}
				else
				{
					// Draw only a single frame from the spritesheet onto the Graphics object
					g2d.drawImage(i, 
							e.getPos()[0]-MainFrame.screenPosition[0] + e.getSize()[0], e.getPos()[1]-MainFrame.screenPosition[1], e.getPos()[0]-MainFrame.screenPosition[0], e.getPos()[1]-MainFrame.screenPosition[1] + e.getSize()[1], 
							e.getSize()[0]*(e.getAnimateStage()-1), e.getSize()[1]*(e.getAnimateStrip()-1), e.getSize()[0]*e.getAnimateStage(), e.getSize()[1]*e.getAnimateStrip(),
							null);
				}

				g2d.setColor(Color.RED);

				//g2d.drawRect(e.getCollisionShape()[0]-MainFrame.screenPosition[0]+e.getPos()[0], e.getCollisionShape()[1]-MainFrame.screenPosition[1]+e.getPos()[1], e.getCollisionShape()[2], e.getCollisionShape()[3]);

				g2d.setColor(Color.YELLOW);

			}
		}
	}
	
	/**
	 * Method that returns a deep copy of the bufferedimage
	 * @param bi
	 * @return
	 */
	public BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	/**
	 * Method that tints the supplied bufferedimage with red
	 * @param image
	 * @param minX
	 * @param minY
	 * @param maxX
	 * @param maxY
	 * @return
	 */
	public BufferedImage tintImage(BufferedImage image, int minX, int minY, int maxX, int maxY)
	{
		BufferedImage im = deepCopy(image);
		
		for (int x = minX; x < maxX; x++)
		{
			for (int y = minY; y < maxY; y++)
			{
				int colour = im.getRGB(x, y);

				int alpha = (colour>>24) & 0xff;
				
				if (alpha == 0)
					continue;
				
				int blue = colour & 0xFF;
				int green = (colour >> 8) & 0xFF;
				int red = (colour >> 16) & 0xFF;
				
				blue -= 60;
				if (blue < 0)
					blue = 0;
				
				green -= 60;
				if (green < 0)
					green = 0;
				
				red += 100;
				if (red > 250)
					red = 250;
				
				im.setRGB(x, y, new Color(red, green, blue, alpha).getRGB());

			}
		}
		
		return im;
	}

	/**
	 * Method to split text along word boundries so each line is less than the given length
	 * @param text
	 * @param len
	 * @return
	 */
	String [] wrapText (String text, int len)
	{
	  // return empty array for null text
	  if (text == null)
	  return new String [] {};

	  // return text if len is zero or less
	  if (len <= 0)
	  return new String [] {text};

	  // return text if less than length
	  if (text.length() <= len)
	  return new String [] {text};

	  char [] chars = text.toCharArray();
	  Vector<String> lines = new Vector<String>();
	  StringBuffer line = new StringBuffer();
	  StringBuffer word = new StringBuffer();

	  for (int i = 0; i < chars.length; i++) {
	    word.append(chars[i]);

	    if (chars[i] == ' ') {
	      if ((line.length() + word.length()) > len) {
	        lines.add(line.toString());
	        line.delete(0, line.length());
	      }

	      line.append(word);
	      word.delete(0, word.length());
	    }
	  }

	  // handle any extra chars in current word
	  if (word.length() > 0) {
	    if ((line.length() + word.length()) > len) {
	      lines.add(line.toString());
	      line.delete(0, line.length());
	    }
	    line.append(word);
	  }

	  // handle extra line
	  if (line.length() > 0) {
	    lines.add(line.toString());
	  }

	  String [] ret = new String[lines.size()];
	  int c = 0; // counter
	  for (Enumeration<String> e = lines.elements(); e.hasMoreElements(); c++) {
	    ret[c] = (String) e.nextElement();
	  }

	  return ret;
	}

	/**
	 * Method that draws all the background (3 layers) onto the Graphics object <p>
	 * Background taken from GameData <p>
	 * Background drawn is based on location of the screen {@link MainFrame#screenPosition}
	 * @param g2d
	 */
	private void drawBackground(Graphics2D g2d)
	{
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
	}

	/**
	 * Method that draws all the foreground (1 layer) onto the Graphics object <p>
	 * Foreground taken from GameData (background index 3+)<p>
	 * Foreground drawn is based on location of the screen {@link MainFrame#screenPosition}
	 * @param g2d
	 */
	private void drawForeground(Graphics2D g2d)
	{
		// Foreground layer
		g2d.drawImage(Main.gamedata.getBackground()[4],
				0, 0, resolution[0], resolution[1],
				screenPosition[0], screenPosition[1], screenPosition[0]+resolution[0], screenPosition[1]+resolution[1],
				null);
	}

	/**
	 * Method to calculate the current location of the screen. <p>
	 * Is around the player (Game Entity index 0), but always stays within the bounds of the level.
	 */
	private void calculateScreen()
	{
		// Extract position of the player
		int[] pos = Main.gamedata.getGameEntities().get("Player").getPos();

		// If screen is at the left edge then lock it so it wont show blank areas
		if (pos[0] - (resolution[0]/2) < 0)
		{
			screenPosition[0] = 0;
		}
		// If the screen is at the right edge then lock it
		else if (pos[0] + (resolution[0]/2) > GameData.levelSize[0])
		{
			screenPosition[0] = GameData.levelSize[0]-resolution[0];
		}
		// Else position the screen so the player is in the center of it
		else
		{
			screenPosition[0] = pos[0]-(resolution[0]/2);
		}

		// If screen is at the top edge then lock it
		if (pos[1] - (resolution[1]/2) < 0)
		{
			screenPosition[1] = 0;
		}
		// If the screen is at the bottom edge then lock it
		else if (pos[1] + (resolution[1]/2) > GameData.levelSize[1])
		{
			screenPosition[1] = GameData.levelSize[1]-resolution[1];
		}
		// Else position the screen so the player is in the center
		else
		{
			screenPosition[1] = pos[1]-(resolution[1]/2);
		}
	}

	/* --------- Key values --------- */
	public static boolean up;
	public static boolean left;
	public static boolean right;
	public static boolean down;
	public static boolean space;
	public static boolean enter;
	public static boolean key1;

	/** I use this method to store is a key has been pressed
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			Main.setState(3);
		}
		else if ((e.getKeyCode() == KeyEvent.VK_A) || (e.getKeyCode() == KeyEvent.VK_LEFT))
		{
			MainFrame.left = true;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_S) || (e.getKeyCode() == KeyEvent.VK_DOWN))
		{
			MainFrame.down = true;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_D) || (e.getKeyCode() == KeyEvent.VK_RIGHT))
		{
			MainFrame.right = true;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_W) || (e.getKeyCode() == KeyEvent.VK_UP))
		{
			MainFrame.up = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			MainFrame.space = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			MainFrame.enter = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_1)
		{
			MainFrame.key1 = true;
		}

	}

	/** I use this method to 'unpress' keys that have been released
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {

		if ((e.getKeyCode() == KeyEvent.VK_A) || (e.getKeyCode() == KeyEvent.VK_LEFT))
		{
			MainFrame.left = false;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_S) || (e.getKeyCode() == KeyEvent.VK_DOWN))
		{
			MainFrame.down = false;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_D) || (e.getKeyCode() == KeyEvent.VK_RIGHT))
		{
			MainFrame.right = false;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_W) || (e.getKeyCode() == KeyEvent.VK_UP))
		{
			MainFrame.up = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			MainFrame.space = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			MainFrame.enter = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_1)
		{
			MainFrame.key1 = false;
		}

	}

	/** Unused method.
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {

	}
}
