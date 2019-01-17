package halliGalliServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;


public class UserCreator {
	private ServerManager SM;
	private ServerSocket socket; //��������
	private Socket tempSocket;
	private int port;
	
	
	public UserCreator(ServerManager SM) {
		this.SM = SM;
		Init();
	}
	
	private void Init() {
		port = 33356;
		Start();
	}
	
	private void Start() {
		try {
			socket = new ServerSocket(port);
			if(socket!=null)
			{
			Thread th = new Thread(new Runnable() { // ����� ������ ���� ������
				@Override
				public void run() {
					while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
						try {
							SM.AddUIText("����� ���� �����...");
							// Ŭ���̾�Ʈ ���� ���� ���.
							tempSocket = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
							User tempUser = new User(tempSocket, SM);
							SM.AddUser(tempUser);
							tempUser.start(); // ���� ��ü�� ������ ����
						} catch (IOException e) {
							SM.AddUIText("!!!! accept ���� �߻�... !!!!");
						} 
					}
				}
			});
			th.start();
			}
			
		} catch (IOException e) {
			SM.AddUIText("�̹� ��� ���� ��Ʈ...");
		}

	}
	

}
