Feature: Autour du paiement d'un abonnement à un tipster
  En tant que client
  Je souhaite m'abonner au compte d'un Tipster
  Afin de profiter de ses pronostics

  Background:

    Given des clients existent:
      | id     | pseudo          |
      | 0      | Admin           |
      | 123    | Massi           |
      | 345    | Bobby           |
      | 456    | Zboubi          |

    Given des tarifs existent:
      | id     | rate       | duration   |
      | 123    | 10.00      | 1          |
      | 123    | 25.00      | 3          |
      | 123    | 40.00      | 6          |


  Scenario Outline: S'acquitter du paiement d'un abonnement
    Given je suis un client authentifié en tant que "Bobby"
    And le solde de mon compte est de "<solde_avant>" crédits
    And le solde du compte de "Massi" est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de m'abonner au compte de "123" pour 1 mois le "2019/12/11"
    Then l'abonnement au compte de "Massi" est effectif
    And le solde de mon compte est de "<solde_apres>" crédits

    Examples:
    | solde_avant     | solde_apres     |
    | 10.00           | 0.00            |
    | 15.00           | 5.00            |
    | 12.00           | 2.00            |


  Scenario: S'acquitter du paiement d'un abonnement en étant abonné également à un autre compte
    Given je suis un client authentifié en tant que "Bobby"
    And je suis abonné au compte de "456" le "2019/11/01" pour 3 mois
    And le solde de mon compte est de "32.00" crédits
    And le solde du compte de "Massi" est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de m'abonner au compte de "123" pour 1 mois le "2019/12/11"
    Then l'abonnement au compte de "Massi" est effectif
    And le solde de mon compte est de "22.00" crédits


  Scenario: Solde insuffisant pour le paiement d'un abonnement
    Given je suis un client authentifié en tant que "Bobby"
    And le solde de mon compte est de "8.00" crédits
    And le solde du compte de "Massi" est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de m'abonner au compte de "123" pour 1 mois le "2019/12/11"
    Then une erreur de solde insuffisant est remontée
    And le solde de mon compte est de "8.00" crédits

  Scenario: Obtenir le meilleur tarif d'abonnement
    Given je suis un client authentifié en tant que "Bobby"
    When je demande le tarif pour s'abonner au compte de "123" pour 3 mois
    Then le tarif de l'abonnement est de "25.00"


  Scenario: S'abonner à un Tipster pour une durée de 3 mois avec une offre spéciale 3 mois
    Given je suis un client authentifié en tant que "Bobby"
    And le solde de mon compte est de "30.00" crédits
    And le solde du compte de "Massi" est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de m'abonner au compte de "123" pour 3 mois le "2019/12/11"
    Then l'abonnement au compte de "123" est effectif
    And le solde de mon compte est de "5.00" crédits

  Scenario: Valider les dates de début et de fin d'abonnement
    Given je suis un client authentifié en tant que "Bobby"
    And le solde de mon compte est de "30.00" crédits
    And le solde du compte de "Massi" est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de m'abonner au compte de "123" pour 1 mois le "2019/12/11"
    Then l'abonnement au compte de "123" est effectif
    And la fin de mon abonnement est le "2020/01/11" inclus

  Scenario: Reverser le montant payé par le nouvel abonné au Tipster
    Given je suis un client authentifié en tant que "Bobby"
    And le solde de mon compte est de "30.00" crédits
    And le solde du compte de "Massi" est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de m'abonner au compte de "123" pour 1 mois le "2019/11/11"
    Then l'abonnement au compte de "123" est effectif
    And le solde de mon compte est de "20.00" crédits
    And le solde du compte de "Massi" est alimenté

  Scenario: Reverser une partie de l'abonnement au compte système
    Given je suis un client authentifié en tant que "Bobby"
    And le solde de mon compte est de "40.00" crédits
    And le solde du compte de "Massi" est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
    When je tente de m'abonner au compte de "123" pour 3 mois le "2019/11/11"
    Then l'abonnement au compte de "123" est effectif
    And le solde de mon compte est de "15.00" crédits
    And le solde du compte de "Massi" est alimenté
    And le solde du compte de "Admin" est alimenté



