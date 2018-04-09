package Silenium;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Created by Arnold on 09.04.2018.
 */
public class Otodom {
    private static final int TIME_OUT_IN_SECONDS_POPUP = 5;
    public static final String OFFERS_ITEM_IN_LIST = ".col-md-content article.offer-item";
    private static String startPage = "https://www.otodom.pl/wynajem/mieszkanie/warszawa/?search%5Bfilter_enum_media_types%5D%5B0%5D=internet&search%5Bfilter_enum_extras_types%5D%5B0%5D=balcony&search%5Bfilter_enum_extras_types%5D%5B1%5D=garage&search%5Bfilter_enum_extras_types%5D%5B2%5D=non_smokers_only&search%5Bphotos%5D=1&search%5Bdescription%5D=1&search%5Bdist%5D=0&search%5Bsubregion_id%5D=197&search%5Bcity_id%5D=26&search%5Border%5D=created_at_first%3Adesc";
    private RemoteWebDriver driver=null;
    public Otodom() {
        driver = new FirefoxDriver();
    }


    public void getPage() {
        this.driver.get(startPage);
        this.removePopUp();
    }

    public List<WebElement> getListOffers() {
        List<WebElement> element = driver.findElements(By.cssSelector(OFFERS_ITEM_IN_LIST));
        return element;
    }

    private void removePopUp() {
        WebDriverWait wait = new WebDriverWait(driver,TIME_OUT_IN_SECONDS_POPUP);

        try {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(@NullableDecl WebDriver webDriver) {
                    WebElement element = webDriver.findElement(By.cssSelector(".modal-dialog"));
                    if (element.isDisplayed()) {
                        element.findElement(By.cssSelector(".bootbox-close-button .close")).click();
                        return true;
                    }
                    else return false;
                }
            });
        } catch (TimeoutException e){
            return;
        }
    }
}
