package app.subd.components;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVExportUtil {

    public static <T> void exportTableToCSV(TableView<T> tableView, String title, Stage stage) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить отчет");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );

            String fileName = title.replaceAll("[^a-zA-Zа-яА-Я0-9]", "_") +
                             "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
            fileChooser.setInitialFileName(fileName);

            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                exportToCSV(tableView, title, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> void exportToCSV(TableView<T> tableView, String title, File file) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write('\uFEFF'); // BOM for Excel
            writer.write(title + "\n");
            writer.write("Дата экспорта: " +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + "\n\n");

            List<String> header = new ArrayList<>();
            for (TableColumn<T, ?> column : tableView.getColumns()) {
                header.add("\"" + column.getText() + "\"");
            }
            writer.write(String.join(";", header) + "\n");

            ObservableList<T> items = tableView.getItems();
            for (T item : items) {
                List<String> row = new ArrayList<>();
                for (TableColumn<T, ?> column : tableView.getColumns()) {
                    Object value = column.getCellObservableValue(item).getValue();
                    if (value != null) {
                        String cellValue = value.toString().replace("\"", "\"\"");
                        row.add("\"" + cellValue + "\"");
                    } else {
                        row.add("\"\"");
                    }
                }
                writer.write(String.join(";", row) + "\n");
            }

            writer.write("\nВсего записей:;" + items.size() + "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}