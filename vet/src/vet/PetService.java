package vet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PetService {
    public int addPet(Pet pet) throws SQLException {
        String query = "INSERT INTO pets (client_id, name, species, breed, birth_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pet.getClientId());
            stmt.setString(2, pet.getName());
            stmt.setString(3, pet.getSpecies());
            stmt.setString(4, pet.getBreed());
            stmt.setDate(5, pet.getBirthDate());

            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pet.setPetId(generatedKeys.getInt(1));
                    return pet.getPetId();
                }
            }
        }
        return -1;
    }

    public Pet getPetById(int petId) throws SQLException {
        String query = "SELECT * FROM pets WHERE pet_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, petId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Pet pet = new Pet();
                pet.setPetId(rs.getInt("pet_id"));
                pet.setClientId(rs.getInt("client_id"));
                pet.setName(rs.getString("name"));
                pet.setSpecies(rs.getString("species"));
                pet.setBreed(rs.getString("breed"));
                pet.setBirthDate(rs.getDate("birth_date"));
                return pet;
            }
        }
        return null;
    }

    public int getPetIdByClientAndName(int clientId, String petName) throws SQLException {
        String query = "SELECT pet_id FROM pets WHERE client_id = ? AND name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clientId);
            stmt.setString(2, petName);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("pet_id");
            }
        }
        return -1;
    }
}