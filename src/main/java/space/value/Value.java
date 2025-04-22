/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.value;

public class Value<Object> {
    protected final String name;
    protected Object value;
    protected boolean show;

    public Value(final String name, final Object defaultValue) {
        this.show = true;
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public boolean noShow() {
        return !show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}
