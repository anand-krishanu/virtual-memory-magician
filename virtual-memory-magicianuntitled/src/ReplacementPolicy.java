public interface ReplacementPolicy {
    int chooseVictimFrame();
    void frameAccessed(int frameIndex);
    void frameLoaded(int frameIndex);
}
