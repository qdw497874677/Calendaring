import com.qdw.utils.PropertiesUtil;

public class Test {

    @org.junit.Test
    public void test(){
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        System.out.println(propertiesUtil.getValuesString("noOfStep"));
        System.out.println(propertiesUtil.getValuesDouble("caOfnw"));
        System.out.println(propertiesUtil.getValuesString("test"));
        System.out.println(propertiesUtil.getValuesBoolean("isOnline"));
    }
}
