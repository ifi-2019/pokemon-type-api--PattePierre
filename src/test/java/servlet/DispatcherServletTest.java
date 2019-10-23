package servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.servlet.Servlet;

import org.junit.jupiter.api.Test;

import controller.Controller;
import controller.HelloController;
import mapping.RequestMapping;

class DispatcherServletTest { 

    @Test 
    void registerController_throwsIllegalArgumentException_forNonControllerClasses() {
        DispatcherServlet servlet = new DispatcherServlet();

        assertThrows(IllegalArgumentException.class,
                () -> servlet.registerController(String.class));
        assertThrows(IllegalArgumentException.class,
                () -> servlet.registerController(SomeEmptyClass.class));
    }

    @Test
    void registerController_doesNotRegisters_nonAnnotatedMethods() {
    	DispatcherServlet servlet = new DispatcherServlet();

        servlet.registerController(SomeControllerClassWithAMethod.class);

        assertTrue(servlet.getMappings().isEmpty());
    }

    @Test
    void registerController_doesNotRegisters_voidReturningMethods() {
    	DispatcherServlet servlet = new DispatcherServlet();

        servlet.registerController(SomeControllerClassWithAVoidMethod.class);

        assertTrue(servlet.getMappings().isEmpty());
    }

    @Test 
    void registerController_shouldRegisterCorrectyMethods(){
    	DispatcherServlet servlet = new DispatcherServlet();

        servlet.registerController(SomeControllerClass.class);
        servlet.registerController(SomeOtherControllerClass.class);

        assertEquals("someGoodMethod",
                servlet.getMappingForUri("/test").getName());
        assertEquals("someOtherNiceMethod",
                servlet.getMappingForUri("/otherTest").getName());
    }

    @Test
    void registerHelloController_shouldWorkCorrectly(){
    	DispatcherServlet servlet = new DispatcherServlet();
        servlet.registerController(HelloController.class);

        assertEquals("sayHello", servlet.getMappingForUri("/hello").getName());
        assertEquals("sayGoodBye", servlet.getMappingForUri("/bye").getName());
        assertEquals("explode", servlet.getMappingForUri("/boum").getName());
    }
}


class SomeEmptyClass{}


@Controller
class SomeControllerClassWithAMethod{
    public String myMethod(){
        return "test";
    }
}

@Controller
class SomeControllerClassWithAVoidMethod{
    @RequestMapping(uri="/test")
    public void myMethod(){}
}

@Controller
class SomeControllerClass {
    @RequestMapping(uri="/test")
    public String someGoodMethod(){
        return "Hello";
    }

    @RequestMapping(uri="/test-throwing")
    public String someThrowingMethod(){
        throw new RuntimeException("some exception message");
    }

    @RequestMapping(uri="/test-with-params")
    public String someThrowingMethod(Map<String, String[]> params){
        return params.get("id")[0];
    }
}

@Controller
class SomeOtherControllerClass {
    @RequestMapping(uri="/otherTest")
    public String someOtherNiceMethod(){
        return "Hello again";
    }
}