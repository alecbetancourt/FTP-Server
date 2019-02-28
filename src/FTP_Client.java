/**********************************************************************
 * Filename: FTP_Client
 *
 * Authors: Alec Betancourt, Parker Petroff, and Randy Nguyen
 **********************************************************************/

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FTP_Client {

    private Socket socket;
    private String hostName;
    private int portNumber;
    private PrintWriter out;
    private BufferedReader in;
    private FileOutputStream fileOut;
    private PrintWriter fileWrite;
    private boolean exists;
    private int status;

    public FTP_Client() {
        // Something goes here?

    }

    public int connect(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        // Create a socket that connects to the above host and port number
        try {
            socket = new Socket(this.hostName, this.portNumber);
            // Create a PrintWriter from the socket's output stream
            // Use the autoFlush option
            out = new PrintWriter(socket.getOutputStream(), true);
            // Create a BufferedReader from the socket's input stream
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.println("error");
            return -1;
        }
        return 0;
    }

    public int retrieve(String file) {

        try {
            out.println("retrieve " + file);
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
            out.println("list");
        } catch (Exception e) {
            System.out.println("No connection");
            return -1;
        }
        return 0;
    }

    public int store(String file) {
        try {
            out.println("store " + file);
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

    public static void main(String[] args) {
        FTP_Client client = new FTP_Client();
        Scanner scnr = new Scanner(System.in);

        int status;

        while (true) {

            String cmd = scnr.nextLine();
            String[] commands = cmd.split(" ");

            if (commands.length < 1 || commands.length > 3)
                System.out.println("Invalid arguments");

            else {

                String command = commands[0].toUpperCase();
                switch (command) {
                    case "CONNECT":
                        if (commands.length != 3)
                            System.out.println("Invalid arguments");
                        else {
                            try {
                                System.out.println("Attempting Connection");
                                int pNum = Integer.parseInt(commands[2]);
                                status = client.connect(commands[1], pNum);
                            } catch (Exception e) {
                                System.out.print("Could not convert port to number");
                            }
                        }
                        break;

                    case "LIST":
                        client.list();
                        break;

                    case "RETRIEVE":
                        if (commands.length != 2)
                            System.out.println("Invalid arguments");
                        else
                            status = client.retrieve(commands[1]);
                        break;

                    case "STORE":
                        if (commands.length != 2)
                            System.out.println("Invalid arguments");
                        else
                            status = client.store(commands[2]);
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
}
