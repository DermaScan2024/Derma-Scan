from Cliente import Client # type: ignore
from multiprocessing import connection
import socket
import threading
import os
os.system("cls")
os.system("color 5")
print("▄██████░▄█████░█████▄░██░░░██░▄█████░█████▄")
print("██░░░░░░██░░░░░██░░██░██░░░██░██░░░░░██░░██")
print("▀█████▄░█████░░█████▀░██░░░██░█████░░█████▀")
print("░░░░░██░██░░░░░██░░██░██░░██░░██░░░░░██░░██")
print("██████▀░▀█████░██░░██░▀███▀░░░▀█████░██░░██")
print("░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░")

def start_server():
    server = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    server.bind(('localhost', 8000))
    server.listen(1)
    print(">>>>>>>>>> The server is ready <<<<<<<<<<<")
    while True:
        connection, address = server.accept()
        cliente = Client(connection,address)
        thread = threading.Thread(target=cliente.run_process())
        thread.start()
        
        
if __name__ == "__main__":
    while True:
        try:
            start_server()
        except Exception as e :
            print("Error : " + str(e))
    