import subprocess
import os

# We will use ImageMagick's rich vector drawing commands (MVG) which it interprets natively
# with full anti-aliasing and subpixel precision!

mvg_content = '''
viewbox 0 0 512 512
fill #BA4514
rectangle 0,0 512,512

# White Calendar Card
fill #FFFFFF
stroke none
roundrectangle 72,78 440,462 46,46

# Upper Banner in Orange
push graphic-context
  # Clip to the rounded calendar card shape
  fill #BA4514
  roundrectangle 72,78 440,154 46,46
  # Square out the bottom of the banner
  rectangle 72,120 440,154
pop graphic-context

# Hanging Ring Loops (Left & Right)
# Left Ring Loop
fill #FFFFFF
stroke #BA4514
stroke-width 7
roundrectangle 147,48 185,140 19,19
fill #BA4514
stroke none
roundrectangle 158,68 174,120 8,8

# Right Ring Loop
fill #FFFFFF
stroke #BA4514
stroke-width 7
roundrectangle 327,48 365,140 19,19
fill #BA4514
stroke none
roundrectangle 338,68 354,120 8,8

# Persian Winged Griffin / Lion in Terracotta Orange
fill none
stroke #BA4514
stroke-width 6.5
stroke-linecap round
stroke-linejoin round

# Griffin Head & Horns/Ears
path 'M 306,192 C 314,180 324,182 328,190 C 334,178 344,180 348,190 C 352,196 350,204 345,210'
# Head Crown & Brow
path 'M 306,192 C 298,196 295,204 296,212 C 298,220 306,225 316,225'
# Beak & Open Roaring Mouth
path 'M 345,200 C 358,202 368,206 370,212 C 364,217 352,220 342,220'
path 'M 342,220 L 358,228 C 352,234 344,236 336,234'
# Jaw line & Throat
path 'M 336,234 C 328,240 318,242 308,242 C 298,242 292,236 290,230'

# Eye
push graphic-context
  fill #BA4514
  stroke none
  circle 332,204 332,208
  fill #FFFFFF
  circle 333,203 333,205
pop graphic-context

# Neck / Mane Feathers (Layered collar)
path 'M 308,242 C 316,252 326,262 334,272'
path 'M 296,236 C 304,248 312,258 320,268'
path 'M 284,232 C 292,246 298,258 304,270'

# Majestic Wing
# Leading outer wing edge sweeping high and back
path 'M 252,230 C 240,195 210,165 192,160 C 190,160 194,168 202,178 C 220,202 240,240 250,260'
# Primary Flight Feathers
path 'M 198,172 C 215,190 235,215 258,235'
path 'M 208,188 C 225,206 245,228 268,246'
path 'M 220,205 C 236,222 255,242 278,256'
path 'M 234,222 C 248,238 265,255 288,266'
path 'M 248,240 C 260,252 276,266 296,274'

# Muscular Chest & Body Contour
path 'M 334,272 C 328,290 310,300 292,302 C 265,302 245,292 225,296 C 205,300 190,290 178,272'
path 'M 292,302 C 285,305 260,305 240,300 C 225,296 210,290 200,278'

# Front Legs & Paws
# Right Front Leg (extended forward)
path 'M 326,280 C 330,300 338,322 344,342 L 354,346 C 354,348 348,350 334,350 L 328,350 C 324,345 322,335 320,320 L 316,295'
# Left Front Leg (inner)
path 'M 306,290 C 310,308 314,328 318,344 L 328,347 C 326,350 320,350 310,350 L 305,350 C 300,340 298,325 298,300'

# Claws on front paws
path 'M 344,348 L 348,348'
path 'M 338,348 L 342,348'
path 'M 318,348 L 322,348'

# Hindquarters & Haunch
path 'M 178,272 C 168,255 174,235 190,225 C 205,225 215,240 210,260 C 208,275 198,285 192,295'
# Haunch muscle accent line
path 'M 188,245 C 196,252 198,265 194,275'

# Hind Legs & Paws
# Left Hind Leg
path 'M 192,295 C 188,310 185,328 190,344 L 202,348 C 200,350 195,350 185,350 L 178,350 C 174,340 174,325 178,300'
# Right Hind Leg (back leg)
path 'M 174,285 C 165,302 162,320 166,342 L 176,346 C 174,349 170,350 162,350 L 156,350 C 152,338 152,322 158,295'

# Ground Baseline Accent under paws
path 'M 150,350 L 360,350'

# S-Curved Lion Tail
path 'M 175,236 C 158,220 148,235 145,250 C 142,268 155,282 170,285 C 180,287 188,282 185,272 C 182,262 168,262 165,250 C 162,238 170,225 180,220'
# Tail flame / tuft finial
push graphic-context
  fill #BA4514
  stroke none
  path 'M 180,220 C 185,212 188,206 182,202 C 176,206 172,212 174,222 Z'
pop graphic-context
'''

with open('icon_base.mvg', 'w', encoding='utf-8') as f:
    f.write(mvg_content)

# Render base vector image
subprocess.run(['convert', 'icon_base.mvg', 'icon_base.png'], check=True)

# Now render the top Persian calligraphy "تقویم" in white using Vazirmatn-Bold
# Shapped 'تقویم' for LTR canvas:
taghvim_shaped_reversed = '\uFEE1\uFEF3\uFEED\uFED7\uFE97'

subprocess.run([
    'convert', '-size', '200x50', 'xc:none',
    '-font', './Vazirmatn-Bold.ttf', '-pointsize', '34', '-fill', '#FFFFFF',
    '-gravity', 'center', '-annotate', '+0+0', taghvim_shaped_reversed,
    'text_taghvim.png'
], check=True)

# Now render the bottom Persian text "تابستان ۱۴۰۵" in Terracotta Orange #BA4514
# Right side: 'تابستان', Left side: '۱۴۰۵'
tabestan_shaped_reversed = '\uFEE5\uFE8E\uFE97\uFEB3\uFE91\uFE8E\uFE97'
persian_year = '۱۴۰۵'
bottom_full_text = f'{persian_year}   {tabestan_shaped_reversed}'

subprocess.run([
    'convert', '-size', '340x60', 'xc:none',
    '-font', './Vazirmatn-Bold.ttf', '-pointsize', '36', '-fill', '#BA4514',
    '-gravity', 'center', '-annotate', '+0+0', bottom_full_text,
    'text_bottom.png'
], check=True)

# Composite everything onto the base image
# Composite "تقویم" at center-top (X=256, Y=126)
subprocess.run([
    'convert', 'icon_base.png',
    'text_taghvim.png', '-geometry', '+156+103', '-composite',
    'text_bottom.png', '-geometry', '+86+390', '-composite',
    'master_512.png'
], check=True)

print("Generated master_512.png successfully!")
