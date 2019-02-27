import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FTP_Server implements Runnable{
    private final boolean DEBUG = false;
    private Path path;
    private Socket controlSocket;

    public FTP_Server(Socket controlSocket) {
        this.controlSocket = controlSocket;
        path = Paths.get(System.getProperty("user.dir"));
    }

    public void run() {
        //add some threading/port/socket debug messages
        System.out.print("Thread id:" + Thread.currentThread().getId() + " of " + Thread.activeCount());
        System.out.println(" " + controlSocket.getRemoteSocketAddress().toString().substring(1));

        //add input/data/output streams and readers
        //Input
        InputStreamReader iStream = new InputStreamReader(controlSocket.getInputStream());
        BufferedReader reader = new BufferedReader(iStream);

        //Data
        DataInputStream byteStream = new DataInputStream(controlSocket.getInputStream());

        //Output
        OutputStream oStream = controlSocket.getOutputStream();
        DataOutputStream dStream = new DataOutputStream(oStream);

        exitThread:
        while (true) {
            try {
                //Server will try to connect every 20 ms
                while (!reader.ready())
                    Thread.sleep(10);

                //capture and tokenize command
                List<String> tokens = new ArrayList<String>();
                String command = reader.readLine();
                Scanner tokenize = new Scanner(command);
                //gets command
                if (tokenize.hasNext())
                    tokens.add(tokenize.next());
                //gets rest of string after the command; this allows filenames with spaces: 'file1 test.txt'
                if (tokenize.hasNext())
                    tokens.add(command.substring(tokens.get(0).length()).trim());
                tokenize.close();

                // switch statement for commands
                switch (tokens.get(0)) {
                    case "list":
                        try {
                            DirectoryStream<Path> dirStream = Files.newDirectoryStream(path);
                            for (Path entry : dirStream)
                                dStream.writeBytes(entry.getFileName() + "\n");
                            dStream.writeBytes("\n");
                        } catch (Exception e) {
                            dStream.writeBytes("ls: failed to retrive contents" + "\n");
                            dStream.writeBytes("\n");
                        }

                        break;

                    case "retrieve":
                        //not a directory or file
                        if (Files.notExists(path.resolve(tokens.get(1))))
                            dStream.writeBytes("get: " + tokens.get(1) + ": No such file or directory" + "\n");

                            //is a directory
                        else if (Files.isDirectory(path.resolve(tokens.get(1))))
                            dStream.writeBytes("get: " + tokens.get(1) + ": Is a directory" + "\n");

                            //transfer file
                        else {
                            //blank message
                            dStream.writeBytes("\n");

                            File file = new File(path.resolve(tokens.get(1)).toString());
                            long fileSize = file.length();

                            //send file size
                            dStream.writeBytes(fileSize + "\n");

                            //need to figure
                            Thread.sleep(100);

                            byte[] buffer = new byte[8192];
                            try {
                                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                                int count = 0;
                                while ((count = in.read(buffer)) > 0)
                                    dStream.write(buffer, 0, count);

                                in.close();
                                break;
                            } catch (Exception e) {
                                System.out.println("Error while retrieving");
                            }
                        }
                        break;

                    case "store":
                        //get file size
                        long fileSize = Long.parseLong(reader.readLine());
                        FileOutputStream f = new FileOutputStream(new File(path.resolve(tokens.get(1)).toString()));
                        int count = 0;
                        byte[] buffer = new byte[8192];
                        long bytesReceived = 0;
                        while (bytesReceived < fileSize) {
                            count = byteStream.read(buffer);
                            f.write(buffer, 0, count);
                            bytesReceived += count;
                        }
                        f.close();
                        break;


                    case "quit":
                        if (DEBUG) System.out.println("-quit");
                         //close socket
                        controlSocket.close();

                        //exit while loop
                        break exitThread;
                }
            }
            catch (Exception e) {
                System.out.println("Error while running");
            }
        }
    }

    //might have to be moved up to top function, check runnable use
    public static void main(String[] args) {
        int port;
        port = Integer.parseInt(args[0]);
        System.out.println("Port Number: " + port);
        ServerSocket socketControl = new ServerSocket(port);
        while (true) {
            //blocks until client connects; starts connection on new thread
            (new Thread(new FTP_Server(socketControl.accept()))).start();
        }
    }
}
