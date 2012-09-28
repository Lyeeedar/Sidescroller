import java.awt.Color;

/**
 * @author Lyeeedar
 *
 */
public class SystemMessage {

	String message;
	public int aliveTime = 10000;
	Color colour;
	
	public static final float step = (float) 0.1275;
	
	public SystemMessage(String message, Color colour)
	{
		this.message = message;
		this.colour = colour;
	}
	
	public Color getColour()
	{
		float alpha = 255;
		
		if (aliveTime < 2000)
		{
			alpha = (float)step*aliveTime;
			
			colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), (int)alpha);
		}

		return colour;
	}
}
