import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.net.URL;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

/**
 * Play-by-play event scraper and data persistent processor.
 */
public class EventScraper {
    // Upper boundary for safety to prevent Denial of Service (DoS) memory/network strain
    private static final int MAX_EVENT_NUMBER = 1000;

    public static void main(String[] args) {
        int gameNumber;
        String eventType = "";
        int eventNumber;
        
        System.out.println("Welcome to the play by play event scraper. The system will get events for the specified game for the specified event type.");
        
        // Using try-with-resources to ensure Scanner is closed properly upon program termination
        try (Scanner console = new Scanner(System.in)) {
            String userInput;
            
            do { // A LOOP (Game Number Selection)
                System.out.println("Please enter a valid game number (or type EXIT):");
                userInput = console.nextLine();
                
                if (!userInput.equalsIgnoreCase("EXIT")) {
                    int userInt;
                    try {
                        userInt = Integer.parseInt(userInput);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid game number. Please try again");
                        continue;
                    }
                    
                    if (userInt >= 20001 && userInt <= 21230) {
                        System.out.println("Valid game number");
                        gameNumber = userInt;
                    } else {
                        System.out.println("Invalid game number. Please try again");
                        continue;
                    }
                    
                    do { // B LOOP (Event Type Selection)
                        System.out.println("Please enter an event type (i.e. SHOT): ");
                        userInput = console.nextLine();
                        
                        if (!userInput.equalsIgnoreCase("EXIT")) {
                            if (userInput.matches("SHOT")) {
                                System.out.println("It is a valid event type");
                                eventType = userInput;
                            } else {
                                System.out.println("Invalid event type. Please try again");
                                continue;
                            }

                            do { // C LOOP (Event Number Selection)
                                System.out.println("Please enter the nth number " + eventType + " you would like: ");
                                userInput = console.nextLine();
                                
                                if (!userInput.equalsIgnoreCase("EXIT")) {
                                    try {
                                        userInt = Integer.parseInt(userInput);
                                    } catch (NumberFormatException ex) {
                                        System.out.println("Wrong Input");
                                        continue;
                                    }
                                    
                                    // Safe boundaries checking to prevent arbitrary loops
                                    if (userInt > 0 && userInt <= MAX_EVENT_NUMBER) {
                                        System.out.println("It is a valid event number");
                                        eventNumber = userInt;
                                    } else {
                                        System.out.println("Invalid event number. Must be between 1 and " + MAX_EVENT_NUMBER + ". Please try again");
                                        continue;
                                    }

                                    String html = getNthEventByType(eventNumber, eventType, gameNumber);
                                    
                                    // Safeguard in case environment configuration is completely missing
                                    if ("Domain URL Not Configured".equalsIgnoreCase(html) || "not found".equalsIgnoreCase(html)) {
                                        System.out.println("Aborting operation: Scraper target is unavailable.");
                                        break;
                                    }

                                    // Placeholder context preservation for extraction logic
                                    String recordData = html; 
                                    
                                    int writeStatus = writeRecordToFile(gameNumber, eventType, recordData);
                                    System.out.println("Write Status is " + writeStatus);

                                    int readStatus = printRecordsFromFile(gameNumber, eventType);
                                    System.out.println("Read Status is " + readStatus);
                                    break;
                                }
                            } while (!userInput.equalsIgnoreCase("EXIT"));
                        }
                        break;
                    } while (!userInput.equalsIgnoreCase("EXIT"));
                }
            } while (!userInput.equalsIgnoreCase("EXIT"));
        }
        
        System.out.println("Bye");
    }

    /**
     * Writes the given record to the correct event data file.
     */
    public static int writeRecordToFile(int gameNumber, String eventType, String record) {
        File newfile = new File(createDataFileName(gameNumber, eventType));
        
        // Try-with-resources handles safe creation, writing, flushing, and closing automatically
        try (FileWriter fileWrite = new FileWriter(newfile);
             BufferedWriter buffWrite = new BufferedWriter(fileWrite)) {
            
            buffWrite.write(record);
            return 1;
        } catch (IOException e) {
            System.err.println("File write error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Reads and outputs data from the given event data file.
     */
    public static int printRecordsFromFile(int gameNumber, String eventType) {
        File sourceFile = new File(createDataFileName(gameNumber, eventType));
        if (!sourceFile.exists()) {
            return -1;
        }
        
        int success = -1;
        // Try-with-resources guarantees file handle closes even if loops error out
        try (BufferedReader buffRead = new BufferedReader(new FileReader(sourceFile))) {
            String currline;
            while ((currline = buffRead.readLine()) != null) {
                System.out.println(currline);
                success = 1;
            }
        } catch (IOException e) {
            System.err.println("File read error: " + e.getMessage());
            return -1;
        }
        return success;
    }

    /**
     * Creates a consistent file naming structure for output storage.
     */
    static String createDataFileName(int gameNumber, String eventType) {
        // Safe context: gameNumber is strictly validated digits, eventType is strictly "SHOT" via regex
        return gameNumber + "_" + eventType.toUpperCase() + ".csv";
    }

    /**
     * Gets the nth event of the specified type from the game data stream.
     */
    public static String getNthEventByType(int n, String eventType, int gameNumber) {
        // SECURE FIX: Fetches target from system environment variables rather than hardcoding it
        String baseUrl = System.getenv("SCRAPER_TARGET_URL"); 
        
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            System.err.println("CRITICAL ERROR: Environment variable 'SCRAPER_TARGET_URL' is missing.");
            return "Domain URL Not Configured";
        }
        
        // Append context parameters cleanly to your dynamic base url string
        String urlText = baseUrl + "?game=" + gameNumber; 
        String line = "not found"; 
        int eventCount = 0;

        // Try-with-resources dynamically tracks both stream objects and cleanly terminates them
        try (InputStream is = new URL(urlText).openStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            while (eventCount < n && (line = br.readLine()) != null) {
                if (line.contains(eventType.toUpperCase())) {
                    line = br.readLine();
                    eventCount += 1;
                }
            }
        } catch (MalformedURLException mue) {
            System.err.println("Network structural issue: " + mue.getMessage());
        } catch (IOException ioe) {
            System.err.println("Input retrieval issue: " + ioe.getMessage());
        }

        return line;
    }
}
