import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class Slf4jTest {

    @Test
    public void test() {
        int a = 1;

        log.info("\ninfo{}", a);
        log.warn("warn{}", a);
        log.debug("debug{}", a);
        log.error("\nerr{}", a);
    }
}
