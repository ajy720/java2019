package views;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import domain.TodoVO;
import domain.UserVO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class TodoController extends MasterController{
	@FXML
	private TextField txtTitle;
	@FXML
	private TextArea txtContent;
	@FXML
	private ListView<TodoVO> todoList;
	
	@FXML
	private TextField todoTitle;
	@FXML
	private TextArea todoContent;
	
	private LocalDate date;
	private ObservableList<TodoVO> list;

	//�ʱ�ȭ�� ����Ǵ� ��������Ʈ�� ������ش�.
	@FXML
	private void initialize() {
		list = FXCollections.observableArrayList();
		todoList.setItems(list);
	}
	
	public void init(LocalDate date) {
		txtTitle.setText("");
		txtContent.setText("");
		todoTitle.setText("");
		todoContent.setText("");
		
		this.date = date;
		UserVO user = MainApp.app.getLoginUser();
		
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		list.clear(); //������ �ִ� ������ ����ְ�
		//�ش� ������ �ش� ��¥�� ������ ��� �����´�.
		String sql = "SELECT * FROM diary_todos WHERE date = ? AND owner = ?";
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setDate(1, Date.valueOf(this.date));
			pstmt.setString(2, user.getId());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				TodoVO vo = new TodoVO();
				vo.setId(rs.getInt("id"));
				vo.setTitle(rs.getString("title"));
				vo.setContent(rs.getString("content"));
				vo.setDate(rs.getDate("date").toLocalDate());
				vo.setOwner(rs.getString("owner"));
				list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Util.showAlert("����", "�����ͺ��̽� ������ ���� �߻�, ���ͳ� ������ Ȯ���ϼ���." , AlertType.ERROR);
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}
	
	//�����űԵ��
	public void register() {
		String title = txtTitle.getText();
		String content = txtContent.getText();
		UserVO user = MainApp.app.getLoginUser();
		
		if(title.isEmpty() || content.isEmpty()) {
			Util.showAlert("�ʼ��׸� �������", "�����̳� ������ ��� ������� �� �����ϴ�", AlertType.INFORMATION);
			return;
		}
		
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO diary_todos(title, content, date, owner) VALUES(?, ?, ?, ?)";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setDate(3, Date.valueOf(this.date));
			pstmt.setString(4, user.getId());
			int result = pstmt.executeUpdate();
			
			if(result != 1) {
				Util.showAlert("����", "�����ͺ��̽��� ���������� �Էµ��� �ʾҽ��ϴ�. ��õ����ּ���.", AlertType.ERROR);
				return;
			}
			Util.showAlert("����", "�����ͺ��̽��� ���������� �Է¾����ϴ�.", AlertType.INFORMATION);
			
			MainApp.app.slideOut(this.getRoot());
			
		} catch (Exception e) {
			e.printStackTrace();
			Util.showAlert("����", "�����ͺ��̽� ������ ���� �߻�. ���ͳ� ���¸� Ȯ���ϼ���.", AlertType.ERROR);
		} finally {
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}
	
	//���� ����
	public void update() {
		String title = todoTitle.getText();
		String content = todoContent.getText();
		UserVO user = MainApp.app.getLoginUser();
		
		if(title.isEmpty() || content.isEmpty()) {
			Util.showAlert("�ʼ��׸� �������", "�����̳� ������ ��� ������� �� �����ϴ�", AlertType.INFORMATION);
			return;
		}

		int idx = todoList.getSelectionModel().getSelectedIndex();
		if(idx < 0) {
			return;
		}
		
		TodoVO todo = list.get(idx);
		
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		String sql = "UPDATE diary_todos SET title = ?, content = ? WHERE id = ?";
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setInt(3, todo.getId());
			int result = pstmt.executeUpdate();
			
			if(result != 1) {
				Util.showAlert("����", "�����ͺ��̽� ó���� ���� �߻�. �Է¹��ڸ� Ȯ���ϼ���.", AlertType.ERROR);
				return;
			}
			
			Util.showAlert("����", "�����ͺ��̽��� ���������� �����Ǿ����ϴ�.", AlertType.INFORMATION);
			MainApp.app.slideOut(this.getRoot());
			
		} catch (Exception e) {
			e.printStackTrace();
			Util.showAlert("����", "�����ͺ��̽� ������ ���� �߻�. ���ͳ� ���¸� Ȯ���ϼ���.", AlertType.ERROR);
		}finally {
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}
	
	//����â �ݱ�
	public void close() {
		MainApp.app.slideOut(this.getRoot());		
	}
	
	//����Ʈ �� Ŭ���� ������ ��������
	public void loadTodo() {
		int idx = todoList.getSelectionModel().getSelectedIndex();
		if(idx < 0) {
			return;
		}
		
		TodoVO vo = list.get(idx); //Ŭ���� �༮�� �����͸� �����ͼ�
		todoContent.setText(vo.getContent());
		todoTitle.setText(vo.getTitle());
	}
	
}
