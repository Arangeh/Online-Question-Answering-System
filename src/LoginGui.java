import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;


public class LoginGui extends JFrame implements KeyListener , MouseListener{
	private Button logout;
	private Button Add,Search,ChangePicture;
	static String username;
	static String password;
	public LoginGui(String username , String password){
		setSize(800,600);
		setLocation(500,150);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(username);
		setLayout(null);
		this.password = password;
		this.username = username;
		logout = new Button("Logout");
		logout.setSize(100,40);
		logout.addMouseListener(this);
		logout.setLocation(650,10);
		getContentPane().add(logout);
		Add = new Button("Add");
		Add.setSize(120,40);
		Add.addMouseListener(this);
		Add.setLocation(320,150);
		getContentPane().add(Add);		
		Search = new Button("Search");
		Search.setSize(120,40);
		Search.addMouseListener(this);
		Search.setLocation(320,230);
		getContentPane().add(Search);	
		ChangePicture = new Button("Change Picture");
		ChangePicture.setSize(120,40);
		ChangePicture.addMouseListener(this);
		ChangePicture.setLocation(320,310);
		getContentPane().add(ChangePicture);		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new LoginGui(username , password);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-g	enerated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	class Button extends JButton implements ActionListener{
		private static final long serialVersionUID = 1L;
		public Button(String s){
			super(s);
			addActionListener(this);
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if("Logout".equals(getText())){
				dispose();  
			}
		}
	}
	
}