#/usr/bin/env python3
# -*- coding: utf8 -*-

JUPYTER_NOTEBOOK_ROOT_FOLDER = "/root/.jupyter"
#JUPYTER_NOTEBOOK_ROOT_FOLDER = """C:\\Users\\Administrator\\Desktop\\jupyter"""

HTML_DST_FOLDER = "source"

COURSE_INFO = {
    "javase":
    {
        "show_menu_name_1st":"Computer Science",
        "show_menu_name_2st":"Thinking in Java",
        "2st_addr":"读书笔记",
        "address":"learn_java",
        "id":"c3",
        "chapter_list":[
            ("对象、操作符和流程控制","0","chapter1_play_java.html","w1"),
            ("初始化和清理","0","chapter2_init_finalize.html","w2"),
            ("权限、类重用","0","chapter3_permission.html","w3"),
            ("多态","0","chapter4_cast.html","w4"),
            ("接口","0","chapter5_interface.html","w5"),
            ("内部类","0","chapter6_innerclass.html","w6"),
            ("容器","0","chapter7_container.html","w7"),
        ],
    },
    "javaee":
    {
        "show_menu_name_1st":"Computer Science",
        "show_menu_name_2st":"Learn Java EE",
        "2st_addr":None,
        "2st_addr":"读书笔记",
        "address":"learn_javaee",
        "id":"js",
        "chapter_list":[
            ("serlvet_jsp_basic","0","chapter1_servelt_basic.html","c1"),
            ("action_el","0","chapter2_action_el.html","c2"),
            ("jstl_taglib","0","chapter3_jstl_taglib.html","c3"),
            ("deploy_secret_filter_wrapper","0","chapter4_deploy_security_filter_wrapper.html","c4"),
            ("spring_ioc","0","chapter5_spring_ioc.html","c5"),
            ("spring_aop","0","chapter6_spring_aop.html","c6"),
        ],
    },
}



