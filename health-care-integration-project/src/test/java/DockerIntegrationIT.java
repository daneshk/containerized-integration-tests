import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;

public class DockerIntegrationIT {

    Network sharedNetwork = Network.newNetwork();
    GenericContainer<?> doctorinfoContainer = new GenericContainer<>("daneshk/backend-healthcare-service")
            .withExposedPorts(9090, 9091)
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
            .withNetwork(sharedNetwork)
            .withNetworkAliases("doctorinfo");

    @Rule
    public GenericContainer<?> appContainer = new GenericContainer<>("myapp/health-care-integration-project:1.0.0")
            .withExposedPorts(8290, 8253, 9164)
            // Use the cmd modifier to make the root filesystem read-only
            .withCreateContainerCmdModifier(cmd -> Objects.requireNonNull(cmd.getHostConfig()).withReadonlyRootfs(true))
            // Create a tmpfs mount at /tmp with read-write access
            .withTmpFs(getTmpFsMounts())
            .withNetwork(sharedNetwork)
            .withNetworkAliases("mi")
            .dependsOn(doctorinfoContainer)
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));

    // Helper method to create the tmpfs map
    private Map<String, String> getTmpFsMounts() {
        Map<String, String> tmpFsMounts = new HashMap<>();
        tmpFsMounts.put("/tmp", "rw"); // rw = read-write
        return tmpFsMounts;
    }



    @Test
    public void testAppContainer() {
        doctorinfoContainer.start();
        String appContainerLogs = doctorinfoContainer.getLogs();
        System.out.println("Backend App Container logs: " + appContainerLogs);

        appContainer.start();
        String miLogs = appContainer.getLogs();
        System.out.println("MI Container logs: " + miLogs);

        assertTrue("The container should be running", appContainer.isRunning());

        // Get the container's exposed port
        Integer port = appContainer.getMappedPort(8290);
        //int port = 8290;
        String host = appContainer.getHost();

        // Interact with the container (e.g., send HTTP requests)
        String url = "http://" + host + ":" + port + "/healthcare/doctor/Ophthalmologist";
        System.out.println("URL: " + url);

        // Validate the container's behavior
        // Example: Check if the service is running properly by making HTTP requests
        // You can use RestAssured, HttpClient, etc., to verify the response
        // Invoke the url endpoint
        Response response = RestAssured.get(url);
        // Verify the response status code and content
        response.then().statusCode(200);
        response.then().log().all();
        // Assert the length of the outer JSON array
        response.then().body("$", hasSize(2));  // Checks if outer array has 2 elements

        // Assert the length of inner arrays
        response.then().body("[0]", hasSize(2)); // Checks if the first inner array has 2 elements
        response.then().body("[1]", hasSize(2)); // Checks if the second inner array has 2 elements
    }
}
