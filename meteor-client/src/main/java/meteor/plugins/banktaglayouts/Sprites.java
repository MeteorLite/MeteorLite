package meteor.plugins.banktaglayouts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import meteor.game.SpriteOverride;

@RequiredArgsConstructor
public enum Sprites implements SpriteOverride {
    APPLY_PREVIEW(-3192, "confirm_icon.png"), // 3192 is definitely not my bank pin.
    CANCEL_PREVIEW(-3193, "delete.png"),
    AUTO_LAYOUT(-3194, "auto_layout.png"),
    ;

    @Getter
    private final int spriteId;

    @Getter
    private final String fileName;
}
