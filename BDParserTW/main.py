import os
'''
Created on 15-04-2015

@author: Marcial
'''

import gzip
import json as simplejson

def CrearDirectorio(rutaNuevoDirectorio):
    try:
        os.makedirs(rutaNuevoDirectorio)
    except OSError:
        if os.path.exists(rutaNuevoDirectorio):
            pass
        # We are nearly safe
        else:
        # There was an error on creation, so make sure we know about it
            raise

def parse(filename):
    f = gzip.open(filename, 'r')
    print "procesando"
    entry = {}
    for l in f:
        l = l.strip()
        colonPos = l.find(':')
        if colonPos == -1:
            yield entry
            entry = {}
            continue
        eName = l[:colonPos]
        rest = l[colonPos+2:]
        entry[eName] = rest
    yield entry

if __name__ == '__main__':
    #limites
    cantidadJsonsPorArchivo=5000
    cantidadArchivosACrear=20
    ##########################
    contadorArchivosDiferentes=0
    contadorLectura=0
    CrearDirectorio("bd")
    f = open("bd/jsonFile"+str(contadorArchivosDiferentes)+".txt","w")
    for e in parse("Movies_&_TV.txt.gz"):
        if contadorLectura==0:
            f.write("[\n")
            
        f.write(simplejson.dumps(e , sort_keys=True,indent=4, separators=(',', ': ')))
        f.write(",\n")
        contadorLectura+=1
        
        if contadorLectura==cantidadJsonsPorArchivo:
            contadorLectura=0
            f.write("]")
            
            print "archivo numero "+str(contadorArchivosDiferentes)+" Listo!!"
            contadorArchivosDiferentes+=1
            f.close()
            if contadorArchivosDiferentes==cantidadArchivosACrear:
                break
            else:
                f = open("bd/jsonFile"+str(contadorArchivosDiferentes)+".txt","w")