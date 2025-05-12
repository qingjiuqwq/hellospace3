/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils.font;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class FontRenderer implements AutoCloseable {

    public static final Color SHADOW_COLOR = new Color(26, 26, 26, 160);
    public static final Color BACKGROUND_COLOR = new Color(255, 255, 255, 1);
    public final Font font;
    private final Font boldFont;
    private final boolean antiAliased;
    private final FontRenderContext context;
    private final FontMetrics fontMetrics;
    private final HashMap<Character, GlyphData> glyphMap = new HashMap<>(512);
    private final HashMap<Character, GlyphData> boldGlyphMap = new HashMap<>(128);

    public FontRenderer(final Font font, final boolean antiAliased) {
        this.antiAliased = antiAliased;
        this.boldFont = font.deriveFont(Font.BOLD);
        this.fontMetrics = new Canvas().getFontMetrics(font);
        this.font = new Font(font.getFontName(), font.getStyle(), font.getSize() * 2);
        this.context = new FontRenderContext(new AffineTransform(), antiAliased, false);
    }

    public FontRenderer(final String name, final int size) {
        this(new Font(name, Font.PLAIN, size), true);
    }

    public FontRenderer(final int size) {
        this("Default", size);
    }

    public static void color(final int hex) {
        color((hex >> 16) & 0xFF, (hex >> 8) & 0xFF, hex & 0xFF, (hex >> 24) & 0xFF);
    }

    public static void color(final int r, final int g, final int b, final int a) {
        RenderSystem.setShaderColor(r / 255F, g / 255F, b / 255F, a / 255F);
    }

    public static int uploadTexture(final BufferedImage image) {
        NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), false);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                nativeImage.setPixelRGBA(x, y, image.getRGB(x, y));
            }
        }
        return new DynamicTexture(nativeImage).getId();
    }

    public void drawCenteredString(PoseStack matrices, String str, float x, float y, int color) {
        this.drawString(matrices, str, x - getStringWidth(str) / 2F, y, color, false);
    }

    public void drawCenteredString(PoseStack matrices, String str, float x, float y, int color, boolean shadow) {
        this.drawString(matrices, str, x - getStringWidth(str) / 2F, y, color, shadow);
    }

    public void drawCenteredStringWithShadow(PoseStack matrices, String str, float x, float y, int color) {
        this.drawString(matrices, str, x - getStringWidth(str) / 2F, y, color, true);
    }

    public void drawString(PoseStack matrices, String str, float x, float y, int color) {
        this.drawString(matrices, str, x, y, color, false);
    }

    public void drawStringWithShadow(PoseStack matrices, String str, float x, float y, int color) {
        this.drawString(matrices, str, x, y, color, true);
    }

    public void drawString(PoseStack matrices, String str, float x, float y, int color, boolean dropShadow) {
        if (dropShadow) {
            this.renderString(matrices, str, x + 0.5f, y + 0.5f, new Color(SHADOW_COLOR.getRed(), SHADOW_COLOR.getGreen(), SHADOW_COLOR.getBlue(), SHADOW_COLOR.getAlpha() * Math.min(1, Math.max(0, (int) (160 * ((color >> 24) & 0xFF) / 255f)))).getRGB(), true);
            this.renderString(matrices, str, x, y, color, false);
        }
        this.renderString(matrices, str, x, y, color, false);
    }

    private void renderString(final PoseStack poseStack, final String str, final float x, final float y, final int color, final boolean shadow) {
        if (str == null || str.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 1F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        color(color);
        float x1 = x * 2;
        float y1 = (y - 2) * 2;

        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underlined = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\n' && i < str.length() - 1) {
                y1 += fontMetrics.getAscent();
                x1 = x1 * 2;
                i++;
            }
            GlyphData glyph = getGlyphData(c, bold);
            if (shadow) {
                drawGlyph(poseStack, glyph, x1, y1, false, false, italic);
            } else {
                drawGlyph(poseStack, glyph, x1, y1, strikethrough, underlined, italic);
            }
            x1 += glyph.charWidth;
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void drawGlyph(final PoseStack poseStack, final GlyphData glyphData, final float x, final float y, final boolean strikethrough, final boolean underlined, final boolean italic) {
        float offset = italic ? 4 : 0;

        float texel = 1.0F / glyphData.imageSize;

        Matrix4f mat4 = poseStack.last().pose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, glyphData.texture);

        BufferBuilder worldRenderer = Tesselator.getInstance().getBuilder();
        worldRenderer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX);
        worldRenderer.vertex(mat4, x + offset, y, 0.0F).uv(glyphData.u * texel, glyphData.v * texel).endVertex();
        worldRenderer.vertex(mat4, x - offset, y + glyphData.charHeight, 0.0F).uv(glyphData.u * texel, (glyphData.v + glyphData.charHeight) * texel).endVertex();
        worldRenderer.vertex(mat4, x + glyphData.charWidth + offset, y, 0.0F).uv((glyphData.u + glyphData.charWidth) * texel, glyphData.v * texel).endVertex();
        worldRenderer.vertex(mat4, x + glyphData.charWidth - offset, y + glyphData.charHeight, 0.0F).uv((glyphData.u + glyphData.charWidth) * texel, (glyphData.v + glyphData.charHeight) * texel).endVertex();
        BufferUploader.drawWithShader(worldRenderer.end());

        if (strikethrough || underlined) {

            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.setShader(GameRenderer::getPositionShader);

            if (strikethrough) {
                worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                worldRenderer.vertex(mat4, x, y + glyphData.charHeight / 2F, 0.0F).endVertex();
                worldRenderer.vertex(mat4, x + glyphData.charWidth, y + glyphData.charHeight / 2F, 0.0F).endVertex();
                worldRenderer.vertex(mat4, x + glyphData.charWidth, y + glyphData.charHeight / 2F - 1, 0.0F).endVertex();
                worldRenderer.vertex(mat4, x, y + glyphData.charHeight / 2F - 1, 0.0F).endVertex();
                BufferUploader.drawWithShader(worldRenderer.end());
            }

            if (underlined) {
                worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                worldRenderer.vertex(mat4, x, y + font.getSize(), 0.0F).endVertex();
                worldRenderer.vertex(mat4, x + glyphData.charWidth, y + font.getSize(), 0.0F).endVertex();
                worldRenderer.vertex(mat4, x + glyphData.charWidth, y + font.getSize() - 1, 0.0F).endVertex();
                worldRenderer.vertex(mat4, x, y + font.getSize() - 1, 0.0F).endVertex();
                BufferUploader.drawWithShader(worldRenderer.end());
            }

            RenderSystem.setShaderTexture(0, glyphData.texture);
        }
    }

    private GlyphData getGlyphData(final char character, final boolean bold) {
        Map<Character, GlyphData> map = bold ? boldGlyphMap : glyphMap;

        if (!map.containsKey(character)) {
            GlyphData glyphData = createGlyphData(character, bold);
            map.put(character, glyphData);
            return glyphData;
        }

        return map.get(character);
    }

    private GlyphData createGlyphData(char character, boolean bold) {
        String charStr = String.valueOf(character);

        Font font = bold ? this.boldFont : this.font;

        Rectangle charBounds = font.getStringBounds(charStr, context).getBounds();
        int charWidth = charBounds.width;
        int charHeight = charBounds.height;

        int imageSize = Math.max(charWidth, charHeight) * 2 + 5 * 2;

        int u = imageSize / 2 - charWidth / 2;
        int v = imageSize / 2 - charHeight / 2;

        BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, imageSize, imageSize);

        graphics.setFont(font);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        graphics.setColor(Color.WHITE);
        graphics.drawString(charStr, u, v + fontMetrics.getAscent());

        graphics.dispose();

        int texture = uploadTexture(image);
        RenderSystem.bindTexture(texture);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        return new GlyphData(charWidth, charHeight, imageSize, u, v, texture);
    }

    public float getStringWidth(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        float ret = 0;

        for (int i = 0; i < s.length(); i++) {
            ret += getCharWidth(s.charAt(i), false);
        }

        return ret / 2F;
    }

    public float getCharWidth(char c, boolean bold) {
        return getGlyphData(c, bold).charWidth;
    }

    @Override
    public void close() {
        glyphMap.values().forEach(glyphData -> RenderSystem.deleteTexture(glyphData.texture));
        glyphMap.clear();

        boldGlyphMap.values().forEach(glyphData -> RenderSystem.deleteTexture(glyphData.texture));
        boldGlyphMap.clear();
    }
}