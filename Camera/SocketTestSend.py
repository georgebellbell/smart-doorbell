import socket


def send():
	host, port = "localhost", 4445

	output = '{"request":"openDoor","id":"00000001","message":"open"}\r\n'

	with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
		s.connect((host, port))
		s.sendall(bytes(output, 'utf-8'))
		data = s.recv(1024)
		print(data)


if __name__ == "__main__":
	send()
