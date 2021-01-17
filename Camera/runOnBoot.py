import os
import re
import sys


class runOnBoot:
	def __init__(self):
		# Get arguments
		self.arguments = sys.argv
		print(self.arguments, len(self.arguments))

		# Directory of runOnBoot
		self.cwd = os.getcwd()
		self.fileLocation = (self.cwd + "/" + __file__)[:-12]
		print("Directory of Program", self.fileLocation)

		# Path to file that is to be edited
		self.path = "/etc/rc.local"

		# Check that the program is run with sudo
		if os.geteuid() != 0:
			print("ERROR: Permission denied. Must be run with sudo")
			exit()

		# Output error if the file doesn't exist
		if not os.path.exists(self.path):
			print("ERROR: File not found")
			exit()

	def runOnBoot(self):
		output = self.readFile()
		self.updateFile(output)

	def readFile(self):
		# Opens file in read mode
		file = open(self.path, "r")

		output = []
		for line in file:
			# Check for the end of the file
			if line == "exit 0\n":
				if len(self.arguments) == 2:
					if str(self.arguments[1]).lower() == "true":
						print("arg - true")
						add = True
					elif str(self.arguments[1]).lower == "false":
						print("arg - false")
						add = False
					else:
						print("arg - unknown")
						add = False

				elif len(self.arguments) >= 3:
					print("Too many arguments")
					add = False
				else:
					print("arg - none")
					add = True

				if add:
					print("Run On Boot ENABLED")
					# Add the script to run the camera on boot
					output.append("sudo -u pi python3 " + self.fileLocation + "/main.py &\n")
					output.append(line)
				else:
					print("Run On Boot Disabled")
					output.append(line)

			elif None != re.search("main\.py", line):
				pass  # Skips adding the old script to the file
			else:
				output.append(line)

		# Close the file
		file.close()

		return output

	def updateFile(self, output):
		# Open the file in write mode
		file = open(self.path, "w")
		# Overwrite the file with the new contents
		for line in output:
			file.write(line)
		# Close the file
		file.close()


if __name__ == "__main__":
	runOnBoot().runOnBoot()
