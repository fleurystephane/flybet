@créationDeProjet
Feature: Créer ses projets pour un Tipster

  En tant que Tipster
  Je souhaite pouvoir créer un nouveau projet
  Afin d'organiser mes pronostics pour un nouvel objectif

  Background:

    Given des clients existent:
      | id     | pseudo          |
      | ADM    | Admin           |
      | ABC    | Massi           |
      | TRY    | Bobby           |
      | OPL    | Zboubi          |

    Given des projets existent
      | customerId   | projectId   | title           | bankrol  | objectif               | endDate     |
      | ABC          | 1111        | fun             | 200.00   | Atteindre 400.00 euros | 2019/12/25  |

  @miseAJourSoldes
  Scenario: Mettre à jour les soldes Admin et customer après création d'un projet sans échéance
    Given je suis un client authentifié en tant que "Massi"
    And le solde de mon compte est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de créer un projet "Gestion de bankrol" avec une bankrol de "200.00" euros
    Then la création du projet est effective
    And le solde du compte de "Massi" est débité
    And le nombre total de projet de "Massi" est de 2
    And le solde du compte de "Admin" est alimenté

  @dateFinDansLePassé
  Scenario: Créer un projet avec une date de fin dans le passé
    Given je suis un client authentifié en tant que "Massi"
    And le solde de mon compte est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de créer un projet "Gestion de bankrol" avec une bankrol de "200.00" euros et une fin le "2019/01/01"
    Then une erreur est remontée car la date de fin du projet ne peut être passée

  @miseAJourSoldes
  Scenario: Mettre à jour les soldes Admin et customer après création d'un projet avec échéance
    Given je suis un client authentifié en tant que "Massi"
    And le solde de mon compte est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de créer un projet "Gestion de bankrol" avec une bankrol de "200.00" euros et une fin le "2025/01/01"
    Then la création du projet est effective
    And le solde du compte de "Massi" est débité
    And le nombre total de projet de "Massi" est de 2
    And le solde du compte de "Admin" est alimenté

  @nomExistant
  Scenario: Impossible de créer un projet avec comme intitulé le nom d'un projet existant pour ce Tipster
    Given je suis un client authentifié en tant que "Massi"
    And le solde de mon compte est de "2500.00" crédits
    When je tente de créer un projet "fun" avec une bankrol de "200.00" euros et une fin le "2025/01/01"
    Then une erreur est remontée car un projet existe déjà pour ce titre
    And le nombre total de projet de "Massi" est de 1


