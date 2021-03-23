# TesiTriennale-Flavio.Fabbrizi

Il sistema è composto da una soluzione node-RED e da una soluzione Alexa.

La cartella CustomInstallNodered-master contiene un installer con il qualel è possibile installare node-red, i plugin necessari e il flow necessario.

La cartella node_red contiene il flow relativo all'intera soluzione e l'elenco dei plugin e componenti utilizzati, che riportiamo anche di seguito:
- node-red-contrib-alexa-cakebaked
- node-red-contrib-cast
- node-red-dashboard
- node-red-node-sqlite

Per utilizzare il flow è necessario installare node-red, sqlite3 e scaricare ngrok e quindi installare tutti i plugin necessari tramite la voce "manage palette" del menu di node-red.

La cartella Alexa contiene l'esportazione completa delle 4 skill utilizzate. Per utilizzare il sistema è necessario utilizzare l'account di "flaviofabbrizi78@gmail.com" per avere già a disposizione le skills richieste, oppure importare le 4 skill nel proprio account ed eseguire la build; in questo modo saranno disponibili nell'account corrente.
