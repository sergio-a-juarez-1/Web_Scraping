#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

// Function Prototypes
void trim_newline(char *str);
int write_record_to_file(int game_number, const char *event_type, const char *record);
int print_records_from_file(int game_number, const char *event_type);
void create_data_file_name(int game_number, const char *event_type, char *filename_out);
void get_nth_event_by_type(int n, const char *event_type, int game_number, char *html_out);
void get_shot_data_from_event_html(const char *event_html, char *csv_out);

int main() {
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
        
        // Safe integer parsing
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
                strcpy(event_type, user_input);
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
                
                if (user_int > 0) {
                    printf("It is a valid event number\n");
                    event_number = (int)user_int;
                } else {
                    printf("Invalid event number. Please try again\n");
                    continue;
                }
                
                // Processing Pipeline
                char mock_html[512];
                char csv_record[256];
                
                get_nth_event_by_type(event_number, event_type, game_number, mock_html);
                get_shot_data_from_event_html(mock_html, csv_record);
                
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

// Helper function to remove trailing newline characters from input
void trim_newline(char *str) {
    size_t len = strlen(str);
    if (len > 0 && str[len - 1] == '\n') {
        str[len - 1] = '\0';
    }
}

// Generates the standard CSV output filename string
void create_data_file_name(int game_number, const char *event_type, char *filename_out) {
    sprintf(filename_out, "%d_%s.csv", game_number, event_type);
}

// Writes the record line directly into a local storage flat-file
int write_record_to_file(int game_number, const char *event_type, const char *record) {
    char filename[64];
    create_data_file_name(game_number, event_type, filename);
    
    FILE *file = fopen(filename, "w");
    if (file == NULL) {
        return -1;
    }
    
    fprintf(file, "%s\n", record);
    fclose(file);
    return 1;
}

// Reads and displays data line by line from the output file
int print_records_from_file(int game_number, const char *event_type) {
    char filename[64];
    create_data_file_name(game_number, event_type, filename);
    
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        return -1;
    }
    
    char buffer[256];
    int success = -1;
    while (fgets(buffer, sizeof(buffer), file)) {
        printf("%s", buffer);
        success = 1;
    }
    
    fclose(file);
    return success;
}

// Simulates finding the nth HTML event match safely
void get_nth_event_by_type(int n, const char *event_type, int game_number, char *html_out) {
    // Replicates Java behavior. In production, this would open a socket descriptor.
    // Provided with a consistent structural string layout matching your parser's design.
    sprintf(html_out, ">MTL #81 SURE_SHOT, HOME, 45 feet, sequence_%d_game_%d", n, game_number);
}

// Slices out individual tokens based on explicit layout markers
void get_shot_data_from_event_html(const char *event_html, char *csv_out) {
    char team[16] = "";
    int player_number = 0;
    char last_name[64] = "";
    char shot_type[32] = "";
    char zone[32] = "";
    char length[16] = "";
    
    // Finds structural hooks (">" and "#") safely
    const char *start = strchr(event_html, '>');
    if (!start) {
        strcpy(csv_out, "No match data available");
        return;
    }
    
    // Parses fixed layout tokens safely out of the stream pointer
    int items = sscanf(start, ">%3s #%d %63[^,], %31[^,], %15s", 
                       team, &player_number, last_name, shot_type, length);
    
    if (items >= 5) {
        // Mock zone placement to mimic Java's column-count output indices
        strcpy(zone, "ZONE"); 
        sprintf(csv_out, "%s,%d,%s,%s,%s,%s", team, player_number, last_name, shot_type, zone, length);
    } else {
        strcpy(csv_out, "Parsing failure: Data structural anomaly");
    }
}
