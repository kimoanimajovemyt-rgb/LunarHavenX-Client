package com.lunarhavenx.client.gui;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.gui.LunarButton;
import com.lunarhavenx.client.gui.LunarUi;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiLunarServers extends GuiScreen {
   private static final ResourceLocation BACKGROUND = new ResourceLocation("lunarhavenx", "textures/gui/lunarhavenx_menu_background.png");
   private ServerList serverList;
   private GuiTextField nameField;
   private GuiTextField addressField;
   private int selected;
   private int scroll;
   private int lastClickedRow;
   private long lastClickTime;
   private final LunarLanguageManager language;
   private final GuiScreen parent;
   private String notice;
   private long noticeUntil;

   public GuiLunarServers() {
      this((GuiScreen)null);
   }

   public GuiLunarServers(GuiScreen parent) {
      this.selected = -1;
      this.lastClickedRow = -1;
      this.language = LunarLanguageManager.get();
      this.parent = parent;
   }

   public void initGui() {
      this.serverList = new ServerList(this.mc);
      this.serverList.loadServerList();
      if(this.notice == null) {
         this.notice = this.language.text("serverSelect");
      }

      int margin = Math.max(20, Math.min(42, this.width / 10));
      int contentWidth = this.width - margin * 2;
      int nameWidth = (contentWidth - 12) * 2 / 5;
      int fieldsTop = this.height - 94;
      this.nameField = new GuiTextField(0, this.fontRendererObj, margin, fieldsTop, nameWidth, 20);
      this.addressField = new GuiTextField(1, this.fontRendererObj, margin + nameWidth + 12, fieldsTop, contentWidth - nameWidth - 12, 20);
      this.nameField.setMaxStringLength(48);
      this.addressField.setMaxStringLength(128);
      this.buttonList.clear();
      int gap = 8;
      int actionWidth = (contentWidth - gap * 3) / 4;
      int actionY = this.height - 62;
      this.buttonList.add(new LunarButton(0, margin, actionY, actionWidth, 24, this.language.text("connect"), true));
      this.buttonList.add(new LunarButton(1, margin + actionWidth + gap, actionY, actionWidth, 24, this.language.text("add")));
      this.buttonList.add(new LunarButton(2, margin + (actionWidth + gap) * 2, actionY, actionWidth, 24, this.language.text("remove")));
      this.buttonList.add(new LunarButton(3, margin + (actionWidth + gap) * 3, actionY, actionWidth, 24, this.language.text("refresh")));
      this.buttonList.add(new LunarButton(4, this.width - margin - 112, this.height - 30, 112, 22, this.language.text("back")));
      this.clampScroll();
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      switch(button.id) {
      case 0:
         this.connectSelected();
         break;
      case 1:
         this.addServer();
         break;
      case 2:
         if(this.selected >= 0 && this.selected < this.serverList.countServers()) {
            this.serverList.removeServerData(this.selected);
            this.serverList.saveServerList();
            this.selected = -1;
            this.notice = this.language.text("serverRemoved");
            this.noticeUntil = System.currentTimeMillis() + 1800L;
         }
         break;
      case 3:
         this.serverList.loadServerList();
         this.notice = this.language.text("serverUpdated");
         this.noticeUntil = System.currentTimeMillis() + 1800L;
         break;
      case 4:
         this.mc.displayGuiScreen(this.parent);
      }

   }

   private void connectSelected() {
      if(this.selected >= 0 && this.selected < this.serverList.countServers()) {
         ServerData data = this.serverList.getServerData(this.selected);
         this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, data));
      } else {
         this.notice = this.language.text("serverChoose");
         this.noticeUntil = System.currentTimeMillis() + 1800L;
      }
   }

   private void addServer() {
      String name = this.nameField.getText().trim();
      String address = this.addressField.getText().trim();
      if(name.length() == 0) {
         name = address;
      }

      if(address.length() == 0) {
         this.notice = this.language.text("serverAddressMissing");
         this.noticeUntil = System.currentTimeMillis() + 1800L;
      } else {
         this.serverList.addServerData(new ServerData(name, address, false));
         this.serverList.saveServerList();
         this.selected = this.serverList.countServers() - 1;
         this.nameField.setText("");
         this.addressField.setText("");
         this.notice = this.language.text("serverAdded");
         this.noticeUntil = System.currentTimeMillis() + 1800L;
      }
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if(!this.nameField.textboxKeyTyped(typedChar, keyCode) && !this.addressField.textboxKeyTyped(typedChar, keyCode)) {
         super.keyTyped(typedChar, keyCode);
      }
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
      this.addressField.mouseClicked(mouseX, mouseY, mouseButton);
      int listTop = 80;
      int rowHeight = 36;
      int margin = Math.max(20, Math.min(42, this.width / 10));
      if(mouseButton == 0 && mouseX >= margin && mouseX <= this.width - margin && mouseY >= listTop && mouseY < this.listBottom()) {
         int row = (mouseY - listTop) / rowHeight + this.scroll;
         if(row >= 0 && row < this.serverList.countServers()) {
            long now = Minecraft.getSystemTime();
            boolean doubleClick = row == this.lastClickedRow && now - this.lastClickTime < 280L;
            this.selected = row;
            ServerData data = this.serverList.getServerData(this.selected);
            this.nameField.setText(data.serverName);
            this.addressField.setText(data.serverIP);
            this.lastClickedRow = row;
            this.lastClickTime = now;
            if(doubleClick) {
               this.connectSelected();
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int wheel = Mouse.getEventDWheel();
      if(wheel != 0) {
         this.scroll += wheel > 0?-1:1;
         this.clampScroll();
      }

   }

   private int listBottom() {
      return this.height - 136;
   }

   private int visibleRows() {
      return Math.max(1, (this.listBottom() - 80) / 36);
   }

   private void clampScroll() {
      if(this.serverList == null) {
         this.scroll = Math.max(0, this.scroll);
      } else {
         this.scroll = Math.max(0, Math.min(this.scroll, this.serverList.countServers() - this.visibleRows()));
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

      drawRect(0, 0, this.width, this.height, -586936287);
      LunarUi.drawHeading(this.language.text("servers"), this.language.text("serverSubtitle"), this.width, 20);
      int margin = Math.max(20, Math.min(42, this.width / 10));
      int contentWidth = this.width - margin * 2;
      int listTop = 80;
      int rowHeight = 36;
      int listBottom = this.listBottom();
      LunarUi.drawPanel(margin - 8, listTop - 20, contentWidth + 16, listBottom - listTop + 28, false);
      LunarUi.drawSection(this.language.text("favorites"), margin, 65);
      this.clampScroll();

      for(int i = this.scroll; i < this.serverList.countServers() && i < this.scroll + this.visibleRows(); ++i) {
         int y = listTop + (i - this.scroll) * rowHeight;
         ServerData data = this.serverList.getServerData(i);
         boolean active = i == this.selected;
         int right = this.width - margin;
         drawRect(margin, y, right, y + 31, active?-367703957:-1190518728);
         drawRect(margin, y, margin + 3, y + 31, active?-11150849:-13543819);
         String name = this.fontRendererObj.trimStringToWidth(data.serverName, contentWidth - 150);
         String address = this.fontRendererObj.trimStringToWidth(data.serverIP, contentWidth - 150);
         this.drawString(this.fontRendererObj, name, margin + 12, y + 4, -919553);
         this.drawString(this.fontRendererObj, address, margin + 12, y + 17, -6572852);
         String ping = data.pingToServer >= 0L?data.pingToServer + " ms":this.language.text("pingUnknown");
         this.drawString(this.fontRendererObj, ping, right - this.fontRendererObj.getStringWidth(ping) - 10, y + 10, -11150849);
      }

      if(this.serverList.countServers() == 0) {
         this.drawCenteredString(this.fontRendererObj, this.language.text("serverEmpty"), this.width / 2, 128, -6572852);
      }

      this.drawCenteredString(this.fontRendererObj, this.language.text("serverHint"), this.width / 2, this.height - 126, -6572852);
      this.drawString(this.fontRendererObj, this.language.text("name"), margin, this.height - 108, -6572852);
      int nameWidth = (contentWidth - 12) * 2 / 5;
      this.drawString(this.fontRendererObj, this.language.text("address"), margin + nameWidth + 12, this.height - 108, -6572852);
      this.nameField.drawTextBox();
      this.addressField.drawTextBox();
      if(System.currentTimeMillis() < this.noticeUntil) {
         this.drawCenteredString(this.fontRendererObj, this.notice, this.width / 2, this.height - 10, -919553);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
