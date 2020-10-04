Feature: Renouveler son abonnement à un tipster
  En tant que client abonné à un Tipster
  Je souhaite renouveler mon abonnement à ce  Tipster
  Afin de continuer de profiter de ses pronostics

  Background:

    Given des clients existent:
      | id     | pseudo          | nbClaims  |
      | ADM    | Admin           | 3         |
      | ABC    | Massi           | 3         |
      | TRY    | Bobby           | 3         |
      | OPL    | Zboubi          | 3         |

    Given des tarifs existent:
      | id     | rate       | duration   |
      | ABC    | 10.00      | 1          |
      | ABC    | 25.00      | 3          |

    Scenario: Renouveler son abonnement 5 jours avant la fin de l'abonnement en cours
      Given je suis un client authentifié en tant que "Bobby"
      And le solde de mon compte est de "50.00" crédits
      And je suis abonné au compte de "ABC" le "2019/06/20" pour 3 mois
      And le solde du compte de "Massi" est de "2500.00" crédits
      And le solde du compte de "Admin" est de "150000.00" crédits
      # Donc une fin d'abonnement prévue pour le 20 Sept 2019...
      When je tente de m'abonner au compte de "ABC" pour 1 mois le "2019/09/15"
      Then l'abonnement est effectif
      And la fin de mon abonnement est le "2019/10/21" inclus