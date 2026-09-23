# Kickstarter Crowdfunding Data Pipeline

A robust, modular Python web scraping pipeline designed to extract empirical data from crowdfunding campaigns. Optimized for empirical analysis and academic research, this system programmatically harvests campaign-level indicators—such as project titles, creator identities, backer counts, amounts pledged, and total funding targets—and structures them into tidy, secure CSV datasets ideal for econometric modeling (e.g., OLS, Probit, or Logit regressions).

---

## ✨ Features

* **Two-Phase Scraper Pipeline:** Separates discovery harvesting (index tracking) from detailed deep-page feature extraction to maximize pipeline stability.
* **Semantic DOM Resilience:** Avoids volatile CSS utility classes by leveraging stable HTML `<meta>` attributes, structural tags (`og:title`), and explicit semantic selectors.
* **Persistent File De-duplication:** Checks existing files on disk before running to skip already-harvested links, protecting bandwidth and preventing uncontrolled disk storage growth.
* **CSV Injection Safeguards:** Automatically sanitizes text fields to neutralize spreadsheet trigger characters (`=`, `+`, `-`, `@`), preventing formula execution exploits when opening data in Excel or Google Sheets.
* **Academic/Statistical Readiness:** Cleans financial markers and formats data rows smoothly, outputting zero-loss CSV structures ready for statistical suites like Stata, R, or Python Pandas.
* **Rate-Limit Safeguards:** Implements gentle sleep intervals between network buffers to mimic organic web traffic and protect against platform IP bans.

---

## 🛠️ System Architecture & Workflow

The pipeline automates what would otherwise require tedious manual console browsing across three distinct execution phases:

[ Phase 1: URL Harvester ] ──> Programmatic Index Search ──> Unique Links Text File
                                                                   │
[ Phase 2: Metadata Parser ] ──> Meta Layer Data Extraction <─────────┘
                                       │
                                       ▼
[ Phase 3: Dataset Builder ] ──> Structural Serialization ──> Clean Thesis CSV


1. **Phase 1: Link Harvesting (`harvest_category_urls`)** — Connects to the discovery engine, steps dynamically through pagination markers, extracts project endpoints, strips trackable reference fragments, and records unique new items to a flat file.
2. **Phase 2: Individual Page Parsing (`extract_project_metrics`)** — Loads each campaign URL, maps indicators out of header structures, and strips non-numeric metadata characters away from dynamic numerical fields.
3. **Phase 3: Pipeline Execution (`run_batch_pipeline`)** — Reads unique links out of storage buffers, pipes items through the parsing engine, sanitizes outputs against spreadsheet injections, and saves columns cleanly into a schema-mapped flat file.

---

## 🚀 Getting Started

### Prerequisites
* **Python:** Version 3.6 or higher installed on your system.
* **Dependencies:** Uses external networking and markup parsing packages.

### Installation
Clone the repository and install the verified tracking requirements using your local package terminal:

```bash
# Clone the repository
git clone https://github.com/sergio-a-juarez-1/Web_Scraping.git

# Navigate into the project folder
cd Python

# Install the required scraping libraries
pip install requests beautifulsoup4
```

### Execution Run Safely on Your Machine
To safely publish this open-source code without hardcoding tracking domains, the pipeline reads its entry target directly from an environment variable (`SCRAPER_BASE_URL`). If not set, it defaults to public Kickstarter mirrors.

Set this variable in your terminal session before execution:

#### 💻 macOS / Linux
```bash
export SCRAPER_BASE_URL="https://kickstarter.com"
python3 main.py
```

#### 🪟 Windows (Command Prompt)
```cmd
set SCRAPER_BASE_URL=https://kickstarter.com
python3 main.py
```

#### 🟦 Windows (PowerShell)
```powershell
$env:SCRAPER_BASE_URL="https://kickstarter.com"
python3 main.py
```

---

## 📈 Compiled Data Output Schema

When execution ends successfully, the pipeline generates a clean `thesis_dataset.csv` flat-file structured according to the following layout matrix:

| Column Header | Data Type | Description | Empirical Use Case |
| :--- | :--- | :--- | :--- |
| `title` | String | The official title name of the campaign (Sanitized against injection). | Text analysis / length controls |
| `author` | String | Name signature of the project developer (Sanitized against injection). | Experience tracking |
| `url` | String | The unique, un-tracked web address path. | Index key identifier |
| `pledged` | Integer | Total currency amount pledged to date (numbers only). | Continuous dependent variable |
| `goal` | Integer | Target funding volume required by the creator. | Core independent variable (Risk metric) |
| `backers` | Integer | Total count of individual investors participating. | Engagement intensity variable |

---

## ⚙️ Project Customization

The constants at the top of `main.py` let you quickly adjust parameters without risking structural line issues:

```python
# Modify these constants to track different categories or change files
URL_LIST_FILE = "theater_urls.txt"   # Storage file for collected URLs
OUTPUT_CSV_FILE = "thesis_dataset.csv" # Target data file name
```
To expand your research across other verticals, you can change the `"category_id"` query parameter inside `harvest_category_urls` to target categories like **Design (12)**, **Games (11)**, or **Technology (16)**.
