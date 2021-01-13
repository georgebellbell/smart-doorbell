from gpiozero import *
from time import sleep
from picamera import PiCamera  # This library installed on the raspberry pi by default
import os
import socket
import base64
import Crop

# Setup file path for image location
cwd = os.getcwd() + "/"
print(__file__[:-7])
photoPath = cwd + "photo.jpg"

# GPIO pin setup
led1 = LED(10)
button1 = Button(15)
led2 = LED(27)
led2.on()

button2 = Button(3)

# Socket
host = "192.168.1.123"
port = 4444

# Read the Raspberry Pi's unique ID from a file (ID assigned at factory)
with open(__file__[:-7] + "PiID.txt", "r") as file:
	PiId = file.readline()
	print("Unique Device ID: " + PiId)


# Take picture
def capture(path):
	camera = PiCamera()
	camera.capture(path)
	camera.close()


# Send the photo to the socket server
def sendImage(path):
	imageData = getImage(path)
	output = '{"request":"image","id":"' + PiId + '","data":"' + imageData + '"}\r\n'
	with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
		s.connect((host, port))
		s.sendall(bytes('doorbell\r\n', 'utf-8'))
		s.sendall(bytes(output, 'utf-8'))
		data = s.recv(1024)

	print("Received", repr(data))
	return


# Get the photo from the filesystem and return it in the correct format
def getImage(path):
	with open(path, "rb") as image:
		imageData = str(base64.encodebytes(image.read()))[2:-3]

	return imageData


# Main loop
while True:
	if button1.is_pressed:
		led1.on()
		sleep(0.5)

		# Take a photo
		try:
			capture(photoPath)
			print("Captured photo")
		except Exception as e:
			print("Failed to capture photo")

		# Crop the faces from the image
		faces = Crop.main(photoPath)

		if faces == 0:
			print("ERROR - No faces found")
			led1.off()
		else:
			print(str(faces) + " Face(s) found")

			# Send faces to the server
			try:
				for n in range(faces):
					sendImage(cwd + str(n) + ".jpg")
					print("Image " + str(n) + " sent to server")

				# Flash led to show picture has finished being sent
				led1.off()
				sleep(1)
				led1.on()
				# Delay before being able to be ring the doorbell again
				sleep(2)  # 20

			except Exception as e:
				print("Failed to send image to server")

			finally:
				led1.off()

	# Close the program if button 2 is pressed
	if button2.is_pressed:
		print("Exit")
		exit()
