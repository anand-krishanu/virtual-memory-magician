import java.util.HashMap;
import java.util.Map;

public class LRUReplacement implements ReplacementPolicy {
    private Map<Integer, Long> lastAccessTime;
    private long timestamp;

    public LRUReplacement() {
        lastAccessTime = new HashMap<>();
        timestamp = 0;
    }

    @Override
    public int chooseVictimFrame() {
        // Find frame with the least recent access time
        int victimFrame = -1;
        long oldestTime = Long.MAX_VALUE;
        
        for (Map.Entry<Integer, Long> entry : lastAccessTime.entrySet()) {
            if (entry.getValue() < oldestTime) {
                oldestTime = entry.getValue();
                victimFrame = entry.getKey();
            }
        }
        
        lastAccessTime.remove(victimFrame);
        return victimFrame;
    }

    @Override
    public void frameAccessed(int frameIndex) {
        // Update access time when page is accessed (hit)
        lastAccessTime.put(frameIndex, ++timestamp);
    }

    @Override
    public void frameLoaded(int frameIndex) {
        // Record access time when page is loaded
        lastAccessTime.put(frameIndex, ++timestamp);
    }
}
