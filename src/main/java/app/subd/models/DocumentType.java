package app.subd.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;

public enum DocumentType {
    PASSPORT_RF ("Паспорт РФ"),
    FOREIGN_PASSPORT ("Паспорт иностранного гражданина"),
    BIRTH_CERTIFICATE ("Свидетельство о рождении"),
    TEMPORARY_ID_CARD ("Временное удостоверение"),
    INTERNATIONAL_PASSPORT_RF ("Загранпаспорт РФ"),
    NOT_SPECIFIED ("Не указан");

    private final String description;

    DocumentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static DocumentType getDocumentType(String status) {
        if (status == null) {
            return DocumentType.NOT_SPECIFIED;
        }
        for (DocumentType documentType : DocumentType.values()) {
            if (documentType.getDescription().equals(status)) {
                return documentType;
            }
        }
        return null;
    }

    public static ObservableList<Object> getDocumentTypeValues() {
        final ObservableList<Object> documentTypeValues = FXCollections.observableArrayList();
        documentTypeValues.addAll(Arrays.asList(DocumentType.values()));
        return documentTypeValues;
    }

    @Override
    public String toString() {
        return description;
    }
}
