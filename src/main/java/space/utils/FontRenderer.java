/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.Closeable;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FontRenderer implements Closeable, AutoCloseable {

    public final int size;
    private final Map<ResourceLocation, List<DrawEntry>> glyphPageCache = new HashMap<>();
    private final List<GlyphMap> glyphMaps = new ArrayList<>();
    private final Map<Character, Glyph> glyphCache = new HashMap<>();
    private final int characterPadding;
    private final int charactersPerPage;
    public Font font;
    private int scaleMultiplier = 0;
    private int previousGuiScale = -1;
    private Future<Void> prebakeGlyphsFuture;

    public FontRenderer(final Font font, final int charactersPerPage, final int paddingBetweenCharacters) {
        this.size = font.getSize();
        this.charactersPerPage = charactersPerPage;
        this.characterPadding = paddingBetweenCharacters;
        this.init(font);
    }

    public FontRenderer(final Font font) {
        this(font, 256, 5);
    }

    public FontRenderer(final String name, final int size) {
        this(new Font(name, Font.PLAIN, size));
    }

    public FontRenderer(int size) {
        this(new Font(null, Font.PLAIN, 11).getName(), size);
    }

    private static int floorToNearestMultiple(final int value, final int multiple) {
        return multiple * (int) Math.floor((double) value / multiple);
    }

    public static String removeControlCharacters(final String input) {
        char[] chars = input.toCharArray();
        StringBuilder filteredText = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '§') {
                i++;
                continue;
            }
            filteredText.append(c);
        }
        return filteredText.toString();
    }

    public static double roundToDecimal(final double n, final int point) {
        if (point == 0) {
            return Math.floor(n);
        }
        double factor = Math.pow(10, point);
        return Math.round(n * factor) / factor;
    }

    private void checkAndResetScale() {
        if (Wrapper.mc().getWindow().getGuiScale() != this.previousGuiScale) {
            this.close();
            this.init(this.font);
        }
    }

    private void init(final Font font) {
        this.previousGuiScale = (int) Wrapper.mc().getWindow().getGuiScale();
        this.scaleMultiplier = this.previousGuiScale;
        this.font = font.deriveFont(font.getStyle(), font.getSize() * this.scaleMultiplier);
    }

    private GlyphMap generateMap(final char from, final char to) {
        GlyphMap gm = new GlyphMap(from, to, this.font, ResourceLocation.parse("minecraft"), this.characterPadding);
        this.glyphMaps.add(gm);
        return gm;
    }

    private Glyph findGlyph(final char character) {
        for (GlyphMap map : this.glyphMaps) {
            if (map.contains(character)) {
                return map.getGlyph(character);
            }
        }
        int base = floorToNearestMultiple(character, this.charactersPerPage);
        GlyphMap glyphMap = this.generateMap((char) base, (char) (base + this.charactersPerPage));
        return glyphMap.getGlyph(character);
    }

    private Glyph getCachedGlyph(final char character) {
        return this.glyphCache.computeIfAbsent(character, this::findGlyph);
    }

    public void drawString(final PoseStack stack, final String s, final double x, final double y, final int color) {
        this.drawString(stack, s, x, y, (color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, (color >> 24 & 0xFF) / 255f);
    }

    public void drawString(final PoseStack stack, final String s, final double x, final double y, final Color color) {
        this.drawString(stack, s, x, y, color.getRGB());
    }

    public void drawString(final PoseStack stack, final String s, final double x, final double y, final float r, final float g, final float b, final float a) {
        this.drawString(stack, s, x, y, r, g, b, a, false);
    }

    public void drawString(final PoseStack stack, final String s, final double x, final double y, final float r, final float g, final float b, final float a, final boolean gradient) {
        if (this.prebakeGlyphsFuture != null && !this.prebakeGlyphsFuture.isDone()) {
            try {
                this.prebakeGlyphsFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                e.fillInStackTrace();
            }
        }

        this.checkAndResetScale();
        Color color = new Color(r, g, b);
        boolean wasBlendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        int originalTextureId = RenderSystem.getShaderTexture(0);
        float[] originalColorValues = RenderSystem.getShaderColor();
        RenderSystem.getShaderColor();

        stack.pushPose();
        stack.translate(roundToDecimal(x, 1), roundToDecimal(y - 3f, 1), 0);
        stack.scale(1f / this.scaleMultiplier, 1f / this.scaleMultiplier, 1f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        Matrix4f matrix4f = stack.last().pose();

        char[] chars = s.toCharArray();
        float xOffset = 0;
        float yOffset = 0;
        int lineStart = 0;

        synchronized (this.glyphPageCache) {
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];

                if (gradient) {
                    color = Color.green;
                }

                if (c == '\n') {
                    yOffset += (float) (this.getStringHeight(s.substring(lineStart, i)) * this.scaleMultiplier);
                    xOffset = 0;
                    lineStart = i + 1;
                    continue;
                }

                Glyph glyph = this.getCachedGlyph(c);
                if (glyph != null) {
                    if (glyph.value != ' ') {
                        ResourceLocation i1 = glyph.glyphMap.textureResource;
                        DrawEntry entry = new DrawEntry(xOffset, yOffset, color, glyph);
                        this.glyphPageCache.computeIfAbsent(i1, integer -> new ArrayList<>()).add(entry);
                    }
                    xOffset += glyph.width;
                }

            }
            for (ResourceLocation resourceLocation : this.glyphPageCache.keySet()) {
                RenderSystem.setShaderTexture(0, resourceLocation);
                List<DrawEntry> objects = this.glyphPageCache.get(resourceLocation);
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                for (DrawEntry object : objects) {
                    float xo = object.drawPositionX;
                    float yo = object.drawPositionY;
                    float cr = object.color.getRed() / 255f;
                    float cg = object.color.getGreen() / 255f;
                    float cb = object.color.getBlue() / 255f;
                    Glyph glyph = object.targetGlyph;
                    GlyphMap owner = glyph.glyphMap;
                    float w = glyph.width;
                    float h = glyph.height;
                    float u1 = (float) glyph.x / owner.width;
                    float v1 = (float) glyph.y / owner.height;
                    float u2 = (float) (glyph.x + glyph.width) / owner.width;
                    float v2 = (float) (glyph.y + glyph.height) / owner.height;
                    bufferBuilder.vertex(matrix4f, xo, yo + h, 0).uv(u1, v2).color(cr, cg, cb, a).endVertex();
                    bufferBuilder.vertex(matrix4f, xo + w, yo + h, 0).uv(u2, v2).color(cr, cg, cb, a).endVertex();
                    bufferBuilder.vertex(matrix4f, xo + w, yo, 0).uv(u2, v1).color(cr, cg, cb, a).endVertex();
                    bufferBuilder.vertex(matrix4f, xo, yo, 0).uv(u1, v1).color(cr, cg, cb, a).endVertex();
                }
                tesselator.end();
            }
            this.glyphPageCache.clear();
        }

        if (wasBlendEnabled) {
            RenderSystem.enableBlend();
        } else {
            RenderSystem.disableBlend();
        }

        RenderSystem.enableCull();
        RenderSystem.setShaderTexture(0, originalTextureId);
        RenderSystem.setShaderColor(originalColorValues[0], originalColorValues[1], originalColorValues[2], originalColorValues[3]);
        stack.popPose();
    }

    public double getStringWidth(final String text) {
        if (text == null) {
            return 0;
        }
        char[] c = removeControlCharacters(text).toCharArray();
        float currentLine = 0;
        float maxPreviousLines = 0;
        for (char c1 : c) {
            if (c1 == '\n') {
                maxPreviousLines = Math.max(currentLine, maxPreviousLines);
                currentLine = 0;
                continue;
            }
            Glyph glyph = this.getCachedGlyph(c1);
            currentLine += glyph == null ? 0 : (glyph.width / (float) this.scaleMultiplier);
        }
        return Math.max(currentLine, maxPreviousLines);
    }

    public double getStringHeight(final String text) {
        char[] c = removeControlCharacters(text).toCharArray();
        if (c.length == 0) {
            c = new char[]{' '};
        }
        float currentLine = 0;
        float previous = 0;
        for (char c1 : c) {
            if (c1 == '\n') {
                if (currentLine == 0) {
                    currentLine = (this.getCachedGlyph(' ') == null ? 0 : (Objects.requireNonNull(this.getCachedGlyph(' ')).height / (float) this.scaleMultiplier));
                }
                previous += currentLine;
                currentLine = 0;
                continue;
            }
            Glyph glyph = this.getCachedGlyph(c1);
            currentLine = Math.max(glyph == null ? 0 : (glyph.height / (float) this.scaleMultiplier), currentLine);
        }
        return currentLine + previous;
    }

    @Override
    public void close() {
        try {
            if (this.prebakeGlyphsFuture != null && !this.prebakeGlyphsFuture.isDone() && !this.prebakeGlyphsFuture.isCancelled()) {
                this.prebakeGlyphsFuture.cancel(true);
                this.prebakeGlyphsFuture.get();
                this.prebakeGlyphsFuture = null;
            }
            for (GlyphMap map : this.glyphMaps) {
                map.destroy();
            }
            this.glyphMaps.clear();
            this.glyphCache.clear();
        } catch (ExecutionException | InterruptedException e) {
            e.fillInStackTrace();
        }
    }

    public static class DrawEntry {
        private final float drawPositionX;
        private final float drawPositionY;
        private final Color color;
        private final Glyph targetGlyph;

        public DrawEntry(final float atX, final float atY, final Color color, final Glyph toDraw) {
            this.drawPositionX = atX;
            this.drawPositionY = atY;
            this.color = color;
            this.targetGlyph = toDraw;
        }

    }

    public static class GlyphMap {
        private final char startCharacter, endCharacter;
        private final Font font;
        private final ResourceLocation textureResource;
        private final int pixelPadding;
        private final Map<Character, Glyph> glyphs = new HashMap<>();
        private int width, height;

        private boolean generated = false;

        public GlyphMap(final char from, final char to, final Font font, final ResourceLocation ResourceLocation, final int padding) {
            this.startCharacter = from;
            this.endCharacter = to;
            this.font = font;
            this.textureResource = ResourceLocation;
            this.pixelPadding = padding;
        }

        public static void registerTextureFromImage(final ResourceLocation textureLocation, final BufferedImage image) {
            try {
                int ow = image.getWidth();
                int oh = image.getHeight();
                NativeImage image1 = new NativeImage(NativeImage.Format.RGBA, ow, oh, false);
                Field ptr1 = ReflectionHelper.findField("com.mojang.blaze3d.platform.NativeImage", "pixels", "f_84964_");
                if (ptr1 == null) {
                    return;
                }
                long ptr = (long) ptr1.get(image1);
                IntBuffer backingBuffer = MemoryUtil.memIntBuffer(ptr, image1.getWidth() * image1.getHeight());
                Object _d;
                WritableRaster _ra = image.getRaster();
                ColorModel _cm = image.getColorModel();
                int colorChannelCount = _ra.getNumBands();
                int dataType = _ra.getDataBuffer().getDataType();
                _d = switch (dataType) {
                    case DataBuffer.TYPE_BYTE -> new byte[colorChannelCount];
                    case DataBuffer.TYPE_USHORT -> new short[colorChannelCount];
                    case DataBuffer.TYPE_INT -> new int[colorChannelCount];
                    case DataBuffer.TYPE_FLOAT -> new float[colorChannelCount];
                    case DataBuffer.TYPE_DOUBLE -> new double[colorChannelCount];
                    default -> throw new Exception("Unknown buffer type: " + dataType);
                };

                for (int y = 0; y < oh; y++) {
                    for (int x = 0; x < ow; x++) {
                        _ra.getDataElements(x, y, _d);
                        int a = _cm.getAlpha(_d);
                        int r = _cm.getRed(_d);
                        int g = _cm.getGreen(_d);
                        int b = _cm.getBlue(_d);
                        backingBuffer.put(a << 24 | b << 16 | g << 8 | r);
                    }
                }
                DynamicTexture tex = new DynamicTexture(image1);
                tex.upload();
                if (RenderSystem.isOnRenderThread()) {
                    Wrapper.mc().getTextureManager().register(textureLocation, tex);
                } else {
                    RenderSystem.recordRenderCall(() -> Wrapper.mc().getTextureManager().register(textureLocation, tex));
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        public Glyph getGlyph(final char c) {
            if (!this.generated) {
                this.generate();
            }
            return this.glyphs.get(c);
        }

        public void destroy() {
            Wrapper.mc().getTextureManager().release(this.textureResource);
            this.glyphs.clear();
            this.width = -1;
            this.height = -1;
            this.generated = false;
        }

        public boolean contains(final char c) {
            return c >= this.startCharacter && c < this.endCharacter;
        }

        private Font getFontForGlyph(final char c) {
            this.font.canDisplay(c);
            return this.font;
        }

        public void generate() {
            if (this.generated) {
                return;
            }
            int range = this.endCharacter - this.startCharacter - 1;
            int charsVert = (int) (Math.ceil(Math.sqrt(range)) * 1.5);
            this.glyphs.clear();
            int generatedChars = 0;
            int charNX = 0;
            int maxX = 0, maxY = 0;
            int currentX = 0, currentY = 0;
            int currentRowMaxY = 0;
            List<Glyph> glyphs1 = new ArrayList<>();
            AffineTransform af = new AffineTransform();
            FontRenderContext frc = new FontRenderContext(af, true, false);
            while (generatedChars <= range) {
                char currentChar = (char) (this.startCharacter + generatedChars);
                Rectangle2D stringBounds = this.getFontForGlyph(currentChar).getStringBounds(String.valueOf(currentChar), frc);

                int width = (int) Math.ceil(stringBounds.getWidth());
                int height = (int) Math.ceil(stringBounds.getHeight());
                generatedChars++;
                maxX = Math.max(maxX, currentX + width);
                maxY = Math.max(maxY, currentY + height);
                if (charNX >= charsVert) {
                    currentX = 0;
                    currentY += currentRowMaxY + this.pixelPadding;
                    charNX = 0;
                    currentRowMaxY = 0;
                }
                currentRowMaxY = Math.max(currentRowMaxY, height);
                glyphs1.add(new Glyph(currentX, currentY, width, height, currentChar, this));
                currentX += width + this.pixelPadding;
                charNX++;
            }
            BufferedImage bi = new BufferedImage(Math.max(maxX + this.pixelPadding, 1), Math.max(maxY + this.pixelPadding, 1),
                    BufferedImage.TYPE_INT_ARGB);
            this.width = bi.getWidth();
            this.height = bi.getHeight();
            Graphics2D g2d = bi.createGraphics();
            g2d.setColor(new Color(255, 255, 255, 0));
            g2d.fillRect(0, 0, this.width, this.height);
            g2d.setColor(Color.WHITE);

            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            for (Glyph glyph : glyphs1) {
                g2d.setFont(this.getFontForGlyph(glyph.value));
                FontMetrics fontMetrics = g2d.getFontMetrics();
                g2d.drawString(String.valueOf(glyph.value), glyph.x, glyph.y + fontMetrics.getAscent());
                this.glyphs.put(glyph.value, glyph);
            }
            registerTextureFromImage(this.textureResource, bi);
            this.generated = true;
        }
    }

    public static class Glyph {
        public int x;
        public int y;
        public int width;
        public int height;
        public char value;
        public GlyphMap glyphMap;

        public Glyph(final int x, final int y, final int width, final int height, final char value, final GlyphMap glyphMap) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.value = value;
            this.glyphMap = glyphMap;
        }
    }

}