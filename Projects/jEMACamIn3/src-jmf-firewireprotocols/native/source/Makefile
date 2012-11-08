
all: clean compile deploy

clean:
	rm -f stereoApp
	rm -f lib*.so
	sudo rm -f imagem_*.ppm

compile:
	./compile.sh

deploy:
	rm -f ../lib/lib*.so
	cp lib*.so ../lib/.

run:
	sudo ./stereoApp

