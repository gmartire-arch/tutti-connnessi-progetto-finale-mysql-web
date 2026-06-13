package it.tutti.connessi.gestore.biblioteca.controller;

import it.tutti.connessi.gestore.biblioteca.dto.ArticoloDTO;
import it.tutti.connessi.gestore.biblioteca.model.Articolo;
import it.tutti.connessi.gestore.biblioteca.service.impl.GestioneArticoloServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articoli")
@CrossOrigin(origins = "*") // Permette al frontend di fare richieste anche se gira su una porta diversa
public class ArticoloRestController {

    private final GestioneArticoloServiceImpl bibliotecaService = new GestioneArticoloServiceImpl();

    // 1. READ ALL - Recupera tutto il catalogo per la tabella jQuery
    @GetMapping
    public List<ArticoloDTO> getAllArticoli() {
        return bibliotecaService.mostraArticoli();
    }

    // 2. CREATE - Inserisce un nuovo Libro o DVD
    @PostMapping
    public void aggiungiArticolo(@RequestBody ArticoloDTO dto) {
        bibliotecaService.aggiungiArticolo(dto.getTitolo(), dto.getAutore(), dto.getTipo(), dto.getRegista(), dto.getDurata());
    }

    // 3. UPDATE - Modifica i dati di una risorsa esistente
    @PutMapping("/{id}")
    public void modificaArticolo(@PathVariable int id, @RequestBody Map<String, String> body) {
        String titolo = body.get("titolo");
        String autore = body.get("autore");
        String regista = body.get("regista");
        String durata = body.get("durata");
        bibliotecaService.modificaArticolo(id, titolo, autore, regista, Integer.valueOf(durata));
    }

    // 4. DELETE - Elimina definitivamente una risorsa
    @DeleteMapping("/{id}")
    public void eliminaArticolo(@PathVariable int id) {
        bibliotecaService.eliminaArticolo(id);
    }

    // 5. PATCH - Gestione Prestiti e Restituzioni (Modifica parziale della disponibilità)
    @PatchMapping("/{id}/disponibilita")
    public void cambiaDisponibilita(@PathVariable int id, @RequestBody Map<String, Boolean> body) {
        boolean disponibile = body.get("disponibile");
        if (disponibile) {
            bibliotecaService.restituisciRisorsa(id);
        } else {
            bibliotecaService.prestaRisorsa(id);
        }
    }
}