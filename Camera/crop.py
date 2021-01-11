import numpy
import cv2
import sys

class crop:
	def __init__(self, imagePath):
		self.imagePath = imagePath
		self.image = cv2.imread(self.imagePath)

	def crop(self):
		grayscale = cv2.cvtColor(self.image, cv2.COLOR_BGR2GRAY)
		faceCascade = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
		faces = faceCascade.detectMultiScale(grayscale, scaleFactor=1.3, minNeighbors=3, minSize=(30, 30))

		print(len(faces), faces)
