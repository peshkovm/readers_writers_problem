import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class LogFile {
    private File file;
    private PrintWriter outStream;

    LogFile() {
        file = new File("C:/JavaLessons/Readers-writers problem/log/logFile.txt");
        try {
            outStream = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    synchronized void writeToFile(String str) {
        outStream.println(str);
        outStream.flush();
    }
}
