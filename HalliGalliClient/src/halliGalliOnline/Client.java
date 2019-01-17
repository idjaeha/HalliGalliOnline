package halliGalliOnline;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Client{
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String ip;
	private int port;
	private ClientManager CM;
	private String id;
	private int curRoomNo;
	
	public Client(ClientManager CM, String ip) {
		Init(CM, ip);
	}
	
	public void Init(ClientManager CM, String ip) {
		this.CM = CM;
		this.ip = ip;
		port = 33356;
		StartSocket();
	}
	
	private void StartSocket() {
		try { // 스트림 설정
			socket = new Socket(ip, port);
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			CM.SendNotification("서버와 연결되지 않습니다.");
		}
		SendMsg(ip); // 정상적으로 연결되면 ip를 전송
		Thread th = new Thread(new Runnable() { // 스레드를 돌려서 서버로부터 메세지를 수신
			@SuppressWarnings("null")
			@Override
			public void run() {
				while (true) {
					try {
						byte[] b = new byte[64];
						dis.read(b);
						String msg = new String(b);
						msg = msg.trim();
						ExecMsg(msg);
						
					} catch (IOException e) {
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							break; // 에러 발생하면 while문 종료
						} catch (IOException e1) {
						}
					}
				} // while문 끝
			}// run메소드 끝
		});
		th.start();
	}
	
	public String GetId() {
		return id;
	}
	
	public int GetRoomNo() {
		return curRoomNo;
	}
	
	public void SetRoomNo(int roomNo) {
		curRoomNo = roomNo;
	}
	
	public void SendMsg(String str) { // 서버로 메세지를 보내는 메소드
		try {
			byte[] bb = new byte[64];
			String s = String.format("%-64s", str);
			bb = s.getBytes("euc-kr");
			dos.write(bb);
		} catch (IOException e) {
		}
	}
	
	public void ExecMsg(String msg) {
		String[] msgArr = msg.split("\t");
		String cmd = msgArr[0];
		
		if(cmd == null)
			return;
		//로그인 응답
		else if(cmd.equals("7")) {
			//로그인 처리
			if(msgArr[1].equals("0")) {
				//화면 처리
				CM.ChangeView(1);
				curRoomNo = 0;
				this.id = msgArr[2].trim();
			}
			else if(msgArr[1].equals("1")) {
				//화면 처리
				CM.ChangeView(1);
				curRoomNo = 0;
				this.id = msgArr[2].trim();
			}
			else if(msgArr[1].equals("2")) {
				CM.SendNotification("비밀번호가 올바르지 않습니다.");
			}
			else if(msgArr[1].equals("3")) {
				CM.SendNotification("이미 접속 중인 계정입니다.");
			}
		}
		
		//로그아웃
		else if(cmd.equals("8")) {
			CM.ChangeView(0);
			this.id = null;
		}
		
		//방 생성 응답
		else if(cmd.equals("11")) {
			if(msgArr[1].equals("0")) {
				CM.ChangeView(2);
				curRoomNo = Integer.parseInt(msgArr[2]);
				((UIGame)CM.curView).SetMyId(msgArr[3]);
				SendMsg("16\t0\t");
			}
			else {
				
			}
		}
		
		//방 참가 응답
		else if(cmd.equals("13")) {
			if(msgArr[1].equals("0")) {
				CM.ChangeView(2);
				curRoomNo = Integer.parseInt(msgArr[2]);
				((UIGame)CM.curView).SetMyId(msgArr[3]);
				SendMsg("16\t0\t");
			}
			else {
				SendMsg("14");
			}
		}
		
		//방 목록 새로고침 응답
		else if(cmd.equals("14")) {
			((UIGameList)CM.curView).RefreshList(msgArr);
		}
		
		//준비 응답
		else if(cmd.equals("17")) {
			((UIGame)CM.curView).SetStateCode(msgArr[1].trim());
			((UIGame)CM.curView).Update();
		}
		
		//아이디 업데이트 응답
		else if(cmd.equals("18")) {
			((UIGame)CM.curView).SetIdCode(msgArr);
			((UIGame)CM.curView).Update();
		}
		
		//게임 시작
		else if(cmd.equals("19")) {
			((UIGame)CM.curView).StartGame();
			((UIGame)CM.curView).SetCardImage(0);
			((UIGame)CM.curView).ChangeBtn();
		}
		
		//라운드 시작 응답
		else if(cmd.equals("31")) {
			((UIGame)CM.curView).SetCurTurn(msgArr[2]);
			((UIGame)CM.curView).SetRound(msgArr[1]);
		}
		
		//뒤집기 응답
		else if(cmd.equals("33")) {
			((UIGame)CM.curView).SetPlayerCount(msgArr[3]);
			((UIGame)CM.curView).SetCardAni(msgArr[1], msgArr[2]);
		}
		
		//벨 누름 응답
		else if(cmd.equals("35")) {
			//실패
			if(msgArr[1].equals("0")) {
				((UIGame)CM.curView).SetPlayerCount(msgArr);
			}
			//성공
			else if(msgArr[1].equals("1")) {
				((UIGame)CM.curView).SetPlayerCount(msgArr[3], msgArr[2]);
			}
			((UIGame)CM.curView).StartRing();
		}
		
		//라운드 종료 알림
		else if(cmd.equals("36")) {
			((UIGame)CM.curView).SetCardImage(msgArr[1]);
			((UIGame)CM.curView).KillPlayer(msgArr[1]);
		}
		
		//게임 종료
		else if(cmd.equals("37")) {
			if(!msgArr[1].equals("9")) {
				((UIGame)CM.curView).ShowResult(msgArr[1]);
			}
			((UIGame)CM.curView).EndGame();
			((UIGame)CM.curView).SetCardImage(22);
			((UIGame)CM.curView).ChangeBtn();
			((UIGame)CM.curView).SetRound(null);
		}
		
		//차례 변경 알림
		else if(cmd.equals("38")) {
			((UIGame)CM.curView).SetCurTurn(msgArr[1]);
		}
		
		//메세지 받음
		else if(cmd.equals("1")) {
			((UIGame)CM.curView).AppendMessage(msgArr[1], msgArr[2]);
		}
		
	}
}