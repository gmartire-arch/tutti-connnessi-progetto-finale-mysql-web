package it.tutti.connessi.gestore.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LibroDAO extends ArticoloDAO {

    public boolean aggiungiLibro(String titolo, String autore) {
        String queryFiglio = "INSERT INTO libri (id, autore) VALUES (?, ?)";
        Connection conn = null;

        try {
            conn = ConnessioneDB.getConnessione();
            if (conn == null) return false;
            
            conn.setAutoCommit(false); // Avvia la transazione

            // 1. Inserisce nella tabella padre e ottiene l'ID generato
            int idRisorsa = inserisciRisorsaPadre(conn, titolo, "LIBRO");

            // 2. Inserisce nella tabella figlia 'libri' usando lo stesso ID
            try (PreparedStatement stmtFiglio = conn.prepareStatement(queryFiglio)) {
                stmtFiglio.setInt(1, idRisorsa);
                stmtFiglio.setString(2, autore);
                stmtFiglio.executeUpdate();
            }

            conn.commit(); // Salva tutto nel DB
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Se qualcosa fallisce, annulla l'intera operazione
                    System.err.println("Transazione annullata (Rollback effettuato).");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Errore nell'aggiunta del libro: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

	@Override
	public boolean modificaRisorsa(int id, String nuovoTitolo, int parametroInutile, String nuovoAutore) {
        String queryFiglio = "UPDATE libri SET autore = ? WHERE id = ?";
        Connection conn = null;

        try {
            conn = ConnessioneDB.getConnessione();
            if (conn == null) return false;
            
            conn.setAutoCommit(false); // Avvia la transazione per aggiornare entrambe le tabelle

            // 1. Aggiorna la tabella specifica 'libri'
            try (PreparedStatement stmtFiglio = conn.prepareStatement(queryFiglio)) {
                stmtFiglio.setString(1, nuovoAutore);
                stmtFiglio.setInt(2, id);
                
                int righeModificate = stmtFiglio.executeUpdate();
                if (righeModificate == 0) {
                    // Se non trova l'ID qui, significa che non è un Libro (corrisponde al controllo instanceof fallito)
                    System.out.println("❌ ID " + id + " non trovato in 'libri' o la risorsa non è un Libro. Impossibile modificare.");
                    conn.rollback();
                    return false;
                }
            }

            // 2. Aggiorna il titolo nella tabella padre 'articoli'
            aggiornaTitoloPadre(conn, id, nuovoTitolo);

            conn.commit(); // Salva le modifiche stabilmente
            System.out.println("📝 Libro ID " + id + " modificato con successo nel database!");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("Errore durante la modifica del libro: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }


}