package com.mazhangjing.muninn.v1.Entry;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EntryTest {
    private SessionFactory sessionFactory;
    private StandardServiceRegistry registry;
    private Session session;
    private Transaction transaction;
    @Before
    public void init() {
        sessionFactory = new MetadataSources(
                new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build()
                ).buildMetadata().buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }
   @After
    public void destroy() {
        transaction.commit();
        session.close();
        sessionFactory.close();
    }

    public void doIt() {
        session.save(new Book("b1","Thinking in Java","None","http://a.cn"));
    }
}