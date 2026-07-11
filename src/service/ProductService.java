package service;

import model.Product;
import repository.ProductRepository;
import java.util.ArrayList;

public class ProductService {
    private ProductRepository productRepository = new ProductRepository();

    public ArrayList<Product> getAllProducts() {
        try {
            return productRepository.getAllProductsList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void saveProduct(String name, double price, int stock, String image) throws Exception {
        if (price <= 0) throw new Exception("السعر يجب أن يكون أكبر من صفر");
        productRepository.addProduct(name, price, stock, image);
    }

    public void deleteProduct(int id) throws Exception {
        productRepository.deleteProduct(id);
    }

    public void updateProduct(int id, String name, double price, int stock) throws Exception {
        productRepository.updateProduct(id, name, price, stock);
    }
}

