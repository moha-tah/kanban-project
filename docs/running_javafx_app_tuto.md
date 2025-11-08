# Guide to run the JavaFX application with VS Code

This guide will help you configure and run the Kanban JavaFX application in VS Code, step by step.

---

## Prerequisites

Before starting, make sure you have installed:

1. **Java JDK 17** (or higher)
   - Download from: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
   - Check the installation: `java -version` in a terminal
   - The version must be **17 or higher**

2. **Maven 3.6+**
   - Download from: https://maven.apache.org/download.cgi
   - Check the installation: `mvn -version` in a terminal

---

## Installation of the VS Code extensions

Open VS Code and install the following extensions (via `Ctrl+Shift+X` or `Cmd+Shift+X` on Mac):

### Required extensions:
1. **Extension Pack for Java** (by Microsoft)
   - Includes: Language Support for Java, Debugger for Java, Test Runner for Java, Maven for Java, etc.
   - This is the main extension that installs everything you need

2. **JavaFX** (optional, but recommended)
   - Help for the JavaFX syntax

3. Maven for Java (by Microsoft)
   - Help for the Maven syntax

---

## Configuration of the project (.vscode)

The `.vscode` directory contains the project-specific configurations. **These files are already created for you** in the `.vscode/` directory at the root of the project.

### Structure of the .vscode directory:
```
.vscode/
├── settings.json       # Configuration Java, Maven, etc.
├── launch.json         # Configuration for debugging
└── tasks.json          # Maven tasks (compile, run, etc.)
```

### `.vscode/settings.json` file
This file configures:
- The JDK used (Java 17)
- The Maven path
- The Java compiler options
- The necessary JavaFX modules

### `.vscode/launch.json` file
This file allows to:
- Run the application in debug mode
- Configure the VM arguments for JavaFX
- Define the main class (`com.kanban.App`)

### `.vscode/tasks.json` file
This file defines Maven tasks:
- `maven: compile` - Compile the project
- `maven: javafx:run` - Run the JavaFX application
- `maven: clean` - Clean the project

---

## First execution

### Method 1: Via the command palette (Recommended)

1. **Open the project** in VS Code:
   ```bash
   # From the terminal, navigate to the project directory
   cd path/to/kanban-project
   code .
   ```

2. **Wait for VS Code to index the project**
   - VS Code will automatically download the Maven dependencies
   - Look at the status bar at the bottom (progress of the indexing)
   - This can take a few minutes the first time

3. **Open the main class** :
   - Navigate to `src/main/java/com/kanban/App.java`
   - Click on the file

4. **Run the application** :
   - Press `F5` or `Ctrl+F5` (or `Cmd+F5` on Mac)
   - Or : `Ctrl+Shift+P` → Type "Java: Run" → Select "Run Java"
   - Or : Click on the "Run" button above `public static void main`

### Method 2: Via the integrated terminal

1. **Open the integrated terminal** :
   - `Ctrl+`` (backtick) ou `Terminal → New Terminal`

2. **Compile and run with Maven** :
   ```bash
   mvn clean javafx:run
   ```

---

## Checking the installation

### Check Java
```bash
java -version
# Should display something like: openjdk version "17.x.x" or java version "17.x.x"
```

### Check Maven
```bash
mvn -version
# Should display the Maven and Java version
```

### Check that VS Code recognizes Java
1. Open `App.java`
2. Look at the bottom right of VS Code: you should see "Java 17" or similar
3. If you see an error message, click on it to see the details

---

## Useful Maven commands

### Compile the project
```bash
mvn clean compile
```

### Run the application
```bash
mvn javafx:run
```

### Compile and run in one command
```bash
mvn clean javafx:run
```

### Clean the project (delete the target/ directory)
```bash
mvn clean
```

### Download the dependencies
```bash
mvn dependency:resolve
```

### Run the tests
```bash
mvn test
```

### Create an executable JAR
```bash
mvn clean package
# Le JAR sera créé dans target/kanban-project-1.0-SNAPSHOT.jar
```

---

## Troubleshooting

### Error: "Java runtime could not be located"

**Solution :**
1. Open the VS Code settings: `Ctrl+,`
2. Search for: `java.configuration.runtimes`
3. Add in `settings.json` :
   ```json
   {
     "java.configuration.runtimes": [
       {
         "name": "JavaSE-17",
         "path": "C:\\Program Files\\Java\\jdk-17",
         "default": true
       }
     ]
   }
   ```
   ⚠️ Replace the path with your own Java installation

4. Restart VS Code

### Error: "Error: JavaFX runtime components are missing"

**Solution :**
This error should not occur because JavaFX is included in the Maven dependencies. If it happens:
1. Check that `pom.xml` contains the JavaFX dependencies
2. Run: `mvn clean install`
3. Restart VS Code

### Error: "Maven wrapper not found"

**Solution :**
1. Install Maven globally (see prerequisites)
2. Or configure the Maven path in VS Code:
   - `Ctrl+,` → Search `maven.executable.path`
   - Add the path to `mvn` (ex: `C:\Program Files\Apache\maven\bin\mvn`)

### Error: "The application does not launch (window does not open)"

**Checks :**
1. Check the logs in the integrated terminal of VS Code
2. Check that the main class is well `com.kanban.App`
3. Try to launch from the terminal: `mvn javafx:run`
4. Check that JavaFX is well installed: the dependencies in `pom.xml`

### Errors of compilation (red squiggles)

**Solution :**
1. Wait for VS Code to finish indexing (status bar at the bottom)
2. If the errors persist:
   ```bash
   mvn clean compile
   ```
3. Reload the VS Code window: `Ctrl+Shift+P` → "Developer: Reload Window"

### Error: "The dependencies do not download"

**Solution :**
1. Check your internet connection
2. Force the download:
   ```bash
   mvn dependency:resolve -U
   ```
3. Check the proxy settings if you are on a corporate network

### Error: "Path problem (Windows with spaces)"

**Solution :**
If your project is in a path with spaces (ex: `C:\Users\Mon Nom\Documents\...`), this can cause problems. Move the project in a path without spaces.

---

## Project structure

To better understand the project:

```
kanban-project/
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── com/kanban/
│   │       │   └── App.java          ← Main class (entry point)
│   │       ├── client/               ← Client code
│   │       ├── server/               ← Server code
│   │       └── common/               ← Shared code
│   └── resources/                    ← Resources (images, FXML, CSS, etc.)
├── target/                           ← Compilation directory (generated)
├── pom.xml                           ← Maven configuration
└── .vscode/                          ← VS Code configuration
```

---

## Need help?
Contact Martin Halbourg on Discord (halbourg.martin@gmail.com)

