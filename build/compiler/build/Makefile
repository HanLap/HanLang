gb: main.gb

main.gb: main.o linkfile
	../wlalink -d -r -v -s linkfile main.gb 2> wlalink-log.txt || (cat wlalink-log.txt | grep -v DISCARD && false)
# 	grep --color=never BANK < wlalink-log.txt 2> /dev/null
	rm wlalink-log.txt

main.o: main.asm
	../wla-gb main.asm

linkfile:
	echo "[objects]" > linkfile
	echo "main.o" >> linkfile
	
clean:
	rm -f main.sym
	rm -f main.o
	rm -f wlalink-log.txt
	rm -f main.gb
	rm -f linkfile
