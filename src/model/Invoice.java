package model;
import java.awt.Desktop;
import java.io.PrintWriter;
import java.io.File;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles the generation of a physical text-based invoice.
 * Demonstrates File I/O (Requirement for the course).
 */
public class Invoice {
    private String cashierName;
    private List<OrderItem> items;
    private double total;

    public Invoice(String cashierName, List<OrderItem> items, double total) {
        this.cashierName = cashierName;
        this.items = items;
        this.total = total;
    }

    /**
     * Generates a formatted text file as a receipt.
     */
        /**
     * Generates a perfectly formatted receipt.
     */
        public void generateReceiptFile() {
        String fileName = "Receipt_" + System.currentTimeMillis() + ".txt";
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(fileName))) {
            
            writer.println("==========================================");
            writer.println("           SWEETS SHOP SYSTEM             ");
            writer.println("==========================================");
            writer.println("Cashier: " + cashierName);
            writer.println("Date: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            writer.println("------------------------------------------");
            
            // رسم رؤوس الأعمدة بتنسيق ثابت (15 حرف للاسم، 8 للكمية، 10 للسعر)
            // %-15s تعني نص جهة اليسار بمساحة 15 خانة
            writer.printf(java.util.Locale.US, "%-15s %-8s %-10s%n", "Item", "Qty", "Total");
            writer.println("------------------------------------------");

            for (OrderItem item : items) {
                String name = item.getProductName();
                // إذا كان الاسم طويلاً جداً نقصه لكي لا يخرب العمود
                if (name.length() > 15) name = name.substring(0, 12) + "...";

                // طباعة الصنف: الاسم ثم الكمية ثم المجموع (كلها إنجليزي ومنظمة)
                writer.printf(java.util.Locale.US, "%-15s %-8d %-10.2f%n", 
                    name, 
                    item.getQuantity(), 
                    item.getSubTotal());
            }

            writer.println("------------------------------------------");
            writer.printf(java.util.Locale.US, "GRAND TOTAL:             %.2f LYD%n", total);
            writer.println("==========================================");
            writer.println("        THANK YOU FOR YOUR VISIT          ");
            writer.println("==========================================");
            
            writer.flush();
            writer.close();

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(new java.io.File(fileName));
            }
          
        } catch (Exception e) {
            System.out.println("Error generating receipt: " + e.getMessage());
        }
    }
}


    

