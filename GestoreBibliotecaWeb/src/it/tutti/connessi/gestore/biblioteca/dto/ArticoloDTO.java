package it.tutti.connessi.gestore.biblioteca.dto;

public class ArticoloDTO {
	
	private int id;
    private String tipo;
    private String titolo;
    private String autore; 
    private String regista;
    private int durata;
    private boolean disponibile;

    // Getter e Setter
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }
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
	public boolean isDisponibile() {
		return disponibile;
	}
	public void setDisponibile(boolean disponibile) {
		this.disponibile = disponibile;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
    
}