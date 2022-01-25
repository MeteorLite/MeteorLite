package dev.hoot.api.magic;

import dev.hoot.api.game.Vars;
import dev.hoot.api.items.Inventory;

import java.util.Arrays;

public class RunePouch {
    private static final int SLOT_1_TYPE_BIT = 29;
    private static final int SLOT_1_QUANTITY_BIT = 1624;
    private static final int SLOT_2_TYPE_BIT = 1622;
    private static final int SLOT_2_QUANTITY_BIT = 1625;
    private static final int SLOT_3_TYPE_BIT = 1623;
    private static final int SLOT_3_QUANTITY_BIT = 1626;

    public enum RuneSlot {
        FIRST(SLOT_1_TYPE_BIT, SLOT_1_QUANTITY_BIT),
        SECOND(SLOT_2_TYPE_BIT, SLOT_2_QUANTITY_BIT),
        THIRD(SLOT_3_TYPE_BIT, SLOT_3_QUANTITY_BIT);

        private final int type;
        private final int quantityVarbitIdx;

        RuneSlot(int type, int quantityVarbitIdx) {
            this.type = type;
            this.quantityVarbitIdx = quantityVarbitIdx;
        }

        public int getType() {
            return type;
        }

        public int getQuantityVarbitIdx() {
            return quantityVarbitIdx;
        }

        public String getRuneName() {
            return switch (Vars.getBit(type)) {
                case 1 -> "Air rune";
                case 2 -> "Water rune";
                case 3 -> "Earth rune";
                case 4 -> "Fire rune";
                case 5 -> "Mind rune";
                case 6 -> "Chaos rune";
                case 7 -> "Death rune";
                case 8 -> "Blood rune";
                case 9 -> "Cosmic rune";
                case 10 -> "Nature rune";
                case 11 -> "Law rune";
                case 12 -> "Body rune";
                case 13 -> "Soul rune";
                case 14 -> "Astral rune";
                case 15 -> "Mist rune";
                case 16 -> "Mud rune";
                case 17 -> "Dust rune";
                case 18 -> "Lava rune";
                case 19 -> "Steam rune";
                case 20 -> "Smoke rune";
                default -> null;
            };
        }

        public int getQuantity() {
            return Vars.getBit(quantityVarbitIdx);
        }
    }

    public static int getQuantity(Rune rune) {
        if (!hasPouch()) {
            return 0;
        }

        RuneSlot runeSlot =
                Arrays.stream(RuneSlot.values()).filter(x -> Arrays.stream(rune.getRuneNames())
                                .anyMatch(name -> x.getRuneName() != null && x.getRuneName().startsWith(name)))
                        .findFirst()
                        .orElse(null);

        if (runeSlot == null) {
            return 0;
        }

        return runeSlot.getQuantity();
    }

    public static boolean hasPouch() {
        return Inventory.getFirst("Rune pouch") != null;
    }
}
