package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {

	private Connection con1 = null;
	private Statement st = null;
	private ResultSet rs = null;
	private int rs1;
	static public int hid1;
	static public int hdepart;
	static public Object UserList;
	public DBConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospitaldb", "root", "minsol7665@@");
			st = con1.createStatement();
			System.out.println("데이터베이스 접속 성공");
		}catch(Exception e) {
			System.out.println("데이터베이스 연결 오류 : " + e.getMessage());
		}
	}
	public boolean UserInfo_m(){
		try {
			rs = st.executeQuery("SELECT * FROM cust JOIN reserve ON cust.cid=reserve.cid JOIN hospital ON hospital.hid=reserve.hid AND hospital.depart = reserve.depart");
			if(rs != null) {
				while(rs.next()) {
					System.out.print(rs.getString("CID")+"\t");
					System.out.print(rs.getString("CNAME")+"\t");
					System.out.print(rs.getString("BIRTH")+"\t");
					System.out.print(rs.getString("HP")+"\t");
					System.out.print(rs.getString("HNAME")+"\t");
					System.out.print(rs.getString("HTIME")+"\t");
					System.out.println(rs.getString("DOCTOR")+"\t");
					String cid = rs.getString("CID");
					String name = rs.getString("HNAME");
					String birth = rs.getString("BIRTH");
					String hp = rs.getString("HP");
					String hname = rs.getString("HNAME");
					String htime = rs.getString("HTIME");
					String doctor = rs.getString("DOCTOR");
					
					Object data[] = {cid,name,birth,hp,hname,htime,doctor};
					getUserList(data);
				}
				return true;
			}
		}catch(Exception e) {
			System.out.println("데이터베이스 검색 오류 : " + e.getMessage());
		}
		return false;
	}
	public boolean insertUser(int num, String name, String birth, String hp) {
		try {
			rs1 = st.executeUpdate("INSERT INTO cust VALUES("+num+","+"'"+name+"'"+","+birth+","+hp+")");
			return true;
		}catch(Exception e) {
			System.out.println("데이터베이스 저장1 오류 : " + e.getMessage());
			return false;
		}
	}
	public boolean insertReserve(int rid, int cid, int hid, int depart, String htime, String state, int did) {
		try {
			rs1 = st.executeUpdate("INSERT INTO reserve(rid,cid,hid,depart,rdate,state,did) VALUES("+rid+","+cid+","+hid+","+depart+",'"+htime+"','"+state+"',"+did+")");
			return true;
		}catch(Exception e) {
			System.out.println("데이터베이스 저장2 오류 : " + e.getMessage());
			return false;
		}
	}
	public int searchUserCnt() { //cust테이블 개수
		try {
			rs = st.executeQuery("SELECT MAX(CID) FROM cust");
			rs.next();
			int userNum = rs.getInt(1);
			return userNum;
		}catch(Exception e) {
			System.out.println("데이터베이스 검색 오류 : 갯수 모름 " + e.getMessage());
			return -1;
		}
	}
	public int searchReserveCnt() { //reserve테이블 개수
		try {
			rs = st.executeQuery("SELECT MAX(RID) FROM reserve");
			rs.next();
			int reserveNum = rs.getInt(1);
			return reserveNum;
		}catch(Exception e) {
			System.out.println("데이터베이스 검색 오류 : 갯수 모름 " + e.getMessage());
			return -1;
		}
	}
	public int searchOrder() {
		try {
			rs = st.executeQuery("SELECT MAX(CID) FROM reserve WHERE hid=1 AND hdate='2022/12/01' AND htime='11:30'");
			rs.next();
			int userNum = rs.getInt(1);
			return userNum;
		}catch(Exception e) {
			System.out.println("데이터베이스 검색 오류 : 갯수 모름 " + e.getMessage());
			return -1;
		}
	}
	public void hp_select(int id) {
		int hid = id;
		hid1 = hid;
		System.out.print(hid1);
	}
	public void getUserList(Object Users) {
		UserList = Users;
	}
	public Object getUser() {
		return UserList;
	}
	public int get_hp_select() {
		return hid1;
	}
	public void set_hdepart_select(int depart) {
		hdepart = depart;
	}
	public int get_hdepart_select() {
		return hdepart;
	}
	
	//cid 검색 - 사용자 정보 유무 확인
	public int get_cid_live(String cname, String birth, String hp) {
		int count = 0;
		try {
			rs = st.executeQuery("SELECT COUNT(*) FROM cust WHERE CNAME="+"'"+cname+"' and BIRTH="+"'"+birth+"' and hp="+"'"+hp+"'");
			rs.next();
			if(rs.getInt("COUNT(*)") >= 1) {
				count = 1;
			}
			else if(rs.getInt("COUNT(*)") == 0){
				count = 0;
			}
		}catch(Exception e) {
			System.out.println("데이터베이스 사용자 찾기 오류 : " + e.getMessage());
		}
		return count;
	}
	//rid 검색 - 예약 정보 유무 확인
	public int get_rid_live(int cid, int hid, int depart, String htime, String state, int did) {
		int count = 0;
		try {
			rs = st.executeQuery("SELECT COUNT(*) FROM reserve WHERE cid="+cid+" and hid="+hid+" and depart="+depart+" and rdate="+"'"+htime+"' and state="+"'"+state+"' and did="+did);
			rs.next();
			if(rs.getInt("COUNT(*)") >= 1) {
				count = 1;
			}
			else if(rs.getInt("COUNT(*)") == 0){
				count = 0;
			}
		}catch(Exception e) {
			System.out.println("데이터베이스 사용자 찾기 오류 : " + e.getMessage());
		}
		return count;
	}
	public int searchCid(String cname, String birth, String hp) {
		int CID = 0;
		try {
			rs = st.executeQuery("SELECT cid FROM cust WHERE CNAME="+"'"+cname+"' and BIRTH="+"'"+birth+"' and hp="+"'"+hp+"'");
			rs.next();
			CID = rs.getInt("CID");
			System.out.println(CID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return CID;
	}
	//depart목록 가져오기
	public String[] searchDepartList(int hid) {
		String[] str = new String[2];
		String str1 = "";
		String str2 = "";
		try {
			rs = st.executeQuery("SELECT hname FROM hospital WHERE HID="+hid);
			rs.next();
			str1 = rs.getString("hname");
			rs.next();
			rs.next();
			str2 = rs.getString("hname");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		str[0] = str1;
		str[1] = str2;
		return str;
	}
	//doctor목록 가져오기
		public String[] searchDoctorList(int hid, int depart) {
			String[] str = new String[4];
			String str1 = "";
			String str2 = "";
			String str3 = "";
			String str4 = "";

			try {
				rs = st.executeQuery("SELECT doctor,did FROM hospital WHERE hid="+hid+" and depart="+depart);
				rs.next();
				str1 = rs.getString("doctor");
				str3 = rs.getString("did");
				rs.next();
				str2 = rs.getString("doctor");
				str4 = rs.getString("did");
			} catch (SQLException e) {
			}
			str[0] = str1;
			str[1] = str2;
			str[2] = str3;
			str[3] = str4;

			return str;
		}
}
