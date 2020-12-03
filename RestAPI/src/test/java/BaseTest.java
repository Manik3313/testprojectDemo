import io.testproject.sdk.DriverBuilder;
import io.testproject.sdk.drivers.TestProjectCapabilityType;
import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class BaseTest {

    private static Properties properties;
    private static String baseURL;
    private static String devToken;
    private static String browserStackUserName;
    private static String browserStackAccessKey;
    public static String cloudUrl;
    protected static String baseUrlApi;
    protected static ChromeDriver driver;
    private static boolean isBrowserStackRequired;
    ChromeOptions caps = new ChromeOptions();

    @BeforeSuite
    public void setConfig() throws IOException {
        properties = new Properties();
        String propFileName = "common.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        baseURL = properties.getProperty("url");
        devToken = properties.getProperty("dev_token");
        baseUrlApi = properties.getProperty("baseUrl_api");
        isBrowserStackRequired = Boolean.parseBoolean(properties.getProperty("isBrowserStackRequired"));

        // Setting the browser stack property if required
        if (isBrowserStackRequired) {
            browserStackUserName = properties.getProperty("browserStackUserName");
            browserStackAccessKey = properties.getProperty("browserStackAccessKey");
            cloudUrl = "https://" + browserStackUserName + ":" + browserStackAccessKey + "@hub-cloud.browserstack.com/wd/hub";
        }
    }

    @BeforeTest
    public void setBrowserCapabilities() {
        if (isBrowserStackRequired) {
            caps.setCapability("os", "Windows");
            caps.setCapability("os_version", "10");
            caps.setCapability("browser", "Firefox");
            caps.setCapability("browser_version", "80");
            caps.setCapability("name", "manikgupta5's First Test");
            caps.setCapability(
                    TestProjectCapabilityType.CLOUD_URL, cloudUrl);
        }

    }

    @BeforeMethod
    public void launchBrowser() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        if (isBrowserStackRequired)
            driver = new ChromeDriver(devToken, caps, "browserstackProject", "browserstackjob");
        else
            driver = new DriverBuilder<ChromeDriver>(new ChromeOptions())
                    .withRemoteAddress(new URL("http://localhost:8585"))
                    .withToken(devToken)
                    .withProjectName("TestProjectDemo").withJobName("TestProjectDemoJob")
                    .build(ChromeDriver.class);
        driver.navigate().to(baseURL);
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }
}
