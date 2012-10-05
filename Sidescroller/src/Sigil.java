/**
 * 
 */

/**
 * @author Lyeeedar
 *
 */
public class Sigil extends Item {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6973097036136114663L;
	String element;

	/**
	 * @param name
	 * @param description
	 * @param spriteFile
	 * @param pos
	 * @param number
	 * @param type
	 */
	public Sigil(String name, String description, String element, String spriteFile, int[] pos,
			int number) {
		super(name, description, spriteFile, pos, number, 0);

		this.element = element;
		
	}

}
