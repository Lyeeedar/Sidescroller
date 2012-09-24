import java.io.File;


class Spell extends Entity
{

	/**
	 * @param name
	 * @param animateTime
	 * @param totalAnimateStrip
	 * @param pos
	 * @param spritefile
	 * @param collision
	 * @param behaviour
	 * @param dialogue
	 */
	public Spell(String name, long animateTime, int totalAnimateStrip,
			int[] pos, File spritefile, int[] collision, boolean[] behaviour,
			Dialogue dialogue) {
		super(name, animateTime, totalAnimateStrip, pos, spritefile, collision,
				behaviour, dialogue);
		// TODO Auto-generated constructor stub
	}
	
}