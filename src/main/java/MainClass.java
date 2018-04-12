import Hibernate.Entity.Dom;
import Hibernate.*;
import Mail.SendMail;
import Silenium.Otodom;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arnold on 09.04.2018.
 */
public class MainClass {

    private static void initSilenium() {
        System.setProperty("webdriver.gecko.driver", "d:\\Shikov\\java\\silenium\\geckodriver.exe");
    }

    public static void main(String[] args) throws InterruptedException {
        initSilenium();
        DbConfigure dbConfigure = new DbConfigure();

        Otodom otodom = new Otodom();

        ArrayList<Dom> newDom = new ArrayList<Dom>();

        String regex = "url\\(\"(.*)\"\\)";

        Pattern pattern = Pattern.compile(regex);
        int i=0;
        while (otodom.getPage()) {
            for (WebElement element : otodom.getListOffers()) {
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

                Dom dom = new Dom(originalId, name, cost, img, url);

                if (dbConfigure.addEntry(dom)) {
                    newDom.add(dom);
                }
            }
//            if(i > 2) break;
//            i++;
        }
        if(newDom.size()>0) {
            System.out.println("Send new!");
            (new SendMail()).send(newDom);
        } else {
            System.out.println("No new offers!");
        }
        otodom.getDriver().close();
    }
}
