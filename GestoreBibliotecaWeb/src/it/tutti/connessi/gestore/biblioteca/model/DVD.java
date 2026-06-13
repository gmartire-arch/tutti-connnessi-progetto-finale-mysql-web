package it.tutti.connessi.gestore.biblioteca.model;

public class DVD extends Articolo {

	public DVD(int id, String titolo, boolean disponibile, String regista, int durata) {
		super(id, titolo);
		this.durata = durata;
		this.disponibile = disponibile;
		this.regista = regista;
	}

	private String regista;
	private int durata;

	public String getRegista() {
		return regista;
	}

	public void setRegista(String regista) {
		this.regista = regista;
	}

	public int getDurata() {
		return durata;
	}

	public void setDurata(int durata) {
		this.durata = durata;
	}

	@Override
	public String ottieniTipo() {
		return "DVD";
	}
	
	// Sovrascriviamo il toString per personalizzare la stampa del DVD
    @Override
    public String toString() {
        String stato = disponibile ? "Disponibile" : "In Prestito";
        return "[" + id + "] (DVD) " + titolo + " - " + " [" + stato + "]";
    }

	

}
