package com.lunarhavenx.client.gui;

import com.lunarhavenx.client.chat.LunarChatManager;
import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.gui.LunarUi;
import net.minecraft.client.gui.GuiChat;

public class GuiLunarChat extends GuiChat {
   public void sendChatMessage(String message) {
      super.sendChatMessage(LunarChatManager.replaceEmojiCodes(message));
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      int bannerWidth = Math.min(this.width - 20, Math.max(220, this.fontRendererObj.getStringWidth(LunarChatManager.getHelpText()) + 82));
      LunarUi.drawPanel(10, this.height - 44, bannerWidth, 18, false);
      String hint = this.fontRendererObj.trimStringToWidth(LunarLanguageManager.get().text("emojis") + "  " + LunarChatManager.getHelpText(), bannerWidth - 16);
      this.drawString(this.fontRendererObj, hint, 18, this.height - 39, -919553);
      LunarUi.drawPanel(10, 10, 164, 20, false);
      this.drawString(this.fontRendererObj, LunarLanguageManager.get().text("chatTitle"), 18, 16, -11150849);
   }
}
