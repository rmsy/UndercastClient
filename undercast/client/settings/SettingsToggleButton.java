package undercast.client.settings;

import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.GuiButton;
import net.minecraft.src.mod_Undercast;

public class SettingsToggleButton extends GuiButton {
    private String enabledString;
    private String disabledString;
    private String setting;

    public SettingsToggleButton(int par1, int par2, int par3, int par4, int par5,
            String par6Str, String en, String dis, String set) {
        super(par1, par2, par3, par4, par5, par6Str);
        this.enabledString = en;
        this.disabledString = dis;
        this.setting = set;
        this.updateString();
    }

    private void updateString() {
        if (mod_Undercast.CONFIG.getBoolProperty(setting)) {
            this.displayString = EnumChatFormatting.GREEN+enabledString;
        } else {
            this.displayString = EnumChatFormatting.RED+disabledString;
        }
    }

    public void buttonPressed() {
        boolean current = !mod_Undercast.CONFIG.getBoolProperty(setting);
        mod_Undercast.CONFIG.setProperty(setting, current ? "true" : "false");
        mod_Undercast.CONFIG.loadConfigData();
        this.updateString();
    }
}
