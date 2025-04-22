package space.utils.vec.rot;

import net.minecraft.network.protocol.Packet;
import space.utils.Wrapper;

import java.util.ArrayList;

public class LegalPacket {
    public final ArrayList<Packet<?>> legalPacket;

    public LegalPacket() {
        this.legalPacket = new ArrayList<>();
    }

    public void clear() {
        this.legalPacket.clear();
    }

    public void add(Packet<?> packet) {
        this.legalPacket.add(packet);
        Wrapper.sendPacket(packet);
    }

    public int size() {
        return this.legalPacket.size();
    }

    public Packet<?> get(final int index) {
        return this.legalPacket.get(index);
    }

    public void remove(final int index) {
        this.legalPacket.remove(index);
    }


}
