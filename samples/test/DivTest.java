import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class DivTest {
    @Test
    public void testOneDivOne(){
        DivImpl impl = new DivImpl();
        int res = impl.div(1, 1);
        Assert.assertEquals(1, res);
    }
    @Test
    public void testFourDivTwo(){
        DivImpl impl = new DivImpl();
        int res = impl.div(4, 2);
        Assert.assertEquals(2, res);
    }
    @Test
    public void testOneDivZero(){
        DivImpl impl = new DivImpl();
        int res = impl.div(1, 0);
        Assert.assertEquals(0, res);
    }
    @Test
    public void testZeroDivOne(){
        DivImpl impl = new DivImpl();
        int res = impl.div(0, 1);
        //for failed case
        Assert.assertEquals(1, res);
    }
    @Test
    @Ignore
    public void testIgnore(){
        Assert.assertTrue(false);
    }
}