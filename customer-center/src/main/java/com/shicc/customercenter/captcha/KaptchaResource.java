package com.shicc.customercenter.captcha;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.shicc.customercenter.dto.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api")
public class KaptchaResource {
    private final Logger log = LoggerFactory.getLogger(KaptchaResource.class);

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private KaptchaService kaptchaService;


    @PostMapping(value = "/captcha/verify-code/create")
    public void getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //用字节数组存储
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        ServletOutputStream responseOutputStream =
                response.getOutputStream();
        final HttpSession httpSession = request.getSession();
        try {
            //生产验证码字符串并保存到session中
            String createText = kaptchaProducer.createText();
            //打印随机生成的字母和数字
            log.debug(createText);
            httpSession.setAttribute(Constants.KAPTCHA_SESSION_KEY, createText);
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = kaptchaProducer.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
            captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");
            //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
            responseOutputStream.write(captchaChallengeAsJpeg);
            responseOutputStream.flush();
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        } finally {
            responseOutputStream.close();
        }
    }

    /*
     * @Description //校验验证码
     * @Param [request]
     * @return java.lang.String
     **/
    @PostMapping(value = "/captcha/verify-code/check")
    public ResponseEntity<Result> checkVerifyCode(String verifyCode,HttpServletRequest request) {
        return ResponseEntity.ok(kaptchaService.checkVerifyCode(verifyCode, request));
    }


}
