package halliGalliOnline;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.*;
import javax.swing.JLabel;
import java.util.*;

public class UIGame extends JFrame implements Runnable{

	private Board contentPane;
	private JTextField inputText;
	private JTextPane textPane;
	private Client myClient;
	private ClientManager CM;
	JButton btnL;
	JButton btnR;
	private Vector<JLabel> playersName;
	private Vector<JLabel> playersCount;
	private Vector<Card> playersCard;
	private Card turn;
	private String stateCode = "----";
	private String[] idCode = {null, "-", "-", "-", "-"};
	private int ready;
	boolean isStart = false;
	boolean isAlive = true;
	private String myId;
	private int myIdx = 0;
	private int curTurn = 0;
	private Vector<ImageIcon> iconList;
	private Result result;
	private Bell bell;
	private JLabel round;
	int gamecnt = 0;
	final int maxPlayers = 4;

	/**
	 * Create the frame.
	 */
	public UIGame(ClientManager CM) {
		this.setTitle("�Ҹ����� �¶���");
		this.CM = CM;
		this.setResizable(false);
		InitIconList();
		playersName = new Vector<JLabel>();
		playersCount = new Vector<JLabel>();
		playersCard = new Vector<Card>();
		turn = new Card(this);
		result = new Result(this);
		bell = new Bell(this);
		round = new JLabel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new Board(this);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		for(int i = 0; i < maxPlayers; i++) {
			Card temp = new Card(this);
			temp.setBounds(35 + 180 * i, 60, 160, 180);
			playersCard.add(temp);
			contentPane.add(temp);
		}
		
		turn.setBounds(90, 20, 50, 50);
		turn.SetImage(24);
		contentPane.add(turn);
		turn.setVisible(false);
		
		result.setBounds(200, 200, 400, 200);
		contentPane.add(result);
		result.setVisible(false);
		
		bell.setBounds(325, 400, 100, 100);
		contentPane.add(bell);
		bell.SetImage(49);
		bell.setVisible(true);
		
		round.setBounds(90, 350, 400, 200);
		round.setText("");
		round.setForeground(Color.YELLOW);
		round.setFont(new Font("�޸�����ü", Font.PLAIN, 40));
		contentPane.add(round);
		round.setVisible(true);
		
		for(int i = 0; i < maxPlayers; i++) {
			JLabel temp = new JLabel("name");
			temp.setBounds(90 + 180 * i, 370, 80, 20);
			temp.setForeground(Color.WHITE);
			temp.setFont(new Font("�޸�����ü", Font.PLAIN, 23));
			playersName.add(temp);
			contentPane.add(temp);
		}
		
		for(int i = 0; i < maxPlayers; i++) {
			JLabel temp = new JLabel("name");
			temp.setBounds(90 + 180 * i, 320, 80, 20);
			temp.setForeground(Color.WHITE);
			temp.setFont(new Font("�޸�����ü", Font.PLAIN, 23));
			playersCount.add(temp);
			contentPane.add(temp);
			temp.setVisible(false);
		}
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(470, 430, 280, 80);
		contentPane.add(scrollPane);
		
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		
		inputText = new JTextField();
		inputText.setBounds(470, 510, 280, 20);
		contentPane.add(inputText);
		inputText.setColumns(10);
		
		MsgSendAction sendAction = new MsgSendAction();
		inputText.addActionListener(sendAction);
		
		btnL = new JButton("�غ�");
		btnL.setBounds(40, 510, 100, 20);
		//contentPane.add(btnL);
		
		btnR = new JButton("������");
		btnR.setBounds(50, 500, 100, 20);
		contentPane.add(btnR);
		
		BtnRAction btnRAction = new BtnRAction();
		btnR.addActionListener(btnRAction);
		
		BtnLAction btnLAction = new BtnLAction();
		btnL.addActionListener(btnLAction);
		
		contentPane.addKeyListener(new GameKeyListener());
	}
	
	public void SetClient(Client client) {
		myClient = client;
	}
	
	public void Focus() {
		contentPane.setFocusable(true);
		contentPane.requestFocus();
	}
	
	public void SetRound(String num) {
		if(num == null) {
			round.setText("");
		}
		else {
			String str = num + " ����";
			round.setText(str);
		}
	}
	
	public void ShowResult(String index) {
		int idx = Integer.parseInt(index);
		if(myIdx == idx) {
			result.StartEnding(47);
		}
		else {
			result.StartEnding(48);
		}
	}
	
	@Override
	public void run() {
		while(true) {
			if(CM.GetStage() == 2) {
				gamecnt++;
				repaint();
				try {
					Thread.sleep(50);
				}
				catch(InterruptedException e) {
					return;
				}
			}
			else {
				Thread.yield();
			}
			
		}
	}
	
	class GameKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			//�غ�
			if(e.getKeyCode() == KeyEvent.VK_F5) {
				if(!isStart) {
					if(ready == 0) {
						myClient.SendMsg("16\t1\t");
						ready = 1;
					}
					else {
						myClient.SendMsg("16\t0\t");
						ready = 0;
					}
				}
			}
			
			//������
			if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
				if(isStart && myIdx == curTurn) {
					myClient.SendMsg("32\t" + myIdx + "\t");
				}
			}
			
			//�� �︮��
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				if(isStart && isAlive) {
					myClient.SendMsg("34\t" + myIdx + "\t");
				}
			}
			
			//������
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if(!isStart) {
					CM.ChangeView(1);
					myClient.SendMsg("15\t" + myClient.GetRoomNo() + "\t");
					myClient.SendMsg("14");
					myClient.SetRoomNo(0);
					ready = 0;
				}
			}
			
			//ä��
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				contentPane.setFocusable(false);
				inputText.setFocusable(true);
				inputText.requestFocus();
			}
		}
	}
	
	public void AppendMessage(String id, String msg) {			
		String str = id + " : " + msg + "\n";
		int len = textPane.getDocument().getLength();
		textPane.setCaretPosition(len);
		textPane.replaceSelection(str);
	}
	
	public void Update() {
		FindMyIndex();
		SetPlayersId();
		SetPlayersState();
	}
	
	public void SetStateCode(String code) {
		stateCode = code;
	}
	
	public void SetPlayersState() {
		for(int i = 0; i < 4; i++) {
			if(stateCode.charAt(i) == '0') {
				playersCard.elementAt(i).SetImage(22);
			}
			else if(stateCode.charAt(i) == '1'){
				playersCard.elementAt(i).SetImage(23);
			}
			else if(stateCode.charAt(i) == '-') {
				playersCard.elementAt(i).SetImage(22);
			}
		}
	}
	
	public void SetIdCode(String[] code) {
		idCode = code;
	}
	
	public void SetPlayersId() {
		for(int i = 0; i < 4; i++) {
			if(idCode[i+1].equals("-")) {
				playersName.elementAt(i).setText("");
			}
			else {
				playersName.elementAt(i).setText(idCode[i+1]);
			}
		}
	}
	
	public void StartGame() {
		isStart = true;
		isAlive = true;
		//���� ���̰� �ϱ�
		turn.setVisible(true);
		
		//ī�� ���� �ʱ�ȭ�� ���̰� �ϱ�
		for(int i = 0; i < maxPlayers; i++) {
			playersCount.elementAt(i).setText("14");
			playersCount.elementAt(i).setVisible(true);
		}
	}
	
	public void EndGame() {
		isStart = false;
		ready = 0;
		
		//���� �����
		turn.setVisible(false);
		
		//ī�� ���� �ʱ�ȭ�� �����
		for(int i = 0; i < maxPlayers; i++) {
			playersCount.elementAt(i).setText("14");
			playersCount.elementAt(i).setVisible(false);
		}
	}
	
	public void SetMyId(String id) {
		this.myId = id;
	}
	
	public void FindMyIndex() {
		for(int i = 0; i < 4; i++) {
			if(idCode[i+1].equals(myId)) {
				this.myIdx = i;
				return;
			}
		}
	}
	
	public void SetCurTurn(String userIdx) {
		int idx = Integer.parseInt(userIdx);
		curTurn = idx;
		turn.setBounds(90 + 180 * idx, 20, 50, 50);
	}
	
	public void SetPlayerCount(String count) {
		//ī�� ������ �������ش�.
		playersCount.elementAt(curTurn).setText(count);
	}
	
	public void SetPlayerCount(String count, String idx) {
		int userIdx = Integer.parseInt(idx);
		//ī�� ������ �������ش�.
		playersCount.elementAt(userIdx).setText(count);
	}
	
	public void SetPlayerCount(String[] count) {
		//ī�� ������ �������ش�.
		for(int i = 0; i < maxPlayers; i++) {
			playersCount.elementAt(i).setText(count[i+3]);
		}
	}
	
	public void SetCardAni(String cardIdx, String userIdx) {
		//�ش��ϴ� ������ ī�� �̹����� �������ش�.
		int cardIndex = Integer.parseInt(cardIdx);
		playersCard.elementAt(curTurn).PlayAni(cardIndex);
		
		//���� ���ʷ� �Ѿ��.
		SetCurTurn(userIdx);
	}
	
	public void SetCardImage(String userCode) {
		//�ش��ϴ� ������ ī�� �̹����� �������ش�.
		for(int i = 0; i < userCode.length(); i++) {
			if(userCode.charAt(i) == '0')
				playersCard.elementAt(i).SetImage(25);
			else
				playersCard.elementAt(i).SetImage(0);
		}
	}
	
	public void SetCardImage(int cardIdx) {
		for(int i = 0; i < maxPlayers; i++) {
			playersCard.elementAt(i).SetImage(cardIdx);
		}
	}
	
	public void KillPlayer(String code) {
		for(int i = 0; i < maxPlayers; i++) {
			if(code.charAt(i) == '0') {
				if(myIdx == i) {
					isAlive = false;
				}
			}
		}
	}
	
	public void StartRing() {
		bell.StartRing();
	}
	
	public void ChangeBtn() {
		if(!isStart) {
			btnR.setVisible(true);
		}
		else {
			btnR.setVisible(false);
		}
	}
	
	public void InitIconList() {
		iconList = new Vector<ImageIcon>();
		//0
		iconList.add(new ImageIcon("Image/CardBack.jpg")); 
		
		//1~5
		iconList.add(new ImageIcon("Image/Banana_1.jpg"));
		iconList.add(new ImageIcon("Image/Banana_2.jpg"));
		iconList.add(new ImageIcon("Image/Banana_3.jpg"));
		iconList.add(new ImageIcon("Image/Banana_4.jpg"));
		iconList.add(new ImageIcon("Image/Banana_5.jpg"));
		
		//6~10
		iconList.add(new ImageIcon("Image/Lemon_1.jpg"));
		iconList.add(new ImageIcon("Image/Lemon_2.jpg"));
		iconList.add(new ImageIcon("Image/Lemon_3.jpg"));
		iconList.add(new ImageIcon("Image/Lemon_4.jpg"));
		iconList.add(new ImageIcon("Image/Lemon_5.jpg"));
		
		//11~15
		iconList.add(new ImageIcon("Image/Peach_1.jpg"));
		iconList.add(new ImageIcon("Image/Peach_2.jpg"));
		iconList.add(new ImageIcon("Image/Peach_3.jpg"));
		iconList.add(new ImageIcon("Image/Peach_4.jpg"));
		iconList.add(new ImageIcon("Image/Peach_5.jpg"));
		
		//16~20
		iconList.add(new ImageIcon("Image/Straw_1.jpg"));
		iconList.add(new ImageIcon("Image/Straw_2.jpg"));
		iconList.add(new ImageIcon("Image/Straw_3.jpg"));
		iconList.add(new ImageIcon("Image/Straw_4.jpg"));
		iconList.add(new ImageIcon("Image/Straw_5.jpg"));
		
		//21~25
		iconList.add(new ImageIcon("Image/Board3.jpg"));
		iconList.add(new ImageIcon("Image/���.png"));
		iconList.add(new ImageIcon("Image/�غ�.png"));
		iconList.add(new ImageIcon("Image/����.png"));
		iconList.add(new ImageIcon("Image/Cemetry.png"));
		
		//26~30
		iconList.add(new ImageIcon("Image/�ٳ��� 1 ������.png"));
		iconList.add(new ImageIcon("Image/�ٳ��� 2 ������.png"));
		iconList.add(new ImageIcon("Image/�ٳ��� 3 ������.png"));
		iconList.add(new ImageIcon("Image/�ٳ��� 4 ������.png"));
		iconList.add(new ImageIcon("Image/�ٳ��� 5 ������.png"));
		
		//31~35
		iconList.add(new ImageIcon("Image/���� 1 ������.png"));
		iconList.add(new ImageIcon("Image/���� 2 ������.png"));
		iconList.add(new ImageIcon("Image/���� 3 ������.png"));
		iconList.add(new ImageIcon("Image/���� 4 ������.png"));
		iconList.add(new ImageIcon("Image/���� 5 ������.png"));
		
		//36~40
		iconList.add(new ImageIcon("Image/��ġ 1 ������.png"));
		iconList.add(new ImageIcon("Image/��ġ 2 ������.png"));
		iconList.add(new ImageIcon("Image/��ġ 3 ������.png"));
		iconList.add(new ImageIcon("Image/��ġ 4 ������.png"));
		iconList.add(new ImageIcon("Image/��ġ 5 ������.png"));
		
		//41~45
		iconList.add(new ImageIcon("Image/���� 1 ������.png"));
		iconList.add(new ImageIcon("Image/���� 2 ������.png"));
		iconList.add(new ImageIcon("Image/���� 3 ������.png"));
		iconList.add(new ImageIcon("Image/���� 4 ������.png"));
		iconList.add(new ImageIcon("Image/���� 5 ������.png"));
		
		//46~50
		iconList.add(new ImageIcon("Image/�޸� ������.png")); 
		iconList.add(new ImageIcon("Image/�¸�.png"));
		iconList.add(new ImageIcon("Image/�й�.png"));
		iconList.add(new ImageIcon("Image/�� ����.png"));
		iconList.add(new ImageIcon("Image/�� ���� �︲.png"));
	}
	
	public ImageIcon GetImageIcon(int idx) {
		ImageIcon temp = iconList.get(idx);
		return temp;
	}
	
	class BtnRAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(!isStart) {
				CM.ChangeView(1);
				myClient.SendMsg("15\t" + myClient.GetRoomNo() + "\t");
				myClient.SendMsg("14");
				myClient.SetRoomNo(0);
				ready = 0;
			}
			else {
				if(isAlive)
					myClient.SendMsg("34\t" + myIdx + "\t");
			}
			Focus();
		}
	}
	
	class BtnLAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(!isStart) {
				if(ready == 0) {
					myClient.SendMsg("16\t1\t");
					ready = 1;
				}
				else {
					myClient.SendMsg("16\t0\t");
					ready = 0;
				}
			}
			else {
				if(myIdx == curTurn) {
					myClient.SendMsg("32\t" + myIdx + "\t");
				}
			}
			Focus();
		}
	}
	
	class MsgSendAction implements ActionListener // ����Ŭ������ �׼� �̺�Ʈ ó�� Ŭ����
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			// �׼� �̺�Ʈ�� sendBtn�϶� �Ǵ� textField ���� Enter key ġ��
			if (e.getSource() == inputText) 
			{
				if(!inputText.getText().equals("")) {
					String msg = null;
					msg = String.format("1\t" +myId +  "\t" +  inputText.getText() + "\t");
					myClient.SendMsg(msg);
					inputText.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
				}
			}
			Focus();
		}

	}

}

class Board extends JPanel {
	private UIGame gameroom;
	private ImageIcon icon;
	private Image img;
	
	public Board(UIGame gameroom) {
		this.gameroom = gameroom;
		icon = gameroom.GetImageIcon(21);
		img = icon.getImage();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}
}

class Card extends JLabel {
	private UIGame gameroom;
	private ImageIcon icon;
	private Image img;
	private int imgSX, imgSY, imgEX, imgEY;
	private boolean isAni;
	private int nowgamecnt;
	private int aniIdx;
	
	public Card(UIGame gameroom) {
		this.gameroom = gameroom;
		icon = gameroom.GetImageIcon(0);
		img = icon.getImage();
		isAni = false;
		imgSX = imgSY = 0;
		imgEX = 160;
		imgEY = 200;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(!isAni)
			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		else {
			if(nowgamecnt + 10 <= gameroom.gamecnt) {
				icon = gameroom.GetImageIcon(aniIdx - 25);
				img = icon.getImage();
				imgEX = 100;
				imgEY = 150;
				isAni = false;
			}
			
			if(nowgamecnt + 5 == gameroom.gamecnt) {
				icon = gameroom.GetImageIcon(aniIdx);
				img = icon.getImage();
			}
			
			g.drawImage(img, 0, 0, getWidth(), getHeight(),
					((gameroom.gamecnt - nowgamecnt) % 5 ) * 100 , 0,
					((gameroom.gamecnt - nowgamecnt) % 5 + 1) * 100, 150, this);

		}
	}
	
	public void SetImage(int idx) {
		icon = gameroom.GetImageIcon(idx);
		img = icon.getImage();
	}
	
	public void PlayAni(int idx) {
		//�޸鿡������ ���ڷ� ���� �̹������� �ִϸ��̼��� ����մϴ�.
		nowgamecnt = gameroom.gamecnt;
		aniIdx = idx + 25;
		icon = gameroom.GetImageIcon(46);
		img = icon.getImage();
		isAni = true;
	}
}

class Result extends JLabel {
	private UIGame gameroom;
	private ImageIcon icon;
	private Image img;
	private int ending;
	private boolean isEnding;
	
	public Result(UIGame gameroom) {
		this.gameroom = gameroom;
		icon = gameroom.GetImageIcon(47);
		img = icon.getImage();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		if(isEnding && ending <= gameroom.gamecnt)
			this.setVisible(false);
	}
	
	public void SetImage(int idx) {
		icon = gameroom.GetImageIcon(idx);
		img = icon.getImage();
	}
	
	public void StartEnding(int idx) {
		SetImage(idx);
		this.setVisible(true);
		isEnding = true;
		ending = gameroom.gamecnt + 20;
	}
}

class Bell extends JLabel {
	private UIGame gameroom;
	private ImageIcon icon;
	private Image img;
	private int ring;
	private boolean isRing;
	
	public Bell(UIGame gameroom) {
		this.gameroom = gameroom;
		icon = gameroom.GetImageIcon(47);
		img = icon.getImage();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		if(isRing && ring <= gameroom.gamecnt) {
			SetImage(49);
			isRing = false;
		}
	}
	
	public void SetImage(int idx) {
		icon = gameroom.GetImageIcon(idx);
		img = icon.getImage();
	}
	
	public void StartRing() {
		SetImage(50);
		ring = gameroom.gamecnt + 10;
		isRing = true;
	}
}