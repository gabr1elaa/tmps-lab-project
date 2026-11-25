# Lab 2 - Structural Design Patterns

**Project:** myTasks - a personal task manager (console application)\
**Author:** Gabriela Bîtca, FAF-242\
**Language:** Java 21 **Build tool:** Maven

myTasks is a console task manager with an Eisenhower matrix (urgent / important). In this lab, tasks are **saved to a file** and loaded again on the next start, using three structural patterns: **Adapter**, **Decorator** and **Facade**.

The full report is in **[REPORT.md](REPORT.md)**.

## Requirements

- Java 21 or newer
- Maven (the one built into IntelliJ IDEA is enough)
- Internet the first time, so Maven can download **Gson** and **JUnit**

## Build

Open the `tmps-lab-project` folder in IntelliJ IDEA. It loads as a Maven project; click **Load Maven changes** (or Maven tool window Reload) so Gson is downloaded.

From a terminal in the `tmps-lab-project` folder:

```bash
mvn -pl lab3 compile
```

## Run

| What | How |
|---|---|
| **The app** | Open `lab3/src/main/java/com/mytasks/Main.java` and press ▶ |
| **The app, with screen clearing** | Open `lab3/run.sh` and press ▶ next to the first line |
| **The pattern demo** (non-interactive) | Open `lab3/src/main/java/com/mytasks/Demo.java` and press ▶ |

The app saves tasks to `tasks.json` in its working folder after every change, and keeps the previous version in `tasks.json.bak`. On the first run (no file yet), it creates four sample tasks.

## Tests

One command, from the `tmps-lab-project` folder:

```bash
mvn -pl lab3 test
```

Or in IntelliJ: Maven tool window → **lab3 → Lifecycle → test**, or right-click `lab3/src/test/java` → **Run 'All Tests'**.

## Project structure

```
lab3/src/main/java/com/mytasks/
├── Main.java              the app: wires everything and starts the console
├── Demo.java              runnable demo of the three patterns
├── model/                 Task, TaskDeadline, TaskBuilder, Quadrant, TaskStatus
├── util/                  IdGenerator (Singleton)
├── repository/            TaskRepository
├── service/               TaskService
├── validation/            TaskValidator
├── filter/                TaskFilter and its implementations
├── storage/               TaskStorage - the interface shared by the patterns
├── adapter/               GsonTaskStorage, TaskRecord          (Adapter)
├── decorator/             StorageDecorator, Backup..., Logging... (Decorator)
├── facade/                PersistenceFacade                    (Facade)
└── ui/                    ConsoleUI

lab3/src/test/java/com/mytasks/
├── adapter/GsonTaskStorageTest.java
├── decorator/StorageDecoratorTest.java
└── facade/PersistenceFacadeTest.java
```
