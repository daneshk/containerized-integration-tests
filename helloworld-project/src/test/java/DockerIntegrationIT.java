import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;

public class DockerIntegrationIT {
    @Rule
    public GenericContainer<?> appContainer = new GenericContainer<>("wso2inc/hello-k8s:v0.1.0")
            .withExposedPorts(8290, 8253, 9164)
            // Use the cmd modifier to make the root filesystem read-only
            .withCreateContainerCmdModifier(cmd -> Objects.requireNonNull(cmd.getHostConfig()).withReadonlyRootfs(true))
            // Create a tmpfs mount at /tmp with read-write access
            .waitingFor(Wait.forHttp("/helloworld").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(60)))
            .withTmpFs(getTmpFsMounts())
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));

    // Helper method to create the tmpfs map
    private Map<String, String> getTmpFsMounts() {
        Map<String, String> tmpFsMounts = new HashMap<>();
        tmpFsMounts.put("/tmp", "rw"); // rw = read-write
        return tmpFsMounts;
    }



    @Test
    public void testAppContainer() {
        appContainer.start();
        String logs = appContainer.getLogs();
        System.out.println("Container logs: " + logs);

        assertTrue("The container should be running", appContainer.isRunning());

        // Get the container's exposed port
        Integer port = appContainer.getMappedPort(8290);
        String host = appContainer.getHost();

        // Interact with the container (e.g., send HTTP requests)
        String url = "http://" + host + ":" + port + "/helloworld";
        System.out.println("URL: " + url);

        // Validate the container's behavior
        // Example: Check if the service is running properly by making HTTP requests
        // You can use RestAssured, HttpClient, etc., to verify the response
        // Invoke the url endpoint
        Response response = RestAssured.get(url);
        // Verify the response status code and content
        response.then().statusCode(200);
        response.then().body("message", equalTo("Hello World"));
    }
}
