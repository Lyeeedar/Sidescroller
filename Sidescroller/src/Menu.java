import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Lyeeedar
 *
 */
public class Menu {
	/**
	 * Menu types: <p>
	 * "Menu" - Layout = <p>
	 * 
	 * ################ <p>
	 * ##-0-######-1-## <p>
	 * ################ <p>
	 * ##-2-######-3-## <p>
	 * ################ <p>
	 * ##-4-######-5-## <p>
	 * ################ <p>
	 *  <p> <p>
	 * "Skill" - Layout = 
	 *
	 *		0			|	12	O
	 *	1		2		|		|	\
	 *	3		4		|		O	O
	 *					|		|	/
	 *	5	6	7	 	|		O
	 *	8	9	10		|		/	\
	 *		11
	 */
	MenuScreen menu;

	public void drawMenus(Graphics2D g2d)
	{
		menu.draw(g2d);
	}

	public void evaluateButtons()
	{
		menu.evaluateButtons();
	}

	public void changeMenu(String menu)
	{
		if (menu.equals("Game"))
		{
			this.menu = new GameMenu(this);
		}
		else if (menu.equals("Spells"))
		{
			this.menu = new SpellMenu(this);
		}
		else if (menu.equals("Char"))
		{
			this.menu = new CharacterMenu(this);
		}
		else if (menu.equals("Save"))
		{
			this.menu = new SaveMenu(this);
		}
		else if (menu.equals("Load"))
		{
			this.menu = new LoadMenu(this);
		}
	}


}
// End Menu

abstract class MenuScreen
{
	Menu menu;
	BufferedImage backImage;

	public MenuScreen(Menu menu)
	{
		this.menu = menu;
		
		backImage = GameData.getImage("Data/Resources/GUI/Spellbook.png");
	}

	public void draw(Graphics2D g2d)
	{
		drawBackground(g2d);
		drawLeft(g2d);
		drawRight(g2d);
	}

	protected void drawBackground(Graphics2D g2d)
	{
		g2d.drawImage(backImage, 0, 0, null);
	}

	abstract void evaluateButtons();
	abstract protected void drawLeft(Graphics2D g2d);
	abstract protected void  drawRight(Graphics2D g2d);
}

class GameMenu extends MenuScreen
{
	BufferedImage[] images = new BufferedImage[3];
	
	/**
	 * @param menu
	 */
	public GameMenu(Menu menu) {
		super(menu);

		images[0] = GameData.getImage("Data/Resources/GUI/spellbookGameText.png");
		images[1] = GameData.getImage("Data/Resources/GUI/spellbookButton.png");
		images[2] = GameData.getImage("Data/Resources/GUI/spellbookButtonSelected.png");
	}

	int selectedIndex;

	@Override
	protected void drawBackground(Graphics2D g2d)
	{
		super.drawBackground(g2d);
		
		g2d.drawImage(images[0], 0, 0, null);
	}
	
	protected void drawLeft(Graphics2D g2d)
	{
		//110 , 65
		Color normal = new Color(0, 0, 0, 170);
		Color selected = new Color(54, 4, 89, 190);

		g2d.setFont(g2d.getFont().deriveFont((float) 20));

		if (selectedIndex == 0)
		{
			g2d.drawImage(images[2], 90, 90, null);
			g2d.setColor(selected);
			g2d.drawString("Resume", 200, 155);
		}
		else
		{
			g2d.drawImage(images[1], 90, 90, null);
			g2d.setColor(normal);
			g2d.drawString("Resume", 200, 155);
		}

		if (selectedIndex == 1)
		{
			g2d.drawImage(images[2], 490, 90, null);
			g2d.setColor(selected);
			g2d.drawString("Save", 600, 155);
		}
		else
		{
			g2d.drawImage(images[1], 490, 90, null);
			g2d.setColor(normal);
			g2d.drawString("Save", 600, 155);
		}

		if (selectedIndex == 2)
		{
			g2d.drawImage(images[2], 90, 220, null);
			g2d.setColor(selected);
			g2d.drawString("Spells", 200, 285);
		}
		else
		{
			g2d.drawImage(images[1], 90, 220, null);
			g2d.setColor(normal);
			g2d.drawString("Spells", 200, 285);
		}

		if (selectedIndex == 3)
		{
			g2d.drawImage(images[2], 490, 220, null);
			g2d.setColor(selected);
			g2d.drawString("Load", 600, 285);
		}
		else
		{
			g2d.drawImage(images[1], 490, 220, null);
			g2d.setColor(normal);
			g2d.drawString("Load", 600, 285);
		}

		if (selectedIndex == 4)
		{
			g2d.drawImage(images[2], 90, 350, null);
			g2d.setColor(selected);
			g2d.drawString("Character", 200, 415);
		}
		else
		{
			g2d.drawImage(images[1], 90, 350, null);
			g2d.setColor(normal);
			g2d.drawString("Character", 200, 415);
		}

		if (selectedIndex == 5)
		{
			g2d.drawImage(images[2], 490, 350, null);
			g2d.setColor(selected);
			g2d.drawString("Exit", 600, 415);
		}
		else
		{
			g2d.drawImage(images[1], 490, 350, null);
			g2d.setColor(normal);
			g2d.drawString("Exit", 600, 415);
		}
	}

	/* (non-Javadoc)
	 * @see Menu#evaluateButtons()
	 */
	@Override
	void evaluateButtons() {
		if (MainFrame.up)
		{
			selectedIndex -= 2;
			MainFrame.up = false;
		}
		else if (MainFrame.right)
		{
			selectedIndex++;
			MainFrame.right = false;
		}
		else if (MainFrame.left)
		{
			selectedIndex--;
			MainFrame.left = false;
		}
		else if (MainFrame.down)
		{
			selectedIndex += 2;
			MainFrame.down = false;
		}
		else if (MainFrame.esc)
		{
			Main.setState(1);
			MainFrame.esc = false;
		}
		else if (MainFrame.enter)
		{
			if (selectedIndex == 0)
			{
				Main.setState(1);
			}
			else if (selectedIndex == 1)
			{
				menu.changeMenu("Save");
			}
			else if (selectedIndex == 2)
			{
				menu.changeMenu("Spells");
			}
			else if (selectedIndex == 3)
			{
				menu.changeMenu("Load");
			}
			else if (selectedIndex == 4)
			{
				menu.changeMenu("Char");
			}

			MainFrame.enter = false;
		}

		if (selectedIndex < 0)
		{
			selectedIndex = 0;
		}
		else if (selectedIndex > 5)
		{
			selectedIndex = 5;
		}

	}

	/* (non-Javadoc)
	 * @see MenuScreen#drawRight(java.awt.Graphics2D)
	 */
	@Override
	protected void drawRight(Graphics2D g2d) {
		// TODO Auto-generated method stub

	}

}

class SpellMenu extends MenuScreen
{

	BufferedImage[] images = new BufferedImage[5];

	public SpellMenu(Menu menu) {
		super(menu);

		images[0] = GameData.getImage("Data/Resources/GUI/spellIconBase.png");
		images[1] = GameData.getImage("Data/Resources/GUI/spellIconBaseSelected.png");
		images[2] = GameData.getImage("Data/Resources/GUI/spellbookSpellText.png");
		images[3] = GameData.getImage("Data/Resources/GUI/spellbookSpellTree.png");
		images[4] = GameData.getImage("Data/Resources/GUI/spellbookSpellTreeSelected.png");
	}

	@Override
	protected void drawBackground(Graphics2D g2d)
	{
		super.drawBackground(g2d);
		g2d.drawImage(images[2], 0, 0, null);
	}
	
	boolean treeSelected = false;
	int selectedIndex = 0;

	int spellStage = 0;
	int spellIndex = 0;

	String element = "Fire";

	final int[] size = {240, 302};
	
	final String[] sockets = {"Shout", "Punch", "Strike", "Kick", "Stomp"};

	private BufferedImage getSpellTreeImage(ArrayList<SpellsStage> spells, int stage, int selected)
	{
		BufferedImage im = new BufferedImage(1000, 100+(spells.size()*100), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = im.createGraphics();
		
		g2d.setColor(Color.BLACK);
		
		for (int i = 0; i < spells.size(); i++)
		{
			SpellsStage ss = spells.get(i);
			
			for (int ii = 0; ii < ss.spells.size(); ii++)
			{
				SpellsStageEntry sse = ss.spells.get(ii);
				if (sse.unlocked > 0)
				{
					if ((i == stage) && (ii == selected))
					{
						g2d.drawImage(images[1], sse.pos[0]-22, sse.pos[1]-22, null);
					}
					else
					{
						g2d.drawImage(images[0], sse.pos[0]-15, sse.pos[1]-15, null);
					}
				}
				
				if (sse.unlocked == 3)
				{
					g2d.drawImage(sse.images[1], sse.pos[0]-15, sse.pos[1]-15, null);
				}
				else if (sse.unlocked  == 2)
				{
					g2d.drawImage(sse.images[0], sse.pos[0]-15, sse.pos[1]-15, null);
				}
				else if (sse.unlocked == 0)
				{
					continue;
				}
				
				for (String p : sse.parents)
				{
					SpellsStageEntry parent = spells.get(i-1).getSpellStageEntry(p);
					
					g2d.drawLine(sse.pos[0], sse.pos[1], parent.pos[0], parent.pos[1]);
				}
			}
		}

		g2d.dispose();
		
		
		return im;
	}
	
	protected void drawRight(Graphics2D g2d)
	{
		BufferedImage im = getSpellTreeImage(Character.getSpell(element), spellStage, spellIndex);

		int minX = Character.getSpell(element).get(spellStage).spells.get(spellIndex).pos[0]-(size[0]/2);
		int maxX = Character.getSpell(element).get(spellStage).spells.get(spellIndex).pos[0]+(size[0]/2);

		int minY = Character.getSpell(element).get(spellStage).spells.get(spellIndex).pos[1]-(size[1]/2);
		int maxY = Character.getSpell(element).get(spellStage).spells.get(spellIndex).pos[1]+(size[1]/2);

		if (minX < 0)
		{
			minX = 0;
			maxX = size[0];
		}
		else if (maxX > im.getWidth())
		{
			maxX = im.getWidth();
			minX = im.getWidth()-size[0];
		}

		if (minY < 0)
		{
			minY = 0;
			maxY = size[1];
		}
		else if (maxY > im.getHeight())
		{
			maxY = im.getHeight();
			minY = im.getHeight()-size[1];
		}

		if (treeSelected)
		{
			g2d.drawImage(images[4], 0, 0, null);
		}
		else if (selectedIndex == 12)
		{
			g2d.drawImage(images[3], 0, 0, null);
		}

		g2d.drawImage(im, 478, 70, 478+size[0], 70+size[1], minX, minY, maxX, maxY, null);

		SpellsStageEntry sse = Character.getSpell(element).get(spellStage).spells.get(spellIndex);

		if (selectedIndex < 5)
		{
			sse = Character.socketedSpells[selectedIndex];
		}

		g2d.setColor(Color.BLACK);

		g2d.drawString(sse.name, 485, 100+size[1]+15);

		String[] description = MainFrame.wrapText(sse.description, 35);

		int i;
		for (i = 0 ; i < description.length; i++)
		{
			g2d.drawString(description[i], 495, 100+size[1]+35+(20*i));
		}

		i++;

		if (sse.unlocked == 1)
		{
			g2d.drawString("You know that this spell exists.", 495, 100+size[1]+35+(20*i));
		}
		else if (sse.unlocked == 2)
		{
			g2d.drawString("You have learnt this spell.", 495, 100+size[1]+35+(20*i));
		}
		else if (sse.unlocked == 3)
		{
			g2d.drawString("You have mastered this spell.", 495, 100+size[1]+35+(20*i));
		}

		i++;

		for (int ii = 0; ii < 5; ii++)
		{
			if (sse.name.equals(Character.socketedSpells[ii].name))
			{
				//g2d.drawString("Bound to "+sockets[ii], 495, 110+size[1]+35+(20*i));
				i++;
			}
		}
	}

	protected void drawLeft(Graphics2D g2d)
	{

		if (selectedIndex == 0)
		{
			g2d.drawImage(images[1], 168, 53, null);
		}
		else
		{
			g2d.drawImage(images[0], 175, 60, null);
		}
		
		SpellsStageEntry sse = null;
		
		sse = Character.socketedSpells[0];
		
		if (sse.unlocked == 3)
		{
			g2d.drawImage(sse.images[1], 175, 60, null);
		}
		else if (sse.unlocked  == 2)
		{
			g2d.drawImage(sse.images[0], 175, 60,null);
		}

		g2d.setColor(Color.BLACK);
		g2d.drawLine(195, 100, 145, 160);
		g2d.drawLine(195, 100, 245, 160);

		g2d.drawLine(195, 100, 195, 200);

		g2d.drawLine(195, 200, 145, 280);
		g2d.drawLine(195, 200, 245, 280);

		if (selectedIndex == 1)
		{
			g2d.drawImage(images[1], 118, 133, null);
		}
		else
		{
			g2d.drawImage(images[0], 125, 140, null);
		}
		
		sse = Character.socketedSpells[1];
		
		if (sse.unlocked == 3)
		{
			g2d.drawImage(sse.images[1], 125, 140, null);
		}
		else if (sse.unlocked  == 2)
		{
			g2d.drawImage(sse.images[0], 125, 140,null);
		}
			
		if (selectedIndex == 2)
		{
			g2d.drawImage(images[1], 218, 133, null);
		}
		else
		{
			g2d.drawImage(images[0], 225, 140, null);
		}
		sse = Character.socketedSpells[2];
		
		if (sse.unlocked == 3)
		{
			g2d.drawImage(sse.images[1], 225, 140, null);
		}
		else if (sse.unlocked  == 2)
		{
			g2d.drawImage(sse.images[0], 225, 140,null);
		}


		if (selectedIndex == 3)
		{
			g2d.drawImage(images[1], 118, 253, null);
		}
		else
		{
			g2d.drawImage(images[0], 125, 260, null);
		}
		sse = Character.socketedSpells[3];
		
		if (sse.unlocked == 3)
		{
			g2d.drawImage(sse.images[1], 125, 260, null);
		}
		else if (sse.unlocked  == 2)
		{
			g2d.drawImage(sse.images[0], 125, 260,null);
		}


		if (selectedIndex == 4)
		{
			g2d.drawImage(images[1], 218, 253, null);
		}
		else
		{
			g2d.drawImage(images[0], 225, 260, null);
		}
		sse = Character.socketedSpells[4];
		
		if (sse.unlocked == 3)
		{
			g2d.drawImage(sse.images[1], 225, 260, null);
		}
		else if (sse.unlocked  == 2)
		{
			g2d.drawImage(sse.images[0], 225, 260,null);
		}



		if (selectedIndex == 5)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		g2d.drawRect(60, 400, 80, 30);
		g2d.drawString("Fire", 70, 420);

		if (selectedIndex == 6)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		g2d.drawRect(155, 400, 80, 30);
		g2d.drawString("Air", 165, 420);

		if (selectedIndex == 7)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		g2d.drawRect(250, 400, 80, 30);
		g2d.drawString("Earth", 260, 420);

		if (selectedIndex == 8)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		g2d.drawRect(60, 450, 80, 30);
		g2d.drawString("Water", 70, 470);

		if (selectedIndex == 9)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		g2d.drawRect(155, 450, 80, 30);
		g2d.drawString("Death", 165, 470);

		if (selectedIndex == 10)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		g2d.drawRect(250, 450, 80, 30);
		g2d.drawString("Life", 260, 470);

		if (selectedIndex == 11)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		g2d.drawRect(155, 500, 80, 30);
		g2d.drawString("Back", 165, 520);
	}

	void evaluateButtons() {

		if (MainFrame.esc)
		{
			menu.changeMenu("Game");
			MainFrame.esc = false;
		}

		if (!treeSelected)
		{
			if (MainFrame.up)
			{
				if (selectedIndex < 7)
				{
					selectedIndex -= 2;
				}
				else if (selectedIndex == 12)
				{

				}
				else if (selectedIndex == 11)
				{
					selectedIndex -= 2;
				}
				else
				{
					selectedIndex -= 3;
				}
				MainFrame.up = false;
			}
			else if (MainFrame.down)
			{
				if (selectedIndex == 0)
				{
					selectedIndex++;
				}
				else if (selectedIndex < 5)
				{
					selectedIndex += 2;
				}
				else if (selectedIndex > 10)
				{

				}
				else
				{
					selectedIndex += 3;
					if (selectedIndex > 11)
					{
						selectedIndex = 11;
					}
				}

				MainFrame.down = false;
			}
			else if (MainFrame.left)
			{
				
				if (selectedIndex == 12)
				{
					selectedIndex = 4;
				}
				else
				{
					selectedIndex--;
				}
					
				MainFrame.left = false;
			}
			else if (MainFrame.right)
			{
				if ((selectedIndex == 0) || (selectedIndex == 2) || (selectedIndex == 4) || (selectedIndex == 7) || (selectedIndex == 10) || (selectedIndex == 11))
				{
					selectedIndex = 12;
				}
				else
				{
					selectedIndex++;
				}

				MainFrame.right = false;
			}
			else if (MainFrame.enter)
			{
				if (selectedIndex == 5)
				{
					if (element.equals("Fire"))
					{
						
					}
					else
					{
						element = "Fire";
						spellIndex = 0;
						spellStage = 0;
					}
				}
				else if (selectedIndex == 6)
				{
					if (element.equals("Air"))
					{
						
					}
					else
					{
						element = "Air";
						spellIndex = 0;
						spellStage = 0;
					}
				}
				else if (selectedIndex == 7)
				{
					if (element.equals("Earth"))
					{
						
					}
					else
					{
						element = "Earth";
						spellIndex = 0;
						spellStage = 0;
					}
				}
				else if (selectedIndex == 8)
				{
					if (element.equals("Water"))
					{
						
					}
					else
					{
						element = "Water";
						spellIndex = 0;
						spellStage = 0;
					}
				}
				else if (selectedIndex == 9)
				{
					if (element.equals("Death"))
					{
						
					}
					else
					{
						element = "Death";
						spellIndex = 0;
						spellStage = 0;
					}
				}
				else if (selectedIndex == 10)
				{
					if (element.equals("Life"))
					{
						
					}
					else
					{
						element = "Life";
						spellIndex = 0;
						spellStage = 0;
					}
				}
				else if (selectedIndex == 11)
				{
					menu.changeMenu("Game");
				}
				else if (selectedIndex == 12)
				{
					treeSelected = true;
				}

				MainFrame.enter = false;
			}
		}
		else
		{
			if (MainFrame.up)
			{
				spellStage--;

				if (spellStage < 0)
				{
					spellStage = 0;
				}

				int newIndex = spellIndex;

				if (newIndex > Character.getSpell(element).get(spellStage).spells.size()-1)
				{
					newIndex = Character.getSpell(element).get(spellStage).spells.size()-1;
				}

				SpellsStageEntry sse = Character.getSpell(element).get(spellStage).spells.get(newIndex);

				boolean found = false;

				if (sse.unlocked != 0)
				{
					found = true;
				}

				for (int i = 1; i < Character.getSpell(element).get(spellStage).spells.size(); i++)
				{
					if (found)
					{
						break;
					}

					int lower = newIndex - i;

					if (lower > -1)
					{
						sse = Character.getSpell(element).get(spellStage).spells.get(lower);

						if (sse.unlocked != 0)
						{
							newIndex = lower;
							found = true;
							break;
						}
					}

					int higher = newIndex + i;

					if (higher < Character.getSpell(element).get(spellStage).spells.size())
					{
						sse = Character.getSpell(element).get(spellStage).spells.get(higher);

						if (sse.unlocked != 0)
						{
							newIndex = higher;
							found = true;
							break;
						}
					}
				}

				if (found)
					spellIndex = newIndex;

				MainFrame.up = false;
			}
			else if (MainFrame.down)
			{
				if (spellStage == Character.getSpell(element).size()-1)
				{

				}
				else
				{
					int newStage = spellStage + 1;

					int newIndex = spellIndex;

					if (newIndex > Character.getSpell(element).get(newStage).spells.size()-1)
					{
						newIndex = Character.getSpell(element).get(newStage).spells.size()-1;
					}

					SpellsStageEntry sse = Character.getSpell(element).get(newStage).spells.get(newIndex);
					boolean found = false;

					if (sse.unlocked != 0)
					{
						found = true;
					}

					for (int i = 1; i < Character.getSpell(element).get(newStage).spells.size(); i++)
					{
						if (found)
						{
							break;
						}

						int lower = newIndex - i;

						if (lower > -1)
						{
							sse = Character.getSpell(element).get(newStage).spells.get(lower);

							if (sse.unlocked != 0)
							{
								newIndex = lower;
								found = true;
								break;
							}
						}

						int higher = newIndex + i;

						if (higher < Character.getSpell(element).get(newStage).spells.size())
						{
							sse = Character.getSpell(element).get(newStage).spells.get(higher);

							if (sse.unlocked != 0)
							{
								newIndex = higher;
								found = true;
								break;
							}
						}
					}

					if (found)
					{
						spellIndex = newIndex;
						spellStage = newStage;
					}
				}

				MainFrame.down = false;
			}
			else if (MainFrame.left)
			{
				if (spellIndex == 0)
				{

				}
				else
				{
					int newIndex = spellIndex - 1;

					SpellsStageEntry sse = Character.getSpell(element).get(spellStage).spells.get(newIndex);

					while (sse.unlocked == 0)
					{
						newIndex--;
						if (newIndex < 0)
						{
							newIndex = spellIndex;
							break;
						}

						sse =  Character.getSpell(element).get(spellStage).spells.get(newIndex);
					}

					spellIndex = newIndex;
				}

				MainFrame.left = false;
			}
			else if (MainFrame.right)
			{
				if (spellIndex == Character.getSpell(element).get(spellStage).spells.size()-1)
				{

				}
				else
				{
					int newIndex = spellIndex + 1;

					SpellsStageEntry sse = Character.getSpell(element).get(spellStage).spells.get(newIndex);

					while (sse.unlocked == 0)
					{
						newIndex++;
						if (newIndex == Character.getSpell(element).get(spellStage).spells.size())
						{
							newIndex = spellIndex;
							break;
						}

						sse =  Character.getSpell(element).get(spellStage).spells.get(newIndex);
					}

					spellIndex = newIndex;
				}

				MainFrame.right = false;
			}
			else if (MainFrame.enter)
			{
				treeSelected = false;

				MainFrame.enter = false;
			}
			if (Character.getSpell(element).get(spellStage).spells.get(spellIndex).unlocked > 1)
			{
				if (MainFrame.key1)
				{
					Character.socketedSpells[0] = Character.getSpell(element).get(spellStage).spells.get(spellIndex);
				}
				else if (MainFrame.key2)
				{
					Character.socketedSpells[1] = Character.getSpell(element).get(spellStage).spells.get(spellIndex);
				}
				else if (MainFrame.key3)
				{
					Character.socketedSpells[2] = Character.getSpell(element).get(spellStage).spells.get(spellIndex);
				}
				else if (MainFrame.key4)
				{
					Character.socketedSpells[3] = Character.getSpell(element).get(spellStage).spells.get(spellIndex);
				}
				else if (MainFrame.key5)
				{
					Character.socketedSpells[4] = Character.getSpell(element).get(spellStage).spells.get(spellIndex);
				}
			}



			if (spellStage < 0)
			{
				spellStage = 0;
			}
			else if (spellStage == Character.getSpell(element).size())
			{
				spellStage = Character.getSpell(element).size()-1;
			}

			if (spellIndex < 0)
			{
				spellIndex = 0;
			}
			else if (spellIndex == Character.getSpell(element).get(spellStage).spells.size())
			{
				spellIndex = Character.getSpell(element).get(spellStage).spells.size()-1;
			}

		}

		if (selectedIndex < 0)
		{
			selectedIndex = 0;
		}
		else if (selectedIndex > 12)
		{
			selectedIndex = 12;
		}

	}

}


class SaveMenu extends MenuScreen
{

	int selectedIndex = 0;
	File[] files;
	SaveGame[] saves;
	BufferedImage[] images = new BufferedImage[1];
	
	public SaveMenu(Menu menu) {
		super(menu);

		File directory = new File("Data/Saves");
		files = directory.listFiles();
		Arrays.sort(files, new Comparator<File>(){
		    public int compare(File f1, File f2)
		    {
		        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
		    } });
		
		saves = new SaveGame[files.length];
		
		for (int i = 0; i < files.length; i++)
		{
			saves[i] = loadFile(files[i]);
		}
		
		images[0] = GameData.getImage("Data/Resources/GUI/spellbookFilesText.png");
	}
	
	@Override
	protected void drawBackground(Graphics2D g2d)
	{
		super.drawBackground(g2d);
		
		g2d.drawImage(images[0], 0, 0, null);
	}

	/* (non-Javadoc)
	 * @see MenuScreen#evaluateButtons()
	 */
	@Override
	void evaluateButtons() {
		if (MainFrame.up)
		{
			selectedIndex--;
			
			MainFrame.up = false;
		}
		else if (MainFrame.down)
		{
			selectedIndex++;
			
			MainFrame.down = false;
		}
		else if (MainFrame.left)
		{
			selectedIndex--;
			
			MainFrame.left = false;
		}
		else if (MainFrame.right)
		{
			selectedIndex++;
			
			MainFrame.right = false;
		}
		else if (MainFrame.esc)
		{
			menu.changeMenu("Game");
			MainFrame.esc = false;
		}
		else if (MainFrame.enter)
		{
			if (selectedIndex < files.length)
			{
				Main.gamedata.saveGame(files[selectedIndex]);
				menu.changeMenu("Game");
			}
			else if (selectedIndex == files.length)
			{
				File file = new File("Data/Saves/"+System.currentTimeMillis()+".sav");
				Main.gamedata.saveGame(file);
				menu.changeMenu("Game");
			}
			else if (selectedIndex == files.length+1)
			{
				menu.changeMenu("Game");
			}
			
			MainFrame.enter = false;
		}
		
		if (selectedIndex < 0)
		{
			selectedIndex = 0;
		}
		if (selectedIndex > files.length+1)
		{
			selectedIndex = files.length+1;
		}
	}
	
	public SaveGame loadFile(File file)
	{
		SaveGame save = null;
		
		try{
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream oin = new ObjectInputStream(fin);
			save = (SaveGame) oin.readObject();
			oin.close();
			fin.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return save;
	}

	/* (non-Javadoc)
	 * @see MenuScreen#drawLeft(java.awt.Graphics2D)
	 */
	@Override
	protected void drawLeft(Graphics2D g2d) {
		if (selectedIndex < files.length)
		{
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
			Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(files[selectedIndex].lastModified());
	        
	        String timePlayed = String.format("%d min, %d sec", 
	        	    TimeUnit.MILLISECONDS.toMinutes(saves[selectedIndex].timePlayed),
	        	    TimeUnit.MILLISECONDS.toSeconds(saves[selectedIndex].timePlayed) - 
	        	    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(saves[selectedIndex].timePlayed))
	        	);
			
			g2d.setColor(Color.BLACK);
			g2d.drawString("Filename:", 70, 190);
			g2d.drawString(files[selectedIndex].getName(), 200, 190);
			g2d.drawString("Last Saved:", 70, 210);
			g2d.drawString(formatter.format(calendar.getTime()), 200, 210);
			g2d.drawString("Current Level:", 70, 240);
			g2d.drawString(saves[selectedIndex].currentLevel, 200, 240);
			g2d.drawString("Time Played:", 70, 270);
			g2d.drawString(timePlayed, 200, 270);
		}
		
	}

	/* (non-Javadoc)
	 * @see MenuScreen#drawRight(java.awt.Graphics2D)
	 */
	@Override
	protected void drawRight(Graphics2D g2d) {
		
		int filePos = selectedIndex;
		
		if (filePos >= files.length)
		{
			filePos = files.length-1;
		}

		g2d.drawImage(getImage(), 500, 90, 695, 485, 0, -100+(filePos*20), 195, 245+(filePos*20), null);
		
		if (selectedIndex == files.length)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		
		g2d.drawString("New Save", 495, 545);
		
		if (selectedIndex == files.length+1)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		
		g2d.drawString("Back", 610, 545);
	}
	
	private BufferedImage getImage()
	{
		BufferedImage im = new BufferedImage(195, 30+(files.length*20), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = im.createGraphics();
		
		for (int i = 0; i < files.length; i++)
		{
			if (selectedIndex == i)
			{
				g2d.setColor(Color.BLUE);
			}
			else
			{
				g2d.setColor(Color.BLACK);
			}
			
			g2d.drawString(files[i].getName(), 15, 30+(i*20));
		}
		
		g2d.dispose();
		
		return im;
	}
	
}

class LoadMenu extends MenuScreen
{

	int selectedIndex = 0;
	File[] files;
	SaveGame[] saves;
	BufferedImage[] images = new BufferedImage[1];
	
	public LoadMenu(Menu menu) {
		super(menu);

		File directory = new File("Data/Saves");
		files = directory.listFiles();
		Arrays.sort(files, new Comparator<File>(){
		    public int compare(File f1, File f2)
		    {
		        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
		    } });
		
		saves = new SaveGame[files.length];
		
		for (int i = 0; i < files.length; i++)
		{
			saves[i] = loadFile(files[i]);
		}
		images[0] = GameData.getImage("Data/Resources/GUI/spellbookFilesText.png");
	}
	
	@Override
	protected void drawBackground(Graphics2D g2d)
	{
		super.drawBackground(g2d);
		
		g2d.drawImage(images[0], 0, 0, null);
	}
	/* (non-Javadoc)
	 * @see MenuScreen#evaluateButtons()
	 */
	@Override
	void evaluateButtons() {
		if (MainFrame.up)
		{
			selectedIndex--;
			
			MainFrame.up = false;
		}
		else if (MainFrame.down)
		{
			selectedIndex++;
			
			MainFrame.down = false;
		}
		else if (MainFrame.left)
		{
			selectedIndex--;
			
			MainFrame.left = false;
		}
		else if (MainFrame.right)
		{
			selectedIndex++;
			
			MainFrame.right = false;
		}
		else if (MainFrame.esc)
		{
			menu.changeMenu("Game");
			MainFrame.esc = false;
		}
		else if (MainFrame.enter)
		{
			if (selectedIndex < files.length)
			{
				Main.gamedata.loadGame(files[selectedIndex]);
				Main.setState(1);
			}
			else if (selectedIndex == files.length+1)
			{
				menu.changeMenu("Game");
			}
			
			MainFrame.enter = false;
		}
		
		if (selectedIndex < 0)
		{
			selectedIndex = 0;
		}
		if (selectedIndex > files.length+1)
		{
			selectedIndex = files.length+1;
		}
	}
	
	public SaveGame loadFile(File file)
	{
		SaveGame save = null;
		
		try{
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream oin = new ObjectInputStream(fin);
			save = (SaveGame) oin.readObject();
			oin.close();
			fin.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return save;
	}

	/* (non-Javadoc)
	 * @see MenuScreen#drawLeft(java.awt.Graphics2D)
	 */
	@Override
	protected void drawLeft(Graphics2D g2d) {
		if (selectedIndex < files.length)
		{
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
			Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(files[selectedIndex].lastModified());
	        
	        String timePlayed = String.format("%d min, %d sec", 
	        	    TimeUnit.MILLISECONDS.toMinutes(saves[selectedIndex].timePlayed),
	        	    TimeUnit.MILLISECONDS.toSeconds(saves[selectedIndex].timePlayed) - 
	        	    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(saves[selectedIndex].timePlayed))
	        	);
			
	        g2d.setColor(Color.BLACK);
			g2d.drawString("Filename:", 70, 190);
			g2d.drawString(files[selectedIndex].getName(), 200, 190);
			g2d.drawString("Last Saved:", 70, 210);
			g2d.drawString(formatter.format(calendar.getTime()), 200, 210);
			g2d.drawString("Current Level:", 70, 240);
			g2d.drawString(saves[selectedIndex].currentLevel, 200, 240);
			g2d.drawString("Time Played:", 70, 270);
			g2d.drawString(timePlayed, 200, 270);
		}
		
	}

	/* (non-Javadoc)
	 * @see MenuScreen#drawRight(java.awt.Graphics2D)
	 */
	@Override
	protected void drawRight(Graphics2D g2d) {

		int filePos = selectedIndex;
		
		if (filePos >= files.length)
		{
			filePos = files.length-1;
		}

		g2d.drawImage(getImage(), 500, 90, 695, 485, 0, -100+(filePos*20), 195, 245+(filePos*20), null);
		
		if (selectedIndex == files.length)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		
		g2d.drawString("New Save", 495, 545);
		
		if (selectedIndex == files.length+1)
		{
			g2d.setColor(Color.BLUE);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		
		g2d.drawString("Back", 610, 545);
	}
	
	private BufferedImage getImage()
	{
		BufferedImage im = new BufferedImage(270, 30+(files.length*20), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = im.createGraphics();
		
		for (int i = 0; i < files.length; i++)
		{
			if (selectedIndex == i)
			{
				g2d.setColor(Color.BLUE);
			}
			else
			{
				g2d.setColor(Color.BLACK);
			}
			
			g2d.drawString(files[i].getName(), 15, 30+(i*20));
		}
		
		g2d.dispose();
		
		return im;
	}
	
}


class CharacterMenu extends MenuScreen
{

	int selectedIndex = 0;
	int selectedType = 2;
	int selectedItem = 0;
	
	public CharacterMenu(Menu menu) {
		super(menu);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see MenuScreen#evaluateButtons()
	 */
	@Override
	void evaluateButtons() {
		if (MainFrame.esc)
		{
			menu.changeMenu("Game");
			MainFrame.esc = false;
		}
	}

	/* (non-Javadoc)
	 * @see MenuScreen#drawLeft(java.awt.Graphics2D)
	 */
	@Override
	protected void drawLeft(Graphics2D g2d) {
		
	}

	/* (non-Javadoc)
	 * @see MenuScreen#drawRight(java.awt.Graphics2D)
	 */
	@Override
	protected void drawRight(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		
		g2d.drawRect(460, 50, 270, 400);
		
		g2d.drawImage(getImage(), 460, 50, 730, 450, 0, -40+(selectedItem*20), 270, -40+(selectedItem*20)+440, null);
	}
	
	private BufferedImage getImage()
	{
		BufferedImage im = new BufferedImage(270, 40+(Character.inventory.get(selectedType).size()*20), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = im.createGraphics();
		
		int i = 0;
		for (Map.Entry<String, Item> entry : Character.inventory.get(selectedType).entrySet())
		{
			Item item = entry.getValue();
			
			if (i == selectedItem)
			{
				g2d.setColor(Color.BLUE);
			}
			else
			{
				g2d.setColor(Color.BLACK);
			}
			g2d.drawString(item.name, 10, 20+(i*20));
			g2d.drawString(Integer.toString(item.number), 100, 20+(i*20));
			
			
			i++;
		}
		
		return im;
	}
	
}