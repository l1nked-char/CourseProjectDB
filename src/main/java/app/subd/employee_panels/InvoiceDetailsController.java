package app.subd.employee_panels;

import app.subd.Database_functions;
import app.subd.components.Session;
import app.subd.models.Invoice;
import app.subd.models.InvoiceDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;

public class InvoiceDetailsController {

    @FXML private Label invoiceNumberLabel;
    @FXML private Label bookingNumberLabel;
    @FXML private Label totalAmountLabel;
    @FXML private TableView<InvoiceDetail> detailsTable;
    @FXML private TableColumn<InvoiceDetail, String> descriptionColumn;
    @FXML private TableColumn<InvoiceDetail, Integer> quantityColumn;
    @FXML private TableColumn<InvoiceDetail, BigDecimal> unitPriceColumn;
    @FXML private TableColumn<InvoiceDetail, BigDecimal> totalPriceColumn;

    private final ObservableList<InvoiceDetail> detailsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        detailsTable.setItems(detailsList);
    }

    public void loadInvoiceData(Invoice invoice) {
        if (invoice == null) {
            return;
        }

        invoiceNumberLabel.setText("Счет № " + invoice.getInvoiceNumber());
        bookingNumberLabel.setText("Бронирование № " + invoice.getBookingNumber());
        totalAmountLabel.setText("Итого: " + invoice.getTotalAmount() + " руб.");

        try {
            Connection connection = Session.getConnection();
            ResultSet rs = Database_functions.callFunction(connection, "get_invoice_details", invoice.getBookingNumber());
            detailsList.clear();

            while (rs.next()) {
                detailsList.add(new InvoiceDetail(
                        rs.getString("item_description"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("unit_price"),
                        rs.getBigDecimal("total_price")
                ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки деталей счета: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
