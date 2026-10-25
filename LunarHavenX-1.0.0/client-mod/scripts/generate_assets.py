from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import random, math

root = Path(__file__).resolve().parents[1]
out = root / 'src/main/resources/assets/lunarhavenx/textures/gui'
out.mkdir(parents=True, exist_ok=True)
W, H = 1280, 720
img = Image.new('RGB', (W, H), '#050b2b')
p = img.load()
for y in range(H):
    for x in range(W):
        t = y / H
        r = int(4 + 7*t + 5*max(0, 1-abs(x-W*.55)/(W*.75)))
        g = int(9 + 18*t + 16*max(0, 1-abs(x-W*.55)/(W*.75)))
        b = int(35 + 56*t + 55*max(0, 1-abs(x-W*.55)/(W*.75)))
        p[x,y] = (r,g,b)
d = ImageDraw.Draw(img, 'RGBA')
rng = random.Random(189)
for _ in range(260):
    x, y = rng.randrange(W), rng.randrange(480)
    a = rng.randrange(80, 230)
    s = rng.choice([1,1,1,2])
    d.ellipse((x-s,y-s,x+s,y+s), fill=(130,220,255,a))
    if s == 2:
        d.line((x-6,y,x+6,y), fill=(80,190,255,70), width=1)
        d.line((x,y-6,x,y+6), fill=(80,190,255,70), width=1)
# moon glow
cx, cy, rr = 920, 205, 150
for rad in range(rr+70, rr, -2):
    a = int(2 + 18*(rr+70-rad)/70)
    d.ellipse((cx-rad,cy-rad,cx+rad,cy+rad), fill=(70,160,255,a))
d.ellipse((cx-rr,cy-rr,cx+rr,cy+rr), fill=(126,220,255,235), outline=(190,250,255,255), width=3)
d.ellipse((cx-rr+13,cy-rr+8,cx+rr-8,cy+rr-4), fill=(23,84,170,80))
# distant clouds
for i in range(18):
    x = rng.randrange(-100, W)
    y = rng.randrange(130, 360)
    d.ellipse((x,y,x+rng.randrange(90,220),y+rng.randrange(30,75)), fill=(36,91,190,45))
# floating islands
for i in range(10):
    x = 40 + i*135 + rng.randrange(-35,35)
    y = rng.randrange(300, 540)
    topw = rng.randrange(75,150)
    d.polygon([(x,y),(x+topw,y),(x+topw-22,y+24),(x+topw//2,y+85),(x+18,y+25)], fill=(8,32,75,245), outline=(30,153,218,180))
    d.rectangle((x+12,y-9,x+topw-16,y+3), fill=(21,88,105,230))
    for tx in range(x+18, x+topw-20, 22):
        d.line((tx,y-18,tx+5,y-3), fill=(36,144,105,240), width=5)
# foreground island and waterfall
base_y = 605
d.polygon([(0,base_y-30),(150,base_y-15),(280,base_y-55),(425,base_y-20),(560,base_y-70),(700,base_y-10),(830,base_y-50),(1000,base_y-20),(1120,base_y-70),(1280,base_y-20),(1280,H),(0,H)], fill=(5,24,52,250))
for x in [570, 584, 602, 620, 638]:
    d.line((x,550,x+rng.randrange(-15,16),H), fill=(31,174,239,170), width=5)
# castle silhouettes
for x,h in [(750,150),(820,225),(900,175),(1000,260)]:
    y=base_y-h
    d.rectangle((x,y,x+70,base_y), fill=(8,21,50,235), outline=(58,128,194,160))
    d.polygon([(x-12,y),(x+35,y-40),(x+82,y)], fill=(8,21,50,235), outline=(58,128,194,160))
    for wx in (x+18,x+47):
        d.rectangle((wx,y+40,wx+8,y+60), fill=(252,194,72,200))
# title
font_big = '/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf'
try:
    f1 = ImageFont.truetype(font_big, 58)
    f2 = ImageFont.truetype(font_big, 26)
except OSError:
    f1 = f2 = ImageFont.load_default()
def centered(text, y, font, fill, stroke=0):
    box=d.textbbox((0,0),text,font=font,stroke_width=stroke)
    d.text(((W-(box[2]-box[0]))/2,y),text,font=font,fill=fill,stroke_width=stroke,stroke_fill=(4,20,70,230))
centered('LUNARHAVENX', 300, f1, (230,248,255,255), 2)
centered('CLIENT', 365, f2, (65,215,255,255), 1)
img.save(out/'loading_background.png', optimize=True)

lang_dir = root / 'src/main/resources/assets/lunarhavenx/lang'
lang_dir.mkdir(parents=True, exist_ok=True)
translations = {
    'pt_BR': {'lunarhavenx.loading': 'CARREGANDO RECURSOS...', 'lunarhavenx.ready': 'PRONTO PARA JOGAR'},
    'en_US': {'lunarhavenx.loading': 'LOADING RESOURCES...', 'lunarhavenx.ready': 'READY TO PLAY'},
    'es_ES': {'lunarhavenx.loading': 'CARGANDO RECURSOS...', 'lunarhavenx.ready': 'LISTO PARA JUGAR'},
    'ru_RU': {'lunarhavenx.loading': 'ЗАГРУЗКА РЕСУРСОВ...', 'lunarhavenx.ready': 'ГОТОВО К ИГРЕ'},
    'ja_JP': {'lunarhavenx.loading': 'リソースを読み込み中...', 'lunarhavenx.ready': 'プレイ準備完了'},
}
import json
for name, values in translations.items():
    (lang_dir/f'{name}.json').write_text(json.dumps(values, ensure_ascii=False, indent=2)+'\n', encoding='utf-8')
print(out/'loading_background.png')
