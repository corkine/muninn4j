#/usr/bin/env python3
# -*- encoding:utf8 -*-
from shutil import *
import os,sys,datetime
import subprocess
from subprocess import PIPE
from pprint import pprint
from muninn_config import JUPYTER_NOTEBOOK_ROOT_FOLDER,HTML_DST_FOLDER
import pickle, traceback
SEP = os.path.sep

# 第一步，将 Jupyter 的媒体文件和目录结构迁移到网站目录下，会自动比较哪个文件较新，然后只转移较新的文件
def transData(clist, from_source=JUPYTER_NOTEBOOK_ROOT_FOLDER, to_source="source"+SEP):
    """用来转移文件的胶水层，耦合fatch_data
    使用clist提供的期望的地址来寻找"""
    print("="*10,"正在迁移指定文件夹下的ipynb文件以及其附带的媒体文件","="*10)
    def getAllowedFolders(clist):
        af = []
        res = ""
        for course in clist:
            af.append(course.sourceuri)
            res += course.sourceuri + "::"
        return res, af

    def transfile(reset=False,fast=True,from_source="source/",to_source="new_source/",allowed_folder=[]):
        "只拷贝出现在允许列表的文件夹以及其文件"
        print("正在遍历文件树",from_source)
        fast_copytree(src=from_source,dst=to_source,symlinks=False,allowed_folder=allowed_folder)

    def fast_copytree(src,dst,symlinks=False,allowed_folder=[],needconvert=""):
        """从src文件夹拷贝数据到dst文件夹，这是一个copytree的简化版本"""
        #首先遍历所有的src文件夹的子文件夹
        names = os.listdir(src)

        #遍历的文件中一定包含那些文件夹，因此-1可以取出这些文件夹，然后创建它们
        if src.split(SEP)[-1] in allowed_folder:
            # print("创建文件夹",src.split(SEP)[-1])
            try: os.makedirs(dst)
            except: pass
        errors = []
        #对于每个子文件夹
        for name in names:
            srcname = os.path.join(src,name)
            dstname = os.path.join(dst,name)
            try:
                if os.path.isdir(srcname):
                    fast_copytree(srcname,dstname,symlinks,allowed_folder=allowed_folder)
                else:
                    if src.split(SEP)[-1] in allowed_folder:
                        # print("现在正在处理：",src,dst)
                        srcname = srcname.replace(SEP,"/")
                        dstname = dstname.replace(SEP,"/")
                        #没有文件或者文件有更新
                        if not os.path.isfile(dstname) or os.stat(srcname).st_mtime > os.stat(dstname).st_mtime:
                            try:
                                if os.path.isfile(dstname):
                                    os.remove(dstname)
                                    print("发现旧文件，正在删除..")
                            except: print("删除旧文件出错",dstname)
                            print("正在复制文件到",dstname)
                            #dstname source/coursera_learn_computer/chapter2_x86_mips.ipynb
                            copy2(srcname,dstname)
            except OSError as why:
                errors.append((srcname,dstname,str(why)))
            except Error as err:
                errors.extend(err.args[0])
        try:
            copystat(src.replace(SEP,"/"), dst.replace(SEP,"/"))
        except:
            pass
        if errors:
            raise Error(errors)

    res, JUPYTER_NOTEBOOK_ALLOWED_FOLDER = getAllowedFolders(clist)
    print("Allowed Floder is",res)
    transfile(from_source=from_source,to_source=to_source,allowed_folder=JUPYTER_NOTEBOOK_ALLOWED_FOLDER)
    print("文件转移完毕")

def findIpynb(clist,from_source=JUPYTER_NOTEBOOK_ROOT_FOLDER,to_source="source"+SEP):
    needfiles = []
    try:
        print("正在根据配置文件寻找需要进行转换的ipynb文件(ipynb文件日期新于html文件)")
        count = 0
        for course in clist:
            for chapter in course.chapters:
                # coursera_learn_models\WEEK2_model_thinking.html
                address = chapter.sourceuri
                # coursera_learn_models\WEEK2_model_thinking.ipynb
                filename = address.replace(".html",".ipynb")
                # C:\Users\Administrator\Desktop\jupyter\coursera_learn_models\WEEK2_model_thinking.ipynb
                from_filename = os.path.join(from_source,filename)
                # source\coursera_learn_models\WEEK2_model_thinking.ipynb
                to_filename = os.path.join(to_source,filename)
                to_filename_html = to_filename.replace(".ipynb",".html")
                # 如果不存在html文件或者ipynb文件有更新，则进行下一步
                if not os.path.isfile(to_filename_html) or os.stat(from_filename).st_mtime > os.stat(to_filename_html).st_mtime:
                    count += 1
                    print("%s. 以下文件应该被找到并且更新"%count,to_filename)
                    needfiles.append(filename)
    except:
        print(traceback.format_exc())
    return needfiles

def convertNotes(clist,chapter_dir,needfiles=[]):
    """对每一个Notebook，进行转换，胶水层，耦合fatch_data"""
    print("="*10,"正在转换IPYNB文件","="*10)
    # print("需要处理的文件为",needfiles)
    print("更改CWD到",chapter_dir)
    cwd = os.getcwd() #之后均在source目录下运行
    os.chdir(chapter_dir)

    def convert(filename,fname):
        """调用命令行工具对ipynb文件进行html转换，
        放在其原始文件夹下，fname为其所在文件夹，filename为其文件名"""
        current = os.getcwd()
        os.chdir(fname)
        c = "jupyter nbconvert %s"%filename
        print("切换目录为: ",os.getcwd(),"正在执行指令：",c)
        p = subprocess.Popen(c,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
        p.wait()

        if p.returncode != 0:
            print("转换出错，错误原因为",str(p.communicate()[0],"utf-8"),p.communicate()[1])
            os.chdir(current)
            return 0
        os.chdir(current)
        return 1

    co = ""
    for course in clist:
        co += str(course.name) + " :: "
    print("课程列表为",co)
    #获取需要转换的课程和笔记，这一步是因为需要在同一个目录下运行convert
    conf = {}
    for course in clist:
        conf[(course.sourceuri,course.id)] = []
        count = 0
        for chapter in course.chapters:
            filename = chapter.sourceuri
            #对象生成的是html地址，这显然是不对的，现在还没有转换，因此转换成为ipynb文件类型
            if filename.endswith(".html"):
                filename = filename.replace(".html",".ipynb")
            #如果此文件不能找到，则跳过转换 xxx/xxx.ipynb
            if not os.path.isfile(filename):
                print(filename,"此文件无法被找到，但是存在于配置文件中，请手动检查，目前已跳过转换")
                continue
            if not filename in needfiles:
                count += 1
                continue
            #获得文件名，不含地址
            name = filename.split(SEP)[-1]
            conf[(course.sourceuri,course.id)].append(name)
    #遍历这些课程，同一课程笔记统一处理（多个笔记文件）
    print("需要处理的章节和课程为",conf)
    for path,id in conf:
        alist = conf[(path,id)]
        if len(alist) == 0: continue
        fnames = ""
        for a in alist:
            fnames += "%s"%a + " "
        try:
            convert(fnames,path)
        except Exception as e:
            print(traceback.format_exc())
            print("转换 [%s] 此文件夹内容出错"%path,e)
    os.chdir(cwd)
    print("CWD切换回",cwd)
    print("转换完毕")




if __name__ == "__main__":
    
    clist = pickle.load(open("muninn_test_last.data","rb"))
    transData(clist,from_source=JUPYTER_NOTEBOOK_ROOT_FOLDER,to_source="source"+SEP)
    needfiles = findIpynb(clist,from_source=JUPYTER_NOTEBOOK_ROOT_FOLDER,to_source="source"+SEP)
    convertNotes(clist,"source",needfiles=needfiles)
