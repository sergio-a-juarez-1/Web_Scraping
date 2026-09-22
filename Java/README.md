# NHL Event Data Scraper

A robust, console-based Java application designed to scrape, extract, and locally persist granular event data from web-based play-by-play reports. The application utilizes targeted stream parsing and string-indexing algorithms to isolate core features from unstructured HTML rows and exports them into structured CSV datasets.

---

## ✨ Features

* **Targeted Event Scraping:** Retrieves the precise n-th occurrence of specific data entries from game records.
* **Granular Extraction:** Slices data attributes directly from markup segments including team identifiers, player numbers, names, zones, and distances.
* **Local Persistence Layer:** Automatically formats extracted attributes into a structured CSV flat file mapped uniquely by game number and action filters.
* **Fault-Tolerant Inputs:** Bulletproof interactive CLI menus that gracefully handle formatting anomalies, bad text entries, and boundary checks without crashing.

---

## 🛠️ System Architecture & Workflow

The application runs a synchronized pipeline spanning validation, stream-reading, field slicing, and data writing:

[ Interactive CLI ] ──> Input Validation Checks ──> [ Network HTML Stream Buffer ]│[ Local CSV File ] <── Console Diagnostic Mirror <── [ String Slicing Parser Engine ]

1. **Validation Domain:** Evaluates game identification parameters (20001 ≤ ID ≤ 21230) and active keywords (e.g., `SHOT`).
2. **Buffering Engine:** Opens a low-overhead `InputStream` to scan web metrics row by row, keeping memory allocations to a minimum.
3. **Extraction Slicer:** Uses positional character indexing metrics (`indexOf`, `substring`) to pull discrete target strings out of unstructured columns.

---

## 🚀 Getting Started

### Prerequisites
* **Java Development Kit (JDK):** Version 8 or higher installed on your system.
* **Network Connectivity:** Required for accessing remote data streams during runtime execution.

### Installation & Compilation
Clone the repository and compile the source code file using any standard terminal environment:

```bash
# Clone the repository
git clone https://github.com

# Navigate to the project folder
cd nhl-event-scraper

# Compile the Java application class
javac EventScraper.java
```

### Execution Run
Start the application from the console:

```bash
java EventScraper
```

---

## 📈 Sample Data Output Structure

When an event record is parsed successfully, it maps properties into an isolated `.csv` tracking file named dynamically (e.g., `20123_SHOT.csv`). The structured schema inside matches the following blueprint:

```csv
TEAM,PLAYER_NUMBER,LAST_NAME,SHOT_TYPE,ZONE,DISTANCE
```

---

## ⚙️ Configuration & URL Domains

To safely run network scrapes, open the source file and navigate down to the `getNthEventByType` module. Update the unconfigured `urlText` string template variable to map to your chosen analytical sports network domain or private mock tracking server:

```java
// Locate this block in EventScraper.java and configure your domain layout
String urlText = "http://your-analytical-domain.com" + gameNumber + ".html";
```

---


