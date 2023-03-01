package moscow.ptnl.schr.security;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import ru.mos.emias.system.v1.usercontext.ObjectFactory;
import ru.mos.emias.system.v1.usercontext.UserContext;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class UserContextHeaderGenerator extends AbstractSoapInterceptor {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

	private final JAXBContext jaxbContext;

	private final SOAPFactory factory;

	private final DocumentBuilderFactory documentBuilderFactory;

	private final List<Long> userRightIds;

	private String login;

	public UserContextHeaderGenerator(String login, List<Long> userRightIds) throws JAXBException, SOAPException {
        super(Phase.PRE_PROTOCOL);
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.jaxbContext = JAXBContext.newInstance(UserContext.class);
		this.factory = SOAPFactory.newInstance();
		this.userRightIds = userRightIds;
		this.login = login;
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        try {
			List<Header> headers = message.getHeaders();

			UserContext.UserRights userRights = new UserContext.UserRights();
			userRights.getUserRightId().addAll(userRightIds);
			UserContext userContext = new UserContext();
			userContext.setSystemName(this.login);
			userContext.setUserName(this.login.toLowerCase());
			userContext.setUserRoleId(-1);
			userContext.setUserRights(userRights);

			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			jaxbContext.createMarshaller().marshal(new ObjectFactory().createUserContext(userContext), document);
			SOAPElement element = factory.createElement(document.getDocumentElement());

			Header userContextHeader = new Header(element.getElementQName(), element);
			headers.add(userContextHeader);

			message.put(Header.HEADER_LIST, headers);
		} catch (Exception ex) {
			LOG.error("UserContext", ex);
		}
	}
}
