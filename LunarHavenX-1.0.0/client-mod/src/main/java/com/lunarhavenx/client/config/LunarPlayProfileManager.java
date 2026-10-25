package com.lunarhavenx.client.config;

import com.lunarhavenx.client.config.LunarLanguageManager;
import com.lunarhavenx.client.config.LunarVisualSettings;
import com.lunarhavenx.client.performance.LunarPerformanceManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.client.Minecraft;

public final class LunarPlayProfileManager {
   public static final int PVP = 0;
   public static final int BALANCED = 1;
   public static final int LIGHT = 2;
   public static final int PROFILE_COUNT = 6;
   private static final String[] PROFILE_FIELDS = new String[]{"hudEnabled", "showCoordinates", "showStatus", "showArmor", "showCps", "showKeystrokes", "animationsEnabled", "particlesEnabled", "showClock", "showBiome", "showDirection", "hudOpacity", "uiScale", "performance", "hudInfoX", "hudInfoY", "hudArmorX", "hudArmorY", "hudStatusX", "hudStatusY", "hudKeysX", "hudKeysY", "hudCpsX", "hudCpsY"};
   private static final LunarPlayProfileManager INSTANCE = new LunarPlayProfileManager();
   private final File file;
   private final File profileDirectory;
   private int active = -1;

   private LunarPlayProfileManager() {
      this.profileDirectory = new File(Minecraft.getMinecraft().mcDataDir, "config/lunarhavenx/profiles");
      this.file = new File(this.profileDirectory.getParentFile(), "play-profiles.properties");
      Properties saved = this.read();

      try {
         this.active = Integer.parseInt(saved.getProperty("active", "-1"));
      } catch (NumberFormatException var3) {
         this.active = -1;
      }

      if(this.active < -1 || this.active >= 6) {
         this.active = -1;
      }

   }

   public static LunarPlayProfileManager get() {
      return INSTANCE;
   }

   public int getActive() {
      return this.active;
   }

   public String getLabel(int index) {
      Properties p = this.read();
      String savedName = p.getProperty("profile." + index + ".name", "").trim();
      return !savedName.isEmpty()?savedName:(index == 0?LunarLanguageManager.get().text("pvp"):(index == 1?LunarLanguageManager.get().text("balanced"):(index == 2?LunarLanguageManager.get().text("light"):LunarLanguageManager.get().text("profileSlot") + " " + (index + 1))));
   }

   public boolean isSaved(int index) {
      return Boolean.parseBoolean(this.read().getProperty("profile." + index + ".saved", "false"));
   }

   public void setLabel(int index, String label) {
      if(index >= 0 && index < 6) {
         Properties p = this.read();
         String clean = label == null?"":label.trim();
         if(clean.length() > 24) {
            clean = clean.substring(0, 24);
         }

         if(clean.isEmpty()) {
            p.remove("profile." + index + ".name");
         } else {
            p.setProperty("profile." + index + ".name", clean);
         }

         this.write(p);
      }
   }

   public void apply(int index) {
      if(index >= 0 && index < 6) {
         Properties p = this.read();
         String prefix = "profile." + index + ".";
         if(Boolean.parseBoolean(p.getProperty(prefix + "saved", "false"))) {
            LunarVisualSettings s = LunarVisualSettings.get();
            s.hudEnabled = this.bool(p, prefix + "hudEnabled", true);
            s.showCoordinates = this.bool(p, prefix + "showCoordinates", true);
            s.showStatus = this.bool(p, prefix + "showStatus", true);
            s.showArmor = this.bool(p, prefix + "showArmor", true);
            s.showCps = this.bool(p, prefix + "showCps", true);
            s.showKeystrokes = this.bool(p, prefix + "showKeystrokes", true);
            s.animationsEnabled = this.bool(p, prefix + "animationsEnabled", true);
            s.particlesEnabled = this.bool(p, prefix + "particlesEnabled", true);
            s.showClock = this.bool(p, prefix + "showClock", false);
            s.showBiome = this.bool(p, prefix + "showBiome", false);
            s.showDirection = this.bool(p, prefix + "showDirection", false);
            s.hudOpacity = this.number(p, prefix + "hudOpacity", 72, 20, 100);
            s.uiScale = this.number(p, prefix + "uiScale", 100, 75, 125);
            s.applyHudPositionProperties(p, prefix);
            s.save();
            LunarPerformanceManager.Profile performance = this.performance(p.getProperty(prefix + "performance"));
            LunarPerformanceManager.get().setProfile(performance);
         } else if(index <= 2) {
            this.applyDefault(index);
         }

         this.active = index;
         p.setProperty("active", String.valueOf(this.active));
         this.write(p);
      }
   }

   public void saveCurrent(int index) {
      if(index >= 0 && index < 6) {
         LunarVisualSettings s = LunarVisualSettings.get();
         Properties p = this.read();
         String prefix = "profile." + index + ".";
         p.setProperty(prefix + "saved", "true");
         p.setProperty(prefix + "hudEnabled", String.valueOf(s.hudEnabled));
         p.setProperty(prefix + "showCoordinates", String.valueOf(s.showCoordinates));
         p.setProperty(prefix + "showStatus", String.valueOf(s.showStatus));
         p.setProperty(prefix + "showArmor", String.valueOf(s.showArmor));
         p.setProperty(prefix + "showCps", String.valueOf(s.showCps));
         p.setProperty(prefix + "showKeystrokes", String.valueOf(s.showKeystrokes));
         p.setProperty(prefix + "animationsEnabled", String.valueOf(s.animationsEnabled));
         p.setProperty(prefix + "particlesEnabled", String.valueOf(s.particlesEnabled));
         p.setProperty(prefix + "showClock", String.valueOf(s.showClock));
         p.setProperty(prefix + "showBiome", String.valueOf(s.showBiome));
         p.setProperty(prefix + "showDirection", String.valueOf(s.showDirection));
         p.setProperty(prefix + "hudOpacity", String.valueOf(s.hudOpacity));
         p.setProperty(prefix + "uiScale", String.valueOf(s.uiScale));
         s.writeHudPositionProperties(p, prefix);
         p.setProperty(prefix + "performance", LunarPerformanceManager.get().getProfile().name());
         this.active = index;
         p.setProperty("active", String.valueOf(this.active));
         this.write(p);
      }
   }

   public void markCustom() {
      if(this.active != -1) {
         this.active = -1;
         Properties p = this.read();
         p.setProperty("active", "-1");
         this.write(p);
      }

   }

   public boolean exportProfile(int index, String fileName) {
      if(index >= 0 && index < 6) {
         String safeName = this.safeFileName(fileName);
         if(safeName == null) {
            return false;
         } else {
            if(!this.isSaved(index)) {
               this.saveCurrent(index);
               if(!this.isSaved(index)) {
                  return false;
               }
            }

            Properties source = this.read();
            String prefix = "profile." + index + ".";
            Properties exported = new Properties();

            for(String field : PROFILE_FIELDS) {
               String value = source.getProperty(prefix + field);
               if(value != null) {
                  exported.setProperty(field, value);
               }
            }

            exported.setProperty("profileFormat", "1");
            exported.setProperty("name", this.getLabel(index));
            if(!this.profileDirectory.exists() && !this.profileDirectory.mkdirs()) {
               return false;
            } else {
               File outFile = new File(this.profileDirectory, safeName + ".lhxprofile");

               try {
                  FileOutputStream out = new FileOutputStream(outFile);

                  try {
                     exported.store(out, "LunarHavenX profile export");
                  } finally {
                     out.close();
                  }

                  return true;
               } catch (IOException var16) {
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean importProfile(int targetIndex, String fileName) {
      if(targetIndex >= 0 && targetIndex < 6) {
         String safeName = this.safeFileName(fileName);
         if(safeName == null) {
            return false;
         } else {
            File inFile = new File(this.profileDirectory, safeName + ".lhxprofile");
            if(!inFile.isFile()) {
               return false;
            } else {
               Properties imported = new Properties();

               try {
                  FileInputStream in = new FileInputStream(inFile);

                  try {
                     imported.load(in);
                  } finally {
                     in.close();
                  }
               } catch (IOException var16) {
                  return false;
               }

               if(!"1".equals(imported.getProperty("profileFormat"))) {
                  return false;
               } else {
                  for(String field : PROFILE_FIELDS) {
                     if(imported.getProperty(field) == null) {
                        return false;
                     }
                  }

                  Properties target = this.read();
                  String prefix = "profile." + targetIndex + ".";

                  for(String field : PROFILE_FIELDS) {
                     String value = imported.getProperty(field);
                     if(value != null) {
                        target.setProperty(prefix + field, value);
                     }
                  }

                  target.setProperty(prefix + "saved", "true");
                  String importedName = imported.getProperty("name", "").trim();
                  if(importedName.length() > 24) {
                     importedName = importedName.substring(0, 24);
                  }

                  if(importedName.isEmpty()) {
                     target.remove(prefix + "name");
                  } else {
                     target.setProperty(prefix + "name", importedName);
                  }

                  this.write(target);
                  if(!this.isSaved(targetIndex)) {
                     return false;
                  } else {
                     this.apply(targetIndex);
                     return true;
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   private void applyDefault(int index) {
      LunarVisualSettings s = LunarVisualSettings.get();
      s.hudEnabled = true;
      s.showCoordinates = true;
      s.showStatus = index != 2;
      s.showArmor = index != 2;
      s.showCps = index == 0 || index == 1;
      s.showKeystrokes = index == 0 || index == 1;
      s.animationsEnabled = index == 1;
      s.particlesEnabled = index != 2;
      s.showClock = false;
      s.showBiome = false;
      s.showDirection = index == 0;
      s.hudOpacity = index == 2?60:72;
      s.uiScale = 100;
      s.resetHudPositions();
      s.save();
      LunarPerformanceManager.Profile performance = index == 0?LunarPerformanceManager.Profile.ALTO_FPS:(index == 2?LunarPerformanceManager.Profile.CELULAR_FRACO:LunarPerformanceManager.Profile.BALANCEADO);
      LunarPerformanceManager.get().setProfile(performance);
   }

   private Properties read() {
      Properties p = new Properties();
      if(!this.file.isFile()) {
         return p;
      } else {
         try {
            FileInputStream in = new FileInputStream(this.file);

            try {
               p.load(in);
            } finally {
               in.close();
            }
         } catch (IOException var7) {
            ;
         }

         return p;
      }
   }

   private void write(Properties p) {
      File parent = this.file.getParentFile();
      if(parent.exists() || parent.mkdirs()) {
         try {
            FileOutputStream out = new FileOutputStream(this.file);

            try {
               p.store(out, "LunarHavenX local play profiles");
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

   private LunarPerformanceManager.Profile performance(String saved) {
      try {
         return LunarPerformanceManager.Profile.valueOf(saved);
      } catch (Exception var3) {
         return LunarPerformanceManager.Profile.BALANCEADO;
      }
   }

   private String safeFileName(String fileName) {
      if(fileName == null) {
         return null;
      } else {
         String clean = fileName.trim();
         if(clean.endsWith(".lhxprofile")) {
            clean = clean.substring(0, clean.length() - 11);
         }

         return !clean.isEmpty() && !clean.equals(".") && !clean.equals("..") && clean.matches("[A-Za-z0-9_-][A-Za-z0-9._-]{0,39}")?clean:null;
      }
   }
}
