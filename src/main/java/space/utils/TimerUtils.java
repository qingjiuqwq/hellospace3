/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils;

import space.value.IntValue;

public class TimerUtils {
    public long lastMS;

    public TimerUtils() {
        this.lastMS = 0L;
    }

    public boolean isDelay(final long delay) {
        int j1 = Utils.random(1, 50);
        int k1 = Utils.random(1, 60);
        int l1 = Utils.random(1, 70);
        return this.isDelayX((1000 + j1 - k1 + l1) / delay);
    }

    public boolean isDelay(final int min, final int max) {
        return this.isDelay(Utils.random(min, max));
    }

    public boolean isDelayX(final long delay) {
        return this.getDelay() >= delay;
    }

    public boolean isDelayX(final int min, final int max) {
        return this.isDelayX(Utils.random(min, max));
    }

    public boolean isDelayX(final IntValue min, final IntValue max) {
        return this.isDelayX(min.getValue(), max.getValue());
    }

    public long getDelay() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public void setLastMS() {
        this.lastMS = System.currentTimeMillis();
    }

}
