import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Lyeeedar
 *
 */
public class Menu {
	
	public int selectedIndex = 0;
	/**
	 * Menu types: <p>
	 * Menu - Layout = <p>
	 * 
	 * ################ <p>
	 * ##-0-######-1-## <p>
	 * ################ <p>
	 * ##-2-######-3-## <p>
	 * ################ <p>
	 * ##-4-######-5-## <p>
	 * ################ <p>
	 * ######-6-####### <p>
	 * ################
	 * 
	 */
	public String menuType = "Menu";
	
	BufferedImage[] menuGraphics = new BufferedImage[2];
	
	public Menu()
	{
		try{
			menuGraphics[0] = ImageIO.read(new File("Data/Resources/GUI/MenuPanel.png"));
			menuGraphics[1] = ImageIO.read(new File("Data/Resources/GUI/button.png"));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	private void drawOptionsMenu(Graphics2D g2d)
	{
		Color normal = new Color(0, 0, 0, 170);
		Color selected = new Color(0, 50, 100, 250);
		
		g2d.drawImage(menuGraphics[0], 0, 0, null);
		
		
		g2d.setFont(g2d.getFont().deriveFont((float) 20));
		
		if (selectedIndex == 0)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("Fullscreen (requires restart)   :   " + Main.fullscreen, 250, 260);
		
		if (selectedIndex == 1)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("", 460, 260);
		
		if (selectedIndex == 2)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("", 250, 318);
		
		if (selectedIndex == 3)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("", 460, 318);
		
		if (selectedIndex == 4)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("", 250, 373);
		
		if (selectedIndex == 5)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("", 460, 373);
		
		if (selectedIndex == 6)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("Back", 360, 428);
	}
	
	private void drawGameMenu(Graphics2D g2d)
	{
		Color normal = new Color(0, 0, 0, 170);
		Color selected = new Color(0, 50, 100, 250);
		
		g2d.drawImage(menuGraphics[0], 0, 0, null);
		
		
		g2d.setFont(g2d.getFont().deriveFont((float) 20));
		
		if (selectedIndex == 0)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("Resume", 250, 260);
		
		if (selectedIndex == 1)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("Options", 460, 260);
		
		if (selectedIndex == 2)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("Spells", 250, 318);
		
		if (selectedIndex == 3)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("Character", 460, 318);
		
		if (selectedIndex == 4)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("Save", 250, 373);
		
		if (selectedIndex == 5)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("Load", 460, 373);
		
		if (selectedIndex == 6)
			g2d.setColor(selected);
		else
			g2d.setColor(normal);
		
		g2d.drawString("Exit", 360, 428);
	}
	
	public void drawMenus(Graphics2D g2d)
	{
		if (menuType.equals("Menu"))
		{
			drawGameMenu(g2d);
		}
		else if (menuType.equals("Options"))
		{
			drawOptionsMenu(g2d);
		}
	}
	
	public void evaluateButtons()
	{
		if (MainFrame.right)
		{
			selectedIndex++;
			MainFrame.right = false;
			if (selectedIndex > 6)
				selectedIndex = 6;
		}
		else if (MainFrame.left)
		{
			selectedIndex--;
			MainFrame.left = false;
			if (selectedIndex < 0)
				selectedIndex = 0;
		}
		else if (MainFrame.up)
		{
			selectedIndex -= 2;
			MainFrame.up = false;
			if (selectedIndex < 0)
				selectedIndex = 0;
		}
		else if (MainFrame.down)
		{
			selectedIndex += 2;
			MainFrame.down = false;
			if (selectedIndex > 6)
				selectedIndex = 6;
		}
		else if (MainFrame.enter)
		{
			if (menuType.equals("Menu"))
			{
				if (selectedIndex == 0)
				{
					Main.setState(1);
					selectedIndex = 0;
				}
				if (selectedIndex == 1)
				{
					menuType = "Options";
					selectedIndex = 0;
				}
				else if (selectedIndex == 4)
				{
					Main.gamedata.saveGame();
					Main.setState(1);
				}
				else if (selectedIndex == 6)
				{
					Main.setState(0);
					selectedIndex = 0;
				}
				MainFrame.enter = false;
			}
			else if (menuType.equals("Options"))
			{
				if (selectedIndex == 6)
				{
					menuType = "Menu";
					selectedIndex = 1;
				}
				MainFrame.enter = false;
			}
			
		}
		
		
	}

}
