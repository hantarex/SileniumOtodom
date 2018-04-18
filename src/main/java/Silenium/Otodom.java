package Silenium;

import Hibernate.DbConfigure;
import Hibernate.Entity.Dom;
import Service.ArrayThread;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arnold on 09.04.2018.
 */
public class Otodom implements Runnable {
    private static final int TIME_OUT_IN_SECONDS_NEXT_PAGE = 2;
    private DbConfigure dbConfigure;
    private ArrayThread<Dom> newDom;
    private String curPage=null;
    private static final int TIME_OUT_IN_SECONDS_POPUP = 2;
    public static final String OFFERS_ITEM_IN_LIST = ".col-md-content article.offer-item";
    private static String startPage = "https://www.otodom.pl/wynajem/mieszkanie/warszawa/?search%5Bfilter_float_price%3Ato%5D=2300&search%5Bfilter_enum_extras_types%5D%5B0%5D=garage&search%5Bphotos%5D=1&search%5Bdescription%5D=1&search%5Border%5D=created_at_first%3Adesc&search%5Bdist%5D=0&search%5Bsubregion_id%5D=197&search%5Bcity_id%5D=26&nrAdsPerPage=72";
    private RemoteWebDriver driver=null;

    public Otodom(ArrayThread<Dom> newDom, DbConfigure dbConfigure) {
        driver = new FirefoxDriver();
        this.newDom = newDom;
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

    public ArrayList<Dom> modifyDb(DbConfigure dbConfigure) throws InterruptedException {
        ArrayList<Dom> newDom = new ArrayList<Dom>();

        String regex = "url\\(\"(.*)\"\\)";

        Pattern pattern = Pattern.compile(regex);
        int i=0;

        while (this.getPage()) {
            for (WebElement element : this.getListOffers()) {
                String originalId = element.getAttribute("data-tracking-id");
                String name = element.findElement(By.cssSelector("h3>a .text-nowrap .offer-item-title")).getText();
                String cost = element.findElement(By.cssSelector(".offer-item-details .offer-item-price")).getText();
                String url = element.getAttribute("data-url");

                String imgStyle = element.findElement(By.cssSelector(".img-cover")).getAttribute("style");
                String img = null;

                Matcher matcher = pattern.matcher(imgStyle);

                if (matcher.find()) {
                    img = matcher.group(1);
                }

                Dom dom = new Dom(originalId, name, cost, img, url, Dom.Otodom);

                if (dbConfigure.addEntry(dom)) {
                    newDom.add(dom);
                }
            }
//            if(i > 2) break;
//            i++;
        }
        return newDom;
    }

    @Override
    public void run() {
        try {
            this.newDom.merge(this.modifyDb(this.dbConfigure));
            this.getDriver().close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
