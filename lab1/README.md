# Lab 1 - SOLID Principles

**Project:** myTasks - a personal task manager
**Author:** _&lt;Gabriela Bîtca&gt;, &lt;FAF-242&gt;_
**Programming Language:** Java 21

---

## Objectives

- Get familiar with the SOLID principles.
- Build a small project that will be extended in all following labs.
- Apply 3 SOLID principles in that project: SRP, OCP and LSP.

## Domain

<!-- 3–5 sentences: what myTasks is and what a user can do with it:
     add a task (title + description), display all tasks, mark a task as completed,
     delete a task, view tasks filtered by completion status. -->

## Chosen SOLID principles

| Principle | Where it is applied |
|---|---|
| **S** - Single Responsibility | <!-- which classes, one job each --> |
| **O** - Open/Closed | <!-- the filter interface and its implementations --> |
| **L** - Liskov Substitution | <!-- the Task parent type and its subtypes --> |

---

## Starting point: the version that breaks SOLID

<!-- Paste a shortened version of your first TaskManager class
     and list what is wrong with it: how many reasons it has to change,
     the if/else filter chain, everything mixed in one place. -->

---

## Implementation

### Project structure

<!-- Paste the package tree of lab1/src/main/java/com/mytasks
     after the refactoring. -->

### 1. Single Responsibility Principle (SRP)

> A class should have only one reason to change.

<!-- Problem in the old version → what you split (task management, input validation,
     console interaction) → table "class | responsibility" → one short code snippet
     with its file path. -->

### 2. Open/Closed Principle (OCP)

> Software entities should be open for extension, but closed for modification.

<!-- Show the filter interface and one filter. Then show the extra filter
     (e.g. "title contains keyword") that was added without modifying any existing class. -->

### 3. Liskov Substitution Principle (LSP)

> Objects of a subtype must be usable wherever the parent type is expected, without breaking the program.

<!-- Show the Task parent type and the subtypes (e.g. SimpleTask, DeadlineTask).
     Show code that works with Task and behaves correctly for every subtype.
     Then describe what a violation would look like (e.g. a subtype whose
     complete() throws an exception or does not complete the task). -->

---

## Refactoring (bonus)

<!-- Before/after class diagram (IntelliJ: right-click package → Diagrams → Show Diagram,
     then export as PNG into lab1/images/) and/or a screenshot of the commit history.
     Example: ![After refactoring](images/diagram-after.png) -->

## Results

<!-- Screenshot(s) of the program running in IntelliJ, saved in lab1/images/.
     Example: ![Console output](images/console.png) -->

## Conclusion

<!-- 3–4 sentences: what changed after applying the principles and why it matters
     for the next labs. -->
