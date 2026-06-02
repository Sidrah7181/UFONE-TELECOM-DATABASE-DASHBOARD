# UFONE Telecom Network Simulation Dashboard

**FSC321 Database Systems — Spring 2026**

A dual-database desktop simulation dashboard built with Java Swing, PostgreSQL (via JDBC), and MongoDB (via Java Driver).

---

## Project Overview

This system simulates a telecom network control room. It demonstrates independent CRUD operations on two completely separate databases:

- **PostgreSQL (Supabase)** — owns all relational/core data
- **MongoDB Atlas** — owns all event, log, and simulation documents

The databases **never communicate**. The search layer aggregates read results in memory only.

---

## Architecture

```
Main.java
│
├── ui/           → Swing UI panels (MainFrame, SqlPanel, MongoPanel, SearchPanel, StatusBar)
├── db/           → Connection handlers (PostgresConn, MongoConn, DbConfig)
├── crud/sql/     → JDBC-based CRUD for PostgreSQL tables
├── crud/mongo/   → MongoDB Driver-based CRUD for collections
├── service/      → SearchService (in-memory aggregation only)
├── model/        → POJOs for SQL and Mongo entities
├── util/         → SoundPlayer, TableBuilder
└── assets/       → ufone_logo.png, ufone_intro.wav, click.wav
```

---

## SQL Ownership (PostgreSQL)

PostgreSQL handles FULL CRUD for:
- `region` — telecom regions
- `tower` — cell towers per region
- `user_account` — subscriber accounts
- `connection_log` — per-session connection records
- `network_status` — tower load snapshots

---

## MongoDB Ownership

MongoDB handles FULL CRUD for:
- `event_log` — tower/user events
- `anomaly_log` — detected anomalies with risk levels
- `cyber_threat` — attack simulations with status tracking

---

## No-Sync Architecture

```
SQL Database  ←——————————————————→  MongoDB
     ↓                                   ↓
SqlRegionCRUD                    MongoEventCRUD
SqlTowerCRUD                     MongoAnomalyCRUD
SqlUserCRUD                      MongoCyberThreatCRUD
     ↓                                   ↓
     └──────→ SearchService ←────────────┘
                    ↓
              List<SearchResult>   (in memory only)
                    ↓
              SearchPanel (UI)
```

**SearchService does not merge databases — it merges perceptions of databases.**

---

## Setup Guide

### Prerequisites
- Java 17+
- PostgreSQL JDBC Driver (`postgresql-42.x.x.jar`)
- MongoDB Java Driver (`mongodb-driver-sync-4.x.x.jar` + `bson.jar`)

### Dependencies (add to classpath or build tool)
```
postgresql-42.7.3.jar
mongodb-driver-sync-4.11.0.jar
bson-4.11.0.jar
mongodb-driver-core-4.11.0.jar
```

### Maven (pom.xml) — if using Maven
```xml
<dependencies>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.3</version>
    </dependency>
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>4.11.0</version>
    </dependency>
</dependencies>
```

### Database Credentials (already set in DbConfig.java)
- **PostgreSQL**: `db.npqtuhpfvqoklrscywoy.supabase.co:5432/postgres`
- **MongoDB**: `telecomcluster.ehql8ts.mongodb.net / Telecom_network_simulation`

---

## How to Run

### Option A — IntelliJ IDEA
1. Open project root as IntelliJ project
2. Add JAR dependencies to Project Structure → Libraries
3. Mark `src/` as Sources Root
4. Run `Main.java`

### Option B — Command Line
```bash
# Compile (adjust classpath separator for Windows: use ; instead of :)
javac -cp "lib/*" -d out $(find src -name "*.java")

# Run
java -cp "out:lib/*" Main
```

### Option C — VS Code
1. Install Extension Pack for Java
2. Add JARs to `.classpath`
3. Run `Main.java`

---

## File Hierarchy

```
src/
├── Main.java
├── ui/
│   ├── MainFrame.java
│   ├── SqlPanel.java
│   ├── MongoPanel.java
│   ├── SearchPanel.java
│   └── StatusBar.java
├── db/
│   ├── DbConfig.java
│   ├── PostgresConn.java
│   └── MongoConn.java
├── crud/
│   ├── sql/
│   │   ├── SqlRegionCRUD.java
│   │   ├── SqlTowerCRUD.java
│   │   ├── SqlUserCRUD.java
│   │   └── SqlConnectionLogCRUD.java
│   └── mongo/
│       ├── MongoEventCRUD.java
│       ├── MongoAnomalyCRUD.java
│       └── MongoCyberThreatCRUD.java
├── service/
│   └── SearchService.java
├── model/
│   ├── sql/
│   │   ├── Region.java
│   │   ├── Tower.java
│   │   ├── User.java
│   │   └── ConnectionLog.java
│   └── mongo/
│       ├── EventLog.java
│       ├── Anomaly.java
│       └── CyberThreat.java
├── util/
│   ├── SoundPlayer.java
│   └── TableBuilder.java
└── assets/
    ├── ufone_logo.png      ← place your logo here
    ├── ufone_intro.wav     ← startup sound (2s max)
    └── click.wav           ← button click sound
```

---

## SQL CRUD Demo (via UI)
1. Click **SQL CORE** tab
2. Select table from dropdown: `region`, `tower`, `user_account`, `connection_log`
3. Click **VIEW** to load all rows
4. Fill in the form fields below the table
5. Click **INSERT** / **UPDATE** (select row first) / **DELETE** (select row first)

## MongoDB CRUD Demo (via UI)
1. Click **MONGO EVENTS** tab
2. Select collection: `event_log`, `anomaly_log`, `cyber_threat`
3. Click **VIEW DOCS** to see JSON cards
4. Fill form fields and click **INSERT**
5. Click a card to select it (border turns green), then **UPDATE** or **DELETE**

## Search Demo
1. Click **SEARCH** tab
2. Type any keyword (e.g. `Ali`, `Critical`, `Tower`, `DDoS`)
3. Choose filter: ALL / SQL ONLY / MONGO ONLY
4. Results show with 🟩 green border (SQL) or 🟧 orange border (MongoDB)

---

## Assets
Place these files in `src/assets/`:
- `ufone_logo.png` — any PNG logo (56×56 recommended)
- `ufone_intro.wav` — short startup beep/jingle (< 2 seconds)
- `click.wav` — short click sound for button feedback

If files are missing, the app runs fine — sound is silently skipped and a fallback "U" logo is shown.

---

## Color Theme
| Purpose | Color |
|---------|-------|
| Header background | `#366500` |
| Ufone lime green | `#ABE300` |
| Accent orange (Mongo) | `#F78200` |
| Background | `#F4FCE0` |
| Dark text | `#2E2E2E` |
