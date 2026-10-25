package com.lunarhavenx.client.gui;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.config.LunarPlayProfileManager;
import com.lunarhavenx.client.config.LunarVisualSettings;
import com.lunarhavenx.client.gui.LunarButton;
import com.lunarhavenx.client.gui.LunarUi;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiLunarHudEditor extends GuiScreen {
   private static final ResourceLocation BACKGROUND = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_menu_background.png");
   private final GuiScreen parent;
   private final LunarVisualSettings settings = LunarVisualSettings.get();
   private final LunarLanguageManager language = LunarLanguageManager.get();
   private int selectedElement = -1;
   private int dragOffsetX;
   private int dragOffsetY;
   private boolean dragging;

   public GuiLunarHudEditor(GuiScreen parent) {
      this.parent = parent;
   }

   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new LunarButton(0, this.width / 2 - 158, this.height - 32, 150, 22, this.language.text("hudReset")));
      this.buttonList.add(new LunarButton(1, this.width / 2 + 8, this.height - 32, 150, 22, this.language.text("done"), true));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if(button.id == 0) {
         this.settings.resetHudPositions();
         this.settings.save();
         LunarPlayProfileManager.get().markCustom();
      } else if(button.id == 1) {
         this.settings.save();
         this.mc.displayGuiScreen(this.parent);
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if(mouseButton == 0) {
         for(int i = 0; i < 5; ++i) {
            int[] bounds = this.bounds(i);
            if(mouseX >= bounds[0] && mouseX <= bounds[0] + bounds[2] && mouseY >= bounds[1] && mouseY <= bounds[1] + bounds[3]) {
               this.selectedElement = i;
               this.dragOffsetX = mouseX - bounds[0];
               this.dragOffsetY = mouseY - bounds[1];
               this.dragging = true;
               return;
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
      if(this.dragging && this.selectedElement >= 0) {
         int[] shape = this.shape(this.selectedElement);
         float scale = this.uiScale();
         int maxX = Math.max(1, this.width - (int)((float)shape[0] * scale));
         int maxY = Math.max(1, this.height - (int)((float)shape[1] * scale));
         int x = Math.max(0, Math.min(100, Math.round((float)(mouseX - this.dragOffsetX) * 100.0F / (float)maxX)));
         int y = Math.max(0, Math.min(100, Math.round((float)(mouseY - this.dragOffsetY) * 100.0F / (float)maxY)));
         this.settings.setHudPosition(this.selectedElement, x, y);
         this.settings.save();
         LunarPlayProfileManager.get().markCustom();
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      if(this.dragging) {
         this.settings.save();
      }

      this.dragging = false;
   }

   private float uiScale() {
      return Math.max(0.75F, Math.min(1.25F, (float)this.settings.uiScale / 100.0F));
   }

   private int[] shape(int element) {
      switch(element) {
      case 1:
         return new int[]{164, 44};
      case 2:
         return new int[]{218, this.mc.thePlayer == null?60:22 + Math.max(1, Math.min(5, this.mc.thePlayer.getActivePotionEffects().size())) * 15};
      case 3:
         return new int[]{94, 92};
      case 4:
         return new int[]{112, 27};
      default:
         int rows = 4;
         return new int[]{204, 36 + rows * 14};
      }
   }

   private int[] bounds(int element) {
      float scale = this.uiScale();
      int[] size = this.shape(element);
      int scaledWidth = Math.max(1, (int)((float)this.width / scale));
      int scaledHeight = Math.max(1, (int)((float)this.height / scale));
      int availableX = Math.max(0, scaledWidth - size[0]);
      int availableY = Math.max(0, scaledHeight - size[1]);
      int x = availableX * this.settings.getHudX(element) / 100;
      int y = availableY * this.settings.getHudY(element) / 100;
      return new int[]{(int)((float)x * scale), (int)((float)y * scale), Math.max(1, (int)((float)size[0] * scale)), Math.max(1, (int)((float)size[1] * scale))};
   }

   private String elementName(int element) {
      switch(element) {
      case 1:
         return this.language.text("armor");
      case 2:
         return this.language.text("status");
      case 3:
         return this.language.text("keys");
      case 4:
         return this.language.text("cps");
      default:
         return this.language.text("hudElementInfo");
      }
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      boolean hasWorld = this.mc.thePlayer != null && this.mc.theWorld != null;
      if(!hasWorld && LunarUi.hasTexture(BACKGROUND)) {
         this.mc.getTextureManager().bindTexture(BACKGROUND);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, this.width, this.height, 1280.0F, 720.0F);
      } else if(!hasWorld) {
         drawRect(0, 0, this.width, this.height, -16446684);
      } else {
         drawRect(0, 0, this.width, this.height, 1141049880);
      }

      if(!hasWorld) {
         drawRect(0, 0, this.width, this.height, 1711475224);
      }

      LunarUi.drawHeading(this.language.text("hudEditorTitle"), this.language.text("hudEditorHelp"), this.width, 12);

      for(int i = 0; i < 5; ++i) {
         int[] b = this.bounds(i);
         if(!hasWorld) {
            drawRect(b[0], b[1], b[0] + b[2], b[1] + b[3], -871950550);
            drawRect(b[0], b[1], b[0] + b[2], b[1] + 2, -11150849);
            this.drawCenteredString(this.fontRendererObj, this.elementName(i), b[0] + b[2] / 2, b[1] + b[3] / 2 - 4, -919553);
         }

         int color = this.selectedElement != i && (!this.dragging || this.selectedElement != i)?-11150849:-11930;
         drawRect(b[0], b[1], b[0] + b[2], b[1] + 2, color);
         drawRect(b[0], b[1] + b[3] - 2, b[0] + b[2], b[1] + b[3], color);
         drawRect(b[0], b[1], b[0] + 2, b[1] + b[3], color);
         drawRect(b[0] + b[2] - 2, b[1], b[0] + b[2], b[1] + b[3], color);
         int labelY = b[1] < 30?b[1] + b[3] + 2:b[1] - 11;
         this.drawString(this.fontRendererObj, this.elementName(i), b[0] + 3, labelY, color);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
