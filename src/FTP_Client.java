import java.io.*;
import java.net.Socket;

public class Ftp_Client {

    private Socket socket;
    private String hostName;
    private int portNumber;
    private PrintWriter out;
    private BufferedReader in;
    private FileOutputStream fileOut;
    private PrintWriter fileWrite;
    private boolean exists;
    private int status;

    public Ftp_Client() {
        // Something goes here?

    }

    public int connect(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        // Step 1: Create a socket that connects to the above host and port number
        try {
            socket = new Socket(this.hostName, this.portNumber);
            // Step 2: Create a PrintWriter from the socket's output stream
            // Use the autoFlush option
            out = new PrintWriter(socket.getOutputStream(), true);
            // Step 3: Create a BufferedReader from the socket's input stream
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.println("error");
            return -1;
        }
        return 0;
    }

    public int retrieve(String file) {
        // Step 4: Send an HTTP GET request via the PrintWriter.
        // Remember to print the necessary blank line

        try {
            out.println("RETRIEVE " + file);
        } catch (Exception e) {
            System.out.println("No connection");
            return -1;
        }

        if (exists) {
            try {
                fileOut = new FileOutputStream(file);
            } catch (FileNotFoundException e) {

                e.printStackTrace();
                return -1;
            }
            fileWrite = new PrintWriter(fileOut);
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    fileWrite.println(line);
                }
            } catch (IOException e) {

                e.printStackTrace();
                return -1;
            }
            fileWrite.close();
        }
        return 0;
    }

    public int list() {
        try {
            out.println("LIST");
        } catch (Exception e) {
            System.out.println("No connection");
            return -1;
        }
        return 0;
    }

    public int store(String file) {
        try {
            out.println("STORE " + file);
        } catch (Exception e) {
            System.out.println("No connection");
            return -1;
        }
        return 0;
    }

    public int close() {
        try {
            socket.close();
            out.close();
            return 0;
        } catch (IOException e) {
            System.out.println("No connection to close");
            return -1;
        }
    }

    public int quit() {
        try {
            out.println("QUIT");
        } catch (Exception e) {
            System.out.println("No connection");
            return -1;
        }
        return 0;
    }
    public static void main(String[] args) {
        Ftp_Client client = new Ftp_Client();
        int status;

        if (args.length < 1 || args.length > 3)
            System.out.println("Invalid arguements");

        else {

            String command = args[0].toUpperCase();
            switch(command)
            {
                case "CONNECT":
                    if (args.length != 3)
                        System.out.println("Invalid arguements");
                    else {
                        try {
                            int pNum = Integer.parseInt(args[2]);
                            status = client.connect(args[1], pNum);
                        } catch (Exception e) {
                            System.out.print("Could not convert port to number");
                        }
                    }
                    break;

                case "LIST":
                    System.out.println("two");
                    break;

                case "RETREIVE":
                    if (args.length != 3)
                        System.out.println("Invalid arguements");
                    else
                        status = client.retrieve(args[1]);
                    break;

                case "STORE":
                    if (args.length != 3)
                        System.out.println("Invalid arguements");
                    else
                        status = client.store(args[2]);
                    break;

                case "QUIT":
                    status = client.close();
                    break;

                case "HELP":
                    System.out.println("--- HELP MENU ---\n");
                    System.out.println("CONNECT <server name/IP address> <server port>");
                    System.out.println("LIST");
                    System.out.println("RETRIEVE <filename>");
                    System.out.println("STORE <filename>");
                    System.out.println("QUIT\n");
                    break;

                default:
                    System.out.println("No Matching Argument");
            }
        }
    }
}