public class Main {
    public static void main(String[] args) {
        System.out.println("Virtual Memory Simulator (CLI version)");

        int numPages = 10;
        int numFrames = 3;

        // Simple CLI simulation without UI
        FIFOReplacement fifo = new FIFOReplacement();
        PageTableEntry[] pageTable = new PageTableEntry[numPages];
        for (int i = 0; i < numPages; i++) pageTable[i] = new PageTableEntry();
        int[] frames = new int[numFrames];
        java.util.Arrays.fill(frames, -1);

        int[] accessSequence = {1,2,3,2,4,1,5,2,1};
        int hits = 0, faults = 0;

        for (int page : accessSequence) {
            PageTableEntry pte = pageTable[page];
            if (pte.isValid()) {
                hits++;
                System.out.println("Page " + page + " HIT in frame " + pte.getFrameNumber());
            } else {
                faults++;
                System.out.println("Page " + page + " FAULT");

                // Find free frame
                boolean loaded = false;
                for (int i = 0; i < frames.length; i++) {
                    if (frames[i] == -1) {
                        frames[i] = page;
                        pte.setFrameNumber(i);
                        fifo.frameLoaded(i);
                        loaded = true;
                        break;
                    }
                }

                if (!loaded) {
                    int victim = fifo.chooseVictimFrame();
                    int victimPage = frames[victim];
                    pageTable[victimPage].invalidate();
                    frames[victim] = page;
                    pte.setFrameNumber(victim);
                    fifo.frameLoaded(victim);
                }
            }
        }

        System.out.println("\nHits: " + hits + ", Faults: " + faults);
        System.out.printf("Fault Rate: %.2f%%\n", (faults*100.0)/(hits+faults));
    }
}