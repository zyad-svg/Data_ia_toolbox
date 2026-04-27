# Data & IA Toolbox

Application desktop JavaFX regroupant 6 modules autour de la data et de l'intelligence artificielle. Chaque module illustre un concept (NLP, statistiques, recommandation, chatbot, IA de jeu, scoring) avec une interface graphique moderne et une persistance SQLite.

---

## Modules

| # | Module | Description |
|---|--------|-------------|
| 1 | **Analyse de texte** | Comptage mots/phrases/caractères, analyse de sentiment (positif/neutre/négatif), top 10 des mots les plus fréquents, fréquence des lettres. Import de fichiers `.txt` supporté. |
| 2 | **Analyse météo** | Statistiques descriptives (moyenne, min, max, écart-type) sur température, humidité et vent. Détection automatique d'anomalies (> 2 écarts-types). Import CSV ou jeu de démo intégré. |
| 3 | **Recommandation de films** | Import d'un catalogue CSV (détection auto du séparateur et des colonnes), recherche par titre, filtre par genre, recommandation des 5 films les plus similaires (indice de Jaccard). |
| 4 | **Chatbot** | Assistant conversationnel avec base de connaissances extensible, reconnaissance de mots-clés et gestion des questions sans réponse. |
| 5 | **Morpion (Tic-Tac-Toe)** | Jeu contre une IA avec plusieurs niveaux de difficulté. Statistiques de victoires/défaites sauvegardées. |
| 6 | **Scoring crédit** | Simulation de scoring bancaire basé sur l'âge, revenus, charges, situation professionnelle et historique. Historique des évaluations consultable. |

---

## Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Java (JDK) | 17 |
| Maven | 3.8+ |

> Les dépendances (JavaFX 21, SQLite JDBC) sont gérées automatiquement par Maven.

---

## Lancement rapide (Terminal)

```bash
# Cloner le projet
git clone <url-du-repo>
cd Data_ia_toolbox

# Lancer l'application
mvn javafx:run
```

La base SQLite (`toolbox.db`) est créée automatiquement à la racine du projet au premier lancement.

---

## Lancement avec IntelliJ IDEA

### 1. Ouvrir le projet

- **File → Open** → sélectionner le dossier `Data_ia_toolbox`
- IntelliJ détecte automatiquement le `pom.xml` et importe le projet Maven

### 2. Configurer le JDK

- **File → Project Structure → Project**
- Sélectionner un JDK 17+ (télécharger via IntelliJ si besoin : **Add SDK → Download JDK**)

### 3. Lancer l'application

**Option A — Via Maven (recommandé) :**
- Ouvrir le panneau **Maven** (barre latérale droite)
- Naviguer dans **Plugins → javafx → javafx:run**
- Double-cliquer pour lancer

**Option B — Via une Run Configuration :**
1. **Run → Edit Configurations → + → Application**
2. Remplir :
   - **Name** : `Data & IA Toolbox`
   - **Main class** : `com.toolbox.Main`
   - **VM options** :
     ```
     --add-modules javafx.controls,javafx.fxml
     ```
   - **Working directory** : le dossier racine du projet
3. Cliquer sur **Run**

> Si l'erreur `JavaFX runtime components are missing` apparait, utiliser l'Option A (Maven) ou vérifier les VM options.

---

## Lancement avec VS Code

### 1. Extensions requises

Installer ces extensions depuis le marketplace :
- **Extension Pack for Java** (Microsoft)
- **Maven for Java** (Microsoft)

### 2. Ouvrir le projet

- **File → Open Folder** → sélectionner le dossier `Data_ia_toolbox`
- VS Code détecte le `pom.xml` et configure le classpath automatiquement

### 3. Lancer l'application

**Option A — Via le terminal intégré (recommandé) :**

Ouvrir le terminal (`Ctrl+ù` ou **Terminal → New Terminal**) et exécuter :

```bash
mvn javafx:run
```

**Option B — Via une launch configuration :**

Créer le fichier `.vscode/launch.json` à la racine du projet :

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Data & IA Toolbox",
      "request": "launch",
      "mainClass": "com.toolbox.Main",
      "vmArgs": "--add-modules javafx.controls,javafx.fxml",
      "projectName": "toolbox"
    }
  ]
}
```

Puis appuyer sur **F5** ou cliquer sur **Run and Debug**.

> **Note :** Si JavaFX n'est pas résolu par VS Code, privilégier l'Option A (`mvn javafx:run`) qui gère tout automatiquement.

---

## Structure du projet

```
Data_ia_toolbox/
├── pom.xml                              # Configuration Maven + dépendances
├── toolbox.db                           # Base SQLite (générée au runtime)
└── src/main/
    ├── java/com/toolbox/
    │   ├── Main.java                    # Point d'entrée
    │   ├── MainApp.java                 # Application JavaFX (fenêtre principale)
    │   ├── MainController.java          # Navigation entre les 6 modules
    │   ├── AppLauncher.java             # Launcher alternatif
    │   ├── database/
    │   │   └── DatabaseManager.java     # Connexion SQLite singleton
    │   ├── module1/
    │   │   └── Module1Controller.java   # Analyse de texte
    │   ├── module2/
    │   │   └── Module2Controller.java   # Analyse météo
    │   ├── module3/
    │   │   └── Module3Controller.java   # Recommandation films
    │   ├── module4/
    │   │   ├── Module4Controller.java   # Chatbot
    │   │   └── Module4Knowledge.java    # Base de connaissances
    │   ├── module5/
    │   │   ├── Module5Controller.java   # Morpion IA
    │   │   ├── TicTacToeLogic.java      # Logique de jeu / IA
    │   │   └── GameDao.java             # Persistance des parties
    │   └── module6/
    │       └── Module6Controller.java   # Scoring crédit
    └── resources/
        ├── config.properties            # Configuration
        └── com/toolbox/
            ├── main.fxml                # Layout principal
            ├── main.css                 # Styles globaux
            ├── module1/
            │   ├── Module1View.fxml
            │   └── module1.css
            ├── module2/
            │   ├── Module2View.fxml
            │   └── module2.css
            ├── module3/
            │   ├── Module3View.fxml
            │   └── module3.css
            ├── module4/
            │   ├── Module4View.fxml
            │   └── module4.css
            ├── module5/
            │   └── Module5View.fxml
            └── module6/
                ├── Module6View.fxml
                └── module6.css
```

---

## Stack technique

| Composant | Rôle |
|-----------|------|
| **Java 17** | Langage |
| **JavaFX 21** | Interface graphique (FXML + CSS) |
| **SQLite** (sqlite-jdbc) | Base de données locale embarquée |
| **Maven** | Build, dépendances, lancement |

---

## Résolution de problèmes

| Problème | Solution |
|----------|----------|
| `JavaFX runtime components are missing` | Lancer avec `mvn javafx:run` au lieu de Run direct |
| `ClassNotFoundException: javafx.*` | Vérifier que le JDK 17+ est configuré et que Maven a bien téléchargé les dépendances (`mvn clean install`) |
| La fenêtre ne s'affiche pas | Vérifier qu'aucun autre processus ne bloque le port graphique. Sur Linux, vérifier que `DISPLAY` est défini |
| Erreur SQLite au démarrage | Vérifier les droits d'écriture dans le dossier du projet (le fichier `toolbox.db` doit pouvoir être créé) |
