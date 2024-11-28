module LoginSystem {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.base;
	requires java.sql;
	requires java.desktop;
	requires com.h2database;
	requires org.junit.jupiter.api;
	
	opens MAIN to javafx.graphics, javafx.fxml;
}
