Feature: Modifier la cote d'un pronostic publié

  En tant que Tipster
  Je souhaite pouvoir modifier la cote d'un pronostic
  Afin de rendre exact les données du pronostic

  Background:

    Given des clients existent:
      | id     | pseudo          | nbClaims  |
      | ABC    | Massi           | 3         |
      | DEF    | Joe             | 3         |

    Given des projets existent
      | customerId   | projectId   | title           | bankrol  | objectif               | endDate     |
      | ABC          | 1111        | fun             | 200.00   | Atteindre 400.00 euros | 2019/12/25  |
      | ABC          | 2222        | global          | 100.00   | Atteindre 200.00 euros |             |
      | ABC          | 3333        | Gestion de BK   | 1000.00  |                        |             |

    Given des pronostics existent:
      | project      | pronoId  | cote   | status         |
      | 1111         | A1       | 1.56   | PUBLISHED      |
      | 1111         | A2       | 1.96   | LOST           |
      | 1111         | C1       | 2.23   | WON            |

  Scenario: Modifier la cote d'un pronostic avant qu'il soit décidé Gagnant ou Perdant
  On peut modifier la cote d'u npronostic tant qu'il n'a pas été décidé mais pas après
  Ex : un des matchs du prono a été annulé, la cote est impactée.

    Given je suis un client authentifié en tant que "Massi"
    When je saisis une nouvelle cote de "1.40" sur le pronostic "A1"
    Then je vérifie que le changement de cote est effectif

  Scenario: Modifier la cote d'un pronostic après qu'il soit décidé Gagnant
  On peut modifier la cote d'un pronostic tant qu'il n'a pas été décidé mais pas après
    """
    Vérifier qu'on ne peut pas modifier une côte d'un prono qui a été déclaré Gagnant ou Perdant
    """

    Given je suis un client authentifié en tant que "Massi"
    When je saisis une nouvelle cote de "1.40" sur le pronostic "C1"
    Then je vérifie que le changement de cote n'est pas effectif


  Scenario: Modifier la cote d'un pronostic après qu'il soit décidé Perdant
  On peut modifier la cote d'un pronostic tant qu'il n'a pas été décidé mais pas après

    Given je suis un client authentifié en tant que "Massi"
    When je saisis une nouvelle cote de "1.40" sur le pronostic "A2"
    Then je vérifie que le changement de cote n'est pas effectif