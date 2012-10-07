/**
 * 
 */

/**
 * @author Lyeeedar
 *
 */
abstract class Upgrade extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8977348280794476295L;

	/**
	 * @param name
	 * @param description
	 * @param spriteFile
	 * @param pos
	 * @param number
	 * @param type
	 */
	public Upgrade(String name, String description, String spriteFile,
			int[] pos) {
		super(name, description, spriteFile, pos, 1, 1);
		// TODO Auto-generated constructor stub
	}
	
	abstract void upgrade();

}
