from gpiozero import *
from time import sleep
from picamera import PiCamera  # This library installed on the raspberry pi by default
import os
import socket
import base64
import Crop

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
host = "192.168.1.122"
port = 4444

# Read the Raspberry Pi's unique ID from a file (ID assigned at factory)
with open("PiID.txt", "r") as file:
	PiId = file.readline()
	print("Unique Device ID: " + PiId)


# Take picture
def capture():
	camera = PiCamera()
	camera.resolution = (512, 384)
	camera.capture(imagePath)
	camera.close()


# Send the photo to the socket server
def sendImage():
	imageData = getImage()
	output = '{"request":"image","id":"' + PiId + '","data":"' + imageData + '"}\r\n'
	with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
		s.connect((host, port))
		s.sendall(bytes('doorbell\r\n', 'utf-8'))
		s.sendall(bytes(output, 'utf-8'))
		data = s.recv(1024)

	print("Recived", repr(data))
	return


# Get the photo from the filesystem and return it in the correct format
def getImage():
	with open("photo.jpg", "rb") as image:
		imageData = str(base64.encodebytes(image.read()))[2:-3]

	return imageData


# Main loop
while True:
	if button1.is_pressed:
		led1.on()
		sleep(0.5)

		# Take a photo
		try:
			capture()
			print("Captured photo")
		except Exception as e:
			print("Failed to capture photo")

		# Crop the faces from the image
		Crop.main(imagePath)

		# Send photo to the server
		try:
			sendImage()
			print("Image sent to server")
			sleep(2)  # 20
		except Exception as e:
			print("Failed to send image to server")
		led1.off()

	# Close the program if button 2 is pressed
	if button2.is_pressed:
		print("Exit")
		exit()
