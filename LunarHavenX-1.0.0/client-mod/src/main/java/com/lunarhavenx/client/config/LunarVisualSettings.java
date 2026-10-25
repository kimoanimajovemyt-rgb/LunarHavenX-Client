package com.lunarhavenx.client.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.client.Minecraft;

public final class LunarVisualSettings {
   private static final LunarVisualSettings INSTANCE = new LunarVisualSettings();
   private final File file;
   public boolean hudEnabled = true;
   public boolean showCoordinates = true;
   public boolean showStatus = true;
   public boolean showArmor = true;
   public boolean showCps = true;
   public boolean showKeystrokes = true;
   public boolean showClock;
   public boolean showBiome;
   public boolean showDirection;
   public boolean animationsEnabled = true;
   public boolean particlesEnabled = true;
   public int hudOpacity = 72;
   public int uiScale = 100;
   public int hudInfoX = 4;
   public int hudInfoY = 2;
   public int hudArmorX = 100;
   public int hudArmorY = 2;
   public int hudStatusX = 4;
   public int hudStatusY = 25;
   public int hudKeysX = 4;
   public int hudKeysY = 60;
   public int hudCpsX = 4;
   public int hudCpsY = 83;

   private LunarVisualSettings() {
      this.file = new File(Minecraft.getMinecraft().mcDataDir, "config/lunarhavenx/visual.properties");
      this.load();
   }

   public static LunarVisualSettings get() {
      return INSTANCE;
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

            this.hudEnabled = this.bool(p, "hudEnabled", this.hudEnabled);
            this.showCoordinates = this.bool(p, "showCoordinates", this.showCoordinates);
            this.showStatus = this.bool(p, "showStatus", this.showStatus);
            this.showArmor = this.bool(p, "showArmor", this.showArmor);
            this.showCps = this.bool(p, "showCps", this.showCps);
            this.showKeystrokes = this.bool(p, "showKeystrokes", this.showKeystrokes);
            this.showClock = this.bool(p, "showClock", this.showClock);
            this.showBiome = this.bool(p, "showBiome", this.showBiome);
            this.showDirection = this.bool(p, "showDirection", this.showDirection);
            this.animationsEnabled = this.bool(p, "animationsEnabled", this.animationsEnabled);
            this.particlesEnabled = this.bool(p, "particlesEnabled", this.particlesEnabled);
            this.hudOpacity = this.number(p, "hudOpacity", this.hudOpacity, 20, 100);
            this.uiScale = this.number(p, "uiScale", this.uiScale, 75, 125);
            this.applyHudPositionProperties(p, "");
         } catch (IOException var7) {
            ;
         }

      }
   }

   public void save() {
      File parent = this.file.getParentFile();
      if(parent.exists() || parent.mkdirs()) {
         Properties p = new Properties();
         p.setProperty("hudEnabled", String.valueOf(this.hudEnabled));
         p.setProperty("showCoordinates", String.valueOf(this.showCoordinates));
         p.setProperty("showStatus", String.valueOf(this.showStatus));
         p.setProperty("showArmor", String.valueOf(this.showArmor));
         p.setProperty("showCps", String.valueOf(this.showCps));
         p.setProperty("showKeystrokes", String.valueOf(this.showKeystrokes));
         p.setProperty("showClock", String.valueOf(this.showClock));
         p.setProperty("showBiome", String.valueOf(this.showBiome));
         p.setProperty("showDirection", String.valueOf(this.showDirection));
         p.setProperty("animationsEnabled", String.valueOf(this.animationsEnabled));
         p.setProperty("particlesEnabled", String.valueOf(this.particlesEnabled));
         p.setProperty("hudOpacity", String.valueOf(this.hudOpacity));
         p.setProperty("uiScale", String.valueOf(this.uiScale));
         this.writeHudPositionProperties(p, "");

         try {
            FileOutputStream out = new FileOutputStream(this.file);

            try {
               p.store(out, "LunarHavenX visual settings");
            } finally {
               out.close();
            }
         } catch (IOException var8) {
            ;
         }

      }
   }

   private boolean bool(Properties p, String key, boolean fallback) {
      return Boolean.parseBoolean(p.getProperty(key, String.valueOf(fallback)));
   }

   private int number(Properties p, String key, int fallback, int min, int max) {
      try {
         return Math.max(min, Math.min(max, Integer.parseInt(p.getProperty(key))));
      } catch (Exception var7) {
         return fallback;
      }
   }

   public int getHudX(int element) {
      switch(element) {
      case 1:
         return this.hudArmorX;
      case 2:
         return this.hudStatusX;
      case 3:
         return this.hudKeysX;
      case 4:
         return this.hudCpsX;
      default:
         return this.hudInfoX;
      }
   }

   public int getHudY(int element) {
      switch(element) {
      case 1:
         return this.hudArmorY;
      case 2:
         return this.hudStatusY;
      case 3:
         return this.hudKeysY;
      case 4:
         return this.hudCpsY;
      default:
         return this.hudInfoY;
      }
   }

   public void setHudPosition(int element, int x, int y) {
      x = Math.max(0, Math.min(100, x));
      y = Math.max(0, Math.min(100, y));
      switch(element) {
      case 1:
         this.hudArmorX = x;
         this.hudArmorY = y;
         break;
      case 2:
         this.hudStatusX = x;
         this.hudStatusY = y;
         break;
      case 3:
         this.hudKeysX = x;
         this.hudKeysY = y;
         break;
      case 4:
         this.hudCpsX = x;
         this.hudCpsY = y;
         break;
      default:
         this.hudInfoX = x;
         this.hudInfoY = y;
      }

   }

   public void resetHudPositions() {
      this.hudInfoX = 4;
      this.hudInfoY = 2;
      this.hudArmorX = 100;
      this.hudArmorY = 2;
      this.hudStatusX = 4;
      this.hudStatusY = 25;
      this.hudKeysX = 4;
      this.hudKeysY = 60;
      this.hudCpsX = 4;
      this.hudCpsY = 83;
   }

   public void writeHudPositionProperties(Properties properties, String prefix) {
      properties.setProperty(prefix + "hudInfoX", String.valueOf(this.hudInfoX));
      properties.setProperty(prefix + "hudInfoY", String.valueOf(this.hudInfoY));
      properties.setProperty(prefix + "hudArmorX", String.valueOf(this.hudArmorX));
      properties.setProperty(prefix + "hudArmorY", String.valueOf(this.hudArmorY));
      properties.setProperty(prefix + "hudStatusX", String.valueOf(this.hudStatusX));
      properties.setProperty(prefix + "hudStatusY", String.valueOf(this.hudStatusY));
      properties.setProperty(prefix + "hudKeysX", String.valueOf(this.hudKeysX));
      properties.setProperty(prefix + "hudKeysY", String.valueOf(this.hudKeysY));
      properties.setProperty(prefix + "hudCpsX", String.valueOf(this.hudCpsX));
      properties.setProperty(prefix + "hudCpsY", String.valueOf(this.hudCpsY));
   }

   public void applyHudPositionProperties(Properties properties, String prefix) {
      this.hudInfoX = this.number(properties, prefix + "hudInfoX", this.hudInfoX, 0, 100);
      this.hudInfoY = this.number(properties, prefix + "hudInfoY", this.hudInfoY, 0, 100);
      this.hudArmorX = this.number(properties, prefix + "hudArmorX", this.hudArmorX, 0, 100);
      this.hudArmorY = this.number(properties, prefix + "hudArmorY", this.hudArmorY, 0, 100);
      this.hudStatusX = this.number(properties, prefix + "hudStatusX", this.hudStatusX, 0, 100);
      this.hudStatusY = this.number(properties, prefix + "hudStatusY", this.hudStatusY, 0, 100);
      this.hudKeysX = this.number(properties, prefix + "hudKeysX", this.hudKeysX, 0, 100);
      this.hudKeysY = this.number(properties, prefix + "hudKeysY", this.hudKeysY, 0, 100);
      this.hudCpsX = this.number(properties, prefix + "hudCpsX", this.hudCpsX, 0, 100);
      this.hudCpsY = this.number(properties, prefix + "hudCpsY", this.hudCpsY, 0, 100);
   }
}
