package dst.ass2.ejb.interceptor;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import dst.ass2.ejb.model.IAuditParameter;
import dst.ass2.ejb.model.impl.AuditLog;
import dst.ass2.ejb.model.impl.AuditParameter;

public class AuditInterceptor {

    @PersistenceContext
    private EntityManager entityManager;

    @AroundInvoke
    public Object interceptor(InvocationContext ctx) throws Exception {
        AuditLog log = new AuditLog();
        log.setInvocationTime(new Date());
        log.setMethod(ctx.getMethod().getName());

        Object[] params = ctx.getParameters();
        List<IAuditParameter> parameters = new LinkedList<IAuditParameter>();
        for (int i = 0; params != null && i < params.length; i++) {
            Object o = params[i];

            AuditParameter parameter = new AuditParameter();
            parameter.setAuditLog(log);
            parameter.setParameterIndex(i);
            parameter.setType(o == null ? "null" : o.getClass().getName());
            parameter.setValue(o == null ? "null" : o.toString());

            parameters.add(parameter);
        }
        log.setParameters(parameters);

        Object result = null;
        try {
            result = ctx.proceed();
            log.setResult(result == null ? "null" : result.toString());

            return result;
        } catch (Exception e) {
            log.setResult(e.toString());
            throw e;
        } finally {
            entityManager.persist(log);
            entityManager.flush();
        }
    }

}
