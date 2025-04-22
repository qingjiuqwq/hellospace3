/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.hud.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import space.hack.Hack;
import space.hack.hud.Hud;
import space.manager.HackManager;
import space.utils.Utils;
import space.utils.ValidUtils;
import space.utils.Wrapper;
import space.value.Mode;
import space.value.ModeValue;
import space.value.Value;

import java.awt.*;

public class HArrayList extends Hud {

    private final ModeValue tagsStyle;
    private final ModeValue titleStyle;

    public HArrayList() {
        super("ArrayList");
        this.tagsStyle = new ModeValue("TagsStyle", this.getMode());
        this.titleStyle = new ModeValue("TitleStyle", this.getMode());
        this.addValue(this.tagsStyle, this.titleStyle);
    }

    public Mode[] getMode() {
        Mode[] modes = new Mode[6];
        modes[0] = new Mode("Default", true);
        modes[1] = new Mode("One");
        modes[2] = new Mode("Two");
        modes[3] = new Mode("Three");
        modes[4] = new Mode("Four");
        modes[5] = new Mode("Five");
        return modes;
    }

    @Override
    public void onRender(final GuiGraphics event) {
        String title = GetTags(this.tagsStyle.getMode(), "Nirvana", "Space3");
        LivingEntity target = ValidUtils.SimpleUpdate(360, 5, "Range", 10);

        if (target != null) {
            title = GetTags(this.tagsStyle.getMode(), "Nirvana", Utils.getDistance(target, true));
        } else {
            if (Wrapper.player().getHealth() < (Wrapper.player().getMaxHealth() / 3)) {
                title = GetTags(this.tagsStyle.getMode(), "Nirvana", Utils.health(Wrapper.player().getHealth()));
            } else {
                int fps = Wrapper.getFps();
                if (fps != 0 && fps < 120) {
                    title = GetTags(this.tagsStyle.getMode(), "Nirvana", fps + " FPS");
                }
            }
        }

        this.drawString(event, title, 12, this.x, this.y, Utils.rainbow(10, 3));
        int index = this.y + 11;

        for (Hack h : HackManager.getSortedHacks()) {
            if (!h.isToggled()) {
                continue;
            }
            String modeName = "";
            for (final Value<?> value : h.getValues()) {
                if (value instanceof ModeValue modeValue) {
                    if (modeValue.getName().equals("Mode")) {
                        modeName = GetTags(this.titleStyle.getMode(), modeName, modeValue.getMode());
                    }
                }
            }
            double width = this.drawString(event, h.getName(), 9, this.x + 1, index + 3, Color.WHITE).getStringWidth(h.getName());
            this.drawString(event, modeName, 9, width + this.x + 1, index + 3, Color.GRAY);
            index += Wrapper.font().lineHeight;
        }

    }

    public String GetTags(final String tags, final String front, final String behind) {
        return switch (tags) {
            case "One" -> front + " " + behind;
            case "Two" -> front + " - " + behind;
            case "Three" -> front + " (" + behind + ")";
            case "Four" -> front + " [" + behind + "]";
            case "Five" -> front + " <" + behind + ">";
            default -> front + " | " + behind;
        };
    }

}