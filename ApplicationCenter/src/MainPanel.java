import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

/*
 * the game interface
 * mouse listener
 * draw the game board and pieces
 */
public class MainPanel extends JPanel implements MouseListener{
	
	private static final Color[] player_color = {null, Color.BLACK, Color.WHITE};
	//private final int UNIT_SIZE = 20;
	
	private int width;
	private int height;
	
	private GameBoard gb;
	private WinTable wt = new WinTable();
	
	public MainPanel(GameBoard gb){
		this.gb = gb;
		this.width = gb.getWidth();
		this.height = gb.getHeight();
		addMouseListener(this);
	}

	public void paintComponent(Graphics g){
		
		super.paintComponent(g);
		g.setColor(Color.black);
		
		// draw game board
		for(int y = 1; y <= this.height; y++){
            g.drawLine( 20 ,y*20 ,this.width*20, y*20);
            g.drawString(String.valueOf(y), this.width*20+5, y*20+5);
            
        }
        for(int x = 1; x <= this.width; x++){
            g.drawLine( x*20, 20, x*20, 20*this.height);
            g.drawString(String.valueOf(x), x*20-5, this.height*20+15);
        }
        
        g.setColor(Color.white);
        g.drawRect((WinTable.a+1)*20-8 , (WinTable.b+1)*20-8 , 16 , 16 ); 
        
        // draw player pieces
        for (int j = 0; j < this.height; j++) {
        	for (int i = 0; i < this.width; i++) {
        		int who = gb.getGrid()[i][j]; // who can be 1 or 2
        		if (who == 1 || who == 2) {
     			   g.setColor(player_color[who]); // 1: black; 2: white
     			   g.fillOval(i*20+20-8,j*20+20-8, 20-4, 20-4);
        		}
     	   	}   
     	}
	}
	
	
	public void regret(){
		gb.regret();
		repaint();
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int col = e.getX();
		int row = e.getY();
		
		if(col < width*20 + 10 && row < height*20 + 10){
			int x = (col - 10) / 20;
			int y = (row - 10) / 20;
			
			if(e.getModifiers() == MouseEvent.BUTTON1_MASK && !wt.isWin()){
				gb.play(x,y);
				repaint();
				
				if( wt.isWin()){
                    gb.showMsg(this);
                    e.consume();
                    repaint();
                }
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void setModel(GameBoard gb) {
		this.gb = gb;
		this.width = gb.getWidth();
		this.height = gb.getHeight();
		
	}

}
