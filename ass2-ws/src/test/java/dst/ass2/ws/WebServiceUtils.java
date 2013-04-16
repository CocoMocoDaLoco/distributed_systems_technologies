package dst.ass2.ws;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * Contains some utility methods for testing web services.
 */
public final class WebServiceUtils {
    private WebServiceUtils() {
    }

    /**
     * Return a Web service proxy for a given interface.
     * @param serviceInterface the service interface.
     * @param targetNamespace target namespace of the Web service.
     * @param serviceName qualified name of the Web service.
     * @param wsdlURL WSDL location of the Web service.
     * @return
     */
    public static <T> T getServiceProxy(Class<T> serviceInterface, String targetNamespace, String serviceName, String wsdlURL) {
        try {
            QName serviceQName = new QName(targetNamespace, serviceName);
            Service serviceFactory = Service.create(new URL(wsdlURL), serviceQName);
            T service = serviceFactory.getPort(serviceInterface);
            return service;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
