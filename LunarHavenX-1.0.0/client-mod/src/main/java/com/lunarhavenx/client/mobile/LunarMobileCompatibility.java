package com.lunarhavenx.client.mobile;

import com.lunarhavenx.client.performance.LunarPerformanceManager;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Locale;

public final class LunarMobileCompatibility {
   private static final int ANDROID_KEYBOARD_BUFFER_CAPACITY = 65536;
   private static LunarMobileCompatibility.Platform platform;
   private static boolean keyboardBufferExpanded;
   private static boolean keyboardBufferWarningLogged;
   private static Field keyboardReadBufferField;

   public static LunarMobileCompatibility.Platform getPlatform() {
      if(platform == null) {
         platform = detectPlatform();
      }

      return platform;
   }

   public static boolean isAndroid() {
      return getPlatform() == LunarMobileCompatibility.Platform.ANDROID;
   }

   public static void applyRecommendedSettings() {
      LunarPerformanceManager manager = LunarPerformanceManager.get();
      if(isAndroid()) {
         manager.applyMobileDefaultIfUnset();
      } else {
         manager.apply();
      }

   }

   public static void prepareClientTick() {
      // Some Android launchers report Linux/unknown properties; apply this
      // optional LWJGL safeguard unless the runtime is positively desktop.
       if(getPlatform() != LunarMobileCompatibility.Platform.DESKTOP) {
         ensureKeyboardBufferCapacity();
      }
   }

   public static String getPlatformLabel() {
      switch(getPlatform()) {
      case ANDROID:
         return "ANDROID / JAVA LAUNCHER";
      case DESKTOP:
         return "DESKTOP";
      default:
         return "RUNTIME DESCONHECIDO";
      }
   }

   private static LunarMobileCompatibility.Platform detectPlatform() {
      String forced = lower(System.getProperty("lunarhavenx.platform", ""));
      if("android".equals(forced)) {
         return LunarMobileCompatibility.Platform.ANDROID;
      } else if("desktop".equals(forced)) {
         return LunarMobileCompatibility.Platform.DESKTOP;
      } else {
         String values = lower(System.getProperty("java.runtime.name", "") + " "
                 + System.getProperty("java.vm.name", "") + " "
                  + System.getProperty("java.vendor", "") + " "
                  + System.getProperty("java.vm.vendor", "") + " "
                  + System.getProperty("java.specification.vendor", "") + " "
                 + System.getProperty("os.name", "") + " "
                 + System.getProperty("os.version", ""));
         if(values.contains("android") || values.contains("dalvik") || values.contains("art runtime")
                  || hasAndroidEnvironment() || hasAndroidRuntimeClass()) {
            return LunarMobileCompatibility.Platform.ANDROID;
         }
         return values.contains("windows") || values.contains("mac") || values.contains("linux")
                 || values.contains("unix") ? LunarMobileCompatibility.Platform.DESKTOP
                 : LunarMobileCompatibility.Platform.UNKNOWN;
      }
   }

   private static boolean hasAndroidEnvironment() {
      try {
         return System.getenv("ANDROID_ROOT") != null || System.getenv("ANDROID_DATA") != null;
      } catch (SecurityException ignored) {
         return false;
      }
   }

   private static boolean hasAndroidRuntimeClass() {
      try {
         Class.forName("android.os.Build", false, LunarMobileCompatibility.class.getClassLoader());
         return true;
      } catch (Exception ignored) {
         return false;
      } catch (LinkageError ignored) {
         return false;
      }
   }

   private static void ensureKeyboardBufferCapacity() {
      if(keyboardBufferExpanded) {
         return;
      }

      try {
         if(keyboardReadBufferField == null) {
            Class<?> keyboardClass = Class.forName(
                    "org.lwjgl.input.Keyboard", false, LunarMobileCompatibility.class.getClassLoader());
            Field readBufferField = keyboardClass.getDeclaredField("readBuffer");
            readBufferField.setAccessible(true);
            keyboardReadBufferField = readBufferField;
         }

         Field readBufferField = keyboardReadBufferField;
         Object value = readBufferField.get(null);

         // Keyboard.create() assigns this field after the game display starts.
         // Retry on a later client tick if it has not happened yet.
         if(!(value instanceof ByteBuffer)) {
            return;
         }

         ByteBuffer current = (ByteBuffer)value;
         if(current.capacity() >= ANDROID_KEYBOARD_BUFFER_CAPACITY) {
            keyboardBufferExpanded = true;
            return;
         }

         int capacity = Math.max(current.capacity(), ANDROID_KEYBOARD_BUFFER_CAPACITY);
         ByteBuffer expanded = current.isDirect()
                 ? ByteBuffer.allocateDirect(capacity).order(current.order())
                 : ByteBuffer.allocate(capacity).order(current.order());
         expanded.put(current.duplicate());
         expanded.flip();
         readBufferField.set(null, expanded);
         keyboardBufferExpanded = true;
         keyboardBufferWarningLogged = false;
      } catch (Exception error) {
         logKeyboardBufferWarning(error);
      } catch (LinkageError error) {
         logKeyboardBufferWarning(error);
      }
   }

   private static void logKeyboardBufferWarning(Throwable error) {
      if(!keyboardBufferWarningLogged) {
         keyboardBufferWarningLogged = true;
         System.err.println("[LunarHavenX] A proteção do buffer de teclado ainda não pôde ser aplicada; nova tentativa no próximo tick ("
                 + error.getClass().getSimpleName() + ").");
      }
   }

   private static String lower(String value) {
      return value == null?"":value.toLowerCase(Locale.ENGLISH);
   }

   public static enum Platform {
      DESKTOP,
      ANDROID,
      UNKNOWN;
   }
}
