package com.lunarhavenx.client.performance;

import com.lunarhavenx.client.mobile.LunarMobileCompatibility;
import com.lunarhavenx.client.mobile.LunarMobileTuner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public final class LunarPerformanceManager {
   private static final LunarPerformanceManager INSTANCE = new LunarPerformanceManager();
   private final File file;
   private LunarPerformanceManager.Profile profile = LunarPerformanceManager.Profile.BALANCEADO;
   private boolean hasSavedProfile;

   private LunarPerformanceManager() {
      this.file = new File(Minecraft.getMinecraft().mcDataDir, "config/lunarhavenx/performance.properties");
      this.load();
   }

   public static LunarPerformanceManager get() {
      return INSTANCE;
   }

   public LunarPerformanceManager.Profile getProfile() {
      return this.profile;
   }

   public boolean hasSavedProfile() {
      return this.hasSavedProfile;
   }

   public void setProfile(LunarPerformanceManager.Profile profile) {
      this.profile = profile == null?LunarPerformanceManager.Profile.BALANCEADO:profile;
      this.hasSavedProfile = true;
      this.apply();
      this.save();
   }

   public void applyMobileDefaultIfUnset() {
      if(!this.hasSavedProfile) {
         this.setProfile(this.recommendMobileProfile());
      } else {
         this.apply();
      }

   }

   private LunarPerformanceManager.Profile recommendMobileProfile() {
      String recommended = LunarMobileTuner.recommendProfile(
              Runtime.getRuntime().maxMemory(), Runtime.getRuntime().availableProcessors());
      try {
         return LunarPerformanceManager.Profile.valueOf(recommended);
      } catch (IllegalArgumentException ignored) {
         return LunarPerformanceManager.Profile.CELULAR_FRACO;
      }
   }

   public int getMobileFrameLimit() {
      return LunarMobileTuner.frameLimit(this.profile.name());
   }

   public void apply() {
      GameSettings settings = Minecraft.getMinecraft().gameSettings;
      switch(this.profile) {
      case BATATA:
         settings.renderDistanceChunks = 2;
         settings.particleSetting = 2;
         settings.fancyGraphics = false;
         settings.ambientOcclusion = 0;
         settings.entityShadows = false;
         settings.clouds = 0;
         settings.mipmapLevels = 0;
         settings.enableVsync = false;
         settings.limitFramerate = 260;
         break;
      case BAIXO:
         settings.renderDistanceChunks = 4;
         settings.particleSetting = 2;
         settings.fancyGraphics = false;
         settings.ambientOcclusion = 0;
         settings.entityShadows = false;
         settings.clouds = 0;
         settings.mipmapLevels = 1;
         settings.enableVsync = false;
         settings.limitFramerate = 260;
         break;
      case BALANCEADO:
         settings.renderDistanceChunks = 8;
         settings.particleSetting = 1;
         settings.fancyGraphics = false;
         settings.ambientOcclusion = 1;
         settings.entityShadows = false;
         settings.clouds = 1;
         settings.mipmapLevels = 2;
         settings.enableVsync = false;
         settings.limitFramerate = 260;
         break;
      case ALTO_FPS:
         settings.renderDistanceChunks = 6;
         settings.particleSetting = 1;
         settings.fancyGraphics = false;
         settings.ambientOcclusion = 0;
         settings.entityShadows = false;
         settings.clouds = 0;
         settings.mipmapLevels = 1;
         settings.enableVsync = false;
         settings.limitFramerate = 260;
         break;
      case FPS_EXTREMO:
         settings.renderDistanceChunks = 4;
         settings.particleSetting = 2;
         settings.fancyGraphics = false;
         settings.ambientOcclusion = 0;
         settings.entityShadows = false;
         settings.clouds = 0;
         settings.mipmapLevels = 0;
         settings.enableVsync = false;
         settings.limitFramerate = 260;
         break;
      case CELULAR_FRACO:
         settings.renderDistanceChunks = 3;
         settings.particleSetting = 2;
         settings.fancyGraphics = false;
         settings.ambientOcclusion = 0;
         settings.entityShadows = false;
         settings.clouds = 0;
         settings.mipmapLevels = 0;
         settings.enableVsync = false;
         settings.limitFramerate = 60;
      }

      if(LunarMobileCompatibility.isAndroid()) {
         settings.limitFramerate = this.getMobileFrameLimit();
      }

      settings.saveOptions();
   }

   public void load() {
      if(this.file.isFile()) {
         Properties properties = new Properties();

         try {
            FileInputStream in = new FileInputStream(this.file);

            try {
               properties.load(in);
            } finally {
               in.close();
            }

            String saved = properties.getProperty("profile");
            if(saved != null) {
               try {
                  this.profile = LunarPerformanceManager.Profile.valueOf(saved);
                  this.hasSavedProfile = true;
               } catch (IllegalArgumentException var8) {
                  this.profile = LunarPerformanceManager.Profile.BALANCEADO;
                  this.hasSavedProfile = false;
               }
            }
         } catch (IOException var10) {
            ;
         }

      }
   }

   public void save() {
      File parent = this.file.getParentFile();
      if(parent.exists() || parent.mkdirs()) {
         Properties properties = new Properties();
         properties.setProperty("profile", this.profile.name());

         try {
            FileOutputStream out = new FileOutputStream(this.file);

            try {
               properties.store(out, "LunarHavenX performance profile");
            } finally {
               out.close();
            }
         } catch (IOException var8) {
            ;
         }

      }
   }

   public static enum Profile {
      BATATA("BATATA"),
      BAIXO("BAIXO"),
      BALANCEADO("BALANCEADO"),
      ALTO_FPS("ALTO FPS"),
      FPS_EXTREMO("FPS EXTREMO"),
      CELULAR_FRACO("CELULAR FRACO");

      public final String label;

      private Profile(String label) {
         this.label = label;
      }

      public LunarPerformanceManager.Profile next() {
         LunarPerformanceManager.Profile[] values = values();
         return values[(this.ordinal() + 1) % values.length];
      }
   }
}
