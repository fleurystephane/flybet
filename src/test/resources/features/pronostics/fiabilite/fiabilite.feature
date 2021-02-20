@fiabilite
Feature: Fiabiliser les résultats saisis par les clients eux-mêmes

  En tant que Administrateur du site
  Je souhaite proposer un système aux utilisateurs du site
  Afin de fiabiliser les résultats

  Background:

    Given des clients existent:
      | id     | pseudo          | nbClaims  |
      | 123    | Massi           | 3         |
      | 456    | Joe             | 2         |
      | 789    | Bobby           | 3         |
      | 100    | Mike            | 3         |
      | 0      | Admin           | 3         |

    Given des projets existent
      | customerId   | projectId   | title           | bankrolInit  | objectif               | endDate     |
      | 123          | 1111        | fun             | 200.00       | Atteindre 400.00 euros | 2019/12/25  |
      | 123          | 2222        | global          | 100.00       | Atteindre 200.00 euros |             |
      | 123          | 3333        | Gestion de BK   | 1000.00      |                        |             |

    Given des pronostics existent:
      | project      | pronoId  | cote   | status         | mise         | uniteMise      |
      | 1111         | 11       | 1.56   | WON            | 10           | EURO           |
      | 1111         | 110      | 1.98   | LOST           | 15.00        | EURO           |
      | 1111         | 22       | 1.75   | PUBLISHED      | 15.00        | EURO           |
      | 1111         | 33       | 1.78   | CERTIFIED_WON  | 15.00        | EURO           |
      | 1111         | 110      | 2.68   | CERTIFIED_LOST | 15.00        | EURO           |
      | 2222         | 1001     | 3.40   | DRAFT          | 10.00        | EURO           |
      | 3333         | 15       | 1.56   | WON            | 15.00        | EURO           |
      | 3333         | 16       | 1.56   | WON            | 15.00        | EURO           |
      | 3333         | 17       | 1.56   | WON            | 15.00        | EURO           |


  @desapprouverProno @pronoValide
  Scenario: Désapprouver un pronostic validé
    Given je suis un client authentifié en tant que "Joe"
    When je tente de désapprouver le pronostic "11"
    Then le pronostic "11" contient 1 désapprobation
    And je ne dispose plus que de 1 revendications

  @desapprouverProno @SansRevendicationRestante
  Scenario: Désapprouver un pronostic validé par un customer qui ne dispose plus de revendication
    Given je suis un client authentifié en tant que "Joe"
    And j'ai posé une désapprobation sur le pronostic "15"
    And j'ai posé une désapprobation sur le pronostic "16"
    And j'ai posé une désapprobation sur le pronostic "17"
    When je tente de désapprouver le pronostic "11"
    Then une erreur est remontée car je ne suis plus autorisé à désapprouver un pronostic


  @desapprouverProno @pronoInexistant
  Scenario: Désapprouver un pronostic inexistant
    Given je suis un client authentifié en tant que "Joe"
    When je tente de désapprouver le pronostic "1500"
    Then une erreur est remontée car le pronostic n'existe pas

  @desapprouverProno @propreProno
  Scenario: Désapprouver son propre pronostic
    Given je suis un client authentifié en tant que "Massi"
    When je tente de désapprouver le pronostic "11"
    Then une erreur est remontée car on ne peut pas désapprouver son propre pronostic

  @desapprouverProno @dejaDesapprouveParMoi
  Scenario: Désapprouver un pronostic validé déjà désapprouvé par moi
    Given je suis un client authentifié en tant que "Bobby"
    And j'ai posé une désapprobation sur le pronostic "11"
    When je tente de désapprouver le pronostic "11"
    Then une erreur est remontée indiquant que j'ai déjà désapprouvé ce pronostic

  @desapprouverProno @pronoConfirme
  Scenario: Impossible de désapprouver un pronostic confirmé
    Given je suis un client authentifié en tant que "Bobby"
    When je tente de désapprouver le pronostic "33"
    Then une erreur est remontée indiquant que le pronostic est déjà confirmé

  @desapprouverProno @pronoPerdant
    Scenario: impossible de désapprouver un pronostic décidé perdant
    Given je suis un client authentifié en tant que "Bobby"
    When je tente de désapprouver le pronostic "33"
    Then une erreur est remontée car le pronostic n'est pas désapprouvable

  @declarerGagnant
  Scenario: Déclarer gagnant un pronostic publié et déclencher une notification
    Given je suis un client authentifié en tant que "Massi"
    And le nombre d'abonné à "Massi" est 0
    When je tente de déclarer gagnant le pronostic "22"
    Then une notification a été envoyée sur le pronostic "22"
    And le pronostic "22" est gagnant

  @declarerGagnant
  Scenario: Déclarer Gagnant un pronostic DRAFT conduit à un échec
    Given je suis un client authentifié en tant que "Massi"
    And le nombre d'abonné à "Massi" est 0
    When je tente de déclarer gagnant le pronostic "1001"
    Then une erreur est remontée indiquant que le pronostic doit être publié

  @administrateur @declarerPerdant @avertissement
  Scenario: Validation par le système du pronostic erroné suite à la présence de désapprobation(s)
    Given je suis un client authentifié en tant que "Admin"
    And "Joe" a posé une désapprobation sur le pronostic "11"
    And "Bobby" a posé une désapprobation sur le pronostic "11"
    And "Mike" a posé une désapprobation sur le pronostic "11"
    And il y a 3 désapprobations sur le pronostic "11"
    When je déclare Perdant le pronostic "11" de "Massi" déclaré gagnant
    Then toutes les désapprobations sur le pronostic "11" sont restituées
    And le propriétaire "Massi" du pronostic "11" est sanctionné d'un avertissement

  @administrateur @declarerPerdant @deuxiemeAverstissement
  Scenario: Un tipster reçoit un 2ème avertissement
    Given je suis un client authentifié en tant que "Admin"
    And "Massi" a déjà reçu un avertissement pour le pronostic "110"
    When je déclare Perdant le pronostic "11" de "Massi" déclaré gagnant
    Then je vérifie que "Massi" est sous 2 avertissements
    And une notification a été envoyée sur le pronostic "11"
