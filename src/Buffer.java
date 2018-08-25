import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    private int[] buffer;
    private Semaphore filledSemaphore;
    private Semaphore emptySemaphore;
    private ReentrantLock[] locks;
    private CountDownLatch countDownLatch;

    Buffer(final int[] buffer) {
        this.buffer = buffer;
        filledSemaphore = new Semaphore(0);
        emptySemaphore = new Semaphore(buffer.length);
        locks = new ReentrantLock[buffer.length];

        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantLock();
        }

        countDownLatch = new CountDownLatch(Main.WRITER_COUNT + Main.READER_COUNT);
    }

    void writeNum(int num, int page) {
        checkIndexNotOutOfBounds(page);
        buffer[page] = num;
    }

    int readNum(int page) {
        checkIndexNotOutOfBounds(page);
        int retNum = buffer[page];
        buffer[page] = -1;

        return retNum;
    }

    int findEmptyCell() {
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] == -1)
                return i;
        }
        return -1;
    }

    int findFilledCell() {
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] > -1)
                return i;
        }
        return -1;
    }

    void checkIndexNotOutOfBounds(int index) {
        if (index >= buffer.length || index < 0)
            throw new IndexOutOfBoundsException("index >= buffer.length || index < 0");
    }

    void acquireFilledSemaphore() throws InterruptedException {
        filledSemaphore.acquire();
    }

    void acquireEmptySemaphore() throws InterruptedException {
        emptySemaphore.acquire();
    }

    void releaseFilledSemaphore() {
        filledSemaphore.release();
    }

    void releaseEmptySemaphore() {
        emptySemaphore.release();
    }

    void lockLock(int page) {
        locks[page].lock();
    }

    boolean isLocked(int page) {
        return locks[page].isLocked();
    }

    void unlockLock(int page) {
        locks[page].unlock();
    }

    void countDown() {
        countDownLatch.countDown();
    }

    void await() throws InterruptedException {
        countDownLatch.await();
    }
}
