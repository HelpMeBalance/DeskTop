package org.example.service;


import org.example.models.Formulaire;
import org.example.models.Formulaireq;
import org.example.models.Formulairerep;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormulaireService implements IService<Formulaire> {
    Connection connection;
    public FormulaireService() {
        connection= MyDataBase.getInstance().getConnection();
    }




        public void add1(Formulaire formulaire, int idq, int idr) throws SQLException {
            // Add the Formulaire object to the database
            add(formulaire);

            // Retrieve the ID of the newly inserted Formulaire object
            int idf = formulaire.getId();


            // Prepare the SQL statements for inserting into formulaire_question and formulaire_reponse tables
            String sql1 = "INSERT INTO formulaire_question (formulaire_id, question_id) VALUES (?, ?)";
            String sql2 = "INSERT INTO formulaire_reponse (formulaire_id, reponse_id) VALUES (?, ?)";
            // Create prepared statements for executing SQL queries
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);

            // Set parameters for the first SQL statement
            preparedStatement1.setInt(1, idf); // Set formulaire_id
            preparedStatement1.setInt(2, idq); // Set question_id

            // Set parameters for the second SQL statement
            preparedStatement2.setInt(1, idf); // Set formulaire_id
            preparedStatement2.setInt(2, idr); // Set reponse_id

            // Execute the SQL statements to insert data into the database
            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();
        }


    @Override
    public void add(Formulaire formulaire) throws SQLException {
        String sql = "INSERT INTO formulaire (user_id, rendez_vous_id) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, 2); // Remplacez par le bon user_id
        preparedStatement.setInt(2, 24); // Remplacez par le bon rendez_vous_id
        preparedStatement.executeUpdate();

        // Récupérez l'ID généré automatiquement
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            int idf = generatedKeys.getInt(1);
            formulaire.setId(idf); // Mettez à jour l'ID du formulaire avec l'ID généré
        } else {
            throw new SQLException("L'insertion du formulaire a échoué, aucun ID généré.");
        }

    }

    @Override
    public void update(Formulaire formulaire) throws SQLException {

    }


    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM formulaire WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Formulaire> select() throws SQLException {
        List<Formulaire> formulaires = new ArrayList<>();
        String sql = "SELECT * FROM formulaire";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int idu = resultSet.getInt("user_id");
                int idr = resultSet.getInt("rendez_vous_id");
                int id = resultSet.getInt("id");
                Formulaire formulaire = new Formulaire(id,idu,idr);
                formulaires.add(formulaire);
            }
        }
        return formulaires;
   }




    public List<Formulaireq> selectq() throws SQLException {
        List<Formulaireq> formulairesq = new ArrayList<>();
        String sql = "SELECT * FROM formulaire_question";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int questionid = resultSet.getInt("question_id");
                int formulaireid = resultSet.getInt("formulaire_id");


                Formulaireq formulaireq = new Formulaireq(formulaireid,questionid);
                formulairesq.add(formulaireq);
            }
        }
        return formulairesq;
    }

    public List<Formulairerep> selectr() throws SQLException {
        List<Formulairerep> formulairesrep = new ArrayList<>();
        String sql = "SELECT * FROM formulaire_reponse";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int reponseid = resultSet.getInt("reponse_id");
                int formulaireid = resultSet.getInt("formulaire_id");


                Formulairerep formulairep = new Formulairerep(formulaireid,reponseid);
                formulairesrep.add(formulairep);
            }
        }
        return formulairesrep;
    }
    @Override
    public Formulaire selectWhere(int id) throws SQLException {
        Formulaire formulaire = null;
        String sql = "SELECT * FROM formulaire WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int idu = resultSet.getInt("user_id");
                    int idr = resultSet.getInt("rendez_vous_id");

                    formulaire = new Formulaire(id,idu,idr);
                }
            }
        }
        return formulaire;
    }
}
