import java.awt.AWTEvent;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class SignUpGui extends JFrame implements MouseListener , KeyListener{
	/**
	 * 
	 */
	private String nameS , userNameS , emailS , passwordS ;
	private static final long serialVersionUID = 1L;
	private Text nameT , userNameT , emailT , passwordT ;
	private Label nameL , userNameL , emailL , passwordL , favoriateL;
	private JRadioButton  android , java , c , php , ios ;
	private Button submit , cancle ;
	static DataBase db;
	public SignUpGui(){
		setSize(800,600);
		setLocation(500,150);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setTitle("Sign_Up");
		nameL = new Label("Name");
		nameL.setSize(80,60);
		nameL.setLocation(50,40);
		getContentPane().add(nameL);
		nameT = new Text(nameL);
		nameT.setSize(120,40);
		nameT.setLocation(150,50);
		getContentPane().add(nameT);
		userNameL = new Label("UserName");
		userNameL.setSize(80,60);
		userNameL.setLocation(50,100);
		getContentPane().add(userNameL);
		userNameT = new Text(userNameL);
		userNameT.setSize(120,40);
		userNameT.setLocation(150,110);
		getContentPane().add(userNameT);
		emailL = new Label("Email");
		emailL.setSize(80,60);
		emailL.setLocation(50,160);
		getContentPane().add(emailL);
		emailT = new Text(emailL);
		emailT.setSize(120,40);
		emailT.setLocation(150,170);
		getContentPane().add(emailT);
		passwordL = new Label("Password");
		passwordL.setSize(80,60);
		passwordL.setLocation(50,220);
		getContentPane().add(passwordL);
		passwordT = new Text(passwordL);
		passwordT.setSize(120,40);
		passwordT.setLocation(150,230);
		getContentPane().add(passwordT);
		favoriateL = new Label("Favorites");
		favoriateL.setSize(80,60);
		favoriateL.setLocation(50,280);
		getContentPane().add(favoriateL);
		android = new JRadioButton("Android");
		android.setSize(80,40);
		android.setLocation(100,340);
		getContentPane().add(android);
		java = new JRadioButton("Java");
		java.setSize(60,40);
		java.setLocation(200,340);
		getContentPane().add(java);
		c = new JRadioButton("C");
		c.setSize(60,40);
		c.setLocation(300,340);
		getContentPane().add(c);
		php = new JRadioButton("php");
		php.setSize(60,40);
		php.setLocation(400,340);
		getContentPane().add(php);
		ios = new JRadioButton("ios");
		ios.setSize(60,40);
		ios.setLocation(500,340);
		getContentPane().add(ios);
		submit = new Button("Submit",this);
		submit.setSize(80,40);
		submit.setLocation(60,400);
		getContentPane().add(submit);
		cancle = new Button("Cancle",this);
		cancle.setSize(80,40);
		cancle.setLocation(180,400);
		getContentPane().add(cancle);
		setVisible(true);
	}
	
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
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

	class Text extends JTextField {
		private static final long serialVersionUID = 1L;
		Label j;
		String s ;
		public Text(Label n){
			j = n ;
		}
		
	}


class Button extends JButton implements ActionListener{
	private static final long serialVersionUID = 1L;
	SignUpGui m ;
	public Button(String s , SignUpGui n ){
		super(s);
		m = n ;
		addActionListener(this);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if("Cancle".equals(getText()))
			dispose();
		if("Submit".equals(getText())){
			nameS = nameT.getText();
			userNameS = userNameT.getText();
			emailS = emailT.getText();
			passwordS = passwordT.getText();
			if(nameS.equals("")){
				System.out.println("Enter Name Pls");
			}
			if(userNameS.equals("")){
				System.out.println("Enter UserName Pls");
			}
			if(emailS.equals("")){
				System.out.println("Enter Email Pls");
			}
			if(passwordS.equals("")){
				System.out.println("Enter PassWord Pls");
			}
			if(!nameS.equals("") && !passwordS.equals("") && !emailS.equals("") && !userNameS.equals("")){

				/*if(userName){
					// Go login page
					dispose();
				}*/
			}
			
			
		}
	}
}
	
}


