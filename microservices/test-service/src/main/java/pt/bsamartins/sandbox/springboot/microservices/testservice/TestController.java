package pt.bsamartins.sandbox.springboot.microservices.testservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by martinsb on 19/04/2016.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String index() {
        return "Hello World";
    }

}
