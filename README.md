# Evolutionary Data Engineering: From Structured Logic to Empirical Thesis Pipelines

An academic and engineering timeline demonstrating the progressive evolution of automated web scraping architectures. This repository preserves the development lifecycle of a data-engineering framework across three distinct language domains (**Java**, **C**, and **Python**). The final Python implementation served as the core data engine for a Senior Honors Thesis at the University of Utah, harvesting, parsing, and structured-modeling an empirical sample of **over 112,173 crowdfunding projects**.

---

## 📈 Executive Summary: The Evolutionary Timeline

The software engineering lifecycle in this repository scales progressively through three execution paradigms:

[ Phase 1: Java Core ]  ──> Structured CLI Logic & Stream Mechanics│↓[ Phase 2: C Processor ] ──> Pointer-Driven Layout Tokenization (Zero-Overhead)│↓[ Phase 3: Python Engine ] ──> Semantic DOM Resilience & Production Thesis Dataset


| Dimension | Phase 1: Java Scraper | Phase 2: C Processor | Phase 3: Python Pipeline (Thesis Engine) |
| :--- | :--- | :--- | :--- |
| **Primary Focus** | Object-Oriented Logic | Zero-Overhead Tokenization | Semantic DOM Resilience & Automation |
| **Input Strategy** | Multi-Loop Terminal Validation | Safe `fgets` Stack Bounds Check | Continuous Batch File / Set De-duplication |
| **Parsing Mechanism**| Hardcoded `.indexOf()` slicing | Native `sscanf()` Format Masks | Stable HTML `<meta>` Layer Property Scopes |
| **Fault Tolerance** | Catches `NumberFormatException` | Pointer validation via `strtol` | Graceful network-timeout error handlers |
| **Academic Outcome** | Proof of concept logic | Architectural translation step | Generated data for Probit Empirical Models |

---

## 🛠️ Deep Dive: Architectural Phases

### ☕ Phase 1: Java Scraper
* **File Name:** `main.java`
* **Purpose:** Establishes the foundational user interface and input validation loops.
* **Core Mechanics:** Implements a three-tiered nested loop system (`do-while`) that collects and validates parameters (Game IDs, action classifications, event counters). It validates inputs gracefully, handles string conversions safely, and mirrors structural attributes down to raw local files.

### 🥩 Phase 2: C Processor
* **File Name:** `main.c` (Builds via `Makefile`)
* **Purpose:** Translates object-oriented abstractions into low-overhead, pointer-driven machine code.
* **Core Mechanics:** Strips away modern runtime garbage collectors to process network indicators at the bare-metal level. Uses `strtol` for safe memory-integer decoding and processes raw data arrays with memory-isolated token slicing masks (`sscanf`) to completely eliminate buffer overflows.

### 🐍 Phase 3: Python Pipeline (The Thesis Engine)
* **File Name:** `main.py`
* **Purpose:** A scalable data pipeline engineered to handle complex web page structures and automate batch processing for academic research.
* **Core Mechanics:** Uses a two-phase architecture: An **Index Harvester** that programmatically steps through discovery networks to log target URLs, followed by an **Individual Page Parser** that uses universal open-graph properties (`og:title`, `<meta>`) instead of fragile CSS styles. It cleans currency markers automatically, ensures data rows are cleanly aligned, and applies smart rate-limiting to prevent IP blocking.

---

## 🚀 Getting Started & Execution

### 1. Running the Java Core
```bash
# Compile the source class file
javac EventScraper.java

# Run the interactive console interface
java EventScraper
```

### 2. Building the C Processor
The C directory includes a professional automation script (`Makefile`) to cleanly orchestrate builds:
```bash
# Compile the binary automatically with safe optimization flags
make

# Compile and immediately run the executable
make run

# Clean the workspace (removes binaries and any generated test CSV files)
make clean
```

### 3. Executing the Python Thesis Engine
```bash
# Install the required scraping libraries
pip install requests beautifulsoup4

# Execute the complete automated web pipeline
python main.py
```

---

## 🎓 Empirical Research Outcomes: Senior Honors Thesis

The final Python pipeline successfully constructed a database of **112,173 campaigns** used to model investor participation behaviors. The extracted metrics were evaluated using a **Probit Regression Model** in Stata, yielding key insights into the dynamics of online crowdfunding success:

### Key Empirical Findings
* **Visibility Premium (`staff_pick`):** Showed an incredibly strong positive correlation (**+0.369 coefficient**), proving that platform feature status significantly boosts consumer trust.
* **The Communication Penalty (`disable_communication`):** Blocked message boards heavily reduced funding velocity (**-0.294 coefficient**), demonstrating that backers actively avoid "black-box" campaigns.
* **Temporal Windows:** Captured a clear curvilinear relationship. While extended timelines (`duration`) generally hurt momentum, the squared term (`duration^2`) proved that this negative penalty tapers off significantly over long periods. 
* **Optimal Launch Windows:** The statistical data pinpointed **March** as the most lucrative funding month and **Sunday** as the premier launch day, matching weekend browsing behavior.

### Empirical Data Schema Matrix
When the Python pipeline finishes running, it outputs a clean, standardized dataset (`thesis_dataset.csv`) ready for direct import into statistical software suites like Stata, R, or Python Pandas:

```csv
title,author,url,pledged,goal,backers
```