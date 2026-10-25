package com.lunarhavenx.client.cosmetics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.client.Minecraft;

public final class LunarCosmeticManager {
   private static final LunarCosmeticManager INSTANCE = new LunarCosmeticManager();
   private final File file;
   private LunarCosmeticManager.Cape cape = LunarCosmeticManager.Cape.NONE;

   private LunarCosmeticManager() {
      this.file = new File(Minecraft.getMinecraft().mcDataDir, "config/lunarhavenx/cosmetics.properties");
      this.load();
   }

   public static LunarCosmeticManager get() {
      return INSTANCE;
   }

   public LunarCosmeticManager.Cape getCape() {
      return this.cape;
   }

   public void setCape(LunarCosmeticManager.Cape cape) {
      this.cape = cape == null?LunarCosmeticManager.Cape.NONE:cape;
      this.save();
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

            try {
               this.cape = LunarCosmeticManager.Cape.valueOf(p.getProperty("cape", this.cape.name()));
            } catch (IllegalArgumentException var7) {
               this.cape = LunarCosmeticManager.Cape.NONE;
            }
         } catch (IOException var9) {
            ;
         }

      }
   }

   public void save() {
      File parent = this.file.getParentFile();
      if(parent.exists() || parent.mkdirs()) {
         Properties p = new Properties();
         p.setProperty("cape", this.cape.name());

         try {
            FileOutputStream out = new FileOutputStream(this.file);

            try {
               p.store(out, "LunarHavenX local cosmetics");
            } finally {
               out.close();
            }
         } catch (IOException var8) {
            ;
         }

      }
   }

   public static enum Cape {
      NONE("SEM CAPA", ""),
      LUNAR("LUNAR", "textures/cosmetics/cape_lunar.png"),
      HAVEN("HAVEN", "textures/cosmetics/cape_haven.png"),
      AURORA("AURORA", "textures/cosmetics/cape_aurora.png"),
      NOVA("NOVA", "textures/cosmetics/cape_nova.png");

      public final String label;
      public final String texture;

      private Cape(String label, String texture) {
         this.label = label;
         this.texture = texture;
      }

      public LunarCosmeticManager.Cape next() {
         return values()[(this.ordinal() + 1) % values().length];
      }
   }
}
