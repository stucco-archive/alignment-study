package alignmentStudy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test stubs.
 */
public class TimeTest extends TestCase	{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public TimeTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( TimeTest.class );
	}

	/**
	 * Test stub
	 */
	public void testOne()
	{
		RemoveStopWords rsw = new RemoveStopWords("resources/StopWords.txt");
		assertEquals(rsw.containsString("a"), true);
	}
}
