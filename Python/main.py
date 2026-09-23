import os
import csv
import re
import time
import requests
from bs4 import BeautifulSoup

# --- CONFIGURATION CONSTANTS ---
HEADERS = {
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}
URL_LIST_FILE = "theater_urls.txt"
OUTPUT_CSV_FILE = "thesis_dataset.csv"

# SECURE FIX: Remove hardcoded domain and pull from environment variables
BASE_URL = os.getenv("SCRAPER_BASE_URL", "https://kickstarter.com").rstrip("/")


def sanitize_for_csv(value):
    """
    Mitigates CSV/Formula Injection attacks. If a field begins with a spreadsheet 
    trigger character (+, -, =, @), prepend an apostrophe to force it to render as raw text.
    """
    if not value:
        return ""
    string_value = str(value).strip()
    if string_value and string_value[0] in ['=', '+', '-', '@']:
        return f"'{string_value}"
    return string_value


def harvest_category_urls(output_file, max_pages=3):
    """
    PHASE 1: Automated Discovery URL Harvester
    Iterates through the discovery engine and collects unique project links.
    """
    print(f"[*] Starting Phase 1: Gathering links across {max_pages} pages from {BASE_URL}...")
    
    unique_links = set()
    
    # Read existing URLs to prevent duplicating file writes across pipeline executions
    existing_links = set()
    if os.path.exists(output_file):
        with open(output_file, "r", encoding="utf-8") as f:
            existing_links = {line.strip() for line in f if line.strip()}
    
    for page in range(1, max_pages + 1):
        params = {
            "category_id": "17",  # Theater category ID
            "sort": "newest",
            "page": page
        }
        
        try:
            response = requests.get(BASE_URL, params=params, headers=HEADERS, timeout=10)
            if response.status_code != 200:
                print(f"[!] Warning: Skipped page {page} (Status Code: {response.status_code})")
                continue
                
            soup = BeautifulSoup(response.content, "html.parser")
            page_links_count = 0
            
            for anchor in soup.find_all("a", href=True):
                href = anchor["href"]
                if "/projects/" in href:
                    clean_url = href.split("?")[0]
                    
                    if not clean_url.startswith("http"):
                        clean_url = f"{BASE_URL}{clean_url}"
                        
                    if clean_url not in unique_links and clean_url not in existing_links:
                        unique_links.add(clean_url)
                        page_links_count += 1
            
            print(f"[+] Page {page}: Found {page_links_count} new campaign links.")
            time.sleep(1.5)  # Slightly elevated safety rate pacing
            
        except Exception as e:
            print(f"[X] Network error on page {page}: {str(e)}")

    if unique_links:
        # Append only strictly new gathered links
        with open(output_file, "a", encoding="utf-8") as f:
            for link in unique_links:
                f.write(f"{link}\n")
            
    print(f"[+] Phase 1 Complete. Saved new unique links to: {output_file}\n")


def extract_project_metrics(project_url):
    """
    PHASE 2: Individual Page Parser
    Extracts static and dynamic metrics from a live or completed project page.
    """
    try:
        response = requests.get(project_url, headers=HEADERS, timeout=10)
        if response.status_code != 200:
            return None
            
        soup = BeautifulSoup(response.content, "html.parser")
        
        # Title Extraction
        title_tag = soup.find("meta", property="og:title")
        title = title_tag["content"] if title_tag else "Unknown Project"
        
        # Author Extraction
        author_tag = soup.find("meta", {"name": "author"})
        author = author_tag["content"] if author_tag else "Unknown Creator"
        
        if title == "Unknown Project":
            title_el = soup.find("h2") or soup.find("title")
            title = title_el.text.strip() if title_el else "Unknown"

        # Pledged Metric Extraction (Using non-deprecated 'string' selector parameter)
        pledged = "0"
        pledged_el = soup.find(string=re.compile(r"\$\d+"))
        if pledged_el:
            pledged = re.sub(r"[^\d]", "", pledged_el)

        # Target Funding Goal Extraction
        goal = "0"
        goal_el = soup.find(string=re.compile(r"pledged of \$\d+"))
        if goal_el:
            goal = re.sub(r"[^\d]", "", goal_el)
        else:
            goal_meta = soup.find("span", {"class": re.compile(r"money|goal")})
            if goal_meta:
                goal = re.sub(r"[^\d]", "", goal_meta.text)

        # Backer Count Extraction
        backers = "0"
        backers_el = soup.find(attrs={"data-backers-count": True}) or soup.find(string=re.compile(r"\d+ backer"))
        if backers_el:
            backers = re.sub(r"[^\d]", "", backers_el.text if hasattr(backers_el, 'text') else backers_el)

        # Securely sanitize all text variables before packaging them for CSV ingestion
        return {
            "title": sanitize_for_csv(title),
            "author": sanitize_for_csv(author),
            "url": project_url,
            "pledged": pledged if pledged else "0",
            "goal": goal if goal else "0",
            "backers": backers if backers else "0"
        }
        
    except Exception as e:
        print(f"[!] Scraping failure on link: {project_url} -> {str(e)}")
        return None


def run_batch_pipeline(input_txt, output_csv):
    """
    PHASE 3: Pipeline Executor
    Reads the gathered txt index file, crawls each link, and builds your database file.
    """
    print(f"[*] Starting Phase 2: Compiling data rows into {output_csv}...")
    
    if not os.path.exists(input_txt):
        print(f"[X] Abort: Target input file '{input_txt}' does not exist.")
        return
        
    with open(input_txt, "r", encoding="utf-8") as f:
        urls = list(set([line.strip() for line in f if line.strip()]))
        
    if not urls:
        print("[!] No URLs found to process. Pipeline ended.")
        return

    fields = ["title", "author", "url", "pledged", "goal", "backers"]
    
    with open(output_csv, "w", newline="", encoding="utf-8") as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=fields)
        writer.writeheader()
        
        for idx, url in enumerate(urls, 1):
            print(f"    -> Scraping row {idx}/{len(urls)}: {url}")
            record = extract_project_metrics(url)
            
            if record:
                writer.writerow(record)
                
            time.sleep(2.0)  # Safe delay pacing to prevent IP rate-limiting blocks
            
    print(f"[+] Dataset compiled completely! File saved to: {output_csv}")


if __name__ == "__main__":
    print("=== DATA WORKFLOW PIPELINE INITIALIZED ===")
    
    harvest_category_urls(output_file=URL_LIST_FILE, max_pages=3)
    run_batch_pipeline(input_txt=URL_LIST_FILE, output_csv=OUTPUT_CSV_FILE)
    
    print("=== WORKFLOW PIPELINE SUCCESSFUL ===")
