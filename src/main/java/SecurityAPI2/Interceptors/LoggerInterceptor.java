package SecurityAPI2.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoggerInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String route = request.getRequestURI();
        logger.info("Route: " + route + ", Type: REQUEST" + ", Method: " + request.getMethod() +  ", Ip: " + request.getRemoteAddr());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        int statusCode = response.getStatus();
        String route = request.getRequestURI();
        logger.info("Route: " + route+ ", Type: RESPONSE" + ", Status Code: " + statusCode);
    }
}
