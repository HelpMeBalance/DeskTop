package org.example.service;

import org.example.models.Personne;
import org.example.models.RendezVous;
import org.example.models.User;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RendezVousService implements IService<RendezVous>{
    Connection connection;
    public RendezVousService() {
        connection= MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(RendezVous rendezVous) throws SQLException {
        String sql = "INSERT INTO rendez_vous (date_r, nom_service, statut, certificat, user_id, patient_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(rendezVous.getDateR()));
        preparedStatement.setString(2, rendezVous.getNomService());
        preparedStatement.setBoolean(3, rendezVous.isStatut());
        preparedStatement.setBoolean(4, rendezVous.isCertificat());
        preparedStatement.setInt(5, /*rendezVous.getPsy().getId()*/1);
        preparedStatement.setInt(6, /*rendezVous.getPatient().getId()*/1);

        preparedStatement.executeUpdate();
    }

    @Override
    public void update(RendezVous rendezVous) throws SQLException {
        String sql = "UPDATE rendez_vous SET date_r = ?, nom_service = ?, statut = ?, certificat = ?, user_id = ?, patient_id = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(rendezVous.getDateR()));
        preparedStatement.setString(2, rendezVous.getNomService());
        preparedStatement.setBoolean(3, rendezVous.isStatut());
        preparedStatement.setBoolean(4, rendezVous.isCertificat());
        preparedStatement.setInt(5, rendezVous.getPsy().getId());
        preparedStatement.setInt(6, rendezVous.getPatient().getId());
        preparedStatement.setInt(7, rendezVous.getId());

        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM rendez_vous WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        preparedStatement.executeUpdate();
    }

    @Override
    public List<RendezVous> select() throws SQLException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String sql = "SELECT * FROM rendez_vous";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                LocalDateTime dateR = resultSet.getTimestamp("date_r").toLocalDateTime();
                String nomService = resultSet.getString("nom_service");
                boolean statut = resultSet.getBoolean("statut");
                boolean certificat = resultSet.getBoolean("certificat");
                int psy_id = resultSet.getInt("user_id");
                int patient_Id = resultSet.getInt("patient_id");

                // Assuming you have constructors or setter methods in the RendezVous class
                rendezVousList.add(new RendezVous(id, dateR, nomService, statut, certificat, new UserService().selectWhere(psy_id), new UserService().selectWhere(patient_Id)));
            }
        }
        return rendezVousList;
    }

    @Override
    public RendezVous selectWhere(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM  rendez_vous WHERE id = ?");
        try (preparedStatement)
        {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int idrv = resultSet.getInt("id");
                    LocalDateTime dateR = resultSet.getTimestamp("date_r").toLocalDateTime();
                    String nomService = resultSet.getString("nom_service");
                    boolean statut = resultSet.getBoolean("statut");
                    boolean certificat = resultSet.getBoolean("certificat");
                    int psy_id = resultSet.getInt("user_id");
                    int patient_Id = resultSet.getInt("patient_id");

                    // Assuming you have constructors or setter methods in the RendezVous class
                    return new RendezVous(idrv, dateR, nomService, statut, certificat, new UserService().selectWhere(psy_id), new UserService().selectWhere(patient_Id));
                }
            }
        }

        return null ;
    }
}
