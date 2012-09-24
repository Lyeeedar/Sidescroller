import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;


/**
 * @author Lyeeedar
 *
 */
public class Dialogue implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7786012719423291608L;
	private ArrayList<ArrayList<String>> quest;
	private int stage = 0;
	private int internalstage = 0;
	
	public static final int dialogueFade = 10000;
	public static final int fadeDuration = 3000;
	public static final float fadeStep = (float) 255/fadeDuration;
	
	public Dialogue(ArrayList<ArrayList<String>> text)
	{
		this.quest = text;
	}

	public String getText()
	{
		ArrayList<String> stagetext = null;
		if (stage == quest.size())
			stagetext = quest.get(stage-1);
		else
			stagetext = quest.get(stage);
		
		if (stagetext.get(0).equals("Speech"))
		{
			return speech(stagetext);
		}
		else if (stagetext.get(0).equals("Kill"))
		{
			return kill(stagetext);
		}
		
		return null;
	}
	
	private String speech(ArrayList<String> stagetext)
	{
		if (internalstage == 0)
			internalstage++;
		
		return stagetext.get(internalstage);
	}
	
	private void incrSpeech()
	{
		internalstage++;
		
		if (internalstage == quest.get(stage).size())
		{
			stage++;
			internalstage = 0;
		}
	}
	
	private String kill (ArrayList<String> stagetext)
	{
		Entity e = Main.gamedata.getGameEntities().get(stagetext.get(1));
		if ((e != null) && (e.isAlive()))
		{
			return stagetext.get(2);
		}
		else
		{
			return stagetext.get(3);
		}
	}
	
	private void incrKill()
	{
		Entity e = Main.gamedata.getGameEntities().get(quest.get(stage).get(1));
		if ((e != null) && (!e.isAlive()))
		{
			stage++;
			internalstage = 0;
		}
	}
	
	public void incrStage()
	{
		ArrayList<String> stagetext = null;
		if (stage == quest.size())
			stagetext = quest.get(stage-1);
		else
			stagetext = quest.get(stage);
		
		if (stagetext.get(0).equals("Speech"))
		{
			incrSpeech();
		}
		else if (stagetext.get(0).equals("Kill"))
		{
			incrKill();
		}
	}
	
	
	public Dialogue copy()
	{
		
		ArrayList<ArrayList<String>> newquest = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> block : quest)
		{
			ArrayList<String> newblock = new ArrayList<String>();
			for (String line : block)
			{
				newblock.add(line);
			}
			newquest.add(newblock);
		}
		Dialogue d = new Dialogue(newquest);
		return d;
	}
	
	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	/**
	 * Returns {@link Dialogue#quest}
	 * @return the quest
	 */
	public ArrayList<ArrayList<String>> getQuest() {
		return quest;
	}

	/**
	 * Sets {@link Dialogue#quest}
	 * @param quest the quest to set
	 */
	public void setQuest(ArrayList<ArrayList<String>> quest) {
		this.quest = quest;
	}

	/**
	 * Returns {@link Dialogue#internalstage}
	 * @return the internalstage
	 */
	public int getInternalstage() {
		return internalstage;
	}

	/**
	 * Sets {@link Dialogue#internalstage}
	 * @param internalstage the internalstage to set
	 */
	public void setInternalstage(int internalstage) {
		this.internalstage = internalstage;
	}

}
