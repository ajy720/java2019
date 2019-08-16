package net.gondr.tetris;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class App extends Application
{
	public static App app; //싱글톤 기법을 활용하기 위한 스태틱 변수
	
	public Game game;
	
    public static void main( String[] args )
    {
    	launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			app = this; //스태틱 변수에 자기자신을 넣어서 싱글톤으로 활용함.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/net/gondr/views/Main.fxml"));
			AnchorPane anchorPane = (AnchorPane)loader.load();
			
			Scene scene = new Scene(anchorPane);
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
