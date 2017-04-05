#!/usr/bin/python

import socket

s = socket.socket()
host = 'localhost'
port = 33333

s.connect((host, port))

message = raw_input('Message to send: ')

s.send(message)
print 'Message received from server: ', s.recv(1024)
s.close
