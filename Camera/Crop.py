import cv2


def crop(image, faces):
	results = []
	# Get image resolution
	resY, resX, c = image.shape
	
	# iterate over all faces found in the image
	for (x, y, w, h) in faces:
		# Crop out a section where a face was detected
		
		# Adjust image crop in Y axis
		if int(round(y - 0.3*h)) < 0:
			# Top of head is cut off
			h = int(round(1.5*h + (y-0.3*h)))
			y = 0
		else:
			# Head intact
			y = int(round(y - 0.3*h))
			h = int(round(1.5*h))

		# make sure that crop doesn't go past the end of the image
		if h > resY:
			h = resY
		
		# Adjust image crop in X axis
		if int(round(x-0.1*w)) < 0:
			# cut off
			w = int(round(w*1.2 + (x-0.1*w)))
			x = 0
		else:
			# intact
			x = int(round(x-w*0.1))
			w = int(round(w*1.2))
			
		# make sure that crop doesn't go past the end of the image
		if w > resX:
			w = resX
		
		imageSection = image[y:y + h, x:x + w]
		results.append(imageSection)

	return results


def identifyFaces(image):
	# Make the image grayscale and equalise the histogram to work better for face identification
	grayscale = cv2.equalizeHist(cv2.cvtColor(image, cv2.COLOR_BGR2GRAY))
	# Pre trained machine learning algorithm to detect faces from the opencv library
	faceCascade = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
	# Get the coordinates of the faces from the image
	# minSize 130 x 130 is slightly over 2m from the doorbell at 1080p
	faces = faceCascade.detectMultiScale(grayscale, scaleFactor=1.3, minNeighbors=3, minSize=(130, 130))

	return faces


def main(imagePath):
	# Read image from file path
	image = cv2.imread(imagePath)

	# Extract the faces
	faces = identifyFaces(image)
	crops = crop(image, faces)
	for n in range(len(crops)):
		# Save the section containing the face to a file
		cv2.imwrite(imagePath[:-9](str(n) + ".jpg"), crops[n])

	return len(crops)
