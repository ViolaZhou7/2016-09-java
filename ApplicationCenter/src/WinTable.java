/*
 * core of the game - distinguish win or not?
 */
public class WinTable {
	
	private static final int TABLE_LENGTH = 2000;

	public static int a; // cursor's x coordinate
	public static int b; // cursor's y coordinate
	
	private static boolean isWin;
	private String[][] table = new String[TABLE_LENGTH][7];
	
	private int index; // number of effective records
	private int width;
	private int height;
	private String winColor;
	
	private int temp_int;
    private int temp_int2;
    private String temp_str;
    private String temp_str2;

	public void newTable(int width, int height) {
		isWin = false;
		index = 0;
		a = (int)(width/2);
        b = (int)(height/2);
        this.width = width;
        this.height = height; 
		
        // row
        for(int x = 0; x < this.width - 4; x++){
            for(int y = 0; y<this.height; y++){
                for(int point = 0; point < 5; point++){
                    temp_int = x + point;
                    temp_str = intToString(temp_int,y); 
                    table[index][point]=temp_str;
                }
                table[index][5] = "";
                table[index][6] = "";
                index++;
            }
        }
        // column
        for(int x=0; x<this.width; x++){
            for(int y=0; y<this.height-4; y++){
                for(int point=0; point<5; point++){
                    temp_int2 = y + point;
                    temp_str = intToString(x,temp_int2);
                    table[index][point] = temp_str;
                }
                table[index][5] = "";
                table[index][6] = "";
                index++;
            }
        }
        // diagonal "\"
        for(int x=0; x<this.width-4; x++){
            for(int y=0; y<this.height-4; y++){
                for(int point=0; point<5; point++){
                    temp_int = x + point;
                    temp_int2 = y + point;
                    temp_str = intToString(temp_int,temp_int2);
                    table[index][point] = temp_str;
                }
                table[index][5] = "";
                table[index][6] = "";
                index++;
            }
        }
        // diagonal "/"
        for(int x=0; x<this.width-4; x++){
            for(int y=4; y<this.height; y++){
                for(int point=0; point<5; point++){
                    temp_int = x + point;
                    temp_int2 = y-point;
                    temp_str=intToString(temp_int,temp_int2);
                    table[index][point] = temp_str;
                }
                table[index][5] = "";
                table[index][6] = "";
                index++;
            }
        }
        
        // clean the remaining table
        for(int i=index; i<table.length; i++){
            for(int j=0; j<7; j++){
                table[i][j] = "";
            }
        }
	}

	public String intToString(int a, int b) {
		return (a<10?"0"+a:""+a) + (b<10?"0"+b:""+b);
	}

	// record each step in the table
	public void goStep(String color, String point, int[][] grid) {
		if(point.length() == 4){
			a = Integer.parseInt(point.substring(0, 2)); 
			b = Integer.parseInt(point.substring(2));
		}
		
		// retrieve coordinate, store into [6]
		for(int i=0; i<TABLE_LENGTH; i++){
            for(int j=0; j<5; j++){
                if(table[i][j].equals(point)){
                    if(table[i][5].equals("")){ // not occupied
                        table[i][5] = color;
                        table[i][6] = ""+j; 
                    }else if(table[i][5].equals(color)){ // occupied
                        table[i][6] += j;
                        if(table[i][6].length() == 5){
                            this.winColor = color;
                            isWin=true;
                            return;
                        }
                    }else // occupied or destroyed
                        table[i][5] = "destroy";
                }           
            }
        }
	}
	
	public boolean isWin() {
		return this.isWin;
	}
	
	public String getWinColor() {
		return this.winColor;
	}

	public void resetTable(int[][] g, int w, int h) {
		this.newTable(w, h);
		
		for(int i=0; i<this.width; i++){
            for(int j=0; j<this.height; j++){
                if( g[i][j] != 0 ){
                    this.temp_str = (i<10?"0"+i:""+i) + (j<10?"0"+j:""+j);
                    this.goStep( g[i][j] == 1?"black":"white", temp_str, g);
                }
            }
        }
        isWin = false;
	}
	
}
