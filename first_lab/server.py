#!/usr/bin/python

import socket

s = socket.socket()
host = "localhost"
port = 33333
s.bind((host, port))

s.listen(5)
while True:
	c, addr = s.accept()
	print 'Got connection from', addr
	print 'Message received: ', c.recv(1024)
	answer = raw_input('Type an answer: ')
	c.send(answer)
	c.close
