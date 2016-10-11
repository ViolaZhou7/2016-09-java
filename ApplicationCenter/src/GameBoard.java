import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*
 * manage the items on the game board, provide play, regret functions
 * distinguish validation of steps
 * 
 */
public class GameBoard {
	private final int EMPTY_GRID = 0; // empty
	private final int BLACK_GRID = 1; // black
	private final int WHITE_GRID = 2; // white 
	
	private int x;
	private int y;
	private int width;
	private int height;
	private int numberOfSteps;
	private int[][] grid; // 0=empty, 1=black, 2=white
	private String[][] process; // a matrix records every step

	private boolean isOdd, isExist; // isOdd: true - black's turn, false - white's turn
	private WinTable wt = new WinTable(); // a WinTable instance
	
	/*
	 * constructor: a new game board with width and height
	 */
	public GameBoard(int width, int height){
		this.isOdd = true; // black's turn first
		this.isExist = false;
		this.numberOfSteps = 0;
		wt.newTable(width, height); // creates a new win table
		panelInit(width, height);
	}
	
	/*
	 * initialize the panel
	 */
	private void panelInit(int width, int height) {
		this.width = width;
        this.height = height;
        this.grid = new int[width][height];
        
        // total width*height rows, 2 columns
        // first column stores "0 (EMPTY_GRID)", "1 (BLACK_GRID)", or "2 (WHITE_GRID)" as string
        // second column stores the x, y coordinates of the step, i.e. "0040", "2060" as string
        this.process = new String[width*height][2];
        
        // initialize each grid as EMPTY_GRID = 0 as int
        for(int i=0; i < width; i++){
            for(int j=0; j < height; j++){
                grid[i][j] = EMPTY_GRID;
            }
        }
	}

	/*
	 * width of the game board
	 */
	public int getWidth() {
		return this.width;
	}

	/*
	 * height of the game board
	 */
	public int getHeight() {
		return this.height;
	}

	public int getX(){
        return this.x;
    }
 
    public int getY(){
        return this.y;
    }
    
    
	public int[][] getGrid() {
		return grid;
	}
	
	public String[][] getProcess(){
		return process;
	}
	
	public int getNumberOfSteps(){
		return numberOfSteps;
	}

	/*
	 * regret the current last step
	 */
	public void regret() {
		if( this.numberOfSteps < 1 ){ // no step to regret
            JOptionPane.showMessageDialog(GoBang.getJP(),"Invalid!","Warning",JOptionPane.INFORMATION_MESSAGE);
            return;
		}
		
		if(!this.process[this.numberOfSteps-1][0].equals("0")){
			this.process[this.numberOfSteps-1][0] = "" + EMPTY_GRID; // back to EMPTY_GRID
			String str1 = this.process[this.numberOfSteps-1][1]; // information about coordinate of last step
			int x = Integer.parseInt(str1.substring(0,2)); // x coordinate
			int y = Integer.parseInt(str1.substring(2)); // y coordinate
			
			this.grid[x][y] = EMPTY_GRID; // back to EMPTY_GRID
			this.process[this.numberOfSteps-1][1] = ""; // back to empty string
			this.isOdd = !this.isOdd; // odd back to even, even back to odd
		}
		this.numberOfSteps--;
		wt.resetTable(this.grid, this.width, this.height); // reset the win table
	}

	/*
	 * play the game
	 */
	public void play(int x, int y) {
		if(badXY(x,y)) // check if click position exceeds the game board
			return;
		if(pieceExist(x,y)) // check if current grid already occupied
			return;
		if(getIsOdd()){ // getIsOdd() is true, means current is black's turn, next is white's
			this.isOdd = false;
			this.grid[x][y] = BLACK_GRID;
			this.process[numberOfSteps][0] = "" + BLACK_GRID;
			this.process[numberOfSteps][1] = wt.intToString(x,y);
			wt.goStep("black", wt.intToString(x,y), grid);
		}else{ // getIsOdd() is false, means current is white's turn, next is black's
			this.isOdd = true;
			this.grid[x][y] = WHITE_GRID;
			this.process[numberOfSteps][0] = "" + WHITE_GRID;
			this.process[numberOfSteps][1] = wt.intToString(x,y);
			wt.goStep("white", wt.intToString(x,y), grid);
		}
		this.numberOfSteps++;
	}
	
	private boolean getIsOdd() {
		return this.isOdd;
	}
	
	public boolean getIsExist(){
        return this.isExist;
    }
	
	/*
	 * if play piece already exists in this grid
	 */
	private boolean pieceExist(int x, int y) {
		if(this.grid[x][y] == BLACK_GRID || this.grid[x][y] == WHITE_GRID)
			this.isExist = true;
		else
			this.isExist = false;
		return this.isExist;
	}

	/*
	 * check if click position exceeds the game board
	 */
	private boolean badXY(int x, int y) {
		if(x > width+20 || x < 0)
			return true;
		if(y > height+20 || y < 0)
			return true;
		return false;
	}

	/*
	 * message
	 */
	public void showMsg(JPanel jp){
		String msg1 = "";
		String msg2 = "Game Over";
		
		if(wt.getWinColor().equals("white")){
			msg1 = "White Win!";
		}else{
			msg1 = "Black Win!";
		}
		
		JOptionPane.showMessageDialog(jp, msg1, msg2, JOptionPane.INFORMATION_MESSAGE);
	}
	
}
