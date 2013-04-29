package dst.ass2.ejb.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class AuditInterceptor {

    @AroundInvoke
    public Object interceptor(InvocationContext ctx) throws Exception {
        System.out.printf("Intercepting call to: %s%n", ctx.getMethod().getName());
        return ctx.proceed();
    }

}
