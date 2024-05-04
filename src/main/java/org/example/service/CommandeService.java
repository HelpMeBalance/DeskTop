package org.example.service;

import org.example.models.Commande;
import org.example.utils.MyDataBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandeService {
    private final Connection connect;

    public CommandeService() {
        connect = MyDataBase.getInstance().getConnection();
    }

    public void addCommande(Commande commande) throws SQLException {
        String deletePanierItemsQuery = "DELETE FROM panier WHERE user_id = ?";
        String addCommandeQuery = "INSERT INTO commande (user_id, username, address, paymentmethode, status, total_price, sale_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement deleteStatement = connect.prepareStatement(deletePanierItemsQuery);
             PreparedStatement addCommandeStatement = connect.prepareStatement(addCommandeQuery)) {
            // Set parameters for deleting Panier items
            deleteStatement.setInt(1, commande.getUserId());
            // Execute the delete query
            deleteStatement.executeUpdate();

            // Set parameters for adding Commande
            addCommandeStatement.setInt(1, commande.getUserId());
            addCommandeStatement.setString(2, commande.getUsername());
            addCommandeStatement.setString(3, commande.getAddress());
            addCommandeStatement.setString(4, commande.getPaymentMethod());
            addCommandeStatement.setInt(5, commande.getStatus());
            addCommandeStatement.setDouble(6, commande.getTotalPrice());
            addCommandeStatement.setString(7, commande.getSaleCode());

            // Execute the insert query
            addCommandeStatement.executeUpdate();
        }
    }
}
