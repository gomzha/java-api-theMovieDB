# Java Spring template project

Este proyecto está basado en el [Project Template](https://docs.gitlab.com/ee/gitlab-basics/create-project.html) de GitLab, y fue modificado por el equipo de DevOps, para adaptar una Pipeline de CI/CD standard al mismo. Ante cualquier duda o sugerencia, por favor contectarnos a través del canal de Slack [#gitlab-openshift](https://santander-tecno-ar.slack.com/archives/CMVJZN1U2)

Recuerde modificar los archivos:
* **config/staging.yml** para apuntar el step "dast" a donde despleguemos nuestra API


### CI Pipeline

Actualmente la Pipeline para Maven cuenta de 5 pasos estándar, traídos de los [Global CI Templates](https://gitlab.ar.bsch/santander-tecnologia/global-ci-templates):

- Package

- Test

- Quality

- Build-image

- Deploy


Por otro lado, la pipeline utiliza siempre imagenes base preparadas para correr en Openshift, configurada con HTTP_PROXY, HTTPS_PROXY y NO_PROXY, y utilizando usuario no-root.

  

**El Dockerfile, el `FROM` de la imagen se puede cambiar `"v17.0.5-runtime"` por `"v8-maven"` si requeris usar la versión 8 de Java, pero solo como último recurso, para más información visite https://confluence.ar.bsch/display/JAVA/Recomendaciones+para+usar+JAVA+con+Docker**


##### Package

Ejecuta `mvn clean install`, para crear el artifact. El template completo puede verse en [este repositorio](https://gitlab.ar.bsch/santander-tecnologia/global-ci-templates/blob/master/maven/.build-package-jdk8.yml).

##### Test

Ejecuta `mvn test`. Los tests deben estar separados en una de cuatro carpetas: `unit`, `functional`, `integration`, o `smoke` (las 4 deben existir, incluso si están vacías). En caso de que las 4 carpetas no existan, y no se quiera aprovechar del paralelismo, debe setearse la variable `PARALLEL_TESTS: false` en el archivo `ci-variables.yml`. De lo contrario, el step tendrá comportamiento errático. Este proyecto ya tiene las 4 carpetas creadas, si bien 3 de ellas están vacías. Si se quiere evitar que Maven falle por no encontrar tests para ejecutar, se debe setear la variable `<TEST_TYPE>_TESTS_ARE_MANDATORY: false` en el archivo `.ci-variables.yml`. Esto ya se encuentra seteado por default para el proyecto actual, por lo que se puede utilizar de ejemplo. El template completo puede verse en [este repositorio](https://gitlab.ar.bsch/santander-tecnologia/global-ci-templates/blob/master/maven/.tests-jdk8.yml).

##### Quality

Utiliza [SonarQube](https://sonarprod.ar.bsch/) para analizar Code Smells, Vulnerabilidades, Coverage de los Tests, etc. Se puede ver el resultado del último Quality Gate en cada Merge Request, si se desea. Para realizar esto, por favor comunicarse con el equipo de DevOps, ya que requiere configuración de Administrador de Sonar. Para más información respecto a Sonar, recomendamos visitar su [documentación](https://sonarprod.ar.bsch/documentation). El template completo puede verse en [este repositorio](https://gitlab.ar.bsch/santander-tecnologia/global-ci-templates/blob/master/maven/.sonar-scanner.yml).

  

##### Build-image

Utiliza una imagen de [Docker in Docker (dind)](https://hub.docker.com/_/docker/) para a su vez crear una imagen que contenga el código buildeado, y la sube al Container Registry local del proyecto, dejándola lista para hacer el traspaso a OKD, o a cualquier otro lugar donde se desee deployar la misma. El template completo puede verse en [este repositorio](https://gitlab.ar.bsch/santander-tecnologia/global-ci-templates/blob/master/common/.build-image.yml).

##### Deploy

Utilizando un template de Openshift, y variables declaradas en un archivo con esa función, este step importa a la instancia de Openshift apropiada la imagen generada en el paso anterior, y luego la lanza, asegurándose que ésta cumpla con las especificaciones del template de Openshift.

A tener en cuenta, el path relativo al mismo debe estar definido en la variable `OKD_TEMPLATE_NAME: <path>/<file_name>` en el archivo `ci-variables.yml`, así como el path relativo al archivo de variables de Openshift debe estar definido en la variable `OKD_TEMPLATE_PARAMS: <path>/<file_name>`. Por default, ambos archivos se encuentran contenidos actualmente en la carpeta `okd`, y son funcionales a este proyecto. A tener en cuenta, en caso de querer utilizar el template, también es obligatorio setear la variable `OKD_PROJECT` en `ci-variables.yml`, así como las variables en `okd/okd-params.env`, para que estas reflejen la data del proyecto actual.

Como paso final, es necesario también crear un Deploy Token, desde _Settings_ -> _Repository_ -> _Deploy Token_. El mismo debe tener el nombre __gitlab-deploy-token__, y permisos __read\_registry__. No es necesario completar las otras variables ni almacenar el token.


El template completo puede verse en [este repositorio](https://gitlab.ar.bsch/santander-tecnologia/global-ci-templates/blob/master/common/.deploy-okd.yml).

### GitFlow

La estrategia de branches propuesta para todos los equipos es Gitflow. En la misma, existen las siguientes branches:

- Master.

- Staging.

- Development.

- Features.

- Hotfixes.

El movimiento entre branches es exclusivamente a través de Merge Requests, y requiere que los mismos se aprueben por miembros del equipo, acorde a la branch de destino.  

##### Master

Es la única branch no volátil del repositorio. Contiene el código que se deploya en producción, y debe tener un tag acorde al mismo. Toda branch partirá siempre a partir de ésta.

##### Features

Aquí se programa la solución. Como se comentó antes, su origen es `master`, y su nombre debe comenzar con la palabra `feature`. Una vez el código esté listo, __el desarrollador__ debe crear un Merge Request a `development`. El merge request deberá ser aprobado por __otro miembro del equipo que no haya participado de este desarrollo__. __El Team Leader__ es el encargado de Mergear el código, una vez la pipeline asociada al merge request sea exitosa y el mismo esté aprobado. Una vez mergeado el código, la branch debe ser eliminada.  

##### Development

Aquí se solucionan los conflictos entre las distintas branches de feature. Como se comentó antes, su origen es `master`, y su nombre debe comenzar con la palabra `dev`. Recomendamos `development` para su nombre, aunque acepta alternativas. Una vez el código esté listo, __el desarrollador__ debe crear un Merge Request a `staging`. El merge request deberá ser aprobado por __el Team Leader__. __Un representante de Quality Assurance__ es el encargado de Mergear el código, una vez la pipeline asociada al merge request sea exitosa y el mismo esté aprobado. Una vez mergeado el código, la branch debe ser eliminada.

##### Staging

Aquí se prepara el entregable, mezclando el código de `development` con `master`, y resolviendo posibles conflictos por eventuales hotfixes. Como se comentó antes, su origen es `master`, y su nombre debe ser `staging`. Una vez el código esté listo, __el QA Leader__ debe crear un Merge Request a `staging`. El merge request deberá ser aprobado por __el Head o el Sponsor__. __El QA Leader__ es el encargado de Mergear el código, una vez la pipeline asociada al merge request sea exitosa y el mismo esté aprobado. Una vez mergeado el código, la branch debe ser eliminada. Staging es el step en el cual se deben ejecutar las User Acceptance Tests.

##### Hotfixes

Aquí se programan arreglos ad-hoc de vicios ocultos que hubieran llegado inadvertidamente a master. Como se comentó antes, su origen es `master`, y su nombre debe comenzar con la palabra hotfix. Una vez el código esté listo, se deberán seguir los mismos pasos que si fuera una branch de `Features`, si bien se entiende que se podrán crear branches paralelas de `Development` y `Staging`, para llegar con la mayor agilidad posible a Master.


### Ejecución de la Pipeline

Existen 2 pipelines distintas:

- Merge Request Pipeline.

- Branch Pipeline.

Es importante nombrar que las branches de feature no tienen pipeline asociada, no así sus Merge Requests.


##### Merge Request Pipeline

Ejecuta pruebas estáticas del código: QA (`Sonar`), SAST (`Kiuwan`), y tests (`Unit`, `Functional`, `Integration` y `Smoke`). Es propia de cada Merge Request.

  

##### Branch Pipeline

Deploya y ejecuta pruebas dinámicas del código: DAST (sólamente `Staging`), Build-Image (`Development` y `Staging`), y OKD-Deploy (`Development` y `Staging`). En el caso de `master`, el deploy ocurre promoviendo el container que ya está corriendo actualmente en `Staging`.

### Dudas

Para mayor información, siempre se puede consultar por Slack, a través del canal [#gitlab-openshift](https://santander-tecno-ar.slack.com/archives/CMVJZN1U2)

### [Más sobre el análisis DAST 2.0 - Cyber](https://confluence.ar.bsch/pages/viewpage.action?pageId=83205284)
# [F.A.Q en los Pipelines](https://confluence.ar.bsch/display/DEV/Errores+frecuentes+en+pipelines)
