package com.oushu.config;

import com.google.gson.Gson;
import com.oushu.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截请求: {}",request.getRequestURI());//这个是日志信息，直接输出拦截的请求
        //1、获取前端的本次请求的URI
        String requestURI = request.getRequestURI();

        if (requestURI.equals("/login")){
            response.sendRedirect("/");
            return;
        }
        //登录请求直接转发
        if (requestURI.equals("/api/phoenix/login")){
            request.getRequestDispatcher("/phoenix/login").forward(request, response);
            return;
        }
        String[] urls = new String[]{//列举放行资源
                "/",
                "/**/*.html",
                "/**/*.js",
                "/**/*.css",
                "/**/*.svg",
                "/**/*.gz",
                "/**/*.ico",
                "/**/*.map"//放行静态资源和静态页面
        };
        //2、判断本次请求是否需要处理
        boolean check = check(requestURI, urls);
        //3、如果不需要处理,则直接放行
        if(check){
            filterChain.doFilter(request,response);//放行
            return;
        }
        //4、判断登录状态,如果已登录,则直接放行
        if(request.getSession().getAttribute("oushu") != null){
            if (requestURI.indexOf("/api") == 0){
                response.setContentType("text/json;charset=utf-8");
                request.getRequestDispatcher(requestURI.replaceFirst("/api", ""))
                        .forward(request, response);
                return;
            }
            filterChain.doFilter(request,response);//放行
            return;
        }

        ResponseModel responseModel = new ResponseModel();
        Gson gson = new Gson();
        response.setStatus(401);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(gson.toJson(responseModel.failure("not login")));
    }

    /**
     * 路径匹配,检查本次请求是否放行
     * @param requetURI 从前端传回的请求
     * @param urls 上面不用拦截的请求
     * @return 进行对比返回
     */
    public boolean check(String requetURI, String[] urls) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requetURI);//使用上面定义的路径匹配器,进行比对
            if (match) {
                return true;//匹配成功就返回true
            }
        }
        return false;//一个也没匹配上,返回false
    }
}
