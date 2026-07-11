package service;

import model.OrderItem;
import repository.SalesRepository;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SalesService {
    private SalesRepository salesRepository = new SalesRepository();

    public void completeSale(String cashier, double total, List<OrderItem> items) throws Exception {
        if (items.isEmpty()) throw new Exception("السلة فارغة!");
        salesRepository.saveSale(cashier, total, items);
    }

    public ResultSet getOrdersForChef() {
        try {
            return salesRepository.getPendingSales();
        } catch (Exception e) {
            return null;
        }
    }

    public void markOrderAsReady(int id) throws Exception {
        salesRepository.updateSaleStatus(id, "Ready");
    }

    public ArrayList<Object[]> getReadyOrders() {
        try {
            return salesRepository.getReadyOrdersList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void finishDelivery(int id) throws Exception {
        salesRepository.updateSaleStatus(id, "Completed");
    }
    
    public ResultSet getItemsBySaleId(int id) {
        try {
            return salesRepository.getSaleItems(id);
        } catch (Exception e) {
            return null;
        }
    }
}
