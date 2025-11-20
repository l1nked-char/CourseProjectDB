package app.subd.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Invoice {
    private String invoiceNumber;
    private String bookingNumber;
    private BigDecimal totalAmount;
    private LocalDate issueDate;
    private boolean isPaid;

    public Invoice() {
        this.invoiceNumber = "";
        this.bookingNumber = "";
        this.totalAmount = BigDecimal.ZERO;
        this.issueDate = LocalDate.now();
        this.isPaid = false;
    }

    public Invoice(String invoiceNumber, String bookingNumber, BigDecimal totalAmount, LocalDate issueDate, boolean isPaid) {
        this.invoiceNumber = invoiceNumber;
        this.bookingNumber = bookingNumber;
        this.totalAmount = totalAmount;
        this.issueDate = issueDate;
        this.isPaid = isPaid;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getStatus() {
        return isPaid ? "Оплачен" : "Не оплачен";
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}