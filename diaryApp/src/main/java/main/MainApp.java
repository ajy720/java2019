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
	
	//컨트롤러들을 저장하기 위한 맵을 만든다.
	private Map<String, MasterController> controllerMap = new HashMap<>();
	
	@Override
	public void start(Stage primaryStage) {
		app = this; //싱글톤으로 작성
		try {
			FXMLLoader mainLoader = new FXMLLoader();
			mainLoader.setLocation(getClass().getResource("/views/MainLayout.fxml"));
			mainPage = mainLoader.load();
			
			//메인 컨트롤러를 가져와서 컨트롤러 맵에 넣어준다.
			MainController mc = mainLoader.getController();
			mc.setRoot(mainPage);
			controllerMap.put("main", mc);
			
			FXMLLoader loginLoader = new FXMLLoader();
			loginLoader.setLocation(getClass().getResource("/views/LoginLayout.fxml"));
			AnchorPane loginPage = loginLoader.load();
			
			//로그인 컨트롤러를 가져와서 컨트롤러 맵에 넣는다.
			LoginController lc = loginLoader.getController();
			lc.setRoot(loginPage);
			controllerMap.put("login", lc); 
			
			FXMLLoader registerLoader = new FXMLLoader();
			registerLoader.setLocation(getClass().getResource("/views/RegisterLayout.fxml"));
			AnchorPane registerPage = registerLoader.load();
			
			//회원가입 컨트롤러를 가져와서 컨트롤러 맵에 넣는다.
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
	
	//지정한 페이지를 로딩하는 것
	public void loadPane(String name) {
		MasterController c = controllerMap.get(name); //지정한 컨트롤러를 맵에서 꺼낸다.
		Pane pane = c.getRoot();
		mainPage.getChildren().add(pane);
		
		pane.setTranslateX(-800); //왼쪽으로 보내고 투명화시킨 뒤 애니메이션 시작
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
		//현재 로그인된 유저정보를 가져온다.
		MainController mc = (MainController)controllerMap.get("main");
		return mc.getUser();
	}
}

