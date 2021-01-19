import socket
from time import sleep
import re
from gpiozero import *


class pollServer:

	def __init__(self, host, port, PiId):
		# Connection setup
		self.host, self.port = host, port

		self.PiId = PiId

		# Led pin setup
		self.led2 = LED(27)

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
				self.response = s.recv(1024)
				s.close()
				# handle response from server
				self.response = self.decodeResponse()
				# print(response)
				if self.openCheck():
					print("OPEN DOOR")
					self.led2.on()
					sleep(5)
					self.led2.off()

			except Exception as e:
				print(e)
			sleep(5)

	def decodeResponse(self):
		# encode response as a string
		response = str(self.response, "utf-8")
		# Remove unwanted characters from the response
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

	def openCheck(self):
		response = self.response
		# Check that response message is not empty
		if response[1][1] != "none":
			# check if message contents contains a list
			if len(response[1][1][0]) == 1:
				# check if "non list" message contents is "open"
				if response[1][1].lower() == "open":
					return True
			else:
				# check all items in message contents to see if any say "open"
				for n in range(len(response[1][1])):
					if response[1][1][n].lower() == "open":
						return True

		return False


if __name__ == "__main__":
	pollServer("192.168.1.123", 4444, "00000001").socketPoll()
