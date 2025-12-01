package app.subd.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;

public enum BookingStatus {
    NOT_SETTLED ("не заселились"),
    BOOKED ("забронирован"),
    REMOVED_FROM_BOOKING ("снят с бронирования"),
    MOVE_OUT_EARLIER ("выселились досрочно"),
    OCCUPIED ("занят");

    private final String description;

    BookingStatus(String s) {
        this.description = s;
    }

    public String getDescription() {
        return description;
    }

    public static BookingStatus getBookingStatus(String status) {
        for (BookingStatus bookingStatus : BookingStatus.values()) {
            if (bookingStatus.getDescription().equals(status)) {
                return bookingStatus;
            }
        }
        return null;
    }

    public static ObservableList<Object> getBookingStatusValues() {
        final ObservableList<Object> bookingStatusValues = FXCollections.observableArrayList();
        bookingStatusValues.addAll(Arrays.asList(BookingStatus.values()));
        return bookingStatusValues;
    }

    @Override
    public String toString() {
        return description;
    }
}