package org.wso2.samples;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;
public class FileConnectorIT {
    @Rule
    public GenericContainer<?> appContainer = new GenericContainer<>("myapp/file-connector-project:1.0.0")
            .withExposedPorts(8290, 8253, 9164)
            // Use the cmd modifier to make the root filesystem read-only
            .withCreateContainerCmdModifier(cmd -> Objects.requireNonNull(cmd.getHostConfig()).withReadonlyRootfs(true))
            // Create a tmpfs mount at /tmp with read-write access
            .waitingFor(Wait.forHttp("/healthcheck").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(60)))
            .withTmpFs(getTmpFsMounts())
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));

    // Helper method to create the tmpfs map
    private static Map<String, String> getTmpFsMounts() {
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
        String createUrl = "http://" + host + ":" + port + "/fileconnector/create";
        System.out.println("URL: " + createUrl);

        String jsonBody = "{\n" +
                "\"filePath\":\"/tmp/create.txt\",\n" +
                "\"inputContent\": \"This is a test file\"\n" +
                "}";

        // Validate the container's behavior
        // Example: Check if the service is running properly by making HTTP requests
        // You can use RestAssured, HttpClient, etc., to verify the response
        // Invoke the url endpoint
        // Send the POST request with the JSON body
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(jsonBody)
                .post(createUrl);

        response.then().log().all();
        // Verify the response status code and content
        response.then().statusCode(202);

        // Interact with the container (e.g., send HTTP requests)
        String readUrl = "http://" + host + ":" + port + "/fileconnector/read";
        System.out.println("URL: " + readUrl);

        // Send the POST request with the JSON body
        Response readResponse = RestAssured.given()
                .contentType("application/json")
                .body("{\"filePath\":\"/tmp/create.txt\"}")
                .post(readUrl);

        readResponse.then().log().all();
        // Verify the response status code and content
        readResponse.then().statusCode(200);
        // Verify text content
        readResponse.then().body(containsString("This is a test file"));
    }
}
