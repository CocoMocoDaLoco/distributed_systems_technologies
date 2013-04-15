package dst.ass2.ws;

import static dst.ass2.ws.MiscUtils.getAnnotation;
import static dst.ass2.ws.MiscUtils.matches;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ReflectionUtils.findMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.persistence.Entity;

import org.junit.Test;
import org.w3c.dom.Element;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import dst.ass2.ejb.session.JobStatisticsBean;
import dst.ass2.ejb.ws.Constants;
import dst.ass2.ejb.ws.IGetStatsRequest;

/**
 * Some tests that verify that the WSDL contract is correct.
 */
public class BackgroundWebServiceTest {
	String content;

	/**
	 * Verifies that the SOAP service is defined correctly.
	 */
	@Test
	public void testJaxWSWsdl() {
		String wsdl = getWsdl();
		assertTrue("Incorrect service name in the WSDL's <service> section", matches(wsdl,
				".*<service.+name=\"" + Constants.SERVICE_NAME + "\".+"));
		assertTrue("Incorrect port name in the WSDL's <service> section", matches(wsdl,
				".*<port.+name=\"" + Constants.PORT_NAME + "\".+binding=\".*:" + Constants.BINDING_NAME + "\".+"));
		assertTrue("Incorrect address in the WSDL's <service> section", matches(wsdl,
				".*<soap:address.+location=\"" + Constants.SERVICE_URL + "\".+"));
	}

	/**
	 * Checks if WS-Addressing is used.
	 */
	@Test
	public void testWSAddressingWSDL() {
		assertTrue("WSDL does not contain binding with WS addressing", matches(getWsdl(),
				".*<binding.+name=\"" + Constants.BINDING_NAME + "\".+", ".*<.*:UsingAddressing.+"));

		assertTrue("WSDL does not contain a correct fault section", matches(getWsdl(),
				".*</output>.*", ".*<fault.+name=\"WebServiceException\".+", ".*<soap:fault.+name=\"WebServiceException\".+"));
	}

	/**
	 * Verifies that the grid name is transported as a SOAP header parameter.
	 */
	@Test
	public void testSoapHeaderWsdl() {
		Method method = findWebServiceMethod();
		WebMethod webMethod = getAnnotation(method, WebMethod.class);
		WebParam webParam = null;
		for (int i = 0; i < method.getParameterAnnotations().length; i++) {
			for (Annotation paramAnnotation : method.getParameterAnnotations()[i]) {
				if (paramAnnotation.annotationType() == WebParam.class) {
					webParam = (WebParam) paramAnnotation;
					break;
				}
			}
		}
		assertNotNull("No parameter is annotated with @WebParam", webParam);

		assertTrue("The WSDL does not use a SOAP header as input to the operation.", matches(getWsdl(),
				String.format(".*<soap:header.+message=\"tns:%s\".+part=\"%s\".+", webMethod.operationName(), webParam.partName())));
	}
	
	/**
	 * Checks whether JPA entities or DTOs are used as parameters and return
	 * type.
	 */
	@Test
	public void testDtos() {
		// Verify that the return type is no JPA entity
		Method method = findWebServiceMethod();
		Class<?> returnType = method.getReturnType();
		assertNull(
				String.format(
						"The return type %s of method %s must not be annotated with @Entity",
						returnType.getName(), method.getName()),
				findAnnotation(returnType, Entity.class));
		assertNull(
				String.format(
						"The return type %s of method %s must not be annotated with @Entity",
						returnType.getName(), method.getName()),
				findAnnotation(returnType,
						org.hibernate.annotations.Entity.class));

		// Verify that no method parameter is a JPA entity
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			Class<?> parameterType = method.getParameterTypes()[i];
			assertNull(
					String.format(
							"The %d. parameter of method %s must not be annotated with @Entity",
							i, method.getName()),
					findAnnotation(parameterType, Entity.class));
			assertNull(
					String.format(
							"The %d. parameter of method %s must not be annotated with @Entity",
							i, method.getName()),
					findAnnotation(parameterType,
							org.hibernate.annotations.Entity.class));
		}
	}

	/**
	 * Returns the content of the WSDL.
	 *
	 * @return the content
	 */
	protected String getWsdl() {
		if (content == null) {
			content = readURL(Constants.SERVICE_WSDL_URL);
		}
		return content;
	}

	/**
	 * Returns the string content of a given URL.
	 *
	 * @return the content
	 */
	protected String readURL(String url) {
		Client client = Client.create();
		try {
			WebResource wsdlResource = client.resource(url);
			return wsdlResource.get(String.class);
		} finally {
			client.destroy();
		}
	}

	/**
	 * Returns the schema contained in the WSDL.
	 *
	 * @return the content
	 */
	protected String getSchema() {
		String wsdl = getWsdl();
		Element wsdlEl = XMLUtils.toElement(wsdl);
		Element schemaEl = XMLUtils.findDescendantsByName(wsdlEl, "schema").get(0);
		List<Element> imports = XMLUtils.findDescendantsByName(wsdlEl, "import");
		String schema = null;
		if(!imports.isEmpty()) {
			String schemaLocation = imports.get(0).getAttribute("schemaLocation");
			schema = readURL(schemaLocation);
		} else {
			schema = XMLUtils.toString(schemaEl);
		}
		return schema;
	}

	/**
	 * @return Find the Web service method.
	 */
	public static Method findWebServiceMethod() {
		return findMethod(JobStatisticsBean.class, "getStatisticsForGrid", IGetStatsRequest.class, String.class);
	}

}
