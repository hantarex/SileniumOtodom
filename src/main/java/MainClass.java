import Hibernate.Entity.Dom;
import Hibernate.*;
import Mail.SendMail;
import Silenium.Olx;
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

        ArrayList<Dom> newDom = new ArrayList<Dom>();

        Otodom otodom = new Otodom();
        newDom.addAll(otodom.modifyDb(dbConfigure));
        otodom.getDriver().close();

        Olx olx = new Olx();
        newDom.addAll(olx.modifyDb(dbConfigure));
        olx.getDriver().close();

        if(newDom.size()>0) {
            System.out.println("Send new!");
            (new SendMail()).send(newDom);
        } else {
            System.out.println("No new offers!");
        }
    }
}
