import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Map Editor. Allows loading and saving of levels, and the modifying of all the entity properties within the levels. Minimal Commenting because most of it is just GUI stuff, positioning and etc.
 * @author Lyeeedar
 *
 */
public class MapEditor {

	public static GameData gamedata = Main.gamedata;
	public static String[] background = new String[5];
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new EditorFrame();
	}

}


class EditorFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static MapPanel mapPanel = new MapPanel();
	OptionsPanel optionsPanel = new OptionsPanel();
	public static JScrollPane sp;

	public EditorFrame()
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		sp = new JScrollPane(mapPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		this.add(sp, BorderLayout.CENTER);
		this.add(optionsPanel, BorderLayout.NORTH);


		this.setTitle("Map Editor");
		this.setSize(1500, 900);

		this.setVisible(true);
	}
}


class MapPanel extends JPanel implements MouseListener, MouseMotionListener
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Entity controlled = null;

	public MapPanel()
	{
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		this.setPreferredSize(new Dimension(GameData.levelSize[0], GameData.levelSize[1]));
		this.setSize(GameData.levelSize[0], GameData.levelSize[1]);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (MapEditor.gamedata.getBackground()[0] != null)
			g.drawImage(MapEditor.gamedata.getBackground()[0], 0, 0, null);
		if (MapEditor.gamedata.getBackground()[1] != null)
			g.drawImage(MapEditor.gamedata.getBackground()[1], 0, 0, null);
		if (MapEditor.gamedata.getBackground()[2] != null)
			g.drawImage(MapEditor.gamedata.getBackground()[2], 0, 0, null);
		if (MapEditor.gamedata.getBackground()[3] != null)
			g.drawImage(MapEditor.gamedata.getBackground()[3], 0, 0, null);

		for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
		{
			Entity e = entry.getValue();
			g.setColor(new Color(255, 255, 255, 120));
			
			g.fillRect(e.getPos()[0]-55, e.getPos()[1]-50, 55, 70);
			
			g.setColor(Color.BLACK);
			
			g.drawRect(e.getPos()[0]-55, e.getPos()[1]-50, 55, 70);
			
			g.drawString(e.getName(), e.getPos()[0]-50, e.getPos()[1]-30);
			g.drawString(e.faction, e.getPos()[0]-50, e.getPos()[1]-15);
			g.drawString(""+e.pos[0], e.getPos()[0]-50, e.getPos()[1]);
			g.drawString(""+e.pos[1], e.getPos()[0]-50, e.getPos()[1]+15);

			if (e.isPassable())
			{
				g.setColor(Color.BLUE);
			}
			else if (e.checkCollision(e.getPos()) != null)
			{
				g.setColor(Color.RED);
			}
			else
			{
				g.setColor(Color.GREEN);
			}

			if (!e.isVisible())
			{

			}
			else if (e.getPos()[2] == 0)
			{
				g.drawImage(e.getSpriteSheet(),
						e.getPos()[0]+e.getSize()[0], e.getPos()[1], e.getPos()[0],	e.getPos()[1]+e.getSize()[1],
						e.getSize()[0]*(e.getAnimateStage()-1), e.getSize()[1]*(e.getAnimateStrip()-1), e.getSize()[0]*(e.getAnimateStage()), e.getSize()[1]*e.getAnimateStrip(), null);
			}
			else
			{
				g.drawImage(e.getSpriteSheet(),
						e.getPos()[0], e.getPos()[1], e.getPos()[0]+e.getSize()[0],	e.getPos()[1]+e.getSize()[1],
						e.getSize()[0]*(e.getAnimateStage()-1), e.getSize()[1]*(e.getAnimateStrip()-1), e.getSize()[0]*(e.getAnimateStage()), e.getSize()[1]*e.getAnimateStrip(), null);
			}

			g.drawRect(e.getCollisionShape()[0]+e.getPos()[0], e.getCollisionShape()[1]+e.getPos()[1],
					e.getCollisionShape()[2], e.getCollisionShape()[3]);

		}

		if (MapEditor.gamedata.getBackground()[4] != null)
			g.drawImage(MapEditor.gamedata.getBackground()[4], 0, 0, null);

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (controlled == null)
		{
			return;
		}

		controlled.changePosition(e.getX(), e.getY(), controlled.getPos()[2]);
		this.repaint();

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (controlled != null)
		{
			controlled = null;
			return;
		}

		for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
		{
			Entity ent = entry.getValue();
			if ((e.getX() > ent.getCollisionShape()[0]+ent.getPos()[0]) && (e.getX() < ent.getCollisionShape()[0]+ent.getPos()[0]+ent.getCollisionShape()[2])
					&& (e.getY() > ent.getCollisionShape()[1]+ent.getPos()[1]) && (e.getY() < ent.getCollisionShape()[1]+ent.getPos()[1]+ent.getCollisionShape()[3]))
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					controlled = ent;
					return;
				}
				else
				{
					new EntityFrame(ent);
					return;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}

class OptionsPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPanel panel = this;
	
	public OptionsPanel()
	{
		this.add(new JLabel("Level Name"));
		final JTextField name = new JTextField(10);
		this.add(name);
		
		this.add(new JLabel("BGM Name"));
		final JTextField bgmname = new JTextField(10);
		this.add(bgmname);
		
		final JCheckBox transform = new JCheckBox("Transform Allowed");
		transform.setSelected(Main.gamedata.transformAllowed);
		this.add(transform);

		JButton background = new JButton("Choose Background");
		background.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				new BackgroundFrame();

			}

		});
		this.add(background);

		JButton entity = new JButton("Create Entity");
		entity.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				Entity e = new Entity("Unnamed", 100, 0, 8, new int[]{EditorFrame.sp.getHorizontalScrollBar().getValue(), EditorFrame.sp.getVerticalScrollBar().getValue(), 0}, 0, null, new int[]{0, 0, 50, 50}, new boolean[]{false, false, false}, null);

				MapEditor.gamedata.getGameEntities().put("Unnamed"+MapEditor.gamedata.getGameEntities().size(), e);

				new EntityFrame(e);

				EditorFrame.mapPanel.repaint();

			}});
		this.add(entity);
		
		JButton clearEntity = new JButton("Clear Entities");
		clearEntity.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				MapEditor.gamedata.getGameEntities().clear();

				EditorFrame.mapPanel.repaint();

			}});
		this.add(clearEntity);

		JButton gravity = new JButton("Gravity");
		gravity.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				for (int i = 0; i < 50; i++)
				{
					for (Map.Entry<String, Entity> entry : Main.gamedata.getGameEntities().entrySet())
					{
						Entity e = entry.getValue();
						if (e.getBehavior()[1])
							e.behavior1();
					}
				}

				EditorFrame.mapPanel.repaint();

			}});
		this.add(gravity);

		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				File file = new File("Data/Resources/Levels/"+name.getText());
				file.mkdirs();
				
				Level level = new Level();
				level.create(name.getText(), MapEditor.gamedata.getGameEntities(), bgmname.getText(), transform.isSelected());

				Level.save(level);
				
				for (int i = 0; i < 5; i++)
				{
					file = new File("Data/Resources/Levels/"+name.getText()+"/back"+i+".png");
					try{
						ImageIO.write(MapEditor.gamedata.background[i], "PNG", file);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException eio) {
						eio.printStackTrace();
					}
				}
				
				JOptionPane.showMessageDialog(null, "Save Done");

			}});
		this.add(save);

		JButton load = new JButton("Load");
		load.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				File file = null;

				final JFileChooser fc = new JFileChooser(new File("").getAbsolutePath());

				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();

					Level level = Level.load(file);

					name.setText(level.name);
					bgmname.setText(level.BGM);
					Main.gamedata.transformAllowed = level.transformAllowed;
					
					MapEditor.gamedata.loadLevelImages(level.name);
					MapEditor.gamedata.setGameEntities(level.gameEntities);
					MapEditor.gamedata.createCollisionMap();
					transform.setSelected(Main.gamedata.transformAllowed);
					panel.revalidate();
					panel.repaint();
					
					for (Map.Entry<String, Entity> entry : level.gameEntities.entrySet())
					{
						Entity ent = entry.getValue();
						ent.processSpritesheet();
					}

					EditorFrame.mapPanel.repaint();
					EditorFrame.mapPanel.setPreferredSize(new Dimension(MapEditor.gamedata.collisionX, MapEditor.gamedata.collisionY));
					EditorFrame.mapPanel.revalidate();
				}


				JOptionPane.showMessageDialog(null, "Load Done");

			}});
		this.add(load);
	}
}

class BackgroundFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panel = new JPanel();

	public BackgroundFrame()
	{
		this.add(panel);

		this.setSize(600, 200);
		this.setVisible(true);

		init();
	}

	public void init()
	{
		panel.removeAll();

		panel.setLayout(new GridLayout(5, 2));


		JButton distBtn = new JButton("Distant");
		distBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				filechoose(0);

			}});

		panel.add(distBtn);

		JTextField distantTxt = new JTextField(15);
		panel.add(distantTxt);
		distantTxt.setText(MapEditor.background[0]);

		JButton fbackBtn = new JButton("Far Background");
		fbackBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				filechoose(1);

			}});
		panel.add(fbackBtn);
		JTextField farTxt = new JTextField(15);
		panel.add(farTxt);
		farTxt.setText(MapEditor.background[1]);

		
		
		JButton cbackBtn = new JButton("Close Background");
		cbackBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				filechoose(2);

			}});


		panel.add(cbackBtn);

		JTextField backTxt = new JTextField(15);
		panel.add(backTxt);
		backTxt.setText(MapEditor.background[2]);

		JButton collBtn = new JButton("Collision");
		collBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				filechoose(3);

			}});


		panel.add(collBtn);

		JTextField collTxt = new JTextField(15);
		panel.add(collTxt);
		collTxt.setText(MapEditor.background[3]);

		JButton foreBtn = new JButton("Foreground");
		foreBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				filechoose(4);

			}});


		panel.add(foreBtn);

		JTextField foreTxt = new JTextField(15);
		panel.add(foreTxt);
		foreTxt.setText(MapEditor.background[4]);


		panel.revalidate();
		panel.repaint();
	}

	public void filechoose(int index)
	{
		final JFileChooser fc = new JFileChooser(new File("").getAbsolutePath());

		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			BufferedImage im = null;

			try{
				im = ImageIO.read(file);
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			
			if (im == null)
			{
				System.err.println("Invalid file: " + file.getPath());
				return;
			}

			MapEditor.background[index] = file.getPath();
			MapEditor.gamedata.getBackground()[index] = im;

			if (index == 3)
			{
				GameData.levelSize[0] = MapEditor.gamedata.background[0].getWidth();
				GameData.levelSize[1] = MapEditor.gamedata.background[0].getHeight();

				MapEditor.gamedata.fillCollisionMap();
				
				EditorFrame.mapPanel.setPreferredSize(new Dimension(MapEditor.gamedata.background[0].getWidth(), MapEditor.gamedata.background[0].getHeight()));
				EditorFrame.mapPanel.revalidate();
			}

			EditorFrame.mapPanel.repaint();

			init();

		}
	}
}

class EntityFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panel = new JPanel();
	Entity e;
	String spritefile;
	JFrame frame = this;
	boolean[] behavior;
	Dialogue d;
	HashMap<String, Integer> dropList;

	public EntityFrame(Entity e)
	{
		this.e = e;
		
		dropList = e.dropList;
		d = e.getDialogue().copy();
		spritefile = e.getSpriteFile();

		behavior = new boolean[4];
		for (int i = 0; i < e.getBehavior().length; i++)
		{
			behavior[i] = e.getBehavior()[i];
		}

		JScrollPane sp = new JScrollPane(panel);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(panel);
		init();
		this.setSize(1000, 600);
		panel.setPreferredSize(new Dimension(1000, 400));
		this.setVisible(true);
	}

	public void init()
	{
		panel.removeAll();
		panel.setLayout(new GridLayout(27, 2));

		panel.add(new JLabel("Name: "));
		final JTextField name = new JTextField(10);
		name.setText(e.getName());
		panel.add(name);
		
		panel.add(new JLabel("Faction: "));
		final JTextField faction = new JTextField(10);
		faction.setText(e.getFaction());
		panel.add(faction);

		panel.add(new JLabel("Animation Update Time: "));
		final JTextField animUpdate = new JTextField(5);
		animUpdate.setText(Long.toString(e.getAnimateTime()));
		panel.add(animUpdate);
		
		panel.add(new JLabel("Animation Stages: "));
		final JTextField animStages = new JTextField(5);
		animStages.setText(Integer.toString(e.getAnimStages()));
		panel.add(animStages);

		panel.add(new JLabel("Total Animation Strips: "));
		final JTextField animStrips = new JTextField(5);
		animStrips.setText(Integer.toString(e.getTotalAnimateStrip()));
		panel.add(animStrips);

		panel.add(new JLabel("Current Animation Strips: "));
		final JTextField cuanimStrips = new JTextField(5);
		cuanimStrips.setText(Integer.toString(e.getAnimateStrip()));
		panel.add(cuanimStrips);
		
		panel.add(new JLabel("Current Animation Stage: "));
		final JTextField cuanimStage = new JTextField(5);
		cuanimStage.setText(Integer.toString(e.getAnimateStage()));
		panel.add(cuanimStage);

		JButton spritesheet = new JButton("Spritesheet");
		spritesheet.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				filechoose();

			}});
		panel.add(spritesheet);
		final JTextField spriteFile = new JTextField(spritefile);
		panel.add(spriteFile);

		panel.add(new JLabel("Collision Values: "));

		final JTextField x = new JTextField(3);
		x.setText(Integer.toString(e.getCollisionShape()[0]));
		final JTextField y = new JTextField(3);
		y.setText(Integer.toString(e.getCollisionShape()[1]));
		final JTextField width = new JTextField(3);
		width.setText(Integer.toString(e.getCollisionShape()[2]));
		final JTextField height = new JTextField(3);
		height.setText(Integer.toString(e.getCollisionShape()[3]));

		JPanel collision = new JPanel();

		collision.add(x);
		collision.add(y);
		collision.add(width);
		collision.add(height);

		panel.add(collision);

		panel.add(new JLabel("Speed: "));
		final JTextField speed = new JTextField(5);
		speed.setText(Integer.toString(e.speed));
		panel.add(speed);
		
		panel.add(new JLabel("Passable: "));
		final JCheckBox passable = new JCheckBox("");
		passable.setSelected(e.isPassable());
		panel.add(passable);

		panel.add(new JLabel("Visible: "));
		final JCheckBox visible = new JCheckBox("");
		visible.setSelected(e.isVisible());
		panel.add(visible);

		panel.add(new JLabel("Weight: "));
		final JTextField weight = new JTextField(5);
		weight.setText(Long.toString(e.getWeight()));
		panel.add(weight);

		ButtonGroup dir = new ButtonGroup();
		final JRadioButton left = new JRadioButton("Left");
		panel.add(left);
		dir.add(left);

		final JRadioButton right = new JRadioButton("Right");
		panel.add(right);
		dir.add(right);

		if (e.getPos()[2] == 0)
		{
			left.setSelected(true);
		}
		else
		{
			right.setSelected(true);
		}

		final JButton behaviorBtn = new JButton("Behavior");
		behaviorBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				new BehaviorFrame(behavior);

			}});
		panel.add(behaviorBtn);
		
		final JButton dialogue = new JButton("Dialogue");
		dialogue.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				new DialogueFrame(d);
				
			}});
		panel.add(dialogue);
		
		panel.add(new JLabel("Health"));
		final JTextField health = new JTextField(5);
		health.setText(Double.toString(e.getHealth()));
		panel.add(health);
		
		panel.add(new JLabel("Armour - Phys"));
		final JTextField armorPhys = new JTextField(5);
		armorPhys.setText(Double.toString(e.getDefense().get(Entity.PHYSICAL)));
		panel.add(armorPhys);
		
		panel.add(new JLabel("Armour - Fire"));
		final JTextField armorFire = new JTextField(5);
		armorFire.setText(Double.toString(e.getDefense().get(Entity.FIRE)));
		panel.add(armorFire);
		
		panel.add(new JLabel("Armour - Air"));
		final JTextField armorAir = new JTextField(5);
		armorAir.setText(Double.toString(e.getDefense().get(Entity.AIR)));
		panel.add(armorAir);
		
		panel.add(new JLabel("Armour - Earth"));
		final JTextField armorEarth = new JTextField(5);
		armorEarth.setText(Double.toString(e.getDefense().get(Entity.EARTH)));
		panel.add(armorEarth);
		
		panel.add(new JLabel("Armour - Water"));
		final JTextField armorWater = new JTextField(5);
		armorWater.setText(Double.toString(e.getDefense().get(Entity.WATER)));
		panel.add(armorWater);
		
		panel.add(new JLabel("Armour - Death"));
		final JTextField armorDeath = new JTextField(5);
		armorDeath.setText(Double.toString(e.getDefense().get(Entity.DEATH)));
		panel.add(armorDeath);
		
		panel.add(new JLabel("Armour - Life"));
		final JTextField armorLife = new JTextField(5);
		armorLife.setText(Double.toString(e.getDefense().get(Entity.LIFE)));
		panel.add(armorLife);
		
		panel.add(new JLabel("EXP"));
		final JTextField exp = new JTextField(5);
		exp.setText(Integer.toString(e.expAmount));
		panel.add(exp);
		
		final JButton drops = new JButton("Drops");
		drops.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				new DropListFrame(dropList);

			}});
		panel.add(drops);

		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				try{
					e.dropList = dropList;
					e.expAmount = Integer.parseInt(exp.getText());
					
					MapEditor.gamedata.getGameEntities().remove(e.getName());
					e.setName(name.getText());
					MapEditor.gamedata.getGameEntities().put(e.getName(), e);
					e.setFaction(faction.getText());
					e.setAnimateTime(Long.parseLong(animUpdate.getText()));
					e.setTotalAnimateStrip(Integer.parseInt(animStrips.getText()));
					e.speed = Integer.parseInt(speed.getText());
					e.setAnimStages(Integer.parseInt(animStages.getText()));
					e.setAnimateStrip(Integer.parseInt(cuanimStrips.getText()));
					e.setAnimateStage(Integer.parseInt(cuanimStage.getText()));
					e.setWeight(Integer.parseInt(weight.getText()));
					e.setSpriteFile(spriteFile.getText());
					e.processSpritesheet();

					int[] r = {Integer.parseInt(x.getText()), Integer.parseInt(y.getText()),
							Integer.parseInt(width.getText()), Integer.parseInt(height.getText())};

					e.setCollisionShape(r);

					e.setPassable(passable.isSelected());
					e.setVisible(visible.isSelected());

					if (left.isSelected())
					{
						e.getPos()[2] = 0;
					}
					else
					{
						e.getPos()[2] = 1;
					}

					e.setBehavior(behavior);
					
					e.setDialogue(d);
					
					e.setMaxHealth(Double.parseDouble(health.getText()));
					e.setHealth(Double.parseDouble(health.getText()));
					
					HashMap<String, Double> newDefense = new HashMap<String, Double>();
					newDefense.put(Entity.PHYSICAL, Double.parseDouble(armorPhys.getText()));
					newDefense.put(Entity.FIRE, Double.parseDouble(armorFire.getText()));
					newDefense.put(Entity.AIR, Double.parseDouble(armorAir.getText()));
					newDefense.put(Entity.EARTH, Double.parseDouble(armorEarth.getText()));
					newDefense.put(Entity.WATER, Double.parseDouble(armorWater.getText()));
					newDefense.put(Entity.DEATH, Double.parseDouble(armorDeath.getText()));
					newDefense.put(Entity.LIFE, Double.parseDouble(armorLife.getText()));
					
					e.setDefense(newDefense);
					
					EditorFrame.mapPanel.repaint();
					init();
				}
				catch (Exception i)
				{
					System.err.println("Invalid input");
					init();
				}

			}});
		panel.add(apply);

		JButton reset = new JButton("Close");
		reset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();

			}});
		panel.add(reset);
		
		JButton delete = new JButton("Delete");
		delete.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				MapEditor.gamedata.getGameEntities().remove(e.getName());
				EditorFrame.mapPanel.repaint();
				frame.dispose();
				
			}});
		panel.add(delete);

		panel.revalidate();
	}


	public void filechoose()
	{
		String location = spritefile;
		
		if ((location == null) || (location.equals("")))
		{
			location = new File("").getAbsolutePath();
		}
		
		final JFileChooser fc = new JFileChooser(location);

		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			spritefile = fc.getSelectedFile().getName();

			init();

		}
	}
}

class BehaviorFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean[] behavior;
	JPanel panel = new JPanel();
	JFrame frame = this;

	public BehaviorFrame(boolean[] behavior)
	{
		this.behavior = behavior;

		this.add(panel);
		init();

		this.setSize(800, 600);
		this.setVisible(true);
	}

	public void init()
	{
		panel.removeAll();
		panel.setLayout(new GridLayout(5, 2));

		panel.add(new JLabel("KeyBoard Controlled"));
		final JCheckBox KeyCon = new JCheckBox("");
		KeyCon.setSelected(behavior[0]);
		panel.add(KeyCon);

		panel.add(new JLabel("Gravity + Collision"));
		final JCheckBox GravCol = new JCheckBox("");
		GravCol.setSelected(behavior[1]);
		panel.add(GravCol);

		panel.add(new JLabel("Simple Enemy AI - Move to Player and shoot spells"));
		final JCheckBox simpleAI = new JCheckBox("");
		simpleAI.setSelected(behavior[2]);
		panel.add(simpleAI);
		
		panel.add(new JLabel("Activate Dialogue when stepped into"));
		final JCheckBox stepOnActivate = new JCheckBox("");
		if (behavior.length > 3)
			stepOnActivate.setSelected(behavior[3]);
		panel.add(stepOnActivate);

		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				try{
					
					behavior[0] = KeyCon.isSelected();
					behavior[1] = GravCol.isSelected();
					behavior[2] = simpleAI.isSelected();
					behavior[3] = stepOnActivate.isSelected();

					frame.dispose();
				}
				catch (Exception i)
				{
					System.err.println("Invalid input");
					init();
				}

			}});
		panel.add(apply);

		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				init();

			}});
		panel.add(reset);

		panel.revalidate();

	}
}

class DialogueFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dialogue d;
	JPanel panel = new JPanel();
	JPanel dialoguePanel = new JPanel();
	JFrame frame = this;
	
	public DialogueFrame(final Dialogue d)
	{
		this.d = d;
		
		this.add(panel);
		panel.setLayout(new BorderLayout());
		
		JScrollPane sp = new JScrollPane(dialoguePanel);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		panel.add(sp, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel();
		panel.add(bottom, BorderLayout.SOUTH);
		
		final String[] genders = {"Female", "Male"};
		final JComboBox genderBox = new JComboBox(genders);
		genderBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				d.lastGender = genderBox.getSelectedIndex();
				init();
				
			}});
		genderBox.setSelectedIndex(Character.gender);
		bottom.add(genderBox);
		
		final String[] bubbleType = {"Speech", "Examine"};
		final JComboBox bubbleBox = new JComboBox(bubbleType);
		bubbleBox.setSelectedIndex(d.type);
		bubbleBox.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				d.setType(bubbleBox.getSelectedIndex());
				
			}});
		bottom.add(bubbleBox);
		
		final String[] dialogueTypes = {"Speech", "Kill", "ChangeLevel", "ChangePosition", "ChangeStage", "ChangePassable", "ChangeVisible", "GetItem", "Suicide"};
		final JComboBox comboBox = new JComboBox(dialogueTypes);
		bottom.add(comboBox);
		
		JButton add = new JButton("Add Dialogue");
		add.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> newblock = new ArrayList<String>();
				newblock.add(dialogueTypes[comboBox.getSelectedIndex()]);
				d.getQuest().add(newblock);
				init();
			}});
		bottom.add(add);
		
		JButton delete = new JButton("Delete Dialogue");
		delete.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (d.getQuest().size() == 0)
					return;
				
				d.getQuest().remove(d.getQuest().size()-1);
				init();
			}});
		bottom.add(delete);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				
			}});
		bottom.add(close);
		
		init();
		
		this.setSize(600, 400);
		this.setVisible(true);
	}
	
	public void init()
	{
		dialoguePanel.removeAll();
		dialoguePanel.setLayout(new GridLayout(50, 1));
		
		int i = 0;
		for (final ArrayList<String> block : d.getQuest())
		{
			JButton blockBtn = new JButton(i+ " " + block.get(0));
			blockBtn.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					new DialogueBlockFrame(block);
					
				}});
			dialoguePanel.add(blockBtn);
			i++;
		}
		
		dialoguePanel.revalidate();
		dialoguePanel.repaint();
	}
}

class DialogueBlockFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<String> block;
	JFrame frame = this;
	JPanel panel = new JPanel();
	
	public DialogueBlockFrame(ArrayList<String> block)
	{
		this.block = block;
		
		this.add(panel);
		
		init();
		
		this.setSize(600, 400);
		this.setVisible(true);
	}
	
	public void init()
	{
		panel.removeAll();
		
		if (block.get(0).equals("Speech"))
		{
			speech();
		}
		else if (block.get(0).equals("Kill"))
		{
			kill();
		}
		else if (block.get(0).equals("ChangeLevel"))
		{
			changeLevel();
		}
		else if (block.get(0).equals("ChangePosition"))
		{
			changePosition();
		}
		else if (block.get(0).equals("ChangeStage"))
		{
			changeStage();
		}
		else if (block.get(0).equals("ChangePassable"))
		{
			changePassable();
		}
		else if (block.get(0).equals("ChangeVisible"))
		{
			changeVisible();
		}
		else if (block.get(0).equals("GetItem"))
		{
			getItem();
		}
		else if (block.get(0).equals("Suicide"))
		{
			suicide();
		}
		
		
		panel.revalidate();
	}
	
	public void speech()
	{
		final ArrayList<JTextField> text = new ArrayList<JTextField>();
		
		panel.setLayout(new BorderLayout());	
		final JPanel npanel = new JPanel();
		npanel.setLayout(new GridLayout(50, 1));
		JScrollPane sp = new JScrollPane(npanel);
		panel.add(sp, BorderLayout.CENTER);
		
		for (int i = 1; i < block.size(); i++)
		{
			JTextField line = new JTextField(50);
			line.setText(block.get(i));
			text.add(line);
			
			npanel.add(line);
		}
		
		
		JPanel opanel = new JPanel();
		panel.add(opanel, BorderLayout.SOUTH);
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				for (int i = 0; i < text.size(); i++)
				{
					block.set(i+1, text.get(i).getText());
				}
				
				frame.dispose();
				
			}});
		opanel.add(apply);
		
		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				for (int i = 0; i < text.size(); i++)
				{
					block.set(i+1, text.get(i).getText());
				}
				
				block.add("");
				
				init();
				
			}});
		opanel.add(add);
		
		JButton delete = new JButton("Delete");
		delete.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (block.size()>1)
				{
					block.remove(block.size()-1);
					init();
				}
				
			}});
		
		opanel.add(delete);
	}


	public void kill()
	{
		while (block.size() < 4)
		{
			block.add("");
		}
		
		panel.setLayout(new GridLayout(10, 2));
		
		panel.add(new JLabel("Target: "));
		final JTextField target = new JTextField(10);
		target.setText(block.get(1));
		panel.add(target);
		
		panel.add(new JLabel("Success Text: "));
		final JTextField success = new JTextField(30);
		success.setText(block.get(2));
		panel.add(success);
		
		panel.add(new JLabel("Fail Text: "));
		final JTextField fail = new JTextField(30);
		fail.setText(block.get(3));
		panel.add(fail);
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				block.set(1, target.getText());
				block.set(2, success.getText());
				block.set(3, fail.getText());
				
				frame.dispose();
				
			}});
		panel.add(apply);
	}
	
	public void changeLevel()
	{
		while (block.size() < 5)
		{
			block.add("");
		}
		
		panel.setLayout(new GridLayout(5, 2));
		
		panel.add(new JLabel("Next Level: "));
		final JTextField target = new JTextField(10);
		target.setText(block.get(1));
		panel.add(target);
		
		panel.add(new JLabel("Pos X: "));
		final JTextField posx = new JTextField(10);
		posx.setText(block.get(2));
		panel.add(posx);
		
		panel.add(new JLabel("Pos Y: "));
		final JTextField posy = new JTextField(10);
		posy.setText(block.get(3));
		panel.add(posy);
		
		panel.add(new JLabel("Direction: "));
		final JTextField dir = new JTextField(10);
		dir.setText(block.get(4));
		panel.add(dir);
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				block.set(1, target.getText());
				block.set(2, posx.getText());
				block.set(3, posy.getText());
				block.set(4, dir.getText());
				
				frame.dispose();
				
			}});
		panel.add(apply);
	}
	
	public void changePosition()
	{
		while (block.size() < 5)
		{
			block.add("");
		}
		
		panel.setLayout(new GridLayout(5, 2));
		
		panel.add(new JLabel("Pos X: "));
		final JTextField posx = new JTextField(10);
		posx.setText(block.get(1));
		panel.add(posx);
		
		panel.add(new JLabel("Pos Y: "));
		final JTextField posy = new JTextField(10);
		posy.setText(block.get(2));
		panel.add(posy);
		
		panel.add(new JLabel("Direction: "));
		final JTextField dir = new JTextField(10);
		dir.setText(block.get(3));
		panel.add(dir);
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				block.set(1, posx.getText());
				block.set(2, posy.getText());
				block.set(3, dir.getText());
				
				frame.dispose();
				
			}});
		panel.add(apply);
	}
	
	public void changeStage()
	{
		while (block.size() < 3)
		{
			block.add("");
		}
		
		panel.setLayout(new GridLayout(5, 2));
		
		panel.add(new JLabel("Stage: "));
		final JTextField stage = new JTextField(10);
		stage.setText(block.get(1));
		panel.add(stage);
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				block.set(1, stage.getText());
				
				frame.dispose();
				
			}});
		panel.add(apply);
	}
	
	public void changePassable()
	{
		while (block.size() < 3)
		{
			block.add("");
		}
		
		panel.setLayout(new GridLayout(5, 2));
		
		panel.add(new JLabel("Target: "));
		final JTextField target = new JTextField(10);
		target.setText(block.get(1));
		panel.add(target);
		
		panel.add(new JLabel("Passable: "));
		final JTextField passable = new JTextField(10);
		passable.setText(block.get(2));
		panel.add(passable);
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				block.set(1, target.getText());
				block.set(2, passable.getText());
				
				frame.dispose();
				
			}});
		panel.add(apply);
	}
	
	public void changeVisible()
	{
		while (block.size() < 3)
		{
			block.add("");
		}
		
		panel.setLayout(new GridLayout(5, 2));
		
		panel.add(new JLabel("Target: "));
		final JTextField target = new JTextField(10);
		target.setText(block.get(1));
		panel.add(target);
		
		panel.add(new JLabel("Visible: "));
		final JTextField visible = new JTextField(10);
		visible.setText(block.get(2));
		panel.add(visible);
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				block.set(1, target.getText());
				block.set(2, visible.getText());
				
				frame.dispose();
				
			}});
		panel.add(apply);
	}
	
	public void getItem()
	{
		while (block.size() < 4)
		{
			block.add("");
		}
		
		panel.setLayout(new GridLayout(10, 2));
		
		panel.add(new JLabel("Item Name: "));
		final JTextField target = new JTextField(10);
		target.setText(block.get(1));
		panel.add(target);
		
		panel.add(new JLabel("Success Text: "));
		final JTextField success = new JTextField(30);
		success.setText(block.get(2));
		panel.add(success);
		
		panel.add(new JLabel("Fail Text: "));
		final JTextField fail = new JTextField(30);
		fail.setText(block.get(3));
		panel.add(fail);
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				block.set(1, target.getText());
				block.set(2, success.getText());
				block.set(3, fail.getText());
				
				frame.dispose();
				
			}});
		panel.add(apply);
	}
	
	public void suicide()
	{
		while (block.size() < 2)
		{
			block.add("");
		}
		
		panel.setLayout(new GridLayout(5, 2));
		
		panel.add(new JLabel("Target: "));
		final JTextField target = new JTextField(10);
		target.setText(block.get(1));
		panel.add(target);
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				block.set(1, target.getText());
				
				frame.dispose();
				
			}});
		panel.add(apply);
	}
	
}

class DropListFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<JTextField[]> drops = new ArrayList<JTextField[]>();
	JPanel panel = new JPanel();
	HashMap<String, Integer> dropsHash;
	JFrame frame = this;
	
	public DropListFrame(HashMap<String, Integer> dropsHash)
	{
		this.dropsHash = dropsHash;
		
		for (Map.Entry<String, Integer> entry : dropsHash.entrySet())
		{
			JTextField dropName = new JTextField(10);
			dropName.setText(entry.getKey());
			
			JTextField dropChance = new JTextField(10);
			dropChance.setText(Integer.toString(entry.getValue()));
			
			this.drops.add(new JTextField[]{dropName, dropChance});
		}
		
		init();
		
		this.add(panel);
		this.setSize(600, 400);
		this.setVisible(true);
	}
	
	public void init()
	{
		panel.removeAll();
		panel.setLayout(new GridLayout(2, 1));
		
		JPanel dropPanel = new JPanel();
		dropPanel.setLayout(new GridLayout(10, 1));
		
		for (JTextField[] drop : drops)
		{
			JPanel singleDrop = new JPanel();
			
			singleDrop.add(drop[0]);
			singleDrop.add(drop[1]);
			
			dropPanel.add(singleDrop);
		}
		
		panel.add(dropPanel);
		
		JPanel buttons = new JPanel();
		
		JButton newDrop = new JButton("New");
		newDrop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField dropName = new JTextField(10);
				JTextField dropChance = new JTextField(10);
				drops.add(new JTextField[]{dropName, dropChance});
				
				init();
				
			}});
		buttons.add(newDrop);
		
		JButton delete = new JButton("Delete");
		delete.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (drops.size() == 0)
					return;
				
				drops.remove(drops.size()-1);
				
				init();
				
			}});
		buttons.add(delete);
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dropsHash.clear();
				for (JTextField[] drop : drops)
				{
					dropsHash.put(drop[0].getText(), Integer.parseInt(drop[1].getText()));
				}
				
				frame.dispose();
				
			}});
		buttons.add(apply);
		
		panel.add(buttons);
		panel.revalidate();
	}
}