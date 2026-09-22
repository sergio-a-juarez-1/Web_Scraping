# ==========================================
# Makefile for NHL Event Data Processor
# ==========================================

# Compiler settings
CC = gcc
CFLAGS = -Wall -Wextra -O2

# Build targets
TARGET = EventProcessor
SRC = EventProcessor.c

# Default rule: Compiles the executable
all: $(TARGET)

$(TARGET): $(SRC)
	$(CC) $(CFLAGS) $(SRC) -o $(TARGET)

# Convenience rule: Compiles and immediately executes the program
run: $(TARGET)
	./$(TARGET)

# Clean rule: Deletes the compiled executable and any generated CSV datasets
clean:
	rm -f $(TARGET)
	rm -f *.csv

.PHONY: all run clean
