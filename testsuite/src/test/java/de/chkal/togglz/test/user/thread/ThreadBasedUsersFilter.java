package de.chkal.togglz.test.user.thread;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import de.chkal.togglz.core.user.SimpleFeatureUser;
import de.chkal.togglz.core.user.thread.ThreadLocalFeatureUserProvider;

@WebFilter(urlPatterns = "/*")
public class ThreadBasedUsersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        String username = request.getParameter("user");
        if (username == null) {
            throw new IllegalArgumentException("Query parameter 'user' must be set!");
        }

        ThreadLocalFeatureUserProvider.setFeatureUser(new SimpleFeatureUser(username, "ck".equals(username)));

        try {
            chain.doFilter(request, response);
        } finally {
            ThreadLocalFeatureUserProvider.setFeatureUser(null);
        }

    }

    @Override
    public void destroy() {
    }

}
