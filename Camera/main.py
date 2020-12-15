from gpiozero import *
from cam import Capture
from time import sleep

# GPIO pin setup
led1 = LED(10)
button1 = Button(15)
led2 = LED(27)
led2.on()

#button2 = Button(3)


# Main loop
while True:
    if button1.is_pressed:
        led1.on()
        Capture()
        sleep(1)
    else:
        led1.off()






