package vet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VaccineStockService {
    public void addVaccineStock(VaccineStock stock) throws SQLException {
        String query = "INSERT INTO vaccine_stock (vaccine_name, quantity) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, stock.getVaccineName());
            stmt.setInt(2, stock.getQuantity());
            stmt.executeUpdate();
        }
    }
    
    public void updateVaccineStock(int vaccineId, int quantity) throws SQLException {
        String query = "UPDATE vaccine_stock SET quantity = ? WHERE vaccine_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, quantity);
            stmt.setInt(2, vaccineId);
            stmt.executeUpdate();
        }
    }
    
    public VaccineStock getVaccineStock(int vaccineId) throws SQLException {
        String query = "SELECT * FROM vaccine_stock WHERE vaccine_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, vaccineId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                VaccineStock stock = new VaccineStock();
                stock.setVaccineId(rs.getInt("vaccine_id"));
                stock.setVaccineName(rs.getString("vaccine_name"));
                stock.setQuantity(rs.getInt("quantity"));
                return stock;
            }
        }
        
        return null;
    }
}