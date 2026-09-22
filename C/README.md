# NHL Event Data Processor (C Version)

A streamlined, console-based C application designed to safely capture, parse, and locally save granular sports metrics from game files. The project replicates the exact multi-loop logic of the original Java scraper workflow while taking advantage of low-overhead memory slices and robust pointer management.

---

## ✨ System Optimizations & Features

* **Memory-Safe Inputs:** Leverages `fgets` rather than standard, unconstrained reading sequences to ensure that user inputs cannot run past fixed buffer limits.
* **Rugged Integer Filtering:** Utilizes `strtol` to carefully unpack text selections. If unexpected text characters are inputted, the processor loops back without a stack crash.
* **Efficient Slicing Engine:** Extracts data parameters—including team code, player shirt number, name, action type, and distance details—using standard formatting string masks (`sscanf`).
* **Clean Storage Architecture:** Saves variables out to dynamic local text structures mapping directly to `.csv` layout frameworks.

---

## 🛠️ Program Layout & Pipeline

The software uses a classic sequential loop to capture target details before handing over to string buffer slicing algorithms:

[ Console Terminal ] ──> String Validation ──> [ Simulated Data Array Stream ]│[ Local CSV File ] <── Verification Mirror <── [ Fixed-Format String Parser ]

1. **Validation Domain:** Checks constraints across all user queries (forcing IDs between 20001 and 21230) and verifies exact actions (e.g., `SHOT`).
2. **Parsing Framework:** Scans past HTML formatting anchors (`>` and `#`) using string pointers to extract variables into independent registers.

---

## 🚀 Getting Started

### Prerequisites
* **C Compiler:** Any standard compiler such as `gcc` or `clang` available on your local execution environment.

### Compilation & Automation
You can compile the project manually or use the provided `Makefile` to automate your workflow:

```bash
# Option 1: Build the project automatically using the Makefile
make

# Option 2: Compile the source script manually
gcc -Wall EventProcessor.c -o EventProcessor
```

### Running and Cleaning the Project
The `Makefile` includes built-in commands to quickly run or wipe the workspace:

```bash
# Compile and immediately run the executable
make run

# Delete the executable and any locally generated .csv data files
make clean
```

---

## 📈 Standard Export Format

Upon successfully processing a record slice, data is stored into a distinct local dataset named using a logical key (e.g., `20050_SHOT.csv`). The internal structure matches this clean blueprint:

```csv
TEAM,PLAYER_NUMBER,LAST_NAME,SHOT_TYPE,ZONE,DISTANCE
```

---

## ⚙️ Development Customizations

To transition this from a local test file reader into an active network engine, replace the body of the `get_nth_event_by_type` function with standard C socket structures (`<sys/socket.h>`) or mirror it alongside external libraries like `libcurl` to pipe website streams directly into the extraction pipeline.

