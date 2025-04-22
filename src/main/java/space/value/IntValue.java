/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.value;

public class IntValue extends Value<Integer> {
    public final Integer Default;
    protected final Integer min;
    protected final Integer max;

    public IntValue(final String name, final int defaultValue, final int min, final int max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.Default = defaultValue;
    }

    public Integer getValue() {
        return super.getValue();
    }

    public void setValue(final int value) {
        if (value > this.max) {
            this.value = this.max;
        } else if (value < this.min) {
            this.value = this.min;
        } else {
            this.value = value;
        }
    }

    public Integer getMin() {
        return this.min;
    }

    public Integer getMax() {
        return this.max;
    }

    public Integer getDefault() {
        return Default;
    }
}
