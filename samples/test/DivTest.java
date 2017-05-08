import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class DivTest {
    @Test
    public void testOneDivOne(){
        DivImpl impl = new DivImpl();
        int res = impl.div(1, 1);
        Assert.assertEquals(res, 1);
    }
    @Test
    public void testFourDivTwo(){
        DivImpl impl = new DivImpl();
        int res = impl.div(4, 2);
        Assert.assertEquals(res, 2);
    }
    @Test
    public void testOneDivZero(){
        DivImpl impl = new DivImpl();
        int res = impl.div(1, 0);
        Assert.assertEquals(res, 0);
    }
    @Test
    public void testZeroDivOne(){
        DivImpl impl = new DivImpl();
        int res = impl.div(0, 1);
        //for failed case
        Assert.assertEquals(res, 1);
    }
    @Test
    @Ignore
    public void testIgnore(){
        Assert.assertTrue(false);
    }
}