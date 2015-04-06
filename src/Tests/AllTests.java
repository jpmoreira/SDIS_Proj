package Tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DatabaseTests.class, TestChunk.class, TestSFile.class, MessagesTests.class, ProtocolTests.class, WorkerTests.class })
public class AllTests {

}
