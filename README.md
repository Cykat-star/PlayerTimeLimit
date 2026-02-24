# 🕒 PlayerTimeLimit (Hybrid SQL Edition)
> **High-performance playtime management for modern Minecraft networks.**

PlayerTimeLimit is a robust solution for server owners who need to restrict daily playtime. This version is a modernized fork of the original plugin, rebuilt to handle high-traffic environments and cross-server synchronization.

[![Version](https://img.shields.io/badge/version-2.0.0-blue.svg)](CHANGELOG.md)

### 🌟 Key Use Cases

#### 1. Tiered Access & VIP Rewards
The plugin is designed to work natively with your existing permission system (like [LuckPerms](https://luckperms.net/)). You can create custom time limits for different ranks:
* **Trial Users:** Provide a limited window of daily playtime for new players.
* **Supporters:** Reward your VIP tiers with extended or unlimited playtime as a perk for their contribution to the server.
* **Network Sync:** Using MySQL, a player's time follows them across your entire server network (Hub, Survival, Skyblock, Creative, Adventure, etc).

#### 2. Parental Management & Digital Wellbeing
For parents running private family servers, **PlayerTimeLimit** acts as an automated "digital curfew."
* **Daily Allowance:** Set a hard cap on daily gaming hours to ensure a healthy balance between screen time and other activities.
* **Automatic Enforcement:** The plugin handles the warnings and kicks automatically, removing the need for manual monitoring.
* **Consistency:** Because data is saved to a database, play logs are persistent even if the server is restarted.

---

## 💎 Why the SQL Edition?

This fork was developed to solve critical scaling and networking limitations while providing a cleaner, more professional codebase:

* **Eliminating File Clutter:** The original plugin creates a unique `.yml` file for every single player (one per UUID). On large servers, this results in thousands of files, slowing down disk I/O and making backups difficult. This version uses a **single database file (H2)** or **remote server (MySQL)**.
* **True Network Support:** Original flat-file systems are locked to a single server. This **Hybrid SQL engine** allows a BungeeCord or Velocity network to share a single database, so a player's time limit follows them across the entire network.
* **Modern Refactor & Cleanup:** The entire project directory structure has been reorganized for better maintainability. The codebase is fully refactored into English and optimized with asynchronous batch-saving to ensure **Zero TPS impact**.
* **Enhanced Implementation:** While granular control via permissions existed previously, this edition makes those systems more reliable and performant, whether you are managing **Tiered VIP Access** via [LuckPerms](https://luckperms.net/) or setting a **Digital Curfew** for a family-run server.

---

## 🚀 Key Features

* **Hybrid Database Engine**
    * **Local Mode (SQLite):** High-speed SQL storage for single servers. No external setup required. Data is stored in `plugins/PlayerTimeLimit/playerdata/`.
    * **Network Mode (MySQL):** Perfect for multi-server networks (BungeeCord & Velocity compatible).
* **Smart HUD Integration:** Real-time **ActionBar** or **BossBar** notifications with player toggles.
* **Dynamic Limits:** Assign unique daily limits via Vault groups or permissions.
* **Automated Resets:** Configure a global daily reset time (e.g., `00:00`).

---

## 🛠️ Building from Source

If you want to compile the plugin yourself or contribute to the code, follow these steps.

### Prerequisites
* **Java 17 JDK** or higher.
* **Maven** installed and added to your system PATH.

### Build Instructions
Run the following commands in your terminal:

    git clone https://github.com/Cykat-star/PlayerTimeLimit.git
    cd PlayerTimeLimit
    mvn clean package

### 📦 Where is my JAR?
Once the build is successful, navigate to the `/target/` directory. You will see two files:

| File Name | Description | Use this? |
| :--- | :--- | :--- |
| **`PlayerTimeLimit-2.0.0.jar`** | **Shaded/Fat JAR.** Contains all database drivers (MySQL/SQLite. | ✅ **YES** |
| `original-PlayerTimeLimit-2.0.0.jar` | Lightweight JAR. Contains only your, no drivers. | ❌ **NO** |

> **Note:** If you use the `original` version, the plugin will crash with a `ClassNotFoundException` because it won't be able to find the SQL libraries.

---

## 📂 Installation

1. Place `PlayerTimeLimit.jar` in your `plugins` folder.
2. **Restart** your server to generate the default configuration.
3. Choose your database type in `config.yml`:
    * For **Local**, set `database.type: "SQLite"`.
    * For **Networks**, set `database.type: "MYSQL"` and enter your credentials.
4. Run `/ptl reload` to apply changes.

---

## 🛠 Commands & Permissions

| Command | Alias | Description | Permission |
| :--- | :--- | :--- | :--- |
| `/ptl info` | | View time until the global reset. | `playertimelimit.command.info` |
| `/ptl check [p]` | | Check time remaining for yourself/others. | `playertimelimit.command.check` |
| `/ptl message` | | Toggle Action/BossBar notifications. | `playertimelimit.command.message` |
| `/ptl resettime` | | Manually reset a player's daily time. | `playertimelimit.admin` |
| `/ptl reload` | | Reload all configurations and tasks. | `playertimelimit.admin` |

---

## ⚙️ Configuration

The plugin uses a hierarchical permission system for time limits. You can create as many groups as you want in the `config.yml`.

### How Time Limits Work:
The plugin checks for the permission node: `playertimelimit.limit.<name>`
* **Default:** Players with no specific permission get the `default` limit.
* **VIP:** Assign the permission `playertimelimit.limit.vip` using LuckPerms, GroupManager, etc.
* **Unlimited:** Set time to `0` (as seen in the `op` group) for unlimited playtime.

---

## 🛠 Resilience & Maintenance

* **Auto-Recovery:** If the `config.yml` is accidentally deleted or corrupted, the plugin will automatically recreate the default configuration upon the next startup or reload.
* **Instant Reloading:** Use `/ptl reload` to apply changes to your configuration, time limits, or database settings without restarting the server.
* **Safe Saving:** Even if the server crashes, the plugin’s asynchronous batch-saving system minimizes data loss by pushing player times to the database every 5 minutes (configurable).

---

## 📜 Credits & Acknowledgements

* **Original Author:** [Ajneb97](https://github.com/Ajneb97) - This project is a specialized fork of the original [PlayerTimeLimit](https://www.spigotmc.org/resources/playertimelimit-1-8-1-19.96577/).
* **SQL Refactor & Fork:** [Cykat-star](https://github.com/Cykat-star)
* **Libraries:** [HikariCP](https://github.com/brettwooldridge/HikariCP) (Pooling), [H2 Database](http://www.h2database.com/) (Local SQL).

---
© 2026 Cykat-star | Optimized for Spigot, Paper, and Purpur.
