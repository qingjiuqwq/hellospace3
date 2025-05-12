/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.manager;

import space.utils.font.FontRenderer;

import java.util.ArrayList;
import java.util.List;

public class FontManager {

    public static List<FontRenderer> fontRenderers = new ArrayList<>();

    public static FontRenderer getFont(final int size) {
        for (FontRenderer font : fontRenderers) {
            if (font.font.getSize() == size * 2) {
                return font;
            }
        }
        FontRenderer font = new FontRenderer(size);
        fontRenderers.add(font);
        return font;
    }

}
