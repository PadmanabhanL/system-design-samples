# Logging Framework - Low Level Design (LLD)

This document explains the architectural and low-level design choices made when building the miniature Java logging framework.

## 1. Core Components

The framework is divided into several key components to ensure a clean separation of concerns:

- **`LogLevel`**: Enum defining the severity of the log.
- **`LogMessage`**: A value object encapsulating the details of a single log event.
- **`LogAppender`**: An interface representing a destination for the logs.
- **`Logger`**: The core class responsible for filtering and routing messages to appenders.
- **`LogManager`**: A central registry and factory for managing `Logger` instances.

---

## 2. Design Patterns Used

### 2.1. Singleton Pattern (`LogManager`)
- **Choice:** The `LogManager` is implemented as a thread-safe Singleton using double-checked locking and the `volatile` keyword.
- **Reason:** A logging framework needs a single, global registry to keep track of all logger configurations across an application. We don't want multiple managers maintaining separate configurations or creating duplicate loggers for the same class.

### 2.2. Factory / Flyweight Pattern (`LogManager.getLogger`)
- **Choice:** `LogManager` acts as a factory for `Logger` instances, caching them in a `ConcurrentHashMap`.
- **Reason:** In a large application, the same `getLogger(MyClass.class)` will be called many times. By caching loggers by name, we ensure we return the exact same instance. This saves memory (Flyweight) and ensures that if a logger's configuration (like its log level) is changed dynamically, that change is instantly reflected everywhere that logger is used.

### 2.3. Strategy Pattern (`LogAppender`)
- **Choice:** The destination of the log output is abstracted behind the `LogAppender` interface. 
- **Reason:** This makes the framework infinitely extensible. The `Logger` doesn't need to know *how* to write to a console, a file, or a database. It simply iterates through its attached `LogAppender` strategies and calls `.append()`. You can easily create a `FileAppender` without modifying existing code (Open-Closed Principle).

### 2.4. Value Object Pattern (`LogMessage`)
- **Choice:** Creating an immutable `LogMessage` object that bundles the level, message, timestamp, and thread name.
- **Reason:** Instead of passing 4 or 5 different parameters to the `append` method, we pass a single object. This keeps the `LogAppender` interface clean. If we later decide to add a `StackTrace` or `ContextID` to our logs, we only need to modify `LogMessage`, and the `LogAppender` interface remains unbroken.

---

## 3. Concurrency and Thread Safety

- **`ConcurrentHashMap` in `LogManager`:** Applications are inherently multi-threaded. Multiple threads might try to initialize a logger simultaneously. Using `ConcurrentHashMap` combined with `computeIfAbsent` ensures thread-safe, atomic creation and retrieval of loggers without heavy locking overhead.
- **`volatile` and Double-Checked Locking:** Used in the `LogManager` singleton initialization to prevent race conditions during the very first access while maintaining high performance on subsequent accesses.
- **Immutable LogEvents:** By capturing the timestamp and thread name at the exact moment the `LogMessage` is instantiated (and making the fields `final`), we ensure that even if the formatting and writing to disk happens asynchronously on a background thread later, the data reflects the exact state of when the log was triggered.

---

## 4. Performance Considerations

- **Fast Filtering:** The `Logger` class immediately checks `if (logLevel.getLevel() >= this.level.getLevel())` before doing any work. This prevents the framework from instantiating `LogMessage` objects or formatting strings if the log isn't going to be output anyway, which is crucial for high-performance applications that might have thousands of `logger.debug()` calls that are disabled in production.
