# Digital-Vault
Digitl Vault è un applicazione backend progettata per archiviare informazioni sensibili (password, pin, documenti)
ARCHITETTURA
1. Containerizzazione: definizione dell'infrastruttura. 
Il Database (MongoDB) e il gestore delle identità (KeyCloak) sono configurati allo stesso identico modo per ogni sviluppatore. KeyCloak agirà come Trusted Theority, l'unico ente autorizzato a dichiarare chi è l'utente, il db invece fungerà da deposito persistente per i documenti di Digital Vault. Per utilizzare questo si userà Docker Compose, che permette di coordinare più cointainer tramite un unico file di configurazione YAML.

2. Configurazione dell'identity Realm. 
Si stabilisce un rapporto Trust tra il Backend e Keycloak. Keycloak funge da intermediario tra l'utente e l'applicazione backend, quando l'utente vuole accedere alla cassaforte, keycloak ne verifica l'identità e rilascia un JWT (oggetto JSON che contiene generalità e ruoli dell'utente). Queste quindi non rimangono del db di mongo, ma vengono custodite all'interno di keycloak.
La configurazione del Relam definisce le regole della cassaforte. Si stabilisce che solo i token emessi dall'autorità saranno accettati dal codcie Java. Il client rappresenta l'identità del backend all'interno di keycloak, possiede una private key che userà per validare le richieste. L'identità dell'utente è quindi certa, immutabile e verificabile tramite la firma digitale del token.

3. Configurazione del backend come Resource Server. Il backend non si occupa della logica del codice Java ma solo di valirdare e ascoltare. Spring Boot non comunicherà direttamente con keycloak per verificare le richieste, userà delle librerie specifiche per decodificare il token JWT, usando la chiave crittografica che scaricherà da keycloak, rendendo il sitema scalabile. Il backend negherà l'accesso a qualsiasi URL a meno che nella richiesta a meno che questa non contenga un JWT. Per controllare il JWT, all'avvio del backend Spring Boot contatta keycloak, scarica le regole di firma, ogni volta che arriva una chiamata scansiona il token e se questo è scaduto o contraffatto, non arriva al database. Una volta che il backend ha stabilito la valadità del JWT, lo trasforma in un oggetto Java "SecurityContext". Il database così non vedrà mai sensitive data, ma solo id anonim (sub).

4. Configurazione della sicurezza con Spring Security. La sicurezza del backend si basa su 3 pilastri:
    1. Security filter chain, la sicurezza è delegata a una catena di filtri che agiscono come un Proxy. Ogni richiesta HTTP in entrata viene intercettata prima di raggiungere il controller, se un filtro rileva un'anomalia interrompe immediatamente l'esecuzione.
    2. Stateleness, include il JWT in ogni intestazione delle richieste in quanto il server non mantiene alcuna traccia della sessione tra una richiesta e l'altra
    3. Il backend viene istruito a creare una gerarchia di permessi, con esposizione pubblica, autenticata e autorizzata, con ognuna di queste esposizioni che richiede cose diverse. Pubblica accesso anonimo, autenticato JWT valido e autorizzato JWT + ruoli
    4. Disaccopiamento tra autenticazione e autorizzazione.
    