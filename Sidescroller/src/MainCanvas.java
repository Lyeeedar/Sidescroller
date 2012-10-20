import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

/**
 * Main frame of the Game. All game graphics gets drawn directly to this. Uses fullscreen exclusive mode.<p>
 * Also keeps track of the state of all keys, and therefore implements KeyListener <p>
 * @author Lyeeedar
 *
 */
@SuppressWarnings("serial")
public class MainCanvas extends Canvas implements KeyListener{

	/**
	 * Frame resolution. X, Y
	 */
	public static int[] resolution = new int[2];
	public static BufferStrategy bufferStrategy;
	public static int[] screenPosition = new int[2];
	public BufferedImage[] HUDImages = new BufferedImage[5];
	public static Menu menu = new Menu();

	public MainCanvas(GraphicsConfiguration gc)
	{
		// Initialise the Frame with the given graphics configuration
		super(gc);

		// Set the game resolution
		MainCanvas.resolution = new int[]{800, 600};

		this.setIgnoreRepaint(true);

		this.setPreferredSize(new Dimension(resolution[0], resolution[1]));
		this.setIgnoreRepaint(true);
		this.requestFocusInWindow();

		// Add a key listener to the frame to record key presses
		this.addKeyListener(this);

		HUDImages[0] = GameData.getImage("GUI", "HUD.png");
		HUDImages[1] = GameData.getImage("GUI", "spellIconBase.png");
		HUDImages[2] = GameData.getImage("GUI", "HUDMale.png");
		HUDImages[3] = GameData.getImage("GUI", "HUDFemale.png");
		HUDImages[4] = GameData.getImage("GUI", "HUDNoTransform.png");

	}

	public void createStrategy()
	{
		// Initialise the buffer strategy
		this.createBufferStrategy(3);

		// Store the buffer Strategy
		bufferStrategy = this.getBufferStrategy();
	}

	public void paintLoad(GraphicsConfiguration gc)
	{
		Graphics2D g2d = null;

		try {
			// Let the OS have a little time...
			Thread.yield();

			g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

			// Enable AA
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.setColor(Color.DARK_GRAY);

			g2d.fillRect(0, 0, resolution[0], resolution[1]);

			g2d.setColor(Color.GREEN);
			for (int i = 0; i < Main.gamedata.loadStage; i++)
			{
				g2d.fillRect(200+(20*i), 300, 20, 50);
			}

			g2d.setColor(Color.WHITE);

			g2d.drawString(Main.gamedata.loadText, 250, 450);


		} finally {
			// Dispose of the graphics object
			if (g2d != null)
				g2d.dispose();
		}
		// Show the back buffer (Page Flipping)
		if( !bufferStrategy.contentsLost() )
			bufferStrategy.show();

		Toolkit.getDefaultToolkit().sync();	
	}

	/**
	 * Draws all the in-game graphics
	 */
	public void paintMenu(long framerate, GraphicsConfiguration gc)
	{
		Graphics2D g2d = null;

		try {
			// Let the OS have a little time...
			Thread.yield();

			g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

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

			if (Main.debug)
			{
				// Setup fonts and colour for fps
				g2d.setFont(g2d.getFont().deriveFont((float) 20));
				g2d.setColor(Color.YELLOW);

				// Draw fps
				g2d.drawString(Long.toString(framerate), 750, 50);
			}

		} finally {
			// Dispose of the graphics object
			if (g2d != null)
				g2d.dispose();
		}
		// Show the back buffer (Page Flipping)
		if( !bufferStrategy.contentsLost() )
			bufferStrategy.show();

		Toolkit.getDefaultToolkit().sync();	
	}

	/**
	 * Draws all the in-game graphics
	 */
	public void paintGame(long totalTime, GraphicsConfiguration gc)
	{
		Graphics2D g2d = null;

		try {
			// Let the OS have a little time...
			Thread.yield();

			// Calculate the screen position
			this.calculateScreen();

			g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

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

			// Draw HUD
			drawHUD(g2d, totalTime);

		} finally {
			// Dispose of the graphics object
			if (g2d != null)
				g2d.dispose();
		}
		// Show the back buffer (Page Flipping)
		if( !bufferStrategy.contentsLost() )
			bufferStrategy.show();

		Toolkit.getDefaultToolkit().sync();	
	}

	/**
	 * Draws all the in-game graphics
	 */
	public void paintPaused(GraphicsConfiguration gc)
	{
		Graphics2D g2d = null;

		try {
			// Let the OS have a little time...
			Thread.yield();

			// Calculate the screen position
			this.calculateScreen();

			g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

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

			// Draw HUD
			drawHUD(g2d, 0);

			g2d.setColor(new Color(0, 0, 0, 180));

			g2d.fillRect(0, 0, resolution[0], resolution[1]);

			g2d.setColor(Color.WHITE);

			g2d.drawString("Paused", resolution[0]/2, resolution[1]/2);

		} finally {
			// Dispose of the graphics object
			if (g2d != null)
				g2d.dispose();
		}
		// Show the back buffer (Page Flipping)
		if( !bufferStrategy.contentsLost() )
			bufferStrategy.show();

		Toolkit.getDefaultToolkit().sync();	
	}

	public void paintScene(GraphicsConfiguration gc)
	{
		Graphics2D g2d = null;

		try {
			// Let the OS have a little time...
			Thread.yield();

			// Calculate the screen position
			this.calculateScreen();

			g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

			// Enable AA
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			Scene s = Main.gamedata.currentScene;

			s.draw(g2d);

		} finally {
			// Dispose of the graphics object
			if (g2d != null)
				g2d.dispose();
		}
		// Show the back buffer (Page Flipping)
		if( !bufferStrategy.contentsLost() )
			bufferStrategy.show();

		Toolkit.getDefaultToolkit().sync();	
	}

	public void drawHUD(Graphics2D g2d, long totalTime)
	{
		if (Main.gamedata.systemMessages.size() > 0)
		{
			g2d.setColor(new Color(0, 0, 0, 70));
			g2d.fillRect(10, 30, 230, 210);
		}

		for (int i = 0; (i < 10)&&(i < Main.gamedata.systemMessages.size()); i++)
		{
			g2d.setColor(Main.gamedata.systemMessages.get(i).getColour());
			// Draw fps
			g2d.drawString(Main.gamedata.systemMessages.get(i).message, 20, 50+(20*i));
		}


		double health = ((Main.gamedata.getGameEntities().get("Player").getHealth()/Main.gamedata.getGameEntities().get("Player").getMaxHealth())*100);

		if (health > 66)
		{
			g2d.setColor(Color.GREEN);
		}
		else if (health > 33)
		{
			g2d.setColor(Color.YELLOW);
		}
		else
		{
			g2d.setColor(Color.RED);
		}

		int x = 260;
		int y = 30;

		health = health*1.24;

		g2d.fillRect(x+58, y+18, (int)health, 25);

		g2d.drawImage(HUDImages[0], x, y, null);

		for (int i = 0; i < 5; i++)
		{
			g2d.drawImage(HUDImages[1], x-8+(i*40), y+59-8, null);

			if (Character.socketedSpells[i].unlocked == 3)
			{
				g2d.drawImage(Character.socketedSpells[i].images[1], x-8+(i*40), y+59-8, null);
			}

			if (Character.socketedSpells[i].unlocked > 1)
			{
				g2d.drawImage(Character.socketedSpells[i].images[0], x-8+(i*40), y+59-8, null);
			}

			if (Character.spellCooldown[i] < 1)
			{

			}
			else if (Character.spellCooldown[i] > 2550)
			{
				g2d.setColor(Color.BLACK);

				g2d.fillRoundRect(x+(i*40), y+59, 40, 40, 40, 40);
			}
			else
			{
				double trans = 0.1 * Character.spellCooldown[i];

				g2d.setColor(new Color(0, 0, 0, (int)trans));

				g2d.fillRoundRect(x+(i*40), y+59, 40, 40, 40, 40);
			}

		}

		if (Character.gender == 0)
		{
			g2d.drawImage(HUDImages[3], x+8, y+3, null);
		}
		else if (Character.gender == 1)
		{
			g2d.drawImage(HUDImages[2], x+8, y+3, null);
		}

		if (!Main.gamedata.transformAllowed)
		{
			g2d.drawImage(HUDImages[4], x+8, y+3, null);
		}

		if (Character.genderSwapCD < 1)
		{

		}
		else if (Character.genderSwapCD > 2550)
		{
			g2d.setColor(Color.BLACK);

			g2d.fillRoundRect(x+16, y+11, 40, 40, 40, 40);
		}
		else
		{
			double trans = 0.1 * Character.genderSwapCD;

			g2d.setColor(new Color(0, 0, 0, (int)trans));

			g2d.fillRoundRect(x+16, y+11, 40, 40, 40, 40);
		}

		if (Main.debug)
		{
			// Setup fonts and colour for fps
			g2d.setFont(g2d.getFont().deriveFont((float) 20));
			g2d.setColor(Color.YELLOW);

			// Draw fps
			g2d.drawString(Long.toString(totalTime), 750, 50);
		}
	}

	public void drawSpeech(Graphics2D g2d)
	{
		// Loop over all entities
		for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
		{
			Entity e = entry.getValue();
			if (e.isTalking())
			{
				String text = e.getDialogue().getText();

				if (text == null)
				{
					e.setTalking(false);
					continue;
				}

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
				int width = 20+text.length()*6;

				if (width > 230)
					width = 230;

				String[] textLines = wrapText(text, 34);
				int height = textLines.length*25;

				// Is the dialogue is of the type 'Speech' then do a speech bubble.
				if (e.getDialogue().getType() == 0)
				{

					int x = e.getPos()[0]-MainCanvas.screenPosition[0]+15;
					int y = e.getPos()[1]-MainCanvas.screenPosition[1]-height-20;

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
					for (int i = 0; i < textLines.length; i++)
					{
						g2d.drawString(textLines[i], x+15, y+((i+1)*20));
					}

				}
				// If dialogue is of the type 'Examine' do a thought bubble
				else if (e.getDialogue().getType() == 1)
				{
					int x = e.getPos()[0]-MainCanvas.screenPosition[0]-(width/2)+(e.getSize()[0]/2);
					int y = e.getPos()[1]-MainCanvas.screenPosition[1]-height-50;

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
					for (int i = 0; i < textLines.length; i++)
					{
						g2d.drawString(textLines[i], x+15, y+((i+1)*20));
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
				if (e.getDamaged() > 0)
				{
					e.damaged--;
					i = tintImage(i, e.getSize()[0]*(e.getAnimateStage()-1), e.getSize()[1]*(e.getAnimateStrip()-1), e.getSize()[0]*e.getAnimateStage(), e.getSize()[1]*e.getAnimateStrip());
				}

				if (e.getPos()[2] == 1)
				{
					// Draw only a single frame from the spritesheet onto the Graphics object
					g2d.drawImage(i, 
							e.getPos()[0]-MainCanvas.screenPosition[0], e.getPos()[1]-MainCanvas.screenPosition[1], e.getPos()[0]-MainCanvas.screenPosition[0] + e.getSize()[0], e.getPos()[1]-MainCanvas.screenPosition[1] + e.getSize()[1], 
							e.getSize()[0]*(e.getAnimateStage()-1), e.getSize()[1]*(e.getAnimateStrip()-1), e.getSize()[0]*e.getAnimateStage(), e.getSize()[1]*e.getAnimateStrip(),
							null);
				}
				else
				{
					// Draw only a single frame from the spritesheet onto the Graphics object
					g2d.drawImage(i, 
							e.getPos()[0]-MainCanvas.screenPosition[0] + e.getSize()[0], e.getPos()[1]-MainCanvas.screenPosition[1], e.getPos()[0]-MainCanvas.screenPosition[0], e.getPos()[1]-MainCanvas.screenPosition[1] + e.getSize()[1], 
							e.getSize()[0]*(e.getAnimateStage()-1), e.getSize()[1]*(e.getAnimateStrip()-1), e.getSize()[0]*e.getAnimateStage(), e.getSize()[1]*e.getAnimateStrip(),
							null);
				}

				if (Main.debug)
				{
					g2d.setColor(Color.RED);

					g2d.drawRect(e.getCollisionShape()[0]-MainCanvas.screenPosition[0]+e.getPos()[0], e.getCollisionShape()[1]-MainCanvas.screenPosition[1]+e.getPos()[1], e.getCollisionShape()[2], e.getCollisionShape()[3]);

					if (e.alerted)
					{
						g2d.drawString("!", e.getPos()[0]+e.getCollisionShape()[0]-MainCanvas.screenPosition[0], e.getPos()[1]+e.getCollisionShape()[1]-MainCanvas.screenPosition[1]-30);
					}
				}

				for (SystemMessage sysM : e.getInfoText())
				{
					int yPos = (3000 - sysM.aliveTime)/100;
					g2d.setColor(sysM.colour);

					g2d.drawString(sysM.message, e.getPos()[0]-MainCanvas.screenPosition[0]+e.getCollisionShape()[0], e.getPos()[1]-MainCanvas.screenPosition[1]+e.getCollisionShape()[1]-yPos);
				}

				if ((!e.getName().equals("Player")) && (! (e instanceof Spell)) && (! (e instanceof Item)) && (!(e.getFaction().equals(""))))
				{
					double health = (e.getHealth()/e.getMaxHealth())*100;

					if (health > 66)
					{
						g2d.setColor(Color.GREEN);
					}
					else if (health > 33)
					{
						g2d.setColor(Color.YELLOW);
					}
					else
					{
						g2d.setColor(Color.RED);
					}

					health /= 5;

					g2d.fillRect(e.getPos()[0]+e.getCollisionShape()[0]-MainCanvas.screenPosition[0], e.getPos()[1]+e.getCollisionShape()[1]-MainCanvas.screenPosition[1]-10, (int)health, 5);
				}

			}
		}

		if (Character.genderSwapAnimating)
		{
			Entity e = Main.gamedata.getGameEntities().get("Player");
			g2d.drawImage(Character.genderSwapSprite, e.getPos()[0]-MainCanvas.screenPosition[0], e.getPos()[1]-MainCanvas.screenPosition[1],
					e.getPos()[0]-MainCanvas.screenPosition[0]+e.getSize()[0], e.getPos()[1]-MainCanvas.screenPosition[1]+e.getSize()[1],
					(113*Character.genderSwapAnimStage), 0, (113*(Character.genderSwapAnimStage+1)), 100, null);
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

		int width = im.getWidth()-1;
		int height = im.getHeight()-1;

		for (int x = minX; x < maxX; x++)
		{
			if ((x < 0) || (x > width))
				continue;

			for (int y = minY; y < maxY; y++)
			{
				if ((y < 0) || (y > height))
					continue;

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
	static String [] wrapText (String text, int len)
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
	 * Background drawn is based on location of the screen {@link MainCanvas#screenPosition}
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
	 * Foreground drawn is based on location of the screen {@link MainCanvas#screenPosition}
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
	public static boolean esc;
	public static boolean key1;
	public static boolean key2;
	public static boolean key3;
	public static boolean key4;
	public static boolean key5;
	public static boolean tab;

	/** I use this method to store is a key has been pressed
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			MainCanvas.esc = true;

			if ((Main.getState() == 1) || (Main.getState() == 4))
			{
				if (Main.gamedata.BGM.isPaused())
					Main.gamedata.BGM.resume();

				menu.changeMenu("Game");
				MainCanvas.esc = false;
			}

			Main.setState(3);
		}
		else if ((e.getKeyCode() == KeyEvent.VK_A) || (e.getKeyCode() == KeyEvent.VK_LEFT))
		{
			MainCanvas.left = true;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_S) || (e.getKeyCode() == KeyEvent.VK_DOWN))
		{
			MainCanvas.down = true;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_D) || (e.getKeyCode() == KeyEvent.VK_RIGHT))
		{
			MainCanvas.right = true;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_W) || (e.getKeyCode() == KeyEvent.VK_UP))
		{
			MainCanvas.up = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			MainCanvas.space = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			MainCanvas.enter = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_1)
		{
			MainCanvas.key1 = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_2)
		{
			MainCanvas.key2 = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_3)
		{
			MainCanvas.key3 = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_4)
		{
			MainCanvas.key4 = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_5)
		{
			MainCanvas.key5 = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_TAB)
		{
			MainCanvas.tab = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		{

			if (Main.gamedata.currentScene == null)
			{
				Main.gamedata.currentScene = new Scene("Player");

				Main.gamedata.currentScene.start();
			}
			else
			{
				Main.gamedata.currentScene.mode = 3;
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_P)
		{
			if (Main.getState() == 1)
			{
				Main.setState(4);
				Main.gamedata.BGM.pause();
			}
			else
			{
				Main.setState(1);
				Main.gamedata.BGM.resume();
			}
		}

	}

	/** I use this method to 'unpress' keys that have been released
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {

		if ((e.getKeyCode() == KeyEvent.VK_A) || (e.getKeyCode() == KeyEvent.VK_LEFT))
		{
			MainCanvas.left = false;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_S) || (e.getKeyCode() == KeyEvent.VK_DOWN))
		{
			MainCanvas.down = false;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_D) || (e.getKeyCode() == KeyEvent.VK_RIGHT))
		{
			MainCanvas.right = false;
		}
		else if ((e.getKeyCode() == KeyEvent.VK_W) || (e.getKeyCode() == KeyEvent.VK_UP))
		{
			MainCanvas.up = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			MainCanvas.esc = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			MainCanvas.space = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			MainCanvas.enter = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_1)
		{
			MainCanvas.key1 = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_2)
		{
			MainCanvas.key2 = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_3)
		{
			MainCanvas.key3 = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_4)
		{
			MainCanvas.key4 = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_5)
		{
			MainCanvas.key5 = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_TAB)
		{
			MainCanvas.tab = false;
		}

	}

	/** Unused method.
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {

	}
}
