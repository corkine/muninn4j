package com.mazhangjing.muninn.v2.reward;

public enum Reward {
    NOTE_THIS_WEEK(1,"本周笔记数量","在本周（七天）内记录的 iPynb 笔记数量，包括新增和修改的笔记数量。"),
    POST_THIS_WEEK(2,"本周评论数量","在本周（七天）内记录的在任意笔记下的评论数量。"),
    NEW_HOPE(3,"新的希望","在本周记录笔记数大于1，0表示已完成，1表示未完成"),
    EXPLORE(4,"探索发现","在本周记录评论数大于1，0表示已完成，1表示未完成"),
    NOTE_TODAY(5,"新的一天","今天记录笔记的情况，包括新增和修改原来的笔记。0表示无，1表示有"),
    NOTE_2DAY(6,"坚持不懈","今天和昨天记录笔记的情况，包括新增和修改原来的笔记。0表示有中断，1表示已完成或者可能完成"),
    NOTE_3DAY(7,"三联冠军","今天、昨天和前天记录笔记的情况，包括新增和修改原来的笔记。0表示有中断，1表示已完成或者可能完成"),
    NOTE_WEEK(8,"完美一周","本周七天记录和修改笔记的情况，包括新增和修改原来的笔记。0表示有中断，1表示已完成或者可能完成"),
    POST_TODAY(9,"温故知新","今天记录的评论数量，包括对任意笔记进行的评论，0表示没有，1表示有"),
    POST_2DAY(10,"学而时习","今天和昨天均写了新的评论，0表示有中断，1表示已完成或者可能完成"),
    POST_3DAY(11,"勤思好学","今天、昨天和前天撰写评论，0表示有中断，1表示已完成或者可能完成"),
    POST_WEEK(12,"知行合一","在本周七天里，每天都有一条及其以上的评论，0表示否，1表示是"),
    NOTE_POST(13,"精益求精","为本周写的笔记添加评论，0表示未完成，1表示已完成"),
    NOBEL_PRIZE(14,"诺贝尔奖","在本周的七天里，每天修改或者记录一篇笔记，每天添加至少一条评论，0表示有中断，1表示无中断"),
    ;
    private final String desp;
    private final String name;
    private final Integer id;

    private Reward(Integer id, String name, String desp) {
        this.id = id; this.name = name; this.desp = desp;
    }

    public String getDesp() {
        return desp;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.getId() + "::" + this.getName() + "::" + this.getDesp();
    }
}
