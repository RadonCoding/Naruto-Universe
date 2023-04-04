package radon.naruto_universe.ability;

public class AbilityType<T extends Ability> {
    private final AbilityFactory<T> factory;
    private final float damage;
    private final float cost;
    private final boolean dojutsu;

    public AbilityType(AbilityFactory<T> factory, float damage, float cost, boolean dojutsu) {
        this.factory = factory;
        this.damage = damage;
        this.cost = cost;
        this.dojutsu = dojutsu;
    }

    public T create() {
        return this.factory.create(this);
    }

    public float getDamage() {
        return this.damage;
    }

    public float getCost() {
        return this.cost;
    }

    public boolean isDojutsu() {
        return this.dojutsu;
    }

    public static class Builder<T extends Ability> {
        private final AbilityFactory<T> factory;
        private float damage;
        private float cost;
        private boolean dojutsu;

        private Builder(AbilityFactory<T> factory) {
            this.factory = factory;
        }

        public Builder<T> setDamage(float damage) {
            this.damage = damage;
            return this;
        }

        public Builder<T> setCost(float cost) {
            this.cost = cost;
            return this;
        }

        public Builder<T> setDojutsu() {
            this.dojutsu = true;
            return this;
        }

        public static <T extends Ability> Builder<T> of(AbilityFactory<T> factory) {
            return new Builder<>(factory);
        }

        public AbilityType<T> build() {
            return new AbilityType<>(this.factory, this.damage, this.cost, this.dojutsu);
        }
    }

    public interface AbilityFactory<T extends Ability> {
        T create(AbilityType<T> type);
    }
}