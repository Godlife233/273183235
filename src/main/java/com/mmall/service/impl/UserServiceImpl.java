package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user =userMapper.selectLogin(username,md5Password);

        if(user ==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);

    }
    //用户注册
    public  ServerResponse<String> register(User user){

        ServerResponse validServerResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validServerResponse.isSuccess()){
            return validServerResponse;
        }
         validServerResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validServerResponse.isSuccess()){
            return validServerResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return  ServerResponse.createBySuccessMessage("注册成功");
    }
    //校验用户
    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }

            if(Const.EMAIL.equals(type)){
               int resultCount = userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("邮箱已经存在");
                }
            }

        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return  ServerResponse.createBySuccessMessage("校验成功");
    }

    //找回密码问题
    public ServerResponse<String> forgetGetQuestion(String username){
        ServerResponse validServerResponse = this.checkValid(username,Const.USERNAME);
        if(validServerResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question =userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return  ServerResponse.createBySuccess(question);
        }
        return  ServerResponse.createByErrorMessage("没有设置密码找回问题");


    }

    //校验找回密码答案是否正确
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
       int resultCount = userMapper.checkAnswer(username,question,answer);
       if(resultCount==0){
           return  ServerResponse.createByErrorMessage("问题答案错误");
       }else{
           String forgetToken = UUID.randomUUID().toString();
           //将forgetToken放到本地cache中并设置有效期
           TokenCache.setKey(TokenCache.TOKEN_REFIX+username,forgetToken);
           return  ServerResponse.createBySuccess(forgetToken);
       }


    }
    //忘记密码中的重置密码
    public ServerResponse<String> forgrtRestPassword(String username,String passwordNew,String forgetToken){
        if(!StringUtils.isNotBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，Token需要传递");
        }
        ServerResponse validServerResponse = this.checkValid(username,Const.USERNAME);
        if(validServerResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_REFIX+username);

        if(!StringUtils.isNotBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        if(StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByusername(username,md5Password);
            if(rowCount>0)return ServerResponse.createBySuccessMessage("修改密码成功");
        }else{
            return ServerResponse.createByErrorMessage("token错误请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    //登录状态下的重置密码
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user){

        //防止横向越权，要检验这个用户的旧密码，一定要指定是这个用户，
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());

        if(resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return  ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return  ServerResponse.createByErrorMessage("密码修改失败");

    }

    //修改用户个人信息
    public ServerResponse<User> updateInfomation(User user){
        //username不能被更新
        //email要进行一个校验，校验新的email是否已经存在
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return  ServerResponse.createByErrorMessage("email已存在");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setUsername(user.getUsername());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);

        if(updateCount>0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null ){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return  ServerResponse.createBySuccess(user);
    }

    //backend
    //校验是否为管理员
    public  ServerResponse checkAdminRole(User user){
        if(user != null &&user.getRole() == Const.Role.ROLE_ADMIIN){
            return ServerResponse.createBySuccess();
        }else{
            return ServerResponse.createByError();
        }
    }

}
