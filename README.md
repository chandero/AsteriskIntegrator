# AsteriskIntegrator
Integrador de Asterisk con Crm Vtiger y Otros

Marcador Integrador CRM Vtiger con Asterisk 13+

Cómo instalar?

Uso probado -- crear la carpeta /opt/crmintegrator

mkdir -p /opt/crmintegrator cd /opt/crmintegrator mkdir -p bin classes conf db libs logs

-- copiar en la carpeta bin los archivos bin start.sh stop.sh webapp.sh

-- copiar en la carpeta classes los archivos de clases del aplicativo, estos se encuentran en el directorio build/classes -- copiar en la carpeta conf el archivo AsteriskIntegrator.properties -- copiar en la carpeta libs los archivos de librerias de dependencia (todas las de la carpeta lib del proyecto)

Ajustar el archivo AsteriskIntegrator.properties

///////
ServerIP = 0.0.0.0 // dirección ip en donde voy a atender las peticiones provenientes del vtiger 
ServerPort = 5000 // puerto sobre el cuál se atenderan las peticiones provenientes del vtiger AsteriskAppDBPath = ../db

AsteriskServerIP = 127.0.0.1
AsteriskServerPort = 5038 
AsteriskUsername = admin // usuario del manager de asterisk a usar 
AsteriskPassword = admin // contraseña del usuario del manager de asterisk a usar

CrmURL = http://localhost/vtigercrm/ // Url del crm vtiger 
CrmURLPrefix = /modules/PBXManager/callbacks/PBXManager.php 
CrmSecretKey = test // secret de conexión entre vtiger y el integrador (debe ser el mismo para conectar con vtiger)

CheckKeyOnListenRequest=true 
LookUpVariablesNames= // lista de variables personalizadas que se deseen conservar separadas por coma (,) 
DefaultOriginateChannelProtocol = SIP

//////////

Como lanzar el aplicativo

-- ejecutar el archivo bin/start.sh para iniciar o bin/stop.sh para detener

Lista de comandos por Url

---pedir marcación: http://{ServerIP}:{ServerPort}/makecall?from=extension&to=numeroamarcar&context=from-internal&secret={CrmSecretKey} 
---consulta resultado de llamadas: http://{ServerIP}:{ServerPort}/cdrinfo
