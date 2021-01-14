import os
import re
import sys

# Get arguments
arguments = sys.argv
print(arguments, len(arguments))

# Directory of runOnBoot
cwd = os.getcwd()
fileLocation = (cwd + "/" + __file__)[:-12]
print("Directory of Program", fileLocation)

# Path to file that is to be edited
path = "/etc/rc.local"

# Check that the program is run with sudo
if os.geteuid() != 0:
	print("Permission denied. Must be run with sudo")
	exit()

# Checks that the file exists
if os.path.exists(path):
	# Opens file in read mode
	file = open(path, "r")

	status = "FAILURE"
	add = None

	# Output array
	output = []

	# Go through every line in the file
	for line in file:
		# Check for the end of the file
		if line == "exit 0\n":
			if len(arguments) == 2:
				if str(arguments[1]).lower() == "true":
					add = True
				elif str(arguments[1]).lower == "false":
					add = False
				else:
					print("Program takes up to 1 argument. true or false \nAborted")
					add = False

			elif len(arguments) >= 3:
				print("Program takes up to 1 argument. true or false \nAborted")
				add = False
			else:
				add = True

			if add:
				status = "SUCCESS: Run On Boot ENABLED"
				# Add the script to run the camera on boot
				output.append("sudo -u pi python3 " + fileLocation + "/main.py &\n")
				output.append(line)
			else:
				status = "SUCCESS: Run On Boot Dissabled"
				output.append(line)

		elif None != re.search("main\.py", line):
			pass  # Skips adding the old script to the file
		else:
			output.append(line)
	# Close the file
	file.close()

	# Open the file in write mode
	file = open(path, "w")
	# Overwrite the file with the new contents
	for line in output:
		file.write(line)
	# Close the file
	file.close()

	# Print error if it failed to find exit 0 in the file
	
	if status == "FAILURE":
		print("ERROR: EOF not found")
	print(status)
	
else:
	# Output error if the file doesn't exist
	print("ERROR: File not found")
