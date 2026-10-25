package com.lunarhavenx.client.mobile;

/**
 * Small, deterministic performance recommendations for Android runtimes.
 * The values describe the JVM heap limit and reported logical CPU count;
 * they are not a hardware benchmark.
 */
public final class LunarMobileTuner {
   private static final long MEGABYTE = 1024L * 1024L;

   private LunarMobileTuner() {
   }

   public static String recommendProfile(long maxHeapBytes, int logicalProcessors) {
      long maxHeapMegabytes = Math.max(0L, maxHeapBytes) / MEGABYTE;
      int processors = Math.max(0, logicalProcessors);

      if(maxHeapMegabytes <= 1536L || processors <= 2) {
         return "CELULAR_FRACO";
      }
      if(maxHeapMegabytes <= 2048L || processors <= 4) {
         return "BAIXO";
      }
      if(maxHeapMegabytes >= 4096L && processors >= 8) {
         return "ALTO_FPS";
      }
      return "BALANCEADO";
   }

   public static int frameLimit(String profileName) {
      if("BATATA".equals(profileName)) {
         return 30;
      }
      if("ALTO_FPS".equals(profileName)) {
         return 90;
      }
      if("FPS_EXTREMO".equals(profileName)) {
         return 120;
      }
      return 60;
   }
}
