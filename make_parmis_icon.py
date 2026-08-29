from PIL import Image, ImageDraw, ImageFont

# Load the source image which has the exact griffin
src_img = Image.open("app/src/main/res/drawable/fall1405_1.jpg").convert("RGBA")
W, H = 512, 512

BG_COLOR = (238, 144, 0, 255) # Orange-Gold #EE9000
WHITE = (255, 255, 255, 255)

# Crop the Griffin from fall1405_1.jpg
griffin_crop = src_img.crop((145, 148, 368, 345))

# Process pixels in pure PIL
pixels = griffin_crop.getdata()
new_pixels = []
for p in pixels:
    r, g, b, a = p
    # If pixel is whitish background
    if r > 215 and g > 215 and b > 215:
        new_pixels.append((255, 255, 255, 0)) # transparent
    else:
        new_pixels.append(BG_COLOR) # Orange-Gold stroke

griffin_clean = Image.new('RGBA', griffin_crop.size)
griffin_clean.putdata(new_pixels)

# Create 512x512 canvas
canvas = Image.new("RGBA", (W, H), BG_COLOR)
draw = ImageDraw.Draw(canvas)

# White rounded calendar card
card_rect = [70, 78, 442, 444]
draw.rounded_rectangle(card_rect, radius=52, fill=WHITE)

# Top header bar (Orange-gold)
header_rect = [86, 98, 426, 152]
draw.rounded_rectangle(header_rect, radius=12, fill=BG_COLOR)

# Top hanging hooks
for cx in [162, 350]:
    draw.rounded_rectangle([cx - 20, 52, cx + 20, 138], radius=20, fill=WHITE)
    draw.rounded_rectangle([cx - 8, 64, cx + 8, 118], radius=8, fill=BG_COLOR)

# Paste Griffin
gw, gh = griffin_clean.size
canvas.paste(griffin_clean, ((W - gw) // 2, 154), griffin_clean)

# Fonts
font_top = ImageFont.truetype("/tmp/Vazirmatn-Bold.ttf", 46)
font_bottom = ImageFont.truetype("/tmp/Vazirmatn-Bold.ttf", 52)

# Top text: "۱۴۰۵"
draw.text((W // 2, 122), "۱۴۰۵", font=font_top, fill=WHITE, anchor="mm")

# Bottom text: "تقویم پارمیس"
draw.text((W // 2, 385), "تقویم پارمیس", font=font_bottom, fill=BG_COLOR, anchor="mm")

out_master = "app/src/main/res/drawable/parmis_app_icon.png"
canvas.save(out_master, "PNG")
print("Successfully generated master icon:", out_master)
