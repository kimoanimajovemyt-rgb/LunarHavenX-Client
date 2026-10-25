package com.lunarhavenx.client.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.client.Minecraft;

public final class LunarLanguageManager {
   private static final LunarLanguageManager INSTANCE = new LunarLanguageManager();
   private static final String[] NAMES = new String[]{"Português (Brasil)", "English", "Español", "Русский", "日本語"};
   private final File file;
   private int language;

   private LunarLanguageManager() {
      this.file = new File(Minecraft.getMinecraft().mcDataDir, "config/lunarhavenx/language.properties");
      this.load();
   }

   public static LunarLanguageManager get() {
      return INSTANCE;
   }

   public int getIndex() {
      return this.language;
   }

   public String getName() {
      return NAMES[this.language];
   }

   public void next() {
      this.language = (this.language + 1) % NAMES.length;
      this.save();
   }

   public String state(boolean enabled) {
      return this.language == 1?(enabled?"ON":"OFF"):(this.language == 2?(enabled?"ACTIVADO":"DESACTIVADO"):(this.language == 3?(enabled?"ВКЛ":"ВЫКЛ"):(this.language == 4?(enabled?"オン":"オフ"):(enabled?"ATIVADO":"DESATIVADO"))));
   }

   public String text(String key) {
      short var4 = -1;
      switch(key.hashCode()) {
      case -2027574035:
         if(key.equals("shortcuts")) {
            var4 = 12;
         }
         break;
      case -1995748591:
         if(key.equals("perfBalanced")) {
            var4 = 126;
         }
         break;
      case -1965593298:
         if(key.equals("featureCosmetics")) {
            var4 = 138;
         }
         break;
      case -1953696364:
         if(key.equals("loadingCompatibility")) {
            var4 = 150;
         }
         break;
      case -1947219940:
         if(key.equals("loadingPreferences")) {
            var4 = 146;
         }
         break;
      case -1924829944:
         if(key.equals("balanced")) {
            var4 = 31;
         }
         break;
      case -1897169419:
         if(key.equals("statFps")) {
            var4 = 152;
         }
         break;
      case -1890194309:
         if(key.equals("serverSubtitle")) {
            var4 = 68;
         }
         break;
      case -1887995251:
         if(key.equals("editHud")) {
            var4 = 108;
         }
         break;
      case -1859110884:
         if(key.equals("profileExported")) {
            var4 = 106;
         }
         break;
      case -1849459809:
         if(key.equals("chatMacros")) {
            var4 = 83;
         }
         break;
      case -1826281366:
         if(key.equals("serverHint")) {
            var4 = 70;
         }
         break;
      case -1800314195:
         if(key.equals("particles")) {
            var4 = 8;
         }
         break;
      case -1785238953:
         if(key.equals("favorites")) {
            var4 = 67;
         }
         break;
      case -1776575311:
         if(key.equals("biomeValue")) {
            var4 = 34;
         }
         break;
      case -1775250732:
         if(key.equals("clearMacros")) {
            var4 = 87;
         }
         break;
      case -1754981362:
         if(key.equals("selectedCape")) {
            var4 = 57;
         }
         break;
      case -1689762057:
         if(key.equals("resourceSection")) {
            var4 = 131;
         }
         break;
      case -1676556340:
         if(key.equals("equippedLocal")) {
            var4 = 62;
         }
         break;
      case -1622360428:
         if(key.equals("profileApplied")) {
            var4 = 102;
         }
         break;
      case -1613589672:
         if(key.equals("language")) {
            var4 = 14;
         }
         break;
      case -1548283250:
         if(key.equals("tagline")) {
            var4 = 38;
         }
         break;
      case -1502782804:
         if(key.equals("localCapeInfo")) {
            var4 = 60;
         }
         break;
      case -1446938196:
         if(key.equals("moduleTitleVisual")) {
            var4 = 37;
         }
         break;
      case -1413741364:
         if(key.equals("rightClick")) {
            var4 = 123;
         }
         break;
      case -1396126043:
         if(key.equals("hudEditorHelp")) {
            var4 = 110;
         }
         break;
      case -1354750946:
         if(key.equals("coords")) {
            var4 = 4;
         }
         break;
      case -1306084975:
         if(key.equals("effect")) {
            var4 = 117;
         }
         break;
      case -1299347219:
         if(key.equals("emojis")) {
            var4 = 91;
         }
         break;
      case -1289153612:
         if(key.equals("export")) {
            var4 = 101;
         }
         break;
      case -1276865791:
         if(key.equals("tipDesktop")) {
            var4 = 158;
         }
         break;
      case -1276283108:
         if(key.equals("pressKey")) {
            var4 = 88;
         }
         break;
      case -1273775369:
         if(key.equals("previous")) {
            var4 = 50;
         }
         break;
      case -1267206133:
         if(key.equals("opacity")) {
            var4 = 17;
         }
         break;
      case -1242351615:
         if(key.equals("noEffects")) {
            var4 = 116;
         }
         break;
      case -1221262756:
         if(key.equals("health")) {
            var4 = 114;
         }
         break;
      case -1206104397:
         if(key.equals("hunger")) {
            var4 = 115;
         }
         break;
      case -1205293236:
         if(key.equals("moduleTitle")) {
            var4 = 21;
         }
         break;
      case -1184795739:
         if(key.equals("import")) {
            var4 = 100;
         }
         break;
      case -1147692044:
         if(key.equals("address")) {
            var4 = 72;
         }
         break;
      case -1066618006:
         if(key.equals("cyclePerformance")) {
            var4 = 94;
         }
         break;
      case -1041157068:
         if(key.equals("noCape")) {
            var4 = 56;
         }
         break;
      case -1038130864:
         if(key.equals("undefined")) {
            var4 = 89;
         }
         break;
      case -1002263574:
         if(key.equals("profiles")) {
            var4 = 26;
         }
         break;
      case -964126197:
         if(key.equals("moduleTitleHud")) {
            var4 = 36;
         }
         break;
      case -962590849:
         if(key.equals("direction")) {
            var4 = 25;
         }
         break;
      case -934610812:
         if(key.equals("remove")) {
            var4 = 65;
         }
         break;
      case -934381561:
         if(key.equals("cosmeticSubtitle")) {
            var4 = 54;
         }
         break;
      case -902040694:
         if(key.equals("elyByHelp")) {
            var4 = 59;
         }
         break;
      case -902002441:
         if(key.equals("elyByInfo")) {
            var4 = 58;
         }
         break;
      case -895679974:
         if(key.equals("sprint")) {
            var4 = 120;
         }
         break;
      case -892481550:
         if(key.equals("status")) {
            var4 = 5;
         }
         break;
      case -882563029:
         if(key.equals("loadingPrepare")) {
            var4 = 143;
         }
         break;
      case -870526003:
         if(key.equals("moduleHelp")) {
            var4 = 22;
         }
         break;
      case -861391249:
         if(key.equals("android")) {
            var4 = 155;
         }
         break;
      case -806703022:
         if(key.equals("featureHudDesc")) {
            var4 = 133;
         }
         break;
      case -806395957:
         if(key.equals("textureMissing")) {
            var4 = 55;
         }
         break;
      case -804350520:
         if(key.equals("featureShortcutsDesc")) {
            var4 = 141;
         }
         break;
      case -786771075:
         if(key.equals("serverAdded")) {
            var4 = 79;
         }
         break;
      case -782796854:
         if(key.equals("serverEmpty")) {
            var4 = 69;
         }
         break;
      case -678861909:
         if(key.equals("perfLow")) {
            var4 = 125;
         }
         break;
      case -668971098:
         if(key.equals("profileFileHelp")) {
            var4 = 99;
         }
         break;
      case -640751782:
         if(key.equals("featurePerformance")) {
            var4 = 134;
         }
         break;
      case -636534694:
         if(key.equals("openCosmetics")) {
            var4 = 95;
         }
         break;
      case -566933834:
         if(key.equals("controls")) {
            var4 = 82;
         }
         break;
      case -464792700:
         if(key.equals("cosmetics")) {
            var4 = 43;
         }
         break;
      case -372024179:
         if(key.equals("openSettings")) {
            var4 = 92;
         }
         break;
      case -355442739:
         if(key.equals("profileImported")) {
            var4 = 104;
         }
         break;
      case -319025292:
         if(key.equals("hudEditorTitle")) {
            var4 = 109;
         }
         break;
      case -309425751:
         if(key.equals("profile")) {
            var4 = 11;
         }
         break;
      case -252712397:
         if(key.equals("playerStatus")) {
            var4 = 113;
         }
         break;
      case -232004976:
         if(key.equals("loadingPlay")) {
            var4 = 148;
         }
         break;
      case -196915620:
         if(key.equals("clientForge")) {
            var4 = 47;
         }
         break;
      case -150722084:
         if(key.equals("featureEly")) {
            var4 = 136;
         }
         break;
      case -150718943:
         if(key.equals("featureHud")) {
            var4 = 132;
         }
         break;
      case -15196917:
         if(key.equals("featurePerformanceDesc")) {
            var4 = 135;
         }
         break;
      case 96417:
         if(key.equals("add")) {
            var4 = 64;
         }
         break;
      case 98726:
         if(key.equals("cps")) {
            var4 = 9;
         }
         break;
      case 103671:
         if(key.equals("hud")) {
            var4 = 3;
         }
         break;
      case 111402:
         if(key.equals("pvp")) {
            var4 = 30;
         }
         break;
      case 3015911:
         if(key.equals("back")) {
            var4 = 53;
         }
         break;
      case 3089282:
         if(key.equals("done")) {
            var4 = 16;
         }
         break;
      case 3127582:
         if(key.equals("exit")) {
            var4 = 46;
         }
         break;
      case 3288564:
         if(key.equals("keys")) {
            var4 = 10;
         }
         break;
      case 3357105:
         if(key.equals("mods")) {
            var4 = 42;
         }
         break;
      case 3373707:
         if(key.equals("name")) {
            var4 = 71;
         }
         break;
      case 3377907:
         if(key.equals("next")) {
            var4 = 52;
         }
         break;
      case 3443508:
         if(key.equals("play")) {
            var4 = 39;
         }
         break;
      case 3522941:
         if(key.equals("save")) {
            var4 = 29;
         }
         break;
      case 77900163:
         if(key.equals("perfExtreme")) {
            var4 = 128;
         }
         break;
      case 93029230:
         if(key.equals("apply")) {
            var4 = 28;
         }
         break;
      case 93086015:
         if(key.equals("armor")) {
            var4 = 6;
         }
         break;
      case 93743264:
         if(key.equals("biome")) {
            var4 = 24;
         }
         break;
      case 94755854:
         if(key.equals("clock")) {
            var4 = 23;
         }
         break;
      case 96757808:
         if(key.equals("equip")) {
            var4 = 51;
         }
         break;
      case 102970646:
         if(key.equals("light")) {
            var4 = 32;
         }
         break;
      case 109250890:
         if(key.equals("scale")) {
            var4 = 18;
         }
         break;
      case 109582100:
         if(key.equals("sneak")) {
            var4 = 118;
         }
         break;
      case 112386354:
         if(key.equals("voice")) {
            var4 = 45;
         }
         break;
      case 134311157:
         if(key.equals("serverAddressMissing")) {
            var4 = 78;
         }
         break;
      case 177328266:
         if(key.equals("profileHelp")) {
            var4 = 27;
         }
         break;
      case 177366519:
         if(key.equals("profileInfo")) {
            var4 = 19;
         }
         break;
      case 177662791:
         if(key.equals("profileSlot")) {
            var4 = 97;
         }
         break;
      case 269163160:
         if(key.equals("hudReset")) {
            var4 = 111;
         }
         break;
      case 314070383:
         if(key.equals("animations")) {
            var4 = 7;
         }
         break;
      case 329678242:
         if(key.equals("restoreKeys")) {
            var4 = 86;
         }
         break;
      case 334662795:
         if(key.equals("profileImportFailed")) {
            var4 = 105;
         }
         break;
      case 349719486:
         if(key.equals("voiceUnavailable")) {
            var4 = 49;
         }
         break;
      case 385300558:
         if(key.equals("sneaking")) {
            var4 = 119;
         }
         break;
      case 429991979:
         if(key.equals("perfHigh")) {
            var4 = 127;
         }
         break;
      case 451145790:
         if(key.equals("loadingFallback")) {
            var4 = 145;
         }
         break;
      case 490649401:
         if(key.equals("keyTitle")) {
            var4 = 80;
         }
         break;
      case 517750503:
         if(key.equals("statPlatform")) {
            var4 = 154;
         }
         break;
      case 562356570:
         if(key.equals("multiplayer")) {
            var4 = 41;
         }
         break;
      case 587484813:
         if(key.equals("featureElyDesc")) {
            var4 = 137;
         }
         break;
      case 588913375:
         if(key.equals("equipped")) {
            var4 = 61;
         }
         break;
      case 599603540:
         if(key.equals("tipAndroid")) {
            var4 = 157;
         }
         break;
      case 616633525:
         if(key.equals("statProfile")) {
            var4 = 153;
         }
         break;
      case 694984312:
         if(key.equals("pingUnknown")) {
            var4 = 73;
         }
         break;
      case 740297519:
         if(key.equals("profileFileLabel")) {
            var4 = 98;
         }
         break;
      case 740894712:
         if(key.equals("serverUpdated")) {
            var4 = 76;
         }
         break;
      case 753758514:
         if(key.equals("directionValue")) {
            var4 = 35;
         }
         break;
      case 766592663:
         if(key.equals("featureShortcuts")) {
            var4 = 140;
         }
         break;
      case 881946629:
         if(key.equals("loadingFallbackReady")) {
            var4 = 149;
         }
         break;
      case 942321923:
         if(key.equals("toggleHud")) {
            var4 = 93;
         }
         break;
      case 951351530:
         if(key.equals("connect")) {
            var4 = 63;
         }
         break;
      case 951403286:
         if(key.equals("localClient")) {
            var4 = 48;
         }
         break;
      case 960105153:
         if(key.equals("oneMessage")) {
            var4 = 84;
         }
         break;
      case 972193687:
         if(key.equals("keySubtitle")) {
            var4 = 81;
         }
         break;
      case 1053973739:
         if(key.equals("perfMobile")) {
            var4 = 129;
         }
         break;
      case 1081155075:
         if(key.equals("clockValue")) {
            var4 = 33;
         }
         break;
      case 1085444827:
         if(key.equals("refresh")) {
            var4 = 66;
         }
         break;
      case 1097519758:
         if(key.equals("restore")) {
            var4 = 15;
         }
         break;
      case 1098118800:
         if(key.equals("hudModules")) {
            var4 = 2;
         }
         break;
      case 1140390000:
         if(key.equals("perfPotato")) {
            var4 = 124;
         }
         break;
      case 1212257886:
         if(key.equals("profileSaved")) {
            var4 = 103;
         }
         break;
      case 1227433863:
         if(key.equals("modules")) {
            var4 = 13;
         }
         break;
      case 1241618655:
         if(key.equals("featureCosmeticsDesc")) {
            var4 = 139;
         }
         break;
      case 1277667300:
         if(key.equals("statVersion")) {
            var4 = 151;
         }
         break;
      case 1288806473:
         if(key.equals("loadingResources")) {
            var4 = 144;
         }
         break;
      case 1292077532:
         if(key.equals("localSaved")) {
            var4 = 1;
         }
         break;
      case 1300409038:
         if(key.equals("quickHelp")) {
            var4 = 20;
         }
         break;
      case 1357921363:
         if(key.equals("hudElementInfo")) {
            var4 = 112;
         }
         break;
      case 1384149608:
         if(key.equals("loadingInitial")) {
            var4 = 142;
         }
         break;
      case 1399418311:
         if(key.equals("loadingReady")) {
            var4 = 147;
         }
         break;
      case 1401403004:
         if(key.equals("loadingTitle")) {
            var4 = 130;
         }
         break;
      case 1429809032:
         if(key.equals("sprinting")) {
            var4 = 121;
         }
         break;
      case 1434631203:
         if(key.equals("settings")) {
            var4 = 0;
         }
         break;
      case 1441190714:
         if(key.equals("serverChoose")) {
            var4 = 77;
         }
         break;
      case 1526703450:
         if(key.equals("profileExportFailed")) {
            var4 = 107;
         }
         break;
      case 1543498876:
         if(key.equals("noAutoMessages")) {
            var4 = 85;
         }
         break;
      case 1557106716:
         if(key.equals("desktop")) {
            var4 = 156;
         }
         break;
      case 1609627104:
         if(key.equals("chatTitle")) {
            var4 = 90;
         }
         break;
      case 1714009417:
         if(key.equals("singlePlayer")) {
            var4 = 40;
         }
         break;
      case 1715872193:
         if(key.equals("leftClick")) {
            var4 = 122;
         }
         break;
      case 1787323366:
         if(key.equals("openServers")) {
            var4 = 96;
         }
         break;
      case 1896387103:
         if(key.equals("serverSelect")) {
            var4 = 74;
         }
         break;
      case 1984149904:
         if(key.equals("servers")) {
            var4 = 44;
         }
         break;
      case 2067160989:
         if(key.equals("serverRemoved")) {
            var4 = 75;
         }
      }

      String[] values;
      switch(var4) {
      case 0:
         values = new String[]{"CONFIGURAÇÕES VISUAIS", "VISUAL SETTINGS", "AJUSTES VISUALES", "ВИЗУАЛЬНЫЕ НАСТРОЙКИ", "ビジュアル設定"};
         break;
      case 1:
         values = new String[]{"Preferências locais • salvas automaticamente", "Local preferences • saved automatically", "Preferencias locales • guardado automático", "Локальные настройки • автосохранение", "ローカル設定 • 自動保存"};
         break;
      case 2:
         values = new String[]{"MÓDULOS DO HUD", "HUD MODULES", "MÓDULOS DEL HUD", "МОДУЛИ HUD", "HUD モジュール"};
         break;
      case 3:
         values = new String[]{"HUD", "HUD", "HUD", "HUD", "HUD"};
         break;
      case 4:
         values = new String[]{"COORDENADAS", "COORDINATES", "COORDENADAS", "КООРДИНАТЫ", "座標"};
         break;
      case 5:
         values = new String[]{"STATUS", "STATUS", "ESTADO", "СТАТУС", "ステータス"};
         break;
      case 6:
         values = new String[]{"ARMADURA", "ARMOR", "ARMADURA", "БРОНЯ", "防具"};
         break;
      case 7:
         values = new String[]{"ANIMAÇÕES", "ANIMATIONS", "ANIMACIONES", "АНИМАЦИИ", "アニメーション"};
         break;
      case 8:
         values = new String[]{"PARTÍCULAS", "PARTICLES", "PARTÍCULAS", "ЧАСТИЦЫ", "パーティクル"};
         break;
      case 9:
         values = new String[]{"CPS", "CPS", "CPS", "CPS", "CPS"};
         break;
      case 10:
         values = new String[]{"TECLAS", "KEYSTROKES", "TECLAS", "КЛАВИШИ", "キー表示"};
         break;
      case 11:
         values = new String[]{"PERFIL", "PROFILE", "PERFIL", "ПРОФИЛЬ", "プロファイル"};
         break;
      case 12:
         values = new String[]{"ATALHOS E MACROS", "KEYBINDS & MACROS", "ATAJOS Y MACROS", "КЛАВИШИ И МАКРОСЫ", "キー設定とマクロ"};
         break;
      case 13:
         values = new String[]{"MÓDULOS", "MODULES", "MÓDULOS", "МОДУЛИ", "モジュール"};
         break;
      case 14:
         values = new String[]{"IDIOMA", "LANGUAGE", "IDIOMA", "ЯЗЫК", "言語"};
         break;
      case 15:
         values = new String[]{"RESTAURAR PADRÕES", "RESTORE DEFAULTS", "RESTAURAR VALORES", "СБРОСИТЬ НАСТРОЙКИ", "初期設定に戻す"};
         break;
      case 16:
         values = new String[]{"CONCLUÍDO", "DONE", "LISTO", "ГОТОВО", "完了"};
         break;
      case 17:
         values = new String[]{"OPACIDADE DO HUD", "HUD OPACITY", "OPACIDAD DEL HUD", "ПРОЗРАЧНОСТЬ HUD", "HUD の不透明度"};
         break;
      case 18:
         values = new String[]{"ESCALA DA INTERFACE", "INTERFACE SCALE", "ESCALA DE INTERFAZ", "МАСШТАБ ИНТЕРФЕЙСА", "UI スケール"};
         break;
      case 19:
         values = new String[]{"Perfis ajustam gráficos; PvP não automatiza ações.", "Profiles tune graphics; PvP never automates actions.", "Los perfiles ajustan gráficos; PvP no automatiza acciones.", "Профили настраивают графику; PvP не автоматизирует действия.", "プロファイルは画質を調整し、PvP操作は自動化しません。"};
         break;
      case 20:
         values = new String[]{"RSHIFT abre configurações • apenas ajustes locais.", "RSHIFT opens settings • local changes only.", "RSHIFT abre ajustes • solo cambios locales.", "RSHIFT открывает настройки • только локальные параметры.", "RSHIFT で設定を開く • ローカル設定のみ。"};
         break;
      case 21:
         values = new String[]{"MÓDULOS DO CLIENTE", "CLIENT MODULES", "MÓDULOS DEL CLIENTE", "МОДУЛИ КЛИЕНТА", "クライアントモジュール"};
         break;
      case 22:
         values = new String[]{"Informações locais e visuais; sem vantagens ocultas.", "Local and visual information; no hidden advantages.", "Información local y visual; sin ventajas ocultas.", "Локальная и визуальная информация; без скрытых преимуществ.", "ローカル・視覚情報のみ。隠れた優位機能はありません。"};
         break;
      case 23:
         values = new String[]{"RELÓGIO", "CLOCK", "RELOJ", "ЧАСЫ", "時計"};
         break;
      case 24:
         values = new String[]{"BIOMA", "BIOME", "BIOMA", "БИОМ", "バイオーム"};
         break;
      case 25:
         values = new String[]{"DIREÇÃO", "DIRECTION", "DIRECCIÓN", "НАПРАВЛЕНИЕ", "方角"};
         break;
      case 26:
         values = new String[]{"PERFIS DE JOGO", "GAME PROFILES", "PERFILES DE JUEGO", "ИГРОВЫЕ ПРОФИЛИ", "ゲームプロファイル"};
         break;
      case 27:
         values = new String[]{"6 espaços com nomes próprios • importar e exportar arquivos locais", "6 named slots • import and export local profile files", "6 espacios con nombre • importar y exportar perfiles locales", "6 слотов с именами • импорт и экспорт локальных профилей", "6 個の名前付きスロット • ローカル設定をインポート・エクスポート"};
         break;
      case 28:
         values = new String[]{"APLICAR", "APPLY", "APLICAR", "ПРИМЕНИТЬ", "適用"};
         break;
      case 29:
         values = new String[]{"SALVAR ATUAL", "SAVE CURRENT", "GUARDAR ACTUAL", "СОХРАНИТЬ ТЕКУЩЕЕ", "現在の設定を保存"};
         break;
      case 30:
         values = new String[]{"PVP", "PVP", "PVP", "PVP", "PvP"};
         break;
      case 31:
         values = new String[]{"GERAL", "BALANCED", "EQUILIBRADO", "СБАЛАНСИРОВАННЫЙ", "標準"};
         break;
      case 32:
         values = new String[]{"LEVE", "LOW-END", "LIGERO", "ЛЁГКИЙ", "軽量"};
         break;
      case 33:
         values = new String[]{"Hora local", "Local time", "Hora local", "Местное время", "現地時刻"};
         break;
      case 34:
         values = new String[]{"Bioma atual", "Current biome", "Bioma actual", "Текущий биом", "現在のバイオーム"};
         break;
      case 35:
         values = new String[]{"Direção da câmera", "Camera direction", "Dirección de cámara", "Направление камеры", "カメラの方角"};
         break;
      case 36:
         values = new String[]{"HUD E INFORMAÇÕES", "HUD & INFORMATION", "HUD E INFORMACIÓN", "HUD И ИНФОРМАЦИЯ", "HUD と情報"};
         break;
      case 37:
         values = new String[]{"VISUAL", "VISUAL", "VISUAL", "ВИЗУАЛЬНОЕ", "ビジュアル"};
         break;
      case 38:
         values = new String[]{"Mais que um cliente, um novo horizonte.", "More than a client, a new horizon.", "Más que un cliente, un nuevo horizonte.", "Больше, чем клиент — новый горизонт.", "クライアント以上の、新しい地平線。"};
         break;
      case 39:
         values = new String[]{"JOGAR AGORA", "PLAY NOW", "JUGAR AHORA", "ИГРАТЬ", "今すぐプレイ"};
         break;
      case 40:
         values = new String[]{"UM JOGADOR", "SINGLEPLAYER", "UN JUGADOR", "ОДИНОЧНАЯ ИГРА", "シングルプレイ"};
         break;
      case 41:
         values = new String[]{"MULTIJOGADOR", "MULTIPLAYER", "MULTIJUGADOR", "СЕТЕВАЯ ИГРА", "マルチプレイ"};
         break;
      case 42:
         values = new String[]{"MODS", "MODS", "MODS", "МОДЫ", "MOD"};
         break;
      case 43:
         values = new String[]{"COSMÉTICOS", "COSMETICS", "COSMÉTICOS", "КОСМЕТИКА", "コスメ"};
         break;
      case 44:
         values = new String[]{"SERVIDORES", "SERVERS", "SERVIDORES", "СЕРВЕРЫ", "サーバー"};
         break;
      case 45:
         values = new String[]{"CHAT DE VOZ", "VOICE CHAT", "CHAT DE VOZ", "ГОЛОСОВОЙ ЧАТ", "ボイスチャット"};
         break;
      case 46:
         values = new String[]{"SAIR", "EXIT", "SALIR", "ВЫХОД", "終了"};
         break;
      case 47:
         values = new String[]{"CLIENTE • FORGE 1.8.9", "CLIENT • FORGE 1.8.9", "CLIENTE • FORGE 1.8.9", "КЛИЕНТ • FORGE 1.8.9", "クライアント • FORGE 1.8.9"};
         break;
      case 48:
         values = new String[]{"LUNARHAVENX • CLIENTE LOCAL", "LUNARHAVENX • LOCAL CLIENT", "LUNARHAVENX • CLIENTE LOCAL", "LUNARHAVENX • ЛОКАЛЬНЫЙ КЛИЕНТ", "LUNARHAVENX • ローカルクライアント"};
         break;
      case 49:
         values = new String[]{"Simple Voice Chat não lista uma versão Forge 1.8.9; voz exige cliente e servidor compatíveis.", "Simple Voice Chat has no listed Forge 1.8.9 build; voice requires compatible client and server.", "Simple Voice Chat no tiene una versión Forge 1.8.9; requiere cliente y servidor compatibles.", "Для Forge 1.8.9 нет версии Simple Voice Chat; нужны совместимые клиент и сервер.", "Simple Voice Chat に Forge 1.8.9 版はありません。対応するクライアントとサーバーが必要です。"};
         break;
      case 50:
         values = new String[]{"ANTERIOR", "PREVIOUS", "ANTERIOR", "НАЗАД", "前へ"};
         break;
      case 51:
         values = new String[]{"EQUIPAR", "EQUIP", "EQUIPAR", "НАДЕТЬ", "装備"};
         break;
      case 52:
         values = new String[]{"PRÓXIMA", "NEXT", "SIGUIENTE", "ДАЛЕЕ", "次へ"};
         break;
      case 53:
         values = new String[]{"VOLTAR", "BACK", "VOLVER", "НАЗАД", "戻る"};
         break;
      case 54:
         values = new String[]{"Pré-visualização e seleção local", "Preview and local selection", "Vista previa y selección local", "Предпросмотр и локальный выбор", "プレビューとローカル選択"};
         break;
      case 55:
         values = new String[]{"TEXTURA INDISPONÍVEL", "TEXTURE UNAVAILABLE", "TEXTURA NO DISPONIBLE", "ТЕКСТУРА НЕДОСТУПНА", "テクスチャを利用できません"};
         break;
      case 56:
         values = new String[]{"SEM CAPA", "NO CAPE", "SIN CAPA", "БЕЗ ПЛАЩА", "マントなし"};
         break;
      case 57:
         values = new String[]{"CAPA SELECIONADA", "SELECTED CAPE", "CAPA SELECCIONADA", "ВЫБРАННЫЙ ПЛАЩ", "選択中のマント"};
         break;
      case 58:
         values = new String[]{"Skins Ely.by aparecem para todos quando o servidor usa o sistema oficial.", "Ely.by skins are visible to everyone when the server uses its official system.", "Las skins Ely.by son visibles para todos si el servidor usa el sistema oficial.", "Скины Ely.by видны всем на серверах с официальной системой.", "サーバーが公式システムを使うと Ely.by スキンが全員に表示されます。"};
         break;
      case 59:
         values = new String[]{"Configure sua skin na conta Ely.by e entre em um servidor compatível.", "Set your skin on Ely.by and join a compatible server.", "Configura tu skin en Ely.by y entra a un servidor compatible.", "Настройте скин в Ely.by и зайдите на совместимый сервер.", "Ely.by でスキンを設定し、対応サーバーに参加してください。"};
         break;
      case 60:
         values = new String[]{"As capas LunarHavenX desta tela continuam sendo cosméticos locais.", "LunarHavenX capes on this screen remain local cosmetics.", "Las capas LunarHavenX de esta pantalla son cosméticos locales.", "Плащи LunarHavenX на этом экране видны только вам.", "この画面の LunarHavenX マントは自分だけに表示されます。"};
         break;
      case 61:
         values = new String[]{"Cosmético", "Cosmetic", "Cosmético", "Косметика", "コスメ"};
         break;
      case 62:
         values = new String[]{"equipado localmente.", "equipped locally.", "equipado localmente.", "надета локально.", "をローカルで装備しました。"};
         break;
      case 63:
         values = new String[]{"CONECTAR", "CONNECT", "CONECTAR", "ПОДКЛЮЧИТЬСЯ", "接続"};
         break;
      case 64:
         values = new String[]{"ADICIONAR", "ADD", "AÑADIR", "ДОБАВИТЬ", "追加"};
         break;
      case 65:
         values = new String[]{"REMOVER", "REMOVE", "ELIMINAR", "УДАЛИТЬ", "削除"};
         break;
      case 66:
         values = new String[]{"ATUALIZAR", "REFRESH", "ACTUALIZAR", "ОБНОВИТЬ", "更新"};
         break;
      case 67:
         values = new String[]{"FAVORITOS SALVOS", "SAVED FAVORITES", "FAVORITOS GUARDADOS", "СОХРАНЁННЫЕ", "保存済みのお気に入り"};
         break;
      case 68:
         values = new String[]{"Seus favoritos • armazenamento vanilla", "Your favorites • vanilla storage", "Tus favoritos • almacenamiento vanilla", "Избранное • хранилище Minecraft", "お気に入り • Minecraft 標準保存"};
         break;
      case 69:
         values = new String[]{"Nenhum servidor salvo. Adicione um favorito abaixo.", "No saved servers. Add a favorite below.", "No hay servidores guardados. Añade uno abajo.", "Нет сохранённых серверов. Добавьте сервер ниже.", "保存済みサーバーはありません。下から追加してください。"};
         break;
      case 70:
         values = new String[]{"Clique duas vezes ou selecione e conecte • scroll para navegar", "Double-click, or select and connect • scroll to browse", "Doble clic o selecciona y conecta • usa scroll", "Дважды щёлкните или выберите и подключитесь • прокрутка", "ダブルクリック、または選択して接続 • スクロールで移動"};
         break;
      case 71:
         values = new String[]{"NOME", "NAME", "NOMBRE", "НАЗВАНИЕ", "名前"};
         break;
      case 72:
         values = new String[]{"ENDEREÇO", "ADDRESS", "DIRECCIÓN", "АДРЕС", "アドレス"};
         break;
      case 73:
         values = new String[]{"ping não medido", "ping not measured", "ping no medido", "пинг не измерен", "ping 未計測"};
         break;
      case 74:
         values = new String[]{"Selecione um servidor salvo ou adicione um favorito.", "Select a saved server or add a favorite.", "Selecciona un servidor guardado o añade uno.", "Выберите сервер или добавьте избранное.", "保存済みサーバーを選択するか追加してください。"};
         break;
      case 75:
         values = new String[]{"Favorito removido.", "Favorite removed.", "Favorito eliminado.", "Сервер удалён из избранного.", "お気に入りを削除しました。"};
         break;
      case 76:
         values = new String[]{"Lista de favoritos atualizada.", "Favorites list refreshed.", "Lista de favoritos actualizada.", "Список избранного обновлён.", "お気に入り一覧を更新しました。"};
         break;
      case 77:
         values = new String[]{"Escolha um servidor antes de conectar.", "Choose a server before connecting.", "Elige un servidor antes de conectar.", "Выберите сервер для подключения.", "接続するサーバーを選択してください。"};
         break;
      case 78:
         values = new String[]{"Informe o endereço do servidor.", "Enter the server address.", "Introduce la dirección del servidor.", "Введите адрес сервера.", "サーバーアドレスを入力してください。"};
         break;
      case 79:
         values = new String[]{"Servidor adicionado aos favoritos.", "Server added to favorites.", "Servidor añadido a favoritos.", "Сервер добавлен в избранное.", "サーバーをお気に入りに追加しました。"};
         break;
      case 80:
         values = new String[]{"ATALHOS E MACROS", "KEYBINDS & MACROS", "ATAJOS Y MACROS", "КЛАВИШИ И МАКРОСЫ", "キー設定とマクロ"};
         break;
      case 81:
         values = new String[]{"Atalhos locais • sem automação de combate", "Local keybinds • no combat automation", "Atajos locales • sin automatización de combate", "Локальные клавиши • без автоматизации боя", "ローカルキー設定 • 戦闘の自動化なし"};
         break;
      case 82:
         values = new String[]{"CONTROLES", "CONTROLS", "CONTROLES", "УПРАВЛЕНИЕ", "操作"};
         break;
      case 83:
         values = new String[]{"MACROS DE CHAT OPCIONAIS", "OPTIONAL CHAT MACROS", "MACROS DE CHAT OPCIONALES", "НЕОБЯЗАТЕЛЬНЫЕ МАКРОСЫ ЧАТА", "チャットマクロ（任意）"};
         break;
      case 84:
         values = new String[]{"Uma mensagem por tecla pressionada.", "One message per key press.", "Un mensaje por pulsación.", "Одно сообщение на нажатие клавиши.", "1 回のキー入力につき 1 メッセージ。"};
         break;
      case 85:
         values = new String[]{"Não envia mensagens automaticamente.", "Messages are never sent automatically.", "No se envían mensajes automáticamente.", "Сообщения не отправляются автоматически.", "メッセージは自動送信されません。"};
         break;
      case 86:
         values = new String[]{"RESTAURAR TECLAS", "RESET KEYS", "RESTAURAR TECLAS", "СБРОСИТЬ КЛАВИШИ", "キーを初期化"};
         break;
      case 87:
         values = new String[]{"LIMPAR MACROS", "CLEAR MACROS", "BORRAR MACROS", "ОЧИСТИТЬ МАКРОСЫ", "マクロを消去"};
         break;
      case 88:
         values = new String[]{"PRESSIONE UMA TECLA...", "PRESS A KEY...", "PULSA UNA TECLA...", "НАЖМИТЕ КЛАВИШУ...", "キーを押してください..."};
         break;
      case 89:
         values = new String[]{"NÃO DEFINIDO", "UNASSIGNED", "SIN ASIGNAR", "НЕ НАЗНАЧЕНО", "未設定"};
         break;
      case 90:
         values = new String[]{"LUNARHAVENX CHAT", "LUNARHAVENX CHAT", "CHAT LUNARHAVENX", "ЧАТ LUNARHAVENX", "LUNARHAVENX チャット"};
         break;
      case 91:
         values = new String[]{"EMOJIS", "EMOJIS", "EMOJIS", "ЭМОДЗИ", "絵文字"};
         break;
      case 92:
         values = new String[]{"Abrir configurações", "Open settings", "Abrir ajustes", "Открыть настройки", "設定を開く"};
         break;
      case 93:
         values = new String[]{"Alternar HUD", "Toggle HUD", "Alternar HUD", "Переключить HUD", "HUD の切り替え"};
         break;
      case 94:
         values = new String[]{"Ciclar perfil de desempenho", "Cycle performance profile", "Cambiar perfil de rendimiento", "Сменить профиль производительности", "パフォーマンス設定を切り替え"};
         break;
      case 95:
         values = new String[]{"Abrir cosméticos", "Open cosmetics", "Abrir cosméticos", "Открыть косметику", "コスメを開く"};
         break;
      case 96:
         values = new String[]{"Abrir servidores", "Open servers", "Abrir servidores", "Открыть серверы", "サーバーを開く"};
         break;
      case 97:
         values = new String[]{"ESPAÇO", "SLOT", "ESPACIO", "СЛОТ", "スロット"};
         break;
      case 98:
         values = new String[]{"ARQUIVO .LHXPROFILE • NOME SEM EXTENSÃO", "PROFILE FILE • NAME WITHOUT EXTENSION", "ARCHIVO .LHXPROFILE • NOMBRE SIN EXTENSIÓN", "ФАЙЛ .LHXPROFILE • ИМЯ БЕЗ РАСШИРЕНИЯ", "プロフィールファイル • 拡張子なしの名前"};
         break;
      case 99:
         values = new String[]{"Local: config/lunarhavenx/profiles • clique em um nome para escolher o espaço.", "Local: config/lunarhavenx/profiles • click a name field to choose the target slot.", "Local: config/lunarhavenx/profiles • pulsa un nombre para elegir el espacio.", "Папка: config/lunarhavenx/profiles • нажмите имя, чтобы выбрать слот.", "保存先: config/lunarhavenx/profiles • 名前欄をクリックして対象スロットを選択。"};
         break;
      case 100:
         values = new String[]{"IMPORTAR", "IMPORT", "IMPORTAR", "ИМПОРТ", "読み込み"};
         break;
      case 101:
         values = new String[]{"EXPORTAR", "EXPORT", "EXPORTAR", "ЭКСПОРТ", "書き出し"};
         break;
      case 102:
         values = new String[]{"Perfil aplicado.", "Profile applied.", "Perfil aplicado.", "Профиль применён.", "プロファイルを適用しました。"};
         break;
      case 103:
         values = new String[]{"Perfil salvo com este nome.", "Profile saved with this name.", "Perfil guardado con este nombre.", "Профиль сохранён с этим именем.", "この名前でプロファイルを保存しました。"};
         break;
      case 104:
         values = new String[]{"Perfil importado e aplicado.", "Profile imported and applied.", "Perfil importado y aplicado.", "Профиль импортирован и применён.", "プロファイルを読み込み、適用しました。"};
         break;
      case 105:
         values = new String[]{"Arquivo inválido ou não encontrado na pasta de perfis.", "File invalid or missing from the profiles folder.", "Archivo inválido o no encontrado en la carpeta de perfiles.", "Файл недействителен или не найден в папке профилей.", "プロフィールフォルダーにファイルがないか、形式が不正です。"};
         break;
      case 106:
         values = new String[]{"Perfil exportado para a pasta de perfis.", "Profile exported to the profiles folder.", "Perfil exportado a la carpeta de perfiles.", "Профиль экспортирован в папку профилей.", "プロフィールフォルダーへ書き出しました。"};
         break;
      case 107:
         values = new String[]{"Não foi possível exportar. Use um nome simples de arquivo.", "Export failed. Use a simple file name.", "No se pudo exportar. Usa un nombre de archivo sencillo.", "Не удалось экспортировать. Используйте простое имя файла.", "書き出し失敗。シンプルなファイル名を使ってください。"};
         break;
      case 108:
         values = new String[]{"EDITAR POSIÇÃO DO HUD", "EDIT HUD LAYOUT", "EDITAR POSICIÓN DEL HUD", "ИЗМЕНИТЬ HUD", "HUD の配置を編集"};
         break;
      case 109:
         values = new String[]{"EDITOR DO HUD", "HUD EDITOR", "EDITOR DEL HUD", "РЕДАКТОР HUD", "HUD エディター"};
         break;
      case 110:
         values = new String[]{"Arraste os painéis para reposicionar • posições salvas por resolução", "Drag panels to reposition • positions adapt to resolution", "Arrastra paneles • posiciones adaptables a la resolución", "Перетаскивайте панели • позиции подстраиваются под разрешение", "パネルをドラッグして配置 • 解像度に合わせて保存"};
         break;
      case 111:
         values = new String[]{"RESTAURAR POSIÇÕES", "RESET POSITIONS", "RESTABLECER POSICIONES", "СБРОСИТЬ ПОЗИЦИИ", "位置をリセット"};
         break;
      case 112:
         values = new String[]{"Informações", "Info panel", "Información", "Информация", "情報"};
         break;
      case 113:
         values = new String[]{"STATUS DO JOGADOR", "PLAYER STATUS", "ESTADO DEL JUGADOR", "СТАТУС ИГРОКА", "プレイヤー状態"};
         break;
      case 114:
         values = new String[]{"VIDA", "HEALTH", "VIDA", "ЗДОРОВЬЕ", "体力"};
         break;
      case 115:
         values = new String[]{"FOME", "HUNGER", "HAMBRE", "ГОЛОД", "空腹度"};
         break;
      case 116:
         values = new String[]{"Nenhum efeito ativo", "No active effects", "Sin efectos activos", "Нет активных эффектов", "効果なし"};
         break;
      case 117:
         values = new String[]{"Efeito", "Effect", "Efecto", "Эффект", "効果"};
         break;
      case 118:
         values = new String[]{"AGACHAR", "SNEAK", "AGACHARSE", "КРАСТЬСЯ", "スニーク"};
         break;
      case 119:
         values = new String[]{"AGACHADO", "SNEAKING", "AGACHADO", "КРАДЁТСЯ", "スニーク中"};
         break;
      case 120:
         values = new String[]{"CORRER", "SPRINT", "CORRER", "БЕГ", "ダッシュ"};
         break;
      case 121:
         values = new String[]{"CORRENDO", "SPRINTING", "CORRIENDO", "БЕЖИТ", "ダッシュ中"};
         break;
      case 122:
         values = new String[]{"ESQ", "L", "IZQ", "ЛКМ", "左"};
         break;
      case 123:
         values = new String[]{"DIR", "R", "DER", "ПКМ", "右"};
         break;
      case 124:
         values = new String[]{"BATATA", "VERY LOW", "MUY BAJO", "МИНИМАЛЬНЫЙ", "最低"};
         break;
      case 125:
         values = new String[]{"BAIXO", "LOW", "BAJO", "НИЗКИЙ", "低"};
         break;
      case 126:
         values = new String[]{"BALANCEADO", "BALANCED", "EQUILIBRADO", "СБАЛАНСИРОВАННЫЙ", "標準"};
         break;
      case 127:
         values = new String[]{"ALTO FPS", "HIGH FPS", "FPS ALTO", "ВЫСОКИЙ FPS", "高 FPS"};
         break;
      case 128:
         values = new String[]{"FPS EXTREMO", "EXTREME FPS", "FPS EXTREMO", "ЭКСТРЕМАЛЬНЫЙ FPS", "FPS 優先"};
         break;
      case 129:
         values = new String[]{"CELULAR FRACO", "LOW-END MOBILE", "MÓVIL BÁSICO", "СЛАБЫЙ ТЕЛЕФОН", "低性能端末"};
         break;
      case 130:
         values = new String[]{"CARREGANDO MÓDULOS...", "LOADING MODULES...", "CARGANDO MÓDULOS...", "ЗАГРУЗКА МОДУЛЕЙ...", "モジュールを読み込み中..."};
         break;
      case 131:
         values = new String[]{"RECURSOS DO CLIENTE", "CLIENT FEATURES", "RECURSOS DEL CLIENTE", "ВОЗМОЖНОСТИ КЛИЕНТА", "クライアント機能"};
         break;
      case 132:
         values = new String[]{"HUD AVANÇADO", "ADVANCED HUD", "HUD AVANZADO", "РАСШИРЕННЫЙ HUD", "拡張 HUD"};
         break;
      case 133:
         values = new String[]{"CPS, teclas, FPS e armadura", "CPS, keys, FPS and armor", "CPS, teclas, FPS y armadura", "CPS, клавиши, FPS и броня", "CPS、キー、FPS、防具"};
         break;
      case 134:
         values = new String[]{"DESEMPENHO", "PERFORMANCE", "RENDIMIENTO", "ПРОИЗВОДИТЕЛЬНОСТЬ", "パフォーマンス"};
         break;
      case 135:
         values = new String[]{"Perfis gráficos e modo leve", "Graphics profiles and low-end mode", "Perfiles gráficos y modo ligero", "Графические профили и лёгкий режим", "画質プロファイルと軽量モード"};
         break;
      case 136:
         values = new String[]{"ELY.BY", "ELY.BY", "ELY.BY", "ELY.BY", "ELY.BY"};
         break;
      case 137:
         values = new String[]{"Em servidores compatíveis", "On compatible servers", "En servidores compatibles", "На совместимых серверах", "対応サーバーで利用可能"};
         break;
      case 138:
         values = new String[]{"COSMÉTICOS", "COSMETICS", "COSMÉTICOS", "КОСМЕТИКА", "コスメ"};
         break;
      case 139:
         values = new String[]{"Capas originais locais", "Original local capes", "Capas originales locales", "Оригинальные локальные плащи", "オリジナルのローカルマント"};
         break;
      case 140:
         values = new String[]{"ATALHOS", "KEYBINDS", "ATAJOS", "КЛАВИШИ", "キー設定"};
         break;
      case 141:
         values = new String[]{"Configurações rápidas no jogo", "Quick in-game settings", "Ajustes rápidos en el juego", "Быстрые настройки в игре", "ゲーム内のクイック設定"};
         break;
      case 142:
         values = new String[]{"INICIANDO...", "STARTING...", "INICIANDO...", "ЗАПУСК...", "起動中..."};
         break;
      case 143:
         values = new String[]{"PREPARANDO CLIENTE...", "PREPARING CLIENT...", "PREPARANDO CLIENTE...", "ПОДГОТОВКА КЛИЕНТА...", "クライアントを準備中..."};
         break;
      case 144:
         values = new String[]{"TEXTURA E RECURSOS VALIDADOS", "TEXTURE AND RESOURCES READY", "TEXTURA Y RECURSOS VALIDADOS", "ТЕКСТУРЫ И РЕСУРСЫ ПРОВЕРЕНЫ", "テクスチャとリソースを確認しました"};
         break;
      case 145:
         values = new String[]{"TEXTURA AUSENTE — FUNDO DE SEGURANÇA ATIVO", "TEXTURE MISSING — FALLBACK BACKGROUND", "TEXTURA AUSENTE — FONDO ALTERNATIVO", "ТЕКСТУРА НЕ НАЙДЕНА — РЕЗЕРВНЫЙ ФОН", "テクスチャなし — 代替背景を使用"};
         break;
      case 146:
         values = new String[]{"PREFERÊNCIAS CARREGADAS", "PREFERENCES LOADED", "PREFERENCIAS CARGADAS", "НАСТРОЙКИ ЗАГРУЖЕНЫ", "設定を読み込みました"};
         break;
      case 147:
         values = new String[]{"ATALHOS E COSMÉTICOS PRONTOS", "KEYBINDS AND COSMETICS READY", "ATAJOS Y COSMÉTICOS LISTOS", "КЛАВИШИ И КОСМЕТИКА ГОТОВЫ", "キー設定とコスメの準備完了"};
         break;
      case 148:
         values = new String[]{"PRONTO PARA JOGAR", "READY TO PLAY", "LISTO PARA JUGAR", "ГОТОВО К ИГРЕ", "プレイできます"};
         break;
      case 149:
         values = new String[]{"PRONTO • FUNDO DE SEGURANÇA", "READY • FALLBACK BACKGROUND", "LISTO • FONDO ALTERNATIVO", "ГОТОВО • РЕЗЕРВНЫЙ ФОН", "準備完了 • 代替背景"};
         break;
      case 150:
         values = new String[]{"HUD • PERFIL LEVE • ELY.BY COMPATÍVEL", "HUD • LOW-END PROFILE • ELY.BY READY", "HUD • PERFIL LIGERO • ELY.BY COMPATIBLE", "HUD • ЛЁГКИЙ ПРОФИЛЬ • ELY.BY", "HUD • 軽量プロファイル • ELY.BY 対応"};
         break;
      case 151:
         values = new String[]{"VERSÃO", "VERSION", "VERSIÓN", "ВЕРСИЯ", "バージョン"};
         break;
      case 152:
         values = new String[]{"FPS", "FPS", "FPS", "FPS", "FPS"};
         break;
      case 153:
         values = new String[]{"PERFIL", "PROFILE", "PERFIL", "ПРОФИЛЬ", "プロファイル"};
         break;
      case 154:
         values = new String[]{"PLATAFORMA", "PLATFORM", "PLATAFORMA", "ПЛАТФОРМА", "プラットフォーム"};
         break;
      case 155:
         values = new String[]{"ANDROID", "ANDROID", "ANDROID", "ANDROID", "ANDROID"};
         break;
      case 156:
         values = new String[]{"DESKTOP", "DESKTOP", "ESCRITORIO", "ПК", "デスクトップ"};
         break;
      case 157:
         values = new String[]{"DICA: configure os controles de toque no MJLauncher.", "TIP: configure touch controls in MJLauncher.", "CONSEJO: configura los controles táctiles en MJLauncher.", "СОВЕТ: настройте сенсорное управление в MJLauncher.", "ヒント: MJLauncher でタッチ操作を設定してください。"};
         break;
      case 158:
         values = new String[]{"DICA: RSHIFT abre configurações • F7 alterna perfis.", "TIP: RSHIFT opens settings • F7 cycles profiles.", "CONSEJO: RSHIFT abre ajustes • F7 cambia perfiles.", "СОВЕТ: RSHIFT — настройки • F7 — смена профиля.", "ヒント: RSHIFT で設定、F7 でプロファイル切替。"};
         break;
      default:
         return key;
      }

      return values[this.language];
   }

   public String performanceName(String id) {
      return "BATATA".equals(id)?this.text("perfPotato"):("BAIXO".equals(id)?this.text("perfLow"):("BALANCEADO".equals(id)?this.text("perfBalanced"):("ALTO_FPS".equals(id)?this.text("perfHigh"):("FPS_EXTREMO".equals(id)?this.text("perfExtreme"):("CELULAR_FRACO".equals(id)?this.text("perfMobile"):id)))));
   }

   private void load() {
      if(this.file.isFile()) {
         Properties p = new Properties();

         try {
            FileInputStream in = new FileInputStream(this.file);

            try {
               p.load(in);
            } finally {
               in.close();
            }

            this.language = Math.max(0, Math.min(NAMES.length - 1, Integer.parseInt(p.getProperty("language", "0"))));
         } catch (Exception var7) {
            this.language = 0;
         }

      }
   }

   private void save() {
      File parent = this.file.getParentFile();
      if(parent.exists() || parent.mkdirs()) {
         Properties p = new Properties();
         p.setProperty("language", String.valueOf(this.language));

         try {
            FileOutputStream out = new FileOutputStream(this.file);

            try {
               p.store(out, "LunarHavenX interface language");
            } finally {
               out.close();
            }
         } catch (IOException var8) {
            ;
         }

      }
   }
}
