#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_EVENT_NUMBER 1000

// Function Prototypes
void trim_newline(char *str);
int write_record_to_file(int game_number, const char *event_type, const char *record);
int print_records_from_file(int game_number, const char *event_type);
void create_data_file_name(int game_number, const char *event_type, char *filename_out, size_t max_len);
void get_nth_event_by_type(int n, const char *event_type, int game_number, char *html_out, size_t max_len);
void get_shot_data_from_event_html(const char *event_html, char *csv_out, size_t max_len);

int main(void) {
    int game_number = 0;
    char event_type[32] = "";
    int event_number = 0;
    char user_input[64];
    
    printf("Welcome to the play by play event scraper. The system will get events for the specified game for the specified event type.\n");
    
    // --- LOOP A: Game Number Selection ---
    while (1) {
        printf("Please enter a valid game number (or type EXIT):\n");
        if (!fgets(user_input, sizeof(user_input), stdin)) break;
        trim_newline(user_input);
        
        if (strcasecmp(user_input, "EXIT") == 0) break;
        
        char *endptr;
        long user_int = strtol(user_input, &endptr, 10);
        if (*endptr != '\0' || user_input == endptr) {
            printf("Invalid game number. Please try again\n");
            continue;
        }
        
        if (user_int >= 20001 && user_int <= 21230) {
            printf("Valid game number\n");
            game_number = (int)user_int;
        } else {
            printf("Invalid game number. Please try again\n");
            continue;
        }
        
        // --- LOOP B: Event Type Selection ---
        while (1) {
            printf("Please enter an event type (i.e. SHOT):\n");
            if (!fgets(user_input, sizeof(user_input), stdin)) break;
            trim_newline(user_input);
            
            if (strcasecmp(user_input, "EXIT") == 0) break;
            
            if (strcmp(user_input, "SHOT") == 0) {
                printf("It is a valid event type\n");
                // Securely copy bounded string input
                strncpy(event_type, user_input, sizeof(event_type) - 1);
                event_type[sizeof(event_type) - 1] = '\0';
            } else {
                printf("Invalid event type. Please try again\n");
                continue;
            }
            
            // --- LOOP C: Event Number Selection ---
            while (1) {
                printf("Please enter the nth number %s you would like:\n", event_type);
                if (!fgets(user_input, sizeof(user_input), stdin)) break;
                trim_newline(user_input);
                
                if (strcasecmp(user_input, "EXIT") == 0) break;
                
                user_int = strtol(user_input, &endptr, 10);
                if (*endptr != '\0' || user_input == endptr) {
                    printf("Wrong Input\n");
                    continue;
                }
                
                // Enforce safety ceiling constraints to prevent DoS resource exhaustion
                if (user_int > 0 && user_int <= MAX_EVENT_NUMBER) {
                    printf("It is a valid event number\n");
                    event_number = (int)user_int;
                } else {
                    printf("Invalid event number. Must be 1-%d. Please try again\n", MAX_EVENT_NUMBER);
                    continue;
                }
                
                // Elevated allocation constraints to safely handle combined substring formatting
                char mock_html[512];
                char csv_record[512]; 
                
                get_nth_event_by_type(event_number, event_type, game_number, mock_html, sizeof(mock_html));
                get_shot_data_from_event_html(mock_html, csv_record, sizeof(csv_record));
                
                int write_status = write_record_to_file(game_number, event_type, csv_record);
                printf("Write Status is %d\n", write_status);
                
                int read_status = print_records_from_file(game_number, event_type);
                printf("Read Status is %d\n", read_status);
                break;
            }
            break; 
        }
    }
    
    printf("Bye\n");
    return 0;
}

void trim_newline(char *str) {
    size_t len = strlen(str);
    if (len > 0 && str[len - 1] == '\n') {
        str[len - 1] = '\0';
    }
}

// Bounded file formatting destination verification
void create_data_file_name(int game_number, const char *event_type, char *filename_out, size_t max_len) {
    snprintf(filename_out, max_len, "%d_%s.csv", game_number, event_type);
}

int write_record_to_file(int game_number, const char *event_type, const char *record) {
    char filename[128]; // Expanded buffer size for additional safety margin
    create_data_file_name(game_number, event_type, filename, sizeof(filename));
    
    FILE *file = fopen(filename, "w");
    if (file == NULL) {
        return -1;
    }
    
    fprintf(file, "%s\n", record);
    fclose(file);
    return 1;
}

int print_records_from_file(int game_number, const char *event_type) {
    char filename[128];
    create_data_file_name(game_number, event_type, filename, sizeof(filename));
    
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        return -1;
    }
    
    char buffer[512];
    int success = -1;
    while (fgets(buffer, sizeof(buffer), file)) {
        printf("%s", buffer);
        success = 1;
    }
    
    fclose(file);
    return success;
}

void get_nth_event_by_type(int n, const char *event_type, int game_number, char *html_out, size_t max_len) {
    // SECURE FIX: snprintf completely truncates data safely if output exceeds memory limits
    snprintf(html_out, max_len, ">MTL #81 SURE_SHOT, HOME, 45 feet, sequence_%d_game_%d", n, game_number);
}

void get_shot_data_from_event_html(const char *event_html, char *csv_out, size_t max_len) {
    char team[16] = "";
    int player_number = 0;
    char last_name[64] = "";
    char shot_type[32] = "";
    char zone[32] = "";
    char length[16] = "";
    
    const char *start = strchr(event_html, '>');
    if (!start) {
        strncpy(csv_out, "No match data available", max_len - 1);
        csv_out[max_len - 1] = '\0';
        return;
    }
    
    // Explicit length indicators inside formatting tokens strictly enforces memory boundaries
    int items = sscanf(start, ">%15s #%d %63[^,], %31[^,], %15s", 
                       team, &player_number, last_name, shot_type, length);
    
    if (items >= 5) {
        strncpy(zone, "ZONE", sizeof(zone) - 1);
        zone[sizeof(zone) - 1] = '\0';
        
        // SECURE FIX: Checked construction boundaries prevent local buffer overrides
        snprintf(csv_out, max_len, "%s,%d,%s,%s,%s,%s", team, player_number, last_name, shot_type, zone, length);
    } else {
        strncpy(csv_out, "Parsing failure: Data structural anomaly", max_len - 1);
        csv_out[max_len - 1] = '\0';
    }
}
