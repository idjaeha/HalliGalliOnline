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
	private ServerSocket socket; //서버소켓
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
			Thread th = new Thread(new Runnable() { // 사용자 접속을 받을 스레드
				@Override
				public void run() {
					while (true) { // 사용자 접속을 계속해서 받기 위해 while문
						try {
							SM.AddUIText("사용자 접속 대기중...");
							// 클라이언트 접속 무한 대기.
							tempSocket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
							User tempUser = new User(tempSocket, SM);
							SM.AddUser(tempUser);
							tempUser.start(); // 만든 객체의 스레드 실행
						} catch (IOException e) {
							SM.AddUIText("!!!! accept 에러 발생... !!!!");
						} 
					}
				}
			});
			th.start();
			}
			
		} catch (IOException e) {
			SM.AddUIText("이미 사용 중인 포트...");
		}

	}
	

}
