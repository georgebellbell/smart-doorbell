from picamera import PiCamera


def Capture(location = "/home/pi/Desktop/image.jpg"):
    camera = PiCamera()
    camera.capture(location)
    camera.close()
