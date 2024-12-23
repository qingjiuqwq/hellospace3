/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.value;

public class SeverValue {
    public String name;
    public String value;

    public SeverValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public boolean isName(String e1) {
        return this.name.equals(e1);
    }

    public boolean canConvertToDouble() {
        try {
            Double.parseDouble(this.value);
            return true;
        } catch (NumberFormatException ee) {
            return false;
        }
    }

    public boolean canConvertInt() {
        try {
            Integer.parseInt(this.value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int toInteger() {
        return Integer.parseInt(this.value);
    }

    public boolean toBoolean() {
        return Boolean.parseBoolean(this.value);
    }

    public double toDouble() {
        return Double.parseDouble(this.value);
    }

    public boolean canConvertToBool() {
        return this.value.equalsIgnoreCase("true") || this.value.equalsIgnoreCase("false");
    }

}
