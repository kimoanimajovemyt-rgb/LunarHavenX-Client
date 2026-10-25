package com.lunarhavenx.client.gui;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.gui.LunarButton;
import com.lunarhavenx.client.gui.LunarUi;
import com.lunarhavenx.client.input.LunarKeybindManager;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class GuiLunarKeybinds extends GuiScreen {
   private static final ResourceLocation BACKGROUND = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_menu_background.png");
   private final LunarKeybindManager manager;
   private final LunarLanguageManager language;
   private final GuiScreen parent;
   private final GuiTextField[] macroFields;
   private int waiting;

   public GuiLunarKeybinds() {
      this((GuiScreen)null);
   }

   public GuiLunarKeybinds(GuiScreen parent) {
      this.manager = LunarKeybindManager.get();
      this.language = LunarLanguageManager.get();
      this.macroFields = new GuiTextField[3];
      this.waiting = -1;
      this.parent = parent;
   }

   public void initGui() {
      this.buttonList.clear();
      int left = this.width / 2 - 210;

      for(int i = 0; i < 8; ++i) {
         int y = 66 + i * 26;
         this.buttonList.add(new LunarButton(i, left, y, 220, 22, this.label(i)));
      }

      for(int i = 0; i < 3; ++i) {
         this.macroFields[i] = new GuiTextField(20 + i, this.fontRendererObj, left + 230, 66 + (5 + i) * 26, 190, 22);
         this.macroFields[i].setMaxStringLength(100);
         this.macroFields[i].setText(this.manager.getMacro(i));
      }

      this.buttonList.add(new LunarButton(20, left, 300, 130, 24, this.language.text("restoreKeys")));
      this.buttonList.add(new LunarButton(21, left + 140, 300, 130, 24, this.language.text("clearMacros")));
      this.buttonList.add(new LunarButton(22, left + 280, 300, 140, 24, this.language.text("back"), true));
   }

   private String label(int index) {
      if(this.waiting == index) {
         return this.language.text("pressKey");
      } else {
         String[] descriptions = new String[]{this.language.text("openSettings"), this.language.text("toggleHud"), this.language.text("cyclePerformance"), this.language.text("openCosmetics"), this.language.text("openServers"), "Macro 1", "Macro 2", "Macro 3"};
         String name = this.manager.getBindings()[index].getKeyCode() == 0?this.language.text("undefined"):Keyboard.getKeyName(this.manager.getBindings()[index].getKeyCode());
         return descriptions[index] + "  [" + name + "]";
      }
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if(button.id >= 0 && button.id < 8) {
         this.waiting = button.id;
         this.initGui();
         this.waiting = button.id;
      } else {
         if(button.id == 20) {
            int[] defaults = new int[]{54, 64, 65, 66, 67, 0, 0, 0};

            for(int i = 0; i < defaults.length; ++i) {
               this.manager.getBindings()[i].setKeyCode(defaults[i]);
            }

            this.manager.save();
            this.waiting = -1;
            this.initGui();
         } else if(button.id == 21) {
            for(int i = 0; i < 3; ++i) {
               this.macroFields[i].setText("");
               this.manager.setMacro(i, "");
            }

            this.manager.save();
         } else if(button.id == 22) {
            this.saveMacros();
            this.mc.displayGuiScreen(this.parent);
         }

      }
   }

   private void saveMacros() {
      for(int i = 0; i < 3; ++i) {
         this.manager.setMacro(i, this.macroFields[i].getText());
      }

      this.manager.save();
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if(this.waiting >= 0) {
         if(keyCode == 1) {
            this.waiting = -1;
            this.initGui();
         } else {
            this.manager.getBindings()[this.waiting].setKeyCode(keyCode);
            this.manager.save();
            this.waiting = -1;
            this.initGui();
         }
      } else {
         for(GuiTextField field : this.macroFields) {
            if(field.textboxKeyTyped(typedChar, keyCode)) {
               return;
            }
         }

         super.keyTyped(typedChar, keyCode);
      }
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      for(GuiTextField field : this.macroFields) {
         field.mouseClicked(mouseX, mouseY, mouseButton);
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if(LunarUi.hasTexture(BACKGROUND)) {
         this.mc.getTextureManager().bindTexture(BACKGROUND);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, this.width, this.height, 1280.0F, 720.0F);
      } else {
         drawRect(0, 0, this.width, this.height, -16446684);
      }

      drawRect(0, 0, this.width, this.height, -435941333);
      LunarUi.drawPanel(this.width / 2 - 220, 48, 440, 274, false);
      LunarUi.drawHeading(this.language.text("keyTitle"), this.language.text("keySubtitle"), this.width, 20);
      int left = this.width / 2 - 210;
      LunarUi.drawSection(this.language.text("controls"), left, 53);
      LunarUi.drawSection(this.language.text("chatMacros"), left + 230, 53);

      for(GuiTextField field : this.macroFields) {
         field.drawTextBox();
      }

      this.drawString(this.fontRendererObj, this.language.text("oneMessage"), left + 230, 292, -6572852);
      this.drawString(this.fontRendererObj, this.language.text("noAutoMessages"), left, 338, -6572852);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
