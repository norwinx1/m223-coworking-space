# Coworking Space Projekt

Ein Buchungssystem in einem Coworking Space für den ÜK M223.

## Testdaten
Die Testdaten werden beim Start im Entwicklungsmodus geladen und sind in der Klasse *TestDataService* definiert.
Vorher werden auch alle Daten gelöscht.
Es wird ein Admin, zwei Mitglieder und ein paar Buchungen erstellt, welche diesen beiden Mitgliedern gehören.

## Änderungen gegenüber der Planung
* Der Endpunkt /bookings/cancel/{id} kann den Status FORBIDDEN werfen, wenn dem User die ausgewählte Buchung nicht gehört.
* Im Enum *Role* gibt es den Wert VISITOR nicht mehr.

## Hinweise
* Das Salt für den Passwort-Hash ist aus Einfachheitsgründen für jeden User gleich und hardcoded.