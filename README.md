# Symulacja Inteligentnych Świateł Drogowych

## Opis Projektu

Projekt przedstawia symulację inteligentnych świateł drogowych na skrzyżowaniu czterech dróg (**NORTH, SOUTH, EAST, WEST**).  
Celem projektu jest dynamiczne dostosowanie sygnalizacji do aktualnego obciążenia ruchem – zarówno pojazdów, jak i pieszych – przy jednoczesnym zapobieganiu zagłodzeniu dróg o niższym priorytecie.

## Główne Funkcjonalności

- **Czterokierunkowe skrzyżowanie**  
    - Drogi: `NORTH`, `SOUTH`, `EAST`, `WEST`  
    - Każda droga posiada konfigurowalną liczbę pasów ruchu (wpływa to na przepustowość pojazdów i maksymalną liczbę pojazdów, które mogą być buforowane).

- **Sterowanie Sygnalizacją z Fazami GREEN i YELLOW**  
    - **Faza zielona (GREEN):**  
        - W danym ticku aktywne jest zielone światło dla jednej osi, czyli jednocześnie dla dróg należących do osi NS (NORTH i SOUTH) lub EW (EAST i WEST).  
        - Czas trwania zielonego sygnału dla danej osi obliczany jest jako:  
          `greenDuration * effectivePriority`  
          (gdzie `greenDuration` to bazowy czas zielonego światła, a `effectivePriority` to maksymalny priorytet spośród dróg danej osi).  
        - Podczas fazy GREEN:
            - Jeżeli w kolejce na aktywnej drodze znajdują się piesi, przechodzą oni jednocześnie – pojazdy nie są przepuszczane (nawet jeśli były już w buforze).  
            - Jeżeli nie ma pieszych, pojazdy są przepuszczane do bufora (yellowBuffer) – jednak dla każdej drogi bufor może zawierać maksymalnie tyle pojazdów, ile wynosi liczba pasów tej drogi.
            - Dodatkowo, jeśli pierwszą kolejkę stanowi pojazd skręcający w lewo, to zostaje on pominięty, o ile w kolejce znajdują się pojazdy jadące prosto lub skręcające w prawo – dzięki temu lewy skręt ustępuje pierwszeństwa.
    - **Faza żółta (YELLOW):**  
        - Faza żółta pełni rolę bufora – w niej ostatecznie przepuszczane są wszystkie pojazdy zgromadzone w yellowBuffer dla każdej drogi aktywnej osi.  
        - Czas trwania fazy żółtej (`yellowDuration`) jest stały i niezależny od priorytetu drogi.
    - Po zakończeniu fazy YELLOW zielone światło przełącza się na przeciwstawną oś – jeśli wcześniej była NS, teraz będzie EW, i odwrotnie.

- **Obsługa Pieszych**  
    - Piesi mogą przechodzić wyłącznie wtedy, gdy droga, po której idą, ma zielone światło.  
    - W fazie GREEN, jeśli w kolejce znajdują się piesi, przechodzą oni jednocześnie, a pojazdy nie opuszczają skrzyżowania (lub już trafiają do bufora).
    - Pojazdy skręcające (zwłaszcza skręt w lewo) muszą ustąpić pierwszeństwa pojazdom jadącym prosto.

- **Priorytetyzacja Dróg**  
    - Każda droga posiada przypisany priorytet, który wpływa na czas trwania zielonego światła (dla osi, efektywny priorytet to maksymalny priorytet dróg danej osi).  
    - System cyklicznie przełącza zielone światło między osiami NS i EW, dzięki czemu drogi o niższym priorytecie nie są zagłodzone.

- **Obsługa Komend JSON**  
    - `addVehicle` – dodaje pojazd o określonym `vehicleId`, `startRoad` oraz `endRoad`.  
    - `addPedestrian` – dodaje pieszego o określonym `pedestrianId`, `startRoad` oraz `endRoad`.  
    - `step` – wykonuje pojedynczy krok symulacji, podczas którego przetwarzane są encje zgodnie z aktualną fazą sygnalizacji.

- **Wyniki Symulacji w JSON**  
    - Po każdym kroku (`step`) rejestrowane są identyfikatory pojazdów (`leftVehicles`) oraz pieszych (`leftPedestrians`), którzy opuszczają skrzyżowanie.  
    - Końcowy wynik zawiera listę statusów każdego kroku (`stepStatuses`).

---

## Algorytm Sterowania Ruchem

Algorytm sterowania sygnalizacją został zaimplementowany w klasie **`Intersection`**. Kluczowe założenia:

1. **Faza Zielona (GREEN)**  
    - Aktywna jest cała oś – albo NS (NORTH i SOUTH), albo EW (EAST i WEST).  
    - Zielony czas dla osi = `greenDuration * effectivePriority` (efektywny priorytet to maksymalny priorytet spośród dróg danej osi).  
    - Podczas fazy GREEN:
        - Jeśli w kolejce na dowolnej drodze aktywnej osi znajdują się piesi, przetwarzani są oni jednocześnie, a pojazdy pozostają (lub już znajdują się w yellowBuffer).  
        - Jeżeli brak pieszych, pojazdy są przenoszone z kolejki do yellowBuffer – dla każdej drogi yellowBuffer może zawierać maksymalnie tyle pojazdów, ile wynosi liczba pasów tej drogi.
        - Podczas transferu do bufora, jeśli napotkamy pojazd skręcający w lewo, system najpierw sprawdza, czy w kolejce znajdują się pojazdy jadące prosto lub skręcające w prawo. Jeśli tak, te pojazdy są przenoszone do bufora, a pojazd skręcający w lewo zostaje pominięty do momentu, gdy nie będzie już innych opcji.

2. **Faza Żółta (YELLOW)**  
    - W fazie YELLOW wszystkie pojazdy zgromadzone w yellowBuffer (dla każdej drogi aktywnej osi) są ostatecznie przepuszczane – opuszczają skrzyżowanie.  
    - Czas trwania fazy żółtej jest stały i wynosi `yellowDuration`.
    
3. **Priorytetyzacja i Cykl**  
    - Po wyczerpaniu czasu aktywnej fazy (GREEN lub YELLOW) następuje przełączenie:  
      - Jeśli kończy się faza GREEN, przechodzi się do fazy YELLOW.  
      - Po zakończeniu fazy YELLOW, aktywna oś zmienia się z NS na EW lub odwrotnie, a zielone światło dla nowej osi trwa `greenDuration * effectivePriority`.

4. **Obsługa Pieszych**  
    - Piesi przechodzą jednocześnie tylko wtedy, gdy droga, po której idą, ma zielone światło.  
    - Jeśli w fazie GREEN występują piesi, przetwarzani są oni wyłącznie, a pojazdy nie są przenoszone do bufora.

5. **Kroki Symulacyjne**  
    - Każda komenda `step` wywołuje metodę `step()` w `Intersection`, która:  
        - Przetwarza kolejki dla dróg należących do aktywnej osi zgodnie z aktualną fazą (GREEN lub YELLOW).  
        - Aktualizuje pozostały czas trwania bieżącej fazy.  
        - Po zakończeniu bieżącej fazy przełącza sygnalizację (zmiana fazy lub aktywnej osi).

---

## Instrukcja Uruchomienia (Maven)

### 1. Klonowanie Repozytorium

Pobierz projekt na swój komputer.

### 2. Kompilacja i Budowanie Projektu

W katalogu głównym projektu uruchom:

```bash
mvn clean package
```
Po kompilacji w katalogu target/ pojawi się plik traffic-lights-project-final-jar-with-dependencies.jar.
### 3. Uruchamianie symulacji

Przygotuj plik input.json z komendami, np.:
```json
{
  "commands": [
    {
      "type": "addVehicle",
      "vehicleId": "car1",
      "startRoad": "north",
      "endRoad": "south"
    },
    {
      "type": "addPedestrian",
      "pedestrianId": "p1",
      "startRoad": "north",
      "endRoad": "south"
    },
    {
      "type": "step"
    },
    {
      "type": "step"
    }
  ]
}
```
Uruchom symulację:
```bash
java -jar target/traffic-simulator.jar input.json output.json
```
    input.json – plik z listą komend
    output.json – plik, do którego zostanie zapisany wynik symulacji

### 4. Sprawdzenie Wyników

Przykładowa zawartość output.json:
```json
{
  "stepStatuses": [
    {
      "leftVehicles": ["car1"],
      "leftPedestrians": ["p1"]
    },
    {
      "leftVehicles": [],
      "leftPedestrians": []
    }
  ]
}
```

## Testy jednostkowe

Projekt zawiera testy w katalogu src/test/java. Aby uruchomić testy:
```bash
mvn test
```

## Przykładowe danej
Dodatkowo, w katalogu `src/main/resources` znajduje się 5 przykładowych wejść.

## Autor
Maciej Kmąk

## Uwaga
Przy aktualnej konfiguracji parametrów `greenDuration` oraz `yellowDuration` w klasie `TrafficLightSimulation` oczekiwane wyniki testów przedstawione w mailu mogą różnić się od rzeczywistych, gdyż czas symulacji jest rozbity na ticki w zależności od priorytetów dróg, co wpływa na ostateczny wynik symulacji.