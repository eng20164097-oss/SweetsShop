import view.LoginFrame;
import db.DatabaseManager;

public class App {
    public static void main(String[] args) {
        System.out.println("--- Sweets Shop System Starting ---");
        
        try {
            // 1. تشغيل قاعدة البيانات
            System.out.println("Initializing Database...");
            DatabaseManager.initializeDatabase();
            
            // 2. إظهار نافذة تسجيل الدخول
            System.out.println("Opening Login Window...");
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            
        } catch (Exception e) {
            System.out.println("System Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
