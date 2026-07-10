import view.LoginFrame;
import db.DatabaseManager;

public class App {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        // نبدأ بواجهة الزبون العامة
        new view.CustomerHome(); 
    }
}


