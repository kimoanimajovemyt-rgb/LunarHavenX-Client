package com.lunarhavenx.client.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public final class LunarUi {
   public static final int TEXT = -919553;
   public static final int MUTED = -6572852;
   public static final int ACCENT = -11150849;
   public static final int PANEL = -385280213;
   public static final int PANEL_SOFT = -922017736;
   public static final int BORDER = -14400674;
   private static final Map TEXTURE_CACHE = new HashMap();
   private static final ResourceLocation MENU_TEXTURE = new ResourceLocation(
           "lunarhavenx", "textures/gui/lunarhavenx_menu_background.png");
   private static final int MENU_TEXTURE_WIDTH = 1280;
   private static final int MENU_TEXTURE_HEIGHT = 720;
   private static boolean textureWarningLogged;

   public static void drawPanel(int x, int y, int width, int height, boolean highlighted) {
      int edge = highlighted?-11150849:-14400674;
      Gui.drawRect(x, y, x + width, y + height, edge);
      Gui.drawRect(x + 1, y + 1, x + width - 1, y + height - 1, -385280213);
      drawTexturePatch(x + 2, y + 2, width - 4, height - 4, 0, 0, highlighted?0.10F:0.07F);
      Gui.drawRect(x + 1, y + 1, x + width - 1, y + 2, highlighted?-7541761:-13543819);
   }

   /**
    * Draws a low-opacity crop of the decorative background inside a UI panel.
    * Missing or unreadable art falls back to the solid panel already drawn.
    */
   public static void drawTexturePatch(int x, int y, int width, int height,
                                       int textureX, int textureY, float alpha) {
      if(width <= 0 || height <= 0
              || textureX < 0 || textureY < 0
              || textureX + width > MENU_TEXTURE_WIDTH
              || textureY + height > MENU_TEXTURE_HEIGHT
              || !hasTexture(MENU_TEXTURE)) {
         return;
      }

      try {
         Minecraft mc = Minecraft.getMinecraft();
         mc.getTextureManager().bindTexture(MENU_TEXTURE);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         float safeAlpha = Math.max(0.0F, Math.min(1.0F, alpha));
         GlStateManager.color(1.0F, 1.0F, 1.0F, safeAlpha);
         Gui.drawModalRectWithCustomSizedTexture(x, y, (float)textureX, (float)textureY,
                 width, height, (float)MENU_TEXTURE_WIDTH, (float)MENU_TEXTURE_HEIGHT);
      } catch (RuntimeException error) {
         if(!textureWarningLogged) {
            textureWarningLogged = true;
            System.err.println("[LunarHavenX] Textura decorativa indisponível; usando painéis sólidos.");
         }
      } finally {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public static void drawHeading(String title, String subtitle, int width, int y) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.fontRendererObj.drawStringWithShadow(title, (float)(width - mc.fontRendererObj.getStringWidth(title)) / 2.0F, (float)y, -919553);
      mc.fontRendererObj.drawStringWithShadow(subtitle, (float)(width - mc.fontRendererObj.getStringWidth(subtitle)) / 2.0F, (float)(y + 17), -11150849);
   }

   public static void drawSection(String label, int x, int y) {
      Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(label, (float)x, (float)y, -11150849);
   }

   public static void drawDivider(int x, int y, int width) {
      Gui.drawRect(x, y, x + width, y + 1, -2008057706);
   }

   public static boolean hasTexture(ResourceLocation location) {
      Boolean cached = (Boolean)TEXTURE_CACHE.get(location);
      if(cached != null) {
         return cached.booleanValue();
      } else {
         boolean available;
         try {
            Minecraft.getMinecraft().getResourceManager().getResource(location);
            available = true;
         } catch (IOException var4) {
            available = false;
         }

         TEXTURE_CACHE.put(location, Boolean.valueOf(available));
         return available;
      }
   }
}
