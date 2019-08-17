package com.mazhangjing.muninn.v2.controller;

import com.mazhangjing.muninn.v2.annotation.NeedSessionUser;
import com.mazhangjing.muninn.v2.entry.*;
import com.mazhangjing.muninn.v2.service.LogService;
import com.mazhangjing.muninn.v2.service.LoginService;
import com.mazhangjing.muninn.v2.service.RewardService;
import com.mazhangjing.muninn.v2.service.VolumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/")
public class HelloController {

    private final LogService logService;

    private final VolumeService volumeService;

    private final LoginService loginService;

    private final RewardService rewardService;

    @Autowired
    public HelloController(LogService logService, LoginService loginService, VolumeService volumeService, RewardService rewardService) {
        this.logService = logService;
        this.loginService = loginService;
        this.volumeService = volumeService;
        this.rewardService = rewardService;
    }


    @RequestMapping("/")
    public String index(Map<String,Object> map, HttpSession session) {
        map.put("head", volumeService.getHeader());
        map.put("reward",rewardService.getReward());
        session.setAttribute("version",logService.getVersion());
        return "index";
    }

    /**处理课程的请求*/
    @RequestMapping("/volume/{volume_id}")
    public String volumeIndex(@PathVariable("volume_id") String volumeId,
                              Map<String,Object> map) {
        Volume volume = volumeService.getVolume(volumeId);
        map.put("volume",volume);
        map.put("head", volumeService.getHeader());
        return "volume";
    }

    /**处理章节的请求*/
    @RequestMapping("/volume/{volume_id}/chapter/{chapter_id}")
    public String chapterIndex(@PathVariable("volume_id") String volumeId,
                               @PathVariable("chapter_id") String chapterId,
                               Map<String, Object> map,
                               HttpServletRequest request,
                               HttpSession session){

        Volume volume = volumeService.getVolume(volumeId);
        Chapter chapter = volumeService.getChapter(volumeId, chapterId);
        chapter.setVolume(volume); //此处必须保证查出来的 chapter 包含 volume！！！
        map.put("volume",volume);
        map.put("head", volumeService.getHeader());
        map.put("chapter",chapter);

        //保留此地址，以备鉴权后跳转
        String requestURI = request.getRequestURI();
        session.setAttribute("from",requestURI);
        return "chapter";
    }


    /**处理新建笔记的提交的请求
     * 实现 CRUD 风格的 URL 编码在 TOMCAT8 上需要在 web.xml 为 Filter 添加 servlet Name 标签
     * 否则无法进行方法的隐藏和转发
     * 鉴权，并且调用服务层进行 PostScript 的添加操作
     * */
    @NeedSessionUser(UserType.USER)
    @RequestMapping(value = "volume/{volume_id}/chapter/{chapter_id}/postscript",method = RequestMethod.POST)
    public String addChapterCommit(@PathVariable("volume_id") String volumeId,
                                   @PathVariable("chapter_id") String chapterId,
                                   @ModelAttribute("postscript") PostScript postScript) {
        volumeService.addPostScript(chapterId,postScript);
        return String.format("redirect:/volume/%s/chapter/%s",volumeId,chapterId);
    }

    /**新建笔记的 POJO 提供者*/
    @ModelAttribute
    public void getPostScript(Map<String, Object> map) {
        PostScript postScript = new Link();
        postScript.setTime(new Date());
        map.put("postscript",postScript);
    }

    /**处理笔记的删除请求
     * 鉴权，并且调用服务层进行PostScript 的删除操作
     * */
    @NeedSessionUser(UserType.USER)
    @RequestMapping(value = "volume/{volume_id}/chapter/{chapter_id}/postscript/{postscript_id}",method = RequestMethod.DELETE)
    public String deleteChapterCommit(@PathVariable("volume_id") String volumeId,
                                      @PathVariable("chapter_id") String chapterId,
                                      @PathVariable("postscript_id") String psId) {

        volumeService.deletePostScript(volumeId,chapterId,psId);
        return String.format("redirect:/volume/%s/chapter/%s",volumeId,chapterId);
    }

    /**处理登录的页面展示请求*/
    @RequestMapping(value = "login",method = RequestMethod.GET)
    public String loginIndex() {
        return "login";
    }

    /**处理登录的页面处理请求
     * 处理登录的认证和之后的转发请求，从 session from 中取出之前的地址，完成转发，或者是重定向到 /
     * 如果无法通过验证，则回显表单*/
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(Map<String, Object> map, @ModelAttribute("token") UserToken token, HttpSession session) {

        Boolean isPassed = Optional.ofNullable(token)
                .map(u -> loginService.loginAndRemember(token,session))
                .orElse(false);
        String goTo = (String) Optional.ofNullable(session.getAttribute("from")).orElse("/");

        if (isPassed)
            return "redirect:"+goTo;
        else {
            map.put("name",token == null ? "User Name" : token.getName());
            map.put("user_callback","no_auth");
            //这里如果是重定向，所有 request 的 attr 都会放到 parm 中，而清空 request
            //如果是直接 get，那么将保留所有已提交的数据，这可能是因为HTTP属性域保留着原来的值
            return "redirect:/login";
        }
    }

    /**处理注销页面的请求*/
    @RequestMapping("logout")
    public String logout(HttpSession session, SessionStatus status) {
        loginService.logout(null,session,status);
        return "redirect:/";
    }

    @RequestMapping("about")
    public String aboutThisApp(Map<String, String> map) {
        map.put("version",logService.getVersion());
        map.put("log",logService.getLog());
        return "about";
    }

    @RequestMapping("/api/today")
    @ResponseBody
    @CrossOrigin(origins = "*",allowedHeaders = "*")
    public Object today() {
        return rewardService.getReward();
    }

    @RequestMapping("/api/todayChapter")
    @ResponseBody
    @CrossOrigin(origins = "*",allowedHeaders = "*")
    public Object changed() {
        System.out.println("In Changed");
        return volumeService.getTodayChapters();
    }

}
