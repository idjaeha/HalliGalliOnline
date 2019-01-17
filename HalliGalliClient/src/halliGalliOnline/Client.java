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
		try { // ��Ʈ�� ����
			socket = new Socket(ip, port);
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			CM.SendNotification("������ ������� �ʽ��ϴ�.");
		}
		SendMsg(ip); // ���������� ����Ǹ� ip�� ����
		Thread th = new Thread(new Runnable() { // �����带 ������ �����κ��� �޼����� ����
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
							break; // ���� �߻��ϸ� while�� ����
						} catch (IOException e1) {
						}
					}
				} // while�� ��
			}// run�޼ҵ� ��
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
	
	public void SendMsg(String str) { // ������ �޼����� ������ �޼ҵ�
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
		//�α��� ����
		else if(cmd.equals("7")) {
			//�α��� ó��
			if(msgArr[1].equals("0")) {
				//ȭ�� ó��
				CM.ChangeView(1);
				curRoomNo = 0;
				this.id = msgArr[2].trim();
			}
			else if(msgArr[1].equals("1")) {
				//ȭ�� ó��
				CM.ChangeView(1);
				curRoomNo = 0;
				this.id = msgArr[2].trim();
			}
			else if(msgArr[1].equals("2")) {
				CM.SendNotification("��й�ȣ�� �ùٸ��� �ʽ��ϴ�.");
			}
			else if(msgArr[1].equals("3")) {
				CM.SendNotification("�̹� ���� ���� �����Դϴ�.");
			}
		}
		
		//�α׾ƿ�
		else if(cmd.equals("8")) {
			CM.ChangeView(0);
			this.id = null;
		}
		
		//�� ���� ����
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
		
		//�� ���� ����
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
		
		//�� ��� ���ΰ�ħ ����
		else if(cmd.equals("14")) {
			((UIGameList)CM.curView).RefreshList(msgArr);
		}
		
		//�غ� ����
		else if(cmd.equals("17")) {
			((UIGame)CM.curView).SetStateCode(msgArr[1].trim());
			((UIGame)CM.curView).Update();
		}
		
		//���̵� ������Ʈ ����
		else if(cmd.equals("18")) {
			((UIGame)CM.curView).SetIdCode(msgArr);
			((UIGame)CM.curView).Update();
		}
		
		//���� ����
		else if(cmd.equals("19")) {
			((UIGame)CM.curView).StartGame();
			((UIGame)CM.curView).SetCardImage(0);
			((UIGame)CM.curView).ChangeBtn();
		}
		
		//���� ���� ����
		else if(cmd.equals("31")) {
			((UIGame)CM.curView).SetCurTurn(msgArr[2]);
			((UIGame)CM.curView).SetRound(msgArr[1]);
		}
		
		//������ ����
		else if(cmd.equals("33")) {
			((UIGame)CM.curView).SetPlayerCount(msgArr[3]);
			((UIGame)CM.curView).SetCardAni(msgArr[1], msgArr[2]);
		}
		
		//�� ���� ����
		else if(cmd.equals("35")) {
			//����
			if(msgArr[1].equals("0")) {
				((UIGame)CM.curView).SetPlayerCount(msgArr);
			}
			//����
			else if(msgArr[1].equals("1")) {
				((UIGame)CM.curView).SetPlayerCount(msgArr[3], msgArr[2]);
			}
			((UIGame)CM.curView).StartRing();
		}
		
		//���� ���� �˸�
		else if(cmd.equals("36")) {
			((UIGame)CM.curView).SetCardImage(msgArr[1]);
			((UIGame)CM.curView).KillPlayer(msgArr[1]);
		}
		
		//���� ����
		else if(cmd.equals("37")) {
			if(!msgArr[1].equals("9")) {
				((UIGame)CM.curView).ShowResult(msgArr[1]);
			}
			((UIGame)CM.curView).EndGame();
			((UIGame)CM.curView).SetCardImage(22);
			((UIGame)CM.curView).ChangeBtn();
			((UIGame)CM.curView).SetRound(null);
		}
		
		//���� ���� �˸�
		else if(cmd.equals("38")) {
			((UIGame)CM.curView).SetCurTurn(msgArr[1]);
		}
		
		//�޼��� ����
		else if(cmd.equals("1")) {
			((UIGame)CM.curView).AppendMessage(msgArr[1], msgArr[2]);
		}
		
	}
}