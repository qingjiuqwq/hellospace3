package space.hack.hud.element;

import net.minecraft.client.gui.GuiGraphics;
import space.hack.hud.Hud;
import space.utils.Wrapper;

import java.awt.*;

public class MusicLyrics extends Hud {

    public static String first = "No music playing";
    public static String second = "Please go to Space to play music";

    public MusicLyrics() {
        super("MusicLyrics");
    }

    @Override
    public void onRender(final GuiGraphics event) {
        this.drawString(event, first, this.x, this.y, Color.WHITE);
        this.drawString(event, second, this.x, this.y + Wrapper.font().lineHeight + 1, Color.WHITE);
    }

}
