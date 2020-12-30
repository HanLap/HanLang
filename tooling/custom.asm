.section "myAdd"
myAdd:
  add c
  ret
.ends

.section "mul"
mul:
  push bc
  push hl
  ld l, a
-:
  dec c
  jp z, +
  add l
  jp -
+:
  pop hl
  pop bc
  ret
.ends


.section "div"
div:
  push bc
  push hl
  ld l, 0
-:
  cp c
  jp c, +
  inc l
  sub c
  jp -
+:
  ld a, l
  pop hl
  pop bc
  ret
.ends
