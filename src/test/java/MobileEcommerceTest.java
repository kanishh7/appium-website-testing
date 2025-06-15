import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;

public class MobileEcommerceTest {
    private AppiumDriver driver;

    @BeforeClass
    public void setUp() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("deviceName", "Android Emulator");
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("chromedriverExecutable", "C:\\Users\\tejaswini\\chromedriver137\\chromedriver.exe");
        caps.setCapability("automationName", "UiAutomator2");
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
        driver.get("http://192.168.0.212:8000/index.html");
    }

    @Test
    public void testFullEcommerceFlow() throws Exception {
        // Wrong login attempt
        driver.findElement(By.id("username")).sendKeys("wronguser");
        driver.findElement(By.id("password")).sendKeys("wrongpass");
        driver.findElement(By.id("loginBtn")).click();
        Thread.sleep(1000);
        assert driver.getPageSource().contains("Wrong username/password");

        // Forgot password
        driver.findElement(By.id("forgotBtn")).click();
        Thread.sleep(1000);
        assert driver.getCurrentUrl().contains("forgot.html");

        // Submit email and go back to login
        driver.findElement(By.id("email")).sendKeys("test@example.com");
        driver.findElement(By.xpath("//form[@id='forgotForm']//button")).click();
        Thread.sleep(1700);
        driver.findElement(By.linkText("Back to Login")).click();
        Thread.sleep(1000);
        assert driver.getCurrentUrl().contains("index.html");

        // Correct login
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("username")).sendKeys("mobileuser");
        driver.findElement(By.id("password")).sendKeys("pass123");
        driver.findElement(By.id("loginBtn")).click();
        Thread.sleep(1200);
        assert driver.getCurrentUrl().contains("shop.html") || driver.getPageSource().contains("Shop");

        // Add Car to cart, handle alert
        driver.findElement(By.xpath("//div[@id='product1']//button")).click();
        driver.switchTo().alert().accept(); // Dismiss alert
        Thread.sleep(500);

        // Add Bike to cart, handle alert
        driver.findElement(By.xpath("//div[@id='product2']//button")).click();
        driver.switchTo().alert().accept(); // Dismiss alert
        Thread.sleep(500);

        // Add Boat to cart, handle alert (optional, if you want 3 items)
        driver.findElement(By.xpath("//div[@id='product3']//button")).click();
        driver.switchTo().alert().accept(); // Dismiss alert
        Thread.sleep(500);

        // Go to cart page
        driver.findElement(By.linkText("Go to Cart")).click();
        Thread.sleep(1000);

        // Screenshot of the cart page
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String filename = "cart_screenshot_" + System.currentTimeMillis() + ".png";
        Files.copy(scrFile.toPath(), new File(filename).toPath());


        // Check items in cart
        String page = driver.getPageSource();
        assert page.contains("Car") : "Car not found in cart";
        assert page.contains("Bike") : "Bike not found in cart";
        assert page.contains("Boat") : "Boat not found in cart"; // Optional, remove if only 2 items
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
