
/**
 * @author Lyeeedar
 *
 */
public class ItemList {

	public static Item getItem(String item, int[] pos, int number)
	{
		Item i = null;
		
		if (item.equals("Chest"))
		{
			i = new Item(item, "A chest full of stuff. May be useful to someone.", "Data/Resources/Items/chest.png", pos, 1, 2);			
		}
		else if (item.equals("Speed Sigil - Fire"))
		{
			i = new Sigil(item, "A sigil that will increase your speed, at the expense of some defense", Entity.FIRE,
					"testsigil.png", pos, new int[]{0, 1, -5, -5, -5, -5, -5, -5, -5});
		}
		else if (item.equals("Speed Sigil - Life"))
		{
			i = new Sigil(item, "A sigil that will increase your speed, at the expense of some defense", Entity.LIFE,
					"testsigil.png", pos, new int[]{0, 1, -5, -5, -5, -5, -5, -5, -5});
		}
		
		return i;
	}
}
