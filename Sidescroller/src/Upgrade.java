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

	boolean used = false;
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
	
	public void use()
	{
		if (used)
			return;
		
		upgrade();
		
		used = true;
	}
	
	abstract void upgrade();

}
