# Coworking Space Projekt

Ein Buchungssystem in einem Coworking Space für den ÜK M223 geschrieben in Java mit dem Framework [Quarkus](https://quarkus.io/).

## Aufsetzen
Um am Projekt entwicklen zu können, sind folgende Tools notwendig:
* Docker
* Visual Studio Code mit folgenden Extensions:
* * Dev Containers https://code.visualstudio.com/docs/devcontainers/containers
* * Java Extension Pack https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack

Um das Projekt aufzusetzen, sind folgende Schritte notwendig:
* Git Projekt klonen: ```git clone https://github.com/norwinx1/m223-coworking-space.git```
* Projekt in Visual Studio Code öffnen und in Container mit dem Kommandobefehl 'Dev Containers: Reopen in Container' öffnen.
* Sicherstellen, dass die oben genannten und folgende Extensions im Container vorhanden sind:
* * Quarkus https://marketplace.visualstudio.com/items?itemName=redhat.vscode-quarkus
* Das Projekt eventuell mit 'Developer: Reload window' neu laden, damit das Java Projekt erkannt wird und die Abhängigkeiten heruntergeladen werden.

## Starten
Um das Projekt zu starten, muss der Kommandobefehl 'Quarkus: Debug current quarkus project' ausgeführt werden.
Das Programm ist nun unter ```localhost:8080``` erreichbar. Es öffnet sich auch ein Terminal bei dem man mit der Eingabe von 'r' die Tests laufen lassen kann.

## Testdaten
Die Testdaten werden beim Start im Entwicklungsmodus geladen und sind in der Klasse *TestDataService* definiert.
Vorher werden auch alle Daten gelöscht. 
Für die Tests gibt es eine eigene solche Klasse. Für jeden Test wird diese Klasse aufgerufen und die Testdaten neu generiert.

## Änderungen gegenüber der Planung
* Der Endpunkt /bookings/cancel/{id} kann den Status FORBIDDEN werfen, wenn dem angemeldeten User die ausgewählte Buchung nicht gehört.
* Im Enum *Role* gibt es den Wert VISITOR nicht mehr, da man als registrierter User automatisch mindestens MEMBER sein muss.

## Hinweise
* Das Pepper für den Passwort-Hash ist aus Einfachheitsgründen für jeden User gleich und hardcoded.

© Norwin Schäfer