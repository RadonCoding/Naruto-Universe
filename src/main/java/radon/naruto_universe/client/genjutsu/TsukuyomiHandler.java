package radon.naruto_universe.client.genjutsu;

public class TsukuyomiHandler {
    private static TsukyomiInfo current;

    public static void tick() {
        if (current == null) return;

        current.age++;

        if (current.age >= current.lifetime) {
            current = null;
        }
    }

    public static void trigger(int duration) {
        current = new TsukyomiInfo(duration);
    }

    public static boolean isActive() {
        return current != null;
    }

    private static class TsukyomiInfo {
        private final int lifetime;
        private int age;

        public TsukyomiInfo(int lifetime) {
            this.lifetime = lifetime * 10;
        }
    }
}
