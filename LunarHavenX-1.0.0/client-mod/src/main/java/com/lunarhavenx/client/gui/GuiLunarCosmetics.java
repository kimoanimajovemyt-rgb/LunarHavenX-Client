package com.lunarhavenx.client.gui;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.cosmetics.LunarCosmeticManager;
import com.lunarhavenx.client.gui.LunarButton;
import com.lunarhavenx.client.gui.LunarUi;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiLunarCosmetics extends GuiScreen {
   private static final ResourceLocation BACKGROUND = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_menu_background.png");
   private final LunarCosmeticManager cosmetics;
   private final LunarLanguageManager language;
   private final GuiScreen parent;
   private LunarCosmeticManager.Cape preview;
   private String notice;
   private long noticeUntil;

   public GuiLunarCosmetics() {
      this((GuiScreen)null);
   }

   public GuiLunarCosmetics(GuiScreen parent) {
      this.cosmetics = LunarCosmeticManager.get();
      this.language = LunarLanguageManager.get();
      this.notice = "";
      this.parent = parent;
   }

   public void initGui() {
      this.preview = this.cosmetics.getCape();
      this.buttonList.clear();
      this.buttonList.add(new LunarButton(0, this.width / 2 - 155, this.height - 75, 95, 25, this.language.text("previous")));
      this.buttonList.add(new LunarButton(1, this.width / 2 - 50, this.height - 75, 100, 25, this.language.text("equip"), true));
      this.buttonList.add(new LunarButton(2, this.width / 2 + 60, this.height - 75, 95, 25, this.language.text("next")));
      this.buttonList.add(new LunarButton(3, this.width / 2 - 50, this.height - 42, 100, 22, this.language.text("back")));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      switch(button.id) {
      case 0:
         this.preview = this.previous(this.preview);
         break;
      case 1:
         this.cosmetics.setCape(this.preview);
         this.notice = this.language.text("equipped") + " " + this.preview.label + " " + this.language.text("equippedLocal");
         this.noticeUntil = System.currentTimeMillis() + 2200L;
         break;
      case 2:
         this.preview = this.preview.next();
         break;
      case 3:
         this.mc.displayGuiScreen(this.parent);
      }

   }

   private LunarCosmeticManager.Cape previous(LunarCosmeticManager.Cape value) {
      LunarCosmeticManager.Cape[] all = LunarCosmeticManager.Cape.values();
      return all[(value.ordinal() + all.length - 1) % all.length];
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if(LunarUi.hasTexture(BACKGROUND)) {
         this.mc.getTextureManager().bindTexture(BACKGROUND);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, this.width, this.height, 1280.0F, 720.0F);
      } else {
         drawRect(0, 0, this.width, this.height, -16446684);
      }

      drawRect(0, 0, this.width, this.height, -654045151);
      LunarUi.drawHeading(this.language.text("cosmetics"), this.language.text("cosmeticSubtitle"), this.width, 24);
      int cardLeft = this.width / 2 - 164;
      int cardTop = 70;
      LunarUi.drawPanel(cardLeft, cardTop, 328, 190, true);
      this.drawCenteredString(this.fontRendererObj, this.preview.label, this.width / 2, cardTop + 16, -919553);
      if(this.preview != LunarCosmeticManager.Cape.NONE) {
         ResourceLocation texture = new ResourceLocation("lunarhavenx", this.preview.texture);
         if(LunarUi.hasTexture(texture)) {
            this.mc.getTextureManager().bindTexture(texture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            drawModalRectWithCustomSizedTexture(this.width / 2 - 96, cardTop + 48, 0.0F, 0.0F, 192, 96, 64.0F, 32.0F);
         } else {
            this.drawCenteredString(this.fontRendererObj, this.language.text("textureMissing"), this.width / 2, cardTop + 88, -6572852);
         }
      } else {
         this.drawCenteredString(this.fontRendererObj, this.language.text("noCape"), this.width / 2, cardTop + 86, -6572852);
      }

      this.drawCenteredString(this.fontRendererObj, this.language.text("selectedCape") + "  •  " + this.cosmetics.getCape().label, this.width / 2, cardTop + 158, -11150849);
      this.drawCenteredString(this.fontRendererObj, this.language.text("elyByInfo"), this.width / 2, this.height - 119, -6572852);
      this.drawCenteredString(this.fontRendererObj, this.language.text("elyByHelp"), this.width / 2, this.height - 105, -6572852);
      this.drawCenteredString(this.fontRendererObj, this.language.text("localCapeInfo"), this.width / 2, this.height - 91, -6572852);
      if(System.currentTimeMillis() < this.noticeUntil) {
         this.drawCenteredString(this.fontRendererObj, this.notice, this.width / 2, this.height - 15, -919553);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
