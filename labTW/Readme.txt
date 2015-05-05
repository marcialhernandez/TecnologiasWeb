Integrantes:

Pedro Rojas Beriestain
Marcial Hernández Sánchez
Jose Miguel Santibañez

----------------------------------------------------------------------------

Explicación:

Proyecto hecho con la IDE netbeans 8.1
para poder ejecutarlo correctamente se necesita por lo menos de Netbeans 7 en adelante (u otra ID de preferencia), y tener jdk-7u80 instalado en el computador, debido a que se trabaja con Lucene 5, que necesita la biblioteca "java.nio.file.Path", y solo se encuentra en el mencionado jdk (o uno posterior a este).Este proyecto lee todos los archivos desde una carpeta llamada "bd" que se debe encontrar en el mismo directorio, y cada archivo lo indexa a un único indice invertido. O sea, con el primer archivo que se encuentre en la carpeta, se crea el indice invertido, y con los demás archivos, solo se agregan documentos al ya existente. El indice invertido se crea de la siguiente forma: lee cada archivo, y de este extrae todos los documentos que contenga (JSONs), una vez que ya los ha extraído todos, sigue con el siguiente archivo, y así hasta que no quede ninguno.

-----------------------------------------------------------------------------

Se recomienda importar proyecto usando NetBeans

Github proyecto:

https://github.com/marcialhernandez/TecnologiasWeb