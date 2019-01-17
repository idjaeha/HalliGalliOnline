package halliGalliOnline;

import java.awt.BorderLayout;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.JLabel;
import java.util.*;

public class UIGameList extends JFrame {
	private Background contentPane;
	private Client myClient;
	private ClientManager CM;
	private JList roomList;
	private Vector<Integer> roomInfo;
	private JScrollPane scrollPane;
	private UICreateRoom UICR;
	private ClickList clickList;

	/**
	 * Create the frame.
	 */
	public UIGameList(ClientManager CM) {
		this.setTitle("할리갈리 온라인");
		this.CM = CM;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new Background();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnCreateRoom = new JButton("방 생성");
		btnCreateRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCreateRoom.setBounds(530, 480, 100, 30);
		contentPane.add(btnCreateRoom);
		
		JButton btnExit = new JButton("나가기");
		btnExit.setBounds(650, 480, 100, 30);
		contentPane.add(btnExit);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(24, 24, 400, 500);
		contentPane.add(scrollPane);
		
		JButton btnRefresh = new JButton("방 목록 새로고침");
		scrollPane.setColumnHeaderView(btnRefresh);
		
		CreateAction createAction = new CreateAction();
		btnCreateRoom.addActionListener(createAction);
		
		RefreshAction refreshAction = new RefreshAction();
		btnRefresh.addActionListener(refreshAction);
		
		ExitAction exitAction = new ExitAction();
		btnExit.addActionListener(exitAction);
		
		clickList = new ClickList();
	}	
	
	public void SetClient(Client client) {
		myClient = client;
		UICR = new UICreateRoom(myClient);
	}
	
	class CreateAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			UICR.setVisible(true);
		}
	}
	
	class ExitAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			myClient.SendMsg("8\t");
		}
	}
	
	class ClickList extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				int idx = roomList.getSelectedIndex();
				int roomNo = roomInfo.elementAt(idx);
				myClient.SendMsg("12\t" + roomNo + "\t");
			}
		}
	}

	
	class RefreshAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {		
			myClient.SendMsg("14");
		}
	}
	
	public void RefreshList(String[] msgArr) {
		int num = Integer.parseInt(msgArr[1]);
		String title, curPlayers, pwd, state, result = "";
		String[] rawRoomInfo;
		Vector<String> temp = new Vector<String>();
		roomInfo = new Vector<Integer>();
		for(int i = 2, count = 0; count < num; i+=2, count++) {
			roomInfo.add(Integer.parseInt(msgArr[i+1]));
			rawRoomInfo = msgArr[i].split("<<");
			title = rawRoomInfo[0];
			curPlayers = rawRoomInfo[1];
			state = rawRoomInfo[3];
			if(state.equals("0")) {
				state = "게임 대기 중";
			}
			else if (state.equals("1")) {
				state = "게임 진행 중";
			}
			result = String.format("[%s/4]%-50s%s", curPlayers, title, state);
			temp.add(result);
		}
		roomList = new JList(temp);
		roomList.addMouseListener(clickList);
		scrollPane.setViewportView(roomList);
	
	}
	
}

class Background extends JPanel {
	private Image img;
	private ImageIcon icon;
	
	public Background() {
		icon = new ImageIcon("Image/게임방.png");
		img = icon.getImage();
	}

	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}
}

