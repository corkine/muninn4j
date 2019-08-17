package com.mazhangjing.muninn.v2.dao;

import com.mazhangjing.muninn.v2.entry.PostScript;
import com.mazhangjing.muninn.v2.reward.Reward;
import javafx.geometry.Pos;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class RewardDao {

    private final SessionFactory factory;

    private Session getSession() { return factory.getCurrentSession(); }

    @Autowired public RewardDao(SessionFactory factory) { this.factory = factory; }

    public Map<Reward, Long> getReward() throws ParseException {
        HashMap<Reward, Long> map = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate before = getWeekStart(today);

        Session session = getSession();
        
        String hql = "select count(c) from Chapter c where c.lastEdit >= ?1";
        String hql2 = "select count(p) from PostScript p where p.time >= ?1";
        String hql3 = "select count(c) from Chapter c where c.lastEdit = ?1";
        String hql4 = "select count(p) from PostScript p where p.time = ?1";
        String hql5 = "select c.postscripts from Chapter c where c.lastEdit >= ?1 and c.lastEdit < ?2";
        
        
        Query query = session.createQuery(hql);
        Long chapterCount = (Long) query.setParameter(1, conv(before)).getSingleResult();
        
        Query query2 = session.createQuery(hql2);
        Long postCount = (Long) query2.setParameter(1, conv(before)).getSingleResult();
        
        Query cq = session.createQuery(hql3);
        Query pq = session.createQuery(hql4);
        Query fq = session.createQuery(hql5);

        List<Long> resultChapter = getResult(today, cq).stream().map(boo -> boo ? 1L : 0L).collect(Collectors.toList());
        List<Long> resultPost = getResult(today, pq).stream().map(boo -> boo ? 1L : 0L).collect(Collectors.toList());

        boolean we = false;
        try {
            Set<PostScript> res  = (Set<PostScript>) fq.setParameter(1, conv(getWeekStart(today)))
                    .setParameter(2, conv(getWeekStart(today).plusDays(6))).getSingleResult();
            we = !res.isEmpty();
        } catch (Exception e) {}

        map.put(Reward.NOTE_THIS_WEEK, chapterCount);
        map.put(Reward.POST_THIS_WEEK, postCount);
        map.put(Reward.NEW_HOPE, chapterCount > 0 ? 1L : 0L);
        map.put(Reward.EXPLORE, postCount > 0 ? 1L : 0L);
        map.put(Reward.NOTE_TODAY, resultChapter.get(0));
        map.put(Reward.NOTE_2DAY, resultChapter.get(1));
        map.put(Reward.NOTE_3DAY, resultChapter.get(2));
        map.put(Reward.NOTE_WEEK, resultChapter.get(3));
        map.put(Reward.POST_TODAY, resultPost.get(0));
        map.put(Reward.POST_2DAY, resultPost.get(1));
        map.put(Reward.POST_3DAY, resultPost.get(2));
        map.put(Reward.POST_WEEK, resultPost.get(3));
        map.put(Reward.NOBEL_PRIZE, resultChapter.get(3)!= 0 && resultPost.get(3) != 0 ? 1L : 0L);
        map.put(Reward.NOTE_POST, we ? 1L : 0L);
        return map;
}

    private List<Boolean> getResult(LocalDate today, Query cq) {
        boolean d1 = false; boolean d2 = false; boolean d3 = false; boolean d7 = false;
        switch (today.getDayOfWeek()) {
            case MONDAY:
                //如果是周一，若没有完成任务，则这周就毁了，NOTE_TODAY, NOTE_2DAY, NOTE_3DAY, NOTE_WEEK 均为 false
                //如果完成了任务，则认为所有 NOTE 开头的奖牌都有希望
                if (((Long) cq.setParameter(1, conv(today)).getSingleResult()) != 0) {
                    d1 = true; d2 = true; d3 = true; d7 = true;
                } //否则全不给
                break;
            case TUESDAY:
                //如果是周二的话，先判断今天，再判断昨天
                if (((Long) cq.setParameter(1, conv(today)).getSingleResult()) != 0) {
                    if (((Long) cq.setParameter(1, conv(today.minusDays(1))).getSingleResult()) != 0) {
                        //如果两天均完成，那么1级奖牌给，2级奖牌给，3级奖牌给，7级奖牌给
                        d1 = true; d2 = true; d3 = true; d7 = true;
                    } else {
                        //如果昨天没有完成任务，但今天完成了，那么1级奖牌给，其余不给
                        d1 = true; d2 = false; d3 = false; d7 = false;
                    }
                }  //如果今天任务没有完成，则1级奖牌不给，2级奖牌不给，3级奖牌不给，7级奖牌不给
                break;
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
            case SATURDAY:
            case SUNDAY:
                //如果是周三、四、五、六、日的话，先判断今天，再判断昨天，再判断前天
                if (((Long) cq.setParameter(1, conv(today)).getSingleResult()) != 0) {
                    //如果今天完成目标，则继续判断昨天
                    if (((Long) cq.setParameter(1, conv(today.minusDays(1))).getSingleResult()) != 0) {
                        //如果昨天完成目标，则继续判断前天
                        if (((Long) cq.setParameter(1, conv(today.minusDays(2))).getSingleResult()) != 0) {
                            //如果前天完成目标，则三天均完成目标，则所有奖牌全给
                            d1 = true; d2 = true; d3 = true; d7 = true;
                        } else {
                            //如果前天没有完成目标，而昨天完成，今天完成，则3级奖牌不给，7级奖牌不给，其余给
                            d1 = true; d2 = true; d3 = false; d7 = false;
                        }
                    } else {
                        //如果昨天没有完成目标，而今天完成目标，则1级奖牌给，2级不给，3级不给，7级不给
                        d1 = true; d2 = false; d3 = false; d7 = false;
                    }
                }  //如果今天没有完成目标，则1级不给，2级不给，3级不给，7级不给
                break;
            default: break;
        }
        return Arrays.asList(d1,d2,d3,d7);
    }

    private LocalDate getWeekStart(LocalDate today) {
        return today.with(temporal -> {
            LocalDate localDate = (LocalDate) temporal;
            switch (localDate.getDayOfWeek()) {
                case MONDAY:
                    return localDate.minusDays(0);
                case TUESDAY:
                    return localDate.minusDays(1);
                case WEDNESDAY:
                    return localDate.minusDays(2);
                case THURSDAY:
                    return localDate.minusDays(3);
                case FRIDAY:
                    return localDate.minusDays(4);
                case SATURDAY:
                    return localDate.minusDays(5);
                case SUNDAY:
                    return localDate.minusDays(6);
                default:
                    return null; }
        });
    }
    
    private Date conv(LocalDate date) {
        ZoneId zoneId = ZoneId.systemDefault();
        return Date.from(date.atStartOfDay(zoneId).toInstant());
    }

}
