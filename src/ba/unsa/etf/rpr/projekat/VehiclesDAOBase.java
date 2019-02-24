package ba.unsa.etf.rpr.projekat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class VehiclesDAOBase implements VehiclesDAO {
    private Connection conn;
    private PreparedStatement getOwnersQuery, getOwnerQuery, getNewOwnerIdQuery, addOwnerQuery, changeOwnerQuery, deleteOwnerQuery;
    private PreparedStatement getVehiclesQuery, getNewVehicleIdQuery, getVehiclesForOwnerQuery, addVehicleQuery, changeVehicleQuery, deleteVehicleQuery;
    private PreparedStatement getLocationsQuery, getLocationQuery, getNewLocationIdQuery, addLocationQuery;
    private PreparedStatement getBrandsQuery, getBrandQuery, getNewBrandIdQuery, addBrandQuery;

    VehiclesDAOBase() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:resources/db/vehicle.db");

            //Owner
            getOwnersQuery = conn.prepareStatement("SELECT * FROM owner");
            getOwnerQuery = conn.prepareStatement("SELECT * FROM owner WHERE id=?");
            getNewOwnerIdQuery = conn.prepareStatement("SELECT MAX(id)+1 FROM owner");
            addOwnerQuery = conn.prepareStatement("INSERT INTO owner VALUES (?,?,?,?,?,?,?,?,?)");
            changeOwnerQuery = conn.prepareStatement("UPDATE owner SET first_name=?, last_name=?, parent_name=?, date_of_birth=?, place_of_birth=?, address=?, place_of_residence=?, national_id_number=? WHERE id=?");
            deleteOwnerQuery = conn.prepareStatement("DELETE FROM owner WHERE id=?");

            //Vehicle
            getVehiclesQuery = conn.prepareStatement("SELECT * FROM vehicle");
            getNewVehicleIdQuery = conn.prepareStatement("SELECT MAX(id)+1 FROM vehicle");
            getVehiclesForOwnerQuery = conn.prepareStatement("SELECT COUNT(*) FROM vehicle WHERE owner=?");
            addVehicleQuery = conn.prepareStatement("INSERT INTO vehicle VALUES (?,?,?,?,?,?)");
            changeVehicleQuery = conn.prepareStatement("UPDATE vehicle SET brand=?, model=?, vehicle_id_number=?, number_plate=?, owner=? WHERE id=?");
            deleteVehicleQuery = conn.prepareStatement("DELETE FROM vehicle WHERE id=?");

            //Location
            getLocationsQuery = conn.prepareStatement("SELECT * FROM location ORDER BY name");
            getLocationQuery = conn.prepareStatement("SELECT * FROM location WHERE id=?");
            getNewLocationIdQuery = conn.prepareStatement("SELECT MAX(id)+1 FROM location");
            addLocationQuery = conn.prepareStatement("INSERT INTO location VALUES (?,?,?)");

            //Brand
            getBrandsQuery = conn.prepareStatement("SELECT * FROM brand ORDER BY name");
            getBrandQuery = conn.prepareStatement("SELECT * FROM brand WHERE id=?");
            getNewBrandIdQuery = conn.prepareStatement("SELECT MAX(id)+1 FROM brand");
            addBrandQuery = conn.prepareStatement("INSERT INTO brand VALUES (?,?)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ObservableList<Owner> getOwners() {
        ObservableList<Owner> owners = FXCollections.observableArrayList();
        try {
            ResultSet rs = getOwnersQuery.executeQuery();
            while (rs.next()) {
                Owner owner = getOwnerFromResultSet(rs);
                owners.add(owner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return owners;
    }

    private Owner getOwnerFromResultSet(ResultSet rs) throws SQLException {
        getLocationQuery.setInt(1, rs.getInt(6));
        ResultSet rs2 = getLocationQuery.executeQuery();
        Location placeOfBirth = null;
        while (rs2.next()) {
            placeOfBirth = new Location(rs2.getInt(1), rs2.getString(2), rs2.getString(3));
        }

        getLocationQuery.setInt(1, rs.getInt(8));
        rs2 = getLocationQuery.executeQuery();
        Location placeOfResidence = null;
        while (rs2.next()) {
            placeOfResidence = new Location(rs2.getInt(1), rs2.getString(2), rs2.getString(3));
        }

        LocalDate dateOfBirth = rs.getDate(5).toLocalDate();

        Owner owner = new Owner(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                dateOfBirth, placeOfBirth, rs.getString(7), placeOfResidence, rs.getString(9));
        return owner;
    }

    @Override
    public ObservableList<Vehicle> getVehicles() {
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
        try {
            ResultSet rs = getVehiclesQuery.executeQuery();
            while (rs.next()) {
                getBrandQuery.setInt(1, rs.getInt(2));
                ResultSet rs2 = getBrandQuery.executeQuery();
                Brand brand = null;
                while (rs2.next()) {
                    brand = new Brand(rs2.getInt(1), rs2.getString(2));
                }

                getOwnerQuery.setInt(1, rs.getInt(6));
                ResultSet rs3 = getOwnerQuery.executeQuery();
                Owner owner = null;
                if (rs3.next()) owner = getOwnerFromResultSet(rs3);

                Vehicle vehicle = new Vehicle(rs.getInt(1), brand, rs.getString(3), rs.getString(4), rs.getString(5), owner);
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    @Override
    public ObservableList<Location> getLocations() {
        ObservableList<Location> locations = FXCollections.observableArrayList();
        try {
            ResultSet rs = getLocationsQuery.executeQuery();
            while (rs.next()) {
                Location location = new Location(rs.getInt(1), rs.getString(2), rs.getString(3));
                locations.add(location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }

    @Override
    public ObservableList<Brand> getBrands() {
        ObservableList<Brand> brands = FXCollections.observableArrayList();
        try {
            ResultSet rs = getBrandsQuery.executeQuery();
            while (rs.next()) {
                Brand brand = new Brand(rs.getInt(1), rs.getString(2));
                brands.add(brand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return brands;
    }

    private Location addLocationIfNotExists(Location location) {
        try {
            getLocationQuery.setInt(1, location.getId());
            ResultSet rs = getLocationQuery.executeQuery();
            if (!rs.next()) {
                int newId = 1;
                ResultSet rs2 = getNewLocationIdQuery.executeQuery();
                if (rs2.next()) newId = rs2.getInt(1);
                addLocationQuery.setInt(1, newId);
                addLocationQuery.setString(2, location.getName());
                addLocationQuery.setString(3, location.getPostalCode());
                addLocationQuery.executeUpdate();
                location.setId(newId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void addOwner(Owner owner) {
        try {
            owner.setPlaceOfBirth(addLocationIfNotExists(owner.getPlaceOfBirth()));
            owner.setPlaceOfResidence(addLocationIfNotExists(owner.getPlaceOfResidence()));

            ResultSet rs = getNewOwnerIdQuery.executeQuery();
            int newId = 1;
            if (rs.next()) newId = rs.getInt(1);
            owner.setId(newId);

            addOwnerQuery.setInt(1, owner.getId());
            addOwnerQuery.setString(2, owner.getFirstName());
            addOwnerQuery.setString(3, owner.getLastName());
            addOwnerQuery.setString(4, owner.getParentName());
            addOwnerQuery.setDate(5, Date.valueOf(owner.getDateOfBirth()));
            addOwnerQuery.setInt(6, owner.getPlaceOfBirth().getId());
            addOwnerQuery.setString(7, owner.getAddress());
            addOwnerQuery.setInt(8, owner.getPlaceOfResidence().getId());
            addOwnerQuery.setString(9, owner.getNationalIdNumber());
            addOwnerQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeOwner(Owner owner) {
        try {
            owner.setPlaceOfBirth(addLocationIfNotExists(owner.getPlaceOfBirth()));
            owner.setPlaceOfResidence(addLocationIfNotExists(owner.getPlaceOfResidence()));
            changeOwnerQuery.setInt(9, owner.getId());
            changeOwnerQuery.setString(1, owner.getFirstName());
            changeOwnerQuery.setString(2, owner.getLastName());
            changeOwnerQuery.setString(3, owner.getParentName());
            changeOwnerQuery.setDate(4, Date.valueOf(owner.getDateOfBirth()));
            changeOwnerQuery.setInt(5, owner.getPlaceOfBirth().getId());
            changeOwnerQuery.setString(6, owner.getAddress());
            changeOwnerQuery.setInt(7, owner.getPlaceOfResidence().getId());
            changeOwnerQuery.setString(8, owner.getNationalIdNumber());
            changeOwnerQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOwner(Owner owner) {
        try {
            getVehiclesForOwnerQuery.setInt(1, owner.getId());
            ResultSet rs = getVehiclesForOwnerQuery.executeQuery();
            if (rs.next())
                if (rs.getInt(1) > 0)
                    throw new IllegalArgumentException("Owner already has a vehicle!");
            deleteOwnerQuery.setInt(1, owner.getId());
            deleteOwnerQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Brand addBrandIfNotExists(Brand brand) {
        try {
            getBrandQuery.setInt(1, brand.getId());
            ResultSet rs = getBrandQuery.executeQuery();
            if (!rs.next()) {
                int newId = 1;
                ResultSet rs2 = getNewBrandIdQuery.executeQuery();
                if (rs2.next()) newId = rs2.getInt(1);

                addBrandQuery.setInt(1, newId);
                addBrandQuery.setString(2, brand.getName());
                addBrandQuery.executeUpdate();
                brand.setId(newId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return brand;
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        try {
            vehicle.setBrand(addBrandIfNotExists(vehicle.getBrand()));

            // Check owner
            getOwnerQuery.setInt(1, vehicle.getOwner().getId());
            ResultSet rs = getOwnerQuery.executeQuery();
            if (!rs.next())
                throw new IllegalArgumentException("Unknown owner with ID" + vehicle.getOwner().getId());

            rs = getNewVehicleIdQuery.executeQuery();
            int newId = 1;
            if (rs.next()) newId = rs.getInt(1);
            vehicle.setId(newId);

            addVehicleQuery.setInt(1, vehicle.getId());
            addVehicleQuery.setInt(2, vehicle.getBrand().getId());
            addVehicleQuery.setString(3, vehicle.getModel());
            addVehicleQuery.setString(4, vehicle.getVehicleIdNumber());
            addVehicleQuery.setString(5, vehicle.getNumberPlate());
            addVehicleQuery.setInt(6, vehicle.getOwner().getId());
            addVehicleQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeVehicle(Vehicle vehicle) {
        try {
            vehicle.setBrand(addBrandIfNotExists(vehicle.getBrand()));

            // Check owner
            getVehiclesQuery.setInt(1, vehicle.getOwner().getId());
            ResultSet rs = getVehiclesQuery.executeQuery();
            if (!rs.next())
                throw new IllegalArgumentException("Unknown owner with ID." + vehicle.getOwner().getId());

            changeVehicleQuery.setInt(6, vehicle.getId());
            changeVehicleQuery.setInt(1, vehicle.getBrand().getId());
            changeVehicleQuery.setString(2, vehicle.getModel());
            changeVehicleQuery.setString(3, vehicle.getVehicleIdNumber());
            changeVehicleQuery.setString(4, vehicle.getNumberPlate());
            changeVehicleQuery.setInt(5, vehicle.getOwner().getId());
            changeVehicleQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteVehicle(Vehicle vehicle) {
        try {
            deleteVehicleQuery.setInt(1, vehicle.getId());
            deleteVehicleQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
