package it.tutti.connessi.gestore.biblioteca.model;

public abstract class Articolo {
    protected int id;
    protected String titolo;
    protected boolean disponibile;

    public Articolo(int id, String titolo) {
        this.id = id;
        this.titolo = titolo;
    }

    // Getter comuni
    public int getId() { return id; }
    public String getTitolo() { return titolo; }
    public boolean isDisponibile() { return disponibile; }
    
    
	public void setId(int id) {
		this.id = id;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public void setDisponibile(boolean disponibile) {
		this.disponibile = disponibile;
	}

    // Metodo astratto: ogni figlio dovrà dire che tipo di risorsa è ("Libro", "DVD", etc.)
    public abstract String ottieniTipo();
}


