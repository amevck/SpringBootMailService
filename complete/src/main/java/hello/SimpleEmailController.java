package hello;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.*;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Controller
public class SimpleEmailController {

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private Configuration freemarkerConfig;


    private void sendEmail(String payload) throws Exception {
        MimeMessage message = sender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);

        Map<String, Object> model = new HashMap();
        model.put("user", "Creative");
        System.out.println(payload);
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.keys().forEachRemaining(k ->
        {
            //System.out.println(k);
            try {
                System.out.println(jsonObj.get((String) k));
                model.put((String) k, jsonObj.get((String) k));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        // set loading location to src/main/resources
        // You may want to use a subfolder such as /templates here
        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/");

        Template t = freemarkerConfig.getTemplate("welcome.ftl");
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

        helper.setTo((String)jsonObj.get("sentTo"));
        helper.setText(text, true); // set to html
        helper.setSubject("Hi");

        sender.send(message);
    }

    @PostMapping("/process")
    @ResponseBody
    public String process(@RequestBody String payload) throws Exception {

            try {
                sendEmail(payload);
                return "Email Sent!";
            } catch (Exception ex) {
                return "Error in sending email: " + ex;
            }
    }
}