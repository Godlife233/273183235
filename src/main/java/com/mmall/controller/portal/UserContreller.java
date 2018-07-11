package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserContreller {
    @Autowired
    private IUserService iUserService;
    /*
    用户登录
     */
    @RequestMapping(value ="login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){

        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;

    }
    //用户退出
    @RequestMapping(value ="logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout (HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }
    //用户注册
    @RequestMapping(value ="register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register (User user){
        return  iUserService.register(user);
    }
    //用户校验
    @RequestMapping(value ="check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }
    //获得登录用户信息
    @RequestMapping(value ="get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<User> getUserInfo(HttpSession session){
        User  user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user !=null){
            return  ServerResponse.createBySuccess(user);
        }
        return  ServerResponse.createByErrorMessage("用户未登录，无法获得当前用户的信息");
    }

    //返回找回密码问题
    @RequestMapping(value ="forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.forgetGetQuestion(username);
    }

    //校验找回密码答案是否正确
    @RequestMapping(value ="forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.forgetCheckAnswer(username,question,answer);
    }

    //重置密码
    @RequestMapping(value ="forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgrtRestPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgrtRestPassword(username,passwordNew,forgetToken);
    }

    //登录状态下的修改密码
    @RequestMapping(value ="reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);

    }

    @RequestMapping(value ="update_infomation.do",method = RequestMethod.POST)
    @ResponseBody
    //修改用户个人信息
    public ServerResponse<User> updateInfomation(HttpSession session ,User user){
        User currentUser =(User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInfomation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }

        return response;
    }

    //获取用户详细信息
    @RequestMapping(value ="get_infomation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        User currentUser =(User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要登录");

        }
        return iUserService.getInformation(currentUser.getId());
    }

}
