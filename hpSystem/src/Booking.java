import java.awt.BorderLayout;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class Booking extends JFrame{
	JFrame frame;
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String []data;
	JTextArea text1;
	JTextArea text2;
	JTextArea ctext = new JTextArea(3,60);
	MenuPane mpane = new MenuPane();
	Mypage mpage = new Mypage();
	DeleteDialog deleteDlg = new DeleteDialog(frame,"rid");
	Booking(){
		setTitle("마이페이지");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		
		c.add(mpage,BorderLayout.CENTER);
		c.add(mpane,BorderLayout.SOUTH);
		
		dbConn();
		setSize(600,600);
		setVisible(true);
	}
	
	class Mypage extends JPanel{
		Mypage(){
			setLayout(new FlowLayout());
			JLabel NLa = new JLabel("예약확인하기");
			JLabel YLa = new JLabel("지난예약목록");
			text1 = new JTextArea(8,60); //나의예약확인 TextArea
			text2 = new JTextArea(15,60); //실시간예약확인 TextArea
			JButton mybtn = new JButton("정보입력");
			mybtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String []names = {"이름","생년월일(뒷자리포함)"};
					MyDialog mDlg = new MyDialog(frame,names);
					
					data = mDlg.getData();				
					String item="'";
					item = item.concat(data[0]);
					item = item.concat("'");
					data[0] = item;
					item="'";
					item = item.concat(data[1]);
					item = item.concat("'");
					data[1] = item;				
					MyDataSearch(data);
					MyDataSearch2(data);
				}
			});
			JButton delect = new JButton("예약삭제");
			delect.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					deleteDlg.setVisible(true);
					//String idx = deleteDlg.getData();
					
					
				}
			});
			
			add(NLa); add(mybtn); add(delect);add(text1); add(YLa);add(text2);
		}
	}
	class CheckDialog extends JDialog{
		CheckDialog(){
			super(frame,"예약정보를확인하세요",true);
			setSize(600,200);
			String idx = deleteDlg.getData();
			setLayout(new FlowLayout());
			CheckData(idx);
			JLabel la = new JLabel("위의 정보가 맞으면 삭제 아니면 취소를 눌러주세요");
			JButton lastok = new JButton("삭제");
			lastok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					deleteData("reserve","rid",idx);
					MyDataSearch(data);
					MyDataSearch2(data);
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
			deleteBtn.setToolTipText("버튼을 누루면 삭제됩니다.");
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
	
	class MyDialog extends JDialog{
		String []str;
		JTextField []tf;
		MyDialog(JFrame frame,String[] names){
			super(frame,"정보입력하기",true);
			setLayout(new FlowLayout());			
			tf = new JTextField[names.length];
			JLabel []la = new JLabel[names.length];
			str = new String[names.length];
			for(int i=0; i<names.length; i++) {
				la[i] = new JLabel(names[i]);
				tf[i] = new JTextField(20);
				add(la[i]);
				add(tf[i]);
			}
			JButton okBtn = new JButton("확인");
			add(okBtn);			
			okBtn.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					for(int i=0; i<str.length; i++)
						if(tf[i].getText().length() != 0) {
							str[i] = tf[i].getText();
						}
					setVisible(false);					
				}
			});
			setSize(700,150);
			setVisible(true);			
		}
		
		String[] getData() {
			if(str[0].length() == 0)
				return null;
			else
				return str;
		}
	}
	void MyDataSearch(String []data) {
		String q1 = "select * from cust,hospital,reserve where state='예약완료' and cust.cid=reserve.cid and hospital.hid=reserve.hid and hospital.depart=reserve.depart and hospital.did=reserve.did and cname= ";
		String q2 = "and birth=";
		String query;
		query = q1.concat(data[0]+" ");
		query = query.concat(q2);
		query = query.concat(data[1]);
		text1.append(query);
		text1.setText("");
		text1.append("예약번호"+"\t");
		text1.append("이름"+"\t");
		text1.append("병원이름"+"\t");
		text1.append("날짜"+"\t"+"\t");
		text1.append("의사"+"\t"+"\t");
		text1.append("현황"+"\n");
		try {
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				text1.append(rs.getString("rid")+"\t");
				text1.append(rs.getString("cname")+"\t");
				text1.append(rs.getString("hname")+"\t");
				text1.append(rs.getString("rdate")+"\t");
				text1.append(rs.getString("doctor")+"\t"+"\t");
				text1.append(rs.getString("state")+"\n");
			}
		}catch(SQLException e) {e.printStackTrace();}
	}
	void MyDataSearch2(String []data) {
		String q1 = "select * from cust,hospital,reserve where state='진료완료' and cust.cid=reserve.cid and hospital.hid=reserve.hid and hospital.depart=reserve.depart and hospital.did=reserve.did and cname= ";
		String q2 = "and birth=";
		String query;
		query = q1.concat(data[0]+" ");
		query = query.concat(q2);
		query = query.concat(data[1]);
		text2.setText("");
		text2.append("예약번호"+"\t");
		text2.append("이름"+"\t");
		text2.append("병원이름"+"\t");
		text2.append("날짜"+"\t"+"\t");
		text2.append("의사"+"\t"+"\t");
		text2.append("현황"+"\n");
		try {
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				text2.append(rs.getString("rid")+"\t");
				text2.append(rs.getString("cname")+"\t");
				text2.append(rs.getString("hname")+"\t");
				text2.append(rs.getString("rdate")+"\t");
				text2.append(rs.getString("doctor")+"\t");
				text2.append(rs.getString("state")+"\n");
			}
		}catch(SQLException e) {e.printStackTrace();}
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
	class MenuPane extends JPanel{
		MenuPane(){
			JButton rehomebtn = new JButton("홈버튼");
			
			rehomebtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			add(rehomebtn);
		}
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
		new Booking();

	}
}