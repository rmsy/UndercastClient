package net.minecraft.src;
//You may not release this source under any condition, it must be linked to this page
//You may recompile and publish as long as skipperguy12 and Guru_Fraser are given credit
//You may not claim this to be your own
//You may not remove these comments

import net.minecraft.client.Minecraft;
import tc.oc.*;
import tc.oc.AresData.Teams;
import tc.oc.controls.*;
import tc.oc.server.*;
import tc.oc.update.*;
import tc.oc.internetTools.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class mod_Ares extends BaseMod {
    public final static String MOD_VERSION = "1.5.1";
    protected String username = "Not_Found";
    protected Minecraft mc = Minecraft.getMinecraft();
    private boolean deathScreenActive;
    private boolean mainMenuActive;
    public static AresConfig CONFIG;
    public static boolean brightActive;
    public float brightLevel = (float) 20.0D;
    public float defaultLevel = mc.gameSettings.gammaSetting;
    private ControlsAres controlAres;

    @Override
    public String getVersion() {
        return MOD_VERSION;
    }

    @Override
    public void load() {
        // Custom Config
        CONFIG = new AresConfig();

        //main hooks
        ModLoader.setInGUIHook(this, true, false);
        ModLoader.setInGameHook(this, true, false);

        ModLoader.addLocalization("keybind", "gui");
        ModLoader.addLocalization("keybind", "inGameGui");
        ModLoader.addLocalization("keybind", "fullBright");

        //load variables defaults
        new AresData();
        
        //check for update
        new Ares_UpdaterThread();
        
        //load the new controls menu
        controlAres = new ControlsAres();

        //hook keybinds
        ModLoader.registerKey(this, AresData.keybind, false);
        ModLoader.registerKey(this, AresData.keybind2, false);
        ModLoader.registerKey(this, AresData.keybind3, false);
    }

    /**
     * On client chat event this is called.
     * Send all the info to the AresChatHandler
     * NOTE: only sends none global ares messages
     */
    public void clientChat(String var1) {
        try {
            Minecraft mc = ModLoader.getMinecraftInstance();
            EntityPlayer player = mc.thePlayer;
            username = mc.thePlayer.username;
            String message = StringUtils.stripControlCodes(var1);
            // stop global msg to go through
            if(!message.startsWith("<")) {
                new AresChatHandler(message, username, player);
            }
        } catch(Exception e) {
        }
    }

    /**
     * On game tick this is called.
     * Draws the gui ingame based on the config file
     */
    public boolean onTickInGame(float time, Minecraft mc) {
        AresData.update();
        
        //if the game over screen is active then you have died
        //if it is the first time it is active count a death
        //if it is not don't do anything
        if (mc.currentScreen instanceof GuiGameOver) {
            if (!deathScreenActive) {
                AresData.addDeaths(1);
                AresData.resetKillstreak();
                deathScreenActive = true;
            }
            //get the title screen button
            GuiButton titleScreen = (GuiButton) mc.currentScreen.buttonList.get(1);
            //if the button is enabled and the user wants to disable it
            if (titleScreen.enabled && CONFIG.toggleTitleScreenButton) {
                titleScreen.enabled = false;
                mc.currentScreen.buttonList.set(1, titleScreen);
                mc.currentScreen.updateScreen();
            }
        } else {
            deathScreenActive = false;
        }

        //get debug info for the fps
        String fps = mc.debug.split(",")[0];
        int height = CONFIG.x;
        int width = CONFIG.y;
        //if the gui is enabled display
        //if chat is open and config says yes then show gui
        if (AresData.guiShowing && (mc.inGameHasFocus || CONFIG.showGuiChat && mc.currentScreen instanceof GuiChat)) {
            //show fps
            if (CONFIG.showFPS) {
                mc.fontRenderer.drawStringWithShadow(fps, width, height, 0xffff);
                height += 8;
            }
        }
        //if on Ares server then display this info.
        //if chat is open and config says yes then show gui
        if (AresData.isPlayingAres() && AresData.guiShowing && (mc.inGameHasFocus || CONFIG.showGuiChat && mc.currentScreen instanceof GuiChat)) {
            // Server display
            if (CONFIG.showServer) {
                mc.fontRenderer.drawStringWithShadow("Server: \u00A76" + AresData.getServer(), width, height, 16777215);
                height += 8;
            }

            // Team display (based on color)
            if (CONFIG.showTeam && !AresData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("Team: " + AresData.getTeam(), width, height, getTeamColors());
                height += 8;
            }
            // Friend display:
            if (CONFIG.showFriends) {
                mc.fontRenderer.drawStringWithShadow("Friends Online: \u00A73" + AresData.getFriends(), width, height, 16777215);
                height += 8;
            }
            // Map fetcher:
            if (CONFIG.showMap && !AresData.isLobby) {
                if (AresData.getMap() != null) {
                    mc.fontRenderer.drawStringWithShadow("Current Map: \u00A7d" + AresData.getMap(), width, height, 16777215);
                    height += 8;
                } else {
                    AresData.setMap("Fetching...");
                    mc.fontRenderer.drawStringWithShadow("Current Map: \u00A78" + AresData.getMap(), width, height, 16777215);
                    height += 8;
                }
            }
            // Show next map
            if (CONFIG.showNextMap && !AresData.isLobby) {
                if (AresData.getNextMap() != null) {
                    mc.fontRenderer.drawStringWithShadow("Next Map: \u00A7d" + AresData.getNextMap(), width, height, 16777215);
                    height += 8;
                } else {
                    mc.fontRenderer.drawStringWithShadow("Next Map: \u00A78Loading...", width, height, 16777215);
                    height += 8;
                }
            }
            //Show KD Ratio
            if (CONFIG.showKD && !AresData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("K/D: \u00A73" + AresCustomMethods.getKD(), width, height, 16777215);
                height += 8;
            }
            //show KK Ratio
            if (CONFIG.showKK && !AresData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("K/K: \u00A73" + AresCustomMethods.getKK(), width, height, 16777215);
                height += 8;
            }
            //show amount of kills
            if (CONFIG.showKills && !AresData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("Kills: \u00A7a" + AresData.getKills(), width, height, 16777215);
                height += 8;
            }
            //show amount of deaths
            if (CONFIG.showDeaths && !AresData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("Deaths: \u00A74" + AresData.getDeaths(), width, height, 16777215);
                height += 8;
            }
            // Kill Streak display
            if (CONFIG.showStreak && !AresData.isLobby) {
                mc.fontRenderer.drawStringWithShadow("Current Killstreak: \u00A75" + (int)AresData.getKillstreak() + "/" + (int)AresData.getLargestKillstreak(), width, height, 16777215);
                height += 8;
            }
        }
        
        //if you not on obs turn it off
        if(AresData.team != Teams.Observers){
            brightActive=false;
            //if full bright is on turn it off
            if(mc.gameSettings.gammaSetting>=brightLevel){
                mc.gameSettings.gammaSetting=defaultLevel;
                if(defaultLevel>=brightLevel){
                    mc.gameSettings.gammaSetting=(float) 0.0D;
                    defaultLevel=(float) 0.0D;
                }
            }
        }
        
        //gui display for obs if you have brightness
        if(AresData.isPlayingAres() && AresData.guiShowing && (mc.inGameHasFocus || CONFIG.showGuiChat && mc.currentScreen instanceof GuiChat)){
            if(brightActive && CONFIG.fullBright && AresData.team == Teams.Observers){
                mc.fontRenderer.drawStringWithShadow("Full Bright: \u00A72ON", width, height, 16777215);
                 height += 8;
            }else if(!brightActive && CONFIG.fullBright && AresData.team == Teams.Observers){
        	 mc.fontRenderer.drawStringWithShadow("Full Bright: \u00A7cOFF", width, height, 16777215);
                 height += 8;
            }
        }
        return true;
    }
    
    public boolean onTickInGUI(float tick, Minecraft mc, GuiScreen screen){
        controlAres.onTickInGUI(tick, mc, screen);
        AresData.update();
        this.addOvercastButton();
    	return true;
    }

    /**
     * Called on client connect to the server
     * Sets variables if the server is a Ares server
     */
    public void clientConnect(NetClientHandler var1) {
        AresData.setTeam(AresData.Teams.Observers); 
        //if logging onto a project ares server, then enable the main mod
        if (var1.getNetManager().getSocketAddress().toString().contains("us.oc.tc")) {
            // What happens if logs into project ares
            AresData.isPA = true;
            AresData.isLobby = true;
            AresData.guiShowing = true;
            System.out.println("Ares mod activated!");
            AresData.setTeam(AresData.Teams.Observers);
            AresData.isPA = true;
            AresData.setServer("Lobby");
        } else{
            AresData.isPA=false;
        }
        //update notifier
        if(!AresData.isUpdate()){
            Thread thread = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(3000);
                        for(int c = 0; c < 10; c++) { // don't wait longer than 10 sec
                            Thread.sleep(1000);
                            if(Ares_UpdaterThread.finished) {
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                    }
                    mc.thePlayer.addChatMessage("\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-");
                    mc.thePlayer.addChatMessage("[ProjectAres]: A New Version of the Project Ares Mod is avaliable");
                    mc.thePlayer.addChatMessage("[ProjectAres]: Link: \u00A74"+AresData.updateLink);
                    mc.thePlayer.addChatMessage("\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-\u00A7m-");
                }
            };
            thread.start();
        }
    }

    /**
     * Called when client disconnects.
     * Resets all the values
     */
    public void onDisconnect(NetClientHandler handler) {
        AresData.isPA = false;
        AresData.guiShowing = false;
        AresData.setTeam(AresData.Teams.Observers);
        AresData.resetKills();
        AresData.resetKilled();
        AresData.resetDeaths();
        AresData.resetKillstreak();
        AresData.resetLargestKillstreak();
        AresData.setMap("Attempting to fetch map...");
        if(mc.gameSettings.gammaSetting>=brightLevel){
            brightActive=false;
            mc.gameSettings.gammaSetting=defaultLevel;
        }
        // for the next connect
        AresData.welcomeMessageExpected = true;
    }

    /**
     * Called when a key is pressed.
     * Used to activate the gui ect.
     */
    public void keyboardEvent(KeyBinding keybinding) {
        if (mc.inGameHasFocus) {
            if (keybinding == AresData.keybind) {
                AresData.guiShowing = !AresData.guiShowing;
            } else if (keybinding == AresData.keybind2) {
                ModLoader.openGUI(mc.thePlayer, new Ares_ServerGUI(true));
            }
            //if you are an obs;have the config to true; toggle fullbright and play sound
            else if(AresData.isPlayingAres() && keybinding == AresData.keybind3 && AresData.team == Teams.Observers && CONFIG.fullBright){
                if(mc.inGameHasFocus){
                    brightActive = !brightActive;
                    if(brightActive)
                        mc.gameSettings.gammaSetting = brightLevel;
                    else
                        mc.gameSettings.gammaSetting = defaultLevel;
                    mc.sndManager.playSoundFX("random.click", 0.5F, 1.0F);
                }
            }
        }
    }

    /**
     * Returns the team color hex based on the team you are on
     *
     * @return hex value of team color
     */
    public int getTeamColors() {
        if (AresData.getTeam() == AresData.Teams.Red) {
            return 0x990000;
        } else if (AresData.getTeam() == AresData.Teams.Blue) {
            return 0x0033FF;
        } else if (AresData.getTeam() == AresData.Teams.Purple) {
            return 0x9933CC;
        } else if (AresData.getTeam() == AresData.Teams.Cyan) {
            return 0x00FFFF;
        } else if (AresData.getTeam() == AresData.Teams.Lime) {
            return 0x00FF00;
        } else if (AresData.getTeam() == AresData.Teams.Yellow) {
            return 0xFFFF00;
        } else if (AresData.getTeam() == AresData.Teams.Green) {
            return 0x006600;
        } else if (AresData.getTeam() == AresData.Teams.Orange) {
            return 0xFF9900;
        } else if (AresData.getTeam() == AresData.Teams.Observers) {
            return 0x00FFFF;
        } else {
            return 0x606060;
        }
    }

    /**
     * Adds the Overcast Network button
     */
    private void addOvercastButton() {
        // if the main menu is active then add a button
        if (mc.currentScreen instanceof GuiMainMenu && mc.currentScreen.buttonList.size() > 0 && CONFIG.showGuiMulti) {
            //if you have not added the button already then add it
            if (!mainMenuActive) {
                //edit the current multiplayer button
                GuiButton multi = ((GuiButton) mc.currentScreen.buttonList.get(1));
                multi.width = (multi.width / 2) - 1;
                mc.currentScreen.buttonList.set(1, multi);
                //get values
                int y = multi.yPosition;
                int x = multi.xPosition + multi.width + 2;
                int height = multi.height;
                int width = multi.width;
                //add the custom ares button
                AresMenuButton test = new AresMenuButton(-1, x, y, width, height, "Overcast Network", "Serverlist with Overcast Network Servers");
                mc.currentScreen.buttonList.add(test);
                mc.currentScreen.updateScreen();
                mainMenuActive = true;
            }
        } else {
            mainMenuActive = false;
        }
    }
}
