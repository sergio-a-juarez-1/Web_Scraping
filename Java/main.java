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
    public static void main(String[] args) throws IOException {
        int gameNumber;
        String eventType = "";
        int eventNumber;
        
        System.out.println("Welcome to the play by play event scraper. The system will get events for the specified game for the specified event type.");
        Scanner console = new Scanner(System.in);
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
                    continue; // Fixed bug: Prevents dropping down and crashing on re-parsing
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
                                    continue; // Fixed bug: Prevents assignments from running on faulty text
                                }
                                
                                if (userInt > 0) {
                                    System.out.println("It is a valid event number");
                                    eventNumber = userInt;
                                } else {
                                    System.out.println("Invalid event number. Please try again");
                                    continue;
                                }

                                String html = getNthEventByType(eventNumber, eventType, gameNumber);
                                String recordData = getShotDataFromEventHTML(html);
                                
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
        
        System.out.println("Bye");
    }
    /**
     * Writes the given record to the correct event data file.
     * 
     * @param gameNumber The unique reference code for the game
     * @param eventType  The matching criteria label
     * @param record     The formatted raw CSV properties line
     * @return 1 if successful, -1 if an error occurs
     */
    public static int writeRecordToFile(int gameNumber, String eventType, String record) throws IOException {
        try {
            File newfile = new File(createDataFileName(gameNumber, eventType));
            newfile.createNewFile();
            FileWriter fileWrite = new FileWriter(newfile);
            BufferedWriter buffWrite = new BufferedWriter(fileWrite);
            buffWrite.write(record);
            buffWrite.flush();
            buffWrite.close();
        } catch (IOException e) {
            return -1;
        }
        return 1;
    }

    /**
     * Reads and outputs data from the given event data file.
     *
     * @param gameNumber The unique reference code for the game
     * @param eventType  The matching criteria label
     * @return 1 if lines were printed, -1 if no lines or error
     */
    public static int printRecordsFromFile(int gameNumber, String eventType) throws IOException {
        int success = -1;
        File sourceFile = new File(createDataFileName(gameNumber, eventType));
        if (!sourceFile.exists()) {
            return success;
        }
        
        BufferedReader buffRead = new BufferedReader(new FileReader(sourceFile));
        String currline;
        while ((currline = buffRead.readLine()) != null) {
            System.out.println(currline);
            success = 1;
        }
        buffRead.close();
        return success;
    }

    /**
     * Creates a consistent file naming structure for output storage.
     */
    static String createDataFileName(int gameNumber, String eventType) {
        return gameNumber + "_" + eventType.toUpperCase() + ".csv";
    }
    /**
     * Gets the nth event of the specified type from the game data stream.
     *
     * @param n          The targeted instance count
     * @param eventType  The target tracking criteria string
     * @param gameNumber The target matching identifier
     * @return td HTML tag row with event details
     */
    public static String getNthEventByType(int n, String eventType, int gameNumber) {
        // Base placeholder variable - replace with target structural data domain
        String urlText = "" + gameNumber; 
        
        InputStream is = null;
        BufferedReader br;
        String line = "not found"; 
        int eventCount = 0;

        try {
            if (urlText.isEmpty() || urlText.equals(String.valueOf(gameNumber))) {
                return "Domain URL Not Configured";
            }
            
            URL url = new URL(urlText);
            is = url.openStream();
            br = new BufferedReader(new InputStreamReader(is));

            while (eventCount < n && (line = br.readLine()) != null) {
                if (line.contains(eventType.toUpperCase())) {
                    line = br.readLine();
                    eventCount += 1;
                }
            }
        } catch (MalformedURLException mue) {
            System.out.println("Network structural issue: " + mue.getMessage());
        } catch (IOException ioe) {
            System.out.println("Input retrieval issue: " + ioe.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                // Closed safely
            }
        }
        return line;
    }

    /**
     * Slices isolated feature attributes using fixed structural tag indexes.
     */
    public static String getShotDataFromEventHTML(String eventHTML) {
        if (eventHTML == null || !eventHTML.contains(">") || !eventHTML.contains("#")) {
            return "No match data available";
        }
        
        String result;

        // get shooter team
        int positionOfFirstGreaterThan = eventHTML.indexOf(">", 0);
        String shooterTeam = eventHTML.substring(positionOfFirstGreaterThan + 1, positionOfFirstGreaterThan + 4);

        // get shooter number
        int positionOfFirstHash = eventHTML.indexOf("#", 0);
        int positionOfFirstSpaceAfterFirstHash = eventHTML.indexOf(" ", positionOfFirstHash);
        String shooterPlayerNumber = eventHTML.substring(positionOfFirstHash + 1, positionOfFirstSpaceAfterFirstHash);

        // get shooter name
        int positionOfFirstCommaSpaceAfterFirstHash = eventHTML.indexOf(", ", positionOfFirstSpaceAfterFirstHash + 1);
        String shooterLastName = eventHTML.substring(positionOfFirstSpaceAfterFirstHash + 1, positionOfFirstCommaSpaceAfterFirstHash);

        // get shot type
        int positionOfSecondCommaSpaceAfterShooterName = eventHTML.indexOf(", ", positionOfFirstCommaSpaceAfterFirstHash + 1);
        String shotType = eventHTML.substring(positionOfFirstCommaSpaceAfterFirstHash + 2, positionOfSecondCommaSpaceAfterShooterName);

        // get zone
        int positionOfThirdCommaSpaceAfterShooterName = eventHTML.indexOf(", ", positionOfSecondCommaSpaceAfterShooterName + 1);
        String zone = eventHTML.substring(positionOfSecondCommaSpaceAfterShooterName + 2, positionOfThirdCommaSpaceAfterShooterName);

        // get length
        int positionOfFirstSpaceAfterThirdCommaSpaceAfterShooterName = eventHTML.indexOf(" ", positionOfThirdCommaSpaceAfterShooterName + 2);
        String length = eventHTML.substring(positionOfThirdCommaSpaceAfterShooterName + 2, positionOfFirstSpaceAfterThirdCommaSpaceAfterShooterName);

        result = shooterTeam + "," + shooterPlayerNumber + "," + shooterLastName + "," + shotType + "," + zone + "," + length;
        return result;
    }
}
