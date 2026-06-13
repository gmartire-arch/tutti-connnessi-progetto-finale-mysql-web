$(document).ready(function() {


    // ==========================================
    // 1. NAVIGAZIONE SIDEBAR (Gestione Viste)
    // ==========================================
    $('.menu-item').not('[disabled]').click(function(e) {
        e.preventDefault();
        $('.menu-item').removeClass('active');
        $(this).addClass('active');

        const targetSection = $(this).data('target');
        $('.content-section').addClass('hidden');
        $('#' + targetSection).removeClass('hidden');

        if(targetSection === 'view-catalogo') {
            caricaCatalogo();
        }
    });

    // ==========================================
    // 2. READ: Carica Catalogo Completo
    // ==========================================
    function caricaCatalogo() {
        $.ajax({
            url: '/api/articoli',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                renderizzaTabella(data);
            },
            error: function(err) {
                mostraAlert("Errore nel caricamento del catalogo remoto.", "error");
            }
        });
    }

    function renderizzaTabella(articoli) {
        const tbody = $('#tabella-catalogo tbody');
        tbody.empty();

        if(articoli.length === 0) {
            tbody.append('<tr><td colspan="6" style="text-align:center;">Nessun articolo in biblioteca.</td></tr>');
            return;
        }

        articoli.forEach(function(art) {
            const badgeStato = art.disponibile 
                ? '<span class="badge badge-disponibile">Disponibile</span>' 
                : '<span class="badge badge-prestito">In Prestito</span>';
				
				// Determiniamo il tipo normalizzando la stringa in maiuscolo (gestisce sia 'LIBRO'/'DVD' che i dati mock)
				    // Nota: usiamo art.ottieniTipo (se arriva dal backend Java) oppure art.tipo_risorsa / art.tipo
				    const tipoInUso = (art.ottieniTipo || art.tipo || "").toUpperCase();

				    let dettaglioSpecifico = 'N/D';

				    // CONTROLLO POLIMORFICO DEL TIPO
				    if (tipoInUso === 'LIBRO') {
				        // Se è un libro, mostriamo l'autore
				        dettaglioSpecifico = art.autore ? `Autore: ${art.autore}` : 'Autore sconosciuto';
				    } else if (tipoInUso === 'DVD') {
				        // Se è un DVD, mostriamo il regista (che nel tuo DB è salvato sotto la colonna autore o dato_specifico) 
				        // e la durata in minuti
				        const regista = art.autore || art.dato_specifico || 'Regista sconosciuto';
				        const durataMinuti = art.durata || 0;
				        
				        dettaglioSpecifico = `Regista: ${regista} (${durataMinuti} min)`;
				    }
				
				

            const riga = `
                <tr data-id="${art.id}">
                    <td>${art.id}</td>
                    <td><strong>${art.tipo}</strong></td>
                    <td>${art.titolo}</td>
                    <td>${dettaglioSpecifico}</td>
                    <td>${badgeStato}</td>
                    <td>
                        <button class="btn btn-secondary btn-sm btn-edit" data-id="${art.id}"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn btn-danger btn-sm btn-delete" data-id="${art.id}"><i class="fa-solid fa-trash"></i></button>
                    </td>
                </tr>
            `;
            tbody.append(riga);
        });
    }

    // Botone di aggiornamento manuale
    $('#btn-refresh').click(caricaCatalogo);

    // ==========================================
    // 3. CREATE: Aggiungi Articolo (Libro/DVD)
    // ==========================================
    // Cambia etichetta dinamica a seconda della selezione nel form inserimento
    $('#tipo-risorsa').change(function() {
        if($(this).val() === 'DVD') {
           // $('#group-autore label').text('Regista / Durata DVD');
			$('#form-group-libro').hide();
			$('#form-group-dvd').show();
        } else {
			$('#form-group-libro').show();
			$('#form-group-dvd').hide();
        }
    });

    $('#form-aggiungi').submit(function(e) {
        e.preventDefault();
        
        const nuovoArticolo = {
            tipo: $('#tipo-risorsa').val(),
            titolo: $('#titolo').val(),
            autore: $('#autore').val(),
			regista : $('#regista').val(),
			durata: $('#durata').val()
        };

        $.ajax({
            url: '/api/articoli',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(nuovoArticolo),
            success: function() {
                mostraAlert("Risorsa inserita con successo nel database!", "success");
                $('#form-aggiungi')[0].reset();
            },
            error: function() {
                mostraAlert("Errore durante il salvataggio.", "error");
            }
        });
    });

	// ==========================================
	// 4. UPDATE: Modifica Risorsa (Finestra Modale)
	// ==========================================
	// Apertura Modale e Popolamento Dati
	$(document).on('click', '.btn-edit', function() {
	    const idSelezionato = $(this).data('id');
	    
	    // Recuperiamo la riga della tabella (<tr>) cliccata per leggere i dati direttamente dall'HTML
	    const riga = $(this).closest('tr');
	    
	    // Estraiamo i testi dalle celle della riga
	    const tipoRisorsa = riga.find('td:eq(1)').text().trim().toUpperCase(); // Prende il valore della colonna "Tipo"
	    const titoloAttuale = riga.find('td:eq(2)').text().trim();             // Prende il valore della colonna "Titolo"
	    const datoSpecifico = riga.find('td:eq(3)').text().trim();             // Prende il valore di "Autore/Regista"

	    // Popoliamo i campi nascosti e visibili della modale
	    $('#modifica-id').val(idSelezionato);
	    $('#modifica-tipo').val(tipoRisorsa);
	    $('#modifica-titolo').val(titoloAttuale);
	    $('#modifica-specifico').val(datoSpecifico);
	    
	    // Adattiamo la grafica della modale in base al tipo di risorsa
	    if(tipoRisorsa === 'DVD') {
	        $('.id-dvd-only').removeClass('hidden');
	        $('#label-modifica-specifico').text('Regista');
	        
            // Se siamo su DB reale, lasciamo il campo vuoto o pronto da inserire se non salvato nella tabella principale
            $('#modifica-casa-disc').val(''); 
	    } else {
	        $('.id-dvd-only').addClass('hidden');
	        $('#label-modifica-specifico').text('Autore');
	    }
	    
	    // Mostriamo finalmente la finestra modale a schermo!
	    $('#modal-modifica').removeClass('hidden');
	});

    // Chiusura Modale
    $('.close-modal').click(function() {
        $('#modal-modifica').addClass('hidden');
    });

    // Invio form di modifica
    $('#form-modifica').submit(function(e) {
        e.preventDefault();
        const id = parseInt($('#modifica-id').val());

        // Chiamata AJAX reale (PUT)
        const datiModificati = {
            titolo: $('#modifica-titolo').val(),
            specifico: $('#modifica-specifico').val(),
            casaDisc: $('#modifica-casa-disc').val()
        };

        $.ajax({
            url: `/api/articoli/${id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(datiModificati),
            success: function() {
                $('#modal-modifica').addClass('hidden');
                mostraAlert("Risorsa modificata nel database!", "success");
                caricaCatalogo();
            }
        });
    });

    // ==========================================
    // 5. DELETE: Elimina Risorsa
    // ==========================================
    $(document).on('click', '.btn-delete', function() {
        const id = $(this).data('id');
        if(confirm(`Sei sicuro di voler eliminare definitivamente la risorsa ID ${id}?`)) {
            
            $.ajax({
                url: `/api/articoli/${id}`,
                method: 'DELETE',
                success: function() {
                    mostraAlert("Articolo rimosso dal DB.", "success");
                    caricaCatalogo();
                }
            });
        }
    });

    // ==========================================
    // 6. PRESTITI E RESI (Update Disponibilità)
    // ==========================================
    function impostaDisponibilita(id, statoDisponibile) {
        if(!id) {
            mostraAlert("Inserisci un ID valido", "error");
            return;
        }

        $.ajax({
            url: `/api/articoli/${id}/disponibilita`,
            method: 'PATCH',
            contentType: 'application/json',
            data: JSON.stringify({ disponibile: statoDisponibile }),
            success: function() {
                mostraAlert(`Stato risorsa ${id} aggiornato con successo!`, "success");
                $('input[type="number"]').val('');
            },
            error: function() {
                mostraAlert(`Impossibile aggiornare l'ID ${id}. Controlla che esista.`, "error");
            }
        });
    }

    $('#btn-prestito').click(function() {
        const id = parseInt($('#id-prestito').val());
        impostaDisponibilita(id, false); // In prestito = non disponibile
    });

    $('#btn-restituzione').click(function() {
        const id = parseInt($('#id-restituzione').val());
        impostaDisponibilita(id, true); // Restituito = disponibile
    });

    // ==========================================
    // UTILS: Funzione di Notifica Visiva
    // ==========================================
    function mostraAlert(testo, tipo) {
        const alertBox = $('#alert-box');
        alertBox.text(testo).removeClass('hidden alert-success alert-error');
        
        if(tipo === "success") alertBox.addClass('alert-success');
        else alertBox.addClass('alert-error');

        // Scompare automaticamente dopo 4 secondi
        setTimeout(function() {
            alertBox.addClass('hidden');
        }, 4000);
    }

    // Caricamento iniziale del catalogo all'avvio
    caricaCatalogo();
});