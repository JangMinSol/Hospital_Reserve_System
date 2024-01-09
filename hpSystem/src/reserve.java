import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import database.DBConnection;

class CalendarDataManager{ // 6*7배열에 나타낼 달력 값을 구하는 class
	DBConnection connection = new DBConnection();
	static final int CAL_WIDTH = 7;
	final static int CAL_HEIGHT = 6;
	int calDates[][] = new int[CAL_HEIGHT][CAL_WIDTH];
	int calYear;
	int calMonth;
	int calDayOfMon;
	final int calLastDateOfMonth[]={31,28,31,30,31,30,31,31,30,31,30,31};
	int calLastDate;
	Calendar today = Calendar.getInstance();
	Calendar cal;
	
	public CalendarDataManager(){
		setToday();
	}
	public void setToday(){
		calYear = today.get(Calendar.YEAR); 
		calMonth = today.get(Calendar.MONTH);
		calDayOfMon = today.get(Calendar.DAY_OF_MONTH);
		makeCalData(today);
	}
	private void makeCalData(Calendar cal){
		// 1일의 위치와 마지막 날짜를 구함
		int calStartingPos = (cal.get(Calendar.DAY_OF_WEEK)+7-(cal.get(Calendar.DAY_OF_MONTH))%7)%7;
		if(calMonth == 1) calLastDate = calLastDateOfMonth[calMonth] + leapCheck(calYear);
		else calLastDate = calLastDateOfMonth[calMonth];
		// 달력 배열 초기화
		for(int i = 0 ; i<CAL_HEIGHT ; i++){
			for(int j = 0 ; j<CAL_WIDTH ; j++){
				calDates[i][j] = 0;
			}
		}
		// 달력 배열에 값 채워넣기
		for(int i = 0, num = 1, k = 0 ; i<CAL_HEIGHT ; i++){
			if(i == 0) k = calStartingPos;
			else k = 0;
			for(int j = k ; j<CAL_WIDTH ; j++){
				if(num <= calLastDate) calDates[i][j]=num++;
			}
		}
	}
	private int leapCheck(int year){ // 윤년인지 확인하는 함수
		if(year%4 == 0 && year%100 != 0 || year%400 == 0) return 1;
		else return 0;
	}
	public void moveMonth(int mon){ // 현재달로 부터 n달 전후를 받아 달력 배열을 만드는 함수(1년은 +12, -12달로 이동 가능)
		calMonth += mon;
		if(calMonth>11) while(calMonth>11){
			calYear++;
			calMonth -= 12;
		} else if (calMonth<0) while(calMonth<0){
			calYear--;
			calMonth += 12;
		}
		cal = new GregorianCalendar(calYear,calMonth,calDayOfMon);
		makeCalData(cal);
	}
}
public class reserve extends CalendarDataManager{ // CalendarDataManager의 GUI + 메모기능 + 시계
	// 창 구성요소와 배치도
	JFrame mainFrame;
	Scrollbar sc = new Scrollbar(Scrollbar.HORIZONTAL,0,5,0,100);
	ImageIcon icon = new ImageIcon ("icon.png");
	timeBtn tb = new timeBtn();
	JPanel calOpPanel;
	JButton todayBut;
	JLabel todayLab;
	JButton lMonBut;
	JLabel curMMYYYYLab;
	JButton nMonBut;
	ListenForCalOpButtons lForCalOpButtons = new ListenForCalOpButtons();
    LocalDate now = LocalDate.now();
    String convertedDate1 = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    JPanel calPanel;
	JButton weekDaysName[];
	JButton dateButs[][] = new JButton[6][7];
	listenForDateButs lForDateButs = new listenForDateButs();

	JPanel infoPanel;
	JLabel infoClock;
	JLabel selectedText;
	JLabel selectedDate;
	JButton saveBut; 
	JButton delBut; 
	JButton clearBut;
	JComboBox<String> hpCombo;
	JComboBox<String> timeCombo;
	JComboBox<String> doctorCombo;
	JComboBox<String> hpListCombo;
	JButton rBtn;
	int ng = connection.get_hp_select();
	
	//선택 depart
	int select_depart;
	
	//의사 고유번호
	String did1; String did2; String did3; String did4;
	static String[] doctor = {"진료 예약할 병원을 선택하세요", ""};
	JTextField[] tf; //입력창(이름,생년,전화번호)
	JPanel frameBottomPanel;
	int hid;
	static JComboBox<String> setting(String[] dList) {
		System.out.println("들어옴!!!!!!!!!!!!!!!!!!");
		String[] dLists = {"",""};
		JComboBox<String> doctorCb;
		doctorCb = new JComboBox<String>(dLists);
		return doctorCb;
	}
	class timeBtn extends JPanel{
		timeBtn(){
			JComboBox[] cb = new JComboBox[3];
			String[] scb = {"년", "월", "일"};
			setLayout(new GridLayout(0,2,3,3));
			String[] lists = {"이름", "생년월일(13자리)", "전화번호(010,'-' 제외)","예약 정보"};
			tf = new JTextField[4];
			JLabel []la = new JLabel[4];
			for(int i=0; i<4; i++) {
				la[i] = new JLabel(lists[i]);
				tf[i] = new JTextField(10);
				add(la[i]);
				add(tf[i]);
				la[i].setFont(new Font("gothic", Font.BOLD, 13));
			}
			tf[3].setText("캘린더에서 예약일을 선택하세요");

			rBtn = new JButton("                 예약하기                 ");
			rBtn.setBackground(Color.white);
			this.add(Box.createVerticalStrut(5));
			
			String[] time = {"10:00", "10:30", "11:00", "11:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"};

			
			timeCombo = new JComboBox<String>(time);
			timeCombo.setVisible(true);
			add(timeCombo);
			this.add(Box.createVerticalStrut(15));
			doctorCombo = new JComboBox(doctor);
			add(doctorCombo);
			doctorCombo.setOpaque(true);
			this.add(Box.createVerticalStrut(10));
			rBtn.setFont(new Font("Gothic", Font.BOLD,17));
			rBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println(calYear+"/ "+(calMonth+1)+"/ "+calDayOfMon);
					String confirm = calYear+"년"+(calMonth+1)+"월"+calDayOfMon+"일";
					String confirm1 = calYear+"/"+(calMonth+1)+"/"+calDayOfMon;
					System.out.println(timeCombo.getSelectedItem().toString());
					System.out.println(doctorCombo.getSelectedItem().toString());
					JOptionPane jop = new JOptionPane();
					int i = connection.get_hp_select();
					int j = connection.get_hdepart_select();
					String hp_name;
					if(i == 1)
						hp_name = "내과";
					else if(i == 2)
						hp_name = "정형외과";
					else if(i == 3)
						hp_name = "치과";
					else
						hp_name = "피부과";
					
					//did 구하기
					int did;
					if(doctorCombo.getSelectedIndex() == 0) {
						did = Integer.parseInt(did3);
					}
					else {
						did = Integer.parseInt(did4);

					}
					int Unum = connection.searchUserCnt(); //cust테이블 수
					int RNum = connection.searchReserveCnt(); //reserve테이블 수
					int RDoctor = 1;
					String Rtime = confirm +(String)timeCombo.getSelectedItem();
					Unum++;
					RNum++;
					String t1 = tf[0].getText();
					String t2 = tf[1].getText();
					String t3 = tf[2].getText();
					System.out.println(t1.equals(""));
						if(t1.equals("") || t3.equals("") || t3.equals("") || confirm.equals("") || confirm.equals("지난 날짜이므로 예약 불가합니다")) { //모든 정보 입력 안됐
							jop.showMessageDialog(rBtn, "모든 정보를 입력하세요", "예약 정보 부족", JOptionPane.WARNING_MESSAGE);
						}
						else{ //모두 입력했는가
							System.out.println(tf[0].getText());
							System.out.println(tf[1].getText());
							System.out.println(tf[2].getText());
							int result = connection.get_cid_live(tf[0].getText(), tf[1].getText(), tf[2].getText());
							System.out.println("값 : "+connection.get_cid_live(tf[0].getText(), tf[1].getText(), tf[2].getText())+","+result);
							if(result == 0) { //기존 사용자 X
								int dial = jop.showConfirmDialog(rBtn,hpListCombo.getSelectedItem()+"\n"+tf[0].getText()+"님\n생년 : "+tf[1].getText()+",\n전화번호 : "+tf[2].getText()+"\n"
										+confirm+", "+timeCombo.getSelectedItem()+"\n"+doctorCombo.getSelectedItem()+"\n예약을 확정 하시겠습니까?","예약정보 확인",JOptionPane.YES_NO_OPTION );
								
								if(dial == jop.YES_OPTION) {
									boolean insertData1 = connection.insertUser(Unum, tf[0].getText(), tf[1].getText(), tf[2].getText());						
									boolean insertData2 = connection.insertReserve(RNum,Unum,i,select_depart,Rtime,"예약완료",did);
									if(insertData1 == true && insertData2 == true) {
										jop.showMessageDialog(rBtn, "진료예약이 완료되었습니다", "진료예약완료", JOptionPane.INFORMATION_MESSAGE);
										mainFrame.dispose();
									}
								}
							}
							else if(result == 1) { //기존 사용자
								int SearchCid = connection.searchCid(tf[0].getText(), tf[1].getText(), tf[2].getText()); //CID 찾은 것
								if(connection.get_rid_live(SearchCid,i,select_depart,Rtime,"예약완료",did) == 1) { //reserve에 있음
									jop.showMessageDialog(rBtn, "이미 예약된 정보입니다.", "예약된 정보", JOptionPane.WARNING_MESSAGE);
								}
								else { //reserve에 같은 예약정보 없으면, cid는 원래꺼 쓰고, rid만 reserve 테이블 개수+1로 진행
									jop.showConfirmDialog(rBtn,hpListCombo.getSelectedItem()+"\n"+tf[0].getText()+"님\n생년 : "+tf[1].getText()+",\n전화번호 : "+tf[2].getText()+"\n"
											+confirm+", "+timeCombo.getSelectedItem()+"\n"+doctorCombo.getSelectedItem()+"\n예약을 확정 하시겠습니까?","예약정보 확인",JOptionPane.YES_NO_OPTION );
									String Rdoctor = (String) doctorCombo.getSelectedItem();
									boolean insertData2 = connection.insertReserve(RNum,Unum,i,select_depart,Rtime,"예약완료",did);
									if(insertData2 == true && insertData2 == true) {
										jop.showMessageDialog(rBtn, "진료예약이 완료되었습니다", "진료예약완료", JOptionPane.INFORMATION_MESSAGE);
										mainFrame.dispose();
									}
								}	
							}
					}
				}
			});
			add(rBtn);
		}
	}
	final String WEEK_DAY_NAME[] = { "일", "월", "화", "수", "목", "금", "토" };
	final String title = "병원 진료 예약";

	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new reserve();
			}
		});
	}
	public reserve(){ //구성요소 순으로 정렬되어 있음. 각 판넬 사이에 빈줄로 구별
		mainFrame = new JFrame(title);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(600,640);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setResizable(true);
		
		String[] dList = connection.searchDepartList(connection.get_hp_select());
		//System.out.println(dList);
		hpListCombo = new JComboBox(dList);
		mainFrame.add(hpListCombo);
		JButton searchID = new JButton("조회");
		searchID.setBackground(Color.white);
		hpListCombo.setPreferredSize(new Dimension(130,25));
		searchID.setPreferredSize(new Dimension(100,25));
		
		searchID.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(hpListCombo.getSelectedIndex());
				select_depart = hpListCombo.getSelectedIndex()+1;

				doctorCombo.removeAllItems();
				String[] DList = connection.searchDoctorList(ng, hpListCombo.getSelectedIndex()+1);
				String s = DList[0];
				String s1 = DList[1];
				did1 = DList[0];
				did2 = DList[1];
				did3 = DList[2];
				did4 = DList[3];
//				System.out.println("시작1:"+did1);
//				System.out.println("2:"+did2);
//				System.out.println("3:"+did3);
//				System.out.println("4:"+did4);				
				doctorCombo.addItem(s);
				doctorCombo.addItem(s1);
			}
		});
		mainFrame.add(searchID);
		JButton rehome = new JButton("뒤로가기");
		rehome.setBackground(Color.white);
		rehome.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispose();
				new hpSystem();
				
			}
		});
		rehome.setPreferredSize(new Dimension(100,25));

		mainFrame.add(rehome);
		
		JLabel alabel = new JLabel("*실시간 예약 현황을 확인 후 예약을 진행해주세요 :)");
		alabel.setFont(new Font("gothic", Font.BOLD, 11));

		calOpPanel = new JPanel();
			lMonBut = new JButton("<");
			lMonBut.setToolTipText("Previous Month");
			lMonBut.addActionListener(lForCalOpButtons);
			curMMYYYYLab = new JLabel(calYear+"년 / "+((calMonth+1)<10?"":"")+(calMonth+1)+"월");
			curMMYYYYLab.setFont(new Font("gothic", Font.BOLD, 17));
			nMonBut = new JButton(">");
			nMonBut.setToolTipText("Next Month");
			nMonBut.addActionListener(lForCalOpButtons);
			
			calOpPanel.setLayout(new GridBagLayout());
			GridBagConstraints calOpGC = new GridBagConstraints();
			calOpGC.gridx = 1;
			calOpGC.gridy = 1;
			calOpGC.gridwidth = 2;
			calOpGC.gridheight = 1;
			calOpGC.weightx = 1;
			calOpGC.weighty = 1;
			calOpGC.insets = new Insets(5,5,0,0);
			calOpGC.anchor = GridBagConstraints.WEST;
			calOpGC.fill = GridBagConstraints.NONE;
			calOpGC.gridwidth = 3;
			calOpGC.gridx = 2;
			calOpGC.gridy = 1;
			calOpGC.anchor = GridBagConstraints.CENTER;
			calOpGC.gridwidth = 1;
			calOpGC.gridx = 1;
			calOpGC.gridy = 2;
			calOpPanel.add(lMonBut,calOpGC);
			calOpGC.gridwidth = 2;
			calOpGC.gridx = 3;
			calOpGC.gridy = 2;
			calOpPanel.add(curMMYYYYLab,calOpGC);
			calOpGC.gridwidth = 1;
			calOpGC.gridx = 5;
			calOpGC.gridy = 2;
			calOpPanel.add(nMonBut,calOpGC);
		
		calPanel=new JPanel();
			weekDaysName = new JButton[7];
			for(int i=0 ; i<CAL_WIDTH ; i++){
				weekDaysName[i]=new JButton(WEEK_DAY_NAME[i]);
				weekDaysName[i].setBorderPainted(false);
				weekDaysName[i].setContentAreaFilled(false);
				weekDaysName[i].setForeground(Color.WHITE);
				if(i == 0) weekDaysName[i].setBackground(new Color(200, 50, 50));
				else if (i == 6) weekDaysName[i].setBackground(new Color(50, 100, 200));
				else weekDaysName[i].setBackground(new Color(150, 150, 150));
				weekDaysName[i].setOpaque(true);
				weekDaysName[i].setFocusPainted(false);
				calPanel.add(weekDaysName[i]);
			}
			for(int i=0 ; i<CAL_HEIGHT ; i++){
				for(int j=0 ; j<CAL_WIDTH ; j++){
					dateButs[i][j]=new JButton();
					dateButs[i][j].setBorderPainted(false);
					dateButs[i][j].setContentAreaFilled(false);
					dateButs[i][j].setBackground(Color.WHITE);
					if(j==0) {
						dateButs[i][j].setBackground(Color.GRAY);					}
					else if(j==6)
						dateButs[i][j].setBackground(Color.GRAY);
					dateButs[i][j].setOpaque(true);
					dateButs[i][j].addActionListener(lForDateButs);
					calPanel.add(dateButs[i][j]);
				}
			}
			calPanel.setLayout(new GridLayout(0,7,2,2));
			calPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			showCal(); // 달력을 표시
						
		infoPanel = new JPanel();
			infoPanel.setLayout(new BorderLayout());
			infoClock = new JLabel("", SwingConstants.RIGHT);
			infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			infoPanel.add(infoClock, BorderLayout.NORTH);
			int t = (today.get(Calendar.MONTH)+1)+today.get(Calendar.DAY_OF_MONTH)+today.get(Calendar.YEAR);
//			selectedDate = new JLabel(Integer.toString(t));
//			selectedDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
						
		//calOpPanel, calPanel을  frameSubPanelWest에 배치
		JPanel frameSubPanelWest = new JPanel();
		Dimension calOpPanelSize = calOpPanel.getPreferredSize();
		calOpPanelSize.height = 90;
		calOpPanel.setPreferredSize(calOpPanelSize);
		frameSubPanelWest.setLayout(new BorderLayout());
		frameSubPanelWest.add(calOpPanel,BorderLayout.NORTH);
		frameSubPanelWest.add(calPanel,BorderLayout.CENTER);
		
		//frame에 전부 배치
		mainFrame.setLayout(new FlowLayout());
		mainFrame.add(frameSubPanelWest);
		mainFrame.add(tb);
		mainFrame.setVisible(true);
	}
	private void showCal(){
		for(int i=0;i<CAL_HEIGHT;i++){
			for(int j=0;j<CAL_WIDTH;j++){
				String fontColor="black";
				if(j==0) {
					fontColor="red";
					dateButs[i][j].setEnabled(false); //공휴일은 클릭 no
				}
				else if(j==6) {
					fontColor="blue";
					dateButs[i][j].setEnabled(false);
				}
				
				dateButs[i][j].setText("<html><font color="+fontColor+">"+calDates[i][j]+"</font></html>");

				JLabel todayMark = new JLabel("<html><font color=RED>*</html>");
				dateButs[i][j].removeAll();
				if(calMonth == today.get(Calendar.MONTH) &&
						calYear == today.get(Calendar.YEAR) &&
						calDates[i][j] == today.get(Calendar.DAY_OF_MONTH)){
					dateButs[i][j].add(todayMark);
					dateButs[i][j].setToolTipText("Today");
				}
				if(calDates[i][j] == 0) dateButs[i][j].setVisible(false);
				else dateButs[i][j].setVisible(true);
			}
		}
	}
	private class ListenForCalOpButtons implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == todayBut){
				setToday();
				lForDateButs.actionPerformed(e);
			}
			else if(e.getSource() == lMonBut) moveMonth(-1);
			else if(e.getSource() == nMonBut) moveMonth(1);
			curMMYYYYLab.setText("<html><table><tr><th>"+((calMonth+1)<10?"&nbsp;":"")+(calMonth+1)+"월 / "+calYear+"년"+"</th></tr></table></html>");
			showCal();
		}
	}
	private class listenForDateButs implements ActionListener{ //날짜 눌렀을 때 - 타임 테이블 나오게끔
			public void actionPerformed(ActionEvent e) {
				int k=0,l=0;
				for(int i=0 ; i<CAL_HEIGHT ; i++){
					for(int j=0 ; j<CAL_WIDTH ; j++){
						if(e.getSource() == dateButs[i][j]){ 
							k=i;
							l=j;
						}
					}
				}
				if(!(k == 0 && l == 0)) {
					calDayOfMon = calDates[k][l]; //today버튼을 눌렀을때도 이 actionPerformed함수가 실행되기 때문에 넣은 부분
				}
				cal = new GregorianCalendar(calYear,calMonth,calDayOfMon);
				
				String dDayString = new String();
				int dDay=((int)((cal.getTimeInMillis() - today.getTimeInMillis())/1000/60/60/24));
				if(dDay == 0 && (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR))
						&& (cal.get(Calendar.MONTH) == today.get(Calendar.MONTH))
						&& (cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))) dDayString = "(오늘)";
				else if(dDay >=0) dDayString ="("+(dDay+1)+"일 후)";
//				if(calDayOfMon < 10) {
//					calDayOfMon = 0+calDayOfMon;
//				}
				
				int month = calMonth+1;
				String monthString = String.format("%02d", month); //월을 두자리수로 표현
//				String calDayofMon = Integer.toString(calDayOfMon);
//				calDayofMon = String.format("%02d", calDayofMon);
				String add;
				if(calDayOfMon < 10 ) {
					add = (Integer.toString(calYear)+monthString+"0"+Integer.toString(calDayOfMon));
				}
				else {
					add = (Integer.toString(calYear)+monthString+Integer.toString(calDayOfMon));
				}
				System.out.println("누른 : "+Integer.parseInt(add));
				System.out.println("현재 : "+Integer.parseInt(convertedDate1));
				int a1 = Integer.valueOf(add);
				int a2 = Integer.valueOf(convertedDate1);
				
				if(a1 < a2) {
					tf[3].setForeground(Color.red);
					tf[3].setText("지난 날짜이므로 예약 불가합니다");
				}
				else {
					tf[3].setForeground(Color.black);
					tf[3].setText(+calYear+"년 "+(calMonth+1)+"월 "+calDayOfMon+"일 "+dDayString);

				}
		}
	}
}
