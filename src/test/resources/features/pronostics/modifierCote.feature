Feature: Modifier la cote d'un pronostic publié

  En tant que Tipster
  Je souhaite pouvoir modifier la cote d'un pronostic
  Afin de rendre exact les données du pronostic

  Background:

    Given des clients existent:
      | id     | pseudo          | nbClaims  |
      | 123    | Massi           | 3         |
      | 456    | Joe             | 3         |

    Given des projets existent
      | customerId   | projectId   | title           | bankrolInit  | objectif               | endDate     |
      | 123          | 1111        | fun             | 200.00       | Atteindre 400.00 euros | 2019/12/25  |
      | 123          | 2222        | global          | 100.00       | Atteindre 200.00 euros |             |
      | 123          | 3333        | Gestion de BK   | 1000.00      |                        |             |

    Given des pronostics existent:
      | project      | pronoId  | cote   | status         | mise         | uniteMise      |
      | 1111         | 11       | 1.56   | PUBLISHED      | 15.00        | EURO           |
      | 1111         | 12       | 1.96   | LOST           | 15.00        | EURO           |
      | 1111         | 13       | 2.23   | WON            | 15.00        | EURO           |

  Scenario: Modifier la cote d'un pronostic avant qu'il soit décidé Gagnant ou Perdant
  On peut modifier la cote d'u npronostic tant qu'il n'a pas été décidé mais pas après
  Ex : un des matchs du prono a été annulé, la cote est impactée.

    Given je suis un client authentifié en tant que "Massi"
    When je saisis une nouvelle cote de "1.40" sur le pronostic "11"
    Then je vérifie que le changement de cote est effectif

  Scenario: Modifier la cote d'un pronostic après qu'il soit décidé Gagnant
  On peut modifier la cote d'un pronostic tant qu'il n'a pas été décidé mais pas après
    """
    Vérifier qu'on ne peut pas modifier une côte d'un prono qui a été déclaré Gagnant ou Perdant
    """

    Given je suis un client authentifié en tant que "Massi"
    When je saisis une nouvelle cote de "1.40" sur le pronostic "13"
    Then je vérifie que le changement de cote n'est pas effectif


  Scenario: Modifier la cote d'un pronostic après qu'il soit décidé Perdant
  On peut modifier la cote d'un pronostic tant qu'il n'a pas été décidé mais pas après

    Given je suis un client authentifié en tant que "Massi"
    When je saisis une nouvelle cote de "1.40" sur le pronostic "12"
    Then je vérifie que le changement de cote n'est pas effectif