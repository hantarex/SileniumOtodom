package Silenium;

import Hibernate.DbConfigure;
import Hibernate.Entity.Dom;
import Service.ArrayThread;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arnold on 09.04.2018.
 */
public class Olx implements Runnable{
    private static final int TIME_OUT_IN_SECONDS_NEXT_PAGE = 2;
    private final DbConfigure dbConfigure;
    private ArrayThread<Dom> newDom=null;
    private String curPage=null;
    private static final int TIME_OUT_IN_SECONDS_POPUP = 2;
    public static final String OFFERS_ITEM_IN_LIST = "#offers_table .wrap > .offer";
    private static String startPage = "https://www.olx.pl/nieruchomosci/mieszkania/wynajem/q-warszawa/?search%5Bfilter_float_price%3Ato%5D=2300&search%5Bphotos%5D=1";
    private RemoteWebDriver driver=null;
    public Olx(ArrayThread<Dom> newDom, DbConfigure dbConfigure) {
        this.newDom = newDom;
        driver = new FirefoxDriver();
        this.dbConfigure = dbConfigure;
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

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);


            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            link.click();
            WebDriverWait wait = new WebDriverWait(driver,TIME_OUT_IN_SECONDS_POPUP);
            try {
                wait.until(new ExpectedCondition<Boolean>() {
                    public Boolean apply(@NullableDecl WebDriver webDriver) {
                        WebElement newElement = webDriver.findElement(By.cssSelector("#loader"));
                        WebElement firstElementId = webDriver.findElement(By.cssSelector(OFFERS_ITEM_IN_LIST + ":first-child"));
                        try {
                            if (!newElement.isDisplayed()) {
                                return true;
                            } else return false;
                        } catch (StaleElementReferenceException e) {
                            return true;
                        }
                    }
                });
            } catch (TimeoutException e){
                return this.getPage();
            }

        }
        this.removePopUp();
        return true;
    }

    public List<WebElement> getListOffers() {
        List<WebElement> element = driver.findElements(By.cssSelector(OFFERS_ITEM_IN_LIST));
        return element;
    }

    public ArrayList<Dom> modifyDb(DbConfigure dbConfigure) throws InterruptedException {
        ArrayList<Dom> newDom = new ArrayList<Dom>();

        String regex = "url\\(\"(.*)\"\\)";

        Pattern pattern = Pattern.compile(regex);
        int i=0;

        while (this.getPage()) {
            for (WebElement element : this.getListOffers()) {
                String img = null;
                String originalId = element.findElement(By.cssSelector("table")).getAttribute("data-id");
                String name = element.findElement(By.cssSelector("h3>a>strong")).getText();
                String cost = element.findElement(By.cssSelector("p.price>strong")).getText();
                String url = element.findElement(By.cssSelector("tr>td>a.rel")).getAttribute("href");
                try {
                    img = element.findElement(By.cssSelector("tr>td>a.rel>img")).getAttribute("src");
                } catch (NoSuchElementException e){

                }

                Dom dom = new Dom(originalId, name, cost, img, url, Dom.Olx);

                if (dbConfigure.addEntry(dom)) {
                    newDom.add(dom);
                }
            }
//            break;
//            if(i > 3) break;
            i++;
        }
        return newDom;
    }

    public RemoteWebDriver getDriver(){
        return this.driver;
    }

    private void removePopUp() {
        WebDriverWait wait = new WebDriverWait(driver,TIME_OUT_IN_SECONDS_POPUP);

        try {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(@NullableDecl WebDriver webDriver) {
                    WebElement element = webDriver.findElement(By.cssSelector(".gtm-survey__close"));
                    if (element.isDisplayed()) {
                        element.findElement(By.cssSelector(".gtm-survey__close")).click();

                        return true;
                    }
                    else return false;
                }
            });
        } catch (TimeoutException e){
            return;
        } finally {
            try {
                driver.findElement(By.cssSelector("#favouritesBarClose")).click();
            } catch (Exception e){

            }
        }
    }

    public WebElement getNextPage() throws InterruptedException {
        final WebElement[] link = {null};
        WebDriverWait wait = new WebDriverWait(driver,TIME_OUT_IN_SECONDS_NEXT_PAGE);
        try {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(@NullableDecl WebDriver webDriver) {
                    WebElement element = webDriver.findElement(By.cssSelector(".fbold.next > a"));
                    ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

    @Override
    public void run() {
        try {
            newDom.merge(this.modifyDb(this.dbConfigure));
            this.getDriver().close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
