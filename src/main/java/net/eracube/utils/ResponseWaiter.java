package net.eracube.utils;

import net.eracube.Artemis;
import net.eracube.commons.packets.Packet;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ResponseWaiter {
    public static Packet waitResponse(Artemis artemis, String tag, Class<?> responseType, int checkerTime,
                                      TimeUnit timeUnit, int maxTimeout, Exception exception) {
        AtomicReference<Packet> packet = new AtomicReference<>();
        AtomicBoolean response = new AtomicBoolean(false);
        AtomicInteger timeout = new AtomicInteger(0);

        artemis.getPacketManager().addReceiver(tag, (p) -> {
            packet.set((Packet) p);
            response.set(true);
        });


        artemis.getScheduledExecutorService().scheduleAtFixedRate(() -> {
            if(!response.get()) {
                if(timeout.addAndGet(1) == maxTimeout) {
                    try {
                        throw exception;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, checkerTime, timeUnit);

        if(packet.get().getClass() != responseType) {
            throw new IllegalArgumentException("The received packet isn't as the response type class required!");
        }

        artemis.getPacketManager().removeReceiver(tag);

        return packet.get();
    }
}
