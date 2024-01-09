import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Chatting extends JFrame{
	JButton button, but_input;
	JTextArea ta;
	JTextField tf;
	
	static PrintWriter out = null;
	static BufferedReader in = null;
	
	Chatting(){
		setSize(700, 600);
		setTitle("비대면상담");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton rehome = new JButton("홈버튼");
		setBackground(Color.WHITE);
		String []hlist = {"병원선택","우리내과","연세내과","튼튼정형외과","본드정형외과","이가튼튼치과","아리치과","뽀얀피부과","클리어피부과"};
		JComboBox<String> strCombo = new JComboBox<String>(hlist);
		
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		ta = new JTextArea(25, 60);
		tf = new JTextField(30);
		but_input = new JButton("입력");
		but_input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == but_input) {
					String s = "사용자 : " + tf.getText();
					ta.append(s+"\n");
					out.println(s);
					tf.setText("");
				}
			}
		});
		strCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
				int index = cb.getSelectedIndex();
				if(hlist[index]=="우리내과") 
					try {
						client();
					} catch (IOException e1) {e1.printStackTrace();}
				
			}
		});
		panel.add(strCombo);
		panel.add(ta);
		panel.add(tf);
		panel.add(but_input);
		add(panel2, BorderLayout.NORTH);
		add(panel);
		panel.add(rehome,BorderLayout.SOUTH);
		rehome.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		setVisible(true);
	}
	
	void client() throws IOException {
		Socket socket = null;
		try {
			socket = new Socket("localhost", 5555);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

		} catch (UnknownHostException e) {
			System.err.println("localhost에 접근할 수 없습니다.");
			ta.append("상담시간이 아닙니다.");
		} catch (IOException eg) {
			System.err.println("입출력 오류!!");
			ta.append("상담시간이 아닙니다. 상담시간은 10시~12시,13시~17시 입니다. 점심시간:12시-13시"+"\n");
		}
		String fromServer;
		while ((fromServer = in.readLine()) != null) {
			String s =fromServer+"\n";
			//System.out.println(s);
			ta.append(s);
			System.out.println(fromServer);
			if (fromServer.equals("bye"))
				break;
		}
		out.close();
		in.close();
		socket.close();
	}
	
	public static void main(String[] args){
		new Chatting();
	}

}