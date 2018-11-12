# Tilbakemelding

### Sammendrag
Fint prosjekt med mye funksjonalitet og gode kommentarer.

### Dokumentasjon
Fin dokumentasjon som dekker alle krav for dokumentasjon. Instruksjonene er gode og lette og følge. Rekkefølgen på maven package og oppdatere Properties filen må endres.

- [x] Navn og Feide-ID på dere de som var på teamet
- [x] Inkluderer dokumentasjonen hvordan man tester ut funksjonaliteten programmet manuelt? (Inkludert eventuell ekstra funksjonalitet dere har tatt med)
- [x] Inkluderer dokumentasjonen en evaluering av hvordan man jobbet sammen?
- [x] Inkluderer dokumentasjonen en screencast av en parprogrammeringsesjon?
- [x] Inkluderer dokumentasjonen en evaluering *fra* en annen gruppe og en evaluering *til* en annen gruppe?
- [x] Inkluderer dokumentasjonen en UML diagram med datamodellen?
- [x] Inkluderer dokumentasjonen en egenevaluering med hvilken karakter gruppen mener de fortjener?


### Funksjonalitet
Programmet kjører og har all funksjonaliteten som oppgaven spør etter, og noe ekstra. (f.eks at man kan avslutte serveren fra cli).

- [x] add: Legg til et foredrag i databasen med title, description og topic (valgfritt)
- [x] list: List opp alle foredrag i basen med et valgfritt topic
 - [x] show: Vis detaljer for et foredrag
- [x] update: Endre title, description eller topic for et foredrag
- [ ] Valgfri tillegg: Kommandoer for å sette opp hvor mange dager og timer konferansen skal vare og hvor mange parallelle spor den skal inneholde.


### Kodekvalitet
Strukturen til prosjektet er oversiktelig og delt inn i flere modules. Blir feil når prosjektet bygges på travis. Fine tester, men det er noen klasser som ikke dekkes. 


- [x] Koden er klonet fra GitHub classrom
 - [x] Produserer `mvn package` en executable jar? (tips: Bruk `maven-shade-  plugin`)
- [x] Bruker koden Java 8 og UTF-8
- [] Bygger prosjektet på [https://travis-ci.com](https://travis-ci.com)?
- [ ] Har du god test-dekning? (tips: Sett opp jacoco-maven-plugin til å kreve at minst 65% av linjene har testdekning)
- [x] Er koden delt inn i flere Maven `<modules>`?
- [x] Bruker kommunikasjon mellom klient og server HTTP korrekt?
- [x] Kobler serveren seg opp mot PostgreSQL ved hjelp av konfigurasjon i fila `innlevering.properties` i *current working directory* med `dataSource.url`, `dataSource.username`, `dataSource.password`?
