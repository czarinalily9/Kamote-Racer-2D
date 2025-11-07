module com.nesteatea.p1.kamoteracer2d {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.nesteatea.p1.kamoteracer2d to javafx.fxml;
    exports com.nesteatea.p1.kamoteracer2d;
}