HEAD = \#!/bin/bash

all: Sender.class Receiver.class
Sender.class: Sender.java
	javac -g Sender.java;\
	echo $(HEAD) >> Sender;\
	echo java Sender \$1 \$2 \$3 \"\$4\" >> Sender

Receiver.class: Receiver.java
	javac -g Receiver.java;\
	echo $(HEAD) >> Receiver;\
	echo java Receiver \"\$1\" \$2 >> Receiver

clean:
	rm *.class;\
	rm Sender;\
	rm Receiver
    
