package alignmentStudy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test stubs.
 */
public class AffineGapTest 
extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AffineGapTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( AffineGapTest.class );
	}

	/**
	 * Test stub
	 */
	public void testOne()
	{
		AffineGap ag = new AffineGap();
		assertEquals(0.0, ag.affineGapDistance("month", "month", 1.0, 0.7));
	}

	/**
	 * Test stub
	 */
	public void testTwo()
	{
		AffineGap ag = new AffineGap();
		assertEquals(5.0, ag.affineGapDistance("month", "april", 1.0, 0.7));
	}
	
	/**
	 * Test stub
	 */
	public void testThree()
	{
		AffineGap ag = new AffineGap();
		assertEquals( 10.299999999999997, ag.affineGapDistance("John R. Smith", "Jonathan Richard Smith", 1.0, 0.7));
		assertEquals("Jo----hn R--.--- Smith", ag.getAlignedStrOne());
		assertEquals("Jonathan Richard Smith", ag.getAlignedStrTwo());
	}
}
