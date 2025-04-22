/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.value;

import java.util.ArrayList;

public class SeverMode {

    public final ArrayList<SeverValue> modes;
    public String httpPath;

    public SeverMode() {
        this.modes = new ArrayList<>();
    }

    public boolean isPath(String p1) {
        return p1.equals(this.httpPath);
    }

    public void add(final String name, final String value) {
        modes.add(new SeverValue(name, value));
    }

    public String is(final String name) {
        String value = this.is2(name);
        return value != null ? value : "";
    }

    public String is2(final String name) {
        for (final SeverValue mode : this.modes) {
            if (mode.name.equals(name)) {
                return mode.value;
            }
        }
        return null;
    }

    public boolean bool(final String name, final String value) {
        for (final SeverValue mode : this.modes) {
            if (mode.name.equals(name)) {
                return mode.value.equals(value);
            }
        }
        return false;
    }

    public boolean booc(final String name, final String value) {
        for (final SeverValue mode : this.modes) {
            if (mode.name.equals(name)) {
                return mode.value.contains(value);
            }
        }
        return false;
    }

    public ArrayList<SeverValue> get() {
        return this.modes;
    }

}
