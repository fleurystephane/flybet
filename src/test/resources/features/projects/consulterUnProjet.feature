Feature: Consulter un projet

  En tant qu'abonné à un Tipster
  Je souhaite consulter les projets de ce tipster
  Afin d'accéder aux infos des projets

  Background:

    Given des clients existent:
      | id     | pseudo          |
      | 0      | Admin           |
      | 123    | Massi           |
      | 456    | Bobby           |
      | 789    | Zboubi          |

    Given des projets existent
      | customerId   | projectId   | title           | bankrolInit  | objectif                | endDate     |
      | 123          | 1111        | Noel            | 200.00       | Atteindre 400.00 euros  | 2020/12/25  |
      | 123          | 1112        | MassiSMIC       | 200.00       | Atteindre 1200.00 euros | 2021/02/25  |
      | 789          | 2222        | mon projet      | 100.00       | Atteindre 200.00 euros  | 2021/12/25  |

    Given des pronostics existent:
      | project      | pronoId  | cote   | status         | mise   | uniteMise  |
      | 1111         | 1        | 1.56   | CERTIFIED_WON  | 10.00  | EURO       |
      | 1111         | 2        | 1.81   | CERTIFIED_WON  | 10.00  | EURO       |
      | 1111         | 3        | 1.92   | CERTIFIED_WON  | 10.00  | EURO       |
      | 1111         | 4        | 1.67   | CERTIFIED_LOST | 15.00  | EURO       |
      | 1112         | 5        | 2.15   | CERTIFIED_WON  | 100.00 | EURO       |
      | 1112         | 6        | 2.04   | CERTIFIED_LOST | 15.00  | EURO       |
      | 1112         | 7        | 1.95   | CERTIFIED_WON  | 100.00 | EURO       |

  Scenario: Récupérer la liste des projets d'un tipster auquel je suis abonné
    Given je suis un client authentifié en tant que "Bobby"
    And je suis abonné au compte de "123" le "2020/01/01" pour 100 mois
    When je récupère tous les projets de "Massi"
    Then je vérifie que j'obtiens 2 projets

  Scenario: Récupérer la liste des projets d'un tipster auquel je ne suis pas abonné
    Given je suis un client authentifié en tant que "Bobby"
    And je suis abonné au compte de "123" le "2020/01/01" pour 100 mois
    When je récupère tous les projets de "Zboubi"
    Then je vérifie qu'une erreur d'autorisation pas abonné au tipster est remontée