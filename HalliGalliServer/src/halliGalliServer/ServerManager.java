package halliGalliServer;

import javax.swing.*;
import java.util.*;

public class ServerManager {
	private UserCreator UC;
	private Vector<User> userList;
	private JFrame curView;
	private HashMap<String, UserInfo> userInfoMap; //아이디와 유저 정보를 저장한 해쉬맵
	private HashMap<Integer, Room> roomList; //방 번호와 방 객체를 저장하는 해쉬맵
	private int roomNo; // 방 고유번호, 다른 방 생성과 삭제에 의하여 변경되지 않음
	
	public ServerManager() {
		Init();
	}
	
	public void Init() {
		curView = (UIMain)new UIMain();
		curView.setVisible(true);
		UC = new UserCreator(this);
		userList = new Vector<User>();
		userInfoMap = new HashMap<String, UserInfo>();
		roomList = new HashMap<Integer, Room>();
		roomNo = 1;
	}
	
	public void AddUser(User usr) {
		userList.add(usr);
	}
	
	public void DeleteUser(User usr) {
		userList.remove(usr);
	}
	
	
	public Vector<User> GetUserList() {
		return userList;
	}
	
	public int GetUserListSize() {
		return userList.size();
	}
	
	public void AddUIText(String text) {
		((UIMain)curView).AddText(text);
	}
	
	public void AddUser(String id, UserInfo userInfo) {
		userInfoMap.put(id, userInfo);
	}
	
	public String GetPWD(String id) {
		UserInfo temp = userInfoMap.get(id);
		if(temp == null) 
			return null;
		String PWD = temp.GetPwd();
		return PWD;
	}
	
	public UserInfo GetUserInfo(String id) {
		UserInfo temp = userInfoMap.get(id);
		return temp;
	}
	
	public int CreateRoom(User user, String title) {
		Room temp = new Room(this, title, roomNo);
		roomList.put(roomNo, temp);
		EnterRoom(user, roomNo);
		return roomNo++;
	}
	
	public int EnterRoom(User user, int roomNo) {
		Room temp = roomList.get(roomNo);
		//방이 없으면 -1
		if(temp == null) return -1;
		if(temp.CanEnter() == true) {
			temp.AddUser(user);
			user.GetUserInfo().SetCurRoom(temp);
			return roomNo;
		}
		else return -1;
		
	}
	
	public int GetRoomSize() {
		return roomList.size();
	}
	
	public void ExitRoom(int roomNo, User user) {
		Room temp =  roomList.get(roomNo);
		if(temp == null) return;
		user.GetUserInfo().SetCurRoom(null);
		temp.DeleteUser(user);
		temp.SendIDGroup();
	}
	
	public void ExitRoom(User user) {
		Room temp = user.GetUserInfo().GetCurRoom();
		if(temp == null) {
			return;
		}
		temp.DeleteUser(user);
		temp.SendIDGroup();
	}
	
	public void DeleteRoom(int roomNo) {
		roomList.remove(roomNo);
	}
	
	public String GetRoomInfoToString() {
		String temp = "";
		Set<Integer> keys = roomList.keySet();
		Iterator<Integer> it = keys.iterator();
		while(it.hasNext()) {
			int key = it.next();
			String value = roomList.get(key).GetTitle() + "<<";
			value += roomList.get(key).GetPlayers() + "<<";
			value += roomList.get(key).GetPwd() + "<<";
			value += roomList.get(key).GetState();
			temp += value + "\t" + Integer.toString(key) + "\t";
		}
		return temp;
	}
}
