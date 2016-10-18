import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.*;
import java.util.Stack;

import javax.swing.*;

public class PaintAssistant extends JFrame implements MouseListener, MouseMotionListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Graphics g;
	private JPanel jp1, jp2;
	private JButton[] buttons = new JButton[11];
	private String[] button_names = {"Add Text", "Background color", "New Paint", "Clear Shapes", 
									"Line", "Rectangle", "Oval", "Circle", "RoundRect", "Undo(-)", "Redo(-)"};
	private String[] action_names = {"add text", "set bg color", null, null, "draw line", "draw rectangle", "draw Oval", "draw Circle", "draw RoundRect", null, null};	
	
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 800;
	
	private Stack<Point> starts;  // set of all start points
	private Stack<Point> ends;  // set of all end points
	
	private Stack<Point> starts_redo;  // set of all start points for redo
	private Stack<Point> ends_redo;  // set of all end points for redo
	
	private Point singleStart;  // current start point
	private Point singleEnd;  // current end point
	
	private Color bg_color;  // current bg color, default bg color is white
	private Color pencil_color;  // current pencil color, default pencil color is black
	private Stack<Color> bg_colors;  // set of all bg colors
	private Stack<Color> pencil_colors;  // set of all pencil colors
	
	// usage: undoing: when pop a bg color from bg_colors, push it into bg_colors_redo
	//        redoing: when pop a bg color from bg_colors_redo, push it into bg_colors
	private Stack<Color> bg_colors_redo;   // set of bg colors for redo, matches the actions for redo

	// usage: undoing: when pop a pencil color from pencil_colors, push it into pencil_colors_redo
	//        redoing: when pop a pencil color from pencil_colors_redo, push it into pencil_colors
	private Stack<Color> pencil_colors_redo;   // set of pencil colors for redo, matches the actions for redo
	
	private Stack<String> actions;  // set of actions, includes the following 7 actions
	// usage: undoing: when pop an action from actions, push it into actions_redo
	//        redoing: when pop an action from actions_redo, push it into actions
	private Stack<String> actions_redo;  // set of redo actions	
	private String action_name;  // current action
	
	@SuppressWarnings("deprecation")
	public PaintAssistant(){
		this.setTitle("PaintTool");
		this.setSize(WIDTH, HEIGHT);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
		this.show();
		
		this.setLayout(null);
		jp1 = new JPanel();
		jp2 = new JPanel();
		
		for(int i = 0; i < buttons.length; i++){
			buttons[i] = new JButton(button_names[i]);
			buttons[i].addActionListener(this);
			add(buttons[i]);
		}
		
		jp1.setBorder(BorderFactory.createTitledBorder("Settings"));
		jp1.setBackground(Color.PINK);
		jp1.setBounds(10,10,180,750);
		
		for(int i = 0; i < buttons.length; i++){
			buttons[i].setBounds(25, 40+50*i, 150, 30);
		}
		
		add(jp1);
		
		initialize();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		System.out.println("1. CHANGE CONSTRUCTOR: INITIALIZE MEMBER VARIABLES IN THE CONSTRUCTOR, THEN CALL CONSTRUCTOR IF CLICK NEW PAINT");
		System.out.println("2. Undo/Redo drawing line not finished, need other stacks for keeping track of points in the line.");
		System.out.println("3. Two components needed at jp2, one for bg_color, the other for drawing, o.w. bg color will cover drawings.");
	
	}
	
	public void initialize() {
		starts = new Stack<Point>(); 
		ends = new Stack<Point>(); 
		
		starts_redo = new Stack<Point>();
		ends_redo = new Stack<Point>();
		
		singleStart = new Point(); 
		singleEnd = new Point(); 
		
		bg_color = Color.white; 
		pencil_color = Color.black; 
		bg_colors = new Stack<Color>(); 
		pencil_colors = new Stack<Color>(); 
		
		bg_colors_redo = new Stack<Color>();
		pencil_colors_redo = new Stack<Color>();
		
		actions = new Stack<String>();  
		actions_redo = new Stack<String>(); 
		
		jp2.setBounds(210, 10, 770, 750);
		jp2.setBackground(bg_color);
		
		g = this.getGraphics();	
		g.setClip(213, 36, 770, 750);
		g.setColor(bg_color);
		g.fillRect(213,36,770 ,750);
		
		bg_colors.push(bg_color);
		//System.out.println("initialzie() pushing " + bg_color.toString() + " to bg_colors");
		action_name = action_names[1];
		actions.push(action_name);
		//System.out.println("initialzie() pushing " + action_names[1] + " to actions");
		add(jp2);
		
		pencil_colors.push(pencil_color);
		//System.out.println("initialzie() pushing " + pencil_color.toString() + " to pencil_colors");
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttons[0]){  // text
			Color chosen_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			if(chosen_color != null)
				pencil_color = chosen_color;
			//pencil_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			g.setColor(pencil_color);
			action_name = action_names[0];
		}
		if(e.getSource() == buttons[1]){  // background color
			Color chosen_color = JColorChooser.showDialog(null, "Set Background Color", bg_color);
			if(chosen_color != null){
				bg_color = chosen_color;	
				//bg_color = JColorChooser.showDialog(null, "Set Background Color", bg_color);
				g.setColor(bg_color);
				//jp2.setBackground(bg_color);
				g.fillRect(213,36,770 ,750);
				bg_colors.push(bg_color);
				//System.out.println("actionPerformed() pushing " + bg_color.toString() + " to bg_colors");
			
				action_name = action_names[1];
				actions.push(action_names[1]); // action: set background color
				//System.out.println("actionPerformed() pushing " + action_names[1] + " to actions");
			}else
				return;
		}else if(e.getSource() == buttons[2]){  // new paint
			// remove all members from all stacks
			// set bg color to white
			// change default pencil color to black
			int dialogResult;
			dialogResult = JOptionPane.showConfirmDialog (null, "Would You Like to Delete All Painting?");
			if(dialogResult == JOptionPane.YES_OPTION){
				initialize();
			}
			
		}else if(e.getSource() == buttons[3]){  // clear shapes and texts
			int dialogResult;
			dialogResult = JOptionPane.showConfirmDialog (null, "Would You Like to Clear All Shapes and Texts?");
			if(dialogResult == JOptionPane.YES_OPTION){
				
				// retain "set bg color" actions only, remove other actions
				for(int i = 0; i < actions.size(); i++){
					String current_action = actions.get(i);
					if(!current_action.equals(action_names[1])){ // remove all actions except set bg color action
						actions.remove(i);
						//System.out.println("actionPerformed() removing action: " + current_action);
					}
				}
					
				
				// remove any drawing-shape related information, but retain bg_colors
				// set bg_color to last bg_color
				// set action_name to "set bg color"
				bg_colors_redo.removeAllElements();
				pencil_colors.removeAllElements();
				pencil_colors_redo.removeAllElements();
				
				bg_color = bg_colors.peek();
				pencil_color = Color.black;
				pencil_colors.push(pencil_color);
				
				actions_redo.removeAllElements();
				action_name = action_names[1];
				
				starts.removeAllElements();
				ends.removeAllElements();
				starts_redo.removeAllElements();
				ends_redo.removeAllElements();
				
				singleStart = new Point();
				singleEnd = new Point();
				
				g.setColor(bg_color);    
				g.fillRect(213,36,770 ,750);
			}
		}else if(e.getSource() == buttons[4]){  // line
			Color chosen_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			if(chosen_color != null)
				pencil_color = chosen_color;
			
			//pencil_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			g.setColor(pencil_color);
			action_name = action_names[4];
		}else if(e.getSource() == buttons[5]){  // rectangle
			Color chosen_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			if(chosen_color != null)
				pencil_color = chosen_color;
			
			//pencil_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			g.setColor(pencil_color);
			action_name = action_names[5];
		}else if(e.getSource() == buttons[6]){  // oval
			Color chosen_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			if(chosen_color != null)
				pencil_color = chosen_color;
			
			//pencil_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			g.setColor(pencil_color);
			action_name = action_names[6];
		}else if(e.getSource() == buttons[7]){  // circle
			Color chosen_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			if(chosen_color != null)
				pencil_color = chosen_color;
			
			//pencil_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			g.setColor(pencil_color);
			action_name = action_names[7];
		}else if(e.getSource() == buttons[8]){  // round rectangle
			Color chosen_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			if(chosen_color != null)
				pencil_color = chosen_color;
			
			//pencil_color = JColorChooser.showDialog(null, "Change Pencil Color", pencil_color);
			g.setColor(pencil_color);
			action_name = action_names[8];
		}else if(e.getSource() == buttons[9]){  // undo
			if(!actions.isEmpty()){	
				if(actions.peek().equals(action_names[1]) && bg_colors.size() > 1){ // undo set bg color action
					//System.out.println("actionPerformed() poping " + bg_colors.peek().toString() + " from bg_colors");
					bg_colors_redo.push(bg_colors.pop());
					bg_color = bg_colors.peek();
					//System.out.println("actionPerformed() current bg_color: " + bg_color.toString());
					
					g.setColor(bg_color);    
					g.fillRect(213,36,770 ,750);
					
					//System.out.println("actionPerformed() poping: " + actions.peek() + " from actions");
					actions_redo.push(actions.pop());
					action_name = actions.peek();
					//System.out.println("actionPerformed() current action: " + action_name);
				}else if(actions.peek().equals(action_names[1]) && bg_colors.size() <= 1){
					// first action is always setting bg to white
					// cannot undo the first action
					System.out.println("no more action to undo!!");
				}else{  // current action name is 0 or 4 or 5 or 6 or 7 or 8
					g.setColor(bg_color);
					singleStart = starts.pop();
					starts_redo.push(singleStart);
					singleEnd = ends.pop();
					ends_redo.push(singleEnd);
					
					//System.out.println("actionPerformed() poping " + pencil_colors.peek().toString() + " from pencil_colors");
					pencil_colors_redo.push(pencil_colors.pop());
					pencil_color = pencil_colors.peek();
					//System.out.println("actionPerformed() current pencil_color: " + pencil_color.toString());
					
					if(actions.peek().equals(action_names[4])){  // undo 4
						System.out.println("UNDO DRAWING A LINE FUNCTION NOT IMPLEMENTED!!!");
			
					}else{  // undo 0 or 5 or 6 or 7 or 8
						int[] param = drawingParameters();
						
						if(action_name.equals(action_names[0])){  //text
							g.drawString("default text", param[0], param[1]);
						}else if(action_name.equals(action_names[5])){  // rect
							g.drawRect(param[0], param[1], param[2], param[3]);
						}else if(action_name.equals(action_names[6])){  // oval
							g.drawOval(param[0], param[1], param[2], param[3]);
						}else if(action_name.equals(action_names[7])){  // circle
							g.drawOval(param[0], param[1], Math.min(param[2], param[3]), Math.min(param[2], param[3]));
						}else if(action_name.equals(action_names[8])){  // roundrect
							g.drawRoundRect(param[0], param[1], param[2], param[3], 15, 15);
						}	
					}
					
					//System.out.println("actionPerformed() poping: " + actions.peek() + " from actions");
					actions_redo.push(actions.pop());
					
					action_name = actions.peek();
					//System.out.println("actionPerformed() current action: " + action_name);
				}
				
			}else{
				System.out.println("no more action to undo!!");
			}
		}else if(e.getSource() == buttons[10]){ // redo
			//System.out.println("REDO FUNCTION NOT IMPLEMENTED!!!");
			if(!actions_redo.isEmpty()){	
				if(actions_redo.peek().equals(action_names[1])){ // redo set bg color action
					//System.out.println("actionPerformed() poping " + bg_colors_redo.peek().toString() + " from bg_colors_redo");
					bg_colors.push(bg_colors_redo.pop());
					bg_color = bg_colors.peek();
					//System.out.println("actionPerformed() current bg_color: " + bg_color.toString());
					
					g.setColor(bg_color);    
					g.fillRect(213,36,770 ,750);
					
					//System.out.println("actionPerformed() poping: " + actions_redo.peek() + " from actions_redo");
					actions.push(actions_redo.pop());
					action_name = actions.peek();
					//System.out.println("actionPerformed() current action: " + action_name);
				}else{  // current action name is 0 or 4 or 5 or 6 or 7 or 8
					//g.setColor(bg_color);
					singleStart = starts_redo.pop();
					starts.push(singleStart);
					singleEnd = ends_redo.pop();
					ends.push(singleEnd);
					
					//System.out.println("actionPerformed() poping " + pencil_colors_redo.peek().toString() + " from pencil_colors_redo");
					pencil_colors.push(pencil_colors_redo.pop());
					pencil_color = pencil_colors.peek();
					g.setColor(pencil_color);
					//System.out.println("actionPerformed() current pencil_color: " + pencil_color.toString());
					
					if(actions_redo.peek().equals(action_names[4])){  // redo 4
						actions.push(actions_redo.pop());
						action_name = actions.peek();
						
						System.out.println("UNDO DRAWING A LINE FUNCTION NOT IMPLEMENTED!!!");
			
					}else{  // redo 0 or 5 or 6 or 7 or 8
						actions.push(actions_redo.pop());
						action_name = actions.peek();
						
						int[] param = drawingParameters();
						
						if(action_name.equals(action_names[0])){  //text
							g.drawString("default text", param[0], param[1]);
						}else if(action_name.equals(action_names[5])){  // rect
							g.drawRect(param[0], param[1], param[2], param[3]);
						}else if(action_name.equals(action_names[6])){  // oval
							g.drawOval(param[0], param[1], param[2], param[3]);
						}else if(action_name.equals(action_names[7])){  // circle
							g.drawOval(param[0], param[1], Math.min(param[2], param[3]), Math.min(param[2], param[3]));
						}else if(action_name.equals(action_names[8])){  // roundrect
							g.drawRoundRect(param[0], param[1], param[2], param[3], 15, 15);
						}	
					}
					
					//System.out.println("actionPerformed() poping: " + actions.peek() + " from actions");
					//actions.push(actions_redo.pop());
					
					//action_name = actions.peek();
					//System.out.println("actionPerformed() current action: " + action_name);
				}
				
			}else{
				System.out.println("no more action to redo!!");
			}
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		singleEnd = e.getPoint();		
		//repaint();
		if(action_name.equals(action_names[4])){
			// draw the current line with the changing start point and end point
			g.drawLine(singleStart.x, singleStart.y, singleEnd.x, singleEnd.y);
			singleStart = e.getPoint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		singleStart = e.getPoint();
		starts.push(singleStart);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		singleEnd = e.getPoint();
		ends.push(singleEnd);
		pencil_colors.push(pencil_color);
		System.out.println("mouseReleased() pushing " + pencil_color.toString() + " to pencil_colors");
		//repaint();
		if(action_name.equals(action_names[4])){
			actions.push(action_names[4]);
			System.out.println("mouseReleased() pushing " + action_names[4] + " to actions");
		}else 
			drawDiffShapes();
	}

	public void drawDiffShapes() {
		// store moving parameters, param[0]: x, param[1]: y, param[2]: width, param[3]: height
		int[] param = drawingParameters();
		
		if(action_name.equals(action_names[0])){  //text
			g.drawString("default text", param[0], param[1]);
			actions.push(action_names[0]);
			//System.out.println("mouseReleased() pushing " + action_names[0] + " to actions");
		}else if(action_name.equals(action_names[5])){  // rect
			g.drawRect(param[0], param[1], param[2], param[3]);
			actions.push(action_names[5]);
			//System.out.println("mouseReleased() pushing " + action_names[5] + " to actions");
		}else if(action_name.equals(action_names[6])){  // oval
			g.drawOval(param[0], param[1], param[2], param[3]);
			actions.push(action_names[6]);
			//System.out.println("mouseReleased() pushing " + action_names[6] + " to actions");
		}else if(action_name.equals(action_names[7])){  // circle
			g.drawOval(param[0], param[1], Math.min(param[2], param[3]), Math.min(param[2], param[3]));
			actions.push(action_names[7]);
			//System.out.println("mouseReleased() pushing " + action_names[7] + " to actions");
		}else if(action_name.equals(action_names[8])){  // roundrect
			g.drawRoundRect(param[0], param[1], param[2], param[3], 15, 15);
			actions.push(action_names[8]);
			//System.out.println("mouseReleased() pushing " + action_names[8] + " to actions");
		}	
	}

	public int[] drawingParameters() {
		int x;
		int y;
		int width;
		int height;
		
		int[] param = new int[4];  // store parameters
		
		if(movingDirection().equals("rightdown")){
			x = singleStart.x;
			y = singleStart.y;
			width = singleEnd.x-singleStart.x;
			height = singleEnd.y-singleStart.y;	
		} else if(movingDirection().equals("rightup")){
			x = singleStart.x;
			y = singleEnd.y;
			width = singleEnd.x-singleStart.x;
			height = singleStart.y-singleEnd.y;		
		}else if(movingDirection().equals("leftdown")){
			x = singleEnd.x;
			y = singleStart.y;
			width = singleStart.x-singleEnd.x;
			height = singleEnd.y-singleStart.y;
		}else{  // "leftup"
			x = singleEnd.x;
			y = singleEnd.y;
			width = singleStart.x-singleEnd.x;
			height = singleStart.y-singleEnd.y;
		}
		
		param[0] = x;
		param[1] = y;
		param[2] = width;
		param[3] = height;
		
		return param;
	}

	// direction of moving
	public String movingDirection() {
		String direction="";
		if(singleStart.x <= singleEnd.x && singleStart.y <= singleEnd.y)
			direction = "rightdown";
		else if(singleStart.x <= singleEnd.x && singleStart.y > singleEnd.y)
			direction = "rightup";
		else if(singleStart.x > singleEnd.x && singleStart.y <= singleEnd.y)
			direction = "leftdown";
		else  // singleStart.x >singleEnd.x && singleStart.y > singleEnd.y
			direction = "leftup";
		return direction;
	}
	
	/*public void paint(Graphics g2){
		super.paint(g2);
		
		// draw the current line with the start point and the changing end point
		g.drawLine(singleStart.x, singleStart.y, singleEnd.x, singleEnd.y);
		
		// re-draw all previous lines
		for (int i = 0; i < starts.size() && i < ends.size(); i++) {
			g.setColor(pencil_colors.get(i));
	        g.drawLine(starts.get(i).x, starts.get(i).y,
	                ends.get(i).x, ends.get(i).y);
	    }
	}*/

}
