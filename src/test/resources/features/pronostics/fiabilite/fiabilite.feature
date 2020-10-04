@fiabilite
Feature: Fiabiliser les résultats saisis par les clients eux-mêmes

  En tant que Administrateur du site
  Je souhaite proposer un système aux utilisateurs du site
  Afin de fiabiliser les résultats

  Background:

    Given des clients existent:
      | id     | pseudo          |
      | ABC    | Massi           |
      | DEF    | Joe             |
      | GHI    | Bobby           |
      | ZQE    | Mike            |
      | ADM    | Admin           |

    Given des projets existent
      | customerId   | projectId   | title           | bankrol  | objectif               | endDate     |
      | ABC          | 1111        | fun             | 200.00   | Atteindre 400.00 euros | 2019/12/25  |
      | ABC          | 2222        | global          | 100.00   | Atteindre 200.00 euros |             |
      | ABC          | 3333        | Gestion de BK   | 1000.00  |                        |             |

    Given des pronostics existent:
      | project      | pronoId  | cote   | status         |
      | 1111         | A1       | 1.56   | WON            |
      | 1111         | A10      | 1.98   | LOST           |
      | 1111         | A2       | 1.75   | PUBLISHED      |
      | 1111         | A3       | 1.78   | CERTIFIED_WON  |
      | 1111         | A10      | 2.68   | CERTIFIED_LOST |
      | 2222         | D1       | 3.40   | DRAFT          |
      | 3333         | T1       | 1.56   | WON            |
      | 3333         | T2       | 1.56   | WON            |
      | 3333         | T3       | 1.56   | WON            |


  @desapprouverProno @pronoValide
  Scenario: Désapprouver un pronostic validé
    Given je suis un client authentifié en tant que "Joe"
    When je tente de désapprouver le pronostic "A1"
    Then le pronostic "A1" contient 1 désapprobation
    And je ne dispose plus que de 2 revendications

  @desapprouverProno @SansRevendicationRestante
  Scenario: Désapprouver un pronostic validé par un customer qui ne dispose plus de revendication
    Given je suis un client authentifié en tant que "Joe"
    And j'ai posé une désapprobation sur le pronostic "T1"
    And j'ai posé une désapprobation sur le pronostic "T2"
    And j'ai posé une désapprobation sur le pronostic "T3"
    When je tente de désapprouver le pronostic "A1"
    Then une erreur est remontée car je ne suis plus autorisé à désapprouver un pronostic


  @desapprouverProno @pronoInexistant
  Scenario: Désapprouver un pronostic inexistant
    Given je suis un client authentifié en tant que "Joe"
    When je tente de désapprouver le pronostic "B0"
    Then une erreur est remontée car le pronostic n'existe pas

  @desapprouverProno @propreProno
  Scenario: Désapprouver son propre pronostic
    Given je suis un client authentifié en tant que "Massi"
    When je tente de désapprouver le pronostic "A1"
    Then une erreur est remontée car on ne peut pas désapprouver son propre pronostic

  @desapprouverProno @dejaDesapprouveParMoi
  Scenario: Désapprouver un pronostic validé déjà désapprouvé par moi
    Given je suis un client authentifié en tant que "Bobby"
    And j'ai posé une désapprobation sur le pronostic "A1"
    When je tente de désapprouver le pronostic "A1"
    Then une erreur est remontée indiquant que j'ai déjà désapprouvé ce pronostic

  @desapprouverProno @pronoConfirme
  Scenario: Impossible de désapprouver un pronostic confirmé
    Given je suis un client authentifié en tant que "Bobby"
    When je tente de désapprouver le pronostic "A3"
    Then une erreur est remontée indiquant que le pronostic est déjà confirmé

  @desapprouverProno @pronoPerdant
    Scenario: impossible de désapprouver un pronostic décidé perdant
    Given je suis un client authentifié en tant que "Bobby"
    When je tente de désapprouver le pronostic "A3"
    Then une erreur est remontée car le pronostic n'est pas désapprouvable

  @declarerGagnant
  Scenario: Déclarer gagnant un pronostic publié et déclencher une notification
    Given je suis un client authentifié en tant que "Massi"
    And le nombre d'abonné à "Massi" est 0
    When je tente de déclarer gagnant le pronostic "A2"
    Then une notification a été envoyée
    And le pronostic "A2" est gagnant

  @declarerGagnant
  Scenario: Déclarer Gagnant un pronostic DRAFT conduit à un échec
    Given je suis un client authentifié en tant que "Massi"
    And le nombre d'abonné à "Massi" est 0
    When je tente de déclarer gagnant le pronostic "D1"
    Then une erreur est remontée indiquant que le pronostic doit être publié

  @administrateur @declarerPerdant @avertissement
  Scenario: Validation par le système du pronostic erroné suite à la présence de désapprobation(s)
    Given je suis un client authentifié en tant que "Admin"
    And "Joe" a posé une désapprobation sur le pronostic "A1"
    And "Bobby" a posé une désapprobation sur le pronostic "A1"
    And "Mike" a posé une désapprobation sur le pronostic "A1"
    And il y a 3 désapprobations sur le pronostic "A1"
    When je déclare Perdant le pronostic "A1" de "Massi" déclaré gagnant
    Then toutes les désapprobations sur le pronostic "A1" sont restituées
    And le propriétaire "Massi" du pronostic "A1" est sanctionné d'un avertissement

  @administrateur @declarerPerdant @deuxiemeAverstissement
  Scenario: Un tipster reçoit un 2ème avertissement
    Given je suis un client authentifié en tant que "Admin"
    And "Massi" a déjà reçu un avertissement pour le pronostic "A10"
    When je déclare Perdant le pronostic "A1" de "Massi" déclaré gagnant
    Then je vérifie que "Massi" est sous 2 avertissements
    And une notification a été envoyée
