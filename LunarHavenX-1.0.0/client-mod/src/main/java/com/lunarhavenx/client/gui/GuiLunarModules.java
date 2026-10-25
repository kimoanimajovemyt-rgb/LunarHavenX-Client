package com.lunarhavenx.client.gui;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.config.LunarPlayProfileManager;
import com.lunarhavenx.client.config.LunarVisualSettings;
import com.lunarhavenx.client.gui.GuiLunarHudEditor;
import com.lunarhavenx.client.gui.GuiLunarProfiles;
import com.lunarhavenx.client.gui.LunarButton;
import com.lunarhavenx.client.gui.LunarUi;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiLunarModules extends GuiScreen {
   private static final ResourceLocation BACKGROUND = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_menu_background.png");
   private final GuiScreen parent;
   private final LunarVisualSettings settings = LunarVisualSettings.get();
   private final LunarLanguageManager language = LunarLanguageManager.get();

   public GuiLunarModules(GuiScreen parent) {
      this.parent = parent;
   }

   public void initGui() {
      this.buttonList.clear();
      int left = this.width / 2 - 190;
      int top = 98;
      this.addToggle(0, left, top, this.language.text("hud"), this.settings.hudEnabled);
      this.addToggle(1, left + 200, top, this.language.text("coords"), this.settings.showCoordinates);
      this.addToggle(2, left, top + 34, this.language.text("status"), this.settings.showStatus);
      this.addToggle(3, left + 200, top + 34, this.language.text("armor"), this.settings.showArmor);
      this.addToggle(4, left, top + 68, this.language.text("cps"), this.settings.showCps);
      this.addToggle(5, left + 200, top + 68, this.language.text("keys"), this.settings.showKeystrokes);
      this.addToggle(6, left, top + 102, this.language.text("clock"), this.settings.showClock);
      this.addToggle(7, left + 200, top + 102, this.language.text("biome"), this.settings.showBiome);
      this.addToggle(8, left, top + 136, this.language.text("direction"), this.settings.showDirection);
      this.addToggle(9, left + 200, top + 136, this.language.text("animations"), this.settings.animationsEnabled);
      this.addToggle(10, left, top + 170, this.language.text("particles"), this.settings.particlesEnabled);
      this.buttonList.add(new LunarButton(20, left + 200, top + 170, 180, 24, this.language.text("profiles"), true));
      this.buttonList.add(new LunarButton(22, left, top + 204, 180, 24, this.language.text("editHud")));
      this.buttonList.add(new LunarButton(21, this.width / 2 - 90, this.height - 38, 180, 24, this.language.text("done")));
   }

   private void addToggle(int id, int x, int y, String label, boolean value) {
      this.buttonList.add(new LunarButton(id, x, y, 180, 24, label + ": " + this.language.state(value)));
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
         this.settings.showCps = !this.settings.showCps;
         break;
      case 5:
         this.settings.showKeystrokes = !this.settings.showKeystrokes;
         break;
      case 6:
         this.settings.showClock = !this.settings.showClock;
         break;
      case 7:
         this.settings.showBiome = !this.settings.showBiome;
         break;
      case 8:
         this.settings.showDirection = !this.settings.showDirection;
         break;
      case 9:
         this.settings.animationsEnabled = !this.settings.animationsEnabled;
         break;
      case 10:
         this.settings.particlesEnabled = !this.settings.particlesEnabled;
         break;
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
         return;
      case 20:
         this.mc.displayGuiScreen(new GuiLunarProfiles(this));
         return;
      case 21:
         this.settings.save();
         this.mc.displayGuiScreen(this.parent);
         return;
      case 22:
         this.settings.save();
         this.mc.displayGuiScreen(new GuiLunarHudEditor(this));
         return;
      }

      this.settings.save();
      LunarPlayProfileManager.get().markCustom();
      this.initGui();
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
      LunarUi.drawPanel(this.width / 2 - 202, 68, 404, 326, false);
      LunarUi.drawHeading(this.language.text("moduleTitle"), this.language.text("moduleHelp"), this.width, 25);
      int left = this.width / 2 - 190;
      LunarUi.drawSection(this.language.text("moduleTitleHud"), left, 78);
      LunarUi.drawSection(this.language.text("moduleTitleVisual"), left + 200, 78);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
