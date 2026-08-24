
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

public class JavaFXTestBase {
    private static boolean initialised = false;

    @BeforeAll
    public static void initJFX() throws Exception {
        if (!initialised) {
            Platform.startup(() -> {});
            initialised = true;
        }
    }
}
