package views;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.UserVO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class MainController extends MasterController{
	@FXML
	private Button btnPrev;
	@FXML
	private Button btnNext;
	@FXML
	private Label lblDate;
	@FXML
	private Label lblDay;
	
	@FXML
	private Label loginInfo;
	
	
	@FXML
	private GridPane gridCalendar;
	
	private UserVO user = null;
	
	private List<DayController> dayList;
	private Map<String, String> dayOfWeek = new HashMap<>();
	
	private YearMonth currentYM; //������ ����� �����ϴ� ����
	@FXML
	private void initialize() {
		dayList = new ArrayList<>();
		
		for(int i = 0; i < 5; i++) { //�޷��� ��
			for(int j = 0; j < 7; j++) { //�޷��� ��
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/views/DayLayout.fxml"));
				try {
					AnchorPane ap = loader.load();
					gridCalendar.add(ap, j, i);
					DayController dc = loader.getController();
					dc.setRoot(ap);
					dayList.add(dc);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.printf("j : %d, i : %d ��° �׸��� �� ���� �߻�\n", j, i);
					Util.showAlert("����", "�޷�ĭ�� �׸��� �� ������ �߻�", AlertType.ERROR);
				}
			}
		}
		
		String[] engDay = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
		String[] korDay = {"�Ͽ���", "������", "ȭ����", "������", "�����", "�ݿ���", "�����"};
				
		for(int i = 0; i < engDay.length; i++) {
			dayOfWeek.put(engDay[i], korDay[i]); 
		}
		
	}
	
	public void setToday(LocalDate date) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		lblDate.setText(date.format(dtf));
		lblDay.setText(dayOfWeek.get(date.getDayOfWeek().toString()));
	}
	
	public void loadMonthData(YearMonth ym) {
		LocalDate calendarDate = LocalDate.of(ym.getYear(), ym.getMonthValue(), 1); //�ش� ����� 1���� �����´�.
		while(!calendarDate.getDayOfWeek().toString().equals("SUNDAY")) { //�Ͽ����� �ƴҶ����� �Ϸ羿 ������.
			calendarDate = calendarDate.minusDays(1); //�Ϸ羿 ����
		}
		
		//������� ���� �ش��ְ��� ù°���� ���Եȴ�. ���⼭���� Ķ������ �׸��� �����Ѵ�.
		
		//�ش� ���� ù°���� ������ ���� ��¥�� ���Ѵ�.
		LocalDate first = LocalDate.of(ym.getYear(), ym.getMonthValue(), 1);
		LocalDate last = LocalDate.of(ym.getYear(), ym.getMonthValue() + 1, 1).minusDays(1);
		
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT date, COUNT(*) AS cnt FROM diary_todos WHERE owner = ? AND date BETWEEN ? AND ? GROUP BY date";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, user.getId());
			pstmt.setDate(2, Date.valueOf(first));
			pstmt.setDate(3, Date.valueOf(last));
			
			rs = pstmt.executeQuery();
			
			Map<LocalDate, Integer> cntMap = new HashMap<>();
			while(rs.next()) {
				//������ ��¥�� ������.
				LocalDate key = rs.getDate("date").toLocalDate();
				Integer value = rs.getInt("cnt");
				cntMap.put(key, value);
			}
			
			for(DayController day : dayList) {
				day.setDayLabel(calendarDate);
				if(cntMap.containsKey(calendarDate)) {
					day.setCountLabel(cntMap.get(calendarDate));
				}else {
					day.setCountLabel(0); //���� ������ �ش� ������ �ƴϸ� 0���� ����
				}
				calendarDate = calendarDate.plusDays(1); //�Ϸ羿 ����
			}
		} catch (Exception e) {
			e.printStackTrace();
			Util.showAlert("����", "�����ͺ��̽� ������ �����߻�, ���ͳ� ������ Ȯ���ϼ���", AlertType.ERROR);
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
		
		currentYM = ym;
	}
	
	public void logout() {
		MainApp.app.loadPane("login");
	}
	
	public void prevMonth() {
		loadMonthData(currentYM.minusMonths(1)); //�Ѵ� �� �޷��� �ε�
		LocalDate firstDay = LocalDate.of(currentYM.getYear(), currentYM.getMonthValue(), 1);
		setToday(firstDay);
	}
	
	public void nextMonth() {
		loadMonthData(currentYM.plusMonths(1)); //�Ѵ� �� �޷��� �ε�
		LocalDate firstDay = LocalDate.of(currentYM.getYear(), currentYM.getMonthValue(), 1);
		setToday(firstDay);
	}
	
	public void setLoginInfo(UserVO user) {
		this.user = user;
		loginInfo.setText(user.getName() + "[" + user.getId() + "]");
		
		//�α������� �������¿��� �����͸� �ҷ����� �ʵ��� �ش� �ڵ带 initialize���� �̰����� �Ű��. 
		loadMonthData(YearMonth.now());
		setToday(LocalDate.now());
	}
	
	public void setClickData(LocalDate date) {
		setToday(date); //��¥ �����ϰ�
		for(DayController dc : dayList) {
			dc.outFocus(); //��� DayController���� active�� �����Ѵ�.
		}
	}
	
	public UserVO getUser() {
		return user;
	}
}
