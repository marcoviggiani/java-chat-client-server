# Chat Client-Server (Java + JavaFX)

Chat didattica con server a socket e client grafico JavaFX.

## Requisiti
- JDK 25 (o più recente) e Maven
- Maven

Le librerie (JavaFX, log4j, gson) si scaricano da sole: Maven sceglie il
JavaFX giusto per il tuo sistema (Mac Intel, Mac Apple Silicon, Windows, Linux).

## Avvio
Server (in un terminale):

    mvn compile exec:java -Dexec.mainClass=com.server.Server

Client (in un altro terminale, anche più di uno):

    mvn javafx:run

Nella schermata iniziale usa `localhost` come indirizzo se il server è sullo stesso computer.
