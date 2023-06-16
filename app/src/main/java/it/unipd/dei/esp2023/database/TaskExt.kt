package it.unipd.dei.esp2023.database

/*
* Classe usata per il risultato della query getTaskExtListFromSessionId di PomodoroDatabaseDao
* Potenzialmente potrebbe essere una classe figlia di Task (a parte per il problema del nome
* del campo order non utilizzabile come nome di colonna della query), ma l'ereditarietà per le
* data class non funziona.
*
* Contesto: un attributo per avere il numero di pomodori completati non è stato aggiunto a Task
* per non avere un attributo ridondante che va tenuto aggiornato ad ogni inserimento e rimozione
* in completed_pomodoro. In SessionDetailsRecyclerViewAdapter, però, devo sapere in qualche modo
* se il singolo task è completato o no e la soluzione più facile è fare questa classe con un
* attributo in più, da ritornare al posto di Task messa in una lista in modo analogo a come
* succede con getTaskListFromSessionId.
*
* Avviso ai naviganti: se questa soluzione (comprensibilmente) non piace, soluzioni già tentate
* non funzionanti sono quella di provare a fare una query in qualcosa come Transformation.switchMap
* o simili (ma non ho trovato un modo per fare qualche query lì dentro e costruire una lista di
* Pair<Task, Boolean> o qualcosa del genere partendo dalla List<Task>) e quella di fare la query
* dei pomodori completati per il singolo task in SDetailsViewHolder (RecyclerViewAdapter) per
* problemi di mancanza di lifecycle a cui associare l'osservazione del livedata risultato della
* query. GLHF
*/
data class TaskExt(
    var id: Long = 0L,
    var session: Long = 0L,
    var name: String = "",
    // Ho dovuto cambiare il nome da 'order' a 'taskOrder' perchè, quando faccio la query, non posso usare 'order'
    // come nome di tabella. Room faceva questo cambiamento da solo quindi per Task non me ne dovevo preoccupare io
    var taskOrder: Int = 0,
    var totalPomodoros: Int = 1,
    var completedPomodoros: Int = 1
)
