
package meteor.config

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class ExpandResizeType(type: String) {
    KEEP_WINDOW_SIZE("Keep window size"), KEEP_GAME_SIZE("Keep game size");


}