import socketserver
from multiprocessing import Process
import os


class SocketListener(socketserver.BaseRequestHandler):
	def handle(self):
		# receive request
		self.data = self.request.recv(1024).strip()
		print(self.client_address[0], self.data)
		# respond to request
		self.request.sendall(self.data)


def runServer(host, port):
	print(os.getpid())
	with socketserver.TCPServer((host, port), SocketListener) as server:
		server.serve_forever()


if __name__ == "__main__":
	host, port = "localhost", 4445

	p = Process(target=runServer, args=(host, port))
	p.start()
	print(os.getpid())


