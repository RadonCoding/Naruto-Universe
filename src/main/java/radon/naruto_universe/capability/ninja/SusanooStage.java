package radon.naruto_universe.capability.ninja;

public enum SusanooStage {
    RIBCAGE(1000),
    SKELETAL(1500),
    HUMANOID(2500),
    ARMORED(5000),
    PERFECT(10000);

    private final int experience;

    SusanooStage(int experience) {
        this.experience = experience;
    }

    public int getExperience() {
        return this.experience;
    }
}
