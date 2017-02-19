package com.jim.framework.web.filter;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiang on 2016/12/22.
 */
@WebFilter(filterName="timeFilter",urlPatterns="/*")
public class TimeFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Stopwatch stopwatch=Stopwatch.createStarted();
        filterChain.doFilter(servletRequest, servletResponse);
        stopwatch.stop();
        this.logger.info("time(ns):"+stopwatch.elapsed(TimeUnit.NANOSECONDS));
    }

    @Override
    public void destroy() {

    }
}
