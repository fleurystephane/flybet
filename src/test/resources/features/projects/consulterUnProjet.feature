Feature: Consulter un projet

  En tant qu'abonné à un Tipster
  Je souhaite consulter un projet
  Afin de savoir où on en est

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

    Given des pronostics existent:
      | project      | pronoId  | cote   | status         |
      | 1111         | A1       | 1.56   | CERTIFIED_WON  |
      | 1111         | A2       | 1.81   | CERTIFIED_WON  |
      | 1111         | A3       | 1.92   | CERTIFIED_WON  |
      | 1111         | A4       | 1.67   | CERTIFIED_LOST |
      | 1111         | A5       | 2.15   | CERTIFIED_WON  |
      | 1111         | A6       | 2.04   | CERTIFIED_LOST |
      | 1111         | A7       | 1.95   | CERTIFIED_WON  |


    Scenario: