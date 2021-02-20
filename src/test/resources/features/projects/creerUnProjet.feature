@créationDeProjet
Feature: Créer ses projets pour un Tipster

  En tant que Tipster
  Je souhaite pouvoir créer un nouveau projet
  Afin d'organiser mes pronostics pour un nouvel objectif

  Background:

    Given des clients existent:
      | id     | pseudo          |
      | 0      | Admin           |
      | 123    | Massi           |
      | 456    | Bobby           |
      | 789    | Zboubi          |

    Given des projets existent
      | customerId   | projectId   | title           | bankrolInit  | objectif               | endDate     |
      | 123          | 1111        | dejafun         | 200.00       | Atteindre 400.00 euros | 2019/12/25  |

    Scenario: Créer un projet d'une durée de 7 jours
      Given je suis un client authentifié en tant que "Massi"
      And j'envisage de créer un nouveau projet "Open de ma ville" avec une bankrol de "100" euros
      And ce futur projet aura une durée de 7 jours
      And le solde de mon compte est de "2500.00" crédits
      And le solde du compte de "Admin" est de "150000.00" crédits
      When je tente de créer ce projet "Open de ma ville"
      Then la création du projet est effective

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
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de créer un projet "dejafun" avec une bankrol de "200.00" euros et une fin le "2025/01/01"
    Then une erreur est remontée car un projet existe déjà pour ce titre
    And le nombre total de projet de "Massi" est de 1


