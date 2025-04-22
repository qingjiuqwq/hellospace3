/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.value;

public class NumberValue extends Value<Double> {
    public final Double Default;
    protected final Double min;
    protected final Double max;

    public NumberValue(final String name, final Double defaultValue, final Double min, final Double max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.Default = defaultValue;
    }

    public Double getValue() {
        return super.getValue();
    }

    public void setValue(final double value) {
        if (value > this.max) {
            this.value = this.max;
        } else if (value < this.min) {
            this.value = this.min;
        } else {
            this.value = value;
        }
    }

    public Double getMin() {
        return this.min;
    }

    public Double getMax() {
        return this.max;
    }

    public Double getDefault() {
        return Default;
    }
}
