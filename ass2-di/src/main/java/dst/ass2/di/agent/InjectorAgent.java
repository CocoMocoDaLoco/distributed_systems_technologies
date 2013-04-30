package dst.ass2.di.agent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import dst.ass2.di.annotation.Component;

public class InjectorAgent implements ClassFileTransformer {

    public static void premain(String args, Instrumentation instrumentation) {
        new InjectorAgent(args, instrumentation);
    }

    private final Instrumentation instrumentation;

    public InjectorAgent(String args, Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        this.instrumentation.addTransformer(this);
    }

    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {
        final String jaClassName = className.replace('/', '.');
        final ClassPool cp = ClassPool.getDefault();
        cp.insertClassPath(new ByteArrayClassPath(jaClassName, classfileBuffer));

        CtClass cc = null;
        try {
            cc = cp.get(jaClassName);
        } catch (NotFoundException e) {
            System.err.println("Could not open class as bytecode");
            return classfileBuffer;
        }

        Component component = null;
        try {
            component = (Component)cc.getAnnotation(Component.class);
            if (component == null) {
                return classfileBuffer;
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
            return classfileBuffer;
        }

        final CtConstructor[] ctors = cc.getConstructors();
        for (CtConstructor ctor : ctors) {
            try {
                ctor.insertBefore(String.format("System.out.println(\"Hello World: %s\");", className));
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }

        try {
            return cc.toBytecode();
        } catch (IOException e) {
            e.printStackTrace();
            return classfileBuffer;
        } catch (CannotCompileException e) {
            e.printStackTrace();
            return classfileBuffer;
        }
    }

}
