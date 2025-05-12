package org.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.hibernate.SessionFactory;
import org.config.HibernateUtil; // votre classe utilitaire qui configure la SessionFactory

@WebListener
public class HibernateContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Crée et stocke la SessionFactory dans le contexte de l'application
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        sce.getServletContext().setAttribute("SessionFactory", sessionFactory);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Récupère et ferme la SessionFactory
        SessionFactory sessionFactory = (SessionFactory) sce.getServletContext().getAttribute("SessionFactory");
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
