package com.mazhangjing.muninn.v1.DAO;

import com.mazhangjing.muninn.v1.Entry.Chapter;
import com.mazhangjing.muninn.v1.Entry.Volume;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class VolumeDAO {

    @Autowired SessionFactory sessionFactory;

    public List<Volume> getVolumes() {
        List<Volume> volumes = new ArrayList<>();
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        String hql = "select v from Volume v";
        Query query = session.createQuery(hql);
        volumes = query.list();
        transaction.commit();
        session.close();
        return volumes;
    }

    public Volume getVolume(String volumnId) {
        Volume volume;
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        String hql = "select v from Volume v left join fetch v.chapters where v.id = ?1";
        Query query = session.createQuery(hql);
        query.setParameter(1,volumnId);
        volume = (Volume) query.list().get(0);
        transaction.commit();
        session.close();
        return volume;
    }

    public Chapter getChapter(String volumeId, String characterId) {
        Chapter chapter = null;
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        String hql = "from Chapter c where c.volume.id = ?1 and c.id = ?2";
        Query query = session.createQuery(hql);
        query.setParameter(1,volumeId);
        query.setParameter(2,characterId);
        try {
            chapter =  (Chapter) query.list().get(0);
        } catch (Exception e) {}
        transaction.commit();
        session.close();
        return chapter;
    }
}
