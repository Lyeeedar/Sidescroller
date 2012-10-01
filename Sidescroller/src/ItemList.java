
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
			i = new Item(item, "Data/Resources/Items/chest.png", pos, 1, 2);			
		}
		
		return i;
	}
}
