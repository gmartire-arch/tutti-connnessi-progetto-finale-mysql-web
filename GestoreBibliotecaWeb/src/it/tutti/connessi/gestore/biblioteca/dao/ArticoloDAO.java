package it.tutti.connessi.gestore.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.tutti.connessi.gestore.biblioteca.dto.ArticoloDTO;
import it.tutti.connessi.gestore.biblioteca.model.Articolo;
import it.tutti.connessi.gestore.biblioteca.model.DVD;
import it.tutti.connessi.gestore.biblioteca.model.Libro;

public abstract class ArticoloDAO {

	// Metodo helper (usato dalle classi figlie)
    protected int inserisciRisorsaPadre(Connection conn, String titolo, String tipoRisorsa) throws SQLException {
        String query = "INSERT INTO articoli (titolo, tipo_articolo) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, titolo);
            stmt.setString(2, tipoRisorsa);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) return generatedKeys.getInt(1);
                else throw new SQLException("Errore: ID non recuperato.");
            }
        }
    }

    // 1. METODO POLIMORFICO: Visualizza tutto il catalogo (Libri + DVD)
    public List<ArticoloDTO> visualizzaCatalogoCompleto() {
    	
    	List<ArticoloDTO> lista = new ArrayList<ArticoloDTO>();
    	String query = "SELECT r.id, r.titolo, r.tipo_articolo, r.disponibile, l.autore, d.durata_minuti, d.regista " +
                       "FROM articoli r " +
                       "LEFT JOIN libri l ON r.id = l.id " +
                       "LEFT JOIN dvd d ON r.id = d.id";

        try (Connection conn = ConnessioneDB.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=== CATALOGO GENERALE BIBLIOTECA ===");
            while (rs.next()) {
                int id = rs.getInt("id");
                String titolo = rs.getString("titolo");
                String tipo = rs.getString("tipo_articolo");
                boolean disponibile = rs.getBoolean("disponibile");

                ArticoloDTO articolo = new ArticoloDTO();
                
                if ("LIBRO".equals(tipo)) {
                	articolo.setId(id);
                	articolo.setTitolo(titolo);
                	articolo.setDisponibile(disponibile);
                	articolo.setAutore(rs.getString("autore"));
                    articolo.setTipo("LIBRO");
                	lista.add(articolo);
                } else if ("DVD".equals(tipo)) {
                	articolo.setId(id);
                	articolo.setTitolo(titolo);
                	articolo.setDisponibile(disponibile);
                	articolo.setRegista(rs.getString("regista"));
                	articolo.setDurata( rs.getInt("durata_minuti"));
                    articolo.setTipo("DVD");
                	lista.add(articolo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore nel recupero del catalogo: " + e.getMessage());
        }
        
        return lista;
    }
    
    
 // Helper comune per aggiornare il titolo nella tabella padre
    protected boolean aggiornaTitoloPadre(Connection conn, int id, String nuovoTitolo) throws SQLException {
        String query = "UPDATE articoli SET titolo = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nuovoTitolo);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Metodo astratto che ogni DAO figlio implementerà per la sua parte specifica
    public abstract boolean modificaRisorsa(int id, String nuovoTitolo, int datoSpecifico1, String datoSpecifico2);
    
    

    // 2. METODO POLIMORFICO: Cambia lo stato di disponibilità (utile per i Prestiti)
    public boolean aggiornaDisponibilita(int id, boolean disponibile) {
        String query = "UPDATE articoli SET disponibile = ? WHERE id = ?";
        try (Connection conn = ConnessioneDB.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setBoolean(1, disponibile);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore aggiornamento disponibilità: " + e.getMessage());
            return false;
        }
    }

    // 3. METODO COMUNE: Elimina una risorsa qualsiasi dal catalogo
    public boolean eliminaRisorsa(int id) {
        String query = "DELETE FROM articoli WHERE id = ?";
        try (Connection conn = ConnessioneDB.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore eliminazione: " + e.getMessage());
            return false;
        }
    }
    
    
    public Articolo trovaRisorsaPerId(int id) {
        String query = "SELECT r.id, r.titolo, r.tipo_articolo, r.disponibile, l.autore, d.durata_minuti, d.regista " +
                       "FROM articoli r " +
                       "LEFT JOIN libri l ON r.id = l.id " +
                       "LEFT JOIN dvd d ON r.id = d.id " +
                       "WHERE r.id = ?";
        
        try (Connection conn = ConnessioneDB.getConnessione();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String titolo = rs.getString("titolo");
                    String tipo = rs.getString("tipo_articolo");
                    boolean disponibile = rs.getBoolean("disponibile");
                    
                    if ("LIBRO".equals(tipo)) {
                        String autore = rs.getString("autore");
                        // Ipotizzando il costruttore della tua classe Libro(id, titolo, disponibile, autore)
                        return new Libro(id, titolo, disponibile, autore);
                    } else if ("DVD".equals(tipo)) {
                        int durata = rs.getInt("durata_minuti");
                        String regista = rs.getString("regista");
                        // Ipotizzando il costruttore della tua classe DVD(id, titolo, disponibile, durata)
                        return new DVD(id, titolo, disponibile, regista, durata);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore nel recupero della risorsa: " + e.getMessage());
        }
        return null; // Ritorna null se non trova nulla nel DB, esattamente come prima
    }
}