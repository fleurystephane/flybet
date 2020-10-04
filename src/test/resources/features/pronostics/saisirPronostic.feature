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
        | ABC    | Massi           |
        | DEF    | Joe             |

      Given des projets existent
        | customerId   | projectId   | title           | bankrol  | objectif               | endDate     |
        | ABC          | 1111        | fun             | 200.00   | Atteindre 400.00 euros | 2019/12/25  |
        | ABC          | 2222        | global          | 100.00   | Atteindre 200.00 euros |             |
        | ABC          | 3333        | Gestion de BK   | 1000.00  |                        |             |

      Given des pronostics existent:
        | project      | pronoId  | cote   | status       |
        | 1111         | A1       | 1.56   | PUBLISHED    |
        | 1111         | A2       | 1.75   | DRAFT        |
        | 3333         | C0       | 2.33   | DRAFT        |
        | 3333         | E0       | 1.09   | DRAFT        |


Scenario: Publier un pronostic dans un projet alors qu'un pronostic est déjà publié dans ce projet
  Given je suis un client authentifié en tant que "Massi"
  And j'ai créé un nouveau pronostic "A3" de cote "1.55"
  When je tente de publier le pronostic "A3" dans le projet "1111"
  Then je vérifie qu'une erreur est remontée

  Scenario: Publier un pronostic dans un projet alors qu'un pronostic est déjà publié dans un autre projet.

    Given je suis un client authentifié en tant que "Massi"
    When je tente de publier le pronostic "A2" dans le projet "2222"
    Then je vérifie que la publication est effective

  Scenario: Publier un pronostic enregistré comme brouillon dans un projet
    Passage de brouillon à publié

    Given je suis un client authentifié en tant que "Massi"
    And j'ai enregistré le pronostic "B0" de cote "1.77" dans le projet "2222"
    When je tente de publier le pronostic "B0" dans le projet "2222"
    Then je vérifie que la publication est effective

  Scenario: Enregistrer un pronostic dans un projet contenant un pronostic publié
    Given je suis un client authentifié en tant que "Massi"
    When je tente d'enregistrer le pronostic "A3" et de cote "1.56" dans le projet "1111"
    Then je vérifie que l'enregistrement est effectif

  Scenario: Enregistrer un pronostic dans un projet contenant un pronostic enregistré
    Given je suis un client authentifié en tant que "Massi"
    When je tente d'enregistrer le pronostic "A3" et de cote "1.56" dans le projet "1111"
    Then je vérifie que l'enregistrement est effectif


  Scenario: Accepter l'enregistrement d'un pronostic avec une cote < 1,10
    """
    Il est possible d'enregistrer un brouillon avec une cote < 1,10
    """
    Given je suis un client authentifié en tant que "Massi"
    When je tente d'enregistrer le pronostic "E1" et de cote "1.08" dans le projet "1111"
    Then je vérifie que l'enregistrement est effectif

  Scenario: Rejeter la publication d'un pronostic avec une cote < 1,10
    """
    Il n'est pas autorisé de publier un pronostic avec une cote < 1,10
    """
    Given je suis un client authentifié en tant que "Massi"
    When je tente de publier le pronostic "E0" dans le projet "3333"
    Then je vérifie qu'une erreur est remontée


