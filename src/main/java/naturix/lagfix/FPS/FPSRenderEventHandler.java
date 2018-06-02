package naturix.lagfix.FPS;


import org.lwjgl.opengl.GL11;

import naturix.lagfix.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FPSRenderEventHandler extends Gui {

    private Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.getModID().equals(Main.MODID)) {
            Main.syncConfig();
        }
    }

    @SideOnly(Side.CLIENT) // Don't want to render on server
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.TEXT || !Main.shouldRender) {
            return;
        }

        // All values needed for render position
        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();

        int yPosTop = 2;
        int xPosLeft = 4;
        int yPosDown = height - 4;
        int xPosRight = width - 6;
        int xPosCenter = width / 2 - 4;
        int yPosCenter = height / 2 - 2;

        GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);

        int fpsCount = Minecraft.getDebugFPS();
        if (Main.pos == CounterPosition.TOP_LEFT) {
            this.drawString(mc.fontRenderer, String.valueOf(fpsCount), xPosLeft, yPosTop, Main.counterColorCode);
            return;
        }
        if (Main.pos == CounterPosition.TOP_MIDDLE) {
            this.drawString(mc.fontRenderer, String.valueOf(fpsCount), xPosCenter, yPosTop, Main.counterColorCode);
            return;
        }
        if (Main.pos == CounterPosition.TOP_RIGHT) {
            this.drawString(mc.fontRenderer, String.valueOf(fpsCount), xPosRight, yPosTop, Main.counterColorCode);
            return;
        }
        if (Main.pos == CounterPosition.CENTER_RIGHT) {
            this.drawString(mc.fontRenderer, String.valueOf(fpsCount), xPosRight, yPosCenter, Main.counterColorCode);
            return;
        }
        if (Main.pos == CounterPosition.BOTTOM_RIGHT) {
            this.drawString(mc.fontRenderer, String.valueOf(fpsCount), xPosRight, yPosDown, Main.counterColorCode);
            return;
        }
        if (Main.pos == CounterPosition.BOTTOM_MIDDLE) {
            this.drawString(mc.fontRenderer, String.valueOf(fpsCount), xPosCenter, yPosDown, Main.counterColorCode);
            return;
        }
        if (Main.pos == CounterPosition.BOTTOM_LEFT) {
            this.drawString(mc.fontRenderer, String.valueOf(fpsCount), xPosLeft, yPosDown, Main.counterColorCode);
            return;
        }
        if (Main.pos == CounterPosition.CENTER_LEFT) {
            this.drawString(mc.fontRenderer, String.valueOf(fpsCount), xPosLeft, yPosCenter, Main.counterColorCode);
            return;
        }
        this.drawString(mc.fontRenderer, String.valueOf("?"), xPosLeft, yPosTop, Main.counterColorCode);
    }

}