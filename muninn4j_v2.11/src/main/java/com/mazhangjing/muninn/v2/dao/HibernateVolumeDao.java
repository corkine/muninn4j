package com.mazhangjing.muninn.v2.dao;

import com.mazhangjing.muninn.v2.entry.Chapter;
import com.mazhangjing.muninn.v2.entry.PostScript;
import com.mazhangjing.muninn.v2.entry.Volume;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class HibernateVolumeDao implements VolumeDao {

    private final static Logger logger = LoggerFactory.getLogger(HibernateVolumeDao.class);

    @Autowired SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
        //return sessionFactory.openSession();
    }

    public List<Volume> getVolumes() {
        List<Volume> volumes = null;
        Session session = getSession();
        String hql = "select distinct v from Volume v";
        Query query = session.createQuery(hql);
        volumes = query.list();
        return volumes;
    }

    public Volume getVolume(String volumnId) {
        Volume volume;
        Session session = getSession();
        String hql = "select v from Volume v left join fetch v.chapters where v.id = ?1";
        Query query = session.createQuery(hql);
        query.setParameter(1,volumnId);
        volume = (Volume) query.list().get(0);
        return volume;
    }

    public Chapter getChapter(String volumeId, String characterId) {
        Chapter chapter = null;
        Session session = getSession();
        String hql = "select c from Chapter c left join fetch c.volume left join fetch c.postscripts where c.volume.id = ?1 and c.id = ?2";
        Query query = session.createQuery(hql);
        query.setParameter(1,volumeId);
        query.setParameter(2,characterId);
        try {
            chapter =  (Chapter) query.list().get(0);
        } catch (Exception e) {}
        return chapter;
    }

    @Override
    public void addPostScript(String chapterId, PostScript postScript) {
        //设置 postscript 的 id，如果为 null，直接返回，否则进行保存
        //单向多对一关系，单纯保存一侧是没有用的
        Optional.ofNullable(postScript).ifPresent(post -> {
            post.setId(String.valueOf(post.hashCode()));
        });
        if (postScript == null) return;

        Session session = getSession();
        Chapter chapter = session.get(Chapter.class,chapterId);
        Optional.ofNullable(chapter)
                .filter(chapt -> chapt.getPostscripts() != null)
                .map(ch -> ch.getPostscripts().add(postScript));
        session.save(chapter);
        session.save(postScript);
    }

    @Override
    public void deletePostScript(String psId) {
        Session session = getSession();
        PostScript postScript = session.get(PostScript.class, psId);
        Optional.ofNullable(postScript).ifPresent(session::delete);
    }

    @Override
    public Integer getPostScriptsCountInChapter(String chapterId) {
        Session session = getSession();
        Chapter chapter = session.get(Chapter.class, chapterId);
        return Optional.ofNullable(chapter)
                .filter(chap -> chap.getPostscripts() != null)
                .map(cha -> cha.getPostscripts().size()).orElse(null);
    }

    @Override
    public List<Chapter> getChapters(Date date) {
        List<Chapter> chapters = new ArrayList<>();
        try {
            Session session = getSession();
            String hql = "select c from Chapter c left join fetch c.postscripts where c.lastEdit = ?1";
            Query query = session.createQuery(hql);
            query.setParameter(1, date);
            chapters = query.list();
        } catch (Exception e) {
            logger.warn("在执行方法 getChapters 时发生错误： " + e.getMessage());
        }
        return chapters;
    }

    @Override
    public void saveOrUpdateVolume(Volume volume) { }

    @Override
    public void saveOrUpdateChapter(Chapter chapter) { }

    @Override
    public void savePostScripts(Chapter chapter, boolean deleteBeforeUpdate) { }

    @Override
    public void deleteChaptersAndPostScriptsByVolume(String volumeId) { }

    @Override
    public void deleteChapterAndPostScripts(String chapterId) { }

    @Override
    public List<String> queryAllChapterIdByVolume(String volumeId) {
        return null;
    }
}
