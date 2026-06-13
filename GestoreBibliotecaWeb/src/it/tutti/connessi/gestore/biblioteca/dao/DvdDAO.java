package it.tutti.connessi.gestore.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DvdDAO extends ArticoloDAO {

    public boolean aggiungiDvd(String titolo, int durataMinuti, String regista) {
        String queryFiglio = "INSERT INTO dvd (id, durata_minuti, regista) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = ConnessioneDB.getConnessione();
            if (conn == null) return false;

            conn.setAutoCommit(false); // Avvia la transazione

            // 1. Inserisce nella tabella padre e ottiene l'ID generato
            int idRisorsa = inserisciRisorsaPadre(conn, titolo, "DVD");

            // 2. Inserisce nella tabella figlia 'dvd' usando lo stesso ID
            try (PreparedStatement stmtFiglio = conn.prepareStatement(queryFiglio)) {
                stmtFiglio.setInt(1, idRisorsa);
                stmtFiglio.setInt(2, durataMinuti);
                stmtFiglio.setString(3, regista);
                stmtFiglio.executeUpdate();
            }

            conn.commit(); // Salva tutto nel DB
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Annulla in caso di errore
                    System.err.println("Transazione annullata (Rollback effettuato).");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Errore nell'aggiunta del DVD: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

	public boolean modificaRisorsa(int id, String nuovoTitolo, int durataMinuti, String regista) {
        // Ipotizzando che nella tabella dvd ci siano le colonne 'regista_autore' e 'casa_discografica'
        String queryFiglio = "UPDATE dvd SET regista = ?, durata_minuti = ? WHERE id = ?";
        Connection conn = null;

        try {
            conn = ConnessioneDB.getConnessione();
            if (conn == null) return false;
            
            conn.setAutoCommit(false); // Avvia transazione

            // 1. Aggiorna la tabella specifica 'dvd'
            try (PreparedStatement stmtFiglio = conn.prepareStatement(queryFiglio)) {
                stmtFiglio.setString(1, regista);
                stmtFiglio.setInt(2, durataMinuti);
                stmtFiglio.setInt(3, id);
                
                int righeModificate = stmtFiglio.executeUpdate();
                if (righeModificate == 0) {
                    System.out.println("❌ ID " + id + " non trovato in 'dvd' o la risorsa non è un DVD. Impossibile modificare.");
                    conn.rollback();
                    return false;
                }
            }

            // 2. Aggiorna la tabella padre
            aggiornaTitoloPadre(conn, id, nuovoTitolo);

            conn.commit();
            System.out.println("📝 DVD ID " + id + " modificato con successo nel database!");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("Errore durante la modifica del DVD: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }


}