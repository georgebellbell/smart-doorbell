import numpy
import cv2
import sys


class Crop:
	def __init__(self, imagePath):
		# Prepare variables
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
		# Make the image grayscale to work better for face identification
		grayscale = cv2.cvtColor(self.image, cv2.COLOR_BGR2GRAY)
		# Pre trained machine learning algorithm to detect faces from the opencv library
		faceCascade = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
		# Get the coordinates of the faces from the image
		faces = faceCascade.detectMultiScale(grayscale, scaleFactor=1.3, minNeighbors=3, minSize=(30, 30))

		print("Found", len(faces), "face(s)", faces)
		return faces
