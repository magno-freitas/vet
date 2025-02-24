package vet;

public class VaccineStock {
    private int vaccineId;
    private String vaccineName;
    private int quantity;
    
    public VaccineStock() {}
    
    public int getVaccineId() {
        return vaccineId;
    }
    
    public void setVaccineId(int vaccineId) {
        this.vaccineId = vaccineId;
    }
    
    public String getVaccineName() {
        return vaccineName;
    }
    
    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}