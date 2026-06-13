package it.tutti.connessi.gestore.biblioteca.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnessioneDB {
	
	private static final String URL = "jdbc:mysql://mysql-tutticonnessi-tutticonnessi-database.l.aivencloud.com:18981/gestione_biblioteca?ssl-mode=REQUIRED";
	private static final String USER = "avnadmin";
	private static final String PASSWORD = "AVNS_fSF2olOLAjCFYnt1Rqj";
	
    public static Connection getConnessione() {
        try {
            // Carica il driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Errore di connessione al Database: " + e.getMessage());
            return null;
        }
    }
}