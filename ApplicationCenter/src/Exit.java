import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
 * exit the game and close the dialog
 */
class Exit extends WindowAdapter{
    public void windowClosing(WindowEvent e){
        System.exit(0);
    }
}