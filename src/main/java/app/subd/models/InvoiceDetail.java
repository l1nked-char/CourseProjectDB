package app.subd.models;

import java.math.BigDecimal;

public class InvoiceDetail {
    private final String description;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalPrice;

    public InvoiceDetail(String description, int quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters
    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
