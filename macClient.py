import sys
import time
from threading import *
import select
import socket
import os
import sys

if sys.version_info[0] < 3:
	print("BELOW VERSION 3")
	sys.exit(1)

def waitForJavaInput():
	result = ""
	while result == "":
		result = input("")
	return 

scoutName = waitForJavaInput()
print("Scout name received = " + scoutName)

address = waitForJavaInput()
print("Address= " + address)

print("Looking for scouting server...")

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
	sock.connect((address, 28590))
	print("Sock: " + str(sock.getsockname()))
	sock.send(scoutName.encode("utf-8"))
except TypeError as err:
    	print ("failed to connect ")
    	print ("Error: ", err)
    	sys.exit(1)

while True:
	consoleInput = waitForJavaInput()
	print("Input is: " + consoleInput)
	msgs = consoleInput.split("^")
	for msg in msgs:
        		if (not(msg is "")):
		        	if (msg == "SEND_SCOUTING_DATA"):
		        		file = open(os.path.dirname(os.path.realpath(__file__)) + "/unsentData/scoutingData.csv", "r")
		        		data = file.read()
		        		file.close()
		        		print("Sending: " + data)
		        		data = data + "^"
		        		sock.send(data.encode("utf-8"))
		        		print("Passed to socket...")
		        		millis = int(round(time.time() * 1000))
		        		newFile = open(os.path.dirname(os.path.realpath(__file__)) + "/" + str(millis) + ".csv", "w")
		        		newFile.write(data)
		        		newFile.close()
		        		print ("Scouting Data Sent")
		        		os.remove(os.path.dirname(os.path.realpath(__file__)) + "/unsentData/scoutingData.csv")
		        	elif (msg != "READ_ONLY"):
		        		msg = msg + "^"
		        		sock.send(msg.encode("utf-8"))
		        	else:
		        		try:
			        		ready_to_read, ready_to_write, in_error = select.select([sock], [], [sock], 5)
			        		print(ready_to_read)
			        		print(ready_to_write)
			        		print(in_error)
			        	except select.error:
			        		print ("Error in Python 'Select'")
			        	if len(ready_to_read) > 0:
			        		recv = sock.recv(2048).decode("utf-8")
			        		msgs = recv.split("^")
			        		for msg in msgs:
			        			if (not(msg is "")):
			        				print ("MESSAGE" + msg)