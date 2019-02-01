package ba.unsa.etf.rpr.projekat;

import java.time.LocalDate;

public class Owner {
    private int id;
    private String firstName, lastName, parentName;
    private LocalDate dateOfBirth;
    private Location placeOfBirth;
    private String address;
    private Location placeOfResidence;
    private String nationalIdentificationNumber;
    private long dateOfBirthDays;

    public Owner() {
    }

    public Owner(int id, String firstName, String lastName, String parentName, LocalDate dateOfBirth, Location placeOfBirth, String address, Location placeOfResidence, String nationalIdentificationNumber, long dateOfBirthDays) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.parentName = parentName;
        this.dateOfBirth = dateOfBirth;
        this.placeOfBirth = placeOfBirth;
        this.address = address;
        this.placeOfResidence = placeOfResidence;
        this.nationalIdentificationNumber = nationalIdentificationNumber;
        this.dateOfBirthDays = dateOfBirthDays;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        this.dateOfBirthDays = dateOfBirth.toEpochDay();
    }

    public Location getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(Location placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getPlaceOfResidence() {
        return placeOfResidence;
    }

    public void setPlaceOfResidence(Location placeOfResidence) {
        this.placeOfResidence = placeOfResidence;
    }

    public String getNationalIdentificationNumber() {
        return nationalIdentificationNumber;
    }

    public void setNationalIdentificationNumber(String nationalIdentificationNumber) {
        this.nationalIdentificationNumber = nationalIdentificationNumber;
    }

    public long getDateOfBirthDays() {
        return dateOfBirthDays;
    }

    public void setDateOfBirthDays(long dateOfBirthDays) {
        this.dateOfBirthDays = dateOfBirthDays;
        this.dateOfBirth = LocalDate.ofEpochDay(dateOfBirthDays);
    }

    @Override
    public String toString() {
        return firstName + ' ' + lastName;
    }
}
