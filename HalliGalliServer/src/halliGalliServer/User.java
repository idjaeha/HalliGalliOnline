package halliGalliServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

//client�� �ϳ��� ����Ǵ� object thread
class User extends Thread {
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket mySocket;
	private Vector<User> userList;
	private ServerManager SM;
	private UserInfo userInfo;

	public User(Socket soc, ServerManager SM) // �����ڸ޼ҵ�
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
			dis.read(b); // ip�� ����.
			String ip = new String(b);
			ip = ip.trim();
			SM.AddUIText("IP :" + ip + " ����");
		} catch (Exception e) {
			SM.AddUIText("��Ʈ�� ���� ����");
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
		//���̵� ���θ������ ��
		if(SM.GetPWD(id) == null) {
			userInfo = new UserInfo();
			userInfo.SetId(id);
			userInfo.SetPwd(pwd);
			userInfo.SetState(1);
			userInfo.SetCurUser(this);
			SM.AddUser(id, userInfo);
			//��� �濡 �����ϴ� �ڵ� �߰�
			text = "���ο� ��� " + id + "���� ���ӿ� �����ϼ̽��ϴ�.";
			msg = "7\t0\t";
		}
		//������ �ִ� ���̵�� �������� ��
		else if(SM.GetPWD(id).equals(pwd)) {
			UserInfo temp = SM.GetUserInfo(id);
			//�α��� �Ǿ��ִ� ������ ���
			if(temp.GetState() == 1) {
				text = id + "���� ���� ���̵�� ������ �õ��߽��ϴ�.";
				msg = "7\t3\t";
			}
			//���� �α���
			else {
				userInfo = temp;
				userInfo.SetState(1);
				userInfo.SetCurUser(this);
				//��� �濡 �����ϴ� �ڵ� �߰�
				text = id + "���� �����ϼ̽��ϴ�.";
				msg = "7\t1\t";
			}
		}
		else if(!SM.GetPWD(id).equals(pwd)){
			text = id + "�� ���� �����Ͽ����ϴ�.";
			msg = "7\t2\t";
		}
		else {
			return;
		}
		try {
			msg = msg + id;
			byte[] b = msg.getBytes("euc-kr");
			SM.AddUIText(text);
			SendMsg(msg); // ����� ����ڿ��� ���������� �˸�
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
			SM.AddUIText("�޼��� ���� ����");
		}
	}
	
	//client�� �ϳ��� ���� �ִ� Thread
	public void run() // ������ ����
	{

		while (true) {
			try {
				// ����ڿ��� �޴� �޼���
				byte[] b = new byte[64];
				dis.read(b);//client �޽��� ����. ���� ���.
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
					userList.removeElement( this ); // ������ �߻��� ���� ��ü�� ���Ϳ��� �����
					SM.AddUIText(userList.size() +" : ���� ���Ϳ� ����� ����� ��");
					SM.AddUIText("����� ���� ������ �ڿ� �ݳ�");
					break;
				
				} catch (Exception ee) {
				
				}// catch�� ��
			}// �ٱ� catch����

		}
	}// run�޼ҵ� ��
	
	public void ExecMsg(String msg) {
		String[] msgArr = msg.split("\t");
		String cmd = msgArr[0].trim();
		
		if(cmd == null)
			return;
		//�α��� ����
		else if(cmd.equals("6")) {
			String id = msgArr[1].trim();
			String pwd = msgArr[2].trim();
			Login(id, pwd);
		}
		//�α׾ƿ�
		else if(cmd.equals("8")) {
			userInfo.SetState(0);
			SendMsg(msg);
		}
		//�� ����
		else if(cmd.equals("10")) {
			String roomTitle = msgArr[1].trim();
			int roomNo = SM.CreateRoom(this, roomTitle);
			//�� ���� ����
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
		//�� ����
		else if(cmd.equals("12")) {
			int temp = SM.EnterRoom(this, Integer.parseInt(msgArr[1]));
			//�� ���� ����
			if(temp == -1) {
				msg = "13\t1";
				SendMsg(msg);
			}
			//�� ���� ����
			else {
				msg = "13\t0\t" + temp + "\t" + userInfo.GetId() + "\t";
				SendMsg(msg);
				userInfo.GetCurRoom().SendIDGroup();
			}
		}
		//�� ���ΰ�ħ ����
		else if(cmd.equals("14")) {
			//�� ������ �ڿ� ������� ���ȣ�� �����ش�.
			msg = "14\t" + SM.GetRoomSize() + "\t";
			msg = msg + SM.GetRoomInfoToString();
			SendMsg(msg);
		}
		//�� ������
		else if(cmd.equals("15")) {
			int roomNo = Integer.parseInt(msgArr[1]); 
			SM.ExitRoom(roomNo, this);
		}
		//�غ�
		else if(cmd.equals("16")) {
			userInfo.GetCurRoom().Ready(this, msgArr[1]);
		}
		//���� ����
		else if(cmd.equals("30")) {
			userInfo.GetCurRoom().StartGame();
		}
		//������
		else if(cmd.equals("32")) {
			userInfo.GetCurRoom().DrawCard(msgArr[1]);
		}
		//�� ����
		else if(cmd.equals("34")) {
			userInfo.GetCurRoom().Ring(msgArr[1]);
		}
		//�޼��� ����
		else if(cmd.equals("1")) {
			userInfo.GetCurRoom().BroadCast(msg);
		}
	}
	
}