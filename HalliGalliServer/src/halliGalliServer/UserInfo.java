package halliGalliServer;

public class UserInfo {
	private User curUser;
	private String id;
	private String pwd;
	private int games;
	private int wins;
	private String profileMsg;
	private String img;
	private int state; // 0 : 비로그인, 1 : 로그인, 2 : 휴식 중, 3 : 게임 대기, 4 : 게임 중
	private Room curRoom;
	
	public UserInfo() {
		wins = 0;
		games = 0;
		state = 0;
		profileMsg = "Hello!";
	}
	
	public String GetPwd() {
		return pwd;
	}
	
	public void SetPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String GetId() {
		return id;
	}
	
	public void SetId(String id) {
		this.id = id;
	}
	
	public void AddWin() {
		wins++;
		games++;
	}
	
	public void AddDefeat() {
		games++;
	}
	
	public int GetState() {
		return this.state;
	}
	
	public void SetState(int state) {
		this.state = state;
	}
	
	public Room GetCurRoom() {
		return curRoom;
	}
	
	public void SetCurRoom(Room room) {
		this.curRoom = room;
	}
	
	public User GetCurUser() {
		return curUser;
	}
	
	public void SetCurUser(User user) {
		this.curUser = user;
	}
	
}
