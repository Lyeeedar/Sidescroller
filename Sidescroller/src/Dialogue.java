import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class contains all the dialogue for an Entity. It also functions as the quest system (due to conditional branches)
 * @author Lyeeedar
 *
 */
public class Dialogue implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7786012719423291608L;
	
	/**
	 * This array list holds all the dialogue blocks for the entire dialogue object. <br>
	 * Block types: <p>
	 * Speech = Just show the blocks of text sequentially. After finishing all the text then increment the stage <p>
	 * Kill = Show prompt text until the entity specified is not alive, then show success text and increment stage <p>
	 * ChangeLevel = change to the given level.
	 */
	private ArrayList<ArrayList<String>> quest;
	
	/**
	 * The current dialogue block
	 */
	private int stage = 0;
	/**
	 * The current line inside the dialogue block
	 */
	private int internalstage = 0;
	
	public static final int dialogueFade = 10000;
	public static final int fadeDuration = 3000;
	public static final float fadeStep = (float) 255/fadeDuration;
	
	/**
	 * The dialogue type. Effects the way the dialogue is drawn to the screen. <br>
	 * Currently has types 'Speech' and 'Examine'
	 */
	public int type;
	
	public Dialogue(ArrayList<ArrayList<String>> text, int type)
	{
		this.type = type;
		this.quest = text;
	}

	/**
	 * Returns the text from the current block and current line. <br>
	 * Uses each block types specific method to get customized effects for different types.
	 * @return
	 */
	public String getText()
	{
		ArrayList<String> stagetext = null;
		
		// Make sure there is never a null reference passed back (unless the dialogue object is empty)
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
		else if (stagetext.get(0).equals("ChangeLevel"))
		{
			return changeLevel(stagetext);
		}
		
		return null;
	}
	
	/**
	 * Method that returns the current text for the speech block type
	 * @param stagetext
	 * @return
	 */
	private String speech(ArrayList<String> stagetext)
	{
		if (internalstage == 0)
			internalstage++;
		
		return stagetext.get(internalstage);
	}
	
	/**
	 * Method that increments internalstage and stage for the speech block type
	 */
	private void incrSpeech()
	{
		internalstage++;
		
		if (internalstage == quest.get(stage).size())
		{
			stage++;
			internalstage = 0;
		}
	}
	
	/**
	 * Method that returns the current text for the kill block type
	 * @param stagetext
	 * @return
	 */
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
	
	/**
	 * Method that increments internalstage and stage for the kill block type
	 */
	private void incrKill()
	{
		Entity e = Main.gamedata.getGameEntities().get(quest.get(stage).get(1));
		if ((e != null) && (!e.isAlive()))
		{
			stage++;
			internalstage = 0;
		}
	}
	
	private String changeLevel(ArrayList<String> stagetext)
	{
		Main.gamedata.saveGame();
		Main.gamedata.loadLevel(stagetext.get(1));
		return "Loading Level "+stagetext.get(1);
	}
	
	/**
	 * This method calls the customized increment mehtods for the current block type
	 */
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
	
	/**
	 * This method returns a deep copy of this dialogue object
	 * @return
	 */
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
		Dialogue d = new Dialogue(newquest, type);
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

	/**
	 * Returns {@link Dialogue#type}
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets {@link Dialogue#type}
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

}
