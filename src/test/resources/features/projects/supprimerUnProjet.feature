Feature: Supprimer un projet

  En tant que Tipster
  Je souhaite pouvoir supprimer un projet non utilisé
  Afin d'afficher une meilleure visibilité de mes projets vis à vis des abonnés

  Background:

    Given des clients existent:
      | id     | pseudo          |
      | 0      | Admin           |
      | 123    | Massi           |
      | 456    | Bobby           |
      | 789    | Zboubi          |

    Given des projets existent
      | customerId   | projectId   | title           | bankrolInit  | objectif               | endDate     |
      | 123          | 1111        | fun             | 200.00       | Atteindre 400.00 euros | 2019/12/25  |
      | 123          | 2222        | global          | 100.00       | Atteindre 200.00 euros |             |
      | 123          | 3333        | Gestion de BK   | 1000.00      |                        |             |

    Given des pronostics existent:
      | project      | pronoId  | cote   | status       | mise      | uniteMise     |
      | 1111         | 1        | 1.56   | PUBLISHED    | 25.00     | EURO          |
      | 1111         | 2        | 1.75   | DRAFT        |           |               |
      | 3333         | 3        | 1.75   | DRAFT        |           |               |

  Scenario: Supprimer un projet ne contenant pas de pronostic
    Given je suis un client authentifié en tant que "Massi"
    When je tente de supprimer le projet "2222"
    Then la suppresion du projet est effective

  Scenario: Supprimer un projet contenant 1 ou plusieurs pronostics publié(s)
    Given je suis un client authentifié en tant que "Massi"
    When je tente de supprimer le projet "1111"
    Then une erreur de suppression de projet est remontée car le projet est débuté
