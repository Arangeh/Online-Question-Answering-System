import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;


public class Gui extends JFrame implements MouseListener , KeyListener{
	private Button Login,SignUp;
	private Text  userNameT  , passwordT ;
	private Label userNameL , passwordL;
	private String userNameS , passwordS;
	public Gui(){
		setSize(800,600);
		setLocation(500,150);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("StackOverFlow");
		setLayout(null);
		userNameL = new Label("UserName");
		userNameL.setSize(80,60);
		userNameL.setLocation(260,100);
		getContentPane().add(userNameL);
		userNameT = new Text(userNameL);
		userNameT.setSize(120,40);
		userNameT.setLocation(350,110);
		getContentPane().add(userNameT);
		passwordL = new Label("Password");
		passwordL.setSize(80,60);
		passwordL.setLocation(260,180);
		getContentPane().add(passwordL);
		passwordT = new Text(passwordL);
		passwordT.setSize(120,40);
		passwordT.setLocation(350,190);
		getContentPane().add(passwordT);		
		Login = new Button("Login");
		Login.setSize(100,40);
		Login.addMouseListener(this);
		Login.setLocation(280,300);
		getContentPane().add(Login);
		SignUp = new Button("SignUp");
		SignUp.setSize(100,40);
		SignUp.addMouseListener(this);
		SignUp.setLocation(430, 300);
		getContentPane().add(SignUp);
		addKeyListener(this);
		setLayout(null);
		setResizable(false);
		setVisible(true);
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
			
			if("Login".equals(getText())){
				userNameS = userNameT.getText();
				passwordS = passwordT.getText();
				new LoginGui(userNameS , passwordS);          
			}
			else if("SignUp".equals(getText())){
		        new SignUpGui();
		     }
		}
	}

	public void setButtonVisible(Boolean set){
		Login.setVisible(set);
		SignUp.setVisible(set);
	}

	public static void main(String[] args) {
		new Gui();
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
		// TODO Auto-generated method stub
		if(arg0.getKeyCode()== KeyEvent.VK_F4){
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	class Text extends JTextField {
		private static final long serialVersionUID = 1L;
		Label j;
		String s ;
		public Text(Label n){
			j = n ;
		}
		
	}
}