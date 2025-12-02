// app/subd/owner_panels/OwnerController.java
package app.subd.owner_panels;

import app.subd.components.CSVExportUtil;
import app.subd.components.Session;
import app.subd.models.ReportModels.*;
import app.subd.components.ReportService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class OwnerController {

    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        statusLabel.setText("Владелец: " + Session.getUsername());
    }

    private <T> VBox createTableWithExportVBox(TableView<T> tableView, String title) {
        Button exportButton = new Button("Экспорт в CSV");
        exportButton.setOnAction(e -> CSVExportUtil.exportTableToCSV(tableView, title, (Stage) exportButton.getScene().getWindow()));

        VBox vbox = new VBox(10, tableView, exportButton);
        vbox.setPadding(new Insets(10));
        VBox.setVgrow(tableView, Priority.ALWAYS);
        return vbox;
    }

    private <T, N extends Number> void setNumericCellFactory(TableColumn<T, N> column) {
        DecimalFormat df = new DecimalFormat("#,##0.000");
        df.setRoundingMode(RoundingMode.HALF_UP);

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(N item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(df.format(item));
                }
            }
        });
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
        ObservableList<DetailedIncomeReport> data = ReportService.getDetailedIncomeReport();
        TableView<DetailedIncomeReport> tableView = new TableView<>(data);

        TableColumn<DetailedIncomeReport, String> hotelCol = new TableColumn<>("Адрес отеля");
        hotelCol.setCellValueFactory(new PropertyValueFactory<>("hotelAddress"));
        TableColumn<DetailedIncomeReport, Integer> roomCol = new TableColumn<>("Номер комнаты");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        TableColumn<DetailedIncomeReport, BigDecimal> baseIncomeCol = new TableColumn<>("Доход от номера");
        baseIncomeCol.setCellValueFactory(new PropertyValueFactory<>("baseRoomIncome"));
        setNumericCellFactory(baseIncomeCol);
        TableColumn<DetailedIncomeReport, BigDecimal> convIncomeCol = new TableColumn<>("Доход от удобств");
        convIncomeCol.setCellValueFactory(new PropertyValueFactory<>("conveniencesIncome"));
        setNumericCellFactory(convIncomeCol);
        TableColumn<DetailedIncomeReport, BigDecimal> servIncomeCol = new TableColumn<>("Доход от услуг");
        servIncomeCol.setCellValueFactory(new PropertyValueFactory<>("servicesIncome"));
        setNumericCellFactory(servIncomeCol);
        TableColumn<DetailedIncomeReport, BigDecimal> totalIncomeCol = new TableColumn<>("Общий доход");
        totalIncomeCol.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
        setNumericCellFactory(totalIncomeCol);

        tableView.getColumns().addAll(hotelCol, roomCol, baseIncomeCol, convIncomeCol, servIncomeCol, totalIncomeCol);
        openWindow(createTableWithExportVBox(tableView, "Детализированный отчет о доходах"), "Детализированный отчет о доходах", 1000, 600);
    }

    @FXML
    private void showHotelIncomeSummary() {
        ObservableList<HotelIncomeSummary> data = ReportService.getHotelIncomeSummary();
        TableView<HotelIncomeSummary> tableView = new TableView<>(data);

        TableColumn<HotelIncomeSummary, String> hotelCol = new TableColumn<>("Адрес отеля");
        hotelCol.setCellValueFactory(new PropertyValueFactory<>("hotelAddress"));
        TableColumn<HotelIncomeSummary, BigDecimal> baseIncomeCol = new TableColumn<>("Доход от номеров");
        baseIncomeCol.setCellValueFactory(new PropertyValueFactory<>("baseRoomIncome"));
        setNumericCellFactory(baseIncomeCol);
        TableColumn<HotelIncomeSummary, BigDecimal> convIncomeCol = new TableColumn<>("Доход от удобств");
        convIncomeCol.setCellValueFactory(new PropertyValueFactory<>("conveniencesIncome"));
        setNumericCellFactory(convIncomeCol);
        TableColumn<HotelIncomeSummary, BigDecimal> servIncomeCol = new TableColumn<>("Доход от услуг");
        servIncomeCol.setCellValueFactory(new PropertyValueFactory<>("servicesIncome"));
        setNumericCellFactory(servIncomeCol);
        TableColumn<HotelIncomeSummary, BigDecimal> totalIncomeCol = new TableColumn<>("Общий доход");
        totalIncomeCol.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
        setNumericCellFactory(totalIncomeCol);
        TableColumn<HotelIncomeSummary, Long> bookingsCol = new TableColumn<>("Всего бронирований");
        bookingsCol.setCellValueFactory(new PropertyValueFactory<>("totalBookings"));

        tableView.getColumns().addAll(hotelCol, baseIncomeCol, convIncomeCol, servIncomeCol, totalIncomeCol, bookingsCol);
        openWindow(createTableWithExportVBox(tableView, "Сводка по доходам отелей"), "Сводка по доходам отелей", 1000, 600);
    }

    @FXML
    private void showIncomeBreakdown() {
        ObservableList<IncomeBreakdown> data = ReportService.getIncomeBreakdownByCategory();
        TableView<IncomeBreakdown> tableView = new TableView<>(data);

        TableColumn<IncomeBreakdown, String> catCol = new TableColumn<>("Категория");
        catCol.setCellValueFactory(new PropertyValueFactory<>("categoryType"));
        TableColumn<IncomeBreakdown, String> subcatCol = new TableColumn<>("Подкатегория");
        subcatCol.setCellValueFactory(new PropertyValueFactory<>("subcategory"));
        TableColumn<IncomeBreakdown, BigDecimal> incomeCol = new TableColumn<>("Доход");
        incomeCol.setCellValueFactory(new PropertyValueFactory<>("income"));
        setNumericCellFactory(incomeCol);
        TableColumn<IncomeBreakdown, BigDecimal> percentCol = new TableColumn<>("Процент");
        percentCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        setNumericCellFactory(percentCol);

        tableView.getColumns().addAll(catCol, subcatCol, incomeCol, percentCol);
        openWindow(createTableWithExportVBox(tableView, "Разбивка доходов по категориям"), "Разбивка доходов по категориям", 800, 600);
    }

    // Отчеты с графиками
    @FXML
    private void showMonthlyIncomeTrendWithChart() {
        try {
            ObservableList<MonthlyIncomeTrend> data = ReportService.getMonthlyIncomeTrend();

            TableView<MonthlyIncomeTrend> tableView = new TableView<>();
            TableColumn<MonthlyIncomeTrend, String> monthCol = new TableColumn<>("Месяц");
            monthCol.setCellValueFactory(new PropertyValueFactory<>("yearMonth"));
            TableColumn<MonthlyIncomeTrend, BigDecimal> baseCol = new TableColumn<>("Доход от номеров");
            baseCol.setCellValueFactory(new PropertyValueFactory<>("baseRoomIncome"));
            setNumericCellFactory(baseCol);
            TableColumn<MonthlyIncomeTrend, BigDecimal> totalCol = new TableColumn<>("Общий доход");
            totalCol.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
            setNumericCellFactory(totalCol);
            TableColumn<MonthlyIncomeTrend, BigDecimal> growthCol = new TableColumn<>("Рост");
            growthCol.setCellValueFactory(new PropertyValueFactory<>("incomeGrowth"));
            setNumericCellFactory(growthCol);
            tableView.getColumns().addAll(monthCol, baseCol, totalCol, growthCol);
            tableView.setItems(data);

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

            VBox tableVBox = createTableWithExportVBox(tableView, "Тренды доходов по месяцам");
            SplitPane splitPane = new SplitPane(tableVBox, lineChart);
            splitPane.setDividerPositions(0.5);
            openWindow(splitPane, "Тренды доходов по месяцам", 1000, 600);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить данные для графика: " + e.getMessage());
        }
    }

    @FXML
    private void showHotelKPIDashboard() {
        try {
            ObservableList<HotelKPI> kpis = ReportService.getHotelKPI();
            GridPane kpiGrid = new GridPane();
            kpiGrid.setHgap(20);
            kpiGrid.setVgap(15);
            kpiGrid.setPadding(new javafx.geometry.Insets(20));
            kpiGrid.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1;");

            int row = 0, col = 0;
            for (HotelKPI kpi : kpis) {
                kpiGrid.add(createKPIBox(kpi), col++, row);
                if (col > 1) {
                    col = 0;
                    row++;
                }
            }

            VBox chartsBox = new VBox(20, createRevenueDistributionChart(), createKPIChart(kpis));
            TabPane tabPane = new TabPane(new Tab("Ключевые показатели", kpiGrid), new Tab("Визуализация", chartsBox));
            openWindow(tabPane, "KPI Отелей - Панель управления", 800, 600);
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
        DecimalFormat df = new DecimalFormat("#,##0.000");
        df.setRoundingMode(RoundingMode.HALF_UP);
        Label valueLabel = new Label(df.format(kpi.getMetricValue()) + " " + kpi.getMetricUnit());
        valueLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        box.getChildren().addAll(nameLabel, valueLabel);
        return box;
    }

    private PieChart createRevenueDistributionChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Распределение доходов по категориям");
        try {
            ReportService.getIncomeBreakdownByCategory().stream()
                    .filter(item -> "Всего".equals(item.getSubcategory()))
                    .forEach(item -> pieChart.getData().add(new PieChart.Data(
                            item.getCategoryType(), item.getIncome().doubleValue())));
        } catch (Exception e) {
            pieChart.getData().addAll(new PieChart.Data("Номера", 65), new PieChart.Data("Удобства", 20), new PieChart.Data("Услуги", 15));
        }
        return pieChart;
    }

    private BarChart<String, Number> createKPIChart(ObservableList<HotelKPI> kpis) {
        BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChart.setTitle("Сравнение ключевых показателей");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Значения показателей");
        kpis.stream()
            .filter(kpi -> !"%".equals(kpi.getMetricUnit()))
            .forEach(kpi -> series.getData().add(new XYChart.Data<>(kpi.getMetricName(), kpi.getMetricValue())));
        barChart.getData().add(series);
        return barChart;
    }

    // Аналитические отчеты
    @FXML
    private void showCustomerSegmentation() {
        ObservableList<CustomerSegmentation> data = ReportService.getCustomerSegmentation();
        TableView<CustomerSegmentation> tableView = new TableView<>(data);

        TableColumn<CustomerSegmentation, String> segmentCol = new TableColumn<>("Сегмент клиента");
        segmentCol.setCellValueFactory(new PropertyValueFactory<>("customerSegment"));
        TableColumn<CustomerSegmentation, Long> countCol = new TableColumn<>("Количество клиентов");
        countCol.setCellValueFactory(new PropertyValueFactory<>("customerCount"));
        TableColumn<CustomerSegmentation, BigDecimal> avgBaseCol = new TableColumn<>("Средние траты на номера");
        avgBaseCol.setCellValueFactory(new PropertyValueFactory<>("avgBaseSpent"));
        setNumericCellFactory(avgBaseCol);
        TableColumn<CustomerSegmentation, BigDecimal> avgConvCol = new TableColumn<>("Средние траты на удобства");
        avgConvCol.setCellValueFactory(new PropertyValueFactory<>("avgConveniencesSpent"));
        setNumericCellFactory(avgConvCol);
        TableColumn<CustomerSegmentation, BigDecimal> avgServCol = new TableColumn<>("Средние траты на услуги");
        avgServCol.setCellValueFactory(new PropertyValueFactory<>("avgServicesSpent"));
        setNumericCellFactory(avgServCol);
        TableColumn<CustomerSegmentation, BigDecimal> avgTotalCol = new TableColumn<>("Средние общие траты");
        avgTotalCol.setCellValueFactory(new PropertyValueFactory<>("avgTotalSpent"));
        setNumericCellFactory(avgTotalCol);

        tableView.getColumns().addAll(segmentCol, countCol, avgBaseCol, avgConvCol, avgServCol, avgTotalCol);
        openWindow(createTableWithExportVBox(tableView, "Сегментация клиентов"), "Сегментация клиентов", 1100, 600);
    }

    @FXML
    private void showHighIncomeRooms() {
        TextInputDialog dialog = new TextInputDialog("10000");
        dialog.setTitle("Высокодоходные номера");
        dialog.setHeaderText("Введите минимальный порог дохода");
        dialog.setContentText("Порог дохода:");
        dialog.showAndWait().ifPresent(thresholdStr -> {
            try {
                BigDecimal threshold = new BigDecimal(thresholdStr);
                ObservableList<HighIncomeRoom> data = ReportService.getHighIncomeRooms(threshold);
                TableView<HighIncomeRoom> tableView = new TableView<>(data);

                TableColumn<HighIncomeRoom, Integer> roomCol = new TableColumn<>("Номер");
                roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
                TableColumn<HighIncomeRoom, String> typeCol = new TableColumn<>("Тип");
                typeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
                TableColumn<HighIncomeRoom, BigDecimal> totalCol = new TableColumn<>("Общий доход");
                totalCol.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
                setNumericCellFactory(totalCol);
                TableColumn<HighIncomeRoom, Double> occupancyCol = new TableColumn<>("Загрузка (%)");
                occupancyCol.setCellValueFactory(new PropertyValueFactory<>("occupancyRate"));
                setNumericCellFactory(occupancyCol);
                tableView.getColumns().addAll(roomCol, typeCol, totalCol, occupancyCol);

                BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
                chart.setTitle("Доходы по номерам");
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Общий доход");
                data.forEach(room -> series.getData().add(new XYChart.Data<>("Комн. " + room.getRoomNumber(), room.getTotalIncome())));
                chart.getData().add(series);

                String title = "Высокодоходные номера (порог: " + threshold + ")";
                VBox tableVBox = createTableWithExportVBox(tableView, title);
                SplitPane splitPane = new SplitPane(tableVBox, chart);
                splitPane.setDividerPositions(0.5);
                openWindow(splitPane, title, 900, 600);
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Введите корректное числовое значение для порога дохода");
            }
        });
    }

    @FXML
    private void showRoomEfficiency() {
        Dialog<RoomEfficiencyParams> dialog = createRoomEfficiencyDialog();
        dialog.showAndWait().ifPresent(params -> {
            ObservableList<RoomEfficiency> data = ReportService.getRoomEfficiencyAnalysis(
                    new BigDecimal(params.minOccupancy), params.minIncome, params.startDate, params.endDate);
            TableView<RoomEfficiency> tableView = new TableView<>(data);

            TableColumn<RoomEfficiency, Integer> roomCol = new TableColumn<>("Номер");
            roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
            TableColumn<RoomEfficiency, String> typeCol = new TableColumn<>("Тип");
            typeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
            TableColumn<RoomEfficiency, BigDecimal> totalCol = new TableColumn<>("Общий доход");
            totalCol.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
            setNumericCellFactory(totalCol);
            TableColumn<RoomEfficiency, Double> occupancyCol = new TableColumn<>("Загрузка (%)");
            occupancyCol.setCellValueFactory(new PropertyValueFactory<>("occupancyRate"));
            setNumericCellFactory(occupancyCol);
            TableColumn<RoomEfficiency, String> ratingCol = new TableColumn<>("Эффективность");
            ratingCol.setCellValueFactory(new PropertyValueFactory<>("efficiencyRating"));

            tableView.getColumns().addAll(roomCol, typeCol, totalCol, occupancyCol, ratingCol);
            openWindow(createTableWithExportVBox(tableView, "Анализ эффективности номеров"), "Анализ эффективности номеров", 800, 600);
        });
    }

    private Dialog<RoomEfficiencyParams> createRoomEfficiencyDialog() {
        Dialog<RoomEfficiencyParams> dialog = new Dialog<>();
        dialog.setTitle("Анализ эффективности номеров");
        dialog.setHeaderText("Введите параметры анализа");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        TextField minOccupancyField = new TextField("50");
        TextField minIncomeField = new TextField("5000");
        DatePicker startDatePicker = new DatePicker(java.time.LocalDate.now().withDayOfMonth(1));
        DatePicker endDatePicker = new DatePicker(java.time.LocalDate.now());

        grid.add(new Label("Минимальная загрузка (%):"), 0, 0);
        grid.add(minOccupancyField, 1, 0);
        grid.add(new Label("Минимальный доход:"), 0, 1);
        grid.add(minIncomeField, 1, 1);
        grid.add(new Label("Начальная дата:"), 0, 2);
        grid.add(startDatePicker, 1, 2);
        grid.add(new Label("Конечная дата:"), 0, 3);
        grid.add(endDatePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        ButtonType analyzeButton = new ButtonType("Анализировать", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(analyzeButton, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == analyzeButton) {
                try {
                    return new RoomEfficiencyParams(
                            Double.parseDouble(minOccupancyField.getText()),
                            new BigDecimal(minIncomeField.getText()),
                            startDatePicker.getValue(),
                            endDatePicker.getValue()
                    );
                } catch (NumberFormatException e) { return null; }
            }
            return null;
        });
        return dialog;
    }

    private static class RoomEfficiencyParams {
        final double minOccupancy;
        final BigDecimal minIncome;
        final java.time.LocalDate startDate;
        final java.time.LocalDate endDate;

        RoomEfficiencyParams(double minOccupancy, BigDecimal minIncome, java.time.LocalDate startDate, java.time.LocalDate endDate) {
            this.minOccupancy = minOccupancy;
            this.minIncome = minIncome;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    private void openWindow(Region content, String title, double width, double height) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(content, width, height));
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void showIncomeByCity() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Доходы по городам");
        dialog.setHeaderText("Введите шаблон названия города");
        dialog.setContentText("Город:");
        dialog.showAndWait().ifPresent(cityPattern -> {
            ObservableList<IncomeByCity> data = ReportService.getIncomeByCity(cityPattern);
            TableView<IncomeByCity> tableView = new TableView<>(data);

            TableColumn<IncomeByCity, String> cityCol = new TableColumn<>("Город");
            cityCol.setCellValueFactory(new PropertyValueFactory<>("cityName"));
            TableColumn<IncomeByCity, BigDecimal> totalCol = new TableColumn<>("Общий доход");
            totalCol.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
            setNumericCellFactory(totalCol);
            TableColumn<IncomeByCity, BigDecimal> baseCol = new TableColumn<>("Доход от номеров");
            baseCol.setCellValueFactory(new PropertyValueFactory<>("baseRoomIncome"));
            setNumericCellFactory(baseCol);
            TableColumn<IncomeByCity, BigDecimal> convCol = new TableColumn<>("Доход от удобств");
            convCol.setCellValueFactory(new PropertyValueFactory<>("conveniencesIncome"));
            setNumericCellFactory(convCol);
            TableColumn<IncomeByCity, BigDecimal> servCol = new TableColumn<>("Доход от услуг");
            servCol.setCellValueFactory(new PropertyValueFactory<>("servicesIncome"));
            setNumericCellFactory(servCol);
            TableColumn<IncomeByCity, BigDecimal> avgStayCol = new TableColumn<>("Средняя продолжительность");
            avgStayCol.setCellValueFactory(new PropertyValueFactory<>("averageStayDuration"));
            setNumericCellFactory(avgStayCol);

            tableView.getColumns().addAll(cityCol, totalCol, baseCol, convCol, servCol, avgStayCol);
            String title = "Доходы по городам: " + cityPattern;
            openWindow(createTableWithExportVBox(tableView, title), title, 1000, 600);
        });
    }

    @FXML
    private void showPremiumServices() {
        Dialog<ServiceParams> dialog = createServiceParamsDialog();
        dialog.showAndWait().ifPresent(params -> {
            ObservableList<PremiumService> data = ReportService.getPremiumServices(params.minUsage, params.minIncome);
            TableView<PremiumService> tableView = new TableView<>(data);

            TableColumn<PremiumService, String> serviceCol = new TableColumn<>("Услуга");
            serviceCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
            TableColumn<PremiumService, Long> usageCol = new TableColumn<>("Количество использований");
            usageCol.setCellValueFactory(new PropertyValueFactory<>("usageCount"));
            TableColumn<PremiumService, BigDecimal> totalCol = new TableColumn<>("Общий доход");
            totalCol.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
            setNumericCellFactory(totalCol);
            TableColumn<PremiumService, BigDecimal> avgCol = new TableColumn<>("Средний доход за использование");
            avgCol.setCellValueFactory(new PropertyValueFactory<>("avgIncomePerUse"));
            setNumericCellFactory(avgCol);

            tableView.getColumns().addAll(serviceCol, usageCol, totalCol, avgCol);
            openWindow(createTableWithExportVBox(tableView, "Премиальные услуги"), "Премиальные услуги", 800, 600);
        });
    }

    private Dialog<ServiceParams> createServiceParamsDialog() {
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
        dialog.setResultConverter(btn -> {
            if (btn == analyzeButton) {
                try {
                    return new ServiceParams(Integer.parseInt(minUsageField.getText()), new BigDecimal(minIncomeField.getText()));
                } catch (NumberFormatException e) { return null; }
            }
            return null;
        });
        return dialog;
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
        ObservableList<IncomeSource> data = ReportService.getCombinedIncomeSources();
        TableView<IncomeSource> tableView = new TableView<>(data);

        TableColumn<IncomeSource, String> sourceCol = new TableColumn<>("Источник дохода");
        sourceCol.setCellValueFactory(new PropertyValueFactory<>("incomeSource"));
        TableColumn<IncomeSource, String> typeCol = new TableColumn<>("Тип источника");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("sourceType"));
        TableColumn<IncomeSource, BigDecimal> amountCol = new TableColumn<>("Сумма");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        setNumericCellFactory(amountCol);

        tableView.getColumns().addAll(sourceCol, typeCol, amountCol);
        openWindow(createTableWithExportVBox(tableView, "Все источники дохода"), "Все источники дохода", 800, 600);
    }
}