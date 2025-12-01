// app/subd/models/ReportModels.java
package app.subd.models;

import java.math.BigDecimal;

public class ReportModels {

    // Модель для детализированного отчета о доходах (запрос 11)
    public static class DetailedIncomeReport {
        private String hotelAddress;
        private Integer roomNumber;
        private BigDecimal baseRoomIncome;
        private BigDecimal conveniencesIncome;
        private BigDecimal servicesIncome;
        private BigDecimal totalIncome;

        public DetailedIncomeReport() {}

        public DetailedIncomeReport(String hotelAddress, Integer roomNumber,
                                    BigDecimal baseRoomIncome, BigDecimal conveniencesIncome,
                                    BigDecimal servicesIncome, BigDecimal totalIncome) {
            this.hotelAddress = hotelAddress;
            this.roomNumber = roomNumber;
            this.baseRoomIncome = baseRoomIncome;
            this.conveniencesIncome = conveniencesIncome;
            this.servicesIncome = servicesIncome;
            this.totalIncome = totalIncome;
        }

        // геттеры и сеттеры
        public String getHotelAddress() { return hotelAddress; }
        public void setHotelAddress(String hotelAddress) { this.hotelAddress = hotelAddress; }

        public Integer getRoomNumber() { return roomNumber; }
        public void setRoomNumber(Integer roomNumber) { this.roomNumber = roomNumber; }

        public BigDecimal getBaseRoomIncome() { return baseRoomIncome; }
        public void setBaseRoomIncome(BigDecimal baseRoomIncome) { this.baseRoomIncome = baseRoomIncome; }

        public BigDecimal getConveniencesIncome() { return conveniencesIncome; }
        public void setConveniencesIncome(BigDecimal conveniencesIncome) { this.conveniencesIncome = conveniencesIncome; }

        public BigDecimal getServicesIncome() { return servicesIncome; }
        public void setServicesIncome(BigDecimal servicesIncome) { this.servicesIncome = servicesIncome; }

        public BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    }

    // Модель для сводки по доходам отелей (запрос 12)
    public static class HotelIncomeSummary {
        private String hotelAddress;
        private BigDecimal baseRoomIncome;
        private BigDecimal conveniencesIncome;
        private BigDecimal servicesIncome;
        private BigDecimal totalIncome;
        private Long totalBookings;

        public HotelIncomeSummary() {}

        public HotelIncomeSummary(String hotelAddress, BigDecimal baseRoomIncome,
                                  BigDecimal conveniencesIncome, BigDecimal servicesIncome,
                                  BigDecimal totalIncome, Long totalBookings) {
            this.hotelAddress = hotelAddress;
            this.baseRoomIncome = baseRoomIncome;
            this.conveniencesIncome = conveniencesIncome;
            this.servicesIncome = servicesIncome;
            this.totalIncome = totalIncome;
            this.totalBookings = totalBookings;
        }

        // геттеры и сеттеры
        public String getHotelAddress() { return hotelAddress; }
        public void setHotelAddress(String hotelAddress) { this.hotelAddress = hotelAddress; }

        public BigDecimal getBaseRoomIncome() { return baseRoomIncome; }
        public void setBaseRoomIncome(BigDecimal baseRoomIncome) { this.baseRoomIncome = baseRoomIncome; }

        public BigDecimal getConveniencesIncome() { return conveniencesIncome; }
        public void setConveniencesIncome(BigDecimal conveniencesIncome) { this.conveniencesIncome = conveniencesIncome; }

        public BigDecimal getServicesIncome() { return servicesIncome; }
        public void setServicesIncome(BigDecimal servicesIncome) { this.servicesIncome = servicesIncome; }

        public BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

        public Long getTotalBookings() { return totalBookings; }
        public void setTotalBookings(Long totalBookings) { this.totalBookings = totalBookings; }
    }

    // Модель для разбивки доходов по категориям (запрос 13)
    public static class IncomeBreakdown {
        private String categoryType;
        private String subcategory;
        private BigDecimal income;
        private BigDecimal percentage;

        public IncomeBreakdown() {}

        public IncomeBreakdown(String categoryType, String subcategory,
                               BigDecimal income, BigDecimal percentage) {
            this.categoryType = categoryType;
            this.subcategory = subcategory;
            this.income = income;
            this.percentage = percentage;
        }

        // геттеры и сеттеры
        public String getCategoryType() { return categoryType; }
        public void setCategoryType(String categoryType) { this.categoryType = categoryType; }

        public String getSubcategory() { return subcategory; }
        public void setSubcategory(String subcategory) { this.subcategory = subcategory; }

        public BigDecimal getIncome() { return income; }
        public void setIncome(BigDecimal income) { this.income = income; }

        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    }

    // Модель для доходов по городам (запрос 14)
    public static class IncomeByCity {
        private String cityName;
        private BigDecimal baseRoomIncome;
        private BigDecimal conveniencesIncome;
        private BigDecimal servicesIncome;
        private BigDecimal totalIncome;
        private Double averageStayDuration;

        public IncomeByCity() {}

        public IncomeByCity(String cityName, BigDecimal baseRoomIncome,
                            BigDecimal conveniencesIncome, BigDecimal servicesIncome,
                            BigDecimal totalIncome, Double averageStayDuration) {
            this.cityName = cityName;
            this.baseRoomIncome = baseRoomIncome;
            this.conveniencesIncome = conveniencesIncome;
            this.servicesIncome = servicesIncome;
            this.totalIncome = totalIncome;
            this.averageStayDuration = averageStayDuration;
        }

        // геттеры и сеттеры
        public String getCityName() { return cityName; }
        public void setCityName(String cityName) { this.cityName = cityName; }

        public BigDecimal getBaseRoomIncome() { return baseRoomIncome; }
        public void setBaseRoomIncome(BigDecimal baseRoomIncome) { this.baseRoomIncome = baseRoomIncome; }

        public BigDecimal getConveniencesIncome() { return conveniencesIncome; }
        public void setConveniencesIncome(BigDecimal conveniencesIncome) { this.conveniencesIncome = conveniencesIncome; }

        public BigDecimal getServicesIncome() { return servicesIncome; }
        public void setServicesIncome(BigDecimal servicesIncome) { this.servicesIncome = servicesIncome; }

        public BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

        public Double getAverageStayDuration() { return averageStayDuration; }
        public void setAverageStayDuration(Double averageStayDuration) { this.averageStayDuration = averageStayDuration; }
    }

    // Модель для высокодоходных номеров (запрос 15)
    public static class HighIncomeRoom {
        private Integer roomNumber;
        private String roomType;
        private BigDecimal baseIncome;
        private BigDecimal conveniencesIncome;
        private BigDecimal servicesIncome;
        private BigDecimal totalIncome;
        private Double occupancyRate;

        public HighIncomeRoom() {}

        public HighIncomeRoom(Integer roomNumber, String roomType,
                              BigDecimal baseIncome, BigDecimal conveniencesIncome,
                              BigDecimal servicesIncome, BigDecimal totalIncome,
                              Double occupancyRate) {
            this.roomNumber = roomNumber;
            this.roomType = roomType;
            this.baseIncome = baseIncome;
            this.conveniencesIncome = conveniencesIncome;
            this.servicesIncome = servicesIncome;
            this.totalIncome = totalIncome;
            this.occupancyRate = occupancyRate;
        }

        // геттеры и сеттеры
        public Integer getRoomNumber() { return roomNumber; }
        public void setRoomNumber(Integer roomNumber) { this.roomNumber = roomNumber; }

        public String getRoomType() { return roomType; }
        public void setRoomType(String roomType) { this.roomType = roomType; }

        public BigDecimal getBaseIncome() { return baseIncome; }
        public void setBaseIncome(BigDecimal baseIncome) { this.baseIncome = baseIncome; }

        public BigDecimal getConveniencesIncome() { return conveniencesIncome; }
        public void setConveniencesIncome(BigDecimal conveniencesIncome) { this.conveniencesIncome = conveniencesIncome; }

        public BigDecimal getServicesIncome() { return servicesIncome; }
        public void setServicesIncome(BigDecimal servicesIncome) { this.servicesIncome = servicesIncome; }

        public BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

        public Double getOccupancyRate() { return occupancyRate; }
        public void setOccupancyRate(Double occupancyRate) { this.occupancyRate = occupancyRate; }
    }

    // Модель для премиальных услуг (запрос 16)
    public static class PremiumService {
        private String serviceName;
        private Long usageCount;
        private BigDecimal totalIncome;
        private BigDecimal avgIncomePerUse;

        public PremiumService() {}

        public PremiumService(String serviceName, Long usageCount,
                              BigDecimal totalIncome, BigDecimal avgIncomePerUse) {
            this.serviceName = serviceName;
            this.usageCount = usageCount;
            this.totalIncome = totalIncome;
            this.avgIncomePerUse = avgIncomePerUse;
        }

        // геттеры и сеттеры
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }

        public Long getUsageCount() { return usageCount; }
        public void setUsageCount(Long usageCount) { this.usageCount = usageCount; }

        public BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

        public BigDecimal getAvgIncomePerUse() { return avgIncomePerUse; }
        public void setAvgIncomePerUse(BigDecimal avgIncomePerUse) { this.avgIncomePerUse = avgIncomePerUse; }
    }

    // Модель для эффективности номеров (запрос 17)
    public static class RoomEfficiency {
        private Integer roomNumber;
        private String roomType;
        private BigDecimal baseIncome;
        private BigDecimal conveniencesIncome;
        private BigDecimal servicesIncome;
        private BigDecimal totalIncome;
        private Double occupancyRate;
        private String efficiencyRating;

        public RoomEfficiency() {}

        public RoomEfficiency(Integer roomNumber, String roomType,
                              BigDecimal baseIncome, BigDecimal conveniencesIncome,
                              BigDecimal servicesIncome, BigDecimal totalIncome,
                              Double occupancyRate, String efficiencyRating) {
            this.roomNumber = roomNumber;
            this.roomType = roomType;
            this.baseIncome = baseIncome;
            this.conveniencesIncome = conveniencesIncome;
            this.servicesIncome = servicesIncome;
            this.totalIncome = totalIncome;
            this.occupancyRate = occupancyRate;
            this.efficiencyRating = efficiencyRating;
        }

        // геттеры и сеттеры
        public Integer getRoomNumber() { return roomNumber; }
        public void setRoomNumber(Integer roomNumber) { this.roomNumber = roomNumber; }

        public String getRoomType() { return roomType; }
        public void setRoomType(String roomType) { this.roomType = roomType; }

        public BigDecimal getBaseIncome() { return baseIncome; }
        public void setBaseIncome(BigDecimal baseIncome) { this.baseIncome = baseIncome; }

        public BigDecimal getConveniencesIncome() { return conveniencesIncome; }
        public void setConveniencesIncome(BigDecimal conveniencesIncome) { this.conveniencesIncome = conveniencesIncome; }

        public BigDecimal getServicesIncome() { return servicesIncome; }
        public void setServicesIncome(BigDecimal servicesIncome) { this.servicesIncome = servicesIncome; }

        public BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

        public Double getOccupancyRate() { return occupancyRate; }
        public void setOccupancyRate(Double occupancyRate) { this.occupancyRate = occupancyRate; }

        public String getEfficiencyRating() { return efficiencyRating; }
        public void setEfficiencyRating(String efficiencyRating) { this.efficiencyRating = efficiencyRating; }
    }

    // Модель для трендов доходов по месяцам (запрос 18)
    public static class MonthlyIncomeTrend {
        private String yearMonth;
        private BigDecimal baseRoomIncome;
        private BigDecimal conveniencesIncome;
        private BigDecimal servicesIncome;
        private BigDecimal totalIncome;
        private BigDecimal incomeGrowth;

        public MonthlyIncomeTrend() {}

        public MonthlyIncomeTrend(String yearMonth, BigDecimal baseRoomIncome,
                                  BigDecimal conveniencesIncome, BigDecimal servicesIncome,
                                  BigDecimal totalIncome, BigDecimal incomeGrowth) {
            this.yearMonth = yearMonth;
            this.baseRoomIncome = baseRoomIncome;
            this.conveniencesIncome = conveniencesIncome;
            this.servicesIncome = servicesIncome;
            this.totalIncome = totalIncome;
            this.incomeGrowth = incomeGrowth;
        }

        // геттеры и сеттеры
        public String getYearMonth() { return yearMonth; }
        public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

        public BigDecimal getBaseRoomIncome() { return baseRoomIncome; }
        public void setBaseRoomIncome(BigDecimal baseRoomIncome) { this.baseRoomIncome = baseRoomIncome; }

        public BigDecimal getConveniencesIncome() { return conveniencesIncome; }
        public void setConveniencesIncome(BigDecimal conveniencesIncome) { this.conveniencesIncome = conveniencesIncome; }

        public BigDecimal getServicesIncome() { return servicesIncome; }
        public void setServicesIncome(BigDecimal servicesIncome) { this.servicesIncome = servicesIncome; }

        public BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

        public BigDecimal getIncomeGrowth() { return incomeGrowth; }
        public void setIncomeGrowth(BigDecimal incomeGrowth) { this.incomeGrowth = incomeGrowth; }
    }

    // Модель для источников доходов (запрос 19)
    public static class IncomeSource {
        private String incomeSource;
        private String sourceType;
        private BigDecimal amount;

        public IncomeSource() {}

        public IncomeSource(String incomeSource, String sourceType, BigDecimal amount) {
            this.incomeSource = incomeSource;
            this.sourceType = sourceType;
            this.amount = amount;
        }

        // геттеры и сеттеры
        public String getIncomeSource() { return incomeSource; }
        public void setIncomeSource(String incomeSource) { this.incomeSource = incomeSource; }

        public String getSourceType() { return sourceType; }
        public void setSourceType(String sourceType) { this.sourceType = sourceType; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    // Модель для KPI отелей (запрос 20)
    public static class HotelKPI {
        private String metricName;
        private BigDecimal metricValue;
        private String metricUnit;

        public HotelKPI() {}

        public HotelKPI(String metricName, BigDecimal metricValue, String metricUnit) {
            this.metricName = metricName;
            this.metricValue = metricValue;
            this.metricUnit = metricUnit;
        }

        // геттеры и сеттеры
        public String getMetricName() { return metricName; }
        public void setMetricName(String metricName) { this.metricName = metricName; }

        public BigDecimal getMetricValue() { return metricValue; }
        public void setMetricValue(BigDecimal metricValue) { this.metricValue = metricValue; }

        public String getMetricUnit() { return metricUnit; }
        public void setMetricUnit(String metricUnit) { this.metricUnit = metricUnit; }
    }

    // Модель для сегментации клиентов (запрос 22)
    public static class CustomerSegmentation {
        private String customerSegment;
        private Long customerCount;
        private BigDecimal avgBaseSpent;
        private BigDecimal avgConveniencesSpent;
        private BigDecimal avgServicesSpent;
        private BigDecimal avgTotalSpent;

        public CustomerSegmentation() {}

        public CustomerSegmentation(String customerSegment, Long customerCount,
                                    BigDecimal avgBaseSpent, BigDecimal avgConveniencesSpent,
                                    BigDecimal avgServicesSpent, BigDecimal avgTotalSpent) {
            this.customerSegment = customerSegment;
            this.customerCount = customerCount;
            this.avgBaseSpent = avgBaseSpent;
            this.avgConveniencesSpent = avgConveniencesSpent;
            this.avgServicesSpent = avgServicesSpent;
            this.avgTotalSpent = avgTotalSpent;
        }

        // геттеры и сеттеры
        public String getCustomerSegment() { return customerSegment; }
        public void setCustomerSegment(String customerSegment) { this.customerSegment = customerSegment; }

        public Long getCustomerCount() { return customerCount; }
        public void setCustomerCount(Long customerCount) { this.customerCount = customerCount; }

        public BigDecimal getAvgBaseSpent() { return avgBaseSpent; }
        public void setAvgBaseSpent(BigDecimal avgBaseSpent) { this.avgBaseSpent = avgBaseSpent; }

        public BigDecimal getAvgConveniencesSpent() { return avgConveniencesSpent; }
        public void setAvgConveniencesSpent(BigDecimal avgConveniencesSpent) { this.avgConveniencesSpent = avgConveniencesSpent; }

        public BigDecimal getAvgServicesSpent() { return avgServicesSpent; }
        public void setAvgServicesSpent(BigDecimal avgServicesSpent) { this.avgServicesSpent = avgServicesSpent; }

        public BigDecimal getAvgTotalSpent() { return avgTotalSpent; }
        public void setAvgTotalSpent(BigDecimal avgTotalSpent) { this.avgTotalSpent = avgTotalSpent; }
    }
}