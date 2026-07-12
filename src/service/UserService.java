package service;

import repository.UserRepository;
import java.sql.ResultSet;
/**
 * Layer 2: Business Logic Layer.
 * Handles user-related business logic, including authentication (login)
 * and managing staff accounts (Manager, Cashier, Chef).
 */


public class UserService {
    private UserRepository userRepository = new UserRepository();

    public String login(String username, String password) {
        try {
            // منطق العمل: منع المحاولات الفارغة
            if (username.isEmpty() || password.isEmpty()) return null;
            return userRepository.authenticateUser(username, password);
        } catch (Exception e) {
            return null;
        }
    }

    public ResultSet fetchAllUsers() {
        try {
            return userRepository.getAllUsers();
        } catch (Exception e) {
            return null;
        }
    }

    public void createStaff(String name, String role, String pass) throws Exception {
        if (name.isEmpty() || pass.length() < 3) throw new Exception("بيانات غير صالحة");
        userRepository.addUser(name, role, pass);
    }

    public void removeStaff(int id) throws Exception {
        userRepository.deleteUser(id);
    }
        public void updateStaff(int id, String name, String role) throws Exception {
        // نطلب من الـ Repository تنفيذ الأمر
        userRepository.updateUser(id, name, role);
    }

}
