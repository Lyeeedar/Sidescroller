import java.io.File;
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
	private ArrayList<ArrayList<String>> quest1;
	private ArrayList<ArrayList<String>> quest2;
	
	/**
	 * The current dialogue block
	 */
	private int stage1 = 0;
	private int stage2 = 0;
	/**
	 * The current line inside the dialogue block
	 */
	private int internalstage1 = 0;
	private int internalstage2 = 0;
	
	public int lastGender = Character.gender;
	
	public static final int dialogueFade = 10000;
	public static final int fadeDuration = 3000;
	public static final float fadeStep = (float) 255/fadeDuration;
	
	/**
	 * The dialogue type. Effects the way the dialogue is drawn to the screen. <br>
	 * Currently has types 'Speech', 'Examine', 'Change Level' and 'Change Position'
	 */
	public int type;
	
	public Dialogue(ArrayList<ArrayList<String>> text1, ArrayList<ArrayList<String>> text2, int type)
	{
		this.type = type;
		this.quest1 = text1;
		this.quest2 = text2;
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
		if (getStage() == getQuest().size())
			stagetext = getQuest().get(getStage()-1);
		else
			stagetext = getQuest().get(getStage());
		
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
		else if (stagetext.get(0).equals("ChangePosition"))
		{
			return changePosition(stagetext);
		}
		else if (stagetext.get(0).equals("ChangeStage"))
		{
			return changeStage(stagetext);
		}
		else if (stagetext.get(0).equals("ChangePassable"))
		{
			return changePassable(stagetext);
		}
		else if (stagetext.get(0).equals("ChangeVisible"))
		{
			return changeVisible(stagetext);
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
		if (getInternalstage() == 0)
			setInternalstage(getInternalstage()+1);
		
		return stagetext.get(getInternalstage());
	}
	
	/**
	 * Method that increments internalstage and stage for the speech block type
	 */
	private void incrSpeech()
	{
		setInternalstage(getInternalstage()+1);
		
		if (getInternalstage() == getQuest().get(getStage()).size())
		{
			setStage(getStage()+1);
			setInternalstage(0);
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
		Entity e = Main.gamedata.getGameEntities().get(getQuest().get(getStage()).get(1));
		if ((e != null) && (!e.isAlive()))
		{
			setStage(getStage()+1);
			setInternalstage(0);
		}
	}
	
	private String changeLevel(ArrayList<String> stagetext)
	{
		Main.gamedata.saveGame(new File("Data/Saves/autosave.sav"));
		Main.gamedata.loadLevel(stagetext.get(1));
		
		int posX = Integer.parseInt(stagetext.get(2));
		int posY = Integer.parseInt(stagetext.get(3));
		int dir = Integer.parseInt(stagetext.get(4));
		
		Main.gamedata.getGameEntities().get("Player").setPos(new int[]{posX, posY, dir});
		
		setStage(getStage()+1);
		setInternalstage(0);
		
		return "Loading Level "+stagetext.get(1);
	}
	
	private String changePosition(ArrayList<String> stagetext)
	{	
		int posX = Integer.parseInt(stagetext.get(1));
		int posY = Integer.parseInt(stagetext.get(2));
		int dir = Integer.parseInt(stagetext.get(3));
		
		Main.gamedata.getGameEntities().get("Player").setPos(new int[]{posX, posY, dir});
		
		setStage(getStage()+1);
		setInternalstage(0);
		
		return "";
	}
	
	private String changeStage(ArrayList<String> stagetext)
	{
		setStage(Integer.parseInt(stagetext.get(1)));
		setInternalstage(0);
		
		return "";
	}
	
	private String changePassable(ArrayList<String> stagetext)
	{
		boolean passable = stagetext.get(2).equals("true");
		
		Main.gamedata.getGameEntities().get(stagetext.get(1)).setPassable(passable);
		
		setStage(getStage()+1);
		setInternalstage(0);
		
		return "";
	}
	
	private String changeVisible(ArrayList<String> stagetext)
	{
		boolean visible = stagetext.get(2).equals("true");
		
		Main.gamedata.getGameEntities().get(stagetext.get(1)).setVisible(visible);
		
		setStage(getStage()+1);
		setInternalstage(0);
		
		return "";
	}
	
	/**
	 * This method calls the customized increment mehtods for the current block type
	 */
	public void incrStage()
	{
		ArrayList<String> stagetext = null;
		if (getStage() == getQuest().size())
			stagetext = getQuest().get(getStage()-1);
		else
			stagetext = getQuest().get(getStage());
		
		if (stagetext.get(0).equals("Speech"))
		{
			incrSpeech();
		}
		else if (stagetext.get(0).equals("Kill"))
		{
			incrKill();
		}
		
		if (getStage() > this.getQuest().size()-1)
			setStage(getQuest().size()-1);
	}
	
	/**
	 * This method returns a deep copy of this dialogue object
	 * @return
	 */
	public Dialogue copy()
	{
		
		ArrayList<ArrayList<String>> newquest1 = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> newquest2 = new ArrayList<ArrayList<String>>();
		
		for (ArrayList<String> block : quest1)
		{
			ArrayList<String> newblock = new ArrayList<String>();
			for (String line : block)
			{
				newblock.add(line);
			}
			newquest1.add(newblock);
		}
		
		for (ArrayList<String> block : quest2)
		{
			ArrayList<String> newblock = new ArrayList<String>();
			for (String line : block)
			{
				newblock.add(line);
			}
			newquest2.add(newblock);
		}
		
		Dialogue d = new Dialogue(newquest1, newquest2, type);
		return d;
	}
	
	public int getStage() {
		if (lastGender == 0)
			return stage1;
		else if (lastGender == 1)
			return stage2;
		return 0;
	}
	
	public void setStage(int stage) {
		if (lastGender == 0)
			stage1 = stage;
		else if (lastGender == 1)
			stage2 = stage;
	}

	/**
	 * Returns {@link Dialogue#quest}
	 * @return the quest
	 */
	public ArrayList<ArrayList<String>> getQuest() {
		
		if (lastGender == 0)
			return quest1;
		else if (lastGender == 1)
			return quest2;
		return null;
	}

	/**
	 * Sets {@link Dialogue#quest}
	 * @param quest the quest to set
	 */
	public void setQuest(ArrayList<ArrayList<String>> quest) {
		if (lastGender == 0)
			quest1 = quest;
		else if (lastGender == 1)
			quest2 = quest;
	}

	/**
	 * Returns {@link Dialogue#internalstage}
	 * @return the internalstage
	 */
	public int getInternalstage() {
		if (lastGender == 0)
			return internalstage1;
		else if (lastGender == 1)
			return internalstage2;
		return 0;
	}

	/**
	 * Sets {@link Dialogue#internalstage}
	 * @param internalstage the internalstage to set
	 */
	public void setInternalstage(int internalstage) {
		if (lastGender == 0)
			internalstage1 = internalstage;
		else if (lastGender == 1)
			internalstage2 = internalstage;
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
