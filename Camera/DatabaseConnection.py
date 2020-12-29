import socket
#192.168.1.106
host = "localhost"
port = 4444

imageData = "hello"  # placeholder
imageSize = str(234234)  # placeholder
PiId = "unique ID"  # placeholder

output = '{"request":"image","id":"' + PiId + '","size":"' + imageSize + '","data":"' + imageData + '"}\r\n'
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((host, port))
    s.sendall(bytes(output, 'utf-8'))
    data = s.recv(1024)

print("Recived", repr(data))
