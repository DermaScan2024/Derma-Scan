from multiprocessing import connection
import socket
import threading
from keras.models import load_model # type: ignore
from keras.layers import DepthwiseConv2D # type: ignore
from PIL import Image, ImageOps
import numpy as np
import os
import datetime
class Client:
    def __init__(self,conn,address):
        self.conn = conn
        self.address = address
        
    def analyze_image(self, name):
        
        np.set_printoptions(suppress=True)

        class ModifiedDepthwiseConv2D(DepthwiseConv2D):
            def __init__(self, **kwargs):
                kwargs.pop('groups', None)  
                super(ModifiedDepthwiseConv2D, self).__init__(**kwargs)

        
        model = load_model("keras_model.h5", custom_objects={'DepthwiseConv2D': ModifiedDepthwiseConv2D}, compile=False)

        names_class = open("labels.txt", "r").readlines()

        datas = np.ndarray(shape=(1, 224, 224, 3), dtype=np.float32)

        image = Image.open(name).convert("RGB")

        size = (224, 224)
        image = ImageOps.fit(image, size, Image.Resampling.LANCZOS)

        array_image = np.asarray(image)

        array_image_normalizado = (array_image.astype(np.float32) / 127.5) - 1

        datas[0] = array_image_normalizado

        forecast = model.predict(datas)
        index = np.argmax(forecast)
        name_class = names_class[index]
        trust_score = forecast[0][index]

        probability_cancer = forecast[0][1]  
        probability_cancer_formatada = round(probability_cancer * 100, 2)
        
        time = datetime.datetime.now()
        print("[{}:{}:{}][Classified with: {}]".format(time.hour, time.minute, time.second, name_class[2:].strip("\n")))
        print("[{}:{}:{}][Trust Score: {}]".format(time.hour, time.minute, time.second, trust_score))
        image.close()
        return f"{probability_cancer_formatada}"

    @staticmethod
    def extract_number_file(filename):
        filename = filename.split(".")[0]
        return int(filename)
    
    def history(self, ID):
        destiny = ID
        extensions = ["jpeg","jpg","png","bmp","gif","tiff","tif","ico" ,"webp","pbm","pgm","ppm"]
        
        if os.path.isdir(destiny):
            os.chdir(destiny)
            files_name = os.listdir(os.getcwd())
            names_selecteds = []
            for names in files_name:
                parts_in_name = names.split(".")
                for exten in extensions:
                    exten = exten.rstrip()
                    if parts_in_name[len(parts_in_name) - 1] == exten:
                        names_selecteds.append(names)
                        break
            
            names_selecteds = sorted(names_selecteds, key=Client.extract_number_file)
            
            print(names_selecteds)
            
            for selecteds in names_selecteds:
                with open(selecteds,"rb") as file:
                    for data in file.readlines():
                        self.conn.send(data)
                self.conn.send(b'DELIMITER')
                print("Enviado")
                file.close()

            os.chdir("..")
            print(os.listdir(os.getcwd()))
            print("Arquivo enviado!!")
        else:
            self.conn.send("Histórico não encontrado".encode())
            print("Histórico não encontrado")
        self.conn.close()


    def treat_image(self, decode_data, received_data ):
        
        filter_to_infos = decode_data.split("DIVISOR")
        user_ID = filter_to_infos[len(filter_to_infos) - 3]
        extension_image = filter_to_infos[len(filter_to_infos) - 2]
        code_image = received_data.split("DIVISOR".encode())

        print(f"Connection for : {user_ID}")
        
        validFile = False
        

        lines = ["jpeg","jpg","png","bmp","gif","tiff","tif","ico" ,"webp","pbm","pgm","ppm"]
        
        for extensions_valids in lines:
            extensions_valids = extensions_valids.rstrip()
            if extensions_valids == extension_image:
                validFile = True
                
        if validFile == True:
            
            users_registred = os.listdir(os.getcwd())
            name_file = ""
            
            existing_user = False
            
            for users in users_registred:
                if users == user_ID:
                    existing_user = True
                
                
            if existing_user == True:
                os.chdir(user_ID)
                num_files = len(os.listdir(os.getcwd()))
                name_file = str(num_files + 1)
            else:
                os.mkdir(user_ID)
                name_file = str(1)
                    
                    
            os.chdir("C:/Users/Gilberto/Music/TCC/Code")
            
            with open(f"{name_file}.{extension_image}", 'wb') as file:
                file.write(code_image[0])
       
            analysis_result = self.analyze_image(f"{name_file}.{extension_image}")
            
            file.close()

            print(analysis_result)
            self.conn.sendall(analysis_result.encode())
            self.conn.close()
        
            os.rename(f"C:/Users/Gilberto/Music/TCC/Code/{name_file}.{extension_image}", f"C:/Users/Gilberto/Music/TCC/Code/{user_ID}/{name_file}.{extension_image}")
            
        else:
            print("Invalid File") 
            print(extension_image)
            self.conn.sendall("Invalid File".encode())
            self.conn.close()

        

    def run_process(self):
        
        received_data = b''
        while True:
            data = self.conn.recv(1024)
            if not data:
                break
            if data.endswith(b"END_OF_FILE"):
                received_data += data[:-len(b"END_OF_FILE")]
                break
            received_data += data
            
        try:
            decode_data = received_data.decode('utf-8')
        except UnicodeDecodeError:
            decode_data = received_data.decode('utf-8', errors='ignore')
        
        
        if len(received_data) > 100:
            self.treat_image(decode_data,received_data)
        else :
            user_ID = decode_data.split(";")
            self.history(user_ID[0])
    
    

    
    
