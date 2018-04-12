package Silenium;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Created by Arnold on 09.04.2018.
 */
public class Otodom {
    private static final int TIME_OUT_IN_SECONDS_NEXT_PAGE = 2;
    private String curPage=null;
    private static final int TIME_OUT_IN_SECONDS_POPUP = 2;
    public static final String OFFERS_ITEM_IN_LIST = ".col-md-content article.offer-item";
    private static String startPage = "https://www.otodom.pl/wynajem/mieszkanie/warszawa/?search%5Bfilter_enum_media_types%5D%5B0%5D=internet&search%5Bfilter_enum_extras_types%5D%5B0%5D=balcony&search%5Bfilter_enum_extras_types%5D%5B1%5D=garage&search%5Bfilter_enum_extras_types%5D%5B2%5D=non_smokers_only&search%5Bphotos%5D=1&search%5Bdescription%5D=1&search%5Bdist%5D=0&search%5Bsubregion_id%5D=197&search%5Bcity_id%5D=26&search%5Border%5D=created_at_first%3Adesc&nrAdsPerPage=72";
    private RemoteWebDriver driver=null;
    public Otodom() {
        driver = new FirefoxDriver();
    }


    public boolean getPage() throws InterruptedException {
        if(curPage == null){
            curPage=startPage;
            this.driver.get(curPage);
        } else {
            WebElement link = this.getNextPage();
            if(link == null){
                return false;
            }
            curPage = link.getAttribute("href");
            link.click();
        }
        this.removePopUp();
        return true;
    }

    public List<WebElement> getListOffers() {
        List<WebElement> element = driver.findElements(By.cssSelector(OFFERS_ITEM_IN_LIST));
        return element;
    }

    public RemoteWebDriver getDriver(){
        return this.driver;
    }

    private void removePopUp() {
        WebDriverWait wait = new WebDriverWait(driver,TIME_OUT_IN_SECONDS_POPUP);

        try {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(@NullableDecl WebDriver webDriver) {
                    WebElement element = webDriver.findElement(By.cssSelector(".bootbox .modal-dialog"));
                    if (element.isDisplayed()) {
                        element.findElement(By.cssSelector(".bootbox-close-button.close")).click();

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                    else return false;
                }
            });
        } catch (TimeoutException e){
            return;
        } finally {
            try {
                driver.findElement(By.cssSelector(".cookiesBarClose.icon-close")).click();
            } catch (Exception e){

            }
        }
    }

    public WebElement getNextPage() {
        final WebElement[] link = {null};
        WebDriverWait wait = new WebDriverWait(driver,TIME_OUT_IN_SECONDS_NEXT_PAGE);
        try {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(@NullableDecl WebDriver webDriver) {
                    WebElement element = webDriver.findElement(By.cssSelector(".pager .pager-next>a"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                    if (element.isDisplayed()) {
                        link[0] = element;
                        return true;
                    }
                    else return false;
                }
            });
        } catch (TimeoutException e){
            return null;
        }
        return link[0];
    }
}
