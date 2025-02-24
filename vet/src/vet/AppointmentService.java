package vet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    public void scheduleAppointment(Appointment appointment) throws SQLException {
        String query = "INSERT INTO appointments (pet_id, service, start_time, end_time, status, notes) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, appointment.getPetId());
            stmt.setString(2, appointment.getService());
            stmt.setTimestamp(3, appointment.getStartTime());
            stmt.setTimestamp(4, appointment.getEndTime());
            stmt.setString(5, appointment.getStatus());
            stmt.setString(6, appointment.getNotes());

            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    appointment.setAppointmentId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Appointment> getAppointmentsByClientId(int clientId) throws SQLException {
        String query = "SELECT a.* FROM appointments a " +
                      "JOIN pets p ON a.pet_id = p.pet_id " +
                      "WHERE p.client_id = ?";
        List<Appointment> appointments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setPetId(rs.getInt("pet_id"));
                appointment.setService(rs.getString("service"));
                appointment.setStartTime(rs.getTimestamp("start_time"));
                appointment.setEndTime(rs.getTimestamp("end_time"));
                appointment.setStatus(rs.getString("status"));
                appointment.setNotes(rs.getString("notes"));
                appointments.add(appointment);
            }
        }

        return appointments;
    }

    public void updateAppointmentStatus(int appointmentId, String status) throws SQLException {
        String query = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            stmt.executeUpdate();
        }
    }

    public void updateAppointment(Appointment appointment) throws SQLException {
        String query = "UPDATE appointments SET pet_id = ?, service = ?, start_time = ?, end_time = ?, status = ?, notes = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, appointment.getPetId());
            stmt.setString(2, appointment.getService());
            stmt.setTimestamp(3, appointment.getStartTime());
            stmt.setTimestamp(4, appointment.getEndTime());
            stmt.setString(5, appointment.getStatus());
            stmt.setString(6, appointment.getNotes());
            stmt.setInt(7, appointment.getAppointmentId());
            
            stmt.executeUpdate();
        }
    }

    public Appointment getAppointmentById(int appointmentId) throws SQLException {
        String query = "SELECT * FROM appointments WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(rs.getInt("appointment_id"));
                appointment.setPetId(rs.getInt("pet_id"));
                appointment.setService(rs.getString("service"));
                appointment.setStartTime(rs.getTimestamp("start_time"));
                appointment.setEndTime(rs.getTimestamp("end_time"));
                appointment.setStatus(rs.getString("status"));
                appointment.setNotes(rs.getString("notes"));
                return appointment;
            }
        }
        return null;
    }
}