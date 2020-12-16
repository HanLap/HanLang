.include "../framework.asm"
.section "main"
main:

ld a, 1
ld c, 3
ld b, 6
call setTile


call waitVBlank

ret
.ends
