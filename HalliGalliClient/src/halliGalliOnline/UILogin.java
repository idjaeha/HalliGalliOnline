package halliGalliOnline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class UILogin extends JFrame {

	private BackGroundLogin PaneLogin;
	private JTextField textLogin;
	private JTextField textPWD;
	private JTextField textIP;
	private Client myClient;
	private ClientManager CM;
	private JLabel notification;
	private JLabel labelIP;
	private JButton btnIP;

	/**
	 * Create the frame.
	 */
	public UILogin(ClientManager CM) {
		this.CM = CM;
		
		setTitle("�Ҹ����� �¶���");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		PaneLogin = new BackGroundLogin();
		PaneLogin.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(PaneLogin);
		PaneLogin.setLayout(null);
		
		labelIP = new JLabel("������");
		labelIP.setBounds(100, 445, 60, 30);
		PaneLogin.add(labelIP);
		
		textIP = new JTextField();
		textIP.setBounds(150, 445, 116, 30);
		PaneLogin.add(textIP);
		textIP.setColumns(10);
		
		btnIP = new JButton("����");
		btnIP.setBounds(580, 445, 97, 30);
		PaneLogin.add(btnIP);
		
		IpAction ipAction = new IpAction();
		btnIP.addActionListener(ipAction);
		textIP.addActionListener(ipAction);
		}
	
	public void Init() {
		textLogin = new JTextField();
		textLogin.setBounds(150, 445, 116, 30);
		PaneLogin.add(textLogin);
		textLogin.setColumns(10);
		
		textPWD = new JTextField();
		textPWD.setBounds(390, 445, 116, 30);
		PaneLogin.add(textPWD);
		textPWD.setColumns(10);
		
		JLabel labelID = new JLabel("���̵�");
		labelID.setBounds(100, 445, 60, 30);
		PaneLogin.add(labelID);
		
		JLabel labelPWD = new JLabel("��й�ȣ");
		labelPWD.setBounds(310, 445, 60, 30);
		PaneLogin.add(labelPWD);
		
		JButton btnLogin = new JButton("�α���");
		btnLogin.setBounds(580, 445, 97, 30);
		PaneLogin.add(btnLogin);
		
		notification = new JLabel("");
		notification.setBounds(50, 30, 196, 61);
		PaneLogin.add(notification);
		
		LoginAction loginAction = new LoginAction();
		btnLogin.addActionListener(loginAction);
		textPWD.addActionListener(loginAction);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
	}
	
	class LoginAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {		
			String _id =	textLogin.getText().trim(); // ������ ���� �𸣴� ���� ���� trim() ���
			String _pwd =	textPWD.getText().trim();
			
			if(_id.equals("") || _pwd.equals("")) {
				notification.setText("���̵�� ��й�ȣ�� �� �� �Է����ּ���.");
				return;
			}
			
			myClient.SendMsg("6" + "\t" + _id + "\t" + _pwd);

			textLogin.setText("");
			textPWD.setText("");
		}
	}
	
	class IpAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {		
			String _ip = textIP.getText().trim();
			myClient = CM.CreateClient(_ip);
			textIP.setVisible(false);
			labelIP.setVisible(false);
			btnIP.setVisible(false);
			Init();
		}
	}
	
	public void SetNotification(String text) {
		notification.setText(text);
	}
}

class BackGroundLogin extends JPanel {
	private Image img;
	private ImageIcon icon;
	
	public BackGroundLogin() {
		icon = new ImageIcon("Image/�α���.png");
		img = icon.getImage();
	}

	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}
}
