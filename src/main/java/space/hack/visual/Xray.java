/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.hack.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import space.hack.Hack;
import space.hack.HackCategory;
import space.utils.RenderUtils;
import space.utils.Wrapper;
import space.value.BooleanValue;
import space.value.IntValue;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Xray extends Hack {
    private final IntValue range;
    private final BooleanValue iron;
    private final BooleanValue gold;
    private final BooleanValue diamond;
    private final BooleanValue emerald;
    private final BooleanValue laps;
    private final BooleanValue redsTone;
    private final BooleanValue coal;
    private final BooleanValue spawner;
    private List<xBook> boxes = new ArrayList<>();

    public Xray() {
        super("Xray", HackCategory.Visual);
        this.range = new IntValue("Range", 20, 5, 50);
        this.iron = new BooleanValue("Iron", true);
        this.gold = new BooleanValue("Gold", true);
        this.diamond = new BooleanValue("Diamond", true);
        this.emerald = new BooleanValue("Emerald", true);
        this.laps = new BooleanValue("Laps", true);
        this.redsTone = new BooleanValue("RedsTone", true);
        this.coal = new BooleanValue("Coal", true);
        this.spawner = new BooleanValue("Spawner", true);
        this.addValue(this.range, this.iron, this.gold, this.diamond, this.emerald, this.laps, this.redsTone, this.coal, this.spawner);
    }

    @Override
    public void onEnable() {
        this.boxes.clear();
    }

    @Override
    public void onAllTick() {
        List<xBook> ren = new ArrayList<>();
        int range = this.range.getValue();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos blockPos = Wrapper.player().blockPosition().offset(x, y, z);
                    BlockState bl = Wrapper.level().getBlockState(blockPos);
                    xBook xb = new xBook();
                    if (iron.getValue() && bl.is(Blocks.IRON_ORE)) {
                        xb.color = new Color(255, 255, 255);
                    } else if (gold.getValue() && bl.is(Blocks.GOLD_ORE)) {
                        xb.color = new Color(255, 255, 0);
                    } else if (diamond.getValue() && bl.is(Blocks.DIAMOND_ORE)) {
                        xb.color = new Color(0, 220, 255);
                    } else if (emerald.getValue() && bl.is(Blocks.EMERALD_ORE)) {
                        xb.color = new Color(35, 255, 0);
                    } else if (laps.getValue() && bl.is(Blocks.LAPIS_ORE)) {
                        xb.color = new Color(0, 50, 255);
                    } else if (redsTone.getValue() && bl.is(Blocks.REDSTONE_ORE)) {
                        xb.color = new Color(255, 0, 0);
                    } else if (coal.getValue() && bl.is(Blocks.COAL_ORE)) {
                        xb.color = new Color(50, 50, 50);
                    } else if (spawner.getValue() && bl.is(Blocks.SPAWNER)) {
                        xb.color = new Color(30, 0, 135);
                    } else {
                        continue;
                    }
                    xb.blockPos = blockPos;
                    ren.add(xb);
                }
            }
        }
        this.boxes = ren;
    }

    @Override
    public void onRender(final PoseStack poseStack) {
        for (xBook xb : this.boxes) {
            RenderUtils.drawBlockESP(poseStack, xb.blockPos, xb.color);
        }
    }

    public static class xBook {
        Color color;
        BlockPos blockPos;
    }
}