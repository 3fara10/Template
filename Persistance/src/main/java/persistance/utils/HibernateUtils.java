package persistance.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtils {
    private static SessionFactory sessionFactory = null;

    public static synchronized SessionFactory getInstance() {

        if(sessionFactory == null){
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            try{
                sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            }catch (Exception e){
                System.err.println("Exceptie "+e);
                StandardServiceRegistryBuilder.destroy(registry);
            }
        }
        return sessionFactory;
    }

}
