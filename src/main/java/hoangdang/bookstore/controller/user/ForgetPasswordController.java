/**
 * @(#)ForgetPasswordController.java 2021/09/09.
 * 
 * Copyright(C) 2021 by PHOENIX TEAM.
 * 
 * Last_Update 2021/09/09.
 * Version 1.00.
 */
package hoangdang.bookstore.controller.user;

import hoangdang.bookstore.service.UserService;
import hoangdang.bookstore.service.impl.MailerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import hoangdang.bookstore.common.Constants;
import hoangdang.bookstore.entity.User;
import hoangdang.bookstore.model.UserRegister;

/**
 * Class de lay lai mat khau
 * 

 * @version 1.00
 */
@Controller
public class ForgetPasswordController {
	
	@Autowired
    UserService userService;
	
	@Autowired
    MailerServiceImpl mailerService;
	
	@Autowired
	PasswordEncoder pe;

	/**
	 * Hien thi man hinh forget-password
	 * 
	 * @param model
	 * @return man hinh forget-password
	 */
	@GetMapping("/forget-password")
	public String displayFormForgetPassword(Model model) {
		UserRegister userForm = new UserRegister();
		model.addAttribute("userForm", userForm);
		return Constants.USER_DISPLAY_FORGET_PASSWORD;
	}

	@PostMapping("/forget-password")
	public String handlerFormForgetPassword(Model model, @ModelAttribute("userForm") @Validated UserRegister userForm,
			BindingResult result) {
		if (userForm.getEmail().isEmpty()) {
			result.rejectValue("email", "NotBlank.userRegister.email");
		} else {
			User user = userService.findUserByEmail(userForm.getEmail());
			if (user == null) {
				result.rejectValue("email", "NotExist.userLogin.username");
			}
			else {
				String password = pe.encode(user.getPassword());
				mailerService.queue(userForm.getEmail(), "Làm mới mật khẩu!", "Vui lòng click vào link này: "+ "http://localhost:8080/reset-password?code="+password+"&email="+user.getEmail() +" để reset mật khẩu.");
			}
		}

		if (result.hasErrors()) {
			return Constants.USER_DISPLAY_FORGET_PASSWORD;
		}
		
		model.addAttribute("alert", "Thông báo!");
		model.addAttribute("message", "Vui lòng kiểm tra email để thay đổi mật khẩu!");
		return Constants.USER_DISPLAY_ALERT_STATUS;
	}
}
