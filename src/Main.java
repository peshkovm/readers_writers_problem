import java.util.Arrays;

public class Main {
    static final int WRITER_COUNT = 8;
    static final int READER_COUNT = 8;
    static final int BUFFER_SIZE = 6;

    public static void main(String[] args) throws InterruptedException {
        int[] buf = new int[BUFFER_SIZE];
        Arrays.fill(buf, -1);

        Buffer buffer = new Buffer(buf);
        LogFile logFile = new LogFile();

        WriterWork writerWork = new WriterWork(buffer, logFile);
        ReaderWork readerWork = new ReaderWork(buffer, logFile);

        for (int i = 0; i < WRITER_COUNT; i++) {
            new Thread(writerWork).start();
        }

        for (int i = 0; i < READER_COUNT; i++) {
            new Thread(readerWork).start();
        }

        try {
            buffer.await();
            System.out.println("All threads have stopped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}