package halliGalliOnline;

import javax.swing.*;
import java.util.*;

public class ClientManager {
	private int stage;
	private Client myClient;
	private Vector<JFrame> views;
	JFrame curView;
	
	public ClientManager() {
		Init();
	}
	
	public void Init() {
		views = new Vector<JFrame>();
		views.add(new UILogin(this));
		views.add(new UIGameList(this));
		views.add(new UIGame(this));
		Thread th = new Thread((UIGame)views.elementAt(2));
		th.start();
		ChangeView(0);
	}
	
	public Client CreateClient(String ip) {
		myClient = new Client(this, ip);
		((UIGameList)views.elementAt(1)).SetClient(myClient);
		((UIGame)views.elementAt(2)).SetClient(myClient);
		return myClient;
	}
	
	public void SendNotification(String str) {
		if(stage == 0) {
			((UILogin)curView).SetNotification(str);
		}
	}
	
	public void ChangeView(int stage) {
		this.stage = stage;
		if(curView != null)
			curView.setVisible(false);
		curView = views.elementAt(stage);
		if(stage == 1) {
			myClient.SendMsg("14");
		}
		curView.setVisible(true);
		if(stage == 2) {
			((UIGame)curView).Focus();
		}
	}
	
	public int GetStage() {
		return stage;
	}
}
