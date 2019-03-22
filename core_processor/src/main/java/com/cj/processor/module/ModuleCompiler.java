package com.cj.processor.module;

import com.cj.annontations.module.ModuleInfo;
import com.cj.annontations.module.ModuleRegister;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ModuleCompiler extends AbstractProcessor {

    private Messager messager;
    public ModuleCompiler() {}

    //执行初始化逻辑
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();

        Map<String,String> options = processingEnv.getOptions();
        //这个是gradle文件中 配置的javaCompileOptions
        String module_name = options.get("module_name");

    }

    //扫描、解析自定义注解
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        //遍历所有添加了@ModuleRegister注解的元素{包含类、属性、方法、接口。但是@ModuleRegister注解限定了只能类使用}
        Set annotatedElements = roundEnvironment.getElementsAnnotatedWith(ModuleRegister.class);

        //同一个module添加了多次注解
        if(annotatedElements.size()>1){
            error("每个module中@ModuleRegister注解只需用一次，请去除重复注解");
            return false;//退出处理
        }

        Iterator<Element> iterator=annotatedElements.iterator();

        if(iterator==null){
            error("@ModuleRegister注解迭代器为空");
            return false;
        }

        while (iterator.hasNext()){
            Element e=iterator.next();
            if(e.getKind() == ElementKind.CLASS){
                //获取添加的注解信息
                ModuleRegister moduleRegister=e.getAnnotation(ModuleRegister.class);
                if(moduleRegister==null){
                    error("@ModuleRegister注解对象为空，请检查注解");
                    return false;
                }

                ModuleInfo proxy=new ModuleInfo();
                proxy.setModuleName(moduleRegister.moduleName());
                proxy.setDelegateName(moduleRegister.delegateName());
                proxy.setPackageName(getPackageName(moduleRegister.moduleName()));
                writeJsonToAssets(proxy);

                return true;

            }else {
                error("@ModuleRegister注解只能用户类上");
                return false;
            }
        }
        return false;
    }

    //指定该注解处理器是指定给哪一个注解的
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(ModuleRegister.class.getCanonicalName());
        return annotations;
    }

    //指定使用的java版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean isEmpty(String str){
        if(str==null){
            return true;
        }

        if(str.isEmpty()){
            return true;
        }

        return false;
    }


    private String getPackageName(String moduleName) {
        File manifest = new File(moduleName + "/src/main/AndroidManifest.xml");
        if(!manifest.exists()) {
            return null;
        } else {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser e = factory.newSAXParser();
                PackageParserHandler handler = new PackageParserHandler();
                e.parse(manifest, handler);
                return handler.getPackageName();
            } catch (ParserConfigurationException ex1) {
                ex1.printStackTrace();
            } catch (SAXException ex2) {
                ex2.printStackTrace();
            } catch (IOException ex3) {
                ex3.printStackTrace();
            }
            return null;
        }
    }

    private void writeJsonToAssets(ModuleInfo moduleProxyInfo) {
        File dir = new File(moduleProxyInfo.getModuleName() + "/src/main/assets");
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, ModuleInfo.MODULE_PREFIX + moduleProxyInfo.getModuleName() + ".json");
        try {
            if(file.exists()) {
                file.delete();
            }

            file.createNewFile();
            this.writerJsonToFile(moduleProxyInfo, file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void writerJsonToFile(ModuleInfo moduleProxyInfo, File file) throws Exception {
        Gson gson = new Gson();
        FileOutputStream out = new FileOutputStream(file);
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        gson.toJson(moduleProxyInfo, ModuleInfo.class, writer);
        writer.flush();
        writer.close();
    }

    private void error(String msg) {
        this.messager.printMessage(Kind.ERROR, msg);
    }

}
