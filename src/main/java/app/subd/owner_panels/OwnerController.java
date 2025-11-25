// app/subd/owner_panels/OwnerController.java
package app.subd.owner_panels;

import app.subd.components.Session;
import app.subd.config.TableConfig;
import app.subd.config.ColumnConfig;
import app.subd.models.ReportModels.*;
import app.subd.components.ReportService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.Arrays;

public class OwnerController {

    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        statusLabel.setText("Владелец: " + Session.getUsername());
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clear();
            Parent root = FXMLLoader.load(getClass().getResource("/app/subd/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Авторизация");
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.setScene(new Scene(root, 400, 300));
            stage.show();

            Stage currentStage = (Stage) mainTabPane.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Основные финансовые отчеты
    @FXML
    private void showDetailedIncomeReport() {
        TableConfig config = new TableConfig(
                "Детализированный отчет о доходах",
                (filters) -> {
                    ObservableList<Object> data = javafx.collections.FXCollections.observableArrayList();
                    data.addAll(ReportService.getDetailedIncomeReport());
                    return data;
                },
                null, // onAdd - нет возможности добавления
                null, // onEdit - нет возможности редактирования
                null, // onRefresh
                null, // onBooking
                Arrays.asList(
                        new ColumnConfig("hotelAddress", "Адрес отеля", 200),
                        new ColumnConfig("roomNumber", "Номер комнаты", 120),
                        new ColumnConfig("baseRoomIncome", "Доход от номера", 150),
                        new ColumnConfig("conveniencesIncome", "Доход от удобств", 150),
                        new ColumnConfig("servicesIncome", "Доход от услуг", 150),
                        new ColumnConfig("totalIncome", "Общий доход", 150)
                ),
                null, // filters
                null  // onToggleActive
        );

        openReportWindow(config, "Детализированный отчет о доходах");
    }

    @FXML
    private void showHotelIncomeSummary() {
        TableConfig config = new TableConfig(
                "Сводка по доходам отелей",
                (filters) -> {
                    ObservableList<Object> data = javafx.collections.FXCollections.observableArrayList();
                    data.addAll(ReportService.getHotelIncomeSummary());
                    return data;
                },
                null, null, null, null,
                Arrays.asList(
                        new ColumnConfig("hotelAddress", "Адрес отеля", 200),
                        new ColumnConfig("baseRoomIncome", "Доход от номеров", 150),
                        new ColumnConfig("conveniencesIncome", "Доход от удобств", 150),
                        new ColumnConfig("servicesIncome", "Доход от услуг", 150),
                        new ColumnConfig("totalIncome", "Общий доход", 150),
                        new ColumnConfig("totalBookings", "Всего бронирований", 120)
                ),
                null, null
        );

        openReportWindow(config, "Сводка по доходам отелей");
    }

    @FXML
    private void showIncomeBreakdown() {
        TableConfig config = new TableConfig(
                "Разбивка доходов по категориям",
                (filters) -> {
                    ObservableList<Object> data = javafx.collections.FXCollections.observableArrayList();
                    data.addAll(ReportService.getIncomeBreakdownByCategory());
                    return data;
                },
                null, null, null, null,
                Arrays.asList(
                        new ColumnConfig("categoryType", "Категория", 200),
                        new ColumnConfig("subcategory", "Подкатегория", 200),
                        new ColumnConfig("income", "Доход", 150),
                        new ColumnConfig("percentage", "Процент", 100)
                ),
                null, null
        );

        openReportWindow(config, "Разбивка доходов по категориям");
    }

    // Отчеты с графиками
    @FXML
    private void showMonthlyIncomeTrendWithChart() {
        try {
            ObservableList<MonthlyIncomeTrend> data = ReportService.getMonthlyIncomeTrend();

            Stage stage = new Stage();
            stage.setTitle("Тренды доходов по месяцам");

            // Создаем таблицу
            TableView<MonthlyIncomeTrend> tableView = new TableView<>();

            TableColumn<MonthlyIncomeTrend, String> monthCol = new TableColumn<>("Месяц");
            monthCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("yearMonth"));

            TableColumn<MonthlyIncomeTrend, BigDecimal> baseCol = new TableColumn<>("Доход от номеров");
            baseCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("baseRoomIncome"));

            TableColumn<MonthlyIncomeTrend, BigDecimal> totalCol = new TableColumn<>("Общий доход");
            totalCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totalIncome"));

            TableColumn<MonthlyIncomeTrend, BigDecimal> growthCol = new TableColumn<>("Рост");
            growthCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("incomeGrowth"));

            tableView.getColumns().addAll(monthCol, baseCol, totalCol, growthCol);
            tableView.setItems(data);

            // Создаем график
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Динамика доходов по месяцам");

            XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
            totalSeries.setName("Общий доход");

            XYChart.Series<String, Number> baseSeries = new XYChart.Series<>();
            baseSeries.setName("Доход от номеров");

            for (MonthlyIncomeTrend trend : data) {
                totalSeries.getData().add(new XYChart.Data<>(trend.getYearMonth(), trend.getTotalIncome()));
                baseSeries.getData().add(new XYChart.Data<>(trend.getYearMonth(), trend.getBaseRoomIncome()));
            }

            lineChart.getData().addAll(totalSeries, baseSeries);

            // Размещаем в SplitPane
            SplitPane splitPane = new SplitPane();
            splitPane.getItems().addAll(tableView, lineChart);
            splitPane.setDividerPositions(0.5);

            Scene scene = new Scene(splitPane, 1000, 600);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить данные для графика: " + e.getMessage());
        }
    }

    @FXML
    private void showHotelKPIDashboard() {
        try {
            ObservableList<HotelKPI> kpis = ReportService.getHotelKPI();

            Stage stage = new Stage();
            stage.setTitle("KPI Отелей - Панель управления");

            // Создаем сетку для KPI
            GridPane kpiGrid = new GridPane();
            kpiGrid.setHgap(20);
            kpiGrid.setVgap(15);
            kpiGrid.setPadding(new javafx.geometry.Insets(20));
            kpiGrid.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1;");

            int row = 0;
            int col = 0;
            for (HotelKPI kpi : kpis) {
                VBox kpiBox = createKPIBox(kpi);
                kpiGrid.add(kpiBox, col, row);
                col++;
                if (col > 1) {
                    col = 0;
                    row++;
                }
            }

            // Создаем круговую диаграмму распределения доходов
            PieChart revenueChart = createRevenueDistributionChart();

            // Создаем столбчатую диаграмму для KPI
            BarChart<String, Number> kpiChart = createKPIChart(kpis);

            VBox chartsBox = new VBox(20);
            chartsBox.getChildren().addAll(revenueChart, kpiChart);

            TabPane tabPane = new TabPane();
            Tab kpiTab = new Tab("Ключевые показатели", kpiGrid);
            Tab chartTab = new Tab("Визуализация", chartsBox);

            tabPane.getTabs().addAll(kpiTab, chartTab);

            Scene scene = new Scene(tabPane, 800, 600);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить KPI: " + e.getMessage());
        }
    }

    private VBox createKPIBox(HotelKPI kpi) {
        VBox box = new VBox(5);
        box.setPadding(new javafx.geometry.Insets(15));
        box.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        box.setPrefSize(200, 100);

        Label nameLabel = new Label(kpi.getMetricName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        nameLabel.setWrapText(true);

        Label valueLabel = new Label(kpi.getMetricValue() + " " + kpi.getMetricUnit());
        valueLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        box.getChildren().addAll(nameLabel, valueLabel);
        return box;
    }

    private PieChart createRevenueDistributionChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Распределение доходов по категориям");

        try {
            ObservableList<IncomeBreakdown> breakdown = ReportService.getIncomeBreakdownByCategory();

            for (IncomeBreakdown item : breakdown) {
                if ("Всего".equals(item.getSubcategory())) {
                    PieChart.Data slice = new PieChart.Data(
                            item.getCategoryType(),
                            item.getIncome().doubleValue()
                    );
                    pieChart.getData().add(slice);
                }
            }
        } catch (Exception e) {
            // Если данные недоступны, создаем демо-данные
            pieChart.getData().addAll(
                    new PieChart.Data("Номера", 65),
                    new PieChart.Data("Удобства", 20),
                    new PieChart.Data("Услуги", 15)
            );
        }

        return pieChart;
    }

    private BarChart<String, Number> createKPIChart(ObservableList<HotelKPI> kpis) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Сравнение ключевых показателей");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Значения показателей");

        for (HotelKPI kpi : kpis) {
            if (!"%".equals(kpi.getMetricUnit())) { // Исключаем проценты для лучшего отображения
                series.getData().add(new XYChart.Data<>(kpi.getMetricName(), kpi.getMetricValue()));
            }
        }

        barChart.getData().add(series);
        return barChart;
    }

    // Аналитические отчеты
    @FXML
    private void showCustomerSegmentation() {
        TableConfig config = new TableConfig(
                "Сегментация клиентов",
                (filters) -> {
                    ObservableList<Object> data = javafx.collections.FXCollections.observableArrayList();
                    data.addAll(ReportService.getCustomerSegmentation());
                    return data;
                },
                null, null, null, null,
                Arrays.asList(
                        new ColumnConfig("customerSegment", "Сегмент клиента", 150),
                        new ColumnConfig("customerCount", "Количество клиентов", 120),
                        new ColumnConfig("avgBaseSpent", "Средние траты на номера", 180),
                        new ColumnConfig("avgConveniencesSpent", "Средние траты на удобства", 180),
                        new ColumnConfig("avgServicesSpent", "Средние траты на услуги", 180),
                        new ColumnConfig("avgTotalSpent", "Средние общие траты", 150)
                ),
                null, null
        );

        openReportWindow(config, "Сегментация клиентов");
    }

    @FXML
    private void showHighIncomeRooms() {
        // Диалог для ввода порога дохода
        TextInputDialog dialog = new TextInputDialog("10000");
        dialog.setTitle("Высокодоходные номера");
        dialog.setHeaderText("Введите минимальный порог дохода");
        dialog.setContentText("Порог дохода:");

        dialog.showAndWait().ifPresent(thresholdStr -> {
            try {
                BigDecimal threshold = new BigDecimal(thresholdStr);
                ObservableList<HighIncomeRoom> data = ReportService.getHighIncomeRooms(threshold);

                Stage stage = new Stage();
                stage.setTitle("Высокодоходные номера (порог: " + threshold + ")");

                TableView<HighIncomeRoom> tableView = new TableView<>();

                TableColumn<HighIncomeRoom, Integer> roomCol = new TableColumn<>("Номер");
                roomCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("roomNumber"));

                TableColumn<HighIncomeRoom, String> typeCol = new TableColumn<>("Тип");
                typeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("roomType"));

                TableColumn<HighIncomeRoom, BigDecimal> totalCol = new TableColumn<>("Общий доход");
                totalCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totalIncome"));

                TableColumn<HighIncomeRoom, Double> occupancyCol = new TableColumn<>("Загрузка (%)");
                occupancyCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("occupancyRate"));

                tableView.getColumns().addAll(roomCol, typeCol, totalCol, occupancyCol);
                tableView.setItems(data);

                // Создаем график
                BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
                chart.setTitle("Доходы по номерам");

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Общий доход");

                for (HighIncomeRoom room : data) {
                    series.getData().add(new XYChart.Data<>("Комн. " + room.getRoomNumber(), room.getTotalIncome()));
                }

                chart.getData().add(series);

                SplitPane splitPane = new SplitPane();
                splitPane.getItems().addAll(tableView, chart);
                splitPane.setDividerPositions(0.5);

                Scene scene = new Scene(splitPane, 900, 600);
                stage.setScene(scene);
                stage.show();

            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Введите корректное числовое значение для порога дохода");
            }
        });
    }

    @FXML
    private void showRoomEfficiency() {
        // Диалог для ввода параметров
        Dialog<RoomEfficiencyParams> dialog = new Dialog<>();
        dialog.setTitle("Анализ эффективности номеров");
        dialog.setHeaderText("Введите параметры анализа");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField minOccupancyField = new TextField("50");
        TextField minIncomeField = new TextField("5000");

        grid.add(new Label("Минимальная загрузка (%):"), 0, 0);
        grid.add(minOccupancyField, 1, 0);
        grid.add(new Label("Минимальный доход:"), 0, 1);
        grid.add(minIncomeField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType analyzeButton = new ButtonType("Анализировать", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(analyzeButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == analyzeButton) {
                try {
                    double minOccupancy = Double.parseDouble(minOccupancyField.getText());
                    BigDecimal minIncome = new BigDecimal(minIncomeField.getText());
                    return new RoomEfficiencyParams(minOccupancy, minIncome);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(params -> {
            ObservableList<RoomEfficiency> data = ReportService.getRoomEfficiencyAnalysis(
                    params.minOccupancy, params.minIncome);

            TableConfig config = new TableConfig(
                    "Анализ эффективности номеров",
                    (filters) -> {
                        ObservableList<Object> result = javafx.collections.FXCollections.observableArrayList();
                        result.addAll(data);
                        return result;
                    },
                    null, null, null, null,
                    Arrays.asList(
                            new ColumnConfig("roomNumber", "Номер", 80),
                            new ColumnConfig("roomType", "Тип", 150),
                            new ColumnConfig("totalIncome", "Общий доход", 120),
                            new ColumnConfig("occupancyRate", "Загрузка (%)", 100),
                            new ColumnConfig("efficiencyRating", "Эффективность", 120)
                    ),
                    null, null
            );

            openReportWindow(config, "Анализ эффективности номеров");
        });
    }

    // Вспомогательные классы и методы
    private static class RoomEfficiencyParams {
        final double minOccupancy;
        final BigDecimal minIncome;

        RoomEfficiencyParams(double minOccupancy, BigDecimal minIncome) {
            this.minOccupancy = minOccupancy;
            this.minIncome = minIncome;
        }
    }

    private void openReportWindow(TableConfig config, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/subd/tables/universal_table.fxml"));
            Parent root = loader.load();

            app.subd.components.UniversalTableController controller = loader.getController();
            controller.configure(config);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 1000, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть отчет: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Дополнительные отчеты
    @FXML
    private void showIncomeByCity() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Доходы по городам");
        dialog.setHeaderText("Введите шаблон названия города");
        dialog.setContentText("Город:");

        dialog.showAndWait().ifPresent(cityPattern -> {
            ObservableList<IncomeByCity> data = ReportService.getIncomeByCity(cityPattern);

            TableConfig config = new TableConfig(
                    "Доходы по городам: " + cityPattern,
                    (filters) -> {
                        ObservableList<Object> result = javafx.collections.FXCollections.observableArrayList();
                        result.addAll(data);
                        return result;
                    },
                    null, null, null, null,
                    Arrays.asList(
                            new ColumnConfig("cityName", "Город", 150),
                            new ColumnConfig("totalIncome", "Общий доход", 150),
                            new ColumnConfig("baseRoomIncome", "Доход от номеров", 150),
                            new ColumnConfig("conveniencesIncome", "Доход от удобств", 150),
                            new ColumnConfig("servicesIncome", "Доход от услуг", 150),
                            new ColumnConfig("averageStayDuration", "Средняя продолжительность", 180)
                    ),
                    null, null
            );

            openReportWindow(config, "Доходы по городам: " + cityPattern);
        });
    }

    @FXML
    private void showPremiumServices() {
        Dialog<ServiceParams> dialog = new Dialog<>();
        dialog.setTitle("Премиальные услуги");
        dialog.setHeaderText("Введите параметры для анализа услуг");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField minUsageField = new TextField("10");
        TextField minIncomeField = new TextField("1000");

        grid.add(new Label("Минимальное использование:"), 0, 0);
        grid.add(minUsageField, 1, 0);
        grid.add(new Label("Минимальный доход:"), 0, 1);
        grid.add(minIncomeField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType analyzeButton = new ButtonType("Анализировать", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(analyzeButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == analyzeButton) {
                try {
                    int minUsage = Integer.parseInt(minUsageField.getText());
                    BigDecimal minIncome = new BigDecimal(minIncomeField.getText());
                    return new ServiceParams(minUsage, minIncome);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(params -> {
            ObservableList<PremiumService> data = ReportService.getPremiumServices(
                    params.minUsage, params.minIncome);

            TableConfig config = new TableConfig(
                    "Премиальные услуги",
                    (filters) -> {
                        ObservableList<Object> result = javafx.collections.FXCollections.observableArrayList();
                        result.addAll(data);
                        return result;
                    },
                    null, null, null, null,
                    Arrays.asList(
                            new ColumnConfig("serviceName", "Услуга", 200),
                            new ColumnConfig("usageCount", "Количество использований", 150),
                            new ColumnConfig("totalIncome", "Общий доход", 150),
                            new ColumnConfig("avgIncomePerUse", "Средний доход за использование", 180)
                    ),
                    null, null
            );

            openReportWindow(config, "Премиальные услуги");
        });
    }

    private static class ServiceParams {
        final int minUsage;
        final BigDecimal minIncome;

        ServiceParams(int minUsage, BigDecimal minIncome) {
            this.minUsage = minUsage;
            this.minIncome = minIncome;
        }
    }

    @FXML
    private void showIncomeSources() {
        TableConfig config = new TableConfig(
                "Все источники доходов",
                (filters) -> {
                    ObservableList<Object> data = javafx.collections.FXCollections.observableArrayList();
                    data.addAll(ReportService.getCombinedIncomeSources());
                    return data;
                },
                null, null, null, null,
                Arrays.asList(
                        new ColumnConfig("incomeSource", "Источник дохода", 250),
                        new ColumnConfig("sourceType", "Тип источника", 150),
                        new ColumnConfig("amount", "Сумма", 150)
                ),
                null, null
        );

        openReportWindow(config, "Все источники доходов");
    }
}