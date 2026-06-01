# Q 1-8 : Document your docker-compose file

The docker-compose file defines a three-tier architecture composed of three services:

* database: PostgreSQL database initialized with SQL scripts and using a persistent volume (postgres-data).
* backend: Spring Boot REST API connected to PostgreSQL through the internal Docker network.
* httpd: Apache HTTP Server configured as a reverse proxy and exposed on port 80.

The services communicate through the private Docker network app-network.

The database data is persisted using the postgres-data volume.

The depends_on directive ensures that services are started in the correct order.

The restart: unless-stopped policy automatically restarts services after failures or host reboots.


# Q 1-9 : Document your publication commands and published images

Docker image publication process:

1. Login to Docker Hub:

docker login

2. Tag images with a version number:

docker tag tp1-database yassinegharbi2627/database:1.0
docker tag tp1-backend yassinegharbi2627/simpleapi:1.0
docker tag tp1-httpd yassinegharbi2627/httpd:1.0

3. Publish images:

docker push yassinegharbi2627/database:1.0
docker push yassinegharbi2627/simpleapi:1.0
docker push yassinegharbi2627/httpd:1.0

Published images:

* yassinegharbi2627/database:1.0
* yassinegharbi2627/simpleapi:1.0
* yassinegharbi2627/httpd:1.0

Versioning allows us to identify stable releases and deploy the same application version on multiple environments.


# Q 1-10 : Why do we put our images into an online repo?

Docker images are stored in an online registry to make them available from anywhere and to simplify deployment.

Benefits include:

* Sharing images with team members.
* Deploying applications on different servers without rebuilding images.
* Version management and rollback capabilities.
* Integration with CI/CD pipelines.
* Reproducible deployments across environments.
* Centralized storage and distribution of application artifacts.

In professional environments, image registries act as the single source of truth for application releases.
