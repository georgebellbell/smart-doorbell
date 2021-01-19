from gpiozero import *
from time import sleep
from picamera import PiCamera  # This library installed on the raspberry pi by default
import socket
import base64
import os
import Crop
from multiprocessing import Process
import SocketListener
import re


class main:
	def __init__(self):
		# Get file path
		self.cwd = os.getcwd() + "/"
		self.fileLocation = self.cwd + __file__[:-7]
		self.photoPath = self.fileLocation + "photo.jpg"

		# GPIO pin setup
		self.led1 = LED(10)
		self.button1 = Button(15)
		self.led2 = LED(27)
		self.led2.on()

		self.button2 = Button(3)

		# Socket Client
		self.host, self.port = "192.168.1.123", 4444

		# Read the Raspberry Pi's unique ID from a file (ID assigned at factory)
		with open(self.fileLocation + "PiID.txt", "r") as file:
			self.PiId = file.readline()
			print("Unique Device ID: " + self.PiId)


	def main(self):
		# Poll server in separate process
		Process(target=self.socketPoll).start()
		# Main loop
		while True:
			if self.button1.is_pressed:
				self.led1.on()
				# Delay before capture after pressing them button
				sleep(0.5)

				# Take a photo
				try:
					self.capture()
					print("Captured photo")
				except Exception as e:
					print("Failed to capture photo")

				# Crop the faces from the image
				faces = Crop.main(self.photoPath)

				if faces == 0:
					print("ERROR - No faces found")
					self.led1.off()
				else:
					print(str(faces) + " Face(s) found")

					# Send faces to the server
					try:
						for n in range(faces):
							self.sendImage(n)
							print("Image " + str(n) + " sent to server")

						# Flash led to show picture has finished being sent
						self.led1.off()
						sleep(1)
						self.led1.on()
						# Delay before being able to be ring the doorbell again
						sleep(2)  # 20

					except Exception as e:
						import traceback
						traceback.print_exc()
						print("Failed to send image to server")

					finally:
						self.led1.off()

	# Take picture
	def capture(self):
		camera = PiCamera()
		camera.capture(self.photoPath)
		camera.close()

	# Send the photo to the socket server
	def sendImage(self, n):
		imageData = self.getImage(n)
		output = '{"request":"image","id":"' + self.PiId + '","data":"' + imageData + '"}\r\n'

		self.socketSend(output)
		#Process(target=self.socketSend, args=(output,)).start()

		return

	# Get the photo from the filesystem and return it in the correct format
	def getImage(self, n):
		with open(self.fileLocation + str(n) + ".jpg", "rb") as image:
			imageData = str(base64.encodebytes(image.read()))[2:-3]

		return imageData
		
	def decodeResponse(self, response):
		# encode response as a string
		response = str(response, "utf-8")
		# Remove unwanted charachters from the response
		response = re.sub('[{}"\r\n]', '', response)
		
		decodedResponse = []
		# split response by ":"
		for item in response.split(",", 1):
			item = item.split(":")
			decodedResponse.append(item)
		
		# remove squre brackets from message contents
		if "[" in decodedResponse[1][1]:
			decodedResponse[1][1] = decodedResponse[1][1][1:-1]
		
		# split message contents into a list
		if "," in decodedResponse[1][1]:
			decodedResponse[1][1] = decodedResponse[1][1].split(",")

		return decodedResponse

	def openCheck(self, response):
		# Check that response message is not empty
		if response[1][1] != "none":
			# check if message contents contains a list
			if len(response[1][1][0]) == 1:
				# check if "non list" message contents is "open"
				if response[1][1].lower() == "open": return True
			else:
				# check all items in message contents to see if any say "open"
				for n in range(len(response[1][1])):
					if response[1][1][n].lower() == "open": return True
					
		return False

	def socketSend(self, output):
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		s.connect((self.host, self.port))
		s.sendall(bytes('doorbell\r\n', 'utf-8'))
		s.sendall(bytes(output, 'utf-8'))
		data = s.recv(1024)
		s.close()
		print("Received", repr(data))
		return data

	def socketPoll(self):
		# Poll server forever
		# Check to see if there has been a request to open the door
		while True:
			try:
				s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
				s.settimeout(10)
				s.connect((self.host, self.port))
				s.settimeout(None)
				s.sendall(bytes('doorbell\r\n', 'utf-8'))
				s.sendall(bytes('{"request":"poll", "id":"' + self.PiId + '"}\r\n', 'utf-8'))
				data = s.recv(1024)
				s.close()
				# handle response from server
				response = self.decodeResponse(data)
				#print(response)
				if self.openCheck(response):
					print("OPEN DOOR")
									
			except Exception as e:
				print(e)
			sleep(5)
			

if __name__ == "__main__":
	# p1 = Process(target=SocketListener.runServer, args=(LHost, LPort))
	# p1.start()

	doorbell = main()
	doorbell.main()
	

