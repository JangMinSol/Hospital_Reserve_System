import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import database.DBConnection;

public class hpSystem extends JFrame{
	DBConnection connection = new DBConnection();
	Container c;
	hpList hpL = new hpList();
	hpImage hpI = new hpImage();
	int hp_select;
	int id;
	JLabel hpl[] = new JLabel[2];
	JButton hBtn[] = new JButton[2];
//	Hdepart hdepart = new Hdepart();
	int hpid;
	hpSystem(){
		this.addWindowListener(new MyWinExit());
		setTitle("학과 선택");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c = getContentPane();
		setLayout(new BorderLayout());
		
		c.add(BorderLayout.NORTH, hpI); add(BorderLayout.CENTER,hpL); 
		setSize(600,600);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);
	}
	class hpImage extends JPanel{
		hpImage(){
			JLabel hpImg = new JLabel(new ImageIcon("병원.png"));
			add(hpImg);
			setBackground(Color.WHITE);
		}
	}
	public class MyWinExit extends WindowAdapter {
        public void windowClosing(WindowEvent we) {
            System.exit(0); // 윈도 종료
        }
    }
	class hpList extends JPanel{
		hpList(){
			JButton[] hBtn = new JButton[4];
			String[] hList = {"내과_new.jpg", "정형외과_new.jpg", "치과_new.jpg", "피부과_new.jpg"};
			setLayout(new FlowLayout(FlowLayout.CENTER, 55,20));
			for(int i=0; i<hList.length; i++) {
				hBtn[i] = new JButton(new ImageIcon(hList[i]));
				hBtn[i].setPreferredSize(new Dimension(144,120));
				hBtn[i].setBackground(Color.white);
				hBtn[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JButton btn = (JButton) e.getSource();
						hpL.setVisible(false);
						hpI.setVisible(false);
						if(btn == hBtn[0]) {
							hpid = 1;
							connection.hp_select(1);
						}
						else if(btn == hBtn[1])
							connection.hp_select(2);
						
						else if(btn == hBtn[2])
							connection.hp_select(3);
						
						else
							connection.hp_select(4);
					
						new reserve();
						dispose();
					}
				});
				add(hBtn[i]);
			}
				JButton homeBtn = new JButton("홈버튼");
				homeBtn.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				homeBtn.setPreferredSize(new Dimension(150,30));

				add(homeBtn);
		}
	}
//	class Hdepart extends JPanel{
//		Hdepart(){
//			String[] str1 = {"아리내과", "우리내과"};
//			String[] str2 = {"튼튼정형외과", "힘힘정형외과"};
//
//				for(int j=0; j<hpl.length; j++) {
//					hpl[j] = new JLabel(str1[j]);
//					add(hpl[j]);
//					hBtn[j] = new JButton("예약하기");
//					hBtn[j].addActionListener(new ActionListener() {
//						@Override
//						public void actionPerformed(ActionEvent e) {
//							JButton btn = (JButton) e.getSource();
//							if(btn == hBtn[0]) {
//								connection.set_hdepart_select(1);
//							}
//							else {
//								connection.set_hdepart_select(2);
//							}
//							new reserve();
//							dispose();
//						}
//					});
//					add(hBtn[j]);
//				}
//		}
//	}
	void hpreturn(int j) {
		System.out.println(j);
		hpid = j;
		System.out.println(hpid);
	}
	public static void main(String[] args) {
		new hpSystem();
	}
}