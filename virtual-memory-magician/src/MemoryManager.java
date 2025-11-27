import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.util.Arrays;

public class MemoryManager {

    private PageTableEntry[] pageTable;
    private int[] frames;
    private ReplacementPolicy policy;

    // Stats
    private int hits = 0;
    private int faults = 0;

    private JTextArea logArea;
    private JTable frameTable;

    public MemoryManager(int numPages, int numFrames, ReplacementPolicy policy, JTextArea logArea, JTable frameTable) {
        this.pageTable = new PageTableEntry[numPages];
        for (int i = 0; i < numPages; i++) pageTable[i] = new PageTableEntry();

        this.frames = new int[numFrames];
        Arrays.fill(frames, -1);

        this.policy = policy;

        this.logArea = logArea;
        this.frameTable = frameTable;
        updateFrameTable();
    }

    public void accessPage(int pageNumber) {
        log("\nAccessing page " + pageNumber);

        PageTableEntry pte = pageTable[pageNumber];

        if (pte.isValid()) {
            hits++;
            log("→ HIT in frame " + pte.getFrameNumber());
            policy.frameAccessed(pte.getFrameNumber());
        } else {
            faults++;
            log("→ FAULT");
            loadPage(pageNumber);
        }

        updateFrameTable();
    }

    private void loadPage(int pageNumber) {
        // Find free frame
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] == -1) {
                frames[i] = pageNumber;
                pageTable[pageNumber].setFrameNumber(i);
                policy.frameLoaded(i);
                log("Loaded page " + pageNumber + " into free frame " + i);
                return;
            }
        }

        // No free frame → eviction
        int victimFrame = policy.chooseVictimFrame();
        int victimPage = frames[victimFrame];

        log("Evicting page " + victimPage + " from frame " + victimFrame);

        pageTable[victimPage].invalidate();

        frames[victimFrame] = pageNumber;
        pageTable[pageNumber].setFrameNumber(victimFrame);
        policy.frameLoaded(victimFrame);

        log("Loaded page " + pageNumber + " into frame " + victimFrame);
    }

    private void updateFrameTable() {
        for (int i = 0; i < frames.length; i++) {
            String value = frames[i] == -1 ? "FREE" : "Page " + frames[i];
            frameTable.setValueAt(value, i, 1);
        }
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void printSummary() {
        log("\n===== SUMMARY =====");
        log("Hits: " + hits);
        log("Faults: " + faults);
        double rate = (faults * 100.0) / (hits + faults);
        log(String.format("Fault Rate: %.2f%%", rate));
    }
    
    public boolean hasActivity() {
        return (hits + faults) > 0;
    }
}