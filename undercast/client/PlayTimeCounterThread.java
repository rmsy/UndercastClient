package undercast.client;

import net.minecraft.src.mod_Undercast;

public class PlayTimeCounterThread extends Thread {
    public PlayTimeCounterThread() {
        try {
            // only start the Thread if the playing time is used
            if(mod_Undercast.CONFIG.showPlayingTime) {
                this.start();
            }
        } catch (Exception e) {
            System.out.println("[UndercastMod]: Playing Time Counter crashed");
            System.out.println("[UndercastMod]: ERROR: " + e.toString());
        }
    }
    
    public void run() {
        while (true) {
            try {
                Thread.sleep(60000);
                if(!UndercastData.isOC) {
                    return;
                }
                UndercastData.playTimeMin += 1;
                if(UndercastData.playTimeMin == 60) {
                    UndercastData.playTimeHours += 1;
                    UndercastData.playTimeMin = 0;
                }
            } catch (Exception ignored) {
            }
        }
    }
}
