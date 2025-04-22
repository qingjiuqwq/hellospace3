/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.value;

public class Mode {
    private final String name;
    private boolean toggled;

    public Mode(final String name, final boolean toggled) {
        this.name = name;
        this.toggled = toggled;
    }

    public Mode(final String name) {
        this.name = name;
        this.toggled = false;
    }

    public String getName() {
        return this.name;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void setToggled(final boolean toggled) {
        this.toggled = toggled;
    }
}
