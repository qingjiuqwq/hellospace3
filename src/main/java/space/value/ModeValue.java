/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.value;

import java.util.ArrayList;

public class ModeValue extends Value<Mode> {
    public final String Default;
    private final Mode[] modes;

    public ModeValue(final String modeName, final String... modes) {
        super(modeName, null);
        this.modes = new Mode[modes.length];
        for (int i = 0; i < modes.length; i++) {
            this.modes[i] = new Mode(modes[i]);
        }
        this.modes[0].setToggled(true);
        this.Default = this.getMode();
    }

    public ModeValue(final String modeName, final Mode... modes) {
        super(modeName, null);
        this.modes = modes;
        this.Default = this.getMode();
    }

    public ModeValue(final String modeName, final ArrayList<Mode> modes) {
        super(modeName, null);
        this.modes = modes.toArray(new Mode[0]);
        this.Default = this.getMode();
    }

    public Mode[] getModes() {
        return this.modes;
    }

    public Mode getValue(final String name) {
        for (final Mode mode : this.getModes()) {
            if (mode.getName().equals(name)) {
                return mode;
            }
        }
        return null;
    }

    public String getMode() {
        for (final Mode mode : this.getModes()) {
            if (mode.isToggled()) {
                return mode.getName();
            }
        }
        return "";
    }

    public void setMode(final String name) {
        boolean succeed = false;

        for (final Mode modeS : this.getModes()) {
            if (modeS.getName().equals(name)) {
                succeed = true;
                break;
            }
        }

        if (succeed) {
            for (final Mode modeS : this.getModes()) {
                modeS.setToggled(modeS.getName().equals(name));
            }
        }
    }

    public boolean equals(final String name) {
        return this.getMode().equals(name);
    }

    public Mode getValue() {
        for (final Mode mode : this.getModes()) {
            if (mode.isToggled()) {
                return mode;
            }
        }
        return null;
    }

    public String getDefault() {
        return Default;
    }

    public int getLength() {
        return this.modes.length;
    }
}
