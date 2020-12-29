from gpiozero import *
from time import sleep
from picamera import PiCamera  # This library installed on the raspberry pi by default
import os

# Setup file path for image location
cwd = os.getcwd()
imagePath = cwd + "/photo.jpg"

# GPIO pin setup
led1 = LED(10)
button1 = Button(15)
led2 = LED(27)
led2.on()

button2 = Button(3)


# Take picture
def capture():
	camera = PiCamera()
	camera.capture(imagePath)
	camera.close()


def sendImage():
	image = imageToBytes(imagePath)


def imageToBytes(location):
	with open("img.png", "r") as image:
		b = image.read()
		print(b)


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
