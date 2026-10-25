package com.lunarhavenx.client.gui;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.config.LunarVisualSettings;
import com.lunarhavenx.client.cosmetics.LunarCosmeticManager;
import com.lunarhavenx.client.gui.GuiLunarMainMenu;
import com.lunarhavenx.client.gui.LunarUi;
import com.lunarhavenx.client.input.LunarKeybindManager;
import com.lunarhavenx.client.mobile.LunarMobileCompatibility;
import com.lunarhavenx.client.performance.LunarPerformanceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiLunarLoading extends GuiScreen {
   private static final ResourceLocation BACKGROUND = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_loading_screen.png");
   private static final ResourceLocation FALLBACK_BACKGROUND = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_menu_background.png");
   private static final ResourceLocation LOGO = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_logo.png");
   private static final int TOTAL_STAGES = 3;
   private final LunarLanguageManager language = LunarLanguageManager.get();
   private long openedAt;
   private int completedStages;
   private boolean prepared;
   private boolean backgroundAvailable;
   private String status;

   public void initGui() {
      this.openedAt = Minecraft.getSystemTime();
      this.prepared = false;
      this.completedStages = 0;
      this.backgroundAvailable = false;
      this.status = this.language.text("loadingPrepare");
   }

   private void advanceLoading() {
      if(!this.prepared) {
         switch(this.completedStages) {
         case 0:
          this.backgroundAvailable = LunarUi.hasTexture(BACKGROUND);
            this.status = this.backgroundAvailable?this.language.text("loadingResources"):this.language.text("loadingFallback");
            break;
         case 1:
            LunarVisualSettings.get();
            LunarPerformanceManager.get();
            this.status = this.language.text("loadingPreferences");
            break;
         case 2:
            LunarKeybindManager.get();
            LunarCosmeticManager.get();
            this.status = this.language.text("loadingReady");
            break;
         default:
            this.prepared = true;
            this.status = this.backgroundAvailable?this.language.text("loadingPlay"):this.language.text("loadingFallbackReady");
            return;
         }

         ++this.completedStages;
         if(this.completedStages >= 3) {
            this.prepared = true;
            this.status = this.backgroundAvailable?this.language.text("loadingPlay"):this.language.text("loadingFallbackReady");
         }

      }
   }

   public void updateScreen() {
      this.advanceLoading();
      if(this.prepared && Minecraft.getSystemTime() - this.openedAt > 1250L) {
         Minecraft.getMinecraft().displayGuiScreen(new GuiLunarMainMenu());
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if(!this.backgroundAvailable && this.completedStages == 0) {
         this.backgroundAvailable = LunarUi.hasTexture(BACKGROUND);
      }

       ResourceLocation background = this.backgroundAvailable ? BACKGROUND : FALLBACK_BACKGROUND;
       boolean drawn = this.drawBackgroundTexture(background);
       if(!drawn && background != FALLBACK_BACKGROUND) {
          drawn = this.drawBackgroundTexture(FALLBACK_BACKGROUND);
       }
       if(!drawn) {
         drawRect(0, 0, this.width, this.height, -16446684);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

    private boolean drawBackgroundTexture(ResourceLocation texture) {
       if(!LunarUi.hasTexture(texture)) {
          return false;
       }

       try {
          this.mc.getTextureManager().bindTexture(texture);
          GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
          drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F,
                  this.width, this.height, 1280.0F, 720.0F);
          return true;
       } catch (RuntimeException ignored) {
          return false;
       } finally {
          try {
             GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
          } catch (RuntimeException ignored) {
          }
       }
    }

   private void drawHeader() {
      LunarUi.drawPanel(18, 18, 250, 42, false);
      if(LunarUi.hasTexture(LOGO)) {
         this.mc.getTextureManager().bindTexture(LOGO);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawModalRectWithCustomSizedTexture(25, 22, 0.0F, 0.0F, 34, 34, 128.0F, 128.0F);
      }

      this.drawString(this.fontRendererObj, "LUNARHAVENX", 66, 25, -919553);
      this.drawString(this.fontRendererObj, "CLIENT  •  FORGE 1.8.9", 66, 40, -11150849);
      if(this.width >= 700) {
         int[] widths = new int[]{74, 68, 120, 102};
         String[] labels = new String[]{this.language.text("statVersion"), this.language.text("statFps"), this.language.text("statProfile"), this.language.text("statPlatform")};
         String[] values = new String[]{"1.8.9", String.valueOf(Minecraft.getDebugFPS()), this.language.performanceName(LunarPerformanceManager.get().getProfile().name()), LunarMobileCompatibility.isAndroid()?this.language.text("android"):this.language.text("desktop")};
         int gap = 7;
         int total = widths[0] + widths[1] + widths[2] + widths[3] + gap * 3;
         int x = this.width - total - 18;

         for(int i = 0; i < widths.length; ++i) {
            this.drawStatCard(x, widths[i], labels[i], values[i]);
            x += widths[i] + gap;
         }

      }
   }

   private void drawStatCard(int x, int cardWidth, String label, String value) {
      LunarUi.drawPanel(x, 18, cardWidth, 42, false);
      this.drawCenteredString(this.fontRendererObj, label, x + cardWidth / 2, 24, -6572852);
      String clipped = this.fontRendererObj.trimStringToWidth(value, cardWidth - 10);
      this.drawCenteredString(this.fontRendererObj, clipped, x + cardWidth / 2, 38, -919553);
   }

   private void drawFeatures() {
      int panelHeight = 224;
      LunarUi.drawPanel(18, 78, 238, panelHeight, false);
      LunarUi.drawSection(this.language.text("resourceSection"), 30, 91);
      String[][] features = new String[][]{{this.language.text("featureHud"), this.language.text("featureHudDesc")}, {this.language.text("featurePerformance"), this.language.text("featurePerformanceDesc")}, {this.language.text("featureEly"), this.language.text("featureElyDesc")}, {this.language.text("featureCosmetics"), this.language.text("featureCosmeticsDesc")}, {this.language.text("featureShortcuts"), this.language.text("featureShortcutsDesc")}};

      for(int i = 0; i < features.length; ++i) {
         int y = 114 + i * 35;
         this.drawString(this.fontRendererObj, ">", 31, y, -11150849);
         this.drawString(this.fontRendererObj, features[i][0], 44, y, -919553);
         String description = this.fontRendererObj.trimStringToWidth(features[i][1], 194);
         this.drawString(this.fontRendererObj, description, 44, y + 12, -6572852);
      }

   }

   private void drawBrand() {
      int center = this.width >= 700?this.width * 2 / 3:this.width / 2;
      int top = this.height >= 390?Math.max(105, this.height / 3 - 26):Math.max(58, this.height / 4);
      float scale = this.width >= 700?1.8F:1.35F;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)center, (float)top, 0.0F);
      GlStateManager.scale(scale, scale, 1.0F);
      this.drawCenteredString(this.fontRendererObj, "LUNARHAVENX", 0, 0, -919553);
      this.drawCenteredString(this.fontRendererObj, "C L I E N T", 0, 13, -11150849);
      GlStateManager.popMatrix();
      drawRect(center - 93, top + 39, center + 93, top + 40, -866788865);
   }

   private void drawProgress() {
      boolean wide = this.width >= 700 && this.height >= 420;
      int panelWidth = wide?Math.min(484, this.width - 280):Math.min(500, this.width - 32);
      panelWidth = Math.max(180, panelWidth);
      int panelLeft = wide?this.width - panelWidth - 18:(this.width - panelWidth) / 2;
      int panelHeight = this.height >= 390?82:70;
      int panelTop = Math.max(18, this.height - panelHeight - 26);
      LunarUi.drawPanel(panelLeft, panelTop, panelWidth, panelHeight, false);
      this.drawString(this.fontRendererObj, this.language.text("loadingTitle"), panelLeft + 12, panelTop + 9, -919553);
      String clippedStatus = this.fontRendererObj.trimStringToWidth(this.status, panelWidth - 24);
      this.drawString(this.fontRendererObj, clippedStatus, panelLeft + 12, panelTop + 22, -6572852);
      int barLeft = panelLeft + 12;
      int barWidth = panelWidth - 58;
      int barY = panelTop + 39;
      int progress = (int)((float)this.completedStages / 3.0F * (float)barWidth);
      drawRect(barLeft, barY, barLeft + barWidth, barY + 8, -15389629);
      if(progress > 0) {
         drawRect(barLeft, barY, barLeft + progress, barY + 8, -11150849);
      }

      this.drawString(this.fontRendererObj, this.completedStages * 100 / 3 + "%", barLeft + barWidth + 7, barY, -919553);
      this.drawString(this.fontRendererObj, this.language.text("loadingCompatibility"), panelLeft + 12, panelTop + 55, -6572852);
   }

   private void drawTip() {
      String tip = LunarMobileCompatibility.isAndroid()?this.language.text("tipAndroid"):this.language.text("tipDesktop");
      tip = this.fontRendererObj.trimStringToWidth(tip, Math.max(80, this.width - 36));
      this.drawCenteredString(this.fontRendererObj, tip, this.width / 2, this.height - 13, -919553);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
