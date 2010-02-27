package thesmith.eventhorizon;

import java.util.Map;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import thesmith.eventhorizon.controller.AccountsController;

import com.google.appengine.repackaged.com.google.common.collect.Maps;

public class MethodHanlderTest extends DataStoreBaseTest {
  @Autowired
  private AnnotationMethodHandlerAdapter adaptor;
  
  @Ignore
  public void shouldHandle() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/accounts/blah/");
    request.setMethod("GET");
    Map<String, String> attributes = Maps.newHashMap();
    attributes.put("personId", "blah");
    request.setAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", attributes);
    
    MockHttpServletResponse response = new MockHttpServletResponse();
    AccountsController handler = new AccountsController();
    
    adaptor.handle(request, response, handler);
  }
}
