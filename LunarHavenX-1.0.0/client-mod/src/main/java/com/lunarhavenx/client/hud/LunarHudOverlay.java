package com.lunarhavenx.client.hud;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.config.LunarVisualSettings;
import com.lunarhavenx.client.gui.GuiLunarHudEditor;
import com.lunarhavenx.client.gui.LunarUi;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class LunarHudOverlay {
   private final Minecraft mc = Minecraft.getMinecraft();
   private final Deque leftClickTimes;
   private final Deque rightClickTimes;
   private final SimpleDateFormat clockFormat;
   private String cachedClock;
   private long lastClockUpdate;
   private boolean armorRenderWarningLogged;

   public LunarHudOverlay() {
      this.leftClickTimes = new ArrayDeque();
      this.rightClickTimes = new ArrayDeque();
      this.clockFormat = new SimpleDateFormat("HH:mm");
      this.cachedClock = "--:--";
   }

   @SubscribeEvent
   public void onMouseInput(MouseEvent event) {
      if(event.buttonstate && event.button >= 0 && this.mc.currentScreen == null && this.mc.thePlayer != null && this.mc.theWorld != null) {
         long now = System.nanoTime() / 1000000L;
         if(event.button == 0) {
            this.addClick(this.leftClickTimes, now);
         }

         if(event.button == 1) {
            this.addClick(this.rightClickTimes, now);
         }

      }
   }

   private void addClick(Deque clicks, long now) {
      clicks.addLast(Long.valueOf(now));

      while(clicks.size() > 64) {
         clicks.removeFirst();
      }

   }

   @SubscribeEvent
   public void onRenderOverlay(Post event) {
      if(event.type == ElementType.ALL && this.mc.thePlayer != null && this.mc.theWorld != null) {
         LunarVisualSettings settings = LunarVisualSettings.get();
         boolean editing = this.mc.currentScreen instanceof GuiLunarHudEditor;
         if(settings.hudEnabled || editing) {
            EntityPlayer player = this.mc.thePlayer;
            FontRenderer font = this.mc.fontRendererObj;
            float scale = Math.max(0.75F, Math.min(1.25F, (float)settings.uiScale / 100.0F));
            int logicalWidth = (int)((float)event.resolution.getScaledWidth() / scale);
            int logicalHeight = (int)((float)event.resolution.getScaledHeight() / scale);
            int accent = this.accentColor(settings);
            int infoRows = (!settings.showCoordinates && !editing?0:1) + (!settings.showClock && !editing?0:1) + (!settings.showBiome && !editing?0:1) + (!settings.showDirection && !editing?0:1);
            int mainHeight = 36 + infoRows * 14;
            int infoX = this.position(settings.getHudX(0), logicalWidth, 204);
            int infoY = this.position(settings.getHudY(0), logicalHeight, mainHeight);
            int armorX = this.position(settings.getHudX(1), logicalWidth, 164);
            int armorY = this.position(settings.getHudY(1), logicalHeight, 44);
            int statusHeight = this.statusPanelHeight(player);
            int statusX = this.position(settings.getHudX(2), logicalWidth, 218);
            int statusY = this.position(settings.getHudY(2), logicalHeight, statusHeight);
            int keysX = this.position(settings.getHudX(3), logicalWidth, 94);
            int keysY = this.position(settings.getHudY(3), logicalHeight, 92);
            int cpsX = this.position(settings.getHudX(4), logicalWidth, 112);
            int cpsY = this.position(settings.getHudY(4), logicalHeight, 27);
            GlStateManager.pushMatrix();

            try {
               GlStateManager.scale(scale, scale, 1.0F);
               this.drawPanel(infoX, infoY, 204, mainHeight, this.panelColor(settings), accent);
               this.drawString(font, "LUNARHAVENX", infoX + 8, infoY + 6, accent);
               this.drawString(font, "FPS  " + Minecraft.getDebugFPS(), infoX + 8, infoY + 20, -1574913);
               int infoLineY = infoY + 34;
               if(settings.showCoordinates || editing) {
                  this.drawInfoLine(font, "XYZ  " + this.formatCoord(player.posX) + "  " + this.formatCoord(player.posY) + "  " + this.formatCoord(player.posZ), infoX, infoLineY, 188, -5319442);
                  infoLineY += 14;
               }

               if(settings.showClock || editing) {
                  this.drawInfoLine(font, LunarLanguageManager.get().text("clock") + "  " + this.clock(), infoX, infoLineY, 188, -5319442);
                  infoLineY += 14;
               }

               if(settings.showBiome || editing) {
                  String biome = player.worldObj.getBiomeGenForCoords(new BlockPos((int)Math.floor(player.posX), 0, (int)Math.floor(player.posZ))).biomeName;
                  this.drawInfoLine(font, LunarLanguageManager.get().text("biome") + "  " + biome, infoX, infoLineY, 188, -5319442);
                  infoLineY += 14;
               }

               if(settings.showDirection || editing) {
                  this.drawInfoLine(font, LunarLanguageManager.get().text("direction") + "  " + this.direction(player.rotationYaw), infoX, infoLineY, 188, -5319442);
               }

               if(settings.showArmor || editing) {
                  this.drawArmorPanel(armorX, armorY, settings, accent);
               }

               if(settings.showStatus || editing) {
                  this.drawStatusPanel(statusX, statusY, player, settings, accent);
               }

               if(settings.showKeystrokes || editing) {
                  this.drawKeystrokes(keysX, keysY, settings, accent);
               }

               if(settings.showCps || editing) {
                  this.drawCps(cpsX, cpsY, settings, accent);
               }
            } finally {
               GlStateManager.popMatrix();
            }

         }
      }
   }

   private int position(int percent, int screenExtent, int componentExtent) {
      int available = Math.max(0, screenExtent - componentExtent);
      return available * Math.max(0, Math.min(100, percent)) / 100;
   }

   private String clock() {
      long now = System.currentTimeMillis();
      if(now - this.lastClockUpdate >= 1000L) {
         this.cachedClock = this.clockFormat.format(new Date(now));
         this.lastClockUpdate = now;
      }

      return this.cachedClock;
   }

   private String direction(float yaw) {
      String[] directions = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
      int index = (int)Math.floor((double)((yaw + 22.5F) / 45.0F));
      return directions[(index % 8 + 8) % 8];
   }

   private void drawInfoLine(FontRenderer font, String value, int panelLeft, int y, int maxWidth, int color) {
      this.drawString(font, font.trimStringToWidth(value, maxWidth), panelLeft + 8, y, color);
   }

   private void drawKeystrokes(int left, int top, LunarVisualSettings settings, int accent) {
      LunarLanguageManager language = LunarLanguageManager.get();
      this.drawPanel(left, top, 94, 92, this.panelColor(settings), accent);
      this.drawString(this.mc.fontRendererObj, language.text("keys"), left + 8, top + 5, accent);
      GameSettings game = this.mc.gameSettings;
      this.drawKeyCell(left + 35, top + 18, 24, 14, game.keyBindForward, accent);
      this.drawKeyCell(left + 7, top + 35, 24, 14, game.keyBindLeft, accent);
      this.drawKeyCell(left + 35, top + 35, 24, 14, game.keyBindBack, accent);
      this.drawKeyCell(left + 63, top + 35, 24, 14, game.keyBindRight, accent);
      this.drawKeyCell(left + 7, top + 52, 80, 14, game.keyBindJump, accent);
      this.drawString(this.mc.fontRendererObj, game.keyBindSneak.isKeyDown()?language.text("sneaking"):language.text("sneak"), left + 7, top + 73, game.keyBindSneak.isKeyDown()?accent:-6572852);
      this.drawString(this.mc.fontRendererObj, game.keyBindSprint.isKeyDown()?language.text("sprinting"):language.text("sprint"), left + 51, top + 73, game.keyBindSprint.isKeyDown()?accent:-6572852);
   }

   private void drawKeyCell(int left, int top, int width, int height, KeyBinding binding, int accent) {
      boolean down = binding.isKeyDown();
      this.drawRect(left, top, left + width, top + height, down?-300590458:-1039523784);
      String label = binding.getKeyCode() == 0?"-":Keyboard.getKeyName(binding.getKeyCode());
      label = this.mc.fontRendererObj.trimStringToWidth(label, width - 4);
      int textX = left + (width - this.mc.fontRendererObj.getStringWidth(label)) / 2;
      this.drawString(this.mc.fontRendererObj, label, textX, top + 3, down?-1:-919553);
      if(down) {
         this.drawRect(left, top + height - 1, left + width, top + height, accent);
      }

   }

   private void drawCps(int left, int top, LunarVisualSettings settings, int accent) {
      long now = System.nanoTime() / 1000000L;
      int leftClicks = this.countRecent(this.leftClickTimes, now);
      int rightClicks = this.countRecent(this.rightClickTimes, now);
      this.drawPanel(left, top, 112, 27, this.panelColor(settings), accent);
      this.drawString(this.mc.fontRendererObj, LunarLanguageManager.get().text("leftClick") + " " + leftClicks + "   " + LunarLanguageManager.get().text("rightClick") + " " + rightClicks, left + 8, top + 9, -919553);
   }

   private int countRecent(Deque clicks, long now) {
      while(!clicks.isEmpty() && now - ((Long)clicks.peekFirst()).longValue() >= 1000L) {
         clicks.removeFirst();
      }

      return clicks.size();
   }

   private void drawArmorPanel(int left, int top, LunarVisualSettings settings, int accent) {
      this.drawPanel(left, top, 164, 44, this.panelColor(settings), accent);
      this.drawString(this.mc.fontRendererObj, LunarLanguageManager.get().text("armor"), left + 8, top + 5, accent);
      EntityPlayer player = this.mc.thePlayer;
      if(player == null || player.inventory == null || player.inventory.armorInventory == null) {
         return;
      }

      ItemStack[] armor = player.inventory.armorInventory;
      RenderItem itemRenderer = this.mc.getRenderItem();
      int x = left + 8;

      for(int slot = 3; slot >= 0; --slot) {
         ItemStack stack = slot < armor.length ? armor[slot] : null;
         if(stack != null && stack.getItem() != null && itemRenderer != null) {
            GlStateManager.pushMatrix();
            boolean renderFailed = false;
            try {
               RenderHelper.enableGUIStandardItemLighting();
               itemRenderer.renderItemAndEffectIntoGUI(stack, x, top + 17);
               itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRendererObj, stack, x, top + 17, (String)null);
            } catch (NullPointerException renderError) {
               renderFailed = true;
               if(!this.armorRenderWarningLogged) {
                  this.armorRenderWarningLogged = true;
                  System.err.println("[LunarHavenX] Um item de armadura não pôde ser renderizado; o HUD continuará sem esse ícone.");
               }
            } finally {
               RenderHelper.disableStandardItemLighting();
               GlStateManager.popMatrix();
            }
            if(renderFailed) {
               this.drawRect(x + 4, top + 22, x + 16, top + 34, 1715170958);
            }
         } else {
            this.drawRect(x + 4, top + 22, x + 16, top + 34, 1715170958);
         }

         x += 35;
      }

   }

   private void drawStatusPanel(int left, int top, EntityPlayer player, LunarVisualSettings settings, int accent) {
      Collection<PotionEffect> effects = player.getActivePotionEffects();
      int height = this.statusPanelHeight(player);
      this.drawPanel(left, top, 218, height, this.panelColor(settings), accent);
      LunarLanguageManager language = LunarLanguageManager.get();
      this.drawString(this.mc.fontRendererObj, language.text("playerStatus"), left + 8, top + 6, accent);
      String health = language.text("health") + "  " + Math.round(player.getHealth()) + "/" + Math.round(player.getMaxHealth());
      String food = language.text("hunger") + "  " + player.getFoodStats().getFoodLevel() + "/20";
      this.drawString(this.mc.fontRendererObj, health, left + 8, top + 21, -24917);
      this.drawString(this.mc.fontRendererObj, food, left + 112, top + 21, -11398);
      int y = top + 36;
      if(effects.isEmpty()) {
         this.drawString(this.mc.fontRendererObj, language.text("noEffects"), left + 8, y, -6439982);
      } else {
         int count = 0;

         for(PotionEffect effect : effects) {
            if(count++ >= 5) {
               break;
            }

            int potionId = effect.getPotionID();
            Potion[] potions = Potion.potionTypes;
            Potion potion = potions != null && potionId >= 0 && potionId < potions.length?potions[potionId]:null;
            String name = potion == null?language.text("effect"):I18n.format(potion.getName(), new Object[0]);
            this.drawString(this.mc.fontRendererObj, name + "  " + Potion.getDurationString(effect), left + 8, y, -2625793);
            y += 15;
         }

      }
   }

   private int statusPanelHeight(EntityPlayer player) {
      int rows = Math.max(1, Math.min(5, player.getActivePotionEffects().size()));
      return 22 + rows * 15;
   }

   private void drawPanel(int left, int top, int width, int height, int color, int accent) {
      this.drawRect(left, top, left + width, top + height, color);
      LunarUi.drawTexturePatch(left + 1, top + 1, width - 2, height - 2, 0, 600, 0.14F);
      this.drawRect(left, top, left + width, top + 1, accent);
      this.drawRect(left, top + height - 1, left + width, top + height, -1441109094);
   }

   private void drawString(FontRenderer font, String text, int x, int y, int color) {
      font.drawStringWithShadow(text, (float)x, (float)y, color);
   }

   private void drawRect(int left, int top, int right, int bottom, int color) {
      Gui.drawRect(left, top, right, bottom, color);
   }

   private int panelColor(LunarVisualSettings settings) {
      return settings.hudOpacity * 255 / 100 << 24 | 398638;
   }

   private int accentColor(LunarVisualSettings settings) {
      if(!settings.animationsEnabled) {
         return -11150849;
      } else {
         int green = 175 + (int)(24.0D * (0.5D + 0.5D * Math.sin((double)System.currentTimeMillis() / 420.0D)));
         return -11206656 | green << 8 | 255;
      }
   }

   private String formatCoord(double coordinate) {
      return String.valueOf((int)Math.floor(coordinate));
   }
}
