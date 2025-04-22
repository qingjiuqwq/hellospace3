/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.value;

public class TextValue extends Value<String> {

    public TextValue(final String value) {
        super("TextValue", value);
    }

    public TextValue(final int size) {
        super("TextValue", "");
        int size1 = size != -1 ? size : 28;
        this.setValue("-".repeat(Math.max(0, size1)));
    }

}
