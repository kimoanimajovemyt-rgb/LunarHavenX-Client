package com.lunarhavenx.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class LunarButton extends GuiButton {
   private final boolean primary;

   public LunarButton(int id, int x, int y, int width, int height, String label) {
      this(id, x, y, width, height, label, false);
   }

   public LunarButton(int id, int x, int y, int width, int height, String label, boolean primary) {
      super(id, x, y, width, height, label);
      this.primary = primary;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if(this.visible) {
         this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         int background;
         int accent;
         int text;
         if(!this.enabled) {
            background = -1190521823;
            accent = -13614763;
            text = -8877670;
         } else if(this.primary) {
            background = this.hovered?-15244662:-15709072;
            accent = this.hovered?-4983809:-11150849;
            text = -1;
         } else {
            background = this.hovered?-15123883:-385146824;
            accent = this.hovered?-8132353:-13543819;
            text = -919553;
         }

         Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, accent);
         Gui.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, background);
         mc.fontRendererObj.drawStringWithShadow(this.displayString, (float)this.xPosition + (float)(this.width - mc.fontRendererObj.getStringWidth(this.displayString)) / 2.0F, (float)this.yPosition + (float)(this.height - 8) / 2.0F, text);
      }
   }
}
