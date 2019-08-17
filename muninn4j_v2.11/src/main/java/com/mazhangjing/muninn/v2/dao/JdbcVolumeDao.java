package com.mazhangjing.muninn.v2.dao;

import com.mazhangjing.muninn.v2.entry.*;
import com.mazhangjing.muninn.v2.util.JdbcUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 用于自动扫描 JupyterNotebook 并且更新数据库的 Dao 层
 * */
public class JdbcVolumeDao implements VolumeDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private QueryRunner queryRunner = new QueryRunner();

    @Override
    public List<Volume> getVolumes() {
        return null;
    }

    @Override
    public Volume getVolume(String volumeId) {
        return null;
    }

    @Override
    public Chapter getChapter(String volumeId, String characterId) {
        return null;
    }

    public void saveOrUpdateVolume(Volume volume) {
        Connection connection = JdbcUtils.getConnection();
        try {
            String checkId = "select TITLE from volumes where ID = ?";
            String volumeId = volume.getId() == null ? "WRONG_ID" : volume.getId();
            boolean exist = queryRunner.query(connection, checkId, ResultSet::next, volumeId);
            if (exist) {
                logger.info(String.format("get volume %s, will update now...", volumeId));
                if (volumeId.equalsIgnoreCase("WRONG_ID"))
                    throw new RuntimeException("提交对象的 id 不能为空");
                String update = "update volumes set type = ?, title = ?, intro = ?, link = ?, category = ?" +
                        "where id = ?";
                int result = queryRunner.update(connection, update,
                        volume.getClass().getSimpleName().toUpperCase(),
                        volume.getTitle(),
                        volume.getIntro(),
                        volume.getLink(),
                        volume.getCategory(),
                        volume.getId());
                if (result != 0) logger.info("Updating to Database now....");
                else logger.warn("更新对象过程中，数据库未返回受影响的行数");
            } else {
                logger.info(String.format("haven't get volume %s, will insert now...", volumeId));
                String insert = "insert into volumes (id,type,title,intro,link,category) values (?,?,?,?,?,?)";
                int result = queryRunner.update(connection, insert,
                        volume.getId() != null ? volumeId : "WRONG_ID",
                        volume.getClass().getSimpleName().toUpperCase(),
                        volume.getTitle(),
                        volume.getIntro(),
                        volume.getLink(),
                        volume.getCategory());
                if (result != 1) { throw new RuntimeException("插入新对象返回结果错误"); }
                else logger.info("Insert to Database now....");
            }
        } catch (SQLException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            logger.warn(writer.toString());
        } finally {
            JdbcUtils.release(null,null,connection);
        }
    }

    /**必须提供 Volume 的 Id，否则空指针异常*/
    public void saveOrUpdateChapter(Chapter chapter) {
        Connection connection = JdbcUtils.getConnection();
        try {
            String checkId = "select title from chapters where ch_id = ?";
            String chapterId = chapter.getId() == null ? "WRONG_ID" : chapter.getId();
            boolean exist = queryRunner.query(connection, checkId, ResultSet::next, chapterId);
            if (exist) {
                logger.info(String.format("get chapter %s, will update now...",chapterId));
                if (chapterId.equalsIgnoreCase("WRONG_ID"))
                    throw new RuntimeException("提交对象的 id 不能为空");
                String update = "update chapters set title = ?, intro = ?, edit_count = ?, note_count = ?," +
                        "last_edit = ?, vol_id = ?, content = ?, orders = ?, filename = ? where ch_id = ?";
                int result = queryRunner.update(connection, update,
                        chapter.getTitle(),
                        chapter.getIntro(),
                        chapter.getEditCount(),
                        chapter.getNoteCount(),
                        chapter.getLastEdit(),
                        chapter.getVolume().getId(),
                        chapter.getContent(),
                        chapter.getOrders(),
                        chapter.getFileName(),
                        chapter.getId()
                );
                if (result != 0) logger.info("Updating now....");
                else logger.warn("更新对象过程中，数据库未返回受影响的行数");
            } else {
                logger.info(String.format("haven't get chapter %s, will insert now...", chapterId));
                String insert = "insert into chapters (ch_id, title, intro, edit_count, note_count, last_edit," +
                        "vol_id, content, orders, filename) values (?,?,?,?,?,?,?,?,?,?)";
                int result = queryRunner.update(connection, insert,
                        chapter.getId() != null ? chapterId : "WRONG_ID",
                        chapter.getTitle(),
                        chapter.getIntro(),
                        chapter.getEditCount(),
                        chapter.getNoteCount(),
                        chapter.getLastEdit(),
                        chapter.getVolume().getId(),
                        chapter.getContent(),
                        chapter.getOrders(),
                        chapter.getFileName()
                );
                if (result != 1) {
                    throw new RuntimeException("插入新对象返回结果错误");
                } else logger.info("Insert to Database now....");
            }
        } catch (SQLException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            logger.warn(writer.toString());
        } finally {
            JdbcUtils.release(null,null,connection);
        }
    }

    public void savePostScripts(Chapter chapter, boolean deleteBeforeUpdate) {
        Connection connection = JdbcUtils.getConnection();
        try {
            String chapterId = chapter.getId() == null ? "WRONG_ID" : chapter.getId();
            String checkId = "delete from postscripts where ch_id = ?";
            if (chapterId.equalsIgnoreCase("WRONG_ID")) throw new RuntimeException("没有提供章节 Id");

            if (deleteBeforeUpdate) {
                int result = queryRunner.execute(connection, checkId, chapterId);
                if (result == 1) logger.warn("delete postScripts for Chapter " + chapterId);
                else logger.info("no postScripts have been found in Database.");
            }

            chapter.getPostscripts().stream().filter(
                    postScript -> postScript.getBody() != null || postScript.getTitle() != null
            ).forEach(postScript -> {
                String info = postScript.getTitle() != null ? postScript.getTitle() : postScript.getBody();
                logger.info(String.format("get postScript %s, will insert now...",info));

                String insert = "insert into postscripts (pos_id, postscript, title, body, time, ch_id, url, footer)" +
                        " values (?,?,?,?,?,?,?,?)";

                String url = null; String footer = null;
                try {
                    url = ((Link) postScript).getUrl();
                    footer = ((Quote) postScript).getFooter();
                } catch (ClassCastException e) {
                    logger.warn("Try to resolve subclass Info, cast failed. " +
                            "(Maybe you your insert a subclass, don't care it)");
                }

                try {
                    int update = queryRunner.update(connection, insert,
                            info.hashCode(),
                            postScript.getClass().getSimpleName().toUpperCase(),
                            postScript.getTitle(),
                            postScript.getBody(),
                            postScript.getTime(),
                            chapterId,
                            url,
                            footer);
                    if (update != 0)
                        logger.info(String.format("PostScript %s is inserted...",info));
                } catch (SQLException e) {
                    logger.warn("Can't insert this postscript, because maybe it have same " +
                            "primary key in database, If you disabled deleteBeforeUpdate, It is " +
                            "normal. The Message is: " + e.getMessage());
                }
            });
        } catch (SQLException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            logger.warn(writer.toString());
        } finally {
            JdbcUtils.release(null,null,connection);
        }
    }

    private void deletePostScriptsByChapter(Connection connection, String chapterId) {
        try {
            String deleteCommand = "delete from postscripts where ch_id = ?";
            int result = queryRunner.update(connection, deleteCommand, chapterId);
            if (result == 0) logger.info("No postscripts of chapter " + chapterId + " before in Database.");
            else logger.info("Delete postscripts of chapter " + chapterId + ", count line is " + result);
        } catch (SQLException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            logger.warn(writer.toString());
        }
    }

    public void deleteChaptersAndPostScriptsByVolume(String volumeId) {
        Connection connection = JdbcUtils.getConnection();
        try {
            String queryCommand = "select ch_id from chapters where vol_id = ?";
            ResultSetHandler<List<String>> handler = rs -> {
                List<String> list = new ArrayList<>();
                while (rs.next()) list.add(rs.getString(1));
                return list;
            };
            List<String> result = queryRunner.query(connection, queryCommand, handler, volumeId);
            result.stream()
                    .filter(chapterId -> !chapterId.isEmpty())
                    .forEach(chapterId -> deletePostScriptsByChapter(connection,chapterId));

            String deleteCommand = "delete from chapters where vol_id = ?";
            int result2 = queryRunner.update(connection, deleteCommand, volumeId);
            if (result2 == 0) logger.info("No postscripts of volume " + volumeId + " before in Database.");
            else logger.info("Delete postscripts of volume " + volumeId + ", count line is " + result2);
        }catch (SQLException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            logger.warn(writer.toString());
        } finally {
            JdbcUtils.release(null,null,connection);
        }
    }

    public void deleteChapterAndPostScripts(String chapterId) {
        Connection connection = JdbcUtils.getConnection();
        try {
           deletePostScriptsByChapter(connection,chapterId);
           String deleteMe = "delete from chapters where ch_id = ?";
           int result = queryRunner.execute(connection, deleteMe, chapterId);
           logger.info("Delete chapter " + chapterId + ", count line is " + result);
        } catch (SQLException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            logger.warn(writer.toString());
        } finally {
            JdbcUtils.release(null,null,connection);
        }
    }

    public List<String> queryAllChapterIdByVolume(String volumeId) {
        Connection connection = JdbcUtils.getConnection();
        try {
            String query = "select ch_id from chapters where vol_id = ?";
            return queryRunner.query(connection, query, resultSet -> {
                List<String> chapters = new ArrayList<>();
                while (resultSet.next()) chapters.add(resultSet.getString(1));
                logger.info("Getting chapters id by volume " + volumeId + ": " + chapters);
                return chapters;
            },volumeId);
        } catch (SQLException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            logger.warn(writer.toString());
        } finally {
            JdbcUtils.release(null,null,connection);
        } return null;
    }

    @Override
    public void addPostScript(String chapterId, PostScript postScript) {

    }

    @Override
    public void deletePostScript(String psId) {

    }

    @Override
    public Integer getPostScriptsCountInChapter(String chapterId) {
        return null;
    }

    @Override
    public List<Chapter> getChapters(Date date) {
        return null;
    }
}
