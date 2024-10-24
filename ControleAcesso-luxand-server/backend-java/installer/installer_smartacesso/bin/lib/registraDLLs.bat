@ECHO off

ECHO .
ECHO .
ECHO Copiando e registrando bibliotecas...
ECHO .

ECHO .
ECHO Copiando "%~dp0\facesdk.dll" para "%systemroot%\SysWow64\"
COPY /y "%~dp0\facesdk.dll" "%systemroot%\SysWow64\"
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
apache\bin\httpd.exe -k install

ECHO Iniciando serviços do sistema...
apache\bin\httpd.exe -k start

ECHO Instalando backend...
SmartFaces.exe install
ECHO Iniciando backend...
SmartFaces.exe start

ECHO .
ECHO .
ECHO .
ECHO TERMINADO!
