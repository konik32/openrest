package pl.openrest.dto.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import pl.openrest.exception.OrestException;
import pl.openrest.exception.OrestExceptionDictionary;

@RequiredArgsConstructor
public class NonDtoRequestsInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if (isPostOrPatchOrPutRequest(method) && !request.getParameterMap().containsKey("dto")) {
            throw new OrestException(OrestExceptionDictionary.NON_OREST_REQUEST, "Request should cointain dto parameter");
        }
        return true;
    }

    private static boolean isPostOrPatchOrPutRequest(String method) {
        return "POST".equals(method) || "PATCH".equals(method) || "PUT".equals(method) ;
    }
}
