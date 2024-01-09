package database;

public class Main {
	public static void main(String[] args) {
		DBConnection connection = new DBConnection();
//		System.out.println("결과"+connection.searchDoctorList(1,1));
//		System.out.println("결과"+connection.searchDoctorList(1,2));
		System.out.println("결과"+connection.get_cid_live("장민솔","326", "1234"));
//		System.out.println("관리자 여부: "+connection.UserInfo_m());
//		connection.UserInfo_m();
//		System.out.println("데이터 추가 여부: "+connection.insertUser(2, "김길동", "011111", "12345678"));
//		System.out.println("데이터 추가 여부: "+connection.searchUserCnt());
//		System.out.println("데이터 추가 여부: "+connection.insertReserve(2, 2, "내과", 1, "2022/12/05", "11:30", "제2진료실김길동", 1));
//		System.out.println("데이터 추가 여부: "+connection.searchOrder());
	}
}