.include "../framework.asm"
.section "main"
main:

ld a, 42
call printByte

ret
.ends
