import Hibernate.Entity.Dom;
import Mail.SendMail;
import Silenium.Otodom;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arnold on 09.04.2018.
 */
public class MainClass {
    private static SessionFactory sessionFactory = null;
    private static ServiceRegistry serviceRegistry = null;

    private static void initSilenium() {
        System.setProperty("webdriver.gecko.driver", "d:\\Shikov\\java\\silenium\\geckodriver.exe");
    }


    private static SessionFactory configureSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.configure();

        Properties properties = configuration.getProperties();

        serviceRegistry = new ServiceRegistryBuilder().applySettings(properties).buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        return sessionFactory;
    }

    public static void main(String[] args) {
        initSilenium();
        configureSessionFactory();

        Session session = null;
        Transaction transaction = null;


        try {
            session = sessionFactory.openSession();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        Otodom otodom = new Otodom();

        otodom.getPage();

        ArrayList<Dom> newDom = new ArrayList<Dom>();

        String regex = "url\\(\"(.*)\"\\)";

        Pattern pattern = Pattern.compile(regex);

        for (WebElement element : otodom.getListOffers()) {
            String originalId = element.getAttribute("data-tracking-id");
            String name = element.findElement(By.cssSelector("h3>a .text-nowrap .offer-item-title")).getText();
            String cost = element.findElement(By.cssSelector(".offer-item-details .offer-item-price")).getText();

            String imgStyle = element.findElement(By.cssSelector(".img-cover")).getAttribute("style");
            String img = null;

            Matcher matcher = pattern.matcher(imgStyle);

            if(matcher.find()){
                img = matcher.group(1);
            }

            Dom dom = new Dom(originalId, name, cost, img);

            try {
                session = sessionFactory.openSession();
                Query query = session.createQuery("from Dom where originalId = :id").setParameter("id", originalId);

                List<?> list = query.list();

                if(list.size()>0){
                    continue;
                }

                transaction = session.beginTransaction();
                session.save(dom);
                session.flush();
                transaction.commit();
                newDom.add(dom);
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (session != null) {
                    session.close();
                }
            }
        }
        if(newDom.size()>0) {
            System.out.println("Send new!");
            (new SendMail()).send(newDom);
        }
    }
}
