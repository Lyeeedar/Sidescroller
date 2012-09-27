/**
 * @author Lyeeedar
 *
 */
public class SystemMessage {

	String message;
	public int aliveTime = 10000;
	
	public static final float step = (float) 0.1275;
	
	public SystemMessage(String message)
	{
		this.message = message;
	}
	
	public float getAlpha()
	{
		float alpha = 255;
		
		if (aliveTime < 2000)
			alpha = (float)step*aliveTime;

		return alpha;
	}
}
