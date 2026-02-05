# Digital-Vault
Digitl Vault è un applicazione backend progettata per archiviare informazioni sensibili (password, pin, documenti)
ARCHITETTURA
1. Containerizzazione: definizione dell'infrastruttura. 
Il Database (MongoDB) e il gestore delle identità (KeyCloak) sono configurati allo stesso identico modo per ogni sviluppatore. KeyCloak agirà come Trusted Theority, l'unico ente autorizzato a dichiarare chi è l'utente, il db invece fungerà da deposito persistente per i documenti di Digital Vault. Per utilizzare questo si userà Docker Compose, che permette di coordinare più cointainer tramite un unico file di configurazione YAML.

2. Configurazione dell'identity Realm. 
Si stabilisce un rapporto Trust tra il Backend e Keycloak. Keycloak funge da intermediario tra l'utente e l'applicazione backend, quando l'utente vuole accedere alla cassaforte, keycloak ne verifica l'identità e rilascia un JWT (oggetto JSON che contiene generalità e ruoli dell'utente). Queste quindi non rimangono del db di mongo, ma vengono custodite all'interno di keycloak.
