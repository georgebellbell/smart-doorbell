import numpy
import cv2
import sys


class Crop:
	def __init__(self, imagePath):
		self.imagePath = imagePath
		self.image = cv2.imread(self.imagePath)
		self.faces = self.identifyFaces(self)
		Crop(self)

	def Crop(self):
		# iterate over all faces found in the image
		for (x, y, w, h) in self.faces:
			# Crop out a section where a face was detected
			imageSection = self.image[y:y + h, x:x + w]
			# Save the section
			cv2.imwrite(str(w) + str(h) + "_faces.jpg", imageSection)

		return len(self.faces)

	def identifyFaces(self):
		grayscale = cv2.cvtColor(self.image, cv2.COLOR_BGR2GRAY)
		faceCascade = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
		faces = faceCascade.detectMultiScale(grayscale, scaleFactor=1.3, minNeighbors=3, minSize=(30, 30))

		print(len(faces), faces)

		return faces
