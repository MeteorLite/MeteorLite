package dev.hoot.api.movement.pathfinder;

import lombok.Value;
import dev.hoot.api.magic.Rune;

@Value
public class RuneRequirement {
    int quantity;
    Rune rune;

    public boolean meetsRequirements() {
        return rune.getQuantity() >= quantity;
    }
}
