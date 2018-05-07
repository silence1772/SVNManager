package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * 程序的启动入口类
 *
 * @author wuzewei
 */
public class Main extends Application {
	private Controller controller;

	/**
	 * 设置程序窗口界面
	 *
	 * @param primaryStage
	 *            主舞台
	 */
	@Override
	public void start(Stage primaryStage) {
		controller = new Controller();
		StackPane root = new StackPane(controller); // 设置根结点
		Scene scene = new Scene(root, 980, 660);
		scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm()); // 添加样式文件
		primaryStage.setTitle("SVN作业管理系统");
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/resources/logo_01.png"))); // 设置程序图标
		primaryStage.show();
	}

	/**
	 * 启动程序
	 *
	 * @param args
	 *            可选参数
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
