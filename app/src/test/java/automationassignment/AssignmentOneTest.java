package automationassignment;

import static automationassignment.Utils.BASE_URL;
import static automationassignment.Utils.getNewWindowHandle;
import static automationassignment.Utils.sleep;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import automationassignment.pages.ReservePage;
import automationassignment.pages.ReservePage.Contact;
import automationassignment.pages.TopPage;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

@DisplayName("課題１")
public class AssignmentOneTest {
  
  private static WebDriver driver;

  private static WebDriverWait wait;

  private String originalHandle;
  
  @BeforeAll
  static void initAll() {
    driver = Utils.createWebDriver();
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  @BeforeEach
  void init() {
    originalHandle = driver.getWindowHandle();
  }

  @AfterEach
  void tearDown() {
    if (driver.getWindowHandles().size() > 1) {
      driver.close();
    }
    driver.switchTo().window(originalHandle);
    driver.manage().deleteAllCookies();
  }

  @AfterAll
  static void tearDownAll() {
    driver.quit();
  }

  @Test
  void assignmentOne() {
    driver.get(BASE_URL);
    var topPage = new TopPage(driver);
    var loginPage = topPage.goToLoginPage();
    var myPage = loginPage.doLogin("ichiro@example.com", "password");
    var originalHandles = driver.getWindowHandles();

    var plansPage = myPage.goToPlansPage();

    plansPage.openPlanByTitle("テーマパーク優待プラン");
    sleep(500);
    var newHandles = driver.getWindowHandles();
    var newHandle = getNewWindowHandle(originalHandles, newHandles);
    driver.switchTo().window(newHandle);

    var reservePage = new ReservePage(driver);
    reservePage.setReserveDate("2024/07/15");
    reservePage.setReserveTerm("3");
    reservePage.setHeadCount("2");
    reservePage.setBreakfastPlan(true);
    reservePage.setContact(Contact.電話でのご連絡);
    reservePage.setTel("00011112222");
    
    sleep(1000);
    assertEquals("66,000円", reservePage.getTotalPrice());

    var confirmPage = reservePage.goToConfirmPage();

    assertAll("プラン一覧",
        () -> assertEquals("2024年7月15日 〜 2024年7月18日 3泊", confirmPage.getTerm()),
        () -> assertEquals("2名様", confirmPage.getHeadCount()),
        () -> assertEquals("朝食バイキング", confirmPage.getPlans()),
        () -> assertEquals("電話：00011112222", confirmPage.getContact()),
        () -> assertEquals("なし", confirmPage.getComment())
    );

    confirmPage.doConfirm();

    assertEquals("ご来館、心よりお待ちしております。", confirmPage.getModalMessage());

    confirmPage.close();

    driver.close();
    driver.switchTo().window(originalHandle);
    assertEquals("https://hotel.testplanisphere.dev/ja/plans.html", driver.getCurrentUrl());
    
  }
  
}

