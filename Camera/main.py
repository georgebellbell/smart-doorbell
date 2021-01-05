from gpiozero import *
from time import sleep
from picamera import PiCamera  # This library installed on the raspberry pi by default
import os
import socket

# Setup file path for image location
cwd = os.getcwd()
imagePath = cwd + "/photo.jpg"

# GPIO pin setup
led1 = LED(10)
button1 = Button(15)
led2 = LED(27)
led2.on()

button2 = Button(3)

# Socket
host = "192.168.1.123"
port = 4444

imageSize = str(123456789)  # placeholder
PiId = "unique ID"  # placeholder


# Take picture
def capture():
	camera = PiCamera()
	camera.capture(imagePath)
	camera.close()


def sendImage():
	imageData = getImage()
	output = '{"request":"image","id":"' + PiId + '","size":"' + imageSize + '","data":"' + imageData + '"}\r\n'
	with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
		s.connect((host, port))
		s.sendall(bytes(output, 'utf-8'))
		data = s.recv(1024)

	print("Recived", repr(data))
	return


def getImage():
	with open("photo.jpg", "rb") as image:
		imageData = str(image.read(), "utf-8")

	return imageData


# Main loop
while True:
	if button1.is_pressed:
		led1.on()
		sleep(0.5)
		capture()
		sleep(0.5)
		sendImage()

	else:
		led1.off()

	if button2.is_pressed:
		exit()
