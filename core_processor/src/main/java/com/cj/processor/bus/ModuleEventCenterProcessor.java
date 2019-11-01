package com.cj.processor.bus;

import com.cj.annontations.Key;
import com.cj.annontations.bus.EventRegister;
import com.cj.annontations.bus.ModuleEventCenter;
import com.cj.annontations.bus.model.EventRegisterEntity;
import com.cj.annontations.bus.model.ModuleEventCenterEntity;
import com.cj.processor.util.LogUtil;
import com.cj.processor.util.PackageParserHandler;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.swing.plaf.TextUI;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Author:chris - jason
 * Date:2019-08-13.
 * Package:com.cj.compiler.processor
 */
public class ModuleEventCenterProcessor extends AbstractProcessor {

    private Messager messager;
    private Map<String, String> options;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(ModuleEventCenter.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        options = processingEnv.getOptions();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        String moduleName = options.get(Key.OPTION_MODULE_NAME);
        //String filePath = moduleName + "/src/main/assets/" + Key.MODULE_EVENT_FILE_PRE + moduleName + ".json";
        Set elements = roundEnv.getElementsAnnotatedWith(ModuleEventCenter.class);

        if (elements.size() > 1) {
            LogUtil.getInstance(messager).e_Message("@" + ModuleEventCenter.class.getName() + " 注解在每个module中只能添加一次，请去除重复注解");
            return false;
        }

        Iterator<Element> iterator = elements.iterator();
        if (iterator == null) {
            LogUtil.getInstance(messager).e_Message("@" + ModuleEventCenter.class.getName() + " 注解处理器为空");
            return false;
        }


        while (iterator.hasNext()) {
            Element element = iterator.next();

            if (element.getKind() != ElementKind.CLASS) {
                LogUtil.getInstance(messager).e_Message("@" + ModuleEventCenter.class.getName() + " 注解只能作用于类上");
                return false;
            }

            ModuleEventCenter moduleEventCenter = element.getAnnotation(ModuleEventCenter.class);
            if (moduleEventCenter == null) {
                LogUtil.getInstance(messager).e_Message("@" + ModuleEventCenter.class.getName() + " 注解处理器为空");
                return false;
            }

            ModuleEventCenterEntity entity = new ModuleEventCenterEntity();
            entity.setModuleName(moduleName);
            entity.setPkgName(getPackageName(moduleName));

            //获取@ModuleEventCenter注解的类名
            Set<? extends Element> rootElements = roundEnv.getRootElements();
            String qualifiedName = null;
            if (rootElements != null && rootElements.size() > 0) {
                for (Element e : rootElements) {
                    ModuleEventCenter annotation = e.getAnnotation(ModuleEventCenter.class);
                    String str = ((TypeElement) e).getQualifiedName().toString();
                    if (annotation != null && str != null) {
                        qualifiedName = ((TypeElement) e).getQualifiedName().toString();
                        break;
                    }
                }
            }
            if (qualifiedName == null) {
                return false;
            }
            entity.setDelegateName(qualifiedName);

            //获取@ModuleEventCenter注解的类中的属性字段
            List<? extends Element> enclosedElements = element.getEnclosedElements();
            // 保存字段的集合,Class类型在编译期间无法获得
            List<EventRegisterEntity> methodList = new ArrayList<>();
            for (Element ele : enclosedElements) {
                if (ele.getKind() == ElementKind.FIELD) {
                    TypeMirror typeMirror = ele.asType();
                    //字段的类型
                    TypeKind typeKind = typeMirror.getKind();
                    //字段的名称
                    Name fieldName = ele.getSimpleName();

                    List<? extends AnnotationMirror> annotationMirrors = ele.getAnnotationMirrors();
                    if (annotationMirrors != null) {
                        for (AnnotationMirror mirror : annotationMirrors) {
                            String annotationType = mirror.getAnnotationType().toString();
                            if (annotationType != null && annotationType.equals(EventRegister.class.getCanonicalName())) {
                                //key被定义成方法 EventRegisterEntity.type()
                                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
                                    if (entry.getKey().getSimpleName().toString().equals("type")) {
                                        try {
                                            AnnotationValue value = entry.getValue();
                                            String className = value.getValue().toString();
                                            EventRegisterEntity method = new EventRegisterEntity(fieldName.toString(), className);
                                            methodList.add(method);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //通过javaPoet按规则生成对应的接口
            //接口名：Gen& + moduleName + $Interface
            //方法名：Gen& +fieldName + $Method ,无参数,返回类型 com.cj.com.bus.ModuleBus.Observable<className>
            if (methodList.size() == 0) {
                LogUtil.getInstance(messager).e_Message("暂无组件消息注册");
                return false;
            }

            List<MethodSpec> methodSpecs = new ArrayList<>();
            for (EventRegisterEntity eventRegisterEntity : methodList) {
                // 生成方法，必须有Modifier.ABSTRACT或STATIC 2017/4/25 09:25
                try {
                    String[] names = eventRegisterEntity.getClassName().split("\\.");
                    StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < names.length; i++) {
                        if (i < names.length-1) {
                            buffer.append(".");
                            buffer.append(names[i]);
                        }
                    }
                    //@EventRegister可能会添加一些系统类，无法直接通过反射获取Type
                    String packageName = buffer.toString().replaceFirst(".", "");
                    String simpleName = names[names.length - 1];

                    MethodSpec methodSpec = MethodSpec.
                            methodBuilder("Gen$" + eventRegisterEntity.getFieldName() + "$Method")
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .addJavadoc("获取消息\n") // 添加注释
                            .returns(ParameterizedTypeName.get(ClassName.get("com.cj.common.bus.ModuleBus", "Observable"), ClassName.get(packageName, simpleName)))
                            .build();


                    //ClassName.get("android.content", "intent");

                    methodSpecs.add(methodSpec);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // interfaceBuilder方法标志生成接口类 2017/4/25 09:25
            TypeSpec typeSpec = TypeSpec.
                    interfaceBuilder("Gen$" + moduleName + "$Interface")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethods(methodSpecs)
                    .build();

            // 写入文件
            JavaFile javaFile = JavaFile.builder("gen.com.cj.bus", typeSpec)
                    .addFileComment("Auto generated code , Do not edit")
                    .build();
            try {
                //生成代码路径
                javaFile.writeTo(new File("base_common/src/main/java/"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            writeJsonToAssets(entity);
            return true;
        }

        return false;
    }


    //通过module名解析包名
    private String getPackageName(String moduleName) {
        File manifest = new File(moduleName + "/src/main/AndroidManifest.xml");
        if (!manifest.exists()) {
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

    private void writeJsonToAssets(ModuleEventCenterEntity moduleEntity) {
        File dir = new File(moduleEntity.getModuleName() + "/src/main/assets");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, Key.MODULE_EVENT_FILE_PRE + moduleEntity.getModuleName() + ".json");
        try {
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            Gson gson = new Gson();
            FileOutputStream out = new FileOutputStream(file);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
            gson.toJson(moduleEntity, ModuleEventCenterEntity.class, writer);
            writer.flush();
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
