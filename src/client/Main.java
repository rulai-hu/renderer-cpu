package client;

	// You may add files to the windowing package, but you must leave all files
	// that are already present unchanged, except for:
	// 		Main.java (this file)
	//		drawable/Drawables.java

	// Also, do not instantiate Image361 yourself.

import javafx.stage.*;
import windowing.Window361;
import windowing.drawable.Drawable;

import java.util.List;

import javafx.application.Application;

public class Main extends Application {

	public static void main(String[] args) {
        launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		List<String> arguments = getParameters().getUnnamed();

		Window361 window = new Window361(primaryStage);
		Drawable drawable = window.getDrawable();
		
		Client client = new Client(drawable, arguments);
		window.setPageTurner(client);
		client.nextPage();
		
		primaryStage.show();
	}
}
