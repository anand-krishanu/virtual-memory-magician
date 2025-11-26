public class PageTableEntry {
    private int frameNumber;
    private boolean valid;

    public PageTableEntry() {
        this.frameNumber = -1;
        this.valid = false;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
        this.valid = true;
    }

    public boolean isValid() {
        return valid;
    }

    public void invalidate() {
        this.valid = false;
        this.frameNumber = -1;
    }
}