import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ApplicationCenter  extends JFrame implements ActionListener {
    private static final long serialVersionUID = 8679886300517958494L;

    private JButton button_goBang;
    private JButton button_paintAssistant;
    
    private JFrame goBang = null;
    private JFrame paintAssistant = null;

    public ApplicationCenter() {

        //frame1 stuff
    	setTitle("ApplicationCenter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300,200);
        setLayout(new FlowLayout());

        //create button
        button_goBang = new JButton("GoBang");
        button_paintAssistant = new JButton("PaintAssistant");
        button_goBang.addActionListener(this);
        button_paintAssistant.addActionListener(this);
        add(button_goBang);
        add(button_paintAssistant);

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	ApplicationCenter  frame = new ApplicationCenter();
                	frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button_goBang) {
            //if (goBang == null)
            goBang = new GoBang();
            if (!goBang.isVisible())
            	goBang.setVisible(true);
        }
        if (e.getSource() == button_paintAssistant) {
            //if (paintAssistant == null)
            paintAssistant = new PaintAssistant();
            if (!paintAssistant.isVisible())
            	paintAssistant.setVisible(true);
        }

    }

}