.include "../framework.asm"
.include "../custom.asm"

.section "pushRegs"
pushRegs:
  push bc
  push de
  push hl
  ret
.ends

.section "popRegs"
popRegs:
  pop hl
  pop de
  pop bc
  ret
.ends


.section "main"
main:
  ld hl, $d000

  push bc
  push de
  push hl

  ld a, 2
  ld c, 3
  call myAdd

  ld [hl], a

  pop hl
  pop de
  pop bc

  push bc
  push de
  push hl

  ld a, 3
  ld c, 4
  call myAdd

  pop hl
  pop de
  pop bc

  ld c, a

  
  ld a, [hl]

  call myAdd

  call printByte

ret
.ends
