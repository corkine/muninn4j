#/usr/bin/env python3
# -*- encoding: utf8 -*-
server_version  = "0.2.3"
server_log = """
0.0.3 2018-06-11 使用model提供的类方法而不是对象属性重构了项目，使用API解耦合。
                添加了Pickle存储对象，方便进行调试。
                对于从源HTML中获取Chapter的信息，提供了一个类方法，现在只需要一句话就可以完全自动构建Course对象，
                其中自动化Course的信息、包含章节的信息以及每个章节相关笔记的信息。
0.0.4 2018-06-11 完善related_notes笔记接口，提供三种标准（问题、名言、博客类型），整个程序大体完成。
0.1.0 Alpha 上线测试
0.1.1 2018年6月18日 添加了 ‘explain’ 类型的笔记。给每个笔记添加了链接。解决了目录显示顺序随机的问题。
0.2.0 2018年7月14日 修正了一些错误，优化了转换ipynb文件的逻辑，提高了健壮性，现在复制文件会根据时间来计算，避免了全局复制，减小更新负担。
                    转化文件按照文件夹顺序，加快了程序运行。降低了配置文件复杂程度，现在不需要声明允许的文件夹信息。
0.2.2 2018年7月14日 服务器上线，修正了一些日志的逻辑问题，添加了注释，不再保存数据到last_data中。
0.2.3 2018年7月15日 修复convert模块中一个导致日期判断出错的bug。
"""

from muninn_config import *
import random,os,re,pickle
from model import *
import convert,traceback

STOP = False

def constructWorld(courses,head=None,notes=None):
    """构建OO对象，仅初始化课程及其包含的章节，不包括章节具体标题、描述和相关笔记。"""
    clist = []
    #head包含了章节顺序信息，其也是courses字典的key值

    #从config.py中配置Course信息，包括Course信息，其包含的Chapter信息（构建Chapter对象）
    # 以及对应地址包含的Chapter标题、描述和相关Note对象信息

    try:
        for key in head:
            c = Course().set_config(courses[key])
            clist.append(c)
    except: 
        print("\n"+"X"*20,"\n课程添加出错，可能是索引不正确..\n","X"*20+"\n")
        print(traceback.format_exc())

    try:
        convert.transData(clist,from_source=JUPYTER_NOTEBOOK_ROOT_FOLDER,
                            to_source=HTML_DST_FOLDER+SEP)
        needfiles = convert.findIpynb(clist,from_source=JUPYTER_NOTEBOOK_ROOT_FOLDER,
                            to_source=HTML_DST_FOLDER+SEP)
        if len(needfiles) == 0: 
            STOP = True
            return []
        convert.convertNotes(clist,"source",needfiles=needfiles)
    except:
        print("\n"+"X"*20,"在转换文件时出错","X"*20+"\n")
        print(traceback.format_exc())

    try:
        for course in clist:
            course.set_chapter(chapter_address="source",get_header=True,get_description=True,get_notes=True,reload_info=True)
    except:
        print("\n"+"X"*20,"在写入章节信息时出错","X"*20+"\n")
        print(traceback.format_exc())

    return clist

def main(update_data=False,file_path=""):
    #首先调用构建OO对象的函数，构建课程集
    print("="*20,"开始处理数据","="*20)
    if update_data:
        print("正在构建项目")
        clist = constructWorld(COURSE_INFO,COURSE_HEAD)
        #p = pickle.dump(clist,open(file_path,"wb"))
        #print("项目构建完毕，并且存放在:%s"%file_path)
    else:
        print("从备份中读取项目")
        clist = pickle.load(open(file_path,"rb"))
    if STOP or len(clist) == 0:
        print("没有更新内容，本次更新已跳过.")
        return 0

    #首先根据所有项目生成一个HTML页面的菜单，点击链接到下面创建的html文件中
    menu_html = makeHead(clist)
    # 对于index页面进行生成
    index_html = make_Index(menu_html)
    writeToFile(index=True,html=index_html)
    # #接着根据每个页面的信息生成单独的页面，每个页面的命名根据课程名称进行命名
    for c in clist: 
        overview = makeHTMLPage(c,menu_html)
        print("\t生成HTML::课程总览",c)
        writeToFile(c,overview)
        for ch in c.chapters:
            print("\t\t生成HTML::章节信息",ch)
            ch_html = makeHTMLPage(c,menu_html,is_chapter=True,chapter_id=ch.id)
            writeToFile(c,ch_html,suffix="_ch_%s.html"%ch.id)
    print("\n"+"="*20,"FINISHED","="*20+"\n")

def makeHead(clist):
    """根据所有的课程信息返回一个HTML语言的导航条"""
    out_html = ""
    dropdown_html = ""
    finished_2st = []
    for c in clist:
        if not c.have_father():
            out_html += """ <li class="nav-item">
                                <a class="nav-link" href="%s">%s</a>
                            </li>
                            """%("/"+c.get_uri(full_path=True,suffix="_overview.html"),c.get_name())
            #print("here :",c.get_uri(full_path=True,suffix="_overview.html"))
        else:
            rand_a = random.randint(1000,9999)
            sub_html = ""
            for cs in clist:
                if (cs.get_name(fname=True) in finished_2st) or (not cs.have_father()) or\
                         (cs.get_name(fname=True) != c.get_name(fname=True)):
                    continue
                else:
                    if not cs.get_type():
                        _type = ""
                    else: _type = cs.get_type()
                    sub_html += """         
                                        <a class="dropdown-item" href="%s">%s <small>%s</small></a>
                            """%("/"+cs.get_uri(full_path=True,suffix="_overview.html"),cs.get_name(),_type)
            if not c.get_name(fname=True) in finished_2st:
                dropdown_now_html = """  
                                <li class="nav-item dropdown">
                                    <a class="nav-link dropdown-toggle" href="#" id="%s" data-toggle="dropdown">%s</a>
                                    <div class="dropdown-menu" aria-labelledby="%s">%s</div>
                                </li>"""%(rand_a,c.get_name(fname=True),rand_a,sub_html)
                out_html += dropdown_now_html
            finished_2st.append(c.get_name(fname=True))
            
    menu_html = """
                <nav class="navbar navbar-expand navbar-dark bg-dark">
                    <a class="navbar-brand" href="/">MUNINN <small>by Corkine Ma</small></a>
                    <div class="navbar-collapse collapse justify-content-between">
                        <ul class="navbar-nav">
                            %s
                        </ul>
                    </div>
                    <div class="collapse navbar-collapse justify-content-end"></div>
                </nav>
                """%out_html
    return menu_html

def makeHTMLPage(course_info,menu_html,is_chapter=False,chapter_id="",chapter_address="source"):
    """对于课程总览，在此返回HTML页面。对于每个章节，也在此返回HTML页面。"""
    c = course_info
    chapter_html = ""
    notes_html = ""
    html_now = ""
    intro_content = "尚未添加内容"
    detail_map = {
        "h1_title":"",
        "h2_html":"",
    }
    chapter_update = "2018-01-01"
    chapter_url = "#"
    for ch in c.chapters:
        #如果需要处理的是章节页面，找到当前章节并进行以下处理：
        if is_chapter and ch.id == chapter_id:
            #左侧上方标题
            chapter_html += """<a href="%s" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center active">
                                %s<span class="badge badge-primary badge-pill">%s</span></a>"""%(c.get_uri(is_chapter=True,chapter=ch.id),ch.name,ch.mark)

            #左侧下方章节内容
            current_h1 = ""
            html_now = """  <ul class="list-group">
                            <li class="list-group-item list-group-item-light">章节目录</li>"""
            if ch.get_header():
                for head in ch.get_header():
                    if not current_h1: current_h1 = head
                    if head.startswith("<h1>") and current_h1 != head:
                        html_now += """</ul>
                            </li>"""
                        current_h1 = head                   
                    if head.startswith("<h1>"):
                        html_now += """
                        <li class="list-group-item">
                                %s
                                <ul class="list-group list-group-flush">"""%head.replace("<h1>","")
                    if head.startswith("<h2>"):
                        html_now += """<li class="list-group-item">%s</li>"""%head.replace("<h2>","")
                html_now += """</ul></li>"""

            #这里是寻找<intro>标签，然后返回右侧上方的章节总体概要信息。
            intro_content = ch.get_description()

            #返回右下角的笔记信息
            notes = ch.get_related_notes()
            # print("get notes",notes)
            notes_html = ""
            blog_mold = """             
            <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">
                            <a class="card-link" href="{note_url}">{note_title}</a> </h5>
                        <p class="card-text">{note_description}</p>
                        <small class="card-text text-muted">{note_date}</small>
                    </div>
                </div>"""
            quote_mold = """
            <div class="card text-right p-3">
                    <blockquote class="blockquote mb-0">
                        <p>{note_title}</p>
                        <footer class="blockquote-footer">
                            <small class="text-muted">{note_footer}</small>
                        </footer>
                    </blockquote>
                </div>"""
            question_mold = """
            <div class="card border-danger">
                    <div class="card-body">
                        <p class="card-text">{note_title}</p>
                        <span class="card-text"><small>{note_date}</small></span>
                    </div>
                </div>"""
            explain_mold = """
            <div class="card">
                    <div class="card-body">
                        <p class="card-text">{note_title}</p>
                        <span class="card-text"><small>{note_date}</small></span>
                    </div>
            </div>"""
            for note in notes:
                note_map = {
                "note_url":note.sourceuri,
                "note_title":note.name,
                "note_description":note.description,
                "note_date":note.modified_date,
                "note_footer":note.footer,
                }
                if note.mold == "blog":
                    notehtml = blog_mold.format_map(note_map)
                elif note.mold == "quote":
                    notehtml = quote_mold.format_map(note_map)
                elif note.mold == "question":
                    notehtml = question_mold.format_map(note_map)
                elif note.mold == "explain":
                    notehtml = explain_mold.format_map(note_map)
                notes_html += notehtml
            
            chapter_update = ch.get_update()
            chapter_url = "/" + ch.get_url(real=True)

                
        else:
            #全局页面，非单个章节
            chapter_html += """<a href="%s" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                                %s<span class="badge badge-primary badge-pill">%s</span></a>
                                """%(c.get_uri(is_chapter=True,chapter=ch.get_id()),\
                                                ch.get_name(),ch.get_version(only_mark=True))

    isc = isc2 = overview_href = ""
    if is_chapter:
        isc = ""
        isc2 = "active"
        overview_href = c.get_uri(only_name=True,suffix="_overview.html")
    else:
        isc = "active"
        isc2 = "disabled"
        overview_href = "#"
    map_c = {
        "title": c.get_name(),
        "nb_href":"#",
        "single_chapter":chapter_html,
        "overview":isc,
        "notebook":isc2,
        "overview_href":overview_href,
        "html_now":html_now,
        "intro_content":intro_content,
        "notes_html":notes_html,
        "page_name":c.get_name(),
        "chapter_update":chapter_update,
        "chapter_url":chapter_url,
    }
    head_nav = """
        <nav class="nav nav-tabs bg-light">
            <li class="nav-item">
                <small><a class="nav-link">{title}</a></small>
            </li>
            <li class="nav-item">
                <small><a class="nav-link {overview}" href="{overview_href}">Overview</a></small>
            </li>
            <li class="nav-item">
                <small><a class="nav-link" href="#">Course Info</a></small>
            </li>
            <li class="nav-item">
                <small><a class="nav-link {notebook}" href="{nb_href}">Notebook</a></small>
            </li>
        </nav>""".format_map(map_c)

    #row和container不闭合
    left_nav = """
            <div class="container mt-5 ml-auto">
                <div class="row">
                    <!--左侧-->
                    <div class="col-md-4">
                        <!--章节名称-->
                        <div class="list-group list-group-flush">
                            {single_chapter}
                        </div>
                        <!--章节详细信息-->
                        <div class="card mt-4">
                            {html_now}
                        </div>
                    </div>
            """.format_map(map_c)

    header = """<!DOCTYPE html>
            <html lang="zh_cn">
            <head>
                <meta charset="UTF-8">
                <title>{page_name} - 课程和笔记</title>
                <link rel="stylesheet" href="/css/bootstrap.css">
                <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
            </head>
            <body>""".format_map(map_c)

    footer = """
            <footer class="mt-5 pt-5 pl-5 text-muted text-center text-small">
                <ul class="list-inline">
                    <li class="list-inline-item">&copy; 2017-2018 Marvin Studio</li>
                    <li class="list-inline-item"><a href="#">About Project Muninn</a></li>
                </ul>
            </footer>
            </body>
            </html>"""

    #此处闭合contaioner和row
    intro_html = """<!--右侧-->
                    <div class="col-md-8 pl-3">
                        <!--本章概览-->
                        <div class="card">
                            <div class="card-header">本章概览</div>
                            <div class="card-body">
                                    {intro_content}
                                <p class="card-text">
                                    <small class="text-muted">最后更新于 {chapter_update} <a href="{chapter_url}" target="_black" class="card-link">查看笔记</a></small>
                                </p>
                            </div>
                        </div>
                        <!--笔记详情-->
                        <div class="card-columns mt-5">
                            {notes_html}
                        </div>
                    </div>
                </div>
            </div>""".format_map(map_c)
    
    output_html = header + menu_html + head_nav + left_nav + intro_html  + footer
    return output_html

def make_Index(html_head):
    index_map = {
        "html_head":html_head,
        "server_version":(server_version if server_version else "Beta"),
    }
    output_html = """
    <!DOCTYPE html>
    <html lang="cn">
    <head>
        <meta charset="UTF-8">
        <title>Welcome to Muninn!</title>
        <link rel="stylesheet" href="css/bootstrap.css">
    </head>
    <body>
    {html_head}
    <div class="container">
    <div class="row pt-5">
        <div class="col-md-7">
            <div class="jumbotron jumbotron-fluid  p-5 m-0">
                <h1 class="display-4">Hello, Corkine</h1>
                <!--<p class="lead">Muninn是一个存放在Github等静态网站托管服务商上的笔记系统，-->
                <!--此系统被设计用来整合不同领域的知识。本站点包含了奖励系统、课程系统和笔记系统。-->
                    <!--你可以使用动态脚本生成站点。</p>-->
                <!--<hr class="my-2">-->
                <!--<p class="text-muted">本网站是Jupyter Notebook的展示模块，一个动态脚本用来遍历所有文件夹的课程章节的iPynb文件，-->
                    <!--并且生成html文本，从此脚本中检索标题、更新日期、相关笔记，然后套用一个精致的排版展示出来。-->
                <!--</p>-->
                <!--<p class="mt-4 lead">-->
                    <!--<a class="btn btn-lg btn-primary" href="#" role="button">了解更多</a>-->
                <!--</p>-->
                <p class="mt-3 lead">本周您更新了 1 章节，记录了 3 篇笔记，继续保持！</p>
                <hr class="my-2">
                <p class="mt-3">自从上次更新，你获得了 <span class="badge badge-secondary">文思泉涌</span> 勋章。</p>
                <p class="mt-3 text-muted">最后更新于：2018年6月13日</p>
            </div>
            <h1 class="lead mt-3 pb-0 pl-1 pt-4">博客聚焦
                <br><span class="small text-muted font-weight-light">所有的博客均发布于 blog.mazhangjing.com</span> </h1>
            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title lead">Python数据处理学习笔记 - seaborn统计数据可视化篇</h5>
                    <p class="card-text small text-muted">Seaborn是基于Python的一个统计绘图工具包。Seaborn提供了一组高层次封装的matplotlib API接口。使用Seaborn而不是matplotlib，
                        绘图只需要少数几行代码，并且可以更加容易控制Style、Palette。本文基本是按照官方Guide顺序写就的。</p>
                    <a href="#" class="card-link">查看全文</a>
                </div>
            </div>
            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title lead">编程范式——《像计算机科学家一样思考》读书笔记（下）</h5>
                    <p class="card-text small text-muted">这是我关于《如何像计算机科学家一样思考》一书的体会和总结。此书的副标题叫做《Think Python》，
                        作者是Allen.B.Downey，欧林计算机学院计算机科学教授，MIT计算机科学学士和硕士，UCB计算机科学博士。
                        作者本身写作此书的原因是用来讲解的语言是java入门知识，其目标是：简短、循序渐进、专注于编程而非语言。这本书里出现的编程知识基本上是所有语言所共用的，
                        因此用来做一个程序学习之架构是非常合适，这也是本文希望做的——在这本书的基础上建立一个学习所有编程语言的基本框架。</p>
                    <a href="#" class="card-link">查看全文</a>
                </div>
            </div>
            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title lead">使用Python和Slack实现字幕组美剧的更新和通知</h5>
                    <p class="card-text small text-muted">本文介绍了使用Python和Slack实现字幕组美剧更新推送的一种方法，
                        脚本可以部署在Windows或者GNU/Linux或者macOS平台，使用计划任务或者CRON进行定时执行。你需要一个Slack的Webhook地址，
                        用于将消息POST到你的APP – Web、Android、iOS、watchOS以及PC、macOS均支持的Slack应用程序。</p>
                    <a href="#" class="card-link">查看全文</a>
                </div>
            </div>
            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title lead">图书馆七楼的落地窗 - 2017 于桂子山下</h5>
                    <p class="card-text small text-muted">写在华中师大，2017年底某个上弦月（半月），在地球上，武汉循进阳光的黑暗时（大约下午五点三十九分）。
                        此文另有姊妹篇，一年前于华中农业大学图书馆，见此： 图书馆五楼的落地窗 - 2016 于狮子山下</p>
                    <a href="#" class="card-link">查看全文</a>
                </div>
            </div>

        </div>
        <div class="col-md-5 pl-5">
            <div class="card pb-3">
                <!--<div class="card-header">勋章墙</div>-->
                <p class="card-title lead mt-4 ml-4">勋章墙 <span class="badge badge-light">06/13 - 06/20</span></p>
                <div class="row">
                    <div class="col-md-4">
                        <div class="pl-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/idea_596.1401384083px_1209661_easyicon.net.png"  height="70" width="70" />
                            <h6 class="lead text-center pt-3">点子多多</h6>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-0 m-0 pt-3">
                            <img class="rounded mx-auto d-block" src="src/diary_638px_1209882_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">文思泉涌</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pr-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/studying_669.79180887372px_1205971_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">挑灯夜战</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pl-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/Book_2000px_1170680_easyicon.net.png"  height="70" width="70" />
                            <h5 class="lead text-center pt-3">日积月累</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-0 m-0 pt-3">
                            <img class="rounded mx-auto d-block" src="src/quote_128px_1088353_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">引经据典</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pr-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/sun_783px_1209087_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">一天之计</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pl-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/classroom_645.00155520995px_1210165_easyicon.net.png"  height="70" width="70" />
                            <h6 class="lead text-center pt-3">课程达人</h6>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-0 m-0 pt-3">
                            <img class="rounded mx-auto d-block" src="src/plant_639px_1210114_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">新的希望</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pr-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/bee_704px_1210071_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">乐此不疲</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pl-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/cup_1088.8906649616px_1205872_easyicon.net.png"  height="70" width="70" />
                            <h5 class="lead text-center pt-3">完美一周</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-0 pt-3">
                            <img class="rounded mx-auto d-block" src="src/aias_562.56010230179px_1205860_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">探索发现</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pr-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/nobel_1564px_1205896_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">诺贝尔奖</h5>
                        </div>
                    </div>

                </div>
            </div>
            <div class="pt-3 pl-1 text-muted small">

                <p>我的 Jupyter 笔记系统 · 我的 Github 笔记展示仓库
                <br>笔记博客系统部署指南 · Github 代码仓库
                <br>Design & Developed by Corkine Ma (Github@corkine)
                <br>Server Version: {server_version}
                <br>© Marvin Studio 2018 </p>
        </div>
        </div>
    </div>
    <footer class="mt-5 pt-5 pl-5 text-muted text-center text-small">
        <ul class="list-inline">
            <li class="list-inline-item"></li>
            <li class="list-inline-item"></li>
        </ul>
    </footer>
    <!--
    <footer class="mt-5 pt-5 pl-5 text-muted text-center text-small">
        <ul class="list-inline">
            <li class="list-inline-item">&copy; 2018 Marvin Studio</li>
            <li class="list-inline-item"><a href="#">About Project Muninn</a></li>
            <li class="list-inline-item"><a href="#">Jupyter Notebook</a></li>
            <li class="list-inline-item"><a href="#">Github@Corkine</a></li>
        </ul>
    </footer>-->

    </body>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
</html>

    """.format_map(index_map)
    return output_html
    
def writeToFile(c=None,html="",suffix="_overview.html",index=False):
    if index:
        f = open("index.html","w+",encoding="utf8")
        f.write(html)
        f.close()
    else:
        fname_uri = c.get_uri(only_fname=True)
        if os.path.isdir(fname_uri):pass
        else: os.mkdir(fname_uri)
        f = open(c.get_uri(full_path=True,suffix=suffix),"w+",encoding="utf8")
        f.write(html)
        f.close()
    
    
if __name__ == "__main__":
    main(update_data=True,file_path="muninn_test_last.data")
    
