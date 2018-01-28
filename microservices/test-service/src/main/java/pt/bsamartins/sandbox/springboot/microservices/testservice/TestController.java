package pt.bsamartins.sandbox.springboot.microservices.testservice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by martinsb on 19/04/2016.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/")
    public String index() {
        return "Hello World";
    }

}
