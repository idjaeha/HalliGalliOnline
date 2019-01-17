package halliGalliOnline;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;

public class UICreateRoom extends JFrame {

	private JPanel contentPane;
	private JTextField textTitle;
	private Client myClient;
	private UICreateRoom myUI;

	/**
	 * Create the frame.
	 */
	public UICreateRoom(Client client) {
		myClient = client;
		myUI = this;
		setTitle("\uBC29 \uC0DD\uC131");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textTitle = new JTextField();
		textTitle.setBounds(80, 50, 116, 30);
		contentPane.add(textTitle);
		textTitle.setColumns(10);
		
		JLabel labelTitle = new JLabel("제목");
		labelTitle.setBounds(20, 50, 57, 30);
		contentPane.add(labelTitle);
		
		JButton btnCreateRoom = new JButton("생성하기");
		btnCreateRoom.setBounds(320, 210, 97, 30);
		contentPane.add(btnCreateRoom);
		
		CreateAction createAction = new CreateAction();
		btnCreateRoom.addActionListener(createAction);
		textTitle.addActionListener(createAction);
	}
	
	class CreateAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {		
			String title =	textTitle.getText().trim(); // 공백이 있지 모르니 공백 제거 trim() 사용
			textTitle.setText("");
			myClient.SendMsg("10" + "\t" + title);
			myUI.setVisible(false);
		}
	}
	
}
