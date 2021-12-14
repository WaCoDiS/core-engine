# WaCoDiS Core Engine
![Build](https://github.com/WaCoDiS/core-engine/workflows/Build/badge.svg)  
The WaCoDiS Core Engine component provides core functionalities such as job scheduling, evaluation and execution.

**Table of Content**  
1. [WaCoDiS Project Information](#wacodis-project-information)
2. [Overview](#overview)
3. [Installation / Building Information](#installation--building-information)
4. [User Guide](#user-guide)
5. [Developer Information](#developer-information)
6. [Contact](#contact)
7. [Credits and Contributing Organizations](#credits-and-contributing-organizations)

## WaCoDiS Project Information
<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/wacodis.png" width="200">
</p>
Climate changes and the ongoing intensification of agriculture effect in increased material inputs in watercourses and dams. Thus, water industry associations, suppliers and municipalities face new challenges. To ensure an efficient and environmentally friendly water supply for the future, adjustments on changing conditions are necessary. Hence, the research project WaCoDiS aims to geo-locate and quantify material outputs from agricultural areas and to optimize models for sediment and material inputs (nutrient contamination) into watercourses and dams. Therefore, approaches for combining heterogeneous data sources, existing interoperable web based information systems and innovative domain oriented models will be explored.

### Architecture Overview

The WaCoDiS project aims to exploit the great potential of Earth Observation (EO) data (e.g. as provided by the Copernicus Programme) for the development of innovative water management analytics service and the improvement of hydrological models in order to optimize monitoring processes. Existing SDI based geodata and in-situ data from the sensors that monitor water bodies will be combined with Sentinel-1 and Sentinel-2 data. Therefore, the WaCoDiS monitoring system is designed as a modular and extensible software architecture that is based on interoperable interfaces such as the Open Geospatial Consortium (OGC) Web Processing Service. This allows a sustainable and ﬂexible way of integrating different EO processing algorithms. In addition, we consider architectural aspects like publish/subscribe patterns and messaging protocols that increase the effectiveness of processing big EO data sets. Up to now, the WaCoDiS monitoring system comprises the following components:  

**[Job Manager](https://github.com/WaCoDiS/job-definition-api)**  
A REST API enables users to define job descriptions for planning the execution of analysis processes. 

**[Core Engine](https://github.com/WaCoDiS/core-engine)**  
The _Core Engine_ schedules jobs for planned process executions based on the job descriptions. In addition, it is responsible for triggering WPS processes as soon as all required process input data is available.

**[Datasource Observer](https://github.com/WaCoDiS/datasource-observer)**  
Several observing routines requests certain datastores for new available data, such as in-situ measurements, Copernicus satellite data, sdi based geodata and services or meteorological data that are required for process executions.

**[Data Discovery](https://github.com/WaCoDiS/data-access-api)**  
Data Discovery comprises two components: the [WaCoDiS Data Access API](https://github.com/WaCoDiS/data-access-api) and the [WaCoDiS Metadata Connector](https://github.com/WaCoDiS/metadata-connector).  
Metadata  about  all  incoming, available datasets discovered by the _Datasource Observer_ is handled and bundled by the [WaCoDiS Data Access API](https://github.com/WaCoDiS/data-access-api). For the purpose of defining process inputs, the _WaCoDiS Data Access API_ generates references to the required datasets from the metadata and provides these references to the Core Engine via a REST API. The _WaCoDis Data Access API_ uses a instance of Elasticsearch search engine to store the metdata about all available datasets.  
To provide an asynchronous Publish/Subscribe pattern, the [WaCoDiS Metadata Connector](https://github.com/WaCoDiS/metadata-connector) will listen for new datasets and then interacts with the REST API of _WaCoDiS Data Access API_. 

**[Web Processing Service](https://github.com/WaCoDiS/javaps-wacodis-backend)**  
The  execution  of  analysis processes  provided  by  EO  Tools  is  encapsulated  by  a  OGC 
Web Processing Service (WPS), which provides a standardized interface for this purpose. Therefore a custom backend for the [52°North javaPS implementation](https://github.com/WaCoDiS/javaPS) provides certain preprocessing and execution features. 

**[Product Listener](https://github.com/WaCoDiS/product-listener)**  
A _Product Listener_ will be notified as soon as any analyis process has finished and a new earth observation product is available. The component will fetch the product from the WPS and routes it to one or more specific backends (e.g. GeoServer, ArcGIS Image Server) that provides a certain service for the user to access the product.

**Product Importer**  
For each product service backend a certain helper component will import the earth observation product into the specific backend's datastore and may set up a service on top of it. The product importer can be provided as part of the _Product Listener_ or can be provided as an external component (e.g. a [python script](https://github.com/WaCoDiS/Tools/tree/imageServicePublisherTest/imageServicePublisher) for importing porduct into the ArcGIS Image Server).

**[Job Status Listener](https://github.com/WaCoDiS/job-status-listener)**  
The _WaCoDiS Job Status Listener_ is intermediate component that consumes messages (published by the Core Engine) on execution progress of processing jobs. According to the job's progress the _Job Status Listener_ updates the status in the job's describtion by interacting with the REST API of the Job Manager.

<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/develop/architecture/wacodis_high_level_architecture.png" width="600" alt="Diagram of WaCoDiS high level architecture">
</p>

The WaCoDiS monitoring system architecture is designed in a modular fashion and follows a  publish/subscribe pattern. The different components are loosely connected to each other via messages that are passed through a message broker. Each module subscribes to messages of interest at the message broker. This approach enables an independent and asynchronous handling of specific events.  

The messages exchanged via message broker follow a domain model that has been defined by the OpenAPI specification. You can find these definitions and other documentation in the [apis-and-workflows repo](https://github.com/WaCoDiS/apis-and-workflows).

## Overview  
The WaCoDiS Core Engine is the system component that implements the core processing workflow. This processing workflow comprises the following three stepps:
1. scheduling of processing jobs (jobs).
2. evalutation whether all necessary input data is available to start a processing job (job evaluation)
3. initiation of the execution of the processing job by sending a request to the processing environment or rather the web processing service (job execution)  
  
The core engine is also responsible for keeping other WaCoDiS components updated about the progress of processing jobs. The core engine publishes messages via the sytem's message broke if a processing job is started, failt or executed sucessfully. 

### Core Data Types
* **Job**  
A _WacodisJobDefinition_ (Job) describes a processing that is to be executed automatically according to a defined schedule. The WacodisJobDefinition contains (among other attributes) the input data required for execution, as well as the time frame and area of interest. 
* **DataEnvelope**  
The metadata about an existing dataset is described by the _AbstractDataEnvelope_ (DataEnvelope) data format. There are different subtypes for different data sources (e.g _SensorWebDataEnvelope_ or _CopernicusDataEnvelope_).    
* **Resource**  
Access to the actual data records is described by the _AbstractResource_ (Resources) data format. There are the subtypes _GetResources_ and _PostResources_. A GetResources contains only a URL while a PostResource contains a URL and a body for a HTTP-POST request.  
* **SubsetDefinition**  
The required inputs of a job are described by the data format _AbstractSubsetDefinition_ (SubsetDefinition). There are different subtypes for different types of input data (e.g _SensorWebSubsetDefinition_ or _CopernicusSubsetDefinition_). There is usually a subtype of AbstractSubsetDefinition that corresponds to a subtype of AbstractDataEnvelope.  
   
The formal definition of these data types is done with OpenAPI and is available in the [apis-and-workflows repo](https://github.com/WaCoDiS/apis-and-workflows/blob/master/openapi/src/main/definitions/wacodis-schemas.yml).

### Modules
The WaCoDiS Core Engine comprises three Maven modules:
* __WaCoDiS Core Engine Data Models__  
This module contains Java classes that reflect the [basic data model](https://github.com/WaCoDiS/apis-and-workflows/tree/master). This includes the data types specified with OpenAPI
in the WaCoDiS apis-and workflows repository. All model classes were generated by the OpenAPI Generator, which is integrated
and can be used as Maven Plugin within this module.  
* __WaCoDiS Core Engine Job Scheduling__  
This module is responsible for initiating the evaluation (and execution) of a processing job according to cron pattern provided in the job's definition (WacodisJobDefinition). The scheduling is implemented based on the Java API Quartz. 
* __WaCoDiS Core Engine Evaluator__  
This module is responsible for checking if a scheduled job is actually executable; that means checking if all necessary input data is available. If not, the evaluator waits until new data sets become available a re-evaluates the executably of the given processing job. 
* __WaCoDiS Core Engine Executor__  
If a processing job is executable the executor module is responsible for putting together a execution request (containing all input parameters) and submitting this request to the processing environment. The Core Engine Executor keeps track of the job's execution progress and publishes messages via the system's message broker when a processing job is executed sucessfully or processing has failed. 
* __WaCoDiS Core Engine App__  
Since WaCoDiS Core Engine is implemented as Spring Boot application, the App module provides the application runner as well as default externalized configurations.
* __WaCoDiS Core Utils__  
The Core Engine Utils module contains classes that implement routines used in multiple of the above modules. 
### Technologies
* __Java__  
WaCoDiS Core Engine is tested with Oracle JDK 8 and OpenJDK 8. Unless stated otherwise later Java versions can be used as well.
* __Maven__  
This project uses the build-management tool [Apache Maven](https://maven.apache.org/)
* __Spring Boot__  
WaCoDiS Core Engine is a standalone application built with the [Spring Boot](https://spring.io/projects/spring-boot) 
framework. Therefore, it is not necessary to deploy WaCoDiS Data Access manually with a web server.  
* __Spring Cloud__  
[Spring Cloud](https://spring.io/projects/spring-cloud) is used for exploiting some ready-to-use features in order to implement
an event-driven workflow. In particular, [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream) is used
for subscribing to asynchronous messages within the WaCoDiS system.
* __RabbitMQ__  
For communication with other WaCoDiS components of the WaCoDiS system the message broker [RabbitMQ](https://www.rabbitmq.com/)
is utilized. RabbitMQ is not part of WaCoDiS Core Engine and therefore [must be deployed separately](#preconditions).
* __OpenAPI__  
[OpenAPI](https://github.com/OAI/OpenAPI-Specification) is used for the specification of data models used within this project.
* __Quartz__  
[Quartz](http://www.quartz-scheduler.org/) is a Java API for execution of recurrent, regular tasks based on a [Cron definition (crontab)](http://pubs.opengroup.org/onlinepubs/9699919799/utilities/crontab.html#tag_20_25_07). Quartz is used for scheduling the processing jobs.

## Installation / Building Information
### Build from Source
In order to build the WaCoDiS Core Engine from source _Java Development Kit_ (JDK) must be available. **Currently, WaCoDiS Core Engine (or its dependencies) can only be built with Java JDK8.** There is no guarantee that the build process works with other versions of java. 
Since this is a Maven project, [Apache Maven](https://maven.apache.org/) must be available for building it.

Before you build the Core Engine project, it is necessary to clone and build the [WaCoDiS fork of the 52°North WPS Client Lib](https://github.com/WaCoDiS/wps-client-lib). 
1. clone the project: `git clone https://github.com/WaCoDiS/wps-client-lib.git`
2. run `mvn clean install` in WPS Client Lib project's root folder to build the project and install the artifacts in your local Maven repository.  
As mentioned above, the build process of the WPS Client Lib might fail with other java versions than JDK8.

After you built the WPS Client Lib project, you can build the Core Engine project by running `mvn clean install` from (Core Engine) root directory.

### Build using Docker
The project contains a Dockerfile for building a Docker image. Simply run `docker build -t wacodis/core-engine:latest .`
in order to build the image. You will find some detailed information about running the Core Engine as Docker container
within the [deployment section](#run-with-docker).

## User Guide

### Deployment
This section describes deployment scenarios, options and preconditions.

#### Preconditions
* (without using Docker) In order to run WaCoDiS Core Engine Java Runtime Environment (JRE) (version >= 8) must be available. In order to [build Job Definition API from source](#installation--building-information) Java Development Kit (JDK) version >= 8) must be abailable. Core Engine is tested with Oracle JDK 8 and OpenJDK 8.
* In order to receive message about newly available data sets (job evalutation) and to publish message about processing progress (job execution) a running instance a running instance of [RabbitMQ message broker](https://www.rabbitmq.com/) must be available.  
* A running instance of [WaCoDiS Job Manager](https://github.com/WaCoDiS/job-definition-api) must be available because during the job scheduling the Core Engine retrieves detailed job information by consuming the Job Manager's REST API.

  
The server addresses are [configurable](#configuration).  
  
 * If [configuration](#configuration) should be fetched from Configuration Server a running instance of [WaCoDiS Config Server](https://github.com/WaCoDiS/config-server) must be available.

### Run with Maven
Just start the application by running `mvn spring-boot:run` from the root of the `core-engine-app` module. [Make
sure you have installed all dependencies](#build-from-source) with `mvn clean install` from the project root.

### Run with Docker
1. Build Docker Image from [Dockerfile](https://github.com/WaCoDiS/core-engine/blob/master/Dockerfile) that resides in the project's root folder.
2. Run created Docker Image.

Alternatively, latest available docker image (automatically built from master branch) can be pulled from [Docker Hub](https://hub.docker.com/r/wacodis/job-definition-api). See [WaCoDiS Docker repository](https://github.com/WaCoDiS/wacodis-docker) for pre-configured Docker Compose files to run WaCoDiS system components and backend services (RabbitMQ and Elasticsearch).

### Configuration
Configuration is fetched from [WaCoDiS Config Server](https://github.com/WaCoDiS/config-server). If config server is not
available, configuration values located at *src/main/resources/application.yml* within the Core Engine App submodule
are applied instead. 

#### Parameters
The following section contains descriptions for configuration parameters structured by configuration section.

##### spring/cloud/stream/bindings/input-data-envelope
configuration of message chanel for receiving messages on newly available datasets

| value     | description       | note  |
| ------------- |-------------| -----|
| destination     | topic used to receive messages on newly available datasets, must be algined with Data Access API config | e.g. *wacodis.test.jobs.data.accessible* |
| binder      | defines the binder (message broker)   | |
| content-type      | content type of  DataEnvelope acknowledgement messages (mime type)   | should always be *application/json* |

##### spring/cloud/stream/bindings/job-creation
configuration of message chanel for receiving messages when a new job is created

| value     | description       | note  |
| ------------- |-------------| -----|
| destination     | topic used to publish messages about created WaCoDiS jobs | e.g. *wacodis.test.jobs.new* |
| binder      | defines the binder (message broker)   | |
| content-type      | content type of  the messages   | should always be *application/json* |

##### spring/cloud/stream/bindings/job-deletion
configuration of message chanel for receiving messages when an existing job is deleted

| value     | description       | note  |
| ------------- |-------------| -----|
| destination     | topic used to publish message about deleted WaCoDiS jobs | e.g. *wacodis.test.jobs.deleted* |

##### spring/cloud/stream/bindings/toolFinished
configuration of message chanel for publishing messages on successfully executed processing jobs

| value     | description       | note  |
| ------------- |-------------| -----|
| destination     | topic used to publish message about deleted WaCoDiS jobs | e.g. *wacodis.test.tools.finished* |

##### spring/cloud/stream/bindings/toolExecution
configuration of message chanel for publishing messages when a processing job is started (submitted to the processing environment)

| value     | description       | note  |
| ------------- |-------------| -----|
| destination     | topic used to publish message about deleted WaCoDiS jobs | e.g. *wacodis.test.tools.execute* |

##### spring/cloud/stream/bindings/toolExecution
configuration of message chanel for publishing messages when a processing job is started (submitted to the processing environment)

| value     | description       | note  |
| ------------- |-------------| -----|
| destination     | topic used to publish message about deleted WaCoDiS jobs | e.g. *wacodis.test.tools.execute* |

##### spring/rabbitmq
parameters related to WaCoDis message broker

| value     | description       | note  |
| ------------- |-------------| -----|
| host | RabbitMQ host (WaCoDiS message broker) | e.g. *localhost* |
| port | RabbitMQ port (WaCoDiS message broker)   | e.g. *5672*|
| username | RabbitMQ username (WaCoDiS message broker)   | |
| password | RabbitMQ password (WaCoDiS message broker)   | |

##### spring/scheduler
configuration parameters related to the job scheduling module

| value     | description       | note  |
| ------------- |-------------| -----|
| timezone| time zone used for job scheduling  | e.g. *Europe/Berlin* |
| jobrepository/uri | base url of the necessary instance of [Job Management API](https://github.com/WaCoDiS/job-definition-api) | e.g. *http://localhost:8081*|

##### spring/evaluator
configuration parameters related to the job evaluation module

| value     | description       | note  |
| ------------- |-------------| -----|
| matching/preselectCandidates| if true, a waiting processing job (missing input data) is only re-evaluated if a new data set with matching source type becomes available  | boolean, if set to _false_ requests do Data Access API will increase because every new data set triggers re-evaluation of every waiting job |
| dataaccess/uri | url of the **_resource/search_ endpoint** of the necessary instance of [Data Access API](https://github.com/WaCoDiS/data-access-api) | e.g. *http://localhost:8082/resources/search*|

##### spring/executor/wps
configuration parameters related to the job executor module, precisely the interacting with the web processing service (WPS, processing environment)

| value     | description       | note  |
| ------------- |-------------| -----|
| uri| base url of the WaCoDiS (OGC) WPS  | e.g. *http://localhost:8083/wps* |
| version | version of the OGC WPS standard that is implemented by the WaCoDiS WPS | e.g. *2.0.0*|
| defaultResourceMimeType | (optional) define mime type for **every** input resource | default is *text/xml*|
| defaultResourceSchema/name | (optional) define schema for **every** input resource | default is *GML3*, always provide _name_ and _schemaLocation_ |
| defaultResourceSchema/schemaLocation | (optional) define schema for **every** input resource | default is *http://schemas.opengis.net/gml/3.1.1/base/feature.xsd*|
| maxParallelWPSProcessPerJob | max. number of parallel wps processes started per wacodis processing job | only applicable if wacodis job is splitted in multiple wps processes (see WacodisJobDefinition.executionSettings.executionMode), e.g. _3_|
| processInputsDelayed | delay between start of multiple wps processes per wacodis processing job | only applicable if wacodis job is splitted in multiple wps processes (see WacodisJobDefinition.executionSettings.executionMode), boolean, e.g. _false_|
| initialDelay_Milliseconds | delay before the first wps process is started | applicable if processInputDelayed is true, e.g. _0_|
| delay_Milliseconds | delay between separate wps processes are started | applicable if processInputDelayed is true,  e.g. _900000_|
| delay_Milliseconds | if true, squential processing of multiple wps process per wacodis processing job| overrides maxParallelWPSProcessPerJob (effectively same as maxParallelWPSProcessPerJob = 1), only applicable if wacodis job is splitted in multiple wps processes (see WacodisJobDefinition.executionSettings.executionMode),  boolean|

##### spring/datasource.core.quartz-data-source
Per default, Quartz scheduler makes use of an in-memory job store. In order to configure a JDBC-based store, Core Engine
provides a `DataSource` bean which can be configured using `spring.datasource.core.quartz-data-source`. To configure the
`DataSource` just follow the [Spring Boot guide](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-configure-a-datasource).

##### spring/quartz
Quartz scheduler related beans are provided using [spring-boot-starter-quartz](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-quartz).
Hence, the scheduler can be configured via externalized configuration using `spring.quartz.properties`. Just set the usual
[Quartz configuration properties](https://www.quartz-scheduler.org/documentation/quartz-2.3.0/configuration/).

## Developer Information
### How to Contribute
#### Extending Core Engine
##### New Types of DataEnvelope and SubsetDefinition
WaCoDiS Core Engine and [WaCoDiS Data Access](https://github.com/WaCoDiS/data-access-api) must be modified if new types of DataEnvelope or SubsetDefintion are added to [Wacodis schemas](https://github.com/WaCoDiS/apis-and-workflows/blob/master/openapi/src/main/definitions/wacodis-schemas.yml) in order to support the newly introduced data types. See the [Wiki](https://github.com/WaCoDiS/core-engine/wiki/Extending-Core-Engine#integrate-new-dataenvelopes-and-subsetdefinitions) for further information.

#### Pending Developments
##### Support for POSTResources
The Core Engine Executor is currently only able to submit GetResources to the WaCoDiS processing environment. Currently the Core Engine cannot handle POSTResources.

### Branching
The master branch provides sources for stable builds. The develop branch represents the latest (maybe unstable)
state of development.

### License
Apache License, Version 2.0

### Contributing developers
|    Name   |   Organization    |    Mail    |
| :-------------: |:-------------:| :-----:|
| Arne Vogt | 52° North GmbH | [arnevogt](https://github.com/arnevogt) |
| Sebastian Drost | 52° North GmbH | [SebaDro](https://github.com/SebaDro) |
| Matthes Rieke | 52° North GmbH | [matthesrieke](https://github.com/matthesrieke) |

## Contact
The WaCoDiS project is maintained by [52°North GmbH](https://52north.org/). If you have any questions about this or any
other repository related to WaCoDiS, please contact wacodis-info@52north.org.

## Credits and Contributing Organizations
- Department of Geodesy, Bochum University of Applied Sciences, Bochum
- 52° North Initiative for Geospatial Open Source Software GmbH, Münster
- Wupperverband, Wuppertal
- EFTAS Fernerkundung Technologietransfer GmbH, Münster

The research project WaCoDiS is funded by the BMVI as part of the [mFund programme](https://www.bmvi.de/DE/Themen/Digitales/mFund/Ueberblick/ueberblick.html)  
<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/mfund.jpg" height="100">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/bmvi.jpg" height="100">
</p>



