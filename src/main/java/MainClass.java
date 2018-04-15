import Hibernate.Entity.Dom;
import Hibernate.*;
import Mail.SendMail;
import Service.ArrayThread;
import Silenium.Olx;
import Silenium.Otodom;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
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

        final ArrayThread<Dom> newDom = new ArrayThread<>();

        try (DbConfigure dbConfigure = new DbConfigure()) {
            Otodom otodom = new Otodom(newDom, dbConfigure);
            Olx olx = new Olx(newDom, dbConfigure);

            Thread otodomThread = new Thread(otodom);
            Thread olxThread = new Thread(olx);

            otodomThread.start();
            olxThread.start();

            otodomThread.join();
            olxThread.join();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(newDom.size()>0) {
            System.out.println("Send new!");
            (new SendMail()).send(newDom);
        } else {
            System.out.println("No new offers!");
        }
    }
}
