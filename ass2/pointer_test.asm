.include "../framework.asm"
.section "main"
main:


  ld hl, $D000
  ld [hl], 5
  ld a, [hl]
  call printByte

  ld hl, $D002
  ld [hl], 8
  ld a, [hl]
  call printByte

ret
.ends
