package com.tangzy.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.*
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project


/**
 * 利用Gradle Transform流程，修改class到dex的生成过程
 * 1、获取所有的CtClass(包括bootclass、directory、jar)
 * 2、获取得到Application和所有的iApplicationLike
 * 3、利用jassist把iApplicationLike注入到Application中
 *
 * 解决了组件初始化代码，如果放在组件Application中，就不能被app引用的问题
 *
 */
class ComCodeTransform extends Transform {

    private Project project
    ClassPool classPool
    String applicationName

    ComCodeTransform(Project project) {
        this.project = project
    }

    // 输出的内容
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        System.out.println("transform------------ ")
        // Todo: 获取到应用的名字
        getRealApplicationName(transformInvocation.getInputs())
        classPool = new ClassPool()

        // Todo: bootClass
        project.android.bootClasspath.each {
            classPool.appendClassPath((String) it.absolutePath)
        }

        // Todo: 得到CtClass
        def box = ConvertUtils.toCtClasses(transformInvocation.getInputs(), classPool)
        System.out.println("box.size = " + box.size())

        //要收集的application，一般情况下只有一个
        List<CtClass> applications = new ArrayList<>()
        //要收集的applicationlikes，一般情况下有几个组件就有几个applicationlike
        List<CtClass> activators = new ArrayList<>()

        // Todo: 得到application、applicationlikes
        for (CtClass ctClass : box) {
            if (isApplication(ctClass)) {
                applications.add(ctClass)
                continue
            }
            if (isActivator(ctClass)) {
                activators.add(ctClass)
            }
        }
        for (CtClass ctClass : applications) {
            System.out.println("application is   " + ctClass.getName())
        }
        for (CtClass ctClass : activators) {
            System.out.println("applicationlike is   " + ctClass.getName())
        }

        transformInvocation.inputs.each { TransformInput input ->
            //对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput ->
                //jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)

            }
            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                boolean isRegisterCompoAuto = project.extensions.combuild.isRegisterCompoAuto
                if (isRegisterCompoAuto) {
                    String fileName = directoryInput.file.absolutePath
                    File dir = new File(fileName)
                    dir.eachFileRecurse { File file ->
                        String filePath = file.absolutePath
                        String classNameTemp = filePath.replace(fileName, "")
                                .replace("\\", ".")
                                .replace("/", ".")
                        if (classNameTemp.endsWith(".class")) {
                            String className = classNameTemp.substring(1, classNameTemp.length() - 6)
                            if (className.equals(applicationName)) {
                                injectApplicationCode(applications.get(0), activators, fileName)
                            }
                        }
                    }
                }
                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
    }


    private void getRealApplicationName(Collection<TransformInput> inputs) {
        applicationName = project.extensions.combuild.applicationName
        System.out.println("applicationName = " + applicationName)
        if (applicationName == null || applicationName.isEmpty()) {
            throw new RuntimeException("you should set applicationName in combuild")
        }
    }


    private void injectApplicationCode(CtClass ctClassApplication, List<CtClass> activators, String patch) {
        System.out.println("injectApplicationCode begin")
        ctClassApplication.defrost()
        try {
            CtMethod attachBaseContextMethod = ctClassApplication.getDeclaredMethod("onCreate", null)
            attachBaseContextMethod.insertAfter(getAutoLoadComCode(activators))
        } catch (CannotCompileException | NotFoundException e) {
            StringBuilder methodBody = new StringBuilder()
            methodBody.append("protected void onCreate() {")
            methodBody.append("super.onCreate();")
            methodBody.
                    append(getAutoLoadComCode(activators))
            methodBody.append("}")
            ctClassApplication.addMethod(CtMethod.make(methodBody.toString(), ctClassApplication))
        } catch (Exception e) {

        }
        ctClassApplication.writeFile(patch)
        ctClassApplication.detach()

        System.out.println("injectApplicationCode success ")
    }

    private String getAutoLoadComCode(List<CtClass> activators) {
        StringBuilder autoLoadComCode = new StringBuilder()
        for (CtClass ctClass : activators) {
            autoLoadComCode.append("new " + ctClass.getName() + "()" + ".onCreate();")
        }

        return autoLoadComCode.toString()
    }


    private boolean isApplication(CtClass ctClass) {
        try {
            if (applicationName != null && applicationName.equals(ctClass.getName())) {
                return true
            }
        } catch (Exception e) {
            println "class not found exception class name:  " + ctClass.getName()
        }
        return false
    }

    private boolean isActivator(CtClass ctClass) {
        try {
            for (CtClass ctClassInter : ctClass.getInterfaces()) {
                if ("com.tangzy.plugjar.applicationlike.IApplicationLike".equals(ctClassInter.name)) {
                    return true
                }
            }
        } catch (Exception e) {
            println "class not found exception class name:  " + ctClass.getName()
        }

        return false
    }

    // 代表 Transform 对于的 task 名称
    @Override
    String getName() {
        return "ComponentCode"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // Transform 要操作的内容范围
    // 1.PROJECT 只有项目内容
    // 2.SUB_PROJECTS 只有子项目内容
    // 3.EXTERNAL_LIBRARIES 只有外部库
    // 4.TESTED_CODE 当前变量（包括依赖项）测试的代码
    // 5.PROVIDED_ONLY 本地或者员村依赖项
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    // 是否增量编译
    @Override
    boolean isIncremental() {
        return false
    }

}