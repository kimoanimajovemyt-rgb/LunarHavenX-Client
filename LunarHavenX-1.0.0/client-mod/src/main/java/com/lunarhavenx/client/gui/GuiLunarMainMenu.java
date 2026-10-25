package com.lunarhavenx.client.gui;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.config.LunarVisualSettings;
import com.lunarhavenx.client.gui.GuiLunarCosmetics;
import com.lunarhavenx.client.gui.GuiLunarServers;
import com.lunarhavenx.client.gui.GuiLunarSettings;
import com.lunarhavenx.client.gui.LunarButton;
import com.lunarhavenx.client.gui.LunarUi;
import com.lunarhavenx.client.mobile.LunarMobileCompatibility;
import com.lunarhavenx.client.performance.LunarPerformanceManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiModList;

public class GuiLunarMainMenu extends GuiScreen {
   private static final ResourceLocation BACKGROUND = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_menu_background.png");
   private static final ResourceLocation LOGO = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_logo.png");
    private final List<Particle> particles = new ArrayList<Particle>();
   private final Random random = new Random(1829L);
   private final LunarLanguageManager language = LunarLanguageManager.get();
   private String notice;
   private long noticeUntil;

   public void initGui() {
      this.buttonList.clear();
      this.notice = this.language.text("tagline");
      this.particles.clear();

      for(int i = 0; i < 48; ++i) {
         this.particles.add(new GuiLunarMainMenu.Particle(this.random.nextFloat(), this.random.nextFloat(), 0.15F + this.random.nextFloat() * 0.5F, this.random.nextFloat() * 6.28F));
      }

      int contentWidth = Math.min(560, this.width - 32);
      int left = (this.width - contentWidth) / 2;
      int gap = 10;
      int columnWidth = (contentWidth - gap) / 2;
       int top = Math.max(76, (this.height - 166) / 2);
       this.addButton(1, left, top, columnWidth, 30, this.language.text("singlePlayer"), true);
       this.addButton(6, left + columnWidth + gap, top, columnWidth, 30, this.language.text("servers"), true);
       this.addButton(3, left, top + 36, columnWidth, 30, this.language.text("mods"));
       this.addButton(4, left + columnWidth + gap, top + 36, columnWidth, 30, this.language.text("cosmetics"));
       this.addButton(5, left, top + 72, columnWidth, 30, this.language.text("profile"));
       this.addButton(7, left + columnWidth + gap, top + 72, columnWidth, 30, this.language.text("settings"));
       this.addButton(8, left, top + 108, columnWidth, 30, this.language.text("voice"));
       this.addButton(9, left + columnWidth + gap, top + 108, columnWidth, 30, this.language.text("exit"));
   }

   private void addButton(int id, int x, int y, int w, int h, String text) {
      this.addButton(id, x, y, w, h, text, false);
   }

   private void addButton(int id, int x, int y, int w, int h, String text, boolean primary) {
      this.buttonList.add(new LunarButton(id, x, y, w, h, text, primary));
   }

   public void updateScreen() {
      if(LunarVisualSettings.get().animationsEnabled) {
         for(GuiLunarMainMenu.Particle particle : this.particles) {
            particle.phase += 0.018F;
            particle.x += 1.2E-4F * particle.speed;
            particle.y -= 8.0E-5F * particle.speed;
            if(particle.x > 1.04F) {
               particle.x = -0.04F;
            }

            if(particle.y < -0.04F) {
               particle.y = 1.04F;
            }
         }

      }
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      switch(button.id) {
      case 1:
         this.mc.displayGuiScreen(new GuiSelectWorld(this));
         break;
      case 3:
         this.mc.displayGuiScreen(new GuiModList(this));
         break;
      case 4:
         this.mc.displayGuiScreen(new GuiLunarCosmetics(this));
         break;
      case 5:
         LunarPerformanceManager.get().setProfile(LunarPerformanceManager.get().getProfile().next());
          this.notice = this.language.text("profile") + ": " + this.currentProfileLabel();
         this.noticeUntil = Minecraft.getSystemTime() + 2400L;
         break;
      case 6:
         this.mc.displayGuiScreen(new GuiLunarServers(this));
         break;
      case 7:
         this.mc.displayGuiScreen(new GuiLunarSettings(this));
         break;
      case 8:
         this.notice = this.language.text("voiceUnavailable");
         this.noticeUntil = Minecraft.getSystemTime() + 3600L;
         break;
      case 9:
         this.mc.shutdown();
         break;
      default:
         this.notice = button.displayString + " estará disponível na próxima etapa.";
         this.noticeUntil = Minecraft.getSystemTime() + 2400L;
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if(!this.drawBackgroundTexture()) {
         drawRect(0, 0, this.width, this.height, -16446684);
      }

      drawRect(0, 0, this.width, this.height, -1979578345);
      this.drawParticles();
      int contentWidth = Math.min(560, this.width - 32);
      int left = (this.width - contentWidth) / 2;
       int top = Math.max(76, (this.height - 166) / 2);
       LunarUi.drawPanel(left - 14, top - 14, contentWidth + 28, 166, false);
      this.drawHeader();
      super.drawScreen(mouseX, mouseY, partialTicks);
      if(Minecraft.getSystemTime() < this.noticeUntil) {
         int boxWidth = Math.min(this.width - 32, 560);
         int noticeLeft = this.width / 2 - boxWidth / 2;
         LunarUi.drawPanel(noticeLeft, this.height - 36, boxWidth, 24, true);
         String clipped = this.fontRendererObj.trimStringToWidth(this.notice, boxWidth - 20);
         this.drawCenteredString(this.fontRendererObj, clipped, this.width / 2, this.height - 28, -919553);
      }

   }

   private boolean drawBackgroundTexture() {
      if(!LunarUi.hasTexture(BACKGROUND)) {
         return false;
      }

      try {
         this.mc.getTextureManager().bindTexture(BACKGROUND);
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
      LunarUi.drawPanel(18, 18, 202, 42, false);
      if(LunarUi.hasTexture(LOGO)) {
         this.mc.getTextureManager().bindTexture(LOGO);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawModalRectWithCustomSizedTexture(24, 23, 0.0F, 0.0F, 32, 32, 128.0F, 128.0F);
      }

      this.drawString(this.fontRendererObj, "LUNARHAVENX", 62, 25, -919553);
      this.drawString(this.fontRendererObj, this.language.text("clientForge"), 62, 40, -11150849);
      if(this.width >= 520) {
          String profile = this.language.text("profile") + "  " + this.currentProfileLabel();
         int profileWidth = this.fontRendererObj.getStringWidth(profile);
         LunarUi.drawPanel(this.width - profileWidth - 40, 18, profileWidth + 22, 42, false);
         this.drawString(this.fontRendererObj, profile, this.width - profileWidth - 29, 25, -919553);
            this.drawString(this.fontRendererObj, "v0.5.9-alpha", this.width - profileWidth - 29, 40, -6572852);
      }

      this.drawString(this.fontRendererObj, this.language.text("localClient"), 22, this.height - 18, -6572852);
   }

   private String currentProfileLabel() {
      LunarPerformanceManager performance = LunarPerformanceManager.get();
      String profile = this.language.performanceName(performance.getProfile().name());
      return LunarMobileCompatibility.isAndroid()
              ? profile + " • " + performance.getMobileFrameLimit() + " FPS"
              : profile;
   }

   private void drawParticles() {
      if(LunarVisualSettings.get().particlesEnabled) {
         for(GuiLunarMainMenu.Particle particle : this.particles) {
            int x = (int)(particle.x * (float)this.width);
            int y = (int)(particle.y * (float)this.height);
            int alpha = 75 + (int)(70.0F * (0.5F + 0.5F * (float)Math.sin((double)particle.phase)));
            int color = alpha << 24 | 6742015;
            drawRect(x, y, x + 2, y + 2, color);
            if(particle.speed > 0.55F) {
               drawRect(x - 3, y + 1, x + 5, y + 2, color);
            }
         }

      }
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   private static class Particle {
      float x;
      float y;
      final float speed;
      float phase;

      Particle(float x, float y, float speed, float phase) {
         this.x = x;
         this.y = y;
         this.speed = speed;
         this.phase = phase;
      }
   }
}
