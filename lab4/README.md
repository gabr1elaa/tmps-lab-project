# Lab 4 - Behavioral Design Patterns

**Project:** myTasks - a personal task manager (console application)\
**Author:** Gabriela Bîtca, FAF-242\
**Language:** Java 21 **Build tool:** Maven

myTasks is a console task manager with an Eisenhower matrix (urgent / important) that saves tasks to a file (Lab 3). In this lab it gets a task **workflow**, **undo / redo** and **reactions to task events**, using three behavioral patterns: **State**, **Command** and **Observer**.

The full report is in **[REPORT.md](REPORT.md)**.

## Requirements

- Java 21 or newer
- Maven (the one built into IntelliJ IDEA is enough)
- Internet the first time, so Maven can download **Gson** and **JUnit**

## Build

Open the `tmps-lab-project` folder in IntelliJ IDEA. It loads as a Maven project; click **Load Maven changes** .

From a terminal in the `tmps-lab-project` folder:

```bash
mvn -pl lab4 compile
```

## Run

| What | How |
|---|---|
| **The app** | Open `lab4/src/main/java/com/mytasks/Main.java` and press ▶ |
| **The app, with screen clearing** | Open `lab4/run.sh` and press ▶ next to the first line |
| **The pattern demo** (non-interactive) | Open `lab4/src/main/java/com/mytasks/Demo.java` and press ▶ |

New menu options in this lab:

| Option | What it does | Pattern |
|---|---|---|
| 4 / 5 / 6 | Start / Complete / Reopen a task (only the allowed steps work) | State |
| 10 / 11 | Undo / Redo the last action | Command |
| 12 | Activity log of this session | Observer |
| 13 | Settings: switch **auto-save** and **notifications** on or off while the app runs | Observer |

Tasks are saved to `tasks.json` in the working folder (by the auto-save observer), with the previous version in `tasks.json.bak`.

## Tests

One command, from the `tmps-lab-project` folder:

```bash
mvn -pl lab4 test
```

Or in IntelliJ: Maven tool window → **lab4 → Lifecycle → test**, or right-click `lab4/src/test/java` → **Run 'All Tests'**.

## Project structure

```
lab4/src/main/java/com/mytasks/
├── Main.java              the app: wires everything and starts the console
├── Demo.java              runnable demo of the three patterns
├── model/                 Task (State context), TaskStatus, TaskDeadline, TaskBuilder, Quadrant
├── state/                 TaskState, NotStartedState, InProgressState, CompletedState   (State)
├── command/               Command, CommandHistory, Add/Start/Complete/Reopen/Delete/RepeatTaskCommand (Command)
├── observer/              TaskObserver, TaskEventPublisher, ActivityLog/AutoSave/NotificationObserver (Observer)
├── service/               TaskService (receiver of the commands, publishes events)
├── repository/            TaskRepository
├── validation/            TaskValidator
├── filter/                TaskFilter and its implementations
├── storage/, adapter/, decorator/, facade/   saving to a file (Lab 3)
├── util/                  IdGenerator (Singleton, Lab 2)
└── ui/                    ConsoleUI

lab4/src/test/java/com/mytasks/
├── state/TaskStateTest.java
├── command/CommandHistoryTest.java
├── observer/TaskEventPublisherTest.java
├── PatternInteractionTest.java
└── adapter/, decorator/, facade/   the Lab 3 tests, still passing
```
