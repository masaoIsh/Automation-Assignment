package automationassignment;

import static automationassignment.Utils.BASE_URL;
import static automationassignment.Utils.getNewWindowHandle;
import static automationassignment.Utils.sleep;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import automationassignment.pages.ReservePage;
import automationassignment.pages.TopPage;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;


@DisplayName("課題２")
public class AssignmentTwoTest {
  
  private static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

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
  @DisplayName("サンプルコードがテストしていないプランで不正な入力値でエラーとなること_大")
  void testInvalidInputBig() {
    driver.get(BASE_URL);
    var topPage = new TopPage(driver);
    var originalHandles = driver.getWindowHandles();

    var plansPage = topPage.goToPlansPage();
    plansPage.openPlanByTitle("カップル限定プラン");
    sleep(500);
    var newHandles = driver.getWindowHandles();
    var newHandle = getNewWindowHandle(originalHandles, newHandles);
    driver.switchTo().window(newHandle);
    var reservePage = new ReservePage(driver);

    var after90 = SHORT_FORMATTER.format(LocalDate.now().plusDays(91));

    reservePage.setReserveDate(after90);
    reservePage.setReserveTerm("3");
    reservePage.setHeadCount("3");
    reservePage.setUsername("テスト太郎"); 

    assertAll("エラーメッセージ",
        () -> assertEquals("3ヶ月以内の日付を入力してください。", reservePage.getReserveDateMessage()),
        () -> assertEquals("2以下の値を入力してください。", reservePage.getReserveTermMessage()),
        () -> assertEquals("2以下の値を入力してください。", reservePage.getHeadCountMessage())
    );
  }


  
}
