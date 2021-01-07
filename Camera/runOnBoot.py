import os
import re

# Current working directory
cwd = os.getcwd()

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

	flag = False

	# Output array
	output = []

	# Go through every line in the file
	for line in file:
		# Check fo the end of the file
		if line == "exit 0\n":
			flag = True
			# Add the script to run the camera on boot
			output.append("sudo python3 " + cwd + "/main.py &\n")
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
	if flag:
		print("Success")
	else:
		print("ERROR: EOF not found")
else:
	# Output error if the file doesn't exist
	print("ERROR: File not found")
