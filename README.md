# Currency Converter

A Spring Boot currency converter with a static web page served by the same application. The browser calls the API on the same origin, so no frontend server or separate API URL is needed.

## Run locally

You need Java 21. From the repository root, start the application with the Maven wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Open <http://localhost:8080/>. The conversion API can also be tested directly:

```text
http://localhost:8080/api/convert?from=USD&to=INR&amount=100
```

Run tests and package the executable JAR:

```powershell
.\mvnw.cmd clean package
```

Then run the packaged application:

```powershell
java -jar target\currency-converter-0.0.1-SNAPSHOT.jar
```

## Build and run with Docker

Install Docker Desktop, open a terminal in the repository root, and build the image:

```powershell
docker build -t currency-converter .
```

Start a container and publish its port:

```powershell
docker run --rm -p 8080:8080 currency-converter
```

Visit <http://localhost:8080/> or test <http://localhost:8080/api/convert?from=USD&to=INR&amount=100>. Stop the container with Ctrl+C.

## Push the project to GitHub

1. Sign in to GitHub and create a new empty repository. Do not initialize it with a README if you plan to push this existing README.
2. From this project directory, initialize Git if it is not already initialized, then stage and commit the project:

   ```powershell
   git init
   git add .
   git commit -m "Prepare currency converter for deployment"
   ```

3. Add the URL of your GitHub repository as `origin` and push the branch:

   ```powershell
   git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPOSITORY.git
   git branch -M main
   git push -u origin main
   ```

   Replace the example URL with your repository's URL. If `origin` is already configured, use `git remote set-url origin ...` instead of adding it again. Never add credentials or secret environment files to Git.

## Deploy as a Render Web Service

Render's dashboard labels, free-tier availability, and limits can change. Confirm the current options and limits in [Render's free-instance documentation](https://render.com/docs/free), [Docker documentation](https://render.com/docs/docker), and [Web Services documentation](https://render.com/docs/web-services) before creating the service.

1. In the Render dashboard, choose **New** → **Web Service**, connect GitHub, and select the repository and branch you pushed.
2. Set the **Root Directory** to the repository root. For this project, that is the directory containing `pom.xml` and `Dockerfile` (leave the setting blank if the dashboard uses the repository root by default; otherwise enter `.`).
3. Choose **Docker** as the runtime/environment and select the **Free** instance type if it is currently offered for your account and region.
4. Set the Dockerfile path to `./Dockerfile` relative to the root directory. Leave Docker build and start commands at their Docker defaults.
5. Create the Web Service and wait for the Docker build and deployment to finish. Render supplies the `PORT` environment variable; the application listens on that port and binds to all network interfaces.
6. Open the public `onrender.com` URL shown on the service page. Test the homepage at `/` and the API at `/api/convert?from=USD&to=INR&amount=100`.

Free services may become inactive or sleep after a period without requests, and the next request can take longer while the service starts. Check Render's current free-instance documentation for the exact sleep behavior, startup delay, usage limits, and eligibility; these details can change.

## Troubleshooting

- **Docker build fails:** Confirm the service root directory contains `pom.xml`, `.mvn/`, `mvnw`, and `Dockerfile`. Check the Render build logs for Maven or Java errors. This project targets Java 21; the Dockerfile's builder and runtime use Eclipse Temurin 21 images.
- **Service does not start or reports a port error:** Keep `server.port=${PORT:8080}` and `server.address=0.0.0.0` in `src/main/resources/application.properties`. Do not replace the port placeholder with a fixed cloud port; Render provides `PORT`.
- **Homepage returns 404:** Confirm the deployment is built from the repository root and that `src/main/resources/static/index.html` is present in the pushed branch. The frontend is served by Spring Boot at `/`.
- **API request fails:** Test the exact API path on the same public service URL and inspect the Render runtime logs. The service needs outbound network access to `https://open.er-api.com/v6/latest`.
