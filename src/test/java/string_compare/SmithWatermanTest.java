package alignmentStudy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test stubs.
 */
public class SmithWatermanTest 
extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public SmithWatermanTest( String testName )
	{
		super( testName );
	}
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( SmithWatermanTest.class );
	}

	/**
	 * Test stub
	 */
	public void testOne()
	{
		SmithWaterman sw = new SmithWaterman();
		assertEquals(0, sw.smithWatermanScore("John Smith", "John Smith"));
	}

	/**
	 * Test stub
	 */
	public void testTwo()
	{
		SmithWaterman sw = new SmithWaterman();
		assertEquals(30, sw.smithWatermanScore("Prof. John R. Smith, University of Calgory", "John R. Smith, Prof."));
		assertEquals("John R. Smith, ", sw.getAlignedStrOne());
		assertEquals("John R. Smith, ", sw.getAlignedStrTwo());
	}

	/**
	 * Test stub
	 */
	public void testThree()
	{
		SmithWaterman sw = new SmithWaterman();
		assertEquals(0, sw.smithWatermanScore("month", "april"));
	}
}
