package tc.oc.controls;

import org.lwjgl.input.Keyboard;

import tc.oc.AresData;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class ControlsAres {
    protected static Class forgeCommonHandlerClass;
    protected boolean forgeDetected = false;
    private static boolean wasAccessed = false;

    public ControlsAres() {
        // Look for Forge
        try {
            this.forgeCommonHandlerClass = Class.forName("cpw.mods.fml.common.FMLCommonHandler");

            if(this.forgeCommonHandlerClass != null) {
                this.forgeDetected = true;
            }
        } catch(Exception ex) {
        }
    }

    public void onTickInGUI(float tick, Minecraft mc, GuiScreen screen) {
        if(!this.forgeDetected) { // If Forge detected no need for better controls, if not then open better controls when controls is open.
            if(mc.currentScreen instanceof GuiControls && !(mc.currentScreen instanceof GuiAresControls)) {
                mc.displayGuiScreen(new GuiAresControls(new GuiOptions((GuiScreen)null, mc.gameSettings), mc.gameSettings));
                wasAccessed = true;
            }
            if(wasAccessed && !(mc.currentScreen instanceof GuiAresControls)) {
                wasAccessed = false;

                if (!Keyboard.getKeyName(AresData.keybind.keyCode).toUpperCase().equals(mod_Ares.CONFIG.keyGui.toUpperCase())) {
                    mod_Ares.CONFIG.setProperty("keyGui", Keyboard.getKeyName(AresData.keybind.keyCode).toUpperCase());
                    mod_Ares.CONFIG.saveConfig();
                }

                if (!Keyboard.getKeyName(AresData.keybind2.keyCode).toUpperCase().equals(mod_Ares.CONFIG.keyGui2.toUpperCase())) {
                    mod_Ares.CONFIG.setProperty("keyGui2", Keyboard.getKeyName(AresData.keybind2.keyCode).toUpperCase());
                    mod_Ares.CONFIG.saveConfig();
                }

                if (!Keyboard.getKeyName(AresData.keybind3.keyCode).toUpperCase().equals(mod_Ares.CONFIG.keyGui3.toUpperCase())) {
                    mod_Ares.CONFIG.setProperty("keyGui3", Keyboard.getKeyName(AresData.keybind3.keyCode).toUpperCase());
                    mod_Ares.CONFIG.saveConfig();
                }
            }
        }
    }
}
