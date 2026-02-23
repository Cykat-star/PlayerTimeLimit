# Changelog

All notable changes to **PlayerTimeLimit** will be documented in this file.

## [2.0.0] - 2026-02-22
### 🚨 Major Architectural Changes
* **SQL Integration:** Migrated from `config.yml` flat-file data storage to a hybrid **MySQL/H2** database system.
* **Namespace Rebrand:** Refactored entire codebase to the `com.cykatstar.playertimelimit` package.
* **Developer Fork:** Official transition from the original v1.6.1 source to the Cykat-star modernized edition.

### ✨ Added
* **H2 Support:** Local SQL storage is now the default, providing better performance and data integrity over YAML.
* **MySQL Support:** Added ability to sync player playtime across multiple servers in a network.
* **HikariCP:** Integrated connection pooling for efficient database management.
* **Async Data Handling:** All database reads and writes now occur off the main server thread to prevent lag.
* **Safety Net:** Added `saveAllPlayers()` logic to `onDisable` to prevent data loss during server restarts.

### 🔧 Fixed/Improved
* Translated all internal logic and default config comments to English.
* Updated Maven dependencies to support Java 17+ and Spigot 1.20.4+.
* Optimized the `DataSaveTask` to use configurable save intervals.

---
*Note: This version is a breaking change from 1.6.x. Data from the old config-based storage must be manually migrated or reset.*