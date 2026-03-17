package base;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.reqres.test.engine.api.client.HeaderConfig;
import org.reqres.test.engine.utilities.logs.logsUtils;
import org.reqres.test.engine.utilities.properties.PropertiesManager;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Base test: WireMock + request spec. */
public class BaseTest {

    protected RequestSpecification requestSpec;

    private static WireMockServer wireMockServer;

    @BeforeSuite(alwaysRun = true)
    public void startWireMockIfEnabled() {
        PropertiesManager.initializeProperties();
        if (!"true".equalsIgnoreCase(PropertiesManager.getConfig("useWireMock").trim())) {
            logsUtils.info("wiremock disabled, using baseUrl from config");
            return;
        }
        Path wiremockRoot = resolveWireMockRoot();
        wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .dynamicPort()
                        .withRootDirectory(wiremockRoot.toAbsolutePath().toString())
        );
        wireMockServer.start();
        logsUtils.info("wiremock started on port ", String.valueOf(wireMockServer.port()));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (wireMockServer != null && wireMockServer.isRunning()) {
                wireMockServer.stop();
                wireMockServer = null;
                logsUtils.info("wiremock stopped (shutdown hook)");
            }
        }));
    }

    private static Path resolveWireMockRoot() {
        URL resource = BaseTest.class.getClassLoader().getResource("wiremock");
        if (resource != null && "file".equals(resource.getProtocol())) {
            try {
                return Paths.get(resource.toURI());
            } catch (Exception e) {
                logsUtils.warn("wiremock dir not found, using fallback: ", e.getMessage());
            }
        }
        return Paths.get("src/test/resources/wiremock").toAbsolutePath();
    }

    @AfterSuite(alwaysRun = true)
    public void stopWireMockIfEnabled() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            wireMockServer = null;
            logsUtils.info("wiremock stopped");
        }
    }

    @BeforeClass(alwaysRun = true)
    public void buildRequestSpec() {
        PropertiesManager.initializeProperties();
        String baseUri;
        if ("true".equalsIgnoreCase(PropertiesManager.getConfig("useWireMock").trim())
                && wireMockServer != null && wireMockServer.isRunning()) {
            baseUri = "http://localhost:" + wireMockServer.port();
        } else {
            baseUri = PropertiesManager.getConfig("baseUrl");
        }
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .addHeaders(new HeaderConfig().defaultHeaders())
                .build();
        logsUtils.info("using base url: ", baseUri);
    }
}
