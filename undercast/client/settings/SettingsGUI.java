package undercast.client.settings;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_Undercast;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
public class SettingsGUI extends GuiScreen {
    @Override
    public void initGui() {
        // Add buttons		
        int x = width / 2 - 75;
        int y = height / 2;
        this.buttonList.add(new GuiButton(1, x, y-50, 150, 20, "Overlay Settings"));
        this.buttonList.add(new GuiButton(2, x, y-20, 150, 20, "General Settings"));
        this.buttonList.add(new GuiButton(3, x, y+40, 150, 20, "Done"));
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();

        int y = height / 2;
        // Draw label at top of screen
        drawCenteredString(fontRenderer, "Undercast mod settings", width / 2, y-80, 0x4444bb);

        // Draw buttons
        super.drawScreen(par1, par2, par3);
    }
    @Override
    protected void keyTyped(char par1, int par2) {
        if (par2 == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 1) {
            ModLoader.openGUI(mc.thePlayer, new OverlaySettings());
        }
        if (guibutton.id == 2) {
            ModLoader.openGUI(mc.thePlayer, new GeneralSettings());
        }
        if (guibutton.id == 3) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}