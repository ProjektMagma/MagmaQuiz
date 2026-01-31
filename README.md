# Magma Quiz

### Aplikacja do tworzenia i rozwiązywania quizów na konkurs "Złota Apka".

_Poniżej znajduję się opis projektu, który został wykorzystany w formularzu zgłoszeniowym,
dopakowany markdownem :)_

## Na jakie potrzeby/problem odpowiada Wasze rozwiązanie?

Aplikacja ***Magma Quiz*** została stworzona, aby w prosty sposób umożliwiać tworzenie i rozwiązywanie quizów na dowolne
tematy w zamkniętej bazie użytkowników. Dzięki własnemu hostowaniu serwerów użytkownicy zyskują pełną kontrolę nad
treściami w swojej domenie, np. szkolny serwer z quizami nie łączy się z serwerem zawierającym quizy o grach.
***Magma Quiz*** oferuje zatem bardziej kontrolowaną alternatywę dla serwisów takich jak *Wayground* (dawniej
*Quizizz*), *Kahoot* czy *Quizlet*, idealną dla mniejszych społeczności, np. jednej szkoły, bez konieczności korzystania
z zewnętrznych usług firm prywatnych. To lokalni administratorzy decydują o dostępnych treściach.

## W jakich językach programowania, jakich technologiach powstała aplikacja?

Nasza aplikacja dzieli się na trzy różne programy, połączone w jeden projekt za pomocą **Kotlin Multiplatform**:
aplikację mobilną, desktopową i serwer.

**Desktop / Mobile (Android)**

* Język programowania: Kotlin
* Framework: Jetpack Compose  (with Hot Reload)
* Biblioteki: Material3, Navigation3, Koin, Ktor Client, Coil

**Serwer**

* Język programowania: Kotlin
* Biblioteki: Koin, Exposed DAO + DSL, Ktor Server, Bcrypt
* Baza danych: PostgreSQL

Inne narzędzia: Gradle + AGP, Android Studio, IntelliJ, Git, GitHub Projects

## W jaki sposób działa Wasza aplikacja? Co może dzięki niej osiągnąć użytkownik? Jaką ma funkcjonalność?

W aplikacji są zaimplementowane lub w trakcie tworzenia funkcjonalności takie jak:

- Rozwiązywanie quizów
- Tworzenie quizów
- Lista znajomych
- Ulubione quizy
- Historia gier
- Recenzje i komentarze do quizów

## Jak widzicie dalszy rozwój Waszego rozwiązania?

Nasze rozwiązanie może być wykorzystywane w placówkach edukacyjnych oraz w różnych społecznościach tematycznych,
umożliwiając tworzenie zdecentralizowanych przestrzeni bez ryzyka zalewania ich niepowiązanymi treściami. Działa to
podobnie do podejścia *Mattermost* wobec *Discorda*: podczas gdy *Discord* jest otwartą platformą bez realnej kontroli
nad infrastrukturą, *Mattermost* pozwala społecznościom hostować własne, niezależne serwery. W podobny sposób
***Magma Quiz*** daje użytkownikom pełną autonomię nad środowiskiem i publikowanymi materiałami.

## W jaki sposób Wasz projekt mógłby zostać wdrożony lub rozwijany? Jacy partnerzy mogliby się zaangażować w jego rozwój?

Z naszego rozwiązania mogłyby korzystać przede wszystkim szkoły oraz różne społeczności tematyczne. Dzięki
zdecentralizowanym serwerom mogłyby one tworzyć własne bazy testów, działające podobnie do oddzielnych wiki w serwisie
*Fandom*, każda grupa funkcjonowałaby w swojej przestrzeni, bez nakładania się treści. W planach mamy również
rozwinięcie funkcji o tryby gier przypominające pokoje znane z *Kahoota*, jednak na czas rejestracji pozostają one
jeszcze na etapie koncepcji.

## Jakie widzicie zagrożenia/ryzyka dla Waszego rozwiązania?

Największym ryzykiem jest możliwość *wynajdywania koła na nowo*. Choć nie kopiujemy żadnego istniejącego rozwiązania,
nasza koncepcja w naturalny sposób przypomina popularne platformy, co może utrudniać wyróżnienie się na rynku. Kolejnym
zagrożeniem jest korzystanie z eksperymentalnych, dynamicznie rozwijających się bibliotek, które mogą często ulegać
zmianom. Wymusza to stałe dostosowywanie kodu, aktualizacje oraz reagowanie na niestabilność funkcji, co zwiększa
zarówno nakład pracy, jak i ryzyko problemów technicznych.

## Opisz zdiagnozowane zagrożenia jak np. problemy technologiczne czy konieczność zaangażowania innych podmiotów np. urząd miasta

Potencjalnym zagrożeniem jest korzystanie z eksperymentalnych API oraz bibliotek powiązanych z *Jetpack Compose
Multiplatform*, który nadal znajduje się w fazie intensywnego rozwoju, szczególnie w kontekście platform innych niż
Android i iOS. Może to wiązać się z ograniczoną stabilnością i koniecznością dostosowywania kodu wraz z rozwojem
technologii. Dodatkowym wyzwaniem jest potrzeba współpracy z zewnętrznymi podmiotami w celu uruchamiania
zdecentralizowanych serwerów zarządzanych przez lokalnych administratorów. Jest to kluczowy element projektu, dlatego
wsparcie takich organizacji jest niezbędne, choć nie dotyczy to instytucji rządowych, a raczej partnerów technicznych
lub społecznościowych.

## Dlaczego akurat Wy powinniście wygrać?

Uważamy, że powinniśmy wygrać, ponieważ jesteśmy zaangażowanym, pomysłowym i ambitnym zespołem, który konsekwentnie
rozwija swoje umiejętności w obszarze IT. To nasz drugi start w konkursie i traktujemy go nie tylko jako okazję do
rywalizacji, ale przede wszystkim jako wartościowy proces nauki. Niezależnie od zajętego miejsca zdobywamy
doświadczenie, które realnie wpływa na nasz rozwój zawodowy i pozwala nam stawać się coraz lepszymi specjalistami.
Wierzymy, że nasza determinacja, chęć tworzenia oraz umiejętność wyciągania wniosków z każdego etapu pracy wyróżniają
nas na tle innych.

Jak głosi materiał promocyjny  ***Reszta wyjdzie w trakcie***.

## Napisz, co wyróżnia Wasz pomysł lub jego realizacja np. wybór innowacyjnej technologii.

Na tle innych rozwiązań wyróżnia nas wybór technologii *Kotlin* wraz z *Jetpack Compose Multiplatform*, który pozwala
tworzyć jedną bazę kodu dla wielu systemów. W świecie, w którym dominują aplikacje webowo‑hybrydowe, takie podejście
zapewnia nowoczesność i elastyczność. Naszą aplikację można stosunkowo łatwo przenieść na iOS, macOS czy wersję webową,
choć wymaga to środowiska macOS, a sama wersja Webowa pozostaje w niestabilnej fazie alpha, co zauważyliśmy podczas prób
wdrożenia. Drugim wyróżnikiem jest decentralizacja: brak centralnego zbioru danych użytkowników i możliwość hostowania
własnych serwerów. To podejście bliższe społecznościowym projektom niż modelowi dużych korporacji i podkreśla ideę
narzędzia tworzonego ***dla użytkowników i przez użytkowników***.