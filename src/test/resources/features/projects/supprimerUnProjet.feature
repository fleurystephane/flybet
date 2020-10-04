Feature: Supprimer un projet

  En tant que Tipster
  Je souhaite pouvoir supprimer un projet non utilisé
  Afin d'afficher une meilleure visibilité de mes projets vis à vis des abonnés

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
      | ABC          | 2222        | global          | 100.00   | Atteindre 200.00 euros |             |
      | ABC          | 3333        | Gestion de BK   | 1000.00  |                        |             |

    Given des pronostics existent:
      | project      | pronoId  | cote   | status       |
      | 1111         | A1       | 1.56   | PUBLISHED    |
      | 1111         | A2       | 1.75   | DRAFT        |
      | 3333         | A3       | 1.75   | DRAFT        |

  Scenario: Supprimer un projet ne contenant pas de pronostic
    Given je suis un client authentifié en tant que "Massi"
    When je tente de supprimer le projet "2222"
    Then la suppresion du projet est effective

  Scenario: Supprimer un projet contenant 1 ou plusieurs pronostics
    Given je suis un client authentifié en tant que "Massi"
    When je tente de supprimer le projet "3333"
    Then une erreur de suppression de projet est remontée car le projet est débuté
