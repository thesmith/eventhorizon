package thesmith.eventhorizon.controller;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController extends BaseController {
  public static final String AUTH_URL = "/auth";

  @RequestMapping(value = AUTH_URL, method = RequestMethod.GET)
  public String authenticate(HttpServletRequest request, HttpServletResponse response, @RequestParam("ptrt") String ptrt) {
    try {
      URL url = new URL(request.getRequestURL().toString());
      String host = url.getHost();
      if (null != host && host.contains(HOST_POSTFIX)) {
        String personId = host.replace(HOST_POSTFIX, "");
        this.setUserCookie(response, personId);
      }
    } catch (MalformedURLException e) {
      if (logger.isInfoEnabled())
        logger.info("Unable to decode url from " + request.getRequestURL().toString());
    }

    return (null != ptrt ? REDIRECT + ptrt : REDIRECT + "/");
  }
}
