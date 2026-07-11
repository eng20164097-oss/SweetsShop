package service;

import repository.*;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ReportService {
    private SalesRepository salesRepo = new SalesRepository();
    private ProductRepository productRepo = new ProductRepository();
    private UserRepository userRepo = new UserRepository();

    // ميثود تجلب كل الإحصائيات في حقيبة واحدة (Map)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("revenue", salesRepo.getTotalRevenue());
            stats.put("lowStock", productRepo.getLowStockCount());
            stats.put("staffCount", userRepo.getStaffCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    public ResultSet getSalesReport() {
        try { return salesRepo.getAllSales(); } catch (Exception e) { return null; }
    }
        public java.util.Map<String, Integer> getTopSweetsData() {
        try {
            return salesRepo.getTopSellingProducts();
        } catch (Exception e) {
            return new java.util.HashMap<>();
        }
    }

}
