package halliGalliServer;

import java.util.*;

public class Room {
	//�� ����
	private String title;
	private String pwd;
	private LinkedHashMap<User, Boolean> roomUserList; // Boolean�� �غ� ���°� ����ȴ�.
	private int state; //0 : ��� ��, 1 : ����
	private int maxPlayers;
	private int roomNo;
	private ServerManager SM;
	
	//�ΰ��� ����
	private boolean isRing;
	private int curTurn;
	private int round;
	private char[] playerState;
	private Vector<CardList> cardLists;
	private Vector<Integer> waitCardList;
	private int[] openCardList;
	private Random random;
	private int[] allCardList = {
			1, 1, 1, 1, 1,
			6, 6, 6, 6, 6,
			11, 11, 11, 11, 11,
			16, 16, 16, 16 ,16, // 1�� ¥�� ����
			
			2, 2, 2,
			7, 7, 7,
			12, 12, 12,
			17, 17, 17,	// 2��¥�� ����
			
			3, 3, 3,
			8, 8, 8,
			13, 13, 13,
			18, 18, 18,	// 3��¥�� ����
			
			4, 4,
			9, 9,
			14, 14,
			19, 19,	// 4��¥�� ����
			
			5, 10, 15, 20	// 5��¥�� ���� 
			}; 
	
	public Room(ServerManager SM, String title, int roomNo) {
		this.roomUserList = new LinkedHashMap<User, Boolean>();
		this.waitCardList = new Vector<Integer>();
		this.cardLists = new Vector<CardList>();
		this.openCardList = new int[4];
		this.playerState = new char[4];
		this.SM = SM;
		this.title = title;
		this.pwd = null;
		this.maxPlayers = 4;
		this.state = 0;
		this.roomNo = roomNo;
		this.isRing = false;
		random = new Random();
		for(int i = 0; i < maxPlayers; i++) {
			cardLists.add(new CardList());
			playerState[i] = '2';
		}
	}
	
	public int AddUser(User user) {
		if(roomUserList.size() == maxPlayers) return -1;
		roomUserList.put(user, false);
		return roomUserList.size();
	}
	
	public void DeleteUser(User user) {
		roomUserList.remove(user);
		if(state==1) {
			state = 0;
			BroadCast("97\t9\t");
		}
		Ready(null, null);
		if(roomUserList.size() <= 0) {
			SM.DeleteRoom(roomNo);
		}
	}
	
	public void SetTitle(String title) {
		this.title = title;
	}
	public String GetTitle() {
		return title;
	}
	
	public void SetPlayers(int players) {
		this.maxPlayers = players;
	}
	
	public int GetPlayers() {
		return roomUserList.size();
	}
	
	public void SetPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String GetPwd() {
		return pwd;
	}
	
	public int GetState() {
		return state;
	}
	
	public void SetRoomNo(int roomNo) {
		this.roomNo = roomNo;
	}
	
	public Boolean CanEnter() {
		if(roomUserList.size() == maxPlayers) return false;
		if(state != 0) return false;
		return true;
	}
	
	public Boolean IsExist(User user) {
		return roomUserList.get(user);
	}
	
	public void Ready(User user, String cmd) {
		int ready = 0;
		if(user != null) {
			//��û�� �޾� Ready���¸� �����Ѵ�.
			if(cmd.equals("0")) {
				roomUserList.put(user, false);
			}
			else if(cmd.equals("1")){
				roomUserList.put(user, true);
			}
		}
		
		//readyCode�� �����Ѵ�.
		String readyCode = "";
		Set<User> temp = roomUserList.keySet();
		Iterator<User> iter = temp.iterator();
		while(iter.hasNext()) {
			if(roomUserList.get(iter.next())) {
				readyCode += "1";
				ready++;
			}
			else {
				readyCode += "0";
			}
		}
		//����ִ� ���ڿ��� ä���ش�.
		while(readyCode.length() != 4) {
			readyCode += "-";
		}
		
		//�� ���� ��� �÷��̾�� Ready �޽����� �����ش�
		BroadCast("17\t" + readyCode);
		
		//��� �÷��̾ �غ� �ϸ� �����Ѵ�.
		if(ready == maxPlayers) {
			//��� �÷��̾��� �غ� ���¸� �����Ѵ�.
			temp = roomUserList.keySet();
			iter = temp.iterator();
			while(iter.hasNext()) {
				roomUserList.put(iter.next(), false);
			}
			StartGame();
		}
		
	}
	
	public void BroadCast(String str) {
		Set<User> temp = roomUserList.keySet();
		Iterator<User> iter = temp.iterator();
		for(int i=0;i<64;i++)
			str=str+"\t";
		//�� ���� ��� �÷��̾�� Ready �޽����� �����ش�
		while(iter.hasNext()) {
			iter.next().SendMsg(str);	
		}
	}
	
	public void SendIDGroup() {
		String IDGroup = "";
		Set<User> temp = roomUserList.keySet();
		Iterator<User> iter = temp.iterator();
		
		for(int i = 0; i < maxPlayers; i++) {
			if(iter.hasNext()) {
				IDGroup = IDGroup + iter.next().GetUserInfo().GetId() + "\t";
			}
			else {
				IDGroup += "-\t";
			}
		}
		
		BroadCast("18\t" + IDGroup);
	}
	
	public void SetRandomCardList() {
		int idx = 0, selectedIdx, n = maxPlayers;
		while(idx != 56) {
			//�������� 0~maxPlayer���� �����Ѵ�.
			selectedIdx = random.nextInt(n);
			//cardList�� �ִ´�.
			PutCard(selectedIdx, allCardList[idx]);
			idx++;
		}
		for(int i = 0; i < n; i++) {
			//���� ī�带 �������� ���´�
			for(int j = 0; j < 50; j++) {
				int temp = cardLists.elementAt(i).Draw(random.nextInt(14));
				cardLists.elementAt(i).Add(temp);
			}
		}
	}
	
	public void PutCard(int idx, int value) {
		while(true) {
			if (cardLists.elementAt(idx).GetSize() < 14) {
				cardLists.elementAt(idx).Add(value);
				return;
			}
			else {
				idx = (idx + 1) % 4;
			}
		}
	}
	
	public void InitGame() {
		state = 1;
		round = 0;
		//1�� ��� ����ִ� ����
		//0�� ��� ���� ����
		for(int i = 0; i < maxPlayers; i++) { 
			playerState[i] = '1';
			cardLists.elementAt(i).Clear();
		}
		curTurn = random.nextInt(4);
		SetRandomCardList();
	}
	
	public void StartGame() {
		InitGame();
		BroadCast("19\t" + curTurn + "\t");
		StartRound();
	}
	
	public void StartRound() {
		round++;
		isRing = false;
		for(int i = 0; i < maxPlayers; i++) { 
			openCardList[i] = 0;
		}
		// �������� ���� ����
		BroadCast("31\t" + round + "\t" + curTurn + "\t");
	}
	
	
	public void DrawCard(String idx) {
		int userIdx = Integer.parseInt(idx);
		// ������ � ī�带 �̾Ҵ��� ����
		int drawCard = cardLists.elementAt(userIdx).Draw();
		int cardCount = cardLists.elementAt(userIdx).GetSize();
		waitCardList.add(drawCard);
		openCardList[userIdx] = drawCard;
		
		// ���� ���� ����� �Ǵ� ��Ȳ���� ����
		DetectRing();
		
		// ���ʸ� �ѱ�
		ChangeTurn();
		BroadCast("33\t" + drawCard + "\t" + curTurn + "\t" + cardCount + "\t");
	}
	
	public void Ring(String idx) {
		int userIdx = Integer.parseInt(idx);
		if(isRing) {
			//�� �÷��̾�� ��� ī�带 �ش�.
			cardLists.elementAt(userIdx).Add(waitCardList);
			waitCardList.clear();
			
			//ī�� ������ �����ش�.
			int cardCount = cardLists.elementAt(userIdx).GetSize();
			BroadCast("35\t1\t" + userIdx + "\t" + cardCount + "\t");
			curTurn = userIdx;
			EndRound(userIdx);
		}
		else {
			GiveCards(userIdx);
			int cardCount0 = cardLists.elementAt(0).GetSize();
			int cardCount1 = cardLists.elementAt(1).GetSize();
			int cardCount2 = cardLists.elementAt(2).GetSize();
			int cardCount3 = cardLists.elementAt(3).GetSize();
			BroadCast("35\t0\t" + userIdx + "\t" + 
					cardCount0 + "\t" +
					cardCount1 + "\t" +
					cardCount2 + "\t" +
					cardCount3 + "\t"
					);
		}
	}
	
	public void EndRound(int userIdx) {
		//������ �������� Ȯ���Ѵ�.
		if(CheckGameEnd()) {
			EndGame();
		}
		else {
			StartRound();
		}
	}
	
	public void EndGame() {
		int win = 0;
		state = 0;
		//�¸��� �÷��̾ ã�´�.
		for(int i = 0; i < maxPlayers; i++) {
			if(playerState[i] == '1') {
				 win = i;
				 break;
			}
		}
		BroadCast("37\t" + win + "\t");
	}
	
	public boolean CheckGameEnd() {
		int count = 0;
		//ī�� ������ 0�� �÷��̾ ã�´�.
		for(int i = 0; i < maxPlayers; i++) {
			if(cardLists.elementAt(i).GetSize() == 0) {
				count++;
				playerState[i] = '0';
			}
		}
		
		if(count == maxPlayers - 1) {
			return true;
		}
		else {
			BroadCast("36\t" + new String(playerState) +  "\t");
			return false;
		}
	}
	
	public void ChangeTurn() {
		// ī�尡 ���� ���� ���ʰ� �־�����.
		while(true) {
			curTurn = (curTurn + 1) % maxPlayers;
			if(cardLists.elementAt(curTurn).GetSize() != 0)
				return;
		}
	}
	
	public void GiveCards(int userIdx) {
		int count = 0;
		while(count < 4) {
			if (cardLists.elementAt(userIdx).GetSize() == 0) {
				if(curTurn == userIdx) {
					// ���ʸ� �ѱ�
					ChangeTurn();
					BroadCast("38\t" + curTurn + "\t");
				}
				break;
			}
			else if(count != userIdx && playerState[count] == '1') 
				cardLists.elementAt(count).Add(cardLists.elementAt(userIdx).Draw());
			count++;
		}
	}
	
	public void DetectRing() {
		int count = 0, b_count = 0, l_count = 0, p_count = 0, s_count = 0; 
		while(count < 4) {
			int value = openCardList[count];
			
			if(0 < value && value <= 5) {
				b_count += value;
				if(b_count == 5) {
					isRing = true;
					return;
				}
				else {
					isRing = false;
				}
			}
			else if (5 < value && value <= 10) {
				l_count += (value - 5);
				if(l_count == 5) {
					isRing = true;
					return;
				}
				else {
					isRing = false;
				}
			}
			else if (10 < value && value <= 15) {
				p_count += (value - 10);
				if(p_count == 5) {
					isRing = true;
					return;
				}
				else {
					isRing = false;
				}
			}
			else if (15 < value && value <= 20) {
				s_count += (value - 15);
				if(s_count == 5) {
					isRing = true;
					return;
				}
				else {
					isRing = false;
				}
			}
			count++;
		}
	}
}

class CardList {
	private Vector<Integer> cardList;
	
	public CardList() {
		cardList = new Vector<Integer>();
	}
	
	public int GetSize() {
		return cardList.size();
	}
	
	public void Add(int value) {
		cardList.add(value);
	}
	
	public void Add(Vector<Integer> list) {
		cardList.addAll(list);
	}
	
	public void Clear() {
		cardList.clear();
	}
	
	public int Draw() {
		int value = cardList.firstElement();
		cardList.remove(0);
		return value;
	}
	
	public int Draw(int i) {
		return cardList.remove(i);
	}
}