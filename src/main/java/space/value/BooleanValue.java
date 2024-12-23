/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.value;

public class BooleanValue extends Value<Boolean>
{
    public final Boolean Default;

    public BooleanValue(final String name, final Boolean defaultValue) {
        super(name, defaultValue);
        this.Default = defaultValue;
    }

    public Boolean getDefault() {
        return Default;
    }
}
