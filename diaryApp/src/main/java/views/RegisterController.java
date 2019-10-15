package views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import main.MainApp;
import util.JDBCUtil;
import util.Util;

public class RegisterController extends MasterController{
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private PasswordField pass;
	@FXML
	private PasswordField passConfirm;
	@FXML
	private TextArea txtInfo;
	
	@FXML
	private AnchorPane registerPage;
	
	public void register() {
		String id = txtId.getText().trim();
		String name = txtName.getText().trim();
		String strPass = pass.getText().trim();
		String confirm = passConfirm.getText().trim();
		String info = txtInfo.getText().trim();
		
		if(id.isEmpty() || name.isEmpty() || strPass.isEmpty() || info.isEmpty()) {
			Util.showAlert("�������", "�ʼ� �Է�â�� ����ֽ��ϴ�.", AlertType.INFORMATION);
			return;
		}
		
		if(!strPass.equals(confirm)) {
			Util.showAlert("����ġ", "��й�ȣ�� ��й�ȣ Ȯ���� ��ġ���� �ʽ��ϴ�.", AlertType.INFORMATION);
			return;
		}
		
		if (!id.matches("[a-zA-Z0-9]{4,8}")) {
			Util.showAlert("����ġ", "���̵�� ���� �� ���ڷθ� �̷������ 4���� �̻� 8���� ���Ϸ� ������ �մϴ�.", AlertType.INFORMATION);
			return;
		}
		
		if(!strPass.matches("[a-zA-Z0-9]{8,}")) {
			Util.showAlert("����ġ", "��й�ȣ�� �ּ� 8�����̻��� ���� �� ������������ �̷������ �մϴ�.", AlertType.INFORMATION);
			return;
		}
		
		if(!name.matches("[��-�R]{2,}")) {
			Util.showAlert("����ġ", "�̸��� �ѱ۷� �ּ� 2���� �̻��̾�� �մϴ�.", AlertType.INFORMATION);
			return;
		}
		
		
		Connection con = JDBCUtil.getConnection(); //�����ͺ��̽� ���� ��������
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sqlExist = "SELECT * FROM diary_users WHERE id = ?"; //�ش� ������ �����ϴ��� Ȯ���ϱ� ���� sql
		String sqlInsert = "INSERT INTO diary_users(`id`, `name`, `pass`, `info`) VALUES (?, ?, PASSWORD(?), ?)";
		
		try {
			pstmt = con.prepareStatement(sqlExist); //�����ϴ��� Ȯ���ϴ� sql�� �غ��ϰ�
			pstmt.setString(1, id);
			rs = pstmt.executeQuery(); 
			if(rs.next()) { //�̹� �ٸ� ������ ������.
				Util.showAlert("ȸ�� �ߺ�", "�̹� �ش� id�� ������ �����մϴ�. �ٸ� ���̵� ����ϼ���.", AlertType.INFORMATION);
				return;
			}
			
			JDBCUtil.close(pstmt);
			
			pstmt = con.prepareStatement(sqlInsert);
			pstmt.setString(1, id);
			pstmt.setString(2, name);
			pstmt.setString(3, strPass);
			pstmt.setString(4, info);
			
			if( pstmt.executeUpdate() != 1) {
				Util.showAlert("����", "�����ͺ��̽��� ȸ�������� �ùٸ��� �Էµ��� �ʾҽ��ϴ�. �Է°��� Ȯ���ϼ���.", AlertType.ERROR);
				return;
			}
			
			Util.showAlert("����", "ȸ�������� �Ϸ�Ǿ����ϴ�. �α������ּ���.", AlertType.INFORMATION);
			MainApp.app.slideOut(getRoot()); //��Ʈ�� ��Ƽ� �����̵� �ƿ� ��Ŵ.
			
		} catch (Exception e) {
			e.printStackTrace();
			Util.showAlert("����", "�����ͺ��̽� ������ ������ �߻��߽��ϴ�. ���ͳ� ������ Ȯ���ϼ���.", AlertType.ERROR);
			return;
		}finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}
	
	public void cancel() {
		MainApp.app.slideOut(getRoot()); //��Ʈ�� ��Ƽ� �����̵� �ƿ� ��Ŵ.
	}
}
