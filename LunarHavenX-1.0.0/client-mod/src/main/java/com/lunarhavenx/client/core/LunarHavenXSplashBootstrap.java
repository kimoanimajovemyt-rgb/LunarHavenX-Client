package com.lunarhavenx.client.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name("LunarHavenX Custom Splash")
@MCVersion("1.8.9")
@SortingIndex(1001)
@TransformerExclusions({"com.lunarhavenx.client.core"})
public final class LunarHavenXSplashBootstrap implements IFMLLoadingPlugin {
   private static final String SPLASH_LOGO_LOCATION = "lunarhavenx:textures/gui/lunarhavenx_loading_screen.png";
   private static final String SPLASH_READY_PROPERTY = "lunarhavenx.splash.ready";

   public String[] getASMTransformerClass() {
      return isAndroidRuntime()
              ? new String[0]
              : new String[]{"com.lunarhavenx.client.core.LunarHavenXSplashTransformer"};
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map data) {
      System.clearProperty(SPLASH_READY_PROPERTY);
      try {
         Object value = data == null ? null : data.get("mcLocation");
         File gameDirectory = value instanceof File
                 ? (File)value : new File(System.getProperty("user.dir", "."));
         File configDirectory = new File(gameDirectory, "config");
         if(!configDirectory.isDirectory() && !configDirectory.mkdirs()) {
            return;
         }

         File configFile = new File(configDirectory, "splash.properties");
         Properties properties = new Properties();
         if(configFile.isFile()) {
            try (InputStream in = new java.io.FileInputStream(configFile)) {
               properties.load(in);
            }
         }

         if(isAndroidRuntime()) {
            this.restoreAndroidSplash(properties, configFile);
            return;
         }

         String resourcePackPath = properties.getProperty("resourcePackPath", "resources").trim();
         if(resourcePackPath.length() == 0) {
            resourcePackPath = "resources";
         }
         File configuredPath = new File(resourcePackPath);
         File resourcePackDirectory = (configuredPath.isAbsolute()
                 ? configuredPath : new File(gameDirectory, resourcePackPath)).getCanonicalFile();
         String gamePath = gameDirectory.getCanonicalPath();
         String resourcePath = resourcePackDirectory.getCanonicalPath();
         if(!resourcePath.equals(gamePath) && !resourcePath.startsWith(gamePath + File.separator)) {
            return;
         }

         File splashImage = new File(resourcePackDirectory,
                 "assets/lunarhavenx/textures/gui/lunarhavenx_loading_screen.png");
         if(!this.copySplashImage(splashImage)) {
            return;
         }

         boolean changed = !configFile.isFile();
         changed |= this.setIfDifferent(properties, "enabled", "true");
         changed |= this.setIfDifferent(properties, "rotate", "false");
         changed |= this.setIfDifferent(properties, "logoTexture", SPLASH_LOGO_LOCATION);
         changed |= this.setIfDifferent(properties, "background", "0x050B24");
         changed |= this.setIfDifferent(properties, "font", "0xD7F4FF");
         changed |= this.setIfDifferent(properties, "bar", "0x55D9FF");
         changed |= this.setIfDifferent(properties, "barBorder", "0x24435E");
         changed |= this.setIfDifferent(properties, "barBackground", "0x0B2038");
         if(changed) {
            try (FileOutputStream out = new FileOutputStream(configFile)) {
               properties.store(out, "LunarHavenX startup splash");
            }
         }

         System.setProperty(SPLASH_READY_PROPERTY, "true");
      } catch (Throwable ignored) {
         System.clearProperty(SPLASH_READY_PROPERTY);
      }
   }

   private boolean setIfDifferent(Properties properties, String key, String value) {
      if(value.equals(properties.getProperty(key))) {
         return false;
      }
      properties.setProperty(key, value);
      return true;
   }

   private void restoreAndroidSplash(Properties properties, File configFile) throws IOException {
      boolean changed = false;
      String logo = properties.getProperty("logoTexture", "").trim();
      if(SPLASH_LOGO_LOCATION.equals(logo)
              || "lunarhavenx:textures/gui/splash_emblem.png".equals(logo)) {
         changed |= this.setIfDifferent(properties, "logoTexture", "textures/gui/title/mojang.png");
      }

      changed |= this.removeIfEquals(properties, "background", "0x050B24");
      changed |= this.removeIfEquals(properties, "font", "0xD7F4FF");
      changed |= this.removeIfEquals(properties, "bar", "0x55D9FF");
      changed |= this.removeIfEquals(properties, "barBorder", "0x24435E");
      changed |= this.removeIfEquals(properties, "barBackground", "0x0B2038");
      if(changed) {
         try (FileOutputStream out = new FileOutputStream(configFile)) {
            properties.store(out, "LunarHavenX Android-safe Forge splash");
         }
      }
   }

   private boolean removeIfEquals(Properties properties, String key, String value) {
      if(!value.equalsIgnoreCase(properties.getProperty(key, "").trim())) {
         return false;
      }
      properties.remove(key);
      return true;
   }

   private static boolean isAndroidRuntime() {
      String runtime = (System.getProperty("os.name", "") + " "
              + System.getProperty("os.version", "") + " "
              + System.getProperty("java.vm.name", "") + " "
              + System.getProperty("java.runtime.name", "") + " "
              + System.getProperty("java.vendor", "")).toLowerCase(Locale.ENGLISH);
      if(runtime.contains("android") || runtime.contains("dalvik") || runtime.contains("art runtime")) {
         return true;
      }

      try {
         if(System.getenv("ANDROID_ROOT") != null || System.getenv("ANDROID_DATA") != null) {
            return true;
         }
      } catch (SecurityException ignored) {
      }

      try {
         Class.forName("android.os.Build", false, LunarHavenXSplashBootstrap.class.getClassLoader());
         return true;
      } catch (Throwable ignored) {
         return false;
      }
   }

   private boolean copySplashImage(File destination) {
      InputStream image = LunarHavenXSplashBootstrap.class.getResourceAsStream(
              "/assets/lunarhavenx/textures/gui/lunarhavenx_loading_screen.png");
      if(image == null) {
         return false;
      }

      File parent = destination.getParentFile();
      if(!parent.isDirectory() && !parent.mkdirs()) {
         try {
            image.close();
         } catch (IOException ignored) {
         }
         return false;
      }

      try (InputStream in = image; FileOutputStream out = new FileOutputStream(destination)) {
         byte[] buffer = new byte[8192];
         int read;
         while((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
         }
         out.flush();
         return destination.isFile() && destination.length() > 0L;
      } catch (IOException ignored) {
         return false;
      }
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
