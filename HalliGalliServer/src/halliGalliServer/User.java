package halliGalliServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

//client당 하나씩 실행되는 object thread
class User extends Thread {
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket mySocket;
	private Vector<User> userList;
	private ServerManager SM;
	private UserInfo userInfo;

	public User(Socket soc, ServerManager SM) // 생성자메소드
	{
		Init(soc, SM);
		Connect();
	}
	
	private void Init(Socket soc, ServerManager SM) {
		this.mySocket = soc;
		this.SM = SM;
		this.userList = SM.GetUserList();
		this.userInfo = null;
	}
	public void Connect() {
		try {
			is = mySocket.getInputStream();
			dis = new DataInputStream(is);
			os = mySocket.getOutputStream();
			dos = new DataOutputStream(os);
			byte[] b=new byte[128];
			dis.read(b); // ip를 수신.
			String ip = new String(b);
			ip = ip.trim();
			SM.AddUIText("IP :" + ip + " 접속");
		} catch (Exception e) {
			SM.AddUIText("스트림 셋팅 에러");
		}
	}
	
	public UserInfo GetUserInfo() {
		return userInfo;
	}
	
	public void Login(String id, String pwd) {
		id = id.trim();
		pwd = pwd.trim();
		String text;
		String msg;
		//아이디를 새로만들었을 때
		if(SM.GetPWD(id) == null) {
			userInfo = new UserInfo();
			userInfo.SetId(id);
			userInfo.SetPwd(pwd);
			userInfo.SetState(1);
			userInfo.SetCurUser(this);
			SM.AddUser(id, userInfo);
			//대기 방에 입장하는 코드 추가
			text = "새로운 멤버 " + id + "님이 게임에 접속하셨습니다.";
			msg = "7\t0\t";
		}
		//기존에 있던 아이디로 접속했을 때
		else if(SM.GetPWD(id).equals(pwd)) {
			UserInfo temp = SM.GetUserInfo(id);
			//로그인 되어있는 상태일 경우
			if(temp.GetState() == 1) {
				text = id + "님이 같은 아이디로 접속을 시도했습니다.";
				msg = "7\t3\t";
			}
			//정상 로그인
			else {
				userInfo = temp;
				userInfo.SetState(1);
				userInfo.SetCurUser(this);
				//대기 방에 입장하는 코드 추가
				text = id + "님이 접속하셨습니다.";
				msg = "7\t1\t";
			}
		}
		else if(!SM.GetPWD(id).equals(pwd)){
			text = id + "님 접속 실패하였습니다.";
			msg = "7\t2\t";
		}
		else {
			return;
		}
		try {
			msg = msg + id;
			byte[] b = msg.getBytes("euc-kr");
			SM.AddUIText(text);
			SendMsg(msg); // 연결된 사용자에게 정상접속을 알림
			return;
		}
		
		catch (IOException e){
			return;
		}


	}
	
	public void SendMsg(String str) {
		try {
			byte[] bb = new byte[64];
			String s = String.format("%-64s",str);
			bb = s.getBytes("euc-kr");
			dos.write(bb);
			SM.AddUIText("SEND : " + s);
		} 
		catch (IOException e) {
			SM.AddUIText("메세지 전송 실패");
		}
	}
	
	//client당 하나씩 돌고 있는 Thread
	public void run() // 스레드 정의
	{

		while (true) {
			try {
				// 사용자에게 받는 메세지
				byte[] b = new byte[64];
				dis.read(b);//client 메시지 수신. 무한 대기.
				String msg = new String(b);
				String id = userInfo==null? null : userInfo.GetId().trim();
				SM.AddUIText(String.format("RECV : [%s] >> %s", id ,msg));
				ExecMsg(msg);
			} 
			catch (IOException e) 
			{
				
				try {
					dos.close();
					dis.close();
					mySocket.close();
					userInfo.SetState(0);
					SM.ExitRoom(this);
					userList.removeElement( this ); // 에러가 발생한 현재 객체를 벡터에서 지운다
					SM.AddUIText(userList.size() +" : 현재 벡터에 담겨진 사용자 수");
					SM.AddUIText("사용자 접속 끊어짐 자원 반납");
					break;
				
				} catch (Exception ee) {
				
				}// catch문 끝
			}// 바깥 catch문끝

		}
	}// run메소드 끝
	
	public void ExecMsg(String msg) {
		String[] msgArr = msg.split("\t");
		String cmd = msgArr[0].trim();
		
		if(cmd == null)
			return;
		//로그인 전송
		else if(cmd.equals("6")) {
			String id = msgArr[1].trim();
			String pwd = msgArr[2].trim();
			Login(id, pwd);
		}
		//로그아웃
		else if(cmd.equals("8")) {
			userInfo.SetState(0);
			SendMsg(msg);
		}
		//방 생성
		else if(cmd.equals("10")) {
			String roomTitle = msgArr[1].trim();
			int roomNo = SM.CreateRoom(this, roomTitle);
			//방 생성 실패
			if( roomNo == -1) {
				msg = "11\t1\t";
				SendMsg(msg);
			}
			else {
				msg = "11\t0\t" + roomNo + "\t" + userInfo.GetId() + "\t";
				SendMsg(msg);
				userInfo.GetCurRoom().SendIDGroup();
			}
		}
		//방 참가
		else if(cmd.equals("12")) {
			int temp = SM.EnterRoom(this, Integer.parseInt(msgArr[1]));
			//방 참가 실패
			if(temp == -1) {
				msg = "13\t1";
				SendMsg(msg);
			}
			//방 참가 성공
			else {
				msg = "13\t0\t" + temp + "\t" + userInfo.GetId() + "\t";
				SendMsg(msg);
				userInfo.GetCurRoom().SendIDGroup();
			}
		}
		//방 새로고침 응답
		else if(cmd.equals("14")) {
			//방 갯수와 뒤에 방제목과 방번호를 보내준다.
			msg = "14\t" + SM.GetRoomSize() + "\t";
			msg = msg + SM.GetRoomInfoToString();
			SendMsg(msg);
		}
		//방 나가기
		else if(cmd.equals("15")) {
			int roomNo = Integer.parseInt(msgArr[1]); 
			SM.ExitRoom(roomNo, this);
		}
		//준비
		else if(cmd.equals("16")) {
			userInfo.GetCurRoom().Ready(this, msgArr[1]);
		}
		//라운드 시작
		else if(cmd.equals("30")) {
			userInfo.GetCurRoom().StartGame();
		}
		//뒤집기
		else if(cmd.equals("32")) {
			userInfo.GetCurRoom().DrawCard(msgArr[1]);
		}
		//벨 누름
		else if(cmd.equals("34")) {
			userInfo.GetCurRoom().Ring(msgArr[1]);
		}
		//메세지 받음
		else if(cmd.equals("1")) {
			userInfo.GetCurRoom().BroadCast(msg);
		}
	}
	
}