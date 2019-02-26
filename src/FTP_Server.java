import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FTP_Server {
    private final boolean DEBUG = false;
    private Path path;
    private Socket controlSocket;

    public myftpserver(Socket controlSocket) {
        this.controlSocket = controlSocket;
        path = Paths.get(System.getProperty("user.dir"));
    }

    public void run() {
        //add some threading/port/socket debug messages
        //add input/data/output streams and readers
        //Input
        InputStreamReader iStream = new InputStreamReader(controlSocket.getInputStream());
        BufferedReader reader = new BufferedReader(iStream);

        //Data
        DataInputStream byteStream = new DataInputStream(controlSocket.getInputStream());

        //Output
        OutputStream oStream = controlSocket.getOutputStream();
        DataOutputStream dStream = new DataOutputStream(oStream);
        while (true) {
            try {
                while (!reader.ready())
                    Thread.sleep(10);

                //capture and tokenize command
                //switch statement for commands




            } catch(Exception e) {
                System.out.println("Error while running");
            }
        }
    }
}
