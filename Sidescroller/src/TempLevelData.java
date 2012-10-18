import java.awt.image.BufferedImage;

/**
 * Class used to store the data for a level's images and collision to speed up loading recently visited levels.
 * @author Lyeeedar
 *
 */
public class TempLevelData {
	BufferedImage background[];
	String name;
	int[][] collisionMap;
	
	public TempLevelData(BufferedImage background[], String name, int[][] collisionMap)
	{
		this.background = background;
		this.name = name;
		this.collisionMap = collisionMap;
	}

}
