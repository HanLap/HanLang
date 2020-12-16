.include "../framework.asm"
.include "../custom.asm"
.section "main"
main:


  call clearTilemap

  push af
  pop af

  ld a, 1
  push af
  ld a, 3
  push af
  ld a, 6
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 3
  push af
  ld a, 8
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 3
  push af
  ld a, 7
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 3
  push af
  ld a, 9
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 3
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 4
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 5
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 5
  push af
  ld a, 9
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 5
  push af
  ld a, 8
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 5
  push af
  ld a, 7
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 5
  push af
  ld a, 6
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 7
  push af
  ld a, 9
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 7
  push af
  ld a, 8
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 7
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 8
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 9
  push af
  ld a, 8
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 9
  push af
  ld a, 9
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 9
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 10
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 11
  push af
  ld a, 8
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 11
  push af
  ld a, 9
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 11
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 13
  push af
  ld a, 6
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 13
  push af
  ld a, 7
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 13
  push af
  ld a, 8
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 13
  push af
  ld a, 9
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 13
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 14
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 15
  push af
  ld a, 10
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 15
  push af
  ld a, 9
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 15
  push af
  ld a, 8
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 15
  push af
  ld a, 7
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

  ld a, 1
  push af
  ld a, 15
  push af
  ld a, 6
  push af
  pop af
  ld b, a

  pop af
  ld c, a

  pop af
  ld a, a


  call setTile

  push af
  pop af

ret
.ends