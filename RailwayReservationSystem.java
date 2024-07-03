import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RailwayReservationSystem {

    public void viewAvailableTrains() {
        String query = "SELECT * FROM Trains WHERE SeatsAvailable > 0";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.println("TrainID: " + rs.getInt("TrainID"));
                System.out.println("TrainName: " + rs.getString("TrainName"));
                System.out.println("Source: " + rs.getString("Source"));
                System.out.println("Destination: " + rs.getString("Destination"));
                System.out.println("SeatsAvailable: " + rs.getInt("SeatsAvailable"));
                System.out.println("-------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addReservation(String passengerName, int trainID) {
        String insertReservation = "INSERT INTO Reservations (PassengerName, TrainID) VALUES (?, ?)";
        String updateTrain = "UPDATE Trains SET SeatsAvailable = SeatsAvailable - 1 WHERE TrainID = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement psInsert = connection.prepareStatement(insertReservation);
             PreparedStatement psUpdate = connection.prepareStatement(updateTrain)) {
            connection.setAutoCommit(false);

            psInsert.setString(1, passengerName);
            psInsert.setInt(2, trainID);
            psInsert.executeUpdate();

            psUpdate.setInt(1, trainID);
            psUpdate.executeUpdate();

            connection.commit();
            System.out.println("Reservation added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelReservation(int reservationID) {
        String selectTrainID = "SELECT TrainID FROM Reservations WHERE ReservationID = ?";
        String deleteReservation = "DELETE FROM Reservations WHERE ReservationID = ?";
        String updateTrain = "UPDATE Trains SET SeatsAvailable = SeatsAvailable + 1 WHERE TrainID = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement psSelect = connection.prepareStatement(selectTrainID);
             PreparedStatement psDelete = connection.prepareStatement(deleteReservation);
             PreparedStatement psUpdate = connection.prepareStatement(updateTrain)) {
            connection.setAutoCommit(false);

            psSelect.setInt(1, reservationID);
            ResultSet rs = psSelect.executeQuery();
            if (rs.next()) {
                int trainID = rs.getInt("TrainID");

                psDelete.setInt(1, reservationID);
                psDelete.executeUpdate();

                psUpdate.setInt(1, trainID);
                psUpdate.executeUpdate();

                connection.commit();
                System.out.println("Reservation canceled successfully!");
            } else {
                System.out.println("Reservation not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RailwayReservationSystem system = new RailwayReservationSystem();

        // View available trains
        system.viewAvailableTrains();

        // Add a new reservation
        system.addReservation("John Doe", 1);

        // Cancel a reservation
        system.cancelReservation(1);
    }
}
