import numpy
import cv2
import sys


def crop(image, faces):
	# iterate over all faces found in the image
	for (x, y, w, h) in faces:
		# Crop out a section where a face was detected
		imageSection = image[y:y + h, x:x + w]
		# Save the section
		cv2.imwrite(str(w) + str(h) + "_faces.jpg", imageSection)

	return len(faces)


def identifyFaces(image):
	# Make the image grayscale to work better for face identification
	grayscale = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
	# Pre trained machine learning algorithm to detect faces from the opencv library
	faceCascade = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
	# Get the coordinates of the faces from the image
	faces = faceCascade.detectMultiScale(grayscale, scaleFactor=1.3, minNeighbors=3, minSize=(30, 30))

	print("Found", len(faces), "face(s)", faces)
	return faces


def main(imagePath):
	# Read image from path
	image = cv2.imread(imagePath)

	faces = identifyFaces(image)
	return crop(image, faces)
