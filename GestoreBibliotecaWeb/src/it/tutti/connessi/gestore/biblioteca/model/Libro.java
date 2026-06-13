package it.tutti.connessi.gestore.biblioteca.model;

public class Libro extends Articolo {

	public Libro(int id, String titolo, boolean disponibile, String autore) {
		super(id, titolo);
		this.autore = autore;
		this.disponibile = disponibile;
	}

	private String autore;
	
	
	
	public String getAutore() {
		return autore;
	}

	public void setAutore(String autore) {
		this.autore = autore;
	}

	@Override
	public String ottieniTipo() {
		return "Libro";
	}
	
	// Sovrascriviamo il toString per personalizzare la stampa del libro
    @Override
    public String toString() {
        String stato = disponibile ? "Disponibile" : "In Prestito";
        return "[" + id + "] (Libro) " + titolo + " - " + autore + " [" + stato + "]";
    }

	

}
