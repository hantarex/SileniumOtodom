package Hibernate;

import Hibernate.Entity.Dom;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.List;
import java.util.Properties;

/**
 * Created by Arnold on 12.04.2018.
 */
public class DbConfigure {
    private final ServiceRegistry serviceRegistry;
    private final SessionFactory sessionFactory;
    private Session session = null;
    private Transaction transaction = null;


    public DbConfigure() throws HibernateException {
            Configuration configuration = new Configuration();
            configuration.configure();

            Properties properties = configuration.getProperties();

            serviceRegistry = new ServiceRegistryBuilder().applySettings(properties).buildServiceRegistry();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            session = sessionFactory.openSession();
    }

    public boolean addEntry(Dom dom){
        boolean isUpdate=false;
        try {
            session = sessionFactory.openSession();
            Query query = session.createQuery("from Dom where originalId = :id").setParameter("id", dom.getOriginalId());

            List<?> list = query.list();

            if(list.size()>0){
                Dom oldDom = (Dom) list.get(0);

                if(oldDom.getCost().equals(dom.getCost())) {
                    return false;
                }
                oldDom.setOldCost(oldDom.getCost());
                oldDom.setCost(dom.getCost());
                dom=oldDom;
            }

            transaction = session.beginTransaction();
            session.saveOrUpdate(dom);
            session.flush();
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return false;
    }
}
