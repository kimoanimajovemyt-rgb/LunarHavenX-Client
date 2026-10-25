package com.lunarhavenx.client.gui;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.config.LunarPlayProfileManager;
import com.lunarhavenx.client.config.LunarVisualSettings;
import com.lunarhavenx.client.gui.GuiLunarKeybinds;
import com.lunarhavenx.client.gui.GuiLunarModules;
import com.lunarhavenx.client.gui.LunarButton;
import com.lunarhavenx.client.gui.LunarUi;
import com.lunarhavenx.client.mobile.LunarMobileCompatibility;
import com.lunarhavenx.client.performance.LunarPerformanceManager;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiLunarSettings extends GuiScreen {
   private static final ResourceLocation BACKGROUND = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_menu_background.png");
   private final LunarVisualSettings settings = LunarVisualSettings.get();
   private final LunarLanguageManager language = LunarLanguageManager.get();
   private final LunarPerformanceManager performance = LunarPerformanceManager.get();
   private GuiScreen parent;
   private boolean draggingOpacity;
   private boolean draggingScale;

   public GuiLunarSettings(GuiScreen parent) {
      this.parent = parent;
   }

   public void initGui() {
      this.buttonList.clear();
      int left = this.width / 2 - 190;
      int top = 88;
      this.addButton(0, left, top, 180, 24, this.language.text("hud") + ": " + this.onOff(this.settings.hudEnabled));
      this.addButton(1, left + 200, top, 180, 24, this.language.text("coords") + ": " + this.onOff(this.settings.showCoordinates));
      this.addButton(2, left, top + 32, 180, 24, this.language.text("status") + ": " + this.onOff(this.settings.showStatus));
      this.addButton(3, left + 200, top + 32, 180, 24, this.language.text("armor") + ": " + this.onOff(this.settings.showArmor));
      this.addButton(4, left, top + 64, 180, 24, this.language.text("animations") + ": " + this.onOff(this.settings.animationsEnabled));
      this.addButton(5, left + 200, top + 64, 180, 24, this.language.text("particles") + ": " + this.onOff(this.settings.particlesEnabled));
      this.addButton(8, left, top + 96, 180, 24, this.language.text("cps") + ": " + this.onOff(this.settings.showCps));
      this.addButton(9, left + 200, top + 96, 180, 24, this.language.text("keys") + ": " + this.onOff(this.settings.showKeystrokes));
       this.addButton(6, left, top + 128, 180, 24, this.language.text("profile") + ": " + this.currentProfileLabel());
      this.addButton(22, left + 200, top + 128, 180, 24, this.language.text("shortcuts"));
      this.addButton(23, left, top + 160, 180, 24, this.language.text("modules"));
      this.addButton(24, left + 200, top + 160, 180, 24, this.language.text("language") + ": " + this.language.getName());
      this.addButton(20, left, this.height - 42, 180, 26, this.language.text("restore"));
      this.addButton(21, left + 200, this.height - 42, 180, 26, this.language.text("done"), true);
   }

   private void addButton(int id, int x, int y, int w, int h, String text) {
      this.addButton(id, x, y, w, h, text, false);
   }

   private void addButton(int id, int x, int y, int w, int h, String text, boolean primary) {
      this.buttonList.add(new LunarButton(id, x, y, w, h, text, primary));
   }

   private String onOff(boolean value) {
      return this.language.state(value);
   }

   private String currentProfileLabel() {
      String profile = this.language.performanceName(this.performance.getProfile().name());
      return LunarMobileCompatibility.isAndroid()
              ? profile + " • " + this.performance.getMobileFrameLimit() + " FPS"
              : profile;
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      switch(button.id) {
      case 0:
         this.settings.hudEnabled = !this.settings.hudEnabled;
         break;
      case 1:
         this.settings.showCoordinates = !this.settings.showCoordinates;
         break;
      case 2:
         this.settings.showStatus = !this.settings.showStatus;
         break;
      case 3:
         this.settings.showArmor = !this.settings.showArmor;
         break;
      case 4:
         this.settings.animationsEnabled = !this.settings.animationsEnabled;
         break;
      case 5:
         this.settings.particlesEnabled = !this.settings.particlesEnabled;
         break;
      case 6:
         this.performance.setProfile(this.performance.getProfile().next());
      case 7:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      default:
         break;
      case 8:
         this.settings.showCps = !this.settings.showCps;
         break;
      case 9:
         this.settings.showKeystrokes = !this.settings.showKeystrokes;
         break;
      case 20:
         this.settings.hudEnabled = true;
         this.settings.showCoordinates = true;
         this.settings.showStatus = true;
         this.settings.showArmor = true;
         this.settings.showCps = true;
         this.settings.showKeystrokes = true;
         this.settings.animationsEnabled = true;
         this.settings.particlesEnabled = true;
         this.settings.showClock = false;
         this.settings.showBiome = false;
         this.settings.showDirection = false;
         this.settings.hudOpacity = 72;
         this.settings.uiScale = 100;
         this.settings.resetHudPositions();
         this.performance.setProfile(LunarPerformanceManager.Profile.BALANCEADO);
         LunarPlayProfileManager.get().markCustom();
         break;
      case 21:
         this.settings.save();
         this.mc.displayGuiScreen(this.parent);
         return;
      case 22:
         this.mc.displayGuiScreen(new GuiLunarKeybinds(this));
         return;
      case 23:
         this.settings.save();
         this.mc.displayGuiScreen(new GuiLunarModules(this));
         return;
      case 24:
         this.language.next();
         this.initGui();
         return;
      }

      LunarPlayProfileManager.get().markCustom();
      this.settings.save();
      this.initGui();
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      int left = this.width / 2 - 190;
      if(mouseButton == 0 && this.inSlider(mouseX, mouseY, left, 296)) {
         this.draggingOpacity = true;
      }

      if(mouseButton == 0 && this.inSlider(mouseX, mouseY, left, 340)) {
         this.draggingScale = true;
      }

      this.updateSliders(mouseX);
   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
      if(this.draggingOpacity || this.draggingScale) {
         this.updateSliders(mouseX);
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      if(this.draggingOpacity || this.draggingScale) {
         LunarPlayProfileManager.get().markCustom();
         this.settings.save();
      }

      this.draggingOpacity = false;
      this.draggingScale = false;
   }

   private boolean inSlider(int mouseX, int mouseY, int left, int y) {
      return mouseX >= left && mouseX <= left + 380 && mouseY >= y - 5 && mouseY <= y + 12;
   }

   private void updateSliders(int mouseX) {
      int left = this.width / 2 - 190;
      int value = Math.max(0, Math.min(380, mouseX - left));
      if(this.draggingOpacity) {
         this.settings.hudOpacity = 20 + value * 80 / 380;
      }

      if(this.draggingScale) {
         this.settings.uiScale = 75 + value * 50 / 380;
      }

      if(this.draggingOpacity || this.draggingScale) {
         LunarPlayProfileManager.get().markCustom();
         this.settings.save();
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if(LunarUi.hasTexture(BACKGROUND)) {
         this.mc.getTextureManager().bindTexture(BACKGROUND);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, this.width, this.height, 1280.0F, 720.0F);
      } else {
         drawRect(0, 0, this.width, this.height, -16446684);
      }

      drawRect(0, 0, this.width, this.height, -586936277);
      LunarUi.drawPanel(this.width / 2 - 202, 68, 404, 404, false);
      int left = this.width / 2 - 190;
      LunarUi.drawHeading(this.language.text("settings"), this.language.text("localSaved"), this.width, 25);
      LunarUi.drawSection(this.language.text("hudModules"), left, 72);
      this.drawString(this.fontRendererObj, this.language.text("opacity") + "  " + this.settings.hudOpacity + "%", left, 280, -919553);
      this.drawSlider(left, 296, (float)(this.settings.hudOpacity - 20) / 80.0F);
      this.drawString(this.fontRendererObj, this.language.text("scale") + "  " + this.settings.uiScale + "%", left, 324, -919553);
      this.drawSlider(left, 340, (float)(this.settings.uiScale - 75) / 50.0F);
      this.drawString(this.fontRendererObj, this.language.text("profileInfo"), left, 368, -6572852);
      this.drawString(this.fontRendererObj, LunarMobileCompatibility.getPlatformLabel() + (LunarMobileCompatibility.isAndroid()?" • perfil leve na primeira execução":""), left, 388, -6572852);
      this.drawString(this.fontRendererObj, this.language.text("quickHelp"), left, 408, -6572852);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private void drawSlider(int left, int y, float progress) {
      drawRect(left, y, left + 380, y + 5, -1442172591);
      drawRect(left, y, left + (int)(380.0F * Math.max(0.0F, Math.min(1.0F, progress))), y + 5, -14101249);
      int knob = left + (int)(380.0F * Math.max(0.0F, Math.min(1.0F, progress)));
      drawRect(knob - 3, y - 4, knob + 3, y + 9, -1574913);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
