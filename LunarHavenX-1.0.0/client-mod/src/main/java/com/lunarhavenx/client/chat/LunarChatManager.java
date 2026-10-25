package com.lunarhavenx.client.chat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class LunarChatManager {
   private static final Map<String, String> EMOJIS = new LinkedHashMap<String, String>();

   private LunarChatManager() {
   }

   public static String replaceEmojiCodes(String message) {
      if(message != null && message.length() != 0) {
         String result = message;

         for(Entry<String, String> entry : EMOJIS.entrySet()) {
            result = result.replace((CharSequence)entry.getKey(), (CharSequence)entry.getValue());
         }

         return result;
      } else {
         return message;
      }
   }

   public static String getHelpText() {
      return ":lunar:  :heart:  :star:  :gg:  :fire:  :wave:  :check:";
   }

   static {
      EMOJIS.put(":lunar:", "✦");
      EMOJIS.put(":heart:", "♥");
      EMOJIS.put(":star:", "★");
      EMOJIS.put(":gg:", "[GG]");
      EMOJIS.put(":fire:", "[FIRE]");
      EMOJIS.put(":wave:", "[HI]");
      EMOJIS.put(":check:", "[OK]");
   }
}
