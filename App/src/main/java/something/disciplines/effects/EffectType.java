package something.disciplines.effects;

public enum EffectType {

    buffDamage("buffDamage"),
    buffRange("buffRange"),
    buffDefense("buffDefense"),
    DOT("DOT"),
    HOT("HOT"),
    reduceMove("reduceMove");

    public final String name;

    EffectType(String nam){
        name = nam;
    }
}
