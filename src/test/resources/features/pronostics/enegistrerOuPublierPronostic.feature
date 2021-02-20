@publierProno
Feature: Publier un pronostic pour un Tipster au sein d'un projet

  En tant que Tipster
  Je souhaite publier mon pronostic
  Afin de le rendre accessible à mes abonnés
  Et de progresser au classement des Tipsters

  """  Un prono suit les cycle : [enregistré] - publié - décidé - confirmé
  Et la correspondance en statut est  :
    enregistré = DRAFT - c'est un brouillon
    publié = NOT DRAFT et NOT GAGNANT et NOT PERDANT et NOT CERTIFIED - les abonnés ont été notifiés
    décidé = NOT DRAFT et (GAGNANT ou PERDANT) et NOT CERTIFIED - le tipster a marqué le prono Gagnant ou Perdant
    confirmé = (GAGNANT ou PERDANT) et CERTIFIED - le système a confirmé l'issue du pronostic
    """

	Background:

      Given des clients existent:
        | id     | pseudo          |
        | 123    | Massi           |
        | 456    | Joe             |


      Given des projets existent
        | customerId   | projectId   | title           | bankrolInit  | objectif               | endDate     |
        | 123          | 1111        | fun             | 200.00       | Atteindre 400.00 euros | 2019/12/25  |
        | 123          | 2222        | global          | 100.00       | Atteindre 200.00 euros |             |
        | 123          | 3333        | Gestion de BK   | 1000.00      |                        |             |
        | 123          | 1234        | MassiSMIC1      | 200.00       | Atteindre 1200.00 euros| 2021/01/31  |

      Given des pronostics existent:
        | project      | pronoId  | cote   | status         | mise   | uniteMise  |
        | 1111         | 11       | 1.56   | PUBLISHED      | 1      | POURCENT   |
        | 1111         | 12       | 1.75   | DRAFT          | 10.00  | EURO       |
        | 3333         | 20       | 2.33   | DRAFT          | 2      | POURCENT   |
        | 3333         | 25       | 1.09   | DRAFT          | 15.00  | EURO       |
        | 1234         | 1001     | 2.10   | CERTIFIED_LOST | 100.00 | EURO       |



Scenario: Publier un pronostic dans un projet alors qu'un pronostic est déjà publié dans ce projet
  Given je suis un client authentifié en tant que "Massi"
  And j'ai créé un nouveau pronostic "3131" de cote "1.55" de mise "10.00" "EURO"
  When je tente de publier le pronostic "99" dans le projet "1111"
  Then je vérifie qu'une erreur est remontée

  Scenario: Publier un pronostic dans un projet alors qu'un pronostic est déjà publié dans un autre projet.

    Given je suis un client authentifié en tant que "Massi"
    And j'ai enregistré le pronostic "88" de cote "1.88" et de mise "10.00" "EURO" dans le projet "2222"
    When je tente de publier le pronostic "88" dans le projet "2222"
    Then je vérifie que la publication est effective

  Scenario: Publier un pronostic enregistré comme brouillon dans un projet
    Given je suis un client authentifié en tant que "Massi"
    And j'ai enregistré le pronostic "77" de cote "1.77" et de mise "10.00" "EURO" dans le projet "2222"
    When je tente de publier le pronostic "77" dans le projet "2222"
    Then je vérifie que la publication est effective

  Scenario: Enregistrer un pronostic dans un projet contenant un pronostic publié
    Given je suis un client authentifié en tant que "Massi"
    When je tente d'enregistrer le pronostic "30" et de cote "1.56" dans le projet "1111"
    Then je vérifie que l'enregistrement est effectif

  Scenario: Enregistrer un pronostic dans un projet contenant un pronostic enregistré
    Given je suis un client authentifié en tant que "Massi"
    When je tente d'enregistrer le pronostic "3" et de cote "1.56" dans le projet "1111"
    Then je vérifie que l'enregistrement est effectif


  Scenario: Accepter l'enregistrement d'un pronostic avec une cote < 1,10
    """
    Il est possible d'enregistrer un brouillon avec une cote < 1,10
    """
    Given je suis un client authentifié en tant que "Massi"
    When je tente d'enregistrer le pronostic "9411" et de cote "1.08" dans le projet "1111"
    Then je vérifie que l'enregistrement est effectif

  Scenario: Rejeter la publication d'un pronostic avec une cote < 1,10
    """
    Il n'est pas autorisé de publier un pronostic avec une cote < 1,10
    """
    Given je suis un client authentifié en tant que "Massi"
    When je tente de publier le pronostic "25" dans le projet "3333"
    Then je vérifie qu'une erreur est remontée

  Scenario: Accepter l'enregistrement d'un pronostic avec une mise excédent la bankrol du projet
    Given je suis un client authentifié en tant que "Massi"
    And j'ai créé un nouveau pronostic "882" de cote "1.55" de mise "110.00" "EURO"
    When je tente d'enregistrer le pronostic "882" dans le projet "1234"
    Then je vérifie que l'enregistrement est effectif

  Scenario: Rejeter la publication d'un pronostic avec une mise excédent la bankrol du projet
    Given je suis un client authentifié en tant que "Massi"
    And j'ai créé un nouveau pronostic "882" de cote "1.55" de mise "110.00" "EURO"
    When je tente de publier le pronostic "882" dans le projet "1234"
    Then je vérifie qu'une erreur est remontée

