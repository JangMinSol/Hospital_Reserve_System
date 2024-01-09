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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class Admin extends JFrame{
	JFrame frame;
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	JTextArea ta = new JTextArea(25,60);
	JTextArea ctext = new JTextArea(3,60);
	DeleteDialog deleteDlg = new DeleteDialog(frame,"rid");
	int index;
	String []hlist = {"병원선택","우리내과","연세내과","튼튼정형외과","본드정형외과","이가튼튼치과","아리치과","뽀얀피부과","클리어피부과"};
	String []data;
	Admin(){
		setTitle("관리자");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Container c = getContentPane();
		frame = this;
		
		c.setLayout(new FlowLayout());
		setBackground(Color.WHITE);
		
		JComboBox<String> strCombo = new JComboBox<String>(hlist);
		c.add(strCombo);
		JButton delect = new JButton("예약삭제");
		delect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteDlg.setVisible(true);
				//String idx = deleteDlg.getData();
				
			}
		});
		c.add(delect);
		JButton clear = new JButton("진료완료");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ClearDialog clearDlg = new ClearDialog(frame,"rid");
				String idx2 = clearDlg.getData();
				clearData("rid",idx2);
				
			}
		});
		add(clear);
		
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
		
		JButton rehome = new JButton("홈버튼");
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
	class CheckDialog extends JDialog{
		CheckDialog(){
			super(frame,"예약정보를확인하세요",true);
			setSize(600,200);
			String idx = deleteDlg.getData();
			setLayout(new FlowLayout());
			CheckData(idx);
			JLabel la = new JLabel("해당 정보가 맞으면 삭제, 아니면 취소를 눌러주세요");
			JButton lastok = new JButton("삭제");
			lastok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					deleteData("reserve","rid",idx);
					NowBooking(hlist[index]);
					setVisible(false);
				}
			});
			JButton reok = new JButton("취소");
			reok.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
					
				}
			});
			add(new JScrollPane(ctext));
			add(la); add(lastok); add(reok);
			setVisible(true);
			
		}
	}
	void CheckData(String idx) {
		String q1 = "select * from cust,hospital,reserve where state='예약완료' and cust.cid=reserve.cid and hospital.hid=reserve.hid and hospital.depart=reserve.depart and hospital.did=reserve.did and rid=";
		String query;
		query = q1.concat(idx);
		System.out.println(query);
		try {
			ctext.setText("");
			ctext.append("예약번호"+"\t");
			ctext.append("이름"+"\t");
			ctext.append("병원이름"+"\t");
			ctext.append("날짜"+"\t"+"\t");
			ctext.append("의사"+"\t"+"\t");
			ctext.append("현황"+"\n");
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				ctext.append(rs.getString("rid")+"\t");
				ctext.append(rs.getString("cname")+"\t");
				ctext.append(rs.getString("hname")+"\t");
				ctext.append(rs.getString("rdate")+"\t");
				ctext.append(rs.getString("doctor")+"\t");
				ctext.append(rs.getString("state")+"\n");
			}
		}catch(SQLException e) {e.printStackTrace();}
	}
	class DeleteDialog extends JDialog{
		String idx;
		DeleteDialog(JFrame frame, String id){
			super(frame,"삭제정보입력",true);
			setLayout(new FlowLayout());
			JLabel rid = new JLabel("삭제할 예약번호를 입력하세요 :");
			JTextField tf = new JTextField(10);
			JButton deleteBtn = new JButton("삭제");
			deleteBtn.setToolTipText("버튼을 누르면 삭제됩니다.");
			add(rid); add(tf); add(deleteBtn);
			deleteBtn.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					idx = tf.getText();
					CheckDialog cDlg = new CheckDialog();
					deleteDlg.setVisible(false);
				}
			});
			setSize(400,100);
			setVisible(false);
			
		}
		String getData() {
			if(idx.length() == 0)
				return null;
			else
				return idx;
		}
	}
	void deleteData(String tName, String cName, String id) {
		String q1 = "delete from ";
		String q2 = "where ";
		String query;
		query = q1.concat(tName+" ");
		query = query.concat(q2); 
		query = query.concat(cName+"="); 
		query = query.concat(id);
		System.out.println(query);
		try {
			stmt.executeUpdate(query);
		}catch(SQLException e) {e.printStackTrace();}
	}
	class ClearDialog extends JDialog{
		String idx2;
		ClearDialog(JFrame frame, String id){
			super(frame,"예약정보입력",true);
			setLayout(new FlowLayout());
			JLabel rid = new JLabel("완료할 예약번호를 입력하세요 :");
			JTextField tf = new JTextField(10);
			JButton deleteBtn = new JButton("완료");
			add(rid); add(tf); add(deleteBtn);
			deleteBtn.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					idx2 = tf.getText();
					NowBooking(hlist[index]);
					setVisible(false);
				}
			});
			setSize(400,100);
			setVisible(true);
		}
		String getData() {
			if(idx2.length() == 0)
				return null;
			else
				return idx2;
		}
	}
	void clearData(String cName, String id) {
		String q1 = "update reserve set state='진료완료' where rid= ";
		String query;
		query = q1.concat(id);
		try {
			stmt.executeUpdate(query);
		}catch(SQLException e) {e.printStackTrace();}
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
				ta.append(rs.getString("cname")+"\t");
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
//		String url,user,passwd;		
//		url = "jdbc:mysql://localhost:3306/bookdb?serverTimezone=Asia/Seoul";
//		user = "root";
//		passwd = "1234";		
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");
//		}catch(ClassNotFoundException e) {e.printStackTrace();}
//		try {
//		 conn = 
//			DriverManager.getConnection(url,user,passwd);
//		 	System.out.println("연결완료....");		 	
//		 	stmt = conn.createStatement();
//		 	stmt.executeQuery("use hospitaldb");
//		}catch(SQLException e) {e.printStackTrace();}
	}
	public static void main(String[] args) {
		new Admin();

	}

}
