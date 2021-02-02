.include "../framework.asm"
.include "../custom.asm"   

.section "tileOne"
tileOne:
  .db %00000000
  .db %00011000
  .db %00101000
  .db %00001000
  .db %00001000
  .db %00001000
  .db %00111110
  .db %00000000
.ends

.section "main"
main:

  call clearTilemap

  ld hl, tileOne
  ld c, 1
  ld b, 1
  call loadTiles1bpp

-:

  ld a, 1
  ld c, 1
  ld b, 1
  call setTile


  call waitVBlank
  call updateOAM
  jp -
.ends
