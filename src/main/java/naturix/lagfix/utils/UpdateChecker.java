package naturix.lagfix.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import akka.Main;
import naturix.lagfix.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class UpdateChecker implements Runnable {

    private static boolean updateCheck = false;
    private static boolean done = false;
    private static String updateVersion = null;
    private static String updateDownload = null;

    public static void check() {
        if(!Config.CheckForUpdates) {
            done = true;
            return;
        }
        if(!updateCheck) {
            updateCheck = true;
            checkForced();
        }
    }

    public static void checkForced() {
        done = false;
        Thread thread = new Thread(new UpdateChecker(), naturix.lagfix.Main.MODID +" Update Checker");
        thread.setDaemon(true);
        thread.start();
    }

    public static void showChat(EntityPlayerSP player) {
        if(!done) return;
        if(updateVersion == null && updateDownload == null) return;
        if(!naturix.lagfix.Main.isSinglePlayer) return;

        TextComponentTranslation title = new TextComponentTranslation("betterfps.update.available", updateVersion);
        title.setStyle(title.getStyle().setColor(TextFormatting.GREEN).setBold(true));

        TextComponentString buttons = new TextComponentString("  ");
        buttons.setStyle(buttons.getStyle().setColor(TextFormatting.YELLOW));
        buttons.appendSibling(createButton(updateDownload, "betterfps.update.button.download"));
        buttons.appendText("  ");
        buttons.appendSibling(createButton(naturix.lagfix.Main.URL, "betterfps.update.button.more"));

        int phrase = (int)(Math.random() * 12) + 1;
        TextComponentTranslation desc = new TextComponentTranslation("betterfps.update.phrase." + phrase);
        desc.setStyle(desc.getStyle().setColor(TextFormatting.GRAY));

        if(updateVersion.length() < 8) {
            title.appendSibling(buttons);
            player.sendStatusMessage(title, false);
            player.sendStatusMessage(desc, false);
        } else {
            player.sendStatusMessage(title, false);
            player.sendStatusMessage(buttons, false);
            player.sendStatusMessage(desc, false);
        }

        updateVersion = null;
        updateDownload = null;
    }

    private static void showConsole() {
        if(!done) return;
        if(updateVersion == null && updateDownload == null) return;

        naturix.lagfix.Main.logger.info(naturix.lagfix.Main.NAME +" " + updateVersion + " is available");
        naturix.lagfix.Main.logger.info("Download: " + updateDownload);
        naturix.lagfix.Main.logger.info("More: " + naturix.lagfix.Main.URL);

        updateVersion = null;
        updateDownload = null;
    }

    private static TextComponentTranslation createButton(String link, String key) {
        TextComponentTranslation sib = new TextComponentTranslation(key);
        Style style = sib.getStyle();
        style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        TextComponentTranslation h = new TextComponentTranslation(key + ".info");
        h.setStyle(h.getStyle().setColor(TextFormatting.RED));
        style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, h));
        sib.setStyle(style);
        return sib;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(naturix.lagfix.Main.UPDATE_URL);
            InputStream in = url.openStream();
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(new InputStreamReader(in)).getAsJsonObject();
            JsonObject versions = obj.getAsJsonObject("versions");

            if(!versions.has(naturix.lagfix.Main.VERSION)) return;
            JsonArray array = versions.getAsJsonArray(naturix.lagfix.Main.VERSION);
            if(array.size() == 0) return;

            JsonObject latest = array.get(0).getAsJsonObject();
            String version = latest.get("name").getAsString();

            if(!version.contains(naturix.lagfix.Main.VERSION)) {
                updateVersion = version;
                updateDownload = latest.get("url").getAsString();
            }

            done = true;

            if(!naturix.lagfix.Main.isSinglePlayer) {
                showConsole();
            } else {
                if(Minecraft.getMinecraft().player != null) {
                    showChat(Minecraft.getMinecraft().player);
                }
            }
        } catch(Exception ex) {
        	naturix.lagfix.Main.logger.warn("Could not check for updates: " + ex.getMessage());
        } finally {
            done = true;
        }
    }
}