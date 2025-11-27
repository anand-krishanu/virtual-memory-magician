import java.util.ArrayDeque;
import java.util.Queue;

public class FIFOReplacement implements ReplacementPolicy {
    private Queue<Integer> queue;

    public FIFOReplacement() {
        queue = new ArrayDeque<>();
    }

    @Override
    public int chooseVictimFrame() {
        return queue.poll();
    }

    @Override
    public void frameAccessed(int frameIndex) {
        // FIFO does nothing on access
    }

    @Override
    public void frameLoaded(int frameIndex) {
        queue.add(frameIndex);
    }
}
