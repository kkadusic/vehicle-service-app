package ba.unsa.etf.rpr.projekat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class VehiclesDAOXML implements VehiclesDAO {
    private ArrayList<Owner> owners = new ArrayList<>();
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<Location> locations = new ArrayList<>();
    private ArrayList<Brand> brands = new ArrayList<>();
    private ArrayList<Part> parts = new ArrayList<>();

    VehiclesDAOXML() {
        readFiles();
    }

    private void readFiles() {
        owners.clear();
        vehicles.clear();
        try {
            XMLDecoder decoder = new XMLDecoder(new FileInputStream("owners.xml"));
            owners = (ArrayList<Owner>) decoder.readObject();
            decoder.close();
            decoder = new XMLDecoder(new FileInputStream("vehicles.xml"));
            vehicles = (ArrayList<Vehicle>) decoder.readObject();
            decoder.close();
            decoder = new XMLDecoder(new FileInputStream("locations.xml"));
            locations = (ArrayList<Location>) decoder.readObject();
            decoder.close();
            decoder = new XMLDecoder(new FileInputStream("brands.xml"));
            brands = (ArrayList<Brand>) decoder.readObject();
            decoder.close();
            decoder = new XMLDecoder(new FileInputStream("parts.xml"));
            parts = (ArrayList<Part>) decoder.readObject();
            decoder.close();

            // Sorting lists
            locations.sort((m1, m2) -> m1.getName().compareTo(m2.getName()));
            brands.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeFiles() {
        try {
            XMLEncoder encoder = new XMLEncoder(new FileOutputStream("owners.xml"));
            encoder.writeObject(owners);
            encoder.close();
            encoder = new XMLEncoder(new FileOutputStream("vehicles.xml"));
            encoder.writeObject(vehicles);
            encoder.close();
            encoder = new XMLEncoder(new FileOutputStream("locations.xml"));
            encoder.writeObject(locations);
            encoder.close();
            encoder = new XMLEncoder(new FileOutputStream("brands.xml"));
            encoder.writeObject(brands);
            encoder.close();
            encoder = new XMLEncoder(new FileOutputStream("parts.xml"));
            encoder.writeObject(parts);
            encoder.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ObservableList<Owner> getOwners() {
        return FXCollections.observableArrayList(owners);
    }

    @Override
    public ObservableList<Vehicle> getVehicles() {
        return FXCollections.observableArrayList(vehicles);
    }

    @Override
    public ObservableList<Location> getLocations() {
        return FXCollections.observableArrayList(locations);
    }

    @Override
    public ObservableList<Brand> getBrands() {
        return FXCollections.observableArrayList(brands);
    }

    @Override
    public ObservableList<Part> getParts() {
        return FXCollections.observableArrayList(parts);
    }

    @Override
    public void addOwner(Owner owner) {
        owner.setPlaceOfBirth(addLocationIfNotExists(owner.getPlaceOfBirth()));
        owner.setPlaceOfResidence(addLocationIfNotExists(owner.getPlaceOfResidence()));

        int maxId = 0;
        for (Owner o : owners)
            if (o.getId() > maxId)
                maxId = o.getId();

        owner.setId(maxId + 1);

        owners.add(owner);
        writeFiles();
    }

    private Location addLocationIfNotExists(Location location) {
        int maxId = 0;
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).getId() == location.getId()) {
                locations.set(i, location);
                return location;
            } else if (locations.get(i).getId() > maxId)
                maxId = locations.get(i).getId();
        location.setId(maxId + 1);
        locations.add(location);
        locations.sort((m1, m2) -> m1.getName().compareTo(m2.getName()));
        return location;
    }

    @Override
    public void changeOwner(Owner owner) {
        owner.setPlaceOfBirth(addLocationIfNotExists(owner.getPlaceOfBirth()));
        owner.setPlaceOfResidence(addLocationIfNotExists(owner.getPlaceOfResidence()));
        for (int i = 0; i < owners.size(); i++)
            if (owners.get(i).getId() == owner.getId())
                owners.set(i, owner);
        writeFiles();
    }

    @Override
    public void deleteOwner(Owner owner) {
        // If owner already has a vehicle
        for (Vehicle v : vehicles)
            if (v.getOwner().getId() == owner.getId())
                throw new IllegalArgumentException("Owner already has a vehicle");

        for (int i = 0; i < owners.size(); i++)
            if (owners.get(i).getId() == owner.getId())
                owners.remove(i);

        writeFiles();
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        vehicle.setBrand(addBrandIfNotExists(vehicle.getBrand()));

        // Owner check
        boolean is = false;
        for (Owner o : owners)
            if (o.getId() == vehicle.getOwner().getId())
                is = true;
        if (!is)
            throw new IllegalArgumentException("Unknown owner with ID" + vehicle.getOwner().getId());

        // Choosing ID
        int maxId = 0;
        for (Vehicle v : vehicles)
            if (v.getId() > maxId) maxId = v.getId();
        vehicle.setId(maxId + 1);

        vehicles.add(vehicle);
        writeFiles();
    }

    private Brand addBrandIfNotExists(Brand brand) {
        int maxId = 0;
        for (int i = 0; i < brands.size(); i++)
            if (brands.get(i).getId() == brand.getId()) {
                brands.set(i, brand);
                return brand;
            } else if (brands.get(i).getId() > maxId)
                maxId = brands.get(i).getId();
        brand.setId(maxId + 1);
        brands.add(brand);
        brands.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
        return brand;
    }

    @Override
    public void changeVehicle(Vehicle vehicle) {
        vehicle.setBrand(addBrandIfNotExists(vehicle.getBrand()));

        // Owner check
        boolean is = false;
        for (Owner o : owners)
            if (o.getId() == vehicle.getOwner().getId())
                is = true;
        if (!is)
            throw new IllegalArgumentException("Unknown owner with ID" + vehicle.getOwner().getId());

        for (int i = 0; i < vehicles.size(); i++)
            if (vehicles.get(i).getId() == vehicle.getId())
                vehicles.set(i, vehicle);
        writeFiles();
    }

    @Override
    public void deleteVehicle(Vehicle vehicle) {
        for (int i = 0; i < vehicles.size(); i++)
            if (vehicles.get(i).getId() == vehicle.getId())
                vehicles.remove(i);
        writeFiles();
    }

    @Override
    public void addPart(Part part) {
        // Choosing ID
        int maxId = 0;
        for (Part p : parts)
            if (p.getId() > maxId)
                maxId = p.getId();
        part.setId(maxId + 1);
        parts.add(part);
        writeFiles();
    }

    @Override
    public void changePart(Part part) {
        for (int i = 0; i < parts.size(); i++)
            if (parts.get(i).getId() == part.getId())
                parts.set(i, part);
        writeFiles();
    }

    @Override
    public void deletePart(Part part) {
        for (int i = 0; i < parts.size(); i++)
            if (parts.get(i).getId() == part.getId())
                parts.remove(i);
        writeFiles();
    }

    @Override
    public void close() {

    }
}
