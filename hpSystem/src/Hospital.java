import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class Hospital extends JFrame{
	OpenDialog openDlg = new OpenDialog();
	Frame frame;
	Hospital(){
		setTitle("병원예약프로그램");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(null);
		setBackground(Color.WHITE);

		ImageIcon image1 = new ImageIcon("진료예약.jpg");
		ImageIcon image2 = new ImageIcon("마이페이지.jpg");
		ImageIcon image3 = new ImageIcon("실시간예약.jpg");
		//ImageIcon image4 = new ImageIcon("비대면상담.jpg");
		ImageIcon image5 = new ImageIcon("관리자.png");
		JButton rbtn = new JButton(image1);
		JButton mybtn = new JButton(image2);
		JButton nbtn = new JButton(image3);
		//JButton sbtn = new JButton(image4);
		JButton admin = new JButton(image5);
		
		rbtn.setLocation(80,100);
		rbtn.setSize(200,150);
		mybtn.setLocation(300,100);
		mybtn.setSize(200,150);
		nbtn.setLocation(80,280);
		nbtn.setSize(200,150);
		admin.setLocation(300,280);
		admin.setSize(200,150);
		//admin.setLocation(500,10);
		//admin.setSize(80,80);
		
		rbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new hpSystem();
			}
		});
		mybtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Booking();
			}
		});
		nbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AllBooking();
			}
		});
//		sbtn.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				new Chatting();
//			}
//		});
		admin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openDlg.setVisible(true);
			}
		});
		c.add(rbtn); c.add(mybtn); c.add(nbtn); //c.add(sbtn);
		c.add(admin);
	
		//setLocationRelativeTo(null);
		setSize(600,600);
		setVisible(true);
	}
	class OpenDialog extends JDialog{
		OpenDialog(){
			super(frame,"관리자비밀번호입력",true);
			setLayout(new FlowLayout());
			JPasswordField pwText = new JPasswordField(10);
			JButton okbtn = new JButton("확인");
			JLabel openLa = new JLabel("비밀번호를 입력하세요");
			JLabel wLa = new JLabel("비밀번호가 틀렸습니다.");
			JLabel oLa = new JLabel("비밀번호를 입력하세요.");
			wLa.setForeground(Color.red);
			oLa.setForeground(Color.red);
			wLa.setVisible(false); oLa.setVisible(false);
			okbtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String pw = pwText.getText().trim();
					if(pw.length()==0) {
						oLa.setVisible(true);
					}
					if(pw.equals("1234")) {
						openDlg.setVisible(false);
						new Admin();
					}
					else {
						wLa.setVisible(true);
					}
					pwText.setText(""); //기입력한 비밀번호 초기화
				}
			});
			
			add(openLa);add(pwText); add(okbtn); add(wLa); add(oLa);
			setSize(400,200);
		}
	}
	public static void main(String[] args) {
		new Hospital();
	}
}
