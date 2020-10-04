@projetModification
Feature: Modifier un projet pour un Tipster

  En tant que Tipster
  Je souhaite pouvoir modifier un projet
  Afin d'en améliorer son objectif

  Rule:
  - Il n'est plus possible de modifier un projet dès que ce dernier est débuté.
  - Un projet débuté est un projet sur lequel au moins 1 pronostic a été publié


    Background:

    Given des clients existent:
      | id  | pseudo | nbClaims |
      | ADM | Admin  | 3        |
      | ABC | Massi  | 3        |
      | TRY | Bobby  | 3        |
      | OPL | Zboubi | 3        |

    Given des projets existent
      | customerId | projectId | title         | bankrol | objectif               | endDate    |
      | ABC        | 1111      | fun           | 200.00  | Atteindre 400.00 euros | 2019/12/25 |
      | ABC        | 2222      | global        | 100.00  | Atteindre 200.00 euros |            |
      | ABC        | 3333      | Gestion de BK | 1000.00 |                        |            |

    Given des pronostics existent:
      | project | pronoId | cote | status    |
      | 1111    | A1      | 1.56 | PUBLISHED |

  @définirObjectif
  Scenario: Définir l'objectif d'un projet non débuté
    Given je suis un client authentifié en tant que "Massi"
    When je tente de modifier l'objectif du projet "2222" comme étant "Atteindre 300 Euros en 3 semaines"
    Then la modification du projet est effective
    And je vérifie que l'objectif du projet "2222" est "Atteindre 300 Euros en 3 semaines"
    And le projet "2222" contient 0 pronostic


    @modifierObjectif
    Scenario: Impossible de modifier l'objectif d'un projet débuté
      Given je suis un client authentifié en tant que "Massi"
      And je dispose du projet "1111" contenant 1 pronostic
      When je tente de modifier l'objectif du projet "1111" comme étant "Atteindre 300 Euros en 3 semaines"
      Then une erreur est remontée car le projet est déjà débuté



    @modifierBankrol
    Scenario: Impossible de modifier la bankrol d'un projet débuté
      Given je suis un client authentifié en tant que "Massi"
      And je dispose du projet "1111" contenant 1 pronostic
      When je tente de modifier la bankrol du projet "1111" en spécifiant "250.00" euros
      Then une erreur est remontée car le projet est déjà débuté

    @modifierDateFin
    Scenario: Impossible de modifier la date de fin d'un projet débuté
      Given je suis un client authentifié en tant que "Massi"
      And je dispose du projet "1111" contenant 1 pronostic
      When je tente de modifier la date de fin du projet "1111" en spécifiant le "2020/03/03"
      Then une erreur est remontée car le projet est déjà débuté

    @modifierTitre
    Scenario: Impossible de modifier le titre d'un projet débuté
      Given je suis un client authentifié en tant que "Massi"
      And je dispose du projet "1111" contenant 1 pronostic
      When je tente de modifier le titre du projet "1111" avec "blablabla"
      Then une erreur est remontée car le projet est déjà débuté