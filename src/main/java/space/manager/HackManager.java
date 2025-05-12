/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.manager;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import space.Core;
import space.hack.Hack;
import space.hack.another.*;
import space.hack.combat.*;
import space.hack.hud.Hud;
import space.hack.hud.element.EnemyInfo;
import space.hack.hud.element.HArrayList;
import space.hack.hud.element.MusicLyrics;
import space.hack.player.*;
import space.hack.visual.*;
import space.mixin.MixinLoader;
import space.utils.Connection;
import space.utils.TimerUtils;
import space.utils.Utils;
import space.utils.Wrapper;
import space.value.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HackManager {

    private final static ArrayList<Hack> hacks = new ArrayList<>();
    private final static ArrayList<Hud> hud = new ArrayList<>();
    private static final TimerUtils timer = new TimerUtils();
    public static ArrayList<SeverMode> severMode = new ArrayList<>();
    private static boolean initialized = false;
    private static ClientLevel warn = null;

    public HackManager() {
        this.addHacks();
    }

    public static boolean noAimAssist() {
        for (final Hack hack : getHack()) {
            if (!hack.aimAssist && hack.isToggled()) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Hack> getHack() {
        return hacks;
    }

    public static ArrayList<Hud> getHud() {
        return hud;
    }

    public static ArrayList<HaCd> getAll() {
        ArrayList<HaCd> list = new ArrayList<>();
        list.addAll(hacks);
        list.addAll(hud);
        return list;
    }

    public static boolean isDefault(final Object value) {
        if (value instanceof BooleanValue ff) {
            return ff.getValue() == ff.getDefault();
        } else if (value instanceof NumberValue ff) {
            return ff.getValue().equals(ff.getDefault());
        } else if (value instanceof IntValue ff) {
            return ff.getValue().equals(ff.getDefault());
        } else if (value instanceof ModeValue ff) {
            return ff.equals(ff.getDefault());
        }
        return true;
    }

    public static void addHack(final Hack hack) {
        hacks.add(hack);
    }

    public static Hack getHackE(final String name) {
        for (final Hack h : getHack()) {
            if (h.getName().equals(name)) {
                return h;
            }
        }
        return null;
    }

    public static HaCd getSearch(final String name) {
        HaCd text = getHackE(name);
        if (text == null) {
            text = getHudE(name);
        }
        return text;
    }

    public static Hud getHudE(final String name) {
        for (final Hud h : getHud()) {
            if (h.name.equals(name)) {
                return h;
            }
        }
        return null;
    }

    public static void onAttack(final Entity event) {
        for (final Hack hack : getHack()) {
            if (hack.isToggled()) {
                hack.onAttack(event);
            }
        }
    }

    public static rPacket onPacket(final Object packet, final Connection.Side side) {
        boolean suc = true;
        ArrayList<onPacket> list = new ArrayList<>();
        for (final Hack hack : HackManager.getHack()) {
            if (hack.isToggled()) {
                if (Wrapper.level() == null) {
                    continue;
                }
                int suc1 = hack.onPacket(packet, side);
                suc &= (suc1 == 1 || suc1 == 2);
                if (suc1 == 2 || suc1 == 3) {
                    onPacket onPacket = new onPacket();
                    onPacket.hack = hack;
                    onPacket.send = (suc1 == 2);
                    onPacket.packet = packet;
                    list.add(onPacket);
                }
            }
        }
        rPacket rPacket = new rPacket();
        rPacket.suc = suc;
        rPacket.onPacket = list;
        return rPacket;
    }

    public static void onAllTick() {
        if (Wrapper.level() != warn) {
            warn = Wrapper.level();
            for (final Hack hack : getHack()) {
                if (hack.warn) {
                    hack.isToggled(false);
                }
            }
        }
        if (Utils.nullCheck()) {
            if (!initialized) {
                initialized = true;
                new Connection();
                if (MixinLoader.flag) {
                    new MixinLoader().init();
                }
            }
            if (timer.isDelay(30)) {
                for (final Hack hack : getHack()) {
                    if (hack.isToggled()) {
                        hack.onAllTick();
                    }
                }
                timer.setLastMS();
            }
        } else {
            initialized = false;
        }
        onSever();
    }

    public static void onSever() {
        for (int i = severMode.size() - 1; i >= 0; i--) {
            SeverMode value = severMode.get(i);
            severMode.remove(i);
            Disposal(value);
        }
    }

    public static void Disposal(final SeverMode severmode) {
        if (severmode.isPath("/set")) {
            if (severmode.booc("Mode", "Value")) {
                HaCd hack = getSearch(severmode.is("Name"));
                if (hack != null) {
                    String param2 = severmode.is("Value");
                    String param3 = severmode.is("Open");
                    for (final Value<?> x : hack.getValues()) {
                        if (severmode.bool("Mode", "ModeValue")) {
                            if (x instanceof ModeValue modeValue && modeValue.getName().equals(param2)) {
                                modeValue.setMode(param3);
                                break;
                            }
                        } else if (severmode.bool("Mode", "BooleanValue")) {
                            if (x instanceof BooleanValue ff && ff.getName().equals(param2)) {
                                ff.setValue(Boolean.valueOf(param3));
                                break;
                            }
                        } else if (severmode.bool("Mode", "NumberValue") || severmode.bool("Mode", "IntValue")) {
                            if (x instanceof NumberValue ff) {
                                if (ff.getName().equals(param2)) {
                                    ff.setValue(Double.valueOf(param3));
                                    break;
                                }
                            } else if (x instanceof IntValue ff) {
                                if (ff.getName().equals(param2)) {
                                    ff.setValue(Integer.valueOf(param3));
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (severmode.booc("Mode", "Bind")) {
                HaCd hack = getSearch(severmode.is("Name"));
                if (hack != null) {
                    hack.setKey(severmode.is("Value"));
                }
            } else if (severmode.booc("Mode", "Hack")) {
                Hack hack = getHackE(severmode.is("Name"));
                if (hack != null) {
                    hack.isToggled(Boolean.parseBoolean(severmode.is("Value")));
                } else {
                    Hud hud = getHudE(severmode.is("Name"));
                    if (hud != null) {
                        hud.isToggled(Boolean.parseBoolean(severmode.is("Value")));
                    }
                }
            }
        } else if (severmode.isPath("/config")) {

            for (final Hud h : getHud()) {
                h.isToggled(false);
                h.upPosOld();
                loadDefault(h);
            }

            for (final Hack h : getHack()) {
                h.isToggled(false);
                loadDefault(h);
            }

            for (final SeverValue mode : severmode.get()) {
                HaCd hacd = getSearch(mode.name);
                String[] parts = mode.value.split("\\[#@@#]");
                if (hacd != null) {
                    for (String part : parts) {
                        extractFirstValue(part, hacd);
                    }
                }
            }

            int width = Wrapper.mc().getWindow().getWidth();
            int height = Wrapper.mc().getWindow().getHeight();

            for (final Hud h : getHud()) {
                if (h.ux >= 0 && h.ux <= width && h.y >= 0 && h.y <= height) {
                    h.x = h.ux;
                    h.y = h.uy;
                }
            }

        }
    }

    public static void loadDefault(HaCd h) {
        h.setKey(0);
        for (final Value<?> x : h.getValues()) {
            if (x instanceof NumberValue ff) {
                ff.setValue(ff.getDefault());
            } else if (x instanceof IntValue ff) {
                ff.setValue(ff.getDefault());
            } else if (x instanceof BooleanValue ff) {
                ff.setValue(ff.getDefault());
            } else if (x instanceof ModeValue ff) {
                ff.setMode(ff.getDefault());
            }
        }
    }

    public static void extractFirstValue(final String input, final Object object) {
        Pattern pattern = Pattern.compile("\\[#\\((.*?)\\[#&&#](.*?)\\)#]");
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            return;
        }
        SeverValue value = new SeverValue(matcher.group(1).trim(), matcher.group(2).trim());
        HaCd hacd = (HaCd) object;
        if (value.isName("Key")) {
            hacd.setKey(value.value);
            return;
        } else if (value.name.equals("Open")) {
            if (value.canConvertToBool()) {
                if (hacd instanceof Hack ff) {
                    ff.isToggled(value.toBoolean());
                } else if (hacd instanceof Hud ff) {
                    ff.isToggled(value.toBoolean());
                }
                return;
            }
        } else if (hacd instanceof Hud ff && value.canConvertInt()) {
            if (value.name.equals("PosX")) {
                ff.ux = value.toInteger();
            } else if (value.name.equals("PosY")) {
                ff.uy = value.toInteger();
            }
        }

        for (final Value<?> x : hacd.getValues()) {
            if (x instanceof NumberValue ff) {
                if (value.isName(ff.getName())) {
                    if (value.canConvertToDouble()) {
                        ff.setValue(value.toDouble());
                        return;
                    }
                }
            } else if (x instanceof IntValue ff) {
                if (value.isName(ff.getName())) {
                    if (value.canConvertInt()) {
                        ff.setValue(value.toInteger());
                        return;
                    }
                }
            } else if (x instanceof BooleanValue ff) {
                if (value.isName(ff.getName())) {
                    if (value.canConvertToBool()) {
                        ff.setValue(value.toBoolean());
                        return;
                    }
                }
            } else if (x instanceof ModeValue modeValue) {
                if (value.name.equals(modeValue.getName())) {
                    modeValue.setMode(value.value);
                    return;
                }
            }
        }

    }

    public static void onMouseInputEvent(final InputEvent.MouseButton event) {
        for (final Hack hack : getHack()) {
            if (hack.isToggled()) {
                hack.onMouseInputEvent(event);
            }
        }
    }

    public static void onLeftClickBlock(final BlockPos event) {
        for (final Hack hack : getHack()) {
            if (hack.isToggled()) {
                hack.onLeftClickBlock(event);
            }
        }
    }

    public static void onCameraSetup(final ViewportEvent.ComputeCameraAngles event) {
        for (final Hack hack : getHack()) {
            if (hack.isToggled()) {
                hack.onCameraSetup(event);
            }
        }
    }

    public static void onRightClickItem(final Object event) {
        for (final Hack hack : getHack()) {
            if (hack.isToggled()) {
                hack.onRightClickItem(event);
            }
        }
    }

    public static void onRender(final GuiGraphics event) {
        for (final Hack hack : getHack()) {
            if (hack.isToggled() || hack.getName().equals("Hud")) {
                hack.onRender(event);
                hack.onRender(event.pose());
            }
        }
    }

    public static void onRender(final PoseStack event) {
        for (final Hack hack : getHack()) {
            if (hack.isToggled() || hack.getName().equals("Hud")) {
                hack.onRender(event);
            }
        }
    }

    public static List<Hack> getSortedHacks() {
        return getHack().stream()
                .filter(Hack::isToggled)
                .filter(Hack::isShow)
                .sorted(Comparator.comparing((Hack hack) -> {
                    StringBuilder sb = new StringBuilder(hack.getName());
                    appendModes(sb, hack);
                    return sb.toString();
                }, Comparator.comparingInt(s -> -s.length())))
                .collect(Collectors.toList());
    }

    private static void appendModes(final StringBuilder sb, final Hack hack) {
        for (Value<?> value : hack.getValues()) {
            if (value instanceof ModeValue modeValue && value.getName().equals("Mode")) {
                for (Mode mode : modeValue.getModes()) {
                    if (mode.isToggled()) {
                        sb.append(" ").append(mode.getName());
                    }
                }
            }
        }
    }

    public void addHacks() {
        //Another
        addHack(new AntiBot());
        addHack(new Sprint());
        addHack(new Flight());
        addHack(new Targets());
        addHack(new Teams());
        addHack(new Speed());
        addHack(new LongJump());

        //Combat
        addHack(new AimAssist());
        addHack(new AutoClicker());
        addHack(new Criticals());
        addHack(new KillAura());
        addHack(new LegalAura());

        //Player
        addHack(new AutoTool());
        addHack(new Eagle());
        addHack(new NoFall());
        addHack(new AutoArmor());
        addHack(new FastPlace());
        addHack(new ChestStealer());

        //Hud
        addHud(new HArrayList());
        addHud(new EnemyInfo());
        addHud(new MusicLyrics());

        //Visual
        addHack(new NightVision());
        addHack(new Profiler());
        //addHack(new Xray());
        addHack(new HudState());
        addHack(new RotSpeed());
        if (Core.mode) {
            addHack(new TestAdmin());
            addHack(new TestAdmin1());
            addHack(new TestAdmin2());
        }
    }

    public void addHud(final Hud mode) {
        hud.add(mode);
    }

    public static class rPacket {
        public boolean suc;
        public ArrayList<onPacket> onPacket;
    }

    public static class onPacket {
        public Hack hack;
        public boolean send;
        public Object packet;
    }
}