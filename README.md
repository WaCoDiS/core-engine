# WaCoDiS Core Engine
![Build](https://github.com/WaCoDiS/core-engine/workflows/Build/badge.svg)  
The WaCoDiS Core Engine component provides core functionalities such as job scheduling, evaluation and execution.

**Table of Content**  
TODO  
Create a nice table of content, please.


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
Several observing routins requests certain datastores for new available data, such as in-situ measurements, Copernicus satellite data, sdi based geodata and services or meteorological data that are required for process executions.

**[Data Wrapper](https://github.com/WaCoDiS/data-access-api)**  
Information  about  all  incoming  required datasets are bundled by a are stored in a Metadata Storage. For the purpose of defining process inputs, the _Data Wrapper_ generates references to the required datasets from the metadata and provides these references to the Core Engine via a REST API. To provide an asynchronous Pub/Sub pattern, a [Metadata Connector](https://github.com/WaCoDiS/metadata-connector) will listen for new datasets and then interacts with the REST API.

**[Web Processing Service](https://github.com/WaCoDiS/javaps-wacodis-backend)**  
The  execution  of  analysis processes  provided  by  EO  Tools  is  encapsulated  by  a  OGC 
Web Processing Service (WPS), which provides a standardized interface for this purpose. Therefore a custom backend for the [52°North javaPS implementation](https://github.com/WaCoDiS/javaPS) provides certain preprocessing and execution features. 

**[Product Listener](https://github.com/WaCoDiS/product-listener)**  
A _Product Listener_ will be notified as soon as any analyis process has finished and a new earth observation product is available. The component will fetch the product from the WPS and routes it to one or more specific backends (e.g. GeoServer, ArcGIS Image Server) that provides a certain service for the user to access the product.

**Product Importer**  
For each product service backend a certain helper component will import the earth observation product into the specific backend's datastore and may set up a service on top of it. The product importer can be provided as part of the _Product Listener_ or can be provided as an external component (e.g. a [python script](https://github.com/WaCoDiS/Tools/tree/imageServicePublisherTest/imageServicePublisher) for importing porduct into the ArcGIS Image Server).

<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/architecture/wacodis_high_level_architecture.png" width="600" alt="Diagram of WaCoDiS high level architecture">
</p>

The WaCoDiS monitoring system architecture is designed in a modular fashion and follows a  publish/subscribe pattern. The different components are loosely connected to each other via messages that are passed through a message broker. Each module subscribes to messages of interest at the message broker. This approach enables an independent and asynchronous handling of specific events.  

The messages exchanged via message broker follow a domain model that has been defined by the OpenAPI specification. You can find these definitions and other documentation in the [apis-and-workflows repo](https://github.com/WaCoDiS/apis-and-workflows).

## Overview  
TODO
* Brief component description
* OpenAPI Specification
* Utilized technologies

## Installation / Building Information
TODO
* installation information (maven build, etc.)

### Configuration
Configuration is fetched from [WaCoDiS Config Server](https://github.com/WaCoDiS/config-server). If config server is not available configuration values located at *main/resources/bootstrap.yml* are applied instead.  
#### Parameters
TODO
Describe configuration parameters


### Deployment
TODO
This section describes deployment scenarios, options and preconditions.

## User Guide
TODO
Describe how to run and use this component

## Contribution - Developer Information
This section contains information for developers.

### How to Contribute
TODO
Describe how to extend this module

### Branching
The master branch provides sources for stable builds. The develop branch represents the latest (maybe unstable) state of development.

### License and Third Party Lib POM Plugins
[optional]

## Contact
|    Name   |   Organization    |    Mail    |
| :-------------: |:-------------:| :-----:|
| Sebastian Drost | Bochum University of Applied Sciences | sebastian.drost@hs-bochum.de |
| Arne Vogt | Bochum University of Applied Sciences | arne.vogt@hs-bochum.de |
| Andreas Wytzisk  | Bochum University of Applied Sciences | andreas.wytzisk@hs-bochum.de |
| Matthes Rieke | 52° North GmbH | m.rieke@52north.org |

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

