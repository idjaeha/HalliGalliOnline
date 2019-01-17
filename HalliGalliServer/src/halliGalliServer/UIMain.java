package halliGalliServer;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UIMain extends JFrame {

	private JPanel paneMain;
	private JScrollPane scrollView;
	private JTextArea textView;
	/**
	 * Create the frame.
	 */
	public UIMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 500);
		paneMain = new JPanel();
		paneMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(paneMain);
		paneMain.setLayout(null);
		
		scrollView = new JScrollPane();
		scrollView.setBounds(25, 27, 526, 359);
		paneMain.add(scrollView);
		
		textView = new JTextArea();
		scrollView.setViewportView(textView);
	}
	
	public void AddText(String text) {
		text = text + "\n";
		textView.append(text);
		textView.setCaretPosition(textView.getText().length());
	}
}
