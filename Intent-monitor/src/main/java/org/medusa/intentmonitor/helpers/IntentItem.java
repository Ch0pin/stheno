package org.medusa.intentmonitor.helpers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class IntentItem {
    private final SimpleIntegerProperty index;
    private final SimpleStringProperty description;
    private final SimpleStringProperty destinationPackage;
    private final SimpleStringProperty destinationClass;
    private final SimpleBooleanProperty targetIsExported;

    public IntentItem(int index, String description, String destinationPackage, String destinationClass, Boolean exported) {
        this.index = new SimpleIntegerProperty(index);
        this.description = new SimpleStringProperty(description);
        this.targetIsExported = new SimpleBooleanProperty(exported);
        this.destinationPackage = new SimpleStringProperty(destinationPackage);
        this.destinationClass = new SimpleStringProperty(destinationClass);
    }

    public int getIndex() {
        return index.get();
    }

    public String getDescription() {
        return description.get();
    }

    public Boolean getTargetIsExported() {
        return targetIsExported.get();
    }

    public SimpleBooleanProperty targetIsExportedProperty(){
        return targetIsExported;
    }

    public SimpleIntegerProperty indexProperty() {
        return index;
    }


    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleStringProperty destinationPackageProperty() {
        return destinationPackage;
    }

    public SimpleStringProperty destinationClassProperty() {
        return destinationClass;
    }
}
