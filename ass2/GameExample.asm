.include "../framework.asm"

.name "SNAKE"

.ramsection "Vars" slot 1
	buttons db	; uint8 buttons;
	desiredDir db	; uint8 desiredDir;
	dir db		; uint8 dir;
	headX db	; uint8 headX;
	headY db	; uint8 headY;
	field dsb 32*32	; uint8[32*32] field;
	tail dsb 256*2	; uint8[256*2] tail;
	length db	; uint8 length;
	delay db	; uint8 delay;
.ends

.define DELAY 8

.section "main"
main:
	; enableSound();
	call enableSound

	; setVolume(7);
	ld a, 7
	call setVolume

	; clearVRAM();
	call clearVRAM

	; delay = DELAY
	ld a, DELAY
	ld (delay), a
	; headX = 10
	ld a, 10
	ld (headX), a
	; headY = 10
	ld a, 10
	ld (headY), a
	; length = 4;
	ld a, 4
	ld (length), a
	; desiredDir = 1;
	ld a, 1
	ld (desiredDir), a

	; memset(tail, 0x1f, sizeof(tail));
	ld hl, tail
	ld a, $1f
	ld de, 256*2
	call memset

	; loadTiles(tiles, 1, 6);
	ld hl, tiles
	ld a, 1
	ld c, 6
	call loadTiles1bpp

	; createFruit();
	call createFruit

	; while (true) {
-:
	; if (buttons >> 4 != 0 && ((buttons >> 4) & ((dir & 0xa) >> 1)) == 0 && ((buttons >> 4) & ((dir & 5) << 1)) == 0)
	; 	desiredDir = buttons >> 4;
	ld a, (buttons)
	swap a
	and $f
	jr z, +
	ld c, a
	ld a, (dir)
	and $a
	rrca
	and c
	jr nz, +
	ld a, (dir)
	and 5
	rlca
	and c
	jr nz, +
	ld a, (buttons)
	swap a
	and $f
	ld (desiredDir), a
+:

	; waitVBlank();
	call waitVBlank

	; if (--delay == 0) {
	ld a, (delay)
	dec a
	ld (delay), a
	jr nz, +

	; dir = desiredDir;
	ld a, (desiredDir)
	ld (dir), a

	; updatePosition();
	call updatePosition

	; delay = DELAY;
	ld a, DELAY
	ld (delay), a

	; }
+:

	; if (delay == DELAY - 1)
	; 	checkCollisions();
	ld a, (delay)
	cp DELAY - 1
	jr nz, +
	call checkCollisions
+:

	; buttons = ~getButtonStates();
	call getButtonStates
	cpl
	ld (buttons), a

	; }
	jr -
.ends

; void updatePosition()
.section "updatePosition"
updatePosition:
	push bc
	push de
	ld b, 0
	ld a, (length)
	dec a
	ld c, a
	ld hl, tail
	add hl, bc
	add hl, bc
	ldi a, (hl)
	ld c, a
	ld a, (hl)
	ld b, a
	xor a
	call setField
	push hl
	pop de
	dec hl
	dec hl
	ld a, (length)
	dec a
	ld c, a
-:
	push bc
	ldd a, (hl)
	ld (de), a
	dec de
	ldd a, (hl)
	ld (de), a
	dec de
	pop bc
	dec c
	jr nz, -
	ld a, (headY)
	ld b, a
	ld (tail + 1), a
	ld a, (headX)
	ld c, a
	ld (tail), a
	ld a, 1
	call setField
	ld a, (dir)
	and $8
	jr z, +
	ld a, (headY)
	inc a
	ld (headY), a
	ld b, a
	ld a, (headX)
	ld c, a
	ld a, 5
	call setField
	jr ++
+:
	ld a, (dir)
	and $4
	jr z, +
	ld a, (headY)
	dec a
	ld (headY), a
	ld b, a
	ld a, (headX)
	ld c, a
	ld a, 4
	call setField
	jr ++
+:
	ld a, (dir)
	and $2
	jr z, +
	ld a, (headY)
	ld b, a
	ld a, (headX)
	dec a
	ld (headX), a
	ld c, a
	ld a, 3
	call setField
	jr ++
+:
	ld a, (headY)
	ld b, a
	ld a, (headX)
	inc a
	ld (headX), a
	ld c, a
	ld a, 2
	call setField
++:
	pop de
	pop bc
	ret
.ends

; void checkCollisions()
.section "checkCollisions"
checkCollisions:
	push bc
	push hl
	ld a, (headY)
	ld b, a
	cp 144 / 8
	call nc, handleCollision
	ld a, (headX)
	ld c, a
	cp 160 / 8
	call nc, handleCollision
	ld l, b
	ld h, 0
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	ld b, 0
	add hl, bc
	ld bc, field
	add hl, bc
	ld a, (hl)
	cp 1
	call z, handleCollision
	pop hl
	pop bc
	ret
.ends

; void handleCollision()
.section "handleCollision"
handleCollision:
	ld a, $33
	call playNoise
	jp initGB
.ends

; void collectFruit()
.section "collectFruit"
collectFruit:
	ld a, (length)
	inc a
	ld (length), a
	ld a, $20
	call playNoteOnChannel1
	call createFruit
	ret
.ends

; void createFruit()
.section "createFruit"
createFruit:
	push bc
	push hl
-:
	call rand
	push af
	and $f
	ld c, a
	pop af
	swap a
	and $f
	ld b, a
	push bc
	ld l, b
	ld h, 0
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	ld b, 0
	add hl, bc
	ld bc, field
	add hl, bc
	ld a, (hl)
	or a
	jr z, +
	pop bc
	jr -
+
	pop bc
	ld a, 6
	call setField
	pop hl
	pop bc
	ret
.ends

; void setField(uint8 value, uint8 x, uint8 y)
.section "setField"
setField:
	push hl
	push bc
	ld l, b
	ld h, 0
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	ld b, 0
	add hl, bc
	ld bc, field
	add hl, bc
	or a
	jr z, ++
	push af
	ld a, (hl)
	cp 1
	jr nz, +
	pop af
	jr +++
+:
	cp 6
	jr nz, +
	call collectFruit
+:
	pop af
++:
	ld (hl), a
+++:
	pop bc
	call setTile
	pop hl
	ret
.ends

.section "tiles"
tiles:
.db %11111111
.db %11111111
.db %11111111
.db %11111111
.db %11111111
.db %11111111
.db %11111111
.db %11111111

.db %10000000
.db %11000000
.db %11100000
.db %11110000
.db %11110000
.db %11100000
.db %11000000
.db %10000000

.db %00000001
.db %00000011
.db %00000111
.db %00001111
.db %00001111
.db %00000111
.db %00000011
.db %00000001

.db %00000000
.db %00000000
.db %00000000
.db %00000000
.db %00011000
.db %00111100
.db %01111110
.db %11111111

.db %11111111
.db %01111110
.db %00111100
.db %00011000
.db %00000000
.db %00000000
.db %00000000
.db %00000000

.db %00000000
.db %00011000
.db %00111100
.db %01111110
.db %01111110
.db %00111100
.db %00011000
.db %00000000
.ends
