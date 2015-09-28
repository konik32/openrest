package pl.openrest.dto.webmvc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import pl.openrest.exception.OrestException;
import pl.openrest.exception.OrestExceptionDictionary;

@RequiredArgsConstructor
public class NonDtoRequestsFilter implements Filter {

    private static boolean isPostOrPatchOrPutRequest(String method) {
        return "POST".equals(method) || "PATCH".equals(method) || "PUT".equals(method);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String method = req.getMethod();
        if (isPostOrPatchOrPutRequest(method) && !req.getParameterMap().containsKey("dto")) {
            throw new OrestException(OrestExceptionDictionary.NON_OREST_REQUEST, "Request should cointain dto parameter");
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
