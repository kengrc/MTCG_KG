# MTCG_KG
## Summary
Zu Beginn hatte ich große Schwierigkeiten mit dem Verbinden von der Docker DB mit DataGrip.
Aufgrund eines mir unbekannten Bugs, muss ich jedes Mal Postgres neu herunterladen, wenn ich mich mit
der DB verbinden will.

Viel Aufwand brauchte ich zu Beginn um die Grundprinzipien der Layered Architecture zu verstehen.
Deshalb brauchte ich etwas mehr Zeit beim User-Service.

Session-Service ging ganz flott, viel konnte vom User-Service übernommen werden.

Um den Package-Service zu erstellen, habe ich die erweitert, die bis dahin nur aus der `player` 
Tabelle mit `username` und `password` existierte. Da für Booster-Packs auch Karten nötig sind,
erstellte ich auch diese Tabelle und die geplanten restlichen Tabellen (Battle, Trading etc.).

Das Erstellen von Booster-Packs erforderte das Token-System, Erstellen von Packs und Erstellen
von Karten die in den Packs sind. Da meine bisherige Methode um die ID's zu speichern nicht
funktionierte, brauchte ich eine neue und habe nach langer Zeit eine andere (simplere) gefunden.
Das Hinzufügen der Karten in die Packs erweiste sich etwas schwieriger als gedacht.

Das Kaufen von Karten konnte etwas vereinfacht werden durch SQL-Commands in der DB, z.B. wird 
durch `ON DELETE SET NULL` der FK in der Tabelle Cards auf NULL gesetzt wenn ein Booster Pack
gekauft wird.

Beim Konfigurieren des Decks war es teilweise nervig den Body der Payload zu bekommen, doch das
das wirkliche Hinzufügen zum Deck erleichterte sich durch eine gute DB-Architektur.

Das Aktualisieren und anzeigen der User-Profils stellte keine besonderen Herausforderungen dar.

## Tracked Time

| Date        | Time [h] | Comment	                                             |
|-------------|----------|------------------------------------------------------|
| 23.12-24.12 | 16       | Setting up Database and connecting it to the Project |
| 24.12       | 1	       | Creating basic 'player' table in DB                  |
| 24.12-25.12 | 12       | User-Service                                         |
| 25.12       | 3	       | Session-Service, little adjusting of User-Service    |
| 26.12       | 2,5      | Planning and creating final DB-structure and tables  |
| 26.12       | 4	       | Token-based authentication                           |
| 26.12       | 3	       | Creating Booster-Packs (without cards)               |
| 26.12-27.12 | 8	       | Cards are created and added to the Booster Packs	    |
| 28.12       | 4,5      | Transaction-Service	                                 |
| 29.12       | 1	       | Display bought cards	                                |
| 29.12	      | 3,5	     | Deck-Service	                                        |
| 30.12	      | 2	       | Display and update player profile	                   |
| 	           | 	        | 	                                                    |
| 	           | 	        | 	                                                    |
| 	           | 	        | 	                                                    |
