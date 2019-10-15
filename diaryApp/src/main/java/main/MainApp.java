package main;
	
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import domain.UserVO;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import views.LoginController;
import views.MainController;
import views.MasterController;
import views.RegisterController;
import views.TodoController;


public class MainApp extends Application {
	public static MainApp app;
	
	private StackPane mainPage;
	
	//��Ʈ�ѷ����� �����ϱ� ���� ���� �����.
	private Map<String, MasterController> controllerMap = new HashMap<>();
	
	@Override
	public void start(Stage primaryStage) {
		app = this; //�̱������� �ۼ�
		try {
			FXMLLoader mainLoader = new FXMLLoader();
			mainLoader.setLocation(getClass().getResource("/views/MainLayout.fxml"));
			mainPage = mainLoader.load();
			
			//���� ��Ʈ�ѷ��� �����ͼ� ��Ʈ�ѷ� �ʿ� �־��ش�.
			MainController mc = mainLoader.getController();
			mc.setRoot(mainPage);
			controllerMap.put("main", mc);
			
			FXMLLoader loginLoader = new FXMLLoader();
			loginLoader.setLocation(getClass().getResource("/views/LoginLayout.fxml"));
			AnchorPane loginPage = loginLoader.load();
			
			//�α��� ��Ʈ�ѷ��� �����ͼ� ��Ʈ�ѷ� �ʿ� �ִ´�.
			LoginController lc = loginLoader.getController();
			lc.setRoot(loginPage);
			controllerMap.put("login", lc); 
			
			FXMLLoader registerLoader = new FXMLLoader();
			registerLoader.setLocation(getClass().getResource("/views/RegisterLayout.fxml"));
			AnchorPane registerPage = registerLoader.load();
			
			//ȸ������ ��Ʈ�ѷ��� �����ͼ� ��Ʈ�ѷ� �ʿ� �ִ´�.
			RegisterController rc = registerLoader.getController();
			rc.setRoot(registerPage);
			controllerMap.put("register", rc);
			
			FXMLLoader todoLoader = new FXMLLoader();
			todoLoader.setLocation(getClass().getResource("/views/TodoLayout.fxml"));
			AnchorPane todoPage = todoLoader.load();
			TodoController tc = todoLoader.getController();
			tc.setRoot(todoPage);
			controllerMap.put("todo", tc); 
			
			
			Scene scene = new Scene(mainPage);
			scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
			
			mainPage.getChildren().add(loginPage);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void setLoginInfo(UserVO user) {
		MainController mc = (MainController)controllerMap.get("main");
		mc.setLoginInfo(user);
	}
	
	//������ �������� �ε��ϴ� ��
	public void loadPane(String name) {
		MasterController c = controllerMap.get(name); //������ ��Ʈ�ѷ��� �ʿ��� ������.
		Pane pane = c.getRoot();
		mainPage.getChildren().add(pane);
		
		pane.setTranslateX(-800); //�������� ������ ����ȭ��Ų �� �ִϸ��̼� ����
		pane.setOpacity(0);
		
		Timeline timeline = new Timeline();
		KeyValue toRight = new KeyValue(pane.translateXProperty(), 0);
		KeyValue fadeOut = new KeyValue(pane.opacityProperty(), 1);
		KeyFrame keyFrame = new KeyFrame(Duration.millis(500), toRight, fadeOut);
		
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
	}
	
	public void slideOut(Pane pane) {
		Timeline timeline = new Timeline();
		KeyValue toRight = new KeyValue(pane.translateXProperty(), 800);
		KeyValue fadeOut = new KeyValue(pane.opacityProperty(), 0);
		
		KeyFrame keyFrame = new KeyFrame(Duration.millis(500), (e) -> {
			mainPage.getChildren().remove(pane);
		}, toRight, fadeOut);
		
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
	}
	
	public void setFocus(LocalDate date) {
		MainController mc = (MainController)controllerMap.get("main");
		mc.setClickData(date);
	}
	
	public void loadTodoData(LocalDate date) {
		TodoController tc = (TodoController)controllerMap.get("todo");
		tc.init(date);
	}
	
	public UserVO getLoginUser() {
		//���� �α��ε� ���������� �����´�.
		MainController mc = (MainController)controllerMap.get("main");
		return mc.getUser();
	}
}

