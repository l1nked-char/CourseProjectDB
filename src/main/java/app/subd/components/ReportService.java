// app/subd/service/ReportService.java
package app.subd.components;

import app.subd.Database_functions;
import app.subd.models.ReportModels.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;

public class ReportService {

    public static ObservableList<DetailedIncomeReport> getDetailedIncomeReport() {
        ObservableList<DetailedIncomeReport> reports = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_detailed_income_report");

            while (rs.next()) {
                DetailedIncomeReport report = new DetailedIncomeReport(
                        rs.getString("hotel_address"),
                        rs.getInt("room_number"),
                        rs.getBigDecimal("base_room_income"),
                        rs.getBigDecimal("conveniences_income"),
                        rs.getBigDecimal("services_income"),
                        rs.getBigDecimal("total_income")
                );
                reports.add(report);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reports;
    }

    public static ObservableList<HotelIncomeSummary> getHotelIncomeSummary() {
        ObservableList<HotelIncomeSummary> summaries = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_hotel_income_summary");

            while (rs.next()) {
                HotelIncomeSummary summary = new HotelIncomeSummary(
                        rs.getString("hotel_address"),
                        rs.getBigDecimal("base_room_income"),
                        rs.getBigDecimal("conveniences_income"),
                        rs.getBigDecimal("services_income"),
                        rs.getBigDecimal("total_income"),
                        rs.getLong("total_bookings")
                );
                summaries.add(summary);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summaries;
    }

    public static ObservableList<IncomeBreakdown> getIncomeBreakdownByCategory() {
        ObservableList<IncomeBreakdown> breakdown = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_income_breakdown_by_category");

            while (rs.next()) {
                IncomeBreakdown item = new IncomeBreakdown(
                        rs.getString("category_type"),
                        rs.getString("subcategory"),
                        rs.getBigDecimal("income"),
                        rs.getBigDecimal("percentage")
                );
                breakdown.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return breakdown;
    }

    public static ObservableList<IncomeByCity> getIncomeByCity(String cityPattern) {
        ObservableList<IncomeByCity> incomes = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_income_by_city_pattern", cityPattern);

            while (rs.next()) {
                IncomeByCity income = new IncomeByCity(
                        rs.getString("city_name"),
                        rs.getBigDecimal("base_room_income"),
                        rs.getBigDecimal("conveniences_income"),
                        rs.getBigDecimal("services_income"),
                        rs.getBigDecimal("total_income"),
                        rs.getDouble("average_stay_duration")
                );
                incomes.add(income);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return incomes;
    }

    public static ObservableList<HighIncomeRoom> getHighIncomeRooms(BigDecimal threshold) {
        ObservableList<HighIncomeRoom> rooms = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_high_income_rooms", threshold);

            while (rs.next()) {
                HighIncomeRoom room = new HighIncomeRoom(
                        rs.getInt("room_number"),
                        rs.getString("room_type"),
                        rs.getBigDecimal("base_income"),
                        rs.getBigDecimal("conveniences_income"),
                        rs.getBigDecimal("services_income"),
                        rs.getBigDecimal("total_income"),
                        rs.getDouble("occupancy_rate")
                );
                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static ObservableList<PremiumService> getPremiumServices(Integer minUsage, BigDecimal minIncome) {
        ObservableList<PremiumService> services = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_premium_services_usage", minUsage, minIncome);

            while (rs.next()) {
                PremiumService service = new PremiumService(
                        rs.getString("service_name"),
                        rs.getLong("usage_count"),
                        rs.getBigDecimal("total_income"),
                        rs.getBigDecimal("avg_income_per_use")
                );
                services.add(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return services;
    }

    public static ObservableList<RoomEfficiency> getRoomEfficiencyAnalysis(Double minOccupancy, BigDecimal minIncome) {
        ObservableList<RoomEfficiency> efficiencies = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_room_efficiency_analysis", minOccupancy, minIncome);

            while (rs.next()) {
                RoomEfficiency efficiency = new RoomEfficiency(
                        rs.getInt("room_number"),
                        rs.getString("room_type"),
                        rs.getBigDecimal("base_income"),
                        rs.getBigDecimal("conveniences_income"),
                        rs.getBigDecimal("services_income"),
                        rs.getBigDecimal("total_income"),
                        rs.getDouble("occupancy_rate"),
                        rs.getString("efficiency_rating")
                );
                efficiencies.add(efficiency);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return efficiencies;
    }

    public static ObservableList<MonthlyIncomeTrend> getMonthlyIncomeTrend() {
        ObservableList<MonthlyIncomeTrend> trends = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_monthly_income_trend");

            while (rs.next()) {
                MonthlyIncomeTrend trend = new MonthlyIncomeTrend(
                        rs.getString("year_month"),
                        rs.getBigDecimal("base_room_income"),
                        rs.getBigDecimal("conveniences_income"),
                        rs.getBigDecimal("services_income"),
                        rs.getBigDecimal("total_income"),
                        rs.getBigDecimal("income_growth")
                );
                trends.add(trend);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trends;
    }

    public static ObservableList<IncomeSource> getCombinedIncomeSources() {
        ObservableList<IncomeSource> sources = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_combined_income_sources");

            while (rs.next()) {
                IncomeSource source = new IncomeSource(
                        rs.getString("income_source"),
                        rs.getString("source_type"),
                        rs.getBigDecimal("amount")
                );
                sources.add(source);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sources;
    }

    public static ObservableList<HotelKPI> getHotelKPI() {
        ObservableList<HotelKPI> kpis = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_hotel_kpi");

            while (rs.next()) {
                HotelKPI kpi = new HotelKPI(
                        rs.getString("metric_name"),
                        rs.getBigDecimal("metric_value"),
                        rs.getString("metric_unit")
                );
                kpis.add(kpi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kpis;
    }

    public static ObservableList<CustomerSegmentation> getCustomerSegmentation() {
        ObservableList<CustomerSegmentation> segments = FXCollections.observableArrayList();
        try {
            Connection connection = app.subd.components.Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_customer_segmentation");

            while (rs.next()) {
                CustomerSegmentation segment = new CustomerSegmentation(
                        rs.getString("customer_segment"),
                        rs.getLong("customer_count"),
                        rs.getBigDecimal("avg_base_spent"),
                        rs.getBigDecimal("avg_conveniences_spent"),
                        rs.getBigDecimal("avg_services_spent"),
                        rs.getBigDecimal("avg_total_spent")
                );
                segments.add(segment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return segments;
    }
}