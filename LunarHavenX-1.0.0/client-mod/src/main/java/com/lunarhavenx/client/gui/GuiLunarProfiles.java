package com.lunarhavenx.client.gui;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.config.LunarPlayProfileManager;
import com.lunarhavenx.client.gui.LunarButton;
import com.lunarhavenx.client.gui.LunarUi;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class GuiLunarProfiles extends GuiScreen {
   private static final ResourceLocation BACKGROUND = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_menu_background.png");
   private final GuiScreen parent;
   private final LunarLanguageManager language = LunarLanguageManager.get();
   private final LunarPlayProfileManager profiles = LunarPlayProfileManager.get();
   private final GuiTextField[] nameFields = new GuiTextField[6];
   private GuiTextField fileNameField;
   private int selectedSlot;
   private String notice = "";

   public GuiLunarProfiles(GuiScreen parent) {
      this.parent = parent;
   }

   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      if(this.profiles.getActive() >= 0) {
         this.selectedSlot = this.profiles.getActive();
      }

      int left = this.width / 2 - 286;
      int cardWidth = 278;

      for(int i = 0; i < 6; ++i) {
         int col = i % 2;
         int row = i / 2;
         int x = left + col * 294;
         int y = 76 + row * 88;
         if(this.nameFields[i] == null) {
            this.nameFields[i] = new GuiTextField(i, this.fontRendererObj, x + 9, y + 13, cardWidth - 18, 18);
            this.nameFields[i].setMaxStringLength(24);
         }

         if(!this.nameFields[i].isFocused()) {
            this.nameFields[i].setText(this.profiles.getLabel(i));
         }

         this.buttonList.add(new LunarButton(10 + i, x + 8, y + 43, 124, 20, this.language.text("apply"), this.profiles.getActive() == i));
         this.buttonList.add(new LunarButton(20 + i, x + 140, y + 43, 128, 20, this.language.text("save")));
      }

      int ioY = 354;
      if(this.fileNameField == null) {
         this.fileNameField = new GuiTextField(60, this.fontRendererObj, left + 8, ioY + 2, 270, 20);
         this.fileNameField.setMaxStringLength(40);
      }

      this.buttonList.add(new LunarButton(40, left + 288, ioY, 126, 24, this.language.text("import")));
      this.buttonList.add(new LunarButton(41, left + 422, ioY, 126, 24, this.language.text("export"), true));
      this.buttonList.add(new LunarButton(30, this.width / 2 - 90, this.height - 29, 180, 22, this.language.text("done")));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if(button.id >= 10 && button.id < 16) {
         int index = button.id - 10;
         this.selectedSlot = index;
         this.profiles.setLabel(index, this.nameFields[index].getText());
         this.profiles.apply(index);
         this.notice = this.language.text("profileApplied");
         this.initGui();
      } else if(button.id >= 20 && button.id < 26) {
         int index = button.id - 20;
         this.selectedSlot = index;
         this.profiles.setLabel(index, this.nameFields[index].getText());
         this.profiles.saveCurrent(index);
         this.notice = this.language.text("profileSaved");
         this.initGui();
      } else if(button.id == 40) {
         this.notice = this.profiles.importProfile(this.selectedSlot, this.fileNameField.getText())?this.language.text("profileImported"):this.language.text("profileImportFailed");
         if(!this.notice.equals(this.language.text("profileImportFailed"))) {
            this.nameFields[this.selectedSlot].setText(this.profiles.getLabel(this.selectedSlot));
         }
      } else if(button.id == 41) {
         this.profiles.setLabel(this.selectedSlot, this.nameFields[this.selectedSlot].getText());
         this.notice = this.profiles.exportProfile(this.selectedSlot, this.fileNameField.getText())?this.language.text("profileExported"):this.language.text("profileExportFailed");
      } else if(button.id == 30) {
         Keyboard.enableRepeatEvents(false);
         this.mc.displayGuiScreen(this.parent);
      }

   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if(keyCode == 1) {
         Keyboard.enableRepeatEvents(false);
         this.mc.displayGuiScreen(this.parent);
      } else {
         for(GuiTextField field : this.nameFields) {
            if(field != null && field.isFocused()) {
               field.textboxKeyTyped(typedChar, keyCode);
               return;
            }
         }

         if(this.fileNameField != null && this.fileNameField.isFocused()) {
            this.fileNameField.textboxKeyTyped(typedChar, keyCode);
         } else {
            super.keyTyped(typedChar, keyCode);
         }
      }
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);

      for(int i = 0; i < this.nameFields.length; ++i) {
         GuiTextField field = this.nameFields[i];
         if(field != null) {
            field.mouseClicked(mouseX, mouseY, mouseButton);
            if(mouseButton == 0 && mouseX >= field.xPosition && mouseX <= field.xPosition + field.width && mouseY >= field.yPosition && mouseY <= field.yPosition + field.height) {
               this.selectedSlot = i;
            }
         }
      }

      if(this.fileNameField != null) {
         this.fileNameField.mouseClicked(mouseX, mouseY, mouseButton);
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
      int left = this.width / 2 - 286;
      int cardWidth = 278;
      LunarUi.drawHeading(this.language.text("profiles"), this.language.text("profileHelp"), this.width, 12);

      for(int i = 0; i < 6; ++i) {
         int x = left + i % 2 * 294;
         int y = 76 + i / 2 * 88;
         LunarUi.drawPanel(x, y, cardWidth, 70, this.selectedSlot == i || this.profiles.getActive() == i);
         this.drawString(this.fontRendererObj, this.language.text("profileSlot") + " " + (i + 1), x + 9, y + 2, -6572852);
         this.nameFields[i].drawTextBox();
      }

      LunarUi.drawSection(this.language.text("profileFileLabel"), left + 8, 343);
      this.fileNameField.drawTextBox();
      String fileHelp = this.fontRendererObj.trimStringToWidth(this.language.text("profileFileHelp"), this.width - 36);
      this.drawString(this.fontRendererObj, fileHelp, 18, 386, -6572852);
      if(!this.notice.isEmpty()) {
         String clippedNotice = this.fontRendererObj.trimStringToWidth(this.notice, this.width - 32);
         this.drawCenteredString(this.fontRendererObj, clippedNotice, this.width / 2, this.height - 49, -11150849);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }
}
