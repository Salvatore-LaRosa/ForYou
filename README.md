# ForYou - App Promemoria Medicine

ForYou è un'applicazione Android nativa progettata per aiutare gli utenti a gestire e ricordare le proprie assunzioni di farmaci. L'app offre un'interfaccia semplice e intuitiva per aggiungere farmaci, impostare promemoria e tenere traccia delle assunzioni giornaliere.

## ✨ Funzionalità Principali

- **Autenticazione Utente**: Registrazione e login sicuri con email e password.
- **Accesso con PIN e Biometria**: Schermata di blocco con PIN a 4 cifre e possibilità di sblocco tramite impronta digitale per un accesso rapido e sicuro.
- **Gestione Farmaci**: Aggiunta di nuovi farmaci specificando nome, quantità, frequenza, data di fine e orario di assunzione.
- **Programma Giornaliero**: Una schermata "Home" che mostra una vista giornaliera dei farmaci da assumere, con la possibilità di navigare tra le date.
- **Tracciamento Assunzioni**: Funzionalità per marcare un farmaco come "assunto" per una data specifica.
- **Promemoria con Notifiche**: Schedulazione di notifiche locali per ricordare all'utente di assumere i farmaci all'orario impostato.
- **Gestione Sessione**: Logout sicuro con pulizia della sessione utente.

## 🛠️ Architettura e Tecnologie

L'applicazione segue i moderni principi di sviluppo Android, adottando un'architettura pulita basata sul pattern **MVVM (Model-View-ViewModel)**.

- **UI (View)**: L'interfaccia utente è costruita interamente in modo dichiarativo utilizzando **Jetpack Compose**, il toolkit moderno di Android per la creazione di UI native. Il tema si basa su **Material 3**.

- **ViewModel**: I `ViewModel` (`AuthViewModel`, `MedicineViewModel`) gestiscono la logica dell'interfaccia utente e preparano i dati per la visualizzazione, agendo da intermediari tra i Repository e la UI.

- **Repository**: I `Repository` (`UserRepository`, `MedicineRepository`) fungono da unica fonte di verità per i dati, astraendo le fonti dati (database locale) dal resto dell'app.

- **Model (Data Layer)**: La persistenza dei dati è gestita tramite la libreria **Room**, che fornisce un'astrazione sopra SQLite. Le entità (`User`, `Medicine`, `MedicineIntake`) definiscono la struttura delle tabelle del database.

- **Asincronicità**: Le operazioni di I/O (database e rete) vengono gestite in modo asincrono utilizzando **Kotlin Coroutines** e **Flow** per garantire che l'interfaccia utente rimanga reattiva.

- **Autenticazione Biometrica**: Utilizza la libreria `androidx.biometric` per implementare l'accesso tramite impronta digitale.

### Tecnologie Utilizzate

- **Linguaggio**: Kotlin
- **UI**: Jetpack Compose, Material 3
- **Architettura**: MVVM
- **Asincronicità**: Kotlin Coroutines & Flow
- **Database**: Room Persistence Library
- **Navigazione**: Jetpack Navigation for Compose
- **Autenticazione**: Android Biometric Library

## 🚀 Come Iniziare

1.  **Clona il repository**:
    ```bash
    git clone <URL_DEL_REPOSITORY>
    ```
2.  **Apri in Android Studio**:
    - Apri Android Studio (versione Arctic Fox o successiva).
    - Seleziona "Open an Existing Project" e scegli la directory del progetto clonata.
3.  **Esegui il progetto**:
    - Sincronizza il progetto con i file Gradle.
    - Seleziona un emulatore o un dispositivo fisico e clicca sul pulsante "Run".
