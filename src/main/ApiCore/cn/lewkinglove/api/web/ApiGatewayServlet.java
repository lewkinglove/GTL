package cn.lewkinglove.api.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.lewkinglove.api.core.Authenticator;
import cn.lewkinglove.api.core.CallException;
import cn.lewkinglove.api.core.CallProcessor;
import cn.lewkinglove.api.core.CallRequest;
import cn.lewkinglove.api.core.CallResponse;

@WebServlet(
		urlPatterns = "/api", asyncSupported = false, 
		loadOnStartup = 1,  name = "Api_Gateway_Servlet"
	)
public final class ApiGatewayServlet extends HttpServlet{

    private static final long serialVersionUID = 1L;

	public ApiGatewayServlet() {
		super();
	}

	public void init() throws ServletException {
		
	}
	
	public void destroy() {
		super.destroy();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			//进行请求的基础认证
	        CallRequest callRequest = Authenticator.authRequest(request);
	        CallProcessor callProcessor = new CallProcessor(out, callRequest);
	        callProcessor.process();
        } catch (CallException e) {
        	out.print(new CallResponse(e.getCode(), e.getMessage() ));
        	out.flush();
        	out.close();
        	return ;
        }
	}

}

