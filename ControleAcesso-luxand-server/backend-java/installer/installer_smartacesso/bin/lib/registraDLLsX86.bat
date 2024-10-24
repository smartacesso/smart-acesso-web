@ECHO off

ECHO .
ECHO .
ECHO Copiando e registrando bibliotecas...
ECHO .

ECHO .
ECHO Copiando "%~dp0\facesdk.dll" para "%systemroot%\system32\"
COPY /y "%~dp0\facesdk.dll" "%systemroot%\system32\"
IF '%errorlevel%' NEQ '0' (
	ECHO Erro ao copiar
) ELSE (
	ECHO OK!
)

ECHO .
ECHO .
ECHO Instalando e iniciando serviços
ECHO .

ECHO Instalando os serviços do sistema...
cd %~dp0
cd ..
apachex86\bin\httpd.exe -k install

ECHO Iniciando serviços do sistema...
apachex86\bin\httpd.exe -k start

ECHO Instalando backend...
SmartFaces-x86.exe install
ECHO Iniciando backend...
SmartFaces-x86.exe start

ECHO .
ECHO .
ECHO .
ECHO TERMINADO!
