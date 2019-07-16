import com.mmall.util.DateTimeUtil;
import org.joda.time.DateTime;

public class Test {

    @org.junit.Test
    public void test1(){
        System.out.println(DateTimeUtil.strToDate(DateTime.now().toString(),"yyyy-MM-dd hh:mm:ss ").toString()
        );
    }
}
