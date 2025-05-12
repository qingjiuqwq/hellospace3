/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils.font;

public class GlyphData {

    public final float charWidth;
    public final float charHeight;
    public final int imageSize;
    public final float u;
    public final float v;
    public final int texture;

    public GlyphData(float charWidth, float charHeight, int imageSize, float u, float v, int texture) {
        this.charWidth = charWidth;
        this.charHeight = charHeight;
        this.imageSize = imageSize;
        this.u = u;
        this.v = v;
        this.texture = texture;
    }

}