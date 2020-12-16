.include "../framework.asm"
.section "main"
main:

ld a, 2
ld b, 3
add b
call printByte

ret
.ends
