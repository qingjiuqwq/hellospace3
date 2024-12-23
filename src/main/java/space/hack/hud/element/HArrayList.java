/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.hud.element;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
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

    public final ModeValue tagsStyle;
    public final ModeValue titleStyle;

    public HArrayList() {
        super("ArrayList");
        this.tagsStyle = new ModeValue("TagsStyle", new Mode("Default", true), new Mode("One"), new Mode("Two"), new Mode("Three"), new Mode("Four"), new Mode("Five"));
        this.titleStyle = new ModeValue("TitleStyle", this.tagsStyle.getModes());
        this.addValue(this.tagsStyle, this.titleStyle);
    }

    @Override
    public void onRender(final PoseStack event, final boolean debug) {
        String title = GetTags(this.tagsStyle.getMode(), "Nirvana", "Space3");
        LivingEntity target = ValidUtils.SimpleUpdate(360, 5, "Range", 10);

        if (target != null){
            title = GetTags(this.tagsStyle.getMode(), "Nirvana", Utils.getDistance(target, true));
        }else {
            if (Wrapper.player().getHealth() < (Wrapper.player().getMaxHealth() / 3)) {
                title = GetTags(this.tagsStyle.getMode(), "Nirvana", Utils.health(Wrapper.player().getHealth()));
            }
        }

        this.drawString(event, title, 12, this.x, this.y, Utils.rainbow(10, 2).getRGB());
        int index = this.y + 11;

        for (Hack h : HackManager.getSortedHacks()) {
            if (!h.isToggled()) {
                continue;
            }
            String modeName = "";
            for (final Value<?> value : h.getValues()) {
                if (value instanceof ModeValue modeValue) {
                    if (modeValue.getName().equals("Mode")) {
                        modeName = GetTags(this.titleStyle.getMode(), modeName, modeValue.getMode(), ChatFormatting.GRAY);
                    }
                }
            }
            this.drawString(event, h.getName() + modeName, 9, this.x + 1, index + 3, Color.WHITE.getRGB());
            index += Wrapper.font().lineHeight;
        }

    }

    public String GetTags(final String tags, final String front, final String behind, final ChatFormatting color){
        return switch (tags) {
            case "One" -> front + (color != null ? color : "")  + " " + behind;
            case "Two" -> front + (color != null ? color : "") + " - " + behind;
            case "Three" -> front + (color != null ? color : "") + " (" + behind + ")";
            case "Four" -> front + (color != null ? color : "") + " [" + behind + "]";
            case "Five" -> front + (color != null ? color : "") + " <" + behind + ">";
            default -> front + (color != null ? color : "") + " | " + behind;
        };
    }
    public String GetTags(final String tags, final String front, final String behind){
        return this.GetTags(tags, front, behind, null);
    }
}