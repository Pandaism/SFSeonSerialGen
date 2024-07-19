module com.pandaism.sfseonserialgen.sfseonserialgen {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires java.sql;
    requires ucanaccess;

    opens com.pandaism.sfseonserialgen.sfseonserialgen to javafx.fxml;
    exports com.pandaism.sfseonserialgen.sfseonserialgen;
    exports com.pandaism.sfseonserialgen.sfseonserialgen.application;
    opens com.pandaism.sfseonserialgen.sfseonserialgen.application to javafx.fxml;

    opens com.pandaism.sfseonserialgen.sfseonserialgen.application.ui to javafx.fxml;
    exports com.pandaism.sfseonserialgen.sfseonserialgen.application.ui;
    opens com.pandaism.sfseonserialgen.sfseonserialgen.util to javafx.fxml;
    exports com.pandaism.sfseonserialgen.sfseonserialgen.util;
}