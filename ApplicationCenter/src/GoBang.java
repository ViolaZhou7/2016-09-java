import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
 * Peer-to-Peer Five-In-A-Row game
 * manage the interface
 * 
 */
public class GoBang extends JFrame implements ActionListener, KeyListener{
	// constants
	private final int WIDTH = 20;
	private final int HEIGHT = 20;
	private final String[] GRID_SIZE = {"20x20","15x15","30x20"};
	private final Object[] BRANCH_1 = new Object[]{"Start",null,"Regret",null,"Quit"};
    private final Object[] BRANCH_2 = new Object[]{"GameBoard"};
    private final Object[] BRANCH_3 = new Object[]{"About"};
	
    // variables
	private int width;
	private int height;
	private GameBoard gb;
	private static MainPanel mp;
	
	GoBang(){
		gb = new GameBoard(WIDTH, HEIGHT); // a new game board, a new game board calls a new win table
		mp = new MainPanel(gb); // a new main panel
		Container con = this.getContentPane(); 
		con.add(mp, "Center");
		this.setTitle("GoBang");
		this.setMapSize(WIDTH, HEIGHT);
		this.width = WIDTH;
		this.height = HEIGHT;
		this.setResizable(false);
		//this.addWindowListener(new Exit());
		this.addKeyListener(this);
		JMenuBar menubar = new JMenuBar(); // menu bar
		this.setJMenuBar(menubar);
		menubar.add(makeMenu("Game", BRANCH_1, this));
		menubar.add(makeMenu("Settings", BRANCH_2, this));
		menubar.add(makeMenu("Help", BRANCH_3, this));
	}
	
	/*
	 * create a menu bar
	 */
	public JMenu makeMenu(Object parent, Object item[], Object aim) {
		JMenu m = null;
		if(parent instanceof JMenu)
			m = (JMenu) parent;
		else if(parent instanceof String)
			m = new JMenu((String) parent);
		else
			return null;
		for(int i = 0; i < item.length; i++)
			if(item[i] == null)
				m.addSeparator();
			else if(item[i] == "GameBoard"){
				JMenu jm = new JMenu("GameBoard");
				ButtonGroup bg = new ButtonGroup();
				JRadioButtonMenuItem rmenu;
                for (int j=0; j<GRID_SIZE.length; j++){
                      rmenu=makeRadioButtonMenuItem( GRID_SIZE[j], aim);
                      if (j == 0)
                         rmenu.setSelected(true);
                      jm.add(rmenu);
                      bg.add(rmenu);
                }
                m.add(jm);
            }else
                m.add(makeMenuItem( item[i], aim));
        return m;
	}

	public JMenuItem makeMenuItem(Object item, Object aim) {
		JMenuItem r = null;
        if(item instanceof String)
            r = new JMenuItem((String)item);
        else if(item instanceof JMenuItem)
            r = (JMenuItem)item;
        else
            return null;
        if(aim instanceof ActionListener)
            r.addActionListener( (ActionListener)aim );
        return r;
	}

	public JRadioButtonMenuItem makeRadioButtonMenuItem(Object item, Object aim) {
		JRadioButtonMenuItem r = null;
        if(item instanceof String)
            r = new JRadioButtonMenuItem((String)item);
        else
        if(item instanceof JRadioButtonMenuItem)
            r = (JRadioButtonMenuItem)item;
        else
            return null;
        if(aim instanceof ActionListener)
            r.addActionListener((ActionListener)aim);
        return r;
	}

	public void setMapSize(int w, int h) {
		setSize(w*20+50, h*20+100);
		mp.setModel(gb);
		mp.repaint();
		
	}
	
	public void restart(int w,int h){
        gb = new GameBoard(w,h);
        setMapSize(w,h);
    }

	/*public static void main(String[] args) {
		JFrame frame = new GoBang();
        frame.setVisible(true);
	}*/

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
        if(e.getKeyCode() == KeyEvent.VK_F1){
            restart( this.width, this.height);
            System.out.println("new");
        }
        if(e.getKeyCode() == KeyEvent.VK_R){
            mp.regret();
        }
        if(e.getKeyCode() == KeyEvent.VK_A){
            JOptionPane.showMessageDialog( this, "version: 1.0", "About", 0);
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String arg = e.getActionCommand();
        if(arg.indexOf("x") != -1){
        	// i.e. 20x20, width = 20, height = 20
            this.width = new Integer(arg.substring(0,2)).intValue();
            this.height = new Integer(arg.substring(3,5)).intValue();
            gb=new GameBoard( this.width, this.height);
            setMapSize( this.width, this.height);
            SwingUtilities.updateComponentTreeUI(this);
        }
        if(arg.equals("Start")) // a new game
            restart( this.width, this.height);
        if(arg.equals("Regret")) // regret the last step
            mp.regret();
        if(arg.equals("About")) // open information dialog
            JOptionPane.showMessageDialog( this, "version: 1.0", "About", 0);
        if(arg.equals("Quit")) // exit the game
            System.exit(0);
	}

	public static JPanel getJP(){
        return mp;
    }
}
