# ==============================================================================
# Makefile for NHL Event Data Scraper (Hardened Security Edition)
# ==============================================================================

# Compiler settings
CC = gcc

# Hardened Security Flags:
# -fstack-protector-strong: Injects canary tokens to prevent stack-smashing
# -D_FORTIFY_SOURCE=2: Automatically replaces unsafe string functions with bounded checks
CFLAGS = -Wall -Wextra -O2 -fstack-protector-strong -D_FORTIFY_SOURCE=2

# Corrected Build targets to match source script definitions
TARGET = EventScraper
SRC = EventScraper.c

# Default rule: Compiles the secure executable cleanly
all: $(TARGET)

$(TARGET): $(SRC)
	$(CC) $(CFLAGS) $(SRC) -o $(TARGET)

# Convenience rule: Ensures compilation succeeds *before* executing the binary
run: $(TARGET)
	./$(TARGET)

# Clean rule: Deletes the compiled binary executable and safely drops raw output files
clean:
	rm -f $(TARGET)
	rm -f *.csv

# Explicitly declare non-file commands to prevent folder confusion
.PHONY: all run clean
