.DEFINE P1    $FF00
.DEFINE SB    $FF01
.DEFINE SC    $FF02
.DEFINE DIV   $FF04
.DEFINE TIMA  $FF05
.DEFINE TMA   $FF06
.DEFINE TAC   $FF07
.DEFINE IF    $FF0F
.DEFINE NR10  $FF10
.DEFINE NR11  $FF11
.DEFINE NR12  $FF12
.DEFINE NR13  $FF13
.DEFINE NR14  $FF14
.DEFINE NR21  $FF16
.DEFINE NR22  $FF17
.DEFINE NR23  $FF18
.DEFINE NR24  $FF19
.DEFINE NR30  $FF1A
.DEFINE NR31  $FF1B
.DEFINE NR32  $FF1C
.DEFINE NR33  $FF1D
.DEFINE NR34  $FF1E
.DEFINE NR41  $FF20
.DEFINE NR42  $FF21
.DEFINE NR43  $FF22
.DEFINE NR44  $FF23
.DEFINE NR50  $FF24
.DEFINE NR51  $FF25
.DEFINE NR52  $FF26
.DEFINE LCDC  $FF40
.DEFINE STAT  $FF41
.DEFINE SCY   $FF42
.DEFINE SCX   $FF43
.DEFINE LY    $FF44
.DEFINE LYC   $FF45
.DEFINE DMA   $FF46
.DEFINE BGP   $FF47
.DEFINE OBP0  $FF48
.DEFINE OBP1  $FF49
.DEFINE WY    $FF4A
.DEFINE WX    $FF4B
.DEFINE IE    $FFFF

.DEFINE R_P1    $00
.DEFINE R_SB    $01
.DEFINE R_SC    $02
.DEFINE R_DIV   $04
.DEFINE R_TIMA  $05
.DEFINE R_TMA   $06
.DEFINE R_TAC   $07
.DEFINE R_IF    $0F
.DEFINE R_NR10  $10
.DEFINE R_NR11  $11
.DEFINE R_NR12  $12
.DEFINE R_NR13  $13
.DEFINE R_NR14  $14
.DEFINE R_NR21  $16
.DEFINE R_NR22  $17
.DEFINE R_NR23  $18
.DEFINE R_NR24  $19
.DEFINE R_NR30  $1A
.DEFINE R_NR31  $1B
.DEFINE R_NR32  $1C
.DEFINE R_NR33  $1D
.DEFINE R_NR34  $1E
.DEFINE R_NR41  $20
.DEFINE R_NR42  $21
.DEFINE R_NR43  $22
.DEFINE R_NR44  $23
.DEFINE R_NR50  $24
.DEFINE R_NR51  $25
.DEFINE R_NR52  $26
.DEFINE R_LCDC  $40
.DEFINE R_STAT  $41
.DEFINE R_SCY   $42
.DEFINE R_SCX   $43
.DEFINE R_LY    $44
.DEFINE R_LYC   $45
.DEFINE R_DMA   $46
.DEFINE R_BGP   $47
.DEFINE R_OBP0  $48
.DEFINE R_OBP1  $49
.DEFINE R_WY    $4A
.DEFINE R_WX    $4B
.DEFINE R_IE    $FF

.MEMORYMAP
DEFAULTSLOT 0
SLOT 0 START $0000 SIZE $8000
SLOT 1 START $c000 SIZE $2000
.ENDME

.ROMGBC
.COMPUTEGBCHECKSUM
.COMPUTEGBCOMPLEMENTCHECK
.COUNTRYCODE 1
.CARTRIDGETYPE 0
.RAMSIZE 0
.ROMBANKSIZE $8000
.ROMBANKS 2
.NINTENDOLOGO
.EMPTYFILL $ff

.slot 0
.org 0
.section "RESERVED" force
	rst $38
.ends
.org $38
.section "TRAP" force
-:
	jr -
.ends

.org $40
.section "INT40" force
	push af
	xor a
	ld (flagWaitVBlank), a
	pop af
	reti
.ends
.org $48
.section "INT48" force
	reti
.ends
.org $50
.section "INT50" force
	reti
.ends
.org $58
.section "INT58" force
	reti
.ends
.org $60
.section "INT60" force
	reti
.ends

.org $100
.section "entry" force
	nop
	jp initGB
.ends

.ramsection "OAM" slot 1 align $100
oam dsb 160
.ends

.enum $ff80
portGBtester db
portGBtesterChar db
gameboyType db
flagWaitVBlank db
updateOAM dsb 15
.ende

.section "initGB"
initGB:
	di
	ld sp, $dfff
	cp $11
	ld a, 0
	jr nz, +
	bit 0, b
	ld a, 1
	jr z, +
	ld a, 2
+:
	ld (gameboyType), a
	xor a
	ld hl, $dfff
-:	ldd (hl), a
	bit 6, h
	jr nz, -
	ld hl, $38
	push hl
	ld de, updateOAM
	ld hl, oamDMA
	ld b, endOamDMA-oamDMA
-:	ldi a, (hl)
	ld (de), a
	inc de
	dec b
	jr nz, -
	ld a, %11100100
	ldh (R_BGP), a
	ldh (R_OBP0), a
	ldh (R_OBP1), a
	xor a
-:
	push af
	ld hl, _defaultPalette
	ld c, 1
	call loadPalettes
	pop af
	inc a
	cp $10
	jr c, -
	ld hl, IE
	set 0, (hl)
	ld hl, $ff30
.rept 2
	ld a, $ff
.rept 4
	ldi (hl), a
.endr
	xor a
.rept 4
	ldi (hl), a
.endr
.endr
	ei
	jp main
	
_defaultPalette:
	.dw $7fff $5294 $294A $0000
.ends

.section "oamDMA"
oamDMA:
	ld	a, >oam
	ldh	(R_DMA), a
	ld	a, $28
-:
	dec	a
	jr	nz, -
	ret
endOamDMA:
	nop
.ends

; void waitVBlank()
.section "waitVBlank"
waitVBlank:
	ld a, 1
	ld (flagWaitVBlank), a
-:
	halt
	ld a, (flagWaitVBlank)
	or a
	jr nz, -
	ret
.ends

; void setSpritePosition(uint8 sprite, uint8 x, uint8 y)
.section "setSpritePosition"
setSpritePosition:
	push de
	push hl
	ld hl, oam
	add a
	add a
	ld e, a
	ld d, 0
	add hl, de
	ld (hl), b
	inc hl
	ld (hl), c
	pop hl
	pop de
	ret
.ends

; void setSpriteY(uint8 sprite, uint8 y)
.section "setSpriteY"
setSpriteY:
	push de
	push hl
	ld hl, oam
	add a
	add a
	ld e, a
	ld d, 0
	add hl, de
	ld (hl), c
	pop hl
	pop de
	ret
.ends

; void setSpriteX(uint8 sprite, uint8 x)
.section "setSpriteX"
setSpriteX:
	push de
	push hl
	ld hl, oam
	add a
	add a
	inc a
	ld e, a
	ld d, 0
	add hl, de
	ld (hl), c
	pop hl
	pop de
	ret
.ends

; void setSpriteAttributes(uint8 sprite, uint8 tile, uint8 flags)
.section "setSpriteAttributes"
setSpriteAttributes:
	push de
	push hl
	ld hl, oam
	add a
	add a
	add 2
	ld e, a
	ld d, 0
	add hl, de
	ld (hl), c
	inc hl
	ld (hl), b
	pop hl
	pop de
	ret
.ends

; void setSpriteTile(uint8 sprite, uint8 tile)
.section "setSpriteTile"
setSpriteTile:
	push de
	push hl
	ld hl, oam
	add a
	add a
	add 2
	ld e, a
	ld d, 0
	add hl, de
	ld (hl), c
	pop hl
	pop de
	ret
.ends

; void setSpriteFlags(uint8 sprite, uint8 flags)
.section "setSpriteFlags"
setSpriteFlags:
	push de
	push hl
	ld hl, oam
	add a
	add a
	add 3
	ld e, a
	ld d, 0
	add hl, de
	ld (hl), c
	pop hl
	pop de
	ret
.ends

; void enableSprites()
.section "enableSprites"
enableSprites:
	push hl
	ld hl, LCDC
	set 1, (hl)
	pop hl
	ret
.ends

; void disableSprites()
.section "disableSprites"
disableSprites:
	push hl
	ld hl, LCDC
	res 1, (hl)
	pop hl
	ret
.ends

; void enableTallSprites()
.section "enableTallSprites"
enableTallSprites:
	push hl
	ld hl, LCDC
	set 2, (hl)
	pop hl
	ret
.ends

; void disableTallSprites()
.section "disableTallSprites"
disableTallSprites:
	push hl
	ld hl, LCDC
	res 2, (hl)
	pop hl
	ret
.ends

; void enableWindow()
.section "enableWindow"
enableWindow:
	push hl
	ld hl, LCDC
	set 5, (hl)
	set 6, (hl)
	pop hl
	ret
.ends

; void disableWindow()
.section "disableWindow"
disableWindow:
	push hl
	ld hl, LCDC
	res 5, (hl)
	pop hl
	ret
.ends

; void turnScreenOn()
.section "turnScreenOn"
turnScreenOn:
	push hl
	ld hl, LCDC
	set 7, (hl)
	pop hl
	ret
.ends

; void turnScreenOff()
.section "turnScreenOff"
turnScreenOff:
	push hl
	ld hl, LCDC
	or a
	bit 7, (hl)
	jr z, +
	ldh a, (R_IE)
	push af
	res 0, a
	ldh (R_IE), a
-:
	ldh a, (R_LY)
	cp 145
	jr nz, -
	res 7, (hl)
	pop af
	ldh (R_IE), a
	scf
+:
	pop hl
	ret
.ends

; void setTile(uint8 tile, uint8 x, uint8 y)
.section "setTile"
setTile:
	push hl
	ld l, b
	ld h, 0
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	ld b, $98
	add hl, bc
	push hl
	ld hl, STAT
-:
	bit 1, (hl)
	jr nz, -
	pop hl
	ld (hl), a
	pop hl
	ret
.ends

; void setTiles(uint8[] tiles, uint8 numTiles, uint8 x, uint8 y)
.section "setTiles"
setTiles:
	push de
	ld e, l
	ld d, h
	ld l, b
	ld h, 0
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	ld b, $98
	add hl, bc
	ld c, a
--:
	push hl
	ld hl, STAT
-:
	bit 1, (hl)
	jr nz, -
	pop hl
.rept 2
	ld a, (de)
	inc de
	ldi (hl), a
.endr
	dec c
	jr nz, --
	pop de
	ret
.ends

; void setWindowTile(uint8 tile, uint8 x, uint8 y)
.section "setWindowTile"
setWindowTile:
	push hl
	ld l, b
	ld h, 0
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	ld b, $9c
	add hl, bc
	push hl
	ld hl, STAT
-:
	bit 1, (hl)
	jr nz, -
	pop hl
	ld (hl), a
	pop hl
	ret
.ends

; void setFlags(uint8 flags, uint8 x, uint8 y)
.section "setFlags"
setFlags:
	push af
	ld a, (gameboyType)
	or a
	jr nz, +
	pop af
	ret
+:
	ld a, 1
	ldh ($4f), a
	pop af
	push hl
	ld l, b
	ld h, 0
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	add hl, hl
	ld b, $98
	add hl, bc
	push hl
	ld hl, STAT
-:
	bit 1, (hl)
	jr nz, -
	pop hl
	ld (hl), a
	pop hl
	xor a
	ldh ($4f), a
	ret
.ends

; void setScrollX(uint8 scrollX)
.section "setScrollX"
setScrollX:
	ld (SCX), a
	ret
.ends

; void setScrollY(uint8 scrollY)
.section "setScrollY"
setScrollY:
	ld (SCY), a
	ret
.ends

; void setWindowX(uint8 windowX)
.section "setWindowX"
setWindowX:
	ld (WX), a
	ret
.ends

; void setWindowY(uint8 windowY)
.section "setWindowY"
setWindowY:
	ld (WY), a
	ret
.ends

; void clearVRAM()
.section "clearVRAM"
clearVRAM:
	push hl
	call turnScreenOff
	push af
	ld a, (gameboyType)
	or a
	ld a, 1
	jr nz, +
	xor a
+:
--:
	ldh ($4f), a
	push af
	xor a
	ld hl, $9fff
-:
	ldd (hl), a
	bit 7, h
	jr nz, -
	pop af
	or a
	jr z, +
	dec a
	jr --
+:
	pop af
	jr nc, +
	ld hl, LCDC
	set 7, (hl)
+:
	pop hl
	ret
.ends

; void clearTilemap()
.section "clearTilemap"
clearTilemap:
	push hl
	call turnScreenOff
	push af
	ld a, (gameboyType)
	or a
	ld a, 1
	jr nz, +
	xor a
+:
--:
	ldh ($4f), a
	push af
	xor a
	ld hl, $9bff
-:
	ldd (hl), a
	bit 3, h
	jr nz, -
	pop af
	or a
	jr z, +
	dec a
	jr --
+:
	pop af
	jr nc, +
	ld hl, LCDC
	set 7, (hl)
+:
	pop hl
	ret
.ends

; void clearWindow()
.section "clearWindow"
clearWindow:
	push hl
	call turnScreenOff
	push af
	ld a, (gameboyType)
	or a
	ld a, 1
	jr nz, +
	xor a
+:
--:
	ldh ($4f), a
	push af
	xor a
	ld hl, $9fff
-:
	ldd (hl), a
	bit 2, h
	jr nz, -
	pop af
	or a
	jr z, +
	dec a
	jr --
+:
	pop af
	jr nc, +
	ld hl, LCDC
	set 7, (hl)
+:
	pop hl
	ret
.ends

; void loadTiles1bpp(uint8[] tiles, uint8 destination, uint8 numTiles)
.section "loadTiles1bpp"
loadTiles1bpp:
	push de
	ld e, l
	ld d, h
	swap a
	ld l, a
	and $f
	or $80
	ld h, a
	ld a, l
	and $f0
	ld l, a
--:
.rept 4
	push hl
	ld hl, STAT
-:
	bit 1, (hl)
	jr nz, -
	pop hl
.rept 2
	ld a, (de)
	inc de
	ldi (hl), a
	ldi (hl), a
.endr
.endr
	dec c
	jr nz, --
	pop de
	ret
.ends

; void loadTiles(uint16[] tiles, uint8 destination, uint8 numTiles)
.section "loadTiles"
loadTiles:
	push de
	ld e, l
	ld d, h
	swap a
	ld l, a
	and $f
	or $80
	ld h, a
	ld a, l
	and $f0
	ld l, a
--:
.rept 8
	push hl
	ld hl, STAT
-:
	bit 1, (hl)
	jr nz, -
	pop hl
.rept 2
	ld a, (de)
	inc de
	ldi (hl), a
.endr
.endr
	dec c
	jr nz, --
	pop de
	ret
.ends

; void loadPalettes(uint16[] palettes, uint8 destination, uint8 numPalettes)
.section "loadPalettes"
loadPalettes:
	add a
	add a
	add a
	sub $40
	jr nc, +
	add $40
	or $80
	ldh ($68), a
--:
.rept 4
	push hl
	ld hl, STAT
-:
	bit 1, (hl)
	jr nz, -
	pop hl
.rept 2
	ldi a, (hl)
	ldh ($69), a
.endr
.endr
	dec c
	jr nz, --
	ret
+:
	or $80
	ldh ($6a), a
--:
.rept 4
	push hl
	ld hl, STAT
-:
	bit 1, (hl)
	jr nz, -
	pop hl
.rept 2
	ldi a, (hl)
	ldh ($6b), a
.endr
.endr
	dec c
	jr nz, --
	ret
.ends

.section "tableSoundNoteFrequencies"
tableSoundNoteFrequencies:
	.dw $002C $009D $0107 $016B $01C9 $0223 $0277 $02C7
	.dw $0312 $0358 $039B $03DA $0416 $044E $0483 $04B5
	.dw $04E5 $0511 $053B $0563 $0589 $05AC $05CE $05ED
	.dw $060B $0627 $0642 $065B $0672 $0689 $069E $06B2
	.dw $06C4 $06D6 $06E7 $06F7 $0706 $0714 $0721 $072D
	.dw $0739 $0744 $074F $0759 $0762 $076B $0773 $077B
	.dw $0783 $078A $0790 $0797 $079D $07A2 $07A7 $07AC
	.dw $07B1 $07B6 $07BA $07BE $07C1 $07C5 $07C8 $07CB
	.dw $07CE $07D1 $07D4 $07D6 $07D9 $07DB $07DD $07DF
.ends

; void playNoteOnChannel1(uint8 note)
.section "playNoteOnChannel1"
playNoteOnChannel1:
	push de
	push hl
	ld e, a
	ld d, 0
	ld hl, tableSoundNoteFrequencies
	add hl, de
	add hl, de
	xor a
	ldh (R_NR10), a
	ld a, $f0
	ldh (R_NR12), a
	ld a, $80
	ldh (R_NR11), a
	ldi a, (hl)
	ldh (R_NR13), a
	ld a, (hl)
	or $c0
	ldh (R_NR14), a
	pop hl
	pop de
	ret
.ends

; void setChannel1noteAndVolume(uint8 note, uint8 volume)
.section "setChannel1noteAndVolume"
setChannel1noteAndVolume:
	push de
	push hl
	ld e, a
	ld d, 0
	ld hl, tableSoundNoteFrequencies
	add hl, de
	add hl, de
	xor a
	ldh (R_NR10), a
	ld a, c
	swap a
	and $f0
	ldh (R_NR12), a
	ld a, $80
	ldh (R_NR11), a
	ldi a, (hl)
	ldh (R_NR13), a
	ld a, (hl)
	or $80
	ldh (R_NR14), a
	pop hl
	pop de
	ret
.ends

; void playNoteOnChannel2(uint8 note)
.section "playNoteOnChannel2"
playNoteOnChannel2:
	push de
	push hl
	ld e, a
	ld d, 0
	ld hl, tableSoundNoteFrequencies
	add hl, de
	add hl, de
	ld a, $f0
	ldh (R_NR22), a
	ld a, $80
	ldh (R_NR21), a
	ldi a, (hl)
	ldh (R_NR23), a
	ld a, (hl)
	or $c0
	ldh (R_NR24), a
	pop hl
	pop de
	ret
.ends

; void setChannel2noteAndVolume(uint8 note, uint8 volume)
.section "setChannel2noteAndVolume"
setChannel2noteAndVolume:
	push de
	push hl
	ld e, a
	ld d, 0
	ld hl, tableSoundNoteFrequencies
	add hl, de
	add hl, de
	ld a, c
	swap a
	and $f0
	ldh (R_NR22), a
	ld a, $80
	ldh (R_NR21), a
	ldi a, (hl)
	ldh (R_NR23), a
	ld a, (hl)
	or $80
	ldh (R_NR24), a
	pop hl
	pop de
	ret
.ends

; void playNoteOnChannel3(uint8 note)
.section "playNoteOnChannel3"
playNoteOnChannel3:
	push de
	push hl
	ld e, a
	ld d, 0
	ld hl, tableSoundNoteFrequencies
	add hl, de
	add hl, de
	ld a, $c0
	ldh (R_NR31), a
	ld a, $20
	ldh (R_NR32), a
	ld a, $80
	ldh (R_NR30), a
	ldi a, (hl)
	ldh (R_NR33), a
	ld a, (hl)
	or $c0
	ldh (R_NR34), a
	pop hl
	pop de
	ret
.ends

; void setChannel3noteAndVolume(uint8 note, uint8 volume)
.section "setChannel3noteAndVolume"
setChannel3noteAndVolume:
	push de
	push hl
	ld e, a
	ld d, 0
	ld hl, tableSoundNoteFrequencies
	add hl, de
	add hl, de
	ld a, c
	or a
	jr z, ++
	rra
	rra
	cpl
	and 3
	cp 3
	jr nc, +
	inc a
+:
	rla
	swap a
++:
	ldh (R_NR32), a
	ld a, $80
	ldh (R_NR30), a
	ldi a, (hl)
	ldh (R_NR33), a
	ld a, (hl)
	or $80
	ldh (R_NR34), a
	pop hl
	pop de
	ret
.ends

; void playNoise(uint8 parameters)
.section "playNoise"
playNoise:
	ldh (R_NR43), a
	ld a, $f0
	ldh (R_NR42), a
	xor a
	ldh (R_NR41), a
	ld a, $c0
	ldh (R_NR44), a
	ret
.ends

; void enableSound()
.section "enableSound"
enableSound:
	push hl
	ld hl, NR52
	set 7, (hl)
	ld a, $ff
	ldh (R_NR51), a
	pop hl
	ret
.ends

; void disableSound()
.section "disableSound"
disableSound:
	push hl
	ld hl, NR51
	res 7, (hl)
	pop hl
	ret
.ends

; void setVolume(uint8 volume)
.section "setVolume"
setVolume:
	push bc
	and 7
	ld c, a
	swap a
	or c
	ldh (R_NR50), a
	pop bc
ret
.ends

; uint8 getButtonStates()
.section "getButtonStates"
getButtonStates:
	push bc
	ld a, $20
	ldh (R_P1), a
	ldh a, (R_P1)
	ldh a, (R_P1)
	and $f
	swap a
	ld b, a
	ld a, $10
	ldh (R_P1), a
	ldh a, (R_P1)
	ldh a, (R_P1)
	ldh a, (R_P1)
	ldh a, (R_P1)
	ldh a, (R_P1)
	ldh a, (R_P1)
	and $f
	or b
	ld c, a
	ld a, $30
	ldh (R_P1), a
	ld a, c
	pop bc
	ret
.ends

; uint8 rand()
.section "rand"
rand:
	ldh a, (R_DIV)
	ret
.ends

; void memset(uint8[] array, uint8 value, uint16 numBytes)
.section "memset"
memset:
	inc e
	dec e
	jr z, +
	inc d
+:
-:
	ldi (hl), a
	dec e
	jr nz, -
	dec d
	jr nz, -
	ret
.ends

; void memcpy(uint8[] source, uint8[] destination, uint16 numBytes)
.section "memcpy"
memcpy:
	inc c
	dec c
	jr z, +
	inc b
+:
-:
	ldi a, (hl)
	ld (de), a
	inc de
	dec c
	jr nz, -
	dec b
	jr nz, -
	ret
.ends

; void printByte(uint8 var)
; void printBCD(bcd var)
.section "printByte"
printByte:
printBCD:
	ld (portGBtester), a
	ld a, 10
	ld (portGBtesterChar), a
	ret
.ends

; void printSignedByte(int8 var)
.section "printSignedByte"
printSignedByte:
	bit 7, a
	jp z, printByte
	push af
	ld a, '-'
	ld (portGBtesterChar), a
	pop af
	cpl
	inc a
	jp printByte
.ends

; void printWord(uint16 var)
.section "printWord"
printWord:
	ld a, h
	ld (portGBtester), a
	ld a, l
	ld (portGBtester), a
	ld a, 10
	ld (portGBtesterChar), a
	ret
.ends

; void printSignedWord(int16 var)
.section "printSignedWord"
printSignedWord:
	bit 7, h
	jp z, printWord
	ld a, '-'
	ld (portGBtesterChar), a
	ld a, l
	cpl
	ld l, a
	ld a, h
	cpl
	ld h, a
	inc hl
	jp printWord
.ends

; void printChar(uint8 character)
.section "printChar"
printChar:
	ld (portGBtesterChar), a
	ret
.ends

; void printBool(bool var)
.section "printBool"
printBool:
	push hl
	or a
	ld hl, _true
	jr nz, +
	ld hl, _false
+:
	call printStringLn
	pop hl
	ret

_true:
.db "true" 0
_false:
.db "false" 0
.ends

; void printString(uint8[] string)
.section "printString"
printString:
	ldi a, (hl)
	or a
	ret z
	ld (portGBtesterChar), a
	jr printString
.ends

; void printStringLn(uint8[] string)
.section "printStringLn"
printStringLn:
	call printString
	ld a, 10
	ld (portGBtesterChar), a
	ret
.ends

; void printByteArray(uint8[] array, uint16 size)
; void printBCDarray(bcd[] array, uint16 size)
.section "printByteArray"
printByteArray:
printBCDarray:
	ld a, '['
	ld (portGBtesterChar), a
	ld a, e
	or d
	jr z, +
-:
	ldi a, (hl)
	ld (portGBtester), a
	dec de
	ld a, e
	or d
	jr z, +
	ld a, ','
	ld (portGBtesterChar), a
	ld a, ' '
	ld (portGBtesterChar), a
	jr -
+:
	ld a, ']'
	ld (portGBtesterChar), a
	ld a, 10
	ld (portGBtesterChar), a
	ret
.ends

; void printSignedByteArray(int8[] array, uint16 size)
.section "printSignedByteArray"
printSignedByteArray:
	ld a, '['
	ld (portGBtesterChar), a
	ld a, e
	or d
	jr z, ++
-:
	ldi a, (hl)
	bit 7, a
	jr z, +
	push af
	ld a, '-'
	ld (portGBtesterChar), a
	pop af
	cpl
	inc a
+:
	ld (portGBtester), a
	dec de
	ld a, e
	or d
	jr z, ++
	ld a, ','
	ld (portGBtesterChar), a
	ld a, ' '
	ld (portGBtesterChar), a
	jr -
++:
	ld a, ']'
	ld (portGBtesterChar), a
	ld a, 10
	ld (portGBtesterChar), a
	ret
.ends

; void printWordArray(uint16[] array, uint16 size, bool bigEndian)
.section "printWordArray"
printWordArray:
	or a
	jr z, _littleEndian
	ld a, '['
	ld (portGBtesterChar), a
	ld a, e
	or d
	jr z, _end
-:
	ldi a, (hl)
	ld (portGBtester), a
	ldi a, (hl)
	ld (portGBtester), a
	dec de
	ld a, e
	or d
	jr z, _end
	ld a, ','
	ld (portGBtesterChar), a
	ld a, ' '
	ld (portGBtesterChar), a
	jr -
_end:
	ld a, ']'
	ld (portGBtesterChar), a
	ld a, 10
	ld (portGBtesterChar), a
	ret

_littleEndian:
	ld a, '['
	ld (portGBtesterChar), a
	ld a, e
	or d
	jr z, _end
-:
	inc hl
	ldd a, (hl)
	ld (portGBtester), a
	ldi a, (hl)
	inc hl
	ld (portGBtester), a
	dec de
	ld a, e
	or d
	jr z, _end
	ld a, ','
	ld (portGBtesterChar), a
	ld a, ' '
	ld (portGBtesterChar), a
	jr -
.ends

; void printSignedWordArray(int16[] array, uint16 size, bool bigEndian)
.section "printSignedWordArray"
printSignedWordArray:
	or a
	jr z, _littleEndian
	ld a, '['
	ld (portGBtesterChar), a
	ld a, e
	or d
	jr z, _end
-:
	ldi a, (hl)
	push hl
	ld l, (hl)
	ld h, a
	bit 7, h
	jp z, +
	ld a, '-'
	ld (portGBtesterChar), a
	ld a, l
	cpl
	ld l, a
	ld a, h
	cpl
	ld h, a
	inc hl
+:
	ld a, h
	ld (portGBtester), a
	ld a, l
	ld (portGBtester), a
	pop hl
	inc hl
	dec de
	ld a, e
	or d
	jr z, _end
	ld a, ','
	ld (portGBtesterChar), a
	ld a, ' '
	ld (portGBtesterChar), a
	jr -
_end:
	ld a, ']'
	ld (portGBtesterChar), a
	ld a, 10
	ld (portGBtesterChar), a
	ret

_littleEndian:
	ld a, '['
	ld (portGBtesterChar), a
	ld a, e
	or d
	jr z, _end
-:
	ldi a, (hl)
	push hl
	ld h, (hl)
	ld l, a
	bit 7, h
	jp z, +
	ld a, '-'
	ld (portGBtesterChar), a
	ld a, l
	cpl
	ld l, a
	ld a, h
	cpl
	ld h, a
	inc hl
+:
	ld a, h
	ld (portGBtester), a
	ld a, l
	ld (portGBtester), a
	pop hl
	inc hl
	dec de
	ld a, e
	or d
	jr z, _end
	ld a, ','
	ld (portGBtesterChar), a
	ld a, ' '
	ld (portGBtesterChar), a
	jr -
.ends

; void printBoolArray(bool[] array, uint16 size)
.section "printBoolArray"
printBoolArray:
	ld a, '['
	ld (portGBtesterChar), a
	ld a, e
	or d
	jr z, ++
-:
	ldi a, (hl)
	push hl
	or a
	ld hl, _true
	jr nz, +
	ld hl, _false
+:
	call printString
	pop hl
	dec de
	ld a, e
	or d
	jr z, ++
	ld a, ','
	ld (portGBtesterChar), a
	ld a, ' '
	ld (portGBtesterChar), a
	jr -
++:
	ld a, ']'
	ld (portGBtesterChar), a
	ld a, 10
	ld (portGBtesterChar), a
	ret

_true:
.db "true" 0
_false:
.db "false" 0
.ends

; void printCharArray(uint8[] array, uint16 size)
.section "printCharArray"
printCharArray:
	ld a, '['
	ld (portGBtesterChar), a
	ld a, e
	or d
	jr z, +
-:
	ldi a, (hl)
	ld (portGBtesterChar), a
	dec de
	ld a, e
	or d
	jr z, +
	ld a, ','
	ld (portGBtesterChar), a
	ld a, ' '
	ld (portGBtesterChar), a
	jr -
+:
	ld a, ']'
	ld (portGBtesterChar), a
	ld a, 10
	ld (portGBtesterChar), a
	ret
.ends

; void printFixed(fixed var)
.section "printFixed"
printFixed:
	push bc
	push de
	push hl
	bit 7, h
	jr z, +
	ld a, '-'
	ld (portGBtesterChar), a
	ld a, l
	cpl
	ld l, a
	ld a, h
	cpl
	ld h, a
	inc hl
+:
	ld a, h
	ld b, 0
-:
	call _divmod10
	push af
	inc b
	ld a, d
	or a
	jr nz, -
-:
	pop af
	add '0'
	ld (portGBtesterChar), a
	dec b
	jr nz, -
	ld a, '.'
	ld (portGBtesterChar), a
-:
	ld h, 0
	call _mul10
	ld a, h
	add '0'
	ld (portGBtesterChar), a
	ld a, l
	or a
	jr nz, -
	ld a, 10
	ld (portGBtesterChar), a
	pop hl
	pop de
	pop bc
	ret
		
_divmod10:
	ld d, -1
-:
	inc d
	sub 10
	jr nc, -
	add 10
	ret
	
_mul10:
	add hl, hl
	ld e, l
	ld d, h
	add hl, hl
	add hl, hl
	add hl, de
	ret
.ends

; void printFixedArray(fixed[] array, uint16 size, bool bigEndian)
.section "printFixedArray"
printFixedArray:
	or a
	jr z, _littleEndian
	ld a, '['
	ld (portGBtesterChar), a
	ld a, e
	or d
	jr z, _end
-:
	ldi a, (hl)
	push hl
	ld l, (hl)
	ld h, a
	call _printFixed
	pop hl
	inc hl
	dec de
	ld a, e
	or d
	jr z, _end
	ld a, ','
	ld (portGBtesterChar), a
	ld a, ' '
	ld (portGBtesterChar), a
	jr -
_end:
	ld a, ']'
	ld (portGBtesterChar), a
	ld a, 10
	ld (portGBtesterChar), a
	ret

_littleEndian:
	ld a, '['
	ld (portGBtesterChar), a
	ld a, e
	or d
	jr z, _end
-:
	ldi a, (hl)
	push hl
	ld h, (hl)
	ld l, a
	call _printFixed
	pop hl
	inc hl
	dec de
	ld a, e
	or d
	jr z, _end
	ld a, ','
	ld (portGBtesterChar), a
	ld a, ' '
	ld (portGBtesterChar), a
	jr -

_printFixed:	
	push bc
	push de
	push hl
	bit 7, h
	jr z, +
	ld a, '-'
	ld (portGBtesterChar), a
	ld a, l
	cpl
	ld l, a
	ld a, h
	cpl
	ld h, a
	inc hl
+:
	ld a, h
	ld b, 0
-:
	call _divmod10
	push af
	inc b
	ld a, d
	or a
	jr nz, -
-:
	pop af
	add '0'
	ld (portGBtesterChar), a
	dec b
	jr nz, -
	ld a, '.'
	ld (portGBtesterChar), a
-:
	ld h, 0
	call _mul10
	ld a, h
	add '0'
	ld (portGBtesterChar), a
	ld a, l
	or a
	jr nz, -
	pop hl
	pop de
	pop bc
	ret
		
_divmod10:
	ld d, -1
-:
	inc d
	sub 10
	jr nc, -
	add 10
	ret
	
_mul10:
	add hl, hl
	ld e, l
	ld d, h
	add hl, hl
	add hl, hl
	add hl, de
	ret
.ends