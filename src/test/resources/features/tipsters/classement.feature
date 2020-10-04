Feature: Calculer la valeur d'un tipster
  En tant que Système
  Je souhaite calculer la valeur d'un tipster
  Afin de pouvoir en extraire un classement

  Background:
    Given des clients existent:
      | id     | pseudo          |
      | ABC    | Massi           |
      | DEF    | Joe             |
      | GHI    | Bobby           |
      | ZQE    | Mike            |
      | ZBO    | Zboubi          |

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


