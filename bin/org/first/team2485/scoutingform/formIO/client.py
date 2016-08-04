from bluetooth import *
import sys
import time
import threading
import select

if sys.version < '3':
    input = raw_input

addr = None
uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
print("Looking for scouting server...")

service_matches = find_service(uuid = uuid, address = addr)

if len(service_matches) == 0:
	print("Could not find Server")
	sys.exit(1)

port = 0
name = 0
host = 0

for x in xrange(0, len(service_matches)):
	match = service_matches[x]
	port = match["port"]
	name = match["name"]
	host = match["host"]

	if name == "Scouting Server":
        		break;

print("Connecting to Port: \"%s\" Name: \"%s\" Host: \"%s\" " % (port, name, host))

sock=BluetoothSocket( RFCOMM )
try:
	sock.connect(host, port)
	scoutName= input("GETNAME")
	sock.send(scoutName)
except:
    	print ("failed to connect - Port: \"%s\" Name: \"%s\" Host: \"%s\" " % (port, name, host))
    	sys.exit()

t=Timer(10.0, receiveFromServer)
t.start()

while True:
	consoleInput = input("I")
	print("Input is: " + consoleInput)
	if consoleInput.find("BROADCAST") != -1:
		sock.send(consoleInput)
	elif consoleInput.find("SendToServer") !=-1:
        		sock.send(consoleInput)
	else:
        		file = open("unsentData/scoutingData.csv", "r")
        		data = file.read()
        		data.close()
        		print("Sending: " + data)
        		sock.send(data)
        		print("Sent")
        		millis = int(round(time.time() * 1000))
        		newFile = open(str(millis) + ".csv", "w")
        		newFile.write(data)
        		newFile.close()


def receiveFromServer():
	try:
        		ready_to_read, ready_to_write, in_error = select.select([sock], [sock], [sock], 1)
	except select.error:
        		print ("Error in Python 'Select'")
        		return
	if ready_to_read >0:
        		recv = conn.recv(2048)
        		print (recv)
	if not (ready_to_read or ready_to_write or in_error):
		print ("no server message")
		t.start()




