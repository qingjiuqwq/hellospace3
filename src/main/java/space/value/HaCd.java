/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.value;

import java.util.ArrayList;

public class HaCd {

    public int key;
    public String name;
    public final ArrayList<Value> values;

    public HaCd(final String name) {
        this.key = 0;
        this.name = name;
        this.values = new ArrayList<>();
    }

    public void addValue(final Value... values) {
        for (final Value value : values) {
            this.getValues().add(value);
        }
    }

    public ArrayList<Value> getValues() {
        return this.values;
    }

    public void setKey(final int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void inBooleanValue(final String BooleanValue) {
        for (final Value<?> value : this.getValues()) {
            if (value instanceof BooleanValue ff) {
                if (ff.getName().equalsIgnoreCase(BooleanValue)) {
                    ff.setValue(!ff.getValue());
                }
            }
        }
    }

    public void inBooleanValue(final String BooleanValue, final boolean set) {
        for (final Value<?> value : this.getValues()) {
            if (value instanceof BooleanValue ff) {
                if (ff.getName().equalsIgnoreCase(BooleanValue)) {
                    ff.setValue(set);
                }
            }
        }
    }

    public Integer isIntValue(final String IntValue) {
        for (final Value<?> value : this.getValues()) {
            if (value instanceof IntValue ff) {
                if (ff.getName().equalsIgnoreCase(IntValue)) {
                    return ff.getValue();
                }
            }

        }
        return -1;
    }


    public Double isNumberValue(final String IntValue) {
        for (final Value<?> value : this.getValues()) {
            if (value instanceof NumberValue ff) {
                if (ff.getName().equalsIgnoreCase(IntValue)) {
                    return ff.getValue();
                }
            }

        }
        return -1.0;
    }

    public String isToggledMode(final String Name) {
        for (final Value<?> value : this.getValues()) {
            if (value instanceof ModeValue modeValue) {
                if (modeValue.getName().equals(Name)) {
                    return modeValue.getMode();
                }
            }
        }
        return "";
    }

    public boolean isBooleanValue(final String BooleanValue) {
        for (final Value<?> value : this.getValues()) {
            if (value instanceof BooleanValue ff) {
                if (ff.getName().equalsIgnoreCase(BooleanValue)) {
                    return ff.getValue();
                }
            }

        }
        return false;
    }

}
