import view.LoginFrame;
import db.DatabaseManager;

public class App {
    public static void main(String[] args) {
        // تهيئة قاعدة البيانات أولاً
        DatabaseManager.initializeDatabase();

        // فتح واجهة تسجيل الدخول
        new LoginFrame();
    }
}
