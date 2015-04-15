'''
Created on 15-04-2015

@author: Marcial
'''

import gzip
import json as simplejson

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
    contador=0
    f = open("jsonFile.txt","w")
    for e in parse("Movies_&_TV.txt.gz"):
        if contador==0:
            f.write("[\n")
        #print simplejson.dumps(e, sort_keys=True,indent=4, separators=(',', ': '))
        f.write(simplejson.dumps(e , sort_keys=True,indent=4, separators=(',', ': ')))
        if contador==500000:
            f.write("\n]")
            print "listo!!"
            break
        f.write(",\n")
        contador+=1