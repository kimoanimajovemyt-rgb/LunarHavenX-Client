package com.lunarhavenx.client.input;

import com.lunarhavenx.client.config.LunarVisualSettings;
import com.lunarhavenx.client.gui.GuiLunarCosmetics;
import com.lunarhavenx.client.gui.GuiLunarServers;
import com.lunarhavenx.client.gui.GuiLunarSettings;
import com.lunarhavenx.client.performance.LunarPerformanceManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public final class LunarKeybindManager {
   private static final LunarKeybindManager INSTANCE = new LunarKeybindManager();
   private final File file;
   private final KeyBinding[] bindings = new KeyBinding[]{
      new KeyBinding("Abrir configurações visuais", Keyboard.KEY_RSHIFT, "LunarHavenX"),
      new KeyBinding("Alternar HUD", Keyboard.KEY_F6, "LunarHavenX"),
      new KeyBinding("Ciclar perfil de desempenho", Keyboard.KEY_F7, "LunarHavenX"),
      new KeyBinding("Abrir cosméticos", Keyboard.KEY_F8, "LunarHavenX"),
      new KeyBinding("Abrir servidores", Keyboard.KEY_F9, "LunarHavenX"),
      new KeyBinding("Macro 1", Keyboard.KEY_NONE, "LunarHavenX Macros"),
      new KeyBinding("Macro 2", Keyboard.KEY_NONE, "LunarHavenX Macros"),
      new KeyBinding("Macro 3", Keyboard.KEY_NONE, "LunarHavenX Macros")
   };
   private final String[] macros = new String[]{"", "", ""};

   private LunarKeybindManager() {
      this.file = new File(Minecraft.getMinecraft().mcDataDir, "config/lunarhavenx/keybinds.properties");
      this.load();
   }

   public static LunarKeybindManager get() {
      return INSTANCE;
   }

   public KeyBinding[] getBindings() {
      return this.bindings;
   }

   public String getMacro(int index) {
      return this.macros[index];
   }

   public void setMacro(int index, String value) {
      this.macros[index] = value == null?"":value.trim();
      this.save();
   }

   public void register() {
      for(KeyBinding binding : this.bindings) {
         ClientRegistry.registerKeyBinding(binding);
      }

   }

   public void tick() {
      Minecraft mc = Minecraft.getMinecraft();
      if(this.bindings[0].isPressed() && mc.thePlayer != null && mc.currentScreen == null) {
         mc.displayGuiScreen(new GuiLunarSettings((GuiScreen)null));
      }

      if(this.bindings[1].isPressed()) {
         LunarVisualSettings settings = LunarVisualSettings.get();
         settings.hudEnabled = !settings.hudEnabled;
         settings.save();
      }

      if(this.bindings[2].isPressed()) {
         LunarPerformanceManager.get().setProfile(LunarPerformanceManager.get().getProfile().next());
      }

      if(this.bindings[3].isPressed() && mc.currentScreen == null) {
         mc.displayGuiScreen(new GuiLunarCosmetics((GuiScreen)null));
      }

      if(this.bindings[4].isPressed() && mc.currentScreen == null) {
         mc.displayGuiScreen(new GuiLunarServers((GuiScreen)null));
      }

      for(int i = 0; i < 3; ++i) {
         if(this.bindings[5 + i].isPressed() && mc.currentScreen == null && mc.thePlayer != null && this.macros[i].length() > 0) {
            mc.thePlayer.sendChatMessage(this.macros[i]);
         }
      }

   }

   public void load() {
      if(this.file.isFile()) {
         Properties p = new Properties();

         try {
            FileInputStream in = new FileInputStream(this.file);

            try {
               p.load(in);
            } finally {
               in.close();
            }

            for(int i = 0; i < this.bindings.length; ++i) {
               String code = p.getProperty("key." + i);
               if(code != null) {
                  this.bindings[i].setKeyCode(this.parse(code, this.bindings[i].getKeyCode()));
               }
            }

            for(int i = 0; i < this.macros.length; ++i) {
               this.macros[i] = p.getProperty("macro." + i, "");
            }
         } catch (IOException var8) {
            ;
         }

      }
   }

   public void save() {
      File parent = this.file.getParentFile();
      if(parent.exists() || parent.mkdirs()) {
         Properties p = new Properties();

         for(int i = 0; i < this.bindings.length; ++i) {
            p.setProperty("key." + i, String.valueOf(this.bindings[i].getKeyCode()));
         }

         for(int i = 0; i < this.macros.length; ++i) {
            p.setProperty("macro." + i, this.macros[i]);
         }

         try {
            FileOutputStream out = new FileOutputStream(this.file);

            try {
               p.store(out, "LunarHavenX keybinds and single-action macros");
            } finally {
               out.close();
            }
         } catch (IOException var8) {
            ;
         }

      }
   }

   private int parse(String value, int fallback) {
      try {
         return Integer.parseInt(value);
      } catch (NumberFormatException var4) {
         return fallback;
      }
   }
}
