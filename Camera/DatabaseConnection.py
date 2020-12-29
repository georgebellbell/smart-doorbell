import jpysocket

host='192.168.1.106'
port="4444"
socket = jpysocket.jpysocket()  # Create Socket
socket.bind((host, port))
socket.listen(5)
print("Socket is listening")

connection, address = socket.accept()

print("Connected To ", address)
msgsend = jpysocket.jpysocket("Raspberry Pi Says Hi")
connection.send(msgsend)

msgrecv = connection.recv(1024)
msgrecv = jpysocket.jpydecode(msgrecv)
print(msgrecv)
socket.close()
print("end")