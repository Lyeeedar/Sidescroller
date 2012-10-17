
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
			i = new Item(item, "A chest full of stuff. May be useful to someone.", "chest.png", pos, 1, 2);			
		}
		else if (item.equals("Basic Void Sigil"))
		{
			i = new Sigil(item, "A sigil that will increase all your stats slightly.", Entity.LIFE,
					"testsigil.png", pos, new int[]{20, 0, 1, 1, 1, 1, 1, 1, 1});
		}
		else if (item.equals("Scorch Scroll"))
		{
			i = new Scroll(item, "This scroll unlocks the fire spell 'Scorch'", "scroll.png", pos, 0, 1, Entity.FIRE);
		}
		else if (item.equals("Tutorial Key"))
		{
			i = new Item(item, "A key used in the tutorial to unlock something.", "TutorialKey.png", pos, 1, 2);			
		}
		else
			System.err.println("Item not found: "+item);
		
		return i;
	}
}
