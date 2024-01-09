import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AllBooking extends JFrame{
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;	
	JTextArea ta = new JTextArea(25,60);
	JFrame frame;
	int index;
	String []hlist = {"병원선택","우리내과","연세내과","튼튼정형외과","본드정형외과","이가튼튼치과","아리치과","뽀얀피부과","클리어피부과"};
	AllBooking(){
		setTitle("실시간병원예약현황");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		frame = this;
		c.setLayout(new FlowLayout());
		setBackground(Color.WHITE);
		
		JComboBox<String> strCombo = new JComboBox<String>(hlist);
		add(strCombo);
		strCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
				index = cb.getSelectedIndex();
				String item="'";
				item = item.concat(hlist[index]);
				item = item.concat("'");
				hlist[index] = item;
				
				NowBooking(hlist[index]); //모든병원예약
			}
		});
		c.add(new JScrollPane(ta));
		String topText = "진료 예약 시간 기준 10분 후까지 도착하지 않으시면 예약 취소되오니 참고해주세요";
		JLabel tT = new JLabel();
		tT.setForeground(Color.red);
		tT.setText(topText);
		c.add(tT);
		JButton rehome = new JButton("뒤로가기");
		rehome.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
		});
		c.add(rehome);
		
		dbConn();

		setSize(700,600);
		setVisible(true);
	}
	void NowBooking(String hospitalName) {
		String q1 = "select * from cust,hospital,reserve where cust.cid=reserve.cid and hospital.hid=reserve.hid and hospital.depart=reserve.depart and hospital.did=reserve.did and state='예약완료' and hname=";
		String q2 = "order by rdate asc";
		String query;
		query = q1.concat(hospitalName);
		query = query.concat(q2);
		//System.out.println(query);
		ta.setText("");
		ta.append("예약번호"+"\t");
		ta.append("이름"+"\t");
		ta.append("병원이름"+"\t");
		ta.append("날짜"+"\t"+"\t");
		ta.append("의사"+"\t"+"\t");
		ta.append("현황"+"\n");
		try {
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				ta.append(rs.getString("rid")+"\t");
				ta.append("***"+"\t");
				ta.append(rs.getString("hname")+"\t");
				ta.append(rs.getString("rdate")+"\t");
				ta.append(rs.getString("doctor")+"\t");
				ta.append(rs.getString("state")+"\n");
			}
		}catch(SQLException e) {e.printStackTrace();}
	}
	void dbConn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospitaldb", "root", "minsol7665@@");
			stmt = conn.createStatement();
			System.out.println("데이터베이스 접속 성공");
		}catch(Exception e) {
			System.out.println("데이터베이스 연결 오류 : " + e.getMessage());
		}
	}
	public static void main(String[] args) {
		new AllBooking();

	}

}