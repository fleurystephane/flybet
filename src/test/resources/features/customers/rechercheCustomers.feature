@rechercherCustomer
Feature: Recherche d'un tipster

  En tant que client
  Je souhaite pouvoir explorer les détails d'un tipster
  Afin de décider si je m'abonne ou non

  Background:

    Given des clients existent:
      | id     | pseudo          |
      | 0      | Admin           |
      | 2      | Massi           |
      | 3      | Bobby           |
      | 4      | Zboubi          |

    Given des projets existent
      | customerId   | projectId   | title           | bankrolInit  | objectif                | endDate     |
      | 2            | 1111        | Noel            | 200.00       | Atteindre 400.00 euros  | 2020/12/25  |
      | 2            | 1112        | MassiSMIC       | 200.00       | Atteindre 1200.00 euros | 2021/02/25  |
      | 3            | 1113        | mon projet      | 100.00       | Atteindre 200.00 euros  | 2021/12/25  |


  Scenario: Retrouver un tipster à partir d'un pseudo
    Given je suis un client authentifié en tant que "Bobby"
    When je recherche un tipster avec le pseudo "Massi"
    Then je vérifie que j'obtiens le tipster d'id 2

  Scenario: Ne pas retrouver un tipster à partir d'un pseudo
    Given je suis un client authentifié en tant que "Bobby"
    When je recherche un tipster avec le pseudo "TOTO"
    Then je vérifie que je n'obtiens aucun tipster