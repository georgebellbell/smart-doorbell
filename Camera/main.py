from gpiozero import *
from time import sleep
from picamera import PiCamera

# GPIO pin setup
led1 = LED(10)
button1 = Button(15)
led2 = LED(27)
led2.on()


# button2 = Button(3)

# Take picture
def capture(location="/home/pi/Desktop/image.jpg"):
    camera = PiCamera()
    camera.capture(location)
    camera.close()


# Main loop
while True:
    if button1.is_pressed:
        led1.on()
        sleep(0.5)
        capture()
        sleep(0.5)
    else:
        led1.off()
