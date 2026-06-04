# Dev_Ops_Gharbi_Yassine TP1

## 1-1 Why is it better to use the `-e` flag (environment variables) rather than hardcode values in the Dockerfile?

Using environment variables improves security and flexibility. Sensitive information such as database credentials should not be stored directly in the Dockerfile because the Dockerfile is versioned and shared with other developers.

Environment variables allow the same image to be reused in different environments (development, testing, production) without modifying the image itself. Configuration values can be injected at runtime through Docker Compose or a `.env` file.

---

## 1-2 Why do we need a volume attached to our PostgreSQL container?

A Docker volume is required to persist database data outside the container lifecycle.

Without a volume, all data stored in PostgreSQL would be lost when the container is removed or recreated. By attaching a volume, the database files remain available and can be reused by new containers.

**Benefits:**

- Data persistence
- Easier backup and recovery
- Container recreation without data loss
- Separation between application lifecycle and data lifecycle
---

## 1-3 Database container essentials

### Dockerfile

```dockerfile
FROM postgres:17.2-alpine

COPY 01-CreateScheme.sql /docker-entrypoint-initdb.d/
COPY 02-InsertData.sql /docker-entrypoint-initdb.d/
```

This Dockerfile extends the official PostgreSQL image and automatically executes SQL scripts during the first initialization of the database.

### Main Commands

**Build the image:**

```bash
docker build -t database .
```

**Run the container:**

```bash
docker run -d \
  --name postgres-db \
  --network back-network \
  -v postgres-data:/var/lib/postgresql/data \
  database
```

**Display running containers:**

```bash
docker ps
```

**Display container logs:**

```bash
docker logs postgres-db
```

**Stop the container:**

```bash
docker stop postgres-db
```

**Remove the container:**

```bash
docker rm postgres-db
```

---

## 1-4 Why do we need a multistage build? Explain each step of the Dockerfile.

A multistage build separates the build environment from the runtime environment.

**Benefits:**

- Smaller final image
- Better security
- Faster deployment
- No build tools included in production

**Example:**

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS myapp-build

WORKDIR /opt/myapp

RUN apk add --no-cache maven

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /opt/myapp

COPY --from=myapp-build /opt/myapp/target/*.jar /opt/myapp/myapp.jar

ENTRYPOINT ["java","-jar","myapp.jar"]
```

### Explanation

**Stage 1 – Build stage**

- Uses a JDK image because compilation is required
- Installs Maven
- Copies source code
- Builds the application and generates a JAR file

**Stage 2 – Runtime stage**

- Uses a lighter JRE image
- Copies only the generated JAR from the previous stage
- Starts the Spring Boot application

Only the runtime dependencies are included in the final image.

---

## 1-5 Why do we need a reverse proxy?

A reverse proxy acts as an intermediary between clients and backend services.

In this project, Apache receives HTTP requests and forwards them to the Spring Boot application.

**Benefits:**

- Single entry point for users
- Hides internal architecture
- Improved security
- SSL/TLS termination
- Load balancing capabilities
- Easier deployment of multiple services

**Architecture:**

```
Browser → Apache → Spring Boot → PostgreSQL
```

---

## 1-6 Why is Docker Compose so important?

Docker Compose simplifies the management of multi-container applications.

Instead of manually creating networks, volumes and containers, all services are described in a single configuration file.

**Benefits:**

- Infrastructure as code
- Easy deployment
- Reproducibility
- Automatic network creation
- Dependency management
- Simplified maintenance

The entire application can be started with a single command:

```bash
docker compose up -d
```

---

## 1-7 Most important Docker Compose commands

**Start all services:**

```bash
docker compose up -d
```

**Build and start services:**

```bash
docker compose up -d --build
```

**Display running services:**

```bash
docker compose ps
```

**Display logs:**

```bash
docker compose logs
```

**Display logs for a specific service:**

```bash
docker compose logs backend
```

**Stop services:**

```bash
docker compose stop
```

**Restart services:**

```bash
docker compose restart
```

**Remove containers, networks and resources:**

```bash
docker compose down
```

**Remove containers and volumes:**

```bash
docker compose down -v
```

**Pull images:**

```bash
docker compose pull
```

**Execute a command inside a service:**

```bash
docker compose exec backend bash
```
---

## 1-8 Document your docker-compose file

The docker-compose file defines a three-tier architecture composed of four services:

- **database**: PostgreSQL database initialized with SQL scripts and using a persistent volume (postgres-data)
- **backend**: Spring Boot REST API connected to PostgreSQL through the internal Docker network
- **httpd**: Apache HTTP Server configured as a reverse proxy and exposed on port 80
- **adminer**: Database management tool for easy database administration

The services communicate through the private Docker network `app-network`.

The database data is persisted using the `postgres-data` volume.

The `depends_on` directive ensures that services are started in the correct order.

The `restart: unless-stopped` policy automatically restarts services after failures or host reboots.
---

## 1-9 Document your publication commands and published images

### Docker image publication process

1. **Login to Docker Hub:**

```bash
docker login
```

2. **Tag images with a version number:**

```bash
docker tag tp1-database yassinegharbi2627/database:1.0
docker tag tp1-backend yassinegharbi2627/simpleapi:1.0
docker tag tp1-httpd yassinegharbi2627/httpd:1.0
```

3. **Publish images:**

```bash
docker push yassinegharbi2627/database:1.0
docker push yassinegharbi2627/simpleapi:1.0
docker push yassinegharbi2627/httpd:1.0
```

### Published images

- `yassinegharbi2627/database:1.0`
- `yassinegharbi2627/simpleapi:1.0`
- `yassinegharbi2627/httpd:1.0`

Versioning allows us to identify stable releases and deploy the same application version on multiple environments.
---

## 1-10 Why do we put our images into an online repo?

Docker images are stored in an online registry to make them available from anywhere and to simplify deployment.

**Benefits:**

- Sharing images with team members
- Deploying applications on different servers without rebuilding images
- Version management and rollback capabilities
- Integration with CI/CD pipelines
- Reproducible deployments across environments
- Centralized storage and distribution of application artifacts

In professional environments, image registries act as the single source of truth for application releases.


test si ça marche 

# Dev_Ops_Gharbi_Yassine TP2



## 2.1 What are Testcontainers?

Testcontainers is an open-source Java library that allows developers to run lightweight, disposable Docker containers during automated tests. It automatically starts and stops the required services (such as PostgreSQL, MySQL, Redis, or Kafka) before and after test execution.

Using Testcontainers ensures that integration tests run in an isolated and reproducible environment without requiring a manually configured database or external service. This improves test reliability and consistency across different development machines and CI/CD pipelines.

In this project, Testcontainers is used to create a temporary PostgreSQL database for integration tests. The application connects to this containerized database during test execution, allowing the tests to validate the application's behavior in conditions close to a real production environment.

**Benefits of Testcontainers:**

* Provides realistic integration testing environments.
* Eliminates dependency on locally installed services.
* Ensures consistent test execution across developers' machines and CI servers.
* Automatically manages container lifecycle (startup and cleanup).
* Integrates seamlessly with Docker and Maven-based projects.

## 2-2 For what purpose do we need to use secured variables?

Secured variables are used to store sensitive information such as passwords, API keys, database credentials, and access tokens without exposing them in the source code or repository.

Using secured variables improves security because:

* Secrets are not visible in the project files or Git history.
* Different values can be used for development, testing, and production environments.
* Access to sensitive data can be restricted to authorized users and CI/CD pipelines.
* If a secret needs to be changed, it can be updated without modifying the application code.

In a CI/CD context, secured variables allow the pipeline to access resources (databases, cloud services, deployment servers, etc.) while keeping credentials confidential and reducing the risk of accidental leaks.



## 2-3 Why did we put needs: test-backend on this job?

The `needs: test-backend` directive ensures that the Docker image build job is executed only after the backend compilation and tests have completed successfully. This prevents building Docker images from code that does not compile or contains failing tests. It also optimizes the CI/CD pipeline by stopping the workflow early when an error is detected during the testing phase. Without this dependency, Docker images could be built from unstable or broken code, leading to unreliable deployments.


## 2-4 

Docker images are pushed to Docker Hub in order to store and distribute application versions through a central registry. By publishing images, developers and deployment servers can easily download and run the exact same application version. This ensures consistency between development, testing, and production environments. It also enables Continuous Delivery by making the latest validated version of the application automatically available after each successful build.


# Dev_Ops_Gharbi_Yassine TP3 Ansible 


## 3-1 Document your inventory and base commands :

Ansible modules are idempotent. Running the same command multiple times does not produce additional changes once the desired state has been reached. This behavior was verified by removing Apache2 twice: the first execution returned changed=true, while the second returned changed=false.

## 3-2 Document your playbook

### First Playbook

A first playbook was created to validate connectivity between the Ansible control node and the remote server.

```yaml
- hosts: all
  gather_facts: false
  become: true

  tasks:
    - name: Test connection
      ping:
```

This playbook uses the Ansible `ping` module to verify that:

* the remote host is reachable;
* SSH authentication is working;
* Ansible can execute commands on the target machine.

It was executed with:

```bash
ansible-playbook -i inventories/setup.yml playbook.yml
```

### Docker Installation Playbook

A second playbook was created to automate Docker installation on the remote server. The playbook performs the following tasks:

* Install required dependencies.
* Add Docker's official GPG key.
* Configure the Docker APT repository.
* Install Docker CE.
* Install Python and pip.
* Create a Python virtual environment.
* Install the Docker Python SDK.
* Ensure that the Docker service is running.

### Refactoring with Roles

To improve maintainability and reusability, the Docker installation tasks were moved into a dedicated Ansible role named `docker`.

Project structure:

```text
ansible/
├── inventories/
│   └── setup.yml
├── docker.yml
└── roles/
    └── docker/
        ├── handlers/
        │   └── main.yml
        └── tasks/
            └── main.yml
```

The main playbook simply calls the role:

```yaml
- hosts: all
  gather_facts: true
  become: true

  roles:
    - docker
```

### Verification

The playbook was executed successfully and Docker installation was verified using:

```bash
ansible all -i inventories/setup.yml -a "docker --version"
```

Output:

```text
Docker version 29.5.2, build 79eb04c
```

This confirms that Docker is correctly installed and running on the remote server.




## 3-3 Document your docker_container tasks configuration

The application deployment relies on the Ansible module `community.docker.docker_container`. This module allows containers to be created, configured and started directly from Ansible without using Docker Compose.

### Database Container

The PostgreSQL database container is deployed using:

```yaml
- name: Launch PostgreSQL
  community.docker.docker_container:
    name: database
    image: yassinegharbi2627/database:1.0
    state: started
    recreate: true

    env:
      POSTGRES_DB: "{{ postgres_db }}"
      POSTGRES_USER: "{{ postgres_user }}"
      POSTGRES_PASSWORD: "{{ postgres_password }}"

    networks:
      - name: back-network
```

Configuration:

* `name`: container name.
* `image`: Docker image hosted on Docker Hub.
* `state: started`: ensures the container is running.
* `recreate: true`: recreates the container when configuration changes.
* `env`: injects PostgreSQL environment variables.
* `networks`: connects the container to the backend network.

### Backend Container

The Spring Boot application is deployed using:

```yaml
- name: Launch backend
  community.docker.docker_container:
    name: simpleapi
    image: yassinegharbi2627/simpleapi:1.0
    state: started
    recreate: true

    env:
      SPRING_DATASOURCE_URL: "{{ spring_datasource_url }}"
      SPRING_DATASOURCE_USERNAME: "{{ spring_datasource_username }}"
      SPRING_DATASOURCE_PASSWORD: "{{ spring_datasource_password }}"

    networks:
      - name: front-network
      - name: back-network
```

Configuration:

* Environment variables are used to configure the database connection.
* The container is attached to both networks:

  * `back-network` to communicate with PostgreSQL.
  * `front-network` to communicate with the reverse proxy.

### Reverse Proxy Container

The Apache reverse proxy is deployed using:

```yaml
- name: Launch Apache proxy
  community.docker.docker_container:
    name: apache
    image: yassinegharbi2627/httpd:1.0
    state: started
    recreate: true

    ports:
      - "80:80"

    env:
      BACKEND_HOST: "{{ backend_host }}"
      BACKEND_PORT: "{{ backend_port }}"

    networks:
      - name: front-network
```

Configuration:

* Port `80` of the container is exposed on the host.
* Environment variables define the backend target.
* The proxy is connected to the frontend network only.

### Deployment Architecture

```text
Client
   |
   v
Apache Proxy (port 80)
   |
   v
Spring Boot Application
   |
   v
PostgreSQL Database
```

Using the `docker_container` module allows infrastructure and application deployment to be fully automated and reproducible through Ansible.



## Bonus graphana 


## Objective

The application has been monitored using a modern observability stack based on:

* Spring Boot Actuator
* Micrometer
* Prometheus
* Grafana

The goal is to expose application metrics, collect them automatically and visualize them through dashboards.

---

## 1. Spring Boot Actuator

### Dependencies

The following dependencies were added to the backend application:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### Configuration

The following configuration was added to `application.yml`:

```yaml
management:
  server:
    add-application-context-header: false

  endpoints:
    web:
      exposure:
        include: health,info,env,metrics,beans,configprops,prometheus

  endpoint:
    health:
      show-details: always
```

---

## 2. Prometheus Metrics Endpoint

Once the application is deployed, Spring Boot exposes metrics through:

```text
/actuator/prometheus
```

Verification command:

```bash
wget -qO- http://localhost:8080/actuator/prometheus | head -20
```

result:

```text
admin@ip-10-0-1-208:~$ sudo docker exec -it simpleapi sh
/opt/myapp # wget -qO- http://localhost:8080/actuator/prometheus | head -20
# HELP application_ready_time_seconds Time taken for the application to be ready to service requests
# TYPE application_ready_time_seconds gauge
application_ready_time_seconds{main_application_class="fr.takima.training.simpleapi.SimpleApiApplication"} 17.099
# HELP application_started_time_seconds Time taken to start the application
# TYPE application_started_time_seconds gauge
application_started_time_seconds{main_application_class="fr.takima.training.simpleapi.SimpleApiApplication"} 16.891
# HELP disk_free_bytes Usable space for path
# TYPE disk_free_bytes gauge
disk_free_bytes{path="/opt/myapp/."} 8.73721856E8
# HELP disk_total_bytes Total space for path
# TYPE disk_total_bytes gauge
disk_total_bytes{path="/opt/myapp/."} 8.217726976E9
# HELP executor_active_threads The approximate number of threads that are actively executing tasks
# TYPE executor_active_threads gauge
executor_active_threads{name="applicationTaskExecutor"} 0.0
# HELP executor_completed_tasks_total The approximate total number of tasks that have completed execution
# TYPE executor_completed_tasks_total counter
executor_completed_tasks_total{name="applicationTaskExecutor"} 0.0
# HELP executor_pool_core_threads The core number of threads for the pool
# TYPE executor_pool_core_threads gauge
```

This confirms that Micrometer successfully exports metrics in Prometheus format.

---

## 3. Prometheus Deployment

### Configuration file

File: `prometheus.yml`

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'simpleapi'
    metrics_path: '/actuator/prometheus'

    static_configs:
      - targets: ['simpleapi:8080']
```

### Run Prometheus

```bash
docker run -d \
  --name prometheus \
  --network back-network \
  -p 9090:9090 \
  -v /home/admin/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus
```

---

## 4. Prometheus Validation

Check that Prometheus is running:

```bash
curl localhost:9090
```

Result:

```html
<a href="/query">Found</a>.
```

Check scrape targets:

```bash
curl http://localhost:9090/api/v1/targets
```

Result:

```json
"health":"up"
```

This confirms that Prometheus successfully scrapes the Spring Boot application.

---

## 5. Grafana Deployment

Run Grafana:

```bash
docker run -d \
  --name grafana \
  --network back-network \
  -p 3000:3000 \
  grafana/grafana
```

Verification:

```bash
curl localhost:3000
```

Result:

```html
<a href="/login">Found</a>.
```

Default credentials:

```text
Username: admin
Password: admin
```

---

## 6. Architecture

```text
Spring Boot Application
        │
        ▼
Spring Boot Actuator
        │
        ▼
/actuator/prometheus
        │
        ▼
Prometheus
        │
        ▼
Grafana
```

---

## 7. Monitoring Features

The monitoring stack provides:

* JVM memory usage
* CPU usage
* Application startup time
* Thread metrics
* Executor metrics
* HTTP metrics
* Disk usage
* Health indicators

---

## 8. Validation Results

The monitoring stack was successfully validated:

### Actuator

```bash
wget -qO- http://localhost:8080/actuator
```

Prometheus endpoint available:

```json
"prometheus":{"href":"http://localhost:8080/actuator/prometheus"}
```

### Prometheus Endpoint

```bash
wget -qO- http://localhost:8080/actuator/prometheus
```

Metrics successfully returned.

### Prometheus Target

```bash
curl http://localhost:9090/api/v1/targets
```

Result:

```json
"health":"up"
```

This confirms that Prometheus successfully collects metrics from the application.




## Limitations Encountered

During the monitoring implementation, the Prometheus and Grafana stack was successfully deployed and validated.

### VM Resource Limitation

The educational AWS VM provided for the project only offers approximately **1 GB of RAM** and does not have any swap memory configured.

After several hours of execution with the following services running simultaneously:

* PostgreSQL
* Spring Boot Backend
* Apache HTTP Server
* Nginx Frontend
* Prometheus
* Grafana

the Linux OOM Killer terminated the Java backend process due to memory exhaustion.

Evidence collected on the VM:

```bash
sudo dmesg -T | grep -i -E "killed process|out of memory|oom"
```

Output:

```text
[Thu Jun  4 06:45:13 2026] systemd invoked oom-killer
[Thu Jun  4 06:45:13 2026] oom-kill: task=java,pid=56720
[Thu Jun  4 06:45:14 2026] Out of memory: Killed process 56720 (java)
```

This confirms that the backend application was terminated by the Linux kernel because of insufficient available memory.

### Network Restrictions

Although Prometheus and Grafana were successfully deployed on the VM, their web interfaces could not be accessed directly from the Internet.

The ports used by these services:

* Grafana: `3000`
* Prometheus: `9090`

were not exposed through the infrastructure firewall/security configuration managed by the teaching team.

As a result:

* Local verification on the VM was successful using `curl`.
* Prometheus successfully scraped the Spring Boot metrics endpoint.
* Grafana and Prometheus were also deployed and tested locally on a workstation using Docker.
* Direct access through `http://<vm-host>:3000` and `http://<vm-host>:9090` was not possible from outside the VM.

Despite these infrastructure limitations, the monitoring stack was fully configured and validated:

* Spring Boot Actuator exposed metrics through `/actuator/prometheus`.
* Prometheus successfully collected metrics (`health: up`).
* Grafana successfully connected to Prometheus and was operational.
