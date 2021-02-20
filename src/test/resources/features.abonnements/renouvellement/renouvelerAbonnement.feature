Feature: Renouveler son abonnement à un tipster
  En tant que client abonné à un Tipster
  Je souhaite renouveler mon abonnement à ce  Tipster
  Afin de continuer de profiter de ses pronostics

  Background:

    Given des clients existent:
      | id     | pseudo          | nbClaims  |
      | 0      | Admin           | 3         |
      | 123    | Massi           | 3         |
      | 456    | Bobby           | 3         |
      | 789    | Zboubi          | 3         |

    Given des tarifs existent:
      | id     | rate       | duration   |
      | 123    | 10.00      | 1          |
      | 123    | 25.00      | 3          |

  Scenario: Renouveler son abonnement 5 jours avant la fin de l'abonnement en cours
    Given je suis un client authentifié en tant que "Bobby"
    And le solde de mon compte est de "50.00" crédits
    And je suis abonné au compte de "123" le "2019/06/20" pour 3 mois
    And le solde du compte de "Massi" est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
        # Donc une fin d'abonnement prévue pour le 20 Sept 2019...
    When je tente de m'abonner au compte de "123" pour 1 mois le "2019/09/15"
    Then l'abonnement au compte de "123" est effectif
    And la fin de mon abonnement est le "2019/10/21" inclus
      
  Scenario: Renouveler son abonnement 5 jours avant la date de fin de l'abonnement en cours mais sans crédits suffisants
    Given je suis un client authentifié en tant que "Bobby"
    And le solde de mon compte est de "0.00" crédits
    And je suis abonné au compte de "123" le "2019/06/20" pour 3 mois
    And le solde du compte de "Massi" est de "2500.00" crédits
    And le solde du compte de "Admin" est de "150000.00" crédits
        # Donc une fin d'abonnement prévue pour le 20 Sept 2019...
    When je tente de m'abonner au compte de "123" pour 1 mois le "2019/09/15"
    Then une erreur de solde insuffisant est remontée
    And la fin de mon abonnement à "123" est le "2019/09/20" inclus