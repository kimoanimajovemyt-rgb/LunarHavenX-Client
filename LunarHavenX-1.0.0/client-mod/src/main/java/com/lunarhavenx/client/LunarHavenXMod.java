package com.lunarhavenx.client;

import com.lunarhavenx.client.gui.GuiLunarChat;
import com.lunarhavenx.client.gui.GuiLunarLoading;
import com.lunarhavenx.client.gui.GuiLunarMainMenu;
import com.lunarhavenx.client.hud.LunarHudOverlay;
import com.lunarhavenx.client.input.LunarKeybindManager;
import com.lunarhavenx.client.mobile.LunarMobileCompatibility;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod(modid = LunarHavenXMod.MOD_ID, name = LunarHavenXMod.NAME, version = LunarHavenXMod.VERSION,
   acceptedMinecraftVersions = "[1.8.9]")
public class LunarHavenXMod {
   public static final String MOD_ID = "lunarhavenx";
   public static final String NAME = "LunarHavenX Client";
   public static final String VERSION = "0.5.9-alpha";
   // Avoid replaying the startup animation on every return to the main menu.
   private boolean loadingScreenShown;

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(this);
      MinecraftForge.EVENT_BUS.register(new LunarHudOverlay());
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      LunarKeybindManager.get().register();
      LunarMobileCompatibility.applyRecommendedSettings();
      LunarMobileCompatibility.prepareClientTick();
   }

   @SubscribeEvent
   public void onGuiOpen(GuiOpenEvent event) {
      if(event.gui instanceof GuiChat && !(event.gui instanceof GuiLunarChat)) {
         event.gui = new GuiLunarChat();
      } else if(event.gui instanceof GuiMainMenu) {
         if(!this.loadingScreenShown) {
            this.loadingScreenShown = true;
            event.gui = new GuiLunarLoading();
         } else {
            // Keep returning players in the Lunar UI without another loading delay.
            event.gui = new GuiLunarMainMenu();
         }
      }
   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      if(event.phase == Phase.END) {
         LunarMobileCompatibility.prepareClientTick();
         LunarKeybindManager.get().tick();
      }

   }
}
