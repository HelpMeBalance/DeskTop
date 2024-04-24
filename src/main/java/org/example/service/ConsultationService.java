package org.example.service;

import org.example.models.Consultation;
import org.example.models.RendezVous;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationService implements IService<Consultation> {
    Connection connection;

    public ConsultationService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(Consultation consultation) throws SQLException {
        String sql = "INSERT INTO consultation (rendezvous_id, recommandation_suivi, note, psychiatre_id, patient_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, consultation.getAppointment().getId());
        preparedStatement.setBoolean(2, false);
        preparedStatement.setString(3, consultation.getNote());
        preparedStatement.setInt(4, consultation.getPsy().getId());
        preparedStatement.setInt(5, consultation.getPatient().getId());

        preparedStatement.executeUpdate();
    }

    @Override
    public void update(Consultation consultation) throws SQLException {
        String sql = "UPDATE consultation SET avis_patient = ?, recommandation_suivi = ?, note = ?, psychiatre_id = ?, patient_id = ?, rendezvous_id = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDouble(1, consultation.getRating());
        preparedStatement.setBoolean(2, consultation.isRecommandation_suivi());
        preparedStatement.setString(3, consultation.getNote());
        preparedStatement.setInt(4, consultation.getPsy().getId());
        preparedStatement.setInt(5, consultation.getPatient().getId());
        preparedStatement.setInt(6, consultation.getAppointment().getId());
        preparedStatement.setInt(7, consultation.getId());

        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM consultation WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        preparedStatement.executeUpdate();
    }

    @Override
    public List<Consultation> select() throws SQLException {
        List<Consultation> consultationList = new ArrayList<>();
        String sql = "SELECT * FROM consultation";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int appointmentId = resultSet.getInt("rendezvous_id");
                double duration = resultSet.getDouble("duree");
                double rating = resultSet.getDouble("avis_patient");
                if (resultSet.wasNull()) {
                    rating = -1;
                }
                boolean recommandationSuivi = resultSet.getBoolean("recommandation_suivi");
                String note = resultSet.getString("note");
                int psyId = resultSet.getInt("psychiatre_id");
                int patientId = resultSet.getInt("patient_id");

                // Assuming you have constructors or setter methods in the Consultation class
                consultationList.add(new Consultation(id, new RendezVousService().selectWhere(appointmentId), duration, rating, recommandationSuivi, note, new UserService().selectWhere(psyId), new UserService().selectWhere(patientId)));
            }
        }
        return consultationList;
    }

    @Override
    public Consultation selectWhere(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM consultation WHERE id = ?");
        try (preparedStatement) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int appointmentId = resultSet.getInt("rendezvous_id");
                    double duration = resultSet.getDouble("duree");
                    double rating = resultSet.getDouble("avis_patient");
                    boolean recommandationSuivi = resultSet.getBoolean("recommandation_suivi");
                    String note = resultSet.getString("note");
                    int psyId = resultSet.getInt("psychiatre_id");
                    int patientId = resultSet.getInt("patient_id");

                    // Assuming you have constructors or setter methods in the Consultation class
                    return new Consultation(id, new RendezVousService().selectWhere(appointmentId), duration, rating, recommandationSuivi, note, new UserService().selectWhere(psyId), new UserService().selectWhere(patientId));
                }
            }
        }

        return null;
    }
}
