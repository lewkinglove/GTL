package cn.lewkinglove.api.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * 字符编码转换过滤器
 * @author liujing(lewkinglove@gmail.com)
 */
@WebFilter(urlPatterns = {"/*"}, filterName="CharacterEncodingFilter", asyncSupported=true) 
public class CharacterEncodingFilter implements Filter{
	private String encoding = "UTF-8";
	
	@Override
    public void init(FilterConfig filterConfig) throws ServletException {
	    //先获取ContextParam; 优先级较低
		String encoding = filterConfig.getServletContext().getInitParameter("encoding");
	    if(encoding!=null && encoding.trim().length()!=0)
	    	this.encoding = encoding; 
	    
	    //再获取FilterParam; 优先级较高
	    encoding = filterConfig.getInitParameter("encoding");
	    if(encoding!=null && encoding.trim().length()!=0)
	    	this.encoding = encoding;
    }

	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	    //设定该请求编码
		request.setCharacterEncoding(this.encoding);
		
		//设定响应编码
		response.setCharacterEncoding(this.encoding);
		
	    chain.doFilter(request, response);
    }

	@Override
    public void destroy() {
    }

}
